DROP TABLE IF EXISTS `S_VISITANT`;
CREATE TABLE IF NOT EXISTS `S_VISITANT` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`           VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                         -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `ACL_VARIETY`  TEXT          COLLATE utf8mb4_bin COMMENT '「aclVariety」- 多样性的属性集',                -- 多样性的属性集，用于控制集合类型的属性
    `ACL_VERGE`    TEXT          COLLATE utf8mb4_bin COMMENT '「aclVerge」- 依赖属性集',
    `ACL_VIEW`     TEXT          COLLATE utf8mb4_bin COMMENT '「aclView」- 只读的属性集',
    `ACL_VISIBLE`  TEXT          COLLATE utf8mb4_bin COMMENT '「aclVisible」- 可见的属性集',
    `ACL_VOW`      TEXT          COLLATE utf8mb4_bin COMMENT '「aclVow」- 引用类属性集',
    `DM_COLUMN`    TEXT          COLLATE utf8mb4_bin COMMENT '「dmColumn」对应视图中的 Projection',
    `DM_QR`        TEXT          COLLATE utf8mb4_bin COMMENT '「dmQr」对应视图中的 Criteria',
    `DM_ROW`       TEXT          COLLATE utf8mb4_bin COMMENT '「dmRow」对应视图中 Rows',
    `IDENTIFIER`   VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「identifier」- 动态类型中的模型ID',
    `MODE`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「mode」- 模式',                   -- 模式，资源访问者继承于资源，可`替换/扩展`两种模式
    `PHASE`        VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「phase」- 作用周期',
    `SEEK_KEY`     VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「seekKey」- 资源检索的唯一键',
    `VIEW_ID`      VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「viewId」- 视图访问者的读ID',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`         VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',                   -- [类型],

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
    UNIQUE KEY `UK_S_VISITANT_VIEW_ID_TYPE_SEEK_KEY` (`VIEW_ID`, `TYPE`, `SEEK_KEY`) USING BTREE,
    KEY `IDXM_S_VISITANT_VIEW_ID_TYPE_CONFIG` (`VIEW_ID`, `TYPE`, `SEEK_KEY`) USING BTREE,
    KEY `IDXM_S_VISITANT_VIEW_ID_TYPE_IDENTIFIER` (`VIEW_ID`, `TYPE`, `IDENTIFIER`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='S_VISITANT';

-- 缺失公共字段：
-- - VERSION (版本)