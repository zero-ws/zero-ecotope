-- liquibase formatted sql

-- changeset Lang:d-qrule-1
-- 逻辑规则表：D_QRULE
/*
 答案和题目相关的逻辑规则定义，针对每个试卷关联会存在一份，此处不和答案ID相关，而是和某个答题结果相关
 */
DROP TABLE IF EXISTS D_QRULE;
CREATE TABLE IF NOT EXISTS D_QRULE
(
    /*
     基础方向规则
     - refId：某一个考卷实例中关联的记录ID可定位到同一张考卷中的问题
     */
    `REF_ID`         VARCHAR(36) COMMENT '「refId」- 关联考卷中问题实例，对应 D_PAPER_REF 表中内容',


    /*
     规则触发条件
     - answerId：规则中用户选择的答案
     */
    `ANSWER_ID`      VARCHAR(36) COMMENT '「answerId」- 考卷ID',
    `PAPER_ID`       VARCHAR(36) COMMENT '「paperId」- 规则所属考卷ID',


    /*
     规则影响目标
     - questionId：影响操作的目标问题
     - type：显示、隐藏、屏蔽、跳过
     规则基础标记位（影响目标）
     - isRequired：目标问题必答
     - isAttached：目标问题必须上传附件
     - isProof：目标问题必须上传证据
     */
    `TYPE`           VARCHAR(128) COMMENT '「type」- 规则类型',
    `QUESTION_ID`    VARCHAR(36) COMMENT '「questionId」- 问题ID',

    `IS_REQUIRED`    BIT COMMENT '「isRequired」- 必答题',
    `IS_ATTACHED`    BIT COMMENT '「isAttached」- 附件必须',
    `IS_PROOF`       BIT COMMENT '「isProof」- 证据必须',


    /*
     规则配置结果
     1. 一个问题答案触发另外单问题规则
        answerId -> questionId
     2. 一个问题答案触发另外组合规则
        answerId -> ruleSet ( questionId )
     此处 questionId 可以使用 refId 代替
     */
    `RULE_SET`       VARCHAR(255) COMMENT '「ruleSet」- 规则分批执行',
    `RULE_CONFIG`    LONGTEXT COMMENT '「ruleConfig」- 规则相关配置',
    `RULE_COMPONENT` VARCHAR(255) COMMENT '「ruleComponent」- 规则执行组件（扩展用）',
    PRIMARY KEY (`RULE_SET`, `REF_ID`, `TYPE`)
);