DROP TABLE IF EXISTS `L_LOCATION`;
CREATE TABLE IF NOT EXISTS `L_LOCATION` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`          VARCHAR(36)   NOT NULL COLLATE utf8mb4_bin COMMENT '「id」- 主键',                          -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `ADDRESS`     TEXT          DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「address」- 详细地址',
    `CITY`        VARCHAR(32)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「city」- 3.城市',
    `CODE`        VARCHAR(36)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「code」- 编号',
    `COUNTRY`     VARCHAR(32)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「country」- 1.国家',
    `FULL_NAME`   VARCHAR(255)  NOT NULL COLLATE utf8mb4_bin COMMENT '「fullName」- 地址全称',
    `NAME`        VARCHAR(32)   NOT NULL COLLATE utf8mb4_bin COMMENT '「name」- 名称',
    `POSTAL`      VARCHAR(16)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「postal」- 邮政编码',
    `REGION`      VARCHAR(32)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「region」- 4.区域',
    `REGION_ID`   VARCHAR(36)   NOT NULL COLLATE utf8mb4_bin COMMENT '「regionId」- 区域ID',
    `STATE`       VARCHAR(32)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「state」- 2.省会',
    `STREET1`     VARCHAR(72)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「street1」- 街道1',
    `STREET2`     VARCHAR(72)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「street2」- 街道2',
    `STREET3`     VARCHAR(72)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「street3」- 街道3',

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
    UNIQUE KEY `UK_L_LOCATION_CODE_SIGMA` (`CODE`, `SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='L_LOCATION';

-- 缺失公共字段：
-- - VERSION (版本)
-- - TYPE (类型)