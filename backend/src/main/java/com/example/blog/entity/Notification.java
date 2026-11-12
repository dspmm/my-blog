package com.example.blog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(length = 20)
    private String type = "ANNOUNCEMENT"; // ANNOUNCEMENT (全站公告) | SYSTEM / NOTICE (私人私信) | AUDIT (审核通知) | FEEDBACK (意见建议)

    @Column(name = "target_username", length = 50)
    private String targetUsername = "ALL"; // "ALL" 表示全站广播，否则为指定接收人账号

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "comment_id")
    private Long commentId;

    @Column(length = 50)
    private String sender = "小东 (管理员)";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (type == null || type.trim().isEmpty()) type = "ANNOUNCEMENT";
        if (targetUsername == null || targetUsername.trim().isEmpty()) targetUsername = "ALL";
        if (sender == null || sender.trim().isEmpty()) sender = "小东 (管理员)";
    }

    public Notification() {}

    public Notification(String title, String content, String type, String targetUsername) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.targetUsername = targetUsername;
    }

    public Notification(String title, String content, String type, String targetUsername, Long postId, Long commentId) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.targetUsername = targetUsername;
        this.postId = postId;
        this.commentId = commentId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTargetUsername() { return targetUsername; }
    public void setTargetUsername(String targetUsername) { this.targetUsername = targetUsername; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
