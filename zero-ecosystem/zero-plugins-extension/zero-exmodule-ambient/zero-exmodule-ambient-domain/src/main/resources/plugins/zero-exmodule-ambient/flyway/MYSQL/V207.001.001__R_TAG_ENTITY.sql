DROP TABLE IF EXISTS `R_TAG_ENTITY`;
/*
 * 标签关联表，用于设置标签和实际对象的关联详情
   - 广义关联
     ENTITY_TYPE + ENTITY_ID
     实体类型（对应表）+ 实体ID（对应记录）
   - 关联规则
     LINK_COMPONENT + LINK_CONFIG
     关联组件 + 配置
   静态关联模型直接使用
 */
CREATE TABLE IF NOT EXISTS `R_TAG_ENTITY` (
    -- ==================================================================================================
    -- 🔗 1. 关联主键区 (Composite Primary Key)
    -- ==================================================================================================
    `TAG_ID`          VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「tagId」- 标签ID',                 -- [主键] 关联 X_TAG.ID (联合主键1)
    `ENTITY_TYPE`     VARCHAR(64)   COLLATE utf8mb4_bin NOT NULL COMMENT '「entityType」- 实体类型',           -- [主键] 关联实体的类型 (联合主键2)
    `ENTITY_ID`       VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「entityId」- 实体ID',              -- [主键] 关联实体的主键 (联合主键3)

    -- ==================================================================================================
    -- ⚙️ 2. 关系属性区 (Relationship Attributes)
    -- ==================================================================================================
    `LINK_COMPONENT`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「linkComponent」- 执行组件',   -- 关联执行组件
    `LINK_CONFIG`     TEXT          COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「linkConfig」- 执行配置',
    `COMMENT`         TEXT          COLLATE utf8mb4_bin COMMENT '「comment」- 关联备注',                         

    -- ==================================================================================================
    -- ⚡ 7. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`TAG_ID`, `ENTITY_TYPE`, `ENTITY_ID`) USING BTREE                                            -- [约束] 确保标签与实体的唯一绑定关系
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='标签-实体';