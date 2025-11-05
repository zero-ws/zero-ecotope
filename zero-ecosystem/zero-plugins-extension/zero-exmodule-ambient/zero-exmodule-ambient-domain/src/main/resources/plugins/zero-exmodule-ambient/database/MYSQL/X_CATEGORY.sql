-- liquibase formatted sql

-- changeset Lang:ox-category-1
-- 树形类型数据表
DROP TABLE IF EXISTS X_CATEGORY;
CREATE TABLE IF NOT EXISTS X_CATEGORY
(
    `KEY`            VARCHAR(36) COMMENT '「key」- 类型主键',
    `NAME`           VARCHAR(255) COMMENT '「name」- 类型名称',
    `CODE`           VARCHAR(255) COMMENT '「code」- 类型编号',
    `ICON`           VARCHAR(255) COMMENT '「icon」- 类型图标',
    `TYPE`           VARCHAR(255) COMMENT '「type」- 类型的分类',
    `SORT`           INTEGER COMMENT '「sort」- 排序信息',
    `LEAF`           BIT COMMENT '「leaf」- 是否叶节点',
    `PARENT_ID`      VARCHAR(36) COMMENT '「parentId」- 父类ID',
    `IDENTIFIER`     VARCHAR(255) COMMENT '「identifier」- 当前类型描述的Model的标识',
    `COMMENT`        TEXT COMMENT '「comment」- 备注信息',

    /*
     * 树构造组件
     * 1. treeComponent/treeConfig 用于构造树的显示专用（目前使用的就是文档管理中的目录树）
     * 2. runComponent/runConfig 用于执行树操作专用（目前未使用，但后期会用到）
     */
    `TREE_COMPONENT` TEXT COMMENT '「treeComponent」- 目录组件，构造树专用',
    `TREE_CONFIG`    LONGTEXT COMMENT '「treeConfig」- 目录组件运行配置，特殊场景专用',
    `RUN_COMPONENT`  TEXT COMMENT '「runComponent」- 执行组件',
    `RUN_CONFIG`     LONGTEXT COMMENT '「runConfig」- 执行组件相关配置',

    -- ------------------------------ 公共字段 --------------------------------
    `SIGMA`          VARCHAR(128) COMMENT '「sigma」- 用户组绑定的统一标识',
    `LANGUAGE`       VARCHAR(10) COMMENT '「language」- 使用的语言',
    `ACTIVE`         BIT COMMENT '「active」- 是否启用',
    `METADATA`       TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT`     DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`     VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`     DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`     VARCHAR(36) COMMENT '「updatedBy」- 更新人',

    `APP_ID`         VARCHAR(255) COMMENT '「appId」- 应用ID',
    `TENANT_ID`      VARCHAR(36) COMMENT '「tenantId」- 租户ID',
    PRIMARY KEY (`KEY`) USING BTREE
);

-- changeset Lang:ox-category-2
ALTER TABLE X_CATEGORY
    ADD UNIQUE (`APP_ID`, `TYPE`, `CODE`) USING BTREE; -- 每一个应用内的 app - type - code 维持唯一
ALTER TABLE X_CATEGORY
    ADD UNIQUE (`SIGMA`, `TYPE`, `CODE`) USING BTREE;

ALTER TABLE X_CATEGORY
    ADD INDEX IDXM_X_CATEGORY_APP_ID_TYPE_ACTIVE (`APP_ID`, `TYPE`, `ACTIVE`) USING BTREE;
ALTER TABLE X_CATEGORY
    ADD INDEX IDXM_X_CATEGORY_SIGMA_TYPE_ACTIVE (`SIGMA`, `TYPE`, `ACTIVE`) USING BTREE;