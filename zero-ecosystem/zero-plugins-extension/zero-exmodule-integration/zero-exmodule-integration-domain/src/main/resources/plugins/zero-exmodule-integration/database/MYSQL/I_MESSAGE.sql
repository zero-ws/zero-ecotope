-- liquibase formatted sql

-- changeset Lang:i-message-1
-- 消息队列：I_MESSAGE
DROP TABLE IF EXISTS I_MESSAGE;
CREATE TABLE IF NOT EXISTS I_MESSAGE
(
    `KEY`
    VARCHAR
(
    36
) COMMENT '「key」- 消息主键',
    `NAME` VARCHAR
(
    255
) COMMENT '「name」- 消息名称',
    `CODE` VARCHAR
(
    36
) COMMENT '「code」- 消息编码',

    `TYPE` VARCHAR
(
    255
) COMMENT '「type」- 消息类型',
    /*
     * P2P, Point 2 Point
     * -- status:
     *    PENDING -> SENT ( FAILED )
     * 1) Email, Sms etc. ( Reflect to field type )
     * 2) Capture the tpl based join `type` at the same time
     * -- status
     * - PENDING    等待发送
     * - SENT       已发送
     * - HISTORY    已读
     */
    `STATUS` VARCHAR
(
    255
) COMMENT '「status」- 消息状态',
    `SUBJECT` VARCHAR
(
    255
) COMMENT '「subject」- 消息标题',
    `CONTENT` LONGTEXT COMMENT '「content」- 消息内容',

    `SEND_FROM` VARCHAR
(
    255
) COMMENT '「from」- 消息发送方',
    `SEND_TO` VARCHAR
(
    255
) COMMENT '「to」- 消息接收方',

    `SEND_BY` VARCHAR
(
    36
) COMMENT '「sendBy」- 发送者',
    `SEND_AT` VARCHAR
(
    36
) COMMENT '「sendAt」- 发送时间',

    -- ------------------------------ 公共字段 --------------------------------
    `SIGMA` VARCHAR
(
    128
) COMMENT '「sigma」- 用户组绑定的统一标识',
    `LANGUAGE` VARCHAR
(
    10
) COMMENT '「language」- 使用的语言',
    `ACTIVE` BIT COMMENT '「active」- 是否启用',
    `METADATA` TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT` DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY` VARCHAR
(
    36
) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT` DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY` VARCHAR
(
    36
) COMMENT '「updatedBy」- 更新人',

    `APP_ID` VARCHAR
(
    36
) COMMENT '「appId」- 应用ID',
    `TENANT_ID` VARCHAR
(
    36
) COMMENT '「tenantId」- 租户ID',
    PRIMARY KEY
(
    `KEY`
) USING BTREE
    );

-- changeset Lang:i-message-2
ALTER TABLE I_MESSAGE
    ADD UNIQUE (`APP_ID`, `CODE`); -- 模板名称/编码
ALTER TABLE I_MESSAGE
    ADD UNIQUE (`APP_ID`, `NAME`);
ALTER TABLE I_MESSAGE
    ADD UNIQUE (`APP_ID`, `SEND_TO`, `SUBJECT`);