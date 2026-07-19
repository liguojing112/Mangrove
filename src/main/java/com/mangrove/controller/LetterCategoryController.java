package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.entity.LetterCategory;
import com.mangrove.repository.LetterCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/letter-categories")
@RequiredArgsConstructor
public class LetterCategoryController {

    private final LetterCategoryRepository categoryRepository;

    @GetMapping
    public Result<List<LetterCategory>> listAll() {
        return Result.success(categoryRepository.findByStatusOrderBySortOrderAsc(1));
    }
}
