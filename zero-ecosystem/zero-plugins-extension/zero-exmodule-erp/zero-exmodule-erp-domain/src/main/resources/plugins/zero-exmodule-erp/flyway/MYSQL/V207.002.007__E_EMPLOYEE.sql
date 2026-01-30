DROP TABLE IF EXISTS `E_EMPLOYEE`;
CREATE TABLE IF NOT EXISTS `E_EMPLOYEE` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`              VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                      -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `BANK_CARD`       VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「bankCard」- 开户行账号',
    `BANK_ID`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「bankId」- 开户行',
    `COMPANY_ID`      VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「companyId」- 所属公司',
    `DEPT_ID`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「deptId」- 所属部门',
    `IDENTITY_ID`     VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「identityId」- 关联档案',
    `TEAM_ID`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「teamId」- 所属组',
    `VICE_EMAIL`      VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「viceEmail」- 员工邮箱',
    `VICE_MOBILE`     VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「viceMobile」- 员工手机',
    `VICE_NAME`       VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「viceName」- 员工姓名',
    `WORK_EXTENSION`  VARCHAR(20)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「workExtension」- 分机号',
    `WORK_HIRE_AT`    DATETIME      DEFAULT NULL COMMENT '「workHireAt」- 入职时间',
    `WORK_LOCATION`   TEXT          COLLATE utf8mb4_bin COMMENT '「workLocation」- 办公地点',
    `WORK_NUMBER`     VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「workNumber」- 工号',
    `WORK_PHONE`      VARCHAR(20)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「workPhone」- 办公电话',
    `WORK_TITLE`      VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「workTitle」- 头衔',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`            VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',
    `STATUS`          VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「status」- 状态',

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
    UNIQUE KEY `UK_E_EMPLOYEE_WORK_NUMBER_COMPANY_ID` (`WORK_NUMBER`, `COMPANY_ID`) USING BTREE,
    KEY `IDX_E_EMPLOYEE_SIGMA` (`SIGMA`) USING BTREE,
    KEY `IDX_E_EMPLOYEE_SIGMA_ACTIVE` (`SIGMA`, `ACTIVE`) USING BTREE,
    KEY `IDX_E_EMPLOYEE_WORK_NUMBER` (`WORK_NUMBER`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='员工';

-- 缺失公共字段：
-- - VERSION (版本)