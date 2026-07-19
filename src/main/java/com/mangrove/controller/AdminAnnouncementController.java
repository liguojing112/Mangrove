package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Announcement;
import com.mangrove.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/announcements")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminAnnouncementController {

    private final AnnouncementRepository announcementRepository;

    @GetMapping
    public Result<PageResult<Announcement>> list(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Announcement> result = announcementRepository.findAll(pageable);
        return Result.success(PageResult.of(result));
    }

    @GetMapping("/{id}")
    public Result<Announcement> getById(@PathVariable Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "公告不存在"));
        return Result.success(announcement);
    }

    @PostMapping
    @Transactional
    public Result<Announcement> create(@RequestBody Announcement announcement) {
        announcement.setStatusFlag(1);
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setUpdatedAt(LocalDateTime.now());
        return Result.success(announcementRepository.save(announcement));
    }

    @PutMapping("/{id}")
    @Transactional
    public Result<Announcement> update(@PathVariable Long id, @RequestBody Announcement announcement) {
        Announcement existing = announcementRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "公告不存在"));
        if (announcement.getTitle() != null) existing.setTitle(announcement.getTitle());
        if (announcement.getContent() != null) existing.setContent(announcement.getContent());
        existing.setUpdatedAt(LocalDateTime.now());
        return Result.success(announcementRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> delete(@PathVariable Long id) {
        announcementRepository.deleteById(id);
        return Result.success();
    }
}
