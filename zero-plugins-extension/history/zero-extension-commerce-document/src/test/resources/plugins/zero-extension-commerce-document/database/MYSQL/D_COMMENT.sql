-- liquibase formatted sql

-- changeset Lang:d-comment-1
DROP TABLE IF EXISTS D_COMMENT;
CREATE TABLE IF NOT EXISTS D_COMMENT
(
    `KEY`        VARCHAR(36) COMMENT '「key」- 解读主键',
    -- 系统自动计算的章节编号相关信息
    `CODE`       VARCHAR(255) COMMENT '「code」- 备注编号',


    /*
     备注基本信息
     name：备注的一段简单文字描述
     content：备注的详细内容描述
     */
    `NAME`       VARCHAR(255) COMMENT '「name」- 备注标题',
    `CONTENT`    LONGTEXT COMMENT '「content」- 备注内容、解读内容',
    -- 备注人，和 createdBy 等价，而 createdAt 已经表示备注创建时间
    `MADE_NAME`  VARCHAR(123) COMMENT '「madeName」- 备注人姓名',
    `MADE_AT`    DATETIME COMMENT '「madeAt」- 备注时间',


    /*
     第一维度
     备注的类型信息，不同类型表示此备注的不同业务解释
     - EXPLAIN：标准化解读
     - COMMENT：定制化解读
     - REMARK：备注表示普通备注
     - NODE：备注表示读书笔记
     */
    `TYPE`       VARCHAR(128) COMMENT '「type」- 备注类型',
    `SORT`       INT COMMENT '「sort」- 备注排序',


    /*
     第二维度
     （广义关联）备注关联的不同模型实体
     */
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
-- changeset Lang:d-comment-2
ALTER TABLE D_COMMENT
    ADD UNIQUE (`CODE`, `SIGMA`) USING BTREE;