package com.example.blog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SendEmailCodeBody {

    @NotBlank(message = "邮箱地址不能为空")
    @Email(message = "请输入合法的电子邮箱地址")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
