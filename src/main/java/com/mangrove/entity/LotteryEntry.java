package com.mangrove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "lottery_entry",
       uniqueConstraints = @UniqueConstraint(columnNames = {"lottery_id", "user_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotteryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lottery_id", nullable = false)
    private Long lotteryId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_nickname", length = 50)
    private String userNickname;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}
