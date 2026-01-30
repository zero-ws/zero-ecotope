DROP TABLE IF EXISTS `E_IDENTITY`;
CREATE TABLE IF NOT EXISTS `E_IDENTITY` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`               VARCHAR(36)   NOT NULL COLLATE utf8mb4_bin COMMENT '「id」- 主键',                     -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `ADDRESS`          TEXT          COLLATE utf8mb4_bin COMMENT '「address」- 居住地址',
    `BIRTHDAY`         DATETIME      DEFAULT NULL COMMENT '「birthday」- 生日',
    `CAR_PLATE`        VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「carPlate」- 常用车牌',
    `CODE`             VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 编号',
    `CONTACT_ADDRESS`  TEXT          COLLATE utf8mb4_bin COMMENT '「contactAddress」- 联系地址',
    `CONTACT_EMAIL`    VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「contactEmail」- 联系Email',
    `CONTACT_MOBILE`   VARCHAR(20)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「contactMobile」- 联系手机',
    `CONTACT_PHONE`    VARCHAR(20)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「contactPhone」- 座机',
    `COUNTRY`          VARCHAR(20)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「country」- 国籍',
    `DRIVER_LICENSE`   VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「driverLicense」- 驾驶证',
    `EC_ALIPAY`        VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「ecAlipay」- 支付宝',
    `EC_QQ`            VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「ecQq」- QQ号码',
    `EC_WECHAT`        VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「ecWechat」- 微信',
    `GENDER`           BIT(1)        DEFAULT NULL COMMENT '「gender」- 性别',
    `IDC_ADDRESS`      TEXT          COLLATE utf8mb4_bin COMMENT '「idcAddress」- 证件地址',
    `IDC_BACK`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「idcBack」- 证件背面附件',
    `IDC_EXPIRED_AT`   DATETIME      DEFAULT NULL COMMENT '「idcExpiredAt」- 证件过期时间',
    `IDC_FRONT`        VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「idcFront」- 证件正面附件',
    `IDC_ISSUER`       VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「idcIssuer」- 证件签发机构',
    `IDC_ISSUE_AT`     DATETIME      DEFAULT NULL COMMENT '「idcIssueAt」- 证件签发时间',
    `IDC_NUMBER`       VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「idcNumber」- 证件号',
    `IDC_TYPE`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「idcType」- 证件类型',
    `MARITAL`          VARCHAR(32)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「marital」- 婚姻状况',
    `NATION`           VARCHAR(32)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「nation」- 民族',
    `NATIVE_PLACE`     VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「nativePlace」- 籍贯',
    `PASSPORT`         VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「passport」- 护照',
    `REALNAME`         VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「realname」- 真实姓名',
    `URGENT_NAME`      VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「urgentName」- 紧急联系人',
    `URGENT_PHONE`     VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「urgentPhone」- 紧急联系电话',
    `VERIFIED`         BIT(1)        DEFAULT NULL COMMENT '「verified」- 是否验证、备案',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`             VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',               -- [类型],
    `STATUS`           VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「status」- 状态',

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`            VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',          -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`        VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',           -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`           VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',              -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`           BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                             -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`         VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',       -- [国际化] 如: zh_CN, en_US,
    `METADATA`         TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                      -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`          VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`       DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',                          -- [审计] 创建时间
    `CREATED_BY`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',        -- [审计] 创建人
    `UPDATED_AT`       DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                          -- [审计] 更新时间
    `UPDATED_BY`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',        -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_E_IDENTITY_TYPE_IDC_TYPE_IDC_NUMBER` (`TYPE`, `IDC_TYPE`, `IDC_NUMBER`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='E_IDENTITY';

-- 缺失公共字段：
-- - VERSION (版本)