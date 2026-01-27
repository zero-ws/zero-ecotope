-- liquibase formatted sql

-- changeset Lang:my-tpl-1
-- 个人应用表：MY_TPL
DROP TABLE IF EXISTS MY_TPL;
CREATE TABLE IF NOT EXISTS MY_TPL
(
    `KEY`
    VARCHAR
(
    36
) COMMENT '「key」- 个人设置主键',

    -- 关联关系
    `TPL_ID` VARCHAR
(
    36
) COMMENT '「tplId」- 对应TPL的ID',
    `TPL_TYPE` VARCHAR
(
    36
) COMMENT '「tplType」- 对应TPL类型',

    `OWNER` VARCHAR
(
    36
) COMMENT '「owner」- 拥有者ID，我的 / 角色级',
    `OWNER_TYPE` VARCHAR
(
    5
) COMMENT '「ownerType」- ROLE 角色，USER 用户',
    -- 维度控制
    `TYPE` VARCHAR
(
    32
) COMMENT '「type」- 类型（默认全站）',
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
-- changeset Lang:my-tpl-2
ALTER TABLE MY_TPL
    ADD UNIQUE (`TYPE`, `TPL_TYPE`, `TPL_ID`, `OWNER`, `OWNER_TYPE`);