package com.mangrove.repository;

import com.mangrove.entity.Lottery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotteryRepository extends JpaRepository<Lottery, Long> {
    Page<Lottery> findByStatusFlagOrderByCreatedAtDesc(Integer statusFlag, Pageable pageable);
    List<Lottery> findByStatusInAndStatusFlag(List<Lottery.Status> statuses, Integer statusFlag);
    Page<Lottery> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
