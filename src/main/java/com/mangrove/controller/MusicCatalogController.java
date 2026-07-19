package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.dto.response.MusicAlbumDetailResponse;
import com.mangrove.entity.MusicPhotoAlbum;
import com.mangrove.entity.MusicTrack;
import com.mangrove.service.MusicCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicCatalogController {

    private final MusicCatalogService musicCatalogService;

    @GetMapping("/tracks")
    public Result<List<MusicTrack>> tracks(@RequestParam(required = false) String category) {
        return Result.success(musicCatalogService.listPublicTracks(category));
    }

    @PostMapping("/tracks/{id}/play")
    public Result<Void> recordPlay(@PathVariable Long id) {
        musicCatalogService.incrementPlay(id);
        return Result.success();
    }

    @GetMapping("/albums")
    public Result<List<MusicPhotoAlbum>> albums() {
        return Result.success(musicCatalogService.listPublicAlbums());
    }

    @GetMapping("/albums/{id}")
    public Result<MusicAlbumDetailResponse> album(@PathVariable Long id) {
        return Result.success(musicCatalogService.getAlbum(id, false));
    }
}
