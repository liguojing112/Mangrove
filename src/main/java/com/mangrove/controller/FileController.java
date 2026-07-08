package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.config.FileUploadConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB
    private static final Set<String> ALLOWED_FILE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp",
            "video/mp4", "video/webm", "video/quicktime", "video/x-msvideo"
    );

    private final FileUploadConfig fileUploadConfig;

    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        validateFile(file);
        Map<String, String> result = saveFile(file);
        log.info("文件上传成功: {} -> {}", file.getOriginalFilename(), result.get("url"));
        return Result.success(result);
    }

    @PostMapping("/uploads")
    public Result<List<Map<String, String>>> uploadMultiple(@RequestParam("files") List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请至少选择一个文件");
        }
        List<Map<String, String>> results = new ArrayList<>();
        for (MultipartFile file : files) {
            validateFile(file);
            results.add(saveFile(file));
        }
        log.info("批量上传成功，共 {} 个文件", results.size());
        return Result.success(results);
    }

    @DeleteMapping("/{filename}")
    public Result<Void> delete(@PathVariable String filename) {
        try {
            Path uploadDir = Paths.get(fileUploadConfig.getUploadDir()).toAbsolutePath().normalize();
            Path filePath = uploadDir.resolve(filename).normalize();

            // 防止路径穿越攻击
            if (!filePath.startsWith(uploadDir)) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "非法的文件名");
            }

            if (Files.deleteIfExists(filePath)) {
                log.info("文件删除成功: {}", filename);
            } else {
                throw new BusinessException(ResultCode.NOT_FOUND, "文件不存在");
            }
        } catch (IOException e) {
            log.error("文件删除失败: {}", filename, e);
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "文件删除失败");
        }
        return Result.success();
    }

    /**
     * 列出所有已上传文件（管理后台用）
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> listFiles() {
        try {
            Path uploadDir = Paths.get(fileUploadConfig.getUploadDir()).toAbsolutePath().normalize();
            if (!Files.exists(uploadDir)) {
                return Result.success(List.of());
            }
            List<Map<String, Object>> files = Files.list(uploadDir)
                .filter(Files::isRegularFile)
                .sorted((a, b) -> {
                    try { return Files.getLastModifiedTime(b).compareTo(Files.getLastModifiedTime(a)); }
                    catch (IOException e) { return 0; }
                })
                .map(path -> {
                    Map<String, Object> fileInfo = new LinkedHashMap<>();
                    String filename = path.getFileName().toString();
                    fileInfo.put("filename", filename);
                    fileInfo.put("url", "/uploads/" + filename);
                    fileInfo.put("fileType", filename.contains(".mp4") || filename.contains(".webm") ? "video/mp4" : "image/jpeg");
                    try { fileInfo.put("fileSize", Files.size(path)); }
                    catch (IOException e) { fileInfo.put("fileSize", 0); }
                    try { fileInfo.put("createdAt", Files.getLastModifiedTime(path).toString()); }
                    catch (IOException e) { fileInfo.put("createdAt", ""); }
                    return fileInfo;
                })
                .collect(Collectors.toList());
            return Result.success(files);
        } catch (IOException e) {
            log.error("文件列表获取失败", e);
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "获取文件列表失败");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "文件不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "文件大小不能超过10MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !(contentType.startsWith("image/") || contentType.startsWith("video/"))) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "仅支持图片和视频文件");
        }
    }

    /**
     * 直接设置艺人封面
     */
    @PutMapping("/cover/{artistId}")
    @Transactional
    public Result<Void> setCover(@PathVariable Long artistId,
                                  @RequestParam String coverUrl) {
        try {
            java.sql.Connection conn = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mangrove?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true",
                "root", "root123");
            java.sql.PreparedStatement ps = conn.prepareStatement("UPDATE artist SET cover_url = ? WHERE id = ?");
            ps.setString(1, coverUrl);
            ps.setLong(2, artistId);
            int rows = ps.executeUpdate();
            ps.close();
            conn.close();
            if (rows > 0) return Result.success();
            else throw new BusinessException(ResultCode.NOT_FOUND, "艺人不存在");
        } catch (Exception e) {
            log.error("设置封面失败", e);
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "设置封面失败");
        }
    }

    // ─── 分类管理 ───

    @GetMapping("/categories")
    public Result<List<String>> listCategories() {
        try (java.sql.Connection conn = getDbConnection()) {
            java.sql.PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT category FROM file_meta WHERE category IS NOT NULL AND category != '' ORDER BY category");
            java.sql.ResultSet rs = ps.executeQuery();
            List<String> list = new ArrayList<>();
            while (rs.next()) list.add(rs.getString("category"));
            rs.close(); ps.close();
            return Result.success(list);
        } catch (Exception e) { return Result.success(List.of()); }
    }

    // ─── 文件元数据 ───

    @PostMapping("/meta")
    public Result<Void> saveMeta(@RequestBody Map<String, Object> body) {
        try (java.sql.Connection conn = getDbConnection()) {
            String sql = "INSERT INTO file_meta (filename, display_name, seq_no, category, photo_date) VALUES (?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE display_name=VALUES(display_name), seq_no=VALUES(seq_no), category=VALUES(category), photo_date=VALUES(photo_date)";
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, (String) body.get("filename"));
            ps.setString(2, (String) body.getOrDefault("displayName", ""));
            ps.setInt(3, ((Number) body.getOrDefault("seqNo", 0)).intValue());
            ps.setString(4, (String) body.getOrDefault("category", ""));
            String date = (String) body.get("photoDate");
            ps.setString(5, date != null && !date.isEmpty() ? date : null);
            ps.executeUpdate();
            ps.close();
            return Result.success();
        } catch (Exception e) {
            log.error("保存元数据失败", e);
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "保存失败");
        }
    }

    @GetMapping("/meta")
    public Result<List<Map<String, Object>>> listMeta(@RequestParam(required = false) String category) {
        try (java.sql.Connection conn = getDbConnection()) {
            String sql = category != null ? "SELECT * FROM file_meta WHERE category=? ORDER BY seq_no, created_at DESC" 
                : "SELECT * FROM file_meta ORDER BY seq_no, created_at DESC";
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            if (category != null) ps.setString(1, category);
            java.sql.ResultSet rs = ps.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("filename", rs.getString("filename"));
                item.put("displayName", rs.getString("display_name"));
                item.put("seqNo", rs.getInt("seq_no"));
                item.put("category", rs.getString("category"));
                item.put("photoDate", rs.getString("photo_date"));
                item.put("url", "/uploads/" + rs.getString("filename"));
                list.add(item);
            }
            rs.close(); ps.close();
            return Result.success(list);
        } catch (Exception e) {
            log.error("获取元数据失败", e);
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "获取失败");
        }
    }

    private java.sql.Connection getDbConnection() throws java.sql.SQLException {
        return java.sql.DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/mangrove?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true",
            "root", "root123");
    }

    private String uploadFile(MultipartFile file, String originalFilename) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "文件不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "文件大小不能超过10MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_FILE_TYPES.contains(contentType)) {
            throw new BusinessException(ResultCode.BAD_REQUEST,
                    "不支持的文件类型，仅支持 jpg、png、gif、webp、mp4、webm、mov、avi");
        }
    }

    private Map<String, String> saveFile(MultipartFile file) {
        try {
            Path uploadDir = Paths.get(fileUploadConfig.getUploadDir()).toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String storedFilename = UUID.randomUUID().toString() + extension;
            Path filePath = uploadDir.resolve(storedFilename);
            file.transferTo(filePath.toFile());

            Map<String, String> data = new LinkedHashMap<>();
            data.put("url", "/uploads/" + storedFilename);
            data.put("filename", originalFilename != null ? originalFilename : storedFilename);
            return data;
        } catch (IOException e) {
            log.error("文件保存失败", e);
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "文件上传失败");
        }
    }
}
