package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.dto.request.ScheduleRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Schedule;
import com.mangrove.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/schedules")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public Result<PageResult<Schedule>> list(@RequestParam(defaultValue = "0") Integer page,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             @RequestParam(required = false) String type) {
        return Result.success(scheduleService.list(page, size, type));
    }

    @GetMapping("/upcoming")
    public Result<List<Schedule>> upcoming(@RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(scheduleService.upcoming(limit));
    }

    @GetMapping("/{id}")
    public Result<Schedule> getById(@PathVariable Long id) {
        return Result.success(scheduleService.getById(id));
    }

    @PostMapping
    public Result<Schedule> create(@Valid @RequestBody ScheduleRequest req) {
        return Result.success(scheduleService.create(req));
    }

    @PutMapping("/{id}")
    public Result<Schedule> update(@PathVariable Long id,
                                   @Valid @RequestBody ScheduleRequest req) {
        return Result.success(scheduleService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return Result.success();
    }
}
