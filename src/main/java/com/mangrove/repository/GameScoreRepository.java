package com.mangrove.repository;

import com.mangrove.entity.GameScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameScoreRepository extends JpaRepository<GameScore, Long> {

    @Query("SELECT gs FROM GameScore gs ORDER BY gs.score DESC LIMIT :limit")
    List<GameScore> findTopScores(int limit);

    List<GameScore> findByUserIdOrderByScoreDesc(Long userId);

    List<GameScore> findByDifficultyOrderByScoreDesc(String difficulty);
}
