-- liquibase formatted sql

-- changeset Lang:kp-report-instance-1
-- 报表主表：KP_REPORT_INSTANCE
DROP TABLE IF EXISTS KP_REPORT_INSTANCE;
CREATE TABLE IF NOT EXISTS KP_REPORT_INSTANCE
(
    `KEY`
    VARCHAR
(
    36
) COMMENT '「key」- 报表主键',
    `NAME` VARCHAR
(
    255
) COMMENT '「name」- 报表名称',
    `STATUS` VARCHAR
(
    255
) COMMENT '「status」- 报表状态',
    `TYPE` VARCHAR
(
    255
) COMMENT '「type」- 报表类型',

    `TITLE` VARCHAR
(
    255
) COMMENT '「title」- 报表标题',
    `SUBTITLE` VARCHAR
(
    255
) COMMENT '「subtitle」- 副标题', -- region
    `EXTRA` VARCHAR
(
    255
) COMMENT '「extra」- 额外信息', -- extra
    `DESCRIPTION` LONGTEXT COMMENT '「description」- 报表描述',

    `MODE_EXPR` VARCHAR
(
    255
) COMMENT '「modeExpr」- 表达式（和 type 绑定）',

    -- 关联报表ID
    /*
     * 此处 reportContent / reportData 的区别
     * - reportContent 报表最终内容，用于呈现的最终数据
     * - reportData    报表原始数据，用于生成报表的原始数据
     */
    `REPORT_ID` VARCHAR
(
    36
) COMMENT '「reportId」- 关联报表ID',
    `REPORT_CONTENT` LONGTEXT COMMENT '「reportContent」- 报表内容',
    `REPORT_DATA` LONGTEXT COMMENT '「reportData」- 报表最终数据',

    `REPORT_BY` VARCHAR
(
    36
) COMMENT '「reportBy」- 报表人',
    `REPORT_AT` DATETIME COMMENT '「reportAt」- 报表生成时间',

    -- 报表归属（反向广义关联）
    `REF_TYPE` VARCHAR
(
    255
) COMMENT '「refType」- 关联类型',
    `REF_ID` VARCHAR
(
    36
) COMMENT '「refId」- 关联ID',

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
-- changeset Lang:kp-report-instance-2
-- Unique Key: 独立唯一键定义
ALTER TABLE KP_REPORT_INSTANCE
    ADD UNIQUE (`REPORT_ID`, `NAME`) USING BTREE;