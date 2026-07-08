package com.mangrove.service;

import com.mangrove.dto.request.ScheduleRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Schedule;

import java.util.List;

public interface ScheduleService {

    PageResult<Schedule> list(Integer page, Integer size, String type);

    List<Schedule> upcoming(Integer limit);

    Schedule getById(Long id);

    Schedule create(ScheduleRequest req);

    Schedule update(Long id, ScheduleRequest req);

    void delete(Long id);
}
