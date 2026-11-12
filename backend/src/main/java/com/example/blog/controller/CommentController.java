package com.example.blog.controller;

import com.example.blog.common.JwtUtils;
import com.example.blog.common.Result;
import com.example.blog.entity.Comment;
import com.example.blog.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    private Map<String, Object> checkLogin(String authHeader) {
        if (authHeader == null || authHeader.trim().isEmpty()) {
            throw new RuntimeException("访客暂无评论权限，请先登录读者账号后再发表评论");
        }
        Map<String, Object> claims = JwtUtils.parseToken(authHeader);
        if (claims == null) {
            throw new RuntimeException("登录已失效，请重新登录读者账号");
        }
        return claims;
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
            throw new RuntimeException("无权操作：此功能仅限超级管理员使用");
        }
    }

    @GetMapping("/posts/{postId}/comments")
    public Result<List<Comment>> getComments(@PathVariable Long postId,
                                             @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String callerAuthor = null;
        String callerUsername = null;
        boolean isAdmin = false;
        if (authHeader != null && !authHeader.isEmpty()) {
            Map<String, Object> claims = JwtUtils.parseToken(authHeader);
            if (claims != null) {
                String nickname = (String) claims.get("nickname");
                String username = (String) claims.get("username");
                callerUsername = username;
                callerAuthor = (nickname != null && !nickname.trim().isEmpty() && !"null".equalsIgnoreCase(nickname)) ? nickname : username;
                String role = (String) claims.get("role");
                isAdmin = "ADMIN".equalsIgnoreCase(role);
            }
        }
        return Result.success(commentService.getCommentsByPostId(postId, callerAuthor, callerUsername, isAdmin));
    }

    @PostMapping("/posts/{postId}/comments")
    public Result<Comment> addComment(@PathVariable Long postId, 
                                      @RequestBody Comment comment,
                                      @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> claims = checkLogin(authHeader);
        String username = (String) claims.get("username");
        String nickname = (String) claims.get("nickname");
        String callerRole = (String) claims.get("role");
        String callerAuthor = (nickname != null && !nickname.trim().isEmpty() && !"null".equalsIgnoreCase(nickname)) ? nickname.trim() : username;
        return Result.success(commentService.addComment(postId, comment, callerRole, callerAuthor, username));
    }

    @GetMapping("/comments/recent")
    public Result<List<Comment>> getRecentComments() {
        return Result.success(commentService.getRecentComments());
    }

    @GetMapping("/comments/all")
    public Result<List<Comment>> listAllComments(@RequestParam(value = "postId", required = false) Long postId,
                                                 @RequestParam(value = "status", required = false) String status,
                                                 @RequestParam(value = "keyword", required = false) String keyword,
                                                 @RequestHeader(value = "Authorization", required = false) String authHeader) {
        checkAdmin(authHeader);
        return Result.success(commentService.listAllComments(postId, status, keyword));
    }

    @GetMapping("/comments/stats")
    public Result<Map<String, Object>> getAuditStats(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        checkAdmin(authHeader);
        return Result.success(commentService.getAuditStats());
    }

    @PutMapping("/comments/{id}/audit")
    public Result<Comment> auditComment(@PathVariable Long id,
                                        @RequestBody(required = false) Map<String, String> body,
                                        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        checkAdmin(authHeader);
        String status = body != null ? body.get("status") : "APPROVED";
        String remark = body != null ? body.get("remark") : null;
        return Result.success(commentService.auditComment(id, status, remark));
    }

    @PutMapping("/comments/{id}/top")
    public Result<Comment> toggleTopComment(@PathVariable Long id,
                                            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        checkAdmin(authHeader);
        return Result.success(commentService.toggleTopComment(id));
    }

    @DeleteMapping("/comments/{id}")
    public Result<Void> deleteComment(@PathVariable Long id,
                                      @RequestHeader(value = "Authorization", required = false) String authHeader) {
        checkAdmin(authHeader);
        commentService.deleteComment(id);
        return Result.success();
    }
}
