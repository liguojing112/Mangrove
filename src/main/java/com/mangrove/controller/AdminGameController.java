package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.entity.GameLevel;
import com.mangrove.entity.GameQuestion;
import com.mangrove.repository.GameLevelRepository;
import com.mangrove.repository.GameQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/game")
@RequiredArgsConstructor
public class AdminGameController {

    private final GameLevelRepository levelRepository;
    private final GameQuestionRepository questionRepository;

    @GetMapping("/levels")
    public Result<List<GameLevel>> listLevels() {
        return Result.success(levelRepository.findAll());
    }

    @PostMapping("/levels")
    public Result<GameLevel> createLevel(@RequestBody GameLevel level) {
        level.setCreatedAt(LocalDateTime.now());
        level.setUpdatedAt(LocalDateTime.now());
        level.setStatus(1);
        return Result.success(levelRepository.save(level));
    }

    @PutMapping("/levels/{id}")
    public Result<GameLevel> updateLevel(@PathVariable Long id, @RequestBody GameLevel level) {
        GameLevel existing = levelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("关卡不存在"));
        existing.setTitle(level.getTitle());
        existing.setDescription(level.getDescription());
        existing.setLevelNumber(level.getLevelNumber());
        existing.setDifficulty(level.getDifficulty());
        existing.setUpdatedAt(LocalDateTime.now());
        return Result.success(levelRepository.save(existing));
    }

    @PutMapping("/levels/{id}/status")
    public Result<Void> updateLevelStatus(@PathVariable Long id, @RequestParam Integer status) {
        GameLevel level = levelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("关卡不存在"));
        level.setStatus(status);
        level.setUpdatedAt(LocalDateTime.now());
        levelRepository.save(level);
        return Result.success();
    }

    @DeleteMapping("/levels/{id}")
    public Result<Void> deleteLevel(@PathVariable Long id) {
        levelRepository.deleteById(id);
        return Result.success();
    }

    @GetMapping("/levels/{levelId}/questions")
    public Result<List<GameQuestion>> listQuestions(@PathVariable Long levelId) {
        return Result.success(questionRepository.findByLevelIdAndStatusOrderBySortOrderAsc(levelId, 1));
    }

    @PostMapping("/questions")
    public Result<GameQuestion> createQuestion(@RequestBody GameQuestion question) {
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());
        question.setStatus(1);
        if (question.getSortOrder() == null) {
            question.setSortOrder((int) questionRepository.countByLevelId(question.getLevelId()));
        }
        return Result.success(questionRepository.save(question));
    }

    @PutMapping("/questions/{id}")
    public Result<GameQuestion> updateQuestion(@PathVariable Long id, @RequestBody GameQuestion question) {
        GameQuestion existing = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("问题不存在"));
        existing.setQuestion(question.getQuestion());
        existing.setOptionA(question.getOptionA());
        existing.setOptionB(question.getOptionB());
        existing.setOptionC(question.getOptionC());
        existing.setOptionD(question.getOptionD());
        existing.setCorrectAnswer(question.getCorrectAnswer());
        existing.setExplanation(question.getExplanation());
        existing.setSortOrder(question.getSortOrder());
        existing.setUpdatedAt(LocalDateTime.now());
        return Result.success(questionRepository.save(existing));
    }

    @DeleteMapping("/questions/{id}")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        questionRepository.deleteById(id);
        return Result.success();
    }
}
