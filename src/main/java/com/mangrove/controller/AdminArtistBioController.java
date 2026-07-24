package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.entity.Artist;
import com.mangrove.entity.ArtistBioSection;
import com.mangrove.repository.ArtistBioSectionRepository;
import com.mangrove.repository.ArtistRepository;
import com.mangrove.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminArtistBioController {

    private final ArtistBioSectionRepository bioRepository;
    private final ArtistRepository artistRepository;
    private final SysUserRepository sysUserRepository;

    // ── 公开端 ──

    @PostMapping("/api/artist-bio/ask")
    public Result<ArtistBioSection> askQuestion(
            @RequestBody java.util.Map<String, String> body,
            org.springframework.security.core.Authentication authentication) {
        String question = body.get("question");
        if (question == null || question.isBlank()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "问题不能为空");
        }
        Artist artist = artistRepository.findAll().stream().findFirst().orElse(null);
        if (artist == null) throw new BusinessException(ResultCode.NOT_FOUND, "未找到艺人");

        Long userId = null;
        String nickname = null;
        if (authentication != null) {
            var user = sysUserRepository.findByUsername(authentication.getName());
            if (user.isPresent()) {
                userId = user.get().getId();
                nickname = user.get().getNickname();
            }
        }

        ArtistBioSection bio = ArtistBioSection.builder()
                .artistId(artist.getId()).userId(userId).askerNickname(nickname).question(question)
                .answer("").status(1).sortOrder((int) bioRepository.countByArtistId(artist.getId()))
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        return Result.success(bioRepository.save(bio));
    }

    @GetMapping("/api/artist-bio/my-questions")
    public Result<List<ArtistBioSection>> getMyQuestions(
            org.springframework.security.core.Authentication authentication) {
        if (authentication == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        Long userId = sysUserRepository.findByUsername(authentication.getName())
                .map(u -> u.getId()).orElse(null);
        if (userId == null) throw new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在");
        return Result.success(bioRepository.findByUserIdOrderByCreatedAtDesc(userId));
    }

    // ── 管理端 ──

    @GetMapping("/api/admin/artist-bio")
    public Result<List<ArtistBioSection>> list() {
        Artist artist = artistRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "未找到艺人信息"));
        return Result.success(bioRepository.findByArtistIdAndStatusOrderBySortOrderAsc(artist.getId(), 1));
    }

    @PostMapping("/api/admin/artist-bio")
    public Result<ArtistBioSection> create(@RequestBody ArtistBioSection bioSection) {
        Artist artist = artistRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "未找到艺人信息"));
        bioSection.setArtistId(artist.getId());
        bioSection.setStatus(1);
        bioSection.setCreatedAt(LocalDateTime.now());
        bioSection.setUpdatedAt(LocalDateTime.now());
        if (bioSection.getSortOrder() == null) {
            bioSection.setSortOrder((int) bioRepository.countByArtistId(artist.getId()));
        }
        return Result.success(bioRepository.save(bioSection));
    }

    @PutMapping("/api/admin/artist-bio/{id}")
    public Result<ArtistBioSection> update(@PathVariable Long id, @RequestBody ArtistBioSection bioSection) {
        ArtistBioSection existing = bioRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "组件不存在"));
        existing.setQuestion(bioSection.getQuestion());
        existing.setAnswer(bioSection.getAnswer());
        if (bioSection.getSortOrder() != null) existing.setSortOrder(bioSection.getSortOrder());
        existing.setUpdatedAt(LocalDateTime.now());
        return Result.success(bioRepository.save(existing));
    }

    @PutMapping("/api/admin/artist-bio/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        ArtistBioSection existing = bioRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "组件不存在"));
        existing.setStatus(status);
        existing.setUpdatedAt(LocalDateTime.now());
        bioRepository.save(existing);
        return Result.success();
    }

    @DeleteMapping("/api/admin/artist-bio/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        ArtistBioSection existing = bioRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "组件不存在"));
        bioRepository.delete(existing);
        return Result.success();
    }
}
