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
import java.util.HashMap;
import java.util.Map;

public class Main {
    // 作弊模式总开关：true = 开启开发者 / 作弊模式
    private static final boolean CHEAT_MODE = false; // 作弊模式：训练不受次数、时间、正确性的限制，方便测试训练功能
    private static final boolean GOD_MODE = true; // 上帝模式：猫咪不会死，状态自动恢复（不消耗天使猫），方便测试不死之身功能
    // 想关掉作弊的时候只要改成 false 就行
    // 在 Main 类里新加这个方法（在 main(...) 下面就行）

    private enum Lang {
        ZH, EN
    }

    private static Lang currentLang = Lang.ZH;

    private static final Map<String, String> ZH = new HashMap<>();
    private static final Map<String, String> EN = new HashMap<>();

    static {
        // ===== 主界面 =====
        ZH.put("welcome.title", "                  欢迎光临宠物商店                   ");
        EN.put("welcome.title", "                  Welcome to Pet World               ");

        ZH.put("welcome.line1", "  这是一家只卖“文字的小猫”的小店。");
        EN.put("welcome.line1", "  This is a small shop that sells only “text kittens”.");

        ZH.put("welcome.line2", "  在这里，你可以领养、抚摸、喂食、清洁、陪玩，");
        EN.put("welcome.line2", "  Here, you can adopt, pet, feed, clean, and play with your kitten.");

        ZH.put("welcome.line3", "  用每日几分钟，养大一只可爱的小猫。");
        EN.put("welcome.line3", "  Spend a few minutes each day and raise a lovely little cat.");

        ZH.put("help.tip", "  输入h 帮助/说明");
        EN.put("help.tip", "  Type h for help/instructions");

        ZH.put("lang.tip", "  输入l 切换语言 / language");
        EN.put("lang.tip", "  Type l to switch language / 切换语言");

        ZH.put("exit.tip", "  输入exit 退出");
        EN.put("exit.tip", "  Type exit to quit");

        ZH.put("save.path", "存档路径: ");
        EN.put("save.path", "Save path: ");

        ZH.put("input.option", "  请输入选项编号：");
        EN.put("input.option", "  Please enter an option: ");

        ZH.put("invalid.input", "无效输入，请重新输入。");
        EN.put("invalid.input", "Invalid input. Please try again.");

        ZH.put("quit", "退出程序");
        EN.put("quit", "Quit game");

        // ===== 语言菜单 =====
        ZH.put("lang.menu.title", "===== 语言设置 =====");
        EN.put("lang.menu.title", "===== Language Settings =====");

        ZH.put("lang.menu.1", "1. 中文");
        EN.put("lang.menu.1", "1. Chinese");

        ZH.put("lang.menu.2", "2. English");
        EN.put("lang.menu.2", "2. English");

        ZH.put("lang.choose", "请选择语言：");
        EN.put("lang.choose", "Choose language: ");

        ZH.put("lang.changed.zh", "语言已切换为中文。");
        EN.put("lang.changed.zh", "Language switched to Chinese.");

        ZH.put("lang.changed.en", "语言已切换为英文。");
        EN.put("lang.changed.en", "Language switched to English.");

        ZH.put("lang.invalid", "语言选项无效。");
        EN.put("lang.invalid", "Invalid language option.");

        // ===== 动作菜单 =====
        ZH.put("action.enter", "你进入了动作处理菜单");
        EN.put("action.enter", "You entered the action menu.");

        ZH.put("action.1", "1. 喂猫粮");
        EN.put("action.1", "1. Feed cat food");

        ZH.put("action.2", "2. 喂超级猫粮");
        EN.put("action.2", "2. Feed super cat food");

        ZH.put("action.3", "3. 喂牛奶");
        EN.put("action.3", "3. Feed milk");

        ZH.put("action.4", "4. 喂鱼");
        EN.put("action.4", "4. Feed fish");

        ZH.put("action.5", "5. 喂水");
        EN.put("action.5", "5. Give water");

        ZH.put("action.6", "6. 爱抚");
        EN.put("action.6", "6. Pet");

        ZH.put("action.7", "7. 玩耍");
        EN.put("action.7", "7. Play");

        ZH.put("action.8", "8. 洗澡");
        EN.put("action.8", "8. Bathe");

        ZH.put("action.9", "9. 训练");
        EN.put("action.9", "9. Train");

        ZH.put("action.10", "10. 打扫");
        EN.put("action.10", "10. Clean");

        ZH.put("action.11", "11. 治病");
        EN.put("action.11", "11. Treat illness");

        ZH.put("action.12", "12. 挣钱");
        EN.put("action.12", "12. Work for money");

        ZH.put("action.13", "13. 购物");
        EN.put("action.13", "13. Shopping");

        ZH.put("action.0", "0. 返回主菜单");
        EN.put("action.0", "0. Return to main menu");

        ZH.put("action.choose", "请选择动作编号：");
        EN.put("action.choose", "Choose an action: ");

        ZH.put("action.invalid", "无效的动作编号。");
        EN.put("action.invalid", "Invalid action number.");

        ZH.put("back.main", "返回主菜单。");
        EN.put("back.main", "Returned to main menu.");

        // ===== 常用反馈 =====
        ZH.put("food.empty", "你的猫粮已经用完了，先去商店补货吧。");
        EN.put("food.empty", "You have run out of cat food. Go to the shop first.");

        ZH.put("feed.catfood.ok", "你给猫喂了一份猫粮，饥饿度 +1。");
        EN.put("feed.catfood.ok", "You fed the cat some cat food. Hunger +1.");

        ZH.put("cat.full", "猫已经吃得很饱了，饥饿度没有再增加。");
        EN.put("cat.full", "The cat is already full. Hunger did not increase.");

        ZH.put("water.ok", "你给猫喝了一点水，口渴度 +1。");
        EN.put("water.ok", "You gave the cat some water. Thirst +1.");

        ZH.put("water.full", "猫已经不渴了，口渴度没有再增加。");
        EN.put("water.full", "The cat is no longer thirsty. Thirst did not increase.");

        // ===== Startup / naming =====
        ZH.put("file.init.cat", "📝 已自动生成初始宠物状态文件。");
        EN.put("file.init.cat", "📝 Initial pet status file has been created.");

        ZH.put("file.init.goods", "📦 已自动生成初始物品清单文件。");
        EN.put("file.init.goods", "📦 Initial item inventory file has been created.");

        ZH.put("file.init.idiom", "📚 已自动生成成语词库文件。");
        EN.put("file.init.idiom", "📚 Initial idiom word bank has been created.");

        ZH.put("name.first.cat", "🐾 看来这是一只刚来到宠物商店的小猫。");
        EN.put("name.first.cat", "🐾 It looks like this kitten has just arrived at the pet shop.");

        ZH.put("name.ask", "请给你的小猫起个专属的名字吧：");
        EN.put("name.ask", "Please give your kitten a special name: ");

        ZH.put("name.default", "小异端");
        EN.put("name.default", "Little Heretic");

        ZH.put("name.success.prefix", "起名成功！以后它就叫「");
        EN.put("name.success.prefix", "Name set! From now on, this kitten is called \"");

        ZH.put("name.success.suffix", "」啦！");
        EN.put("name.success.suffix", "\"!");

// ===== Day / ascension =====
        ZH.put("day.today.prefix", "今天是你和猫咪在一起的第 ");
        EN.put("day.today.prefix", "Today is day ");

        ZH.put("day.today.suffix", " 天。");
        EN.put("day.today.suffix", " with your kitten.");

        ZH.put("ascend.achievement", "✨ 达成阶段性成就：【满月飞升】！");
        EN.put("ascend.achievement", "✨ Milestone achieved: Full-Moon Ascension!");

        ZH.put("ascend.message", "你的猫咪已养育满 30 天，功德圆满，现已升入天堂修行。");
        EN.put("ascend.message", "You have raised your kitten for 30 days. Its journey is complete, and it has ascended to heaven for training.");

        ZH.put("ascend.reward", "（作为飞升的嘉奖，你立刻获得了 1 只天使猫！）");
        EN.put("ascend.reward", "(As a reward for ascension, you immediately received 1 Angel Cat!)");

        ZH.put("ascend.notice", "它会在天堂注视着你，你可以连续 7 天张贴“寻猫启示”唤它回家。");
        EN.put("ascend.notice", "It will watch over you from heaven. You can post Missing Cat Notices for 7 days in a row to call it home.");

// ===== Death / angel / re-adopt =====
        ZH.put("god.achievement", "达成成就：代码之子，不死之身");
        EN.put("god.achievement", "Achievement unlocked: Child of Code, Immortal Body");

        ZH.put("death.beyond", "达成成就：超越生死");
        EN.put("death.beyond", "Achievement unlocked: Beyond Life and Death");

        ZH.put("angel.auto", "天使猫自动发动！已阻止死亡并恢复所有状态。");
        EN.put("angel.auto", "The Angel Cat activated automatically! Death was prevented and all stats were restored.");

        ZH.put("readopt.ask", "要重新领养一只新猫吗？(y/N)：");
        EN.put("readopt.ask", "Would you like to adopt a new kitten? (y/N): ");

        ZH.put("readopt.success", "你重新领养了一只新的小猫。");
        EN.put("readopt.success", "You adopted a new kitten.");

        ZH.put("readopt.name.ask", "请给新来的小猫起个名字吧：");
        EN.put("readopt.name.ask", "Please give the new kitten a name: ");

        ZH.put("program.exit", "已退出程序。");
        EN.put("program.exit", "Game exited.");

        ZH.put("program.quit", "退出程序");
        EN.put("program.quit", "Quit game");

        // ===== Help =====
        ZH.put("help.path", "帮助文件路径: ");
        EN.put("help.path", "Help file path: ");

        ZH.put("help.read.fail", "无法读取帮助文件: ");
        EN.put("help.read.fail", "Unable to read help file: ");

        // ===== Status Menu =====
        ZH.put("status.enter", "你进入状态显示菜单，可查看宠物状态和物品状态。");
        EN.put("status.enter", "You entered the status menu. You can view the pet status and item inventory.");

        ZH.put("status.pet.title", "===== 宠物状态 =====");
        EN.put("status.pet.title", "===== Pet Status =====");

        ZH.put("status.goods.title", "===== 物品栏 =====");
        EN.put("status.goods.title", "===== Inventory =====");

        ZH.put("status.health.ok", "健康状况：🟢 健康");
        EN.put("status.health.ok", "Health: 🟢 Healthy");

        ZH.put("status.health.sick", "健康状况：🔴 生病中（需要治疗）");
        EN.put("status.health.sick", "Health: 🔴 Sick (treatment needed)");

        ZH.put("status.pet.notice", "♡ 请注意，电子小猫需要定期喂食和饮水，以保持其健康和快乐。");
        EN.put("status.pet.notice", "♡ Please remember: your digital kitten needs regular food and water to stay healthy and happy.");

        ZH.put("status.prompt", "请输入选项：");
        EN.put("status.prompt", "Please enter an option: ");

        ZH.put("ascend.block.1", "猫咪仍在天堂修行中。");
        EN.put("ascend.block.1", "Your kitten is still training in heaven.");

        ZH.put("ascend.block.2", "请继续张贴寻猫启事，直到它回家。");
        EN.put("ascend.block.2", "Keep posting Missing Cat Notices until it comes home.");

        ZH.put("notice.posted.prefix", "你已经张贴了第 ");
        EN.put("notice.posted.prefix", "You have posted Missing Cat Notice day ");

        ZH.put("notice.posted.suffix", " 天的寻猫启事。");
        EN.put("notice.posted.suffix", ".");

        ZH.put("notice.return", "猫咪听见了你的呼唤，回到了人间。");
        EN.put("notice.return", "Your kitten heard your call and returned home.");

        ZH.put("notice.reward", "它带回了一只天使猫作为礼物。");
        EN.put("notice.reward", "It brought back 1 Angel Cat as a gift.");

        ZH.put("notice.remaining.prefix", "还需要再张贴 ");
        EN.put("notice.remaining.prefix", "You need to post ");

        ZH.put("notice.remaining.suffix", " 天寻猫启事。");
        EN.put("notice.remaining.suffix", " more day(s) of Missing Cat Notices.");

        ZH.put("angel.restore", "天使猫发动了力量，猫咪的状态已全部恢复。");
        EN.put("angel.restore", "The Angel Cat activated automatically! All stats were restored.");

        ZH.put("angel.none", "没有天使猫守护，猫咪无法恢复。");
        EN.put("angel.none", "No Angel Cat is available. Your kitten cannot be restored.");

        ZH.put("angel.restore.fail", "天使猫恢复失败：");
        EN.put("angel.restore.fail", "Angel Cat restore failed: ");
    }

