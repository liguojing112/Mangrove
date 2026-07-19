package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.entity.SysConfig;
import com.mangrove.repository.SysConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/config")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminConfigController {

    private final SysConfigRepository sysConfigRepository;

    @GetMapping
    public Result<Map<String, String>> getAll() {
        Map<String, String> config = new HashMap<>();
        sysConfigRepository.findAll().forEach(c -> config.put(c.getConfigKey(), c.getConfigValue()));
        return Result.success(config);
    }

    @GetMapping("/{key}")
    public Result<String> get(@PathVariable String key) {
        return sysConfigRepository.findByConfigKey(key)
                .map(c -> Result.success(c.getConfigValue()))
                .orElse(Result.success(null));
    }

    @PutMapping("/{key}")
    @Transactional
    public Result<Void> put(@PathVariable String key, @RequestBody Map<String, String> body) {
        String value = body.get("value");
        SysConfig config = sysConfigRepository.findByConfigKey(key)
                .orElse(SysConfig.builder().configKey(key).build());
        config.setConfigValue(value);
        config.setUpdatedAt(LocalDateTime.now());
        sysConfigRepository.save(config);
        return Result.success();
    }
}
