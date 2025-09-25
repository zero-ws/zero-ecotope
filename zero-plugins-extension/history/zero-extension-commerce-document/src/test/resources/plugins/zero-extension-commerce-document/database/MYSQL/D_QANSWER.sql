-- liquibase formatted sql

-- changeset Lang:d-qanswer-1
-- 答案专用表：D_QANSWER
/*
 * 题目答案专用表
 */
DROP TABLE IF EXISTS D_QANSWER;
CREATE TABLE IF NOT EXISTS D_QANSWER
(
    `KEY`         VARCHAR(36) COMMENT '「key」- 答题主键',
    -- 系统自动计算的编号相关信息
    `CODE`        VARCHAR(255) COMMENT '「code」- 答案编号',

    /*
     如果是单选、多选、材料题，答案本身是必须的
     */
    `NAME`        VARCHAR(255) COMMENT '「name」- 答案内容',
    `BRIEF`       LONGTEXT COMMENT '「brief」- 答案详细描述',
    `UI_CONFIG`   LONGTEXT COMMENT '「uiConfig」- 答案呈现模式',

    `QUESTION_ID` VARCHAR(36) COMMENT '「questionId」- 所属问题',

    -- 特殊字段
    `SIGMA`       VARCHAR(32) COMMENT '「sigma」- 统一标识',
    `LANGUAGE`    VARCHAR(10) COMMENT '「language」- 使用的语言',
    `ACTIVE`      BIT COMMENT '「active」- 是否启用',
    `METADATA`    TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT`  DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`  VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`  DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`  VARCHAR(36) COMMENT '「updatedBy」- 更新人',
    PRIMARY KEY (`KEY`) USING BTREE
);
-- changeset Lang:d-qanswer-2
ALTER TABLE D_QANSWER
    ADD UNIQUE (`CODE`, `SIGMA`) USING BTREE;
-- 同一题中答案定义（选项）不允许重复
ALTER TABLE D_QANSWER
    ADD UNIQUE (`NAME`, `QUESTION_ID`) USING BTREE;