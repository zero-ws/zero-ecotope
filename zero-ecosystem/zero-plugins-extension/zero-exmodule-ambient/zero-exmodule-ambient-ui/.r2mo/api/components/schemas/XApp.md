---
title: 应用
description: "应用基本信息实体 (X_APP)"
---

## Entity

```yaml
type: object
title: XApp
description: "应用基本信息实体 (X_APP)"
properties:
  id:
    type: string
    description: "主键"
  code:
    type: string
    description: "编号"
  name:
    type: string
    description: "名称"
  title:
    type: string
    description: "应用标题"
  copyRight:
    type: string
    description: "版权"
  email:
    type: string
    description: "管理员Email"
  icp:
    type: string
    description: "ICP备案号"
  logo:
    type: string
    description: "图标"
  favicon:
    type: string
    description: "小图标"
  entry:
    type: string
    description: "入口菜单"
  domain:
    type: string
    description: "服务器域"
  port:
    type: integer
    description: "端口号"
  context:
    type: string
    description: "应用路径"
  endpoint:
    type: string
    description: "Web服务端地址"
  urlAdmin:
    type: string
    description: "管理页URL"
  urlLogin:
    type: string
    description: "登录页URL"
  urlHealth:
    type: string
    description: "健康检查URL"
  appKey:
    type: string
    description: "敏感标识符"
  appSecret:
    type: string
    description: "应用密钥"
  status:
    type: string
    description: "状态"
  namespace:
    type: string
    description: "名空间"
  sigma:
    type: string
    description: "统一标识"
  tenantId:
    type: string
    description: "租户ID"
  appId:
    type: string
    description: "父应用ID"
  active:
    type: boolean
    description: "是否启用"
  language:
    type: string
    description: "语言偏好"
  metadata:
    type: string
    description: "元配置 (JSON String)"
  version:
    type: string
    description: "版本号"
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
```

## Attribute

| 属性名         | 类型            | 说明       | 备注               |
|:------------|:--------------|:---------|:-----------------|
| `id`        | String        | 主键       |                  |
| `code`      | String        | 编号       |                  |
| `name`      | String        | 名称       |                  |
| `title`     | String        | 应用标题     |                  |
| `copyRight` | String        | 版权       |                  |
| `email`     | String        | 管理员Email |                  |
| `icp`       | String        | ICP备案号   |                  |
| `logo`      | String        | 图标       |                  |
| `favicon`   | String        | 小图标      |                  |
| `entry`     | String        | 入口菜单     |                  |
| `domain`    | String        | 服务器域     |                  |
| `port`      | Integer       | 端口号      |                  |
| `context`   | String        | 应用路径     |                  |
| `endpoint`  | String        | Web服务端地址 |                  |
| `urlAdmin`  | String        | 管理页URL   |                  |
| `urlLogin`  | String        | 登录页URL   |                  |
| `urlHealth` | String        | 健康检查URL  |                  |
| `appKey`    | String        | 敏感标识符    |                  |
| `appSecret` | String        | 应用密钥     |                  |
| `status`    | String        | 状态       |                  |
| `namespace` | String        | 名空间      |                  |
| `sigma`     | String        | 统一标识     |                  |
| `tenantId`  | String        | 租户ID     |                  |
| `appId`     | String        | 父应用ID    | 自关联 (Parent App) |
| `active`    | Boolean       | 是否启用     |                  |
| `language`  | String        | 语言偏好     |                  |
| `metadata`  | String        | 元配置      | 通常存储 Json 结构字符串  |
| `version`   | String        | 版本号      |                  |
| `createdAt` | LocalDateTime | 创建时间     |                  |
| `createdBy` | String        | 创建人      |                  |
| `updatedAt` | LocalDateTime | 更新时间     |                  |
| `updatedBy` | String        | 更新人      |                  |

## Relation

| 字段         | 关联实体      | 关系类型               | 说明                     |
|:-----------|:----------|:-------------------|:-----------------------|
| `appId`    | `XApp`    | One-to-Many / Self | 父子应用关系，指向父应用的 ID       |
| `tenantId` | `XTenant` | Many-to-One        | 所属租户 (推测关联 X_TENANT 表) |