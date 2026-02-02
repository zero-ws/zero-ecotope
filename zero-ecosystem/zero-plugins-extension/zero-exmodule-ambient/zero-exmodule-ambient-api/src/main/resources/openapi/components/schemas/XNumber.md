---
title: 编号/发号器
description: "编号 (X_NUMBER)"
---

## Entity

```yaml
type: object
title: XNumber
description: "编号 (X_NUMBER)"
properties:
  id:
    type: string
    description: "主键"
  code:
    type: string
    description: "编码"
  comment:
    type: string
    description: "编号备注"
  current:
    type: integer
    format: int64
    description: "当前值，对应 seed"
  format:
    type: string
    description: "格式信息"
  identifier:
    type: string
    description: "编号对应 identifier"
  prefix:
    type: string
    description: "编号前缀"
  suffix:
    type: string
    description: "编号后缀"
  time:
    type: string
    description: "时间格式"
  length:
    type: integer
    description: "编号长度（不含 prefix/suffix）"
  step:
    type: integer
    description: "步进系数"
  decrement:
    type: boolean
    description: "是否递减"
  runComponent:
    type: string
    description: "发号器执行组件（如雪花算法）"
  renewal:
    type: boolean
    description: "是否循环"
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

| 属性名          | 类型            | 说明     | 备注                    |
|:-------------|:--------------|:-------|:----------------------|
| `id`         | String        | 主键   | - |
| `code`       | String        | 编码   | - |
| `comment`    | String        | 编号备注 | - |
| `current`    | Long          | 当前值  | - |
| `format`     | String        | 格式   | - |
| `identifier` | String        | identifier | - |
| `prefix`     | String        | 前缀   | - |
| `suffix`     | String        | 后缀   | - |
| `time`       | String        | 时间格式 | - |
| `length`     | Integer       | 长度   | - |
| `step`       | Integer       | 步进   | - |
| `decrement`  | Boolean       | 递减   | - |
| `runComponent` | String      | 发号组件 | - |
| `renewal`    | Boolean       | 循环   | - |
| `sigma`      | String        | 统一标识 | - |
| `language`   | String        | 语言   | - |
| `active`     | Boolean       | 启用   | - |
| `metadata`   | String        | 元数据  | - |
| `createdAt`  | LocalDateTime | 创建时间 | - |
| `createdBy`  | String        | 创建人  | - |
| `updatedAt`  | LocalDateTime | 更新时间 | - |
| `updatedBy`  | String        | 更新人  | - |
| `appId`      | String        | 应用ID | - |
| `tenantId`   | String        | 租户ID | - |

## Relation

| 字段     | 关联实体  | 关系类型     | 说明   |
|:------|:-----|:---------|:-----|
| `appId` | XApp | Many-to-One | 应用 |
| `tenantId` | XTenant | Many-to-One | 租户 |
