DROP TABLE IF EXISTS `W_FLOW`;
CREATE TABLE IF NOT EXISTS `W_FLOW` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                    VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `AUTHORIZED_COMPONENT`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「authorizedComponent」- 流程授权组件',
    `AUTHORIZED_CONFIG`     LONGTEXT      COLLATE utf8mb4_bin COMMENT '「authorizedConfig」- 流程授权配置',
    `CODE`                  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 编号',          -- 流程定义编号（系统可用）
    `COMMENT`               LONGTEXT      COLLATE utf8mb4_bin COMMENT '「comment」- 备注',
    `DEFINITION_KEY`        VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「definitionKey」- 定义ID（读取流', -- 定义ID（读取流程图所需）, getProcessDefinitionId
    `END_COMPONENT`         VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「endComponent」- 完成组件',
    `END_CONFIG`            LONGTEXT      COLLATE utf8mb4_bin COMMENT '「endConfig」- 完成配置',
    `GENERATE_COMPONENT`    VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「generateComponent」- Todo生成组件',
    `GENERATE_CONFIG`       LONGTEXT      COLLATE utf8mb4_bin COMMENT '「generateConfig」- Todo生成配置',
    `NAME`                  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `RUN_COMPONENT`         VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「runComponent」- 执行组件',
    `RUN_CONFIG`            LONGTEXT      COLLATE utf8mb4_bin COMMENT '「runConfig」- 执行配置',
    `START_COMPONENT`       VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「startComponent」- 启动组件',
    `START_CONFIG`          LONGTEXT      COLLATE utf8mb4_bin COMMENT '「startConfig」- 启动配置',
    `UI_ASSIST`             LONGTEXT      COLLATE utf8mb4_bin COMMENT '「uiAssist」- 界面辅助数据专用配置',
    `UI_COMPONENT`          VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「uiComponent」- 界面组件',
    `UI_CONFIG`             LONGTEXT      COLLATE utf8mb4_bin COMMENT '「uiConfig」- 界面配置',
    `UI_LINKAGE`            LONGTEXT      COLLATE utf8mb4_bin COMMENT '「uiLinkage」- 关联部分专用配置',      -- 关联部分专用配置：关联工单、关联资产、关联附件

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`                  VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',          -- [类型],

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`                 VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',     -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`             VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',      -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`                VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',         -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`                BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                        -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`              VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',  -- [国际化] 如: zh_CN, en_US,
    `METADATA`              TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                 -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`               VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`            DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',                     -- [审计] 创建时间
    `CREATED_BY`            VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',   -- [审计] 创建人
    `UPDATED_AT`            DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                     -- [审计] 更新时间
    `UPDATED_BY`            VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',   -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_W_FLOW_NAME_SIGMA` (`NAME`, `SIGMA`) USING BTREE,
    UNIQUE KEY `UK_W_FLOW_CODE_SIGMA` (`CODE`, `SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='W_FLOW';

-- 缺失公共字段：
-- - VERSION (版本)