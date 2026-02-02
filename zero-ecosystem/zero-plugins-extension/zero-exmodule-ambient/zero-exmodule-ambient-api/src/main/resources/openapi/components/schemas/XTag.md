---
title: 标签
description: "标签 (X_TAG)"
---

## Entity

```yaml
type: object
title: XTag
description: "标签 (X_TAG)"
properties:
  id:
    type: string
    description: "主键"
  name:
    type: string
    description: "标签名称"
  type:
    type: string
    description: "标签类型"
  icon:
    type: string
    description: "标签图标"
  color:
    type: string
    description: "标签颜色"
  sort:
    type: integer
    format: int64
    description: "标签排序"
  show:
    type: boolean
    description: "是否在导航栏显示"
  description:
    type: string
    description: "标签描述"
  uiStyle:
    type: string
    description: "标签风格（色彩字体等）"
  uiConfig:
    type: string
    description: "标签其他配置"
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

| 属性名        | 类型            | 说明     | 备注                 |
|:-----------|:--------------|:-------|:-------------------|
| `id`        | String        | 主键     |                    |
| `name`      | String        | 标签名称   | 与 appId 联合唯一      |
| `type`      | String        | 标签类型   |                    |
| `icon`      | String        | 标签图标   |                    |
| `color`     | String        | 标签颜色   |                    |
| `sort`      | Long          | 标签排序   |                    |
| `show`      | Boolean       | 导航栏显示  | 默认 false           |
| `description` | String      | 标签描述   | LONGTEXT            |
| `uiStyle`   | String        | 风格     | LONGTEXT，色彩字体等   |
| `uiConfig`  | String        | 其他配置   | LONGTEXT            |
| `sigma`     | String        | 统一标识   |                    |
| `language`  | String        | 使用的语言  |                    |
| `active`    | Boolean       | 是否启用   |                    |
| `metadata`  | String        | 元数据  |                    |
| `createdAt` | LocalDateTime | 创建时间   |                    |
| `createdBy` | String        | 创建人    |                    |
| `updatedAt` | LocalDateTime | 更新时间   |                    |
| `updatedBy` | String        | 更新人    |                    |
| `appId`     | String        | 应用ID   |                    |
| `tenantId`  | String        | 租户ID   |                    |

## Relation

| 字段     | 关联实体  | 关系类型     | 说明       |
|:------|:-----|:---------|:---------|
| `appId` | XApp | Many-to-One | 所属应用     |
| `tenantId` | XTenant | Many-to-One | 所属租户     |
| (R_TAG_ENTITY) | RTagEntity | One-to-Many | 标签与实体的多对多关联 |
