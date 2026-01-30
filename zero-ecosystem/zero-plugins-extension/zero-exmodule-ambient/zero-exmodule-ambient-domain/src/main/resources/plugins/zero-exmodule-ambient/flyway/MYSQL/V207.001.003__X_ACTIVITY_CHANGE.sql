/*
 * ==================================================================================
 * Zero Framework - Activity Change Record / Field Change History
 * ==================================================================================
 *
 * [1. 变更记录详情 (Change Record Details)]
 * - activityId    : 关联的操作记录ID (Parent Activity Reference)
 * - fieldName     : 变更的字段名 (Field that changed)
 * - fieldAlias    : 字段对应的别名 (Field display alias)
 * - fieldType     : 变更字段的数据类型 (Data type from model definition)
 * - valueOld      : 变更前的旧值 (Original value before change)
 * - valueNew      : 变更后的新值 (New value after change)
 *
 * [2. 变更类型与状态 (Change Type & Status)]
 * - type          : 字段变更类型 (ADD | DELETE | UPDATE)
 * - status        : 待确认变更状态 (CONFIRMED | PENDING | CANCEL )
 *
 * ==================================================================================
 */
DROP TABLE IF EXISTS `X_ACTIVITY_CHANGE`;
CREATE TABLE IF NOT EXISTS `X_ACTIVITY_CHANGE` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`           VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                         -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `ACTIVITY_ID`  VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「activityId」- 操作记录ID',
    `FIELD_ALIAS`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「fieldAlias」- 字段别名',           -- 字段别名是呈现在界面上的名称
    `FIELD_NAME`   VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「fieldName」- 字段名',              -- 字段名对应变更Java类属性名
    `FIELD_TYPE`   VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「fieldType」- 字段类型',            -- 变更字段的数据类型，直接从模型定义中读取
    `VALUE_NEW`    LONGTEXT      COLLATE utf8mb4_bin COMMENT '「valueNew」- 新值',                             
    `VALUE_OLD`    LONGTEXT      COLLATE utf8mb4_bin COMMENT '「valueOld」- 旧值',                             -- valueOld 转换到 valueNew 作为变更依据

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`         VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 变更类型',                   -- 变更类型：ADD | UPDATE | DELETE
    `STATUS`       VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「status」- 确认状态',                 -- 待确认变更状态：CONFIRMED | PENDING | CANCEL

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`        VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',              -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`    VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',               -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',                  -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`       BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                                 -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`     VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',           -- [国际化] 如: zh_CN, en_US,
    `METADATA`     TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                          -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`      VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`   DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',                              -- [审计] 创建时间
    `CREATED_BY`   VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',            -- [审计] 创建人
    `UPDATED_AT`   DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                              -- [审计] 更新时间
    `UPDATED_BY`   VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',            -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    KEY `IDX_X_ACTIVITY_CHANGE_ACTIVITY_ID` (`ACTIVITY_ID`) USING BTREE,
    KEY `IDX_X_ACTIVITY_CHANGE_CREATED_AT` (`CREATED_AT`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='X_ACTIVITY_CHANGE';

-- 缺失公共字段：
-- - VERSION (版本)