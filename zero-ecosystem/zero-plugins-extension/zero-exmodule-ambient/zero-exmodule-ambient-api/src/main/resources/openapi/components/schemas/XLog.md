---
title: 日志
description: "日志 (X_LOG)"
---

## Entity

```yaml
type: object
title: XLog
description: "日志 (X_LOG)"
properties:
  id:
    type: string
    description: "主键"
  type:
    type: string
    description: "日志分类"
  level:
    type: string
    description: "级别"
  infoStack:
    type: string
    description: "堆栈信息"
  infoSystem:
    type: string
    description: "日志内容"
  infoReadable:
    type: string
    description: "可读信息"
  infoAt:
    type: string
    format: date-time
    description: "日志记录时间"
  logAgent:
    type: string
    description: "记录日志的 agent 信息"
  logIp:
    type: string
    description: "日志 IP/组件"
  logUser:
    type: string
    description: "日志记录人"
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
| `type`      | String        | 分类   | - |
| `level`     | String        | 级别   | - |
| `infoStack` | String        | 堆栈   | - |
| `infoSystem`| String        | 内容   | - |
| `infoReadable` | String     | 可读   | - |
| `infoAt`    | LocalDateTime | 时间   | - |
| `logAgent`  | String        | agent | - |
| `logIp`     | String        | IP    | - |
| `logUser`   | String        | 记录人  | - |
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

| 字段     | 关联实体  | 关系类型     | 说明 |
|:------|:-----|:---------|:---|
| `appId` | XApp | Many-to-One | 应用 |
| `tenantId` | XTenant | Many-to-One | 租户 |
