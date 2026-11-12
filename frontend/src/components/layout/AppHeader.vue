<template>
  <!-- 现代化顶部导航栏 -->
  <header class="sticky top-0 z-40 backdrop-blur-md bg-white/90 dark:bg-slate-900/90 border-b border-slate-200/80 dark:border-slate-800 transition-colors">
    <div class="max-w-[1440px] mx-auto px-3 sm:px-6 lg:px-8 h-14 sm:h-16 flex items-center justify-between">
      
      <!-- 品牌 Logo -->
      <div class="flex items-center gap-2.5 sm:gap-4 lg:gap-5 shrink-0">
        <a href="/" @click.prevent="navigateView('home')" class="flex items-center gap-2 sm:gap-2.5 group shrink-0">
          <div class="relative w-9 h-9 sm:w-10 sm:h-10 rounded-xl overflow-hidden shadow-md shadow-sky-500/20 group-hover:scale-105 group-hover:shadow-sky-500/40 transition-all shrink-0 border border-slate-700/40 bg-slate-900">
            <img src="/favicon.svg" alt="小东全栈 Logo" class="w-full h-full object-cover" />
          </div>
          <div class="block sm:hidden">
            <div class="font-black text-sm tracking-tight text-slate-900 dark:text-white">小东全栈</div>
          </div>
          <div class="hidden sm:block shrink-0">
            <div class="flex items-center gap-1.5">
              <span class="font-black text-base lg:text-lg tracking-tight bg-gradient-to-r from-sky-500 via-indigo-500 to-cyan-400 dark:from-sky-400 dark:via-indigo-300 dark:to-cyan-300 bg-clip-text text-transparent drop-shadow-xs select-none">
                小东的技术全栈
              </span>
              <span class="px-1.5 py-0.5 rounded-full text-[10px] font-bold font-mono bg-gradient-to-r from-sky-500/10 to-indigo-500/10 text-sky-600 dark:text-sky-300 border border-sky-300/60 dark:border-sky-700/60 shadow-xs">
                v2.0
              </span>
            </div>
            <p class="text-[11px] text-slate-400 dark:text-slate-500 font-mono tracking-tight">Full-Stack · Cloud-Native · DevOps</p>
          </div>
        </a>

        <!-- 导航链接 (PC 宽屏展示：加粗单行极客胶囊，彻底杜绝回车折断) -->
        <nav class="hidden md:flex items-center gap-0.5 sm:gap-1 shrink-0">
          <button @click="navigateView('home')"
                  :class="currentView === 'home' 
                    ? 'text-sky-600 dark:text-sky-400 bg-sky-50/90 dark:bg-sky-950/70 shadow-xs border-sky-200/80 dark:border-sky-800/80' 
                    : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white hover:bg-slate-100/80 dark:hover:bg-slate-800/60 border-transparent'"
                  class="px-2.5 lg:px-3 py-1.5 rounded-xl text-xs lg:text-sm font-bold border transition-all flex items-center gap-1 whitespace-nowrap shrink-0">
            <span>📖</span> <span>文章列表</span>
          </button>
          <button @click="navigateView('categories')"
                  :class="currentView === 'categories' 
                    ? 'text-sky-600 dark:text-sky-400 bg-sky-50/90 dark:bg-sky-950/70 shadow-xs border-sky-200/80 dark:border-sky-800/80' 
                    : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white hover:bg-slate-100/80 dark:hover:bg-slate-800/60 border-transparent'"
                  class="px-2.5 lg:px-3 py-1.5 rounded-xl text-xs lg:text-sm font-bold border transition-all flex items-center gap-1 whitespace-nowrap shrink-0">
            <span>🗂️</span> <span>专栏分类</span>
          </button>
          <button @click="navigateView('archive')"
                  :class="currentView === 'archive' 
                    ? 'text-sky-600 dark:text-sky-400 bg-sky-50/90 dark:bg-sky-950/70 shadow-xs border-sky-200/80 dark:border-sky-800/80' 
                    : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white hover:bg-slate-100/80 dark:hover:bg-slate-800/60 border-transparent'"
                  class="px-2.5 lg:px-3 py-1.5 rounded-xl text-xs lg:text-sm font-bold border transition-all flex items-center gap-1 whitespace-nowrap shrink-0">
            <span>⏳</span> <span>时光归档</span>
          </button>
          <button @click="navigateView('about')"
                  :class="currentView === 'about' 
                    ? 'text-sky-600 dark:text-sky-400 bg-sky-50/90 dark:bg-sky-950/70 shadow-xs border-sky-200/80 dark:border-sky-800/80' 
                    : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white hover:bg-slate-100/80 dark:hover:bg-slate-800/60 border-transparent'"
                  class="px-2.5 lg:px-3 py-1.5 rounded-xl text-xs lg:text-sm font-bold border transition-all flex items-center gap-1 whitespace-nowrap shrink-0">
            <span>👨‍💻</span> <span>关于我 & 架构</span>
          </button>
        </nav>
      </div>

      <!-- 右侧操作区 (移动端与 PC 自适应优雅布局) -->
      <div class="flex items-center gap-1.5 sm:gap-2 shrink-0">
        
        <!-- 写文章按钮 (加粗、强制单行) -->
        <button v-if="isAdmin" @click="emit('open-publish')"
                class="hidden sm:inline-flex items-center gap-1 px-3 py-1.5 rounded-xl text-xs lg:text-sm font-bold bg-gradient-to-r from-sky-600 to-indigo-600 hover:from-sky-700 hover:to-indigo-700 text-white shadow-md shadow-sky-600/25 transition-all hover:shadow active:scale-95 whitespace-nowrap shrink-0">
          <svg class="w-4 h-4 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 4v16m8-8H4"></path></svg>
          <span>写新文章</span>
        </button>

        <!-- 读者改进建议快速入口 -->
        <button @click="emit('open-feedback')"
                class="hidden md:flex items-center gap-1 px-2.5 py-1.5 rounded-xl text-xs font-semibold text-slate-600 dark:text-slate-300 hover:text-amber-600 dark:hover:text-amber-400 hover:bg-amber-50/70 dark:hover:bg-amber-950/40 border border-slate-200/70 dark:border-slate-800 transition-colors"
                title="向博主提交改进建议">
          <span>💡</span>
          <span class="hidden lg:inline">提建议</span>
        </button>

        <!-- 站内消息与通知铃铛按钮 (带未读动态红点) -->
        <button @click="emit('open-notifications')"
                class="p-2 rounded-xl text-slate-500 hover:text-slate-800 dark:text-slate-400 dark:hover:text-slate-100 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors relative shrink-0"
                title="站内消息与通知公告">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"></path>
          </svg>
          <span v-if="unreadNoticeCount > 0"
                class="absolute top-0.5 right-0.5 min-w-[1.05rem] h-[1.05rem] px-1 bg-rose-500 text-white rounded-full text-[10px] font-mono font-bold flex items-center justify-center animate-pulse shadow-md shadow-rose-500/50">
            {{ unreadNoticeCount > 99 ? '99+' : unreadNoticeCount }}
          </span>
        </button>

        <!-- 深色/浅色模式切换 (Linear 级 120fps 丝滑流光渐变 + 物理 360° 弹性旋钮动效) -->
        <button @click="toggleDarkMode($event)"
                class="p-2 rounded-xl text-slate-500 hover:text-slate-800 dark:text-slate-400 dark:hover:text-slate-100 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors group relative overflow-hidden active:scale-90 shrink-0"
                title="切换明暗色彩主题 (Linear 丝滑流畅动效)">
          <div class="relative w-5 h-5 flex items-center justify-center transition-transform duration-500 ease-out"
               :class="isDark ? 'rotate-[360deg]' : 'rotate-0'">
            <svg v-if="!isDark" class="w-5 h-5 text-amber-500 transform group-hover:rotate-45 group-active:scale-90 transition-transform duration-300" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M10 2a1 1 0 011 1v1a1 1 0 11-2 0V3a1 1 0 011-1zm4 8a4 4 0 11-8 0 4 4 0 018 0zm-.464 4.95l.707.707a1 1 0 001.414-1.414l-.707-.707a1 1 0 00-1.414 1.414zm2.12-10.607a1 1 0 010 1.414l-.706.707a1 1 0 11-1.414-1.414l.707-.707a1 1 0 011.414 0zM17 11a1 1 0 100-2h-1a1 1 0 100 2h1zm-7 4a1 1 0 011 1v1a1 1 0 11-2 0v-1a1 1 0 011-1zM5.05 6.464A1 1 0 106.465 5.05l-.708-.707a1 1 0 00-1.414 1.414l.707.707zm1.414 8.486l-.707.707a1 1 0 01-1.414-1.414l.707-.707a1 1 0 011.414 1.414zM4 11a1 1 0 100-2H3a1 1 0 000 2h1z" clip-rule="evenodd"></path></svg>
            <svg v-else class="w-5 h-5 text-slate-300 transform group-hover:-rotate-45 group-active:scale-90 transition-transform duration-300" fill="currentColor" viewBox="0 0 20 20"><path d="M17.293 13.293A8 8 0 016.707 2.707a8.001 8.001 0 1010.586 10.586z"></path></svg>
          </div>
        </button>

        <!-- 极客用户系统入口 -->
        <div v-if="isLoggedIn" class="relative shrink-0">
          <!-- PC 宽屏胶囊式用户菜单 (包含头像、昵称、角色勋章与下箭头) -->
          <button @click="showUserDropdown = !showUserDropdown"
                  class="hidden sm:flex items-center gap-1.5 lg:gap-2 px-2.5 lg:px-3 py-1.5 rounded-xl bg-white dark:bg-slate-900 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors border border-slate-200 dark:border-slate-800 shadow-sm whitespace-nowrap shrink-0">
            <span class="text-base shrink-0">{{ currentUser.avatar || '👨‍💻' }}</span>
            <span class="text-xs font-bold text-slate-800 dark:text-slate-200 max-w-[80px] lg:max-w-[120px] truncate">{{ currentUser.nickname }}</span>
            <span :class="isAdmin ? 'bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300 border-amber-200' : 'bg-sky-100 text-sky-800 dark:bg-sky-950 dark:text-sky-300 border-sky-200'"
                  class="px-2 py-0.5 text-[10px] font-bold rounded-full border whitespace-nowrap shrink-0">
              {{ isAdmin ? '👑 管理员' : '👤 读者' }}
            </span>
            <svg class="w-3 h-3 text-slate-400 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path></svg>
          </button>

          <!-- 手机移动端极简精致微头像 (极简圆润、杜绝文本溢出挤压，支持金冠指示) -->
          <button @click="showUserDropdown = !showUserDropdown"
                  class="sm:hidden relative w-9 h-9 rounded-xl bg-slate-100 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 flex items-center justify-center text-base shadow-sm active:scale-95 transition-transform"
                  :class="isAdmin ? 'ring-2 ring-amber-400/60' : ''"
                  title="用户中心与管理">
            <span>{{ currentUser.avatar || '👨‍💻' }}</span>
            <span v-if="isAdmin" class="absolute -top-1 -right-1 text-[10px] leading-none">👑</span>
          </button>

          <!-- 下拉菜单抽屉 -->
          <div v-show="showUserDropdown"
               class="absolute right-0 mt-2 w-56 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-2xl py-2 z-50 animate-in fade-in zoom-in-95 duration-150">
            <div class="px-4 py-2.5 border-b border-slate-100 dark:border-slate-800">
              <div class="font-bold text-xs text-slate-800 dark:text-slate-100 flex items-center justify-between gap-1.5">
                <div class="flex items-center gap-1.5 truncate">
                  <span>{{ currentUser.avatar || '👨‍💻' }}</span>
                  <span class="truncate">{{ currentUser.nickname }}</span>
                </div>
                <span v-if="isAdmin" class="px-1.5 py-0.5 rounded text-[10px] bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300 font-bold shrink-0">👑 管理员</span>
              </div>
              <div class="text-[11px] text-slate-400 font-mono mt-0.5">@{{ currentUser.username }}</div>
              <div class="text-[10px] text-slate-400 mt-0.5 truncate">{{ currentUser.email || '未绑定邮箱' }}</div>
            </div>

            <button v-if="isAdmin" @click="showUserDropdown = false; emit('open-publish')"
                    class="w-full text-left px-4 py-2 text-xs text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 flex items-center gap-2">
              <span>✍️</span> <span>写新文章</span>
            </button>

            <button v-if="isAdmin" @click="showUserDropdown = false; emit('open-admin')"
                    class="w-full text-left px-4 py-2 text-xs text-sky-600 dark:text-sky-400 font-bold hover:bg-sky-50 dark:hover:bg-sky-950/40 flex items-center gap-2">
              <span>⚡</span> <span>极客管理控制台 (Admin Pro)</span>
            </button>

            <button @click="showUserDropdown = false; emit('open-profile')"
                    class="w-full text-left px-4 py-2 text-xs text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 flex items-center gap-2">
              <span>👤</span> <span>个人中心设置</span>
            </button>

            <div class="my-1 border-t border-slate-100 dark:border-slate-800"></div>

            <button @click="showUserDropdown = false; handleLogout()"
                    class="w-full text-left px-4 py-2 text-xs text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-950/40 flex items-center gap-2 font-medium">
              <span>🚪</span> <span>退出登录</span>
            </button>
          </div>
        </div>

        <!-- 未登录状态展示登录/注册按钮 -->
        <button v-else @click="emit('open-auth', 'login')"
                class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold bg-sky-50 hover:bg-sky-100 dark:bg-sky-950/60 dark:hover:bg-sky-900/60 text-sky-700 dark:text-sky-300 border border-sky-200 dark:border-sky-800 transition-all shadow-sm active:scale-95">
          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1"></path></svg>
          <span>登录</span>
        </button>

        <!-- 移动端汉堡菜单按钮 (仅手机端显示) -->
        <button @click="emit('open-mobile-drawer')"
                class="md:hidden p-2 rounded-xl text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                title="打开全站导航抽屉">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path>
          </svg>
        </button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useAuth } from '@/composables/useAuth';
import { useTheme } from '@/composables/useTheme';
import { useNotification } from '@/composables/useNotification';

defineProps({
  currentView: {
    type: String,
    default: 'home',
  },
  backendConnected: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits([
  'navigate',
  'open-publish',
  'open-notifications',
  'open-feedback',
  'open-admin',
  'open-profile',
  'open-auth',
  'open-mobile-drawer',
]);

const { currentUser, isLoggedIn, isAdmin, showUserDropdown, handleLogout } = useAuth();
const { isDark, toggleDarkMode } = useTheme();
const { unreadNoticeCount } = useNotification();

function navigateView(view) {
  emit('navigate', view);
}
</script>
