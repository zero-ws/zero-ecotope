DROP TABLE IF EXISTS `F_SETTLEMENT_ITEM`;
CREATE TABLE IF NOT EXISTS `F_SETTLEMENT_ITEM` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`             VARCHAR(36)     COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                     -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `AMOUNT`         DECIMAL(18, 2)  NOT NULL COMMENT '「amount」——价税合计，实际结算金额',
    `CODE`           VARCHAR(255)    NOT NULL COLLATE utf8mb4_bin COMMENT '「code」- 编号',
    `COMMENT`        LONGTEXT        COLLATE utf8mb4_bin COMMENT '「comment」- 备注',
    `DEBT_ID`        VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「debtId」 - 应收账单ID',
    `END_AT`         DATETIME        DEFAULT NULL COMMENT '「endAt」 - 结束时间',
    `FINISHED_ID`    VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「finishedId」 - 关联交易ID',
    `GROUP_BY`       VARCHAR(64)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「groupBy」 - 分组',
    `INCOME`         BIT(1)          DEFAULT NULL COMMENT '「income」- true',                                 -- true = 消费类，false = 付款类
    `MANUAL_NO`      VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「manualNo」 - 手工单号（线下单号专用）',
    `NAME`           VARCHAR(255)    NOT NULL COLLATE utf8mb4_bin COMMENT '「name」- 名称',
    `PAY_TERM_ID`    VARCHAR(36)     NOT NULL COLLATE utf8mb4_bin COMMENT '「payTermId」 - 账单项ID',
    `RELATED_ID`     VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「relatedId」 - 关联BillItem ID（保留，原系统存在）',
    `SERIAL`         VARCHAR(255)    NOT NULL COLLATE utf8mb4_bin COMMENT '「serial」- 单号',
    `SETTLEMENT_ID`  VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「settlementId」 - 结算单ID，该字段有值标识已经结算',
    `START_AT`       DATETIME        DEFAULT NULL COMMENT '「startAt」 - 开始时间',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`           VARCHAR(36)     NOT NULL COLLATE utf8mb4_bin COMMENT '「type」- 类型',                   -- [类型],

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`          VARCHAR(128)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',          -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`      VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',           -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`         VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',              -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`         BIT(1)          DEFAULT NULL COMMENT '「active」- 是否启用',                             -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`       VARCHAR(10)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',       -- [国际化] 如: zh_CN, en_US,
    `METADATA`       TEXT            COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                      -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`        VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`     DATETIME        DEFAULT NULL COMMENT '「createdAt」- 创建时间',                          -- [审计] 创建时间
    `CREATED_BY`     VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',        -- [审计] 创建人
    `UPDATED_AT`     DATETIME        DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                          -- [审计] 更新时间
    `UPDATED_BY`     VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',        -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_F_SETTLEMENT_ITEM_CODE_SETTLEMENT_ID_SIGMA` (`CODE`, `SETTLEMENT_ID`, `SIGMA`) USING BTREE,
    UNIQUE KEY `UK_F_SETTLEMENT_ITEM_SERIAL_SETTLEMENT_ID_SIGMA` (`SERIAL`, `SETTLEMENT_ID`, `SIGMA`) USING BTREE,
    KEY `IDX_F_SETTLEMENT_ITEM_DEBT_ID` (`DEBT_ID`) USING BTREE,
    KEY `IDX_F_SETTLEMENT_ITEM_SETTLEMENT_ID` (`SETTLEMENT_ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='F_SETTLEMENT_ITEM';

-- 缺失公共字段：
-- - VERSION (版本)