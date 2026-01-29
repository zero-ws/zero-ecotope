DROP TABLE IF EXISTS `E_PROJECT`;
CREATE TABLE IF NOT EXISTS `E_PROJECT` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`             VARCHAR(36)     NOT NULL COLLATE utf8mb4_bin COMMENT '「id」- 主键',                     -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `AMOUNT`         DECIMAL(18, 2)  DEFAULT NULL COMMENT '「amount」- 项目金额',
    `BUDGET`         VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「budget」- 所属预算',         -- 所属预算，zero.project.budget
    `CODE`           VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 编号',
    `DEPT_ID`        VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「deptId」- 所属部门',         -- 所属部门, resource.departments
    `DESCRIPTION`    LONGTEXT        COLLATE utf8mb4_bin COMMENT '「description」- 描述',
    `END_AT`         DATETIME        DEFAULT NULL COMMENT '「endAt」- 实际结束日期',
    `ICON`           VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「icon」- 图标',
    `LEAD_BY`        VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「leadBy」- 项目经理',
    `LEVEL`          VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「level」- 项目级别',          -- 项目级别，zero.project.level
    `NAME`           VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `PLAN_END_AT`    DATETIME        DEFAULT NULL COMMENT '「planEndAt」- 结束日期',
    `PLAN_START_AT`  DATETIME        DEFAULT NULL COMMENT '「planStartAt」- 开始日期',
    `PRIORITY`       VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「priority」- 优先级',         -- 项目优先级，zero.project.priority
    `REMARK`         LONGTEXT        COLLATE utf8mb4_bin COMMENT '「remark」- 项目备注',
    `RISK`           VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「risk」- 项目风险',           -- 项目风险，zero.project.risk
    `SHORT_NAME`     VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「shortName」- 项目简称',
    `START_AT`       DATETIME        DEFAULT NULL COMMENT '「startAt」- 实际开始日期',
    `SUBJECT`        TEXT            COLLATE utf8mb4_bin COMMENT '「subject」- 项目目标',
    `TITLE`          VARCHAR(1024)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「title」- 标题',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`           VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',               -- [类型],
    `STATUS`         VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「status」- 状态',             -- 项目状态，zero.project.status

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`          VARCHAR(128)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',          -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`      VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',           -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`         VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',              -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`         BIT(1)          DEFAULT NULL COMMENT '「active」- 是否启用',                             -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`       VARCHAR(10)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',       -- [国际化] 如: zh_CN, en_US,
    `METADATA`       TEXT            COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                      -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`        VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`     DATETIME        DEFAULT NULL COMMENT '「createdAt」- 创建时间',                          -- [审计] 创建时间
    `CREATED_BY`     VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',        -- [审计] 创建人
    `UPDATED_AT`     DATETIME        DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                          -- [审计] 更新时间
    `UPDATED_BY`     VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',        -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_E_PROJECT_CODE_SIGMA` (`CODE`, `SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='E_PROJECT';

-- 缺失公共字段：
-- - VERSION (版本)