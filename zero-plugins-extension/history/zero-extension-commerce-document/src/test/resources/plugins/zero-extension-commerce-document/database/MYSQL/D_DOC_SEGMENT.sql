-- liquibase formatted sql

-- changeset Lang:d-doc-segment-1
-- 文档章节表：D_DOC_SEGMENT
/*
 * 章节支持树型结构，可以直接反向构造某一份文档的章节相关信息
 * 文档本身只带简介、描述，不带章节
 */
DROP TABLE IF EXISTS D_DOC_SEGMENT;
CREATE TABLE IF NOT EXISTS D_DOC_SEGMENT
(
    `KEY`          VARCHAR(36) COMMENT '「key」- 文档主键，唯一标识',
    -- 系统自动计算的章节编号相关信息
    `CODE`         VARCHAR(255) COMMENT '「code」- 章节编号',


    /*
     - 章节大类：目录、序、正文、附录，即使是有父章节关系，章节本身也可以带有内容，作为章节前置文本
     1）章节 = 自然段：此自然段的名称可以为 NULL
     2）章节 = 某部分内容，NAME 表示这部分的标题
     3）章节 = 子级段落，可描述此段落名称标题
     4）章节 = 某个 List 中的项，直接将项内容存储在此属性中
     - prefix 章节前缀配合章节实现前缀统一化计算
     */
    `PREFIX`       VARCHAR(255) COMMENT '「prefix」- 章节前缀',
    `NAME`         VARCHAR(255) COMMENT '「name」- 章节标题',
    `TITLE`        VARCHAR(255) COMMENT '「title」- 章节标题（理论上 prefix + name）',
    `TYPE`         VARCHAR(128) COMMENT '「type」- 章节类型',
    `STATUS`       VARCHAR(12) COMMENT '「status」- 状态',


    /*
     * 主格式使用 content
     1. 文本格式多为去掉 HTML 标签之后的纯文本格式
     2. 存储格式为底层存储格式，可能是一个 XML 或一个 JSON
     三种格式相互配合实现章节的整体存储
     - content：呈现章节的专用格式，如果是富文本则包含HTML标签
     - contentText：没有格式只包含相关文字描述
     - contentMeta：章节若比较复杂，执行结构化之后章节的章节存储内容，存储了内容的元数据信息
     ======
     此处为双属性设计，用另外一个辅助属性对章节内容执行描述，如格式、长度、元信息等
     */
    `CONTENT`      LONGTEXT COMMENT '「content」- 章节内容（带格式）',
    `CONTENT_TEXT` LONGTEXT COMMENT '「contentText」- 无格式章节内容',
    `CONTENT_META` LONGTEXT COMMENT '「contentMeta」- 存储格式',


    /*
    sort / level
    章节本身记录了排序序号和层级关系，可用于树型状态下章节的整体排序，完成章节最终的结构
    呈现，此部分内容纯为了树型呈现使用
     */
    `SORT`         INT COMMENT '「sort」- 排序',
    `LEVEL`        INT COMMENT '「level」- 章节层级',


    /*
    - docId：表示此章节属于哪一份文档
    - parentId：章节构成的父子关系
     */
    -- 章节关系
    `DOC_ID`       VARCHAR(36) COMMENT '「docId」- 章节所属文档ID',
    `PARENT_ID`    VARCHAR(36) COMMENT '「parentId」- 父章节ID',


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
-- changeset Lang:d-doc-segment-2
/*
 章节的业务唯一标识性
 第一维度：由于 docId 自带 sigma 属性，所以此处的第一维度如
 - name：章节名称（前缀prefix只是修饰）
 - docId：所属文档ID
 - parentId：所属父章节（同一个父章节之下只允许有一个重复）
 第二维度：系统维度
 - code：系统生成编号，若没有则不考虑唯一性问题，默认会包含，做系统记录
 */
ALTER TABLE D_DOC_SEGMENT
    ADD UNIQUE (`NAME`, `DOC_ID`, `PARENT_ID`) USING BTREE;
ALTER TABLE D_DOC_SEGMENT
    ADD UNIQUE (`CODE`, `SIGMA`) USING BTREE;
