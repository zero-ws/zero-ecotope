-- liquibase formatted sql

-- changeset Lang:d-exam-1
-- 文档专用表：D_EXAM
/*
 * 创建一次考试，一场活动
 */
DROP TABLE IF EXISTS D_EXAM;
CREATE TABLE IF NOT EXISTS D_EXAM
(
    `KEY`           VARCHAR(36) COMMENT '「key」- 考试主键',
    -- 系统自动计算的编号相关信息
    `CODE`          VARCHAR(255) COMMENT '「code」- 考试编号',
    `NAME`          VARCHAR(255) COMMENT '「name」- 考试名称',
    `SUBJECT`       VARCHAR(255) COMMENT '「subject」- 考试科目',
    `BRIEF`         TEXT COMMENT '「brief」- 考试简介',
    `SN`            VARCHAR(255) COMMENT '「sn」- 考试代码，如SCJP、SCWCD 1.5等专业考试专用代码',
    `DURATION`      INT COMMENT '「duration」- 时长，单位：分钟',


    /*
     考试类型可以是多种，只要是考试，那么就存在考卷与之关联
     - AUDIT：审计
     - STANDARD：标准考试
     - FEEDBACK：返回型（问卷）
     */
    `TYPE`          VARCHAR(128) COMMENT '「type」- 考试类型',
    `STATUS`        VARCHAR(12) COMMENT '「status」- 状态',


    /*
     考试模式相关：线上、线下
     online = true
     - addrUrl：线上考试地址
     online = false
     - addrLocation：线下考试地址（关联模式，关联内容填写到 addrContent 中）
     - addrContent：线下地址（填写模式）
     */
    `ONLINE`        BIT COMMENT '「online」- 是否线上考试',
    `ADDR_URL`      VARCHAR(1024) COMMENT '「addrUrl」- 线上考试地址',
    `ADDR_LOCATION` VARCHAR(36) COMMENT '「addrLocation」- 线下考试的考试地址',
    `ADDR_CONTENT`  TEXT COMMENT '「addrContent」- 线下考试地址详细描述',


    -- 所属信息（公司、部门、组）
    `MODEL_ID`      VARCHAR(255) COMMENT '「modelId」- 关联的模型identifier，用于描述',
    `MODEL_KEY`     VARCHAR(36) COMMENT '「modelKey」- 关联的模型记录ID，用于描述哪一个Model中的记录',


    /*
     考试主办方，即发布考试的相关信息，发布者和发布时间
     */
    `ISSUER`        VARCHAR(128) COMMENT '「issuer」- 发布者',
    `ISSUER_AT`     DATETIME COMMENT '「issuerAt」- 发布时间',
    `START_AT`      DATETIME COMMENT '「startAt」- 考试开始时间',
    `END_AT`        DATETIME COMMENT '「endAt」- 考试结束时间',

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
-- changeset Lang:d-exam-2
ALTER TABLE D_EXAM
    ADD UNIQUE (`CODE`, `SIGMA`) USING BTREE;