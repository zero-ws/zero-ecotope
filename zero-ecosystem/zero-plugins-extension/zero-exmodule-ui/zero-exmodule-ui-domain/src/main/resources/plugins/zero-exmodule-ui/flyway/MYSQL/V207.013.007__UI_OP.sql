DROP TABLE IF EXISTS `UI_OP`;
CREATE TABLE IF NOT EXISTS `UI_OP` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`            VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                        -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `ACTION`        VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「action」- S_ACTION',            -- S_ACTION中的code（权限检查专用）
    `CLIENT_ID`     VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「clientId」- 没有特殊情况',      -- 没有特殊情况，clientId = clientKey
    `CLIENT_KEY`    VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「clientKey」- 一般',             -- 一般是Html中对应的key信息，如 $opSave
    `CONFIG`        TEXT          COLLATE utf8mb4_bin COMMENT '「config」- 该按钮操作对应',                   -- 该按钮操作对应的配置数据信息, icon, type
    `CONTROL_ID`    VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「controlId」- 挂载专用的ID',
    `CONTROL_TYPE`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「controlType」- 操作关联的控件类型',
    `EVENT`         VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「event」- 操作中的',             -- 操作中的 event 事件名称
    `PLUGIN`        TEXT          COLLATE utf8mb4_bin COMMENT '「plugin」- 该按钮中的插件',                   -- 该按钮中的插件，如 tooltip，component等
    `TEXT`          VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「text」- 该操作上的文字信息',
    `UI_SORT`       INT           DEFAULT NULL COMMENT '「uiSort」- 按钮',                                    -- 按钮在管理过程中的排序

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`         VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',             -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`     VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',              -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`        VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',                 -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`        BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                                -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`      VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',          -- [国际化] 如: zh_CN, en_US,
    `METADATA`      TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                         -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`       VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`    DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',                             -- [审计] 创建时间
    `CREATED_BY`    VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',           -- [审计] 创建人
    `UPDATED_AT`    DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                             -- [审计] 更新时间
    `UPDATED_BY`    VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',           -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_UI_OP_CONTROL_ID_SIGMA_CLIENT_KEY` (`CONTROL_ID`, `SIGMA`, `CLIENT_KEY`) USING BTREE,
    UNIQUE KEY `UK_UI_OP_CONTROL_ID_SIGMA_ACTION` (`CONTROL_ID`, `SIGMA`, `ACTION`) USING BTREE,
    KEY `IDXM_UI_OP_SIGMA_CONTROL_ID` (`SIGMA`, `CONTROL_ID`) USING BTREE,
    KEY `IDXM_UI_OP_SIGMA_CONTROL_TYPE` (`SIGMA`, `CONTROL_TYPE`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='UI_OP';

-- 缺失公共字段：
-- - VERSION (版本)
-- - TYPE (类型)