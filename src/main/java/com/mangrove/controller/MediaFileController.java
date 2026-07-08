package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.dto.request.MediaFileRequest;
import com.mangrove.entity.MediaFile;
import com.mangrove.service.MediaFileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/media")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MediaFileController {

    private final MediaFileService mediaFileService;

    @GetMapping("/album/{albumId}")
    public Result<List<MediaFile>> listByAlbum(@PathVariable Long albumId) {
        return Result.success(mediaFileService.listByAlbum(albumId));
    }

    @GetMapping("/{id}")
    public Result<MediaFile> getById(@PathVariable Long id) {
        return Result.success(mediaFileService.getById(id));
    }

    @PostMapping
    public Result<MediaFile> create(@Valid @RequestBody MediaFileRequest req) {
        return Result.success(mediaFileService.create(req));
    }

    @PutMapping("/{id}")
    public Result<MediaFile> update(@PathVariable Long id,
                                    @Valid @RequestBody MediaFileRequest req) {
        return Result.success(mediaFileService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        mediaFileService.delete(id);
        return Result.success();
    }

    @DeleteMapping("/album/{albumId}")
    public Result<Void> deleteByAlbum(@PathVariable Long albumId) {
        mediaFileService.deleteByAlbum(albumId);
        return Result.success();
    }
}
