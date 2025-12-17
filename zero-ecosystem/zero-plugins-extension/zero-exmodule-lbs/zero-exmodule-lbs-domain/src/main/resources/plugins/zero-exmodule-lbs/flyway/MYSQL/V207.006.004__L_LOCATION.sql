-- liquibase formatted sql

-- changeset Lang:l-location-1
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for L_LOCATION
-- ----------------------------
DROP TABLE IF EXISTS `L_LOCATION`;
CREATE TABLE `L_LOCATION`
(
    `KEY`        VARCHAR(36)  NOT NULL COMMENT '「key」- 主键',
    `NAME`       VARCHAR(32)  NOT NULL COMMENT '「name」- 名称',
    `CODE`       VARCHAR(36) DEFAULT NULL COMMENT '「code」- 编码',

    `ADDRESS`    TEXT        DEFAULT NULL COMMENT '「address」- 详细地址',
    `CITY`       VARCHAR(32) DEFAULT NULL COMMENT '「city」- 3.城市',
    `COUNTRY`    VARCHAR(32) DEFAULT NULL COMMENT '「country」- 1.国家',
    `REGION`     VARCHAR(32) DEFAULT NULL COMMENT '「region」- 4.区域',
    `FULL_NAME`  VARCHAR(255) NOT NULL COMMENT '「fullName」- 地址全称',
    `STATE`      VARCHAR(32) DEFAULT NULL COMMENT '「state」- 2.省会',
    `STREET1`    VARCHAR(72) DEFAULT NULL COMMENT '「street1」- 街道1',
    `STREET2`    VARCHAR(72) DEFAULT NULL COMMENT '「street2」- 街道2',
    `STREET3`    VARCHAR(72) DEFAULT NULL COMMENT '「street3」- 街道3',
    `POSTAL`     VARCHAR(16) DEFAULT NULL COMMENT '「postal」- 邮政编码',

    `REGION_ID`  VARCHAR(36)  NOT NULL COMMENT '「regionId」- 区域ID',

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

    `APP_ID`     VARCHAR(36) COMMENT '「appId」- 应用ID',
    `TENANT_ID`  VARCHAR(36) COMMENT '「tenantId」- 租户ID',
    PRIMARY KEY (`KEY`) USING BTREE
);

-- changeset Lang:l-location-2
ALTER TABLE L_LOCATION
    ADD UNIQUE (`CODE`, `SIGMA`);