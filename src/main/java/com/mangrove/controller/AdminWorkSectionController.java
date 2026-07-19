package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.entity.WorkSectionItem;
import com.mangrove.repository.WorkSectionItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/work-sections")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminWorkSectionController {

    private final WorkSectionItemRepository workSectionItemRepository;

    @GetMapping("/{section}")
    public Result<List<WorkSectionItem>> listItems(@PathVariable String section) {
        WorkSectionItem.Section sec = WorkSectionItem.Section.valueOf(section.toUpperCase());
        List<WorkSectionItem> items = workSectionItemRepository.findBySectionOrderBySortOrderAsc(sec);
        return Result.success(items);
    }

    @PostMapping("/{section}")
    public Result<WorkSectionItem> addItem(@PathVariable String section, @RequestBody WorkSectionItem item) {
        WorkSectionItem.Section sec = WorkSectionItem.Section.valueOf(section.toUpperCase());
        item.setSection(sec);
        item.setCreatedAt(LocalDateTime.now());
        if (item.getSortOrder() == null) {
            item.setSortOrder(0);
        }
        return Result.success(workSectionItemRepository.save(item));
    }

    @PutMapping("/{id}/sort")
    public Result<Void> updateSortOrder(@PathVariable Long id, @RequestParam Integer sortOrder) {
        WorkSectionItem item = workSectionItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "项目不存在"));
        item.setSortOrder(sortOrder);
        workSectionItemRepository.save(item);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> removeItem(@PathVariable Long id) {
        workSectionItemRepository.deleteById(id);
        return Result.success();
    }
}
