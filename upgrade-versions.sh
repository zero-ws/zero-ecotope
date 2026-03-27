#!/bin/bash
# upgrade-versions.sh - 统一升级 Zero 框架的 R2MO/Momo 版本

set -e

NEW_VERSION="${1:-}"

if [ -z "$NEW_VERSION" ]; then
    echo "Usage: ./upgrade-versions.sh <version>"
    echo "Example: ./upgrade-versions.sh 1.0.50"
    exit 1
fi

# 4 处版本定义文件
FILES=(
    "pom.xml"
    "zero-0216/pom.xml"
    "zero-ecosystem/pom.xml"
    "zero-version/zero-version-overlay/pom.xml"
)

echo "=== 升级 Zero 框架依赖版本 ==="
echo "新版本: ${NEW_VERSION}"
echo ""

for file in "${FILES[@]}"; do
    if [ -f "$file" ]; then
        # macOS 兼容的 sed 命令
        sed -i '' "s/<r2mo.version>.*<\/r2mo.version>/<r2mo.version>${NEW_VERSION}<\/r2mo.version>/g" "$file"
        sed -i '' "s/<momo.version>.*<\/momo.version>/<momo.version>${NEW_VERSION}<\/momo.version>/g" "$file"
        echo "✅ Updated: $file"
    else
        echo "❌ Not found: $file"
    fi
done

echo ""
echo "=== 验证修改 ==="
for file in "${FILES[@]}"; do
    if [ -f "$file" ]; then
        r2mo=$(grep "<r2mo.version>" "$file" | head -1)
        momo=$(grep "<momo.version>" "$file" | head -1)
        echo "$file:"
        echo "  $r2mo"
        echo "  $momo"
    fi
done

echo ""
echo "升级完成！请执行以下命令重新构建："
echo "  mvn clean install -DskipTests"