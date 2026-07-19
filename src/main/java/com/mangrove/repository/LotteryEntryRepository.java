package com.mangrove.repository;

import com.mangrove.entity.LotteryEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LotteryEntryRepository extends JpaRepository<LotteryEntry, Long> {
    Optional<LotteryEntry> findByLotteryIdAndUserId(Long lotteryId, Long userId);
    boolean existsByLotteryIdAndUserId(Long lotteryId, Long userId);
    List<LotteryEntry> findByLotteryIdOrderByCreatedAtAsc(Long lotteryId);
    long countByLotteryId(Long lotteryId);
    Page<LotteryEntry> findByLotteryIdOrderByCreatedAtAsc(Long lotteryId, Pageable pageable);
}
