DROP TABLE IF EXISTS `X_SUBSCRIPTION`;
CREATE TABLE IF NOT EXISTS `X_SUBSCRIPTION` (
    -- ==================================================================================================
    -- 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`               VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                       -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `PLAN_ID`          VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「planId」- 套餐ID',            -- 关联 X_PLAN，订阅所属租户见 TENANT_ID
    `START_AT`         DATETIME      DEFAULT NULL COMMENT '「startAt」- 周期开始',                             -- 当前计费周期开始时间
    `END_AT`           DATETIME      DEFAULT NULL COMMENT '「endAt」- 周期结束',                              -- 当前计费周期结束时间
    `RENEW_TYPE`       VARCHAR(20)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「renewType」- 续费类型',       -- MONTH-月付 / YEAR-年付
    `AUTO_RENEW`       BIT(1)        DEFAULT NULL COMMENT '「autoRenew」- 自动续费',                          -- 1=自动续费, 0=手动

    -- ==================================================================================================
    -- 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`             VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',
    `STATUS`           VARCHAR(32)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「status」- 状态',             -- 正常-ACTIVE / 过期-EXPIRED / 停服-SUSPENDED

    -- ==================================================================================================
    -- 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`            VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',            -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`        VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',             -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`           VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',                -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`           BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                               -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`         VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',         -- [国际化] 如: zh_CN, en_US,
    `METADATA`         TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                        -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`          VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`       DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',                            -- [审计] 创建时间
    `CREATED_BY`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',          -- [审计] 创建人
    `UPDATED_AT`       DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                            -- [审计] 更新时间
    `UPDATED_BY`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',          -- [审计] 更新人

    -- ==================================================================================================
    -- 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    KEY `IDX_X_SUBSCRIPTION_TENANT_ID` (`TENANT_ID`) USING BTREE,
    KEY `IDX_X_SUBSCRIPTION_PLAN_ID` (`PLAN_ID`) USING BTREE,
    KEY `IDX_X_SUBSCRIPTION_STATUS` (`STATUS`) USING BTREE,
    KEY `IDX_X_SUBSCRIPTION_END_AT` (`END_AT`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='X_SUBSCRIPTION';