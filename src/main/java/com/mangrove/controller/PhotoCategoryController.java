package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.entity.PhotoCategory;
import com.mangrove.repository.PhotoCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/photo-categories")
@RequiredArgsConstructor
public class PhotoCategoryController {

    private final PhotoCategoryRepository photoCategoryRepository;

    /** 前台获取所有分类 */
    @GetMapping
    public Result<List<PhotoCategory>> list() {
        return Result.success(photoCategoryRepository.findAllByOrderBySortOrderAsc());
    }

    /** 后台新增分类 */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<PhotoCategory> create(@RequestBody PhotoCategory category) {
        return Result.success(photoCategoryRepository.save(category));
    }

    /** 后台修改分类 */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<PhotoCategory> update(@PathVariable Long id, @RequestBody PhotoCategory category) {
        PhotoCategory exist = photoCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "分类不存在"));
        exist.setName(category.getName());
        exist.setSortOrder(category.getSortOrder());
        return Result.success(photoCategoryRepository.save(exist));
    }

    /** 后台删除分类 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<String> delete(@PathVariable Long id) {
        photoCategoryRepository.deleteById(id);
        return Result.success("已删除");
    }
}
