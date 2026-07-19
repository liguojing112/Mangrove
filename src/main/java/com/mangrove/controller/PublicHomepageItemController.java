package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.entity.HomepageItem;
import com.mangrove.repository.HomepageItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/homepage-items")
@RequiredArgsConstructor
public class PublicHomepageItemController {

    private final HomepageItemRepository homepageItemRepository;

    @GetMapping("/{section}")
    public Result<List<HomepageItem>> listBySection(@PathVariable String section) {
        HomepageItem.Section sec = HomepageItem.Section.valueOf(section.toUpperCase());
        List<HomepageItem> items = homepageItemRepository.findBySectionOrderBySortOrderAsc(sec);
        return Result.success(items);
    }
}
