-- liquibase formatted sql

-- changeset Lang:kp-report-1
-- 报表主表：KP_REPORT
-- 一份报表定义：REPORT x 1 + DIMENSION x 1 + FEATURE x N
--   特征是字段需要特殊处理时才会出现，所以 N 可能会是 0
DROP TABLE IF EXISTS KP_REPORT;
CREATE TABLE IF NOT EXISTS KP_REPORT
(
    `KEY`           VARCHAR(36) COMMENT '「key」- 报表主键',
    `NAME`          VARCHAR(255) COMMENT '「name」- 表表名称',
    `CODE`          VARCHAR(36) COMMENT '「code」- 报表编码',
    `STATUS`        VARCHAR(255) COMMENT '「status」- 报表状态',

    `TITLE`         VARCHAR(255) COMMENT '「title」- 报表标题',

    `REPORT_PARAM`  LONGTEXT COMMENT '「reportParam」- 报表参数配置',
    `REPORT_CONFIG` LONGTEXT COMMENT '「reportConfig」- 主表基础配置',
    `REPORT_BY`     VARCHAR(36) COMMENT '「reportBy」- 模板创建人',
    `REPORT_AT`     DATETIME COMMENT '「reportAt」- 模板创建时间',

    -- 关联数据源
    `DATA_SET_ID`   VARCHAR(36) COMMENT '「dataSetId」- 数据源ID',
    `DATA_TPL_ID`   VARCHAR(36) COMMENT '「dataTplId」- 关联报表模板',

    -- 报表归属（反向广义关联）
    `APP_ID`        VARCHAR(36) COMMENT '「appId」- 应用数据',

    -- 特殊字段
    `ACTIVE`        BIT         DEFAULT NULL COMMENT '「active」- 是否启用',
    `SIGMA`         VARCHAR(32) DEFAULT NULL COMMENT '「sigma」- 统一标识',
    `METADATA`      TEXT COMMENT '「metadata」- 附加配置',
    `LANGUAGE`      VARCHAR(8)  DEFAULT NULL COMMENT '「language」- 使用的语言',

    -- Auditor字段
    `CREATED_AT`    DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`    VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`    DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`    VARCHAR(36) COMMENT '「updatedBy」- 更新人',
    PRIMARY KEY (`KEY`)
);
-- changeset Lang:kp-report-2
-- Unique Key: 独立唯一键定义
ALTER TABLE KP_REPORT
    ADD UNIQUE (`NAME`, `SIGMA`) USING BTREE;
ALTER TABLE KP_REPORT
    ADD UNIQUE (`CODE`, `SIGMA`) USING BTREE;