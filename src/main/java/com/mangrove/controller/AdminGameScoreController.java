package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.entity.GameScore;
import com.mangrove.repository.GameScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/game/scores")
@RequiredArgsConstructor
public class AdminGameScoreController {

    private final GameScoreRepository scoreRepository;

    @GetMapping
    public Result<List<GameScore>> listAll() {
        return Result.success(scoreRepository.findAll());
    }

    @GetMapping("/difficulty/{difficulty}")
    public Result<List<GameScore>> listByDifficulty(@PathVariable String difficulty) {
        return Result.success(scoreRepository.findByDifficultyOrderByScoreDesc(difficulty));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        scoreRepository.deleteById(id);
        return Result.success();
    }
}
