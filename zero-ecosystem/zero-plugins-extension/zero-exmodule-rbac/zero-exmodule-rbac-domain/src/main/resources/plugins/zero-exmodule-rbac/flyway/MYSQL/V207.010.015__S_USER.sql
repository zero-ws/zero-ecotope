DROP TABLE IF EXISTS `S_USER`;
CREATE TABLE `S_USER` (
    -- ==================================================================================================
    -- 🆔 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`            VARCHAR(36)  COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 账号ID',                   -- [主键] 采用 Snowflake/UUID，避开自增ID

    -- ==================================================================================================
    -- 👤 2. 账号基础信息区 (Human Profile)
    -- ==================================================================================================
    `CODE`          VARCHAR(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「code」- 业务代码',            -- [标识] 业务编码/工号 (通常用于ERP对接)
    `USERNAME`      VARCHAR(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「username」- 登录账号',        -- [鉴权] 系统登录凭证 (唯一性由 Username+Sigma 决定)
    `PASSWORD`      VARCHAR(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「password」- 登录密码',        -- [安全] 禁止明文，建议使用 BCrypt/Argon2 Hash
    `REALNAME`      VARCHAR(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「realname」- 真实姓名',        -- [实名] 用于合同、审批等正式场景
    `ALIAS`         VARCHAR(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「alias」- 用户昵称',           -- [显示] 用于社交、评论等非正式场景
    `AVATAR`        VARCHAR(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「avatar」- 用户头像',          -- [资源] 头像 URL 或 OSS Key
    `DESCRIPTION`   TEXT         COLLATE utf8mb4_bin              COMMENT '「description」- 备注',        -- [备注] 内部管理备注/描述信息

    -- ==================================================================================================
    -- 🔗 3. 多账号登录模型 (Account Binding / Federation)
    -- ==================================================================================================
    -- [基础与支付]
    `MOBILE`        VARCHAR(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「mobile」- 手机号码',          -- [基础] 用于 SMS 验证码登录及找回密码
    `EMAIL`         VARCHAR(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「email」- 电子邮箱',           -- [基础] 用于邮件激活链接登录及通知
    `ALIPAY`        VARCHAR(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「alipay」- 支付宝',            -- [支付] 支付宝账号绑定
    
    -- [企业集成]
    `LDAP_ID`       VARCHAR(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「ldapId」- 域账号',            -- [LDAP] 域账号 UID (sAMAccountName)
    `LDAP_PATH`     VARCHAR(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「ldapPath」- LDAP路径',        -- [LDAP] 完整 DN (Distinguished Name)
    `LDAP_MAIL`     VARCHAR(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「ldapMail」- 域邮箱',          -- [LDAP] 域邮箱地址
    
    -- [微信生态]
    `WE_ID`         VARCHAR(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「weId」- 微信号',               -- [微信] 仅记录(API无法获取)，不可自动鉴权
    `WE_OPEN`       VARCHAR(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「weOpen」- 微信OpenId',        -- [微信] 应用级唯一 (App/小程序)
    `WE_UNION`      VARCHAR(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「weUnion」- 微信UnionId',      -- [微信] 开放平台级唯一 (打通账号关键)
    
    -- [企微生态]
    `CP_ID`         VARCHAR(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「cpId」- 企微UserID',          -- [企微] 企业内部员工ID (Corp内唯一)
    `CP_OPEN`       VARCHAR(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「cpOpen」- 企微OpenId',        -- [企微] 服务商维度ID
    `CP_UNION`      VARCHAR(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「cpUnion」- 企微UnionId',      -- [企微] 跨应用维度ID

    -- ==================================================================================================
    -- 🧩 4. 模型关联与多态 (Polymorphic Associations)
    -- ==================================================================================================
    `TYPE`          VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「type」- 账号类型',            -- [类型] 技术类型 (如: USER, SERVICE, BOT)
    `CATEGORY`      VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「category」- 账号分类',        -- [分类] 业务分类 (如: 内部员工, 外部客户)
    `MODEL_ID`      VARCHAR(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelId」- 关联模型',         -- [多态] Soft FK，如: 'hr.employee'
    `MODEL_KEY`     VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「modelKey」- 模型记录ID',        -- [多态] Soft FK，如: 'EMP-001'

    -- ==================================================================================================
    -- ☁️ 5. 多租户与上下文属性 (Multi-Tenancy & Context)
    -- ==================================================================================================
    `SIGMA`         VARCHAR(32)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「sigma」- 统一标识',           -- [物理隔离] 核心分片键/顶层租户标识
    `APP_ID`        VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「appId」- 应用ID',             -- [逻辑隔离] 区分同一租户下的不同应用
    `TENANT_ID`     VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 租户ID',          -- [业务隔离] SaaS 租户/具体公司标识
    `DIRECTORY_ID`  VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「directoryId」- 目录ID',       -- [身份源] 关联的目录服务配置ID (AD/LDAP源)
    -- --------------------------------------------------------------------------------------------------
    `LANGUAGE`      VARCHAR(10)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',        -- [国际化] 如: zh_CN, en_US
    `ACTIVE`        BIT(1)                           DEFAULT NULL COMMENT '「active」- 是否启用',          -- [状态] 1=启用/正常, 0=禁用/冻结
    `METADATA`      TEXT         COLLATE utf8mb4_bin              COMMENT '「metadata」- 扩展配置',        -- [扩展] JSON格式，存储非结构化偏好

    -- ==================================================================================================
    -- ⏱️ 6. 审计字段 (Audit Trail)
    -- ==================================================================================================
    `CREATED_AT`    DATETIME                         DEFAULT NULL COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`    VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`    DATETIME                         DEFAULT NULL COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`    VARCHAR(36)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',

    -- ==================================================================================================
    -- ⚡ 7. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_USERNAME_SIGMA` (`USERNAME`, `SIGMA`) USING BTREE,    -- [约束] 同一物理环境下用户名唯一
    UNIQUE KEY `UK_USERNAME_APP_ID` (`USERNAME`, `APP_ID`) USING BTREE,  -- [约束] 同一应用下用户名唯一 (可选)
    KEY `IDX_S_USER_USERNAME` (`USERNAME`) USING BTREE,                  -- [查询] 登录高频查询
    KEY `IDX_S_USER_CODE` (`CODE`) USING BTREE,                          -- [查询] 业务代码查询
    KEY `IDX_S_USER_MODEL` (`MODEL_KEY`, `MODEL_ID`) USING BTREE,        -- [查询] 反查员工/会员关联
    KEY `IDX_S_USER_MOBILE` (`MOBILE`) USING BTREE,                      -- [查询] 手机号登录/找回
    KEY `IDX_S_USER_WE_UNION` (`WE_UNION`) USING BTREE                   -- [查询] 微信扫码/自动登录

) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='账号';