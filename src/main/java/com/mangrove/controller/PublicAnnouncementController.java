package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.entity.Announcement;
import com.mangrove.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/announcements")
@RequiredArgsConstructor
public class PublicAnnouncementController {

    private final AnnouncementRepository announcementRepository;

    @GetMapping
    public Result<List<Announcement>> list() {
        List<Announcement> announcements = announcementRepository.findByStatusFlagOrderByCreatedAtDesc(1);
        return Result.success(announcements);
    }
}
