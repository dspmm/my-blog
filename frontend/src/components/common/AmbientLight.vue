<template>
  <div>
    <!-- 鼠标跟随全局科技极光光晕 (Ambient Fluid Cursor Follower) -->
    <div id="ambient-cursor"
         class="fixed pointer-events-none z-0 w-[550px] h-[550px] -translate-x-1/2 -translate-y-1/2 rounded-full blur-[110px] opacity-40 dark:opacity-25 transition-opacity duration-500"
         style="background: radial-gradient(circle, rgba(56, 189, 248, 0.35) 0%, rgba(99, 102, 241, 0.22) 40%, transparent 70%);">
    </div>

    <!-- 极客星尘粒子画布 (Magic Dust / Sparkles) -->
    <canvas id="magic-dust-canvas" class="fixed inset-0 pointer-events-none z-[9999]"></canvas>

    <!-- 顶部滚动阅读进度条 (仅阅读长文时显示) -->
    <div v-show="showProgress"
         class="fixed top-0 left-0 h-1 bg-gradient-to-r from-sky-500 via-indigo-500 to-emerald-400 z-50 transition-all duration-75"
         :style="{ width: progress + '%' }">
    </div>
  </div>
</template>

<script setup>
defineProps({
  showProgress: {
    type: Boolean,
    default: false,
  },
  progress: {
    type: Number,
    default: 0,
  },
});
</script>
