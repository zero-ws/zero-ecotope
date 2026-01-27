-- liquibase formatted sql

-- changeset Lang:kp-dataset-1
-- 报表数据源表：KP_DATA_SET
DROP TABLE IF EXISTS KP_DATA_SET;
CREATE TABLE IF NOT EXISTS KP_DATA_SET
(
    `KEY`
    VARCHAR
(
    36
) COMMENT '「key」- 数据源主键',
    `NAME` VARCHAR
(
    255
) COMMENT '「name」- 数据源名称',
    `CODE` VARCHAR
(
    36
) COMMENT '「code」- 数据源编码',

    /*
     * 采集数据的相关配置
     * - dataQuery：数据查询信息
     * - dataSource：数据源，可以是表、视图、或其他
     * - dataConfig：配置信息
     * - dataField：数据字段
     * - dataComponent：数据组件，开组件忽略之前内容
     */
    `DATA_QUERY` LONGTEXT COMMENT '「dataQuery」- 数据查询配置',
    `DATA_SOURCE` LONGTEXT COMMENT '「dataSource」- 基于什么内容做报表',
    `DATA_CONFIG` LONGTEXT COMMENT '「dataConfig」- 数据查询过程中的配置',
    `DATA_FIELD` LONGTEXT COMMENT '「dataField」- 数据字段',
    `DATA_COMPONENT` LONGTEXT COMMENT '「dataComponent」- 数据查询组件',

    /*
     * - PRIMARY
     * - WORKFLOW
     * - HISTORY
     * - DYNAMIC ( X_SOURCE )，此时 sourceId / sourceConfig 才生效
     */
    `TYPE` VARCHAR
(
    255
) COMMENT '「type」- 数据源类型',
    `STATUS` VARCHAR
(
    255
) COMMENT '「status」- 数据源状态',
    `SOURCE_ID` VARCHAR
(
    36
) COMMENT '「sourceId」- 额外数据源',
    `SOURCE_CONFIG` LONGTEXT COMMENT '「sourceConfig」- 数据源相关配置',

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
