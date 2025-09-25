-- liquibase formatted sql

-- changeset Lang:d-result-1
/*
 * 带打分的评价详细表结构信息，可用于考试的改卷表，用于设定改卷的结果
 */
DROP TABLE IF EXISTS D_RESULT;
CREATE TABLE IF NOT EXISTS D_RESULT
(
    `KEY`           VARCHAR(36) COMMENT '「key」- 评价主键',
    -- 系统自动计算的章节编号相关信息
    `CODE`          VARCHAR(255) COMMENT '「code」- 评价编号',
    `NAME`          VARCHAR(255) COMMENT '「name」- 评价标题',
    `CONTENT`       LONGTEXT COMMENT '「content」- 备注内容、评价内容',

    /*
     第一维度，反馈相关类型
     */
    `TYPE`          VARCHAR(128) COMMENT '「type」- 评价类型',
    `SORT`          INT COMMENT '「sort」- 评价排序',

    -- 评价结果
    `SCORE`         DECIMAL(18, 4) COMMENT '「score」- 分数',
    `SCORE_MARK`    VARCHAR(128) COMMENT '「scoreMark」- 评价对应文字描述，符号',
    `SCORE_GRADE`   VARCHAR(32) COMMENT '「scoreGrade」- 最终评级 S/A/B/C/D/E',
    `SCORE_CONTENT` LONGTEXT COMMENT '「scoreContent」- 评价文字，区分于原始内容',


    -- 评价人
    `MADE_NAME`     VARCHAR(123) COMMENT '「madeName」- 评价人姓名',
    `MADE_AT`       DATETIME COMMENT '「madeAt」- 评价时间',


    /*
     第二维度
     （广义关联）备注关联的不同模型实体
     */
    `MODEL_ID`      VARCHAR(255) COMMENT '「modelId」- 关联的模型identifier，用于描述',
    `MODEL_KEY`     VARCHAR(36) COMMENT '「modelKey」- 关联的模型记录ID，用于描述哪一个Model中的记录',

    -- 特殊字段
    `SIGMA`         VARCHAR(32) COMMENT '「sigma」- 统一标识',
    `LANGUAGE`      VARCHAR(10) COMMENT '「language」- 使用的语言',
    `ACTIVE`        BIT COMMENT '「active」- 是否启用',
    `METADATA`      TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT`    DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`    VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`    DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`    VARCHAR(36) COMMENT '「updatedBy」- 更新人',
    PRIMARY KEY (`KEY`) USING BTREE
);
-- changeset Lang:d-result-2
ALTER TABLE D_RESULT
    ADD UNIQUE (`CODE`, `SIGMA`) USING BTREE;