-- ============================================================
-- 青芒纪 (Mangrove) 数据库设计
-- MySQL 8.0+
-- 字符集: utf8mb4, 排序规则: utf8mb4_unicode_ci
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `mangrove`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `mangrove`;

-- ============================================================
-- 1. 用户表 (sys_user)
--    角色区分: FAN(普通粉丝) / CREATOR(创作者) / ADMIN(管理员)
-- ============================================================
CREATE TABLE `sys_user` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '用户ID',
    `username`      VARCHAR(50)     NOT NULL                 COMMENT '用户名(登录用)',
    `password`      VARCHAR(255)    NOT NULL                 COMMENT '密码(BCrypt加密)',
    `nickname`      VARCHAR(50)     NOT NULL                 COMMENT '昵称(展示用)',
    `email`         VARCHAR(100)    DEFAULT NULL             COMMENT '邮箱',
    `phone`         VARCHAR(20)     DEFAULT NULL             COMMENT '手机号',
    `avatar_url`    VARCHAR(500)    DEFAULT NULL             COMMENT '头像URL',
    `role`          ENUM('FAN','CREATOR','ADMIN')
                                    NOT NULL DEFAULT 'FAN'   COMMENT '角色: FAN-粉丝, CREATOR-创作者, ADMIN-管理员',
    `bio`           VARCHAR(500)    DEFAULT NULL             COMMENT '个人简介',
    `status`        TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 0-禁用, 1-正常',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';


-- ============================================================
-- 2. 艺人基础信息表 (artist)
--    存储出生日期、出道日期，可通过 DATEDIFF 计算天数
-- ============================================================
CREATE TABLE `artist` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '艺人ID',
    `name`          VARCHAR(100)    NOT NULL                 COMMENT '本名',
    `stage_name`    VARCHAR(100)    NOT NULL                 COMMENT '艺名/舞台名',
    `birth_date`    DATE            DEFAULT NULL             COMMENT '出生日期',
    `debut_date`    DATE            DEFAULT NULL             COMMENT '出道日期',
    `nationality`   VARCHAR(50)     DEFAULT NULL             COMMENT '国籍',
    `bio`           TEXT            DEFAULT NULL             COMMENT '艺人简介',
    `company`       VARCHAR(100)    DEFAULT NULL             COMMENT '所属公司/团体',
    `avatar_url`    VARCHAR(500)    DEFAULT NULL             COMMENT '头像URL',
    `cover_url`     VARCHAR(500)    DEFAULT NULL             COMMENT '封面大图URL',
    `sort_order`    INT             NOT NULL DEFAULT 0       COMMENT '排序权重(越大越靠前)',
    `status`        TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 0-下架, 1-正常',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_stage_name` (`stage_name`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='艺人基础信息表';


-- ============================================================
-- 3. 相册表 (album) — "照片馆藏" / "影像存档"
-- ============================================================
CREATE TABLE `album` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '相册ID',
    `title`         VARCHAR(200)    NOT NULL                 COMMENT '相册标题',
    `description`   TEXT            DEFAULT NULL             COMMENT '相册描述',
    `cover_url`     VARCHAR(500)    DEFAULT NULL             COMMENT '封面图URL',
    `category`      ENUM('PHOTO','VIDEO')
                                    NOT NULL                 COMMENT '分类: PHOTO-照片馆藏, VIDEO-影像存档',
    `artist_id`     BIGINT          DEFAULT NULL             COMMENT '关联艺人ID',
    `user_id`       BIGINT          NOT NULL                 COMMENT '创建者用户ID',
    `view_count`    INT             NOT NULL DEFAULT 0       COMMENT '浏览次数',
    `like_count`    INT             NOT NULL DEFAULT 0       COMMENT '点赞数',
    `status`        TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 0-隐藏, 1-公开',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_artist_id` (`artist_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category` (`category`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_album_artist` FOREIGN KEY (`artist_id`) REFERENCES `artist`(`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_album_user`   FOREIGN KEY (`user_id`)   REFERENCES `sys_user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='相册表';


