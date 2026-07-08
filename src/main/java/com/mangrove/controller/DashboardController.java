package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.entity.MediaAsset;
import com.mangrove.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class DashboardController {

    private final ArtistRepository artistRepository;
    private final FanWorkRepository fanWorkRepository;
    private final MerchandiseRepository merchandiseRepository;
    private final SysUserRepository sysUserRepository;
    private final MediaAssetRepository mediaAssetRepository;

    @GetMapping
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new LinkedHashMap<>();

        // 统计数据
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("artistCount", artistRepository.count());
        stats.put("workCount", fanWorkRepository.count());
        stats.put("merchandiseCount", merchandiseRepository.count());
        stats.put("userCount", sysUserRepository.count());
        stats.put("photoCount", mediaAssetRepository.countByCategoryAndStatus(
                MediaAsset.Category.PHOTO, 1));
        stats.put("videoCount", mediaAssetRepository.countByCategoryAndStatus(
                MediaAsset.Category.VIDEO, 1));
        data.put("stats", stats);

        // 最近上传
        List<MediaAsset> recent = mediaAssetRepository.findTop6ByStatusAndIsDeletedOrderByCreatedAtDesc(1, 0);
        List<Map<String, Object>> recentList = new ArrayList<>();
        for (MediaAsset m : recent) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", m.getId());
            item.put("title", m.getTitle());
            item.put("category", m.getCategory().name());
            item.put("createdAt", m.getCreatedAt());
            recentList.add(item);
        }
        data.put("recentUploads", recentList);

        return Result.success(data);
    }
}
