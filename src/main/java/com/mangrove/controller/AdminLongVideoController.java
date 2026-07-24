package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.dto.request.LongVideoRequest;
import com.mangrove.entity.LongVideo;
import com.mangrove.service.LongVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/long-videos")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminLongVideoController {

    private final LongVideoService service;

    private boolean isSuperAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
    }

    @GetMapping
    public Result<List<LongVideo>> list() {
        return Result.success(service.listAdmin());
    }

    @PostMapping
    public Result<LongVideo> create(@RequestBody LongVideoRequest request, Authentication auth) {
        if (!isSuperAdmin(auth)) request.setStatus(0);
        return Result.success(service.save(null, request));
    }

    @PutMapping("/{id}")
    public Result<LongVideo> update(@PathVariable Long id, @RequestBody LongVideoRequest request, Authentication auth) {
        if (!isSuperAdmin(auth)) request.setStatus(0);
        return Result.success(service.save(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.success();
    }
}
