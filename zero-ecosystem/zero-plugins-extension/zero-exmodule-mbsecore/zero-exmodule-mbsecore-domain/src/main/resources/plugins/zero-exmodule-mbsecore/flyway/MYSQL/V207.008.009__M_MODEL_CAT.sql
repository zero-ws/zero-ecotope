-- liquibase formatted sql

-- changeset Lang:ox-modeldef-1
DROP TABLE IF EXISTS `M_MODEL_CAT`;

CREATE TABLE IF NOT EXISTS `M_MODEL_CAT` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`            VARCHAR(36)  NOT NULL COLLATE utf8mb4_bin COMMENT '「id」- 分类ID',                  -- [主键] 原 KEY 字段

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `NAME`          VARCHAR(255) DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「name」- 定义名称',           -- 定义名称，不可重复，位于模型分类管理顶层
    `CAT_NAME`      VARCHAR(255) DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「catName」- 分类别名',        -- 分类别名

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
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='M_MODEL_CAT';