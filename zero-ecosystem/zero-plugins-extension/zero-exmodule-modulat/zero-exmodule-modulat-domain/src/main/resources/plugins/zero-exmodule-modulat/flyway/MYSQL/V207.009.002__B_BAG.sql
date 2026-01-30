DROP TABLE IF EXISTS `B_BAG`;
CREATE TABLE IF NOT EXISTS `B_BAG` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`          VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                          -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `ENTRY`       BIT(1)        DEFAULT NULL COMMENT '「entry」- ',                                           -- 是否入口（带入口为应用，当前APP_ID下安装内容）
    `ENTRY_ID`    VARCHAR(36)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「entryId」- 入口专用ID',           -- 入口专用ID，关联 X_MENU 中的ID，其余的直接使用链接
    `NAME`        VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `NAME_ABBR`   VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「nameAbbr」- 模块缩写',
    `NAME_FULL`   VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「nameFull」- 模块全名',
    `PARENT_ID`   VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「parentId」- 父节点',
    `UI_CONFIG`   LONGTEXT      COLLATE utf8mb4_bin COMMENT '「uiConfig」- 模块核心配置',
    `UI_ICON`     VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「uiIcon」- 模块图标',
    `UI_SORT`     BIGINT        DEFAULT NULL COMMENT '「uiSort」- 模块排序',
    `UI_STYLE`    TEXT          COLLATE utf8mb4_bin COMMENT '「uiStyle」- 模块风格',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`        VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',                    -- [类型],

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`       VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',               -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`   VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',                -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`      VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',                   -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`      BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                                  -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`    VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',            -- [国际化] 如: zh_CN, en_US,
    `METADATA`    TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                           -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`     VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`  DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',                               -- [审计] 创建时间
    `CREATED_BY`  VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',             -- [审计] 创建人
    `UPDATED_AT`  DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                               -- [审计] 更新时间
    `UPDATED_BY`  VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',             -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_B_BAG_NAME_APP_ID` (`NAME`, `APP_ID`) USING BTREE,
    UNIQUE KEY `UK_B_BAG_NAME_ABBR_APP_ID` (`NAME_ABBR`, `APP_ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='B_BAG';

-- 缺失公共字段：
-- - VERSION (版本)