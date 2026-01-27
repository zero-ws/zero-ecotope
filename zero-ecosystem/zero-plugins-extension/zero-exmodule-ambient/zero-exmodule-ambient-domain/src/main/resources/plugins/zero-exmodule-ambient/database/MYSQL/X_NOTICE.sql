-- liquibase formatted sql

-- changeset Lang:x-notice-1
-- 公告模块：X_NOTICE
DROP TABLE IF EXISTS X_NOTICE;
CREATE TABLE IF NOT EXISTS X_NOTICE
(
    `KEY`
    VARCHAR
(
    36
) COMMENT '「key」- 公告主键',
    `NAME` VARCHAR
(
    255
) COMMENT '「name」- 公告标题',
    `CODE` VARCHAR
(
    255
) COMMENT '「code」- 公告编码',
    `TYPE` VARCHAR
(
    255
) COMMENT '「type」- 公告类型',

    `STATUS` VARCHAR
(
    255
) COMMENT '「status」- 公告状态',
    `CONTENT` LONGTEXT COMMENT '「content」- 公告内容',
    `EXPIRED_AT` DATETIME COMMENT '「createdAt」- 公告到期时间',

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

-- changeset Lang:x-notice-2
ALTER TABLE X_NOTICE
    ADD UNIQUE (`APP_ID`, `CODE`); -- 模板名称/编码
ALTER TABLE X_NOTICE
    ADD UNIQUE (`APP_ID`, `NAME`);