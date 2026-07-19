package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.entity.WorkSectionItem;
import com.mangrove.repository.WorkSectionItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/work-sections")
@RequiredArgsConstructor
public class PublicWorkSectionController {

    private final WorkSectionItemRepository workSectionItemRepository;

    @GetMapping("/{section}")
    public Result<List<WorkSectionItem>> listItems(@PathVariable String section) {
        WorkSectionItem.Section sec = WorkSectionItem.Section.valueOf(section.toUpperCase());
        List<WorkSectionItem> items = workSectionItemRepository.findBySectionOrderBySortOrderAsc(sec);
        return Result.success(items);
    }
}
