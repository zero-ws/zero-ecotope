---
title: 公告
description: "公告 (X_NOTICE)"
---

## Entity

```yaml
type: object
title: XNotice
description: "公告 (X_NOTICE)"
properties:
  id:
    type: string
    description: "主键"
  name:
    type: string
    description: "公告标题"
  code:
    type: string
    description: "公告编码"
  type:
    type: string
    description: "公告类型"
  status:
    type: string
    description: "公告状态"
  content:
    type: string
    description: "公告内容"
  expiredAt:
    type: string
    format: date-time
    description: "公告到期时间"
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

| 属性名        | 类型            | 说明   | 备注                 |
|:-----------|:--------------|:-----|:-------------------|
| `id`       | String        | 主键   |                    |
| `name`     | String        | 公告标题 | 与 appId 联合唯一      |
| `code`     | String        | 公告编码 | 与 appId 联合唯一      |
| `type`     | String        | 公告类型 |                    |
| `status`   | String        | 公告状态 |                    |
| `content`  | String        | 公告内容 | LONGTEXT           |
| `expiredAt`| LocalDateTime | 到期时间 |                    |
| `sigma`    | String        | 统一标识 |                    |
| `language` | String        | 语言   |                    |
| `active`   | Boolean       | 启用   |                    |
| `metadata` | String        | 元数据  |                    |
| `createdAt`| LocalDateTime | 创建时间 |                    |
| `createdBy`| String        | 创建人  |                    |
| `updatedAt`| LocalDateTime | 更新时间 |                    |
| `updatedBy`| String        | 更新人  |                    |
| `appId`    | String        | 应用ID |                    |
| `tenantId` | String        | 租户ID |                    |

## Relation

| 字段     | 关联实体  | 关系类型     | 说明   |
|:------|:-----|:---------|:-----|
| `appId` | XApp | Many-to-One | 应用 |
| `tenantId` | XTenant | Many-to-One | 租户 |
