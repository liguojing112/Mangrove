package com.mangrove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tree_decoration")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreeDecoration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('LEAF','FLOWER','FRUIT','LIGHT','POT','BACKGROUND')")
    private Type type;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String description;

    @Column(name = "unlock_level", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer unlockLevel;

    @Column(name = "unlock_exp", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long unlockExp;

    @Column(nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer status;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public enum Type {
        LEAF, FLOWER, FRUIT, LIGHT, POT, BACKGROUND
    }
}
