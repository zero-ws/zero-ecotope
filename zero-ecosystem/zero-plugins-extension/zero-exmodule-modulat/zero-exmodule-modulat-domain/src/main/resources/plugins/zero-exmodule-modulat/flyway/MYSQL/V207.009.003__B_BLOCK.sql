DROP TABLE IF EXISTS `B_BLOCK`;
CREATE TABLE IF NOT EXISTS `B_BLOCK` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`              VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                      -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `AUTHORIZED`      BIT(1)        DEFAULT NULL COMMENT '「authorized」- 是否授权',
    `BAG_ID`          VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「bagId」- 所属包ID',
    `CODE`            VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 编号',
    `LIC_IDENTIFIER`  LONGTEXT      COLLATE utf8mb4_bin COMMENT '「licIdentifier」- 允许的模型标识',
    `LIC_MENU`        LONGTEXT      COLLATE utf8mb4_bin COMMENT '「licMenu」- 该Block包含',                   -- 该Block包含的菜单
    `NAME`            VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `SIGN_AT`         DATETIME      DEFAULT NULL COMMENT '「signAt」- 发证时间',
    `SIGN_END`        DATETIME      DEFAULT NULL COMMENT '「signEnd」- 证书过期时间',
    `SIGN_ISSUER`     VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「signIssuer」- 许可证发证机构',
    `SIGN_LIC`        LONGTEXT      COLLATE utf8mb4_bin COMMENT '「signLic」- 许可证内容',
    `SIGN_NAME`       VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「signName」- 许可证名称',
    `SIGN_SECRET`     VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「signSecret」- 证书专用密钥',
    `SIGN_START`      DATETIME      DEFAULT NULL COMMENT '「signStart」- 证书过期时间',
    `UI_CONFIG`       LONGTEXT      COLLATE utf8mb4_bin COMMENT '「uiConfig」- 子模块核心配置',
    `UI_CONTENT`      LONGTEXT      COLLATE utf8mb4_bin COMMENT '「uiContent」- 配置数据',
    `UI_ICON`         VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「uiIcon」- 子模块图标',
    `UI_SORT`         BIGINT        DEFAULT NULL COMMENT '「uiSort」- 子模块排序',
    `UI_STYLE`        TEXT          COLLATE utf8mb4_bin COMMENT '「uiStyle」- 子模块风格',

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`           VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',           -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',            -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`          VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',               -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`          BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                              -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`        VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',        -- [国际化] 如: zh_CN, en_US,
    `METADATA`        TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                       -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`         VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号', -- [版本]
    -- ==================================================================================================
    `CREATED_AT`      DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',                           -- [审计] 创建时间
    `CREATED_BY`      VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',         -- [审计] 创建人
    `UPDATED_AT`      DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                           -- [审计] 更新时间
    `UPDATED_BY`      VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',         -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_B_BLOCK_NAME_APP_ID_BAG_ID` (`NAME`, `APP_ID`, `BAG_ID`) USING BTREE,
    UNIQUE KEY `UK_B_BLOCK_CODE_APP_ID` (`CODE`, `APP_ID`) USING BTREE,
    UNIQUE KEY `UK_B_BLOCK_SIGN_SECRET` (`SIGN_SECRET`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='B_BLOCK';

-- 缺失公共字段：
-- - TYPE (类型)