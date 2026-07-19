package com.mangrove.config;

import com.mangrove.service.MusicCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class MusicCatalogSchemaInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final MusicCatalogService musicCatalogService;

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS music_track (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    title VARCHAR(200) NOT NULL,
                    category VARCHAR(50) NOT NULL,
                    local_audio_url VARCHAR(500) NULL,
                    qq_music_url VARCHAR(1000) NULL,
                    cover_url VARCHAR(500) NULL,
                    duration_seconds INT NULL,
                    sort_order INT NOT NULL DEFAULT 0,
                    play_count BIGINT NOT NULL DEFAULT 0,
                    status TINYINT NOT NULL DEFAULT 1,
                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_music_track_status_sort (status, sort_order),
                    INDEX idx_music_track_category (category)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS music_photo_album (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    title VARCHAR(200) NOT NULL,
                    description TEXT NULL,
                    cover_url VARCHAR(500) NOT NULL,
                    sort_order INT NOT NULL DEFAULT 0,
                    status TINYINT NOT NULL DEFAULT 1,
                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_music_album_status_sort (status, sort_order)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS music_album_photo (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    album_id BIGINT NOT NULL,
                    image_url VARCHAR(500) NOT NULL,
                    caption VARCHAR(300) NULL,
                    sort_order INT NOT NULL DEFAULT 0,
                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    INDEX idx_music_album_photo_sort (album_id, sort_order),
                    CONSTRAINT fk_music_album_photo_album
                        FOREIGN KEY (album_id) REFERENCES music_photo_album(id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);
        musicCatalogService.migrateLegacyTracksIfEmpty();
    }
}
