<template>
  <!-- ======================= 移动端原生底栏导航坞 (Mobile Bottom Navigation Dock) ======================= -->
  <nav v-show="currentView !== 'post'"
       class="md:hidden fixed bottom-0 inset-x-0 z-40 bg-white/95 dark:bg-slate-900/95 backdrop-blur-xl border-t border-slate-200 dark:border-slate-800 safe-bottom shadow-lg">
    <div class="grid grid-cols-4 h-14">
      <button @click="emit('navigate', 'home')"
              :class="currentView === 'home' ? 'text-sky-600 dark:text-sky-400 font-bold' : 'text-slate-500 dark:text-slate-400'"
              class="flex flex-col items-center justify-center gap-1 text-[11px] transition-colors">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"></path></svg>
        <span>首页</span>
      </button>

      <button @click="emit('navigate', 'categories')"
              :class="currentView === 'categories' ? 'text-sky-600 dark:text-sky-400 font-bold' : 'text-slate-500 dark:text-slate-400'"
              class="flex flex-col items-center justify-center gap-1 text-[11px] transition-colors">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"></path></svg>
        <span>分类</span>
      </button>

      <button @click="emit('navigate', 'archive')"
              :class="currentView === 'archive' ? 'text-sky-600 dark:text-sky-400 font-bold' : 'text-slate-500 dark:text-slate-400'"
              class="flex flex-col items-center justify-center gap-1 text-[11px] transition-colors">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
        <span>归档</span>
      </button>

      <button @click="emit('navigate', 'about')"
              :class="currentView === 'about' ? 'text-sky-600 dark:text-sky-400 font-bold' : 'text-slate-500 dark:text-slate-400'"
              class="flex flex-col items-center justify-center gap-1 text-[11px] transition-colors">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
        <span>关于</span>
      </button>
    </div>
  </nav>
</template>

<script setup>
defineProps({
  currentView: {
    type: String,
    default: 'home',
  },
});

const emit = defineEmits(['navigate']);
</script>
