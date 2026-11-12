package com.example.blog.repository;

import com.example.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findByStatusOrderByCreatedAtDesc(String status);

    List<Post> findByCategoryNameOrderByCreatedAtDesc(String categoryName);

    List<Post> findByCategoryNameAndStatusOrderByCreatedAtDesc(String categoryName, String status);

    long countByStatus(String status);

    long countByCategoryName(String categoryName);

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Post p SET p.categoryName = :newName WHERE p.categoryName = :oldName")
    int updateCategoryName(@Param("oldName") String oldName, @Param("newName") String newName);

    @Query("SELECT p FROM Post p WHERE " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY p.createdAt DESC")
    List<Post> searchPosts(@Param("keyword") String keyword);

    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY p.createdAt DESC")
    List<Post> searchPublishedPosts(@Param("keyword") String keyword);

    @Query("SELECT p FROM Post p WHERE p.categoryName = :categoryName AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY p.createdAt DESC")
    List<Post> searchPostsWithCategory(@Param("categoryName") String categoryName, @Param("keyword") String keyword);

    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND p.categoryName = :categoryName AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY p.createdAt DESC")
    List<Post> searchPublishedPostsWithCategory(@Param("categoryName") String categoryName, @Param("keyword") String keyword);

    @Query("SELECT COALESCE(SUM(p.views), 0) FROM Post p")
    Long sumViews();

    @Query("SELECT COALESCE(SUM(p.views), 0) FROM Post p WHERE p.status = 'PUBLISHED'")
    Long sumPublishedViews();

    @Query("SELECT COALESCE(SUM(p.likes), 0) FROM Post p")
    Long sumLikes();

    @Query("SELECT COALESCE(SUM(p.likes), 0) FROM Post p WHERE p.status = 'PUBLISHED'")
    Long sumPublishedLikes();
}
