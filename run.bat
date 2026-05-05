@echo off
:: 设置控制台为 UTF-8 编码，防止中文乱码
chcp 65001 >nul

:: 确保工作目录切换到脚本所在的根目录所在的盘符和路径
cd /d "%~dp0"

echo 🐾 正在编译《宠物世界》...
if not exist out mkdir out

:: 强制使用 UTF-8 编码编译
javac -encoding UTF-8 -d out src\main\java\cat\*.java

:: 检查编译是否成功
if %errorlevel% equ 0 (
    echo 🚀 编译成功！正在启动游戏...
    echo ==================================================
    java -cp out cat.Main
) else (
    echo ❌ 编译失败，请检查代码错误。
)

:: 防止游戏结束后窗口闪退，让玩家能看清最后的提示
echo.
pause