-- ============================================================
-- 4. 媒体文件表 (media_file)
--    关联相册，存储具体图片/视频文件
-- ============================================================
CREATE TABLE `media_file` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '媒体文件ID',
    `album_id`      BIGINT          NOT NULL                 COMMENT '所属相册ID',
    `title`         VARCHAR(200)    DEFAULT NULL             COMMENT '文件标题',
    `file_url`      VARCHAR(500)    NOT NULL                 COMMENT '文件URL',
    `file_type`     VARCHAR(50)     NOT NULL                 COMMENT '文件类型(image/jpeg, video/mp4 等)',
    `file_size`     BIGINT          DEFAULT NULL             COMMENT '文件大小(字节)',
    `thumbnail_url` VARCHAR(500)    DEFAULT NULL             COMMENT '缩略图URL',
    `sort_order`    INT             NOT NULL DEFAULT 0       COMMENT '排序号',
    `upload_date`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传日期',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_album_id` (`album_id`),
    KEY `idx_sort_order` (`sort_order`),
    KEY `idx_upload_date` (`upload_date`),
    CONSTRAINT `fk_media_album` FOREIGN KEY (`album_id`) REFERENCES `album`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='媒体文件表';


-- ============================================================
-- 5. 音乐表 (music) — "音芒分享"
--    存储歌曲名、外链地址、封面图
-- ============================================================
CREATE TABLE `music` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '音乐ID',
    `title`         VARCHAR(200)    NOT NULL                 COMMENT '歌曲名',
    `singer`        VARCHAR(100)    DEFAULT NULL             COMMENT '演唱者',
    `album_name`    VARCHAR(200)    DEFAULT NULL             COMMENT '所属专辑',
    `external_url`  VARCHAR(500)    NOT NULL                 COMMENT '外链地址(播放链接)',
    `cover_url`     VARCHAR(500)    DEFAULT NULL             COMMENT '封面图URL',
    `lyric`         TEXT            DEFAULT NULL             COMMENT '歌词',
    `description`   TEXT            DEFAULT NULL             COMMENT '歌曲描述/推荐语',
    `artist_id`     BIGINT          DEFAULT NULL             COMMENT '关联艺人ID',
    `user_id`       BIGINT          NOT NULL                 COMMENT '分享者用户ID',
    `play_count`    INT             NOT NULL DEFAULT 0       COMMENT '播放次数',
    `like_count`    INT             NOT NULL DEFAULT 0       COMMENT '点赞数',
    `status`        TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 0-下架, 1-正常',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_artist_id` (`artist_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_music_artist` FOREIGN KEY (`artist_id`) REFERENCES `artist`(`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_music_user`   FOREIGN KEY (`user_id`)   REFERENCES `sys_user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐分享表';


