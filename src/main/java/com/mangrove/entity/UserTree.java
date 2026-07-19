package com.mangrove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_tree")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private SysUser user;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer level;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long experience;

    @Column(name = "tree_name", length = 100)
    private String treeName;

    @Column(name = "tree_decoration", columnDefinition = "json")
    private String treeDecoration;

    @Column(name = "total_likes", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer totalLikes;

    @Column(name = "total_uploads", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer totalUploads;

    @Column(name = "consecutive_days", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer consecutiveDays;

    @Column(name = "total_checkins", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer totalCheckins;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer points;

    @Column(name = "total_points", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer totalPoints;

    @Column(name = "last_checkin_at")
    private LocalDate lastCheckinAt;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}
