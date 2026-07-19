package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.request.LongVideoCategoryRequest;
import com.mangrove.entity.LongVideoCategory;
import com.mangrove.repository.LongVideoCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/long-video-categories")
@RequiredArgsConstructor
public class LongVideoCategoryController {

    private final LongVideoCategoryRepository repository;

    @GetMapping
    public Result<List<LongVideoCategory>> list() {
        return Result.success(repository.findAllByOrderBySortOrderAscIdAsc());
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<LongVideoCategory> create(@RequestBody LongVideoCategoryRequest request) {
        String name = request.getName() == null ? "" : request.getName().trim();
        if (name.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请填写分类名称");
        }
        if (name.length() > 50) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "分类名称不能超过 50 个字");
        }
        if (repository.existsByName(name)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该分类已存在");
        }
        int sortOrder = repository.findAllByOrderBySortOrderAscIdAsc().size();
        return Result.success(repository.save(LongVideoCategory.builder()
                .name(name)
                .sortOrder(sortOrder)
                .build()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "分类不存在");
        }
        repository.deleteById(id);
        return Result.success();
    }
}
