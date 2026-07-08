package com.mangrove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private SysUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    @Column(name = "user_answer", columnDefinition = "CHAR(1)")
    private Character userAnswer;

    @Column(name = "is_correct", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    private Integer isCorrect;

    @Column(name = "points_earned", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer pointsEarned;

    @Column(name = "answered_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime answeredAt;
}
