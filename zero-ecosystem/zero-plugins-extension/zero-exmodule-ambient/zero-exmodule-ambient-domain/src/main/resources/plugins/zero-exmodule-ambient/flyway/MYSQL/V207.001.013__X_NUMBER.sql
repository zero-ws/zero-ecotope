-- liquibase formatted sql

-- changeset Lang:ox-number-1
SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for X_NUMBER
-- ----------------------------
DROP TABLE IF EXISTS `X_NUMBER`;
CREATE TABLE `X_NUMBER`
(
    `KEY`           VARCHAR(36) NOT NULL COMMENT '「key」- 主键',
    `CODE`          VARCHAR(36)  DEFAULT NULL COMMENT '「code」- 编码',
    `COMMENT`       VARCHAR(255) DEFAULT NULL COMMENT '「comment」- 编号备注信息，用于描述编号数据,comment,S_COMMENT',

    `CURRENT`       BIGINT      NOT NULL COMMENT '「current」编号当前值，对应$ {seed}，每次变化时current提取后更新为：current + step或current - step,current,L_CURRENT',
    `FORMAT`        VARCHAR(255) DEFAULT NULL COMMENT '「formatFail」格式信息，用于处理最终格式：,formatFail,S_FORMAT',
    `IDENTIFIER`    VARCHAR(64)  DEFAULT NULL COMMENT '「identifier」编号对应的identifier，用于查询当前identifier使用的序号信息,identifier,S_IDENTIFIER',
    `PREFIX`        varchar(64)  DEFAULT NULL COMMENT '「prefix」编号前缀,prefix,S_PREFIX',
    `SUFFIX`        varchar(64)  DEFAULT NULL COMMENT '「suffix」编号后缀,suffix,S_SUFFIX',
    `TIME`          varchar(20)  DEFAULT NULL COMMENT '「time」时间对应Pattern，对应$ {time}：YYYY-MM-DD HH:mm:ss用于描述时间格式生成序号时间部分,time,S_TIME',

    `LENGTH`        INT         NOT NULL COMMENT '「length」编号长度，编号长度不包含prefix和suffix部分,length,I_LENGTH',
    `STEP`          INT         NOT NULL COMMENT '「step」编号的步进系数，每次按照step进行变化,step,I_STEP',
    `DECREMENT`     BIT         NOT NULL COMMENT '「decrement」递增/递减？如果为true则递减，为false则是递增,decrement,IS_DECREMENT',

    `RUN_COMPONENT` TEXT COMMENT '「runComponent」- 发号器执行组件，雪花算法所需',

    -- 特殊字段
    `RENEWAL`       BIT          DEFAULT FALSE COMMENT '「renewal」- 是否循环',

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

    `APP_ID`        VARCHAR(255) COMMENT '「appId」- 应用ID',
    `TENANT_ID`     VARCHAR(36) COMMENT '「tenantId」- 租户ID',
    PRIMARY KEY (`KEY`) USING BTREE
);

-- changeset Lang:ox-number-2
ALTER TABLE X_NUMBER
    ADD UNIQUE (`APP_ID`, `IDENTIFIER`, `CODE`) USING BTREE;
SET
FOREIGN_KEY_CHECKS = 1;