DROP TABLE IF EXISTS `E_COMPANY`;
CREATE TABLE IF NOT EXISTS `E_COMPANY` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                 VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                   -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `ADDRESS`            TEXT          COLLATE utf8mb4_bin COMMENT '「address」- 公司地址',
    `ALIAS`              VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「alias」- 别名',
    `CODE`               VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 编号',
    `COMMENT`            TEXT          COLLATE utf8mb4_bin COMMENT '「comment」- 备注',
    `COMPANY_ID`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「companyId」- 公司、子公司结构', -- 公司、子公司结构时需要
    `CONTACT_NAME`       VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「contactName」- 联系人电话',
    `CONTACT_ONLINE`     VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「contactOnline」- 在线联系方式',
    `CONTACT_PHONE`      VARCHAR(20)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「contactPhone」- 联系人电话',
    `CORPORATION_NAME`   VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「corporationName」- 企业法人',
    `CORPORATION_PHONE`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「corporationPhone」- 法人电话',
    `CUSTOMER_ID`        VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「customerId」- 公司作为客户时', -- 公司作为客户时的客户信息
    `EMAIL`              VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「email」- 企业邮箱',
    `FAX`                VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「fax」- 传真号',
    `HOMEPAGE`           VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「homepage」- 公司主页',
    `LOCATION_ID`        VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「locationId」- 启用LBS时对应', -- 启用LBS时对应的Location主键
    `LOGO`               VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「logo」- 图标',             -- 附件对应的 attachment Key
    `NAME`               VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `PHONE`              VARCHAR(20)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「phone」- 公司座机',
    `TAX_CODE`           VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「taxCode」- 公司税号',
    `TAX_TITLE`          VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「taxTitle」- 开票抬头',
    `TITLE`              VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「title」- 标题',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`               VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`              VARCHAR(32)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',        -- [物理隔离] 核心分片键/顶层租户标识
    `TENANT_ID`          VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',         -- [业务隔离] SaaS 租户/具体公司标识
    `APP_ID`             VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',            -- [逻辑隔离] 区分同一租户下的不同应用
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`             BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                           -- [状态] 1=启用/正常, 0=禁用/冻结
    `LANGUAGE`           VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',     -- [国际化] 如: zh_CN, en_US
    `METADATA`           TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                    -- [扩展] JSON格式，存储非结构化配置
    `VERSION`            VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`         DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`         DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_E_COMPANY_CODE_SIGMA` (`CODE`, `SIGMA`) USING BTREE,
    UNIQUE KEY `UK_E_COMPANY_NAME_SIGMA` (`NAME`, `SIGMA`) USING BTREE,
    UNIQUE KEY `UK_E_COMPANY_TAX_CODE_SIGMA` (`TAX_CODE`, `SIGMA`) USING BTREE,
    UNIQUE KEY `UK_E_COMPANY_CUSTOMER_ID_SIGMA` (`CUSTOMER_ID`, `SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='公司';

-- 缺失公共字段：
-- - VERSION (版本)