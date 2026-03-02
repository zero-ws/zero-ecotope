---
runAt: 2026-03-01.10-50-43
title: 追加应用实例 instance 模式
---
## 核心功能

为了保证导入程序的幂等性，追加 `apps/instance.yml` 的实例运行配置文件，文件结构如：

```yaml
running:
  {UUID}: {code}
```

## 追加功能

1. 加载静态（原始目录）配置：`apps/instance.yml`  中存储了固定 UUID 的应用实例表。
2. 最终输出到目标 `apps` 时输出已插入数据库中的 `apps/instance.yml` 对应的实例表，静态 UUID 加插入时对应的 UUID。
3. 导入数据库的过程中检查目标目录对应 `apps/instance.yml`，将此处 UUID 和静态合并构造最终导入 XApp 的 UUID映射表。
4. 我已移除掉 XApp 中使用 name 和 code 做 unique 的基本规则。