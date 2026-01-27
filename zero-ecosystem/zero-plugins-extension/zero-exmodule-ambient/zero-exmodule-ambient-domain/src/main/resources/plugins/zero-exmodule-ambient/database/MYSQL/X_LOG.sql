-- liquibase formatted sql

-- changeset Lang:ox-info-1
-- 应用程序表：X_LOG
DROP TABLE IF EXISTS X_LOG;
CREATE TABLE IF NOT EXISTS X_LOG
(
    `KEY`
    VARCHAR
(
    36
) COMMENT '「key」- 日志的主键',
    `TYPE` VARCHAR
(
    64
) COMMENT '「type」- 日志的分类',
    `LEVEL` VARCHAR
(
    10
) COMMENT '「level」- 日志级别：ERROR / WARN / INFO',

    -- 日志内容信息
    `INFO_STACK` TEXT COMMENT '「infoStack」- 堆栈信息',
    `INFO_SYSTEM` TEXT COMMENT '「infoSystem」- 日志内容',
    `INFO_READABLE` TEXT COMMENT '「infoReadable」- 日志的可读信息',
    `INFO_AT` DATETIME COMMENT '「infoAt」- 日志记录时间',

    -- 日志扩展信息
    `LOG_AGENT` VARCHAR
(
    255
) COMMENT '「logAgent」- 记录日志的 agent 信息',
    `LOG_IP` VARCHAR
(
    255
) COMMENT '「logIp」- 日志扩展组件',
    `LOG_USER` VARCHAR
(
    36
) COMMENT '「logUser」- 日志记录人',

    -- ------------------------------ 公共字段 --------------------------------
    `SIGMA` VARCHAR
(
    128
) COMMENT '「sigma」- 用户组绑定的统一标识',
    `LANGUAGE` VARCHAR
(
    10
) COMMENT '「language」- 使用的语言',
    `ACTIVE` BIT COMMENT '「active」- 是否启用',
    `METADATA` TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT` DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY` VARCHAR
(
    36
) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT` DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY` VARCHAR
(
    36
) COMMENT '「updatedBy」- 更新人',

    `APP_ID` VARCHAR
(
    36
) COMMENT '「appId」- 应用ID',
    `TENANT_ID` VARCHAR
(
    36
) COMMENT '「tenantId」- 租户ID',
    PRIMARY KEY
(
    `KEY`
) USING BTREE
    );
-- 日志管理
ALTER TABLE X_LOG
    ADD INDEX IDXM_X_LOG_SIGMA_TYPE (`SIGMA`, `TYPE`) USING BTREE;