DROP TABLE IF EXISTS `E_CUSTOMER`;
CREATE TABLE IF NOT EXISTS `E_CUSTOMER` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`              VARCHAR(36)     NOT NULL COLLATE utf8mb4_bin COMMENT '「id」- 主键',                    -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `ADDRESS`         TEXT            COLLATE utf8mb4_bin COMMENT '「address」- 客户地址',
    `BANK_CARD`       VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「bankCard」- 开户行账号',
    `BANK_ID`         VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「bankId」- 开户行',
    `CODE`            VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 编号',
    `COMMENT`         TEXT            COLLATE utf8mb4_bin COMMENT '「comment」- 备注',
    `CONTACT_EMAIL`   VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「contactEmail」- 联系人Email',
    `CONTACT_NAME`    VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「contactName」- 联系人姓名',
    `CONTACT_ONLINE`  VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「contactOnline」- 在线联系方式',
    `CONTACT_PHONE`   VARCHAR(20)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「contactPhone」- 联系人电话',
    `EMAIL`           VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「email」- 企业邮箱',
    `FAX`             VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「fax」- 传真号',
    `HOMEPAGE`        VARCHAR(128)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「homepage」- 客户主页',
    `LOGO`            VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「logo」- 图标',              -- 附件对应的 attachment Key
    `NAME`            VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `PHONE`           VARCHAR(20)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「phone」- 客户座机',
    `RUN_UP`          BIT(1)          DEFAULT NULL COMMENT '「runUp」- 挂账属性',
    `RUN_UP_AMOUNT`   DECIMAL(18, 2)  DEFAULT NULL COMMENT '「runUpAmount」- 挂账限额',
    `SIGN_NAME`       VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「signName」- 签单人姓名',
    `SIGN_PHONE`      VARCHAR(20)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「signPhone」- 签单人电话',
    `TAX_CODE`        VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「taxCode」- 税号',
    `TAX_TITLE`       VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「taxTitle」- 开票抬头',
    `TITLE`           VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「title」- 标题',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`            VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',              -- [类型],
    `STATUS`          VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「status」- 状态',

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
    UNIQUE KEY `UK_E_CUSTOMER_TAX_CODE_SIGMA` (`TAX_CODE`, `SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='E_CUSTOMER';

-- 缺失公共字段：
-- - VERSION (版本)