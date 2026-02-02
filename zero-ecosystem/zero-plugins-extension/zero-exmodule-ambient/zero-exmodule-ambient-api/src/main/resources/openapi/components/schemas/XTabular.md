---
title: 列表/表格类型
description: "列表/表格 (X_TABULAR)"
---

## Entity

```yaml
type: object
title: XTabular
description: "列表/表格 (X_TABULAR)"
properties:
  id:
    type: string
    description: "主键"
  name:
    type: string
    description: "列表名称"
  code:
    type: string
    description: "列表编号"
  type:
    type: string
    description: "列表类型"
  icon:
    type: string
    description: "列表图标"
  sort:
    type: integer
    description: "排序信息"
  comment:
    type: string
    description: "备注信息"
  runComponent:
    type: string
    description: "执行组件"
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

| 属性名          | 类型            | 说明   | 备注                    |
|:-------------|:--------------|:-----|:----------------------|
| `id`         | String        | 主键   |                       |
| `name`       | String        | 列表名称 |                       |
| `code`       | String        | 列表编号 | appId+type+code / sigma+type+code 唯一 |
| `type`       | String        | 列表类型 |                       |
| `icon`       | String        | 列表图标 |                       |
| `sort`       | Integer       | 排序   |                       |
| `comment`    | String        | 备注信息 |                       |
| `runComponent` | String      | 执行组件 |                       |
| `sigma`      | String        | 统一标识 |                       |
| `language`   | String        | 语言   |                       |
| `active`     | Boolean       | 启用   |                       |
| `metadata`   | String        | 元数据  |                       |
| `createdAt`  | LocalDateTime | 创建时间 |                       |
| `createdBy`  | String        | 创建人  |                       |
| `updatedAt`  | LocalDateTime | 更新时间 |                       |
| `updatedBy`  | String        | 更新人  |                       |
| `appId`      | String        | 应用ID |                       |
| `tenantId`   | String        | 租户ID |                       |

## Relation

| 字段     | 关联实体  | 关系类型     | 说明   |
|:------|:-----|:---------|:-----|
| `appId` | XApp | Many-to-One | 应用 |
| `tenantId` | XTenant | Many-to-One | 租户 |
