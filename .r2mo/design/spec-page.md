---
# ==============================================================================
# 🎨 Page Implementation Spec / 页面视觉实现规范
# ==============================================================================

# 1. Context & Relation (上下文与关联)
identifier: "design.page"        # 规范ID
id: "DS_{PAGE_ID}"               # 视觉规范唯一ID (e.g. DS_SYS_USER_LIST)
req_page_id: "{PAGE_ID}"         # 关联的需求页面ID (requirement.page)
name: "{Page Name}"              # 页面名称
updatedAt: "YYYY-MM-DD"          # 最后更新时间

# 2. View Architecture (视图架构)
page_type: "{Type}"              # [Dashboard|List|Form|Detail|Modal|Wizard]
route: "{/path/to/page}"         # 浏览器访问路径 (e.g. /sys/users)
file_path: "{src/views/...}"     # 物理存放路径 (e.g. src/views/sys/UserList.vue)
layout: "{LayoutName}"           # 引用的布局组件 (e.g. AdminLayout)
wrapper_class: "{Classes}"       # 页面根节点样式 (e.g. p-6 bg-slate-50 min-h-screen)
---

# 1. Layout Structure (布局骨架)

[comment]: # (定义页面宏观布局，指导工程师如何写最外层的 div 结构)

## 1.1 Responsive Container
- **Class**: `w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8`
- **Strategy**: 采用移动优先。
    - Mobile: `block (垂直流布局)`
    - Desktop: `flex gap-6` 或 `grid grid-cols-12 gap-6`

## 1.2 Grid Layout Detail
- **Sidebar/Filter Area**: `col-span-12 lg:col-span-3`
- **Main View Area**: `col-span-12 lg:col-span-9`

---

# 2. Region Implementation (区域实现详情)

[comment]: # (按页面区块给出 Tailwind 类名，直接对应 HTML 结构)

## 2.1 Page Header (页头)
- **Structure**: `flex flex-col md:flex-row md:items-center md:justify-between mb-8 pb-4 border-b border-slate-200`
- **Elements**:
    - **Title**: `text-2xl font-bold tracking-tight text-slate-900`
    - **Breadcrumb**: `flex items-center space-x-2 text-sm text-slate-500`
    - **Actions**: `flex items-center gap-x-3 mt-4 md:mt-0`

## 2.2 Content Section (核心区)
- **Container**: `bg-white shadow-sm ring-1 ring-slate-900/5 sm:rounded-xl overflow-hidden`
- **Table/List Pattern**:
    - **Head**: `bg-slate-50 border-b border-slate-200 text-xs font-semibold uppercase text-slate-500`
    - **Body Row**: `h-16 border-b border-slate-100 hover:bg-slate-50 transition-colors`
    - **Cell Padding**: `px-4 py-3 sm:px-6`

## 2.3 Form/Detail Pattern (针对 Form 类型)
- **Section Gap**: `space-y-6 sm:space-y-8`
- **Field Group**: `grid grid-cols-1 gap-y-6 sm:grid-cols-6 sm:gap-x-4`
- **Label**: `block text-sm font-medium leading-6 text-slate-900 mb-2`
- **Input**: `block w-full rounded-md border-0 py-1.5 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6`

---

# 3. Responsive & Breakpoint Logic (响应式与断点逻辑)

[comment]: # (明确各屏幕尺寸下的 UI 变化逻辑，这是开发中最耗时的部分)

| UI Element | Breakpoint | Implementation Logic (Tailwind Classes) |
| :--- | :--- | :--- |
| **Search Bar** | `< sm` | `w-full mb-3` (铺满且留底边距) |
| **Main Table** | `< md` | `hidden` (通过 CSS 隐藏，切换至 Card-List 模式) |
| **Card List** | `>= md` | `hidden` (在大屏隐藏，切回 Table) |
| **Filter Drawer** | `< lg` | `fixed inset-0 z-50` (全屏覆盖抽屉模式) |
| **Nav Menu** | `< lg` | `hidden` -> `flex flex-col` (折叠进汉堡菜单) |

---

# 4. Interactive States (状态与交互)

[comment]: # (定义非静态时的类名映射，如 Loading、空态、错误态)

## 4.1 Loading Patterns
- **Skeleton**: `animate-pulse bg-slate-200 rounded`
- **Progress Bar**: `fixed top-0 left-0 w-full h-1 bg-indigo-600 z-[9999]`

## 4.2 Status Color Mapping
- **Success**: `text-emerald-700 bg-emerald-50 ring-emerald-600/20`
- **Warning**: `text-amber-700 bg-amber-50 ring-amber-600/20`
- **Danger/Error**: `text-rose-700 bg-rose-50 ring-rose-600/20`

## 4.3 Empty State (空状态)
- **Container**: `flex flex-col items-center justify-center py-12 px-6 border-2 border-dashed border-slate-300 rounded-lg text-center`
- **Icon**: `h-12 w-12 text-slate-400 mb-4`
- **Text**: `text-sm font-semibold text-slate-900`