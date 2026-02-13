#!/usr/bin/env bash
set -euo pipefail

# 以脚本所在目录为基准，回到 src/main（脚本放在 src/main/out 下）
BASE_DIR="$(cd "$(dirname "$0")"/.. && pwd)"
cd "$BASE_DIR"

# 编译输出目录
mkdir -p out

# 编译（相对 src/main）
javac -encoding UTF-8 -d out cat/Main.java

# 拷贝帮助文本（存在才拷，避免报错）
[ -f ../resources/how_to_play.txt ] && cp -f ../resources/how_to_play.txt out/

# 运行（注意包名 cat）
cd out
java -cp . cat.Main
