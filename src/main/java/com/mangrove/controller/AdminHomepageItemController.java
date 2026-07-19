package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.entity.HomepageItem;
import com.mangrove.repository.HomepageItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/homepage-items")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminHomepageItemController {

    private final HomepageItemRepository homepageItemRepository;

    @GetMapping("/{section}")
    public Result<List<HomepageItem>> listBySection(@PathVariable String section) {
        HomepageItem.Section sec = HomepageItem.Section.valueOf(section.toUpperCase());
        List<HomepageItem> items = homepageItemRepository.findBySectionOrderBySortOrderAsc(sec);
        return Result.success(items);
    }

    @PostMapping("/{section}")
    @Transactional
    public Result<HomepageItem> addItem(@PathVariable String section, @RequestBody HomepageItem item) {
        HomepageItem.Section sec = HomepageItem.Section.valueOf(section.toUpperCase());
        item.setSection(sec);
        item.setCreatedAt(LocalDateTime.now());
        if (item.getSortOrder() == null) {
            item.setSortOrder(0);
        }
        return Result.success(homepageItemRepository.save(item));
    }

    @PutMapping("/{id}/sort")
    @Transactional
    public Result<Void> updateSortOrder(@PathVariable Long id, @RequestParam Integer sortOrder) {
        HomepageItem item = homepageItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "项目不存在"));
        item.setSortOrder(sortOrder);
        homepageItemRepository.save(item);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> removeItem(@PathVariable Long id) {
        homepageItemRepository.deleteById(id);
        return Result.success();
    }
}
