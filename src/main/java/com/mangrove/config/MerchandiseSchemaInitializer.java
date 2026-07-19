package com.mangrove.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MerchandiseSchemaInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("""
                ALTER TABLE merchandise
                MODIFY COLUMN category
                ENUM('PHOTOCARD','HAND_BANNER','PHYSICAL','DIGITAL','ARCHIVE','BUNDLE') NOT NULL
                """);
        addColumnIfMissing("card_front_image_url",
                "ALTER TABLE merchandise ADD COLUMN card_front_image_url VARCHAR(500) NULL AFTER thumbnail_url");
        addColumnIfMissing("card_back_image_url",
                "ALTER TABLE merchandise ADD COLUMN card_back_image_url VARCHAR(500) NULL AFTER card_front_image_url");
        addColumnIfMissing("profile_image_url",
                "ALTER TABLE merchandise ADD COLUMN profile_image_url VARCHAR(500) NULL AFTER card_back_image_url");
        addColumnIfMissing("bundle_image_urls",
                "ALTER TABLE merchandise ADD COLUMN bundle_image_urls JSON NULL AFTER profile_image_url");
        addColumnIfMissing("producer_id",
                "ALTER TABLE merchandise ADD COLUMN producer_id BIGINT NULL AFTER bundle_image_urls");
        addColumnIfMissing("producer_name",
                "ALTER TABLE merchandise ADD COLUMN producer_name VARCHAR(100) NULL AFTER producer_id");
        addColumnIfMissing("producer_username",
                "ALTER TABLE merchandise ADD COLUMN producer_username VARCHAR(50) NULL AFTER producer_name");
        addColumnIfMissing("producer_public_id",
                "ALTER TABLE merchandise ADD COLUMN producer_public_id VARCHAR(20) NULL AFTER producer_username");
        addColumnIfMissing("producer_role",
                "ALTER TABLE merchandise ADD COLUMN producer_role VARCHAR(30) NULL AFTER producer_name");
    }

    private void addColumnIfMissing(String columnName, String statement) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = 'merchandise'
                  AND COLUMN_NAME = ?
                """, Integer.class, columnName);
        if (count != null && count == 0) {
            jdbcTemplate.execute(statement);
        }
    }
}
