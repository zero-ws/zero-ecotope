---
title: 数据源
description: "数据源 (X_SOURCE)"
---

## Entity

```yaml
type: object
title: XSource
description: "数据源 (X_SOURCE)"
properties:
  id:
    type: string
    description: "主键"
  ipV4:
    type: string
    description: "IP v4 地址"
  ipV6:
    type: string
    description: "IP v6 地址"
  hostname:
    type: string
    description: "主机地址"
  port:
    type: integer
    description: "端口号"
  category:
    type: string
    description: "数据库类型"
  driverClassName:
    type: string
    description: "数据库驱动类名（JDBC4 之前）"
  jdbcUrl:
    type: string
    description: "JDBC 连接字符串"
  jdbcConfig:
    type: string
    description: "连接字符串中的配置 key=value"
  instance:
    type: string
    description: "实例名称"
  username:
    type: string
    description: "账号"
  password:
    type: string
    description: "密码"
  sigma:
    type: string
    description: "统一标识"
  language:
    type: string
    description: "语言"
  active:
    type: boolean
    description: "启用"
  metadata:
    type: string
    description: "元数据"
  createdAt:
    type: string
    format: date-time
    description: "创建时间"
  createdBy:
    type: string
    description: "创建人"
  updatedAt:
    type: string
    format: date-time
    description: "更新时间"
  updatedBy:
    type: string
    description: "更新人"
  appId:
    type: string
    description: "应用ID"
  tenantId:
    type: string
    description: "租户ID"
```

## Attribute

| 属性名              | 类型            | 说明     | 备注                 |
|:-----------------|:--------------|:-------|:-------------------|
| `id`             | String        | 主键     |                    |
| `ipV4`           | String        | IP v4  |                    |
| `ipV6`           | String        | IP v6  |                    |
| `hostname`       | String        | 主机地址  |                    |
| `port`           | Integer       | 端口号   |                    |
| `category`       | String        | 数据库类型 |                    |
| `driverClassName`| String        | 驱动类名  | JDBC4 之前           |
| `jdbcUrl`        | String        | JDBC URL | 最长 1024            |
| `jdbcConfig`     | String        | 连接配置  | TEXT                |
| `instance`       | String        | 实例名称  |                    |
| `username`       | String        | 账号     |                    |
| `password`      | String        | 密码     | 敏感                  |
| `sigma`          | String        | 统一标识  |                    |
| `language`       | String        | 语言   |                    |
| `active`         | Boolean       | 是否启用  |                    |
| `metadata`       | String        | 元数据  |                    |
| `createdAt`      | LocalDateTime | 创建时间  |                    |
| `createdBy`      | String        | 创建人   |                    |
| `updatedAt`      | LocalDateTime | 更新时间  |                    |
| `updatedBy`      | String        | 更新人   |                    |
| `appId`          | String        | 应用ID  | 与应用一对一（当前约定）     |
| `tenantId`       | String        | 租户ID  |                    |

## Relation

| 字段     | 关联实体  | 关系类型     | 说明     |
|:------|:-----|:---------|:-------|
| `appId` | XApp | Many-to-One | 所属应用（当前约定一对一） |
| `tenantId` | XTenant | Many-to-One | 租户 |
