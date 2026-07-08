CREATE TABLE IF NOT EXISTS photo_categories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO photo_categories (id, name, sort_order) VALUES
(1, '官方', 1), (2, '饭拍', 2), (3, '活动', 3), (4, '日常', 4);
