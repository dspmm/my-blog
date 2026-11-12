<template>
  <!-- 3. 💬 实时读者动态 (Recent Comments Stream) -->
  <div class="p-5 h-[310px] flex flex-col rounded-3xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm">
    <div class="flex items-center justify-between mb-3 pb-2 border-b border-slate-100 dark:border-slate-800 shrink-0">
      <span class="text-xs font-extrabold uppercase tracking-wider text-slate-800 dark:text-slate-200 flex items-center gap-1.5">
        <span>💬</span> <span>全站读者微动态</span>
      </span>
      <span class="text-[10px] font-mono text-slate-400">实时互动</span>
    </div>

    <div v-if="comments.length > 0" class="flex-1 overflow-y-auto custom-scrollbar pr-1 space-y-3">
      <div v-for="rc in comments" :key="rc.id"
           @click="emit('jump', rc.postId)"
           class="p-2.5 rounded-xl bg-slate-50/70 dark:bg-slate-800/40 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors cursor-pointer text-xs">
        <div class="flex items-center justify-between text-[11px] mb-1">
          <div class="flex items-center gap-1 font-bold text-slate-800 dark:text-slate-200 truncate">
            <span>{{ rc.avatar || '👨‍💻' }}</span>
            <span>{{ rc.author }}</span>
          </div>
          <span class="text-[10px] text-slate-400 font-mono">{{ formatDate(rc.createdAt) }}</span>
        </div>
        <p class="text-slate-600 dark:text-slate-300 text-[11px] line-clamp-2 leading-relaxed">
          "{{ rc.content }}"
        </p>
        <div class="mt-1 text-[10px] text-sky-600 dark:text-sky-400 truncate">
          来自文章：{{ getPostTitle(rc.postId) }}
        </div>
      </div>
    </div>
    <div v-else class="flex-1 flex items-center justify-center text-xs text-slate-400">
      暂无最新评论互动
    </div>
  </div>
</template>

<script setup>
import { formatDate } from '@/utils/formatters';

defineProps({
  comments: {
    type: Array,
    default: () => [],
  },
  getPostTitle: {
    type: Function,
    default: () => '技术长文',
  },
});

const emit = defineEmits(['jump']);
</script>
