# Harness 2.0 Integration Guide

## 1. 总体集成链路

Zero / R2MO 应用的标准对接链路应抽象为：

```text
需求/.r2mo 规范
  → 数据模型定义（OpenAPI / Proto / JavaDomain）
  → 后端 DPA 落地（Domain → Provider → API）
  → Zero runtime API（HTTP Agent → Event Addr → Actor → Stub → Service → DBE）
  → 前端客户端（React+AntD 或 Tauri+Rust/Leptos）
```

核心原则：`.r2mo` 是契约源头，运行时代码只是实现层，不能反向发明接口或模型。

## 2. 数据模型定义规范

### 2.1 多源模型允许，但必须单向收敛到 .r2mo 契约

本项目显示出 3 种稳定模型来源：

1. **OpenAPI 汇总定义**
   - `.r2mo/api/metadata-with-server.yaml`
   - 通过 `#/components/schemas/*` 统一引用请求/响应模型
2. **分解式 schema/operation 定义**
   - `.r2mo/api/components/schemas/*.md`
   - `.r2mo/api/operations/{uri}/*.md`
3. **Proto 领域模型**
   - `.r2mo/domain/*.proto`

Harness 2.0 应统一视为：

- `operations/*` 定义接口行为
- `components/schemas/*` / `metadata-with-server.yaml` 定义 API DTO 契约
- `domain/*.proto` 定义领域模型或底层结构来源
- JavaDomain / jOOQ Table / DAO / POJO 是运行时实现映射，不是首要契约源

### 2.2 推荐的生成/对接优先级

```text
OpenAPI operation + schema
  → 生成前端 DTO / 调用签名
  → 约束 Agent/Actor/Stub 入参与出参
Proto / JavaDomain
  → 约束后端领域对象、表结构、DAO、迁移脚本
```

### 2.3 模型一致性规则

- 代码中不应出现 `.r2mo` 未定义的接口或模型。
- 前端类型字段必须匹配后端 JSON 结构。
- 后端 Stub / Service / Agent 必须回到 `.r2mo` 中的 operation/schema/proto 校验。
- 数据库表、DAO、Flyway、模型配置文件应服务于契约，不应自成体系。

## 3. 后端 API 契约规范

### 3.1 Zero 的标准运行协议

后端标准执行链必须稳定为：

```text
Agent → Addr → Actor → Stub → Service → DBE
```

职责边界：

- **Agent**：HTTP 入口，仅定义路由和参数搬运，不写业务逻辑
- **Addr**：事件地址常量中心
- **Actor**：消费事件并调用 Stub，不直接访问 DB
- **Stub**：领域服务接口，返回 `Future<T>`
- **Service**：业务实现层，校验、事务、审计、租户上下文都放这里
- **DBE / Dao**：数据库访问层

### 3.2 契约级约束

- 所有 Stub / Service / Actor 方法返回 `Future<T>`。
- 事务只能在 Service 层管理。
- 校验分层：格式校验在入口/POJO，业务校验在 Service。
- 错误码集中在 `domain/exception/ERR.java`，异常在 Provider 抛出，API 层透传。

### 3.3 Query / Search 对接协议

Zero 的查询协议不是自由 JSON，而是 **QQuery / QTree**：

```json
{
  "criteria": {},
  "pager": { "page": 1, "size": 10 },
  "sorter": ["field,ASC"],
  "projection": ["field1", "field2"]
}
```

Harness 2.0 在生成搜索接口时应默认支持：

- `criteria`：QTree 条件树
- `pager`：分页
- `sorter`：排序
- `projection`：字段裁剪

这意味着前端搜索表单、后端搜索 API、DAO 查询层必须共享同一查询语义，而不是各自定义 query 参数格式。

## 4. 前后端对接协议

### 4.1 Rust / Tauri / Leptos 客户端模式

Rust 客户端对接链路：

```text
.r2mo/api Definition
  → src/models/*.rs
  → src/api/client.rs + 业务 API
  → 页面/组件调用
```

约束：

- API base 固定为 `/api`
- Web 模式使用 HTTP 请求
- Tauri 模式使用 `invoke("http_get" | "http_post")`
- 所有 API 返回 `Result<T, AppError>`
- 统一错误类型要区分：Network / Api / Parse / Serialize / Business

### 4.2 鉴权头与应用上下文

客户端侧需要统一注入：

- `Authorization: Bearer <token>`
- `X-App-Id: <appId>`

Harness 2.0 应将这两者视作标准头注入能力，而不是页面级零散实现。

### 4.3 React + AntD 管理端模式

本项目根规则同时证明另一条稳定路径：**Zero runtime API → React/AntD 管理端**。

可抽象规则：

