DROP TABLE IF EXISTS `UI_COLUMN`;

CREATE TABLE IF NOT EXISTS `UI_COLUMN` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`            VARCHAR(36)  NOT NULL COLLATE utf8mb4_bin COMMENT '「id」- 主键',                    -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `CONTROL_ID`    VARCHAR(36)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「controlId」- 控件ID',       -- 关联的控件ID
    `DATA_INDEX`    VARCHAR(255) DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「dataIndex」- 列名',         -- dataIndex
    `TITLE`         VARCHAR(255) DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「title」- 标题',             -- 标题
    `POSITION`      INT          DEFAULT NULL COMMENT '「position」- 位置',                              -- 当前列的位置
    `WIDTH`         INT          DEFAULT NULL COMMENT '「width」- 宽度',                                 -- 当前列的宽度
    `FIXED`         BIT(1)       DEFAULT b'0' COMMENT '「fixed」- 是否固定',                             -- 当前列是否固定
    `SORTER`        BIT(1)       DEFAULT b'0' COMMENT '「sorter」- 是否排序',                            -- 当前列是否支持排序
    `CLASS_NAME`    VARCHAR(255) DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「className」- CSS类',        -- 当前列的特殊CSS类

    -- 配置与渲染
    `CONFIG`        TEXT         COLLATE utf8mb4_bin COMMENT '「config」- 基础配置',                      -- $config专用
    `DATUM`         TEXT         COLLATE utf8mb4_bin COMMENT '「datum」- 数据源配置',                     -- $datum专用
    `EMPTY`         VARCHAR(64)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「empty」- 空值处理',          -- $empty 专用
    `FILTER_CONFIG` TEXT         COLLATE utf8mb4_bin COMMENT '「filterConfig」- 搜索配置',                -- $filter.config 列搜索支持时的搜索配置
    `FILTER_TYPE`   VARCHAR(10)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「filterType」- 搜索类型',     -- $filter.type 支持列搜索时的搜索类型
    `FORMAT`        VARCHAR(128) DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「format」- 格式化',           -- $format时间格式专用
    `MAPPING`       TEXT         COLLATE utf8mb4_bin COMMENT '「mapping」- 映射配置',                     -- $mapping专用
    `OPTION`        TEXT         COLLATE utf8mb4_bin COMMENT '「option」- 选项配置',                      -- $option专用，executor时
    `RENDER`        VARCHAR(64)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「render」- 渲染函数',         -- 使用的Render函数

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`         VARCHAR(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',          -- [物理隔离] 核心分片键/顶层租户标识
    `TENANT_ID`     VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',         -- [业务隔离] SaaS 租户/具体公司标识
    `APP_ID`        VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',            -- [逻辑隔离] 区分同一租户下的不同应用
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`        BIT(1)       DEFAULT NULL COMMENT '「active」- 是否启用',                             -- [状态] 1=启用/正常, 0=禁用/冻结
    `LANGUAGE`      VARCHAR(10)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',       -- [国际化] 如: zh_CN, en_US
    `METADATA`      TEXT         COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                      -- [扩展] JSON格式，存储非结构化配置
    `VERSION`       VARCHAR(64)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',

    -- ==================================================================================================
    -- 🕒 5. 审计字段 (Audit Fields)
    -- ==================================================================================================
    `CREATED_AT`    DATETIME     DEFAULT NULL COMMENT '「createdAt」- 创建时间',                          -- [审计] 创建时间
    `CREATED_BY`    VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',        -- [审计] 创建人
    `UPDATED_AT`    DATETIME     DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                          -- [审计] 更新时间
    `UPDATED_BY`    VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',        -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_UI_COLUMN_SIGMA_CONTROL_ID_DATA_INDEX` (`SIGMA`, `CONTROL_ID`, `DATA_INDEX`) USING BTREE,
    KEY `IDXM_UI_COLUMN_SIGMA_CONTROL_ID` (`SIGMA`, `CONTROL_ID`) USING BTREE,
    KEY `IDXM_UI_COLUMN_DATA_INDEX_CONTROL_ID` (`DATA_INDEX`, `CONTROL_ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='列信息';

-- 缺失公共字段：
-- - VERSION (版本)
-- - TYPE (类型)