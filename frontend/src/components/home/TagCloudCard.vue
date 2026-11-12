<template>
  <!-- 2. 🏷️ 全景技术标签云 (Dynamic Tag Cloud) -->
  <div class="p-5 h-[310px] flex flex-col rounded-3xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm">
    <div class="flex items-center justify-between mb-3 pb-2 border-b border-slate-100 dark:border-slate-800 shrink-0">
      <span class="text-xs font-extrabold uppercase tracking-wider text-slate-800 dark:text-slate-200 flex items-center gap-1.5">
        <span>🏷️</span> <span>全景技术标签云</span>
      </span>
      <span class="text-[10px] font-mono text-slate-400">{{ tags.length }} 个标签</span>
    </div>

    <div class="flex-1 overflow-y-auto custom-scrollbar pr-1 flex flex-wrap gap-1.5 content-start">
      <button v-for="t in tags" :key="t.name"
              @click="emit('filter-tag', t.name)"
              :class="selectedTag === t.name 
                ? 'bg-sky-500 text-white font-bold ring-2 ring-sky-300' 
                : 'bg-slate-100 dark:bg-slate-800/80 text-slate-600 dark:text-slate-300 hover:bg-slate-200 dark:hover:bg-slate-700'"
              class="px-2.5 py-1 rounded-lg text-xs font-mono transition-all flex items-center gap-1">
        <span>#{{ t.name }}</span>
        <span class="text-[10px] opacity-70">({{ t.count }})</span>
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  tags: {
    type: Array,
    default: () => [],
  },
  selectedTag: {
    type: String,
    default: '',
  },
});

const emit = defineEmits(['filter-tag']);
</script>