    private static String t(String key) {
        Map<String, String> dict = (currentLang == Lang.EN) ? EN : ZH;
        return dict.getOrDefault(key, key);
    }
//双语
//    private static final boolean BILINGUAL_MODE = true;
//
//    private static String bi(String zh, String en) {
//        if (!BILINGUAL_MODE) {
//            return currentLang == Lang.EN ? en : zh;
//        }
//
//        if (currentLang == Lang.EN) {
//            return en + "\n" + zh;
//        } else {
//            return zh + "\n" + en;
//        }
//    }
//
//    private static void say(String zh, String en) {
//        System.out.println(bi(zh, en));
//    }
//
//    private static void ask(String zh, String en) {
//        System.out.print(bi(zh, en));
//    }

    private static void loadLanguage(Path savePath) {
        Path langPath = savePath.getParent().resolve("language.txt");

        try {
            if (!Files.exists(langPath)) {
                currentLang = Lang.ZH;
                Files.writeString(langPath, "zh", StandardCharsets.UTF_8);
                return;
            }

            String value = Files.readString(langPath, StandardCharsets.UTF_8).trim();
            currentLang = value.equalsIgnoreCase("en") ? Lang.EN : Lang.ZH;
        } catch (IOException e) {
            currentLang = Lang.ZH;
        }
    }

