package com.example.blog.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class MinioConfig {

    private static final Logger log = LoggerFactory.getLogger(MinioConfig.class);

    @Value("${minio.endpoint:http://localhost:9000}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket-name:my-blog}")
    private String bucketName;

    private volatile boolean bucketReady;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    /**
     * 系统启动就绪后，自动检查并创建存储桶，同时赋予全网公开只读策略
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initBucket() {
        try {
            ensureBucketReady();
        } catch (Exception e) {
            log.warn("⚠️ [MinIO] 初始化存储桶 '{}' 失败；图片上传时会重新尝试: {}", bucketName, e.getMessage());
        }
    }

    public synchronized void ensureBucketReady() {
        if (bucketReady) return;
        try {
            MinioClient client = minioClient();
            boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                log.info("📦 [MinIO] 存储桶 '{}' 不存在，开始自动创建...", bucketName);
                client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("✅ [MinIO] 存储桶 '{}' 创建成功！", bucketName);
            }

            // 无论是新建还是已有桶，统一确保 Public Read (s3:GetObject) 策略生效
            String readOnlyPolicy = """
                    {
                      "Version": "2012-10-17",
                      "Statement": [
                        {
                          "Effect": "Allow",
                          "Principal": "*",
                          "Action": ["s3:GetObject"],
                          "Resource": ["arn:aws:s3:::%s/*"]
                        }
                      ]
                    }
                    """.formatted(bucketName);

            client.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucketName)
                            .config(readOnlyPolicy)
                            .build()
            );
            log.info("🔓 [MinIO] 存储桶 '{}' 已成功应用全网公开只读(Public Read)策略！", bucketName);
            bucketReady = true;
        } catch (Exception e) {
            throw new IllegalStateException("MinIO 存储桶初始化失败: " + bucketName, e);
        }
    }
}
