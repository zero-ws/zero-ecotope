DROP TABLE IF EXISTS `F_BANK`;
CREATE TABLE IF NOT EXISTS `F_BANK` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`           VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                         -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `ALIAS`        VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「alias」- 别名',
    `BANK_ID`      VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「bankId」- 所属银行ID',
    `BRANCH`       BIT(1)        DEFAULT NULL COMMENT '「branch」- 是否支行',
    `BRANCH_CODE`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「branchCode」- 支行编号',
    `BRANCH_NAME`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「branchName」- 支行名称',
    `CODE`         VARCHAR(255)  COLLATE utf8mb4_bin NOT NULL COMMENT '「code」- 编号',
    `COMMENT`      LONGTEXT      COLLATE utf8mb4_bin COMMENT '「comment」- 备注',
    `LOCATION_ID`  VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「locationId」- 银行地址对应信息',
    `LOGO`         VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「logo」- 图标',
    `NAME`         VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `WEBSITE`      VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「website」- 银行主页',

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`        VARCHAR(32)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',              -- [物理隔离] 核心分片键/顶层租户标识
    `TENANT_ID`    VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',               -- [业务隔离] SaaS 租户/具体公司标识
    `APP_ID`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',                  -- [逻辑隔离] 区分同一租户下的不同应用
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`       BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                                 -- [状态] 1=启用/正常, 0=禁用/冻结
    `LANGUAGE`     VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',           -- [国际化] 如: zh_CN, en_US
    `METADATA`     TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                          -- [扩展] JSON格式，存储非结构化配置
    `VERSION`      VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`   DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`   VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`   DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`   VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_F_BANK_CODE_SIGMA` (`CODE`, `SIGMA`) USING BTREE,
    UNIQUE KEY `UK_F_BANK_BRANCH_CODE_SIGMA` (`BRANCH_CODE`, `SIGMA`) USING BTREE,
    UNIQUE KEY `UK_F_BANK_BRANCH_NAME_SIGMA` (`BRANCH_NAME`, `SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='银行';

-- 缺失公共字段：
-- - VERSION (版本)
-- - TYPE (类型)