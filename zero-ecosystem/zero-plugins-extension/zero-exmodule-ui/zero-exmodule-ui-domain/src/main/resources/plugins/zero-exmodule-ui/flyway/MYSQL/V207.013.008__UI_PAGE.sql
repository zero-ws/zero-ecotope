DROP TABLE IF EXISTS `UI_PAGE`;
CREATE TABLE IF NOT EXISTS `UI_PAGE` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                    -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `APP`               VARCHAR(32)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「app」- 入口APP名称',        -- 入口APP名称，APP中的path
    `ASSIST`            TEXT          COLLATE utf8mb4_bin COMMENT '「assist」- 当前页面',                     -- 当前页面的辅助数据Ajax配置
    `CONTAINER_CONFIG`  TEXT          COLLATE utf8mb4_bin COMMENT '「containerConfig」- 当前页面容器相关配置',
    `CONTAINER_NAME`    VARCHAR(32)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「containerName」- 当前页面是否存在容器', -- 当前页面是否存在容器，如果有容器，那么设置容器名称
    `GRID`              TEXT          COLLATE utf8mb4_bin COMMENT '「grid」- 当前页面的布局信息',             -- 当前页面的布局信息，Grid布局格式
    `LAYOUT_ID`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「layoutId」- 使用的模板ID',  -- 使用的模板ID，最终生成 layout 顶层节点数据
    `MODULE`            VARCHAR(32)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「module」- 模块相关信息',
    `PAGE`              VARCHAR(32)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「page」- 页面路径信息',
    `PARAM_MAP`         TEXT          COLLATE utf8mb4_bin COMMENT '「paramMap」- URL地址中',                  -- URL地址中的配置key=findRunning
    `SECURE`            BIT(1)        DEFAULT NULL COMMENT '「secure」- 是否执行安全检查',                    -- 是否执行安全检查（安全检查才会被权限系统捕捉）
    `STATE`             TEXT          COLLATE utf8mb4_bin COMMENT '「state」- 当前页面',                      -- 当前页面的初始化状态信息

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
    UNIQUE KEY `UK_UI_PAGE_APP_MODULE_PAGE_SIGMA` (`APP`, `MODULE`, `PAGE`, `SIGMA`) USING BTREE,
    KEY `IDXM_UI_PAGE_APP_MODULE_PAGE_LANGUAGE_SIGMA` (`APP`, `MODULE`, `PAGE`, `LANGUAGE`, `SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='UI_PAGE';

-- 缺失公共字段：
-- - VERSION (版本)
-- - TYPE (类型)