- 前端页面布局与表单规范可以独立于后端，但字段、过滤、表格列最终仍由契约和 model 配置驱动。
- `column.json`/`entity.json` 这类模型配置文件可作为 UI 元数据桥梁：
  - `entity.json` 负责实体唯一键、默认值、标签等
  - `column.json` 负责表格列、过滤器、渲染器、映射规则
- React UI 与 Rust UI 可以共享后端 API 契约，但各自有不同展示层规则。

## 5. 环境变量契约

环境变量不是部署细节，而是架构契约。Harness 2.0 需要内建这些键的识别和校验：

### 5.1 注册中心 / 配置中心

- `R2MO_NACOS_ADDR`：Nacos 地址
- `R2MO_NACOS_API`：Nacos API 地址
- `R2MO_NACOS_USERNAME`
- `R2MO_NACOS_PASSWORD`

### 5.2 应用运行身份

- `Z_NS`：命名空间/应用包前缀
- `R2MO_INSTANCE`：实例路由契约（框架文档已声明）
- `Z_APP_ID`
- `Z_APP`
- `Z_API_PORT`
- `Z_SOCK_PORT`

### 5.3 租户 / 业务上下文

- `Z_TENANT`
- `Z_SIGMA`

### 5.4 数据库契约

- `Z_DB_TYPE`
- `Z_DBS_INSTANCE`
- `Z_DBW_INSTANCE`
- `Z_DB_APP_USER`
- `Z_DB_APP_PASS`

Harness 2.0 应将上述变量分成：注册发现、运行身份、租户上下文、数据库四类，并在生成 bootstrap / deployment / env 模板时保持命名一致。

## 6. 可泛化落地规则

### 6.1 对接生成器规则

1. 先读取 `.r2mo/api/metadata-with-server.yaml`
2. 再补充 `operations/*` 与 `components/schemas/*`
3. 如存在 `.proto`，将其视为领域模型来源
4. 为后端生成/校验：Agent、Actor、Stub、Service、Addr
5. 为前端生成/校验：DTO、API client、错误模型、鉴权头注入
6. 为搜索接口自动接入 QQuery/QTree

### 6.2 后端实现规则

- DPA 依赖方向固定：`domain ← provider ← api`
- API 层不写业务逻辑，不做事务，不直连 DB
- Service 层处理校验、事务、审计、租户与上下文
- DB 访问只在 Provider 内完成

### 6.3 UI 对接规则

- React/AntD 与 Tauri/Rust 是两种客户端壳，底层共享同一 Zero runtime API 契约
- 前端数据类型从 `.r2mo` 生成，不从页面代码反推
- 统一错误模型、统一鉴权头、统一 `/api` 前缀

## 7. 证据

### 7.1 .r2mo 是契约源头

- `/.cursor/rules/r2-backend-zero.mdc`
  - 明确要求 `.r2mo/domain/*.proto`、`.r2mo/api/operations/{uri}/*.md`、`.r2mo/api/components/schemas/*` 作为实现依据
- `/.cursor/rules/r2-backend-zero-api.mdc`
  - 明确声明 `.r2mo` 之外的接口/模型不应出现在代码中

### 7.2 Zero 运行链路

- `/.cursor/rules/r2-backend-zero.mdc`
- `/.cursor/rules/r2-backend-zero-api.mdc`
  - 明确 `Agent → Addr → Actor → Stub → Service → DBE`

### 7.3 Query 协议

- `/.cursor/rules/r2-backend-dbe.mdc`
  - 明确 QQuery / QTree 结构和操作符规范

### 7.4 Rust / Tauri 客户端对接

- `/.cursor/rules/r2-backend-zero-integration.mdc`
- `/.cursor/rules/r2-frontend-rust.mdc`
  - 明确 `.r2mo/api → Rust types → API client → page`
  - 明确 `Authorization`、`X-App-Id`、`/api`、`Result<T, AppError>`

### 7.5 React / Admin UI 路径

- `/.cursor/rules/r2-frontend-admin-list-page.mdc`
- `/.cursor/rules/r2-frontend-admin-dialog-form.mdc`
  - 明确管理端存在独立的 React/AntD 风格规范，可视为另一类前端壳

### 7.6 环境变量契约

- `r2mo-apps-admin-api/.r2mo/app.env`
- `r2mo-apps-admin-api/src/main/resources/env.properties`
- `r2mo-apps-admin-api/src/main/resources/vertx-boot.yml`
- `README.md` / `CLAUDE.md`
  - 明确 `R2MO_NACOS_ADDR`、`R2MO_NACOS_API`、`Z_DB_APP_USER`、`Z_NS`、`R2MO_INSTANCE` 等是架构契约
