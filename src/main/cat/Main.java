package cat;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    // 作弊模式总开关：true = 开启开发者 / 作弊模式
    private static final boolean CHEAT_MODE = true;
    // 想关掉作弊的时候只要改成 false 就行
    // 在 Main 类里新加这个方法（在 main(...) 下面就行）
//     private static void doTraining(Scanner in,
//                                SaveStore saveStore,
//                                SaveStore.SaveState state,
//                                boolean cheatMode) throws IOException {

//     String word = pickRandomWord();
//     final long limitMs = 3000; // 正常模式的时限

//     if (cheatMode) {
//         System.out.println("【作弊训练】请输入任意内容（不限制时间、不限制对错）：");
//         System.out.print(">>> " + word + "\n");
//     } else {
//         System.out.println("训练开始！请在 " + (limitMs / 1000) + " 秒内准确输入下列单词：");
//         System.out.print(">>> " + word + "\n");
//     }

//     long start = System.currentTimeMillis();
//     String input = in.nextLine();
//     long cost = System.currentTimeMillis() - start;

//     boolean success;
//     if (cheatMode) {
//         // 作弊模式：永远算成功
//         success = true;
//     } else {
//         // 正常模式：要在时限内 & 输入正确
//         success = word.equals(input) && cost <= limitMs;
//     }

//     // ====== 更新状态 ======
//     // 训练次数消耗：正常模式扣一次，作弊模式看你喜好：
//     if (!cheatMode) {
//         state.trainTickets--;
//     } else {
//         // 作弊模式可以不扣，也可以扣，随你
//         // 这里假设也扣，这样喂鱼拿 ticket 仍然有意义
//         state.trainTickets--;
//     }

//     if (success) {
//         //state.TrainLevel++; // 你自己的训练度变量名
//         CatConditionFile.incTrainLevel(condPath, 1, 999);
//         if (cheatMode) {
//             System.out.println("【作弊模式】无论输入什么都算成功，训练度 +1。 (用时 " + cost + " ms)");
//         } else {
//             System.out.println("太棒了，老鼠被抓到了！训练度 +1。 (用时 " + cost + " ms)");
//         }
//     } else {
//         // 正常模式：失败提示
//         System.out.println("可惜，没抓到老鼠。 (用时 " + cost + " ms)");
//     }

//     saveStore.write(state);
// }

// private static void doTraining(
//         java.util.Scanner in,
//         SaveStore saveStore,
//         SaveStore.SaveState state,
//         java.nio.file.Path condPath
// ) throws java.io.IOException {

//     // 1. 先检查有没有训练机会
//     if (state.trainTickets <= 0) {
//         System.out.println("今天的训练机会已经用完了，可以喂鱼来获得新的训练次数。");
//         return;
//     }

//     // 2. 随机挑一个单词，当作“老鼠”
//     String[] words = { "mouse", "catnip", "shadow", "cheese", "whisker" };
//     java.util.Random random = new java.util.Random();
//     String target = words[random.nextInt(words.length)];

//     System.out.println("训练开始！请在 3 秒内准确输入下列单词：");
//     System.out.println(">>> " + target);
//     System.out.print("你输入：");

//     long start = System.currentTimeMillis();
//     String input = in.nextLine();          // 等你敲完回车
//     long elapsed = System.currentTimeMillis() - start;

//     // 不论成功失败，都会消耗一次训练机会
//     state.trainTickets--;

//     boolean ok = false;
//     if (elapsed <= 3000 && target.equals(input.trim())) {
//         ok = true;
//     }

//     if (ok) {
//         System.out.println("太棒了，你成功抓到了老鼠！训练度 +1。");
//         // 修改 cat_condition.txt 里的 “训练度： …”
//         CatConditionFile.incTrainLevel(condPath, 1, 999);
//     } else if (elapsed > 3000) {
//         System.out.println("太慢了，老鼠跑掉了……(用时 " + elapsed + " ms)");
//     } else {
//         System.out.println("打错字了，老鼠溜走了……");
//     }

