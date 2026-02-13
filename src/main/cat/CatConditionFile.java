package cat;

// CatConditionFile.java
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.regex.*;

public final class CatConditionFile {
    private static final Pattern DAY_LINE =
        Pattern.compile("^\\s*天数[:：]\\s*(\\d+).*?$");

    /** 将文件中“天数: N”改为新值；若不存在该行则追加一行 */
    public static void writeDay(Path file, int day) throws IOException {
        List<String> lines = Files.exists(file)
            ? Files.readAllLines(file, StandardCharsets.UTF_8)
            : new java.util.ArrayList<>();

        boolean replaced = false;
        for (int i = 0; i < lines.size(); i++) {
            Matcher m = DAY_LINE.matcher(lines.get(i));
            if (m.find()) {
                lines.set(i, "天数: " + day);
                replaced = true;
                break;
            }
        }
        if (!replaced) lines.add("天数: " + day);

        Files.createDirectories(file.getParent());
        Files.write(file, lines, StandardCharsets.UTF_8,
                   StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /** 若工作副本不存在，从资源复制一份出来（资源路径示例：/cat_condition.txt） */
    public static void ensureWorkCopy(Path workFile, String resourcePath) throws IOException {
        if (Files.exists(workFile)) return;
        try (var in = CatConditionFile.class.getResourceAsStream(resourcePath)) {
            if (in == null) throw new IOException("资源不存在: " + resourcePath);
            Files.createDirectories(workFile.getParent());
            Files.copy(in, workFile);
        }
    }
    //     /** 按照 steps(每3小时为一步) 衰减属性 */
    // public static void degrade(Path condPath, int steps) throws IOException {
    //     if (steps <= 0) return;

    //     // 先把文件内容读进来
    //     List<String> lines = Files.readAllLines(condPath, StandardCharsets.UTF_8);

    //     // 这里只是最简单的实现：追加一行提示
    //     lines.add("已衰减 " + steps + " 步 (每步=3小时)");

    //     // 写回文件
    //     Files.write(condPath, lines, StandardCharsets.UTF_8,
    //                 StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    // }
    // 按 steps(每步=3小时) 衰减：饥饿/口渴/亲密/兴奋 各 -steps 个 ♥；清洁度 -steps*6 个 *
public static void degrade(java.nio.file.Path file, int steps) throws java.io.IOException {
    if (steps <= 0) return;

    java.util.List<String> lines = java.nio.file.Files.readAllLines(file, java.nio.charset.StandardCharsets.UTF_8);
    for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);

        line = decHearts(line, "饥饿度：", steps, 6);
        line = decHearts(line, "口渴度：", steps, 4);
        line = decHearts(line, "亲密度：", steps, 4);
        line = decHearts(line, "兴奋度：", steps, 4);

        // if (line.stripLeading().startsWith("清洁度")) {
        // line = decAster(line, steps * 6);
        // }
        if (line.stripLeading().startsWith("清洁度")) {
            line = decAster(line, steps * 6);
        }

        lines.set(i, line);
    }
    java.nio.file.Files.write(file, lines, java.nio.charset.StandardCharsets.UTF_8);
}

// 兼容三种格式：
// 1) 饥饿度：♥♥♥   (3/6)
// 2) 亲密度： (♥♥♥♥)   (4/4)
// 3) 允许前后空格/♥︎（带变体）等
private static String decHearts(String line, String label, int step, int max) {
    if (!line.contains(label)) return line;

    // 找第一个括号与其右括号
    int p1 = line.indexOf('(');
    int p2 = (p1 >= 0) ? line.indexOf(')', p1 + 1) : -1;

    // 标签结束位置
    int afterLabel = line.indexOf(label) + label.length();

    // 标签后到括号前的区域（饥饿/口渴用）
    String zoneBefore = (p1 >= 0 ? line.substring(afterLabel, p1) : line.substring(afterLabel));
    // 第一个括号内的区域（亲密/兴奋用）
    String zoneInParen = (p1 >= 0 && p2 > p1) ? line.substring(p1 + 1, p2) : "";

    int heartsBefore = countHeart(zoneBefore);
    int heartsInParen = countHeart(zoneInParen);
    boolean useParen = (heartsBefore == 0 && heartsInParen > 0);

    int current = useParen ? heartsInParen : heartsBefore;
    // int next = Math.max(0, current - step);
    // ☆ 关键改动：不再 Math.max，而是先算出 next，若 <0 直接游戏结束
    int next = current - step;
    if (next < 0) {
        gameOverByLabel(label); // 见“新增方法”小节
        // System.exit 已退出；下面 return 只是语法需要
        return line;
    }

    String newHearts = "♥".repeat(next);
    // String countText = " (" + next + "/" + max + ")";

    if (useParen && p1 >= 0 && p2 > p1) {
        // 爱心在第一个括号里（亲密/兴奋）
        String prefix = line.substring(0, p1 + 1);
        String suffix = line.substring(p2 + 1); // 保留后面的排版
        return prefix + newHearts + ")" + suffix;
    } else {
        // 爱心在标签后（饥饿/口渴）
        String after = (p1 >= 0 && p2 > p1) ? line.substring(p2 + 1) : "";
        return line.substring(0, afterLabel) + newHearts + after;
    }
}


// —— 辅助：清洁度星号递减 —— 
private static String decAster(String line, int k) {
    int idx = line.indexOf('：');
    if (idx < 0) idx = line.indexOf(':');
    if (idx < 0) return line;

    String prefix = line.substring(0, idx + 1);  // 含“：”
    String stars = line.substring(idx + 1).trim();

    long have = stars.chars().filter(ch -> ch == '*').count();
    long next = have - k;
    if (next < 0) {
        // 清洁度为负 → 感染病菌
        System.out.println("猫咪感染病菌！游戏结束。");
        System.exit(0);
        return line;
    }
    return prefix + "*".repeat((int) next);
}

// —— 辅助：清洁度星号递增 ——
// 根据这一行里当前有多少个 *，+delta，最多不超过 max，返回新的一行
private static String incAster(String oldLine, int delta, int max) {
    // 找到第一段连续的星号，把它当成清洁度条
    int current = (int)oldLine.chars().filter(ch -> ch == '*').count();

    int next = current + delta;
    if (next > max) {
        next = max;     // 不超过上限
    }
    if (next < 0) {
        next = 0;       // 理论上用不到，只是防御
    }

    // 没变化就直接返回原行（例如已经是满 48 个）
    if (next == current) {
        return oldLine;
    }

    String stars = "*".repeat(next);

    // 用第一段连续的星号替换成新的星号串
    // "\\*+" 的意思是“至少一个 *”
    return oldLine.replaceFirst("\\*+", stars);
}

// 统计 ♥ 个数（兼容 “♥︎” 带变体选择符：先去掉变体符号）
private static int countHeart(String s) {
    String cleaned = s.replace("︎", ""); // 去掉 VARIATION SELECTOR-16
    int c = 0;
    for (int i = 0; i < cleaned.length(); i++) if (cleaned.charAt(i) == '♥') c++;
    return c;
}

private static void gameOverByLabel(String label) {
    String msg;
    switch (label) {
        case "饥饿度：" -> msg = "猫咪饿死了！游戏结束。";
        case "口渴度：" -> msg = "猫咪渴死了！游戏结束。";
        case "亲密度：" -> msg = "猫咪离家出走了！游戏结束。";
        case "兴奋度：" -> msg = "猫咪抑郁生病了！游戏结束。";
        case "清洁度：" -> msg = "猫咪感染病菌！游戏结束。";
        default -> msg = "猫咪状态恶化！游戏结束。";
    }
    System.out.println(msg);
    System.exit(0);
}

public static void restoreAll(Path file) throws IOException {
    List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
    for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);

