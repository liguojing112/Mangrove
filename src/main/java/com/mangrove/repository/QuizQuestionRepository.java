package com.mangrove.repository;

import com.mangrove.entity.QuizQuestion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    List<QuizQuestion> findByStatusAndDifficulty(Integer status, QuizQuestion.Difficulty difficulty, Pageable pageable);

    List<QuizQuestion> findByArtistId(Long artistId);
}
