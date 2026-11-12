package com.example.blog.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /**
     * 上传图片文件并返回全网公开访问直链
     *
     * @param file 待上传的文件
     * @return 图片的永久公网访问 URL
     */
    String uploadImage(MultipartFile file);
}
