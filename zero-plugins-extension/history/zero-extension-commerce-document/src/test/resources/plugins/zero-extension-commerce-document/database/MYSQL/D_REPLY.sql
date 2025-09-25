-- liquibase formatted sql

-- changeset Lang:d-reply-1
/*
 * 带打分的评价详细表结构信息，可用于考试的改卷表，用于设定改卷的结果
 * 答题卡
 */
DROP TABLE IF EXISTS D_REPLY;
CREATE TABLE IF NOT EXISTS D_REPLY
(
    `KEY`          VARCHAR(36) COMMENT '「key」- 答题卡主键',
    -- 系统自动计算的编号相关信息
    `CODE`         VARCHAR(255) COMMENT '「code」- 答题卡编号',


    /*
     答题登记相关信息
     - userId：考生ID
     - userName：考生姓名
     - userNo：准考证编号

     - identityIdc：证件号（需要身份证时的身份证号）
     - identityId：和系统存储考生档案关联时的关联ID
     */
    -- 考生消息
    `USER_ID`      VARCHAR(36) COMMENT '「userId」- 考生ID',
    `USER_NAME`    VARCHAR(255) COMMENT '「userName」- 考生姓名',
    `USER_NO`      VARCHAR(255) COMMENT '「userNo」- 准考证号',
    -- 考生档案
    `IDENTITY_IDC` VARCHAR(255) COMMENT '「identityIdc」- 证件号',
    `IDENTITY_ID`  VARCHAR(36) COMMENT '「identityId」- 关联档案时考生档案ID',


    /*
     答题卡所属
     - examId：所属哪一场考试答题卡
     - paperId：所属哪一张考卷答题卡
     */
    `EXAM_ID`      VARCHAR(36) COMMENT '「examId」- 哪场考试答题卡',
    `PAPER_ID`     VARCHAR(36) COMMENT '「paperId」- 哪张试卷答题卡',
    -- 本题的源头，用于记录题目关联的文档结构中的某部分


    /*
     答题卡关联模型
     - modelId：部门、客户、员工、公司等模型
     - modelKey：关联模型记录主键
     */
    `MODEL_ID`     VARCHAR(255) COMMENT '「modelId」- 关联的模型identifier，用于描述',
    `MODEL_KEY`    VARCHAR(36) COMMENT '「modelKey」- 关联的模型记录ID，用于描述哪一个Model中的记录',


    /*
     答题结果相关统计
     主动结果
     - qTotal：试题总量
     - qReply：已答试题数，未答：qTotal - qReply
     - qOk：正确答题数，错误：qTotal - qOk
     答题卡评价
     - score：分数模式的最终评价
     - grade：等级模式最终评价
     - passed：是否通过（及格、认证通过等）
     */
    -- 考试总成绩
    `SCORE`        DECIMAL(10, 2) COMMENT '「score」- 得分',
    `GRADE`        VARCHAR(32) COMMENT '「grade」- 最终评级 S/A/B/C/D/E',
    `PASSED`       BIT COMMENT '「passed」- 是否及格，及格 passed = true，否则 passed = false',

    -- 考题统计
    `Q_TOTAL`      INT COMMENT '「qTotal」- 试题总量',
    `Q_REPLY`      INT COMMENT '「qReply」- 已答试题数量',
    `Q_OK`         INT COMMENT '「qOk」- 正确试题数量',

    -- 特殊字段
    `SIGMA`        VARCHAR(32) COMMENT '「sigma」- 统一标识',
    `LANGUAGE`     VARCHAR(10) COMMENT '「language」- 使用的语言',
    `ACTIVE`       BIT COMMENT '「active」- 是否启用',
    `METADATA`     TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT`   DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`   VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`   DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`   VARCHAR(36) COMMENT '「updatedBy」- 更新人',
    PRIMARY KEY (`KEY`) USING BTREE
);
-- changeset Lang:d-reply-2
ALTER TABLE D_REPLY
    ADD UNIQUE (`CODE`, `SIGMA`) USING BTREE;