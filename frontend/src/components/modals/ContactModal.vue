<template>
  <!-- ======================= 模态框：联系博主多合一沟通面板 ======================= -->
  <teleport to="body">
    <div v-if="show"
         class="fixed inset-0 z-[65] overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4"
         @click.self="emit('close')">
      <div class="w-full max-w-md bg-white dark:bg-slate-900 rounded-3xl shadow-2xl border border-slate-200 dark:border-slate-800 overflow-hidden relative animate-in fade-in zoom-in-95 duration-200">
        
        <!-- 顶部关闭按钮 -->
        <button @click="emit('close')"
                class="absolute top-4 right-4 p-2 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-xl transition-colors">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
        </button>

        <!-- 头部博主微名片 -->
        <div class="p-6 pb-4 text-center border-b border-slate-100 dark:border-slate-800/80 bg-gradient-to-b from-sky-50/50 via-white to-white dark:from-slate-800/50 dark:via-slate-900 dark:to-slate-900">
          <div class="relative w-14 h-14 mx-auto mb-3">
            <div class="w-14 h-14 rounded-2xl bg-gradient-to-br from-slate-900 via-indigo-950 to-slate-800 dark:from-sky-500 dark:to-indigo-600 flex items-center justify-center text-white text-2xl shadow-lg shadow-sky-500/20">
              👨‍💻
            </div>
            <span class="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 rounded-full bg-emerald-500 border-2 border-white dark:border-slate-900 animate-pulse" title="博主在线"></span>
          </div>
          <h3 class="text-lg font-black text-slate-900 dark:text-white flex items-center justify-center gap-1.5">
            <span>小东</span>
            <span class="text-xs text-slate-400 font-mono font-normal">@xiaodong</span>
          </h3>
          <p class="text-xs text-slate-500 dark:text-slate-400 mt-1">
            全栈架构师 · DevOps 极客 · 欢迎技术交流与探讨
          </p>
        </div>

        <!-- 通道选项内容 -->
        <div class="p-6 space-y-3.5 text-xs">
          
          <!-- 通道 1：站内私信与建言献策 (首选推荐) -->
          <div class="p-4 rounded-2xl bg-sky-50/60 dark:bg-sky-950/30 border border-sky-200/70 dark:border-sky-800/50 space-y-2.5">
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-2 font-bold text-sky-900 dark:text-sky-200">
                <span class="text-base">💡</span>
                <span>站内信 · 建言献策 (推荐)</span>
              </div>
              <span class="text-[10px] px-1.5 py-0.5 rounded-full bg-sky-500 text-white font-bold">闭环直达</span>
            </div>
            <p class="text-slate-600 dark:text-slate-400 text-[11px] leading-relaxed">
              向博主提交技术建议或交流私信。博主在管理后台查收后，可直接向您的账号发送站内回复。
            </p>
            <button @click="handleOpenFeedback"
                    class="w-full py-2.5 rounded-xl bg-gradient-to-r from-sky-600 to-indigo-600 hover:from-sky-700 hover:to-indigo-700 text-white font-bold text-xs shadow-md shadow-sky-600/20 flex items-center justify-center gap-1.5 transition-all active:scale-[0.99]">
              <span>✉️</span>
              <span>发站内信 / 提交建议</span>
            </button>
          </div>

          <!-- 通道 2：电子邮箱一键复制 -->
          <div class="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-700/60 space-y-2">
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-2 font-bold text-slate-800 dark:text-slate-200">
                <span class="text-base">📧</span>
                <span>官方电子邮箱</span>
              </div>
            </div>
            <div class="flex items-center justify-between p-2.5 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800">
              <span class="font-mono text-slate-700 dark:text-slate-300 select-all text-xs font-medium">
                {{ emailAddress }}
              </span>
              <button @click="copyEmail"
                      :class="copied ? 'bg-emerald-50 text-emerald-600 border-emerald-300 dark:bg-emerald-950 dark:text-emerald-400' : 'bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 hover:bg-sky-50 hover:text-sky-600 dark:hover:bg-slate-700'"
                      class="px-2.5 py-1 rounded-lg border border-slate-200 dark:border-slate-700 font-semibold text-[11px] transition-colors shrink-0 flex items-center gap-1">
                <span>{{ copied ? '已复制！✓' : '复制邮箱' }}</span>
              </button>
            </div>
            <div class="flex justify-end pt-0.5">
              <a :href="`mailto:${emailAddress}`"
                 class="text-[11px] text-slate-400 hover:text-sky-600 dark:hover:text-sky-400 transition-colors inline-flex items-center gap-0.5">
                <span>尝试打开系统邮件客户端 &gt;</span>
              </a>
            </div>
          </div>

        </div>

      </div>
    </div>
  </teleport>
</template>

<script setup>
import { ref } from 'vue';
import { useToast } from '@/composables/useToast';

defineProps({
  show: Boolean,
});

const emit = defineEmits(['close', 'open-feedback']);
const { showToast } = useToast();

const emailAddress = 'j2570852518@gmail.com';
const copied = ref(false);

async function copyEmail() {
  try {
    if (navigator.clipboard && navigator.clipboard.writeText) {
      await navigator.clipboard.writeText(emailAddress);
    } else {
      const textarea = document.createElement('textarea');
      textarea.value = emailAddress;
      textarea.style.position = 'fixed';
      textarea.style.opacity = '0';
      document.body.appendChild(textarea);
      textarea.select();
      document.execCommand('copy');
      document.body.removeChild(textarea);
    }
    copied.value = true;
    showToast('博主邮箱已成功复制到剪贴板！', 'success');
    setTimeout(() => {
      copied.value = false;
    }, 2500);
  } catch (err) {
    showToast('复制失败，请手动长按复制邮箱', 'error');
  }
}

function handleOpenFeedback() {
  emit('close');
  emit('open-feedback');
}
</script>
