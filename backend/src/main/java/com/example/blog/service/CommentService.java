package com.example.blog.service;

import com.example.blog.entity.Comment;
import java.util.List;
import java.util.Map;

public interface CommentService {
    List<Comment> getCommentsByPostId(Long postId, String callerAuthor, String callerUsername, boolean isAdmin);
    Comment addComment(Long postId, Comment comment, String callerRole, String callerAuthor, String callerUsername);
    List<Comment> getRecentComments();
    List<Comment> listAllComments(Long postId, String status, String keyword);
    Comment auditComment(Long id, String newStatus, String remark);
    Comment toggleTopComment(Long id);
    void deleteComment(Long id);
    Map<String, Object> getAuditStats();
}
