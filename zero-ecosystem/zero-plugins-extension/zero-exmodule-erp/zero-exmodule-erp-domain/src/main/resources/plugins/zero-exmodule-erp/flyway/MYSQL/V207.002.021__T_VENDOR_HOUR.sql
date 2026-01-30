-- liquibase formatted sql

-- changeset Lang:t-vendor-hour-1
DROP TABLE IF EXISTS `T_VENDOR_HOUR`;

CREATE TABLE IF NOT EXISTS `T_VENDOR_HOUR` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                VARCHAR(36)  NOT NULL COLLATE utf8mb4_bin COMMENT '「id」- 主键',                    -- Ticket Primary Key

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `CLASSIFICATION`    VARCHAR(64)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「classification」- 分类',    -- The ticket related income type
    `REQUEST_TYPE`      VARCHAR(64)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「requestType」- 工时类型',    -- Request type of hour
    `FROM_TYPE`         VARCHAR(36)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「fromType」- 开始类型',       -- Type marker for start (e.g. AM/PM)
    `TO_TYPE`           VARCHAR(36)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「toType」- 结束类型',         -- Type marker for end

    -- ==================================================================================================
    -- 🕒 3. 时间与周期 (Time & Duration)
    -- ==================================================================================================
    `START_AT`          DATETIME     DEFAULT NULL COMMENT '「startAt」- 开始时间',                           -- From (General)
    `END_AT`            DATETIME     DEFAULT NULL COMMENT '「endAt」- 结束时间',                             -- To (General)
    `FROM_AT`           DATETIME     DEFAULT NULL COMMENT '「fromAt」- 具体起始点',                          -- Specific start point
    `TO_AT`             DATETIME     DEFAULT NULL COMMENT '「toAt」- 具体结束点',                            -- Specific end point
    `DAYS`              INT          DEFAULT NULL COMMENT '「days」- 持续天数',                              -- Duration

    -- ==================================================================================================
    -- 📦 4. 扩展信息 (Extensions)
    -- ==================================================================================================
    `COMMENT_EXTENSION` LONGTEXT     COLLATE utf8mb4_bin COMMENT '「commentExtension」- 扩展备注',        -- Extension Comment

    -- ==================================================================================================
    -- ⚡ 7. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='供应商 - 工时申报';