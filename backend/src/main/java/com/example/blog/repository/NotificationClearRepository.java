package com.example.blog.repository;

import com.example.blog.entity.NotificationClear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface NotificationClearRepository extends JpaRepository<NotificationClear, Long> {

    List<NotificationClear> findByUsername(String username);

    List<NotificationClear> findByUsernameIn(Collection<String> usernames);

    boolean existsByNotificationIdAndUsername(Long notificationId, String username);

    void deleteByNotificationId(Long notificationId);
}
