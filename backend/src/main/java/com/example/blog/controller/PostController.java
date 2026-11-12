package com.example.blog.controller;

import com.example.blog.common.JwtUtils;
import com.example.blog.common.Result;
import com.example.blog.entity.Post;
import com.example.blog.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
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

    private boolean isAdmin(String authHeader) {
        Map<String, Object> claims = JwtUtils.parseToken(authHeader);
        return claims != null && "ADMIN".equalsIgnoreCase((String) claims.get("role"));
    }

    @GetMapping
    public Result<List<Post>> listPosts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return Result.success(postService.listPosts(category, search, isAdmin(authHeader)));
    }

    @GetMapping("/{id}")
    public Result<Post> getPost(@PathVariable Long id,
                                @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return Result.success(postService.getPostById(id, isAdmin(authHeader)));
    }

    @PostMapping
    public Result<Post> createPost(@RequestBody Post post,
                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        checkAdmin(authHeader);
        Map<String, Object> claims = JwtUtils.parseToken(authHeader);
        if (claims != null) {
            String nickname = (String) claims.get("nickname");
            if (nickname != null && !nickname.trim().isEmpty()) {
                post.setAuthor(nickname);
            }
        }
        return Result.success(postService.createPost(post));
    }

    @PutMapping("/{id}")
    public Result<Post> updatePost(@PathVariable Long id, 
                                   @RequestBody Post post,
                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        checkAdmin(authHeader);
        return Result.success(postService.updatePost(id, post));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePost(@PathVariable Long id,
                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        checkAdmin(authHeader);
        postService.deletePost(id);
        return Result.success();
    }

    @PostMapping("/{id}/like")
    public Result<Integer> likePost(@PathVariable Long id) {
        return Result.success(postService.likePost(id));
    }
}

