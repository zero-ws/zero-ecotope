-- liquibase formatted sql

-- changeset Lang:kp-dimension-1
-- 报表维度表：KP_DIMENSION
DROP TABLE IF EXISTS KP_DIMENSION;
CREATE TABLE IF NOT EXISTS KP_DIMENSION
(
    `KEY`          VARCHAR(36) COMMENT '「key」- 维度主键',
    `NAME`         VARCHAR(255) COMMENT '「name」- 维度名称',
    `CODE`         VARCHAR(255) COMMENT '「code」- 维度代码',
    `TYPE`         VARCHAR(255) COMMENT '「type」- 维度类型',
    `STATUS`       VARCHAR(255) COMMENT '「status」- 维度状态',

    -- 提取数据
    `DATA_SET_ID`  VARCHAR(36) COMMENT '「dataSetId」- 数据源ID',
    `DATA_QUERY`   LONGTEXT COMMENT '「dataQuery」- 数据查询配置',
    `DATA_SORT`    LONGTEXT COMMENT '「dataSort」- 排序维度处理',
    `DATA_GROUP`   LONGTEXT COMMENT '「dataGroup」- 分组维度处理',

    -- 结果字段配置
    `DATA_OUTPUT`  LONGTEXT COMMENT '「dataOutput」- 输出专用配置',
    `DATA_FIELD`   LONGTEXT COMMENT '「dataField」- 当前报表对应字段',

    `CHART_TYPE`   VARCHAR(255) COMMENT '「chartType」- 图表类型',
    `CHART_CONFIG` LONGTEXT COMMENT '「chartConfig」- 若当前报表是图表，则使用此配置',
    -- 关联报表ID
    `REPORT_ID`    VARCHAR(36) COMMENT '「reportId」- 关联报表ID',

    -- 特殊字段
    `ACTIVE`       BIT         DEFAULT NULL COMMENT '「active」- 是否启用',
    `SIGMA`        VARCHAR(32) DEFAULT NULL COMMENT '「sigma」- 统一标识',
    `METADATA`     TEXT COMMENT '「metadata」- 附加配置',
    `LANGUAGE`     VARCHAR(8)  DEFAULT NULL COMMENT '「language」- 使用的语言',

    -- Auditor字段
    `CREATED_AT`   DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`   VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`   DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`   VARCHAR(36) COMMENT '「updatedBy」- 更新人',
    PRIMARY KEY (`KEY`)
);
-- changeset Lang:kp-dimension-2
-- Unique Key: 独立唯一键定义
ALTER TABLE KP_DIMENSION
    ADD UNIQUE (`CODE`, `REPORT_ID`, `SIGMA`) USING BTREE;