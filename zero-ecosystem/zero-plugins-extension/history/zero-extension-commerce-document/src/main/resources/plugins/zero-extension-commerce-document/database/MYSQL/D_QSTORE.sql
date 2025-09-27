-- liquibase formatted sql

-- changeset Lang:d-qstore-1
-- 文档专用表：D_QSTORE
/*
 * 题库表，题库上会包含多个题目等信息，且题库和试题只能是树型结构
 */
DROP TABLE IF EXISTS D_QSTORE;
CREATE TABLE IF NOT EXISTS D_QSTORE
(
    `KEY`        VARCHAR(36) COMMENT '「key」- 题库主键',
    -- 系统自动计算的编号相关信息
    `CODE`       VARCHAR(255) COMMENT '「code」-  题库编号',


    /*
     题库维度相关
     type：系统基础分类维度
     category：领域类型题库或业务分类树上的题库
     */
    `TYPE`       VARCHAR(128) COMMENT '「type」-  题库类型',
    /*
     * - BUILDING：在建
     * - RELEASE：正式发布
     * - DEPRECATED：已废弃
     */
    `STATUS`     VARCHAR(12) COMMENT '「status」- 状态',
    `CATEGORY`   VARCHAR(36) COMMENT '「category」- 题库类型、树型模型',


    /*
     题库基本信息
     - name：题库标题
     - brief：题库简介
     - version：题库版本
     - questions：题库中试题数量
     */
    `NAME`       VARCHAR(255) COMMENT '「name」-  题库标题',
    `BRIEF`      TEXT COMMENT '「brief」- 题库简介',
    `VERSION`    VARCHAR(32) COMMENT '「version」- 题库版本, N.N',
    `QUESTIONS`  INT COMMENT '「questions」- 题目数量',
    

    -- 所属信息（公司、部门、组）
    `MODEL_ID`   VARCHAR(255) COMMENT '「modelId」- 关联的模型identifier，用于描述',
    `MODEL_KEY`  VARCHAR(36) COMMENT '「modelKey」- 关联的模型记录ID，用于描述哪一个Model中的记录',

    -- 特殊字段
    `SIGMA`      VARCHAR(32) COMMENT '「sigma」- 统一标识',
    `LANGUAGE`   VARCHAR(10) COMMENT '「language」- 使用的语言',
    `ACTIVE`     BIT COMMENT '「active」- 是否启用',
    `METADATA`   TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT` DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY` VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT` DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY` VARCHAR(36) COMMENT '「updatedBy」- 更新人',
    PRIMARY KEY (`KEY`) USING BTREE
);
-- changeset Lang:d-qstore-2
ALTER TABLE D_QSTORE
    ADD UNIQUE (`CODE`, `SIGMA`, `VERSION`) USING BTREE;
ALTER TABLE D_QSTORE
    ADD UNIQUE (`NAME`, `SIGMA`, `VERSION`) USING BTREE;
