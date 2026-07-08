package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.*;
import com.mangrove.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizRecordRepository quizRecordRepository;
    private final QuizLevelRepository quizLevelRepository;
    private final UserQuizProgressRepository userQuizProgressRepository;
    private final SysUserRepository sysUserRepository;
    private final UserTreeRepository userTreeRepository;
    private final CheckinRecordRepository checkinRecordRepository;

    @GetMapping("/questions")
    public Result<PageResult<QuizQuestion>> getQuestions(
            @RequestParam(defaultValue = "EASY") String difficulty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        QuizQuestion.Difficulty diff;
        try {
            diff = QuizQuestion.Difficulty.valueOf(difficulty.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "无效的难度参数");
        }
        List<QuizQuestion> questions = quizQuestionRepository.findByStatusAndDifficulty(1, diff, pageable);
        PageResult<QuizQuestion> result = PageResult.<QuizQuestion>builder()
                .content(questions)
                .totalElements(questions.size())
                .totalPages(1)
                .currentPage(page)
                .size(size)
                .build();
        return Result.success(result);
    }

    @PostMapping("/answer")
    @Transactional
    public Result<Map<String, Object>> answer(Authentication authentication,
                                               @RequestBody Map<String, Object> body) {
        SysUser user = getCurrentUser(authentication);

        Long questionId = Long.valueOf(body.get("questionId").toString());
        String answer = body.get("answer").toString();

        QuizQuestion question = quizQuestionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "题目不存在"));

        if (quizRecordRepository.findByUserIdAndQuestionId(user.getId(), questionId).isPresent()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "已答过此题");
        }

        boolean isCorrect = String.valueOf(question.getCorrectAnswer()).equalsIgnoreCase(answer);
        int pointsEarned = isCorrect ? question.getPoints() : 0;

        QuizRecord record = QuizRecord.builder()
                .user(user)
                .question(question)
                .userAnswer(answer.charAt(0))
                .isCorrect(isCorrect ? 1 : 0)
                .pointsEarned(pointsEarned)
                .build();
        quizRecordRepository.save(record);

        if (isCorrect) {
            UserTree tree = userTreeRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "芒果树不存在"));
            tree.setExperience(tree.getExperience() + pointsEarned);
            userTreeRepository.save(tree);
        }

        Map<String, Object> result = Map.of(
                "correct", isCorrect,
                "pointsEarned", pointsEarned,
                "explanation", question.getExplanation() != null ? question.getExplanation() : ""
        );
        return Result.success(result);
    }

    @GetMapping("/progress")
    public Result<List<UserQuizProgress>> getProgress(Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        List<UserQuizProgress> progress = userQuizProgressRepository.findByUserIdOrderByLevelLevelNumberAsc(user.getId());
        return Result.success(progress);
    }

    @GetMapping("/leaderboard")
    public Result<List<Map<String, Object>>> leaderboard() {
        Pageable top10 = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "pointsEarned"));
        List<QuizRecord> topRecords = quizRecordRepository.findAll(top10).getContent();

        List<Map<String, Object>> leaderboard = topRecords.stream().map(r -> {
            SysUser u = r.getUser();
            return Map.<String, Object>of(
                    "userId", u.getId(),
                    "nickname", u.getNickname(),
                    "avatarUrl", u.getAvatarUrl() != null ? u.getAvatarUrl() : "",
                    "points", r.getPointsEarned()
            );
        }).collect(Collectors.toList());

        return Result.success(leaderboard);
    }

    private SysUser getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));
    }
}
