package com.mangrove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_level")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "level_number", nullable = false, unique = true)
    private Integer levelNumber;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "required_questions", nullable = false, columnDefinition = "INT DEFAULT 10")
    private Integer requiredQuestions;

    @Column(name = "pass_score", nullable = false, columnDefinition = "INT DEFAULT 60")
    private Integer passScore;

    @Column(name = "reward_exp", nullable = false, columnDefinition = "BIGINT DEFAULT 100")
    private Long rewardExp;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}
