package com.example.blog.common;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SecurityUtils {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String HASH_PREFIX = "pbkdf2-sha256$";
    private static final int PBKDF2_ITERATIONS = 210_000;
    private static final int HASH_LENGTH_BITS = 256;
    private static final int SALT_LENGTH_BYTES = 32;

    /**
     * 生成随机盐值
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(salt);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(salt);
    }

    /**
     * 使用 PBKDF2-HMAC-SHA256 计算适用于密码存储的慢哈希。
     */
    public static String encryptPassword(String password, String salt) {
        try {
            byte[] derived = derive(password, salt, PBKDF2_ITERATIONS);
            return HASH_PREFIX + PBKDF2_ITERATIONS + "$" + Base64.getEncoder().withoutPadding().encodeToString(derived);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("无法计算密码哈希", e);
        }
    }

    /**
     * 校验密码
     */
    public static boolean matchesPassword(String rawPassword, String encryptedPassword, String salt) {
        if (rawPassword == null || encryptedPassword == null || salt == null) {
            return false;
        }

        if (!encryptedPassword.startsWith(HASH_PREFIX)) {
            // 兼容既有单轮 SHA-256 密码；登录成功后会由服务层升级为 PBKDF2。
            return MessageDigest.isEqual(
                    legacySha256(rawPassword, salt).getBytes(StandardCharsets.UTF_8),
                    encryptedPassword.toLowerCase(java.util.Locale.ROOT).getBytes(StandardCharsets.UTF_8));
        }

        try {
            String[] parts = encryptedPassword.split("\\$", 3);
            if (parts.length != 3 || !"pbkdf2-sha256".equals(parts[0])) {
                return false;
            }
            int iterations = Integer.parseInt(parts[1]);
            if (iterations < 100_000 || iterations > 1_000_000) {
                return false;
            }
            byte[] expected = Base64.getDecoder().decode(parts[2]);
            byte[] actual = derive(rawPassword, salt, iterations);
            return MessageDigest.isEqual(expected, actual);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean needsPasswordUpgrade(String encryptedPassword) {
        return encryptedPassword == null || !encryptedPassword.startsWith(HASH_PREFIX);
    }

    private static byte[] derive(String password, String salt, int iterations) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), iterations, HASH_LENGTH_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }

    private static String legacySha256(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest((password + "{" + salt + "}").getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("SHA-256 算法未找到", e);
        }
    }
}
