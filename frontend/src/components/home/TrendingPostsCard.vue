<template>
  <!-- 1. 🔥 全站热度 Top 5 排行榜 -->
  <div class="p-5 flex flex-col rounded-3xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm">
    <div class="flex items-center justify-between mb-3.5 pb-2 border-b border-slate-100 dark:border-slate-800 shrink-0">
      <span class="text-xs font-extrabold uppercase tracking-wider text-slate-800 dark:text-slate-200 flex items-center gap-1.5">
        <span>🔥</span> <span>全站热度榜单</span>
      </span>
      <span class="text-[10px] font-mono text-slate-400">TOP 5</span>
    </div>

    <div v-if="posts && posts.length > 0" class="space-y-3">
      <div v-for="(tp, idx) in posts" :key="tp.id"
           @click="emit('view', tp)"
           class="flex items-start gap-3 group cursor-pointer">
        
        <!-- 金银铜或序号徽章 -->
        <span class="w-5 h-5 rounded-md flex items-center justify-center text-xs font-mono font-black shrink-0"
              :class="idx === 0 
                 ? 'bg-amber-400 text-slate-950 shadow-sm shadow-amber-400/30' 
                 : idx === 1 
                 ? 'bg-slate-300 dark:bg-slate-700 text-slate-900 dark:text-white' 
                 : idx === 2 
                 ? 'bg-amber-700 text-white' 
                 : 'bg-slate-100 dark:bg-slate-800 text-slate-500'">
          {{ idx + 1 }}
        </span>

        <div class="flex-1 min-w-0">
          <h4 class="text-xs font-bold text-slate-800 dark:text-slate-200 group-hover:text-sky-600 dark:group-hover:text-sky-400 transition-colors line-clamp-1 leading-snug">
            {{ tp.title }}
          </h4>
          <div class="flex items-center gap-2 mt-1 text-[11px] font-mono text-slate-400">
            <span>👁️ {{ tp.views || 0 }} 次阅读</span>
            <span>·</span>
            <span>❤️ {{ tp.likes || 0 }}</span>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="py-6 text-center text-xs text-slate-400">
      暂无榜单数据
    </div>
  </div>
</template>

<script setup>
defineProps({
  posts: {
    type: Array,
    default: () => [],
  },
});

const emit = defineEmits(['view']);
</script>
