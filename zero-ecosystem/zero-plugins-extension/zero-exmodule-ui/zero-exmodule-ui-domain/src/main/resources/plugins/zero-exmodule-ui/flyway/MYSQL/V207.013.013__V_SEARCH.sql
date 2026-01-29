DROP TABLE IF EXISTS `V_SEARCH`;
CREATE TABLE IF NOT EXISTS `V_SEARCH` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`               VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                     -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `ADVANCED`         BIT(1)        DEFAULT NULL COMMENT '「advanced」- search.a',                           -- search.advanced: 是否启用高级搜索
    `ADVANCED_NOTICE`  TEXT          COLLATE utf8mb4_bin COMMENT '「advancedNotice」- search.a',              -- search.advanced.notice: 提示信息结构（Alert）
    `ADVANCED_TITLE`   VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「advancedTitle」- search.a',  -- search.advanced.title: 高级搜索窗口标题
    `ADVANCED_VIEW`    TEXT          COLLATE utf8mb4_bin COMMENT '「viewOption」- search.c',                  -- search.criteria.xxx：视图选项信息
    `ADVANCED_WIDTH`   VARCHAR(100)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「advancedWidth」- search.a',  -- search.advanced.width: 高级搜索窗口宽度
    `COND`             TEXT          COLLATE utf8mb4_bin COMMENT '「cond」- search.c',                        -- search.cond: 搜索条件
    `CONFIRM_CLEAR`    VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「confirmClear」- search.c',   -- search.confirm.clear: 清除条件提示
    `ENABLED`          BIT(1)        DEFAULT NULL COMMENT '「enabled」- search.e',                            -- search.enabled: 是否启用搜索
    `OP_ADVANCED`      VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「opAdvanced」- search.o',     -- search.op.advanced: 高级搜索按钮提示文字
    `OP_REDO`          VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「opRedo」- search.o',         -- search.op.redo: 清除条件按钮提示文字
    `OP_VIEW`          VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「opView」- search.o',         -- search.op.view：视图查询条件修改文字
    `PLACEHOLDER`      VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「placeholder」- search.p',    -- search.placeholder: 搜索框水印文字

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='V_SEARCH';

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