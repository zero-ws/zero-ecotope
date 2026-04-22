---
runAt: 2026-04-22.18-19-17
title: 强化MCP对接说明
author:
---
- 全英文版本  
- 在现有的对接上做升级、合并  
- 如果追加规则中有重复规则，想办法合并或升级，尽可能不做删除  
- code-reviewer-graph  
- 地址 mxt/ 之下  
- 规则使用单一职责规则

## 规则清单

### 核心层
- zero-version 版本管理原理
- zero-epoch 核心容器层整体架构
- 基于SPI的核心插件层
- 基于本地和Nacos的配置中心
- Jooq为核心的DBE处理
- 关于DBS的多数据源应用原理（静态、动态组合）
- 和 r2mo-rapid 的对接层（zero-overlay）
- 三大核心工具：Ut, Ux, Fx
- 任务模型：Job
- 启动矩阵：@Actor 模型

### 核心功能层
- 缓存应用：zero-plugins-cache*，包括 redis
- Excel导入模型：zero-plugins-excel
- Flyway数据初始化模型：zero-plugins-flyway
- 监控中心：zero-plugins-monitor*
- 安全处理流程：zero-plugins-security*
- 图数据库 neo4j 应用：zero-plugins-neo4j
- 邮件功能：-email
- 短信功能：-sms
- 微信功能：-weco
- 主动推送WebSocket：-websocket

### 扩展中心
- 功能型
	- CRUD引擎，zero-extension-crud
	- 扩展底座，zero-extension-skeleton
	- 统一抽象接口（单体专用），zero-extension-api
- 业务型
	- 配置管理：-ambient
	- 组织架构管理：-erp
	- 财务管理：-finance
	- 图引擎：-graphic
	- 集成管理：-integration
	- 地理定位服务：-lbs
	- MBSE动态接口：-mbseapi
	- MBSE动态建模：-mbsecore
	- 模块化：-modulat
	- RBAC模型：-rbac
	- 报表引擎：-report
	- 模版中心&个人设置：-tpl
	- 界面配置中心：-ui
	- 工作流引擎：-workflow
- 复杂组合功能点
	- 附件上传，核心存储，可通过集成做 FTP 等相关定义（-ambient, -integration）
	- 静态建模列成（-extension-crud, -ui）
	- 日志记录（-ambient），内置规则引擎 Activity 处理
	- 可配置的报表中心（-report）
	- 授权数据域部分，ACL部分，强化的授权模型（-rbac）
	- 模块化配置服务（-modulat），统一模块化处理，统一界面处理

## Changes

- 2026-04-22 Team Leader
- Added `mxt/mcp-integration-map.md` as the topic-routing layer for MCP-assisted Zero framework reading.
- Refocused `mxt/mcp-code-review-graph-rules.md` into graph playbooks only, so it no longer duplicates entry rules or graph discipline.
- Expanded `mxt/graph-usage-rules.md` to own repository anchor, graph baseline, and refresh guidance.
- Updated `mxt/README.md` and `mxt/agent-quick-start.md` so the reading path is now topic routing -> graph discipline -> graph playbooks.
- Updated `mxt/document-boundary-audit.md` and `mxt/evolution-rules.md` so the new document boundary is explicit and maintainable.
- Team mode was not explicitly configured in this repository, so the task was executed with a lightweight leader plus audit workers model only.
- 2026-04-22 Team Leader
- Added dedicated SRP documents for version, config center, DBS, overlay bridge, job model, cache/Redis, Excel, monitor, security plugins, Neo4j, email, SMS, Weco, WebSocket, and `zero-extension-api`.
- Added dedicated SRP documents for each exmodule named in the task list: ambient, ERP, finance, graphic, integration, LBS, MBSE API, MBSE core, modulat, report, TPL, UI, and workflow.
- Updated `mxt/mcp-integration-map.md` and `mxt/README.md` so each task-list item now has an explicit document owner instead of relying on framework overviews alone.
- 2026-04-22 Team Leader
- Used `code-review-graph` plus source anchors to re-verify the remaining boundary gaps and confirmed `ExBoot` / `SPI_SET` belong to `zero-extension-skeleton`, while `seekSyntax` / `SResourceDao` / `ScDetent` belong to `zero-exmodule-rbac`.
- Added `mxt/extension-skeleton-guide.md` and `mxt/exmodule-rbac-guide.md` so the last two rule-list topics also have single-responsibility owner documents instead of borrowing SPI or RBAC rule pages.
- Synced `mxt/README.md`, `mxt/mcp-integration-map.md`, `mxt/agent-quick-start.md`, `mxt/document-boundary-audit.md`, and `mxt/evolution-rules.md` so the index, routing, audit, and maintenance policy all agree on the final ownership model.
- Synced `mxt/search-hints.md` with the new `extension-skeleton-guide.md` and `exmodule-rbac-guide.md` owner documents, matching the pack evolution rule for newly created files.
- Final coverage result: every item in the task rule list now maps to an explicit owner document under `mxt/`, while overview and playbook files remain routing-only or rules-only.
- 2026-04-22 Team Leader
- Added `mxt/distillation-rules.md`, `mxt/purification-rules.md`, and `mxt/mcp-fast-retrieval-rules.md` to make document compression, duplicate-rule cleanup, and shortest-path MCP retrieval explicit single-responsibility rule sets.
- Updated `mxt/README.md`, `mxt/agent-quick-start.md`, `mxt/mcp-integration-map.md`, `mxt/search-hints.md`, `mxt/document-boundary-audit.md`, and `mxt/evolution-rules.md` so AI Agents can route from topic to owner to source proof with fewer loaded files and fewer tokens.
- 2026-04-22 Team Leader
- Ran a temporary 200-case MXT regression suite covering internal Markdown references, 43 task-list route mappings, English-only MXT documents, and fast-retrieval/distillation/purification rule anchors.
- Removed the temporary regression test file after the test passed, so no regression-only test artifact remains in the final commit.
