package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.entity.LetterNote;
import com.mangrove.repository.LetterNoteRepository;
import com.mangrove.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/letters")
@RequiredArgsConstructor
public class AdminLetterController {

    private final LetterNoteRepository letterRepository;
    private final ArtistRepository artistRepository;

    @GetMapping
    public Result<List<LetterNote>> listAll() {
        return Result.success(letterRepository.findAll());
    }

    @PostMapping
    public Result<LetterNote> create(@RequestBody Map<String, Object> body) {
        LetterNote letter = new LetterNote();

        // Set artist (use first artist by default)
        letter.setArtistId(artistRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("未找到艺人信息")).getId());

        letter.setTitle(body.get("title").toString());
        letter.setContent(body.get("content").toString());

        // Parse category
        String categoryStr = body.get("category").toString();
        letter.setCategory(categoryStr);

        if (body.get("source") != null) {
            letter.setSource(body.get("source").toString());
        }

        if (body.get("noteDate") != null && !body.get("noteDate").toString().isEmpty()) {
            letter.setNoteDate(LocalDate.parse(body.get("noteDate").toString()));
        }
        if (body.get("coverUrl") != null && !body.get("coverUrl").toString().isBlank()) {
            letter.setCoverUrl(body.get("coverUrl").toString().trim());
        }

        letter.setLikes(0);
        letter.setStatus(1);
        letter.setCreatedAt(LocalDateTime.now());

        return Result.success(letterRepository.save(letter));
    }

    @PutMapping("/{id}")
    public Result<LetterNote> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        LetterNote existing = letterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("来信不存在"));

        if (body.get("title") != null) {
            existing.setTitle(body.get("title").toString());
        }
        if (body.get("content") != null) {
            existing.setContent(body.get("content").toString());
        }
        if (body.get("category") != null) {
            existing.setCategory(body.get("category").toString());
        }
        if (body.containsKey("source")) {
            existing.setSource(body.get("source") != null ? body.get("source").toString() : null);
        }
        if (body.containsKey("noteDate")) {
            existing.setNoteDate(body.get("noteDate") != null && !body.get("noteDate").toString().isEmpty()
                    ? LocalDate.parse(body.get("noteDate").toString()) : null);
        }
        if (body.containsKey("coverUrl")) {
            existing.setCoverUrl(body.get("coverUrl") != null && !body.get("coverUrl").toString().isBlank()
                    ? body.get("coverUrl").toString().trim() : null);
        }

        return Result.success(letterRepository.save(existing));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        LetterNote letter = letterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("来信不存在"));
        letter.setStatus(status);
        letterRepository.save(letter);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        letterRepository.deleteById(id);
        return Result.success();
    }
}
