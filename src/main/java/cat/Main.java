package cat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    // 作弊模式总开关：true = 开启开发者 / 作弊模式
    private static final boolean CHEAT_MODE = false; // 作弊模式：训练不受次数、时间、正确性的限制，方便测试训练功能
    private static final boolean GOD_MODE = true; // 上帝模式：猫咪不会死，状态自动恢复（不消耗天使猫），方便测试不死之身功能
    // 想关掉作弊的时候只要改成 false 就行
    // 在 Main 类里新加这个方法（在 main(...) 下面就行）

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        Scanner in = new Scanner(System.in);

        // SaveStore saveStore = new SaveStore(Paths.get("savefile.properties"));
        // 以当前工作目录（out/）为基准，回到上一级，指向 ../data/save.properties
        Path projectDir = Paths.get(System.getProperty("user.dir")).normalize();
        Path dataDir = projectDir.resolve("src/main/data").normalize();
        Path resourceDir = projectDir.resolve("src/main/resources").normalize();

        Path condPath = dataDir.resolve("cat_condition.txt");
        Path goodsPath = dataDir.resolve("goods_condition.txt");
        Path idiomPath = dataDir.resolve("idiom.txt");

        // 先用这一种
        Path savePath = getSavePath();
        // 或者开发期临时用：Path savePath = dataDir.resolve("save.properties");

        var saveStore = new SaveStore(savePath);
        // var goodsPath = dataDir.resolve("goods_condition.txt");

        SaveStore.SaveState state = saveStore.read(); // 读取游戏进度
        // Timekeeper timekeeper = new Timekeeper();

        // int days = timekeeper.syncDays(saveStore, state);
        int days = Timekeeper.syncDays(saveStore, state); // 改为静态调用

        // ===== 新增：起名指引 =====
        if (state.catName == null || state.catName.isEmpty()) {
            System.out.println("🐾 看来这是一只刚来到宠物商店的小猫。");
            System.out.print("请给你的小猫起个专属的名字吧：");
            state.catName = in.nextLine().trim();
            if (state.catName.isEmpty()) {
                state.catName = "小异端"; // 玩家直接敲回车的话，给个默认硬核名字
            }
            saveStore.write(state);
            System.out.println("起名成功！以后它就叫「" + state.catName + "」啦！\n");
        }
        // ==========================

        // ===== ⭐新增这行：无论如何，启动时把名字同步到TXT文件里 =====
        CatConditionFile.writeName(condPath, state.catName);
        // ==========================

        // 在 Main.java 的 main 方法天数同步后
        if (state.dayCount >= 30 && !state.isAscended) {
            state.isAscended = true; // 进入修行状态
            saveStore.write(state);
            System.out.println("✨ 达成阶段性成就：【满月飞升】！");
            System.out.println("你的猫咪已养育满 30 天，功德圆满，现已升入天堂修行。");

            // ===== 补上缺失的这行代码，发放飞升奖励 =====
            CatConditionFile.incAngelCount(condPath);
            System.out.println("（作为飞升的嘉奖，你立刻获得了 1 只天使猫！）");
            // ==========================================

            System.out.println("它会在天堂注视着你，你可以连续 7 天张贴“寻猫启示”唤它回家。");
        }

        // int steps = Timekeeper.decayBy3Hours(saveStore, state, condPath);
        // if (steps > 0) System.out.println("已按 3 小时衰减了 " + steps + " 次");
    try {
        int steps = Timekeeper.decayBy3Hours(saveStore, state, condPath);
        if (steps > 0) {
            System.out.println("已按 3 小时衰减了 " + steps + " 次");
        }
    } catch (CatConditionFile.CatDiedException e) {
        try {
            if (GOD_MODE || CatConditionFile.hasAngel(condPath)) {
                CatConditionFile.restoreAll(condPath);

                if (!GOD_MODE) {
                    CatConditionFile.decAngelCount(condPath);
                }

                state = saveStore.read();
                // 地狱之子，无用天使（如果注释后两行）
                state.lastTs = System.currentTimeMillis();
                saveStore.write(state);

                if (GOD_MODE) {
                    System.out.println("达成成就：代码之子，不死之身");
                }
                if (!GOD_MODE) {
                    System.out.println("达成成就：超越生死");
                }
                System.out.println("天使猫自动发动！已阻止死亡并恢复所有状态。");
            } else {
                System.out.println(e.getMessage());
                System.out.print("要重新领养一只新猫吗？(y/N)：");
                String ans = in.nextLine().trim();

                if (ans.equalsIgnoreCase("y") || ans.equalsIgnoreCase("yes")) {
                    resetForReAdopt(savePath, condPath);
                    state = saveStore.read();
                    System.out.println("你重新领养了一只新的小猫。");

                    // ===== 新增：重新领养后立刻起名 =====
                    System.out.print("请给新来的小猫起个名字吧：");
                    state.catName = in.nextLine().trim();
                    if (state.catName.isEmpty()) {
                        state.catName = "小异端";
                    }
                    saveStore.write(state);
                    // ==================================

                    // ===== ⭐新增这行：无论如何，启动时把名字同步到TXT文件里 =====
                    CatConditionFile.writeName(condPath, state.catName);
                    // ==========================

                } else {
                    System.out.println("已退出程序。");
                    return;
                }
            }
        } catch (IOException ex) {
            System.out.println("天使猫/重置处理失败：" + ex.getMessage());
            return;
        }

    }

        System.out.println("今天是你和猫咪在一起的第 " + (state.dayCount + 1) + " 天。");

        CatConditionFile.writeDay(condPath, state.dayCount);


            System.out.println("**************************************************");
            System.out.println("                  欢迎光临宠物商店                   ");
            System.out.println("**************************************************");
            System.out.println("  这是一家只卖“文字的小猫”的小店。");
            System.out.println("  在这里，你可以领养、抚摸、喂食、清洁、陪玩，");
            System.out.println("  用每日几分钟，养大一只可爱的小猫。");
            System.out.println();
            System.out.println("  输入h 帮助/说明");
            System.out.println("  输入exit 退出");
            System.out.println("存档路径: " + savePath.toAbsolutePath());

        while (true) {
        try {
            System.out.print("  请输入选项编号：");
            String x = in.nextLine();
            if (x.equalsIgnoreCase("die")) {
                throw new CatConditionFile.CatDiedException("【调试模式】猫咪立即死亡。");
            }
            if (x.equalsIgnoreCase("h")) {
                Path helpPath = Paths.get(System.getProperty("user.dir"))
                .resolve("src/main/resources/how_to_play.txt")
                .normalize();
                System.out.println("帮助文件路径: " + helpPath);

                try (BufferedReader br = Files.newBufferedReader(helpPath, StandardCharsets.UTF_8)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.out.println("无法读取帮助文件: " + e.getMessage());
                }
            } 
            else if (x.equalsIgnoreCase("exit")) {
                System.out.println("退出程序");
                break;
            } else if (x.equalsIgnoreCase("z")) {

                // ================== 新增：修行拦截 ==================
                if (state.isAscended) {
                    System.out.println("🚫 此时猫咪正在天堂修行，你的呼唤它暂时听不见。");
                    System.out.println("请通过主菜单的 V 键张贴启事。");
                    continue; // 直接跳回主循环开头，不显示下面的动作菜单
                }
                // ====================================================

                System.out.println("你进入了动作处理菜单");
                System.out.println("1. 喂猫粮");
                System.out.println("2. 喂超级猫粮");
                System.out.println("3. 喂牛奶");
                System.out.println("4. 喂鱼");
                System.out.println("5. 喂水");
                System.out.println("6. 爱抚");
                System.out.println("7. 玩耍");
                System.out.println("8. 洗澡");
                System.out.println("9. 训练");
                System.out.println("10. 打扫");
                System.out.println("11. 治病");
                System.out.println("12. 挣钱");
                System.out.println("13. 购物");
                System.out.println("0. 返回主菜单");
                System.out.print("请选择动作编号：");
                String action = in.nextLine();

                // ================== 全局负面状态拦截 ==================
                if (state.isSick) {
                    // 允许：治病(11)、挣钱(12)、购物(13)、退出(0)
                    if (!action.equals("11") &&
                            !action.equals("12") &&
                            !action.equals("13") &&
                            !action.equals("0") &&
                            !action.equalsIgnoreCase("exit")) {
                        System.out.println("😿 " + state.catName + " 看起来病恹恹的，没有精神理你。请先【治病】！");
                        continue;
                    }
                }

                // ================== 新增：卫生环境拦截机制 ==================
                if (state.poopCount >= 4) {
                    // 如果达到或超过 4，且玩家选择的不是 打扫(10)、治病(11)、挣钱(12)、购物(13)、退出(0)
                    if (!action.equals("10") &&
                            !action.equals("11") &&
                            !action.equals("12") &&
                            !action.equals("13") &&
                            !action.equals("0") &&
                            !action.equalsIgnoreCase("exit")) {
                        System.out.println("💩 猫砂盆太满了，房间里味道很重！" + state.catName + " 心情很烦躁，拒绝了你的互动！");
                        System.out.println("👉 请先输入 10 进行【打扫】！");
                        continue; // 直接跳过本次 switch 循环，不执行动作
                    }
                }
                // ==========================================================

                switch (action) {
                    case "1" ->
                    { // 猫粮
                        try {
                        // 1. 先尝试消耗一份「猫粮」
                        boolean used = CatConditionFile.useItem(goodsPath, "猫粮");
                        if (!used) {
                        System.out.println("你的猫粮已经用完了，先去商店补货吧。");
                        } else {
                        // 2. 物品数量成功减 1，再去加饥饿度
                        boolean ok = CatConditionFile.feed(condPath, 1, 6); // +1，最多 6 颗 ♥
                        if (ok) {
                            System.out.println("你给猫喂了一份猫粮，饥饿度 +1。");
                            // ===== 新增：吃进去1点，垃圾累加1点 =====
                            state.poopCount += 1;
                            saveStore.write(state);
                        }
                        else {
                            System.out.println("猫已经吃得很饱了，饥饿度没有再增加。");
                                }
                            }
                        } catch (java.io.IOException e) {
                            System.out.println("喂猫粮时发生错误：" + e.getMessage());
                        }
                    }
                    // System.out.println("你假装给猫喂了一份猫粮。（尚未实现数值变化）");
                    case "2" -> {
                        try {
                        boolean used = CatConditionFile.useItem(goodsPath, "超级猫粮");
                        if (!used) {
                        System.out.println("你的超级猫粮已经用完了，先去商店补货吧。");
                        } else
                        {
                            boolean ok = CatConditionFile.feed(condPath, 2, 6); // +2，最多 6 颗 ♥
                            if (ok) {
                                System.out.println("你给猫喂了一份超级猫粮，饥饿度 +2。");
                                // ===== 新增：吃进去2点，垃圾累加2点 =====
                                state.poopCount += 2;
                                saveStore.write(state);
                            }
                                else {
                                    System.out.println("猫已经吃得很饱了，饥饿度没有再增加。");
                                }
                            }
                        } catch (java.io.IOException e) {
                            System.out.println("喂超级猫粮时发生错误：" + e.getMessage());
                        }
                    }
                    case "3" -> {  // 喂牛奶
                    try {
                    // 1. 先试着消耗一份牛奶
                    boolean used = CatConditionFile.useItem(goodsPath, "牛奶");
                    if (!used) {
                        System.out.println("你的牛奶已经用完了，先去商店补货吧。");
                    } else {
                    // 2. 饥饿度 +1
                    boolean fed = CatConditionFile.feed(condPath, 1, 6);
                    // 3. 口渴度 +1
                    boolean drank = CatConditionFile.drink(condPath, 1, 4); // 口渴度 max = 4

                    // 4. 显示提示
                    if (fed && drank) {
                        System.out.println("你喂了猫一杯牛奶，饥饿度 +1，口渴度 +1。");
                        // ===== 新增：吃进去1点，垃圾累加1点 =====
                        state.poopCount += 1;
                        saveStore.write(state);
                    } else if (!fed && drank) {
                        System.out.println("猫已经不饿了，但口渴得到了缓解 +1。");
                    } else if (fed && !drank) {
                        System.out.println("猫已经不渴了，但饥饿度得到 +1。");
                        // ===== 新增：吃进去1点，垃圾累加1点 =====
                        state.poopCount += 1;
                        saveStore.write(state);
                    } else {
                        System.out.println("猫既不饿也不渴，现在牛奶没什么效果。");
                    }
                }
                    } catch (java.io.IOException e) {
                        System.out.println("喂牛奶时发生错误：" + e.getMessage());
                    }
                }
                case "4" -> { // 喂鱼
                    // 先检查鱼是不是还有（你原来怎么写的就怎么保留）
                    // 比如：检查 goods_condition.txt 里的数量，扣一条鱼

                    boolean canGrantTicket;
                    if (CHEAT_MODE) {
                        // 作弊模式：每次喂鱼都能拿训练机会
                        canGrantTicket = true;
                    } else {
                        // 正常模式：一天只能拿一次
                        canGrantTicket = (state.lastFishDay != state.dayCount);
                    }

                    if (!canGrantTicket) {
                        System.out.println("你又给猫喂了一条鱼，但今天的训练机会已经拿过了。");
                    } else {
                        state.trainTickets++;            // 训练机会 +1
                        state.lastFishDay = state.dayCount;  // 作弊模式可以保留这行，也无所谓
                        saveStore.write(state);

                        System.out.println("你给猫喂了一条鱼，获得了一次训练机会！");
                        if (CHEAT_MODE) {
                            System.out.println("【作弊模式】今天可以反复喂鱼拿训练机会。");
                        }
                    }
                }

                    case "5" ->
                    { // 喂水
                        try {
                            boolean ok = CatConditionFile.drink(condPath, 1, 4); // 口渴度 +1，最多 4 ♥
                            if (ok) {
                                System.out.println("你给猫喝了一点水，口渴度 +1。");
                            } else {
                                System.out.println("猫已经不渴了，口渴度没有再增加。");
                            }
                        } catch (java.io.IOException e) {
                            System.out.println("喂水时发生错误: " + e.getMessage());
                        }
                    }
                    //System.out.println("你假装给猫倒了一碗水。（尚未实现数值变化）");
                    case "6" ->
                    // 爱抚：亲密度 +1，最多 4 颗 ♥
                    {
                        try {
                        // var condPath = java.nio.file.Paths.get(System.getProperty("user.dir"))
                        //     .resolve("../data/cat_condition.txt")
                        //     .normalize();

                        boolean ok = CatConditionFile.pet(condPath, 1, 4);
                        if (ok) {
                            System.out.println("你轻轻地抚摸了猫咪，它发出满足的呼噜声，亲密度 +1。");
                        } else {
                            System.out.println("猫已经很黏你了，亲密度没有再增加。");
                        }
                        } catch (java.io.IOException e) {
                            System.out.println("爱抚时发生错误: " + e.getMessage());
                        }
                    }
                    //System.out.println("你轻轻地摸了摸猫，小猫发出咕噜声。（尚未实现数值变化）");
                    case "7" ->
                    {
                    // ===== 玩家选择“玩耍” =====
                    // 先准备两个文件路径：宠物状态、物品状态
                    var baseDir = java.nio.file.Paths.get(System.getProperty("user.dir"));

                    try {
                        // 1. 先扣掉一个“球”（无论兴奋度满不满，只要有球就-1）
                        boolean usedBall = CatConditionFile.useItem(goodsPath, "球");

                        if (!usedBall) {
                            System.out.println("你已经没有球可以玩了。");
                        } else {
                            // CatConditionFile.changeItemCount(goodsPath, "球", -1);

                            // 2. 再尝试让兴奋度 +1（最多 4 颗♥，你可以按自己设定改 maxHearts）
                            // boolean ok = CatConditionFile.incExciteLine(condPath, 1, 4);
                            boolean ok = CatConditionFile.play(condPath, 1, 4);

                            if (ok) {
                                System.out.println("你和猫玩了一会儿球，兴奋度 +1，消耗 1 个球。");
                            } else {
                                System.out.println("猫已经非常兴奋了，兴奋度没有再增加，但还是消耗了 1 个球。");
                            }
                        }
                    } catch (java.io.IOException e) {
                        System.out.println("和猫玩耍时发生错误：" + e.getMessage());
                    }
                }
                    //System.out.println("你和猫玩了一会儿玩具。（尚未实现数值变化）");
                    case "8" ->
                        {
                            System.out.println("你帮猫洗了个澡。（清洁度 +24 个井号）");

                        try {
                            boolean ok = CatConditionFile.wash(condPath, 24, 48);  // delta = 24, max = 48
                            if (!ok) {
                                System.out.println("猫已经非常干净了，清洁度没有再增加。\n");
                            }
                        } catch (java.io.IOException e) {
                            System.out.println("洗澡时发生错误：" + e.getMessage());
                        }
                    }

                    case "9" -> { // 训练
                        doTraining(saveStore, state, condPath, in, false);
                        // if (!CHEAT_MODE && state.trainTickets <= 0) {
                        //     System.out.println("今天的训练机会已经用完了，可以喂鱼来获得新的训练次数。");
                        // } else {
                        //     doTraining(scanner, saveStore, state, CHEAT_MODE);
                        //     // 注意：下面我们要把 doTraining 改成带一个 cheatMode 参数
                        // }
                    }
                    //System.out.println("你训练了猫抓老鼠的技能。（尚未实现数值变化）");
                    case "10" -> {
                        if (state.poopCount >= 4) {
                            state.poopCount = 0;
                            saveStore.write(state);
                            System.out.println("✨ 你清理了猫砂盆，打扫了房间，空气变得清新了！" + state.catName + " 看起来开心多了。");
                        } else {
                            System.out.println("环境还算干净，暂时不需要大扫除。");
                            // 如果你想允许玩家提前打扫，也可以在这里加: state.poopCount = 0; saveStore.write(state);
                        }
                    }//System.out.println("你打扫了猫的生活环境。（尚未实现数值变化）");
                    case "11" -> {
                        if (!state.isSick) {
                            System.out.println("🐱 " + state.catName + " 非常健康，不需要看医生。");
                        } else {
                            int money = CatConditionFile.readMoney(goodsPath);
                            if (money >= 20) {
                                CatConditionFile.incMoney(goodsPath, -20); // 扣 20 元
                                state.isSick = false;
                                state.poopCount = 0; // 医生顺便做了全身清洁，清空便便
                                saveStore.write(state);

                                // 治好病后，顺手把数值回满一点（比如加 2 颗心），体验更好
                                // 营养液也属于食物,医生给猫咪打了营养液，并送了一个小罐头安抚情绪，饥饿度恢复 2 颗心
                                CatConditionFile.feed(condPath, 2, 6);

                                // ===== 补上这行：让医生顺手把清洁度洗满 =====
                                CatConditionFile.wash(condPath, 48, 48);

                                System.out.println("💉 你花了 20 元带猫咪看了兽医，打了一针后，它又变得活蹦乱跳了！");
                            } else {
                                System.out.println("💰 你的余额不足 20 元，付不起医药费！快去【挣钱】！");
                            }
                        }
                    }//System.out.println("你给猫看了兽医，猫咪恢复了健康。（尚未实现数值变化）");
                    case "12" ->
                    {
                        doWork(scanner, idiomPath, goodsPath);
                        break;
                    }
                    //System.out.println("你回答了一些题，赚了一些钱。（尚未实现数值变化）");
                    case "13" ->
                    {
                        doShopping(scanner, goodsPath);
                    }
                    //System.out.println("你去了商店，买了一些物品。（尚未实现数值变化）");
                    case "0" -> System.out.println("返回主菜单。");

                    // ===== 新增这三行：让 exit 直接退出程序 =====
                    case "exit" -> {
                        System.out.println("退出程序");
                        System.exit(0); // 直接强行结束当前 Java 进程
                    }
                    // =======================================

                    // 比如作弊菜单里：
                    case "cheat" -> {  // 你自己选一个编号 / 关键字
                        doTraining(saveStore, state, condPath, in, true); // 作弊训练
                    }
                    //case "cheat" -> showCheatMenu(state); // 作弊菜单，方便测试
                    default -> System.out.println("无效的动作编号。");
                    }
            } else if (x.equalsIgnoreCase("x")) {
                System.out.println("你进入状态显示菜单，可查看宠物状态和物品状态。");
                 // 路径：src/main/data/cat_condition.txt
//            var catPath = java.nio.file.Paths.get(System.getProperty("user.dir"))
//                .resolve("../data/cat_condition.txt")
//                .normalize();
            // var goodsPath = java.nio.file.Paths.get(System.getProperty("user.dir"))
            //     .resolve("../data/goods_condition.txt")
            //     .normalize();

            try {
                System.out.println("===== 宠物状态 =====");
                if (state.isSick) System.out.println("健康状况：🔴 生病中 (需要治疗)");
                else System.out.println("健康状况：🟢 健康");
                java.nio.file.Files.lines(condPath).forEach(System.out::println);

                System.out.println("===== 物品状态 =====");
                java.nio.file.Files.lines(goodsPath).forEach(System.out::println);
            } catch (java.io.IOException e) {
                System.out.println("读取状态文件时出错: " + e.getMessage());
            }
            } else if (x.equalsIgnoreCase("c")) {
                System.out.println("你可重新领养宠物。");
            } else if (x.equalsIgnoreCase("v")) {
                try {
                    // ================== 新增：猫在天堂时的“召唤”逻辑 ==================
                    if (state.isAscended) {
                        java.time.LocalDate today = java.time.LocalDate.now();

                        // 1. 判定连续张贴天数
                        if (state.lastPostDate != null && state.lastPostDate.plusDays(1).equals(today)) {
                            // 情况A：昨天贴了，今天接着贴 -> 连续天数 +1
                            state.postNoticeDays++;
                        } else if (state.lastPostDate != null && state.lastPostDate.equals(today)) {
                            // 情况B：今天已经贴过了 -> 防止玩家一天内狂按V键刷天数
                            System.out.println("今天已经张贴过寻猫启事啦，心急吃不了热豆腐，明天再来吧！");
                            continue; // 阻断后续操作，直接重新循环
                        } else {
                            // 情况C：断连了，或者这是第一天贴 -> 天数重置为 1
                            state.postNoticeDays = 1;
                        }

                        // 更新张贴日期并存档
                        state.lastPostDate = today;
                        saveStore.write(state);
                        System.out.println("📢 你张贴了一张寻猫启事。当前已连续坚持：" + state.postNoticeDays + " 天。");

                        // 2. 判定是否满 7 天
                        if (state.postNoticeDays >= 7) {
                            System.out.println("🎊 诚心所至，金石为开！你的猫咪从天堂回到了你身边！");

                            // 重置状态
                            state.isAscended = false; // 猫咪回归人间
                            state.postNoticeDays = 0; // 清空启事天数
                            state.dayCount = 0;       // 重新开启下一次30天修行轮回
                            saveStore.write(state);

                            // 奖励：状态回满，外加送1只天使猫
                            CatConditionFile.incAngelCount(condPath);
                            CatConditionFile.restoreAll(condPath);

                            System.out.println("（不仅如此，猫咪还为你带回了 1 只新的天使猫作为修行礼物！）");
                        } else {
                            System.out.println("距离重逢还需坚持 " + (7 - state.postNoticeDays) + " 天。");
                        }
                    }
                    // ================== 原有：猫在人间时的“回血”逻辑 ==================
                    else {
                        if (GOD_MODE || CatConditionFile.hasAngel(condPath)) {
                            CatConditionFile.restoreAll(condPath);

                            if (!GOD_MODE) {
                                CatConditionFile.decAngelCount(condPath);
                            }

                            System.out.println("天使猫发动，所有状态已恢复！");
                        } else {
                            System.out.println("你现在没有天使猫。");
                        }
                    }
                } catch (IOException e) {
                    System.out.println("读取/恢复状态时出错：" + e.getMessage());
                }
                // System.out.println("你可触发天使猫，恢复所有点数到最大值，如果猫咪出走，按此键可张贴寻猫启事，连续张贴7天猫咪回家。");
                // System.out.println("你使用了天使猫技能，正在恢复所有状态……");
                // try {
                // CatConditionFile.restoreAll(condPath);
                // System.out.println("所有状态已恢复到最大值！");
                // } catch (IOException e) {
                // System.out.println("恢复状态时出错：" + e.getMessage());
                // }
            } else {
                System.out.println("无效输入，请重新输入。");
            }
            System.out.println();
        }  catch (CatConditionFile.CatDiedException e) {
            try {
                if (GOD_MODE || CatConditionFile.hasAngel(condPath)) {
                    CatConditionFile.restoreAll(condPath);

                    if (!GOD_MODE) {
                        CatConditionFile.decAngelCount(condPath);
                    }

                    state = saveStore.read();
                    // 地狱之子，无用天使
                    state.lastTs = System.currentTimeMillis();
                    saveStore.write(state);
                    System.out.println("天使猫自动发动！已阻止死亡并恢复所有状态。");
                } else {
                    System.out.println(e.getMessage());
                    System.out.print("要重新领养一只新猫吗？(y/N)：");
                    String ans = in.nextLine().trim();

                    if (ans.equalsIgnoreCase("y") || ans.equalsIgnoreCase("yes")) {
                        resetForReAdopt(savePath, condPath);
                        state = saveStore.read();
                        System.out.println("你重新领养了一只新的小猫。");

                        // ===== 新增：重新领养后立刻起名 =====
                        System.out.print("请给新来的小猫起个名字吧：");
                        state.catName = in.nextLine().trim();
                        if (state.catName.isEmpty()) {
                            state.catName = "小异端";
                        }
                        saveStore.write(state);
                        // ==================================

                        // ===== ⭐新增这行：无论如何，启动时把名字同步到TXT文件里 =====
                        CatConditionFile.writeName(condPath, state.catName);
                        // ==========================

                    } else {
                        System.out.println("已退出程序。");
                        break;
                    }
                }
            } catch (IOException ex) {
                System.out.println("天使猫/重置处理失败：" + ex.getMessage());
                break;
            }
        }
    }
        in.close();
    }
    // isCheat = false  正常训练（有次数限制、有时间与正确性要求）
    // isCheat = true   作弊训练（无次数限制、无时间与正确性要求）
    private static void doTraining(
            SaveStore saveStore,
            SaveStore.SaveState state,
            java.nio.file.Path condPath,
            java.util.Scanner in,
            boolean isCheat
    ) throws java.io.IOException {
        // 1. 正常模式下要检查和消耗训练次数
        if (!isCheat) {
            if (state.trainTickets <= 0) {
                System.out.println("今天的训练机会已经用完了，可以喂鱼来获得新的训练次数。");
                return;
            }
            // 消耗一次训练机会
            state.trainTickets--;
            saveStore.write(state);
        } else {
            System.out.println("【作弊模式】本次训练不消耗训练次数，也不限制次数。");
        }

        // 2. 选一个单词（你可以自己换 / 增加）
        String[] words = { "Clash", "Mihomo", "Singbox", "Xray", "Openwrt", "iKuai", "V2Ray", "pfsense", "Mikrotik", "Merlin", "ShadowSocks", "Trojan", "NaiveProxy","Hysteria" };
        java.util.Random random = new java.util.Random();
        String target = words[random.nextInt(words.length)];

        System.out.println("训练开始！");
        if (!isCheat) {
            System.out.println("请在 5 秒内输入下面这个单词：");
        } else {
            System.out.println("【作弊模式】随便输入什么都算成功：");
        }
        System.out.println(">>> " + target);

        long start = System.currentTimeMillis();
        String input = in.nextLine();
        long costMs = System.currentTimeMillis() - start;

        boolean success;
        if (isCheat) {
            // 作弊：不看时间、不看对错，一律成功
            success = true;
        } else {
            success = input.equals(target) && costMs <= 5000;
        }

        if (success) {
            // 这里用你原来更新“训练度”的代码，比如：
            // CatConditionFile.incTrainLine(condPath, 1, 24);
            CatConditionFile.incTrainLevel(condPath, 1, 999);
            System.out.println("太棒了，你成功抓到了老鼠！训练度 +1。（用时 " + costMs + " ms）");
        } else {
            System.out.println("很遗憾，老鼠跑掉了……（用时 " + costMs + " ms）");
        }
    }

    // 打工挣钱：成语填空，答对 +5 元，答错无惩罚
    private static void doWork(Scanner scanner, Path idiomPath, Path goodsPath) throws IOException {
        // 1. 读取 idiom.txt 里的成语，只保留四个字的
        List<String> idioms = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(idiomPath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && line.length() == 4) {  // 只用四字成语
                    idioms.add(line);
                }
            }
        }
    
        if (idioms.isEmpty()) {
            System.out.println("成语库为空，请检查 src/main/data/idiom.txt。");
            return;
        }

        // 2. 随机选一个成语 & 随机挖掉一个字
        Random random = new Random();
        String idiom = idioms.get(random.nextInt(idioms.size()));

        int missingIndex = random.nextInt(idiom.length());  // 要挖掉的索引 0~3
        char answerChar = idiom.charAt(missingIndex);       // 正确答案

        StringBuilder sb = new StringBuilder(idiom);
        sb.setCharAt(missingIndex, '□');                   // 用 □ 占位
        String question = sb.toString();

        // 3. 出题
        System.out.println("【打工挣钱】请把成语里的空格补全：");
        System.out.println("  " + question);
        System.out.print("请输入缺失的汉字：");

        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println("你什么也没填，这次没有挣到钱。");
            return;
        }

        char userChar = input.charAt(0);

        // 4. 判定 + 加钱
        if (userChar == answerChar) {
            boolean ok = CatConditionFile.incMoney(goodsPath, 5);  // ★ 我们下一步来实现
            if (!ok) {
                System.out.println("回答正确，但修改金钱时出现问题，请检查 goods_condition.txt。");
            } else {
                System.out.println("回答正确！你赚到了 5 元。");
            }
        } else {
            System.out.println("回答错误，这次没有挣到钱。正确答案是：「" + idiom + "」。");
        }
    }

    // 购物：成语答对的 5 元就在这里花掉
    private static void doShopping(Scanner scanner, Path goodsPath) throws IOException {
        // 固定价格
        final int PRICE_CAT_FOOD   = 5;   // 猫粮
        final int PRICE_SUPER_FOOD = 9;   // 超级猫粮
        final int PRICE_MILK       = 7;   // 牛奶
        final int PRICE_FISH       = 15;  // 鱼
        final int PRICE_BALL       = 8;   // 球

        while (true) {
            System.out.println("*************** 商店 ***************");
            int money = CatConditionFile.readMoney(goodsPath);
            System.out.println("当前金钱：$" + money);
            System.out.println();
            System.out.println("请选择要购买的商品：");
            System.out.println("1. 猫粮      - " + PRICE_CAT_FOOD   + " 元");
            System.out.println("2. 超级猫粮  - " + PRICE_SUPER_FOOD + " 元");
            System.out.println("3. 牛奶      - " + PRICE_MILK       + " 元");
            System.out.println("4. 鱼        - " + PRICE_FISH       + " 元");
            System.out.println("5. 球        - " + PRICE_BALL       + " 元");
            System.out.println("0. 返回主菜单");
            System.out.print("请输入选项编号：");

            String choice = scanner.nextLine().trim();
            if (choice.equals("0") || choice.equalsIgnoreCase("exit")) {
                System.out.println("你离开了商店。");
                break;
            }

            int price;
            int itemIndex;
            String itemName;

            switch (choice) {
                case "1" -> { price = PRICE_CAT_FOOD;   itemIndex = 1; itemName = "猫粮"; }
                case "2" -> { price = PRICE_SUPER_FOOD; itemIndex = 2; itemName = "超级猫粮"; }
                case "3" -> { price = PRICE_MILK;       itemIndex = 3; itemName = "牛奶"; }
                case "4" -> { price = PRICE_FISH;       itemIndex = 4; itemName = "鱼"; }
                case "5" -> { price = PRICE_BALL;       itemIndex = 5; itemName = "球"; }
                default -> {
                    System.out.println("无效选项，请重新输入。");
                    continue;
                }
            }

            // 再读一次钱，防止上面显示后被别的动作改过
            money = CatConditionFile.readMoney(goodsPath);
            if (money < price) {
                System.out.println("你的钱不够，无法购买 " + itemName + "。");
                continue;
            }

            // 扣钱 + 增加库存
            CatConditionFile.incMoney(goodsPath, -price);
            CatConditionFile.incGoodsCount(goodsPath, itemIndex, +1);

            System.out.println("你购买了 1 个 " + itemName + "，花费 " + price + " 元。");
            System.out.println();
        }
    }

    private static void resetForReAdopt(Path savePath, Path condPath) throws IOException {
        // 1. 删除旧存档
        Files.deleteIfExists(savePath);

        // 2. 从 src/main/resources 拷贝初始猫状态到 data
        Path template = Paths.get(System.getProperty("user.dir"))
                .resolve("../resources/cat_condition.txt")
                .normalize();

        if (!Files.exists(template)) {
            throw new IOException("找不到模板文件: " + template);
        }

        Files.copy(template, condPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    private static Path getSavePath() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");

        Path baseDir;

        if (os.contains("mac")) {
            // macOS
            baseDir = Paths.get(home, "Library", "Application Support", "宠物世界");
        } else if (os.contains("win")) {
            // Windows
            String appData = System.getenv("APPDATA");
            if (appData != null && !appData.isBlank()) {
                baseDir = Paths.get(appData, "宠物世界");
            } else {
                baseDir = Paths.get(home, "AppData", "Roaming", "宠物世界");
            }
        } else {
            // Linux / 其他 Unix
            String xdgState = System.getenv("XDG_STATE_HOME");
            if (xdgState != null && !xdgState.isBlank()) {
                baseDir = Paths.get(xdgState, "宠物世界");
            } else {
                baseDir = Paths.get(home, ".local", "state", "宠物世界");
            }
        }

        Files.createDirectories(baseDir);
        return baseDir.resolve("save.properties");
    }
}