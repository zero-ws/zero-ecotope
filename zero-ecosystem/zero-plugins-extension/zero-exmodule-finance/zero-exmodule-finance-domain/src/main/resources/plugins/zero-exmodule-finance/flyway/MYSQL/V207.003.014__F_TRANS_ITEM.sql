DROP TABLE IF EXISTS `F_TRANS_ITEM`;
CREATE TABLE IF NOT EXISTS `F_TRANS_ITEM` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`              VARCHAR(36)     COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                    -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `AMOUNT`          DECIMAL(18, 2)  NOT NULL COMMENT '「amount」——价税合计，所有明细对应的实际结算金额',
    `CODE`            VARCHAR(255)    NOT NULL COLLATE utf8mb4_bin COMMENT '「code」- 编号',
    `END_AT`          DATETIME        DEFAULT NULL COMMENT '「endAt」 - 结束时间',
    `GROUP_BY`        VARCHAR(64)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「groupBy」 - 分组',
    `NAME`            VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `PAY_ID`          VARCHAR(255)    DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「payId」付款账号',
    `PAY_METHOD`      VARCHAR(255)    DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「payMethod」付款方式',
    `PAY_MOBILE`      VARCHAR(128)    DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「payMobile」打款人电话',
    `PAY_NAME`        VARCHAR(128)    DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「payName」打款人姓名',
    `SERIAL`          VARCHAR(36)     NOT NULL COLLATE utf8mb4_bin COMMENT '「serial」- 单号',
    `START_AT`        DATETIME        DEFAULT NULL COMMENT '「startAt」 - 开始时间',
    `TRANSACTION_ID`  VARCHAR(36)     DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「transactionId」交易ID',

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`           VARCHAR(128)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',         -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`       VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',          -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`          VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',             -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`          BIT(1)          DEFAULT NULL COMMENT '「active」- 是否启用',                            -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`        VARCHAR(10)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',      -- [国际化] 如: zh_CN, en_US,
    `METADATA`        TEXT            COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                     -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`         VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`      DATETIME        DEFAULT NULL COMMENT '「createdAt」- 创建时间',                         -- [审计] 创建时间
    `CREATED_BY`      VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',       -- [审计] 创建人
    `UPDATED_AT`      DATETIME        DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                         -- [审计] 更新时间
    `UPDATED_BY`      VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',       -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_F_TRANS_ITEM_CODE_SIGMA` (`CODE`, `SIGMA`) USING BTREE,
    UNIQUE KEY `UK_F_TRANS_ITEM_SERIAL_SIGMA` (`SERIAL`, `SIGMA`) USING BTREE,
    KEY `IDX_F_PAYMENT_ITEM_PAYMENT_ID` (`TRANSACTION_ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='F_TRANS_ITEM';

-- 缺失公共字段：
-- - VERSION (版本)
-- - TYPE (类型)