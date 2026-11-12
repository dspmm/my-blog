package com.example.blog.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    private static volatile String secret;
    private static final int MIN_SECRET_LENGTH = 32;
    private static final long EXPIRATION_MS = 7L * 24 * 60 * 60 * 1000; // 7 天有效
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${jwt.secret:}")
    private String configuredSecret;

    @PostConstruct
    void init() {
        String resolved = (configuredSecret != null && !configuredSecret.isBlank())
                ? configuredSecret.trim()
                : System.getenv("JWT_SECRET");
        if (resolved == null || resolved.trim().length() < MIN_SECRET_LENGTH) {
            throw new IllegalStateException("JWT_SECRET 环境变量未配置或长度不足 32 位，拒绝启动");
        }
        secret = resolved.trim();
    }

    private static String getSecret() {
        String current = secret;
        if (current == null || current.isBlank()) {
            String envSecret = System.getenv("JWT_SECRET");
            if (envSecret != null && envSecret.trim().length() >= MIN_SECRET_LENGTH) {
                current = envSecret.trim();
                secret = current;
            } else {
                throw new IllegalStateException("JWT_SECRET 未初始化或长度不足 32 位");
            }
        }
        return current;
    }

    /**
     * 生成 JWT Token
     */
    public static String createToken(Long userId, String username, String nickname, String role) {
        try {
            long now = System.currentTimeMillis();
            long exp = now + EXPIRATION_MS;

            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", userId);
            payload.put("username", username);
            payload.put("nickname", nickname);
            payload.put("role", role);
            payload.put("iat", now / 1000);
            payload.put("exp", exp / 1000);

            String encodedHeader = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(objectMapper.writeValueAsString(header).getBytes(StandardCharsets.UTF_8));
            String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(objectMapper.writeValueAsString(payload).getBytes(StandardCharsets.UTF_8));

            String content = encodedHeader + "." + encodedPayload;
            String signature = sign(content, getSecret());

            return content + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("生成 Token 失败", e);
        }
    }

    /**
     * 解析并校验 Token，返回 Claims
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return null;
        }

        try {
            String content = parts[0] + "." + parts[1];
            String expectedSignature = sign(content, getSecret());
            if (!MessageDigest.isEqual(
                    expectedSignature.getBytes(StandardCharsets.UTF_8),
                    parts[2].getBytes(StandardCharsets.UTF_8))) {
                return null; // 签名不匹配
            }

            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            Map<String, Object> claims = objectMapper.readValue(payloadBytes, Map.class);

            // 检查过期时间
            Number exp = (Number) claims.get("exp");
            if (exp != null && exp.longValue() * 1000 < System.currentTimeMillis()) {
                return null; // Token 已过期
            }

            return claims;
        } catch (Exception e) {
            return null;
        }
    }

    private static String sign(String data, String secretKeyValue) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secretKeyValue.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
    }
}
