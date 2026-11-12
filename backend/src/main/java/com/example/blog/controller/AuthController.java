package com.example.blog.controller;

import com.example.blog.common.JwtUtils;
import com.example.blog.common.Result;
import com.example.blog.dto.LoginBody;
import com.example.blog.dto.RegisterBody;
import com.example.blog.dto.SendEmailCodeBody;
import com.example.blog.repository.SysUserRepository;
import com.example.blog.service.MailService;
import com.example.blog.service.SysUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final SysUserService sysUserService;
    private final MailService mailService;
    private final SysUserRepository sysUserRepository;

    public AuthController(SysUserService sysUserService, MailService mailService, SysUserRepository sysUserRepository) {
        this.sysUserService = sysUserService;
        this.mailService = mailService;
        this.sysUserRepository = sysUserRepository;
    }

    @PostMapping("/send-code")
    public Result<Void> sendVerificationCode(@RequestBody SendEmailCodeBody body) {
        String email = body != null ? body.getEmail() : null;
        if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return Result.error("请输入合法的电子邮箱地址");
        }
        if (sysUserRepository.existsByEmail(email.trim().toLowerCase())) {
            return Result.error("该邮箱已被其他账号绑定，请直接登录或更换邮箱");
        }
        mailService.sendVerificationCode(email);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginBody loginBody, HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return Result.success(sysUserService.login(loginBody, ip));
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterBody registerBody) {
        sysUserService.register(registerBody);
        return Result.success();
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getInfo(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || authHeader.isEmpty()) {
            return Result.error(401, "请先登录");
        }
        Map<String, Object> claims = JwtUtils.parseToken(authHeader);
        if (claims == null) {
            return Result.error(401, "登录凭证无效或已过期，请重新登录");
        }
        Number userIdNum = (Number) claims.get("userId");
        if (userIdNum == null) {
            return Result.error(401, "用户凭证异常");
        }
        return Result.success(sysUserService.getUserInfo(userIdNum.longValue()));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
}
