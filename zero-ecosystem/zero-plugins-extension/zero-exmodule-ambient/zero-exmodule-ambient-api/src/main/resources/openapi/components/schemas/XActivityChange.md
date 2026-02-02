---
title: 活动变更明细
description: "活动变更明细 (X_ACTIVITY_CHANGE)"
---

## Entity

```yaml
type: object
title: XActivityChange
description: "活动变更明细 (X_ACTIVITY_CHANGE)"
properties:
  id:
    type: string
    description: "主键"
  activityId:
    type: string
    description: "活动ID"
  type:
    type: string
    description: "变更类型"
  status:
    type: string
    description: "状态"
  fieldName:
    type: string
    description: "字段名"
  fieldAlias:
    type: string
    description: "字段别名"
  fieldType:
    type: string
    description: "字段类型"
  valueOld:
    type: string
    description: "旧值"
  valueNew:
    type: string
    description: "新值"
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

| 属性名         | 类型            | 说明   | 备注 |
|:------------|:--------------|:-----|:---|
| `id`        | String        | 主键   | - |
| `activityId` | String       | 活动ID | - |
| `type`      | String        | 变更类型 | - |
| `status`    | String        | 状态   | - |
| `fieldName` | String        | 字段名  | - |
| `fieldAlias`| String        | 字段别名 | - |
| `fieldType` | String        | 字段类型 | - |
| `valueOld`  | String        | 旧值   | - |
| `valueNew`  | String        | 新值   | - |
| `sigma`     | String        | 统一标识 | - |
| `language`  | String        | 语言   | - |
| `active`    | Boolean       | 启用   | - |
| `metadata`  | String        | 元数据  | - |
| `createdAt` | LocalDateTime | 创建时间 | - |
| `createdBy` | String        | 创建人  | - |
| `updatedAt` | LocalDateTime | 更新时间 | - |
| `updatedBy` | String        | 更新人  | - |
| `appId`     | String        | 应用ID | - |
| `tenantId`  | String        | 租户ID | - |

## Relation

| 字段         | 关联实体   | 关系类型     | 说明 |
|:----------|:-------|:---------|:---|
| `activityId` | XActivity | Many-to-One | 活动 |
