package com.mangrove.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "barrage_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BarrageMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(name = "public_id", length = 20)
    private String publicId;

    @Column(name = "like_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer likeCount;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}
