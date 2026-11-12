package com.example.blog.config;

import com.p6spy.engine.spy.appender.Slf4JLogger;

/**
 * P6Spy 高级日志适配器：
 * 继承 Slf4JLogger 并拦截空文本，彻底杜绝输出空行，保持控制台绝对清爽紧凑
 */
public class CustomP6spyLogger extends Slf4JLogger {
    @Override
    public void logText(String text) {
        if (text != null && !text.trim().isEmpty()) {
            super.logText(text);
        }
    }
}
