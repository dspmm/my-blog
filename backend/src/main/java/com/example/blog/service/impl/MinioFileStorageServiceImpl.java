package com.example.blog.service.impl;

import com.example.blog.service.FileStorageService;
import com.example.blog.config.MinioConfig;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class MinioFileStorageServiceImpl implements FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(MinioFileStorageServiceImpl.class);

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(
            Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "svg")
    );

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024; // 10MB

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Value("${minio.bucket-name:my-blog}")
    private String bucketName;

    @Value("${minio.public-url:/my-blog}")
    private String publicUrl;

    public MinioFileStorageServiceImpl(MinioClient minioClient, MinioConfig minioConfig) {
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
    }

    @Override
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("上传图片文件不能超过 10MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new RuntimeException("无效的图片文件名");
        }

        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new RuntimeException("不支持的文件类型，仅支持 JPG、PNG、GIF、WEBP、SVG 图片格式");
        }

        // 按年月目录结构归档：2026/10/uuid.ext
        LocalDate now = LocalDate.now();
        String objectName = String.format("%d/%02d/%s.%s",
                now.getYear(),
                now.getMonthValue(),
                UUID.randomUUID().toString().replace("-", ""),
                ext);

        String contentType = file.getContentType();
        if (contentType == null || contentType.trim().isEmpty()) {
            contentType = switch (ext) {
                case "png" -> "image/png";
                case "gif" -> "image/gif";
                case "webp" -> "image/webp";
                case "svg" -> "image/svg+xml";
                default -> "image/jpeg";
            };
        }

        try (InputStream inputStream = file.getInputStream()) {
            minioConfig.ensureBucketReady();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(contentType)
                            .build()
            );

            String fileUrl;
            if (publicUrl == null || publicUrl.trim().isEmpty() || "/my-blog".equals(publicUrl.trim())) {
                fileUrl = "/" + bucketName + "/" + objectName;
            } else if (publicUrl.startsWith("http://") || publicUrl.startsWith("https://")) {
                String base = publicUrl.endsWith("/") ? publicUrl.substring(0, publicUrl.length() - 1) : publicUrl;
                fileUrl = base + "/" + bucketName + "/" + objectName;
            } else {
                String base = publicUrl.endsWith("/") ? publicUrl.substring(0, publicUrl.length() - 1) : publicUrl;
                fileUrl = base.startsWith("/" + bucketName) ? base + "/" + objectName : base + "/" + bucketName + "/" + objectName;
            }
            log.info("📤 [MinIO] 图片上传成功: {} -> {}", originalFilename, fileUrl);
            return fileUrl;

        } catch (Exception e) {
            log.error("❌ [MinIO] 文件上传至存储桶失败: {}", e.getMessage(), e);
            throw new RuntimeException("图片上传失败：" + e.getMessage(), e);
        }
    }
}
