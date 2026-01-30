-- liquibase formatted sql

-- changeset Lang:my-favor-1
-- 个人收藏夹：MY_FAVOR
DROP TABLE IF EXISTS MY_FAVOR;
CREATE TABLE IF NOT EXISTS MY_FAVOR
(
    `KEY`
    VARCHAR
(
    36
) COMMENT '「key」- 个人应用主键',
    `OWNER` VARCHAR
(
    36
) COMMENT '「owner」- 拥有者ID，我的 / 角色级',
    `OWNER_TYPE` VARCHAR
(
    5
) COMMENT '「ownerType」- ROLE 角色，USER 用户',

    -- UI定制
    `UI_SORT` BIGINT COMMENT '「uiSort」- 模块排序',

    -- 维度控制
    `TYPE` VARCHAR
(
    32
) COMMENT '「type」- 类型（默认全站）',
    `POSITION` VARCHAR
(
    16
) COMMENT '「position」- 位置（默认左侧）',

    -- 参数控制：URI 和 URI_PARAM 可直接反向推导 URI_FULL
    `URI_KEY` VARCHAR
(
    36
) COMMENT '「uriKey」- URI KEY（加密长度）',
    `URI_FULL` LONGTEXT COMMENT '「uriFull」- 收藏完整链接地址（带参数部分）',
    `URI` LONGTEXT COMMENT '「uri」- 收藏地址',
    `URI_PARAM` LONGTEXT COMMENT '「uriParam」- 该收藏参数（收藏夹专用）',

    -- ------------------------------ 公共字段 --------------------------------
    `SIGMA` VARCHAR
(
    128
) COMMENT '「sigma」- 用户组绑定的统一标识',
    `LANGUAGE` VARCHAR
(
    10
) COMMENT '「language」- 使用的语言',
    `ACTIVE` BIT COMMENT '「active」- 是否启用',
    `METADATA` TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT` DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY` VARCHAR
(
    36
) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT` DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY` VARCHAR
(
    36
) COMMENT '「updatedBy」- 更新人',

    `APP_ID` VARCHAR
(
    36
) COMMENT '「appId」- 应用ID',
    `TENANT_ID` VARCHAR
(
    36
) COMMENT '「tenantId」- 租户ID',
    PRIMARY KEY
(
    `KEY`
) USING BTREE
    );

-- changeset Lang:my-favor-2
ALTER TABLE MY_FAVOR
    ADD UNIQUE (`OWNER_TYPE`, `OWNER`, `TYPE`, `POSITION`, `URI_KEY`);