package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.entity.Schedule;
import com.mangrove.entity.ScheduleMedia;
import com.mangrove.repository.ScheduleMediaRepository;
import com.mangrove.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class PublicScheduleController {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMediaRepository scheduleMediaRepository;

    @GetMapping
    public Result<List<Schedule>> list() {
        List<Schedule> schedules = scheduleRepository.findAll();
        return Result.success(schedules.stream()
                .filter(s -> s.getStatus() != null && s.getStatus() == 1)
                .sorted((a, b) -> a.getStartTime().compareTo(b.getStartTime()))
                .toList());
    }

    @GetMapping("/{id}")
    public Result<Schedule> getById(@PathVariable Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .filter(item -> item.getStatus() != null && item.getStatus() == 1)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "行程不存在"));
        return Result.success(schedule);
    }

    @GetMapping("/{id}/media")
    public Result<List<ScheduleMedia>> listMedia(@PathVariable Long id) {
        scheduleRepository.findById(id)
                .filter(item -> item.getStatus() != null && item.getStatus() == 1)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "行程不存在"));
        return Result.success(scheduleMediaRepository.findByScheduleIdOrderBySortOrderAscIdAsc(id));
    }
}
