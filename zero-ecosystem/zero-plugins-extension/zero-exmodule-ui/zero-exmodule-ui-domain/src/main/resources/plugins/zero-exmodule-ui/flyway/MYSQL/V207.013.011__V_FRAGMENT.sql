DROP TABLE IF EXISTS `V_FRAGMENT`;
CREATE TABLE IF NOT EXISTS `V_FRAGMENT` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`              VARCHAR(36)  COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                       -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `BUTTON_CONNECT`  VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「buttonConnect」- 按钮的ID',    -- 按钮的ID（单按钮）
    `BUTTON_GROUP`    TEXT         COLLATE utf8mb4_bin COMMENT '「buttonGroup」- 一组按钮配置',
    `CONFIG`          TEXT         COLLATE utf8mb4_bin COMMENT '「config」- 根目录开始的基本配置',
    `CONTAINER`       TEXT         COLLATE utf8mb4_bin COMMENT '「container」- 容器专用格式',
    `GRID`            INT          DEFAULT 3 COMMENT '「grid」- grid选项',                                    -- grid选项（分区面板专用）
    `MODAL`           TEXT         COLLATE utf8mb4_bin COMMENT '「modal」- modal选项',                        -- modal选项，Model专用结构
    `NOTICE`          TEXT         COLLATE utf8mb4_bin COMMENT '「notice」- notice选项',                      -- notice选项，Alert结构

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='V_FRAGMENT';

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