---
title: 标签-实体关联
description: "标签-实体 (R_TAG_ENTITY)"
---

## Entity

```yaml
type: object
title: RTagEntity
description: "标签-实体 (R_TAG_ENTITY)"
properties:
  tagId:
    type: string
    description: "标签ID"
  entityType:
    type: string
    description: "实体类型"
  entityId:
    type: string
    description: "实体ID"
  linkComponent:
    type: string
    description: "执行组件"
  linkConfig:
    type: string
    description: "执行配置"
  comment:
    type: string
    description: "备注"
```

## Attribute

| 属性名          | 类型     | 说明   | 备注   |
|:-------------|:-------|:-----|:-----|
| `tagId`      | String | 标签ID | 联合主键 |
| `entityType` | String | 实体类型 | 联合主键 |
| `entityId`   | String | 实体ID | 联合主键 |
| `linkComponent` | String | 执行组件 | - |
| `linkConfig` | String | 执行配置 | - |
| `comment`    | String | 备注   | - |

## Relation

| 字段         | 关联实体 | 关系类型   | 说明 |
|:-----------|:-----|:-------|:---|
| `tagId`    | XTag | Many-to-One | 标签 |
