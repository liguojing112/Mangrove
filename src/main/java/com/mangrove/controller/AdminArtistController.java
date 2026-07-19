package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.entity.Artist;
import com.mangrove.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/artist")
@RequiredArgsConstructor
public class AdminArtistController {

    private final ArtistRepository artistRepository;

    @GetMapping
    public Result<Artist> get() {
        Artist artist = artistRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "未找到艺人信息"));
        return Result.success(artist);
    }

    @PutMapping("/{id}")
    public Result<Artist> update(@PathVariable Long id, @RequestBody Artist artist) {
        Artist existing = artistRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "艺人不存在"));

        existing.setAvatarUrl(artist.getAvatarUrl());
        existing.setSignatureImageUrl(artist.getSignatureImageUrl());
        existing.setBrandImageUrl(artist.getBrandImageUrl());
        existing.setBio(artist.getBio());
        existing.setUpdatedAt(java.time.LocalDateTime.now());

        return Result.success(artistRepository.save(existing));
    }
}

