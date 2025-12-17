-- liquibase formatted sql

-- changeset Lang:ox-module-1
-- 应用程序中的模块表：X_MODULE
DROP TABLE IF EXISTS X_MODULE;
CREATE TABLE IF NOT EXISTS X_MODULE
(
    `KEY`        VARCHAR(36) COMMENT '「key」- 模块唯一主键',
    `NAME`       VARCHAR(255) COMMENT '「name」- 模块名称',
    `CODE`       VARCHAR(36) COMMENT '「code」- 模块编码',
    `ENTRY`      VARCHAR(255) COMMENT '「entry」— 模块入口地址',
    `BLOCK_CODE` VARCHAR(255) COMMENT '「blockCode」— 所属模块系统编码',

    `MODEL_ID`   VARCHAR(36) COMMENT '「modelId」- 当前模块关联的主模型ID',

    -- ------------------------------ 公共字段 --------------------------------
    `SIGMA`      VARCHAR(128) COMMENT '「sigma」- 用户组绑定的统一标识',
    `LANGUAGE`   VARCHAR(10) COMMENT '「language」- 使用的语言',
    `ACTIVE`     BIT COMMENT '「active」- 是否启用',
    `METADATA`   TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT` DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY` VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT` DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY` VARCHAR(36) COMMENT '「updatedBy」- 更新人',

    `APP_ID`     VARCHAR(255) COMMENT '「appId」- 应用ID',
    `TENANT_ID`  VARCHAR(36) COMMENT '「tenantId」- 租户ID',
    PRIMARY KEY (`KEY`) USING BTREE
);

-- changeset Lang:ox-module-2
ALTER TABLE X_MODULE
    ADD UNIQUE (`ENTRY`, `APP_ID`) USING BTREE; -- 页面入口地址