DROP TABLE IF EXISTS `UI_FIELD`;
CREATE TABLE IF NOT EXISTS `UI_FIELD` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`             VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                       -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `CONTAINER`      VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「container」- 容器字段专用容器信息', -- 容器字段专用容器信息，映射到 name 中
    `CONTROL_ID`     VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「controlId」- 关联的表单ID',
    `HIDDEN`         BIT(1)        DEFAULT NULL COMMENT '「hidden」- button专用',
    `LABEL`          VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「label」- 字段标签',
    `NAME`           VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `OPTION_CONFIG`  TEXT          COLLATE utf8mb4_bin COMMENT '「optionConfig」- 字段专用配置',
    `OPTION_ITEM`    TEXT          COLLATE utf8mb4_bin COMMENT '「optionItem」- 字段专用配置',
    `OPTION_JSX`     TEXT          COLLATE utf8mb4_bin COMMENT '「optionJsx」- 字段专用配置',
    `RENDER`         VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「render」- 使用',               -- 使用的Render函数
    `ROW_TYPE`       VARCHAR(20)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「rowType」- 行类型',
    `RULES`          TEXT          COLLATE utf8mb4_bin COMMENT '「rules」- optionCo',                         -- optionConfig.rules 验证专用的配置，描述规则
    `SPAN`           INTEGER       DEFAULT NULL COMMENT '「span」- 字段宽度',
    `X_POINT`        INTEGER       DEFAULT NULL COMMENT '「xPoint] - 字段的X坐标（列）',
    `Y_POINT`        INTEGER       DEFAULT NULL COMMENT '「yPoint」- 字段的Y坐标',                            -- 字段的Y坐标（行）

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`          VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',            -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`      VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',             -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',                -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`         BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                               -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`       VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',         -- [国际化] 如: zh_CN, en_US,
    `METADATA`       TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                        -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`        VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`     DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',                            -- [审计] 创建时间
    `CREATED_BY`     VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',          -- [审计] 创建人
    `UPDATED_AT`     DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                            -- [审计] 更新时间
    `UPDATED_BY`     VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',          -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_UI_FIELD_CONTROL_ID_NAME` (`CONTROL_ID`, `NAME`) USING BTREE,
    KEY `IDX_UI_FIELD_CONTROL_ID` (`CONTROL_ID`) USING BTREE,
    KEY `IDXM_UI_FIELD_CONTROL_ID_X_Y` (`CONTROL_ID`, `X_POINT`, `Y_POINT`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='UI_FIELD';

-- 缺失公共字段：
-- - VERSION (版本)
-- - TYPE (类型)