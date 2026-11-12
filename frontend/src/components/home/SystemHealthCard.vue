<template>
  <!-- 3. 系统服务状态监控看板 (System Health Widget) -->
  <div class="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm text-xs space-y-3">
    <div class="flex items-center justify-between pb-2 border-b border-slate-100 dark:border-slate-800">
      <span class="font-bold text-slate-800 dark:text-slate-200 flex items-center gap-1.5">
        <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
        <span>系统服务状态</span>
      </span>
      <span class="text-[11px] font-mono text-emerald-600 dark:text-emerald-400 font-semibold">{{ ping }}ms 延迟</span>
    </div>
    <div class="space-y-2 text-[11px] text-slate-600 dark:text-slate-400">
      <div class="flex items-center justify-between">
        <span class="text-slate-400">节点机房</span>
        <span class="font-medium text-slate-700 dark:text-slate-300">云端高可用节点 (亚太)</span>
      </div>
      <div class="flex items-center justify-between">
        <span class="text-slate-400">后端引擎</span>
        <span class="font-mono text-slate-700 dark:text-slate-300">Spring Boot 3.2.3</span>
      </div>
      <div class="flex items-center justify-between">
        <span class="text-slate-400">数据库存储</span>
        <span class="font-mono text-slate-700 dark:text-slate-300">MySQL 8.0 关系型数据库</span>
      </div>
      <div class="flex items-center justify-between">
        <span class="text-slate-400">持续部署</span>
        <span class="font-mono text-slate-700 dark:text-slate-300">Gitea Actions CI/CD</span>
      </div>
      <div class="flex items-center justify-between pt-1 border-t border-slate-100 dark:border-slate-800">
        <span class="text-slate-400">SLA 可用率</span>
        <span class="font-mono text-emerald-600 dark:text-emerald-400 font-bold">99.98% · 正常</span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  ping: {
    type: Number,
    default: 38,
  },
});
</script>
