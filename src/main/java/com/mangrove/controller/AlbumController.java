package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.dto.request.AlbumRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Album;
import com.mangrove.entity.SysUser;
import com.mangrove.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/albums")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping
    public Result<PageResult<Album>> list(@RequestParam(defaultValue = "0") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam(required = false) String category) {
        return Result.success(albumService.list(page, size, category));
    }

    @GetMapping("/{id}")
    public Result<Album> getById(@PathVariable Long id) {
        return Result.success(albumService.getById(id));
    }

    @PostMapping
    public Result<Album> create(@Valid @RequestBody AlbumRequest req,
                                @AuthenticationPrincipal SysUser sysUser) {
        return Result.success(albumService.create(req, sysUser.getId()));
    }

    @PutMapping("/{id}")
    public Result<Album> update(@PathVariable Long id,
                                @Valid @RequestBody AlbumRequest req) {
        return Result.success(albumService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        albumService.delete(id);
        return Result.success();
    }
}
