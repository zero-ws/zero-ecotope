-- liquibase formatted sql

-- changeset Lang:ox-join-1
DROP TABLE IF EXISTS `M_JOIN`;

CREATE TABLE IF NOT EXISTS `M_JOIN` (
    -- ==================================================================================================
    -- 🔗 1. 核心主键区 (Composite Primary Key)
    -- ==================================================================================================
    `MODEL`       VARCHAR(32)  NOT NULL COLLATE utf8mb4_bin COMMENT '「model」- 模型标识',              -- 模型identifier
    `ENTITY`      VARCHAR(32)  NOT NULL COLLATE utf8mb4_bin COMMENT '「entity」- 实体标识',             -- 实体identifier
    `ENTITY_KEY`  VARCHAR(32)  NOT NULL COLLATE utf8mb4_bin COMMENT '「entityKey」- 实体主键',          -- 实体主键字段名
    `NAMESPACE`   VARCHAR(64)  NOT NULL COLLATE utf8mb4_bin COMMENT '「namespace」- 名空间',            -- 名空间（和App绑定的）

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `PRIORITY`    INT          DEFAULT 0 COMMENT '「priority」- 优先级',                               -- 优先级

    -- ==================================================================================================
    -- ⚡ 7. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`MODEL`, `ENTITY`, `ENTITY_KEY`, `NAMESPACE`) USING BTREE,                           -- [主键] 确保模型与实体的映射关系唯一
    KEY `IDXM_M_JOIN_NAMESPACE_MODEL` (`NAMESPACE`, `MODEL`) USING BTREE                              -- [查询] 优化查询
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='M_JOIN';

-- 缺失公共字段：
-- - CREATED_AT (创建时间)
-- - CREATED_BY (创建人)
-- - UPDATED_AT (更新时间)
-- - UPDATED_BY (更新人)
-- - ACTIVE (是否启用)
-- - LANGUAGE (语言)
-- - VERSION (版本)
-- - METADATA (元配置)
-- - SIGMA (统一标识)
-- - APP_ID (所属应用)
-- - TENANT_ID (所属租户)
-- - TYPE (类型)