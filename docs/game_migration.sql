-- Game Level table
CREATE TABLE IF NOT EXISTS game_level (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    level_number INT NOT NULL,
    difficulty VARCHAR(20) NOT NULL DEFAULT 'EASY',
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Game Question table
CREATE TABLE IF NOT EXISTS game_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    level_id BIGINT NOT NULL,
    question VARCHAR(500) NOT NULL,
    options TEXT,
    correct_answer VARCHAR(500) NOT NULL,
    explanation TEXT,
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_level_id (level_id),
    INDEX idx_status (status),
    FOREIGN KEY (level_id) REFERENCES game_level(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Game Score table
CREATE TABLE IF NOT EXISTS game_score (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    user_nickname VARCHAR(50),
    level_id BIGINT NOT NULL,
    score INT NOT NULL,
    correct_count INT NOT NULL,
    total_questions INT NOT NULL,
    difficulty VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_score (score),
    INDEX idx_difficulty (difficulty)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
