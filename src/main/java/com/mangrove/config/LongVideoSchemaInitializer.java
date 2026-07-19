package com.mangrove.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LongVideoSchemaInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS long_video (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    title VARCHAR(200) NOT NULL,
                    category VARCHAR(50) NOT NULL,
                    description TEXT NULL,
                    local_video_url VARCHAR(500) NULL,
                    bilibili_url VARCHAR(1000) NULL,
                    cover_url VARCHAR(500) NULL,
                    published_date DATE NULL,
                    sort_order INT NOT NULL DEFAULT 0,
                    status TINYINT NOT NULL DEFAULT 1,
                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_long_video_status_sort (status, sort_order),
                    INDEX idx_long_video_category (category)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS long_video_category (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(50) NOT NULL UNIQUE,
                    sort_order INT NOT NULL DEFAULT 0
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);
        jdbcTemplate.update("""
                INSERT IGNORE INTO long_video_category (name, sort_order)
                VALUES ('官方', 0), ('舞台', 1), ('访谈', 2), ('纪录片', 3)
                """);
        jdbcTemplate.update("""
                INSERT IGNORE INTO long_video_category (name, sort_order)
                SELECT DISTINCT category, 100
                FROM long_video
                WHERE category IS NOT NULL AND TRIM(category) <> ''
                """);
    }
}
