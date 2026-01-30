DROP TABLE IF EXISTS `R_COMPANY_CUSTOMER`;

CREATE TABLE IF NOT EXISTS `R_COMPANY_CUSTOMER` (
    -- ==================================================================================================
    -- 🔗 1. 关联主键区 (Composite Primary Key)
    -- ==================================================================================================
    `COMPANY_ID`      VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「companyId」- 企业ID',            -- [主键] 关联 S_COMPANY.ID (联合主键1)
    `CUSTOMER_ID`     VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「customerId」- 客户ID',            -- [主键] 关联 S_CUSTOMER.ID (联合主键2)

    -- ==================================================================================================
    -- ⚙️ 2. 关系属性区 (Relationship Attributes)
    -- ==================================================================================================
    `LINK_COMPONENT`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「linkComponent」- 关联组件',    -- 关联执行组件
    `COMMENT`         TEXT          COLLATE utf8mb4_bin COMMENT '「comment」- 备注',                          -- 关系备注

    -- ==================================================================================================
    -- ⚡ 7. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`COMPANY_ID`, `CUSTOMER_ID`) USING BTREE,                                                    -- [约束] 确保企业与客户的唯一绑定关系
    KEY `IDX_R_COMPANY_CUSTOMER_CUSTOMER_ID` (`CUSTOMER_ID`) USING BTREE                                      -- [查询] 反查客户关联的企业 (Customer -> Companies)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='公司 - 客户';