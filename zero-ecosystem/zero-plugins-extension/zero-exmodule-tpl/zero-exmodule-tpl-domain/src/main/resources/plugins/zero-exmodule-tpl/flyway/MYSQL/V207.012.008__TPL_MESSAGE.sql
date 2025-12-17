-- liquibase formatted sql

-- changeset Lang:tpl-message-1
-- 邮件模板：TPL_MESSAGE
DROP TABLE IF EXISTS TPL_MESSAGE;
CREATE TABLE IF NOT EXISTS TPL_MESSAGE
(
    `KEY`            VARCHAR(36) COMMENT '「key」- 模板唯一主键',
    `NAME`           VARCHAR(255) COMMENT '「name」- 模板名称',
    `CODE`           VARCHAR(36) COMMENT '「code」- 模板编码',
    `TYPE`           VARCHAR(255) COMMENT '「type」- 模板类型',

    -- 模板内容定义
    `EXPR_SUBJECT`   TEXT COMMENT '「exprSubject」- 模板标题，支持表达式',
    `EXPR_CONTENT`   LONGTEXT COMMENT '「exprContent」- 模板内容，支持表达式',
    `EXPR_COMPONENT` VARCHAR(255) COMMENT '「exprComponent」- 模板扩展处理程序，Java类名',

    -- ------------------------------ 公共字段 --------------------------------
    `SIGMA`          VARCHAR(128) COMMENT '「sigma」- 用户组绑定的统一标识',
    `LANGUAGE`       VARCHAR(10) COMMENT '「language」- 使用的语言',
    `ACTIVE`         BIT COMMENT '「active」- 是否启用',
    `METADATA`       TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT`     DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`     VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`     DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`     VARCHAR(36) COMMENT '「updatedBy」- 更新人',

    `APP_ID`         VARCHAR(36) COMMENT '「appId」- 应用ID',
    `TENANT_ID`      VARCHAR(36) COMMENT '「tenantId」- 租户ID',
    PRIMARY KEY (`KEY`) USING BTREE
);
-- changeset Lang:tpl-message-2
ALTER TABLE TPL_MESSAGE
    ADD UNIQUE (`APP_ID`, `CODE`); -- 模板名称/编码
ALTER TABLE TPL_MESSAGE
    ADD UNIQUE (`APP_ID`, `NAME`);