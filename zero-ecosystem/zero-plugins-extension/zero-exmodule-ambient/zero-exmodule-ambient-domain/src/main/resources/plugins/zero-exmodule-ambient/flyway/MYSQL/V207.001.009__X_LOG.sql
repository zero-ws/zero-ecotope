DROP TABLE IF EXISTS `X_LOG`;
CREATE TABLE IF NOT EXISTS `X_LOG` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`             VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                       -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `INFO_AT`        DATETIME      DEFAULT NULL COMMENT '「infoAt」- 记录时间',
    `INFO_READABLE`  TEXT          COLLATE utf8mb4_bin COMMENT '「infoReadable」- 业务日志',
    `INFO_STACK`     TEXT          COLLATE utf8mb4_bin COMMENT '「infoStack」- 堆栈内容',
    `INFO_SYSTEM`    TEXT          COLLATE utf8mb4_bin COMMENT '「infoSystem」- 系统日志',
    `LEVEL`          VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「level」- 级别',               -- 日志级别：ERROR / WARN / INFO
    `LOG_AGENT`      VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「logAgent」- 记录组件',        -- 记录日志的 agent 信息
    `LOG_IP`         VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「logIp」- 日志源IP',
    `LOG_USER`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「logUser」- 记录人',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`           VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',                 -- [类型],

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
    KEY `IDXM_X_LOG_SIGMA_TYPE` (`SIGMA`, `TYPE`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='X_LOG';

-- 缺失公共字段：
-- - VERSION (版本)