        if (line.contains("饥饿度")) {
            line = "饥饿度： ♥♥♥♥♥♥   (6/6)";
        } else if (line.contains("口渴度")) {
            line = "口渴度： ♥♥♥♥   (4/4)";
        } else if (line.contains("亲密度")) {
            line = "亲密度： (♥♥♥♥)   (4/4)";
        } else if (line.contains("兴奋度")) {
            line = "兴奋度： (♥♥♥♥)   (4/4)";
        } else if (line.stripLeading().startsWith("清洁度")) {
            line = "清洁度： ************************************************"; // 随便给一串足够多的 *
        }
        lines.set(i, line);
    }
    Files.write(file, lines, StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING);
}


    /** 通用喂食：饥饿度增加 delta（正数），最多 maxHearts
     *  @return  真：饥饿度发生了变化；假：已经满了，没有变化
     */
    public static boolean feed(java.nio.file.Path file, int delta, int maxHearts)
            throws java.io.IOException {

        java.util.List<String> lines = java.nio.file.Files.readAllLines(
                file,
                java.nio.charset.StandardCharsets.UTF_8
        );

        boolean changed = false;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains("饥饿度")) {
                String newLine = incHungerLine(line, delta, maxHearts);
                if (!newLine.equals(line)) {   // 真的有变化才算
                    lines.set(i, newLine);
                    changed = true;
                }
                break;
            }
        }

        if (changed) {
            java.nio.file.Files.write(
                    file,
                    lines,
                    java.nio.charset.StandardCharsets.UTF_8,
                    java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
            );
        }

        return changed;
    }

    /** 口渴度增加 delta（整数），最多 maxHearts */
    /** 通用喝水：口渴度增加 delta（正数），最多 maxHearts
 *  @return  true: 口渴度发生了变化；false: 已经满了，没有变化
 */
