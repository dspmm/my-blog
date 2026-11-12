<template>
  <!-- ======================= 移动端全站侧滑抽屉 (Mobile Slide-Over Navigation Drawer) ======================= -->
  <div v-if="show" class="fixed inset-0 z-50 overflow-hidden md:hidden animate-in fade-in duration-200">
    <!-- 遮罩层 -->
    <div class="absolute inset-0 bg-slate-900/60 backdrop-blur-sm transition-opacity"
         @click="emit('close')"></div>

    <div class="fixed inset-y-0 right-0 max-w-xs w-full bg-white dark:bg-slate-900 shadow-2xl p-6 flex flex-col justify-between z-10 border-l border-slate-200 dark:border-slate-800 animate-in slide-in-from-right duration-300">
      <div class="space-y-6">
        <!-- 顶部关闭与 Logo -->
        <div class="flex items-center justify-between pb-4 border-b border-slate-100 dark:border-slate-800">
          <div class="flex items-center gap-2.5">
            <img src="/favicon.svg" alt="小东全栈" class="w-7 h-7 rounded-lg shadow-sm border border-slate-700/50 bg-slate-900" />
            <span class="font-black text-sm text-slate-900 dark:text-white font-mono tracking-tight">小东的极客空间</span>
          </div>
          <button @click="emit('close')" class="p-2 rounded-xl text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
          </button>
        </div>

        <!-- 个人账号微卡片 (若已登录) -->
        <div v-if="currentUser" class="p-3.5 rounded-2xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200/60 dark:border-slate-700/60 flex items-center justify-between">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-xl bg-gradient-to-tr from-sky-500 to-indigo-500 flex items-center justify-center text-white font-bold text-sm shadow-md">
              {{ currentUser.avatar || currentUser.nickname?.charAt(0) || currentUser.username?.charAt(0) || 'U' }}
            </div>
            <div>
              <div class="text-xs font-bold text-slate-800 dark:text-slate-100 truncate max-w-[120px]">{{ currentUser.nickname || currentUser.username }}</div>
              <div class="text-[10px] text-slate-400 font-mono">{{ currentUser.role === 'ADMIN' ? '👑 超级管理员' : '🌟 注册极客' }}</div>
            </div>
          </div>
          <button @click="emit('close'); emit('open-profile')" class="text-xs text-sky-500 font-medium px-2 py-1 rounded-lg bg-sky-50 dark:bg-sky-950/60">资料</button>
        </div>

        <!-- 导航链接列表 -->
        <nav class="space-y-1.5 font-medium text-sm">
          <!-- 移动端消息通知入口 -->
          <button @click="emit('open-notifications'); emit('close')"
                  class="w-full text-left px-4 py-2.5 rounded-xl flex items-center justify-between text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800/60 transition-colors">
            <div class="flex items-center gap-3">
              <span class="text-base">🔔</span>
              <span>站内消息与通知</span>
            </div>
            <span v-if="unreadNoticeCount > 0" class="px-2 py-0.5 rounded-full text-[10px] font-mono font-bold bg-rose-500 text-white">
              {{ unreadNoticeCount }}
            </span>
          </button>

          <!-- 移动端改进建议入口 -->
          <button @click="emit('open-feedback'); emit('close')"
                  class="w-full text-left px-4 py-2.5 rounded-xl flex items-center justify-between text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800/60 transition-colors">
            <div class="flex items-center gap-3">
              <span class="text-base">💡</span>
              <span>提交改进建议</span>
            </div>
            <span class="text-[10px] text-amber-600 dark:text-amber-400 font-semibold bg-amber-50 dark:bg-amber-950/60 px-2 py-0.5 rounded-full">
              反馈
            </span>
          </button>

          <!-- 移动端管理员控制台入口 -->
          <button v-if="isAdmin"
                  @click="emit('open-admin'); emit('close')"
                  class="w-full text-left px-4 py-2.5 rounded-xl text-sky-600 dark:text-sky-400 bg-sky-50 dark:bg-sky-950/40 font-bold flex items-center gap-3">
            <span class="text-base">⚡</span>
            <span>极客管理控制台 (Admin Pro)</span>
          </button>
          <button @click="emit('navigate', 'home'); emit('close')"
                  :class="currentView === 'home' ? 'text-sky-600 dark:text-sky-400 bg-sky-50 dark:bg-sky-950/50 font-bold' : 'text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800/60'"
                  class="w-full text-left px-4 py-3 rounded-xl flex items-center gap-3 transition-colors">
            <span class="text-base">🏠</span>
            <span>文章门户</span>
          </button>
          <button @click="emit('navigate', 'categories'); emit('close')"
                  :class="currentView === 'categories' ? 'text-sky-600 dark:text-sky-400 bg-sky-50 dark:bg-sky-950/50 font-bold' : 'text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800/60'"
                  class="w-full text-left px-4 py-3 rounded-xl flex items-center gap-3 transition-colors">
            <span class="text-base">🏷️</span>
            <span>分类与专题</span>
          </button>
          <button @click="emit('navigate', 'archive'); emit('close')"
                  :class="currentView === 'archive' ? 'text-sky-600 dark:text-sky-400 bg-sky-50 dark:bg-sky-950/50 font-bold' : 'text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800/60'"
                  class="w-full text-left px-4 py-3 rounded-xl flex items-center gap-3 transition-colors">
            <span class="text-base">⏳</span>
            <span>文章归档</span>
          </button>
          <button @click="emit('navigate', 'about'); emit('close')"
                  :class="currentView === 'about' ? 'text-sky-600 dark:text-sky-400 bg-sky-50 dark:bg-sky-950/50 font-bold' : 'text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800/60'"
                  class="w-full text-left px-4 py-3 rounded-xl flex items-center gap-3 transition-colors">
            <span class="text-base">🚀</span>
            <span>关于与架构</span>
          </button>
          <button v-if="isAdmin"
                  @click="emit('open-publish'); emit('close')"
                  class="w-full text-left px-4 py-3 rounded-xl text-emerald-600 dark:text-emerald-400 bg-emerald-50 dark:bg-emerald-950/40 font-bold flex items-center gap-3">
            <span class="text-base">✍️</span>
            <span>发布新博客 (Markdown)</span>
          </button>
        </nav>

        <!-- 分类快速直达 -->
        <div class="pt-2 border-t border-slate-100 dark:border-slate-800">
          <div class="text-[11px] font-mono uppercase text-slate-400 mb-2 font-bold tracking-wider">快捷主题分类</div>
          <div class="flex flex-wrap gap-1.5">
            <button v-for="cat in categories" :key="cat"
                    @click="emit('select-category', cat); emit('close')"
                    :class="selectedCategory === cat ? 'bg-sky-500 text-white' : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300'"
                    class="text-xs px-2.5 py-1 rounded-lg font-medium transition-all">
              {{ cat }}
            </button>
          </div>
        </div>
      </div>

      <!-- 底部操作与登录退出 -->
      <div class="pt-4 border-t border-slate-100 dark:border-slate-800 space-y-3">
        <button v-if="!currentUser"
                @click="emit('close'); emit('open-auth', 'login')"
                class="w-full py-2.5 rounded-xl text-xs font-bold bg-sky-600 hover:bg-sky-700 text-white shadow-lg shadow-sky-500/20 flex items-center justify-center gap-2">
          <span>🔑</span>
          <span>登录 / 注册账号</span>
        </button>
        <button v-else
                @click="emit('close'); emit('logout')"
                class="w-full py-2.5 rounded-xl text-xs font-semibold text-rose-600 bg-rose-50 dark:bg-rose-950/40 hover:bg-rose-100 dark:hover:bg-rose-900/50 flex items-center justify-center gap-2">
          <span>🚪</span>
          <span>退出当前账号</span>
        </button>
        <div class="text-[10px] text-center text-slate-400 font-mono">
          小东的极客博客 · v2.1 Mobile
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  show: {
    type: Boolean,
    default: false,
  },
  currentUser: {
    type: Object,
    default: null,
  },
  isAdmin: {
    type: Boolean,
    default: false,
  },
  currentView: {
    type: String,
    default: 'home',
  },
  unreadNoticeCount: {
    type: Number,
    default: 0,
  },
  categories: {
    type: Array,
    default: () => [],
  },
  selectedCategory: {
    type: String,
    default: '全部',
  },
});

const emit = defineEmits([
  'close',
  'navigate',
  'open-profile',
  'open-notifications',
  'open-feedback',
  'open-admin',
  'open-publish',
  'select-category',
  'open-auth',
  'logout',
]);
</script>
