package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.entity.LetterCategory;
import com.mangrove.repository.LetterCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/letter-categories")
@RequiredArgsConstructor
public class AdminLetterCategoryController {

    private final LetterCategoryRepository categoryRepository;

    @GetMapping
    public Result<List<LetterCategory>> listAll() {
        return Result.success(categoryRepository.findAll());
    }

    @PostMapping
    public Result<LetterCategory> create(@RequestBody LetterCategory category) {
        category.setStatus(1);
        return Result.success(categoryRepository.save(category));
    }

    @PutMapping("/{id}")
    public Result<LetterCategory> update(@PathVariable Long id, @RequestBody LetterCategory category) {
        LetterCategory existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        existing.setName(category.getName());
        existing.setCode(category.getCode());
        existing.setSortOrder(category.getSortOrder());
        return Result.success(categoryRepository.save(existing));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        LetterCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        category.setStatus(status);
        categoryRepository.save(category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return Result.success();
    }
}
