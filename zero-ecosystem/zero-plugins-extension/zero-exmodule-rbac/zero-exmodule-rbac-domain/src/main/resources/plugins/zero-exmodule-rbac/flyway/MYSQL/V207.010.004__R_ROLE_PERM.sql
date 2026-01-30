DROP TABLE IF EXISTS `R_ROLE_PERM`;

CREATE TABLE IF NOT EXISTS `R_ROLE_PERM` (
    -- ==================================================================================================
    -- 🔗 1. 关联主键区 (Composite Primary Key)
    -- ==================================================================================================
    `PERM_ID`   VARCHAR(36)  COLLATE utf8mb4_bin NOT NULL COMMENT '「permId」- 权限ID',                  -- [主键] 关联 S_PERM.ID (联合主键1)
    `ROLE_ID`   VARCHAR(36)  COLLATE utf8mb4_bin NOT NULL COMMENT '「roleId」- 角色ID',                  -- [主键] 关联 S_ROLE.ID (联合主键2)

    -- ==================================================================================================
    -- ⚡ 7. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`PERM_ID`, `ROLE_ID`) USING BTREE,                                                   -- [约束] 确保角色与权限的唯一绑定关系
    KEY `IDX_R_ROLE_PERM_ROLE_ID` (`ROLE_ID`) USING BTREE                                             -- [查询] 反查角色拥有的权限 (Role -> Perms)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='角色 - 权限';