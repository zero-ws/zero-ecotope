DROP TABLE IF EXISTS `I_JOB`;

CREATE TABLE IF NOT EXISTS `I_JOB` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                  VARCHAR(36)   NOT NULL COLLATE utf8mb4_bin COMMENT '「id」- 主键',                  -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `NAME`                VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「name」- 名称',
    `CODE`                VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「code」- 编号',
    `GROUP`               VARCHAR(64)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「group」- 任务组',         -- 任务组（按组查询），自由字符串
    `NAMESPACE`           VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「namespace」- 名空间',     -- 任务所在名空间
    `COMMENT`             TEXT          COLLATE utf8mb4_bin COMMENT '「comment」- 备注',
    `ADDITIONAL`          TEXT          COLLATE utf8mb4_bin COMMENT '「additional」- 额外配置',               -- 额外配置信息
    `PROXY`               VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「proxy」- 代理类',         -- 代理类，带有@On/@Off
    `SERVICE_ID`          VARCHAR(36)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「serviceId」- 服务ID',      -- 关联的服务ID

    -- 调度与时间配置
    `DURATION`            BIGINT        DEFAULT NULL COMMENT '「duration」- 间隔时间',                        -- JOB的间隔时间（秒）
    `DURATION_COMPONENT`  VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「durationComponent」- 调度组件', -- 对应复杂调度问题
    `DURATION_CONFIG`     LONGTEXT      COLLATE utf8mb4_bin COMMENT '「durationConfig」- 调度配置',           -- 复杂调度配置
    `RUN_AT`              TIME          DEFAULT NULL COMMENT '「runAt」- 运行时间点',                         -- 定时任务中的具体的运行时间点
    `RUN_FORMULA`         TEXT          COLLATE utf8mb4_bin COMMENT '「runFormula」- 周期表达式',             -- 运行周期专用的表达式
    `THRESHOLD`           INT           DEFAULT NULL COMMENT '「threshold」- 阈值',                           -- 默认值 300s

    -- 输入输出配置
    `INCOME_ADDRESS`      VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「incomeAddress」- 入口地址', -- 字符串，@On -> address
    `INCOME_COMPONENT`    VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「incomeComponent」- 入口组件', -- 必须是JobIncome，@On -> input
    `OUTCOME_ADDRESS`     VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「outcomeAddress」- 出口地址', -- 字符串，@Off -> address
    `OUTCOME_COMPONENT`   VARCHAR(255)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「outcomeComponent」- 出口组件',-- 必须是JobOutcome，@Off -> outcome

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`                VARCHAR(20)   DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「type」- 类型',            -- [类型]

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`               VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',       -- [物理隔离] 核心分片键/顶层租户标识
    `TENANT_ID`           VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',        -- [业务隔离] SaaS 租户/具体公司标识
    `APP_ID`              VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',           -- [逻辑隔离] 区分同一租户下的不同应用
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`              BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                          -- [状态] 1=启用/正常, 0=禁用/冻结
    `LANGUAGE`            VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',    -- [国际化] 如: zh_CN, en_US
    `METADATA`            TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                   -- [扩展] JSON格式，存储非结构化配置
    `VERSION`             VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',       -- [版本] 乐观锁或版本控制

    -- ==================================================================================================
    -- 🕒 5. 审计字段 (Audit Fields)
    -- ==================================================================================================
    `CREATED_AT`          DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',                       -- [审计] 创建时间
    `CREATED_BY`          VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',     -- [审计] 创建人
    `UPDATED_AT`          DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                       -- [审计] 更新时间
    `UPDATED_BY`          VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',     -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_I_JOB_SIGMA_CODE` (`SIGMA`, `CODE`) USING BTREE,
    UNIQUE KEY `UK_I_JOB_SIGMA_NAME` (`SIGMA`, `NAME`) USING BTREE,
    UNIQUE KEY `UK_I_JOB_NAMESPACE_NAME` (`NAMESPACE`, `NAME`) USING BTREE,
    KEY `IDX_I_JOB_SIGMA` (`SIGMA`) USING BTREE,
    KEY `IDX_I_JOB_SERVICE_ID` (`SERVICE_ID`) USING BTREE,
    KEY `IDXM_I_JOB_GROUP_SIGMA` (`SIGMA`, `GROUP`) USING BTREE,
    KEY `IDXM_I_JOB_TYPE_SIGMA` (`SIGMA`, `TYPE`) USING BTREE,
    KEY `IDXM_I_JOB_GROUP_TYPE_SIGMA` (`SIGMA`, `GROUP`, `TYPE`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='任务';

-- 缺失公共字段：
-- - VERSION (版本)