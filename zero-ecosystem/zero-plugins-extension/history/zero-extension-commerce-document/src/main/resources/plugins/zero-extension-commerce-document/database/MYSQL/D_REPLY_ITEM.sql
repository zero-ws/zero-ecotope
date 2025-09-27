-- liquibase formatted sql

-- changeset Lang:d-reply-item-1
-- 关联表：D_REPLY_ITEM
/**
 * 答题卡明细
 */
DROP TABLE IF EXISTS D_REPLY_ITEM;
CREATE TABLE IF NOT EXISTS D_REPLY_ITEM
(
    /*
     此明细所属答题卡ID
     */
    `REPLY_ID`         VARCHAR(36) COMMENT '「replyId」- 答题卡ID',


    /*
     * 问题编号，由于 replyId 中可以直接定位：考试、考卷，所以此处不再考虑编号信息，且同一场考试试题编号不重复
     */
    `QUESTION_CODE`    VARCHAR(255) COMMENT '「questionCode」- 问题编号',


    /*
     * 答题详情
     - answerContent：
       - 单选："xx"，字符串
       - 多选：[xx,yy,zz]，JSON数组
       - 判断：true，布尔
       - 主观：文本，文本
     - answerFiles：答题所需附件
     - answerProof：答题所需凭证（上传证据等）
     */
    `ANSWER_CONTENT`   LONGTEXT COMMENT '「answerContent」- 答题内容',
    `ANSWER_FILES`     LONGTEXT COMMENT '「answerFiles」- 答题文件',
    `ANSWER_PROOF`     LONGTEXT COMMENT '「answerProof」- 答题证明',

    `SORT`             INT COMMENT '「sort」- 问题排序',
    `ANSWER_COMPONENT` VARCHAR(255) COMMENT '「answerComponent」- 关联执行组件（扩展用）',
    PRIMARY KEY (`REPLY_ID`, `QUESTION_CODE`)
);