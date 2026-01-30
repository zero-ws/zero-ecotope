DROP TABLE IF EXISTS `W_TICKET`;
CREATE TABLE IF NOT EXISTS `W_TICKET` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                   VARCHAR(36)    COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `CANCEL_AT`            DATETIME       DEFAULT NULL COMMENT '「cancelAt」- 中断时间',
    `CANCEL_BY`            VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「cancelBy」- 中断人',
    `CATALOG`              VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「catalog」- 关联服务目录',
    `CATEGORY_SUB`         VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「categorySub」- 子类别',
    `CLOSE_AT`             DATETIME       DEFAULT NULL COMMENT '「closeAt」- 关闭时间',
    `CLOSE_BY`             VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「closeBy」- 关闭人',
    `CLOSE_CODE`           VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「closeCode」- 关闭代码',
    `CLOSE_KB`             VARCHAR(1024)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「closeKb」- 关闭时KB链接地址',
    `CLOSE_SOLUTION`       LONGTEXT       COLLATE utf8mb4_bin COMMENT '「closeSolution」- 关闭解决方案',
    `CODE`                 VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 编号',          -- 单据系统编号（内码）
    `DESCRIPTION`          LONGTEXT       COLLATE utf8mb4_bin COMMENT '「description」- 描述',
    `FLOW_DEFINITION_ID`   VARCHAR(64)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「flowDefinitionId」- 流程定义的ID', -- 流程定义的ID，getProcessDefinitionKey
    `FLOW_DEFINITION_KEY`  VARCHAR(64)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「flowDefinitionKey」- 流程定义的KEY', -- 流程定义的KEY, getProcessDefinitionKey
    `FLOW_END`             BIT(1)         DEFAULT NULL COMMENT '「flowEnd」- 主单是否执行完成',
    `FLOW_INSTANCE_ID`     VARCHAR(64)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「flowInstanceId」- 流程定义的ID', -- 流程定义的ID，getProcessId
    `NAME`                 VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `OPEN_AT`              DATETIME       DEFAULT NULL COMMENT '「openAt」- 开单时间',
    `OPEN_BY`              VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「openBy」- 开单人',
    `OPEN_GROUP`           VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「openGroup」- 开单组',
    `OWNER`                VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「owner」- 制单人/拥有者',
    `PHASE`                VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「phase」- 主单据所属阶段（', -- 主单据所属阶段（状态描述，由于挂TODO，所以不使用status）
    `QUANTITY`             INTEGER        DEFAULT NULL COMMENT '「quantity」- 数量信息',                      -- 数量信息，多个模型记录时统计模型总数
    `SERIAL`               VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「serial」- 单号',        -- 单据编号，使用 X_NUMBER 生成
    `SUPERVISOR`           VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「supervisor」- 监督人',
    `TITLE`                VARCHAR(1024)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「title」- 标题',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`                 VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',          -- [类型],
    `CATEGORY`             VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「category」- 类别',
    `MODEL_ID`             VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelId」- 模型标识',   -- 关联的模型identifier，用于描述
    `MODEL_KEY`            VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelKey」- 模型记录ID',  -- 关联的模型记录ID，用于描述哪一个Model中的记录
    `MODEL_CATEGORY`       VARCHAR(128)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelCategory」- 模型类别', -- 关联的category记录，只包含叶节点
    `MODEL_COMPONENT`      VARCHAR(255)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelComponent」- 模型组件',
    `MODEL_CHILD`          LONGTEXT       COLLATE utf8mb4_bin COMMENT '「modelChild」- 子模型集',             -- 关联多个模型的记录ID，JsonArray格式

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`                VARCHAR(128)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',     -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`            VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',      -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`               VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',         -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`               BIT(1)         DEFAULT NULL COMMENT '「active」- 是否启用',                        -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`             VARCHAR(10)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',  -- [国际化] 如: zh_CN, en_US,
    `METADATA`             TEXT           COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                 -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`              VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`           DATETIME       DEFAULT NULL COMMENT '「createdAt」- 创建时间',                     -- [审计] 创建时间
    `CREATED_BY`           VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',   -- [审计] 创建人
    `UPDATED_AT`           DATETIME       DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                     -- [审计] 更新时间
    `UPDATED_BY`           VARCHAR(36)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',   -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_W_TICKET_SIGMA_CODE` (`SIGMA`, `CODE`) USING BTREE,
    UNIQUE KEY `UK_W_TICKET_SIGMA_SERIAL` (`SIGMA`, `SERIAL`) USING BTREE,
    KEY `IDXM_W_TICKET_SIGMA_STATUS` (`SIGMA`, `PHASE`) USING BTREE,
    KEY `IDXM_W_TICKET_SIGMA_FLOW_DEFINITION_KEY` (`SIGMA`, `FLOW_DEFINITION_KEY`) USING BTREE,
    KEY `IDXM_W_TICKET_SIGMA_CATALOG` (`SIGMA`, `CATALOG`) USING BTREE,
    KEY `IDXM_W_TICKET_SIGMA_TYPE_STATUS` (`SIGMA`, `PHASE`, `TYPE`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='W_TICKET';

-- 缺失公共字段：
-- - VERSION (版本)