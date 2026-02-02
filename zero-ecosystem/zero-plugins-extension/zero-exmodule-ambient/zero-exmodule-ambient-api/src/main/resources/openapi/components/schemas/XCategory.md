---
title: 分类/树形类型
description: "分类/树形 (X_CATEGORY)"
---

## Entity

```yaml
type: object
title: XCategory
description: "分类/树形 (X_CATEGORY)"
properties:
  id:
    type: string
    description: "主键"
  name:
    type: string
    description: "名称"
  code:
    type: string
    description: "编号"
  icon:
    type: string
    description: "图标"
  type:
    type: string
    description: "分类"
  sort:
    type: integer
    description: "排序"
  leaf:
    type: boolean
    description: "叶节点"
  parentId:
    type: string
    description: "父ID"
  identifier:
    type: string
    description: "模型标识"
  comment:
    type: string
    description: "备注"
  treeComponent:
    type: string
    description: "树组件"
  treeConfig:
    type: string
    description: "树配置"
  runComponent:
    type: string
    description: "执行组件"
  runConfig:
    type: string
    description: "执行配置"
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

| 属性名            | 类型            | 说明   | 备注 |
|:---------------|:--------------|:-----|:---|
| `id`           | String        | 主键   | - |
| `name`         | String        | 名称   | - |
| `code`         | String        | 编号   | - |
| `icon`         | String        | 图标   | - |
| `type`         | String        | 分类   | - |
| `sort`         | Integer       | 排序   | - |
| `leaf`         | Boolean       | 叶节点  | - |
| `parentId`     | String        | 父ID  | - |
| `identifier`   | String        | 模型标识 | - |
| `comment`      | String        | 备注   | - |
| `treeComponent`| String        | 树组件  | - |
| `treeConfig`   | String        | 树配置  | - |
| `runComponent` | String        | 执行组件 | - |
| `runConfig`    | String        | 执行配置 | - |
| `sigma`        | String        | 统一标识 | - |
| `language`     | String        | 语言   | - |
| `active`       | Boolean       | 启用   | - |
| `metadata`     | String        | 元数据  | - |
| `createdAt`    | LocalDateTime | 创建时间 | - |
| `createdBy`    | String        | 创建人  | - |
| `updatedAt`    | LocalDateTime | 更新时间 | - |
| `updatedBy`    | String        | 更新人  | - |
| `appId`        | String        | 应用ID | - |
| `tenantId`     | String        | 租户ID | - |

## Relation

| 字段        | 关联实体    | 关系类型               | 说明 |
|:---------|:--------|:-------------------|:---|
| `parentId` | XCategory | Many-to-One / Self | 父节点 |
| `appId`  | XApp     | Many-to-One        | 应用 |
| `tenantId` | XTenant  | Many-to-One        | 租户 |
