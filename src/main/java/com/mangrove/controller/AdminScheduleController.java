package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.response.PageResult;
import com.mangrove.dto.request.ScheduleMediaRequest;
import com.mangrove.entity.Schedule;
import com.mangrove.entity.ScheduleMedia;
import com.mangrove.repository.ScheduleMediaRepository;
import com.mangrove.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/admin/schedules2")
@RequiredArgsConstructor
public class AdminScheduleController {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMediaRepository scheduleMediaRepository;

    @GetMapping
    public Result<PageResult<Schedule>> listAll(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startTime"));
        Page<Schedule> schedules = scheduleRepository.findAll(pageable);
        return Result.success(PageResult.of(schedules));
    }

    @PostMapping
    public Result<Schedule> create(@RequestBody Schedule schedule) {
        schedule.setCreatedAt(java.time.LocalDateTime.now());
        schedule.setUpdatedAt(java.time.LocalDateTime.now());
        schedule.setStatus(1);
        return Result.success(scheduleRepository.save(schedule));
    }

    @PutMapping("/{id}")
    public Result<Schedule> update(@PathVariable Long id, @RequestBody Schedule schedule) {
        Schedule existing = scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "行程不存在"));
        existing.setTitle(schedule.getTitle());
        existing.setStartTime(schedule.getStartTime());
        existing.setEndTime(schedule.getEndTime());
        existing.setLocation(schedule.getLocation());
        existing.setScheduleType(schedule.getScheduleType());
        existing.setDescription(schedule.getDescription());
        existing.setVideoLink(schedule.getVideoLink());
        existing.setUpdatedAt(java.time.LocalDateTime.now());
        return Result.success(scheduleRepository.save(existing));
    }

    @PutMapping("/{id}/status")
    public Result<Schedule> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "行程不存在"));
        schedule.setStatus(status);
        schedule.setUpdatedAt(java.time.LocalDateTime.now());
        return Result.success(scheduleRepository.save(schedule));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "行程不存在"));
        scheduleRepository.delete(schedule);
        return Result.success();
    }

    @GetMapping("/{id}/media")
    public Result<List<ScheduleMedia>> listMedia(@PathVariable Long id) {
        requireSchedule(id);
        return Result.success(scheduleMediaRepository.findByScheduleIdOrderBySortOrderAscIdAsc(id));
    }

    @PostMapping("/{id}/media")
    public Result<ScheduleMedia> addMedia(@PathVariable Long id, @RequestBody ScheduleMediaRequest request) {
        requireSchedule(id);
        ScheduleMedia media = ScheduleMedia.builder()
                .scheduleId(id)
                .mediaType(parseMediaType(request.getMediaType()))
                .mediaUrl(requireMediaUrl(request.getMediaUrl()))
                .title(trimToNull(request.getTitle()))
                .sortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder())
                .build();
        return Result.success(scheduleMediaRepository.save(media));
    }

    @PutMapping("/{id}/media/{mediaId}")
    public Result<ScheduleMedia> updateMedia(@PathVariable Long id,
                                             @PathVariable Long mediaId,
                                             @RequestBody ScheduleMediaRequest request) {
        requireSchedule(id);
        ScheduleMedia media = scheduleMediaRepository.findByIdAndScheduleId(mediaId, id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "行程素材不存在"));
        if (request.getMediaType() != null) media.setMediaType(parseMediaType(request.getMediaType()));
        if (request.getMediaUrl() != null) media.setMediaUrl(requireMediaUrl(request.getMediaUrl()));
        if (request.getTitle() != null) media.setTitle(trimToNull(request.getTitle()));
        if (request.getSortOrder() != null) media.setSortOrder(request.getSortOrder());
        return Result.success(scheduleMediaRepository.save(media));
    }

    @DeleteMapping("/{id}/media/{mediaId}")
    public Result<Void> deleteMedia(@PathVariable Long id, @PathVariable Long mediaId) {
        requireSchedule(id);
        ScheduleMedia media = scheduleMediaRepository.findByIdAndScheduleId(mediaId, id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "行程素材不存在"));
        scheduleMediaRepository.delete(media);
        return Result.success();
    }

    private Schedule requireSchedule(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "行程不存在"));
    }

    private ScheduleMedia.MediaType parseMediaType(String value) {
        try {
            return ScheduleMedia.MediaType.valueOf(value == null ? "" : value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "素材类型只能是 IMAGE 或 VIDEO");
        }
    }

    private String requireMediaUrl(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "素材地址不能为空");
        }
        return value.trim();
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
