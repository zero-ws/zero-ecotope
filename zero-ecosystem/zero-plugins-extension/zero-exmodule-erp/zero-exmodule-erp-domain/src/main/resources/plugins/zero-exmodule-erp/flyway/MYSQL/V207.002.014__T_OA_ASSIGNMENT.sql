-- liquibase formatted sql

-- changeset Lang:t-oa-assignment-1
DROP TABLE IF EXISTS `T_OA_ASSIGNMENT`;

CREATE TABLE IF NOT EXISTS `T_OA_ASSIGNMENT` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                VARCHAR(36)  NOT NULL COLLATE utf8mb4_bin COMMENT '「id」- 主键',                   -- Ticket Primary Key

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `COMMENT_EXTENSION` LONGTEXT     COLLATE utf8mb4_bin COMMENT '「commentExtension」- 扩展备注',           -- Extension Comment
    `REQUEST_BY`        VARCHAR(36)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「requestBy」- 申请人',       -- Request User
    `START_AT`          DATETIME     DEFAULT NULL COMMENT '「startAt」- 开始时间',                           -- From
    `END_AT`            DATETIME     DEFAULT NULL COMMENT '「endAt」- 结束时间',                             -- To
    `DAYS`              INT          DEFAULT NULL COMMENT '「days」- 持续天数',                              -- Duration
    `REASON`            LONGTEXT     COLLATE utf8mb4_bin COMMENT '「reason」- 原因',                         -- The reason to be done
    `WORK_CONTENT`      LONGTEXT     COLLATE utf8mb4_bin COMMENT '「workContent」- 工作内容',                -- Working Assignment Content / WTodo

    -- ==================================================================================================
    -- ⚡ 7. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='OA - 任务分配';