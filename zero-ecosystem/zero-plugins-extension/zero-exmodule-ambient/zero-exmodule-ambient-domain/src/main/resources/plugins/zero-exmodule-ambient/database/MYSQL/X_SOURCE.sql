-- liquibase formatted sql

-- changeset Lang:ox-source-1
-- 数据源专用表：X_SOURCE
DROP TABLE IF EXISTS X_SOURCE;
CREATE TABLE IF NOT EXISTS X_SOURCE
(
    `KEY`               VARCHAR(36) COMMENT '「key」- 数据源主键',
    `IP_V4`             VARCHAR(15) COMMENT '「ipV4」- IP v4地址',
    `IP_V6`             VARCHAR(40) COMMENT '「ipV6」- IP v6地址',
    `HOSTNAME`          VARCHAR(255) COMMENT '「hostname」- 主机地址',
    `PORT`              INTEGER COMMENT '「port」- 端口号',
    `CATEGORY`          VARCHAR(32) COMMENT '「category」- 数据库类型',
    `DRIVER_CLASS_NAME` VARCHAR(255) COMMENT '「driverClassName」- 数据库驱动指定，JDBC4之前',
    `JDBC_URL`          VARCHAR(1024) COMMENT '「jdbcUrl」- JDBC连接字符串',
    `JDBC_CONFIG`       TEXT COMMENT '「jdbcConfig」- 连接字符串中的配置key=findRunning',
    `INSTANCE`          VARCHAR(255) COMMENT '「instance」- 实例名称',
    `USERNAME`          VARCHAR(255) COMMENT '「username」- 账号',
    `PASSWORD`          VARCHAR(255) COMMENT '「password」- 密码',

    -- ------------------------------ 公共字段 --------------------------------
    `SIGMA`             VARCHAR(128) COMMENT '「sigma」- 用户组绑定的统一标识',
    `LANGUAGE`          VARCHAR(10) COMMENT '「language」- 使用的语言',
    `ACTIVE`            BIT COMMENT '「active」- 是否启用',
    `METADATA`          TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT`        DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`        VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`        DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`        VARCHAR(36) COMMENT '「updatedBy」- 更新人',

    `APP_ID`            VARCHAR(255) COMMENT '「appId」- 应用ID',
    `TENANT_ID`         VARCHAR(36) COMMENT '「tenantId」- 租户ID',
    PRIMARY KEY (`KEY`)
);

-- changeset Lang:ox-source-2
ALTER TABLE X_SOURCE
    ADD UNIQUE (`APP_ID`) USING BTREE; -- 目前应用程序和数据源一对一，暂定