---
runAt: 2026-03-05.22-36-16
title: 拷贝权限数据
---
拷贝输入目录收集的所有 yml 文件，拷贝到输出目录中。
## 输入目录

- /Users/lang/zero-cloud/app-zero/zero-ecotope/zero-ecosystem/zero-plugins-extension
- /Users/lang/zero-cloud/app-zero/r2mo-apps/app-aisz/aisz-module-hms

查找目录下所有的 RBAC_ROLE/ADMIN.SUPER 目录，并集合所有 yml 文件。

## 输出目录

- /Users/lang/zero-cloud/app-zero/r2mo-apps/app-aisz/aisz-app/aisz-app-hotel/src/main/resources/plugins/zero-exmodule-rbac/security/RBAC_ROLE
- 数据库中提取 S_ROLE 的 CODE 构造目录，ADMIN.SUPER 除外，每个 CODE 创建一个目录

## 数据库信息

```bash
Z_DBS_INSTANCE="DB_HMS_001_APP"  
Z_DB_APP_USER="htl"  
Z_DB_APP_PASS="pl,okmijn123"
```