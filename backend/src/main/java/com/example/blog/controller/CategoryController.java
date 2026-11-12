package com.example.blog.controller;

import com.example.blog.common.JwtUtils;
import com.example.blog.common.Result;
import com.example.blog.entity.Category;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.PostRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    public CategoryController(CategoryRepository categoryRepository, PostRepository postRepository) {
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
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

    @GetMapping
    public Result<List<Category>> listCategories() {
        return Result.success(categoryRepository.findAll());
    }

    @PostMapping
    public Result<Category> createCategory(@RequestBody Category category,
                                           @RequestHeader(value = "Authorization", required = false) String authHeader) {
        checkAdmin(authHeader);
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("专栏名称不能为空");
        }
        if (categoryRepository.findByName(category.getName().trim()).isPresent()) {
            throw new IllegalArgumentException("专栏名称已存在，请勿重复创建");
        }
        category.setName(category.getName().trim());
        if (category.getIcon() == null || category.getIcon().trim().isEmpty()) {
            category.setIcon("📑");
        }
        return Result.success(categoryRepository.save(category));
    }

    @PutMapping("/{id}")
    @Transactional
    public Result<Category> updateCategory(@PathVariable Long id,
                                           @RequestBody Category req,
                                           @RequestHeader(value = "Authorization", required = false) String authHeader) {
        checkAdmin(authHeader);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("专栏不存在，ID: " + id));

        String oldName = category.getName();
        String newName = req.getName() != null ? req.getName().trim() : oldName;

        if (!oldName.equals(newName)) {
            if (categoryRepository.findByName(newName).isPresent()) {
                throw new IllegalArgumentException("新专栏名称已存在，请更换名称");
            }
            category.setName(newName);
            // 级联更新 post 表中所有原分类名称
            postRepository.updateCategoryName(oldName, newName);
        }

        if (req.getIcon() != null && !req.getIcon().trim().isEmpty()) {
            category.setIcon(req.getIcon().trim());
        }
        if (req.getDescription() != null) {
            category.setDescription(req.getDescription());
        }

        return Result.success(categoryRepository.save(category));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> deleteCategory(@PathVariable Long id,
                                       @RequestHeader(value = "Authorization", required = false) String authHeader) {
        checkAdmin(authHeader);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("专栏不存在，ID: " + id));

        long postCount = postRepository.countByCategoryName(category.getName());
        if (postCount > 0) {
            throw new RuntimeException("无法删除：该专栏下仍有 " + postCount + " 篇技术博客文章，请先迁移或删除文章后再操作！");
        }

        categoryRepository.delete(category);
        return Result.success();
    }
}
