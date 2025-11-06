-- liquibase formatted sql

-- changeset Lang:l-country-1
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for L_COUNTRY
-- ----------------------------
DROP TABLE IF EXISTS `L_COUNTRY`;
CREATE TABLE `L_COUNTRY`
(
    `KEY`          VARCHAR(36) NOT NULL COMMENT '「key」- 国家主键',
    `NAME`         VARCHAR(32) NOT NULL COMMENT '「name」- 国家名称',
    `CODE`         VARCHAR(36) DEFAULT NULL COMMENT '「code」- 国家编号',
    `FLAG`         VARCHAR(8)  NOT NULL COMMENT '「flag」- 国旗',
    `PHONE_PREFIX` VARCHAR(8)  NOT NULL COMMENT '「phonePrefix」- 电话前缀',
    `CURRENCY`     VARCHAR(36) COMMENT '「currency」- 使用货币',

    -- 国家的特殊字段
    `ORDER`        INT(11)     DEFAULT NULL COMMENT '「order」- 排序',
    -- ------------------------------ 公共字段 --------------------------------
    `SIGMA`        VARCHAR(128) COMMENT '「sigma」- 用户组绑定的统一标识',
    `LANGUAGE`     VARCHAR(10) COMMENT '「language」- 使用的语言',
    `ACTIVE`       BIT COMMENT '「active」- 是否启用',
    `METADATA`     TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT`   DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`   VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`   DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`   VARCHAR(36) COMMENT '「updatedBy」- 更新人',

    `APP_ID`       VARCHAR(36) COMMENT '「appId」- 应用ID',
    `TENANT_ID`    VARCHAR(36) COMMENT '「tenantId」- 租户ID',
    PRIMARY KEY (`KEY`) USING BTREE
);

-- changeset Lang:l-country-2
ALTER TABLE L_COUNTRY
    ADD UNIQUE (`CODE`, `SIGMA`);
ALTER TABLE L_COUNTRY
    ADD UNIQUE (`PHONE_PREFIX`, `SIGMA`);
