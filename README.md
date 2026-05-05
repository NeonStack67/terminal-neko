# Terminal Neko 🐱 (宠物世界)

A hardcore, command-line virtual pet cat game written in Java with real-time decay and survival mechanics.

You take care of a digital cat by feeding, cleaning, playing, and training it. But beware: **time passes in the real world**. If you neglect your cat, the litter box will overflow, it will get sick, and eventually pass away. The game features a complete life-and-death cycle, an economy system, and persistent state storage.

> **中文简介：** > 这是一个基于 Java 开发的硬核命令行电子宠物小游戏。  
> 你不仅需要通过喂食、喝水、洗澡、玩耍和训练来照顾你的小猫，还要面对**真实的时间流逝**。长时间不登录会导致猫咪饥饿、猫砂盆溢出甚至生病。游戏拥有完整的生老病死轮回、打工赚钱经济系统，以及隐藏的“飞升”与天使猫彩蛋。所有游戏进度均自动持久化保存在本地。

---

## ✨ Core Features (核心特性)

* ⏱️ **Real-Time Decay (真实时间流逝):** 离线期间时间也会流逝！每隔 3 小时自动计算属性衰减，考验你作为主人的责任心。
* 💩 **Hygiene & Sickness (卫生与疾病系统):** 喂食会产生排泄物，猫砂盆满载会导致猫咪拒绝互动。长期处于恶劣环境或不洗澡会导致猫咪生病（需花费金钱看兽医）。
* 💰 **Economy System (经济系统):** 通过“成语填空”打工赚钱，在商店购买猫粮、超级猫粮、牛奶、玩具球，或者用来支付昂贵的医疗费。
* 👼 **Life, Death & Rebirth (生死轮回与飞升):** * **重新领养**：猫咪意外死亡后不再需要手动删档，系统支持一键重新领养并重置世界线。
    * **满月飞升**：存活满 30 天的猫咪将功德圆满，升入天堂并留下“天使猫”庇护后代。连续 7 天张贴寻猫启事可将其唤回人间。
* 💾 **Persistent Data (数据持久化):** 状态全自动存档至纯文本与 `.properties` 文件，随开随玩。
* 🎨 **GUI Preview (图形界面预览):** 正在开发基于 Swing 的跨平台图形化版本 (`GUIMain.java`)。

---

## 🧱 Tech Stack (技术栈)

* **Language:** Java
* **Architecture:** MVC Pattern (Model-View-Controller) separation for logic and rendering
* **Run target:** Command-line (CLI) & Java Swing (Experimental)
* **File I/O:** `java.nio.file` + plain text (`.txt`) & properties (`.properties`) files

---

## 🚀 How to Run (如何运行)

1. **Clone this repository (克隆代码库)**

   ```bash
   git clone [https://github.com/](https://github.com/)<your-name>/terminal-neko.git
   cd terminal-neko

Run the game (运行游戏)

For Windows:

DOS
run.bat
For macOS / Linux:

Bash
chmod +x run.sh
./run.sh
📁 Directory Structure (项目结构)
Plaintext
terminal-neko/
├── src/main/
│   ├── java/cat/          # 核心 Java 源代码
│   │   ├── Main.java            # 游戏主入口 (控制台版本)
│   │   ├── GUIMain.java         # 游戏主入口 (图形化实验版本)
│   │   ├── CatConditionFile.java# 宠物状态逻辑与文件解析
│   │   ├── Timekeeper.java      # 真实时间流逝与衰减计算器
│   │   └── SaveStore.java       # 核心存档对象序列化
│   ├── resources/         # 初始模板数据源 (游戏初始化时读取)
│   └── data/              # 玩家动态生成的存档数据 (git ignore)
├── run.bat                # Windows 启动脚本
├── run.sh                 # macOS/Linux 启动脚本
└── README.md
