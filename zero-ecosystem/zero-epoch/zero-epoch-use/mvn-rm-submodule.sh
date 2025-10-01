#!/bin/bash

SUBMODULE_PATH="$1"  # <<< 修改为你想删除的子模块路径

echo "正在强制删除子模块: $SUBMODULE_PATH"

# 1. 强制移除 Git 索引
git rm -f --cached "$SUBMODULE_PATH" 2>/dev/null || echo "跳过：Git 中无此子模块"

# 2. 删除 .gitmodules 配置
git config -f .gitmodules --remove-section "submodule.$SUBMODULE_PATH" 2>/dev/null || true

# 3. 删除 .git/config 配置
git config -f .git/config --remove-section "submodule.$SUBMODULE_PATH" 2>/dev/null || true

# 4. 删除工作区文件
rm -rf "$SUBMODULE_PATH"

# 5. 删除 Git 缓存
rm -rf ".git/modules/$SUBMODULE_PATH"

# 6. 提交更改
git add .gitmodules
git commit -m "remove submodule: $SUBMODULE_PATH" || echo "无变更或已删除"

echo "✅ 子模块 $SUBMODULE_PATH 已强制删除"