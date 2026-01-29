DROP TABLE IF EXISTS `TPL_TICKET`;
CREATE TABLE IF NOT EXISTS `TPL_TICKET` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                VARCHAR(36)   NOT NULL COLLATE utf8mb4_bin COMMENT '「id」- 主键',                    -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `CODE`              VARCHAR(50)   NOT NULL COLLATE utf8mb4_bin COMMENT '「code」- 编号',
    `DESCRIPTION`       VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「description」- 描述',
    `NAME`              VARCHAR(50)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「name」- 名称',
    `RECORD_COMPONENT`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「recordComponent」- 处理记录的组件',
    `RECORD_JSON`       LONGTEXT      COLLATE utf8mb4_bin COMMENT '「recordJson」- 上一次的记录内容',         -- 上一次的记录内容（Json格式）
    `SYSTEM`            BIT(1)        DEFAULT NULL COMMENT '「system」- 是否属于系统模板',
    `UI_COMPONENT`      VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「uiComponent」- 处理UI的组件',
    `UI_CONFIG`         LONGTEXT      COLLATE utf8mb4_bin COMMENT '「uiConfig」- UI的配置',                   -- UI的配置（Json格式）

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`              VARCHAR(36)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「type」- 类型',              -- [类型],
    `STATUS`            VARCHAR(36)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「status」- 状态',
    `MODEL_ID`          VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「modelId」- 模型标识',       -- 关联的模型identifier，用于描述
    `MODEL_KEY`         VARCHAR(36)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「modelKey」- 模型记录ID',      -- 关联的模型记录ID，用于描述哪一个Model中的记录
    `MODEL_CATEGORY`    VARCHAR(36)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「modelCategory」- 模型类别',

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
    UNIQUE KEY `UK_TPL_TICKET_CODE_SIGMA` (`CODE`, `SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='TPL_TICKET';

-- 缺失公共字段：
-- - VERSION (版本)