public static boolean drink(java.nio.file.Path file, int delta, int maxHearts)
        throws java.io.IOException {

    java.util.List<String> lines = java.nio.file.Files.readAllLines(
            file,
            java.nio.charset.StandardCharsets.UTF_8
    );

    boolean changed = false;

    for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        if (line.contains("口渴度")) {       // 只改“口渴度”这一行
            String newLine = incThirstLine(line, delta, maxHearts);
            if (!newLine.equals(line)) {   // 真的有变化才算
                lines.set(i, newLine);
                changed = true;
            }
            break; // 找到就退出循环
        }
    }

    if (changed) {
        java.nio.file.Files.write(
                file,
                lines,
                java.nio.charset.StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    return changed;
}

/**
     * 玩耍：兴奋度增加 delta（正数），最多 maxHearts
     * @return 真：兴奋度发生了变化；假：已经满了，没有变化
     */
    public static boolean play(java.nio.file.Path file, int delta, int maxHearts)
            throws java.io.IOException {

        java.util.List<String> lines = java.nio.file.Files.readAllLines(
                file,
                java.nio.charset.StandardCharsets.UTF_8
        );

        boolean changed = false;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains("兴奋度")) {           // 找到“兴奋度”这一行
                String newLine = incExciteLine(line, delta, maxHearts);
                if (!newLine.equals(line)) {        // 真的有变化才写回
                    lines.set(i, newLine);
                    changed = true;
                }
                break;
            }
        }

        if (changed) {
            java.nio.file.Files.write(
                    file,
                    lines,
                    java.nio.charset.StandardCharsets.UTF_8,
                    java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
            );
        }

        return changed;
    }

        /**
     * 洗澡：清洁度增加 delta（正数），最多 maxStars。
     *
     * @return true  清洁度有变化；
     *         false 清洁度已经是最大值，没有变化。
     */
    public static boolean wash(java.nio.file.Path file, int delta, int maxStars)
            throws java.io.IOException {

        java.util.List<String> lines = java.nio.file.Files.readAllLines(
                file,
                java.nio.charset.StandardCharsets.UTF_8
        );

        boolean changed = false;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            // 只处理“清洁度”这一行
            if (line.contains("清洁度")) {
                String newLine = incAster(line, delta, maxStars);
                if (!newLine.equals(line)) {
                    lines.set(i, newLine);
                    changed = true;
                }
                break;  // 找到了就可以退出循环
            }
        }

        if (changed) {
            java.nio.file.Files.write(
                    file,
                    lines,
                    java.nio.charset.StandardCharsets.UTF_8,
                    java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
            );
        }

        return changed;
    }

    // 增加训练度：根据 “训练度：数字” 这一行，把数字加 delta（不超过 max）
