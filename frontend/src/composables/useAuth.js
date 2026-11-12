import { ref, computed } from 'vue';
import { login as apiLogin, register as apiRegister, sendEmailCode as apiSendEmailCode, getUserInfo } from '@/api/auth';
import { useToast } from './useToast';

const token = ref(localStorage.getItem('ry_token') || '');
const currentUser = ref(null);
const roles = ref([]);
const permissions = ref([]);

const showAuthModal = ref(false);
const authTab = ref('login'); // 'login' | 'register'
const authLoading = ref(false);
const authErrorMsg = ref('');
const loginForm = ref({ username: '', password: '' });
const registerForm = ref({ username: '', password: '', nickname: '', email: '', code: '' });

// 验证码倒计时与发信状态
const sendingCode = ref(false);
const codeCountdown = ref(0);
let countdownTimer = null;

const showUserDropdown = ref(false);
const showProfileModal = ref(false);
const profileTab = ref('info'); // 'info' | 'pwd'

export function useAuth() {
  const { showToast } = useToast();

  const isLoggedIn = computed(() => !!token.value && !!currentUser.value);
  const isAdmin = computed(() => currentUser.value && currentUser.value.role === 'ADMIN');

  function openAuthModal(tab = 'login') {
    authTab.value = tab;
    authErrorMsg.value = '';
    loginForm.value = { username: '', password: '' };
    registerForm.value = { username: '', password: '', nickname: '', email: '', code: '' };
    showAuthModal.value = true;
  }

  function quickFill(u, p) {
    authErrorMsg.value = '';
    loginForm.value.username = u;
    loginForm.value.password = p;
  }

  // 发送邮箱验证码
  async function handleSendCode() {
    const email = registerForm.value.email.trim();
    if (!email) {
      authErrorMsg.value = '请先输入电子邮箱地址';
      showToast('请先输入电子邮箱地址', 'error');
      return;
    }
    const emailRegex = /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/;
    if (!emailRegex.test(email)) {
      authErrorMsg.value = '请输入合法的电子邮箱地址';
      showToast('请输入合法的电子邮箱地址', 'error');
      return;
    }

    if (codeCountdown.value > 0 || sendingCode.value) {
      return;
    }

    sendingCode.value = true;
    authErrorMsg.value = '';

    try {
      const res = await apiSendEmailCode(email);
      if (res.code === 200) {
        showToast('验证码已发送，请检查您的邮箱（5分钟有效）', 'success');
        codeCountdown.value = 60;
        clearInterval(countdownTimer);
        countdownTimer = setInterval(() => {
          codeCountdown.value--;
          if (codeCountdown.value <= 0) {
            clearInterval(countdownTimer);
          }
        }, 1000);
      } else {
        const err = res.msg || res.message || '验证码发送失败';
        authErrorMsg.value = err;
        showToast(err, 'error');
      }
    } catch (e) {
      authErrorMsg.value = e.message || '发信服务请求异常，请稍后重试';
      showToast(authErrorMsg.value, 'error');
    } finally {
      sendingCode.value = false;
    }
  }

  async function handleLogin(onSuccess) {
    if (!loginForm.value.username.trim() || !loginForm.value.password.trim()) {
      authErrorMsg.value = '请输入用户名和密码';
      showToast('请输入用户名和密码', 'error');
      return;
    }
    authLoading.value = true;
    authErrorMsg.value = '';
    try {
      const result = await apiLogin(loginForm.value);
      if (result.code === 200) {
        token.value = result.data.token;
        localStorage.setItem('ry_token', result.data.token);
        currentUser.value = result.data.user;
        roles.value = result.data.roles;
        permissions.value = result.data.permissions;
        showAuthModal.value = false;
        showToast(`欢迎回来，${currentUser.value.nickname}！`, 'success');
        if (onSuccess) onSuccess(currentUser.value);
      } else {
        const err = result.msg || result.message || '登录失败，请检查账号密码';
        authErrorMsg.value = err;
        showToast(err, 'error');
      }
    } catch (e) {
      authErrorMsg.value = e.message || '登录服务暂不可用，请稍后重试';
      showToast(authErrorMsg.value, 'error');
    } finally {
      authLoading.value = false;
    }
  }

  async function handleRegister(onSuccess) {
    if (!registerForm.value.username.trim() || !registerForm.value.password.trim()) {
      authErrorMsg.value = '用户名和密码不能为空';
      showToast('用户名和密码不能为空', 'error');
      return;
    }
    const email = registerForm.value.email.trim();
    if (!email) {
      authErrorMsg.value = '电子邮箱不能为空';
      showToast('电子邮箱不能为空', 'error');
      return;
    }
    const emailRegex = /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/;
    if (!emailRegex.test(email)) {
      authErrorMsg.value = '请输入合法的电子邮箱地址';
      showToast('请输入合法的电子邮箱地址', 'error');
      return;
    }
    if (!registerForm.value.code.trim()) {
      authErrorMsg.value = '请输入邮箱收到的 6 位验证码';
      showToast('请输入邮箱收到的 6 位验证码', 'error');
      return;
    }

    authLoading.value = true;
    authErrorMsg.value = '';
    try {
      const result = await apiRegister(registerForm.value);
      if (result.code === 200) {
        showToast('🎉 注册成功！正在为您自动登录...', 'success');
        loginForm.value.username = registerForm.value.username;
        loginForm.value.password = registerForm.value.password;
        await handleLogin(onSuccess);
      } else {
        const err = result.msg || result.message || '注册失败';
        authErrorMsg.value = err;
        showToast(err, 'error');
      }
    } catch (e) {
      authErrorMsg.value = e.message || '注册请求失败，请稍后重试';
      showToast(authErrorMsg.value, 'error');
    } finally {
      authLoading.value = false;
    }
  }

  function handleLogout() {
    token.value = '';
    currentUser.value = null;
    roles.value = [];
    permissions.value = [];
    localStorage.removeItem('ry_token');
    showToast('已安全退出登录', 'info');
  }

  async function fetchUserInfo(onSuccess) {
    if (!token.value) return;
    try {
      const result = await getUserInfo();
      if (result.code === 200) {
        currentUser.value = result.data.user;
        roles.value = result.data.roles;
        permissions.value = result.data.permissions;
        if (onSuccess) onSuccess(currentUser.value);
        return;
      }
    } catch (e) {}
    localStorage.removeItem('ry_token');
    token.value = '';
    currentUser.value = null;
  }

  return {
    token,
    currentUser,
    roles,
    permissions,
    isLoggedIn,
    isAdmin,
    showAuthModal,
    authTab,
    authLoading,
    authErrorMsg,
    loginForm,
    registerForm,
    showUserDropdown,
    showProfileModal,
    profileTab,
    openAuthModal,
    quickFill,
    handleLogin,
    handleRegister,
    handleLogout,
    fetchUserInfo,
    sendingCode,
    codeCountdown,
    handleSendCode,
  };
}
