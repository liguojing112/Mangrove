package com.mangrove.service.impl;

import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.request.ScheduleRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Artist;
import com.mangrove.entity.Schedule;
import com.mangrove.repository.ArtistRepository;
import com.mangrove.repository.ScheduleRepository;
import com.mangrove.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ArtistRepository artistRepository;

    @Override
    public PageResult<Schedule> list(Integer page, Integer size, String type) {
        Sort sort = Sort.by(Sort.Direction.DESC, "startTime");
        if (type != null && !type.isBlank()) {
            Schedule.Type scheduleType = Schedule.Type.valueOf(type);
            List<Schedule> allFiltered = scheduleRepository.findAll()
                    .stream()
                    .filter(s -> s.getScheduleType() == scheduleType)
                    .toList();
            return buildPageResult(allFiltered, page, size);
        }
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<Schedule> schedulePage = scheduleRepository.findAll(pageable);
        return PageResult.of(schedulePage);
    }

    @Override
    public List<Schedule> upcoming(Integer limit) {
        PageRequest pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC, "startTime"));
        return scheduleRepository.findByStartTimeAfter(LocalDateTime.now(), pageable)
                .stream()
                .filter(s -> s.getStatus() == 1)
                .toList();
    }

    @Override
    public Schedule getById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
    }

    @Override
    @Transactional
    public Schedule create(ScheduleRequest req) {
        Schedule schedule = Schedule.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .location(req.getLocation())
                .coverUrl(req.getCoverUrl())
                .startTime(LocalDateTime.parse(req.getStartTime()))
                .status(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        if (req.getScheduleType() != null && !req.getScheduleType().isBlank()) {
            schedule.setScheduleType(Schedule.Type.valueOf(req.getScheduleType()));
        } else {
            schedule.setScheduleType(Schedule.Type.OTHER);
        }

        if (req.getEndTime() != null && !req.getEndTime().isBlank()) {
            schedule.setEndTime(LocalDateTime.parse(req.getEndTime()));
        }

        if (req.getArtistId() != null) {
            Artist artist = artistRepository.findById(req.getArtistId())
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
            schedule.setArtist(artist);
        }

        return scheduleRepository.save(schedule);
    }

    @Override
    @Transactional
    public Schedule update(Long id, ScheduleRequest req) {
        Schedule schedule = getById(id);
        if (req.getTitle() != null) {
            schedule.setTitle(req.getTitle());
        }
        if (req.getDescription() != null) {
            schedule.setDescription(req.getDescription());
        }
        if (req.getScheduleType() != null && !req.getScheduleType().isBlank()) {
            schedule.setScheduleType(Schedule.Type.valueOf(req.getScheduleType()));
        }
        if (req.getLocation() != null) {
            schedule.setLocation(req.getLocation());
        }
        if (req.getStartTime() != null && !req.getStartTime().isBlank()) {
            schedule.setStartTime(LocalDateTime.parse(req.getStartTime()));
        }
        if (req.getEndTime() != null && !req.getEndTime().isBlank()) {
            schedule.setEndTime(LocalDateTime.parse(req.getEndTime()));
        }
        if (req.getCoverUrl() != null) {
            schedule.setCoverUrl(req.getCoverUrl());
        }
        if (req.getArtistId() != null) {
            Artist artist = artistRepository.findById(req.getArtistId())
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
            schedule.setArtist(artist);
        }
        schedule.setUpdatedAt(LocalDateTime.now());
        return scheduleRepository.save(schedule);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        scheduleRepository.deleteById(id);
    }

    private PageResult<Schedule> buildPageResult(List<Schedule> allFiltered, int page, int size) {
        int totalSize = allFiltered.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalSize);
        List<Schedule> pagedContent = fromIndex < totalSize
                ? allFiltered.subList(fromIndex, toIndex)
                : List.of();
        int totalPages = totalSize == 0 ? 0 : (int) Math.ceil((double) totalSize / size);
        return PageResult.<Schedule>builder()
                .content(pagedContent)
                .totalElements(totalSize)
                .totalPages(totalPages)
                .currentPage(page)
                .size(size)
                .build();
    }
}
