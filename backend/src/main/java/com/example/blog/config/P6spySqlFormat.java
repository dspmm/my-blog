package com.example.blog.config;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.springframework.stereotype.Component;

/**
 * P6Spy 自定义 SQL 美化与真实参数回填格式化策略
 * 1. 自动将「?」占位符替换为真实参数值，生成可直接在数据库执行的真实完整 SQL；
 * 2. 压缩多余换行与空格，保持控制台单行清晰不乱；
 * 3. 超过 500ms 自动高亮标红预警 [SLOW SQL ⚠️]；
 * 4. 内置 ANSI 终端彩色高亮，与 1Panel 黑色终端完美契合。
 */
@Component
public class P6spySqlFormat implements MessageFormattingStrategy {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_BOLD = "\u001B[1m";

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        if (sql == null || sql.trim().isEmpty()) {
            return "";
        }
        // 仅记录实际执行的 SQL 语句，忽略空连接探测
        if (!"statement".equalsIgnoreCase(category)) {
            return "";
        }

        // 整理单行格式，压缩多余换行与连续制表符
        String singleLineSql = sql.replaceAll("\\s+", " ").trim();

        // 慢查询判断（> 500ms 触发醒目预警）
        if (elapsed > 500) {
            return String.format("%s%s[SLOW SQL ⚠️ 耗时: %d ms]%s ➜ %s%s%s",
                    ANSI_BOLD, ANSI_RED, elapsed, ANSI_RESET,
                    ANSI_YELLOW, singleLineSql, ANSI_RESET);
        } else {
            return String.format("%s[SQL 耗时: %d ms]%s ➜ %s%s%s",
                    ANSI_CYAN, elapsed, ANSI_RESET,
                    ANSI_GREEN, singleLineSql, ANSI_RESET);
        }
    }
}
