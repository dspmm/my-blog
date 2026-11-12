package com.example.blog.controller;

import com.example.blog.entity.Post;
import com.example.blog.repository.PostRepository;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 搜索引擎爬虫（蜘蛛）直出与全站 SEO 控制器
 * 提供 sitemap.xml 动态生成、robots.txt 准则与搜索引擎专属语义化 HTML
 */
@RestController
@RequestMapping("/api/seo")
public class SeoController {

    private static final String BASE_URL = "https://jxdcloud.duckdns.org";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final PostRepository postRepository;
    private final Parser markdownParser;
    private final HtmlRenderer htmlRenderer;

    public SeoController(PostRepository postRepository) {
        this.postRepository = postRepository;
        this.markdownParser = Parser.builder().build();
        this.htmlRenderer = HtmlRenderer.builder()
                .escapeHtml(true)
                .build();
    }

    /**
     * 动态生成标准 XML 格式站点地图 (Sitemap)
     */
    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getSitemap() {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        // 核心一级页面
        appendUrl(xml, BASE_URL + "/", "daily", "1.0", null);
        appendUrl(xml, BASE_URL + "/categories", "weekly", "0.8", null);
        appendUrl(xml, BASE_URL + "/archive", "weekly", "0.8", null);
        appendUrl(xml, BASE_URL + "/about", "monthly", "0.7", null);

        // 全部动态文章
        for (Post post : posts) {
            if ("PUBLISHED".equalsIgnoreCase(post.getStatus())) {
                String lastmod = post.getUpdatedAt() != null ?
                        post.getUpdatedAt().format(DATE_FORMATTER) :
                        (post.getCreatedAt() != null ? post.getCreatedAt().format(DATE_FORMATTER) : null);
                appendUrl(xml, BASE_URL + "/post/" + post.getId(), "weekly", "0.9", lastmod);
            }
        }

        xml.append("</urlset>");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/xml; charset=UTF-8"))
                .body(xml.toString());
    }

    private void appendUrl(StringBuilder xml, String loc, String changefreq, String priority, String lastmod) {
        xml.append("  <url>\n");
        xml.append("    <loc>").append(escapeXml(loc)).append("</loc>\n");
        if (lastmod != null) {
            xml.append("    <lastmod>").append(lastmod).append("</lastmod>\n");
        }
        xml.append("    <changefreq>").append(changefreq).append("</changefreq>\n");
        xml.append("    <priority>").append(priority).append("</priority>\n");
        xml.append("  </url>\n");
    }

    /**
     * 规范 robots.txt 抓取准则声明
     */
    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getRobotsTxt() {
        String robots = "User-agent: *\n" +
                "Allow: /\n" +
                "Disallow: /api/\n" +
                "Allow: /api/seo/\n\n" +
                "Sitemap: " + BASE_URL + "/sitemap.xml\n";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                .body(robots);
    }

    /**
     * Bing 站长平台 XML 文件所有权验证端点兜底
     */
    @GetMapping(value = "/BingSiteAuth.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getBingSiteAuth() {
        String xml = "<?xml version=\"1.0\"?>\n<users>\n\t<user>DB8F7A88A3557035FA0096E077496FC3</user>\n</users>\n";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/xml; charset=UTF-8"))
                .body(xml);
    }

    /**
     * 搜索引擎蜘蛛 (Googlebot / Baiduspider / bingbot 等) 专属文章语义化 HTML 直出
     */
    @GetMapping(value = "/post/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getPostSeoHtml(@PathVariable("id") Long id) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty() || !"PUBLISHED".equalsIgnoreCase(postOpt.get().getStatus())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.parseMediaType("text/html; charset=UTF-8"))
                    .body("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>文章不存在 - 小东的技术博客</title></head><body><h1>404 - 文章未找到或已被移除</h1><p><a href=\"" + BASE_URL + "/\">返回博客首页</a></p></body></html>");
        }

        Post post = postOpt.get();
        String title = escapeHtml(post.getTitle());
        String author = escapeHtml(post.getAuthor() != null ? post.getAuthor() : "小东");
        String category = escapeHtml(post.getCategoryName() != null ? post.getCategoryName() : "技术杂谈");
        String tags = post.getTags() != null ? escapeHtml(post.getTags()) : "";

