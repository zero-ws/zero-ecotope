#!/usr/bin/env bash

# 删除 .lastUpdated 文件
while read -r line
do
    echo "即将删除文件: $line"
    rm -f "$line"
done << EOF
$(find ~/.m2/repository -name "*.lastUpdated")
EOF

# 删除 .part 文件
while read -r line
do
    echo "即将删除文件: $line"
    rm -f "$line"
done << EOF
$(find ~/.m2/repository -name "*.part")
EOF

# 删除 .resolverlock 文件
while read -r line
do
    echo "即将删除文件: $line"
    rm -f "$line"
done << EOF
$(find ~/.m2/repository -name "*.resolverlock")
EOF
