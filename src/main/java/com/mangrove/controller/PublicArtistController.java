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

import java.util.List;

@RestController
@RequestMapping("/api/public/artists")
@RequiredArgsConstructor
public class PublicArtistController {

    private final ArtistRepository artistRepository;
    private final ArtistBioSectionRepository bioRepository;

    @GetMapping("/{id}")
    public Result<Artist> get(@PathVariable Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "未找到艺人信息"));
        return Result.success(artist);
    }

    @GetMapping("/{id}/bio")
    public Result<List<ArtistBioSection>> getBio(@PathVariable Long id) {
        return Result.success(bioRepository.findByArtistIdAndStatusOrderBySortOrderAsc(id, 1));
    }
}
