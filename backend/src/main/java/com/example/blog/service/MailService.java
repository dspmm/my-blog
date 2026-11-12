package com.example.blog.service;

public interface MailService {

    /**
     * 发送邮箱注册验证码（支持 60s 频控与 5 分钟有效期）
     * @param email 目标邮箱地址
     */
    void sendVerificationCode(String email);

    /**
     * 校验邮箱验证码（验证成功即作废该验证码）
     * @param email 目标邮箱地址
     * @param code 用户输入的 6 位验证码
     * @return 验证是否通过
     */
    boolean verifyCode(String email, String code);
}
