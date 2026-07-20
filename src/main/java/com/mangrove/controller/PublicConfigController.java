package com.mangrove.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangrove.common.Result;
import com.mangrove.repository.SysConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public/config")
@RequiredArgsConstructor
public class PublicConfigController {

    private final SysConfigRepository sysConfigRepository;
    private final ObjectMapper objectMapper;

    @GetMapping("/homepage")
    public Result<Map<String, String>> getHomepageConfig() {
        Map<String, String> config = new HashMap<>();
        sysConfigRepository.findByConfigKey("homepage_background_url")
                .ifPresent(c -> config.put("backgroundUrl", c.getConfigValue()));
        return Result.success(config);
    }

    @GetMapping("/photos-hero-cards")
    public Result<List<String>> getPhotosHeroCards() {
        List<String> urls = sysConfigRepository.findByConfigKey("photos_hero_cards")
                .map(c -> {
                    try {
                        return objectMapper.readValue(c.getConfigValue(), new TypeReference<List<String>>() {});
                    } catch (Exception e) {
                        return List.<String>of();
                    }
                })
                .orElse(List.of());
        return Result.success(urls);
    }
}
