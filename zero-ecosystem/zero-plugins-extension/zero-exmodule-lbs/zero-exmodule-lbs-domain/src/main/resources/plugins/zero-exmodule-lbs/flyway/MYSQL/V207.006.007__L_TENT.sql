-- liquibase formatted sql

-- changeset Lang:l-tent-1

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for L_TENT
-- ----------------------------
DROP TABLE IF EXISTS `L_TENT`;
CREATE TABLE `L_TENT`
(
    `KEY`           VARCHAR(36) NOT NULL COMMENT '「key」- 主键',
    `NAME`          VARCHAR(32) NOT NULL COMMENT '「name」- 名称',
    `CODE`          VARCHAR(36) DEFAULT NULL COMMENT '「code」- 编码',

    `CONTACT_PHONE` VARCHAR(20) DEFAULT NULL COMMENT '「contactPhone」- 联系电话',
    `CONTACT_NAME`  VARCHAR(64) DEFAULT NULL COMMENT '「contactName」- 联系人姓名',

    `ORDER`         INT(11)     DEFAULT NULL COMMENT '「order」- 排序',

    `LOCATION_ID`   VARCHAR(36) DEFAULT NULL COMMENT '「locationId」- 关联地址ID',
    `YARD_ID`       VARCHAR(36) DEFAULT NULL COMMENT '「yardId」- 关联小区ID',

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

-- changeset Lang:l-tent-2
ALTER TABLE L_TENT
    ADD UNIQUE (`CODE`, `SIGMA`);