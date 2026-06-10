DROP TABLE IF EXISTS `X_APP_INSTANCE`;
CREATE TABLE IF NOT EXISTS `X_APP_INSTANCE` (
    -- ==================================================================================================
    -- 1. 核心主键区 (Primary Key Strategy)
    -- ==================================================================================================
    `ID`                VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「id」- 主键',

    -- ==================================================================================================
    -- 2. 业务字段区 (Business Fields)
    -- ==================================================================================================
    `INSTANCE_NAME`     VARCHAR(100)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「instanceName」- 实例名称',
    `STATUS`            VARCHAR(20)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「status」- 实例状态',
    `DOCKER_IMAGE`      VARCHAR(200)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「dockerImage」- 镜像名',
    `DOCKER_CONTAINER`  VARCHAR(200)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「dockerContainer」- 容器名',
    `DATABASE_INSTANCE` VARCHAR(100)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「databaseInstance」- 数据库实例名',
    `RUNTIME_ROOT`      VARCHAR(500)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「runtimeRoot」- 运行时目录',
    `INSTANCE_URL`      VARCHAR(500)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「instanceUrl」- 实例访问地址',
    `VERSION`           VARCHAR(50)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「version」- 应用版本',
    `COMPOSITE_APP`     BIT(1)        DEFAULT NULL COMMENT '「compositeApp」- 是否组合应用',

    -- ==================================================================================================
    -- 3. 安装与部署来源 (Installation & Deployment Source)
    -- ==================================================================================================
    `INSTALL_ROOT`         VARCHAR(500)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「installRoot」- 安装产物根路径',
    `RELEASE_ID`           VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「releaseId」- 发布记录主键',
    `PACKAGE_ID`           VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「packageId」- 上传包记录主键',
    `PACKAGE_CHECKSUM`     VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「packageChecksum」- 安装包校验和',
    `DEPLOY_MODE`          VARCHAR(32)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「deployMode」- 部署模式',
    `CONFIG_HASH`          VARCHAR(128)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「configHash」- 运行配置摘要',

    -- ==================================================================================================
    -- 4. 结构化入口与多租户 (Entry & Multi-Tenancy)
    -- ==================================================================================================
    `DOMAIN`           VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「domain」- 实例域名',
    `PORT`             INTEGER       DEFAULT NULL COMMENT '「port」- 实例端口',
    `CONTEXT`          VARCHAR(255)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「context」- 实例上下文路径',
    `SIGMA`            VARCHAR(128)  COLLATE utf8mb4_bin NOT NULL COMMENT '「sigma」- 统一标识',
    `APP_ID`           VARCHAR(36)   COLLATE utf8mb4_bin NOT NULL COMMENT '「appId」- 应用ID',
    `TENANT_ID`        VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「tenantId」- 实例所属租户主键',

    -- ==================================================================================================
    -- 5. 健康与生命周期 (Health & Lifecycle)
    -- ==================================================================================================
    `HEALTH_STATUS`    VARCHAR(32)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「healthStatus」- 最近健康状态',
    `HEALTH_AT`        DATETIME      DEFAULT NULL COMMENT '「healthAt」- 最近健康检查时间',
    `DEPLOYED_AT`      DATETIME      DEFAULT NULL COMMENT '「deployedAt」- 最近部署完成时间',
    `STARTED_AT`       DATETIME      DEFAULT NULL COMMENT '「startedAt」- 最近启动成功时间',
    `STOPPED_AT`       DATETIME      DEFAULT NULL COMMENT '「stoppedAt」- 最近停止/卸载时间',

    -- --------------------------------------------------------------------------------------------------
    `ACTIVE`            BIT(1)        DEFAULT NULL COMMENT '「active」- 是否启用',
    `LANGUAGE`          VARCHAR(10)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「language」- 语言偏好',
    `METADATA`          TEXT          COLLATE utf8mb4_bin COMMENT '「metadata」- 扩展元数据',
    -- ==================================================================================================
    `CREATED_AT`        DATETIME      DEFAULT NULL COMMENT '「createdAt」- 创建时间',
    `CREATED_BY`        VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「createdBy」- 创建人',
    `UPDATED_AT`        DATETIME      DEFAULT NULL COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY`        VARCHAR(36)   COLLATE utf8mb4_bin DEFAULT NULL COMMENT '「updatedBy」- 更新人',

    -- ==================================================================================================
    -- 6. 索引定义 (Index Definition)
    -- ==================================================================================================
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE KEY `UK_X_APP_INSTANCE_APP_SIGMA` (`APP_ID`, `SIGMA`) USING BTREE,
    KEY `IDX_X_APP_INSTANCE_STATUS` (`STATUS`) USING BTREE,
    KEY `IDX_X_APP_INSTANCE_SIGMA` (`SIGMA`) USING BTREE,
    KEY `IDX_X_APP_INSTANCE_TENANT_ID` (`TENANT_ID`) USING BTREE,
    KEY `IDX_X_APP_INSTANCE_APP_TENANT` (`APP_ID`, `TENANT_ID`) USING BTREE,
    KEY `IDX_X_APP_INSTANCE_RELEASE` (`RELEASE_ID`) USING BTREE,
    KEY `IDX_X_APP_INSTANCE_HEALTH` (`HEALTH_STATUS`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT='X_APP_INSTANCE';
