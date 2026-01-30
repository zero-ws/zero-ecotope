DROP TABLE IF EXISTS `S_PATH`;
CREATE TABLE IF NOT EXISTS `S_PATH` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`             VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                       -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `CODE`           VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 编号',
    `DM_COMPONENT`   VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「dmComponent」- 必须绑定组专用D', -- 必须绑定组专用Dao组件
    `DM_CONDITION`   TEXT          COLLATE utf8mb4_bin COMMENT '「dmCondition」- 分组条件',
    `DM_CONFIG`      TEXT          COLLATE utf8mb4_bin COMMENT '「dmConfig」- 组配置信息',                    -- 组配置信息，配置呈现部分
    `DM_TYPE`        VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「dmType」- 分组类型',
    `MAPPING`        TEXT          COLLATE utf8mb4_bin COMMENT '「mapping」- 从',                             -- 从 dm -> ui 转换
    `NAME`           VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `PARENT_ID`      VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「parentId」- 父节点',           -- 区域模式下的父ID，系统内部读取
    `PHASE`          VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「phase」- UI读取数据',          -- UI读取数据的操作周期
    `RUN_COMPONENT`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「runComponent」- HValve执行组件', -- HValve执行组件，组件内置处理 dm / ui 两部分内容
    `RUN_TYPE`       VARCHAR(5)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「runType」- 视图管理类型',      -- 视图管理类型（查询用）
    `UI_COMPONENT`   VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「uiComponent」- 特殊组件',
    `UI_CONDITION`   TEXT          COLLATE utf8mb4_bin COMMENT '「uiCondition」- 查询模板',
    `UI_CONFIG`      TEXT          COLLATE utf8mb4_bin COMMENT '「uiConfig」- 界面配置',
    `UI_SORT`        INT           DEFAULT NULL COMMENT '「sort」- 该板块的排序',                             -- 该板块的排序（前端）
    `UI_SURFACE`     TEXT          COLLATE utf8mb4_bin COMMENT '「uiSurface」- 界面呈现模式',                 -- 界面呈现模式，已经被降维之后（列表、树、其他等相关配置）
    `UI_TYPE`        VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「uiType」- 目标数据源类型',

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
    UNIQUE KEY `UK_S_PATH_CODE_SIGMA` (`CODE`, `SIGMA`) USING BTREE,
    KEY `IDX_S_PATH_RUN_TYPE_SIGMA` (`RUN_TYPE`, `SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='S_PATH';

-- 缺失公共字段：
-- - VERSION (版本)
-- - TYPE (类型)