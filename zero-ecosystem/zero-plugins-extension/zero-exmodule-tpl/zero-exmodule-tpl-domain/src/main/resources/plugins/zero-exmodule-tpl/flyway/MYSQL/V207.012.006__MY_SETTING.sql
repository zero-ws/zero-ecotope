DROP TABLE IF EXISTS `MY_SETTING`;
CREATE TABLE IF NOT EXISTS `MY_SETTING` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`               VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                     -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `MY_BAG`           VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「myBag」- 对应 MY_BA',        -- 对应 MY_BAG 设置，每个BAG有对应设置信息
    `OWNER`            VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「owner」- 拥有者ID',          -- 拥有者ID，我的 / 角色级
    `OWNER_TYPE`       VARCHAR(5)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「ownerType」- ROLE 角色',     -- ROLE 角色，USER 用户
    `P_COLOR_PRIMARY`  VARCHAR(20)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「pColorPrimary」- colorPri',  -- colorPrimary，主色调
    `P_COLOR_WEAK`     BIT(1)        DEFAULT NULL COMMENT '「pColorWeak」- 色彩控制',
    `P_CONTENT_WIDTH`  VARCHAR(12)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「pContentWidth」- 两种',
    `P_FIXED_HEADER`   BIT(1)        DEFAULT NULL COMMENT '「pFixedHeader」- 标题控制',
    `P_FIX_SIDER_BAR`  BIT(1)        DEFAULT NULL COMMENT '「pFixSiderBar」- 侧边栏控制',
    `P_LAYOUT`         VARCHAR(12)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「pLayout」- 布局类型：top',   -- 布局类型：top, menu, mix
    `P_NAV_THEME`      VARCHAR(32)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「pNavTheme」- navTheme',      -- navTheme, 风格处理，对应 light / realdark
    `P_PWA`            BIT(1)        DEFAULT NULL COMMENT '「pPwa」- pwa属性',                                -- pwa属性，暂时未知
    `P_TOKEN`          LONGTEXT      COLLATE utf8mb4_bin COMMENT '「pToken」- 保留',                          -- 保留（后续可能会使用）

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`             VARCHAR(32)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',               -- [类型],

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`            VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',          -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`        VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',           -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`           VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',              -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`           BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                             -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`         VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',       -- [国际化] 如: zh_CN, en_US,
    `METADATA`         TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                      -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`          VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`       DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',                          -- [审计] 创建时间
    `CREATED_BY`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',        -- [审计] 创建人
    `UPDATED_AT`       DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                          -- [审计] 更新时间
    `UPDATED_BY`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',        -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_MY_SETTING_TYPE_MY_BAG_OWNER_OWNER_TYPE` (`TYPE`, `MY_BAG`, `OWNER`, `OWNER_TYPE`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='MY_SETTING';

-- 缺失公共字段：
-- - VERSION (版本)