package com.example.blog.service.impl;

import com.example.blog.common.JwtUtils;
import com.example.blog.common.SecurityUtils;
import com.example.blog.dto.LoginBody;
import com.example.blog.dto.RegisterBody;
import com.example.blog.dto.UpdateProfileBody;
import com.example.blog.dto.UpdatePwdBody;
import com.example.blog.entity.SysUser;
import com.example.blog.repository.SysUserRepository;
import com.example.blog.service.MailService;
import com.example.blog.service.SysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SysUserServiceImpl implements SysUserService {

    private final SysUserRepository sysUserRepository;
    private final MailService mailService;

    public SysUserServiceImpl(SysUserRepository sysUserRepository, MailService mailService) {
        this.sysUserRepository = sysUserRepository;
        this.mailService = mailService;
    }

    @Override
    @Transactional
    public Map<String, Object> login(LoginBody loginBody, String ip) {
        if (loginBody.getUsername() == null || loginBody.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (loginBody.getPassword() == null || loginBody.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        String account = loginBody.getUsername().trim();
        String password = loginBody.getPassword().trim();

        // 优先匹配用户名 (username)，未命中时匹配个性昵称 (nickname)，支持读者使用昵称登录
        SysUser user = sysUserRepository.findByUsername(account)
                .or(() -> sysUserRepository.findFirstByNickname(account))
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        if ("1".equals(user.getStatus())) {
            throw new RuntimeException("对不起，该账号已被停用，请联系管理员");
        }

        if (!SecurityUtils.matchesPassword(password, user.getPassword(), user.getSalt())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (SecurityUtils.needsPasswordUpgrade(user.getPassword())) {
            String newSalt = SecurityUtils.generateSalt();
            user.setSalt(newSalt);
            user.setPassword(SecurityUtils.encryptPassword(password, newSalt));
        }

        // 更新登录信息
        user.setLoginIp(ip != null ? ip : "127.0.0.1");
        user.setLoginDate(LocalDateTime.now());
        sysUserRepository.save(user);

        // 生成系统 Token 与鉴权负载
        String token = JwtUtils.createToken(user.getId(), user.getUsername(), user.getNickname(), user.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);

        Map<String, Object> safeUser = new HashMap<>();
        safeUser.put("id", user.getId());
        safeUser.put("username", user.getUsername());
        safeUser.put("nickname", user.getNickname());
        safeUser.put("email", user.getEmail());
        safeUser.put("avatar", user.getAvatar());
        safeUser.put("role", user.getRole());
        safeUser.put("status", user.getStatus());
        safeUser.put("loginDate", user.getLoginDate());
        result.put("user", safeUser);

        // 角色与权限标识 (标准 RBAC 格式)
        result.put("roles", Collections.singletonList(user.getRole()));
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            result.put("permissions", Collections.singletonList("*:*:*"));
        } else {
            result.put("permissions", Arrays.asList("post:view", "post:like", "comment:add"));
        }

        return result;
    }

    @Override
    @Transactional
    public void register(RegisterBody registerBody) {
        String username = registerBody.getUsername() != null ? registerBody.getUsername().trim() : "";
        String password = registerBody.getPassword() != null ? registerBody.getPassword().trim() : "";
        String email = registerBody.getEmail() != null ? registerBody.getEmail().trim().toLowerCase() : "";
        String code = registerBody.getCode() != null ? registerBody.getCode().trim() : "";

        // 允许 2 到 20 位字符，支持“测试”、“张三”等双字中文用户名
        if (username.length() < 2 || username.length() > 20) {
            throw new IllegalArgumentException("用户名长度必须在 2 到 20 个字符之间");
        }
        if (password.length() < 5 || password.length() > 30) {
            throw new IllegalArgumentException("密码长度必须在 5 到 30 个字符之间");
        }

        if (email.isEmpty()) {
            throw new IllegalArgumentException("电子邮箱不能为空");
        }
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("请输入合法的电子邮箱地址");
        }

        if (code.isEmpty()) {
            throw new IllegalArgumentException("请输入邮箱验证码");
        }

        if (sysUserRepository.existsByUsername(username)) {
            throw new RuntimeException("该用户名已被注册，请尝试其他用户名");
        }

        if (sysUserRepository.existsByEmail(email)) {
            throw new RuntimeException("该邮箱已被其他账号绑定，请直接登录或更换邮箱");
        }

        // 校验 6 位验证码有效性与过期时间
        if (!mailService.verifyCode(email, code)) {
            throw new RuntimeException("邮箱验证码错误或已失效，请重新获取");
        }

        String salt = SecurityUtils.generateSalt();
        String encryptedPassword = SecurityUtils.encryptPassword(password, salt);

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(encryptedPassword);
        user.setSalt(salt);
        user.setNickname(registerBody.getNickname() != null && !registerBody.getNickname().trim().isEmpty() ? registerBody.getNickname().trim() : username);
        user.setEmail(email);
        user.setAvatar("👨‍💻");
        user.setRole("USER");
        user.setStatus("0");

        sysUserRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getUserInfo(Long userId) {
        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> safeUser = new HashMap<>();
        safeUser.put("id", user.getId());
        safeUser.put("username", user.getUsername());
        safeUser.put("nickname", user.getNickname());
        safeUser.put("email", user.getEmail());
        safeUser.put("avatar", user.getAvatar());
        safeUser.put("role", user.getRole());
        safeUser.put("status", user.getStatus());
        safeUser.put("loginDate", user.getLoginDate());
        safeUser.put("createdAt", user.getCreatedAt());

        result.put("user", safeUser);
        result.put("roles", Collections.singletonList(user.getRole()));
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            result.put("permissions", Collections.singletonList("*:*:*"));
        } else {
            result.put("permissions", Arrays.asList("post:view", "post:like", "comment:add"));
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SysUser> listUsers() {
        List<SysUser> users = sysUserRepository.findAll();
        // 清除敏感字段
        for (SysUser u : users) {
            u.setPassword(null);
            u.setSalt(null);
        }
        return users;
    }

    @Override
    @Transactional
    public void changeStatus(Long userId, String status) {
        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            throw new RuntimeException("不允许停用内置超级管理员账号");
        }
        user.setStatus("1".equals(status) ? "1" : "0");
        sysUserRepository.save(user);
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, UpdateProfileBody body) {
        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (body.getNickname() != null && !body.getNickname().trim().isEmpty()) {
            user.setNickname(body.getNickname().trim());
        }
        if (body.getEmail() != null) {
            user.setEmail(body.getEmail().trim());
        }
        if (body.getAvatar() != null && !body.getAvatar().trim().isEmpty()) {
            user.setAvatar(body.getAvatar().trim());
        }
        sysUserRepository.save(user);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, UpdatePwdBody body) {
        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        String oldPassword = body.getOldPassword() != null ? body.getOldPassword().trim() : "";
        String newPassword = body.getNewPassword() != null ? body.getNewPassword().trim() : "";
        if (!SecurityUtils.matchesPassword(oldPassword, user.getPassword(), user.getSalt())) {
            throw new RuntimeException("原密码错误，修改失败");
        }
        if (newPassword.length() < 5 || newPassword.length() > 30) {
            throw new IllegalArgumentException("新密码长度必须在 5 到 30 位之间");
        }

        String newSalt = SecurityUtils.generateSalt();
        String newHash = SecurityUtils.encryptPassword(newPassword, newSalt);
        user.setSalt(newSalt);
        user.setPassword(newHash);
        sysUserRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            throw new RuntimeException("不允许删除内置超级管理员账号");
        }
        sysUserRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public SysUser createUser(SysUser user) {
        if (sysUserRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        String salt = SecurityUtils.generateSalt();
        user.setSalt(salt);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword(), salt));
        return sysUserRepository.save(user);
    }
}
