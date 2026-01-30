/*
 * ==================================================================================
 * Zero Framework - Change History / Audit Log
 * ==================================================================================
 *
 * [1. 变更记录核心 (Record Specifics)]
 * - serial        : 记录单号 (Primary Key / Unique Log ID)
 * - description   : 操作描述信息 (Human readable description)
 * - taskName      : 任务名称 (Associated Task Name)
 * - taskSerial    : 任务单号 (Associated Task Serial/ID)
 * - createdAt     : 操作时间 (Timestamp)
 * - createdBy     : 操作人 (User ID or Name)
 *
 * [2. 应用级作用域 (Application Scope)]
 * - sigma         : 统一标识符 (Multi-Tenancy / Sharding Key)
 *
 * [3. 模型级关联 (Model Polymorphism)]
 * - type          : 记录类型 (Log Type, e.g., INFO, ERROR, WARN)
 * - modelId       : 模型标识 (Target Model Identifier, e.g., 'hr.employee')
 * - modelKey      : 单据主键 (Target Record Key, e.g., 'EMP-001')
 * - modelCategory : 业务类别 (Business Category, Optional)
 *
 * ==================================================================================
 */
DROP TABLE IF EXISTS `X_ACTIVITY`;
CREATE TABLE IF NOT EXISTS `X_ACTIVITY` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`              VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                      -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `DESCRIPTION`     TEXT          COLLATE utf8mb4_bin COMMENT '「description」- 描述',
    `RECORD_NEW`      LONGTEXT      COLLATE utf8mb4_bin COMMENT '「recordNew」- 变更后数据',                  -- 变更之后的数据（用于更新）
    `RECORD_OLD`      LONGTEXT      COLLATE utf8mb4_bin COMMENT '「recordOld」- 变更前数据',                  -- 变更之前的数据（用于回滚）
    `SERIAL`          VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「serial」- 记录单号',
    `TASK_NAME`       VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「taskName」- 任务名称',
    `TASK_SERIAL`     VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「taskSerial」- 任务单号',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
	`TYPE`            VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',                   -- [类型],
    `MODEL_ID`        VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelId」- 模型标识',         -- 组所关联的模型identifier，用于描述
    `MODEL_KEY`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelKey」- 模型记录ID',        -- 组所关联的模型记录ID，用于描述哪一个Model中的记录
    `MODEL_CATEGORY`  VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelCategory」- 模型类别',   -- 关联的category记录，只包含叶节点

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`           VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',           -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',            -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`          VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',               -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`          BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                              -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`        VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',        -- [国际化] 如: zh_CN, en_US,
    `METADATA`        TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                       -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`         VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`      DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',                           -- [审计] 创建时间
    `CREATED_BY`      VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',         -- [审计] 创建人
    `UPDATED_AT`      DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                           -- [审计] 更新时间
    `UPDATED_BY`      VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',         -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    KEY `IDXM_X_ACTIVITY_MODEL_ID_MODEL_KEY` (`MODEL_ID`, `MODEL_KEY`, `ACTIVE`) USING BTREE,
    KEY `IDXM_X_ACTIVITY_SIGMA_ACTIVE` (`SIGMA`, `ACTIVE`) USING BTREE,
    KEY `IDX_X_ACTIVITY_CREATED_AT` (`CREATED_AT`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='X_ACTIVITY';

-- 缺失公共字段：
-- - VERSION (版本)