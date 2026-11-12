<template>
  <!-- 2. 快速专栏导览 (带篇数徽章) -->
  <div class="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm">
    <div class="flex items-center justify-between mb-3 pb-2 border-b border-slate-100 dark:border-slate-800">
      <span class="text-xs font-bold uppercase tracking-wider text-slate-400 dark:text-slate-500">🗂️ 专栏分类</span>
      <span class="text-[11px] font-mono text-slate-400">{{ categories.length - 1 }} 个专栏</span>
    </div>
    <nav class="space-y-1">
      <button v-for="cat in categories" :key="cat"
              @click="emit('select-category', cat)"
              :class="selectedCategory === cat 
                ? 'bg-sky-50 dark:bg-sky-950/70 text-sky-600 dark:text-sky-400 font-bold border-sky-200 dark:border-sky-800' 
                : 'hover:bg-slate-50 dark:hover:bg-slate-800/60 text-slate-600 dark:text-slate-400 border-transparent'"
              class="w-full px-3 py-2 rounded-xl text-xs flex items-center justify-between transition-all border">
        <span class="flex items-center gap-2 truncate">
          <span>{{ getCategoryIcon(cat) }}</span>
          <span>{{ cat }}</span>
        </span>
        <span class="px-2 py-0.5 rounded-full text-[10px] font-mono"
              :class="selectedCategory === cat ? 'bg-sky-500 text-white font-bold' : 'bg-slate-100 dark:bg-slate-800 text-slate-500'">
          {{ cat === '全部' ? totalCount : getCount(cat) }}
        </span>
      </button>
    </nav>
  </div>
</template>

<script setup>
import { getCategoryIcon } from '@/utils/formatters';

const props = defineProps({
  categories: {
    type: Array,
    default: () => [],
  },
  selectedCategory: {
    type: String,
    default: '全部',
  },
  totalCount: {
    type: Number,
    default: 0,
  },
  getCount: {
    type: Function,
    default: () => 0,
  },
});

const emit = defineEmits(['select-category']);
</script>
