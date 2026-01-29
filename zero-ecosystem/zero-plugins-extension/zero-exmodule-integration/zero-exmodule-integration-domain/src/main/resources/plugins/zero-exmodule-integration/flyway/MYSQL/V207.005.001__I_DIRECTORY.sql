DROP TABLE IF EXISTS `I_DIRECTORY`;
CREATE TABLE IF NOT EXISTS `I_DIRECTORY` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`               VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                     -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `CODE`             VARCHAR(255)  NOT NULL COLLATE utf8mb4_bin COMMENT '「code」- 编号',
    `INTEGRATION_ID`   VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「integrationId」 - 该目录关联的 KIntegration，不关联则不转存',
    `LINKED_PATH`      VARCHAR(512)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「linkedPath」 - 链接路径，type = LINK 时专用',
    `NAME`             VARCHAR(255)  NOT NULL COLLATE utf8mb4_bin COMMENT '「name」- 名称',
    `OWNER`            VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「owner」 - 目录访问人',
    `PARENT_ID`        VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「parentId」- 父节点',
    `RUN_COMPONENT`    TEXT          COLLATE utf8mb4_bin COMMENT '「runComponent」 - 目录执行组件，抓文件专用',
    `STORE_PATH`       VARCHAR(512)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「storePath」 - 目录相对路径',
    `VISIT`            BIT(1)        DEFAULT NULL COMMENT '「visit」 - 公有 / 私有',
    `VISIT_COMPONENT`  TEXT          COLLATE utf8mb4_bin COMMENT '「visitComponent」 - 目录访问控制专用组件',
    `VISIT_GROUP`      TEXT          COLLATE utf8mb4_bin COMMENT '「visitGroup」 - 目录访问组',
    `VISIT_MODE`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「visitMode」 - 目录模式：只读 / 可写，以后扩展为其他',
    `VISIT_ROLE`       TEXT          COLLATE utf8mb4_bin COMMENT '「visitRole」 - 目录访问角色',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`             VARCHAR(36)   NOT NULL COLLATE utf8mb4_bin COMMENT '「type」- 类型',                   -- [类型],
    `CATEGORY`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「category」- 类别',

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
    UNIQUE KEY `UK_I_DIRECTORY_CODE_SIGMA` (`CODE`, `SIGMA`) USING BTREE,
    UNIQUE KEY `UK_I_DIRECTORY_NAME_PARENT_ID_SIGMA` (`NAME`, `PARENT_ID`, `SIGMA`) USING BTREE,
    UNIQUE KEY `UK_I_DIRECTORY_STORE_PATH_SIGMA` (`STORE_PATH`, `SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='I_DIRECTORY';

-- 缺失公共字段：
-- - VERSION (版本)