<template>
  <!-- ======================= 视图 4：专栏分类 (CATEGORIES VIEW) ======================= -->
  <main class="flex-1 max-w-[1440px] w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div class="mb-8">
      <h1 class="text-3xl font-black text-slate-900 dark:text-white flex items-center gap-3">
        <span>🗂️</span>
        <span>技术专栏全景</span>
      </h1>
      <p class="mt-2 text-sm text-slate-500">按照企业级架构体系细分的专业技术领域与专题沉淀。</p>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div v-for="cat in categoryList" :key="cat.name"
           @click="emit('filter-category', cat.name)"
           class="spotlight-card p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 hover:border-sky-500/50 shadow-sm hover:shadow-xl transition-all cursor-pointer group">
        <div class="flex items-center justify-between mb-4">
          <div class="text-3xl p-3 rounded-2xl bg-sky-50 dark:bg-sky-950/60 w-fit">
            {{ cat.icon }}
          </div>
          <span class="px-3 py-1 rounded-full text-xs font-mono font-bold bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300">
            {{ getPostsCountByCategory(cat.name) }} 篇文章
          </span>
        </div>
        <h3 class="text-xl font-bold text-slate-900 dark:text-white group-hover:text-sky-600 dark:group-hover:text-sky-400 transition-colors">
          {{ cat.name }}
        </h3>
        <p class="mt-2 text-xs sm:text-sm text-slate-500 leading-relaxed">
          {{ cat.desc }}
        </p>
        <div class="mt-4 pt-4 border-t border-slate-100 dark:border-slate-800/80 flex items-center justify-between text-xs font-semibold text-sky-600 dark:text-sky-400">
          <span>查看该专栏全部文章</span>
          <span class="group-hover:translate-x-1 transition-transform">→</span>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
defineProps({
  categoryList: {
    type: Array,
    default: () => [],
  },
  getPostsCountByCategory: {
    type: Function,
    required: true,
  },
});

const emit = defineEmits(['filter-category']);
</script>
