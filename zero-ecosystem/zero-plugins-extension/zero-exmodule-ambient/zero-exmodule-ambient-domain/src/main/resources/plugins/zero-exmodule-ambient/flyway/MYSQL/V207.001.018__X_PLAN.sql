DROP TABLE IF EXISTS `X_PLAN`;
CREATE TABLE IF NOT EXISTS `X_PLAN` (
    -- ==================================================================================================
    -- 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`             VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                       -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `CODE`           VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 套餐编号',
    `NAME`           VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 套餐名称',
    `BILLING_CYCLE`  VARCHAR(20)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「billingCycle」- 计费周期',     -- MONTH-月付 / YEAR-年付
    `PRICE`          DECIMAL(18,4) DEFAULT NULL COMMENT '「price」- 价格',
    `CURRENCY`       VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「currency」- 货币',           -- 如 CNY, USD
    `DESCRIPTION`    TEXT          COLLATE utf8mb4_bin COMMENT '「description」- 套餐说明',

    -- ==================================================================================================
    -- 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`           VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',
    `STATUS`         VARCHAR(32)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「status」- 状态',             -- 上下架：ON_SALE / OFF_SALE

    -- ==================================================================================================
    -- 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`          VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',            -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`      VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',             -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',                -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`         BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                               -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`       VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',         -- [国际化] 如: zh_CN, en_US,
    `METADATA`       TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                        -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`        VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`     DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',                            -- [审计] 创建时间
    `CREATED_BY`     VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',          -- [审计] 创建人
    `UPDATED_AT`     DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                            -- [审计] 更新时间
    `UPDATED_BY`     VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',          -- [审计] 更新人

    -- ==================================================================================================
    -- 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_X_PLAN_CODE` (`CODE`) USING BTREE,
    KEY `IDX_X_PLAN_STATUS` (`STATUS`) USING BTREE,
    KEY `IDX_X_PLAN_SIGMA` (`SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='X_PLAN';