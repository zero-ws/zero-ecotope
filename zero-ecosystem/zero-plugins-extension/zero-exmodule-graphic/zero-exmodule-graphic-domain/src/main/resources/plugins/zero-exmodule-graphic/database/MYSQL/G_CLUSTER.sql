-- liquibase formatted sql

-- changeset Lang:ox-cluster-1
-- 拓扑图中的组专用表（分组）：G_CLUSTER
DROP TABLE IF EXISTS G_CLUSTER;
CREATE TABLE IF NOT EXISTS G_CLUSTER
(
    `KEY`         VARCHAR(36) COMMENT '「key」- 组ID',
    `NAME`        VARCHAR(255) COMMENT '「name」- 组呈现名称',
    `X`           INT COMMENT '「x」- 当前组在图上的x坐标',
    `Y`           INT COMMENT '「y」- 当前组在图上的y坐标',

    `GRAPHIC_ID`  VARCHAR(36) COMMENT '「graphicId」- 它所关联的图实例ID',
    `UI`          TEXT COMMENT '「ui」- ui配置专用',
    `RECORD_KEY`  VARCHAR(36) COMMENT '「recordKey」- 记录主键',
    `RECORD_DATA` TEXT COMMENT '「recordData」- 组中存储的数据信息',

    -- ------------------------------ 公共字段 --------------------------------
    `SIGMA`       VARCHAR(128) COMMENT '「sigma」- 用户组绑定的统一标识',
    `LANGUAGE`    VARCHAR(10) COMMENT '「language」- 使用的语言',
    `ACTIVE`      BIT COMMENT '「active」- 是否启用',
    `METADATA`    TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT`  DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`  VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`  DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`  VARCHAR(36) COMMENT '「updatedBy」- 更新人',

    `APP_ID`      VARCHAR(36) COMMENT '「appId」- 应用ID',
    `TENANT_ID`   VARCHAR(36) COMMENT '「tenantId」- 租户ID',
    PRIMARY KEY (`KEY`) USING BTREE
);
ALTER TABLE G_CLUSTER
    ADD UNIQUE (`NAME`, `GRAPHIC_ID`) USING BTREE;