    private static void saveLanguage(Path savePath) {
        Path langPath = savePath.getParent().resolve("language.txt");

        try {
            Files.writeString(
                    langPath,
                    currentLang == Lang.EN ? "en" : "zh",
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            System.out.println(t("lang.save.fail") + e.getMessage());
        }
    }

    private static void showLanguageMenu(Scanner in, Path savePath) {
        System.out.println(t("lang.menu.title"));
        System.out.println(t("lang.menu.1"));
        System.out.println(t("lang.menu.2"));
        System.out.print(t("lang.choose"));

        String choice = in.nextLine().trim();

        if (choice.equals("1") || choice.equalsIgnoreCase("zh") || choice.equalsIgnoreCase("chinese")) {
            currentLang = Lang.ZH;
            saveLanguage(savePath);
            System.out.println(t("lang.changed.zh"));
        } else if (choice.equals("2") || choice.equalsIgnoreCase("en") || choice.equalsIgnoreCase("english")) {
            currentLang = Lang.EN;
            saveLanguage(savePath);
            System.out.println(t("lang.changed.en"));
        } else {
            System.out.println(t("lang.invalid"));
        }
    }

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

        // 先加载语言设置，确保初次运行提示也能按当前语言输出
        Path savePath = getSavePath();
        loadLanguage(savePath);

        // ================== 新增：初次运行自动生成数据文件 ==================
        // 确保 data 文件夹存在 (只需执行一次)
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }

