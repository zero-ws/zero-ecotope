DROP TABLE IF EXISTS `R_GROUP_ROLE`;

CREATE TABLE IF NOT EXISTS `R_GROUP_ROLE` (
    -- ==================================================================================================
    -- 🔗 1. 关联主键区 (Composite Primary Key)
    -- ==================================================================================================
    `GROUP_ID`  VARCHAR(36)  COLLATE utf8mb4_bin NOT NULL COMMENT '「groupId」- 关联组ID',                    -- [主键] 关联 group (联合主键1),
    `ROLE_ID`   VARCHAR(36)  COLLATE utf8mb4_bin NOT NULL COMMENT '「roleId」- 关联角色ID',                   -- [主键] 关联 role (联合主键2),

    -- ==================================================================================================
    -- ⚙️ 2. 关系属性区 (Relationship Attributes)
    -- ==================================================================================================
    `PRIORITY`  INTEGER      DEFAULT NULL COMMENT '「priority」- 优先级',                                     -- [排序] 决定多组并存时的权限继承顺序 (数值越小优先级越高),

    -- ==================================================================================================
    -- ⚡ 7. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`GROUP_ID`, `ROLE_ID`) USING BTREE                                                           -- [约束] 确保组与用户的唯一绑定关系
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='关联表';