package cat;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class Timekeeper {

    /** 按真实日期推进天数；返回本次推进的天数 */
    public static int syncDays(SaveStore store, SaveStore.SaveState state) throws IOException {
        LocalDate today = LocalDate.now();
        long delta = ChronoUnit.DAYS.between(state.lastDate, today);
        if (delta > 0) {
            state.dayCount += (int) delta;
            state.lastDate = today;
            store.write(state);
        }
        return (int) delta;
    }

    // ========= 新增：每 3 小时衰减 =========
    private static final long THREE_HOURS_MS = 3L * 60 * 60 * 1000;

    /**
     * 根据与上次时间戳的间隔，按“每 3 小时”为 1 步做衰减。
     * - 饥饿度/口渴度/亲密度/兴奋度：每步 -1 个 ♥（不低于 0）
     * - 清洁度：每步 -6 个 *
     * 返回本次衰减用了多少步（即经过了多少个 3 小时）
     */
    public static int decayBy3Hours(SaveStore store, SaveStore.SaveState state,
                                    Path catConditionPath) throws IOException {
        long now = System.currentTimeMillis();
        if (state.lastTs <= 0) {
            // 兼容老存档：没有 lastTs 就初始化为当前时间
            state.lastTs = now;
            store.write(state);
            return 0;
        }

        long diff = now - state.lastTs;
        int steps = (int) (diff / THREE_HOURS_MS);
        if (steps <= 0) return 0;

        // 修改 cat_condition.txt（内部负责把 ♥ 和 * 递减并同步数值）
        CatConditionFile.degrade(catConditionPath, steps);

        // 推进已消费掉的时间，并落盘
        state.lastTs += steps * THREE_HOURS_MS;
        store.write(state);
        return steps;
    }
}