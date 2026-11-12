package com.example.blog.service.impl;

import com.example.blog.service.MailService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class MailServiceImpl implements MailService {

    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int MAX_VERIFY_ATTEMPTS = 5;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${app.auth.log-verification-code:false}")
    private boolean logVerificationCode;

    // 内存验证码缓存，email -> CodeRecord
    private final Map<String, CodeRecord> codeCache = new ConcurrentHashMap<>();
    private final Set<String> sendsInProgress = ConcurrentHashMap.newKeySet();

    private static class CodeRecord {
        final String code;
        final long expireAt;
        final long lastSentAt;
        int failedAttempts;

        CodeRecord(String code, long expireAt, long lastSentAt) {
            this.code = code;
            this.expireAt = expireAt;
            this.lastSentAt = lastSentAt;
        }
    }

    @Override
    public void sendVerificationCode(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱地址不能为空");
        }
        String targetEmail = email.trim().toLowerCase();
        if (!sendsInProgress.add(targetEmail)) {
            throw new RuntimeException("验证码正在发送，请稍后重试");
        }

        try {
            long now = System.currentTimeMillis();
            CodeRecord existing = codeCache.get(targetEmail);
            if (existing != null && now - existing.lastSentAt < 60 * 1000) {
                long remaining = (60 * 1000 - (now - existing.lastSentAt)) / 1000;
                throw new RuntimeException("验证码发送太频繁，请等待 " + Math.max(1, remaining) + " 秒后再试");
            }
            String code = String.format(Locale.ROOT, "%06d", SECURE_RANDOM.nextInt(1_000_000));
            if (mailSender == null || mailUsername == null || mailUsername.trim().isEmpty()) {
                fallbackOrFail(targetEmail, code, now, new IllegalStateException("邮箱服务未配置"));
                return;
            }

            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(mailUsername, "小东的技术全栈");
                helper.setReplyTo(mailUsername);
                helper.setTo(targetEmail);
                helper.setSubject("【小东的技术全栈】注册验证码");
                helper.setText(buildHtmlTemplate(targetEmail, code), true);

                mailSender.send(message);
                long sentAt = System.currentTimeMillis();
                codeCache.put(targetEmail, new CodeRecord(code, sentAt + 5 * 60 * 1000, sentAt));
                log.info("已通过 SMTP 成功向读者邮箱 [{}] 发送注册验证码", targetEmail);
            } catch (Exception e) {
                log.warn("SMTP 验证码邮件发送失败: {}", e.getMessage());
                fallbackOrFail(targetEmail, code, System.currentTimeMillis(), e);
            }
        } finally {
            sendsInProgress.remove(targetEmail);
        }
    }

    private void fallbackOrFail(String email, String code, long now, Exception cause) {
        if (!logVerificationCode) {
            throw new RuntimeException("邮箱服务暂不可用，请稍后重试", cause);
        }
        codeCache.put(email, new CodeRecord(code, now + 5 * 60 * 1000, now));
        log.warn("开发模式验证码回退已启用；邮箱 [{}] 的验证码为 {}（5 分钟有效）", email, code);
    }

    @Override
    public boolean verifyCode(String email, String code) {
        if (email == null || code == null) {
            return false;
        }
        String targetEmail = email.trim().toLowerCase();
        AtomicBoolean verified = new AtomicBoolean(false);
        String candidate = code.trim();
        if (!candidate.matches("\\d{6}")) {
            return false;
        }
        codeCache.computeIfPresent(targetEmail, (key, record) -> {
            if (System.currentTimeMillis() > record.expireAt) {
                return null;
            }
            boolean matches = MessageDigest.isEqual(
                    record.code.getBytes(StandardCharsets.UTF_8),
                    candidate.getBytes(StandardCharsets.UTF_8));
            if (matches) {
                verified.set(true);
                return null;
            }
            record.failedAttempts++;
            return record.failedAttempts >= MAX_VERIFY_ATTEMPTS ? null : record;
        });
        return verified.get();
    }

    /**
     * 极客科技风 HTML 邮件模板
     */
    private String buildHtmlTemplate(String email, String code) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='margin:0;padding:24px;background-color:#0f172a;font-family:-apple-system,BlinkMacSystemFont,\"Segoe UI\",Roboto,sans-serif;color:#e2e8f0;'>" +
                "  <div style='max-width:540px;margin:0 auto;background:#1e293b;border-radius:16px;border:1px solid #334155;overflow:hidden;box-shadow:0 10px 25px -5px rgba(0,0,0,0.5);'>" +
                "    <div style='padding:24px 30px;background:linear-gradient(135deg,#0284c7,#38bdf8);color:#ffffff;'>" +
                "      <div style='font-size:22px;font-weight:bold;letter-spacing:-0.5px;'>小东的技术全栈 👨‍💻</div>" +
                "      <div style='font-size:12px;opacity:0.9;margin-top:4px;'>Full-Stack · Cloud-Native · DevOps 极客博客</div>" +
                "    </div>" +
                "    <div style='padding:32px 30px;'>" +
                "      <p style='font-size:14px;color:#cbd5e1;margin:0 0 16px 0;'>亲爱的极客读者 (<strong>" + email + "</strong>)：</p>" +
                "      <p style='font-size:14px;color:#94a3b8;line-height:1.6;margin:0 0 24px 0;'>您正在注册「小东的技术全栈」读者账号，请在注册界面输入以下验证码以完成身份核验：</p>" +
                "      <div style='background:#0f172a;border:1px dashed #38bdf8;border-radius:12px;padding:20px;text-align:center;margin:24px 0;'>" +
                "        <span style='font-family:monospace;font-size:36px;font-weight:bold;letter-spacing:8px;color:#38bdf8;display:inline-block;'>" + code + "</span>" +
                "      </div>" +
                "      <p style='font-size:12px;color:#64748b;margin:0 0 10px 0;'>⏳ 验证码有效时间为 <strong>5 分钟</strong>。若非本人操作，请忽略此邮件。</p>" +
                "      <p style='font-size:12px;color:#64748b;margin:0;'>🔒 安全提示：博主与平台绝不会以任何理由向您索要此验证码，请勿泄露给他人。</p>" +
                "    </div>" +
                "    <div style='padding:16px 30px;background:#0f172a;border-top:1px solid #334155;font-size:11px;color:#64748b;text-align:center;'>" +
                "      © 2026 小东的技术全栈 · 驱动自 Spring Boot 3 + MySQL 8.0" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }
}
