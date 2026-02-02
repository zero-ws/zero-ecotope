---
# ==============================================================================
# 🌐 Project Metadata / 项目元数据
# ==============================================================================
identifier: "requirement.project"  # 规范ID
id: "A5X6BHE61H"                   # 项目ID
name: "Ambient Service"            # 项目名称
code: "PRO-001"                    # 项目代号，PRO前缀
version: "1.0.0"                   # 项目版本
author: "Lang"                     # 作者
createdAt: "YYYY-MM-DD HH:mm"      # 立项时间
updatedAt: "YYYY-MM-DD HH:mm"      # 更新时间
---


---

[comment]: # (项目的目标和愿景)
# Purpose

- 业务介绍
- 项目愿景和目标
- 指标信息   `// e.g. 支持10万+ 并发，99.99% 可用性`

---

[comment]: # (领域上下文)

# Domain Context

## Intro

{项目基本介绍}

## Modules

{模块清单}

- `// e.g. Module-01`
- `// e.g. Module-02`
- `...`

## Critical

{特殊流程 / 关键流程}

[comment]: # (外部依赖)

# External Dependencies

## Integration

{集成接口}

## Other

{其他依赖}

---

# Development

## Tech Stack

[comment]: # (项目技术栈)

- 项目类型 `// e.g. WebApp | MobileApp | FullStack | WebService`
- 项目结构 `// e.g. ONE | DPA`
- 基础环境 `// e.g. Docker | K8S`
- 核心语言 `// e.g. Rust | Java | ...`
- 核心框架 `// e.g. AntD | Tauri + Leptos | ...`
- 风格框架 `// e.g. Tailwind Css | ...`

## Tech Elementary

[comment]: # (全局规则说明，规约定义)

- 时间字段：`// e.g. ISO-8601 标准`
- 金额/浮点：`// e.g. 高精度浮点数、2位小数、ROUND_HALF_UP`
- 版本接口：`vN 格式，// e.g. v1, v2, v3`
- 安全规约：`// e.g. RBAC-Level-3 / 用户-角色-权限-资源`

## Deployment

[comment]: # (部署流程、开发迭代流程)

- Git流程：`e.g. Trunk Based
- CI/CD：
	- dev 环境：提交代码自动部署
	- prod 环境：Tag触发构建
- Definition of Done ( DoD )
	- 测试覆盖率： `// e.g. > 80%`
	- 静态扫描：`// e.g. 零警告`
	- 接口部分：`// e.g. Swagger/OpenAPI 文档`