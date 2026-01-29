DROP TABLE IF EXISTS `S_RESOURCE`;
CREATE TABLE IF NOT EXISTS `S_RESOURCE` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`              VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                      -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `CODE`            VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 编号',
    `COMMENT`         TEXT          COLLATE utf8mb4_bin COMMENT '「comment」- 备注',                          -- [备注] 备注信息
    `IDENTIFIER`      VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「identifier」- 模型标识',      -- [关联] 当前资源所属的Model的标识
    `LEVEL`           INTEGER       DEFAULT NULL COMMENT '「level」- 需求级别',                               -- [级别] 资源需求级别
    `MODE_GROUP`      VARCHAR(32)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modeGroup」- 组模式',         -- [模式] 该资源查找组的模式
    `MODE_ROLE`       VARCHAR(32)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modeRole」- 角色模式',        -- [模式] 该资源查找角色的模式
    `MODE_TREE`       VARCHAR(32)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modeTree」- 树模式',          -- [模式] 该资源处理树（用户组）的模式
    `NAME`            VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `SEEK_COMPONENT`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「seekComponent」- 访问组件',   -- [访问] 访问者组件
    `SEEK_CONFIG`     LONGTEXT      COLLATE utf8mb4_bin COMMENT '「seekConfig」- 访问配置',                   -- [访问] 访问者配置
    `SEEK_SYNTAX`     LONGTEXT      COLLATE utf8mb4_bin COMMENT '「seekSyntax」- 访问语法',                   -- [访问] 访问者语法
    `VIRTUAL`         BIT(1)        DEFAULT NULL COMMENT '「virtual」- 是否虚拟',                             -- [类型] 虚拟资源

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`            VARCHAR(60)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`           VARCHAR(32)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',           -- [物理隔离] 核心分片键/顶层租户标识
    `TENANT_ID`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',            -- [业务隔离] SaaS 租户/具体公司标识
    `APP_ID`          VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',               -- [逻辑隔离] 区分同一租户下的不同应用
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`          BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                              -- [状态] 1=启用/正常, 0=禁用/冻结
    `LANGUAGE`        VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',        -- [国际化] 如: zh_CN, en_US
    `METADATA`        TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                       -- [扩展] JSON格式，存储非结构化配置
    `VERSION`         VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`      DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`      VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`      DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`      VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_S_RESOURCE_CODE_SIGMA` (`CODE`, `SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='资源';

-- 缺失公共字段：
-- - VERSION (版本)