        if (!Files.exists(condPath)) {
            Files.copy(resourceDir.resolve("cat_condition.txt"), condPath);
            System.out.println(t("file.init.cat"));
        }

        if (!Files.exists(goodsPath)) {
            Files.copy(resourceDir.resolve("goods_condition.txt"), goodsPath);
            System.out.println(t("file.init.goods"));
        }

        if (!Files.exists(idiomPath)) {
            Files.copy(resourceDir.resolve("idiom.txt"), idiomPath);
            System.out.println(t("file.init.idiom"));
        }
        // ===============================================================

        // 或者开发期临时用：Path savePath = dataDir.resolve("save.properties");

        var saveStore = new SaveStore(savePath);
        // var goodsPath = dataDir.resolve("goods_condition.txt");

        SaveStore.SaveState state = saveStore.read(); // 读取游戏进度
        // 彩蛋是否展示
        maybeShowKuromiEgg(saveStore, state);
        // Timekeeper timekeeper = new Timekeeper();

        // int days = timekeeper.syncDays(saveStore, state);
        int days = Timekeeper.syncDays(saveStore, state); // 改为静态调用

        // ===== 新增：起名指引 =====
        if (state.catName == null || state.catName.isEmpty()) {
            System.out.println(t("name.first.cat"));
            System.out.print(t("name.ask"));
            state.catName = in.nextLine().trim();
            if (state.catName.isEmpty()) {
                state.catName = t("name.default"); // 玩家直接敲回车的话，给个默认硬核名字
            }
            saveStore.write(state);
            System.out.println(t("name.success.prefix") + state.catName + t("name.success.suffix") + "\n");
        }
        // ==========================

