package com.example.blog.service;

import com.example.blog.dto.LoginBody;
import com.example.blog.dto.RegisterBody;
import com.example.blog.dto.UpdateProfileBody;
import com.example.blog.dto.UpdatePwdBody;
import com.example.blog.entity.SysUser;

import java.util.List;
import java.util.Map;

public interface SysUserService {

    Map<String, Object> login(LoginBody loginBody, String ip);

    void register(RegisterBody registerBody);

    Map<String, Object> getUserInfo(Long userId);

    List<SysUser> listUsers();

    void changeStatus(Long userId, String status);

    void updateProfile(Long userId, UpdateProfileBody body);

    void updatePassword(Long userId, UpdatePwdBody body);

    void deleteUser(Long userId);

    SysUser createUser(SysUser user);
}
