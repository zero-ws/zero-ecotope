---
title: 菜单
description: "菜单 (X_MENU)"
---

## Entity

```yaml
type: object
title: XMenu
description: "菜单 (X_MENU)"
properties:
  id:
    type: string
    description: "主键"
  name:
    type: string
    description: "菜单名称"
  icon:
    type: string
    description: "菜单 icon"
  text:
    type: string
    description: "菜单显示文字"
  uri:
    type: string
    description: "菜单地址（不包含应用 path）"
  type:
    type: string
    description: "菜单类型"
  order:
    type: integer
    format: int64
    description: "菜单排序"
  level:
    type: integer
    format: int64
    description: "菜单层级"
  parentId:
    type: string
    description: "菜单父ID"
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

| 属性名        | 类型            | 说明   | 备注               |
|:-----------|:--------------|:-----|:-----------------|
| `id`       | String        | 主键   |                  |
| `name`     | String        | 菜单名称 | - |
| `icon`     | String        | 菜单 icon |                  |
| `text`     | String        | 显示文字 |                  |
| `uri`      | String        | 菜单地址 | 不包含应用 path       |
| `type`     | String        | 菜单类型 |                  |
| `order`    | Long          | 排序   |                  |
| `level`    | Long          | 层级   |                  |
| `parentId` | String        | 父菜单ID |                  |
| `sigma`    | String        | 统一标识 |                  |
| `language` | String        | 语言   | - |
| `active`   | Boolean       | 启用   | - |
| `metadata` | String        | 元数据  | - |
| `createdAt`| LocalDateTime | 创建时间 |                  |
| `createdBy`| String        | 创建人  |                  |
| `updatedAt`| LocalDateTime | 更新时间 |                  |
| `updatedBy`| String        | 更新人  |                  |
| `appId`    | String        | 应用ID |                  |
| `tenantId` | String        | 租户ID |                  |

## Relation

| 字段        | 关联实体  | 关系类型               | 说明     |
|:---------|:-----|:-------------------|:-------|
| `parentId` | XMenu | Many-to-One / Self | 父菜单 |
| `appId`  | XApp | Many-to-One        | 应用   |
| `tenantId` | XTenant | Many-to-One      | 租户   |
