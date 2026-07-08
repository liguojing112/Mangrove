package com.mangrove.repository;

import com.mangrove.entity.LikeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRecordRepository extends JpaRepository<LikeRecord, Long> {

    Optional<LikeRecord> findByUserIdAndTargetTypeAndTargetId(
            Long userId, LikeRecord.TargetType type, Long targetId);

    long countByTargetTypeAndTargetId(LikeRecord.TargetType type, Long targetId);

    void deleteByUserIdAndTargetTypeAndTargetId(Long userId, LikeRecord.TargetType type, Long targetId);
}
