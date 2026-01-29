DROP TABLE IF EXISTS `M_MODEL`;
CREATE TABLE IF NOT EXISTS `M_MODEL` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                    -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `ALIAS`             VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「alias」- 别名',             -- 模型别名（业务名）
    `CATEGORY_ID`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「categoryId」- 关联的类型的ID',
    `CATEGORY_TREE`     VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「categoryTree」- 当前模型所属的类型树',
    `COMMENTS`          TEXT          COLLATE utf8mb4_bin COMMENT '「comments」- 当前模型的描述信息',
    `IDENTIFIER`        VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「identifier」- 当前模型全局唯一ID',
    `IS_TRACK`          BIT(1)        DEFAULT NULL COMMENT '「isTrack」- 该组件为 tra',                       -- 该组件为 track 表示执行 ACTIVITY 的变更监控功能（生成变更历史）
    `NAME`              VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `NAMESPACE`         VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「namespace」- 当前模型使用的名空间',
    `RULE_UNIQUE`       TEXT          COLLATE utf8mb4_bin COMMENT '「ruleUnique」- 当前模型的标识规则',
    `SPIDER`            TEXT          COLLATE utf8mb4_bin COMMENT '「spider」- 主图格式',                     -- 主图格式，存储当前模型为起点的图层Json数据，每个模型只有一张
    `SPIDER_COMPONENT`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「spiderComponent」- 在主图格式上存', -- 在主图格式上存在的插件信息，用于处理主图格式的内容

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`              VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',              -- [类型],

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`             VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',         -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',          -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`            VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',             -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`            BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                            -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`          VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',      -- [国际化] 如: zh_CN, en_US,
    `METADATA`          TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                     -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`           VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`        DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',                         -- [审计] 创建时间
    `CREATED_BY`        VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',       -- [审计] 创建人
    `UPDATED_AT`        DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                         -- [审计] 更新时间
    `UPDATED_BY`        VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',       -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_M_MODEL_NAMESPACE_IDENTIFIER` (`NAMESPACE`, `IDENTIFIER`) USING BTREE,
    UNIQUE KEY `UK_M_MODEL_NAMESPACE_NAME` (`NAMESPACE`, `NAME`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='M_MODEL';

-- 缺失公共字段：
-- - VERSION (版本)