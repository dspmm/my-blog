<template>
  <!-- 全局浮动通知 (Toast) - 超级置顶 z-[99999]，绝对位于全站所有模态框、蒙版与抽屉之上 -->
  <div class="fixed top-5 right-4 sm:right-6 z-[99999] flex flex-col gap-2.5 pointer-events-none max-w-[92vw] sm:max-w-md">
    <transition-group enter-active-class="transform ease-out duration-300 transition"
                      enter-from-class="-translate-y-3 opacity-0 sm:translate-y-0 sm:translate-x-4 scale-95"
                      enter-to-class="translate-y-0 opacity-100 sm:translate-x-0 scale-100"
                      leave-active-class="transition ease-in duration-150"
                      leave-from-class="opacity-100 scale-100"
                      leave-to-class="opacity-0 scale-95">
      <div v-for="toast in toasts" :key="toast.id"
           class="pointer-events-auto px-4 py-3 rounded-2xl shadow-2xl border backdrop-blur-xl flex items-center gap-3 text-sm font-semibold tracking-wide transition-all duration-200"
           :class="toast.type === 'success' 
              ? 'bg-emerald-600/95 dark:bg-emerald-500/95 text-white border-emerald-400/50 shadow-emerald-500/25 ring-1 ring-emerald-300/30' 
              : toast.type === 'error'
              ? 'bg-rose-600/95 dark:bg-rose-500/95 text-white border-rose-400/50 shadow-rose-500/25 ring-1 ring-rose-300/30' 
              : 'bg-slate-900/95 dark:bg-slate-800/95 text-white border-slate-700/60 shadow-slate-900/30 ring-1 ring-slate-600/30'">
        <span class="text-base shrink-0">{{ toast.icon }}</span>
        <span class="leading-snug break-words">{{ toast.text }}</span>
      </div>
    </transition-group>
  </div>
</template>

<script setup>
import { useToast } from '@/composables/useToast';

const { toasts } = useToast();
</script>
