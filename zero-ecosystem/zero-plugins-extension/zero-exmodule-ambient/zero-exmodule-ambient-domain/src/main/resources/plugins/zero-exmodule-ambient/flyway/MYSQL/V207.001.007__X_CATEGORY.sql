DROP TABLE IF EXISTS `X_CATEGORY`;
CREATE TABLE IF NOT EXISTS `X_CATEGORY` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`              VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                      -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `CODE`            VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 编号',
    `COMMENT`         TEXT          COLLATE utf8mb4_bin COMMENT '「comment」- 备注',
    `ICON`            VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「icon」- 图标',
    `IDENTIFIER`      VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「identifier」- 关联标识',     -- 当前类型描述的Model的标识
    `LEAF`            BIT(1)        DEFAULT NULL COMMENT '「leaf」- 是否叶节点',
    `NAME`            VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `PARENT_ID`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「parentId」- 父节点ID',
    `RUN_COMPONENT`   TEXT          COLLATE utf8mb4_bin COMMENT '「runComponent」- 执行组件',
    `RUN_CONFIG`      LONGTEXT      COLLATE utf8mb4_bin COMMENT '「runConfig」- 执行配置',
    `SORT`            INTEGER       DEFAULT NULL COMMENT '「sort」- 排序',
    `TREE_COMPONENT`  TEXT          COLLATE utf8mb4_bin COMMENT '「treeComponent」- 目录组件',                -- 目录组件，构造树专用
    `TREE_CONFIG`     LONGTEXT      COLLATE utf8mb4_bin COMMENT '「treeConfig」- 目录配置',                   -- 目录组件运行配置，特殊场景专用

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`            VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',                -- [类型],

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`           VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',           -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',            -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`          VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',               -- [逻辑隔离] 区分同一租户下的不同应用,
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
    UNIQUE KEY `UK_X_CATEGORY_APP_ID_TYPE_CODE` (`APP_ID`, `TYPE`, `CODE`) USING BTREE,
    UNIQUE KEY `UK_X_CATEGORY_SIGMA_TYPE_CODE` (`SIGMA`, `TYPE`, `CODE`) USING BTREE,
    KEY `IDXM_X_CATEGORY_APP_ID_TYPE_ACTIVE` (`APP_ID`, `TYPE`, `ACTIVE`) USING BTREE,
    KEY `IDXM_X_CATEGORY_SIGMA_TYPE_ACTIVE` (`SIGMA`, `TYPE`, `ACTIVE`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='X_CATEGORY';

-- 缺失公共字段：
-- - VERSION (版本)