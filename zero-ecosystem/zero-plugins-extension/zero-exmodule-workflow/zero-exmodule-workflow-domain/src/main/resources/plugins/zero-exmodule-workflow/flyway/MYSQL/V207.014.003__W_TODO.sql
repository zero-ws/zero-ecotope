DROP TABLE IF EXISTS `W_TODO`;
CREATE TABLE IF NOT EXISTS `W_TODO` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                    -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `ACCEPTED_AT`       DATETIME      DEFAULT NULL COMMENT '「acceptedAt」- 接收时间',
    `ACCEPTED_BY`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「acceptedBy」- 待办接收人',
    `ACCEPTED_GROUP`    LONGTEXT      COLLATE utf8mb4_bin COMMENT '「acceptedGroup」- 当前处理组',
    `ASSIGNED_AT`       DATETIME      DEFAULT NULL COMMENT '「assignedAt」- 指派时间',
    `ASSIGNED_BY`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「assignedBy」- 待办指派人',
    `CODE`              VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 编号',              -- 待办系统码，使用流程时候关联流程的任务ID
    `COMMENT`           LONGTEXT      COLLATE utf8mb4_bin COMMENT '「comment」- 备注',
    `COMMENT_APPROVAL`  LONGTEXT      COLLATE utf8mb4_bin COMMENT '「commentApproval」- 审批描述',
    `COMMENT_REJECT`    LONGTEXT      COLLATE utf8mb4_bin COMMENT '「commentReject」- 拒绝理由',
    `ESCALATE`          BIT(1)        DEFAULT NULL COMMENT '「escalate」- 是否升级',
    `ESCALATE_DATA`     LONGTEXT      COLLATE utf8mb4_bin COMMENT '「escalateData」- 升级单据数据',
    `EXPIRED_AT`        DATETIME      DEFAULT NULL COMMENT '「expiredAt」- 超时时间',
    `FINISHED_AT`       DATETIME      DEFAULT NULL COMMENT '「finishedAt」- 完成时间',
    `FINISHED_BY`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「finishedBy」- 待办完成人',
    `ICON`              VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「icon」- 图标',
    `NAME`              VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `PARENT_ID`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「parentId」- 父节点',        -- 待办支持父子集结构，父待办执行时候子待办同样执行
    `SERIAL`            VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「serial」- 单号',            -- 待办编号，使用 X_NUMBER 生成
    `SERIAL_FORK`       VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「serialFork」- 生成序号的分支序号',
    `TASK_ID`           VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「taskId」- 和待办绑定',      -- 和待办绑定的taskId（任务）
    `TASK_KEY`          VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「taskKey」- 和待办绑定',     -- 和待办绑定的taskKey
    `TODO_URL`          VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「todoUrl」- 待办路径',
    `TO_DEPT`           VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「toDept」- 指定部门',
    `TO_GROUP`          VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「toGroup」- 指定用户组',
    `TO_LOCATION`       VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「toLocation」- 指定地址区域',
    `TO_ROLE`           VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「toRole」- 待办角色（集体）',
    `TO_TEAM`           VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「toTeam」- 指定业务组',
    `TO_USER`           VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「toUser」- 待办指定人',
    `TRACE_ID`          VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「traceId」- 同一个流程',     -- 同一个流程的待办执行分组
    `TRACE_ORDER`       INTEGER       DEFAULT NULL COMMENT '「traceOrder」- 待办的处理顺序',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`              VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',              -- [类型],
    `STATUS`            VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「status」- 状态',
    `MODEL_ID`          VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelId」- 模型标识',       -- 关联的模型identifier，用于描述
    `MODEL_KEY`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelKey」- 模型记录ID',      -- 关联的模型记录ID，用于描述哪一个Model中的记录
    `MODEL_CATEGORY`    VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelCategory」- 模型类别', -- 关联的category记录，只包含叶节点

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`             VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',         -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`         VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',          -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`            VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',             -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`            BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',                            -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`          VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',      -- [国际化] 如: zh_CN, en_US,
    `METADATA`          TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                     -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`           VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`        DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',                         -- [审计] 创建时间
    `CREATED_BY`        VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',       -- [审计] 创建人
    `UPDATED_AT`        DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                         -- [审计] 更新时间
    `UPDATED_BY`        VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',       -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_W_TODO_SIGMA_CODE` (`SIGMA`, `CODE`) USING BTREE,
    UNIQUE KEY `UK_W_TODO_SIGMA_SERIAL` (`SIGMA`, `SERIAL`) USING BTREE,
    KEY `IDXM_W_TODO_SIGMA_STATUS` (`SIGMA`, `STATUS`) USING BTREE,
    KEY `IDXM_W_TODO_SIGMA_TYPE_STATUS` (`SIGMA`, `STATUS`, `TYPE`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='W_TODO';

-- 缺失公共字段：
-- - VERSION (版本)