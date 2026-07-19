package com.mangrove.service;

import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.config.FileUploadConfig;
import com.mangrove.dto.request.MusicAlbumPhotoRequest;
import com.mangrove.dto.request.MusicPhotoAlbumRequest;
import com.mangrove.dto.request.MusicPhotoOrderRequest;
import com.mangrove.dto.request.MusicTrackCatalogRequest;
import com.mangrove.dto.response.MusicAlbumDetailResponse;
import com.mangrove.entity.MusicAlbumPhoto;
import com.mangrove.entity.MusicPhotoAlbum;
import com.mangrove.entity.MusicTrack;
import com.mangrove.repository.MusicAlbumPhotoRepository;
import com.mangrove.repository.MusicPhotoAlbumRepository;
import com.mangrove.repository.MusicTrackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicCatalogService {

    private static final Set<String> AUDIO_EXTENSIONS = Set.of("mp3", "wav", "flac", "aac", "ogg", "m4a");

    private final MusicTrackRepository trackRepository;
    private final MusicPhotoAlbumRepository albumRepository;
    private final MusicAlbumPhotoRepository photoRepository;
    private final JdbcTemplate jdbcTemplate;
    private final FileUploadConfig fileUploadConfig;

    public List<MusicTrack> listPublicTracks(String category) {
        return isBlank(category)
                ? trackRepository.findByStatusOrderBySortOrderAscCreatedAtDesc(1)
                : trackRepository.findByStatusAndCategoryOrderBySortOrderAscCreatedAtDesc(1, category.trim());
    }

    public List<MusicTrack> listAdminTracks() {
        return trackRepository.findAllByOrderBySortOrderAscCreatedAtDesc();
    }

    @Transactional
    public MusicTrack saveTrack(Long id, MusicTrackCatalogRequest request) {
        validateTrack(request);
        MusicTrack track = id == null ? new MusicTrack() : trackRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "歌曲不存在"));
        track.setTitle(request.getTitle().trim());
        track.setCategory(request.getCategory().trim());
        track.setLocalAudioUrl(trimToNull(request.getLocalAudioUrl()));
        track.setQqMusicUrl(trimToNull(request.getQqMusicUrl()));
        track.setCoverUrl(trimToNull(request.getCoverUrl()));
        track.setDurationSeconds(request.getDurationSeconds());
        track.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        track.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        if (track.getPlayCount() == null) track.setPlayCount(0L);
        return trackRepository.save(track);
    }

    @Transactional
    public void deleteTrack(Long id) {
        if (!trackRepository.existsById(id)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "歌曲不存在");
        }
        trackRepository.deleteById(id);
    }

    @Transactional
    public void incrementPlay(Long id) {
        if (trackRepository.incrementPlayCount(id) == 0) {
            throw new BusinessException(ResultCode.NOT_FOUND, "歌曲不存在");
        }
    }

    public List<MusicPhotoAlbum> listPublicAlbums() {
        return albumRepository.findByStatusOrderBySortOrderAscCreatedAtDesc(1);
    }

    public List<MusicPhotoAlbum> listAdminAlbums() {
        return albumRepository.findAllByOrderBySortOrderAscCreatedAtDesc();
    }

    public MusicAlbumDetailResponse getAlbum(Long id, boolean includeHidden) {
        MusicPhotoAlbum album = albumRepository.findById(id)
                .filter(item -> includeHidden || Objects.equals(item.getStatus(), 1))
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "专辑不存在"));
        return new MusicAlbumDetailResponse(album, photoRepository.findByAlbumIdOrderBySortOrderAscCreatedAtAsc(id));
    }

    @Transactional
    public MusicPhotoAlbum saveAlbum(Long id, MusicPhotoAlbumRequest request) {
        if (isBlank(request.getTitle())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请填写专辑名称");
        }
        if (isBlank(request.getCoverUrl())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请上传专辑封面");
        }
        MusicPhotoAlbum album = id == null ? new MusicPhotoAlbum() : albumRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "专辑不存在"));
        album.setTitle(request.getTitle().trim());
        album.setDescription(trimToNull(request.getDescription()));
        album.setCoverUrl(request.getCoverUrl().trim());
        album.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        album.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        return albumRepository.save(album);
    }

    @Transactional
    public void deleteAlbum(Long id) {
        if (!albumRepository.existsById(id)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "专辑不存在");
        }
        photoRepository.deleteByAlbumId(id);
        albumRepository.deleteById(id);
    }

    @Transactional
    public MusicAlbumPhoto addPhoto(Long albumId, MusicAlbumPhotoRequest request) {
        if (!albumRepository.existsById(albumId)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "专辑不存在");
        }
        if (isBlank(request.getImageUrl())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请上传照片");
        }
        MusicAlbumPhoto photo = MusicAlbumPhoto.builder()
                .albumId(albumId)
                .imageUrl(request.getImageUrl().trim())
                .caption(trimToNull(request.getCaption()))
                .sortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder())
                .build();
        return photoRepository.save(photo);
    }

    @Transactional
    public void reorderPhotos(Long albumId, MusicPhotoOrderRequest request) {
        if (!albumRepository.existsById(albumId)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "专辑不存在");
        }
        if (request.getItems() == null) return;
        Map<Long, MusicAlbumPhoto> photos = new HashMap<>();
        photoRepository.findByAlbumIdOrderBySortOrderAscCreatedAtAsc(albumId)
                .forEach(photo -> photos.put(photo.getId(), photo));
        for (MusicPhotoOrderRequest.Item item : request.getItems()) {
            MusicAlbumPhoto photo = photos.get(item.getId());
            if (photo != null) photo.setSortOrder(item.getSortOrder() == null ? 0 : item.getSortOrder());
        }
        photoRepository.saveAll(photos.values());
    }

    @Transactional
    public void deletePhoto(Long albumId, Long photoId) {
        MusicAlbumPhoto photo = photoRepository.findById(photoId)
                .filter(item -> Objects.equals(item.getAlbumId(), albumId))
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "照片不存在"));
        photoRepository.delete(photo);
    }

    @Transactional
    public void migrateLegacyTracksIfEmpty() {
        if (trackRepository.count() > 0) return;
        Path uploadDir = Paths.get(fileUploadConfig.getUploadDir()).toAbsolutePath().normalize();
        if (!Files.isDirectory(uploadDir)) return;

        Map<String, LegacyMeta> metadata = loadLegacyMetadata();
        List<MusicTrack> tracks = new ArrayList<>();
        try (Stream<Path> stream = Files.list(uploadDir)) {
            stream.filter(Files::isRegularFile)
                    .filter(this::isAudioFile)
                    .sorted(Comparator.comparing(Path::getFileName))
                    .forEach(path -> {
                        String filename = path.getFileName().toString();
                        LegacyMeta meta = metadata.get(filename);
                        String fallbackTitle = filename.replaceFirst("\\.[^.]+$", "");
                        LocalDateTime createdAt = LocalDateTime.now();
                        try {
                            createdAt = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneId.systemDefault());
                        } catch (Exception ignored) {
                        }
                        tracks.add(MusicTrack.builder()
                                .title(meta != null && !isBlank(meta.displayName()) ? meta.displayName() : fallbackTitle)
                                .category(meta != null && !isBlank(meta.category()) ? meta.category() : "单曲")
                                .localAudioUrl("/uploads/" + filename)
                                .sortOrder(meta == null ? 0 : meta.sortOrder())
                                .playCount(0L)
                                .status(1)
                                .createdAt(createdAt)
                                .updatedAt(createdAt)
                                .build());
                    });
        } catch (Exception e) {
            log.warn("读取旧音乐文件失败", e);
            return;
        }
        if (!tracks.isEmpty()) {
            trackRepository.saveAll(tracks);
            log.info("已登记 {} 首现有音乐文件", tracks.size());
        }
    }

    private void validateTrack(MusicTrackCatalogRequest request) {
        if (isBlank(request.getTitle())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请填写歌曲名称");
        }
        if (isBlank(request.getCategory())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请选择歌曲分类");
        }
        if (isBlank(request.getLocalAudioUrl()) && isBlank(request.getQqMusicUrl())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "MP3 和 QQ 音乐链接至少填写一个");
        }
        if (!isBlank(request.getQqMusicUrl()) && !isValidQqMusicUrl(request.getQqMusicUrl())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请输入有效的 QQ 音乐 HTTPS 链接");
        }
    }

    private boolean isValidQqMusicUrl(String value) {
        try {
            URI uri = URI.create(value.trim());
            String host = uri.getHost();
            return "https".equalsIgnoreCase(uri.getScheme()) && host != null
                    && (host.equalsIgnoreCase("y.qq.com") || host.toLowerCase(Locale.ROOT).endsWith(".y.qq.com"));
        } catch (Exception e) {
            return false;
        }
    }

    private Map<String, LegacyMeta> loadLegacyMetadata() {
        Map<String, LegacyMeta> result = new HashMap<>();
        try {
            jdbcTemplate.query("SELECT filename, display_name, seq_no, category FROM file_meta", rs -> {
                result.put(rs.getString("filename"), new LegacyMeta(
                        rs.getString("display_name"), rs.getString("category"), rs.getInt("seq_no")));
            });
        } catch (Exception e) {
            log.info("未找到旧 file_meta，按文件名登记音乐");
        }
        return result;
    }

    private boolean isAudioFile(Path path) {
        String name = path.getFileName().toString();
        int index = name.lastIndexOf('.');
        return index >= 0 && AUDIO_EXTENSIONS.contains(name.substring(index + 1).toLowerCase(Locale.ROOT));
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String trimToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private record LegacyMeta(String displayName, String category, int sortOrder) {
    }
}
