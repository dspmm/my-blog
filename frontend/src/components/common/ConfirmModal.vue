<template>
  <!-- 全局极客风格二次确认弹窗 (ConfirmModal) - 超级置顶 z-[100000]，盖过管理后台与所有模态框 -->
  <teleport to="body">
    <transition enter-active-class="transition duration-200 ease-out"
                enter-from-class="opacity-0"
                enter-to-class="opacity-100"
                leave-active-class="transition duration-150 ease-in"
                leave-from-class="opacity-100"
                leave-to-class="opacity-0">
      <div v-if="confirmState.isOpen"
           class="fixed inset-0 z-[100000] flex items-center justify-center p-4 sm:p-6 select-none"
           @keydown.esc="handleCancel">
        <!-- 半透明毛玻璃暗色遮罩 -->
        <div class="fixed inset-0 bg-slate-950/70 backdrop-blur-md transition-opacity"
             @click="handleCancel"></div>

        <!-- 弹窗卡片本体 -->
        <transition enter-active-class="transition duration-200 ease-out"
                    enter-from-class="opacity-0 scale-95 translate-y-2"
                    enter-to-class="opacity-100 scale-100 translate-y-0"
                    leave-active-class="transition duration-150 ease-in"
                    leave-from-class="opacity-100 scale-100 translate-y-0"
                    leave-to-class="opacity-0 scale-95 translate-y-2">
          <div v-if="confirmState.isOpen"
               class="relative w-full max-w-sm sm:max-w-md bg-white/95 dark:bg-slate-900/95 backdrop-blur-xl border border-slate-200 dark:border-slate-800 rounded-3xl shadow-2xl p-6 overflow-hidden z-10 flex flex-col gap-4">
            
            <!-- 头部装饰光晕 -->
            <div class="absolute -top-12 -right-12 w-32 h-32 rounded-full blur-2xl pointer-events-none opacity-40"
                 :class="confirmState.type === 'danger' 
                   ? 'bg-rose-500' 
                   : confirmState.type === 'warning' 
                   ? 'bg-amber-500' 
                   : 'bg-sky-500'"></div>

            <!-- 图标与标题区 -->
            <div class="flex items-start gap-4">
              <div class="w-12 h-12 rounded-2xl flex items-center justify-center text-xl shrink-0 border shadow-xs"
                   :class="confirmState.type === 'danger'
                     ? 'bg-rose-50 dark:bg-rose-950/60 border-rose-200 dark:border-rose-800 text-rose-500'
                     : confirmState.type === 'warning'
                     ? 'bg-amber-50 dark:bg-amber-950/60 border-amber-200 dark:border-amber-800 text-amber-500'
                     : 'bg-sky-50 dark:bg-sky-950/60 border-sky-200 dark:border-sky-800 text-sky-500'">
                <span>{{ confirmState.icon }}</span>
              </div>
              <div class="flex-1 min-w-0 pt-0.5">
                <h3 class="text-base font-bold text-slate-900 dark:text-white leading-tight">
                  {{ confirmState.title }}
                </h3>
                <p class="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-2 leading-relaxed whitespace-pre-line">
                  {{ confirmState.message }}
                </p>
              </div>
            </div>

            <!-- 底部操作按钮组 -->
            <div class="mt-2 flex items-center justify-end gap-3 pt-3 border-t border-slate-100 dark:border-slate-800/80">
              <button @click="handleCancel"
                      type="button"
                      class="px-4 py-2 rounded-xl text-xs sm:text-sm font-semibold text-slate-600 dark:text-slate-300 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 transition-colors cursor-pointer">
                {{ confirmState.cancelText }}
              </button>
              <button @click="handleConfirm"
                      type="button"
                      class="px-4 py-2 rounded-xl text-xs sm:text-sm font-bold text-white shadow-md active:scale-95 transition-all cursor-pointer flex items-center gap-1.5"
                      :class="confirmState.type === 'danger'
                        ? 'bg-rose-600 hover:bg-rose-700 shadow-rose-600/30 ring-1 ring-rose-400/50'
                        : confirmState.type === 'warning'
                        ? 'bg-amber-600 hover:bg-amber-700 shadow-amber-600/30 ring-1 ring-amber-400/50'
                        : 'bg-sky-600 hover:bg-sky-700 shadow-sky-600/30 ring-1 ring-sky-400/50'">
                <span>{{ confirmState.confirmText }}</span>
              </button>
            </div>
          </div>
        </transition>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue';
import { useConfirm } from '@/composables/useConfirm';

const { confirmState, handleConfirm, handleCancel } = useConfirm();

function onKeydown(e) {
  if (confirmState.value.isOpen && e.key === 'Escape') {
    handleCancel();
  }
}

onMounted(() => {
  window.addEventListener('keydown', onKeydown);
});

onUnmounted(() => {
  window.removeEventListener('keydown', onKeydown);
});
</script>
