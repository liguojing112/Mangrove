package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.entity.Lottery;
import com.mangrove.service.LotteryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/lotteries")
@RequiredArgsConstructor
public class PublicLotteryController {

    private final LotteryService lotteryService;

    @GetMapping
    public Result<List<Lottery>> list() {
        return Result.success(lotteryService.listPublic());
    }

    @GetMapping("/{id}")
    public Result<Lottery> getById(@PathVariable Long id) {
        return Result.success(lotteryService.getById(id));
    }
}