//     // 3. 记得把新的 trainTickets 写回存档
//     saveStore.write(state);
// }

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);

        // SaveStore saveStore = new SaveStore(Paths.get("savefile.properties"));
        // 以当前工作目录（out/）为基准，回到上一级，指向 ../data/save.properties
        var savePath = java.nio.file.Paths.get(System.getProperty("user.dir"))
                 .resolve("../data/save.properties")
                 .normalize();
        var saveStore = new SaveStore(savePath);
        // var goodsPath = dataDir.resolve("goods_condition.txt");

        SaveStore.SaveState state = saveStore.read(); // 读取游戏进度
        // Timekeeper timekeeper = new Timekeeper();

        // int days = timekeeper.syncDays(saveStore, state);
        int days = Timekeeper.syncDays(saveStore, state); // 改为静态调用
        var dataDir  = java.nio.file.Paths.get(System.getProperty("user.dir"))
                  .resolve("../data").normalize();
        var condPath = dataDir.resolve("cat_condition.txt");
        var goodsPath = dataDir.resolve("goods_condition.txt");

        int steps = Timekeeper.decayBy3Hours(saveStore, state, condPath);
        if (steps > 0) System.out.println("已按 3 小时衰减了 " + steps + " 次");


        // if (days > 0) {
        //     System.out.println("游戏已推进 " + days + " 天");
        // }
        System.out.println("今天是你和猫咪在一起的第 " + (state.dayCount + 1) + " 天。");

        // 把“天数: N”写回到 src/main/data/cat_condition.txt
        // var condPath = java.nio.file.Paths.get(System.getProperty("user.dir"))
            // .resolve("../data/cat_condition.txt")
            // .normalize();

       // 若你担心文件被删掉，可先解开这一行：把 resources 里的模板拷到 data（若已存在则跳过）
       // CatConditionFile.ensureWorkCopy(condPath, "/cat_condition.txt");

        CatConditionFile.writeDay(condPath, state.dayCount);


            System.out.println("**************************************************");
            System.out.println("**                欢迎光临宠物商店              **");
            System.out.println("**************************************************");
            System.out.println("  这是一家只卖“文字的小猫”的小店。");
            System.out.println("  在这里，你可以领养、抚摸、喂食、清洁、陪玩，");
            System.out.println("  用每日几分钟，养大一只可爱的小猫。");
            System.out.println();
            System.out.println("  输入h 帮助/说明");
            System.out.println("  输入exit 退出");
            System.out.println("存档路径: " + savePath.toAbsolutePath());

        while (true) 
        {
            System.out.print("  请输入选项编号：");
            String x = in.nextLine();
            if (x.equalsIgnoreCase("h")) {
                // 从 resources 里读取
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                Main.class.getResourceAsStream("/how_to_play.txt"), "UTF-8"))) {
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
                } else if (!fed && drank) {
                    System.out.println("猫已经不饿了，但口渴得到了缓解 +1。");
                } else if (fed && !drank) {
                    System.out.println("猫已经不渴了，但饥饿度得到 +1。");
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
    //         case "4" -> {
    //         // 喂鱼：每次都会消耗一条鱼，但“训练机会”一天只增加一次
    //         try {
    //         // 1. 先扣掉一条鱼
    //         boolean used = CatConditionFile.useItem(goodsPath, "鱼");
    //         if (!used) {
    //             System.out.println("你想给猫喂鱼，但物品栏里已经没有鱼了。");
    //             break;  // 没鱼就不要再处理训练机会了
    //         }

    //         // 2. 读取当前存档状态
    //         // SaveStore.SaveState state = saveStore.read();
    //         state = saveStore.read();   // 不再写类型，只是给已经存在的 state 重新赋值

    //         // 3. 判断今天有没有通过鱼增加过训练机会
    //         if (state.lastFishDay == state.dayCount) {
    //             // 今天已经用过鱼拿训练机会了，这次只是浪费一条鱼
    //             System.out.println("你又给猫喂了一条鱼，但今天的训练机会已经拿过了。");
    //         } else {
    //             // 今天第一次喂鱼：增加一次训练机会
    //             state.trainTickets += 1;
    //             state.lastFishDay = state.dayCount;
    //             // saveStore.save(state);
    //             saveStore.write(state);
    //             System.out.println("你给猫喂了一条鱼，获得了一次训练机会！（今天不能再通过喂鱼获取训练机会）");
    //         }

    //     } catch (java.io.IOException e) {
    //         System.out.println("喂鱼时发生错误：" + e.getMessage());
    //     }
    // }


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

                // var catPath = baseDir
                //         .resolve("src/main/data/cat_condition.txt")
                //         .normalize();

                // var goodsPath = baseDir
                //         .resolve("src/main/data/goods_condition.txt")
                //         .normalize();

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
                        System.out.println("你帮猫洗了个澡。（清洁度 +24 个星号）");

                    try {
                        boolean ok = CatConditionFile.wash(condPath, 24, 48);  // delta = 24, max = 48
                        if (!ok) {
                            System.out.println("猫已经非常干净了，清洁度没有再增加。\n");
                        }
                    } catch (java.io.IOException e) {
                        System.out.println("洗澡时发生错误：" + e.getMessage());
                    }
                }
                // {
                //     System.out.println("你给猫咪洗了个澡。");

                //     // 计算 cat_condition.txt 的路径
                //     var baseDir = java.nio.file.Paths.get(System.getProperty("user.dir"))
                //             .normalize();
                //     // var condPath = baseDir
                //     //         .resolve("src/main/data/cat_condition.txt")
                //     //         .normalize();

                //     try {
                //         // 每次洗澡清洁度 +24 个 *，最大 24 个
                //         boolean ok = CatConditionFile.wash(condPath, 24, 24);
                //         if (ok) {
                //             System.out.println("猫咪变得香香的，清洁度大幅提升。");
                //         } else {
                //             System.out.println("猫看起来已经很干净了，清洁度没有再增加。");
                //         }
                //     } catch (java.io.IOException e) {
                //         System.out.println("给猫洗澡时发生错误: " + e.getMessage());
                //     }
                // }
                //System.out.println("你给猫洗了个澡。（尚未实现数值变化）");
                // case "9" -> 
                // {
                //     doTraining(in, saveStore, state, condPath);
                // }
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
                case "10" -> System.out.println("你打扫了猫的生活环境。（尚未实现数值变化）");
                case "11" -> System.out.println("你给猫看了兽医，猫咪恢复了健康。（尚未实现数值变化）");
                case "12" -> System.out.println("你回答了一些题，赚了一些钱。（尚未实现数值变化）");
                case "13" -> System.out.println("你去了商店，买了一些物品。（尚未实现数值变化）");
                case "0" -> System.out.println("返回主菜单。");
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
            var catPath = java.nio.file.Paths.get(System.getProperty("user.dir"))
                .resolve("../data/cat_condition.txt")
                .normalize();
            // var goodsPath = java.nio.file.Paths.get(System.getProperty("user.dir"))
            //     .resolve("../data/goods_condition.txt")
            //     .normalize();

            try {
                System.out.println("===== 宠物状态 =====");
                java.nio.file.Files.lines(catPath).forEach(System.out::println);

                System.out.println("===== 物品状态 =====");
                java.nio.file.Files.lines(goodsPath).forEach(System.out::println);
            } catch (java.io.IOException e) {
                System.out.println("读取状态文件时出错: " + e.getMessage());
            }
            } else if (x.equalsIgnoreCase("c")) {
                System.out.println("你可重新领养宠物。");
            } else if (x.equalsIgnoreCase("v")) {
                System.out.println("你可触发天使猫，恢复所有点数到最大值，如果猫咪出走，按此键可张贴寻猫启事，连续张贴7天猫咪回家。");
                System.out.println("你使用了天使猫技能，正在恢复所有状态……");
                try {
                CatConditionFile.restoreAll(condPath);
                System.out.println("所有状态已恢复到最大值！");
                } catch (IOException e) {
                System.out.println("恢复状态时出错：" + e.getMessage());
                }
            } else {
                System.out.println("无效输入，请重新输入。");
            }
            System.out.println();
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
            System.out.println("请在 3 秒内输入下面这个单词：");
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
            success = input.equals(target) && costMs <= 3000;
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
}