package com.example.blog.controller;

import com.example.blog.common.Result;
import com.example.blog.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class StatsController {

    private final PostService postService;

    public StatsController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Result<Map<String, Object>> getStats() {
        return Result.success(postService.getBlogStats());
    }
}
