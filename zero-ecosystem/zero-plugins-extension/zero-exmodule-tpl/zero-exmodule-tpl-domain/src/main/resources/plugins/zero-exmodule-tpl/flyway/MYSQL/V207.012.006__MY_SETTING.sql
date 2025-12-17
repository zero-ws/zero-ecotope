-- liquibase formatted sql

-- changeset Lang:my-setting-1
-- 个人应用表：MY_SETTING
DROP TABLE IF EXISTS MY_SETTING;
CREATE TABLE IF NOT EXISTS MY_SETTING
(
    `KEY`             VARCHAR(36) COMMENT '「key」- 个人设置主键',
    -- 设置参数
    `P_NAV_THEME`     VARCHAR(32) COMMENT '「pNavTheme」- navTheme, 风格处理，对应 light / realdark',
    `P_COLOR_PRIMARY` VARCHAR(20) COMMENT '「pColorPrimary」- colorPrimary，主色调',
    `P_LAYOUT`        VARCHAR(12) COMMENT '「pLayout」- 布局类型：top, menu, mix',
    `P_CONTENT_WIDTH` VARCHAR(12) COMMENT '「pContentWidth」- 两种',
    `P_FIXED_HEADER`  BIT COMMENT '「pFixedHeader」- 标题控制',
    `P_FIX_SIDER_BAR` BIT COMMENT '「pFixSiderBar」- 侧边栏控制',
    `P_COLOR_WEAK`    BIT COMMENT '「pColorWeak」- 色彩控制',
    `P_PWA`           BIT COMMENT '「pPwa」- pwa属性，暂时未知',

    -- title / logo 不可设置个人的
    `P_TOKEN`         LONGTEXT COMMENT '「pToken」- 保留（后续可能会使用）',

    -- 关联关系
    `MY_BAG`          VARCHAR(36) COMMENT '「myBag」- 对应 MY_BAG 设置，每个BAG有对应设置信息',
    `OWNER`           VARCHAR(36) COMMENT '「owner」- 拥有者ID，我的 / 角色级',
    `OWNER_TYPE`      VARCHAR(5) COMMENT '「ownerType」- ROLE 角色，USER 用户',
    -- 维度控制
    `TYPE`            VARCHAR(32) COMMENT '「type」- 类型（默认全站）',
    -- ------------------------------ 公共字段 --------------------------------
    `SIGMA`           VARCHAR(128) COMMENT '「sigma」- 用户组绑定的统一标识',
    `LANGUAGE`        VARCHAR(10) COMMENT '「language」- 使用的语言',
    `ACTIVE`          BIT COMMENT '「active」- 是否启用',
    `METADATA`        TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT`      DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`      VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`      DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`      VARCHAR(36) COMMENT '「updatedBy」- 更新人',

    `APP_ID`          VARCHAR(36) COMMENT '「appId」- 应用ID',
    `TENANT_ID`       VARCHAR(36) COMMENT '「tenantId」- 租户ID',
    PRIMARY KEY (`KEY`) USING BTREE
);
-- changeset Lang:my-setting-2
ALTER TABLE MY_SETTING
    ADD UNIQUE (`TYPE`, `MY_BAG`, `OWNER`, `OWNER_TYPE`);