-- liquibase formatted sql

-- changeset Lang:my-desktop-1
-- 个人应用表：MY_DESKTOP
DROP TABLE IF EXISTS MY_DESKTOP;
CREATE TABLE IF NOT EXISTS MY_DESKTOP
(
    `KEY`        VARCHAR(36) COMMENT '「key」- 个人工作台',

    `BAG_ID`     VARCHAR(36) COMMENT '「bagId」- 所属个人应用',
    -- 模块核心配置
    `UI_CONFIG`  LONGTEXT COMMENT '「uiConfig」- 看板专用配置',
    `UI_GRID`    LONGTEXT COMMENT '「uiGrid」- 看板布局配置',

    `OWNER`      VARCHAR(128) COMMENT '「owner」- 拥有者ID，我的 / 角色级',
    `OWNER_TYPE` VARCHAR(5) COMMENT '「ownerType」- ROLE 角色，USER 用户',

    -- ------------------------------ 公共字段 --------------------------------
    `SIGMA`      VARCHAR(128) COMMENT '「sigma」- 用户组绑定的统一标识',
    `LANGUAGE`   VARCHAR(10) COMMENT '「language」- 使用的语言',
    `ACTIVE`     BIT COMMENT '「active」- 是否启用',
    `METADATA`   TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT` DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY` VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT` DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY` VARCHAR(36) COMMENT '「updatedBy」- 更新人',

    `APP_ID`     VARCHAR(36) COMMENT '「appId」- 应用ID',
    `TENANT_ID`  VARCHAR(36) COMMENT '「tenantId」- 租户ID',
    PRIMARY KEY (`KEY`) USING BTREE
)