import { ref, computed } from 'vue';
import {
  getNotifications as apiGetNotifications,
  markNotificationRead as apiMarkRead,
  markAllNotificationsRead as apiMarkAllRead,
  clearReadNotifications as apiClearRead,
  publishNotification as apiPublish,
  deleteNotification as apiDelete,
  submitFeedback as apiSubmitFeedback,
} from '@/api/notifications';
import { useToast } from './useToast';
import { useConfirm } from './useConfirm';

const allNotifications = ref([]);
const unreadNoticeCount = ref(0);
const showNotificationModal = ref(false);
const showFeedbackModal = ref(false);
const submittingFeedback = ref(false);

const newNotificationForm = ref({
  title: '',
  content: '',
  type: 'ANNOUNCEMENT',
  targetUsername: 'ALL'
});
const publishingNotification = ref(false);
const selectedNoticeUsers = ref([]);
const showNoticeUserDropdown = ref(false);
const noticeUserSearch = ref('');

export function useNotification() {
  const { showToast } = useToast();

  function getCurrentUserKey() {
    try {
      const userStr = localStorage.getItem('ry_user');
      if (userStr) {
        const u = JSON.parse(userStr);
        if (u && (u.username || u.userId)) {
          return `user_${u.username || u.userId}`;
        }
      }
    } catch (e) {}
    return 'guest';
  }

  function getLocalReadNoticeIds() {
    try {
      const key = `blog_read_notice_ids_${getCurrentUserKey()}`;
      const stored = localStorage.getItem(key);
      return stored ? new Set(JSON.parse(stored).map(Number)) : new Set();
    } catch (e) {
      return new Set();
    }
  }

  function saveLocalReadNoticeId(id) {
    try {
      const key = `blog_read_notice_ids_${getCurrentUserKey()}`;
      const set = getLocalReadNoticeIds();
      set.add(Number(id));
      localStorage.setItem(key, JSON.stringify([...set]));
    } catch (e) {}
  }

  function saveAllLocalReadNoticeIds(ids) {
    try {
      const key = `blog_read_notice_ids_${getCurrentUserKey()}`;
      const set = getLocalReadNoticeIds();
      ids.forEach(id => set.add(Number(id)));
      localStorage.setItem(key, JSON.stringify([...set]));
    } catch (e) {}
  }

  function updateUnreadCountFromNotices() {
    const hasAuth = !!localStorage.getItem('ry_token');
    const localRead = getLocalReadNoticeIds();
    unreadNoticeCount.value = allNotifications.value.filter(
      n => !n.isRead && (hasAuth || !localRead.has(Number(n.id)))
    ).length;
  }

  let isFetchingNotifications = false;

  async function fetchNotifications(token) {
    if (isFetchingNotifications) return;
    isFetchingNotifications = true;
    try {
      const result = await apiGetNotifications();
      const list = result.data || [];
      const hasAuth = !!localStorage.getItem('ry_token');
      const localRead = getLocalReadNoticeIds();
      list.forEach(n => {
        // 已登录读者以服务端 MySQL 持久化状态为准；未登录游客以当前设备本地记录为准
        if (n.isRead || (!hasAuth && localRead.has(Number(n.id)))) {
          n.isRead = true;
        }
      });
      allNotifications.value = list;
      updateUnreadCountFromNotices();
    } catch (e) {
      console.warn('获取通知列表失败:', e);
    } finally {
      isFetchingNotifications = false;
    }
  }

  async function markNoticeRead(notice, token) {
    if (notice.isRead) return;
    notice.isRead = true;
    saveLocalReadNoticeId(notice.id);
    updateUnreadCountFromNotices();
    try {
      if (token) {
        await apiMarkRead(notice.id);
      }
    } catch (e) {}
  }

  async function markAllNoticesRead(token) {
    allNotifications.value.forEach(n => (n.isRead = true));
    saveAllLocalReadNoticeIds(allNotifications.value.map(n => n.id));
    unreadNoticeCount.value = 0;
    showToast('已将全部消息标为已读', 'success');
    try {
      if (token) {
        await apiMarkAllRead();
      }
    } catch (e) {
      showToast('标记已读失败', 'error');
    }
  }

  async function clearReadNotificationsAction(category, token) {
    try {
      let cat = (category || 'ALL').toUpperCase();
      const initialLength = allNotifications.value.length;
      allNotifications.value = allNotifications.value.filter(n => {
        // 未读的严格保留，不能被清理
        if (!n.isRead) return true;
        // 如果指定了具体分类，只清理匹配分类下的已读
        if (cat === 'ANNOUNCEMENT') return n.type !== 'ANNOUNCEMENT';
        if (cat === 'REPLY') return n.type !== 'REPLY';
        if (cat === 'PRIVATE') return n.type !== 'NOTICE' && n.type !== 'SYSTEM';
        if (cat === 'AUDIT') return n.type !== 'AUDIT';
        if (cat === 'FEEDBACK') return n.type !== 'FEEDBACK';
        return false;
      });
      const cleared = initialLength - allNotifications.value.length;
      updateUnreadCountFromNotices();
      if (token) {
        await apiClearRead(category);
      }
      showToast(`已成功清理 ${cleared} 条已读通知`, 'success');
      return true;
    } catch (e) {
      showToast('清理通知失败: ' + (e.message || e), 'error');
      return false;
    }
  }

  async function publishNotificationAction(userList) {
    if (!newNotificationForm.value.title.trim() || !newNotificationForm.value.content.trim()) {
      showToast('通知标题和内容均不能为空', 'error');
      return false;
    }
    if (newNotificationForm.value.type !== 'ANNOUNCEMENT' && selectedNoticeUsers.value.length === 0) {
      showToast('请选择至少一位接收通知的用户，或切换为全站公告', 'error');
      return false;
    }

    publishingNotification.value = true;
    try {
      const isAnnouncement = newNotificationForm.value.type === 'ANNOUNCEMENT';
      const isAllSelected = userList && userList.length > 0 && selectedNoticeUsers.value.length === userList.length;
      const targets = isAnnouncement ? ['ALL'] : (isAllSelected ? ['ALL'] : selectedNoticeUsers.value);
      const payload = {
        title: newNotificationForm.value.title.trim(),
        content: newNotificationForm.value.content.trim(),
        type: newNotificationForm.value.type,
        targetUsernames: targets,
        targetUsername: targets.join(',')
      };

      const result = await apiPublish(payload);
      if (result.code === 200) {
        showToast('🎉 通知已成功向用户广播推送！', 'success');
        newNotificationForm.value = {
          title: '',
          content: '',
          type: 'ANNOUNCEMENT',
          targetUsername: 'ALL'
        };
        selectedNoticeUsers.value = [];
        showNoticeUserDropdown.value = false;
        noticeUserSearch.value = '';
        await fetchNotifications();
        return true;
      } else {
        showToast(result.msg || '发布通知失败', 'error');
      }
    } catch (e) {
      showToast(e.message || '发布通知失败', 'error');
    } finally {
      publishingNotification.value = false;
    }
    return false;
  }

  async function deleteNotificationItem(notice) {
    const { showConfirm } = useConfirm();
    const confirmed = await showConfirm({
      title: '撤回/删除通知',
      message: `确定要撤回/删除通知《${notice.title}》吗？\n所有接收人将不再显示此通知。`,
      confirmText: '确认删除',
      type: 'danger',
      icon: '📢',
    });
    if (!confirmed) return;
    try {
      const result = await apiDelete(notice.id);
      if (result.code === 200) {
        showToast('通知已移除', 'success');
        await fetchNotifications();
      } else {
        showToast(result.msg || '删除失败', 'error');
      }
    } catch (e) {
      showToast(e.message || '删除失败', 'error');
    }
  }

  function toggleNoticeUser(username) {
    const idx = selectedNoticeUsers.value.indexOf(username);
    if (idx > -1) {
      selectedNoticeUsers.value.splice(idx, 1);
    } else {
      selectedNoticeUsers.value.push(username);
    }
  }

  function toggleSelectAllNoticeUsers(userList) {
    const isAll = userList && userList.length > 0 && selectedNoticeUsers.value.length === userList.length;
    if (isAll) {
      selectedNoticeUsers.value = [];
    } else {
      selectedNoticeUsers.value = (userList || []).map(u => u.username);
    }
  }

  function removeNoticeUser(username) {
    selectedNoticeUsers.value = selectedNoticeUsers.value.filter(u => u !== username);
  }

  function setNoticeType(type, fetchUsersFn) {
    newNotificationForm.value.type = type;
    if (type === 'ANNOUNCEMENT') {
      showNoticeUserDropdown.value = false;
    } else {
      if (fetchUsersFn) fetchUsersFn();
    }
  }

  async function submitFeedbackAction(data) {
    submittingFeedback.value = true;
    try {
      const res = await apiSubmitFeedback(data);
      if (res.code === 200) {
        showToast('改进建议已成功发送给博主，非常感谢您的宝贵反馈！', 'success');
        showFeedbackModal.value = false;
        await fetchNotifications();
      } else {
        showToast(res.msg || '提交建议失败', 'error');
      }
    } catch (e) {
      showToast(e.message || '提交建议失败，请稍后重试', 'error');
    } finally {
      submittingFeedback.value = false;
    }
  }

  return {
    allNotifications,
    unreadNoticeCount,
    showNotificationModal,
    showFeedbackModal,
    submittingFeedback,
    newNotificationForm,
    publishingNotification,
    selectedNoticeUsers,
    showNoticeUserDropdown,
    noticeUserSearch,
    fetchNotifications,
    markNoticeRead,
    markAllNoticesRead,
    clearReadNotificationsAction,
    publishNotificationAction,
    deleteNotificationItem,
    submitFeedbackAction,
    toggleNoticeUser,
    toggleSelectAllNoticeUsers,
    removeNoticeUser,
    setNoticeType,
  };
}
