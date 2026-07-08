package com.mangrove.repository;

import com.mangrove.entity.QuizLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizLevelRepository extends JpaRepository<QuizLevel, Long> {

    Optional<QuizLevel> findByLevelNumber(Integer levelNumber);

    List<QuizLevel> findAllByOrderByLevelNumberAsc();
}