-- ============================================================
-- 6. 周边图鉴表 (merchandise) — "芒园周边"
--    区分小卡、手幅、实物，包含高清图和分类标签
-- ============================================================
CREATE TABLE `merchandise` (
    `id`                BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '周边ID',
    `name`              VARCHAR(200)    NOT NULL                 COMMENT '周边名称',
    `category`          ENUM('PHOTOCARD','HAND_BANNER','PHYSICAL')
                                        NOT NULL                 COMMENT '分类: PHOTOCARD-小卡, HAND_BANNER-手幅, PHYSICAL-实物',
    `sub_category`      VARCHAR(50)     DEFAULT NULL             COMMENT '二级分类(如: 官方/饭制, 专辑卡/特典卡)',
    `description`       TEXT            DEFAULT NULL             COMMENT '周边描述',
    `high_res_image_url` VARCHAR(500)   NOT NULL                 COMMENT '高清图URL',
    `thumbnail_url`     VARCHAR(500)    DEFAULT NULL             COMMENT '缩略图URL',
    `tags`              VARCHAR(500)    DEFAULT NULL             COMMENT '标签,逗号分隔(如: 限量,签名,稀有)',
    `artist_id`         BIGINT          DEFAULT NULL             COMMENT '关联艺人ID',
    `publish_date`      DATE            DEFAULT NULL             COMMENT '发行日期',
    `rarity`            ENUM('COMMON','RARE','LIMITED','LEGENDARY')
                                        NOT NULL DEFAULT 'COMMON' COMMENT '稀有度: COMMON-普通, RARE-稀有, LIMITED-限定, LEGENDARY-传说',
    `status`            TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 0-下架, 1-正常',
    `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_artist_id` (`artist_id`),
    KEY `idx_rarity` (`rarity`),
    CONSTRAINT `fk_merch_artist` FOREIGN KEY (`artist_id`) REFERENCES `artist`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='周边图鉴表';


-- ============================================================
-- 7. 作品表 (fan_work) — "创作者殿堂"
--    区分路透/修图/剪辑，关联创作者ID
-- ============================================================
CREATE TABLE `fan_work` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '作品ID',
    `title`         VARCHAR(200)    NOT NULL                 COMMENT '作品标题',
    `description`   TEXT            DEFAULT NULL             COMMENT '作品描述',
    `category`      ENUM('PREVIEW','EDITED_PHOTO','VIDEO_EDIT')
                                    NOT NULL                 COMMENT '分类: PREVIEW-路透, EDITED_PHOTO-修图, VIDEO_EDIT-剪辑',
    `file_url`      VARCHAR(500)    NOT NULL                 COMMENT '作品文件URL',
    `cover_url`     VARCHAR(500)    DEFAULT NULL             COMMENT '封面图URL(视频类作品用)',
    `duration`      INT             DEFAULT NULL             COMMENT '时长(秒,视频类)',
    `artist_id`     BIGINT          DEFAULT NULL             COMMENT '关联艺人ID',
    `creator_id`    BIGINT          NOT NULL                 COMMENT '创作者用户ID',
    `view_count`    INT             NOT NULL DEFAULT 0       COMMENT '浏览次数',
    `like_count`    INT             NOT NULL DEFAULT 0       COMMENT '点赞数',
    `comment_count` INT             NOT NULL DEFAULT 0       COMMENT '评论数',
    `status`        ENUM('DRAFT','PUBLISHED','HIDDEN')
                                    NOT NULL DEFAULT 'PUBLISHED' COMMENT '状态: DRAFT-草稿, PUBLISHED-已发布, HIDDEN-隐藏',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_artist_id` (`artist_id`),
    KEY `idx_creator_id` (`creator_id`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_work_artist`  FOREIGN KEY (`artist_id`)  REFERENCES `artist`(`id`)   ON DELETE SET NULL,
    CONSTRAINT `fk_work_creator` FOREIGN KEY (`creator_id`) REFERENCES `sys_user`(`id`)  ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='创作者作品表';


-- ============================================================
-- 8. 用户养成表 (user_tree) — "我的芒果树"
--    存储等级、经验值、树木装扮(JSON)
-- ============================================================
CREATE TABLE `user_tree` (
    `id`                BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '养成记录ID',
    `user_id`           BIGINT          NOT NULL                 COMMENT '用户ID',
    `level`             INT             NOT NULL DEFAULT 1       COMMENT '当前等级',
    `experience`        BIGINT          NOT NULL DEFAULT 0       COMMENT '当前经验值',
    `tree_name`         VARCHAR(100)    DEFAULT NULL             COMMENT '芒果树名称(用户自定义)',
    `tree_decoration`   JSON            DEFAULT NULL             COMMENT '当前装备的装扮 {"leaf":"leaf_01","flower":null,"fruit":"fruit_03","light":"light_01","pot":"pot_default","background":"bg_spring"}',
    `total_likes`       INT             NOT NULL DEFAULT 0       COMMENT '累计获赞数',
    `total_uploads`     INT             NOT NULL DEFAULT 0       COMMENT '累计上传数',
    `consecutive_days`  INT             NOT NULL DEFAULT 0       COMMENT '连续登录天数',
    `last_checkin_at`   DATE            DEFAULT NULL             COMMENT '最后签到日期',
    `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    CONSTRAINT `fk_tree_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户芒果树养成表';


-- ============================================================
-- 9. 装扮商店表 (tree_decoration) — 芒果树装扮
-- ============================================================
CREATE TABLE `tree_decoration` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '装扮ID',
    `name`          VARCHAR(100)    NOT NULL                 COMMENT '装扮名称',
    `type`          ENUM('LEAF','FLOWER','FRUIT','LIGHT','POT','BACKGROUND')
                                    NOT NULL                 COMMENT '装扮类型: LEAF-叶子, FLOWER-花, FRUIT-果实, LIGHT-灯光, POT-花盆, BACKGROUND-背景',
    `image_url`     VARCHAR(500)    NOT NULL                 COMMENT '装扮图片URL',
    `description`   VARCHAR(500)    DEFAULT NULL             COMMENT '装扮描述',
    `unlock_level`  INT             NOT NULL DEFAULT 1       COMMENT '解锁所需等级',
    `unlock_exp`    BIGINT          NOT NULL DEFAULT 0       COMMENT '解锁所需额外经验',
    `status`        TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 0-下架, 1-正常',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_unlock_level` (`unlock_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='装扮商店表';


-- ============================================================
-- 10. 用户装扮表 (user_decoration)
--     记录用户拥有的装扮及装备状态
-- ============================================================
CREATE TABLE `user_decoration` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '记录ID',
    `user_id`       BIGINT          NOT NULL                 COMMENT '用户ID',
    `decoration_id` BIGINT          NOT NULL                 COMMENT '装扮ID',
    `is_equipped`   TINYINT         NOT NULL DEFAULT 0       COMMENT '是否装备: 0-未装备, 1-已装备',
    `acquired_at`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_decoration` (`user_id`, `decoration_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_decoration_id` (`decoration_id`),
    CONSTRAINT `fk_ud_user`       FOREIGN KEY (`user_id`)       REFERENCES `sys_user`(`id`)        ON DELETE CASCADE,
    CONSTRAINT `fk_ud_decoration` FOREIGN KEY (`decoration_id`) REFERENCES `tree_decoration`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户装扮表';


-- ============================================================
-- 11. 签到记录表 (checkin_record) — 配合芒果树连续签到
-- ============================================================
CREATE TABLE `checkin_record` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '签到记录ID',
    `user_id`       BIGINT          NOT NULL                 COMMENT '用户ID',
    `checkin_date`  DATE            NOT NULL                 COMMENT '签到日期',
    `exp_earned`    INT             NOT NULL DEFAULT 0       COMMENT '获得经验值',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_date` (`user_id`, `checkin_date`),
    CONSTRAINT `fk_checkin_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='签到记录表';


-- ============================================================
-- 12. 题库表 (quiz_question) — "芒园小游戏"题库
-- ============================================================
CREATE TABLE `quiz_question` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '题目ID',
    `question`      TEXT            NOT NULL                 COMMENT '题目内容',
    `option_a`      VARCHAR(500)    NOT NULL                 COMMENT '选项A',
    `option_b`      VARCHAR(500)    NOT NULL                 COMMENT '选项B',
    `option_c`      VARCHAR(500)    NOT NULL                 COMMENT '选项C',
    `option_d`      VARCHAR(500)    NOT NULL                 COMMENT '选项D',
    `correct_answer` CHAR(1)        NOT NULL                 COMMENT '正确答案(A/B/C/D)',
    `explanation`   TEXT            DEFAULT NULL             COMMENT '答案解析',
    `difficulty`    ENUM('EASY','MEDIUM','HARD')
                                    NOT NULL DEFAULT 'EASY'  COMMENT '难度',
    `points`        INT             NOT NULL DEFAULT 10      COMMENT '答对得分',
    `artist_id`     BIGINT          DEFAULT NULL             COMMENT '关联艺人(粉丝向题目)',
    `status`        TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 0-禁用, 1-启用',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_difficulty` (`difficulty`),
    KEY `idx_artist_id` (`artist_id`),
    CONSTRAINT `fk_quiz_artist` FOREIGN KEY (`artist_id`) REFERENCES `artist`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游戏题库表';


-- ============================================================
-- 13. 闯关记录表 (quiz_record)
--     记录用户每次答题情况
-- ============================================================
CREATE TABLE `quiz_record` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '记录ID',
    `user_id`       BIGINT          NOT NULL                 COMMENT '用户ID',
    `question_id`   BIGINT          NOT NULL                 COMMENT '题目ID',
    `user_answer`   CHAR(1)         DEFAULT NULL             COMMENT '用户答案(A/B/C/D)',
    `is_correct`    TINYINT         NOT NULL DEFAULT 0       COMMENT '是否正确: 0-错误, 1-正确',
    `points_earned` INT             NOT NULL DEFAULT 0       COMMENT '获得分数',
    `answered_at`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '答题时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_question_id` (`question_id`),
    CONSTRAINT `fk_record_user`     FOREIGN KEY (`user_id`)     REFERENCES `sys_user`(`id`)      ON DELETE CASCADE,
    CONSTRAINT `fk_record_question` FOREIGN KEY (`question_id`) REFERENCES `quiz_question`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='答题闯关记录表';


-- ============================================================
-- 14. 游戏关卡表 (quiz_level)
--     定义关卡配置
-- ============================================================
CREATE TABLE `quiz_level` (
    `id`                BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '关卡ID',
    `level_number`      INT             NOT NULL                 COMMENT '关卡序号',
    `name`              VARCHAR(100)    NOT NULL                 COMMENT '关卡名称',
    `description`       VARCHAR(500)    DEFAULT NULL             COMMENT '关卡描述',
    `required_questions` INT            NOT NULL DEFAULT 10      COMMENT '需要回答的题目数',
    `pass_score`        INT             NOT NULL DEFAULT 60      COMMENT '通过分数(总分百分比)',
    `reward_exp`        BIGINT          NOT NULL DEFAULT 100     COMMENT '通关奖励经验值',
    `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_level_number` (`level_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游戏关卡表';


-- ============================================================
-- 15. 用户关卡进度表 (user_quiz_progress)
-- ============================================================
CREATE TABLE `user_quiz_progress` (
    `id`                BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '进度ID',
    `user_id`           BIGINT          NOT NULL                 COMMENT '用户ID',
    `level_id`          BIGINT          NOT NULL                 COMMENT '关卡ID',
    `score`             INT             NOT NULL DEFAULT 0       COMMENT '得分',
    `total_questions`   INT             NOT NULL DEFAULT 0       COMMENT '已答题数',
    `correct_count`     INT             NOT NULL DEFAULT 0       COMMENT '正确数',
    `is_passed`         TINYINT         NOT NULL DEFAULT 0       COMMENT '是否通关: 0-未通过, 1-已通过',
    `completed_at`      DATETIME        DEFAULT NULL             COMMENT '完成时间',
    `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_level` (`user_id`, `level_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_level_id` (`level_id`),
    CONSTRAINT `fk_progress_user`  FOREIGN KEY (`user_id`)  REFERENCES `sys_user`(`id`)   ON DELETE CASCADE,
    CONSTRAINT `fk_progress_level` FOREIGN KEY (`level_id`) REFERENCES `quiz_level`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关卡进度表';


-- ============================================================
-- 16. 评论/留言表 (comment) — "音符互动"
--     支持多级回复(parent_id自关联)
-- ============================================================
CREATE TABLE `comment` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '评论ID',
    `user_id`       BIGINT          NOT NULL                 COMMENT '评论用户ID',
    `target_type`   ENUM('MUSIC','ALBUM','FAN_WORK','MERCHANDISE')
                                    NOT NULL                 COMMENT '评论目标类型',
    `target_id`     BIGINT          NOT NULL                 COMMENT '评论目标ID',
    `content`       TEXT            NOT NULL                 COMMENT '评论内容',
    `parent_id`     BIGINT          DEFAULT NULL             COMMENT '父评论ID(回复用,自关联)',
    `like_count`    INT             NOT NULL DEFAULT 0       COMMENT '点赞数',
    `status`        TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 0-删除, 1-正常',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_target` (`target_type`, `target_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_comment_user`   FOREIGN KEY (`user_id`)   REFERENCES `sys_user`(`id`)  ON DELETE CASCADE,
    CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `comment`(`id`)   ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论留言表';


-- ============================================================
-- 17. 点赞记录表 (like_record)
--     通用点赞(作品/评论/音乐)
-- ============================================================
CREATE TABLE `like_record` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '点赞记录ID',
    `user_id`       BIGINT          NOT NULL                 COMMENT '用户ID',
    `target_type`   ENUM('FAN_WORK','COMMENT','MUSIC')
                                    NOT NULL                 COMMENT '点赞目标类型',
    `target_id`     BIGINT          NOT NULL                 COMMENT '点赞目标ID',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
    KEY `idx_target` (`target_type`, `target_id`),
    CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞记录表';


-- ============================================================
-- 18. 收藏表 (favorite)
--     通用收藏(音乐/作品/相册)
-- ============================================================
CREATE TABLE `favorite` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '收藏ID',
    `user_id`       BIGINT          NOT NULL                 COMMENT '用户ID',
    `target_type`   ENUM('MUSIC','FAN_WORK','ALBUM')
                                    NOT NULL                 COMMENT '收藏目标类型',
    `target_id`     BIGINT          NOT NULL                 COMMENT '收藏目标ID',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
    KEY `idx_target` (`target_type`, `target_id`),
    CONSTRAINT `fk_fav_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';


-- ============================================================
-- 19. 系统日志表 (sys_log) [可选]
--     记录关键操作
-- ============================================================
CREATE TABLE `sys_log` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '日志ID',
    `user_id`       BIGINT          DEFAULT NULL             COMMENT '操作用户ID',
    `username`      VARCHAR(50)     DEFAULT NULL             COMMENT '操作用户名',
    `module`        VARCHAR(50)     NOT NULL                 COMMENT '操作模块',
    `action`        VARCHAR(50)     NOT NULL                 COMMENT '操作类型',
    `description`   VARCHAR(500)    DEFAULT NULL             COMMENT '操作描述',
    `ip_address`    VARCHAR(50)     DEFAULT NULL             COMMENT 'IP地址',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_module` (`module`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统操作日志表';


-- ============================================================
-- 20. 艺人行程表 (schedule)
--     后台管理行程增删改查
-- ============================================================
CREATE TABLE `schedule` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '行程ID',
    `title`         VARCHAR(200)    NOT NULL                 COMMENT '行程标题',
    `description`   TEXT            DEFAULT NULL             COMMENT '行程描述',
    `schedule_type` ENUM('PERFORMANCE','FANMEETING','VARIETY','AIRPORT','OTHER')
                                    NOT NULL DEFAULT 'OTHER' COMMENT '行程类型: PERFORMANCE-演出, FANMEETING-见面会, VARIETY-综艺, AIRPORT-机场, OTHER-其他',
    `location`      VARCHAR(200)    DEFAULT NULL             COMMENT '地点',
    `start_time`    DATETIME        NOT NULL                 COMMENT '开始时间',
    `end_time`      DATETIME        DEFAULT NULL             COMMENT '结束时间',
    `artist_id`     BIGINT          DEFAULT NULL             COMMENT '关联艺人ID',
    `cover_url`     VARCHAR(500)    DEFAULT NULL             COMMENT '封面图',
    `status`        TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 0-取消, 1-正常',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_artist_id` (`artist_id`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_schedule_type` (`schedule_type`),
    CONSTRAINT `fk_schedule_artist` FOREIGN KEY (`artist_id`) REFERENCES `artist`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='艺人行程表';


-- ============================================================
-- 21. 生日祝福表 (birthday_wish)
--     粉丝为艺人发送生日祝福，支持匿名署名
-- ============================================================
CREATE TABLE `birthday_wish` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '祝福ID',
    `artist_id`   BIGINT       NOT NULL              COMMENT '艺人ID',
    `user_id`     BIGINT       DEFAULT NULL          COMMENT '用户ID(可匿名)',
    `content`     TEXT         NOT NULL              COMMENT '祝福内容',
    `author_name` VARCHAR(50)  DEFAULT NULL          COMMENT '署名',
    `status`      TINYINT      NOT NULL DEFAULT 0    COMMENT '审核状态: 0-待审核, 1-已通过, 2-已拒绝',
    `likes`       INT          NOT NULL DEFAULT 0    COMMENT '点赞数',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_artist_id` (`artist_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_wish_artist` FOREIGN KEY (`artist_id`) REFERENCES `artist`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_wish_user`   FOREIGN KEY (`user_id`)   REFERENCES `sys_user`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='生日祝福表';


-- ============================================================
-- 初始化数据: 默认管理员
-- 密码: admin123 (BCrypt加密)
-- ============================================================
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `bio`)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'ADMIN', '青芒纪系统管理员');

-- ============================================================
-- 22. 修改用户角色 ENUM：添加 SUPER_ADMIN（超级管理员）
-- ============================================================
ALTER TABLE `sys_user` MODIFY COLUMN `role` ENUM('FAN','CREATOR','ADMIN','SUPER_ADMIN')
    NOT NULL DEFAULT 'FAN' COMMENT '角色: FAN-粉丝, CREATOR-创作者, ADMIN-种植园管理员, SUPER_ADMIN-超级管理员';

-- ============================================================
-- 23. 媒体资产表 (media_assets) — 内容审核统一入口
--     用于照片馆藏、影像存档、创作者产出等用户上传内容的审核
-- ============================================================
CREATE TABLE `media_assets` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '资产ID',
    `title`         VARCHAR(200)    NOT NULL                 COMMENT '标题',
    `file_url`      VARCHAR(500)    NOT NULL                 COMMENT '文件路径/URL',
    `thumbnail_url` VARCHAR(500)    DEFAULT NULL             COMMENT '缩略图URL',
    `uploader_id`   BIGINT          NOT NULL                 COMMENT '上传者用户ID',
    `category`      ENUM('PHOTO','VIDEO','WORK')
                                    NOT NULL                 COMMENT '分类: PHOTO-照片馆藏, VIDEO-影像存档, WORK-创作者产出',
    `description`   TEXT            DEFAULT NULL             COMMENT '描述',
    `status`        TINYINT         NOT NULL DEFAULT 0       COMMENT '审核状态: 0-待审核, 1-已通过, 2-已驳回',
    `is_deleted`    TINYINT         NOT NULL DEFAULT 0       COMMENT '是否软删除: 0-正常, 1-已删除',
    `reject_reason` VARCHAR(500)    DEFAULT NULL             COMMENT '驳回原因',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_uploader_id` (`uploader_id`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    KEY `idx_is_deleted` (`is_deleted`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_status_deleted` (`status`, `is_deleted`),
    CONSTRAINT `fk_assets_uploader` FOREIGN KEY (`uploader_id`) REFERENCES `sys_user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='媒体资产审核表';

-- ============================================================
-- 24. 审核日志表 (audit_logs)
--     记录所有审核相关的操作（上传、修改、删除申请）
-- ============================================================
CREATE TABLE `audit_logs` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '日志ID',
    `action`        ENUM('UPLOAD','MODIFY','DELETE')
                                    NOT NULL                 COMMENT '动作: UPLOAD-上传, MODIFY-修改, DELETE-删除申请',
    `target_id`     BIGINT          NOT NULL                 COMMENT '目标资产ID(media_assets.id)',
    `initiator_id`  BIGINT          NOT NULL                 COMMENT '发起人用户ID',
    `auditor_id`    BIGINT          DEFAULT NULL             COMMENT '审核人用户ID(超级管理员)',
    `audit_status`  TINYINT         NOT NULL DEFAULT 0       COMMENT '审核状态: 0-待审核, 1-已同意, 2-已驳回',
    `audit_comment` VARCHAR(500)    DEFAULT NULL             COMMENT '审核备注/驳回理由',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_target_id` (`target_id`),
    KEY `idx_initiator_id` (`initiator_id`),
    KEY `idx_auditor_id` (`auditor_id`),
    KEY `idx_audit_status` (`audit_status`),
    KEY `idx_action_status` (`action`, `audit_status`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_audit_target`    FOREIGN KEY (`target_id`)    REFERENCES `media_assets`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_audit_initiator` FOREIGN KEY (`initiator_id`) REFERENCES `sys_user`(`id`)     ON DELETE CASCADE,
    CONSTRAINT `fk_audit_auditor`   FOREIGN KEY (`auditor_id`)   REFERENCES `sys_user`(`id`)     ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审核日志表';

-- 插入默认超级管理员（密码: admin123, BCrypt加密）
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `bio`)
VALUES ('superadmin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '超级管理员', 'SUPER_ADMIN', '青芒纪超级管理员，负责内容审核');

-- ============================================================
-- 初始化数据
-- ============================================================

-- 初始化装扮数据
INSERT INTO `tree_decoration` (`name`, `type`, `image_url`, `description`, `unlock_level`, `unlock_exp`) VALUES
('嫩绿新叶', 'LEAF',      '/assets/decorations/leaf_default.png',   '芒果树最初的绿叶',               1,  0),
('金色叶片', 'LEAF',      '/assets/decorations/leaf_golden.png',    '闪耀着金色光芒的叶片',             5,  500),
('樱花',     'FLOWER',    '/assets/decorations/flower_sakura.png',  '粉色的樱花，春天的气息',          3,  300),
('小芒果',   'FRUIT',     '/assets/decorations/fruit_default.png',  '青涩的小芒果，还未成熟',           1,  0),
('星光灯',   'LIGHT',     '/assets/decorations/light_star.png',     '星星形状的装饰灯',               10, 1000),
('陶土花盆', 'POT',       '/assets/decorations/pot_default.png',    '朴素的陶土花盆',                 1,  0),
('春日背景', 'BACKGROUND','/assets/decorations/bg_spring.png',     '温暖的春日阳光背景',              2,  100);
