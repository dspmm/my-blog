package com.example.blog.service.impl;

import com.example.blog.entity.Post;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.service.PostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;

    public PostServiceImpl(PostRepository postRepository, CategoryRepository categoryRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> listPosts(String category, String search, boolean includeDrafts) {
        boolean hasCategory = category != null && !category.trim().isEmpty() && !"全部".equals(category);
        boolean hasSearch = search != null && !search.trim().isEmpty();

        if (hasCategory && hasSearch) {
            return includeDrafts
                    ? postRepository.searchPostsWithCategory(category.trim(), search.trim())
                    : postRepository.searchPublishedPostsWithCategory(category.trim(), search.trim());
        } else if (hasCategory) {
            return includeDrafts
                    ? postRepository.findByCategoryNameOrderByCreatedAtDesc(category.trim())
                    : postRepository.findByCategoryNameAndStatusOrderByCreatedAtDesc(category.trim(), "PUBLISHED");
        } else if (hasSearch) {
            return includeDrafts
                    ? postRepository.searchPosts(search.trim())
                    : postRepository.searchPublishedPosts(search.trim());
        } else {
            return includeDrafts
                    ? postRepository.findAllByOrderByCreatedAtDesc()
                    : postRepository.findByStatusOrderByCreatedAtDesc("PUBLISHED");
        }
    }

    @Override
    @Transactional
    public Post getPostById(Long id, boolean includeDrafts) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在，ID: " + id));
        if (!includeDrafts && !"PUBLISHED".equalsIgnoreCase(post.getStatus())) {
            throw new RuntimeException("文章不存在，ID: " + id);
        }
        post.setViews(post.getViews() + 1);
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post createPost(Post post) {
        if (post.getTitle() == null || post.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("文章标题不能为空");
        }
        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("文章正文内容不能为空");
        }
        if (post.getSummary() == null || post.getSummary().trim().isEmpty()) {
            String content = post.getContent().replaceAll("[#*`>-]", "");
            post.setSummary(content.length() > 120 ? content.substring(0, 120) + "..." : content);
        }
        if (post.getReadTime() == null || post.getReadTime().trim().isEmpty()) {
            int minutes = Math.max(2, Math.round(post.getContent().length() / 350f));
            post.setReadTime(minutes + " min");
        }
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post updatePost(Long id, Post post) {
        Post existing = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在，ID: " + id));
        if (post.getTitle() != null) existing.setTitle(post.getTitle());
        if (post.getSummary() != null) existing.setSummary(post.getSummary());
        if (post.getContent() != null) {
            existing.setContent(post.getContent());
            int minutes = Math.max(2, Math.round(post.getContent().length() / 350f));
            existing.setReadTime(minutes + " min");
        }
        if (post.getCategoryName() != null) existing.setCategoryName(post.getCategoryName());
        if (post.getTags() != null) existing.setTags(post.getTags());
        if (post.getAuthor() != null) existing.setAuthor(post.getAuthor());
        if (post.getStatus() != null) existing.setStatus(post.getStatus());
        existing.setUpdatedAt(java.time.LocalDateTime.now());
        return postRepository.save(existing);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("文章不存在，ID: " + id);
        }
        commentRepository.deleteByPostId(id);
        postRepository.deleteById(id);
    }

    @Override
    @Transactional
    public int likePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在，ID: " + id));
        if (!"PUBLISHED".equalsIgnoreCase(post.getStatus())) {
            throw new RuntimeException("文章不存在，ID: " + id);
        }
        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);
        return post.getLikes();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getBlogStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPosts", postRepository.countByStatus("PUBLISHED"));
        stats.put("totalViews", postRepository.sumPublishedViews());
        stats.put("totalLikes", postRepository.sumPublishedLikes());
        stats.put("totalCategories", categoryRepository.count());
        return stats;
    }
}
