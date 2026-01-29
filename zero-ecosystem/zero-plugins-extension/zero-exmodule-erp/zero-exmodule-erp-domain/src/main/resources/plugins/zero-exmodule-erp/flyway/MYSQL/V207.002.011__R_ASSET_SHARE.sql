DROP TABLE IF EXISTS `R_ASSET_SHARE`;

CREATE TABLE IF NOT EXISTS `R_ASSET_SHARE` (
    -- ==================================================================================================
    -- 🔗 1. 关联主键区 (Composite Primary Key)
    -- ==================================================================================================
    `ASSET_ID`      VARCHAR(36)  NOT NULL COLLATE utf8mb4_bin COMMENT '「assetId」- 资产ID',             -- [主键] 关联 S_ASSET.ID (联合主键1)
    `ENTITY_TYPE`   VARCHAR(64)  NOT NULL COLLATE utf8mb4_bin COMMENT '「entityType」- 关联类型',        -- [主键] 关联实体类型 (如: USER, DEPT) (联合主键2)
    `ENTITY_ID`     VARCHAR(36)  NOT NULL COLLATE utf8mb4_bin COMMENT '「entityId」- 实体ID',            -- [主键] 关联实体表主键 (联合主键3)

    -- ==================================================================================================
    -- ⚡ 7. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ASSET_ID`, `ENTITY_TYPE`, `ENTITY_ID`) USING BTREE,                                 -- [约束] 确保资产与特定实体的唯一对应关系
    KEY `IDX_R_ASSET_SHARE_ENTITY` (`ENTITY_TYPE`, `ENTITY_ID`) USING BTREE                           -- [查询] 反查某实体(人/部门)拥有的资产
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='资产 - 实体共享';