package com.example.blog.controller;

import com.example.blog.common.JwtUtils;
import com.example.blog.common.Result;
import com.example.blog.service.FileStorageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    private void checkAdmin(String authHeader) {
        if (authHeader == null || authHeader.isEmpty()) {
            throw new RuntimeException("无权操作：请使用管理员账号登录");
        }
        Map<String, Object> claims = JwtUtils.parseToken(authHeader);
        if (claims == null) {
            throw new RuntimeException("登录已失效，请重新登录");
        }
        String role = (String) claims.get("role");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("无权操作：文件上传功能仅限博主/管理员使用");
        }
    }

    @PostMapping("/upload")
    public Result<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        checkAdmin(authHeader);
        String url = fileStorageService.uploadImage(file);
        
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        data.put("name", file.getOriginalFilename());
        return Result.success(data);
    }
}
