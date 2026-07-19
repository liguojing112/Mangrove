package com.mangrove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "lottery")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lottery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String conditions;

    @Column(name = "winner_count", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer winnerCount;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_by_nickname", length = 50)
    private String createdByNickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('PENDING','ACTIVE','ENDED','DRAWN') DEFAULT 'PENDING'")
    private Status status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "winner_user_ids", columnDefinition = "JSON")
    private List<Long> winnerUserIds;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "winner_nicknames", columnDefinition = "JSON")
    private List<String> winnerNicknames;

    @Column(name = "entry_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer entryCount;

    @Column(nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer statusFlag;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    public enum Status {
        PENDING, ACTIVE, ENDED, DRAWN
    }
}
