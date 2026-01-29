DROP TABLE IF EXISTS `F_PRE_AUTHORIZE`;
CREATE TABLE IF NOT EXISTS `F_PRE_AUTHORIZE` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`          VARCHAR(36)     COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                        -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `AMOUNT`      DECIMAL(18, 2)  NOT NULL COMMENT '「amount」- 当前预授权刷单金额',
    `BANK_CARD`   VARCHAR(255)    NOT NULL COLLATE utf8mb4_bin COMMENT '「bankCard」- 刷预授权的银行卡号',
    `BANK_NAME`   VARCHAR(255)    NOT NULL COLLATE utf8mb4_bin COMMENT '「bankName」- 预授权银行名称',
    `BILL_ID`     VARCHAR(36)     DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「billId」 - 预授权所属账单ID',
    `BOOK_ID`     VARCHAR(36)     DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「bookId」 - 所属账本ID',
    `CODE`        VARCHAR(255)    NOT NULL COLLATE utf8mb4_bin COMMENT '「code」- 编号',
    `COMMENT`     LONGTEXT        COLLATE utf8mb4_bin COMMENT '「comment」- 备注',
    `EXPIRED_AT`  DATETIME        DEFAULT NULL COMMENT '「expiredAt」——预授权有效期',
    `ORDER_ID`    VARCHAR(36)     DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「orderId」- 预授权所属订单ID',
    `SERIAL`      VARCHAR(36)     NOT NULL COLLATE utf8mb4_bin COMMENT '「serial」- 单号',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `STATUS`      VARCHAR(36)     NOT NULL COLLATE utf8mb4_bin COMMENT '「status」- 状态',

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`       VARCHAR(128)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',             -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`   VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',              -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`      VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',                 -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`      BIT(1)          DEFAULT NULL COMMENT '「active」- 是否启用',                                -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`    VARCHAR(10)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',          -- [国际化] 如: zh_CN, en_US,
    `METADATA`    TEXT            COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                         -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`     VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`  DATETIME        DEFAULT NULL COMMENT '「createdAt」- 创建时间',                             -- [审计] 创建时间
    `CREATED_BY`  VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',           -- [审计] 创建人
    `UPDATED_AT`  DATETIME        DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                             -- [审计] 更新时间
    `UPDATED_BY`  VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',           -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_F_PRE_AUTHORIZE_CODE_BILL_ID_SIGMA` (`CODE`, `BILL_ID`, `SIGMA`) USING BTREE,
    UNIQUE KEY `UK_F_PRE_AUTHORIZE_SERIAL_BILL_ID_SIGMA` (`SERIAL`, `BILL_ID`, `SIGMA`) USING BTREE,
    KEY `IDX_F_PRE_AUTHORIZE_ORDER_ID` (`ORDER_ID`) USING BTREE,
    KEY `IDX_F_PRE_AUTHORIZE_BOOK_ID` (`BOOK_ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='F_PRE_AUTHORIZE';

-- 缺失公共字段：
-- - VERSION (版本)
-- - TYPE (类型)