public static void incTrainLevel(java.nio.file.Path file, int delta, int max)
        throws java.io.IOException {

    java.util.List<String> lines = java.nio.file.Files.readAllLines(
            file,
            java.nio.charset.StandardCharsets.UTF_8
    );

    for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        if (line.contains("训练度")) {
            // 找到这一行，比如 “训练度： 0”
            int idx = line.indexOf('：');      // 注意是全角冒号
            if (idx < 0) {
                // 找不到全角冒号就不改了
                return;
            }

            String prefix = line.substring(0, idx + 1); // “训练度：”
            String rest = line.substring(idx + 1).trim(); // 后面的数字部分

            int current = 0;
            if (!rest.isEmpty()) {
                try {
                    current = Integer.parseInt(rest);
                } catch (NumberFormatException e) {
                    // 如果不是数字，就当 0 处理
                    current = 0;
                }
            }

            int next = current + delta;
            if (next < 0) next = 0;
            if (next > max) next = max;

            // 重新拼回这一行，比如 “训练度： 5”
            lines.set(i, prefix + " " + next);

            java.nio.file.Files.write(
                    file,
                    lines,
                    java.nio.charset.StandardCharsets.UTF_8,
                    java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
            );
            return;
        }
    }
}

    //     /** 洗澡：清洁度增加 delta 个 *，最多 maxStars 个 */
    // public static boolean wash(java.nio.file.Path file, int delta, int maxStars)
    //         throws java.io.IOException {

    //     java.util.List<String> lines = java.nio.file.Files.readAllLines(
    //             file,
    //             java.nio.charset.StandardCharsets.UTF_8
    //     );

    //     boolean changed = false;

    //     for (int i = 0; i < lines.size(); i++) {
    //         String line = lines.get(i);

    //         // 找到“清洁度”这一行
    //         if (line.contains("清洁度")) {
    //             String newLine = incCleanLine(line, delta, maxStars);
    //             if (!newLine.equals(line)) {
    //                 lines.set(i, newLine);
    //                 changed = true;
    //             }
    //             break; // 找到并处理完就可以退出了
    //         }
    //     }

    //     if (changed) {
    //         java.nio.file.Files.write(
    //                 file,
    //                 lines,
    //                 java.nio.charset.StandardCharsets.UTF_8,
    //                 java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
    //         );
    //     }

    //     return changed;
    // }


    /** 内部工具：根据当前行，计算 ♥ 数量，+delta（不超过 max），重新生成一行（兴奋度） */
    private static String incExciteLine(String oldLine, int delta, int max) {
        int current = countHeart(oldLine);
        int next = current + delta;
        if (next > max) next = max;
        if (next < 0) next = 0;

        // 如果没有变化就直接返回原行
        if (next == current) {
            return oldLine;
        }

        String hearts = "♥".repeat(next);

        // 这里格式照着 cat_condition.txt 里“兴奋度”那一行来
        // 例如：兴奋度： ♥♥  (2/4)
        return "兴奋度： " + hearts + "  (" + next + "/" + max + ")";
    }
