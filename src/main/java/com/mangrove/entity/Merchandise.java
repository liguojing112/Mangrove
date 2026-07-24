package com.mangrove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "merchandise")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Merchandise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('PHOTOCARD','HAND_BANNER','PHYSICAL','DIGITAL','ARCHIVE','BUNDLE')")
    private Category category;

    @Column(name = "sub_category", length = 50)
    private String subCategory;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "high_res_image_url", nullable = false, length = 500)
    private String highResImageUrl;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "card_front_image_url", length = 500)
    private String cardFrontImageUrl;

    @Column(name = "card_back_image_url", length = 500)
    private String cardBackImageUrl;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Convert(converter = com.mangrove.config.JsonListConverter.class)
    @Column(name = "bundle_image_urls", columnDefinition = "LONGTEXT")
    private List<String> bundleImageUrls;

    @Column(name = "producer_id")
    private Long producerId;

    @Column(name = "producer_name", length = 100)
    private String producerName;

    @Column(name = "producer_username", length = 50)
    private String producerUsername;

    @Column(name = "producer_public_id", length = 20)
    private String producerPublicId;

    @Column(name = "producer_role", length = 30)
    private String producerRole;

    @Column(length = 500)
    private String tags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @Column(name = "publish_date")
    private LocalDate publishDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('COMMON','RARE','LIMITED','LEGENDARY') DEFAULT 'COMMON'")
    private Rarity rarity;

    @Column(nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer status;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    public enum Category {
        PHOTOCARD, HAND_BANNER, PHYSICAL, DIGITAL, ARCHIVE, BUNDLE
    }

    public enum Rarity {
        COMMON, RARE, LIMITED, LEGENDARY
    }
}
