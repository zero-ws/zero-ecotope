DROP TABLE IF EXISTS `I_API`;
CREATE TABLE IF NOT EXISTS `I_API` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`               VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                     -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `COMMENT`          TEXT          COLLATE utf8mb4_bin COMMENT '「comment」- 备注',
    `CONSUMES`         TEXT          COLLATE utf8mb4_bin COMMENT '「consumes」- 当前接口使用的客户端',        -- 当前接口使用的客户端 MIME
    `IN_MAPPING`       TEXT          COLLATE utf8mb4_bin COMMENT '「inMapping」- 参数映射规则',
    `IN_PLUG`          VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「inPlug」- 参数请求流程中的插件',
    `IN_RULE`          TEXT          COLLATE utf8mb4_bin COMMENT '「inRule」- 参数验证、转换基',              -- 参数验证、转换基本规则
    `IN_SCRIPT`        VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「inScript」- 【保留】参数请求', -- 【保留】参数请求流程中的脚本控制
    `METHOD`           VARCHAR(20)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「method」- 接口对应',         -- 接口对应的HTTP方法
    `NAME`             VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `OUT_WRITER`       VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「outWriter」- 响应格式处理器',
    `PARAM_CONTAINED`  TEXT          COLLATE utf8mb4_bin COMMENT '「paramContained」- 必须参数表',            -- 必须参数表，一个JsonArray用于返回 400基本验证（验证Body）
    `PARAM_MODE`       VARCHAR(20)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「paramMode」- 参数来源',      -- 参数来源，QUERY / BODY / DEFINE / PATH
    `PARAM_REQUIRED`   TEXT          COLLATE utf8mb4_bin COMMENT '「paramRequired」- 必须参数表',             -- 必须参数表，一个JsonArray用于返回 400基本验证（验证Query和Path）
    `PRODUCES`         TEXT          COLLATE utf8mb4_bin COMMENT '「produces」- 当前接口使用的服务端',        -- 当前接口使用的服务端 MIME
    `SECURE`           BIT(1)        DEFAULT NULL COMMENT '「secure」- 是否走安全通道',                       -- 是否走安全通道，默认为TRUE
    `SERVICE_ID`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「serviceId」- 关联的服务ID',
    `URI`              VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「uri」- 接口路径',            -- 接口路径，安全路径位于 /api 之下
    `WORKER_ADDRESS`   VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「workerAddress」- 请求发送地址',
    `WORKER_CLASS`     VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「workerClass」- OX | PLU',    -- OX | PLUG专用，请求执行器对应的JavaClass名称
    `WORKER_CONSUMER`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「workerConsumer」- 请求地址消费专用组件',
    `WORKER_JS`        VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「workerJs」- JS 专用',        -- JS 专用，JavaScript路径：running/workers/<app>/下的执行器
    `WORKER_TYPE`      VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「workerType」- Worker类型',   -- Worker类型：JS / PLUG / STD

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`             VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',               -- [类型],

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
    UNIQUE KEY `UK_I_API_URI_METHOD_SIGMA` (`URI`, `METHOD`, `SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='接口';

-- 缺失公共字段：
-- - VERSION (版本)