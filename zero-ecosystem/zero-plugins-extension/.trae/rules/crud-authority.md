# CRUD 权限配置规则

> 本规则用于创建 CRUD 模块的权限配置文件，适用于基于 RBAC 的权限管理系统。

---

## 一、目录结构规范

### 1.1 基础目录层级
```
{project}/security/
├── RBAC_RESOURCE/
│   └── {一级分类}/
│       └── {模块名称}/
│           ├── {模块名称}写入/
│           ├── {模块名称}读取/
│           └── {模块名称}配置/
└── RBAC_ROLE/
    └── ADMIN.SUPER/
        └── {项目}实体权限@code.yml
```

### 1.2 目录说明
- **一级分类**：根据项目业务领域划分，如"安全管理"、"流程管理"、"动态建模"等
- **模块名称**：具体业务模块的中文名称，如"用户管理"、"工单管理"
- **三个子目录**：每个模块必须包含写入、读取、配置三个子目录

---

## 二、核心配置参数

创建模块前需确定以下参数：

| 参数 | 说明 | 示例 |
| :--- | :--- | :--- |
| identifier | 模块标识符，用于 seekSyntax 关联 | `sec.user`、`w.ticket`、`mod.field` |
| keyword | URI 路径关键字 | `user`、`ticket`、`ui-form`、`x-api` |
| 中文名称 | 模块中文显示名称 | 用户、工单、表单、接口 |
| resource | 资源类型 | `resource.security`、`resource.workflow`、`resource.mbse` |
| 一级分类 | 目录分类 | 安全管理、流程管理、动态建模 |

---

## 三、文件命名与内容规范

### 3.1 PERM.yml 文件

每个子目录下必须包含 `PERM.yml`：

```yaml
data:
  code: "perm.crud.{keyword}.{type}"
  identifier: "{identifier}"
```

- **type** 取值：`write`（写入）、`fetch`（读取）、`meta`（配置）

### 3.2 API 权限文件

文件命名格式：`{中文名称}@{HTTP方法}@_api_{keyword}{参数}.yml`

基础内容：
```yaml
data:
  keyword: "crud.{keyword}.{action}"
  resource: "resource.{resource-type}"
```

### 3.3 $key.yml 特殊配置

读取目录下的 `{名称}读取@GET@_api_{keyword}_$key.yml` 文件需要额外配置：

```yaml
data:
  keyword: "crud.{keyword}.by-key"
  resource: "resource.{resource-type}"
  virtual: true
  seekSyntax:
    phase: AFTER
    data:
      type: RECORD
      viewId: "`${viewId}`"
      identifier: "{identifier}"
```

---

## 四、完整文件清单

### 4.1 写入目录（{模块名称}写入/）

| 文件名 | keyword | HTTP方法 |
| :--- | :--- | :--- |
| PERM.yml | - | - |
| {名称}删除@DELETE@_api_{keyword}_$key.yml | crud.{keyword}.delete | DELETE |
| {名称}导入@POST@_api_{keyword}_import.yml | crud.{keyword}.batch.import | POST |
| {名称}批量删除@DELETE@_api_{keyword}_batch_delete.yml | crud.{keyword}.batch.delete | DELETE |
| {名称}批量编辑@PUT@_api_{keyword}_batch_update.yml | crud.{keyword}.batch.edit | PUT |
| {名称}添加@POST@_api_{keyword}.yml | crud.{keyword}.add | POST |
| {名称}编辑@PUT@_api_{keyword}_$key.yml | crud.{keyword}.edit | PUT |

### 4.2 读取目录（{模块名称}读取/）

