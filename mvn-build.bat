@echo off
:: 设置字符集为 UTF-8 以支持表情符号（取决于控制台字体）
chcp 65001 > nul

:: 1. 停止 mvnd 后台守护进程 (清除常驻内存的脏状态)
echo 🧹 Stopping mvnd daemons to clear cache...
:: 注意：mvnd 的停止命令通常是 mvnd --stop
call mvnd --stop

:: 2. 执行构建 (保留了您原有的参数)
echo 🚀 Starting build...
call mvn clean install -Dquickly -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -Dmvnd.log.target=console

echo.
echo ✅ Build process finished.
pause