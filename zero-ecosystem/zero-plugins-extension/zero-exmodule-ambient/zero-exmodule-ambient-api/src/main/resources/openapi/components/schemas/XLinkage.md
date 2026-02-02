---
title: 关联/链接
description: "关联 (X_LINKAGE)"
---

## Entity

```yaml
type: object
title: XLinkage
description: "关联 (X_LINKAGE)"
properties:
  id:
    type: string
    description: "主键"
  name:
    type: string
    description: "名称"
  type:
    type: string
    description: "连接类型"
  alias:
    type: string
    description: "别称"
  region:
    type: string
    description: "区域"
  linkKey:
    type: string
    description: "关联Key"
  linkType:
    type: string
    description: "关系类型"
  linkData:
    type: string
    description: "关联数据"
  sourceKey:
    type: string
    description: "源Key"
  sourceType:
    type: string
    description: "源类型"
  sourceData:
    type: string
    description: "源数据"
  targetKey:
    type: string
    description: "目标Key"
  targetType:
    type: string
    description: "目标类型"
  targetData:
    type: string
    description: "目标数据"
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

| 属性名        | 类型            | 说明   | 备注                    |
|:-----------|:--------------|:-----|:----------------------|
| `id`       | String        | 主键   | - |
| `name`     | String        | 名称   | - |
| `type`     | String        | 连接类型 | - |
| `alias`    | String        | 别称   | - |
| `region`   | String        | 区域   | - |
| `linkKey`  | String        | 关联Key | - |
| `linkType` | String        | 关系类型 | - |
| `linkData` | String        | 关联数据 | - |
| `sourceKey`  | String      | 源Key  | - |
| `sourceType` | String      | 源类型  | - |
| `sourceData` | String      | 源数据  | - |
| `targetKey`  | String      | 目标Key | - |
| `targetType` | String      | 目标类型 | - |
| `targetData` | String      | 目标数据 | - |
| `sigma`    | String        | 统一标识 | - |
| `language` | String        | 语言   | - |
| `active`   | Boolean       | 启用   | - |
| `metadata` | String        | 元数据  | - |
| `createdAt`| LocalDateTime | 创建时间 | - |
| `createdBy`| String        | 创建人  | - |
| `updatedAt`| LocalDateTime | 更新时间 | - |
| `updatedBy`| String        | 更新人  | - |
| `appId`    | String        | 应用ID | - |
| `tenantId` | String        | 租户ID | - |

## Relation

| 字段   | 关联实体  | 关系类型    | 说明     |
|:----|:-----|:--------|:-------|
| `appId` | XApp | Many-to-One | 应用 |
| `tenantId` | XTenant | Many-to-One | 租户 |
