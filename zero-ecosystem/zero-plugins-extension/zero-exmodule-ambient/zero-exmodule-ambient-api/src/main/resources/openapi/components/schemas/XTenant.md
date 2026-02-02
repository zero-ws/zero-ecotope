---
title: 租户
description: "租户 (X_TENANT)"
---

## Entity

```yaml
type: object
title: XTenant
description: "租户 (X_TENANT)"
properties:
  id:
    type: string
    description: "主键"
  name:
    type: string
    description: "租户名称"
  code:
    type: string
    description: "租户编码"
  status:
    type: string
    description: "租户状态 PENDING | ACTIVE | EXPIRED | LOCKED"
  type:
    type: string
    description: "租户类型"
  idNumber:
    type: string
    description: "身份证号"
  idFront:
    type: string
    description: "身份证正面"
  idBack:
    type: string
    description: "身份证反面"
  bankId:
    type: string
    description: "开户行"
  bankCard:
    type: string
    description: "开户行账号"
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
    description: "父租户ID"
```

## Attribute

| 属性名        | 类型            | 说明   | 备注                                  |
|:-----------|:--------------|:-----|:------------------------------------|
| `id`        | String        | 主键   | - |
| `name`      | String        | 名称   | - |
| `code`      | String        | 编码   | - |
| `status`    | String        | 状态   | - |
| `type`      | String        | 类型   | - |
| `idNumber`  | String        | 身份证号 | - |
| `idFront`   | String        | 身份证正面 | - |
| `idBack`    | String        | 身份证反面 | - |
| `bankId`    | String        | 开户行  | - |
| `bankCard`  | String        | 银行账号 | - |
| `sigma`     | String        | 统一标识 | - |
| `language`  | String        | 语言   | - |
| `active`    | Boolean       | 启用   | - |
| `metadata`  | String        | 元数据  | - |
| `createdAt` | LocalDateTime | 创建时间 | - |
| `createdBy` | String        | 创建人  | - |
| `updatedAt` | LocalDateTime | 更新时间 | - |
| `updatedBy` | String        | 更新人  | - |
| `appId`     | String        | 应用ID | - |
| `tenantId`  | String        | 父租户ID | - |

## Relation

| 字段        | 关联实体  | 关系类型               | 说明 |
|:---------|:-----|:-------------------|:---|
| `appId`  | XApp | Many-to-One        | 应用 |
| `tenantId` | XTenant | Many-to-One / Self | 父租户 |
