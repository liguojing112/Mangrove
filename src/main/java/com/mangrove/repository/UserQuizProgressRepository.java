package com.mangrove.repository;

import com.mangrove.entity.UserQuizProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserQuizProgressRepository extends JpaRepository<UserQuizProgress, Long> {

    Optional<UserQuizProgress> findByUserIdAndLevelId(Long userId, Long levelId);

    List<UserQuizProgress> findByUserIdOrderByLevelLevelNumberAsc(Long userId);
}
