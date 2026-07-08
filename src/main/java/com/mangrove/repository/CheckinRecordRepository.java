package com.mangrove.repository;

import com.mangrove.entity.CheckinRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckinRecordRepository extends JpaRepository<CheckinRecord, Long> {

    Optional<CheckinRecord> findByUserIdAndCheckinDate(Long userId, LocalDate date);

    List<CheckinRecord> findByUserIdOrderByCheckinDateDesc(Long userId, Pageable pageable);
}
