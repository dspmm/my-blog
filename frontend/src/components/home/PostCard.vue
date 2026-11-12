<template>
  <article @click="emit('view', post)"
           class="spotlight-card group p-5 sm:p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm hover:shadow-xl hover:border-sky-500/40 dark:hover:border-sky-500/40 transition-all duration-300 cursor-pointer relative overflow-hidden">
    
    <!-- 顶部分类与时间 -->
    <div class="flex flex-wrap items-center justify-between gap-2 mb-2.5">
      <div class="flex items-center gap-2">
        <span class="px-2.5 py-0.5 text-xs font-bold rounded-lg bg-sky-50 text-sky-700 dark:bg-sky-950/80 dark:text-sky-300 border border-sky-100 dark:border-sky-900/60 flex items-center gap-1">
          <span>{{ getCategoryIcon(post.categoryName) }}</span>
          <span>{{ post.categoryName || '技术架构' }}</span>
        </span>
        <span class="text-xs text-slate-400">·</span>
        <span class="text-xs text-slate-500 dark:text-slate-400 font-mono">📅 {{ formatDate(post.createdAt) }}</span>
      </div>
      <div class="text-xs text-slate-400 font-mono flex items-center gap-1">
        <span>⏱️ {{ post.readTime || '5 min' }}</span>
      </div>
    </div>

    <!-- 标题 -->
    <h2 class="text-lg sm:text-xl font-black text-slate-900 dark:text-slate-100 group-hover:text-sky-600 dark:group-hover:text-sky-400 transition-colors leading-snug">
      {{ post.title }}
    </h2>

    <!-- 摘要 -->
    <p class="mt-2 text-xs sm:text-sm text-slate-600 dark:text-slate-300 line-clamp-2 leading-relaxed">
      {{ post.summary || (post.content && post.content.substring(0, 140)) }}
    </p>

    <!-- 底部信息 -->
    <div class="mt-4 pt-3.5 border-t border-slate-100 dark:border-slate-800/80 flex flex-wrap items-center justify-between gap-3 text-xs text-slate-500 dark:text-slate-400">
      <!-- 标签群 (点击即可过滤标签) -->
      <div class="flex items-center gap-1.5 flex-wrap">
        <button v-for="tag in parseTags(post.tags)" :key="tag"
                @click.stop="emit('filter-tag', tag)"
                :class="selectedTag === tag ? 'bg-sky-500 text-white font-bold' : 'bg-slate-100 dark:bg-slate-800/80 hover:bg-slate-200 text-slate-600 dark:text-slate-300'"
                class="px-2 py-0.5 rounded-md font-mono text-[11px] transition-colors">
          #{{ tag }}
        </button>
      </div>

      <!-- 互动指标 -->
      <div class="flex items-center gap-4">
        <span class="flex items-center gap-1 font-mono text-xs" title="阅读量">
          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path></svg>
          {{ post.views || 0 }}
        </span>

        <button @click.stop="emit('like', post)"
                class="flex items-center gap-1 hover:text-rose-500 transition-colors active:scale-125 font-mono text-xs" title="点赞支持">
          <svg class="w-3.5 h-3.5 text-rose-500 fill-rose-500" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z" clip-rule="evenodd"></path></svg>
          <span>{{ post.likes || 0 }}</span>
        </button>

        <span class="inline-flex items-center gap-1 font-bold text-sky-600 dark:text-sky-400 group-hover:translate-x-1 transition-transform">
          沉浸阅读 →
        </span>
      </div>
    </div>
  </article>
</template>

<script setup>
import { formatDate, parseTags, getCategoryIcon } from '@/utils/formatters';

defineProps({
  post: {
    type: Object,
    required: true,
  },
  selectedTag: {
    type: String,
    default: '',
  },
});

const emit = defineEmits(['view', 'like', 'filter-tag']);
</script>
