<template>
  <!-- ======================= 个人中心设置模态框 ======================= -->
  <div v-if="show"
       class="fixed inset-0 z-50 overflow-y-auto bg-slate-900/70 backdrop-blur-sm flex items-center justify-center p-4"
       @click.self="emit('close')">
    <div class="w-full max-w-md bg-white dark:bg-slate-900 rounded-3xl shadow-2xl border border-slate-200 dark:border-slate-800 overflow-hidden animate-in fade-in zoom-in-95 duration-200">
      <div class="px-6 py-4 border-b border-slate-100 dark:border-slate-800 flex items-center justify-between">
        <h3 class="font-bold text-base text-slate-900 dark:text-white flex items-center gap-2">
          <span>👤 个人中心设置</span>
        </h3>
        <button @click="emit('close')" class="text-slate-400 hover:text-slate-600">✕</button>
      </div>

      <!-- Tabs -->
      <div class="flex border-b border-slate-100 dark:border-slate-800 text-xs font-bold">
        <button @click="profileTab = 'info'" :class="profileTab === 'info' ? 'border-b-2 border-sky-500 text-sky-600 dark:text-sky-400' : 'text-slate-400'" class="flex-1 py-3 text-center transition-colors">
          基本资料
        </button>
        <button @click="profileTab = 'pwd'" :class="profileTab === 'pwd' ? 'border-b-2 border-sky-500 text-sky-600 dark:text-sky-400' : 'text-slate-400'" class="flex-1 py-3 text-center transition-colors">
          修改密码
        </button>
      </div>

      <!-- 修改基本资料 -->
      <div v-if="profileTab === 'info'" class="p-6 space-y-4 text-xs">
        <div>
          <label class="block font-bold mb-1 text-slate-400">用户名</label>
          <input :value="currentUser?.username" disabled class="w-full px-3.5 py-2 rounded-xl bg-slate-100 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 text-slate-400 cursor-not-allowed font-mono">
        </div>
        <div>
          <label class="block font-bold mb-1 text-slate-700 dark:text-slate-300">修改昵称</label>
          <input v-model="profileForm.nickname" type="text" class="w-full px-3.5 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:ring-2 focus:ring-sky-500">
        </div>
        <div>
          <label class="block font-bold mb-1 text-slate-700 dark:text-slate-300">联系邮箱</label>
          <input v-model="profileForm.email" type="email" class="w-full px-3.5 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:ring-2 focus:ring-sky-500">
        </div>
        <div>
          <label class="block font-bold mb-1.5 text-slate-700 dark:text-slate-300">选择头像 Emoji</label>
          <div class="flex flex-wrap gap-2">
            <button v-for="av in ['👨‍💻', '🚀', '💻', '🧠', '⚡', '🐬', '🌟', '🛡️']" :key="av"
                    @click="profileForm.avatar = av"
                    :class="profileForm.avatar === av ? 'ring-2 ring-sky-500 scale-110' : 'opacity-70 hover:opacity-100'"
                    class="w-9 h-9 rounded-xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center text-lg transition-all">
              {{ av }}
            </button>
          </div>
        </div>
        <button @click="emit('save-profile', profileForm)" class="w-full mt-2 py-2.5 rounded-xl bg-sky-600 hover:bg-sky-700 text-white font-bold text-xs shadow-md shadow-sky-600/30 transition-all">
          保存资料修改
        </button>
      </div>

      <!-- 修改密码 -->
      <div v-else class="p-6 space-y-4 text-xs">
        <div>
          <label class="block font-bold mb-1 text-slate-700 dark:text-slate-300">原密码 *</label>
          <input v-model="pwdForm.oldPassword" type="password" placeholder="输入当前使用的密码" class="w-full px-3.5 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:ring-2 focus:ring-sky-500">
        </div>
        <div>
          <label class="block font-bold mb-1 text-slate-700 dark:text-slate-300">新密码 *</label>
          <input v-model="pwdForm.newPassword" type="password" placeholder="不少于 5 位" class="w-full px-3.5 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:ring-2 focus:ring-sky-500">
        </div>
        <div>
          <label class="block font-bold mb-1 text-slate-700 dark:text-slate-300">确认新密码 *</label>
          <input v-model="pwdForm.confirmPassword" type="password" placeholder="再次输入新密码" class="w-full px-3.5 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:ring-2 focus:ring-sky-500">
        </div>
        <button @click="emit('save-password', pwdForm)" class="w-full mt-2 py-2.5 rounded-xl bg-sky-600 hover:bg-sky-700 text-white font-bold text-xs shadow-md shadow-sky-600/30 transition-all">
          确认修改密码
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  show: Boolean,
  currentUser: Object,
});

const emit = defineEmits(['close', 'save-profile', 'save-password']);

const profileTab = ref('info');
const profileForm = ref({
  nickname: props.currentUser?.nickname || '',
  email: props.currentUser?.email || '',
  avatar: props.currentUser?.avatar || '👨‍💻',
});
const pwdForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
});

watch(() => props.currentUser, (user) => {
  if (user) {
    profileForm.value = {
      nickname: user.nickname || '',
      email: user.email || '',
      avatar: user.avatar || '👨‍💻',
    };
  }
});
</script>
