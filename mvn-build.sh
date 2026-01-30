#!/usr/bin/env bash

# 1. 停止 mvnd 后台守护进程 (清除常驻内存的脏状态)
echo "🧹 Stopping mvnd daemons to clear cache..."
mvnd --stop

# 2. 执行构建 (保留了你原有的参数)
echo "🚀 Starting build..."
mvnd clean install -Dquickly -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -Dmvnd.log.target=console