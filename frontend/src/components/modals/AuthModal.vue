<template>
  <!-- ======================= 模态框 1：用户认证 (登录/注册) ======================= -->
  <div v-if="show"
       class="fixed inset-0 z-[70] overflow-y-auto bg-slate-900/75 backdrop-blur-sm flex items-center justify-center p-4">
    <div class="w-full max-w-md bg-white dark:bg-slate-900 rounded-3xl shadow-2xl border border-slate-200 dark:border-slate-800 overflow-hidden relative animate-in fade-in zoom-in-95 duration-200">
      
      <!-- 顶部关闭按钮 -->
      <button @click="emit('close')"
              class="absolute top-4 right-4 p-2 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-xl transition-colors">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
      </button>

      <!-- 头部装饰 -->
      <div class="p-6 pb-0 text-center">
        <!-- 品牌专属 XD 极客 Logo -->
        <div class="w-12 h-12 mx-auto rounded-2xl overflow-hidden shadow-lg shadow-sky-500/25 border border-slate-200/80 dark:border-slate-700/60 bg-slate-900 mb-3 flex items-center justify-center">
          <img src="/logo.svg" alt="小东全栈 Logo" class="w-full h-full object-cover select-none" />
        </div>
        <h3 class="text-xl font-black text-slate-900 dark:text-white">小东全栈博客 · 认证中心</h3>
        <p class="text-xs text-slate-500 dark:text-slate-400 mt-1">统一 RBAC 读者权限与账户体系</p>
        
        <!-- 登录/注册 Tab 切换 -->
        <div class="flex mt-4 bg-slate-100 dark:bg-slate-800 p-1 rounded-xl text-xs font-bold">
          <button @click="tab = 'login'" :class="tab === 'login' ? 'bg-white dark:bg-slate-700 text-sky-600 dark:text-sky-400 shadow-sm' : 'text-slate-500'" class="flex-1 py-1.5 rounded-lg transition-all">
            账号登录
          </button>
          <button @click="tab = 'register'" :class="tab === 'register' ? 'bg-white dark:bg-slate-700 text-sky-600 dark:text-sky-400 shadow-sm' : 'text-slate-500'" class="flex-1 py-1.5 rounded-lg transition-all">
            读者注册
          </button>
        </div>
      </div>

      <!-- 登录表单 -->
      <div v-if="tab === 'login'" class="p-6 space-y-4 text-xs">
        <div v-if="authErrorMsg" class="p-3 rounded-xl bg-rose-50 dark:bg-rose-950/60 border border-rose-200 dark:border-rose-900/80 text-rose-600 dark:text-rose-400 font-semibold text-xs flex items-center gap-2 animate-in fade-in duration-150">
          <span class="text-sm shrink-0">⚠️</span>
          <span class="leading-tight">{{ authErrorMsg }}</span>
        </div>

        <div>
          <label class="block font-bold mb-1 text-slate-700 dark:text-slate-300">用户名 / 昵称 *</label>
          <input v-model="loginForm.username" type="text" placeholder="输入用户名或注册昵称" @input="clearError"
                 class="w-full px-3.5 py-2.5 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:ring-2 focus:ring-sky-500 focus:outline-none text-sm font-medium">
        </div>
        <div>
          <label class="block font-bold mb-1 text-slate-700 dark:text-slate-300">密码 *</label>
          <input v-model="loginForm.password" type="password" placeholder="输入密码" @input="clearError" @keyup.enter="handleLogin"
                 class="w-full px-3.5 py-2.5 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:ring-2 focus:ring-sky-500 focus:outline-none text-sm font-medium">
        </div>

        <button @click="handleLogin" :disabled="authLoading"
                class="w-full mt-4 py-2.5 rounded-xl bg-gradient-to-r from-sky-600 to-indigo-600 hover:from-sky-700 hover:to-indigo-700 disabled:opacity-50 text-white font-bold text-sm shadow-md shadow-sky-600/30 flex items-center justify-center gap-2">
          <span v-if="authLoading" class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
          <span>{{ authLoading ? '验证登录中...' : '立即登录' }}</span>
        </button>
      </div>

      <!-- 注册表单 -->
      <div v-else class="p-6 space-y-3.5 text-xs">
        <div v-if="authErrorMsg" class="p-3 rounded-xl bg-rose-50 dark:bg-rose-950/60 border border-rose-200 dark:border-rose-900/80 text-rose-600 dark:text-rose-400 font-semibold text-xs flex items-center gap-2 animate-in fade-in duration-150">
          <span class="text-sm shrink-0">⚠️</span>
          <span class="leading-tight">{{ authErrorMsg }}</span>
        </div>

        <div>
          <label class="block font-bold mb-1 text-slate-700 dark:text-slate-300">用户名 *</label>
          <input v-model="registerForm.username" type="text" placeholder="2-20 位字符，支持中文/英文/数字" @input="clearError"
                 class="w-full px-3.5 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:ring-2 focus:ring-sky-500 focus:outline-none">
        </div>
        <div>
          <label class="block font-bold mb-1 text-slate-700 dark:text-slate-300">个性昵称</label>
          <input v-model="registerForm.nickname" type="text" placeholder="例如: 极客小明"
                 class="w-full px-3.5 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:ring-2 focus:ring-sky-500 focus:outline-none">
        </div>
        <div>
          <label class="block font-bold mb-1 text-slate-700 dark:text-slate-300">登录密码 *</label>
          <input v-model="registerForm.password" type="password" placeholder="不少于 5 位"
                 class="w-full px-3.5 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:ring-2 focus:ring-sky-500 focus:outline-none">
        </div>
        <div>
          <label class="block font-bold mb-1 text-slate-700 dark:text-slate-300">联系邮箱 *</label>
          <input v-model="registerForm.email" type="email" placeholder="name@example.com" @input="clearError"
                 class="w-full px-3.5 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:ring-2 focus:ring-sky-500 focus:outline-none">
        </div>

        <div>
          <label class="block font-bold mb-1 text-slate-700 dark:text-slate-300">邮箱验证码 *</label>
          <div class="flex items-center gap-2">
            <input v-model="registerForm.code" type="text" maxlength="6" placeholder="输入 6 位验证码" @input="clearError"
                   class="flex-1 px-3.5 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:ring-2 focus:ring-sky-500 focus:outline-none font-mono tracking-wider">
            <button type="button"
                    @click="handleSendCode"
                    :disabled="codeCountdown > 0 || sendingCode"
                    class="px-3.5 py-2 rounded-xl font-bold text-xs transition-all shrink-0 select-none shadow-xs"
                    :class="codeCountdown > 0 || sendingCode
                      ? 'bg-slate-100 text-slate-400 dark:bg-slate-800 dark:text-slate-500 border border-slate-200 dark:border-slate-700 cursor-not-allowed'
                      : 'bg-sky-50 hover:bg-sky-100 text-sky-600 dark:bg-sky-950/80 dark:hover:bg-sky-900/80 dark:text-sky-300 border border-sky-200 dark:border-sky-800'">
              <span v-if="sendingCode">发送中...</span>
              <span v-else-if="codeCountdown > 0">{{ codeCountdown }}s 后重试</span>
              <span v-else>获取验证码</span>
            </button>
          </div>
        </div>

        <button @click="handleRegister" :disabled="authLoading"
                class="w-full mt-4 py-2.5 rounded-xl bg-gradient-to-r from-sky-600 to-indigo-600 hover:from-sky-700 hover:to-indigo-700 disabled:opacity-50 text-white font-bold text-sm shadow-md shadow-sky-600/30 flex items-center justify-center gap-2">
          <span v-if="authLoading" class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
          <span>{{ authLoading ? '创建账号中...' : '立即注册读者账号' }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useAuth } from '@/composables/useAuth';

const props = defineProps({
  show: Boolean,
  initialTab: {
    type: String,
    default: 'login',
  },
});

const emit = defineEmits(['close', 'auth-success']);

const {
  authLoading,
  authErrorMsg,
  loginForm,
  registerForm,
  sendingCode,
  codeCountdown,
  handleSendCode,
  handleLogin: doLogin,
  handleRegister: doRegister,
} = useAuth();

const tab = ref(props.initialTab);

watch(() => props.initialTab, (newVal) => {
  tab.value = newVal;
});

function clearError() {
  authErrorMsg.value = '';
}

function handleLogin() {
  doLogin((user) => {
    emit('auth-success', user);
    emit('close');
  });
}

function handleRegister() {
  doRegister((user) => {
    emit('auth-success', user);
    emit('close');
  });
}
</script>
