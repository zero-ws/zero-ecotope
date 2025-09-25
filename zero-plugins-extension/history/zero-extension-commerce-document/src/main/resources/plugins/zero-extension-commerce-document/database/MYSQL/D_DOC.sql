-- liquibase formatted sql

-- changeset Lang:d-doc-1
-- 文档专用表：D_DOC
/*
 * 注意文档和附件的区别，文档更多是用于描述文档的业务属性
 */
DROP TABLE IF EXISTS D_DOC;
CREATE TABLE IF NOT EXISTS D_DOC
(
    `KEY`         VARCHAR(36) COMMENT '「key」- 文档主键，唯一标识',
    -- 系统自动计算，文档的系统编号，可标识文档的唯一性
    `CODE`        VARCHAR(255) COMMENT '「code」- 文档系统编号',


    /*
     文档的基本状态
     DRAFT - 草稿，在创建过程中支持分布创建，所以这种模式下的文件是不公开的，只有创建人可见
     PENDING - 等待审批（此状态只适合于审批流程），等待审批的文件只有审批人可见
     RELEASE - 发布，发布后的文件可供所有人查看，这种属于正式运行的文档
     ARCHIVE - 归档，归档后的文档只能作为历史文档来执行查看

     DRAFT -> PENDING -> RELEASE -> ARCHIVE
                            |          |
                         RUNNING -> STOPPED
     */
    `STATUS`      VARCHAR(12) COMMENT '「status」- 状态',
    `TYPE`        VARCHAR(128) COMMENT '「type」- 文档类型，用于指定子表信息',
    `CATEGORY`    VARCHAR(36) COMMENT '「category」- 文档类别, 关联对应的分类',


    /*
     关于 NAME / TITLE / FILE_NAME 三个属性的不同使用场景
     （重新修订）
     NAME - 文档名称，和 X_ATTACHMENT 中的 NAME 对齐，不录入，使用上传文件的文件名，带扩展名
     文件名不重复，自带 - 后缀不重复，如此文件类型也不会重复，有了此规则，可以增强文档的管理性，如
     一个 A.docx 和 A.pdf 在整个环境中会作为两份不同的文档来对待，有利于管理。

     FILE_NAME - 文件别名（重命名），不带扩展名
     */
    `NAME`        VARCHAR(255) COMMENT '「name」- 文档名称',


    -- 文档特殊属性
    /*
     文档基本属性，表现了文档的价值体系
     - sn：唯一文档编号
     - prefix：文档前缀，通常是统一管理文档时所需的文档必须属性
     - brief：文档的基本文字内容
     - description：文档的详细描述
     - scope：作用范围描述
     - version：文档版本，文档可拥有多个版本，每个版本都是一个独立的文档，所以文档的版本信息也是文档的基本属性
     */
    `SN`          VARCHAR(255) COMMENT '「sn」- 文档编号',
    `PREFIX`      VARCHAR(255) COMMENT '「prefix」- 文档前缀',
    `BRIEF`       TEXT COMMENT '「brief」- 文档简介',
    `DESCRIPTION` LONGTEXT COMMENT '「description」- 文档描述',
    `SCOPE`       LONGTEXT COMMENT '「scope」- 作用范围描述',
    `VERSION`     VARCHAR(32) COMMENT '「version」- 文档版本, N.N',


    -- 作者属性
    /*
     作者属性，作者属性主要用来处理当前文件的基本作者信息，包括作者、第二作者、第三作者等，作者属性可用于
     对文件本身进行描述，常见的文件两种情况
     -- 基本创作
        - author：表示本文档作者
        - authorOr：表示本文档的第二作者、第三作者等
     -- 原文书
        - author：表示原文作者
        - authorOr：表示原文的翻译，翻译者可以是多个
     */
    `AUTHOR`      VARCHAR(128) COMMENT '「author」- 文档作者',
    `AUTHOR_OR`   LONGTEXT COMMENT '「authorOr」- 第二作者、第三作者',


    /*
     发布机构的完整概念
     （外发）External外部发布
     - publisher：当文档以外部发布时，此处的 publisher 为文本格式，记录了发布机构相关信息
     （内发）Internal内部发布
     - modelId：模型统一标识专用 identifier，标识发布的对象ID
     - modelKey：identifier 模型之下对应的发布机构的记录主键
     publishAt：发布文档的时间
     ======
     内发表示此机构已经在我们的系统库中有所存储，如
     - E_CUSTOMER：客户作为发布机构
     - E_COMPANY：公司、分公司作为发布机构
     - E_DEPT：部门作为发布机构
     - E_TEAM：组作为发布机构
     */
    `PUBLISHER`   TEXT COMMENT '「publisher」- 发布者信息',
    `PUBLISH_AT`  DATETIME COMMENT '「publishAt」- 发布时间或日期',
    `MODEL_ID`    VARCHAR(255) COMMENT '「modelId」- 关联的模型identifier，用于描述',
    `MODEL_KEY`   VARCHAR(36) COMMENT '「modelKey」- 关联的模型记录ID，用于描述哪一个Model中的记录',


    -- 自引用
    /*
     表示当前文件是否从另外一个文件创建而来，如果是创建而来，那么当前文件可能是另外一份文件的副本，记录了文件的创建
     源头，且文件若是第一份则不会有副本的概念，就是原始版本，查询时可以通过 copy = true / false 来鉴别需要查询文
     件的基础需求。
     *：副本自动处理版本 VERSION 的值
     */
    `COPY`        BIT COMMENT '「copy」- 是否副本',
    `COPY_TO`     VARCHAR(36) COMMENT '「copy」- 若是副本，则标注是哪份文档的副本',


    /*
     直接使用 FILE_KEY 可关联到文档的唯一键值，并可根据信息下载文件，内联可直接对应到 X_ATTACHMENT 中的附件
     信息实现全文档的下载相关信息，开启下载渠道直接进入文档的处理方式，包括文档阅览模式也可根据此字段实现对应的
     下载，此处关联采用软关联模式。
     */
    `FILE_NAME`   VARCHAR(255) COMMENT '「fileName」- 原始文件名，带扩展名',
    `FILE_KEY`    VARCHAR(255) COMMENT '「fileKey」- TPL模式中的文件唯一的key（全局唯一）',


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
-- changeset Lang:d-doc-2
/*
 * 存储唯一键值，用于关联到附件表中的文件信息，可实现文件的下载
 * fileKey 是唯一的键值，此键值会使得文件全局唯一，每个文件上传时会生成唯一的 fileKey
 *
 * fileKey 不依赖于应用以及租户，所以可直接实现全局唯一性，保证唯一键值，为了保证应用、租户之间共享存储时文件的唯一性，
 * fileKey的计算主要是依赖文件URI地址实现全局唯一性
 */
ALTER TABLE D_DOC
    ADD UNIQUE (`FILE_KEY`) USING BTREE;
/*
 * 文档的业务唯一性标识：
 * - NAME / CODE：业务文档名称 / 文档编号形成的文件基础标识
 * - SIGMA：可表示租户或应用，若是多租户模式直接标识租户
 * - VERSION：文档版本，同一份文件可拥有多份版本，所以版本信息页纳入到唯一性标识中
 */
ALTER TABLE D_DOC
    ADD UNIQUE (`NAME`, `SIGMA`, `VERSION`) USING BTREE;
ALTER TABLE D_DOC
    ADD UNIQUE (`CODE`, `SIGMA`, `VERSION`) USING BTREE;