        // 生成文章纯文本摘要用于 description
        String summary = post.getSummary();
        if (summary == null || summary.trim().isEmpty()) {
            summary = cleanMarkdownForSummary(post.getContent());
        }
        summary = escapeHtml(summary);

        // 渲染正文 Markdown 为安全语义化 HTML
        Node document = markdownParser.parse(post.getContent() != null ? post.getContent() : "");
        String renderedContentHtml = htmlRenderer.render(document);

        // 规范化标题层级：确保整张网页全局仅有且只有 1 个 <h1>（即文章主标题），将 Markdown 正文内的 <h1> 全部降级为 <h2>，彻底消除搜索引擎 "More than one h1 tag" 警告
        if (renderedContentHtml != null) {
            renderedContentHtml = renderedContentHtml.replaceAll("(?i)<h1(\\b[^>]*)>", "<h2$1>")
                    .replaceAll("(?i)</h1>", "</h2>");
        }

        String postUrl = BASE_URL + "/post/" + post.getId();
        String publishDate = post.getCreatedAt() != null ? post.getCreatedAt().format(DATE_FORMATTER) : "";
        String publishIso = post.getCreatedAt() != null ? post.getCreatedAt().format(ISO_FORMATTER) : "";

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"zh-CN\">\n<head>\n");
        html.append("  <meta charset=\"UTF-8\">\n");
        html.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("  <title>").append(title).append(" - 小东的技术博客</title>\n");
        html.append("  <meta name=\"description\" content=\"").append(summary).append("\">\n");
        html.append("  <meta name=\"keywords\" content=\"").append(category).append(",").append(tags).append(",小东,技术博客,极客\">\n");
        html.append("  <meta name=\"author\" content=\"").append(author).append("\">\n");
        html.append("  <meta name=\"robots\" content=\"index,follow\">\n");
        html.append("  <link rel=\"canonical\" href=\"").append(postUrl).append("\">\n");
        html.append("  <link rel=\"icon\" type=\"image/svg+xml\" href=\"").append(BASE_URL).append("/favicon.svg\">\n");

        // Open Graph 社交元数据 (Facebook, Twitter, Telegram, 微信抓取)
        html.append("  <meta property=\"og:type\" content=\"article\">\n");
        html.append("  <meta property=\"og:title\" content=\"").append(title).append("\">\n");
        html.append("  <meta property=\"og:description\" content=\"").append(summary).append("\">\n");
        html.append("  <meta property=\"og:url\" content=\"").append(postUrl).append("\">\n");
        html.append("  <meta property=\"og:image\" content=\"").append(BASE_URL).append("/favicon.png\">\n");
        html.append("  <meta property=\"og:site_name\" content=\"小东的技术博客\">\n");
        html.append("  <meta property=\"article:published_time\" content=\"").append(publishIso).append("\">\n");
        html.append("  <meta property=\"article:author\" content=\"").append(author).append("\">\n");
        html.append("  <meta property=\"article:section\" content=\"").append(category).append("\">\n");

        // Schema.org Article 结构化数据 (Google/Bing 极度看重)
        html.append("  <script type=\"application/ld+json\">\n");
        html.append("  {\n");
        html.append("    \"@context\": \"https://schema.org\",\n");
        html.append("    \"@type\": \"BlogPosting\",\n");
        html.append("    \"headline\": \"").append(escapeJson(post.getTitle())).append("\",\n");
        html.append("    \"description\": \"").append(escapeJson(summary)).append("\",\n");
        html.append("    \"url\": \"").append(postUrl).append("\",\n");
        html.append("    \"datePublished\": \"").append(publishIso).append("\",\n");
        html.append("    \"author\": {\n");
        html.append("      \"@type\": \"Person\",\n");
        html.append("      \"name\": \"").append(escapeJson(author)).append("\"\n");
        html.append("    },\n");
        html.append("    \"publisher\": {\n");
        html.append("      \"@type\": \"Organization\",\n");
        html.append("      \"name\": \"小东的技术博客\"\n");
        html.append("    }\n");
        html.append("  }\n");
        html.append("  </script>\n");

        // 极简轻量、蜘蛛友好的排版样式
        html.append("  <style>\n");
        html.append("    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; line-height: 1.75; color: #1e293b; background: #f8fafc; margin: 0; padding: 24px; }\n");
        html.append("    .container { max-width: 860px; margin: 0 auto; background: #ffffff; padding: 40px; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); }\n");
        html.append("    .breadcrumb { font-size: 14px; color: #64748b; margin-bottom: 24px; }\n");
        html.append("    .breadcrumb a { color: #2563eb; text-decoration: none; }\n");
        html.append("    h1 { font-size: 2rem; color: #0f172a; margin-top: 0; line-height: 1.3; }\n");
        html.append("    .meta { font-size: 14px; color: #64748b; display: flex; gap: 16px; margin: 16px 0 24px; border-bottom: 1px solid #e2e8f0; padding-bottom: 16px; }\n");
        html.append("    .meta-item { display: inline-flex; align-items: center; }\n");
        html.append("    .post-content { font-size: 16px; color: #334155; word-wrap: break-word; }\n");
        html.append("    .post-content pre { background: #0f172a; color: #f8fafc; padding: 16px; border-radius: 8px; overflow-x: auto; }\n");
        html.append("    .post-content code { background: #f1f5f9; color: #0f172a; padding: 2px 6px; border-radius: 4px; font-family: monospace; }\n");
        html.append("    .post-content pre code { background: transparent; color: inherit; padding: 0; }\n");
        html.append("    .post-content blockquote { border-left: 4px solid #3b82f6; margin: 16px 0; padding-left: 16px; color: #64748b; }\n");
        html.append("    .post-footer { margin-top: 48px; padding-top: 24px; border-top: 1px solid #e2e8f0; text-align: center; }\n");
        html.append("    .interactive-btn { display: inline-block; background: #2563eb; color: #ffffff; text-decoration: none; padding: 10px 24px; border-radius: 6px; font-weight: 500; margin-top: 12px; }\n");
        html.append("    .copyright { font-size: 13px; color: #94a3b8; margin-top: 16px; }\n");
        html.append("  </style>\n");
        html.append("</head>\n<body>\n");
        html.append("  <div class=\"container\">\n");
        html.append("    <nav class=\"breadcrumb\" aria-label=\"Breadcrumb\">\n");
        html.append("      <a href=\"").append(BASE_URL).append("/\">首页</a> &gt; ");
        html.append("      <a href=\"").append(BASE_URL).append("/categories\">").append(category).append("</a> &gt; ");
        html.append("      <span>正文</span>\n");
        html.append("    </nav>\n");
        html.append("    <article>\n");
        html.append("      <header>\n");
        html.append("        <h1>").append(title).append("</h1>\n");
        html.append("        <div class=\"meta\">\n");
        html.append("          <span class=\"meta-item\">作者: ").append(author).append("</span>\n");
        html.append("          <span class=\"meta-item\">发布时间: ").append(publishDate).append("</span>\n");
        html.append("          <span class=\"meta-item\">分类: ").append(category).append("</span>\n");
        if (!tags.isEmpty()) {
            html.append("          <span class=\"meta-item\">标签: ").append(tags).append("</span>\n");
        }
        html.append("        </div>\n");
        html.append("      </header>\n");
        html.append("      <div class=\"post-content\">\n");
        html.append(renderedContentHtml);
        html.append("      </div>\n");
        html.append("    </article>\n");
        html.append("    <footer class=\"post-footer\">\n");
        html.append("      <div>本文首发于<strong>小东的技术博客</strong>，体验极客暗黑主题与完整互动请前往博客主站：</div>\n");
        html.append("      <a class=\"interactive-btn\" href=\"").append(postUrl).append("\">在主站交互式阅读全文</a>\n");
        html.append("      <div class=\"copyright\">© 2026 小东的技术博客 - 保留所有权利</div>\n");
        html.append("    </footer>\n");
        html.append("  </div>\n");
        html.append("</body>\n</html>\n");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/html; charset=UTF-8"))
                .body(html.toString());
    }

    private String cleanMarkdownForSummary(String content) {
        if (content == null) return "";
        // 移除 Markdown 标题符、代码块标记、超链接等
        String plain = content.replaceAll("(?m)^#+\\s+", "")
                .replaceAll("```[\\s\\S]*?```", "")
                .replaceAll("`([^`]+)`", "$1")
                .replaceAll("\\[([^\\]]+)\\]\\([^\\)]+\\)", "$1")
                .replaceAll("[\\r\\n]+", " ")
                .trim();
        if (plain.length() > 160) {
            return plain.substring(0, 157) + "...";
        }
        return plain;
    }

    private String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
