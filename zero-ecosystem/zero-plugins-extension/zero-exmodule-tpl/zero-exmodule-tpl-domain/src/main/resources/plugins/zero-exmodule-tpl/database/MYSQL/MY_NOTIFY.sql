-- liquibase formatted sql

-- changeset Lang:ox-my_notify-1
-- MY_NOTIFY 设置表（个人提醒设置表）
/*
 * 设置个人的消息信息用来搭建个人站内信的设置信息，此设置用于个人提醒定制，
 * 只有定制过的提醒才会才个人提醒中显示出来，若没有定制提醒则不会在消息队列中生成
 * 消息相关信息。
 * MY_NOTIFY   负责设置个人的提醒
 * I_MESSAGE   个人站内信，会标记：已读、未读等，和 I_MESSAGE 集成，同时生成站内消息队列
 *             TO：消息接收方为当前用户
 * TPL_MESSAGE 中负责定义消息的模板，发送提醒时的消费模块
 * I_MESSAGE   中负责定义消息的内容（消息队列）
 *
 * 多人协同时
 * 1）先根据提醒相关定义从 TPL_MESSAGE 中读取提醒所需的模板信息
 * 2）根据参数和 TPL_MESSAGE 中记录的消息生成消息标题和消息内容
 * 3）从 MY_NOTIFY 中筛选出需要提醒的人员（个人设置中设置）
 * 4）将消息内容写入到 I_MESSAGE 中，同时发送到消息队列，若此时客户端并没有连上则直接插入 I_MESSAGE 中
 *    MY_MESSAGE 中会包含：已读 / 未读， I_MESSAGE 中会包含等待和发送
 */
DROP TABLE IF EXISTS MY_NOTIFY;
CREATE TABLE IF NOT EXISTS MY_NOTIFY
(
    `KEY`             VARCHAR(36) COMMENT '「key」- 设置主键',
    /*
     * 1. 拥有者为角色时证明这些角色下所有账号都需要发送
     * 2. 拥有者为用户时证明这个用户已经自定义过了配置信息，这种场景下
     *    不会生成 I_MESSAGE 的队列，所以此处计算会变得稍微复杂
     * 3. 先提取 OWNER_TYPE = ROLE 下的所有用户，再提取 OWNER_ID 为对应用户的配置信息
     */
    `OWNER_TYPE`      VARCHAR(128) COMMENT '「ownerType」- 拥有者类型',
    `OWNER_ID`        VARCHAR(36) COMMENT '「ownerId」- 拥有者ID',

    -- 提醒设置
    `CONFIG_INTERNAL` LONGTEXT COMMENT '「configInternal」- 站内信配置',
    `CONFIG_EMAIL`    LONGTEXT COMMENT '「configEmail」- 邮件配置',
    `CONFIG_SMS`      LONGTEXT COMMENT '「configSms」- 短信配置',

    -- 特殊属性
    `APP_ID`          VARCHAR(36) COMMENT '「id」- 所属应用ID',

    -- 特殊字段
    `ACTIVE`          BIT         DEFAULT NULL COMMENT '「active」- 是否启用',
    `SIGMA`           VARCHAR(32) DEFAULT NULL COMMENT '「sigma」- 统一标识',
    `METADATA`        TEXT COMMENT '「metadata」- 附加配置',
    `LANGUAGE`        VARCHAR(8)  DEFAULT NULL COMMENT '「language」- 使用的语言',

    -- Auditor字段
    `CREATED_AT`      DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`      VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`      DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`      VARCHAR(36) COMMENT '「updatedBy」- 更新人',
    PRIMARY KEY (`KEY`)
);

-- changeset Lang:ox-my_notify-2
ALTER TABLE MY_NOTIFY
    ADD UNIQUE (`APP_ID`, `OWNER_TYPE`, `OWNER_ID`);