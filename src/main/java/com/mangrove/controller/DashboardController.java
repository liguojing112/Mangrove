package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.entity.MediaAsset;
import com.mangrove.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class DashboardController {

    private final ArtistRepository artistRepository;
    private final FanWorkRepository fanWorkRepository;
    private final MerchandiseRepository merchandiseRepository;
    private final SysUserRepository sysUserRepository;
    private final MediaAssetRepository mediaAssetRepository;

    @GetMapping
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new LinkedHashMap<>();

        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("artistCount", artistRepository.count());
        stats.put("workCount", fanWorkRepository.count());
        stats.put("merchandiseCount", merchandiseRepository.count());
        stats.put("userCount", sysUserRepository.count());
        
        // 从 file_meta 统计照片和视频数量（非 media_assets）
        long photoCount = 0;
        long videoCount = 0;
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mangrove?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true",
                "root", "root123")) {
            java.sql.PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM file_meta WHERE filename REGEXP ?");
            ps.setString(1, "\\.(jpg|jpeg|png|gif|webp)$");
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) photoCount = rs.getLong(1);
            rs.close(); ps.close();
            
            ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM file_meta WHERE filename REGEXP ?");
            ps.setString(1, "\\.(mp4|webm|mov|avi)$");
            rs = ps.executeQuery();
            if (rs.next()) videoCount = rs.getLong(1);
            rs.close(); ps.close();
        } catch (Exception e) {
            // fallback
        }
        stats.put("photoCount", photoCount);
        stats.put("videoCount", videoCount);
        data.put("stats", stats);

        // 最近上传
        List<MediaAsset> recent = mediaAssetRepository.findTop6ByStatusAndIsDeletedOrderByCreatedAtDesc(1, 0);
        List<Map<String, Object>> recentList = new ArrayList<>();
        for (MediaAsset m : recent) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", m.getId());
            item.put("title", m.getTitle());
            item.put("category", m.getCategory().name());
            item.put("createdAt", m.getCreatedAt());
            recentList.add(item);
        }
        data.put("recentUploads", recentList);

        return Result.success(data);
    }
}
