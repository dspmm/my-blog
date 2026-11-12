<template>
  <!-- ======================= 视图 5：关于我与架构全景 (ABOUT VIEW) ======================= -->
  <main class="flex-1 max-w-4xl w-full mx-auto px-4 sm:px-6 py-8">
    <article class="p-6 sm:p-10 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm space-y-8">
      
      <div>
        <h1 class="text-3xl font-black text-slate-900 dark:text-white flex items-center gap-3">
          <span>👨‍💻</span>
          <span>关于小东 (XiaoDong) 与本站架构</span>
        </h1>
        <p class="mt-2 text-sm text-slate-500">一个追求工程极致、拥抱云原生与本地大模型的全栈开发者个人空间。</p>
      </div>

      <!-- 技术栈矩阵 -->
      <div class="space-y-4">
        <h2 class="text-lg font-bold text-slate-900 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-2">
          🛠️ 掌握的技术栈 & 工具链
        </h2>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs sm:text-sm">
          <div class="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-700/60">
            <div class="font-bold text-sky-600 dark:text-sky-400 mb-2">🍃 后端企业级架构</div>
            <p class="text-slate-600 dark:text-slate-300">Java 17 / 21 · Spring Boot 3.2 · Spring Data JPA · RESTful API · 统一异常治理</p>
          </div>
          <div class="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-700/60">
            <div class="font-bold text-emerald-600 dark:text-emerald-400 mb-2">🐬 数据持久化与存储</div>
            <p class="text-slate-600 dark:text-slate-300">MySQL 8.0 性能调优 · Docker 命名卷持久化 · 自动库表结构同步 · 读写连接池</p>
          </div>
          <div class="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-700/60">
            <div class="font-bold text-indigo-600 dark:text-indigo-400 mb-2">🐳 容器化 & CI/CD 流水线</div>
            <p class="text-slate-600 dark:text-slate-300">Docker & Docker Compose · 自动化 CI/CD 流水线 · Nginx 反向代理 · 云原生运维</p>
          </div>
          <div class="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/60 dark:border-slate-700/60">
            <div class="font-bold text-purple-600 dark:text-purple-400 mb-2">🧠 现代 AI & 本地模型工程</div>
            <p class="text-slate-600 dark:text-slate-300">Ollama · Open WebUI · DeepSeek R1 性能调优 · CPU 线程释放 · Prompt 预填充优化</p>
          </div>
        </div>
      </div>

      <!-- 本站真实系统架构拓扑图 -->
      <div class="space-y-4">
        <h2 class="text-lg font-bold text-slate-900 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-2">
          🏗️ 本站全链路架构拓扑
        </h2>
        <div class="p-5 rounded-2xl bg-[#0d1117] text-slate-200 font-mono text-xs overflow-x-auto leading-relaxed border border-[#30363d]">
          <pre>┌────────────────────────────────────────────────────────────────────────┐
│                        Google Cloud 台湾节点 (GCP 4核16G)                │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│   [ 用户访问 https://jxdcloud.duckdns.org ]                             │
│                   │                                                    │
│                   ▼                                                    │
│      ┌─────────────────────────┐                                       │
│      │  Nginx 容器 (前端静态+反代)  │                                       │
│      └────────────┬────────────┘                                       │
│                   │ /api/ 反向代理                                      │
│                   ▼                                                    │
│      ┌─────────────────────────┐         ┌─────────────────────────┐   │
│      │ Spring Boot 3 容器 (8080) │ ──JPA──▶│      MySQL 8 容器       │   │
│      └─────────────────────────┘         │   (持久化卷数据隔离保护)   │   │
│                   ▲                      └─────────────────────────┘   │
│                   │ 自动重构镜像上线                                        │
│      ┌─────────────────────────┐         ┌─────────────────────────┐   │
│      │   自动化 CI/CD Runner    │ ◀────── │      私有 Git 代码仓库    │   │
│      └─────────────────────────┘ (git push)└─────────────────────────┘   │
└────────────────────────────────────────────────────────────────────────┘</pre>
        </div>
      </div>

      <!-- 联系与社交 -->
      <div class="pt-4 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between text-xs text-slate-500">
        <div>
          博主邮箱：
          <button type="button" @click="copyEmail" title="点击一键复制邮箱"
                  class="font-mono text-sky-600 dark:text-sky-400 hover:underline inline-flex items-center gap-1 cursor-pointer">
            <span>j2570852518@gmail.com</span>
            <span class="text-[10px] text-slate-400">📋</span>
          </button>
        </div>
        <div>部署架构：<span class="font-medium text-emerald-600 dark:text-emerald-400">Cloud-Native CI/CD 持续交付</span></div>
      </div>

    </article>
  </main>
</template>

<script setup>
import { useToast } from '@/composables/useToast';

const { showToast } = useToast();

function copyEmail() {
  const email = 'j2570852518@gmail.com';
  try {
    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(email);
    } else {
      const ta = document.createElement('textarea');
      ta.value = email;
      document.body.appendChild(ta);
      ta.select();
      document.execCommand('copy');
      document.body.removeChild(ta);
    }
    showToast('博主邮箱已成功复制到剪贴板！', 'success');
  } catch (e) {
    showToast('复制失败，请手动复制', 'error');
  }
}
</script>