        // ===== ⭐新增这行：无论如何，启动时把名字同步到TXT文件里 =====
        CatConditionFile.writeName(condPath, state.catName);
        // ==========================

        // 在 Main.java 的 main 方法天数同步后
        if (state.dayCount >= 30 && !state.isAscended) {
            state.isAscended = true; // 进入修行状态
            saveStore.write(state);
            System.out.println(t("ascend.achievement"));
            System.out.println(t("ascend.message"));

            // ===== 补上缺失的这行代码，发放飞升奖励 =====
            CatConditionFile.incAngelCount(condPath);
            System.out.println(t("ascend.reward"));
            // ==========================================

            System.out.println(t("ascend.notice"));
        }

        // int steps = Timekeeper.decayBy3Hours(saveStore, state, condPath);
        // if (steps > 0) System.out.println(t("decay.prefix") + steps + t("decay.suffix"));
    try {
        int steps = Timekeeper.decayBy3Hours(saveStore, state, condPath);
        if (steps > 0) {
            System.out.println(t("decay.prefix") + steps + t("decay.suffix"));
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
                    System.out.println(t("god.achievement"));
                }
                if (!GOD_MODE) {
                    System.out.println(t("death.beyond"));
                }
                System.out.println(t("angel.auto"));
            } else {
                System.out.println(e.getMessage());
                System.out.print(t("readopt.ask"));
                String ans = in.nextLine().trim();

                if (ans.equalsIgnoreCase("y") || ans.equalsIgnoreCase("yes")) {
                    resetForReAdopt(savePath, condPath);
                    state = saveStore.read();
                    System.out.println(t("readopt.success"));

                    // ===== 新增：重新领养后立刻起名 =====
                    System.out.print(t("readopt.name.ask"));
                    state.catName = in.nextLine().trim();
                    if (state.catName.isEmpty()) {
                        state.catName = t("name.default");
                    }
                    saveStore.write(state);
                    // ==================================

                    // ===== ⭐新增这行：无论如何，启动时把名字同步到TXT文件里 =====
                    CatConditionFile.writeName(condPath, state.catName);
                    // ==========================

                } else {
                    System.out.println(t("program.exit"));
                    return;
                }
            }
        } catch (IOException ex) {
            System.out.println(t("angel.reset.fail") + ex.getMessage());
            return;
        }

    }

        System.out.println(t("day.today.prefix") + (state.dayCount + 1) + t("day.today.suffix"));

        CatConditionFile.writeDay(condPath, state.dayCount);


            System.out.println("**************************************************");
            //System.out.println("                  欢迎光临宠物商店                   ");
            System.out.println(t("welcome.title"));
            System.out.println("**************************************************");
            System.out.println(t("welcome.line1"));
            System.out.println(t("welcome.line2"));
            System.out.println(t("welcome.line3"));
            System.out.println();
            System.out.println(t("help.tip"));
            System.out.println(t("lang.tip"));
            System.out.println(t("exit.tip"));
            System.out.println(t("save.path") + savePath.toAbsolutePath());
