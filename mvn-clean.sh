#!/bin/bash

# 删除当前项目和所有子项目中的 SPI 服务文件
# 文件路径: META-INF/services/io.zerows.core.spi.HorizonIo

echo "开始删除当前项目和所有子项目中的文件: META-INF/services/io.zerows.core.spi.HorizonIo"

# 在当前目录及其子目录中查找并删除指定文件
find . -path "*/META-INF/services/io.zerows.core.spi.HorizonIo" -type f -exec rm -f {} \;

echo "删除 SPI 文件操作完成！"

# 验证 SPI 文件删除结果
echo "检查 SPI 文件是否还有残留："
remaining_spi_files=$(find . -path "*/META-INF/services/io.zerows.core.spi.HorizonIo" -type f)
if [ -z "$remaining_spi_files" ]; then
    echo "所有 SPI 目标文件已成功删除"
else
    echo "仍有残留 SPI 文件："
    echo "$remaining_spi_files"
fi

echo "---"

# 删除 ~/.m2/ 目录下的所有 *.lastUpdated 文件
echo "开始删除 ~/.m2/ 目录下的所有 *.lastUpdated 文件..."

# 查找并删除 ~/.m2/ 及其子目录下的 *.lastUpdated 文件
find ~/.m2 -name "*.lastUpdated" -type f -exec rm -f {} \;

echo "删除 .lastUpdated 文件操作完成！"

# 验证 .lastUpdated 文件删除结果
echo "检查 ~/.m2/ 中是否还有残留的 .lastUpdated 文件："
remaining_lastupdated_files=$(find ~/.m2 -name "*.lastUpdated" -type f)
if [ -z "$remaining_lastupdated_files" ]; then
    echo "所有 .lastUpdated 文件已成功删除"
else
    echo "仍有残留的 .lastUpdated 文件："
    echo "$remaining_lastupdated_files"
fi

echo "脚本执行完毕！"