// 通用爱抚：亲密度增加 delta（正数），最多 maxHearts
// @return  true: 亲密度发生了变化；false: 已经满了，没有变化
public static boolean pet(java.nio.file.Path file, int delta, int maxHearts) throws java.io.IOException {
    java.util.List<String> lines = java.nio.file.Files.readAllLines(
            file,
            java.nio.charset.StandardCharsets.UTF_8
    );

    boolean changed = false;

    for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        // 找到那一行“亲密度”
        if (line.contains("亲密度")) {
            int current = countHeart(line);       // 现在有几颗 ♥
            int next = current + delta;           // 增加 delta
            if (next > maxHearts) next = maxHearts;
            if (next < 0) next = 0;               // 虽然现在不用减，但防一下

            if (next != current) {
                // 重新拼一行：注意格式要跟 cat_condition.txt 一致
                String hearts = "♥".repeat(next);
                String newLine = "亲密度: " + hearts + "   (" + next + "/" + maxHearts + ")";
                lines.set(i, newLine);
                changed = true;
            }
            break;   // 找到了就可以退出循环
        }
    }

    if (changed) {
        java.nio.file.Files.write(
                file,
                lines,
                java.nio.charset.StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING,
                java.nio.file.StandardOpenOption.CREATE
        );
    }

    return changed;
}

/** 工具：根据当前行，计算 ♥ 数量，+delta（不超过 max），重新生成这一行 */
private static String incThirstLine(String oldLine, int delta, int max) {
    int current = countHeart(oldLine);
    int next = current + delta;
    if (next > max) next = max;
    if (next < 0) next = 0;

    if (next == current) {
        return oldLine; // 没变化就原样返回
    }

    String hearts = "♥".repeat(next);
    // 按你 cat_condition.txt 的格式自己调一下：
    // 示例： "口渴度: " + hearts + "    (4/4)"
    return "口渴度: " + hearts + "    (" + next + "/" + max + ")";
}

// public static boolean drink(java.nio.file.Path file, int delta, int maxHearts)
//         throws java.io.IOException {

//     java.util.List<String> lines = java.nio.file.Files.readAllLines(
//             file,
//             java.nio.charset.StandardCharsets.UTF_8
//     );

//     boolean changed = false;

//     for (int i = 0; i < lines.size(); i++) {
//         String line = lines.get(i);
//         if (line.contains("口渴度")) {          // 找到“口渴度”那一行
//             String newLine = incHeartLine(line, delta, maxHearts);
//             if (!newLine.equals(line)) {       // 只有真的变了才写回
//                 lines.set(i, newLine);
//                 changed = true;
//             }
//             break; // 找到后就退出循环
//         }
//     }

//     if (changed) {
//         java.nio.file.Files.write(
//                 file,
//                 lines,
//                 java.nio.charset.StandardCharsets.UTF_8,
//                 java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
//         );
//     }

