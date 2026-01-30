-- liquibase formatted sql

-- changeset Lang:ox-trans-of-1
DROP TABLE IF EXISTS `F_TRANS_OF`;

CREATE TABLE IF NOT EXISTS `F_TRANS_OF` (
    -- ==================================================================================================
    -- 🔗 1. 关联主键区 (Composite Primary Key)
    -- ==================================================================================================
    `TRANS_ID`      VARCHAR(36)  NOT NULL COLLATE utf8mb4_bin COMMENT '「transId」- 交易ID',              -- 关联交易ID
    `OBJECT_TYPE`   VARCHAR(64)  NOT NULL COLLATE utf8mb4_bin COMMENT '「objectType」- 目标类型',          -- [枚举] SETTLEMENT(结算), DEBT(应收), REFUND(应付)
    `OBJECT_ID`     VARCHAR(36)  NOT NULL COLLATE utf8mb4_bin COMMENT '「objectId」- 目标ID',              -- 关联目标ID

    -- ==================================================================================================
    -- 📝 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `COMMENT`       LONGTEXT     COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「comment」- 备注',            -- 关联备注

    -- ==================================================================================================
    -- ⚡ 7. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`TRANS_ID`, `OBJECT_TYPE`, `OBJECT_ID`) USING BTREE,
    KEY `IDX_F_TRANS_OF_TRANS_ID` (`TRANS_ID`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='财务 - 交易关联';

-- 缺失公共字段：
-- - CREATED_AT (创建时间)
-- - CREATED_BY (创建人)
-- - UPDATED_AT (更新时间)
-- - UPDATED_BY (更新人)
-- - ACTIVE (是否启用)
-- - LANGUAGE (语言)
-- - VERSION (版本)
-- - METADATA (元配置)
-- - SIGMA (统一标识)
-- - APP_ID (所属应用)
-- - TENANT_ID (所属租户)
-- - TYPE (类型)