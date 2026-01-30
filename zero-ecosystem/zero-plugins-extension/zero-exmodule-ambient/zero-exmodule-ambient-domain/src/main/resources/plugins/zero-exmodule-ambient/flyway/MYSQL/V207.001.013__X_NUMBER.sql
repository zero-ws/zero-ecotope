DROP TABLE IF EXISTS `X_NUMBER`;

CREATE TABLE IF NOT EXISTS `X_NUMBER` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`            VARCHAR(36)  NOT NULL COLLATE utf8mb4_bin COMMENT '「id」- 主键',                       -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `CODE`          VARCHAR(36)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「code」- 编号',
    `COMMENT`       VARCHAR(255) DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「comment」- 备注',                -- 描述编号数据, S_COMMENT
    `CURRENT`       BIGINT       NOT NULL COMMENT '「current」- 当前值',                                      -- 对应 {seed}，每次变化更新为 current +/- step, L_CURRENT
    `DECREMENT`     BIT(1)       NOT NULL DEFAULT b'0' COMMENT '「decrement」- 递减模式',                     -- True=递减, False=递增, IS_DECREMENT
    `FORMAT`        VARCHAR(255) DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「format」- 格式化串',              -- 格式信息，用于处理最终格式, S_FORMAT
    `IDENTIFIER`    VARCHAR(64)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「identifier」- 标识符',           -- 查询当前identifier使用的序号信息, S_IDENTIFIER
    `LENGTH`        INT          NOT NULL COMMENT '「length」- 长度',                                        -- 编号长度，不包含prefix和suffix部分, I_LENGTH
    `PREFIX`        VARCHAR(64)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「prefix」- 前缀',                 -- 编号前缀, S_PREFIX
    `RENEWAL`       BIT(1)       DEFAULT b'0' COMMENT '「renewal」- 是否循环',                                -- 是否循环生成
    `RUN_COMPONENT` TEXT         COLLATE utf8mb4_bin COMMENT '「runComponent」- 执行组件',                    -- 发号器执行组件，雪花算法所需
    `STEP`          INT          NOT NULL COMMENT '「step」- 步进系数',                                      -- 每次按照step进行变化, I_STEP
    `SUFFIX`        VARCHAR(64)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「suffix」- 后缀',                 -- 编号后缀, S_SUFFIX
    `TIME`          VARCHAR(20)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '「time」- 时间格式',               -- 对应 {time}：YYYY-MM-DD 用于生成时间部分, S_TIME

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`         VARCHAR(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',              -- [物理隔离] 核心分片键/顶层租户标识
    `TENANT_ID`     VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',             -- [业务隔离] SaaS 租户/具体公司标识
    `APP_ID`        VARCHAR(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',                -- [逻辑隔离] 区分同一租户下的不同应用
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`        BIT(1)       DEFAULT NULL COMMENT '「active」- 是否启用',                                 -- [状态] 1=启用/正常, 0=禁用/冻结
    `LANGUAGE`      VARCHAR(10)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',           -- [国际化] 如: zh_CN, en_US
    `METADATA`      TEXT         COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                          -- [扩展] JSON格式，存储非结构化配置
    `VERSION`       VARCHAR(64)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',

    -- ==================================================================================================
    -- 🕒 5. 审计字段 (Audit Fields)
    -- ==================================================================================================
    `CREATED_AT`    DATETIME     DEFAULT NULL COMMENT '「createdAt」- 创建时间',                              -- [审计] 创建时间
    `CREATED_BY`    VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',            -- [审计] 创建人
    `UPDATED_AT`    DATETIME     DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                              -- [审计] 更新时间
    `UPDATED_BY`    VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',            -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_X_NUMBER_APP_ID_IDENTIFIER_CODE` (`APP_ID`, `IDENTIFIER`, `CODE`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='X_NUMBER';


-- 缺失公共字段：
-- - VERSION (版本)
-- - TYPE (类型)