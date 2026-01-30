-- liquibase formatted sql

-- changeset Lang:t-vendor-check-in-1
DROP TABLE IF EXISTS `T_VENDOR_CHECK_IN`;

CREATE TABLE IF NOT EXISTS `T_VENDOR_CHECK_IN` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                VARCHAR(36)  NOT NULL COLLATE utf8mb4_bin COMMENT '「id」- 主键',                    -- Ticket Primary Key

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `CLASSIFICATION`    VARCHAR(64)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「classification」- 入驻类型', -- The ticket related income type
    `ONBOARD_AT`        DATETIME     DEFAULT NULL COMMENT '「onboardAt」- 入驻时间',                         -- Onboard Time / Check-in Time

    -- ==================================================================================================
    -- 🕒 3. 时间与周期 (Time & Duration)
    -- ==================================================================================================
    `START_AT`          DATETIME     DEFAULT NULL COMMENT '「startAt」- 开始时间',                           -- From
    `END_AT`            DATETIME     DEFAULT NULL COMMENT '「endAt」- 结束时间',                             -- To
    `DAYS`              INT          DEFAULT NULL COMMENT '「days」- 持续天数',                              -- Duration

    -- ==================================================================================================
    -- 📦 4. 扩展信息 (Extensions)
    -- ==================================================================================================
    `COMMENT_EXTENSION` LONGTEXT     COLLATE utf8mb4_bin COMMENT '「commentExtension」- 扩展备注',        -- Extension Comment

    -- ==================================================================================================
    -- ⚡ 7. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='供应商 - 入驻登记';