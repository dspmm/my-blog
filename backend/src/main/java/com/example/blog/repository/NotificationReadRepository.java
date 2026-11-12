package com.example.blog.repository;

import com.example.blog.entity.NotificationRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationReadRepository extends JpaRepository<NotificationRead, Long> {

    List<NotificationRead> findByUsername(String username);

    Optional<NotificationRead> findByNotificationIdAndUsername(Long notificationId, String username);

    boolean existsByNotificationIdAndUsername(Long notificationId, String username);

    void deleteByNotificationId(Long notificationId);
}
