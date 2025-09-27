-- liquibase formatted sql

-- changeset Lang:my-app-1
-- 个人应用表：MY_APP
DROP TABLE IF EXISTS MY_APP;
CREATE TABLE IF NOT EXISTS MY_APP
(
    `KEY`        VARCHAR(36) COMMENT '「key」- 个人应用主键',

    -- 关联配置
    /*
     * 关联 Apps 只能处理 AppId 以及入口问题，不可以处理其他问题，比如使用 AppId 直接读取
     * 字典相关数据，不包含在 Apps 的个人设置中，所以此处主要针对 AppId 以及 BagId 进行关联设置
     */
    `APP_ID`     VARCHAR(36) COMMENT '「appId」- 个人应用关联ID',
    `BAG_ID`     VARCHAR(36) COMMENT '「bagId」- 个人应用绑定的 BAG ID',

    `OWNER`      VARCHAR(36) COMMENT '「owner」- 拥有者ID，我的 / 角色级',
    `OWNER_TYPE` VARCHAR(5) COMMENT '「ownerType」- ROLE 角色，USER 用户',

    -- UI定制
    `UI_SORT`    BIGINT COMMENT '「uiSort」- 模块排序',

    -- 维度控制
    `TYPE`       VARCHAR(32) COMMENT '「type」- 类型（默认全站）',
    `POSITION`   VARCHAR(16) COMMENT '「position」- 位置（默认左上）',

    -- 特殊字段
    `ACTIVE`     BIT         DEFAULT NULL COMMENT '「active」- 是否启用',
    `SIGMA`      VARCHAR(32) DEFAULT NULL COMMENT '「sigma」- 统一标识',
    `METADATA`   TEXT COMMENT '「metadata」- 附加配置',
    `LANGUAGE`   VARCHAR(8)  DEFAULT NULL COMMENT '「language」- 使用的语言',

    -- Auditor字段
    `CREATED_AT` DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY` VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT` DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY` VARCHAR(36) COMMENT '「updatedBy」- 更新人',
    PRIMARY KEY (`KEY`)
);

-- changeset Lang:my-app-2
ALTER TABLE MY_APP
    ADD UNIQUE (`OWNER_TYPE`, `OWNER`, `TYPE`, `POSITION`, `APP_ID`);