package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.*;
import com.mangrove.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/letters")
@RequiredArgsConstructor
public class LetterController {

    private final LetterNoteRepository letterNoteRepository;
    private final LetterImageRepository letterImageRepository;
    private final SysUserRepository sysUserRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final MediaFileRepository mediaFileRepository;
    private final FanWorkRepository fanWorkRepository;

    @GetMapping
    public Result<List<LetterNote>> list() {
        return Result.success(letterNoteRepository.findByStatusOrderByNoteDateDesc(1));
    }

    @GetMapping("/category/{category}")
    public Result<List<LetterNote>> listByCategory(@PathVariable String category) {
        return Result.success(letterNoteRepository.findByCategoryAndStatusOrderByNoteDateDesc(category, 1));
    }

    @GetMapping("/{id}")
    @Transactional
    public Result<LetterNote> detail(@PathVariable Long id) {
        LetterNote letter = letterNoteRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "来信不存在"));
        letter.setLikes(letter.getLikes() + 1);
        letterNoteRepository.save(letter);
        return Result.success(letter);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public Result<LetterNote> create(@RequestBody Map<String, Object> body) {
        Long artistId = Long.valueOf(body.get("artistId").toString());
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "艺人不存在"));

        String category = "LETTER";
        if (body.get("category") != null) {
            category = body.get("category").toString();
        }

        LocalDate noteDate = null;
        if (body.get("noteDate") != null) {
            noteDate = LocalDate.parse(body.get("noteDate").toString());
        }

        LetterNote letter = LetterNote.builder()
                .artistId(artistId)
                .title(body.get("title").toString())
                .content(body.get("content").toString())
                .category(category)
                .source(body.get("source") != null ? body.get("source").toString() : null)
                .noteDate(noteDate)
                .coverUrl(body.get("coverUrl") != null ? body.get("coverUrl").toString() : null)
                .likes(0)
                .status(1)
                .createdAt(LocalDateTime.now())
                .build();

        letterNoteRepository.save(letter);
        log.info("创建来信: id={}, title={}", letter.getId(), letter.getTitle());
        return Result.success(letter);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public Result<LetterNote> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        LetterNote letter = letterNoteRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "来信不存在"));

        if (body.get("artistId") != null) {
            Long artistId = Long.valueOf(body.get("artistId").toString());
            letter.setArtistId(artistId);
        }
        if (body.get("title") != null) {
            letter.setTitle(body.get("title").toString());
        }
        if (body.get("content") != null) {
            letter.setContent(body.get("content").toString());
        }
        if (body.get("category") != null) {
            letter.setCategory(body.get("category").toString());
        }
        if (body.containsKey("source")) {
            letter.setSource(body.get("source") != null ? body.get("source").toString() : null);
        }
        if (body.containsKey("noteDate")) {
            letter.setNoteDate(body.get("noteDate") != null ? LocalDate.parse(body.get("noteDate").toString()) : null);
        }
        if (body.containsKey("coverUrl")) {
            letter.setCoverUrl(body.get("coverUrl") != null ? body.get("coverUrl").toString() : null);
        }

        letterNoteRepository.save(letter);
        log.info("更新来信: id={}", letter.getId());
        return Result.success(letter);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public Result<Void> delete(@PathVariable Long id) {
        LetterNote letter = letterNoteRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "来信不存在"));
        letterNoteRepository.delete(letter);
        log.info("删除来信: id={}", id);
        return Result.success();
    }

    // ========== 来信图片 ==========

    @GetMapping("/{id}/images")
    public Result<List<LetterImage>> listImages(@PathVariable Long id) {
        List<LetterImage> images = letterImageRepository.findByLetterIdOrderByCreatedAtDesc(id);
        return Result.success(images);
    }

    @PostMapping("/{id}/images")
    @Transactional
    public Result<LetterImage> uploadImage(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication authentication) {
        String username = authentication.getName();
        SysUser user = sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));

        letterNoteRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "来信不存在"));

        String imageUrl = body.get("imageUrl");
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "图片URL不能为空");
        }

        LetterImage image = LetterImage.builder()
                .letterId(id)
                .user(user)
                .imageUrl(imageUrl)
                .createdAt(LocalDateTime.now())
                .build();
        letterImageRepository.save(image);
        return Result.success(image);
    }

    @DeleteMapping("/{letterId}/images/{imageId}")
    @Transactional
    public Result<Void> deleteImage(@PathVariable Long letterId, @PathVariable Long imageId, Authentication authentication) {
        String username = authentication.getName();
        SysUser user = sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));

        LetterImage image = letterImageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "图片不存在"));

        if (!image.getUser().getId().equals(user.getId()) && user.getRole() != SysUser.Role.ADMIN && user.getRole() != SysUser.Role.SUPER_ADMIN) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权删除此图片");
        }

        letterImageRepository.deleteById(imageId);
        return Result.success(null);
    }

    @GetMapping("/artists/{id}/detail")
    public Result<Map<String, Object>> artistDetail(@PathVariable Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "艺人不存在"));

        LocalDate today = LocalDate.now();
        long debutDays = 0;
        if (artist.getDebutDate() != null) {
            debutDays = ChronoUnit.DAYS.between(artist.getDebutDate(), today);
        }
        long age = 0;
        if (artist.getBirthDate() != null) {
            age = ChronoUnit.YEARS.between(artist.getBirthDate(), today);
        }

        long totalPhotos = 0;
        List<Album> albums = albumRepository.findByArtistId(id, Pageable.unpaged());
        for (Album album : albums) {
            List<MediaFile> files = mediaFileRepository.findByAlbumIdOrderBySortOrderAsc(album.getId());
            totalPhotos += files.size();
        }

        long totalLetters = letterNoteRepository.countByArtistIdAndStatus(id, 1);

        long totalWorks = fanWorkRepository.findByArtistId(id, Pageable.unpaged()).stream()
                .filter(fw -> fw.getStatus() == FanWork.Status.PUBLISHED)
                .count();

        Map<String, Object> data = new HashMap<>();
        data.put("id", artist.getId());
        data.put("name", artist.getName());
        data.put("stageName", artist.getStageName());
        data.put("birthDate", artist.getBirthDate() != null ? artist.getBirthDate().toString() : "");
        data.put("debutDate", artist.getDebutDate() != null ? artist.getDebutDate().toString() : "");
        data.put("bio", artist.getBio() != null ? artist.getBio() : "");
        data.put("avatarUrl", artist.getAvatarUrl() != null ? artist.getAvatarUrl() : "");
        data.put("coverUrl", artist.getCoverUrl() != null ? artist.getCoverUrl() : "");
        data.put("debutDays", debutDays);
        data.put("age", age);
        data.put("totalPhotos", totalPhotos);
        data.put("totalLetters", totalLetters);
        data.put("totalWorks", totalWorks);
        return Result.success(data);
    }
}
