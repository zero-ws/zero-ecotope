---
# ==============================================================================
# 🎨 System Design Specification / 全局设计规范
# ==============================================================================
identifier: "design.system"      # 规范ID
name: "{System Name}"            # 系统名称 (e.g. Nebula UI)
version: "1.0.0"                 # 版本号
framework: "Tailwind CSS v3.4+"  # 技术底座
prefix: "{tw-}"                  # (Optional) Tailwind 类名前缀
updatedAt: "YYYY-MM-DD"          # 更新时间

# --- Base Configuration (核心基准) ---
spacing_base: "0.25rem (4px)"    # Tailwind default spacing unit (1)
root_font_size: "16px"           # 1rem
font_sans: "Inter, sans-serif"   # font-sans
font_mono: "Fira Code, mono"     # font-mono
---

# 1. Color Palette (Theme Colors)

[comment]: # (对应 tailwind.config.js 中的 theme.colors)
[comment]: # (建议采用 Tailwind 默认的 50-950 色阶标准)

## 1.1 Brand Colors (品牌色)
> 定义核心品牌色，通常映射为 `colors.primary`。

| Token Name | Hex Value | Tailwind Utility | Usage Context |
| :--- | :--- | :--- | :--- |
| **primary-50** | `{Hex}` | `bg-primary-50` | 选中态底色 (Selected/Active) |
| **primary-100**| `{Hex}` | `bg-primary-100`| 弱强调背景 |
| **primary-500**| `{Hex}` | `text-primary-500`| 图标高亮, Focus Ring |
| **primary-600**| `{Hex}` | `bg-primary-600`| **主按钮 (Default)**, 强文本 |
| **primary-700**| `{Hex}` | `bg-primary-700`| 悬停交互 (Hover) |

## 1.2 Neutral Colors (中性色)
> 定义背景与文本灰度，通常映射为 `colors.gray` 或 `colors.slate/zinc`。

| Token Name | Hex / Alias | Tailwind Utility | Usage Context |
| :--- | :--- | :--- | :--- |
| **Base White** | `#FFFFFF` | `bg-white` | 卡片背景, 输入框背景 |
| **gray-50** | `{Hex}` | `bg-gray-50` | 页面全局底色 (Body Bg) |
| **gray-200** | `{Hex}` | `border-gray-200`| 默认边框, 分割线 |
| **gray-400** | `{Hex}` | `text-gray-400` | 占位符 (Placeholder), 失效图标 |
| **gray-500** | `{Hex}` | `text-gray-500` | 次级文本 (Secondary Text) |
| **gray-900** | `{Hex}` | `text-gray-900` | **主标题 (Headings)** |

## 1.3 Semantic Aliases (语义化别名)
> 在配置中建立映射关系，不要直接使用色值。

- **Success**: `colors.emerald` (e.g. `text-emerald-600`, `bg-emerald-50`)
- **Warning**: `colors.amber` (e.g. `text-amber-500`, `bg-amber-50`)
- **Error**: `colors.red` (e.g. `text-red-600`, `bg-red-50`)
- **Info**: `colors.blue` (e.g. `text-blue-500`, `bg-blue-50`)

---

# 2. Typography (Theme FontFamily & FontSize)

[comment]: # (对应 theme.fontSize 和 theme.fontFamily)
[comment]: # (Tailwind 默认将 font-size 和 line-height 绑定)

## 2.1 Type Scale
| Token Class | Size / Leading | Font Weight | Recommended Use |
| :--- | :--- | :--- | :--- |
| **text-xs** | 12px / 16px | Regular | Badge, Helper text |
| **text-sm** | 14px / 20px | Regular / Medium | Form Input, Table content |
| **text-base**| **16px / 24px**| **Regular** | **Body Copy (正文)** |
| **text-lg** | 18px / 28px | Semibold | Card Title |
| **text-xl** | 20px / 28px | Semibold | Section Header |
| **text-2xl** | 24px / 32px | Bold | Page Title |

---

# 3. Layout & Spacing (Theme Screens & Spacing)

## 3.1 Breakpoints (Screens)
> 采用移动优先 (Mobile First) 策略。

- **sm**: `640px` (Mobile Landscape)
- **md**: `768px` (Tablet)
- **lg**: `1024px` (Laptop)
- **xl**: `1280px` (Desktop)
- **2xl**: `1536px` (Wide Screen)

## 3.2 Container Configuration
> `theme.container` 设置。

- **Center**: `true` (mx-auto)
- **Padding**:
    - DEFAULT: `1rem` (px-4)
    - sm: `2rem` (px-8)
    - lg: `4rem` (px-16)

---

# 4. Borders & Effects (Theme BorderRadius & BoxShadow)

## 4.1 Border Radius (圆角)
| Token Class | Value | Usage Context |
| :--- | :--- | :--- |
| **rounded-sm** | `0.125rem` (2px) | Checkbox, Small Tags |
| **rounded** | `0.25rem` (4px) | - |
| **rounded-md** | `0.375rem` (6px) | **Standard (Input, Button, Card)** |
| **rounded-lg** | `0.5rem` (8px) | Modals, Large Panels |
| **rounded-full**| `9999px` | Avatar, Pill Buttons |

## 4.2 Shadows (Elevation)
- **shadow-sm**: `0 1px 2px 0 rgb(0 0 0 / 0.05)` (Border enhancement)
- **shadow**: Default card shadow
- **shadow-md**: Dropdown menus, Popovers
- **shadow-xl**: Modals, Dialogs

---

# 5. Component Primitives (Layer Components)

[comment]: # (使用 @apply 指令组合 Utility Class 的基础组件规范)

## 5.1 Buttons
> Base classes applied to `<button>`.

- **Btn-Base**: `inline-flex items-center justify-center rounded-md font-medium transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2`
- **Btn-Size-Default**: `h-10 px-4 py-2 text-sm`
- **Variants**:
    - **Primary**: `bg-primary-600 text-white hover:bg-primary-700 focus:ring-primary-500`
    - **Secondary**: `bg-white text-gray-700 border border-gray-300 hover:bg-gray-50 focus:ring-primary-500`
    - **Ghost**: `text-gray-600 hover:bg-gray-100 hover:text-gray-900`

## 5.2 Form Inputs
> Base classes applied to `<input>`, `<select>`.

- **Input-Base**: `block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 sm:text-sm`
- **Input-Error**: `border-red-300 text-red-900 placeholder-red-300 focus:border-red-500 focus:ring-red-500`
- **Input-Disabled**: `disabled:cursor-not-allowed disabled:bg-gray-50 disabled:text-gray-500`

## 5.3 Badges / Chips
- **Badge-Base**: `inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium`
- **Badge-Success**: `bg-green-100 text-green-800`
- **Badge-Gray**: `bg-gray-100 text-gray-800`