-- liquibase formatted sql

-- changeset Lang:t-vendor-check-out-1
DROP TABLE IF EXISTS `T_VENDOR_CHECK_OUT`;

CREATE TABLE IF NOT EXISTS `T_VENDOR_CHECK_OUT` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                VARCHAR(36)  NOT NULL COLLATE utf8mb4_bin COMMENT '「id」- 主键',                    -- Ticket Primary Key

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `CLASSIFICATION`    VARCHAR(64)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「classification」- 退场类型', -- The ticket related income type / Check-out Type
    `LEAVE_AT`          DATETIME     DEFAULT NULL COMMENT '「leaveAt」- 离场时间',                           -- Time of leaving
    `REASON`            LONGTEXT     COLLATE utf8mb4_bin COMMENT '「reason」- 原因',                         -- The reason to be done

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
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='供应商 - 离场登记';