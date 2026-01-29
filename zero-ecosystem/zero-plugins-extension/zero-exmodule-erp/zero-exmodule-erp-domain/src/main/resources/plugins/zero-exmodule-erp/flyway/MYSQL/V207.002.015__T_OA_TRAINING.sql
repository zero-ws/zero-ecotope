-- liquibase formatted sql

-- changeset Lang:t-oa-training-1
DROP TABLE IF EXISTS `T_OA_TRAINING`;

CREATE TABLE IF NOT EXISTS `T_OA_TRAINING` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                VARCHAR(36)  NOT NULL COLLATE utf8mb4_bin COMMENT '「id」- 主键',                    -- Ticket Primary Key

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `TRAIN_MODE`        VARCHAR(64)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「trainMode」- 培训模式',     -- The mode of training
    `TRAIN_LOCATION`    LONGTEXT     COLLATE utf8mb4_bin COMMENT '「trainLocation」- 培训地点',              -- The location for training
    `START_AT`          DATETIME     DEFAULT NULL COMMENT '「startAt」- 开始时间',                           -- From
    `END_AT`            DATETIME     DEFAULT NULL COMMENT '「endAt」- 结束时间',                             -- To

    -- ==================================================================================================
    -- 👥 3. 角色与审批 (Roles & Reviews)
    -- ==================================================================================================
    `LEADER`            VARCHAR(36)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「leader」- 负责人',          -- The training leader
    `LEADER_COMMENT`    LONGTEXT     COLLATE utf8mb4_bin COMMENT '「leaderComment」- 负责人意见',            -- Comment from leader
    `REVIEWER`          VARCHAR(36)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「reviewer」- 复核人',        -- The training reviewer
    `REVIEWER_COMMENT`  LONGTEXT     COLLATE utf8mb4_bin COMMENT '「reviewerComment」- 复核人意见',          -- Comment from reviewer

    -- ==================================================================================================
    -- 📦 4. 扩展信息 (Extensions)
    -- ==================================================================================================
    `COMMENT_EXTENSION` LONGTEXT     COLLATE utf8mb4_bin COMMENT '「commentExtension」- 扩展备注',        -- Extension Comment

    -- ==================================================================================================
    -- ⚡ 7. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='OA - 培训记录';