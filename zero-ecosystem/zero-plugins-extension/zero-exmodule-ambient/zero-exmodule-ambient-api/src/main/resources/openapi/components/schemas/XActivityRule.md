---
title: 活动规则
description: "活动规则 (X_ACTIVITY_RULE)"
---

## Entity

```yaml
type: object
title: XActivityRule
description: "活动规则 (X_ACTIVITY_RULE)"
properties:
  id:
    type: string
    description: "主键"
  definitionKey:
    type: string
    description: "流程Key"
  taskKey:
    type: string
    description: "任务Key"
  type:
    type: string
    description: "规则类型"
  ruleName:
    type: string
    description: "规则名称"
  ruleOrder:
    type: integer
    format: int64
    description: "触发顺序"
  ruleNs:
    type: string
    description: "名空间"
  ruleIdentifier:
    type: string
    description: "模型ID"
  ruleField:
    type: string
    description: "字段名"
  ruleExpression:
    type: string
    description: "触发表达式"
  ruleTpl:
    type: string
    description: "参数模板"
  ruleConfig:
    type: string
    description: "规则配置"
  ruleMessage:
    type: string
    description: "输出消息"
  hookComponent:
    type: string
    description: "回调组件"
  hookConfig:
    type: string
    description: "回调配置"
  logging:
    type: boolean
    description: "记日志"
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

| 属性名             | 类型            | 说明   | 备注 |
|:----------------|:--------------|:-----|:---|
| `id`            | String        | 主键   | - |
| `definitionKey`  | String        | 流程Key | - |
| `taskKey`       | String        | 任务Key | - |
| `type`          | String        | 规则类型 | - |
| `ruleName`      | String        | 规则名称 | - |
| `ruleOrder`     | Long          | 触发顺序 | - |
| `ruleNs`        | String        | 名空间  | - |
| `ruleIdentifier`| String        | 模型ID | - |
| `ruleField`     | String        | 字段名  | - |
| `ruleExpression`| String        | 触发表达式 | - |
| `ruleTpl`       | String        | 参数模板 | - |
| `ruleConfig`    | String        | 规则配置 | - |
| `ruleMessage`   | String        | 输出消息 | - |
| `hookComponent` | String        | 回调组件 | - |
| `hookConfig`    | String        | 回调配置 | - |
| `logging`       | Boolean       | 记日志  | - |
| `sigma`         | String        | 统一标识 | - |
| `language`      | String        | 语言   | - |
| `active`        | Boolean       | 启用   | - |
| `metadata`      | String        | 元数据  | - |
| `createdAt`     | LocalDateTime | 创建时间 | - |
| `createdBy`     | String        | 创建人  | - |
| `updatedAt`     | LocalDateTime | 更新时间 | - |
| `updatedBy`     | String        | 更新人  | - |
| `appId`         | String        | 应用ID | - |
| `tenantId`      | String        | 租户ID | - |

## Relation

| 字段     | 关联实体  | 关系类型    | 说明 |
|:------|:-----|:--------|:---|
| `appId` | XApp | Many-to-One | 应用 |
| `tenantId` | XTenant | Many-to-One | 租户 |
