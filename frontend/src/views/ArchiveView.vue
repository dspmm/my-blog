<template>
  <!-- ======================= 视图 3：时光归档 (ARCHIVE VIEW) ======================= -->
  <main class="flex-1 max-w-4xl w-full mx-auto px-4 sm:px-6 py-8">
    <div class="mb-8">
      <h1 class="text-3xl font-black text-slate-900 dark:text-white flex items-center gap-3">
        <span>⏳</span>
        <span>技术成长时光轴</span>
      </h1>
      <p class="mt-2 text-sm text-slate-500">按发布时间记录每一篇全栈架构与容器化工程实录，共计 {{ (allPosts.length > 0 ? allPosts : posts).length }} 篇。</p>
    </div>

    <!-- 时间轴主体 -->
    <div class="relative pl-6 sm:pl-8 border-l-2 border-slate-200 dark:border-slate-800 space-y-8">
      <div v-for="post in (allPosts.length > 0 ? allPosts : posts)" :key="post.id" class="relative group cursor-pointer" @click="emit('view-post', post)">
        <!-- 时间轴圆点 -->
        <div class="absolute -left-[31px] sm:-left-[39px] top-1.5 w-4 h-4 rounded-full bg-white dark:bg-slate-900 border-4 border-sky-500 group-hover:scale-125 group-hover:border-indigo-500 transition-all"></div>

        <!-- 卡片 -->
        <div class="p-5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 group-hover:border-sky-500/40 shadow-sm transition-all">
          <div class="flex items-center gap-2 text-xs font-mono text-slate-400 mb-1.5">
            <span>📅 {{ formatDate(post.createdAt) }}</span>
            <span>·</span>
            <span class="text-sky-600 dark:text-sky-400 font-semibold">{{ post.categoryName }}</span>
          </div>
          <h3 class="text-base sm:text-lg font-bold text-slate-900 dark:text-slate-100 group-hover:text-sky-600 dark:group-hover:text-sky-400 transition-colors">
            {{ post.title }}
          </h3>
          <p class="mt-1 text-xs sm:text-sm text-slate-500 line-clamp-1">
            {{ post.summary || (post.content && post.content.substring(0, 100)) }}
          </p>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { formatDate } from '@/utils/formatters';

defineProps({
  posts: {
    type: Array,
    default: () => [],
  },
  allPosts: {
    type: Array,
    default: () => [],
  },
});

const emit = defineEmits(['view-post']);
</script>
