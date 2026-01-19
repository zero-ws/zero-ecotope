#!/bin/bash

# 检查是否安装了 npx
if ! command -v npx &> /dev/null
then
    echo "请先安装 Node.js"
    exit
fi

# 启动 serve
# -s : 单页应用模式 (Single Page App)，将 404 重定向到 index.html
# -p 3000 : 指定端口为 3000
# ./dist : 指定要服务的文件夹
echo "正在启动静态服务器..."
npx serve -s ./webpage -p 7080