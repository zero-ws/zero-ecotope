DROP TABLE IF EXISTS `KP_FEATURE`;
CREATE TABLE IF NOT EXISTS `KP_FEATURE` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`             VARCHAR(36)    COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                      -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `IN_COMPONENT`   LONGTEXT       COLLATE utf8mb4_bin COMMENT '「inComponent」- 特殊输出组件',
    `IN_CONFIG`      LONGTEXT       COLLATE utf8mb4_bin COMMENT '「inConfig」- 特殊输出配置',
    `NAME`           VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `OUT_COMPONENT`  LONGTEXT       COLLATE utf8mb4_bin COMMENT '「outComponent」- 特殊输出组件',
    `OUT_CONFIG`     LONGTEXT       COLLATE utf8mb4_bin COMMENT '「outConfig」- 特殊输出配置',
    `REPORT_ID`      VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「reportId」- 关联报表ID',
    `VALUE_CONFIG`   LONGTEXT       COLLATE utf8mb4_bin COMMENT '「valueConfig」- 特征配置',
    `VALUE_DISPLAY`  VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「valueDisplay」- 特征显示名称',
    `VALUE_PATH`     VARCHAR(1024)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「valuePath」- 特征名称',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`           VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',                -- [类型],
    `STATUS`         VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「status」- 状态',

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`          VARCHAR(128)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',           -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`      VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',            -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`         VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',               -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`         BIT(1)         DEFAULT NULL COMMENT '「active」- 是否启用',                              -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`       VARCHAR(10)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',        -- [国际化] 如: zh_CN, en_US,
    `METADATA`       TEXT           COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                       -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`        VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`     DATETIME       DEFAULT NULL COMMENT '「createdAt」- 创建时间',                           -- [审计] 创建时间
    `CREATED_BY`     VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',         -- [审计] 创建人
    `UPDATED_AT`     DATETIME       DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                           -- [审计] 更新时间
    `UPDATED_BY`     VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',         -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_KP_FEATURE_NAME_REPORT_ID_SIGMA` (`NAME`, `REPORT_ID`, `SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='KP_FEATURE';

-- 缺失公共字段：
-- - VERSION (版本)