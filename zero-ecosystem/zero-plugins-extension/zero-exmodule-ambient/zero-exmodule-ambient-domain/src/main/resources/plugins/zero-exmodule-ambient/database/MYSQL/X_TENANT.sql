-- liquibase formatted sql

-- changeset Lang:ox-tenant-1
-- 应用程序表：X_TENANT
DROP TABLE IF EXISTS X_TENANT;
CREATE TABLE IF NOT EXISTS X_TENANT
(
    `KEY`        VARCHAR(36) COMMENT '「key」- 租户主键',
    `NAME`       VARCHAR(255) COMMENT '「name」- 租户名称',
    `CODE`       VARCHAR(36) COMMENT '「code」- 租户编码',
    /*
     * 租户状态设定
     * PENDING, active = false，未激活
     * - 只有这种状态 active = false
     * ACTIVE, active = true，已激活
     * EXPIRED, active = true, 过期
     * LOCKED, active = true, 锁定
     */
    `STATUS`     VARCHAR(255) COMMENT '「status」- 租户状态',
    `TYPE`       VARCHAR(255) COMMENT '「type」- 租户类型',

    -- 租户登记信息
    `ID_NUMBER`  VARCHAR(255) COMMENT '「idNumber」- 身份证号',
    `ID_FRONT`   VARCHAR(255) COMMENT '「idFront」- 身份证正面',
    `ID_BACK`    VARCHAR(255) COMMENT '「idBack」- 身份证反面',

    -- 银行关联信息（绑定后使用）
    `BANK_ID`    VARCHAR(36) COMMENT '「bankId」- 开户行',
    `BANK_CARD`  VARCHAR(255) COMMENT '「bankCard」- 开户行账号',

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
    PRIMARY KEY (`KEY`)
);
-- changeset Lang:ox-tenant-2
ALTER TABLE X_TENANT
    ADD UNIQUE (`CODE`) USING BTREE;
ALTER TABLE X_TENANT
    ADD UNIQUE (`SIGMA`) USING BTREE;