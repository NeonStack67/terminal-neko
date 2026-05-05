package cat;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GUIMain {

    // 把你需要全局共享的状态和路径存起来
    private static SaveStore.SaveState state;
    private static java.nio.file.Path condPath;
    private static JTextArea statusArea; // 用来显示宠物状态的文本框

    public static void main(String[] args) {
        // GUI 程序必须在特殊的“事件调度线程”中运行
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        // 1. 创建主窗口
        JFrame frame = new JFrame("🐾 宠物世界");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600); // 宽 500，高 600
        frame.setLocationRelativeTo(null); // 居中显示

        // 2. 设置整体布局 (边界布局)
        frame.setLayout(new BorderLayout(10, 10));

        // 3. 中间的状态显示区 (相当于你黑框框里的输出)
        statusArea = new JTextArea("正在加载宠物数据...\n");
        statusArea.setEditable(false); // 玩家不能手敲字修改
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 16)); // 设置好看一点的等宽字体

        // 给文本框加个滚动条，放进窗口中间
        JScrollPane scrollPane = new JScrollPane(statusArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // 4. 底部的操作按钮区 (相当于你以前的 1~13 选项)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 3, 5, 5)); // 2行3列的网格布局

        JButton btnFeed = new JButton("🐟 喂猫粮");
        JButton btnPlay = new JButton("🎾 玩耍");
        JButton btnWash = new JButton("🛁 洗澡");
        JButton btnRefresh = new JButton("🔄 刷新状态");

        // 5. 绑定按钮的点击事件 (这就是 GUI 的灵魂！)
        btnFeed.addActionListener(e -> {
            statusArea.append("你点击了喂食按钮！(这里即将接入你的底层代码)\n");
            // 这里我们等会儿会接入 CatConditionFile.feed(...)
        });

        btnPlay.addActionListener(e -> {
            statusArea.append("你陪猫咪玩了一会儿球！\n");
        });

        btnWash.addActionListener(e -> {
            statusArea.append("你给猫咪洗了个澡！\n");
        });

        btnRefresh.addActionListener(e -> {
            statusArea.append("刷新状态...\n");
        });

        // 把按钮加到面板里
        buttonPanel.add(btnFeed);
        buttonPanel.add(btnPlay);
        buttonPanel.add(btnWash);
        buttonPanel.add(btnRefresh);

        // 把按钮面板加到窗口底部
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // 6. 显示窗口！
        frame.setVisible(true);
    }
}