package com.mangrove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "letter_note")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LetterNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('QUOTE','LETTER','DIARY','MONOLOGUE')")
    private Category category;

    @Column(length = 200)
    private String source;

    @Column(name = "note_date")
    private LocalDate noteDate;

    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer likes;

    @Column(nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer status;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public enum Category {
        QUOTE, LETTER, DIARY, MONOLOGUE
    }
}
