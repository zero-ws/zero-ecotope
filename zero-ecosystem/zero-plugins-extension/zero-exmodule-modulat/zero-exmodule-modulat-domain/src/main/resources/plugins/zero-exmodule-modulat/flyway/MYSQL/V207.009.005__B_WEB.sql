-- liquibase formatted sql

-- changeset Lang:b-web-1
/*
 * BLOCK 中的界面资源定义（按页面分）
 * - UI_LIST, UI_COLUMN（包括静态）
 * - UI_FORM, UI_FIELD（包括静态）
 * - UI_CONTROL（包括静态）
 * （管理端）
 */
DROP TABLE IF EXISTS B_WEB;
CREATE TABLE IF NOT EXISTS B_WEB
(
    `KEY`
    VARCHAR
(
    36
) COMMENT '「key」- 主键',
    `CODE` VARCHAR
(
    255
) COMMENT '「code」- 系统内部编码', -- TYPE + BLOCK_CODE
    `BLOCK_ID` VARCHAR
(
    36
) COMMENT '「blockId」- 所属模块ID',

    /*
     * 这部分的区分
     * - LIST, 列表资源
     * - FORM，表单资源
     * - CONTROL，通用资源
     */
    `TYPE` VARCHAR
(
    64
) COMMENT '「type」- 类型保留，单独区分',

    -- 只针对 code 字段
    `LIC_CONTENT` LONGTEXT COMMENT '「licContent」- 内容编码',
    `LIC_OP` LONGTEXT COMMENT '「licOp」- 界面操作',
    `LIC_MODULE` LONGTEXT COMMENT '「licModule」- 单独指定 X_MODULE 中的记录',
    `LIC_TPL` LONGTEXT COMMENT '「licTpl」- PAGE, LAYOUT, CONTROL 等记录',

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
-- changeset Lang:b-web-2
ALTER TABLE B_WEB
    ADD UNIQUE (`CODE`, `BLOCK_ID`);