package com.example.blog.service.impl;

import com.example.blog.entity.Comment;
import com.example.blog.entity.Notification;
import com.example.blog.entity.NotificationRead;
import com.example.blog.entity.Post;
import com.example.blog.entity.SysUser;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.NotificationReadRepository;
import com.example.blog.repository.NotificationRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.SysUserRepository;
import com.example.blog.service.CommentService;
import com.example.blog.service.SensitiveWordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final SensitiveWordService sensitiveWordService;
    private final NotificationRepository notificationRepository;
    private final NotificationReadRepository notificationReadRepository;
    private final SysUserRepository sysUserRepository;

    public CommentServiceImpl(CommentRepository commentRepository,
                              PostRepository postRepository,
                              SensitiveWordService sensitiveWordService,
                              NotificationRepository notificationRepository,
                              NotificationReadRepository notificationReadRepository,
                              SysUserRepository sysUserRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.sensitiveWordService = sensitiveWordService;
        this.notificationRepository = notificationRepository;
        this.notificationReadRepository = notificationReadRepository;
        this.sysUserRepository = sysUserRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(Long postId, String callerAuthor, String callerUsername, boolean isAdmin) {
        if (isAdmin) {
            // 博主在文章前台查看评论：排除已驳回 REJECTED 评论
            return commentRepository.findNonRejectedByPostId(postId);
        }
        // 普通读者与访客：仅展示已通过 APPROVED + 读者本人正在等待2级机审的 PENDING 评论；彻底隐藏已驳回 REJECTED 评论
        return commentRepository.findVisibleCommentsForUser(postId, callerAuthor, callerUsername);
    }

    @Override
    @Transactional
    public Comment addComment(Long postId, Comment comment, String callerRole, String callerAuthor, String callerUsername) {
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("评论失败，文章不存在，ID: " + postId);
        }
        if (callerUsername == null || callerUsername.trim().isEmpty()) {
            throw new RuntimeException("访客暂无评论权限，请先登录读者账号后再发表评论");
        }
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("评论内容不能为空");
        }
        comment.setPostId(postId);

        // 作者身份对齐与账号追溯（强制与登录账号绑定，杜绝匿名与伪造）
        String finalAuthor = (callerAuthor != null && !callerAuthor.trim().isEmpty() && !"null".equalsIgnoreCase(callerAuthor))
                ? callerAuthor.trim()
                : callerUsername.trim();
        comment.setAuthor(finalAuthor);
        comment.setUsername(callerUsername.trim());

        if (comment.getIsTop() == null) {
            comment.setIsTop(false);
        }

        // ================= 楼中楼两级父级评论归一化校验 =================
        if (comment.getParentId() != null && comment.getParentId() > 0) {
            Comment parentComment = commentRepository.findById(comment.getParentId())
                    .orElseThrow(() -> new RuntimeException("回复失败：目标评论不存在或已被删除，ID: " + comment.getParentId()));
            if (!postId.equals(parentComment.getPostId())) {
                throw new IllegalArgumentException("回复失败：目标评论不属于当前文章");
            }
            if (!"APPROVED".equalsIgnoreCase(parentComment.getStatus())) {
                throw new RuntimeException("回复失败：未通过审核的评论不可回复");
            }
            // 若回复的目标本身为子评论，归一化锁定其根评论 ID，确保严格扁平两级楼中楼结构
            if (parentComment.getParentId() != null && parentComment.getParentId() > 0) {
                comment.setParentId(parentComment.getParentId());
            } else {
                comment.setParentId(parentComment.getId());
            }
            if (comment.getReplyTo() == null || comment.getReplyTo().trim().isEmpty()) {
                comment.setReplyTo(parentComment.getAuthor());
            }
            if (comment.getReplyToUsername() == null || comment.getReplyToUsername().trim().isEmpty()) {
                comment.setReplyToUsername(parentComment.getUsername());
            }
        } else {
            comment.setParentId(null);
            comment.setReplyTo(null);
            comment.setReplyToUsername(null);
        }

        // ================= 1级自动机审过滤与角色豁免 =================
        boolean isAdmin = "ADMIN".equalsIgnoreCase(callerRole);
        if (isAdmin) {
            // 博主/管理员免审，直接发布
            comment.setStatus("APPROVED");
            comment.setSensitiveWords(null);
            comment.setRole("ADMIN");
            comment.setAvatar("👑");
        } else {
            // 普通读者：执行 1 级敏感词自动扫描
            List<String> matchedWords = sensitiveWordService.findSensitiveWords(comment.getContent());
            if (!matchedWords.isEmpty()) {
                // 命中敏感词：拦截进入 2 级待人工审核队列
                comment.setStatus("PENDING");
                comment.setSensitiveWords(String.join(", ", matchedWords));
            } else {
                // 1级自动机审通过，直接上线
                comment.setStatus("APPROVED");
                comment.setSensitiveWords(null);
            }
            if (comment.getRole() == null || comment.getRole().trim().isEmpty()) {
                comment.setRole("USER");
            }
            if (comment.getAvatar() == null || comment.getAvatar().trim().isEmpty()) {
                String userAvatar = sysUserRepository.findByUsername(callerUsername.trim())
                        .map(SysUser::getAvatar)
                        .orElse(null);
                comment.setAvatar((userAvatar != null && !userAvatar.trim().isEmpty()) ? userAvatar.trim() : "👨‍💻");
            }
        }

        Comment saved = commentRepository.save(comment);

        // 仅当读者评论命中敏感词进入待审队列时，向博主发送一条 2 级人工审核待办通知
        if ("PENDING".equals(saved.getStatus())) {
            try {
                String postTitle = postRepository.findById(saved.getPostId()).map(Post::getTitle).orElse("博客文章");
                String auditTitle = "⚖️ 待审核提醒：新评论命中敏感词需人工审核";
                String auditContent = String.format("博主您好：\n\n读者「%s」在文章《%s》下发表了评论，触发 1 级安全机审。\n\n命中敏感词：%s\n\n评论内容：\n> %s\n\n请前往管理后台【评论审核】待审核队列处理。",
                        saved.getAuthor(),
                        postTitle,
                        saved.getSensitiveWords(),
                        saved.getContent().length() > 60 ? saved.getContent().substring(0, 60) + "..." : saved.getContent());

                Notification auditNotice = new Notification(auditTitle, auditContent, "AUDIT", "admin", saved.getPostId(), saved.getId());
                auditNotice.setSender(saved.getAuthor() != null ? saved.getAuthor() : "读者");
                notificationRepository.save(auditNotice);
                log.info("已生成博主 2 级人工审核待办通知: commentId={}, author={}", saved.getId(), saved.getAuthor());
            } catch (Exception e) {
                log.error("生成博主待审核通知失败: ", e);
            }
        } else if ("APPROVED".equals(saved.getStatus())) {
            // 审核通过且属于回复他人，自动触发回复站内信通知
            sendReplyNotification(saved);
        }

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getRecentComments() {
        return commentRepository.findTop5ByStatusOrderByCreatedAtDesc("APPROVED");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> listAllComments(Long postId, String status, String keyword) {
        List<Comment> list;
        boolean hasPost = postId != null;
        boolean hasStatus = status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status);

        if (hasPost && hasStatus) {
            list = commentRepository.findByPostIdAndStatusOrderByIsTopDescCreatedAtDesc(postId, status.trim().toUpperCase());
        } else if (hasPost) {
            list = commentRepository.findByPostIdOrderByIsTopDescCreatedAtDesc(postId);
        } else if (hasStatus) {
            list = commentRepository.findByStatusOrderByIsTopDescCreatedAtDesc(status.trim().toUpperCase());
        } else {
            list = commentRepository.findAllByOrderByIsTopDescCreatedAtDesc();
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim().toLowerCase();
            list = list.stream().filter(c ->
                    (c.getContent() != null && c.getContent().toLowerCase().contains(kw)) ||
                    (c.getAuthor() != null && c.getAuthor().toLowerCase().contains(kw)) ||
                    (c.getSensitiveWords() != null && c.getSensitiveWords().toLowerCase().contains(kw))
            ).collect(Collectors.toList());
        }

        return list;
    }

    @Override
    @Transactional
    public Comment auditComment(Long id, String newStatus, String remark) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("评论不存在，ID: " + id));

        String upperStatus = newStatus != null ? newStatus.trim().toUpperCase() : "APPROVED";
        if (!"APPROVED".equals(upperStatus) && !"REJECTED".equals(upperStatus) && !"PENDING".equals(upperStatus)) {
            throw new IllegalArgumentException("无效的审核操作状态: " + newStatus);
        }

        comment.setStatus(upperStatus);
        Comment saved = commentRepository.save(comment);

        // 触发 2 级审核结果读者站内定向私信通知 (仅在通过 APPROVED 或驳回 REJECTED 时发送)
        if ("APPROVED".equals(upperStatus) || "REJECTED".equals(upperStatus)) {
            try {
                String postTitle = postRepository.findById(comment.getPostId()).map(Post::getTitle).orElse("博客文章");
                String noticeTitle;
                String noticeContent;

                if ("APPROVED".equals(upperStatus)) {
                    noticeTitle = "🎉 您的评论已通过博主审核！";
                    noticeContent = String.format("亲爱的读者：\n\n您在《%s》下发表的评论已通过博主人工审核，现已全站公开展示。\n\n评论摘要：\n> %s",
                            postTitle,
                            comment.getContent().length() > 60 ? comment.getContent().substring(0, 60) + "..." : comment.getContent());
                    // 补充触发对原作者的回复通知
                    sendReplyNotification(saved);
                } else {
                    noticeTitle = "⚠️ 您的评论未能通过博主审核";
                    noticeContent = String.format("亲爱的读者：\n\n您在《%s》下发表的评论未通过审核。\n\n驳回说明：%s\n\n评论内容：\n> %s",
                            postTitle,
                            remark != null && !remark.trim().isEmpty() ? remark : "内容涉及违规敏感词汇或广告灌水",
                            comment.getContent().length() > 60 ? comment.getContent().substring(0, 60) + "..." : comment.getContent());
                }

                // 精准解析接收私信的目标用户账号 targetUsername
                String targetUsername = null;
                if (comment.getUsername() != null && !comment.getUsername().trim().isEmpty() && !"null".equalsIgnoreCase(comment.getUsername())) {
                    targetUsername = comment.getUsername().trim();
                } else if (comment.getAuthor() != null && !comment.getAuthor().trim().isEmpty()) {
                    String author = comment.getAuthor().trim();
                    Optional<SysUser> matchedUser = sysUserRepository.findByUsername(author)
                            .or(() -> sysUserRepository.findFirstByNickname(author));
                    if (matchedUser.isPresent()) {
                        targetUsername = matchedUser.get().getUsername();
                    } else if (!"访客".equals(author) && !"匿名访客".equals(author) && !"热心技术网友".equals(author)) {
                        targetUsername = author;
                    }
                }

                if (targetUsername != null && !targetUsername.trim().isEmpty()) {
                    Notification notice = new Notification(noticeTitle, noticeContent, "AUDIT", targetUsername, comment.getPostId(), comment.getId());
                    notice.setSender("小东 (管理员)");
                    notificationRepository.save(notice);
                    log.info("评论审核结果定向通知已成功发送: commentId={}, status={}, targetUser={}", comment.getId(), upperStatus, targetUsername);
                } else {
                    log.info("评论审核完成，但作者为匿名游客，跳过站内私信发送: commentId={}, author={}", comment.getId(), comment.getAuthor());
                }

                // 联动更新博主此前收到的待审核任务通知，将其标题与内容更新为“已处理”，彻底阻断失效的前往审核按钮
                try {
                    List<Notification> adminAuditNotices = notificationRepository.findAll().stream()
                            .filter(n -> "AUDIT".equals(n.getType()) && "admin".equalsIgnoreCase(n.getTargetUsername()))
                            .filter(n -> (n.getCommentId() != null && n.getCommentId().equals(comment.getId()))
                                    || (n.getTitle() != null && n.getTitle().contains("待审核") && n.getContent() != null && comment.getContent() != null && n.getContent().contains(comment.getContent().substring(0, Math.min(15, comment.getContent().length())))))
                            .collect(Collectors.toList());
                    for (Notification an : adminAuditNotices) {
                        if ("APPROVED".equals(upperStatus)) {
                            an.setTitle(String.format("⚖️ 审核记录：读者「%s」的评论已审核通过", comment.getAuthor()));
                        } else {
                            an.setTitle(String.format("⚖️ 审核记录：读者「%s」的评论已驳回", comment.getAuthor()));
                        }
                        // 既然已经完成人工审核处理，该待办提醒在业务上已闭环，同步为管理员标记已读
                        if (!notificationReadRepository.existsByNotificationIdAndUsername(an.getId(), "admin")) {
                            notificationReadRepository.save(new NotificationRead(an.getId(), "admin"));
                        }
                    }
                    if (!adminAuditNotices.isEmpty()) {
                        notificationRepository.saveAll(adminAuditNotices);
                    }
                } catch (Exception ex) {
                    log.warn("联动更新博主待审核通知状态异常: ", ex);
                }
            } catch (Exception e) {
                log.error("发送评论审核定向私信通知异常: commentId=" + comment.getId(), e);
            }
        }

        return saved;
    }

    @Override
    @Transactional
    public Comment toggleTopComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("评论不存在，ID: " + id));
        if (!"APPROVED".equalsIgnoreCase(comment.getStatus())) {
            throw new RuntimeException("操作失败：未通过审核的评论不可置顶");
        }
        boolean currentTop = Boolean.TRUE.equals(comment.getIsTop());
        comment.setIsTop(!currentTop);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new RuntimeException("评论不存在，ID: " + id);
        }
        commentRepository.deleteRepliesByParentId(id);
        commentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAuditStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", commentRepository.count());
        stats.put("pending", commentRepository.countByStatus("PENDING"));
        stats.put("approved", commentRepository.countByStatus("APPROVED"));
        stats.put("rejected", commentRepository.countByStatus("REJECTED"));
        return stats;
    }

    /**
     * 发送评论定向回复站内信通知
     */
    private void sendReplyNotification(Comment comment) {
        try {
            if (comment == null || comment.getReplyToUsername() == null || comment.getReplyToUsername().trim().isEmpty()) {
                return;
            }
            String targetUser = comment.getReplyToUsername().trim();
            // 避免自己回复自己时发送通知
            if (comment.getUsername() != null && targetUser.equalsIgnoreCase(comment.getUsername().trim())) {
                return;
            }
            String postTitle = postRepository.findById(comment.getPostId()).map(Post::getTitle).orElse("技术长文");
            String noticeTitle = String.format("💬「%s」回复了您的评论", comment.getAuthor());
            String noticeContent = String.format("亲爱的读者：\n\n读者「%s」在文章《%s》下回复了您的评论：\n\n> %s\n\n快前往博客文章互动吧！",
                    comment.getAuthor(),
                    postTitle,
                    comment.getContent().length() > 60 ? comment.getContent().substring(0, 60) + "..." : comment.getContent());

            Notification replyNotice = new Notification(noticeTitle, noticeContent, "REPLY", targetUser, comment.getPostId(), comment.getId());
            replyNotice.setSender(comment.getAuthor() != null ? comment.getAuthor() : "读者");
            notificationRepository.save(replyNotice);
            log.info("评论回复定向站内信通知已成功发送: commentId={}, fromUser={}, toUser={}", comment.getId(), comment.getAuthor(), targetUser);
        } catch (Exception e) {
            log.error("发送评论回复定向站内信通知异常: commentId=" + (comment != null ? comment.getId() : "null"), e);
        }
    }
}
