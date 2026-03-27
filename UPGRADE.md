# Zero Framework 升级指南

## 版本管理架构

Zero 框架依赖两个外部框架：
- **R2MO** (`r2mo.version`) - Vert.x 栈核心框架
- **Momo** (`momo.version`) - Spring 栈核心框架

### 版本定义位置（升级时必须同步修改 4 处）

| # | 文件路径 | 行号 | 说明 |
|---|----------|------|------|
| 🔴 1 | `pom.xml` (根目录) | 14-15 | 主版本定义，所有模块继承 |
| 🔴 2 | `zero-0216/pom.xml` | 105-106 | BOM 层，import 外部 BOM 时需要本地属性 |
| 🔴 3 | `zero-ecosystem/pom.xml` | 17-18 | 消费 `zero-0216` BOM 时需要本地属性 |
| 🔴 4 | `zero-version/zero-version-overlay/pom.xml` | 17 | flatten 后保留属性给依赖方使用 |

> **重要**：4 处版本号必须保持一致，否则会导致 Maven 依赖解析失败（版本默认为 1.0.0）。

---

## 升级步骤

### 方式一：手动升级（推荐）

修改以下 4 个文件中的版本号：

```bash
# 1. 根 pom
vim pom.xml
# 修改: <r2mo.version>1.0.50</r2mo.version>
# 修改: <momo.version>1.0.50</momo.version>

# 2. BOM 层
vim zero-0216/pom.xml
# 修改: <r2mo.version>1.0.50</r2mo.version>
# 修改: <momo.version>1.0.50</momo.version>

# 3. Ecosystem 层
vim zero-ecosystem/pom.xml
# 修改: <r2mo.version>1.0.50</r2mo.version>
# 修改: <momo.version>1.0.50</momo.version>

# 4. Overlay 层
vim zero-version/zero-version-overlay/pom.xml
# 修改: <r2mo.version>1.0.50</r2mo.version>
```

### 方式二：脚本升级

```bash
#!/bin/bash
# upgrade-versions.sh

NEW_VERSION="${1:-1.0.50}"

# 4 处版本定义文件
FILES=(
    "pom.xml"
    "zero-0216/pom.xml"
    "zero-ecosystem/pom.xml"
    "zero-version/zero-version-overlay/pom.xml"
)

for file in "${FILES[@]}"; do
    if [ -f "$file" ]; then
        sed -i '' "s/<r2mo.version>.*<\/r2mo.version>/<r2mo.version>${NEW_VERSION}<\/r2mo.version>/g" "$file"
        sed -i '' "s/<momo.version>.*<\/momo.version>/<momo.version>${NEW_VERSION}<\/momo.version>/g" "$file"
        echo "✅ Updated: $file"
    fi
done

echo ""
echo "升级完成！新版本: ${NEW_VERSION}"
echo "请执行: mvn clean install -DskipTests"
```

使用方法：
```bash
chmod +x upgrade-versions.sh
./upgrade-versions.sh 1.0.50
```

### 重新构建

```bash
# 1. 清理旧的构建产物
mvn clean -q

# 2. 重新构建并安装到本地仓库
mvn install -DskipTests -q

# 3. 验证安装结果
grep "r2mo.version" ~/.m2/repository/io/zerows/zero-version-overlay/1.0.0/zero-version-overlay-1.0.0.pom
grep "r2mo.version" ~/.m2/repository/io/zerows/zero-ecosystem/1.0.0/zero-ecosystem-1.0.0.pom
```

---

## 技术背景

### 为什么需要 4 处定义？

Maven 的两个限制：

1. **flatten-maven-plugin 限制**
   - `resolveCiFriendliesOnly` 模式只解析 `${revision}`, `${sha1}`, `${changelist}`
   - 自定义属性 `${r2mo.version}` 需要显式声明才能保留在 flattened pom 中
   - 父级的 properties **不会** 自动继承到子模块的 flattened pom

2. **import scope 限制**
   - `import` 一个 BOM 时，该 BOM 的 properties **不会** 传递给消费方
   - 消费方必须在本地声明版本属性才能使用

### 依赖链分析

```
zero-ecotope/pom.xml (定义 r2mo.version=1.0.49)
    │
    ├── zero-0216/pom.xml (需要 r2mo.version 来 import r2mo-0216:${r2mo.version})
    │       └── 被 zero-ecosystem import，但 properties 不传递
    │
    ├── zero-ecosystem/pom.xml (需要 r2mo.version，因为 import zero-0216)
    │       └── zero-overlay 依赖 r2mo-boot-vertx，版本从 BOM 获取
    │
    └── zero-version-overlay/pom.xml (需要 r2mo.version 来定义 BOM)
            └── 被 zero-0216 import
```

### flatten-maven-plugin 配置

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>flatten-maven-plugin</artifactId>
    <configuration>
        <flattenMode>resolveCiFriendliesOnly</flattenMode>
        <!-- 保留 properties 到 flattened pom -->
        <pomElements>
            <properties>interpolate</properties>
        </pomElements>
    </configuration>
</plugin>
```

---

## 版本兼容性矩阵

| Zero ${revision} | R2MO 版本 | Momo 版本 | 说明 |
|------------------|-----------|-----------|------|
| 1.0.0 | 1.0.49 | 1.0.49 | 当前稳定版 |
| 1.0.0 | 1.0.50 | 1.0.50 | 计划升级 |

---

## 故障排查

### 问题：`Could not find artifact io.zerows:r2mo-boot-vertx:pom:1.0.0`

**原因**：`${r2mo.version}` 未被解析，默认为 `1.0.0`

**解决步骤**：
1. 检查 4 处版本定义是否一致
2. 执行 `mvn clean` 清理旧的 flattened 文件
3. 重新 `mvn install -DskipTests`
4. 在 IDEA 中 Reload Maven Project

### 问题：版本升级后 IDEA 仍解析旧版本

**解决步骤**：
1. IDEA: File → Invalidate Caches → Invalidate and Restart
2. 删除 `~/.m2/repository/io/zerows/zero-*` 目录
3. 重新 `mvn install -DskipTests`
4. IDEA: Reload Maven Project

### 问题：验证版本是否正确安装

```bash
# 检查关键模块的 properties
cat ~/.m2/repository/io/zerows/zero-version-overlay/1.0.0/zero-version-overlay-1.0.0.pom | grep r2mo.version
cat ~/.m2/repository/io/zerows/zero-ecosystem/1.0.0/zero-ecosystem-1.0.0.pom | grep r2mo.version
cat ~/.m2/repository/io/zerows/zero-0216/1.0.0/zero-0216-1.0.0.pom | grep r2mo.version
```

---

## 未来优化方向

1. **版本文件集中管理**：考虑使用 `gradle.properties` 或 `.versions` 单文件
2. **自动化校验脚本**：CI 中检查 4 处版本是否一致
3. **Maven Enforcer 规则**：强制版本一致性校验