DROP TABLE IF EXISTS `I_SERVICE`;

CREATE TABLE IF NOT EXISTS `I_SERVICE` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                    VARCHAR(36)   NOT NULL COLLATE utf8mb4_bin COMMENT '「id」- 主键',                -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    -- 基础信息
    `NAME`                  VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「name」- 名称',
    `NAMESPACE`             VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「namespace」- 名空间',     -- 服务所在名空间
    `COMMENT`               TEXT          COLLATE utf8mb4_bin COMMENT '「comment」- 备注',

    -- 标识与规则
    `IDENTIFIER`            VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「identifier」- 模型标识',  -- 当前类型描述的Model的标识
    `IDENTIFIER_COMPONENT`  VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「identifierComponent」- 标识组件', -- 当前业务接口使用的标识选择器
    `RULE_UNIQUE`           MEDIUMTEXT    COLLATE utf8mb4_bin COMMENT '「ruleUnique」- 唯一规则',             -- 第二标识规则，当前通道的专用标识规则RuleUnique

    -- 通道配置 (Channel)
    `CHANNEL_TYPE`          VARCHAR(20)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「channelType」- 通道类型', -- ADAPTOR / CONNECTOR / ACTOR / DIRECTOR / DEFINE
    `CHANNEL_COMPONENT`     VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「channelComponent」- 通道组件', -- 自定义通道专用组件
    `CHANNEL_CONFIG`        MEDIUMTEXT    COLLATE utf8mb4_bin COMMENT '「channelConfig」- 通道配置',          -- 通道（自定义）配置信息，Channel专用

    -- 字典配置 (Dict)
    `DICT_COMPONENT`        VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「dictComponent」- 字典组件', -- 字典配置中的插件
    `DICT_CONFIG`           MEDIUMTEXT    COLLATE utf8mb4_bin COMMENT '「dictConfig」- 字典配置',             -- 字典的配置信息
    `DICT_EPSILON`          MEDIUMTEXT    COLLATE utf8mb4_bin COMMENT '「dictEpsilon」- 字典消费配置',        -- 字典的消费配置

    -- 映射配置 (Mapping)
    `MAPPING_MODE`          VARCHAR(20)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「mappingMode」- 映射模式',
    `MAPPING_COMPONENT`     VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「mappingComponent」- 映射组件',
    `MAPPING_CONFIG`        MEDIUMTEXT    COLLATE utf8mb4_bin COMMENT '「mappingConfig」- 映射配置',          -- 映射专用配置

    -- 服务与集成 (Service & Integration)
    `SERVICE_COMPONENT`     VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「serviceComponent」- 服务组件', -- 服务组件定义
    `SERVICE_CONFIG`        MEDIUMTEXT    COLLATE utf8mb4_bin COMMENT '「serviceConfig」- 业务配置',          -- 业务组件配置，业务组件专用
    `SERVICE_RECORD`        VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「serviceRecord」- 服务记录', -- 服务记录定义
    `CONFIG_DATABASE`       MEDIUMTEXT    COLLATE utf8mb4_bin COMMENT '「configDatabase」- 数据库配置',       -- 当前通道访问的Database
    `CONFIG_INTEGRATION`    MEDIUMTEXT    COLLATE utf8mb4_bin COMMENT '「configIntegration」- 集成配置',      -- 第三方专用集成配置

    -- 脚本与引擎 (Script & Engine)
    `IN_SCRIPT`             MEDIUMTEXT    COLLATE utf8mb4_bin COMMENT '「inScript」- 前置脚本',               -- 加载脚本引擎ScriptEngine前置脚本
    `OUT_SCRIPT`            MEDIUMTEXT    COLLATE utf8mb4_bin COMMENT '「outScript」- 后置脚本',              -- 加载脚本引擎ScriptEngine后置脚本
    `IS_GRAPHIC`            BIT(1)        DEFAULT NULL COMMENT '「isGraphic」- 图引擎',                       -- 是否驱动图引擎
    `IS_WORKFLOW`           BIT(1)        DEFAULT NULL COMMENT '「isWorkflow」- 工作流',                      -- 是否驱动工作流引擎

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`                  VARCHAR(64)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「type」- 类型',            -- [类型] 服务类型

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`                 VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',       -- [物理隔离] 核心分片键/顶层租户标识
    `TENANT_ID`             VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',      -- [业务隔离] SaaS 租户/具体公司标识
    `APP_ID`                VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',         -- [逻辑隔离] 区分同一租户下的不同应用
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`                BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                        -- [状态] 1=启用/正常, 0=禁用/冻结
    `LANGUAGE`              VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',  -- [国际化] 如: zh_CN, en_US
    `METADATA`              TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                 -- [扩展] JSON格式，存储非结构化配置
    `VERSION`               VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',     -- [版本] 乐观锁

    -- ==================================================================================================
    -- 🕒 5. 审计字段 (Audit Fields)
    -- ==================================================================================================
    `CREATED_AT`            DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',                     -- [审计] 创建时间
    `CREATED_BY`            VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',   -- [审计] 创建人
    `UPDATED_AT`            DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                     -- [审计] 更新时间
    `UPDATED_BY`            VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',   -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_I_SERVICE_NAME_NAMESPACE` (`NAME`, `NAMESPACE`) USING BTREE,
    KEY `IDX_I_SERVICE_SIGMA` (`SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='服务';

-- 缺失公共字段：
-- - VERSION (版本)
-- - TYPE (类型)