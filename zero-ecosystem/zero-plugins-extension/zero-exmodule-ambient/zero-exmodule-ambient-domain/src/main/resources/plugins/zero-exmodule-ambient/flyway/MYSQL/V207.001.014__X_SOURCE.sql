DROP TABLE IF EXISTS `X_SOURCE`;
CREATE TABLE IF NOT EXISTS `X_SOURCE` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                 VARCHAR(36)    COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                  -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `DRIVER_CLASS_NAME`  VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「driverClassName」- 数据库驱动指定', -- 数据库驱动指定，JDBC4之前
    `HOSTNAME`           VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「hostname」- 主机地址',
    `INSTANCE`           VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「instance」- 实例名称',
    `IP_V4`              VARCHAR(15)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「ipV4」- IP v4地址',
    `IP_V6`              VARCHAR(40)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「ipV6」- IP v6地址',
    `JDBC_CONFIG`        TEXT           COLLATE utf8mb4_bin COMMENT '「jdbcConfig」- 连接字符串中',           -- 连接字符串中的配置key=findRunning
    `JDBC_URL`           VARCHAR(1024)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「jdbcUrl」- JDBC连接字符串',
    `PASSWORD`           VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「password」- 密码',
    `PORT`               INTEGER        DEFAULT NULL COMMENT '「port」- 端口号',
    `USERNAME`           VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「username」- 账号',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `CATEGORY`           VARCHAR(32)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「category」- 类别',

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`              VARCHAR(128)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',       -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`          VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',        -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`             VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',           -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`             BIT(1)         DEFAULT NULL COMMENT '「active」- 是否启用',                          -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`           VARCHAR(10)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',    -- [国际化] 如: zh_CN, en_US,
    `METADATA`           TEXT           COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                   -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`            VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`         DATETIME       DEFAULT NULL COMMENT '「createdAt」- 创建时间',                       -- [审计] 创建时间
    `CREATED_BY`         VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',     -- [审计] 创建人
    `UPDATED_AT`         DATETIME       DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                       -- [审计] 更新时间
    `UPDATED_BY`         VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',     -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_X_SOURCE_APP_ID` (`APP_ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='X_SOURCE';

-- 缺失公共字段：
-- - VERSION (版本)
-- - TYPE (类型)