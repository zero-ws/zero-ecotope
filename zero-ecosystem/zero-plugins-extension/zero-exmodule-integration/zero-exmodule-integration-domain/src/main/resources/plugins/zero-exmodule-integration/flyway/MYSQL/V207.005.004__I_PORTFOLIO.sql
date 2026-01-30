DROP TABLE IF EXISTS `I_PORTFOLIO`;
CREATE TABLE IF NOT EXISTS `I_PORTFOLIO` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                    -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `CODE`              VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 编号',
    `DATA_CONFIG`       LONGTEXT      COLLATE utf8mb4_bin COMMENT '「dataConfig」- 数据基础配置',
    `DATA_INTEGRATION`  LONGTEXT      COLLATE utf8mb4_bin COMMENT '「dataIntegration」- 绑定好过后',          -- 绑定好过后，导入/导出数据专用配置
    `DATA_KEY`          VARCHAR(512)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「dataKey」- LDAP路径做完',   -- LDAP路径做完整标识
    `DATA_SECURE`       LONGTEXT      COLLATE utf8mb4_bin COMMENT '「dataSecure」- 安全专用配置',
    `INTEGRATION_ID`    VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「integrationId」- 是否关联集成配置', -- 是否关联集成配置，管理时直接同步
    `NAME`              VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `OWNER`             VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「owner」- 关联主体主键',
    `OWNER_TYPE`        VARCHAR(20)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「ownerType」- 关联主体类型',
    `RUN_COMPONENT`     TEXT          COLLATE utf8mb4_bin COMMENT '「runComponent」- 执行组件',               -- 执行组件，LDAP执行专用
    `RUN_CONFIG`        LONGTEXT      COLLATE utf8mb4_bin COMMENT '「runConfig」- 执行组件额外配置',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`              VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',              -- [类型],
    `STATUS`            VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「status」- 状态',

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
    UNIQUE KEY `UK_I_PORTFOLIO_CODE_SIGMA` (`CODE`, `SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='I_PORTFOLIO';

-- 缺失公共字段：
-- - VERSION (版本)