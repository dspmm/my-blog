import { marked } from 'marked';
import hljs from 'highlight.js';
import DOMPurify from 'dompurify';

// 初始化并配置 marked 和 highlight.js
export function setupMarked() {
  const renderer = new marked.Renderer();
  renderer.code = ({ text, lang }) => {
    const requestedLanguage = (lang || '').trim().split(/\s+/)[0].toLowerCase();
    const language = hljs.getLanguage(requestedLanguage) ? requestedLanguage : 'plaintext';
    const highlighted = hljs.highlight(text, { language }).value;
    return `<pre><code class="hljs language-${language}">${highlighted}</code></pre>\n`;
  };

  marked.use({ renderer });
  marked.setOptions({ breaks: true, gfm: true });
}

// 渲染 Markdown 为 HTML 并自动注入锚点
export function renderMarkdownWithHeadings(content) {
  if (!content) return '';
  // 智能规整图片访问链接：将所有硬编码的主机名/端口（无论 9000、8088、IP、或外部域名）统一规整为同源相对路径 /my-blog/，彻底杜绝 HTTPS 下的 Mixed Content 阻断与跨域跨端口拦截
  const normalized = content.replace(/(https?:\/\/[^\/\s)]+)?\/my-blog\//g, '/my-blog/');
  let html = marked.parse(normalized);
  let idx = 0;
  // 规范化标题层级：将 Markdown 内容中的 h1 全部降级为 h2，保持页面单一 h1 语义规范
  html = html.replace(/<(\/?)h1\b([^>]*)>/gi, '<$1h2$2>');
  html = html.replace(/<h([1-3])>(.*?)<\/h\1>/g, (match, level, text) => {
    const id = 'heading-' + (++idx);
    return `<h${level} id="${id}">${text}</h${level}>`;
  });
  return DOMPurify.sanitize(html);
}

// 提取文章的大纲目录
export function extractToc(markdown) {
  if (!markdown) return [];
  const lines = markdown.split('\n');
  const toc = [];
  let idx = 0;
  for (const line of lines) {
    const match = line.match(/^(#{1,3})\s+(.+)$/);
    if (match) {
      const level = match[1].length;
      const text = match[2].trim();
      const id = 'heading-' + (++idx);
      toc.push({ id, level, text });
    }
  }
  return toc;
}

// 为 Markdown 渲染后的代码块绑定一键复制代码与 macOS 三色视窗装饰
export function bindCodeCopyButtons(showToast) {
  const pres = document.querySelectorAll('.markdown-body pre');
  pres.forEach(pre => {
    if (pre.closest('.code-block-wrapper')) return;

    const code = pre.querySelector('code');
    const codeText = code ? code.innerText : pre.innerText;

    let lang = 'CODE';
    if (code && code.className) {
      const match = code.className.match(/language-(\w+)/);
      if (match) lang = match[1].toUpperCase();
    }

    const wrapper = document.createElement('div');
    wrapper.className = 'code-block-wrapper';

    const header = document.createElement('div');
    header.className = 'code-block-header';
    header.innerHTML = `
      <div class="code-block-dots">
        <div class="code-dot red"></div>
        <div class="code-dot yellow"></div>
        <div class="code-dot green"></div>
      </div>
      <div style="display:flex;align-items:center;gap:0.75rem;">
        <span class="code-block-lang">${lang}</span>
        <button class="code-copy-btn">
          <svg style="width:0.85rem;height:0.85rem;" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"></path></svg>
          <span>复制代码</span>
        </button>
      </div>
    `;

    const copyBtn = header.querySelector('.code-copy-btn');
    copyBtn.addEventListener('click', () => {
      navigator.clipboard.writeText(codeText).then(() => {
        copyBtn.innerHTML = `<span>已复制！✓</span>`;
        copyBtn.style.color = '#38bdf8';
        copyBtn.style.borderColor = '#38bdf8';
        if (showToast) showToast('代码已成功复制到剪贴板', 'success');
        setTimeout(() => {
          copyBtn.innerHTML = `
            <svg style="width:0.85rem;height:0.85rem;" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"></path></svg>
            <span>复制代码</span>
          `;
          copyBtn.style.color = '#f0f6fc';
          copyBtn.style.borderColor = '#30363d';
        }, 2000);
      });
    });

    pre.parentNode.insertBefore(wrapper, pre);
    wrapper.appendChild(header);
    wrapper.appendChild(pre);
  });
}
