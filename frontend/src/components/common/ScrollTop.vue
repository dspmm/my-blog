<template>
  <!-- 返回顶部浮动胶囊 -->
  <button v-show="show" @click="emit('scroll-top')"
          class="fixed bottom-6 right-6 z-40 p-3 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-slate-700 dark:text-slate-300 shadow-xl hover:scale-110 active:scale-95 transition-all"
          title="回到顶部">
    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18"></path>
    </svg>
  </button>
</template>

<script setup>
defineProps({
  show: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['scroll-top']);
</script>
