DROP TABLE IF EXISTS `UI_LIST`;
CREATE TABLE IF NOT EXISTS `UI_LIST` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`              VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                      -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `CLASS_COMBINER`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「classCombiner」- 组装器',
    `CODE`            VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 编号',
    `DYNAMIC_COLUMN`  BIT(1)        DEFAULT NULL COMMENT '「dynamicColumn」- 动态列？',
    `DYNAMIC_SWITCH`  BIT(1)        DEFAULT NULL COMMENT '「dynamicSwitch」- 动态切换？',
    `IDENTIFIER`      VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「identifier」- 表单所属的模型ID',
    `NAME`            VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `OPTIONS`         TEXT          COLLATE utf8mb4_bin COMMENT '「options」- 配置项',
    `OPTIONS_AJAX`    TEXT          COLLATE utf8mb4_bin COMMENT '「optionsAjax」- 所有',                      -- 所有 ajax系列的配置
    `OPTIONS_SUBMIT`  TEXT          COLLATE utf8mb4_bin COMMENT '「optionsSubmit」- 所有提交类的配置',
    `V_QUERY`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「vQuery」- 连接query到',       -- 连接query到 grid -> query 节点
    `V_SEARCH`        VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「vSearch」- 连接search到',     -- 连接search到 grid -> options 节点
    `V_SEGMENT`       TEXT          COLLATE utf8mb4_bin COMMENT '「vSegment」- Json结构',                     -- Json结构，对应到 grid -> component 节点
    `V_TABLE`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「vTable」- 连接table到',       -- 连接table到 grid -> table 节点

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
    UNIQUE KEY `UK_UI_LIST_CODE_SIGMA` (`CODE`, `SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='UI_LIST';

-- 缺失公共字段：
-- - VERSION (版本)
-- - TYPE (类型)