//     return changed;   // true 表示有变化；false 表示已经满了
// }

    /** 内部工具：根据当前行，计算 ♥ 数量，+delta（不超过 max），重新生成这一行 */
    private static String incHungerLine(String oldLine, int delta, int max) {
        int current = countHeart(oldLine);   // 这里用你原来的 countHeart(String s)
        int next = current + delta;
        if (next > max) next = max;
        if (next < 0) next = 0;              // 以后想写扣饥饿度也不至于变负

        // 如果没有变化就直接返回原行
        if (next == current) {
            return oldLine;
        }

        String hearts = "♥".repeat(next);

        // 根据 cat_condition.txt 的格式拼回去
        return "饥饿度： " + hearts + "   (" + next + "/" + max + ")";
    }
    
    /** 根据当前行的 ♥ 数，增加 delta（不超过 max），返回新的行。如果没变化就返回原行 */
    private static String incHeartLine(String oldLine, int delta, int max) {
        int current = countHeart(oldLine);      // 用你原来的 countHeart(String s)

        int next = current + delta;
        if (next > max) next = max;
        if (next < 0)   next = 0;

        // 没有变化就直接返回原来的行，避免白写文件
        if (next == current) return oldLine;

        String hearts = "♥".repeat(next);

        // oldLine 是类似：
        // 口渴度： ♥♥♥♥  (4/4)
        // 饥饿度： ♥♥♥♥♥♥  (6/6)
        // 你的 cat_condition.txt 已经把“标签 + ：”写死了，
        // 所以我们只要把“标签：”保留，用后面的 ♥ 和 (a/b) 重建即可。
        int pos = oldLine.indexOf("：");
        String label = pos >= 0 ? oldLine.substring(0, pos + 1) : "口渴度：";

        return label + " " + hearts + "   (" + next + "/" + max + ")";
    }

        /** 内部工具：根据当前行，计算 * 数量，+delta（不超过 max），重新生成这一行 */
    private static String incCleanLine(String oldLine, int delta, int max) {
        // 先找到“清洁度：”后面星号开始的位置
        int idx = oldLine.indexOf('：');
        if (idx < 0) {
            idx = oldLine.indexOf(':');
        }
        if (idx < 0) {
            // 格式异常，保守起见原样返回
            return oldLine;
        }

        String prefix = oldLine.substring(0, idx + 1); // 包含“清洁度：”
        String rest = oldLine.substring(idx + 1);      // 后面这一串

        // 统计前面连续的 * 数量
        int current = 0;
        int pos = 0;
        while (pos < rest.length() && rest.charAt(pos) == '*') {
            current++;
            pos++;
        }

        int next = current + delta;
        if (next > max) next = max;
        if (next == current) {
            // 没有变化，直接返回原行
            return oldLine;
        }

        String suffix = rest.substring(pos); // 星号后面的说明文字/空格

        String stars = "*".repeat(next);
        return prefix + stars + suffix;
    }


    /** 使用一个物品：在 goods_condition.txt 里把对应物品数量减 1
     *  行格式示例： "1. 猫粮 * 7 （恢复一点饥饿值）"
     *  @param goodsFile  物品状态文件路径（data 目录那份）
     *  @param name       要匹配的物品名，比如 "猫粮"、"超级猫粮"
     *  @return           真：成功扣减（原来数量 > 0）；假：没扣成（没找到或已经是 0）
     */
    public static boolean useItem(java.nio.file.Path goodsFile, String name)
            throws java.io.IOException {

        java.util.List<String> lines = java.nio.file.Files.readAllLines(
                goodsFile,
                java.nio.charset.StandardCharsets.UTF_8
        );

        boolean changed = false;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains(name)) {
                String newLine = decItemCount(line);
                if (!newLine.equals(line)) {   // 数量真的变了
                    lines.set(i, newLine);
                    changed = true;
                }
                break;
            }
        }

        if (changed) {
            java.nio.file.Files.write(
                    goodsFile,
                    lines,
                    java.nio.charset.StandardCharsets.UTF_8,
                    java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
            );
        }

        return changed;
    }

    /** 把一行里的数量 N 减 1：
     *  例如 "... * 7 （说明）"  ->  "... * 6 （说明）"
     */
    private static String decItemCount(String line) {
        int starIndex = line.indexOf('*');
        if (starIndex < 0) return line;  // 这一行没有 "*"，直接不管

        int i = starIndex + 1;
        // 跳过 "*" 后面的一些空格
        while (i < line.length() && line.charAt(i) == ' ') i++;

        int startNum = i;
        while (i < line.length() && Character.isDigit(line.charAt(i))) i++;
        int endNum = i;

        if (startNum >= endNum) return line;  // 没有数字

        String numStr = line.substring(startNum, endNum);
        int value;
        try {
            value = Integer.parseInt(numStr);
        } catch (NumberFormatException e) {
            return line;                      // 解析失败就放弃
        }

        if (value <= 0) {
            return line;                      // 已经是 0 了，不再扣
        }

        int next = value - 1;
        return line.substring(0, startNum) + next + line.substring(endNum);
    }

    /** 通用喝水：口渴度增加 delta（正数），最多 maxHearts
    *  @return 真：口渴度发生了变化；假：已经满了，没有变化
    */
    // public static boolean drink(java.nio.file.Path file, int delta, int maxHearts)
    //     throws java.io.IOException {

    //     java.util.List<String> lines = java.nio.file.Files.readAllLines(
    //         file,
    //         java.nio.charset.StandardCharsets.UTF_8
    //     );

    //     boolean changed = false;

    //     for (int i = 0; i < lines.size(); i++) {
    //         String line = lines.get(i);
    //         if (line.contains("口渴度")) {
    //             String newLine = incHungerLine(line, delta, maxHearts); // 重用 incHungerLine 逻辑
    //             if (!newLine.equals(line)) {
    //                 lines.set(i, newLine);
    //                 changed = true;
    //             }
    //             break;
    //         }
    //     }

    //     if (changed) {
    //         java.nio.file.Files.write(
    //             file,
    //             lines,
    //             java.nio.charset.StandardCharsets.UTF_8,
    //             java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
    //         );
    //     }

    //     return changed;
    // }

}


