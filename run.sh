#!/bin/bash

# 确保工作目录切换到脚本所在的根目录，防止找不到存档文件
cd "$(dirname "$0")"

echo "🐾 正在编译《宠物世界》..."
mkdir -p out

# 强制使用 UTF-8 编码编译，防止中文字符报错
javac -encoding UTF-8 -d out src/main/java/cat/*.java

# 检查编译是否成功（$? 为 0 表示上一条命令成功）
if [ $? -eq 0 ]; then
    echo "🚀 编译成功！正在启动游戏..."
    echo "=================================================="
    # 运行主程序
    java -cp out cat.Main
else
    echo "❌ 编译失败，请检查代码错误。"
fi