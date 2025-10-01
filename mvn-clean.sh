#!/bin/bash

# 删除当前项目和所有子项目中的 SPI 服务文件
# 文件路径: META-INF/services/io.zerows.core.spi.HorizonIo

echo "开始删除当前项目和所有子项目中的文件: META-INF/services/io.zerows.core.spi.HorizonIo"

# 在当前目录及其子目录中查找并删除指定文件
find . -path "*/META-INF/services/io.zerows.core.spi.HorizonIo" -type f -exec rm -f {} \;

echo "删除操作完成！"

# 验证删除结果
echo "检查是否还有残留文件："
remaining_files=$(find . -path "*/META-INF/services/io.zerows.core.spi.HorizonIo" -type f)
if [ -z "$remaining_files" ]; then
    echo "所有目标文件已成功删除"
else
    echo "仍有残留文件："
    echo "$remaining_files"
fi