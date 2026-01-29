DROP TABLE IF EXISTS `E_ASSET`;
CREATE TABLE IF NOT EXISTS `E_ASSET` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`               VARCHAR(36)     COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',                   -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `ACCOUNT_AT`       DATETIME        DEFAULT NULL COMMENT '「accountAt」- 入账时间',
    `ACCOUNT_BY`       VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「accountBy」- 入账人',
    `CODE`             VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 编号',
    `COMMENT`          TEXT            COLLATE utf8mb4_bin COMMENT '「comment」- 备注',
    `COMPANY_ID`       VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「companyId」- 所属公司',
    `CUSTOMER_ID`      VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「customerId」- 供应商ID',
    `DEPT_ID`          VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「deptId」- 所属部门',
    `ENTER_AT`         DATETIME        DEFAULT NULL COMMENT '「enterAt」- 入库时间',
    `ENTER_BY`         VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「enterBy」- 入库人',
    `EXPIRED_AT`       DATETIME        DEFAULT NULL COMMENT '「expiredAt」- 到期时间',
    `EXPIRED_COMMENT`  TEXT            COLLATE utf8mb4_bin COMMENT '「expiredComment」- 到期说明',
    `K_ASSIGNMENT`     VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「kAssignment」- 折旧费用分配科目',
    `K_CHANGE`         VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「kChange」- 资产变动对方科目',
    `K_DEPRECATED`     VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「kDeprecated」- 累积折旧科目',
    `K_DEVALUE`        VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「kDevalue」- 减值准备科目',
    `K_FIXED`          VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「kFixed」- 固定资产科目',
    `K_TAX`            VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「kTax」- 税金科目',
    `MODEL_NUMBER`     VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelNumber」- 规格型号',
    `NAME`             VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「name」- 名称',
    `NUM`              BIGINT          DEFAULT NULL COMMENT '「num」- 资产数量',
    `NUM_DEPRECATED`   BIGINT          DEFAULT NULL COMMENT '「numDeprecated」- 已折旧数量',
    `NUM_DEPRECATING`  BIGINT          DEFAULT NULL COMMENT '「numDeprecating」- 预计折旧数量',
    `NUM_USED`         BIGINT          DEFAULT NULL COMMENT '「numUsed」- 已使用数量',
    `NUM_USING`        BIGINT          DEFAULT NULL COMMENT '「numUsing」- 预计使用数量',
    `PARENT_ID`        VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「parentId」- 父节点',
    `SCRAP_AT`         DATETIME        DEFAULT NULL COMMENT '「scrapAt」- 报废时间',
    `SCRAP_BY`         VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「scrapBy」- 报废人',
    `STORE_ID`         VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「storeId」- 所属仓库ID',
    `UNIT`             VARCHAR(64)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「unit」- 计量单位',
    `USED_AT`          DATETIME        DEFAULT NULL COMMENT '「usedAt」- 开始使用时间',
    `USED_BY`          VARCHAR(64)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「usedBy」- 使用者',
    `USED_STATUS`      VARCHAR(255)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「usedStatus」- 使用状态',
    `USER_ID`          VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「userId」- 资产管理者',
    `V_DEPRECATED_A`   DECIMAL(18, 2)  DEFAULT NULL COMMENT '「vDeprecatedA」- 累积折旧',
    `V_DEPRECATED_M`   DECIMAL(18, 2)  DEFAULT NULL COMMENT '「vDeprecatedM」- 月折旧',
    `V_DE_READY`       DECIMAL(18, 2)  DEFAULT NULL COMMENT '「vDeReady」- 减值准备',
    `V_NET`            DECIMAL(18, 2)  DEFAULT NULL COMMENT '「vNet」- 净值',
    `V_NET_AMOUNT`     DECIMAL(18, 2)  DEFAULT NULL COMMENT '「vNetAmount」- 净额',
    `V_NET_JUNK`       DECIMAL(18, 2)  DEFAULT NULL COMMENT '「vNetJunk」- 净残值',
    `V_ORIGINAL`       DECIMAL(18, 2)  DEFAULT NULL COMMENT '「vOriginal」- 原价值',
    `V_TAX`            DECIMAL(18, 2)  DEFAULT NULL COMMENT '「vTax」- 税额',
    `WAY_ACCORDING`    VARCHAR(64)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「wayAccording」- 折旧依据',
    `WAY_CHANGE`       VARCHAR(64)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「wayChange」- 变动方式',
    `WAY_DEPRECATE`    VARCHAR(64)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「wayDeprecate」- 折旧方式',

    -- ==================================================================================================
    -- 🧩 3. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`             VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 类型',             -- [类型],
    `STATUS`           VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「status」- 状态',

    -- ==================================================================================================
    -- ☁️ 4. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`            VARCHAR(128)    COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',        -- [物理隔离] 核心分片键/顶层租户标识,
    `TENANT_ID`        VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',         -- [业务隔离] SaaS 租户/具体公司标识,
    `APP_ID`           VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',            -- [逻辑隔离] 区分同一租户下的不同应用,
    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`           BIT(1)          DEFAULT NULL COMMENT '「active」- 是否启用',                           -- [状态] 1=启用/正常, 0=禁用/冻结,
    `LANGUAGE`         VARCHAR(10)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',     -- [国际化] 如: zh_CN, en_US,
    `METADATA`         TEXT            COLLATE utf8mb4_bin COMMENT '「metadata」- 元配置',                    -- [扩展] JSON格式，存储非结构化配置,
    `VERSION`          VARCHAR(64)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 版本号',
    -- ==================================================================================================
    `CREATED_AT`       DATETIME        DEFAULT NULL COMMENT '「createdAt」- 创建时间',                        -- [审计] 创建时间
    `CREATED_BY`       VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',      -- [审计] 创建人
    `UPDATED_AT`       DATETIME        DEFAULT NULL COMMENT '「updatedAt」- 更新时间',                        -- [审计] 更新时间
    `UPDATED_BY`       VARCHAR(36)     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',      -- [审计] 更新人

    -- ==================================================================================================
    -- ⚡ 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_E_ASSET_NAME_SIGMA` (`NAME`, `SIGMA`) USING BTREE,
    UNIQUE KEY `UK_E_ASSET_CODE_SIGMA` (`CODE`, `SIGMA`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='E_ASSET';

-- 缺失公共字段：
-- - VERSION (版本)