//            System.out.println("  这是一家只卖“文字的小猫”的小店。");
//            System.out.println("  在这里，你可以领养、抚摸、喂食、清洁、陪玩，");
//            System.out.println("  用每日几分钟，养大一只可爱的小猫。");
//            System.out.println();
//            System.out.println("  输入h 帮助/说明");
//            System.out.println("  输入exit 退出");
//            System.out.println("存档路径: " + savePath.toAbsolutePath());

        while (true) {
        try {
            //System.out.print("  请输入选项编号：");
            System.out.print(t("input.option"));
            String x = in.nextLine();
            if (x.equalsIgnoreCase("die")) {
                throw new CatConditionFile.CatDiedException(t("debug.cat.die"));
            }
            if (x.equalsIgnoreCase("l") || x.equalsIgnoreCase("lang") || x.equalsIgnoreCase("language")) {
                showLanguageMenu(in, savePath);
            }
            else if (x.equalsIgnoreCase("h")) {
                String helpFileName = (currentLang == Lang.EN) ? "how_to_play_en.txt" : "how_to_play.txt";
                Path helpPath = Paths.get(System.getProperty("user.dir"))
                .resolve("src/main/resources")
                .resolve(helpFileName)
                .normalize();
                System.out.println(t("help.path") + helpPath);

                try (BufferedReader br = Files.newBufferedReader(helpPath, StandardCharsets.UTF_8)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.out.println(t("help.read.fail") + e.getMessage());
                }
            } 
            else if (x.equalsIgnoreCase("exit")) {
                System.out.println(t("program.quit"));
                break;
            }

            else if (GOD_MODE && x.equalsIgnoreCase("debug_heaven_return")) {
                state.isAscended = false;
                state.postNoticeDays = 0;
                state.dayCount = 0;
                saveStore.write(state);

                CatConditionFile.incAngelCount(condPath);
                CatConditionFile.restoreAll(condPath);

                System.out.println(t("notice.return"));
                System.out.println(t("notice.reward"));
                continue;
            }

            else if (x.equalsIgnoreCase("z")) {

                // ================== 新增：修行拦截 ==================
                if (state.isAscended) {
                    System.out.println(t("ascend.block.1"));
                    System.out.println(t("ascend.block.2"));
                    continue; // 直接跳回主循环开头，不显示下面的动作菜单
                }
                // ====================================================

//            } else if (x.equalsIgnoreCase("debug_heaven")) {
//                // 调试：让猫咪直接进入天堂
//                state.isAscended = true;
//                state.postNoticeDays = 0;
//                state.dayCount = 30;
//                saveStore.write(state);
//
//                System.out.println("DEBUG: Cat has ascended to heaven.");
//                continue;
//
//            } else if (x.equalsIgnoreCase("debug_return")) {
//                // 调试：让猫咪立即从天堂回来
//                state.isAscended = false;
//                state.postNoticeDays = 0;
//                state.dayCount = 0;
//
//                saveStore.write(state);
//
//                // 回满状态，并奖励/恢复天使猫
//                CatConditionFile.incAngelCount(condPath);
//                CatConditionFile.restoreAll(condPath);
//
//                System.out.println("DEBUG: Cat has returned from heaven.");
//                continue;
//
//            } else if (x.equalsIgnoreCase("debug_heaven_return")) {
//                // 调试：一键完成“去天堂 -> 立即回来”
//                state.isAscended = false;
//                state.postNoticeDays = 0;
//                state.dayCount = 0;
//
//                saveStore.write(state);
//
//                CatConditionFile.incAngelCount(condPath);
//                CatConditionFile.restoreAll(condPath);
//
//                System.out.println("DEBUG: Heaven cycle completed. Cat returned immediately.");
//                continue;
//            }

                System.out.println(t("action.enter"));
                System.out.println(t("action.1"));
                System.out.println(t("action.2"));
                System.out.println(t("action.3"));
                System.out.println(t("action.4"));
                System.out.println(t("action.5"));
                System.out.println(t("action.6"));
                System.out.println(t("action.7"));
                System.out.println(t("action.8"));
                System.out.println(t("action.9"));
                System.out.println(t("action.10"));
                System.out.println(t("action.11"));
                System.out.println(t("action.12"));
                System.out.println(t("action.13"));
                System.out.println(t("action.0"));
                System.out.print(t("action.choose"));
                String action = in.nextLine();

                // ================== 全局负面状态拦截 ==================
                if (state.isSick) {
                    // 允许：治病(11)、挣钱(12)、购物(13)、退出(0)
                    if (!action.equals("11") &&
                            !action.equals("12") &&
                            !action.equals("13") &&
                            !action.equals("0") &&
                            !action.equalsIgnoreCase("exit")) {
                        System.out.println(t("sick.block.prefix") + state.catName + t("sick.block.suffix"));
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
                        System.out.println(t("poop.block.prefix") + state.catName + t("poop.block.suffix"));
                        System.out.println(t("poop.clean.prompt"));
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
                            System.out.println(t("food.empty"));
                        } else {
                        // 2. 物品数量成功减 1，再去加饥饿度
                        boolean ok = CatConditionFile.feed(condPath, 1, 6); // +1，最多 6 颗 ♥
                        if (ok) {
                            System.out.println(t("feed.catfood.ok"));
                            // ===== 新增：吃进去1点，垃圾累加1点 =====
                            state.poopCount += 1;
                            saveStore.write(state);
                        }
                        else {
                            System.out.println(t("cat.full"));
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
                                System.out.println(t("water.ok"));
                            } else {
                                System.out.println(t("water.full"));
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
                    case "0" -> System.out.println(t("back.main"));

                    // ===== 新增这三行：让 exit 直接退出程序 =====
                    case "exit" -> {
                        System.out.println(t("program.quit"));
                        System.exit(0); // 直接强行结束当前 Java 进程
                    }
                    // =======================================

                    // 比如作弊菜单里：
                    case "cheat" -> {  // 你自己选一个编号 / 关键字
                        doTraining(saveStore, state, condPath, in, true); // 作弊训练
                    }
                    //case "cheat" -> showCheatMenu(state); // 作弊菜单，方便测试
                    default -> System.out.println(t("action.invalid"));
                    }
            } else if (x.equalsIgnoreCase("x")) {
                System.out.println(t("status.enter"));
                 // 路径：src/main/data/cat_condition.txt
//            var catPath = java.nio.file.Paths.get(System.getProperty("user.dir"))
//                .resolve("../data/cat_condition.txt")
//                .normalize();
            // var goodsPath = java.nio.file.Paths.get(System.getProperty("user.dir"))
            //     .resolve("../data/goods_condition.txt")
            //     .normalize();

            try {
                System.out.println(t("status.pet.title"));
                if (state.isSick) System.out.println(t("status.health.sick"));
                else System.out.println(t("status.health.ok"));
                java.nio.file.Files.lines(condPath).forEach(System.out::println);

                System.out.println(t("status.goods.title"));
                java.nio.file.Files.lines(goodsPath).forEach(System.out::println);
            } catch (java.io.IOException e) {
                System.out.println(t("status.read.fail") + e.getMessage());
            }
            } else if (x.equalsIgnoreCase("c")) {
                System.out.println(t("readopt.available"));
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
                            System.out.println(t("notice.already.today"));
                            continue; // 阻断后续操作，直接重新循环
                        } else {
                            // 情况C：断连了，或者这是第一天贴 -> 天数重置为 1
                            state.postNoticeDays = 1;
                        }

                        // 更新张贴日期并存档
                        state.lastPostDate = today;
                        saveStore.write(state);
                        System.out.println(t("notice.posted.prefix") + state.postNoticeDays + t("notice.posted.suffix"));

                        // 2. 判定是否满 7 天
                        if (state.postNoticeDays >= 7) {
                            System.out.println(t("notice.return"));

                            // 重置状态
                            state.isAscended = false; // 猫咪回归人间
                            state.postNoticeDays = 0; // 清空启事天数
                            state.dayCount = 0;       // 重新开启下一次30天修行轮回
                            saveStore.write(state);

                            // 奖励：状态回满，外加送1只天使猫
                            CatConditionFile.incAngelCount(condPath);
                            CatConditionFile.restoreAll(condPath);

                            System.out.println(t("notice.reward"));
                        } else {
                            System.out.println(t("notice.remaining.prefix") + (7 - state.postNoticeDays) + t("notice.remaining.suffix"));
                        }
                    }
                    // ================== 原有：猫在人间时的“回血”逻辑 ==================
                    else {
                        if (GOD_MODE || CatConditionFile.hasAngel(condPath)) {
                            CatConditionFile.restoreAll(condPath);

                            if (!GOD_MODE) {
                                CatConditionFile.decAngelCount(condPath);
                            }

                            System.out.println(t("angel.restore"));
                        } else {
                            System.out.println(t("angel.none"));
                        }
                    }
                } catch (IOException e) {
                    System.out.println(t("angel.restore.fail") + e.getMessage());
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
                System.out.println(t("invalid.input"));
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
                    System.out.println(t("angel.auto"));
                } else {
                    System.out.println(e.getMessage());
                    System.out.print(t("readopt.ask"));
                    String ans = in.nextLine().trim();

                    if (ans.equalsIgnoreCase("y") || ans.equalsIgnoreCase("yes")) {
                        resetForReAdopt(savePath, condPath);
                        state = saveStore.read();
                        System.out.println(t("readopt.success"));

                        // ===== 新增：重新领养后立刻起名 =====
                        System.out.print(t("readopt.name.ask"));
                        state.catName = in.nextLine().trim();
                        if (state.catName.isEmpty()) {
                            state.catName = t("name.default");
                        }
                        saveStore.write(state);
                        // ==================================

                        // ===== ⭐新增这行：无论如何，启动时把名字同步到TXT文件里 =====
                        CatConditionFile.writeName(condPath, state.catName);
                        // ==========================

                    } else {
                        System.out.println(t("program.exit"));
                        break;
                    }
                }
            } catch (IOException ex) {
                System.out.println(t("angel.reset.fail") + ex.getMessage());
                break;
            }
        }
    }
        in.close();
    }

    // 库洛米彩蛋核心代码
    private static void maybeShowKuromiEgg(SaveStore saveStore, SaveStore.SaveState state) throws IOException {
        java.time.LocalDate today = java.time.LocalDate.now();

        // 每天最多触发一次
        if (state.lastKuromiEggDate != null && state.lastKuromiEggDate.equals(today.toString())) {
            return;
        }

        java.util.Random random = new java.util.Random();

        // 第一次触发概率 1/10；触发过以后，概率降低到 1/50
        int chance = state.kuromiEggUnlocked ? 50 : 10;
        //int chance = 1;

        if (random.nextInt(chance) != 0) {
            return;
        }

        System.out.println();
        System.out.println("【隐藏日志：名字修正】");
        System.out.println();
        System.out.println("夜深忽梦少年事，梦啼妆泪红阑干。");
        System.out.println();
        System.out.println("那时是高三，距离高考已经不远了。");
        System.out.println("教室、补习班、卷子、晚自习，几乎占满了全部时间。");
        System.out.println();
        System.out.println("你看见了一个角色，却不知道她叫什么。");
        System.out.println("有人说：“骷髅女。”");
        System.out.println();
        System.out.println("你差点就这样记住她。");
        System.out.println();
        System.out.println("后来，有人认真地写下了她真正的名字：");
        System.out.println();
        System.out.println("库洛米。");
        System.out.println();
        System.out.println("从此，一个模糊的图像拥有了名字。");
        System.out.println();

        state.kuromiEggUnlocked = true;
        state.lastKuromiEggDate = today.toString();
        saveStore.write(state);
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
                .resolve("src/main/resources/cat_condition.txt")
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