package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.entity.GameLevel;
import com.mangrove.entity.GameQuestion;
import com.mangrove.entity.GameScore;
import com.mangrove.repository.GameLevelRepository;
import com.mangrove.repository.GameQuestionRepository;
import com.mangrove.repository.GameScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameLevelRepository levelRepository;
    private final GameQuestionRepository questionRepository;
    private final GameScoreRepository scoreRepository;

    @GetMapping("/levels")
    public Result<List<GameLevel>> listLevels() {
        return Result.success(levelRepository.findByStatusOrderByLevelNumberAsc(1));
    }

    @GetMapping("/levels/{id}")
    public Result<GameLevel> getLevel(@PathVariable Long id) {
        GameLevel level = levelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("关卡不存在"));
        return Result.success(level);
    }

    @GetMapping("/levels/{id}/questions")
    public Result<List<GameQuestion>> getQuestions(@PathVariable Long id) {
        return Result.success(questionRepository.findByLevelIdAndStatusOrderBySortOrderAsc(id, 1));
    }

    @PostMapping("/scores")
    public Result<GameScore> saveScore(@RequestBody GameScore score) {
        score.setCreatedAt(LocalDateTime.now());
        return Result.success(scoreRepository.save(score));
    }

    @GetMapping("/scores/leaderboard")
    public Result<List<GameScore>> getLeaderboard() {
        return Result.success(scoreRepository.findTopScores(10));
    }

    @GetMapping("/scores/user/{userId}")
    public Result<List<GameScore>> getUserScores(@PathVariable Long userId) {
        return Result.success(scoreRepository.findByUserIdOrderByScoreDesc(userId));
    }

    @GetMapping("/scores/difficulty/{difficulty}")
    public Result<List<GameScore>> getScoresByDifficulty(@PathVariable String difficulty) {
        return Result.success(scoreRepository.findByDifficultyOrderByScoreDesc(difficulty));
    }
}

