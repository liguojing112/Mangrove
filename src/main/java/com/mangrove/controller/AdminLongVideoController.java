package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.dto.request.LongVideoRequest;
import com.mangrove.entity.LongVideo;
import com.mangrove.service.LongVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/long-videos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminLongVideoController {

    private final LongVideoService service;

    @GetMapping
    public Result<List<LongVideo>> list() {
        return Result.success(service.listAdmin());
    }

    @PostMapping
    public Result<LongVideo> create(@RequestBody LongVideoRequest request) {
        return Result.success(service.save(null, request));
    }

    @PutMapping("/{id}")
    public Result<LongVideo> update(@PathVariable Long id, @RequestBody LongVideoRequest request) {
        return Result.success(service.save(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.success();
    }
}
