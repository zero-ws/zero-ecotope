-- liquibase formatted sql

-- changeset Lang:b-component-1
/*
 * BLOCK 中的界面资源定义（按页面分）
 * Java中的组件管理（Zero组件大盘点）
 * （管理端）
 */
DROP TABLE IF EXISTS B_COMPONENT;
CREATE TABLE IF NOT EXISTS B_COMPONENT
(
    `KEY`            VARCHAR(36) COMMENT '「key」- 主键',
    `BLOCK_ID`       VARCHAR(36) COMMENT '「blockId」- 所属模块ID',
    /*
     * 类型综述
     * 1. SL，ServiceLoader组件
     * 2. INNER，Zero内部专用组件
     * 3. EXTENSION，Zero Extension专用组件
     * 4. JET-API，zero-jet中定义的 API
     * 5. JET-JOB, zero-jet中定义的 JOB
     */
    `TYPE`           VARCHAR(64) COMMENT '「type」- 类型保留，单独区分',
    `MAVEN_AID`      VARCHAR(255) COMMENT '「mavenAid」- 所在项目ID',
    `MAVEN_GID`      VARCHAR(255) COMMENT '「mavenGid」- 所在Group的ID',

    `SPEC_INTERFACE` VARCHAR(255) COMMENT '「specInterface」- 接口名称',
    `SPEC_IMPL`      VARCHAR(255) COMMENT '「specImpl」- 实现组件',
    `INTEGRATED`     BIT DEFAULT NULL COMMENT '「integrated」- 是否用于外部集成',

    -- ------------------------------ 公共字段 --------------------------------
    `SIGMA`          VARCHAR(128) COMMENT '「sigma」- 用户组绑定的统一标识',
    `LANGUAGE`       VARCHAR(10) COMMENT '「language」- 使用的语言',
    `ACTIVE`         BIT COMMENT '「active」- 是否启用',
    `METADATA`       TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT`     DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`     VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`     DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`     VARCHAR(36) COMMENT '「updatedBy」- 更新人',

    `APP_ID`         VARCHAR(36) COMMENT '「appId」- 应用ID',
    `TENANT_ID`      VARCHAR(36) COMMENT '「tenantId」- 租户ID',
    PRIMARY KEY (`KEY`) USING BTREE
);
-- changeset Lang:b-component-2
ALTER TABLE B_COMPONENT
    ADD UNIQUE (`SPEC_IMPL`, `BLOCK_ID`);