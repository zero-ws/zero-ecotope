DROP TABLE IF EXISTS `S_PACKET`;
CREATE TABLE IF NOT EXISTS `S_PACKET` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`             VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                       -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `CODE`           VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 编号',                 -- 关联的 PATH 表对应的 code
    `H_CONFIG`       LONGTEXT      COLLATE utf8mb4_bin COMMENT '「hConfig」- 字段附加配置',
    `H_MAPPING`      LONGTEXT      COLLATE utf8mb4_bin COMMENT '「hMapping」- 字段映射关系',                  -- 字段映射关系，存在转换时必须
    `H_TYPE`         VARCHAR(16)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「hType」- 行过滤类型',
    `Q_CONFIG`       LONGTEXT      COLLATE utf8mb4_bin COMMENT '「qConfig」- 条件配置',                       -- 条件配置（界面配置相关）
    `Q_MAPPING`      LONGTEXT      COLLATE utf8mb4_bin COMMENT '「qMapping」- 查询条件映射关系',
    `Q_TYPE`         VARCHAR(16)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「qType」- 条件模板',
    `RESOURCE`       VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「resource」- 关联的资源表对应的', -- 关联的资源表对应的 code
    `RUN_COMPONENT`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「runComponent」- 自定义模式下的组件',
    `RUN_CONFIG`     LONGTEXT      COLLATE utf8mb4_bin COMMENT '「runConfig」- 运行专用配置',
    `SEEK_CONFIG`    LONGTEXT      COLLATE utf8mb4_bin COMMENT '「seekConfig」- 访问者配置',
    `SEEK_SYNTAX`    LONGTEXT      COLLATE utf8mb4_bin COMMENT '「seekSyntax」- 访问者语法',
    `V_CONFIG`       LONGTEXT      COLLATE utf8mb4_bin COMMENT '「vConfig」- 列配置',
    `V_MAPPING`      LONGTEXT      COLLATE utf8mb4_bin COMMENT '「vMapping」- 列字段映射关系',                -- 列字段映射关系，存在转换时必须
    `V_TYPE`         VARCHAR(16)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「vType」- 列过滤类型',

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`          VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',            -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`      VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',             -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',                -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`         BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                               -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`       VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',         -- [国际化] 如: zh_CN, en_US,
    `METADATA`       TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                        -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`        VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`     DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',                            -- [审计] 创建时间
    `CREATED_BY`     VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',          -- [审计] 创建人
    `UPDATED_AT`     DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                            -- [审计] 更新时间
    `UPDATED_BY`     VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',          -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_S_PACKET_CODE_RESOURCE_SIGMA` (`CODE`, `RESOURCE`, `SIGMA`) USING BTREE,
    KEY `IDX_S_PACKET_PATH_CODE_SIGMA` (`CODE`, `SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='S_PACKET';

-- 缺失公共字段：
-- - VERSION (版本)
-- - TYPE (类型)