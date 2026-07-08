package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.dto.request.MerchandiseRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Merchandise;
import com.mangrove.service.MerchandiseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/merchandise")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MerchandiseController {

    private final MerchandiseService merchandiseService;

    @GetMapping
    public Result<PageResult<Merchandise>> list(@RequestParam(defaultValue = "0") Integer page,
                                                @RequestParam(defaultValue = "10") Integer size,
                                                @RequestParam(required = false) String category,
                                                @RequestParam(required = false) String rarity) {
        return Result.success(merchandiseService.list(page, size, category, rarity));
    }

    @GetMapping("/{id}")
    public Result<Merchandise> getById(@PathVariable Long id) {
        return Result.success(merchandiseService.getById(id));
    }

    @PostMapping
    public Result<Merchandise> create(@Valid @RequestBody MerchandiseRequest req) {
        return Result.success(merchandiseService.create(req));
    }

    @PutMapping("/{id}")
    public Result<Merchandise> update(@PathVariable Long id,
                                      @Valid @RequestBody MerchandiseRequest req) {
        return Result.success(merchandiseService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        merchandiseService.delete(id);
        return Result.success();
    }
}
