package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.dto.request.LotteryRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Lottery;
import com.mangrove.entity.LotteryEntry;
import com.mangrove.entity.SysUser;
import com.mangrove.repository.SysUserRepository;
import com.mangrove.service.LotteryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/lotteries")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminLotteryController {

    private final LotteryService lotteryService;
    private final SysUserRepository sysUserRepository;

    @GetMapping
    public Result<PageResult<Lottery>> list(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "15") int size) {
        return Result.success(lotteryService.list(page, size));
    }

    @GetMapping("/{id}")
    public Result<Lottery> getById(@PathVariable Long id) {
        return Result.success(lotteryService.getById(id));
    }

    @PostMapping
    public Result<Lottery> create(@Valid @RequestBody LotteryRequest req, Authentication auth) {
        SysUser user = sysUserRepository.findByUsername(auth.getName()).orElseThrow();
        return Result.success(lotteryService.create(req, user.getId(), user.getNickname()));
    }

    @PutMapping("/{id}")
    public Result<Lottery> update(@PathVariable Long id, @Valid @RequestBody LotteryRequest req) {
        return Result.success(lotteryService.update(id, req));
    }

    @PutMapping("/{id}/status")
    public Result<Lottery> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        return Result.success(lotteryService.toggleStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        lotteryService.delete(id);
        return Result.success();
    }

    @PostMapping("/{id}/draw")
    public Result<Lottery> draw(@PathVariable Long id) {
        return Result.success(lotteryService.draw(id));
    }

    @GetMapping("/{id}/entries")
    public Result<PageResult<LotteryEntry>> listEntries(@PathVariable Long id,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "50") int size) {
        return Result.success(lotteryService.listEntries(id, page, size));
    }
}
