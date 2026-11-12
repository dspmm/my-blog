package com.example.blog.controller;

import com.example.blog.common.JwtUtils;
import com.example.blog.common.Result;
import com.example.blog.entity.Notification;
import com.example.blog.entity.NotificationClear;
import com.example.blog.entity.NotificationRead;
import com.example.blog.repository.NotificationClearRepository;
import com.example.blog.repository.NotificationReadRepository;
import com.example.blog.repository.NotificationRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationReadRepository notificationReadRepository;
    private final NotificationClearRepository notificationClearRepository;

    public NotificationController(NotificationRepository notificationRepository,
                                  NotificationReadRepository notificationReadRepository,
                                  NotificationClearRepository notificationClearRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationReadRepository = notificationReadRepository;
        this.notificationClearRepository = notificationClearRepository;
    }

    private String getUsernameFromHeader(String authHeader) {
        Map<String, Object> claims = getClaims(authHeader);
        return claims != null ? (String) claims.get("username") : null;
    }

    private Map<String, Object> getClaims(String authHeader) {
        if (authHeader == null || authHeader.isEmpty()) {
            return null;
        }
        return JwtUtils.parseToken(authHeader);
    }

    private List<String> getTargetUsernames(Map<String, Object> claims) {
        Set<String> set = new LinkedHashSet<>();
        set.add("ALL");
        if (claims != null) {
            String username = (String) claims.get("username");
            String nickname = (String) claims.get("nickname");
            if (username != null && !username.trim().isEmpty() && !"null".equalsIgnoreCase(username)) {
                set.add(username.trim());
            }
            if (nickname != null && !nickname.trim().isEmpty() && !"null".equalsIgnoreCase(nickname)) {
                set.add(nickname.trim());
            }
        }
        return new ArrayList<>(set);
    }

    private List<String> getSenders(Map<String, Object> claims) {
        Set<String> set = new LinkedHashSet<>();
        if (claims != null) {
            String username = (String) claims.get("username");
            String nickname = (String) claims.get("nickname");
            if (username != null && !username.trim().isEmpty() && !"null".equalsIgnoreCase(username)) {
                set.add(username.trim());
            }
            if (nickname != null && !nickname.trim().isEmpty() && !"null".equalsIgnoreCase(nickname)) {
                set.add(nickname.trim());
            }
            String role = (String) claims.get("role");
            if ("ADMIN".equalsIgnoreCase(role)) {
                set.add("小东 (系统管理员)");
                set.add("小东 (管理员)");
                set.add("小东");
                set.add("admin");
            }
        }
        return new ArrayList<>(set);
    }

    private boolean isSentByAdmin(Notification n) {
        if (n == null || n.getSender() == null) return false;
        String s = n.getSender().trim().toLowerCase();
        return s.contains("admin") || s.contains("小东") || s.contains("管理员");
    }

    private boolean isSentByCurrentUser(Notification n, Map<String, Object> claims) {
        if (n == null || n.getSender() == null || claims == null) return false;
        String sender = n.getSender().trim();
        String username = (String) claims.get("username");
        String nickname = (String) claims.get("nickname");
        if (username != null && sender.equalsIgnoreCase(username.trim())) return true;
        if (nickname != null && sender.equalsIgnoreCase(nickname.trim())) return true;
        return false;
    }

    private Set<Long> getReadIds(Map<String, Object> claims) {
        Set<Long> readIds = new HashSet<>();
        if (claims != null) {
            String username = (String) claims.get("username");
            String nickname = (String) claims.get("nickname");
            if (username != null && !username.trim().isEmpty() && !"null".equalsIgnoreCase(username)) {
                readIds.addAll(notificationReadRepository.findByUsername(username.trim()).stream()
                        .map(NotificationRead::getNotificationId).collect(Collectors.toSet()));
            }
            if (nickname != null && !nickname.trim().isEmpty() && !"null".equalsIgnoreCase(nickname) && !nickname.equals(username)) {
                readIds.addAll(notificationReadRepository.findByUsername(nickname.trim()).stream()
                        .map(NotificationRead::getNotificationId).collect(Collectors.toSet()));
            }
        }
        return readIds;
    }

    private Set<Long> getClearedIds(Map<String, Object> claims) {
        Set<Long> clearedIds = new HashSet<>();
        if (claims != null) {
            String username = (String) claims.get("username");
            String nickname = (String) claims.get("nickname");
            if (username != null && !username.trim().isEmpty() && !"null".equalsIgnoreCase(username)) {
                clearedIds.addAll(notificationClearRepository.findByUsername(username.trim()).stream()
                        .map(NotificationClear::getNotificationId).collect(Collectors.toSet()));
            }
            if (nickname != null && !nickname.trim().isEmpty() && !"null".equalsIgnoreCase(nickname) && !nickname.equals(username)) {
                clearedIds.addAll(notificationClearRepository.findByUsername(nickname.trim()).stream()
                        .map(NotificationClear::getNotificationId).collect(Collectors.toSet()));
            }
        }
        return clearedIds;
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
    public Result<List<Map<String, Object>>> listNotifications(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> claims = getClaims(authHeader);
        boolean isAdmin = claims != null && "ADMIN".equalsIgnoreCase((String) claims.get("role"));

        // 严格遵循账号权限隔离：无论是管理员还是普通读者，个人通知中心均拉取定向属于自己的消息、全站公告与业务待办，杜绝泄露读者间的互动
        List<Notification> list = notificationRepository.findUserNotifications(
                getTargetUsernames(claims), getSenders(claims), isAdmin);
        Set<Long> readIds = getReadIds(claims);
        Set<Long> clearedIds = getClearedIds(claims);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Notification n : list) {
            if (clearedIds.contains(n.getId())) {
                continue;
            }
            Map<String, Object> item = new HashMap<>();
            item.put("id", n.getId());
            item.put("title", n.getTitle());
            item.put("content", n.getContent());
            item.put("type", n.getType());
            item.put("targetUsername", n.getTargetUsername());
            item.put("postId", n.getPostId());
            item.put("commentId", n.getCommentId());
            item.put("sender", n.getSender());
            item.put("createdAt", n.getCreatedAt());

            boolean isRead;
            boolean isResolvedAuditNotice = isAdmin && "AUDIT".equalsIgnoreCase(n.getType()) && "admin".equalsIgnoreCase(n.getTargetUsername())
                    && n.getTitle() != null && (n.getTitle().contains("审核记录") || n.getTitle().contains("已通过") || n.getTitle().contains("已驳回") || n.getTitle().contains("已处理"));
            if (isAdmin) {
                // 管理员自己发出的公告和私信自动视为已读，已结案的审核记录自动视为已读，绝不出现红点
                isRead = isSentByAdmin(n) || isResolvedAuditNotice || readIds.contains(n.getId());
            } else {
                // 读者自己提交的反馈默认视为已读
                isRead = isSentByCurrentUser(n, claims) || readIds.contains(n.getId());
            }
            item.put("isRead", isRead);
            result.add(item);
        }

        return Result.success(result);
    }

    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> claims = getClaims(authHeader);
        boolean isAdmin = claims != null && "ADMIN".equalsIgnoreCase((String) claims.get("role"));

        // 严格遵循账号权限隔离：无论是管理员还是普通读者，个人通知中心均拉取定向属于自己的消息、全站公告与业务待办，杜绝泄露读者间的互动
        List<Notification> list = notificationRepository.findUserNotifications(
                getTargetUsernames(claims), getSenders(claims), isAdmin);
        Set<Long> readIds = getReadIds(claims);
        Set<Long> clearedIds = getClearedIds(claims);

        int unread = 0;
        for (Notification n : list) {
            if (clearedIds.contains(n.getId())) {
                continue;
            }
            if (isAdmin && isSentByAdmin(n)) {
                // 管理员自己发出的通知绝不统计为未读
                continue;
            }
            if (isAdmin && "AUDIT".equalsIgnoreCase(n.getType()) && "admin".equalsIgnoreCase(n.getTargetUsername())
                    && n.getTitle() != null && (n.getTitle().contains("审核记录") || n.getTitle().contains("已通过") || n.getTitle().contains("已驳回") || n.getTitle().contains("已处理"))) {
                // 已经由管理员完成审核处置的历史审核记录不再统计为未读
                continue;
            }
            if (!isAdmin && isSentByCurrentUser(n, claims)) {
                // 读者自己提交的反馈不统计为未读
                continue;
            }
            if (!readIds.contains(n.getId())) {
                unread++;
            }
        }
        return Result.success(unread);
    }

    @PostMapping("/{id}/read")
    @Transactional
    public Result<Void> markAsRead(@PathVariable Long id,
                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String username = getUsernameFromHeader(authHeader);
        if (username == null || username.trim().isEmpty()) {
            return Result.success();
        }

        if (!notificationReadRepository.existsByNotificationIdAndUsername(id, username)) {
            NotificationRead nr = new NotificationRead(id, username);
            notificationReadRepository.save(nr);
        }
        return Result.success();
    }

    @PostMapping("/read-all")
    @Transactional
    public Result<Void> markAllAsRead(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> claims = getClaims(authHeader);
        String username = claims != null ? (String) claims.get("username") : null;
        if (username == null || username.trim().isEmpty()) {
            return Result.success();
        }

        boolean isAdmin = "ADMIN".equalsIgnoreCase((String) claims.get("role"));
        // 严格遵循账号权限隔离：无论是管理员还是普通读者，个人通知中心均拉取定向属于自己的消息、全站公告与业务待办，杜绝泄露读者间的互动
        List<Notification> list = notificationRepository.findUserNotifications(
                getTargetUsernames(claims), getSenders(claims), isAdmin);
        Set<Long> readIds = getReadIds(claims);

        List<NotificationRead> newReads = new ArrayList<>();
        for (Notification n : list) {
            if (isAdmin && isSentByAdmin(n)) continue;
            if (!isAdmin && isSentByCurrentUser(n, claims)) continue;
            if (!readIds.contains(n.getId())) {
                newReads.add(new NotificationRead(n.getId(), username));
            }
        }
        if (!newReads.isEmpty()) {
            notificationReadRepository.saveAll(newReads);
        }

        return Result.success();
    }

    @PostMapping("/feedback")
    @Transactional
    public Result<?> submitFeedback(@RequestBody Map<String, Object> req,
                                    @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> claims = getClaims(authHeader);
        if (claims == null) {
            throw new RuntimeException("请先登录读者账号后再提交改进建议");
        }

        String username = (String) claims.get("username");
        String nickname = (String) claims.get("nickname");
        String author = (nickname != null && !nickname.trim().isEmpty() && !"null".equalsIgnoreCase(nickname)) ? nickname : username;

        String category = (String) req.get("category");
        String title = (String) req.get("title");
        String content = (String) req.get("content");

        if (category == null || category.trim().isEmpty()) {
            category = "功能建议";
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("建议主题不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("详细建议内容不能为空");
        }

        String noticeTitle = String.format("💡【%s】%s", category.trim(), title.trim());
        String noticeContent = String.format("来自读者「%s」（账号: %s）的改进建议：\n\n类别：%s\n\n详细说明：\n%s",
                author, username, category.trim(), content.trim());

        Notification feedbackNotice = new Notification(noticeTitle, noticeContent, "FEEDBACK", "admin");
        feedbackNotice.setSender(author);
        Notification saved = notificationRepository.save(feedbackNotice);

        return Result.success(saved);
    }

    @PostMapping
    @Transactional
    public Result<?> createNotification(@RequestBody Map<String, Object> req,
                                        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        checkAdmin(authHeader);
        String title = (String) req.get("title");
        String content = (String) req.get("content");
        String type = (String) req.get("type");
        String targetUsername = (String) req.get("targetUsername");
        Object targetUsernamesObj = req.get("targetUsernames");

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("通知标题不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("通知内容不能为空");
        }
        if (type == null || type.trim().isEmpty()) {
            type = "ANNOUNCEMENT";
        }

        // 解析指定接收人列表
        List<String> targetList = new ArrayList<>();
        if (targetUsernamesObj instanceof List) {
            for (Object item : (List<?>) targetUsernamesObj) {
                if (item != null && !item.toString().trim().isEmpty()) {
                    targetList.add(item.toString().trim());
                }
            }
        } else if (targetUsername != null && !targetUsername.trim().isEmpty()) {
            for (String u : targetUsername.split(",")) {
                if (!u.trim().isEmpty()) targetList.add(u.trim());
            }
        }

        // 若为公告，或者包含 ALL，默认全站所有人可见
        if ("ANNOUNCEMENT".equalsIgnoreCase(type) || targetList.contains("ALL")) {
            Notification n = new Notification(title, content, type, "ALL");
            n.setSender("小东 (系统管理员)");
            return Result.success(notificationRepository.save(n));
        }

        // 若为普通通知但未选任何目标用户
        if (targetList.isEmpty()) {
            throw new IllegalArgumentException("请选择接收通知的目标用户");
        }

        // 为多选的每位用户分别创建并保存定向私信/通知
        List<Notification> toSave = new ArrayList<>();
        for (String u : targetList) {
            Notification n = new Notification(title, content, type, u);
            n.setSender("小东 (系统管理员)");
            toSave.add(n);
        }
        List<Notification> saved = notificationRepository.saveAll(toSave);
        return Result.success(saved.isEmpty() ? null : saved.get(0));
    }

    /**
     * 按当前分类一键清理已读通知（未读通知受保护不可清理）
     */
    @PostMapping("/clear-read")
    @Transactional
    public Result<Map<String, Object>> clearReadNotifications(
            @RequestParam(value = "category", required = false) String category,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> claims = getClaims(authHeader);
        if (claims == null) {
            throw new RuntimeException("请先登录后再清理通知");
        }
        String username = (String) claims.get("username");
        boolean isAdmin = "ADMIN".equalsIgnoreCase((String) claims.get("role"));

        List<Notification> list = notificationRepository.findUserNotifications(
                getTargetUsernames(claims), getSenders(claims), isAdmin);
        Set<Long> readIds = getReadIds(claims);
        Set<Long> clearedIds = getClearedIds(claims);

        // 严格仅清理已读且尚未清理的通知（未读绝对不能清理）
        List<Notification> readNotices = list.stream()
                .filter(n -> !clearedIds.contains(n.getId()))
                .filter(n -> {
                    boolean isRead = (isAdmin && isSentByAdmin(n))
                            || (!isAdmin && isSentByCurrentUser(n, claims))
                            || readIds.contains(n.getId());
                    return isRead;
                })
                .collect(Collectors.toList());

        // 按指定分类标签过滤
        if (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category.trim())) {
            String cat = category.trim().toUpperCase();
            if ("ANNOUNCEMENT".equals(cat)) {
                readNotices = readNotices.stream().filter(n -> "ANNOUNCEMENT".equalsIgnoreCase(n.getType())).collect(Collectors.toList());
            } else if ("REPLY".equals(cat)) {
                readNotices = readNotices.stream().filter(n -> "REPLY".equalsIgnoreCase(n.getType())).collect(Collectors.toList());
            } else if ("PRIVATE".equals(cat)) {
                readNotices = readNotices.stream().filter(n -> "NOTICE".equalsIgnoreCase(n.getType()) || "SYSTEM".equalsIgnoreCase(n.getType())).collect(Collectors.toList());
            } else if ("AUDIT".equals(cat)) {
                readNotices = readNotices.stream().filter(n -> "AUDIT".equalsIgnoreCase(n.getType())).collect(Collectors.toList());
            } else if ("FEEDBACK".equals(cat)) {
                readNotices = readNotices.stream().filter(n -> "FEEDBACK".equalsIgnoreCase(n.getType())).collect(Collectors.toList());
            }
        }

        int count = 0;
        List<NotificationClear> newClears = new ArrayList<>();
        for (Notification n : readNotices) {
            count++;
            if (!notificationClearRepository.existsByNotificationIdAndUsername(n.getId(), username)) {
                newClears.add(new NotificationClear(n.getId(), username));
            }
        }
        if (!newClears.isEmpty()) {
            notificationClearRepository.saveAll(newClears);
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("clearedCount", count);
        return Result.success(resp);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> deleteNotification(@PathVariable Long id,
                                           @RequestHeader(value = "Authorization", required = false) String authHeader) {
        checkAdmin(authHeader);
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("通知记录不存在，ID: " + id);
        }
        notificationReadRepository.deleteByNotificationId(id);
        notificationClearRepository.deleteByNotificationId(id);
        notificationRepository.deleteById(id);
        return Result.success();
    }
}
