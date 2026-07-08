package com.mangrove.repository;

import com.mangrove.entity.Schedule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByStatusOrderByStartTimeAsc(Integer status, Pageable pageable);

    List<Schedule> findByArtistIdAndStatus(Long artistId, Integer status, Pageable pageable);

    List<Schedule> findByStartTimeAfter(LocalDateTime after, Pageable pageable);
}
