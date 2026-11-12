package com.example.blog.repository;

import com.example.blog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByIsTopDescCreatedAtDesc(Long postId);
    List<Comment> findTop5ByStatusOrderByCreatedAtDesc(String status);
    List<Comment> findAllByOrderByIsTopDescCreatedAtDesc();
    long countByPostId(Long postId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.postId = :postId")
    int deleteByPostId(@Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.parentId = :parentId")
    int deleteRepliesByParentId(@Param("parentId") Long parentId);

    // 前台读者端文章评论可见性查询：已通过公开评论 + 当前读者自己发布的待审核评论 (严格排除已驳回 REJECTED 评论)
    @Query("SELECT c FROM Comment c WHERE c.postId = :postId AND (c.status = 'APPROVED' OR (c.status = 'PENDING' AND ((:author IS NOT NULL AND c.author = :author) OR (:username IS NOT NULL AND c.username = :username)))) ORDER BY c.isTop DESC, c.createdAt DESC")
    List<Comment> findVisibleCommentsForUser(@Param("postId") Long postId, @Param("author") String author, @Param("username") String username);

    // 前台文章评论非驳回列表 (供博主查看文章评论区，严格排除已驳回 REJECTED 评论)
    @Query("SELECT c FROM Comment c WHERE c.postId = :postId AND c.status != 'REJECTED' ORDER BY c.isTop DESC, c.createdAt DESC")
    List<Comment> findNonRejectedByPostId(@Param("postId") Long postId);

    // 后台管理多条件筛选查询
    List<Comment> findByStatusOrderByIsTopDescCreatedAtDesc(String status);
    List<Comment> findByPostIdAndStatusOrderByIsTopDescCreatedAtDesc(Long postId, String status);
    long countByStatus(String status);
}
