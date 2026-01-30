DROP TABLE IF EXISTS `X_TENANT`;
CREATE TABLE IF NOT EXISTS `X_TENANT` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`          VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                          -- [主键] 采用 Snowflake/UUID，避开自增ID
    `NAME`        VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `CODE`        VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 编号',

    -- ==================================================================================================
    -- 📝 2.1. 基础业务区 (Profile & Identity)
    -- ==================================================================================================
    `ALIAS`       VARCHAR(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「alias」- 简称/别名',                -- [✨推荐] 用于显示较短的名字 
    `DESCRIPTION` TEXT         COLLATE utf8mb4_bin COMMENT '「desc」- 简介',                                           -- [✨推荐] 企业简介 
    `EMAIL`       VARCHAR(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「email」- 管理员邮箱',                -- 用于接收账单、系统通知 
    `PHONE`       VARCHAR(20)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「phone」- 联系电话', 
    `CONTACT`     VARCHAR(64)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「contact」- 联系人姓名', 
    `ADDRESS`     VARCHAR(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「address」- 联系地址',
    
    -- ==================================================================================================
    -- 📝 2.2. 财务与认证区 (Finance & KYC)
    -- ==================================================================================================
    `ID_TYPE`     VARCHAR(32)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「idType」- 证件类型',               -- [✨推荐] 区分: PERSONAL(个人), COMPANY(企业)
    `ID_NUMBER`   VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「idNumber」- 身份证号',
    `ID_BACK`     VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「idBack」- 身份证反面',
    `ID_FRONT`    VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「idFront」- 身份证正面',
    `BANK_CARD`   VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「bankCard」- 开户行账号',
    `BANK_ID`     VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「bankId」- 开户行',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`        VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',                    -- [类型],
    `STATUS`      VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「status」- 状态',

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`       VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',               -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`   VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',                -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`      VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',                   -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`      BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                                  -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`    VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',            -- [国际化] 如: zh_CN, en_US,
    `METADATA`    TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                           -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`     VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`  DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',                               -- [审计] 创建时间
    `CREATED_BY`  VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',             -- [审计] 创建人
    `UPDATED_AT`  DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                               -- [审计] 更新时间
    `UPDATED_BY`  VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',             -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_X_TENANT_CODE` (`CODE`) USING BTREE,
    UNIQUE KEY `UK_X_TENANT_SIGMA` (`SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='X_TENANT';

-- 缺失公共字段：
-- - VERSION (版本)