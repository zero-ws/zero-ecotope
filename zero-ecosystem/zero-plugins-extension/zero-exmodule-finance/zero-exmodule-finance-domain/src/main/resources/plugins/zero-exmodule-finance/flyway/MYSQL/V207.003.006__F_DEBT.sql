-- liquibase formatted sql


-- changeset Lang:f-debt-1
DROP TABLE IF EXISTS `F_DEBT`;
CREATE TABLE `F_DEBT`
(
    `KEY`            VARCHAR(36) COMMENT '「key」- 应收账单主键ID',
    `NAME`           VARCHAR(255) DEFAULT NULL COMMENT '「name」 - 应收单标题',
    `CODE`           VARCHAR(255)   NOT NULL COMMENT '「code」 - 应收单编号',
    `SERIAL`         VARCHAR(36)    NOT NULL COMMENT '「serial」 - 应收单据号',

    -- 追加 type / status
    /*
     * 不再根据价格来判断是：应收还是付款
     * type = REFUND 退款，DEBT 应收
     */
    `TYPE`           VARCHAR(64) COMMENT '「type」- 类型',

    -- 基本信息
    `AMOUNT`         DECIMAL(18, 2) NOT NULL COMMENT '「amount」——价税合计，所有明细对应的实际结算金额',
    /*
     * 如果生成应收单，那么此处的剩余金额先不填写，剩余金额在处理应收时第一次填写，
     * 如果完全处理，那么剩余金额为0，如果不完全处理，那么剩余金额为计算结果，下一次
     * 处理应收时会使用。
     * 1）未完成的应收：剩余金额为下次应收金额，finished = false
     *    这种应收还可选
     * 2）已完成的应收：剩余金额为0，finished = true
     *    这种应收不可选
     * 这个字段是为了应收多次处理、跨应收处理交易而设计，本来可直接计算，但为了读取过程中
     * 不出现反复读取，所以设计此字段和完成状态对接。
     */
    `AMOUNT_BALANCE` DECIMAL(18, 2) NOT NULL COMMENT '「amountBalance」——剩余金额',
    `FINISHED`       BIT COMMENT '「finished」- 是否完成',
    `FINISHED_AT`    DATETIME COMMENT '「createdAt」- 完成时间',

    `SIGN_NAME`      VARCHAR(128) DEFAULT NULL COMMENT '「signName」签单人姓名',
    `SIGN_MOBILE`    VARCHAR(128) DEFAULT NULL COMMENT '「signMobile」签单人电话',

    `START_AT`       DATETIME COMMENT '「startAt」- 开始时间',
    `END_AT`         DATETIME COMMENT '「endAt」- 结束时间',
    `GROUP_BY`       VARCHAR(64) COMMENT '「groupBy」- 分组',

    `COMMENT`        LONGTEXT COMMENT '「comment」 - 备注',

    -- 关联信息
    `CUSTOMER_ID`    VARCHAR(36)  DEFAULT NULL COMMENT '「customerId」结算对象（单位ID）',

    /*
     * 此处改成跨结算单生成应收/应退
     * 所以不再直接关联结算单，应收单中包含的所有结算明细
     * 可以来自不同结算单
     */
    -- `SETTLEMENT_ID` VARCHAR(36) UNIQUE COMMENT '「settlementId」- 结算单ID，该字段有值标识已经结算',

    -- ------------------------------ 公共字段 --------------------------------
    `SIGMA`          VARCHAR(128) COMMENT '「sigma」- 用户组绑定的统一标识',
    `LANGUAGE`       VARCHAR(10) COMMENT '「language」- 使用的语言',
    `ACTIVE`         BIT COMMENT '「active」- 是否启用',
    `METADATA`       TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT`     DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`     VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`     DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`     VARCHAR(36) COMMENT '「updatedBy」- 更新人',

    `APP_ID`         VARCHAR(36) COMMENT '「appId」- 应用ID',
    `TENANT_ID`      VARCHAR(36) COMMENT '「tenantId」- 租户ID',
    PRIMARY KEY (`KEY`) USING BTREE
);
-- changeset Lang:f-debt-2
ALTER TABLE F_DEBT
    ADD UNIQUE (`CODE`, `SIGMA`);
ALTER TABLE F_DEBT
    ADD UNIQUE (`SERIAL`, `SIGMA`);