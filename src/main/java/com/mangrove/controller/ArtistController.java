package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.dto.request.ArtistRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Artist;
import com.mangrove.service.ArtistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/artists")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping
    public Result<PageResult<Artist>> list(@RequestParam(defaultValue = "0") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(artistService.list(page, size));
    }

    @GetMapping("/all")
    public Result<List<Artist>> listAll() {
        return Result.success(artistService.listAll());
    }

    @GetMapping("/{id}")
    public Result<Artist> getById(@PathVariable Long id) {
        return Result.success(artistService.getById(id));
    }

    @PostMapping
    public Result<Artist> create(@Valid @RequestBody ArtistRequest req) {
        return Result.success(artistService.create(req));
    }

    @PutMapping("/{id}")
    public Result<Artist> update(@PathVariable Long id,
                                 @Valid @RequestBody ArtistRequest req) {
        return Result.success(artistService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        artistService.delete(id);
        return Result.success();
    }
}
