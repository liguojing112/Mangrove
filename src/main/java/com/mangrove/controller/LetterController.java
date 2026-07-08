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
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final MediaFileRepository mediaFileRepository;
    private final FanWorkRepository fanWorkRepository;

    @GetMapping
    public Result<PageResult<LetterNote>> list(
            @RequestParam(required = false) Long artistId,
            @RequestParam(required = false) LetterNote.Category category,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable;
        List<LetterNote> letters;

        if (artistId != null) {
            pageable = PageRequest.of(page, size);
            letters = letterNoteRepository.findByArtistIdAndStatusOrderByNoteDateDesc(artistId, 1, pageable);
        } else if (category != null) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "noteDate"));
            letters = letterNoteRepository.findByCategoryAndStatus(category, 1, pageable);
        } else {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "noteDate"));
            letters = letterNoteRepository.findByStatus(1, pageable);
        }

        PageResult<LetterNote> result = PageResult.<LetterNote>builder()
                .content(letters)
                .totalElements(letters.size())
                .totalPages(1)
                .currentPage(page)
                .size(size)
                .build();
        return Result.success(result);
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

        LetterNote.Category category = LetterNote.Category.LETTER;
        if (body.get("category") != null) {
            try {
                category = LetterNote.Category.valueOf(body.get("category").toString().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "无效的分类参数");
            }
        }

        LocalDate noteDate = null;
        if (body.get("noteDate") != null) {
            noteDate = LocalDate.parse(body.get("noteDate").toString());
        }

        LetterNote letter = LetterNote.builder()
                .artist(artist)
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
            Artist artist = artistRepository.findById(artistId)
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "艺人不存在"));
            letter.setArtist(artist);
        }
        if (body.get("title") != null) {
            letter.setTitle(body.get("title").toString());
        }
        if (body.get("content") != null) {
            letter.setContent(body.get("content").toString());
        }
        if (body.get("category") != null) {
            try {
                letter.setCategory(LetterNote.Category.valueOf(body.get("category").toString().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "无效的分类参数");
            }
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
