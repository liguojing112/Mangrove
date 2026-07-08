CREATE TABLE IF NOT EXISTS nav_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    label VARCHAR(20) NOT NULL COMMENT '导航标题',
    url VARCHAR(50) NOT NULL COMMENT '链接地址',
    sort_order INT NOT NULL DEFAULT 0,
    enabled TINYINT NOT NULL DEFAULT 1 COMMENT '1=启用 0=禁用',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO nav_items (label, url, sort_order) VALUES
('首页', '/', 1),
('艺人', '/artists', 2),
('照片', '/photos', 3),
('影像', '/videos', 4),
('音乐', '/music', 5),
('周边', '/merchandise', 6),
('创作', '/works', 7),
('行程', '/schedule', 8),
('游戏', '/games', 9),
('社区', '/community', 10),
('来信', '/letters', 11),
('芒果树', '/tree', 12);
