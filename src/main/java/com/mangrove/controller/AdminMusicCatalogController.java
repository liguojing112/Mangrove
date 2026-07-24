package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.dto.request.MusicAlbumPhotoRequest;
import com.mangrove.dto.request.MusicPhotoAlbumRequest;
import com.mangrove.dto.request.MusicPhotoOrderRequest;
import com.mangrove.dto.request.MusicTrackCatalogRequest;
import com.mangrove.dto.response.MusicAlbumDetailResponse;
import com.mangrove.entity.MusicAlbumPhoto;
import com.mangrove.entity.MusicPhotoAlbum;
import com.mangrove.entity.MusicTrack;
import com.mangrove.entity.SysUser;
import com.mangrove.service.MusicCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/music")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminMusicCatalogController {

    private final MusicCatalogService musicCatalogService;

    private boolean isSuperAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
    }

    @GetMapping("/tracks")
    public Result<List<MusicTrack>> tracks() {
        return Result.success(musicCatalogService.listAdminTracks());
    }

    @PostMapping("/tracks")
    public Result<MusicTrack> createTrack(@RequestBody MusicTrackCatalogRequest request, Authentication auth) {
        if (!isSuperAdmin(auth)) request.setStatus(0);
        return Result.success(musicCatalogService.saveTrack(null, request));
    }

    @PutMapping("/tracks/{id}")
    public Result<MusicTrack> updateTrack(@PathVariable Long id, @RequestBody MusicTrackCatalogRequest request, Authentication auth) {
        if (!isSuperAdmin(auth)) request.setStatus(0);
        return Result.success(musicCatalogService.saveTrack(id, request));
    }

    @DeleteMapping("/tracks/{id}")
    public Result<Void> deleteTrack(@PathVariable Long id) {
        musicCatalogService.deleteTrack(id);
        return Result.success();
    }

    @GetMapping("/albums")
    public Result<List<MusicPhotoAlbum>> albums() {
        return Result.success(musicCatalogService.listAdminAlbums());
    }

    @GetMapping("/albums/{id}")
    public Result<MusicAlbumDetailResponse> album(@PathVariable Long id) {
        return Result.success(musicCatalogService.getAlbum(id, true));
    }

    @PostMapping("/albums")
    public Result<MusicPhotoAlbum> createAlbum(@RequestBody MusicPhotoAlbumRequest request) {
        return Result.success(musicCatalogService.saveAlbum(null, request));
    }

    @PutMapping("/albums/{id}")
    public Result<MusicPhotoAlbum> updateAlbum(@PathVariable Long id, @RequestBody MusicPhotoAlbumRequest request) {
        return Result.success(musicCatalogService.saveAlbum(id, request));
    }

    @DeleteMapping("/albums/{id}")
    public Result<Void> deleteAlbum(@PathVariable Long id) {
        musicCatalogService.deleteAlbum(id);
        return Result.success();
    }

    @PostMapping("/albums/{id}/photos")
    public Result<MusicAlbumPhoto> addPhoto(@PathVariable Long id, @RequestBody MusicAlbumPhotoRequest request) {
        return Result.success(musicCatalogService.addPhoto(id, request));
    }

    @PutMapping("/albums/{id}/photos/order")
    public Result<Void> reorderPhotos(@PathVariable Long id, @RequestBody MusicPhotoOrderRequest request) {
        musicCatalogService.reorderPhotos(id, request);
        return Result.success();
    }

    @DeleteMapping("/albums/{albumId}/photos/{photoId}")
    public Result<Void> deletePhoto(@PathVariable Long albumId, @PathVariable Long photoId) {
        musicCatalogService.deletePhoto(albumId, photoId);
        return Result.success();
    }
}
