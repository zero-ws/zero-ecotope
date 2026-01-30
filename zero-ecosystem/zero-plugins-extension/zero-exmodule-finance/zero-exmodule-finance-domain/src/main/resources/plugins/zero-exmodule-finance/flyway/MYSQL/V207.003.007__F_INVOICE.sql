DROP TABLE IF EXISTS `F_INVOICE`;
CREATE TABLE IF NOT EXISTS `F_INVOICE` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`              VARCHAR(36)     COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                    -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `AMOUNT`          DECIMAL(18, 2)  NOT NULL COMMENT '「amount」- 发票金额',
    `CODE`            VARCHAR(255)    NOT NULL COLLATE utf8mb4_bin COMMENT '「code」- 编号',
    `COMMENT`         LONGTEXT        COLLATE utf8mb4_bin COMMENT '「comment」- 备注',
    `DESC_BANK`       TEXT            COLLATE utf8mb4_bin COMMENT '「descBank」- 开户行信息',
    `DESC_COMPANY`    TEXT            COLLATE utf8mb4_bin COMMENT '「descCompany」 - 公司信息',
    `DESC_LOCATION`   TEXT            COLLATE utf8mb4_bin COMMENT '「descLocation」 - 地址电话',
    `DESC_USER`       TEXT            COLLATE utf8mb4_bin COMMENT '「descUser」 - 个人发票用户信息',
    `INVOICE_NUMBER`  VARCHAR(255)    NOT NULL COLLATE utf8mb4_bin COMMENT '「invoiceNumber」- 发票代码',
    `INVOICE_SERIAL`  VARCHAR(255)    NOT NULL COLLATE utf8mb4_bin COMMENT '「invoiceSerial」- 发票号码',
    `INVOICE_TITLE`   VARCHAR(255)    NOT NULL COLLATE utf8mb4_bin COMMENT '「invoiceTitle」- 发票抬头',
    `NAME`            VARCHAR(255)    NOT NULL COLLATE utf8mb4_bin COMMENT '「name」- 名称',
    `NAME_BILLING`    VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「nameBilling」开票人',
    `NAME_RECEIPT`    VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「nameReceipt」收款人',
    `NAME_RECHECK`    VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「nameRecheck」复核人',
    `NAME_SELLING`    VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「nameSelling」销售人',
    `ORDER_ID`        VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「orderId」- 订单对应的订单ID',
    `PERSONAL`        BIT(1)          DEFAULT NULL COMMENT '「personal」- 是否个人发票',
    `TIN`             VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tin」- 税号：纳税人识别号',
    `TIN_NAME`        VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tinName」- 纳税人姓名',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`            VARCHAR(36)     NOT NULL COLLATE utf8mb4_bin COMMENT '「type」- 类型',                  -- [类型],
    `MODEL_ID`        VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelId」- 模型标识',       -- 关联的模型identifier，用于描述
    `MODEL_KEY`       VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelKey」- 模型记录ID',      -- 关联的模型记录ID，用于描述哪一个Model中的记录

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
    UNIQUE KEY `UK_F_INVOICE_CODE_SIGMA` (`CODE`, `SIGMA`) USING BTREE,
    UNIQUE KEY `UK_F_INVOICE_INVOICE_NUMBER_SIGMA` (`INVOICE_NUMBER`, `SIGMA`) USING BTREE,
    UNIQUE KEY `UK_F_INVOICE_INVOICE_SERIAL_SIGMA` (`INVOICE_SERIAL`, `SIGMA`) USING BTREE,
    KEY `IDX_F_INVOICE_ORDER_ID` (`ORDER_ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='F_INVOICE';

-- 缺失公共字段：
-- - VERSION (版本)