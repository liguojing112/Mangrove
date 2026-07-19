package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.repository.SysConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public/config")
@RequiredArgsConstructor
public class PublicConfigController {

    private final SysConfigRepository sysConfigRepository;

    @GetMapping("/homepage")
    public Result<Map<String, String>> getHomepageConfig() {
        Map<String, String> config = new HashMap<>();
        sysConfigRepository.findByConfigKey("homepage_background_url")
                .ifPresent(c -> config.put("backgroundUrl", c.getConfigValue()));
        return Result.success(config);
    }
}
