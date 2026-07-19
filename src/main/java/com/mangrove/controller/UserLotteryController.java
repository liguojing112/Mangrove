package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.request.LotteryRequest;
import com.mangrove.entity.Lottery;
import com.mangrove.entity.SysUser;
import com.mangrove.repository.SysUserRepository;
import com.mangrove.service.LotteryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/lotteries")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserLotteryController {

    private final LotteryService lotteryService;
    private final SysUserRepository sysUserRepository;

    @PostMapping
    public Result<Lottery> create(@Valid @RequestBody LotteryRequest req, Authentication auth) {
        SysUser user = sysUserRepository.findByUsername(auth.getName()).orElseThrow();
        return Result.success(lotteryService.create(req, user.getId(), user.getNickname()));
    }

    @PutMapping("/{id}")
    public Result<Lottery> update(@PathVariable Long id, @Valid @RequestBody LotteryRequest req, Authentication auth) {
        Lottery lottery = lotteryService.getById(id);
        SysUser user = sysUserRepository.findByUsername(auth.getName()).orElseThrow();
        if (!lottery.getCreatedBy().equals(user.getId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只能编辑自己的抽奖");
        }
        return Result.success(lotteryService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, Authentication auth) {
        Lottery lottery = lotteryService.getById(id);
        SysUser user = sysUserRepository.findByUsername(auth.getName()).orElseThrow();
        if (!lottery.getCreatedBy().equals(user.getId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只能删除自己的抽奖");
        }
        lotteryService.delete(id);
        return Result.success();
    }

    @PostMapping("/{id}/enter")
    public Result<?> enter(@PathVariable Long id, Authentication auth) {
        SysUser user = sysUserRepository.findByUsername(auth.getName()).orElseThrow();
        lotteryService.enter(id, user.getId(), user.getNickname());
        return Result.success();
    }

    @GetMapping("/{id}/status")
    public Result<Map<String, Boolean>> status(@PathVariable Long id, Authentication auth) {
        SysUser user = sysUserRepository.findByUsername(auth.getName()).orElseThrow();
        boolean entered = lotteryService.hasEntered(id, user.getId());
        return Result.success(Map.of("entered", entered));
    }
}
