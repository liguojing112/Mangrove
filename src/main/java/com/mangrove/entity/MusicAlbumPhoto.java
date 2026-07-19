package com.mangrove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "music_album_photo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicAlbumPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "album_id", nullable = false)
    private Long albumId;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(length = 300)
    private String caption;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (sortOrder == null) sortOrder = 0;
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
