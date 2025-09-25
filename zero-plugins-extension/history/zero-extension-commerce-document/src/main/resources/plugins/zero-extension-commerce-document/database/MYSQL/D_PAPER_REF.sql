-- liquibase formatted sql

-- changeset Lang:d-paper-qref-1
-- 关联表：D_PAPER_REF
/**
  考卷和试题的关联表，某一个试题可以多次被考卷引用，所以此处是多对多的关系
  此表用于描述某一场考试的实例信息，比如创建一张试卷，那么这张试卷和试题的关联关系
  在这张表中全程描述
 */
DROP TABLE IF EXISTS D_PAPER_REF;
CREATE TABLE IF NOT EXISTS D_PAPER_REF
(
    /*
     被关联的考卷ID和试题ID
     - paperId：考卷ID
     - questionId：试题ID
     */
    `PAPER_ID`       VARCHAR(36) COMMENT '「paperId」- 考卷ID',
    `QUESTION_ID`    VARCHAR(36) COMMENT '「questionId」- 问题ID',


    /*
     试题附加信息（基础规则）
     - sort：试题序号，某一张考卷中试题的顺序
     - required：此题必答
     */
    `SORT`           INT COMMENT '「sort」- 问题排序',
    `REQUIRED`       BIT COMMENT '「required」- 必答题',


    /*
     试题规则专用组件（扩展用）
     */
    `RULE_COMPONENT` VARCHAR(255) COMMENT '「ruleComponent」- 关联执行组件（扩展用）',

    PRIMARY KEY (`PAPER_ID`, `QUESTION_ID`)
);