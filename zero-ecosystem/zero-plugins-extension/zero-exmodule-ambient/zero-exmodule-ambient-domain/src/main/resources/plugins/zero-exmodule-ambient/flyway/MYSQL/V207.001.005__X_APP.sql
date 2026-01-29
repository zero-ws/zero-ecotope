DROP TABLE IF EXISTS `X_APP`;
CREATE TABLE IF NOT EXISTS `X_APP` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`          VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                          -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `APP_KEY`     VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appKey」- 敏感标识符',           -- 应用程序专用唯一hashKey
    `APP_SECRET`  VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appSecret」- 应用密钥',
    `CODE`        VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 编号',
    `CONTEXT`     VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「configure」- 应用路径',
    `COPY_RIGHT`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「copyRight」- 版权',             -- CopyRight版权信息
    `DOMAIN`      VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「domain」- 服务器域',
    `EMAIL`       VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「email」- 管理员Email',
    `ENDPOINT`    VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「endpoint」- Web服务端地址',      -- 后端API的根路径，启动时需要
    `ENTRY`       VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「entry」- 入口菜单',              -- App 关联的入口菜单
    `ICP`         VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「icp」- ICP备案号',
    `LOGO`        LONGTEXT      COLLATE utf8mb4_bin COMMENT '「logo」- 图标',
    `NAME`        VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `PORT`        INTEGER       DEFAULT NULL COMMENT '「port」- 端口号',                                     -- 应用程序端口号，和SOURCE的端口号区别开
    `TITLE`       VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「title」- 应用标题',
    `URL_ADMIN`   VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「urlAdmin」- 管理业URL',          -- 应用程序内置主页（带安全）
    `URL_LOGIN`   VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「urlLogin」— 登录页URL',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `STATUS`      VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「status」- 状态',

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`       VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',               -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`   VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',                -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`      VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 父应用ID',               -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`      BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                                  -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`    VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',            -- [国际化] 如: zh_CN, en_US,
    `METADATA`    TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                           -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`     VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`  DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',                               -- [审计] 创建时间
    `CREATED_BY`  VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',             -- [审计] 创建人
    `UPDATED_AT`  DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                               -- [审计] 更新时间
    `UPDATED_BY`  VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',             -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_X_APP_CODE` (`CODE`) USING BTREE,
    UNIQUE KEY `UK_X_APP_CONTEXT_URL_LOGIN` (`CONTEXT`, `URL_LOGIN`) USING BTREE,
    UNIQUE KEY `UK_X_APP_CONTEXT_URL_ADMIN` (`CONTEXT`, `URL_ADMIN`) USING BTREE,
    UNIQUE KEY `UK_X_APP_NAME` (`NAME`) USING BTREE,
    KEY `IDX_X_APP_NAME` (`NAME`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='X_APP';

-- 缺失公共字段：
-- - VERSION (版本)
-- - TYPE (类型)