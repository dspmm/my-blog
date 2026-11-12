<template>
  <!-- ======================= 模态框：读者改进意见与反馈 (Feedback Modal) ======================= -->
  <div v-if="show"
       class="fixed inset-0 z-50 overflow-hidden bg-slate-900/60 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4"
       @click.self="emit('close')">
    <div class="w-full sm:max-w-lg bg-white dark:bg-slate-900 rounded-t-3xl sm:rounded-3xl shadow-2xl border border-slate-200 dark:border-slate-800 overflow-hidden flex flex-col max-h-[85vh] animate-in slide-in-from-bottom sm:zoom-in-95 duration-200 safe-bottom">

      <!-- 头部 Header -->
      <div class="px-5 py-4 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between shrink-0 bg-slate-50/50 dark:bg-slate-950/40">
        <div class="flex items-center gap-2.5">
          <span class="text-2xl">💡</span>
          <div>
            <h3 class="font-bold text-sm text-slate-900 dark:text-white">提交改进意见与建议</h3>
            <div class="text-[11px] text-slate-400 font-mono">欢迎为小东的极客博客建言献策，打造更好的技术社区</div>
          </div>
        </div>

        <button @click="emit('close')" class="p-1.5 rounded-lg text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 transition-colors">
          ✕
        </button>
      </div>

      <!-- 内容区域 Body -->
      <div class="p-5 overflow-y-auto flex-1 space-y-4">
        <!-- 未登录拦截提示 -->
        <div v-if="!isLoggedIn" class="p-6 rounded-2xl bg-amber-50 dark:bg-amber-950/30 border border-amber-200 dark:border-amber-800 text-center space-y-3">
          <div class="text-3xl">🔒</div>
          <h4 class="font-bold text-sm text-amber-900 dark:text-amber-200">需要登录账号</h4>
          <p class="text-xs text-amber-700 dark:text-amber-300 leading-relaxed">
            为了防止恶意广告灌水，并方便博主在收到建议后向您发送站内私信回复，请先登录读者账号。
          </p>
          <button @click="emit('open-auth', 'login')"
                  class="px-4 py-2 rounded-xl bg-amber-500 hover:bg-amber-600 text-white font-bold text-xs shadow-sm transition-colors">
            👉 立即登录 / 注册
          </button>
        </div>

        <!-- 已登录表单 -->
        <div v-else class="space-y-4">
          <!-- 用户身份条 -->
          <div class="flex items-center justify-between p-3 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/80 dark:border-slate-800 text-xs">
            <div class="flex items-center gap-2">
              <span class="text-base">{{ currentUser?.avatar || '👨‍💻' }}</span>
              <span class="text-slate-500">建议人：</span>
              <strong class="text-slate-800 dark:text-white">{{ currentUser?.nickname }}</strong>
              <span class="text-[10px] text-slate-400 font-mono">(@{{ currentUser?.username }})</span>
            </div>
            <span class="text-[11px] text-emerald-600 dark:text-emerald-400 font-medium flex items-center gap-1">
              <span>●</span> 账号已认证
            </span>
          </div>

          <!-- 建议类型选择 -->
          <div class="space-y-1.5">
            <label class="block text-xs font-bold text-slate-700 dark:text-slate-300">
              建议类型 <span class="text-rose-500">*</span>
            </label>
            <div class="grid grid-cols-2 sm:grid-cols-4 gap-2">
              <button v-for="cat in categories" :key="cat.name"
                      type="button"
                      @click="form.category = cat.name"
                      :class="form.category === cat.name
                        ? 'border-amber-500 bg-amber-50 dark:bg-amber-950/40 text-amber-700 dark:text-amber-300 font-bold ring-1 ring-amber-500/50 shadow-xs'
                        : 'border-slate-200 dark:border-slate-800 text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800'"
                      class="px-2.5 py-2 rounded-xl border text-xs flex items-center justify-center gap-1.5 transition-all">
                <span>{{ cat.icon }}</span>
                <span>{{ cat.name }}</span>
              </button>
            </div>
          </div>

          <!-- 建议主题 -->
          <div class="space-y-1.5">
            <label class="block text-xs font-bold text-slate-700 dark:text-slate-300">
              主题概要 <span class="text-rose-500">*</span>
            </label>
            <input v-model="form.title"
                   type="text"
                   maxlength="60"
                   placeholder="一句话提炼您的想法 (例如: 希望文章支持代码一键折叠)"
                   class="w-full px-3.5 py-2.5 rounded-xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200 dark:border-slate-700 text-xs sm:text-sm text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-amber-500">
          </div>

          <!-- 详细内容 -->
          <div class="space-y-1.5">
            <div class="flex items-center justify-between text-xs">
              <label class="font-bold text-slate-700 dark:text-slate-300">
                详细说明与场景 <span class="text-rose-500">*</span>
              </label>
              <span class="text-[11px] text-slate-400 font-mono">{{ form.content.length }} / 500</span>
            </div>
            <textarea v-model="form.content"
                      rows="4"
                      maxlength="500"
                      placeholder="请详细描述您的想法、具体使用场景、遇到的体验缺陷或期待的效果..."
                      class="w-full px-3.5 py-2.5 rounded-xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200 dark:border-slate-700 text-xs sm:text-sm text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-amber-500"></textarea>
          </div>
        </div>
      </div>

      <!-- 底部 Footer -->
      <div v-if="isLoggedIn" class="px-5 py-3.5 border-t border-slate-200 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-950/40 flex items-center justify-end gap-2.5 shrink-0">
        <button @click="emit('close')"
                class="px-4 py-2 rounded-xl text-xs font-semibold text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors">
          取消
        </button>
        <button @click="submit"
                :disabled="submitting || !form.title.trim() || !form.content.trim()"
                class="px-5 py-2 rounded-xl bg-amber-500 hover:bg-amber-600 disabled:opacity-50 text-white font-bold text-xs shadow-sm transition-all flex items-center gap-1.5">
          <span v-if="submitting" class="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
          <span>{{ submitting ? '提交中...' : '🚀 发送给博主' }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  show: Boolean,
  currentUser: Object,
  isLoggedIn: Boolean,
  submitting: Boolean,
});

const emit = defineEmits(['close', 'submit', 'open-auth']);

const categories = [
  { name: '功能建议', icon: '💡' },
  { name: '界面体验', icon: '🎨' },
  { name: '缺陷反馈', icon: '🐛' },
  { name: '其他想法', icon: '💬' },
];

const form = ref({
  category: '功能建议',
  title: '',
  content: '',
});

function submit() {
  if (!form.value.title.trim() || !form.value.content.trim()) return;
  emit('submit', {
    category: form.value.category,
    title: form.value.title.trim(),
    content: form.value.content.trim(),
  });
}
</script>
