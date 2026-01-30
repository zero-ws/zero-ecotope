-- liquibase formatted sql

-- changeset Lang:t-vendor-assessment-1
DROP TABLE IF EXISTS `T_VENDOR_ASSESSMENT`;

CREATE TABLE IF NOT EXISTS `T_VENDOR_ASSESSMENT` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                VARCHAR(36)  NOT NULL COLLATE utf8mb4_bin COMMENT '「id」- 主键',                    -- Ticket Primary Key

    -- ==================================================================================================
    -- 📝 2. 考核核心信息 (Assessment Core)
    -- ==================================================================================================
    `ASSESS_ON`         VARCHAR(36)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「assessOn」- 考核对象',      -- The user/vendor that will be assessed
    `ASSESS_SCORE`      INT          DEFAULT NULL COMMENT '「assessScore」- 考核得分',                       -- The score of the user
    `CLASSIFICATION`    VARCHAR(64)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「classification」- 考核类型', -- The ticket related income type/category

    -- ==================================================================================================
    -- 🕒 3. 时间与周期 (Time & Duration)
    -- ==================================================================================================
    `START_AT`          DATETIME     DEFAULT NULL COMMENT '「startAt」- 开始时间',                           -- From
    `END_AT`            DATETIME     DEFAULT NULL COMMENT '「endAt」- 结束时间',                             -- To
    `DAYS`              INT          DEFAULT NULL COMMENT '「days」- 持续天数',                              -- Duration

    -- ==================================================================================================
    -- 🗣️ 4. 多维评价 (Comments & Reviews)
    -- ==================================================================================================
    `COMMENT_DEPT`      LONGTEXT     COLLATE utf8mb4_bin COMMENT '「commentDept」- 部门评价',                -- from department
    `COMMENT_ASSESS`    LONGTEXT     COLLATE utf8mb4_bin COMMENT '「commentAssess」- 考核评价',              -- from assess
    `COMMENT_LEADER`    LONGTEXT     COLLATE utf8mb4_bin COMMENT '「commentLeader」- 领导评价',              -- from leader of project

    -- ==================================================================================================
    -- 📦 5. 扩展信息 (Extensions)
    -- ==================================================================================================
    `COMMENT_EXTENSION` LONGTEXT     COLLATE utf8mb4_bin COMMENT '「commentExtension」- 扩展备注',        -- Extension Comment

    -- ==================================================================================================
    -- ⚡ 7. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='供应商 - 考核评估';