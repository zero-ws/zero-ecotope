DROP TABLE IF EXISTS `B_WEB`;
CREATE TABLE IF NOT EXISTS `B_WEB` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`           VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                         -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `BLOCK_ID`     VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「blockId」- 所属模块ID',
    `CODE`         VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 编号',
    `LIC_CONTENT`  LONGTEXT      COLLATE utf8mb4_bin COMMENT '「licContent」- 内容编码',
    `LIC_MODULE`   LONGTEXT      COLLATE utf8mb4_bin COMMENT '「licModule」- 单独指定',                       -- 单独指定 X_MODULE 中的记录
    `LIC_OP`       LONGTEXT      COLLATE utf8mb4_bin COMMENT '「licOp」- 界面操作',
    `LIC_TPL`      LONGTEXT      COLLATE utf8mb4_bin COMMENT '「licTpl」- PAGE',                              -- PAGE, LAYOUT, CONTROL 等记录

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`         VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',                   -- [类型],

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`        VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',              -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`    VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',               -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',                  -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`       BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                                 -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`     VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',           -- [国际化] 如: zh_CN, en_US,
    `METADATA`     TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                          -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`      VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`   DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',                              -- [审计] 创建时间
    `CREATED_BY`   VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',            -- [审计] 创建人
    `UPDATED_AT`   DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                              -- [审计] 更新时间
    `UPDATED_BY`   VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',            -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_B_WEB_CODE_BLOCK_ID` (`CODE`, `BLOCK_ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='B_WEB';

-- 缺失公共字段：
-- - VERSION (版本)