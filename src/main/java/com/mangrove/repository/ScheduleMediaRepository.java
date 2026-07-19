package com.mangrove.repository;

import com.mangrove.entity.ScheduleMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleMediaRepository extends JpaRepository<ScheduleMedia, Long> {
    List<ScheduleMedia> findByScheduleIdOrderBySortOrderAscIdAsc(Long scheduleId);
    Optional<ScheduleMedia> findByIdAndScheduleId(Long id, Long scheduleId);
}
