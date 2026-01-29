DROP TABLE IF EXISTS `M_ATTRIBUTE`;
CREATE TABLE IF NOT EXISTS `M_ATTRIBUTE` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                    -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `ALIAS`             VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「alias」- 别名',             -- 属性别名（业务名）
    `COMMENTS`          TEXT          COLLATE utf8mb4_bin COMMENT '「comments」- 当前属性的描述信息',
    `EXPRESSION`        TEXT          COLLATE utf8mb4_bin COMMENT '「expression」- 表达式',
    `IN_COMPONENT`      VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「inComponent」- 写入插件',
    `IS_ARRAY`          BIT(1)        DEFAULT NULL COMMENT '「isArray」- 是否集合属性',                       -- 是否集合属性，集合属性在导入导出时可用（保留）
    `IS_CONFIRM`        BIT(1)        DEFAULT NULL COMMENT '「isConfirm」- 是否生成待确认变更',               -- 是否生成待确认变更，只有放在待确认变更中的数据需要生成待确认变更
    `IS_LOCK`           BIT(1)        DEFAULT NULL COMMENT '「isLock」- 是否锁定',                            -- 是否锁定，锁定属性不可删除
    `IS_REFER`          BIT(1)        DEFAULT NULL COMMENT '「isRefer」- 是否引用属性的主属性',               -- 是否引用属性的主属性，主属性才可拥有 sourceReference 配置，根据 source 有区别
    `IS_SYNC_IN`        BIT(1)        DEFAULT NULL COMMENT '「isSyncIn」- 是否同步读',
    `IS_SYNC_OUT`       BIT(1)        DEFAULT NULL COMMENT '「isSyncOut」- 是否同步写',
    `IS_TRACK`          BIT(1)        DEFAULT NULL COMMENT '「isTrack」- 是否实现历史记录',                   -- 是否实现历史记录，如果是 isTrack 那么启用 ACTIVITY 的变更记录，对应 ITEM
    `NAME`              VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `NORMALIZE`         TEXT          COLLATE utf8mb4_bin COMMENT '「normalize」- 表达式',
    `OUT_COMPONENT`     VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「outComponent」- 读取插件',
    `SOURCE`            VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「source」- 关联实体ID',
    `SOURCE_CONFIG`     TEXT          COLLATE utf8mb4_bin COMMENT '「sourceConfig」- 数据集配置',             -- 数据集配置（区分 Array 和 Object）
    `SOURCE_EXTERNAL`   TEXT          COLLATE utf8mb4_bin COMMENT '「sourceExternal」- 外部配置信息',         -- 外部配置信息（ type = EXTERNAL ）
    `SOURCE_FIELD`      VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sourceField」- 可选',       -- 可选，如果不设置则以name为主
    `SOURCE_REFERENCE`  TEXT          COLLATE utf8mb4_bin COMMENT '「sourceReference」- 引用配置信息',        -- 引用配置信息（ type = REFERENCE）

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`              VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',              -- [类型],
    `MODEL_ID`          VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelId」- 模型标识',

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
    UNIQUE KEY `UK_M_ATTRIBUTE_NAME_MODEL_ID` (`NAME`, `MODEL_ID`) USING BTREE,
    KEY `IDX_M_ATTRIBUTE_MODEL_ID` (`MODEL_ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='M_ATTRIBUTE';

-- 缺失公共字段：
-- - VERSION (版本)