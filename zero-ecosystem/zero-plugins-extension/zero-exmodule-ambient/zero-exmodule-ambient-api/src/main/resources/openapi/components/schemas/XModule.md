---
title: 模块
description: "模块 (X_MODULE)"
---

## Entity

```yaml
type: object
title: XModule
description: "模块 (X_MODULE)"
properties:
  id:
    type: string
    description: "主键"
  name:
    type: string
    description: "模块名称"
  code:
    type: string
    description: "模块编码"
  entry:
    type: string
    description: "模块入口地址"
  blockCode:
    type: string
    description: "所属模块系统编码"
  modelId:
    type: string
    description: "当前模块关联的主模型ID"
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

| 属性名        | 类型            | 说明     | 备注                 |
|:-----------|:--------------|:-------|:-------------------|
| `id`       | String        | 主键     |                    |
| `name`     | String        | 模块名称   |                    |
| `code`     | String        | 模块编码   |                    |
| `entry`    | String        | 入口地址   | 与 appId 联合唯一（页面入口） |
| `blockCode`| String        | 所属模块系统编码 |                    |
| `modelId`  | String        | 主模型ID  |                    |
| `sigma`    | String        | 统一标识   |                    |
| `language` | String        | 使用的语言  |                    |
| `active`   | Boolean       | 是否启用   |                    |
| `metadata` | String        | 元数据  |                    |
| `createdAt`| LocalDateTime | 创建时间   |                    |
| `createdBy`| String        | 创建人    |                    |
| `updatedAt`| LocalDateTime | 更新时间   |                    |
| `updatedBy`| String        | 更新人    |                    |
| `appId`    | String        | 应用ID   |                    |
| `tenantId` | String        | 租户ID   |                    |

## Relation

| 字段     | 关联实体  | 关系类型     | 说明   |
|:------|:-----|:---------|:-----|
| `appId` | XApp | Many-to-One | 应用 |
| `tenantId` | XTenant | Many-to-One | 租户 |
