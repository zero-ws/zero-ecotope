-- liquibase formatted sql

-- changeset Lang:d-paper-1
-- 文档专用表：D_PAPER
/*
 * 问卷表
 * 考试表
 */
DROP TABLE IF EXISTS D_PAPER;
CREATE TABLE IF NOT EXISTS D_PAPER
(
    `KEY`          VARCHAR(36) COMMENT '「key」- 问卷主键',
    -- 系统自动计算的编号相关信息
    `CODE`         VARCHAR(255) COMMENT '「code」- 问卷编号',
    `NAME`         VARCHAR(255) COMMENT '「name」- 问卷标题',
    `TYPE`         VARCHAR(128) COMMENT '「type」- 问卷类型',

    /*
     * - DRAFT：草稿
     * - RELEASE：正式发布
     * - RUNNING：正在考试、答题中
     * - STOPPED：正在改卷，改卷过程中的试卷会被锁定
     * - ARCHIVE：归档，考卷创建一次之后会直接归档到历史考卷
     *            历史考卷会保留在系统中，且对应的引用信息会保存下来
     *            可查看历史考卷的相关信息
     */
    `STATUS`       VARCHAR(12) COMMENT '「status」- 状态',
    `BRIEF`        TEXT COMMENT '「brief」- 问卷简介',
    `VERSION`      VARCHAR(32) COMMENT '「version」- 问卷版本, N.N',
    `SIMULATE`     BIT COMMENT '「simulate」- 是否模拟卷',
    `SCORE`        DECIMAL(18, 4) COMMENT '「score」- 当前考卷总体分数',


    /*
     考卷和文件的呈现属性
     - uiCover：问卷封面图
     - uiBg：问卷背景图
     - uiConfig：问卷的UI呈现专用配置
     - uiComponent：考卷呈现的扩展配置
     */
    `UI_COVER`     LONGTEXT COMMENT '「uiCover」- 封面图片',
    `UI_BG`        LONGTEXT COMMENT '「uiBg」- 问卷背景图',
    `UI_CONFIG`    LONGTEXT COMMENT '「uiConfig」- 问卷配置',
    `UI_COMPONENT` VARCHAR(255) COMMENT '「uiComponent」- 问卷呈现专用配置（扩展用）',


    /*
     如果同一场考试的A B卷其试卷本身版本会不一样
     */
    `EXAM_ID`      VARCHAR(36) COMMENT '「examId」- 关联的考试ID',


    -- 所属信息（公司、部门、组）
    `MODEL_ID`     VARCHAR(255) COMMENT '「modelId」- 关联的模型identifier，用于描述',
    `MODEL_KEY`    VARCHAR(36) COMMENT '「modelKey」- 关联的模型记录ID，用于描述哪一个Model中的记录',


    /*
     考卷副本：当前考卷的创建流程是否直接从某一份考卷创建而来，形成新的考卷或问卷
     考卷和文档的自引用副本模式直接关联，如果是外联关联则直接使用 D_REFER 搞定，就不走副本模式
     *：副本自动处理版本 VERSION 的值
     */
    `COPY`         BIT COMMENT '「copy」- 是否副本',
    `COPY_TO`      VARCHAR(36) COMMENT '「copy」- 若是副本，则标注是哪份文档的副本',


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
-- changeset Lang:d-paper-2
ALTER TABLE D_PAPER
    ADD UNIQUE (`CODE`, `SIGMA`) USING BTREE;
ALTER TABLE D_PAPER
    ADD UNIQUE (`NAME`, `EXAM_ID`, `VERSION`) USING BTREE;