DROP TABLE IF EXISTS `V_QUERY`;
CREATE TABLE IF NOT EXISTS `V_QUERY` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`          VARCHAR(36)  COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                           -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `CRITERIA`    TEXT         COLLATE utf8mb4_bin COMMENT '「criteria」- query/cr',                          -- query/criteria:{}, 查询条件选项
    `PAGER`       TEXT         COLLATE utf8mb4_bin COMMENT '「pager」- query/pa',                             -- query/pager:{}, 分页选项
    `PROJECTION`  TEXT         COLLATE utf8mb4_bin COMMENT '「projection」- query/pr',                        -- query/projection:[], 默认列过滤项
    `SORTER`      TEXT         COLLATE utf8mb4_bin COMMENT '「sorter」- query/so',                            -- query/sorter:[], 排序选项

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='V_QUERY';

-- 缺失公共字段：
-- - CREATED_AT (创建时间)
-- - CREATED_BY (创建人)
-- - UPDATED_AT (更新时间)
-- - UPDATED_BY (更新人)
-- - ACTIVE (是否启用)
-- - LANGUAGE (语言)
-- - VERSION (版本)
-- - METADATA (元配置)
-- - SIGMA (统一标识)
-- - APP_ID (所属应用)
-- - TENANT_ID (所属租户)
-- - TYPE (类型)