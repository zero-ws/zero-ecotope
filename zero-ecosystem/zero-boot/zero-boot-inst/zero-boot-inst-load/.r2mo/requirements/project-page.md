---
# ==============================================================================
# 📄 Page Metadata / 页面元数据
# ==============================================================================
identifier: "requirement.page"     # 规范ID
id: "{PAGE_ID}"                    # 页面唯一ID
module_id: "{MODULE_ID}"           # 所属模块ID (关联 requirement.module)
name: "{Page Name}"                # 页面名称
code: "{page_code}"                # 页面代号/文件名
author: "{Author}"                 # 负责人
updatedAt: "YYYY-MM-DD"            # 更新时间

# 🖥️ View Configuration
route: "{route_path}"              # 页面路由规则
layout: "{LayoutName}"             # 布局容器
keep_alive: false                  # 是否缓存
permissions: []                    # 页面级权限码
---

# Context

[comment]: # (定义页面上下文与入口参数)

## Description
- **Goal**: `// 页面核心目标`
- **User**: `// 目标用户角色`

## Parameters
- **Path Params**: `// 路由路径参数`
- **Query Params**: `// URL查询参数`

---

# Data Model

[comment]: # (定义页面数据结构)

## Props / Inputs
[comment]: # (从外部传入的参数或依赖)
- `// 参数名: 类型 - 说明`

## Local State
[comment]: # (页面内部维护的响应式状态)
- `// 状态名: 类型 - 说明`

## Computed State
[comment]: # (衍生状态/计算属性)
- `// 属性名: 依赖源 -> 计算逻辑简述`

---

# Lifecycle & Logic

[comment]: # (定义页面的生命周期行为)

## Initialization (OnMount)
[comment]: # (页面加载时的动作：权限校验、数据获取等)
1. `// 步骤 1`
2. `// 步骤 2`

## Updates (OnParamsChange)
[comment]: # (当路由参数或核心Props变化时的响应逻辑)
- `// 监听对象 -> 响应动作`

## Destruction (OnUnmount)
[comment]: # (页面销毁时的清理动作)
- `// 清理逻辑`

---

# UI Specification

[comment]: # (界面结构与状态)

## Structure
[comment]: # (页面区域划分或组件树结构)
- **Region A**: `// 区域说明`
- **Region B**: `// 区域说明`

## View States
[comment]: # (定义不同状态下的视图表现)
- **Loading**: `// 加载中状态表现`
- **Empty**: `// 空数据状态表现`
- **Error**: `// 异常状态表现`

---

# API Integration

[comment]: # (接口交互与数据映射)

## Endpoints
[comment]: # (本页面调用的接口清单)

| Action | Method | API URL | Trigger |
| :--- | :--- | :--- | :--- |
| `{Action Name}` | `{Method}` | `{URL}` | `{Trigger Event}` |

## Data Mapping
[comment]: # (前后端数据转换规则)
- `// 后端字段 -> 前端表现转换逻辑`