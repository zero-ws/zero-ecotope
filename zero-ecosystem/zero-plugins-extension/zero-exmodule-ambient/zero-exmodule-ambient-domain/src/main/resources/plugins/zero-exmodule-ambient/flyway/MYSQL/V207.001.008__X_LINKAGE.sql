DROP TABLE IF EXISTS `X_LINKAGE`;
CREATE TABLE IF NOT EXISTS `X_LINKAGE` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`           VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                         -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `ALIAS`        VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「alias」- 别名',
    `LINK_DATA`    LONGTEXT      COLLATE utf8mb4_bin COMMENT '「linkData」- 关系数据',
    `LINK_KEY`     VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「linkKey」- 双向Key',         -- 双向Key计算，根据 source / ofMain 计算
    `LINK_TYPE`    VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「linkType」- 关系类型',
    `NAME`         VARCHAR(255)  NOT NULL COLLATE utf8mb4_bin COMMENT '「name」- 名称',
    `REGION`       VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「region」- 关系区域',         -- 连接区域标识，同一个区域算一个连接（批次）
    `SOURCE_DATA`  LONGTEXT      COLLATE utf8mb4_bin COMMENT '「sourceData」- 源内容',
    `SOURCE_KEY`   VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sourceKey」- 源实体ID',
    `SOURCE_TYPE`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sourceType」- 源类型',
    `TARGET_DATA`  LONGTEXT      COLLATE utf8mb4_bin COMMENT '「targetData」- 目标内容',
    `TARGET_KEY`   VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「targetKey」- 目标实体ID',
    `TARGET_TYPE`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「targetType」- 目标类型',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`         VARCHAR(64)   NOT NULL COLLATE utf8mb4_bin COMMENT '「type」- 类型',                       -- [类型],

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
    UNIQUE KEY `UK_X_LINKAGE_LINK_KEY` (`LINK_KEY`) USING BTREE,
    UNIQUE KEY `UK_X_LINKAGE_REGION_NAME` (`REGION`, `NAME`) USING BTREE,
    KEY `IDX_X_LINKAGE_SIGMA` (`SIGMA`, `TYPE`) USING BTREE,
    KEY `IDX_X_LINKAGE_REGION` (`REGION`) USING BTREE,
    KEY `IDX_X_LINKAGE_R_SOURCE_TYPE` (`SOURCE_TYPE`) USING BTREE,
    KEY `IDX_X_LINKAGE_R_TARGET_TYPE` (`TARGET_TYPE`) USING BTREE,
    KEY `IDX_X_LINKAGE_R_SOURCE_KEY` (`SOURCE_KEY`) USING BTREE,
    KEY `IDX_X_LINKAGE_R_TARGET_KEY` (`TARGET_KEY`) USING BTREE

) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='X_LINKAGE';

-- 缺失公共字段：
-- - VERSION (版本)