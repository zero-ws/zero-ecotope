DROP TABLE IF EXISTS `UI_VIEW`;
CREATE TABLE IF NOT EXISTS `UI_VIEW` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`            VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                        -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `CODE`          VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 编号',
    `CRITERIA`      TEXT          COLLATE utf8mb4_bin COMMENT '「criteria」- 该资源的行查询',
    `IDENTIFIER`    VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「identifier」- 模型标识符',
    `NAME`          VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',                  -- 视图名称，每个 MATRIX 对应一个视图
    `POSITION`      VARCHAR(96)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「position」- 当前视图的模块位置', -- 当前视图的模块位置，比页面低一个维度
    `PROJECTION`    TEXT          COLLATE utf8mb4_bin COMMENT '「projection」- 该资源的列定义',
    `QR_COMPONENT`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「qrComponent」- 查询条件专用组件',
    `QR_CONFIG`     TEXT          COLLATE utf8mb4_bin COMMENT '「qrConfig」- 查询组件专用配置',
    `ROWS`          TEXT          COLLATE utf8mb4_bin COMMENT '「rows」- 该资源针对保存',                     -- 该资源针对保存的行进行过滤
    `SORT`          INT           DEFAULT NULL COMMENT '「sort」- 排序',
    `TITLE`         VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「title」- 标题',                 -- 视图标题，用户输入，可选择
    `UI_CONFIG`     LONGTEXT      COLLATE utf8mb4_bin COMMENT '「uiConfig」- 界面配置',
    `VIEW`          VARCHAR(96)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「view」- 视图名',
    `WORKFLOW`      VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「workflow」- 工作流名称',

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
    UNIQUE KEY `UK_UI_VIEW_SIGMA_CODE_NAME` (`SIGMA`, `CODE`, `NAME`) USING BTREE,
    UNIQUE KEY `UK_UI_VIEW_SIGMA_CODE_VIEW_POSITION` (`SIGMA`, `CODE`, `VIEW`, `POSITION`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='UI_VIEW';

-- 缺失公共字段：
-- - VERSION (版本)
-- - TYPE (类型)