-- liquibase formatted sql

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- changeset Lang:e-brand-1
-- ----------------------------
-- Table structure for E_BRAND
-- ----------------------------
DROP TABLE IF EXISTS `E_BRAND`;
CREATE TABLE `E_BRAND`
(
    `KEY`           VARCHAR(36) NOT NULL COMMENT '「key」- 品牌ID',
    `CODE`          VARCHAR(255) COMMENT '「code」- 品牌编码',
    `NAME`          VARCHAR(255) COMMENT '「name」- 品牌名称',
    `ALIAS`         VARCHAR(255) COMMENT '「alias」- 品牌别名',
    `COMPANY_NAME`  VARCHAR(128) COMMENT '「companyName」- 品牌公司名',

    -- 品牌基础信息
    `CATEGORY_CODE` VARCHAR(16) COMMENT '「categoryCode」- 类别代码',
    `CATEGORY_NAME` VARCHAR(128) COMMENT '「categoryName」- 类别名称',

    -- 区域信息
    `AREA`          VARCHAR(128) COMMENT '「area」- 区域码，如：GB/T2659-2000国际标准',
    `AREA_NAME`     VARCHAR(128) COMMENT '「areaName」- 区域名称',
    `AREA_CATEGORY` VARCHAR(16) COMMENT '「areaCategory」- 区域类别码',

    -- ------------------------------ 公共字段 --------------------------------
    `SIGMA`         VARCHAR(128) COMMENT '「sigma」- 用户组绑定的统一标识',
    `LANGUAGE`      VARCHAR(10) COMMENT '「language」- 使用的语言',
    `ACTIVE`        BIT COMMENT '「active」- 是否启用',
    `METADATA`      TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT`    DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`    VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`    DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`    VARCHAR(36) COMMENT '「updatedBy」- 更新人',

    `APP_ID`        VARCHAR(36) COMMENT '「appId」- 应用ID',
    `TENANT_ID`     VARCHAR(36) COMMENT '「tenantId」- 租户ID',
    PRIMARY KEY (`KEY`) USING BTREE
);
-- changeset Lang:e-brand-2
ALTER TABLE E_BRAND
    ADD UNIQUE (`CODE`, `SIGMA`) USING BTREE;

ALTER TABLE E_BRAND
    ADD INDEX IDX_E_BRAND_SIGMA (`SIGMA`) USING BTREE;