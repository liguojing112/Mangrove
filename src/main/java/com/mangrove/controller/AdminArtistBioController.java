package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.entity.Artist;
import com.mangrove.entity.ArtistBioSection;
import com.mangrove.repository.ArtistBioSectionRepository;
import com.mangrove.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/artist-bio")
@RequiredArgsConstructor
public class AdminArtistBioController {

    private final ArtistBioSectionRepository bioRepository;
    private final ArtistRepository artistRepository;

    @GetMapping
    public Result<List<ArtistBioSection>> list() {
        Artist artist = artistRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "未找到艺人信息"));
        return Result.success(bioRepository.findByArtistIdAndStatusOrderBySortOrderAsc(artist.getId(), 1));
    }

    @PostMapping
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

    @PutMapping("/{id}")
    public Result<ArtistBioSection> update(@PathVariable Long id, @RequestBody ArtistBioSection bioSection) {
        ArtistBioSection existing = bioRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "组件不存在"));

        existing.setQuestion(bioSection.getQuestion());
        existing.setAnswer(bioSection.getAnswer());
        existing.setSortOrder(bioSection.getSortOrder());
        existing.setUpdatedAt(LocalDateTime.now());

        return Result.success(bioRepository.save(existing));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        ArtistBioSection existing = bioRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "组件不存在"));
        existing.setStatus(status);
        existing.setUpdatedAt(LocalDateTime.now());
        bioRepository.save(existing);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        ArtistBioSection existing = bioRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "组件不存在"));
        bioRepository.delete(existing);
        return Result.success();
    }
}
