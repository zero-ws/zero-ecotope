/*
 * 附件存储方式说明
 * - storeWay
 *   BLOB：直接存储在数据库中（二进制格式）
 *   FILE：上传到直接运行的服务器中，直接上传
 *   REMOTE：远程集成
 *
 * 只有当 storeWay = REMOTE 时会执行远程同步
 * - storeId：对应 KIntegration 中存储的相关集成配置信息
 * - storePath：远程存储文件的根地址，如：/root/txt 这种（不带协议和服务器部分）
 * - storeUri：远程存储文件转换的URI地址，主要用于网络访问
 */
DROP TABLE IF EXISTS `X_ATTACHMENT`;
CREATE TABLE IF NOT EXISTS `X_ATTACHMENT` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`              VARCHAR(36)    COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                     -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `DIRECTORY_ID`    VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「directoryId」- 存储目录',
    `EXTENSION`       VARCHAR(10)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「extension」- 文件扩展名',
    `FILE_KEY`        VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「fileKey」- 文件下载Key',       -- TPL模式中的文件唯一的key（全局唯一）
    `FILE_NAME`       VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「fileName」- 原始文件名',     -- 原始文件名（不带扩展名）
    `FILE_PATH`       VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「filePath」- 文件存储地址',   -- 该文件的存储地址，FILE时使用
    `FILE_URL`        VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「fileUrl」- 文件下载链接',    -- 该文件的下载链接（全局唯一）
    `MIME`            VARCHAR(128)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「mime」- MIME类型',
    `NAME`            VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 附件名称',           -- 文件名（带扩展名）
    `SIZE`            INTEGER        DEFAULT NULL COMMENT '「size」- 附件尺寸',
    `STORE_PATH`      VARCHAR(1024)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「storePath」- 存储路径',      -- 远程存储的目录信息（显示专用，去服务器和协议部分）
    `STORE_URI`       VARCHAR(1024)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「storeUri」- 存储URI',       -- 远程存储的目录URI部分
    `STORE_WAY`       VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「storeWay」- 存储方式',       -- 存储方式，BLOB / FILE / REMOTE

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`            VARCHAR(128)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 附件类型',            -- [类型],
    `STATUS`          VARCHAR(12)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「status」- 附件状态',          -- 状态，PROGRESS / SUCCESS
    `MODEL_ID`        VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelId」- 模型标识',         -- 关联的模型identifier，用于描述
    `MODEL_KEY`       VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelKey」- 模型记录ID',        -- 关联的模型记录ID，用于描述哪一个Model中的记录
    `MODEL_CATEGORY`  VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelCategory」- 模型类别',  -- 如果一个模型记录包含多种附件，则需要设置模型相关字段，等价于 field

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`           VARCHAR(128)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',          -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`       VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',           -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`          VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',              -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`          BIT(1)         DEFAULT NULL COMMENT '「active」- 是否启用',                             -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`        VARCHAR(10)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',       -- [国际化] 如: zh_CN, en_US,
    `METADATA`        TEXT           COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                      -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`         VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`      DATETIME       DEFAULT NULL COMMENT '「createdAt」- 创建时间',                          -- [审计] 创建时间
    `CREATED_BY`      VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',        -- [审计] 创建人
    `UPDATED_AT`      DATETIME       DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                          -- [审计] 更新时间
    `UPDATED_BY`      VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',        -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_X_ATTACHMENT_FILE_KEY` (`FILE_KEY`) USING BTREE,
    UNIQUE KEY `UK_X_ATTACHMENT_FILE_URL` (`FILE_URL`) USING BTREE,
    UNIQUE KEY `UK_X_ATTACHMENT_FILE_PATH` (`FILE_PATH`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='X_ATTACHMENT';

-- 缺失公共字段：
-- - VERSION (版本)