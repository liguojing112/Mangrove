package com.mangrove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_quiz_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserQuizProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private SysUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private QuizLevel level;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer score;

    @Column(name = "total_questions", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer totalQuestions;

    @Column(name = "correct_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer correctCount;

    @Column(name = "is_passed", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    private Integer isPassed;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}
