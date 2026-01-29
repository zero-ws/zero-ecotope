-- liquibase formatted sql

-- changeset Lang:my-desktop-1
DROP TABLE IF EXISTS `MY_DESKTOP`;

CREATE TABLE IF NOT EXISTS `MY_DESKTOP` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`            VARCHAR(36)  NOT NULL COLLATE utf8mb4_bin COMMENT '「id」- 个人工作台',              -- [主键] 原 KEY 字段

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `BAG_ID`        VARCHAR(36)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「bagId」- 所属个人应用',
    `UI_CONFIG`     LONGTEXT     COLLATE utf8mb4_bin COMMENT '「uiConfig」- 看板配置',                   -- 看板专用配置
    `UI_GRID`       LONGTEXT     COLLATE utf8mb4_bin COMMENT '「uiGrid」- 布局配置',                     -- 看板布局配置
    `OWNER`         VARCHAR(128) DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「owner」- 拥有者ID',             -- 我的 / 角色级
    `OWNER_TYPE`    VARCHAR(5)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「ownerType」- 拥有者类型',       -- ROLE 角色，USER 用户

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`         VARCHAR(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',          -- 用户组绑定的统一标识
    `APP_ID`        VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',            -- 应用ID
    `TENANT_ID`     VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',         -- 租户ID
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`        BIT(1)       DEFAULT NULL COMMENT '「active」- 是否启用',                             -- 是否启用
    `LANGUAGE`      VARCHAR(10)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言',           -- 使用的语言
    `METADATA`      TEXT         COLLATE utf8mb4_bin COMMENT '「metadata」- 附加配置',                    -- 附加配置数据

    -- ==================================================================================================
    -- 🕒 5. 审计字段 (Audit Fields)
    -- ==================================================================================================
    `CREATED_AT`    DATETIME     DEFAULT NULL COMMENT '「createdAt」- 创建时间',                          -- 创建时间
    `CREATED_BY`    VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',        -- 创建人
    `UPDATED_AT`    DATETIME     DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                          -- 更新时间
    `UPDATED_BY`    VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',        -- 更新人

    -- ==================================================================================================
    -- ⚡ 7. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='个人工作台';