//     /** 内部工具：根据当前行，计算 ♥ 数量，+delta（不超过 max），重新生成这一行 */
//     private static String incHungerLine(String oldLine, int delta, int max) {
//         // 这里用的就是你类里已经有的那个 countHeart(String s) 方法
//         int current = countHeart(oldLine);
//         int next = current + delta;
//         if (next > max) next = max;
//         if (next < 0) next = 0; // 以防以后想写扣饥饿度

//         // 如果没有变化就直接返回原行
//         if (next == current) {
//             return oldLine;
//         }

//         String hearts = "♥".repeat(next);

//         // 这里直接拼一个你想要的格式，
//         // 根据你 cat_condition.txt 的实际情况可以微调
//         // 比如你那边是全角括号，可以把 (6/6) 换成 （6/6）
//         return "饥饿度： " + hearts + "   (" + next + "/" + max + ")";
//     }

//     /** 使用一次指定物品：name 为行里出现的名字，比如 "猫粮"、"超级猫粮"。
//  *  数量 > 0 时减 1，减到 0 为止。
//  *  @return 是否成功使用（数量大于 0）
//  */
// public static boolean useItem(java.nio.file.Path goodsFile, String name)
//         throws java.io.IOException {

//     java.util.List<String> lines = java.nio.file.Files.readAllLines(
//             goodsFile,
//             java.nio.charset.StandardCharsets.UTF_8
//     );

//     boolean changed = false;

//     for (int i = 0; i < lines.size(); i++) {
//         String line = lines.get(i);

//         // 跳过标题行
//         if (i == 0) continue;

//         if (line.contains(name)) {
//             String newLine = decItemCount(line);
//             if (!newLine.equals(line)) {
//                 changed = true;      // 数量真的 -1 了
//                 lines.set(i, newLine);
//             }
//             break;
//         }
//     }

//     if (changed) {
//         java.nio.file.Files.write(
//                 goodsFile,
//                 lines,
//                 java.nio.charset.StandardCharsets.UTF_8,
//                 java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
//         );
//     }

//     return changed;
// }

// /** 把像“猫粮 * 7 （恢复一点饥饿值）”这种行里的数字减 1，最少到 0 */
// private static String decItemCount(String line) {
//     // 找到 '*' 后面的数字
//     int starIndex = line.indexOf('*');
//     if (starIndex < 0) return line;

//     // 从 '*' 后面开始向后读数字
//     int i = starIndex + 1;
//     while (i < line.length() && line.charAt(i) == ' ') {
//         i++;
//     }

//     int startNum = i;
//     while (i < line.length() && Character.isDigit(line.charAt(i))) {
//         i++;
//     }
//     int endNum = i;

//     if (startNum >= endNum) return line; // 没找到数字

//     String numStr = line.substring(startNum, endNum);
//     int value;
//     try {
//         value = Integer.parseInt(numStr);
//     } catch (NumberFormatException e) {
//         return line;
//     }

//     if (value <= 0) {
//         // 已经是 0，就不再减
//         return line;
//     }
//     int next = value - 1;

//     // 把原来的数字替换成新数字
//     String newNumStr = String.valueOf(next);
//     return line.substring(0, startNum) + newNumStr + line.substring(endNum);
//  }
