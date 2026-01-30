DROP TABLE IF EXISTS `S_VIEW`;
CREATE TABLE IF NOT EXISTS `S_VIEW` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`           VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                         -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `CRITERIA`     TEXT          COLLATE utf8mb4_bin COMMENT '「criteria」- 该资源的行查询',                  -- 该资源的行查询（单用户处理）
    `NAME`         VARCHAR(96)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',                   -- 视图名称，每个 MATRIX 对应一个视图
    `OWNER`        VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「owner」- 用户',                  -- 用户 / 角色ID
    `OWNER_TYPE`   VARCHAR(5)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「ownerType」- ROLE 角色',         -- ROLE 角色，USER 用户
    `POSITION`     VARCHAR(96)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「position」- 当前视图的模块位置', -- 当前视图的模块位置，比页面低一个维度
    `PROJECTION`   TEXT          COLLATE utf8mb4_bin COMMENT '「projection」- 该资源的列定义',                -- 该资源的列定义（单用户处理）
    `RESOURCE_ID`  VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「resourceId」- 关联资源ID',
    `ROWS`         TEXT          COLLATE utf8mb4_bin COMMENT '「rows」- 该资源针对保存',                      -- 该资源针对保存的行进行过滤
    `TITLE`        VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「title」- 标题',                  -- 视图标题，用户输入，可选择
    `VISITANT`     BIT(1)        DEFAULT NULL COMMENT '「visitant」- 是否包含了视图访问者',

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
    UNIQUE KEY `UK_S_VIEW_OWNER_TYPE_OWNER_RESOURCE_ID_NAME_POSITION` (`OWNER_TYPE`, `OWNER`, `RESOURCE_ID`, `NAME`, `POSITION`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='S_VIEW';

-- 缺失公共字段：
-- - VERSION (版本)
-- - TYPE (类型)