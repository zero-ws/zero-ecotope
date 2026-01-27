-- liquibase formatted sql

-- changeset Lang:ox-trans-of-1
-- 关联表：F_TRANS_OF
DROP TABLE IF EXISTS F_TRANS_OF;
CREATE TABLE IF NOT EXISTS F_TRANS_OF
(
    /*
     * 一笔交易为一个核心交易对象模型，它和目标对象之间的关系如：
     * 类型有三：
     * 1. object_type = SETTLEMENT（针对结算单交易，直接交易）
     * 2. object_type = DEBT（针对应收单的交易）
     * 3. object_type = REFUND（针对应付单的交易）
     * 不论是哪种情况，都可以支持 1:1 和 1:N 的关系模型（针对对选的操作）
     */
    `TRANS_ID`
    VARCHAR
(
    36
) COMMENT '「transId」- 关联交易ID',
    `OBJECT_TYPE` VARCHAR
(
    64
) COMMENT '「objectType」- 交易目标类型',
    `OBJECT_ID` VARCHAR
(
    36
) COMMENT '「objectId」- 关联目标ID',
    `COMMENT` LONGTEXT COMMENT '「comment」 - 关联备注',
    PRIMARY KEY
(
    `TRANS_ID`,
    `OBJECT_TYPE`,
    `OBJECT_ID`
) USING BTREE
    );
-- changeset Lang:ox-trans-of-2
ALTER TABLE F_TRANS_OF
    ADD INDEX IDX_F_TRANS_OF_TRANS_ID (`TRANS_ID`) USING BTREE;