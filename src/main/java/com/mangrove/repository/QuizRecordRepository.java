package com.mangrove.repository;

import com.mangrove.entity.QuizRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRecordRepository extends JpaRepository<QuizRecord, Long> {

    Optional<QuizRecord> findByUserIdAndQuestionId(Long userId, Long questionId);

    List<QuizRecord> findByUserIdOrderByAnsweredAtDesc(Long userId, Pageable pageable);
}
