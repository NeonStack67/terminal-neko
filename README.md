# Terminal Neko 🐱

A little command-line virtual pet cat game written in Java.

You take care of a digital cat by feeding, cleaning, playing, training,  
and even unlocking a small secret **cheat mode**.  
The game stores state in text files, so your cat's condition and items
will persist between runs.

> 中文简介：  
> 这是一个用 Java 写的命令行电子宠物猫小游戏。  
> 通过喂食、喝水、洗澡、玩耍和训练来照顾你的猫，  
> 游戏状态会保存在文本文件中（包括物品栏、训练次数、天数等），  
> 还藏着一个小小的“作弊模式”彩蛋。

---
如果出现 猫咪饿死了！游戏结束。

删除：src/main/data/save.properties（或你实际存档位置的 save.properties）

用初始版本替换：src/main/data/cat_condition.txt（从 src/main/resources/cat_condition.txt 复制一份覆盖过去）（现在不需要了，直接重新领养）

（可选）如果物品文件也会被你写坏/写空，同样从 resources 覆盖 goods_condition.txt
## ✨ Features

- Text-based virtual pet cat in the terminal
- Hunger / thirst / mood / cleanliness / intimacy / training stats
- Items with effects (cat food, milk, fish, toy ball, etc.)
- Daily training limit & “feed fish to gain training chance”
- Time-based system using save files (days, last training day, etc.)
- Simple cheat mode for unlimited training (as an optional Easter egg)
- All data stored in plain text files (`cat_condition.txt`, `goods_condition.txt`, `save.properties`)

---

## 🧱 Tech Stack

- **Language:** Java
- **Run target:** Command-line (CLI)
- **File I/O:** `java.nio.file` + plain text & properties files

---

## 🚀 How to Run

1. **Clone this repository**

   `bash`
   `git clone https://github.com/<your-name>/terminal-neko.git
   cd terminal-neko`

2. **Run**
   for windows
   `bash run.sh`
   for Mac/Linux
   `run.sh`
