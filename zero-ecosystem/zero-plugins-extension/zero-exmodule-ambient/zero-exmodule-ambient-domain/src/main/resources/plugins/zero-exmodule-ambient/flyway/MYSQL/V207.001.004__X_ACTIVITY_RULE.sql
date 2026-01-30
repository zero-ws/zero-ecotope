DROP TABLE IF EXISTS `X_ACTIVITY_RULE`;
CREATE TABLE IF NOT EXISTS `X_ACTIVITY_RULE` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`               VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                     -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `DEFINITION_KEY`   VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「definitionKey」- 流程节点名',  -- 流程对应的 definitionKey，用于查询所有规则用
    `HOOK_COMPONENT`   VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「hookComponent」- 钩子组件',
    `HOOK_CONFIG`      LONGTEXT      COLLATE utf8mb4_bin COMMENT '「hookConfig」- 钩子配置',                  -- 回调钩子组件配置
    `LOGGING`          BIT(1)        DEFAULT NULL COMMENT '「logging」- 是否记录日志',
    `RULE_CONFIG`      LONGTEXT      COLLATE utf8mb4_bin COMMENT '「ruleConfig」- 规则配置',
    `RULE_EXPRESSION`  LONGTEXT      COLLATE utf8mb4_bin COMMENT '「ruleExpression」- 规则表达式 ',            -- 规则触发表达式 ( 可以是多个，JsonArray格式 )
    `RULE_FIELD`       VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「ruleField」- 规则源字段',
    `RULE_IDENTIFIER`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「ruleIdentifier」- 规则源模型',-- 对应模型的 identifier
    `RULE_MESSAGE`     TEXT          COLLATE utf8mb4_bin COMMENT '「ruleMessage」- 输出消息',                  -- 输出消息专用, Ut.fromExpression解析（特殊解析）
    `RULE_NAME`        VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「ruleName」- 规则名称',       -- 规则名称，如果 type = ATOM 时读取，并设置到 typeName 中
    `RULE_NS`          VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「ruleNs」- 规则名空间', 
    `RULE_ORDER`       BIGINT        DEFAULT NULL COMMENT '「ruleOrder」- 触发顺序',                          -- 规则触发顺序，修正两个时间戳，生成时序号统一，先生成的规则排序在上边
    `RULE_TPL`         TEXT          COLLATE utf8mb4_bin COMMENT '「ruleTpl」- 参数模版',                     -- 参数模板专用，JsonObject结构
    `TASK_KEY`         VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「taskKey」- 任务节点名',      -- 和待办绑定的taskKey

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`             VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 规则类型',           -- [类型],

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
    KEY `IDXM_X_ACTIVITY_RULE_DEFINITION_TASK_KEY` (`DEFINITION_KEY`, `TASK_KEY`, `SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='X_ACTIVITY_RULE';

-- 缺失公共字段：
-- - VERSION (版本)