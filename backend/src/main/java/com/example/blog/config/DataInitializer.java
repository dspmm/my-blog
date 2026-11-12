package com.example.blog.config;

import com.example.blog.common.SecurityUtils;
import com.example.blog.entity.Category;
import com.example.blog.entity.Notification;
import com.example.blog.entity.Post;
import com.example.blog.entity.SysUser;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.NotificationRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.SysUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final SysUserRepository sysUserRepository;
    private final NotificationRepository notificationRepository;

    @Value("${app.init.admin-password:}")
    private String initAdminPassword;

    @Value("${app.init.admin-email:}")
    private String initAdminEmail;

    @Value("${app.init.demo-user-password:}")
    private String initDemoUserPassword;

    public DataInitializer(PostRepository postRepository,
                           CategoryRepository categoryRepository,
                           SysUserRepository sysUserRepository,
                           NotificationRepository notificationRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.sysUserRepository = sysUserRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void run(String... args) {
        // 0. 按环境变量按需初始化系统内置用户（绝不硬编码默认口令）
        initSystemUsers();

        // 1. 初始化分类 (7 大极客专栏)
        if (categoryRepository.count() == 0) {
            List<Category> categories = Arrays.asList(
                new Category("云原生 & Linux", "☁️", "容器化编排、GCP/云服务器运维、系统调优与网络配置实战"),
                new Category("Java & Spring Boot", "🍃", "Spring Boot 3 微服务架构、JPA 数据库持久化与后端工程规范"),
                new Category("AI & 本地模型", "🧠", "Ollama 本地大模型部署、DeepSeek R1 性能压测与应用集成"),
                new Category("全栈开发 & CI/CD", "⚙️", "前后端分离架构、Gitea Actions 自动化流水线交付"),
                new Category("前端工程 & Vue3", "🌐", "现代前端工程化、Vue 3 全家桶、Vite 构建与前端性能调优实战"),
                new Category("数据库 & 中间件", "💾", "MySQL 8.0 性能调优、Redis 核心缓存机制与消息队列解耦"),
                new Category("系统架构 & 最佳实践", "🏗️", "微服务架构演进、高可用系统设计、安全防护与领域驱动设计")
            );
            categoryRepository.saveAll(categories);
        }

        // 2. 初始化预置文章
        if (postRepository.count() == 0) {
            Post p1 = new Post();
            p1.setTitle("云原生实践：在 Google Cloud 台湾节点搭建高性能 Ubuntu 与 1Panel");
            p1.setSummary("本文记录了如何通过 GCP 赠金在亚洲低延迟机房部署 4核16G 高性能云服务器，并使用现代化容器面板 1Panel 进行运维管理实录。");
            p1.setContent("# 云原生实践：在 Google Cloud 台湾节点搭建高性能服务器\n\n" +
                    "在挑选云服务器的过程中，综合对比了各大厂商的免费策略与性能表现，最终选择了 **Google Cloud 台湾节点 (`asia-east1`)**。\n\n" +
                    "## 核心硬件与网络配置\n" +
                    "- **规格**：`e2-standard-4`（4 vCPU，16 GB 内存）\n" +
                    "- **系统**：Ubuntu 22.04 LTS (x86_64)\n" +
                    "- **延迟**：到中国大陆仅 30~50ms，操作极为丝滑。\n\n" +
                    "## 为什么采用 1Panel 面板？\n" +
                    "相比传统臃肿的面料，1Panel 深度拥抱 **Docker 容器化**，所有应用（MySQL、OpenResty、Gitea）完全容器隔离，真正做到“环境随用随建，数据挂载持久化”。\n\n" +
                    "```bash\n# 快速检查 Docker 运行状态\ndocker ps --format \"table {{.Names}}\\t{{.Status}}\\t{{.Ports}}\"\n```\n\n" +
                    "> 💡 **运维心得**：永远在云平台控制台统一放行防火墙端口，切勿盲目在系统内部启用 UFW，避免失联风险。");
            p1.setAuthor("小东");
            p1.setCategoryName("云原生 & Linux");
            p1.setTags("GCP,Ubuntu,1Panel,Linux");
            p1.setReadTime("4 min");
            p1.setViews(682);
            p1.setLikes(58);

            Post p2 = new Post();
            p2.setTitle("探索本地大模型：从 Ollama 运行 DeepSeek R1 到 CPU 推理性能调优");
            p2.setSummary("深度解析在纯 CPU 服务器上运行 DeepSeek R1 和通义千问 2.5 的真实表现，揭开 2050 Token 预填充延迟背后的机制。");
            p2.setContent("# 探索本地大模型：从 Ollama 到 Open WebUI 性能调优实战\n\n" +
                    "近期 DeepSeek R1 推理思考模型火遍全球，我们在 4核16G 的服务器上进行了本地大模型的全链路压测。\n\n" +
                    "## 1. 架构搭建\n" +
                    "- **模型发动机**：`Ollama` 容器\n" +
                    "- **交互界面**：`Open WebUI` 容器\n" +
                    "- **网络通信**：通过 Docker 网关 `http://172.17.0.1:11434` 极速直连\n\n" +
                    "## 2. 性能排查与调优秘籍\n" +
                    "测试过程中遇到了“每次提问卡顿 50 秒”的问题，经过 `top` 分析和指标剖析：\n" +
                    "1. **解除容器 CPU 配额**：确保 4 线程全开（`eval rate` 提升至 14 tokens/s）；\n" +
                    "2. **内存永久常驻**：注入 `OLLAMA_KEEP_ALIVE=-1`，避免 5 分钟超时后重新读盘；\n" +
                    "3. **清空全局默认 Prompt**：消除了默认附带的 2050 历史 Token，读题耗时从 60s 骤降至 0.2s！\n\n" +
                    "```bash\n# 测试模型运行性能\ndocker exec -it 1Panel-ollama-nzre ollama run qwen2.5:1.5b --verbose\n```\n\n" +
                    "> 纯 CPU 跑 7B 参数是物理带宽瓶颈，日常开发推荐搭配 1.5B~3B 模型，速度与逻辑达到最佳平衡！");
            p2.setAuthor("小东");
            p2.setCategoryName("AI & 本地模型");
            p2.setTags("DeepSeek,Ollama,OpenWebUI,性能调优");
            p2.setReadTime("6 min");
            p2.setViews(1048);
            p2.setLikes(96);

            Post p3 = new Post();
            p3.setTitle("全栈工程师实战：Spring Boot 3 + MySQL 8.0 持久化与 Gitea CI/CD 自动化交付");
            p3.setSummary("从零构建工业级 Spring Boot 3 分层架构，结合 MySQL 8.0 数据库持久化存储与私有 Gitea Actions 自动化流水线。");
            p3.setContent("# 全栈工程化实践：Spring Boot 3 + MySQL 8.0 + CI/CD 自动化\n\n" +
                    "现代化开发的精髓在于**“把数据交给专业数据库持久化，把重复劳动交给自动化流水线”**。\n\n" +
                    "## 全栈架构一览\n" +
                    "```text\n[开发者本地 IDEA] ──git push──▶ [私有 Gitea 仓库]\n                                      │\n                               (Gitea Actions 触发流水线)\n                                      ▼\n                              [Docker Compose 全栈构建]\n                                      │\n          ┌───────────────────────────┼───────────────────────────┐\n          ▼                           ▼                           ▼\n    [MySQL 8.0 容器]          [Spring Boot 3 容器]        [Nginx 静态反代容器]\n```\n\n" +
                    "## 数据持久化规范\n" +
                    "- 数据文件挂载至命名卷 `mysql_data`，容器重启或重建数据零丢失；\n" +
                    "- Spring Data JPA 实现对象关系映射，自动完成数据库结构维护；\n" +
                    "- 统一 RESTful API 与响应封装，全栈开发得心应手！");
            p3.setAuthor("小东");
            p3.setCategoryName("全栈开发 & CI/CD");
            p3.setTags("Spring Boot,MySQL,JPA,CI/CD");
            p3.setReadTime("5 min");
            p3.setViews(815);
            p3.setLikes(72);

            postRepository.saveAll(Arrays.asList(p1, p2, p3));
        }

        // 3. 自动迁移平滑更新：如果数据库中存留历史旧作者"旺总"，自动同步更新为"小东"
        List<Post> existingPosts = postRepository.findAll();
        boolean hasMigration = false;
        for (Post p : existingPosts) {
            if ("旺总".equals(p.getAuthor())) {
                p.setAuthor("小东");
                hasMigration = true;
            }
        }
        if (hasMigration) {
            postRepository.saveAll(existingPosts);
        }

        // 4. 自动查漏补缺：确保 Java & Spring Boot 分类下有精品技术文章
        boolean hasJavaPost = existingPosts.stream().anyMatch(p -> "Java & Spring Boot".equals(p.getCategoryName()));
        if (!hasJavaPost) {
            Post pJava = new Post();
            pJava.setTitle("Spring Boot 3 深度实战：Virtual Threads 虚拟线程与 GraalVM 原生镜像赋能高并发微服务");
            pJava.setSummary("深入剖析 JDK 21 虚拟线程在 Spring Boot 3.2+ 中的并发落地实践，以及基于 GraalVM AOT 编译的原生二进制镜像极致启动调优。");
            pJava.setContent("# Spring Boot 3 深度实战：虚拟线程与原生镜像构建\n\n" +
                    "随着 JDK 21 正式成为长期支持版本（LTS），Spring Boot 3 全面引入了协程级的高性能并发革命——**Project Loom 虚拟线程 (Virtual Threads)**。\n\n" +
                    "## 1. 开启 Tomcat 虚拟线程支持\n" +
                    "在 Spring Boot 3.2 之后，仅需一行配置即可解开吞吐量枷锁：\n" +
                    "```yaml\nserver:\n  tomcat:\n    threads:\n      virtual:\n        enabled: true\n```\n\n" +
                    "## 2. 虚拟线程与传统线程池压测对比\n" +
                    "- **平台线程 (Platform Threads)**：受操作系统内核限制，单机创建超过 2000 个线程即可导致内存枯竭并触发 `OutOfMemoryError`；\n" +
                    "- **虚拟线程 (Virtual Threads)**：用户态轻量级调度，1GB 内存即可承载百万级并发连接，特别适合阻塞型 I/O（如数据库查询、远程微服务 RPC 调用）。\n\n" +
                    "> 💡 **架构建议**：在虚拟线程中避免使用 `synchronized` 关键字以防线程固定（Pinning），推荐使用 `ReentrantLock` 替代！");
            pJava.setAuthor("小东");
            pJava.setCategoryName("Java & Spring Boot");
            pJava.setTags("Java21,SpringBoot3,VirtualThreads,微服务");
            pJava.setReadTime("5 min");
            pJava.setViews(756);
            pJava.setLikes(88);
            postRepository.save(pJava);
        }

        // 5. 确保专栏分类图标初始化
        List<Category> allCats = categoryRepository.findAll();
        for (Category c : allCats) {
            if ("云原生 & Linux".equals(c.getName())) c.setIcon("☁️");
            else if ("Java & Spring Boot".equals(c.getName())) c.setIcon("🍃");
            else if ("AI & 本地模型".equals(c.getName())) c.setIcon("🧠");
            else if ("全栈开发 & CI/CD".equals(c.getName())) c.setIcon("⚙️");
            else if (c.getIcon() == null || c.getIcon().trim().isEmpty()) c.setIcon("📑");
        }
        categoryRepository.saveAll(allCats);

        // 6. 初始化全站欢迎通知公告
        if (notificationRepository.count() == 0) {
            Notification n = new Notification(
                "🎉 全站架构升级公告：极客后台管理控制台与站内通知系统正式上线！",
                "亲爱的极客读者：\n\n欢迎来到博主**小东**的极客博客！本次系统迎来重磅里程碑升级：\n- **综合管理控制台**：支持博主在 PC 与移动端高效管理文章、专栏分类与读者互动；\n- **读者评论置顶与审核**：优质技术探讨置顶展示，打造高质量极客技术交流社区；\n- **系统通知中心**：站内公告与私信定向触达，支持一键已读！\n\n祝您阅读与技术探索愉快！",
                "ANNOUNCEMENT",
                "ALL"
            );
            notificationRepository.save(n);
        }
    }

    private void initSystemUsers() {
        // 1. 仅当 admin 尚不存在且显式提供 INIT_ADMIN_PASSWORD 时初始化管理员
        if (!sysUserRepository.existsByUsername("admin")) {
            if (initAdminPassword != null && !initAdminPassword.isBlank()) {
                SysUser admin = new SysUser();
                admin.setUsername("admin");
                String salt = SecurityUtils.generateSalt();
                admin.setSalt(salt);
                admin.setPassword(SecurityUtils.encryptPassword(initAdminPassword.trim(), salt));
                admin.setNickname("小东 (管理员)");
                if (initAdminEmail != null && !initAdminEmail.isBlank()) {
                    admin.setEmail(initAdminEmail.trim());
                }
                admin.setAvatar("👨‍💻");
                admin.setRole("ADMIN");
                admin.setStatus("0");
                sysUserRepository.save(admin);
            } else {
                log.warn("[DataInitializer] 数据库不存在 admin 账号且未设置 INIT_ADMIN_PASSWORD，已跳过默认管理员初始化");
            }
        }

        // 2. 仅当显式提供 INIT_DEMO_USER_PASSWORD 时初始化测试访客账号
        if (!sysUserRepository.existsByUsername("ry_user")
                && initDemoUserPassword != null
                && !initDemoUserPassword.isBlank()) {
            SysUser visitor = new SysUser();
            visitor.setUsername("ry_user");
            String salt = SecurityUtils.generateSalt();
            visitor.setSalt(salt);
            visitor.setPassword(SecurityUtils.encryptPassword(initDemoUserPassword.trim(), salt));
            visitor.setNickname("极客读者");
            visitor.setEmail("visitor@example.com");
            visitor.setAvatar("🚀");
            visitor.setRole("USER");
            visitor.setStatus("0");
            sysUserRepository.save(visitor);
        }
    }
}
