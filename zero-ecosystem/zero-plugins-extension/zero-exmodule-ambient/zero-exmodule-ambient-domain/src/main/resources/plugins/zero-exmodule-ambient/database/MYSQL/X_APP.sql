-- liquibase formatted sql

-- changeset Lang:ox-app-1
-- 应用程序表：X_APP
DROP TABLE IF EXISTS X_APP;
CREATE TABLE IF NOT EXISTS X_APP
(
    `KEY`        VARCHAR(36) COMMENT '「key」- 应用程序主键',
    `NAME`       VARCHAR(255) COMMENT '「name」- 应用程序名称',
    `CODE`       VARCHAR(36) COMMENT '「code」- 应用程序编码',

    `STATUS`     VARCHAR(255) COMMENT '「status」- 应用状态',
    `APP_SECRET` VARCHAR(128) COMMENT '「appSecret」- 专用密钥',
    `APP_KEY`    VARCHAR(128) COMMENT '「appKey」- 应用程序专用唯一hashKey',

    -- 常用属性
    `TITLE`      VARCHAR(64) COMMENT '「title」- 应用程序标题',
    `LOGO`       LONGTEXT COMMENT '「logo」- 应用程序图标',
    `ICP`        VARCHAR(64) COMMENT '「icp」- ICP备案号',
    `COPY_RIGHT` VARCHAR(255) COMMENT '「copyRight」- CopyRight版权信息',
    `EMAIL`      VARCHAR(255) COMMENT '「email」- 应用Email信息',

    -- 部署常用
    /*
     * 当前应用直接为一个独立部署应用时启用此处的几个属性来执行相关部署，此处属性必须是当前 App 是一个独立应用的模式，否则这些属性没有任何
     * 作用，独立应用模式对应的属性值：
     * - domain：部署的应用域名
     * - port：应用端口
     * - path：应用程序前端路径 /xxxx
     * 前端页面配置路径
     * - urlLogin：应用第二登录入口
     * - urlAdmin：应用主入口
     */
    `DOMAIN`     VARCHAR(255) COMMENT '「domain」- 应用程序所在域',
    `PORT`       INTEGER COMMENT '「port」- 应用程序端口号，和SOURCE的端口号区别开',
    `CONTEXT`    VARCHAR(255) COMMENT '「configure」- 应用程序路径',
    `URL_LOGIN`  VARCHAR(255) COMMENT '「urlLogin」— 应用程序入口页面（登录页）',
    `URL_ADMIN`  VARCHAR(255) COMMENT '「urlAdmin」- 应用程序内置主页（带安全）',

    /*
     * 新版后端专用属性：
     * - endpoint：后端API的根路径，用于发布接口专用
     * - entry：App 当前菜单入口，和 B_BLOCK 中的 entryId 维持一致，直接对接到当前环境中的 X_MENU 的 name 属性
     */
    `ENDPOINT`   VARCHAR(255) COMMENT '「endpoint」- 后端API的根路径，启动时需要',
    `ENTRY`      VARCHAR(255) COMMENT '「entry」- App 关联的入口菜单',

    -- ------------------------------ 公共字段 --------------------------------
    `SIGMA`      VARCHAR(128) COMMENT '「sigma」- 用户组绑定的统一标识',
    `LANGUAGE`   VARCHAR(10) COMMENT '「language」- 使用的语言',
    `ACTIVE`     BIT COMMENT '「active」- 是否启用',
    `METADATA`   TEXT COMMENT '「metadata」- 附加配置数据',

    -- Auditor字段
    `CREATED_AT` DATETIME COMMENT '「createdAt」- 创建时间',
    `CREATED_BY` VARCHAR(36) COMMENT '「createdBy」- 创建人',
    `UPDATED_AT` DATETIME COMMENT '「updatedAt」- 更新时间',
    `UPDATED_BY` VARCHAR(36) COMMENT '「updatedBy」- 更新人',

    `APP_ID`     VARCHAR(36) COMMENT '「appId」- 应用ID', -- 此处作为父应用
    `TENANT_ID`  VARCHAR(36) COMMENT '「tenantId」- 租户ID',
    PRIMARY KEY (`KEY`) USING BTREE
);

-- changeset Lang:ox-app-2
-- Unique JsonKeys：独立唯一键定义
ALTER TABLE X_APP
    ADD UNIQUE (`CODE`) USING BTREE;
ALTER TABLE X_APP
    ADD UNIQUE (`CONTEXT`, `URL_LOGIN`) USING BTREE; -- 应用唯一入口
ALTER TABLE X_APP
    ADD UNIQUE (`CONTEXT`, `URL_ADMIN`) USING BTREE; -- 应用唯一主页
ALTER TABLE X_APP
    ADD UNIQUE (`NAME`) USING BTREE;
-- 应用程序名称唯一（这是系统名称）

-- /app/name/:name
ALTER TABLE X_APP
    ADD INDEX IDX_X_APP_NAME (`NAME`) USING BTREE;