package com.example.blog.controller;

import com.example.blog.common.JwtUtils;
import com.example.blog.common.Result;
import com.example.blog.dto.UpdateProfileBody;
import com.example.blog.dto.UpdatePwdBody;
import com.example.blog.entity.SysUser;
import com.example.blog.service.SysUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class SysUserController {

    private final SysUserService sysUserService;

    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    private Map<String, Object> checkAuth(String authHeader) {
        if (authHeader == null || authHeader.isEmpty()) {
            throw new RuntimeException("请先登录后操作");
        }
        Map<String, Object> claims = JwtUtils.parseToken(authHeader);
        if (claims == null) {
            throw new RuntimeException("登录凭证已失效，请重新登录");
        }
        return claims;
    }

    private void checkAdmin(String authHeader) {
        Map<String, Object> claims = checkAuth(authHeader);
        String role = (String) claims.get("role");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("无权操作，此功能仅限超级管理员使用");
        }
    }

    @GetMapping
    public Result<List<SysUser>> listUsers(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        checkAdmin(authHeader);
        return Result.success(sysUserService.listUsers());
    }

    @PostMapping
    public Result<SysUser> createUser(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                      @RequestBody SysUser user) {
        checkAdmin(authHeader);
        return Result.success(sysUserService.createUser(user));
    }

    @PutMapping("/{id}/status")
    public Result<Void> changeStatus(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                     @PathVariable Long id,
                                     @RequestParam String status) {
        checkAdmin(authHeader);
        sysUserService.changeStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                   @PathVariable Long id) {
        checkAdmin(authHeader);
        sysUserService.deleteUser(id);
        return Result.success();
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                      @RequestBody UpdateProfileBody body) {
        Map<String, Object> claims = checkAuth(authHeader);
        Number userId = (Number) claims.get("userId");
        sysUserService.updateProfile(userId.longValue(), body);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                       @RequestBody UpdatePwdBody body) {
        Map<String, Object> claims = checkAuth(authHeader);
        Number userId = (Number) claims.get("userId");
        sysUserService.updatePassword(userId.longValue(), body);
        return Result.success();
    }
}
