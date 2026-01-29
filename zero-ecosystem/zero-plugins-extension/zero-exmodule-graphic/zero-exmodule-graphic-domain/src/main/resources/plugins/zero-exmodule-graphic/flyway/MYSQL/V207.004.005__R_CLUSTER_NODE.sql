DROP TABLE IF EXISTS `R_CLUSTER_NODE`;

CREATE TABLE IF NOT EXISTS `R_CLUSTER_NODE` (
    -- ==================================================================================================
    -- 🔗 1. 关联主键区 (Composite Primary Key)
    -- ==================================================================================================
    `CLUSTER_ID`    VARCHAR(36)  NOT NULL COLLATE utf8mb4_bin COMMENT '「clusterId」- 集群ID',            -- [主键] 关联 cluster (联合主键1)
    `NODE_ID`       VARCHAR(36)  NOT NULL COLLATE utf8mb4_bin COMMENT '「nodeId」- 节点ID',               -- [主键] 关联 node (联合主键2)

    -- ==================================================================================================
    -- ⚡ 7. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`CLUSTER_ID`, `NODE_ID`) USING BTREE,                                                -- [约束] 确保集群与节点的唯一绑定关系
    KEY `IDX_R_CLUSTER_NODE_NODE_ID` (`NODE_ID`) USING BTREE                                          -- [查询] 反查节点所属的集群 (Node -> Clusters)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='集群 - 节点';