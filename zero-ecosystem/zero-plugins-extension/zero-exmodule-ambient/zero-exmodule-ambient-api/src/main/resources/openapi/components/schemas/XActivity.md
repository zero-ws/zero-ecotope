---
title: 活动/变更记录
description: "活动/变更记录 (X_ACTIVITY)"
---

## Entity

```yaml
type: object
title: XActivity
description: "活动/变更记录 (X_ACTIVITY)"
properties:
  id:
    type: string
    description: "主键"
  type:
    type: string
    description: "类型"
  serial:
    type: string
    description: "记录号"
  description:
    type: string
    description: "描述"
  modelId:
    type: string
    description: "模型标识"
  modelKey:
    type: string
    description: "模型记录ID"
  modelCategory:
    type: string
    description: "业务类别"
  taskName:
    type: string
    description: "任务名称"
  taskSerial:
    type: string
    description: "任务单号"
  recordOld:
    type: string
    description: "变更前"
  recordNew:
    type: string
    description: "变更后"
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

| 属性名           | 类型            | 说明   | 备注 |
|:--------------|:--------------|:-----|:---|
| `id`          | String        | 主键   | - |
| `type`        | String        | 类型   | - |
| `serial`      | String        | 记录号  | - |
| `description` | String        | 描述   | - |
| `modelId`     | String        | 模型标识 | - |
| `modelKey`    | String        | 模型记录ID | - |
| `modelCategory` | String      | 业务类别 | - |
| `taskName`    | String        | 任务名称 | - |
| `taskSerial`  | String        | 任务单号 | - |
| `recordOld`   | String        | 变更前  | - |
| `recordNew`   | String        | 变更后  | - |
| `sigma`       | String        | 统一标识 | - |
| `language`    | String        | 语言   | - |
| `active`      | Boolean       | 启用   | - |
| `metadata`    | String        | 元数据  | - |
| `createdAt`   | LocalDateTime | 创建时间 | - |
| `createdBy`   | String        | 创建人  | - |
| `updatedAt`   | LocalDateTime | 更新时间 | - |
| `updatedBy`   | String        | 更新人  | - |
| `appId`       | String        | 应用ID | - |
| `tenantId`    | String        | 租户ID | - |

## Relation

| 字段    | 关联实体  | 关系类型    | 说明 |
|:-----|:-----|:--------|:---|
| `appId` | XApp | Many-to-One | 应用 |
| `tenantId` | XTenant | Many-to-One | 租户 |
