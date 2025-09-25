-- liquibase formatted sql

-- changeset Lang:d-question-1
-- 问题专用表：D_QUESTION
/*
 * 题目专用表
 * - 非考试模式（不依赖标准答案）
 * - 考试模式（依赖标准答案）
 */
DROP TABLE IF EXISTS D_QUESTION;
CREATE TABLE IF NOT EXISTS D_QUESTION
(
    `KEY`            VARCHAR(36) COMMENT '「key」- 问题主键',
    -- 系统自动计算的编号相关信息
    `CODE`           VARCHAR(255) COMMENT '「code」- 问题编号',


    `TYPE`           VARCHAR(128) COMMENT '「type」- 题目类型',
    `SORT`           INT COMMENT '「sort」- 题目排序（题库中题目顺序）',


    /*
     题目基本信息
     - name：题干
     - brief：题目基础描述信息
     - tip：模拟题中题目的提示，或正式题中特殊备注
     - material：材料题的材料相关信息（大段文本）
     - materialFiles：编程题、材料题中材料以文件形式处理
     */
    `NAME`           VARCHAR(255) COMMENT '「name」- 题干',
    `BRIEF`          LONGTEXT COMMENT '「brief」- 题目描述信息',
    `TIP`            LONGTEXT COMMENT '「tip」- 题目提示信息',
    `MATERIAL`       LONGTEXT COMMENT '「material」- 题目的特殊材料',
    `MATERIAL_FILES` LONGTEXT COMMENT '「materialFiles」- 材料文件',


    -- 这部分内容为题目的基础
    /*
     * 题目结果信息
     */
    `SCORE`          DECIMAL(18, 4) COMMENT '「score」- 题目分值',
    `LEVEL`          INT COMMENT '「level」- 题目等级（难度）',
    `LEVEL_CONTENT`  LONGTEXT COMMENT '「levelContent」- 题目等级描述信息',
    /*
     标准答案
     - 对填空题：[xx, yy, zz]，列表类型（关注顺序）
     - 单选题："xx", 字符串
     - 多选题：[xx, yy, zz]，JSON数组
     - 判断题：true，布尔值
     */
    `ANSWER_DEFINE`  LONGTEXT COMMENT '「answerDefine」- 答题描述',


    /*
     * 所属题库
     */
    `STORE_ID`       VARCHAR(36) COMMENT '「storeId」- 所属题库',


    -- 本题的源头，用于记录题目关联的文档结构中的某部分
    `MODEL_ID`       VARCHAR(255) COMMENT '「modelId」- 关联的模型identifier，用于描述',
    `MODEL_KEY`      VARCHAR(36) COMMENT '「modelKey」- 关联的模型记录ID，用于描述哪一个Model中的记录',

    -- 特殊字段
    `SIGMA`          VARCHAR(32) COMMENT '「sigma」- 统一标识',
    `LANGUAGE`       VARCHAR(10) COMMENT '「language」- 使用的语言',
    `ACTIVE`         BIT COMMENT '「active」- 是否启用',
    `METADATA`       TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT`     DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`     VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`     DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`     VARCHAR(36) COMMENT '「updatedBy」- 更新人',
    PRIMARY KEY (`KEY`) USING BTREE
);
-- changeset Lang:d-question-2
ALTER TABLE D_QUESTION
    ADD UNIQUE (`CODE`, `SIGMA`) USING BTREE;
-- 题库中试题不能重复
ALTER TABLE D_QUESTION
    ADD UNIQUE (`STORE_ID`, `NAME`) USING BTREE;