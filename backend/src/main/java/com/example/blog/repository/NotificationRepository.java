package com.example.blog.repository;

import com.example.blog.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByOrderByCreatedAtDesc();

    List<Notification> findByTargetUsernameInOrderByCreatedAtDesc(Collection<String> targetUsernames);

    // 综合通知拉取：
    // 1. 目标接收人匹配当前用户 (targets: ALL, 当前username, 当前nickname)
    // 2. OR 读者自己提交的意见建议 (FEEDBACK 且 sender IN :senders)
    // 3. OR 管理员自己发送的历史公告与私信 (仅当 isAdmin = true 且 sender IN :senders，坚决排除审核回执 AUDIT)
    @Query("SELECT n FROM Notification n WHERE n.targetUsername IN :targets OR (n.type = 'FEEDBACK' AND :senders IS NOT NULL AND n.sender IN :senders) OR (:isAdmin = true AND :senders IS NOT NULL AND n.sender IN :senders AND n.type != 'AUDIT') ORDER BY n.createdAt DESC")
    List<Notification> findUserNotifications(
            @Param("targets") Collection<String> targets,
            @Param("senders") Collection<String> senders,
            @Param("isAdmin") boolean isAdmin);
}
