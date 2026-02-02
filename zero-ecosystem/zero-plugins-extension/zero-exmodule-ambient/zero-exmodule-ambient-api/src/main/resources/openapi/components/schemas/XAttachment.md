---
title: 附件
description: "附件 (X_ATTACHMENT)"
---

## Entity

```yaml
type: object
title: XAttachment
description: "附件 (X_ATTACHMENT)"
properties:
  id:
    type: string
    description: "主键"
  name:
    type: string
    description: "文件名"
  extension:
    type: string
    description: "扩展名"
  type:
    type: string
    description: "文件类型"
  mime:
    type: string
    description: "MIME"
  size:
    type: integer
    description: "尺寸"
  status:
    type: string
    description: "状态"
  directoryId:
    type: string
    description: "目录ID"
  storeWay:
    type: string
    description: "存储方式"
  storePath:
    type: string
    description: "存储路径"
  storeUri:
    type: string
    description: "存储URI"
  modelId:
    type: string
    description: "模型标识"
  modelKey:
    type: string
    description: "模型记录ID"
  modelCategory:
    type: string
    description: "模型类别"
  fileName:
    type: string
    description: "原始文件名"
  fileKey:
    type: string
    description: "文件Key"
  fileUrl:
    type: string
    description: "下载链接"
  filePath:
    type: string
    description: "存储地址"
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

| 属性名           | 类型            | 说明   | 备注 |
|:--------------|:--------------|:-----|:---|
| `id`          | String        | 主键   | - |
| `name`        | String        | 文件名  | - |
| `extension`   | String        | 扩展名  | - |
| `type`        | String        | 文件类型 | - |
| `mime`        | String        | MIME | - |
| `size`        | Integer       | 尺寸   | - |
| `status`      | String        | 状态   | - |
| `directoryId` | String        | 目录ID | - |
| `storeWay`    | String        | 存储方式 | - |
| `storePath`   | String        | 存储路径 | - |
| `storeUri`    | String        | 存储URI | - |
| `modelId`     | String        | 模型标识 | - |
| `modelKey`    | String        | 模型记录ID | - |
| `modelCategory` | String      | 模型类别 | - |
| `fileName`    | String        | 原始文件名 | - |
| `fileKey`     | String        | 文件Key | - |
| `fileUrl`     | String        | 下载链接 | - |
| `filePath`    | String        | 存储地址 | - |
| `sigma`       | String        | 统一标识 | - |
| `language`    | String        | 语言   | - |
| `active`      | Boolean       | 启用   | - |
| `metadata`    | String        | 元数据  | - |
| `createdAt`   | LocalDateTime | 创建时间 | - |
| `createdBy`   | String        | 创建人  | - |
| `updatedAt`   | LocalDateTime | 更新时间 | - |
| `updatedBy`   | String        | 更新人  | - |
| `appId`       | String        | 应用ID | - |
| `tenantId`    | String        | 租户ID | - |

## Relation

| 字段           | 关联实体   | 关系类型     | 说明 |
|:------------|:-------|:---------|:---|
| `directoryId` | XCategory | Many-to-One | 目录 |
| `appId`     | XApp    | Many-to-One | 应用 |
| `tenantId`  | XTenant | Many-to-One | 租户 |
