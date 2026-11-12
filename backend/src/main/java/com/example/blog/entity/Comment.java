package com.example.blog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "parent_id")
    private Long parentId; // 父级评论 ID (若为顶层根评论则为 null)

    @Column(nullable = false, length = 50)
    private String author = "访客";

    @Column(name = "reply_to", length = 50)
    private String replyTo; // 被回复者昵称 (如 "大柿子")

    @Column(name = "reply_to_username", length = 50)
    private String replyToUsername; // 被回复者账号 (用于站内定向通知)

    @Column(length = 50)
    private String username; // 发表此评论的用户真实账号 (未登录游客为 null)

    @Column(length = 255)
    private String avatar = "👨‍💻";

    @Column(length = 20)
    private String role = "USER";

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_top", nullable = false)
    private Boolean isTop = false;

    @Column(length = 20, nullable = false)
    private String status = "APPROVED"; // APPROVED (已审核/已发布) | PENDING (待管理员2级审核) | REJECTED (已驳回)

    @Column(name = "sensitive_words", columnDefinition = "TEXT")
    private String sensitiveWords; // 1级机审命中的敏感词快照

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (author == null || author.trim().isEmpty()) author = "匿名访客";
        if (isTop == null) isTop = false;
        if (status == null || status.trim().isEmpty()) status = "APPROVED";
    }

    public Comment() {}

    public Comment(Long postId, String author, String content) {
        this.postId = postId;
        this.author = author;
        this.content = content;
        this.isTop = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getReplyTo() { return replyTo; }
    public void setReplyTo(String replyTo) { this.replyTo = replyTo; }
    public String getReplyToUsername() { return replyToUsername; }
    public void setReplyToUsername(String replyToUsername) { this.replyToUsername = replyToUsername; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Boolean getIsTop() { return isTop; }
    public void setIsTop(Boolean isTop) { this.isTop = isTop; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSensitiveWords() { return sensitiveWords; }
    public void setSensitiveWords(String sensitiveWords) { this.sensitiveWords = sensitiveWords; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
