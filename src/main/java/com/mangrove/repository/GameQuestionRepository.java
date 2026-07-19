package com.mangrove.repository;

import com.mangrove.entity.GameQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameQuestionRepository extends JpaRepository<GameQuestion, Long> {

    List<GameQuestion> findByLevelIdAndStatusOrderBySortOrderAsc(Long levelId, Integer status);

    long countByLevelId(Long levelId);
}
