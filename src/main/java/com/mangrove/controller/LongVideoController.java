package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.entity.LongVideo;
import com.mangrove.service.LongVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/long-videos")
@RequiredArgsConstructor
public class LongVideoController {

    private final LongVideoService service;

    @GetMapping
    public Result<List<LongVideo>> list(@RequestParam(required = false) String category) {
        return Result.success(service.listPublic(category));
    }
}
