---
runAt: 2026-02-28.20-18-10
title: 任务
---
完善代码 `io.zerows.boot.inst.LoadInst` 的核心逻辑

## 输入

- 系统中可能包含多个模块（假设有10个，其中有5个模块包含了 `apps/` 的目录存在于 `src/main/resources` 中），参考目标路径：`/Users/lang/zero-cloud/app-zero/zero-ecotope/zero-ecosystem/zero-plugins-extension/zero-exmodule-erp/zero-exmodule-erp-domain` 中的 `apps` 目录。
- `apps` 目录中包含了两种类型的数据
	- `X_APP` 数据，直接对应 `apps/{UUID}.yml` 中的数据结构，可使用 `Ut` 工具类加载 `.yml` 文件。
	- `X_MENU` 数据，直接对应 apps/{UUID}/nav/xxx 中的所有菜单结构。
- 此工具类将此处的 `X_APP` 和 `X_MENU` 加载到数据库中，已有 `InstApps` 接口帮助加载了所有合法的部分，有必要的话直接更改 `InstApps` 接口契合需要。

## 核心规则

- 目录中若包含 `MENU.yml` 则表示此菜单的数据就是当前目录。
- 目录命名：`{order}@{text}` 的格式。
- 文件命名：`{order}_{text}` 的格式。
- 启动项目路径：`/Users/lang/zero-cloud/app-zero/r2mo-apps/app-aisz/aisz-app/aisz-app-hotel`，其中公共数据部分可直接参考：`src/main/resources/init/environment.json`  中 `global` 中的数据，若包含 `{{ }}` 则表示环境变量加载，可分析项目提取字段数据（参考 `DataImport` 的用法），其他需要填写的数据全部使用此文件中的数据进行补充，参考：`/Users/lang/zero-cloud/app-zero/zero-ecotope/zero-ecosystem/zero-plugins-equip/zero-plugins-excel` 项目中的载入方法，有现成实现可用。
- 每个菜单的 `appId` 的值对应到菜单所在的 `apps` 的目录中。

## 核心目录

- 模型目录：`XApp / XMenu` 的数据结构位于：`/Users/lang/zero-cloud/app-zero/zero-ecotope/zero-ecosystem/zero-plugins-extension/zero-exmodule-ambient/zero-exmodule-ambient-domain` 
- DBE 用法参考现有内容，记得使用 `NAME + APP_ID` 防止重复，执行 `Upsert` 而不是每次执行都添加

## 输出说明

- 将数据导入 `XApp / XMenu` 两张表中
- `XMenu` 的唯一键比对采用 `NAME + APP_ID`，注，菜单在哪个目录中则 `appId` 采用哪个目录中对应的应用 `UUID` 值。
- `XApp` 中的 `appId` 采用 `Z_APP_ID` 环境变量的值。
- 在运行路径（当前目录中）中创建缓存目录 `apps` ，结构和 `apps` 一致，将同一个 `appId` 下的菜单构造一个 `apps/{UUID}/menu.yml` 完整文件做缓存文件（记得此文件需要追加 `X_MENU` 中的 UUID ）
- 导入过程中先检查 `apps` 中的菜单是否存在（按 NAME 比对），若菜单存在则不生成新的 UUID，否则生成新的 UUID 导入到系统中。
- 应用信息、菜单添加和更新的数量在日志中加以说明，最好有统计结果。
- `runLoad` 中的代码你先不管，开发完成后我自己测试时来取消注释，建议将 `XMenu / XApp` 从文件中加载和导入数据库区分开，分职责处理。