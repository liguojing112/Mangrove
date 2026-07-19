package com.mangrove.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRoleSchemaInitializer implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("ALTER TABLE sys_user MODIFY COLUMN role ENUM('FAN','CREATOR','ADMIN','SUPER_ADMIN') NOT NULL DEFAULT 'FAN'");
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_user' AND COLUMN_NAME = 'public_id'", Integer.class);
        if (count != null && count == 0) {
            jdbcTemplate.execute("ALTER TABLE sys_user ADD COLUMN public_id VARCHAR(20) NULL AFTER username");
        }
        jdbcTemplate.update("UPDATE sys_user SET public_id = CONCAT('MG', LPAD(id, 8, '0')) WHERE public_id IS NULL OR public_id = ''");
        jdbcTemplate.execute("ALTER TABLE sys_user MODIFY COLUMN public_id VARCHAR(20) NOT NULL");
        Integer indexCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_user' AND INDEX_NAME = 'uk_sys_user_public_id'", Integer.class);
        if (indexCount != null && indexCount == 0) {
            jdbcTemplate.execute("ALTER TABLE sys_user ADD UNIQUE INDEX uk_sys_user_public_id (public_id)");
        }
    }
}
