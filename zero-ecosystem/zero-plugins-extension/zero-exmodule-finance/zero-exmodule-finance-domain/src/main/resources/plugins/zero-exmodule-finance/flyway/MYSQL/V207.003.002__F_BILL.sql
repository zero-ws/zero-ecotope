DROP TABLE IF EXISTS `F_BILL`;
CREATE TABLE IF NOT EXISTS `F_BILL` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`          VARCHAR(36)     COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                        -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `AMOUNT`      DECIMAL(18, 2)  NOT NULL COMMENT '「amount」- 账单金额',
    `BOOK_ID`     VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「bookId」- 关联账本ID',
    `CODE`        VARCHAR(255)    NOT NULL COLLATE utf8mb4_bin COMMENT '「code」- 编号',
    `COMMENT`     LONGTEXT        COLLATE utf8mb4_bin COMMENT '「comment」- 备注',
    `END_AT`      DATETIME        DEFAULT NULL COMMENT '「endAt」- 结束时间',
    `GROUP_BY`    VARCHAR(64)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「groupBy」- 分组',
    `INCOME`      BIT(1)          DEFAULT NULL COMMENT '「income」- true',                                    -- true = 消费类，false = 付款类
    `NAME`        VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `ORDER_ID`    VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「orderId」- 订单对应的订单ID',
    `SERIAL`      VARCHAR(255)    NOT NULL COLLATE utf8mb4_bin COMMENT '「serial」- 单号',
    `START_AT`    DATETIME        DEFAULT NULL COMMENT '「startAt」- 开始时间',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`        VARCHAR(36)     NOT NULL COLLATE utf8mb4_bin COMMENT '「type」- 类型',                      -- [类型],
    `CATEGORY`    VARCHAR(36)     NOT NULL COLLATE utf8mb4_bin COMMENT '「category」- 类别',
    `MODEL_ID`    VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelId」- 模型标识',           -- 关联的模型identifier，用于描述
    `MODEL_KEY`   VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelKey」- 模型记录ID',          -- 关联的模型记录ID，用于描述哪一个Model中的记录

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
    UNIQUE KEY `UK_F_BILL_CODE_ORDER_ID_SIGMA` (`CODE`, `ORDER_ID`, `SIGMA`) USING BTREE,
    UNIQUE KEY `UK_F_BILL_SERIAL_ORDER_ID_SIGMA` (`SERIAL`, `ORDER_ID`, `SIGMA`) USING BTREE,
    KEY `IDX_F_BILL_ORDER_ID` (`ORDER_ID`) USING BTREE,
    KEY `IDX_F_BILL_BOOK_ID` (`BOOK_ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='F_BILL';

-- 缺失公共字段：
-- - VERSION (版本)