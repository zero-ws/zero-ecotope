DROP TABLE IF EXISTS `V_TABLE`;

CREATE TABLE IF NOT EXISTS `V_TABLE` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                VARCHAR(36)  NOT NULL COLLATE utf8mb4_bin COMMENT '「id」- 主键',                    -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `BORDERED`          BIT(1)       DEFAULT NULL COMMENT '「bordered」- 是否带表框',
    `CLASS_NAME`        VARCHAR(128) DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「className」- CSS属性',
    `SIZE`              VARCHAR(16)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「size」- 表格尺寸',

    -- 操作列配置 (Operation Column)
    `OP_CONFIG`         TEXT         COLLATE utf8mb4_bin COMMENT '「opConfig」- 执行配置',                   -- columns/[0]/$option, 执行类对应配置，配置按钮
    `OP_DATA_INDEX`     VARCHAR(255) DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「opDataIndex」- 执行列索引',    -- columns/[0]/dataIndex, 执行列标题
    `OP_FIXED`          BIT(1)       DEFAULT b'0' COMMENT '「opFixed」- 是否固定',                           -- columns/[0]/fixed，执行列左还是右
    `OP_TITLE`          VARCHAR(255) DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「opTitle」- 执行列标题',        -- columns/[0]/title, 执行列标题

    -- 行事件配置 (Row Events)
    `ROW_CLICK`         VARCHAR(64)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「rowClick」- 单击事件',         -- row.onClick - 单击事件名
    `ROW_CONTEXT_MENU`  VARCHAR(64)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「rowContextMenu」- 右键菜单',   -- row.onContextMenu - 右键菜单事件名
    `ROW_DOUBLE_CLICK`  VARCHAR(64)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「rowDoubleClick」- 双击事件',   -- row.onDoubleClick - 双击事件名
    `ROW_MOUSE_ENTER`   VARCHAR(64)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「rowMouseEnter」- 鼠标移入',    -- row.onMouseEnter - 鼠标左键事件名
    `ROW_MOUSE_LEAVE`   VARCHAR(64)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「rowMouseLeave」- 鼠标移出',    -- row.onMouseLeave - 鼠标移开事件名

    -- 统计配置 (Total)
    `TOTAL_REPORT`      VARCHAR(128) DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「totalReport」- 统计文案',      -- total.report - 文字: 总共多少条统计
    `TOTAL_SELECTED`    VARCHAR(128) DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「totalSelected」- 选中文案',    -- total.selected - 文字: 选择了多少条

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='V_TABLE';

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