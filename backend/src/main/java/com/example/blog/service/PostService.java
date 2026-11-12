package com.example.blog.service;

import com.example.blog.entity.Post;
import java.util.List;
import java.util.Map;

public interface PostService {
    List<Post> listPosts(String category, String search, boolean includeDrafts);
    Post getPostById(Long id, boolean includeDrafts);
    Post createPost(Post post);
    Post updatePost(Long id, Post post);
    void deletePost(Long id);
    int likePost(Long id);
    Map<String, Object> getBlogStats();
}