| 文件名 | keyword | HTTP方法 |
| :--- | :--- | :--- |
| PERM.yml | - | - |
| {名称}丢失检查@POST@_api_{keyword}_missing.yml | crud.{keyword}.if.missing | POST |
| {名称}存在检查@POST@_api_{keyword}_existing.yml | crud.{keyword}.if.existing | POST |
| {名称}导出@POST@_api_{keyword}_export.yml | crud.{keyword}.batch.export | POST |
| {名称}批量读取@GET@_api_{keyword}_by_sigma.yml | crud.{keyword}.by-sigma | GET |
| {名称}搜索@POST@_api_{keyword}_search.yml | crud.{keyword}.search | POST |
| {名称}读取@GET@_api_{keyword}_$key.yml | crud.{keyword}.by-key | GET |

### 4.3 配置目录（{模块名称}配置/）

| 文件名 | keyword | HTTP方法 |
| :--- | :--- | :--- |
| PERM.yml | - | - |
| {名称}全列读取@GET@_api_columns_{keyword}_full.yml | crud.{keyword}.column.full | GET |
| {名称}视图保存@PUT@_api_columns_{keyword}_my.yml | crud.{keyword}.column.save | PUT |
| {名称}视图读取@GET@_api_columns_{keyword}_my.yml | crud.{keyword}.column.my | GET |

---

## 五、RBAC_ROLE 权限配置

文件位置：`RBAC_ROLE/ADMIN.SUPER/{项目}实体权限@code.yml`

```yaml
data:
  # CRUD - {模块名称}
  - "perm.crud.{keyword}.fetch"
  - "perm.crud.{keyword}.write"
  - "perm.crud.{keyword}.meta"
```

---

## 六、创建步骤

1. **确定配置参数**（identifier、keyword、中文名称、resource、一级分类）
2. **创建目录结构**（写入、读取、配置三个子目录）
3. **创建 PERM.yml 文件**（每个子目录一个）
4. **创建 API 权限文件**（按文件清单创建）
5. **更新 RBAC_ROLE 权限**（添加权限码）

---

## 七、注意事项

1. **URI 路径关键字**
   - 若 keyword 包含连字符（如 `ui-form`），URI 路径也使用连字符
   - 示例：`_api_ui-form_$key.yml`、`_api_x-api_$key.yml`

2. **$key.yml 文件**
   - 必须包含 `virtual: true` 和 `seekSyntax` 配置
   - `identifier` 必须与 PERM.yml 中的一致

3. **目录层级**
   - 创建前需确认项目的一级分类目录

---

## 八、参考示例

以下为已创建的模块配置示例，供参考：

### 8.1 项目与资源类型对照

| 项目 | resource | 一级分类 |
| :--- | :--- | :--- |
| zero-exmodule-rbac | resource.security | 安全管理 |
| zero-exmodule-workflow | resource.workflow | 流程管理 |
| zero-exmodule-ui | resource.ui | UI管理 |
| zero-exmodule-mbsecore | resource.mbse | 动态建模 |
| zero-exmodule-mbseapi | resource.mbse | 动态建模 |

### 8.2 模块配置示例

| 项目 | 模块 | identifier | keyword | 中文名称 |
| :--- | :--- | :--- | :--- | :--- |
| zero-exmodule-rbac | 用户管理 | sec.user | user | 用户 |
| zero-exmodule-rbac | 角色管理 | sec.role | role | 角色 |
| zero-exmodule-workflow | 工单管理 | w.ticket | ticket | 工单 |
| zero-exmodule-ui | 表单管理 | ui.form | ui-form | 表单 |
| zero-exmodule-mbsecore | 字段管理 | mod.field | field | 字段 |
| zero-exmodule-mbseapi | 接口管理 | i.api | x-api | 接口 |

### 8.3 完整模块列表

**安全管理（zero-exmodule-rbac）**：用户管理、角色管理、资源管理、权限管理、用户组管理、操作管理

**流程管理（zero-exmodule-workflow）**：工单管理、待办管理

**UI管理（zero-exmodule-ui）**：表单管理

**动态建模（zero-exmodule-mbsecore）**：字段管理、实体管理、模型管理

**动态建模（zero-exmodule-mbseapi）**：接口管理
