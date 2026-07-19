package com.mangrove.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleMediaSchemaInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS schedule_media (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    schedule_id BIGINT NOT NULL,
                    media_type VARCHAR(20) NOT NULL,
                    media_url VARCHAR(500) NOT NULL,
                    title VARCHAR(200) NULL,
                    sort_order INT NOT NULL DEFAULT 0,
                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    INDEX idx_schedule_media_schedule_sort (schedule_id, sort_order),
                    CONSTRAINT fk_schedule_media_schedule
                        FOREIGN KEY (schedule_id) REFERENCES schedule(id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);
    }
}
