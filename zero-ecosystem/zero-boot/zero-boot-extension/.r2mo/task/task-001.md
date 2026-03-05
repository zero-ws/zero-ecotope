---
runAt: 2026-03-05.22-36-15
title: 开发完整的权限导入程序 BuildPerm
---
包名：`io.zerows.extension.appcontainer` 

环境变量文件位于测试目录中，具体全局环境变量、`init/environment.json` 中加载数据的部分直接参考 BuildApp 中的用法，用 BuildShared 加载，环境变量部分在 BuildApp 中也有相关说明。

- DBE数据库Engine用法参考 BuildApp
- 基础数据和Scope等也参考 BuildApp
- 序列化反序列化也参考 BuildApp
- BuildApp 只是参考的入口程序，但内部会包含核心执行程序

## 输入目录

参考：plugins/{MID}/security/ 目录下的两个核心目录
- 资源定义：`RBAC_RESOURCE` 
- 角色权限：`RBAC_ROLE` 

参考输入目录（其中一个模块）：`/Users/lang/zero-cloud/app-zero/zero-ecotope/zero-ecosystem/zero-plugins-extension/zero-exmodule-rbac/zero-exmodule-rbac-domain/src/main/resources/plugins/zero-exmodule-rbac/security` 

- 根据参考，加载当前应用环境中的类路径下所有 `plugins/{MID}/security/` 中的两个目录名下边内容
- 目前系统包括 15 个 exmodule，后期可能更多，但每个 exmodule 的路径都是 `plusing/{MID}/security/` 一致
- MID 就是当前模块的 ID，如参考目录中是 `zero-exmodule-rbac`

## 核心实体

- 操作：`S_ACTION` 
- 资源：`S_RESOURCE` 
- 权限：`S_PERMISSION` 
- 角色和权限关联：`R_ROLE_PERM` 
三种实体对应的 `pojos` 位于：/Users/lang/zero-cloud/app-zero/zero-ecotope/zero-ecosystem/zero-plugins-extension/zero-exmodule-rbac/zero-exmodule-rbac-domain/src/main/java/io/zerows/extension/module/rbac/domain/tables/ 下，`daos` 也在子目录中。

## 数据填充规则

### 全局填充

全局填充直接参考 BuildApp 中菜单和应用的导入规则直接填充。

### 核心实体

`RBAC_RESOURCE` 资源目录下
#### SPermission

- 一级目录名：填充 `SPermission` 中的 type 属性
- 二级目录名：填充 `SPermission` 中的 directory 属性
- 三级目录名：填充 `SPermission` 中的 name 属性
- 三级目录名/PERM.yml 固定配置：
	- code：对应 code
	- identifier：对应 identifier
	- comment：如果存在就是 comment，否则就是全路径：一级目录名/二级目录名/三级目录名
	- id：自动生成

#### SAction & SResource

- 三级目录名/xxxx.yml 配置文件，每个文件对应 SAction x 1 和 SResource x 1
- 文件名规则：@作为分隔符，{name}@{method}@{uri}，其中 name 同时填充 SAction 和 SResource
- uri转换：`_` 替换成 `/` 路径符，`$` 替换成路径操作符 `:` ，例：`_api_acl_role-view_$owner_$res` 对应的请求路径：`/api/acl/role-view/:owner/:res` 。
- 文件内容 `keyword`：
	- `SAction` 的 `code` = `act.${keyword}`
	- `SResource` 的 `code` = `res.${keyword}`
- `resource`：对应到 `SResource` 中的 `type`
- 级别 level 默认值：
	- GET = 1 
	- POST = 4
	- PUT = 8
	- DELETE = 12
- 资源中 identifier 和所在目录的 `SPermission` 对应
- modeRole 固定值：UNION
- 上述提到的所有属性如果有值从文件中加载，无值则直接使用规则上的默认值。

### 关联实体

`RBAC_ROLE` 资源目录下

#### R_ROLE_PERM

- 一级目录：对应角色的 CODE，可查找唯一角色
- 二级目录名：关联角色数据，{code}@{priority}，priority没有时就只有 code，此时 priority = 0
- 二级目录/xxx@{field}.yml：文件内容，内容中是数组，field = code 时表示查询权限的规则：code in 内容，注意只加载 `yml` 后缀文件

#### 关于关联的UUID

- `SAction` 中的 `permissionId` 直接和它所在的父目录，`resourceId` 就是对应的 `SResource` 实体。
- `SResource` 和 `SPermission` 的ID可以直接生成
- 新生成条件：
	- 如果数据库中不存在时生成新的，根据规则计算出来的 code + appId 查询数据库
	- 若数据库中存在则直接使用存在的 id
- 生成条件同时作用于 `SPermission / SAction / SResource`
- 导入具有幂等性，都要采用 `upsert` 的方式。