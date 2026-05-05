package cat;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Properties;

public final class SaveStore {

    // 【修改点1】将之前重复的 SaveState 合二为一
    public static final class SaveState {
        public LocalDate lastDate;
        public int dayCount;
        public long lastTs;
        public int trainTickets;
        public int lastFishDay;

        // --- 为了“飞升机制”新增的字段 ---
        public boolean isAscended;      // 标志位：猫咪是否正在天堂修行
        public int postNoticeDays;     // 当前已连续张贴启事的天数
        public LocalDate lastPostDate; // 记录上次贴启事的日期，用于判断“连续”

        // ===== 补上这个名字字段 =====
        public String catName;

        // ===== 新增：排泄/环境垃圾累加器 =====
        public int poopCount;

        // ===== 新增：健康状态 =====
        public boolean isSick;
    }

    private final Path file;

    public SaveStore(Path file) {
        this.file = file;
    }

    public SaveState read() throws IOException {
        Properties p = new Properties();
        SaveState s = new SaveState();
        if (Files.exists(file)) {
            try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                p.load(r);
            }
            String last = p.getProperty("lastDate");
            String dc = p.getProperty("dayCount");
            String ts = p.getProperty("lastTs");
            String tt = p.getProperty("trainTickets");
            String lf = p.getProperty("lastFishDay");

            // 【修改点2】读取新增加的飞升字段
            String asc = p.getProperty("isAscended");
            String pnd = p.getProperty("postNoticeDays");
            String lpd = p.getProperty("lastPostDate");

            // ===== 新增读取名字 =====
            String cn = p.getProperty("catName");

            // ===== 新增读取 =====
            String pc = p.getProperty("poopCount");
            s.poopCount = (pc != null) ? Integer.parseInt(pc) : 0;
            // ===== 新增读取 isSick =====
            String sick = p.getProperty("isSick");
            s.isSick = (sick != null) && Boolean.parseBoolean(sick);

            s.catName = (cn == null || cn.isBlank()) ? "" : cn;
            s.trainTickets = (tt != null) ? Integer.parseInt(tt) : 0;
            s.lastFishDay = (lf != null) ? Integer.parseInt(lf) : -1;

            s.lastDate = (last == null || last.isBlank()) ? LocalDate.now() : LocalDate.parse(last);
            s.dayCount = (dc == null || dc.isBlank()) ? 0 : Integer.parseInt(dc);
            s.lastTs = (ts == null || ts.isBlank()) ? System.currentTimeMillis() : Long.parseLong(ts);

            // 解析飞升相关的本地数据
            s.isAscended = (asc != null) && Boolean.parseBoolean(asc);
            s.postNoticeDays = (pnd != null) ? Integer.parseInt(pnd) : 0;
            s.lastPostDate = (lpd == null || lpd.isBlank()) ? null : LocalDate.parse(lpd);

        } else {
            // 新建存档时的默认值
            s.lastDate = LocalDate.now();
            s.dayCount = 0;
            s.lastTs = System.currentTimeMillis();
            s.trainTickets = 0;
            s.lastFishDay = -1;

            s.isAscended = false;
            s.postNoticeDays = 0;
            s.lastPostDate = null;

            // ===== 新增初始化名字 =====
            s.catName = "";
            s.poopCount = 0; // ===== 新建存档默认为 0 =====

            write(s); // 初始化写入
        }
        return s;
    }

    public void write(SaveState s) throws IOException {
        Properties p = new Properties();
        if (s.lastDate != null) {
            p.setProperty("lastDate", s.lastDate.toString());
        }

        // ===== 新增写入名字 =====
        if (s.catName != null && !s.catName.isEmpty()) {
            p.setProperty("catName", s.catName);
        }

        // ===== 新增保存 =====
        p.setProperty("poopCount", Integer.toString(s.poopCount));

        // ===== 新增保存 isSick =====
        p.setProperty("isSick", Boolean.toString(s.isSick));

        p.setProperty("dayCount", Integer.toString(s.dayCount));
        p.setProperty("lastTs", Long.toString(s.lastTs));
        p.setProperty("trainTickets", Integer.toString(s.trainTickets));
        p.setProperty("lastFishDay", Integer.toString(s.lastFishDay));

        // 【修改点3】保存新增加的飞升字段
        p.setProperty("isAscended", Boolean.toString(s.isAscended));
        p.setProperty("postNoticeDays", Integer.toString(s.postNoticeDays));
        if (s.lastPostDate != null) {
            p.setProperty("lastPostDate", s.lastPostDate.toString());
        }

        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (Writer w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            p.store(w, "pet-cat save");
        }
    }

//    public static class CatSickException extends RuntimeException {
//        public CatSickException(String message) { super(message); }
//    }
}