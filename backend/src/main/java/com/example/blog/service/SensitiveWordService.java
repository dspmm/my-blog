package com.example.blog.service;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 评论1级自动机审服务 (基于 GitHub 顶流组件 houbb/sensitive-word + 动态自定义词库扩展)
 */
@Service
public class SensitiveWordService {

    // 允许博主动态追加自定义敏感词集合
    private final Set<String> customSensitiveWords = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public SensitiveWordService() {
        // 预置常见广告导流与恶意灌水自定义词
        customSensitiveWords.add("加微信");
        customSensitiveWords.add("加V");
        customSensitiveWords.add("加v");
        customSensitiveWords.add("兼职刷单");
        customSensitiveWords.add("代开发票");
    }

    /**
     * 自动扫描文本中包含的所有敏感词 (1级自动审核核心)
     *
     * @param text 待审内容
     * @return 命中的敏感词列表（去重）
     */
    public List<String> findSensitiveWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> matched = new LinkedHashSet<>();

        // 1. 采用官方词库进行 AC 自动机与拼音/变形多维度全量匹配
        try {
            List<String> officialMatches = SensitiveWordHelper.findAll(text);
            if (officialMatches != null && !officialMatches.isEmpty()) {
                matched.addAll(officialMatches);
            }
        } catch (Exception e) {
            // 兜底保护，避免词库引擎异常阻断评论流
        }

        // 2. 匹配博主自定义追加的个性化敏感词
        String lowerText = text.toLowerCase();
        for (String word : customSensitiveWords) {
            if (lowerText.contains(word.toLowerCase())) {
                matched.add(word);
            }
        }

        return new ArrayList<>(matched);
    }

    /**
     * 是否包含敏感词
     */
    public boolean containsSensitiveWord(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        if (SensitiveWordHelper.contains(text)) {
            return true;
        }
        String lowerText = text.toLowerCase();
        for (String word : customSensitiveWords) {
            if (lowerText.contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有博主自定义敏感词
     */
    public Set<String> getCustomSensitiveWords() {
        return new HashSet<>(customSensitiveWords);
    }

    /**
     * 添加自定义敏感词
     */
    public void addCustomWord(String word) {
        if (word != null && !word.trim().isEmpty()) {
            customSensitiveWords.add(word.trim());
        }
    }

    /**
     * 移除自定义敏感词
     */
    public void removeCustomWord(String word) {
        if (word != null) {
            customSensitiveWords.remove(word.trim());
        }
    }
}
