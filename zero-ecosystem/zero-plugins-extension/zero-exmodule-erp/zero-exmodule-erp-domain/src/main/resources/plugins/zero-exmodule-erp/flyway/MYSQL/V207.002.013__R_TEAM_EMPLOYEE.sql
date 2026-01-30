DROP TABLE IF EXISTS `R_TEAM_EMPLOYEE`;

CREATE TABLE IF NOT EXISTS `R_TEAM_EMPLOYEE` (
    -- ==================================================================================================
    -- 🔗 1. 关联主键区 (Composite Primary Key)
    -- ==================================================================================================
    `TEAM_ID`         VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「teamId」- 组ID',                 -- [主键] 关联 S_TEAM.ID (联合主键1)
    `EMPLOYEE_ID`     VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「employeeId」- 员工ID',           -- [主键] 关联 E_EMPLOYEE.ID (联合主键2)

    -- ==================================================================================================
    -- ⚙️ 2. 关系属性区 (Relationship Attributes)
    -- ==================================================================================================
    `LINK_COMPONENT`  VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「linkComponent」- 关联组件',    -- 关联执行组件
    `COMMENT`         TEXT          COLLATE utf8mb4_bin COMMENT '「comment」- 备注',                          -- 关系备注

    -- ==================================================================================================
    -- ⚡ 7. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`TEAM_ID`, `EMPLOYEE_ID`) USING BTREE,                                                       -- [约束] 确保团队与员工的唯一绑定关系
    KEY `IDX_R_TEAM_EMPLOYEE_EMPLOYEE_ID` (`EMPLOYEE_ID`) USING BTREE                                         -- [查询] 反查员工所属的团队 (Employee -> Teams)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='团队 - 员工';