<template>
  <!-- 1. 博主极客微名片 (含 3D 悬浮与聚光灯微动) -->
  <div class="tilt-card spotlight-card p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm relative overflow-hidden">
    <!-- 极客背景光晕 -->
    <div class="absolute -right-10 -top-10 w-40 h-40 bg-gradient-to-br from-sky-400/20 via-indigo-500/15 to-purple-500/10 rounded-full blur-2xl pointer-events-none"></div>

    <div class="relative z-10 flex flex-col items-center text-center">
      <!-- 头像与呼吸状态点 -->
      <div class="relative mb-3">
        <div class="w-16 h-16 rounded-2xl bg-gradient-to-br from-slate-900 via-indigo-950 to-slate-800 dark:from-sky-500 dark:to-indigo-600 flex items-center justify-center text-white text-2xl shadow-xl shadow-sky-500/15">
          👨‍💻
        </div>
        <span class="absolute -bottom-0.5 -right-0.5 w-4 h-4 rounded-full bg-emerald-500 border-2 border-white dark:border-slate-900 animate-pulse" title="系统服务正常 · 实时在线"></span>
      </div>

      <!-- 姓名与极客身份 -->
      <h2 class="text-lg font-black text-slate-900 dark:text-white tracking-tight flex items-center gap-1.5">
        <span>小东</span>
        <span class="text-xs text-slate-400 font-mono font-normal">@xiaodong</span>
      </h2>
      <div class="mt-1 px-2.5 py-0.5 text-[11px] font-semibold rounded-md bg-sky-50 dark:bg-sky-950/80 text-sky-700 dark:text-sky-300 border border-sky-200/60 dark:border-sky-800/60">
        🚀 全栈架构师 · DevOps 极客
      </div>
      <p class="mt-2.5 text-xs text-slate-500 dark:text-slate-400 leading-relaxed text-left">
        沉淀高可用企业级工程经验，深耕 Spring Boot 3、MySQL 8.0 持久化架构与云原生自动化交付。
      </p>

      <!-- 数据微看板 -->
      <div class="w-full grid grid-cols-3 gap-2 mt-4 py-2.5 px-2 rounded-2xl bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-800 font-mono text-center">
        <div>
          <div class="text-base font-black text-sky-600 dark:text-sky-400">{{ stats.totalPosts || 0 }}</div>
          <div class="text-[10px] text-slate-400 font-sans">文章数</div>
        </div>
        <div class="border-x border-slate-200 dark:border-slate-700">
          <div class="text-base font-black text-slate-800 dark:text-slate-200">{{ stats.totalViews || 0 }}</div>
          <div class="text-[10px] text-slate-400 font-sans">阅读量</div>
        </div>
        <div>
          <div class="text-base font-black text-rose-500">{{ stats.totalLikes || 0 }}</div>
          <div class="text-[10px] text-slate-400 font-sans">读者赞</div>
        </div>
      </div>

      <!-- 核心技术栈胶囊 -->
      <div class="mt-3.5 flex flex-wrap justify-center gap-1.5 text-[11px]">
        <span class="px-2 py-0.5 rounded-md bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 font-mono">🐬 MySQL 8</span>
        <span class="px-2 py-0.5 rounded-md bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 font-mono">🍃 SpringBoot 3</span>
        <span class="px-2 py-0.5 rounded-md bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 font-mono">🐳 Docker</span>
        <span class="px-2 py-0.5 rounded-md bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 font-mono">🧠 DeepSeek</span>
      </div>

      <!-- 极客操作快捷直达 -->
      <div class="w-full mt-4 pt-3.5 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between text-xs">
        <span class="inline-flex items-center gap-1.5 text-emerald-600 dark:text-emerald-400 font-medium">
          <span class="w-2 h-2 rounded-full bg-emerald-500"></span>
          <span>服务运行中</span>
        </span>
        <button type="button"
                @click="showContactModal = true"
                class="inline-flex items-center gap-1 text-slate-600 dark:text-slate-400 hover:text-sky-600 dark:hover:text-sky-400 transition-colors">
          <span>✉️</span> <span>联系博主</span>
        </button>
      </div>
    </div>

    <!-- 联系博主多合一沟通面板 -->
    <ContactModal
      :show="showContactModal"
      @close="showContactModal = false"
      @open-feedback="emit('open-feedback')"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import ContactModal from '@/components/modals/ContactModal.vue';

defineProps({
  stats: {
    type: Object,
    default: () => ({ totalPosts: 0, totalViews: 0, totalLikes: 0 }),
  },
});

const emit = defineEmits(['open-feedback']);
const showContactModal = ref(false);
</script>
