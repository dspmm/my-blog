<template>
  <div class="flex flex-col min-h-screen relative overflow-x-clip pb-16 md:pb-0" :class="{ 'lg:h-screen lg:overflow-hidden': currentView === 'home' }">
    <!-- 极光背景与光标交互 -->
    <AmbientLight :showProgress="currentView === 'post'" :progress="readingProgress" />

    <!-- 全局浮动通知提示 -->
    <ToastContainer />

    <!-- 顶部导航栏 -->
    <AppHeader
      :currentView="currentView"
      :backendConnected="backendConnected"
      @navigate="navigateView"
      @open-publish="openPublishModal"
      @open-notifications="openNotificationModal"
      @open-feedback="showFeedbackModal = true"
      @open-admin="openAdminDashboard"
      @open-profile="openProfileModal"
      @open-auth="openAuthModal"
      @open-mobile-drawer="showMobileDrawer = true"
    />

    <!-- 移动端全站侧滑抽屉 -->
    <MobileDrawer
      :show="showMobileDrawer"
      :currentUser="currentUser"
      :isAdmin="isAdmin"
      :currentView="currentView"
      :unreadNoticeCount="unreadNoticeCount"
      :categories="categories"
      :selectedCategory="selectedCategory"
      @close="showMobileDrawer = false"
      @navigate="navigateView"
      @open-profile="openProfileModal"
      @open-notifications="openNotificationModal"
      @open-feedback="showFeedbackModal = true"
      @open-admin="openAdminDashboard"
      @open-publish="openPublishModal"
      @select-category="filterByCategory"
      @open-auth="openAuthModal"
      @logout="handleLogout"
    />

    <!-- ======================= 核心视图切换 ======================= -->
    <!-- 视图 1：主页文章流 -->
    <HomeView
      v-if="currentView === 'home'"
      :stats="stats"
      :categories="categories"
      :selectedCategory="selectedCategory"
      :allPosts="allPosts"
      :posts="posts"
      :displayedPosts="displayedPosts"
      :trendingPosts="trendingPosts"
      :allTags="allTags"
      :selectedTag="selectedTag"
      :recentComments="recentComments"
      :systemPing="systemPing"
      :searchKeyword="searchKeyword"
      :sortMode="sortMode"
      :getPostsCountByCategory="getPostsCountByCategory"
      :getPostTitle="getPostTitle"
      @select-category="filterByCategory"
      @update:sortMode="sortMode = $event"
      @update:searchKeyword="searchKeyword = $event; onSearchInput();"
      @search="fetchPosts"
      @filter-tag="filterByTag"
      @clear-tag="selectedTag = ''"
      @view-post="viewPost"
      @like-post="likePostAction"
      @reset-filters="resetFilters"
      @jump-post="jumpToPost"
      @open-feedback="showFeedbackModal = true"
    />

    <!-- 视图 2：沉浸式文章详情阅读页 -->
    <PostDetailView
      v-else-if="currentView === 'post' && activePost"
      :post="activePost"
      :renderedContent="renderedPostContent"
      :toc="postToc"
      :activeTocId="activeTocId"
      :trendingPosts="trendingPosts"
      :comments="comments"
      :newComment="newComment"
      :submittingComment="submittingComment"
      :currentUser="currentUser"
      :isLoggedIn="isLoggedIn"
      :isAdmin="isAdmin"
      :highlightCommentId="highlightTargetCommentId"
      @back="backToHome"
      @share="copyShareLink"
      @like="likePostAction"
      @delete-post="adminDeletePostAction"
      @scroll-top="scrollToTop"
      @scroll-to-heading="scrollToHeading"
      @view-post="viewPost"
      @open-auth="openAuthModal"
      @submit-comment="submitCommentAction(currentUser, $event)"
      @toggle-comment-top="handleToggleCommentTop"
      @delete-comment="handleDeleteComment"
    />

    <!-- 视图 3：时光归档 -->
    <ArchiveView
      v-else-if="currentView === 'archive'"
      :posts="posts"
      :allPosts="allPosts"
      @view-post="viewPost"
    />

    <!-- 视图 4：专栏分类全景 -->
    <CategoriesView
      v-else-if="currentView === 'categories'"
      :categoryList="categoryList"
      :getPostsCountByCategory="getPostsCountByCategory"
      @filter-category="filterByCategory"
    />

    <!-- 视图 5：关于我与架构全景 -->
    <AboutView v-else-if="currentView === 'about'" />

    <!-- 返回顶部浮动胶囊 -->
    <ScrollTop :show="showScrollTop" @scroll-top="scrollToTop" />

    <!-- 现代化页脚 (桌面端首页内嵌至中轴滚动流，移动端与其他视图常规展示) -->
    <AppFooter v-if="currentView !== 'home'" class="mt-auto" />
    <AppFooter v-else class="lg:hidden mt-auto" />

    <!-- 移动端原生底栏导航坞 -->
    <MobileDock :currentView="currentView" @navigate="navigateView" />

    <!-- ======================= 全局弹窗模态框 ======================= -->
    <!-- 写作/编辑博客模态框 -->
    <PostEditModal
      :show="showPublishModal"
      :editingPostId="editingPostId"
      :initialPost="newPostForm"
      :availableCategories="availableCategories"
      :getCategoryDesc="getCategoryDesc"
      :submitting="publishingPost"
      @close="showPublishModal = false"
      @save="handleSavePost"
    />

    <!-- 极客综合管理控制台 (Admin Pro) -->
    <AdminDashboardModal
      :show="showAdminDashboardModal"
      :initialTab="adminTab"
      :initialCommentStatus="adminCommentInitialStatus"
      :initialNoticeSubView="adminNoticeSubView"
      :allPosts="allPosts"
      :categoryList="categoryList"
      :categories="categories"
      :adminComments="adminComments"
      :allNotifications="allNotifications"
      :userList="userList"
      :selectedUsers="selectedNoticeUsers"
      :userSearch="noticeUserSearch"
      :showUserDropdown="showNoticeUserDropdown"
      :noticeForm="newNotificationForm"
      :publishingNotice="publishingNotification"
      :getPostsCountByCategory="getPostsCountByCategory"
      :getPostTitle="getPostTitle"
      @close="showAdminDashboardModal = false"
      @view-post="viewPost"
      @open-publish="openPublishModal"
      @edit-post="openEditPost"
      @toggle-post-status="togglePostStatusAction"
      @delete-post="adminDeletePostAction"
      @open-category-modal="openCategoryModal"
      @delete-category="deleteCategoryAction"
      @refresh-comments="fetchAdminComments"
      @audit-comment="handleAuditComment"
      @toggle-comment-top="handleToggleCommentTop"
      @delete-comment="handleDeleteComment"
      @reply-feedback="onReplyFeedback"
      @set-notice-type="setNoticeType($event, fetchUserList)"
      @toggle-user-dropdown="toggleNoticeUserDropdown"
      @toggle-user="toggleNoticeUser"
      @toggle-select-all-users="toggleSelectAllNoticeUsers(userList)"
      @remove-user="removeNoticeUser"
      @update:userSearch="noticeUserSearch = $event"
      @publish-notification="publishNotificationAction(userList)"
      @delete-notification="deleteNotificationItem"
      @refresh-users="fetchUserList"
      @toggle-user-status="toggleUserStatusAction"
      @delete-user="deleteUserAction"
    />

    <!-- 读者站内消息通知中心 -->
    <NotificationModal
      :show="showNotificationModal"
      :notifications="allNotifications"
      :unreadCount="unreadNoticeCount"
      :isAdmin="isAdmin"
      @close="showNotificationModal = false"
      @mark-read="markNoticeRead($event, token)"
      @mark-all-read="markAllNoticesRead(token)"
      @clear-read="handleClearReadNotices($event)"
      @jump-notice="handleJumpNotice($event)"
      @open-feedback="showFeedbackModal = true"
      @go-audit="onGoAudit"
      @reply-feedback="onReplyFeedback"
    />

    <!-- 读者改进建议模态框 -->
    <FeedbackModal
      :show="showFeedbackModal"
      :currentUser="currentUser"
      :isLoggedIn="isLoggedIn"
      :submitting="submittingFeedback"
      @close="showFeedbackModal = false"
      @submit="submitFeedbackAction"
      @open-auth="openAuthModal"
    />

    <!-- 新建/编辑专栏分类弹窗 -->
    <CategoryModal
      :show="showCategoryModal"
      :editingId="editingCategoryId"
      :initialData="categoryFormData"
      @close="showCategoryModal = false"
      @save="handleSaveCategory"
    />

    <!-- 个人中心设置模态框 -->
    <ProfileModal
      :show="showProfileModal"
      :currentUser="currentUser"
      @close="showProfileModal = false"
      @save-profile="handleSaveProfile"
      @save-password="handleSavePassword"
    />

    <!-- 认证弹窗 (登录 / 注册) - 置于顶层 z-[70]，确保在提建议等前置弹窗触发登录时绝不被遮挡 -->
    <AuthModal
      :show="showAuthModal"
      :initialTab="authTab"
      @close="showAuthModal = false"
      @auth-success="onAuthSuccess"
    />

    <!-- 全局极客风格二次确认弹窗 - 超级置顶 z-[100000] -->
    <ConfirmModal />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue';

// Composables
import { useAuth } from '@/composables/useAuth';
import { useTheme } from '@/composables/useTheme';
import { useToast } from '@/composables/useToast';
import { useConfirm } from '@/composables/useConfirm';
import { useBlog } from '@/composables/useBlog';
import { useNotification } from '@/composables/useNotification';
import { initMouseEffects } from '@/composables/useMouseEffects';

// API
import {
  createPost as apiCreatePost,
  updatePost as apiUpdatePost,
  deletePost as apiDeletePost,
} from '@/api/posts';
import {
  createCategory as apiCreateCategory,
  updateCategory as apiUpdateCategory,
  deleteCategory as apiDeleteCategory,
} from '@/api/categories';
import {
  getAdminComments as apiGetAdminComments,
  toggleCommentTop as apiToggleCommentTop,
  deleteComment as apiDeleteComment,
  auditComment as apiAuditComment,
} from '@/api/comments';
import {
  getUsers as apiGetUsers,
  toggleUserStatus as apiToggleUserStatus,
  deleteUser as apiDeleteUser,
  updateProfile as apiUpdateProfile,
  updatePassword as apiUpdatePassword,
} from '@/api/users';
import { setupMarked } from '@/utils/markdown';

// Components
import AmbientLight from '@/components/common/AmbientLight.vue';
import ToastContainer from '@/components/common/ToastContainer.vue';
import ScrollTop from '@/components/common/ScrollTop.vue';
import AppHeader from '@/components/layout/AppHeader.vue';
import MobileDrawer from '@/components/layout/MobileDrawer.vue';
import MobileDock from '@/components/layout/MobileDock.vue';
import AppFooter from '@/components/layout/AppFooter.vue';

// Views
import HomeView from '@/views/HomeView.vue';
import PostDetailView from '@/views/PostDetailView.vue';
import ArchiveView from '@/views/ArchiveView.vue';
import CategoriesView from '@/views/CategoriesView.vue';
import AboutView from '@/views/AboutView.vue';

// Modals
import AuthModal from '@/components/modals/AuthModal.vue';
import PostEditModal from '@/components/modals/PostEditModal.vue';
import AdminDashboardModal from '@/components/modals/AdminDashboardModal.vue';
import NotificationModal from '@/components/modals/NotificationModal.vue';
import FeedbackModal from '@/components/modals/FeedbackModal.vue';
import CategoryModal from '@/components/modals/CategoryModal.vue';
import ProfileModal from '@/components/modals/ProfileModal.vue';
import ConfirmModal from '@/components/common/ConfirmModal.vue';

// Initialize core composables
const {
  token,
  currentUser,
  isLoggedIn,
  isAdmin,
  showAuthModal,
  authTab,
  showProfileModal,
  openAuthModal,
  handleLogout,
  fetchUserInfo,
} = useAuth();

const { initTheme } = useTheme();
const { showToast } = useToast();
const { showConfirm } = useConfirm();

const {
  currentView,
  navigateView,
  posts,
  allPosts,
  stats,
  selectedCategory,
  searchKeyword,
  sortMode,
  selectedTag,
  recentComments,
  systemPing,
  backendConnected,
  activePost,
  comments,
  newComment,
  submittingComment,
  categories,
  categoryList,
  availableCategories,
  readingProgress,
  postToc,
  activeTocId,
  showScrollTop,
  showMobileDrawer,
  trendingPosts,
  allTags,
  displayedPosts,
  renderedPostContent,
  getPostsCountByCategory,
  getCategoryDesc,
  fetchCategories,
  fetchAllPosts,
  fetchPosts,
  fetchStats,
  filterByCategory,
  filterByTag,
  onSearchInput,
  fetchRecentComments,
  getPostTitle,
  jumpToPost,
  viewPost,
  scrollToHeading,
  fetchComments,
  submitCommentAction,
  likePostAction,
  copyShareLink,
  scrollToTop,
  handleScroll,
  handleRoute,
  backToHome,
} = useBlog();

const {
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
} = useNotification();

const adminCommentInitialStatus = ref('ALL');
const adminNoticeSubView = ref('compose');

function onGoAudit(notice) {
  if (notice && !notice.isRead) {
    markNoticeRead(notice, token.value);
  }
  showNotificationModal.value = false;
  adminCommentInitialStatus.value = 'PENDING';
  openAdminDashboard('comments');
}

function onReplyFeedback(notice) {
  if (notice && !notice.isRead) {
    markNoticeRead(notice, token.value);
  }
  showNotificationModal.value = false;
  adminNoticeSubView.value = 'compose';
  openAdminDashboard('notifications', 'compose');
  setNoticeType('NOTICE', fetchUserList);
  newNotificationForm.value.title = `回复建议：${notice.title.replace(/^💡【[^】]+】/, '')}`;
  newNotificationForm.value.content = `亲爱的读者：\n\n您向博主提交的宝贵建议我们已经认真阅读并评估！\n\n针对您的建议，博主回复如下：\n`;
  let targetUser = notice.sender;
  if (userList.value && userList.value.length > 0) {
    const matched = userList.value.find(u => u.nickname === notice.sender || u.username === notice.sender);
    if (matched) targetUser = matched.username;
  }
  selectedNoticeUsers.value = [targetUser];
}

const highlightTargetCommentId = ref(null);

async function handleClearReadNotices(category) {
  await clearReadNotificationsAction(category, token.value);
}

async function handleJumpNotice(notice) {
  if (!notice) return;
  // 1. 审核类通知 (AUDIT) 全面取消跳转：仅在弹窗内完成标为已读，管理员审核请点击卡片专属【👉 前往审核】按钮
  if (notice.type === 'AUDIT') {
    return;
  }
  // 2. 若为读者建议且当前为管理员，直接拉起快捷回复
  if (notice.type === 'FEEDBACK' && isAdmin.value) {
    onReplyFeedback(notice);
    return;
  }
  // 3. 互动回复 (REPLY) 或关联文章的通知：跳转到对应文章并平滑滚动到评论区
  showNotificationModal.value = false;
  
  let targetPostId = notice.postId;
  // 若通知未显式存 postId，尝试从正文中的《文章标题》或标题中自动匹配
  if (!targetPostId && notice.content) {
    const match = notice.content.match(/《([^》]+)》/);
    if (match && match[1]) {
      const matchedTitle = match[1].trim();
      const found = (allPosts.value || []).find(p => p.title?.includes(matchedTitle) || matchedTitle.includes(p.title));
      if (found) targetPostId = found.id;
    }
  }

  if (targetPostId) {
    const isAlreadyAtPost = currentView.value === 'post' && activePost.value && String(activePost.value.id) === String(targetPostId);
    if (!isAlreadyAtPost) {
      let postObj = (allPosts.value || []).find(p => String(p.id) === String(targetPostId));
      if (!postObj) {
        postObj = { id: targetPostId };
      }
      await viewPost(postObj);
    }

    // 设置高亮目标评论 ID
    if (notice.commentId) {
      highlightTargetCommentId.value = notice.commentId;
      setTimeout(() => {
        highlightTargetCommentId.value = null;
      }, 3000);
    }

    // 采用轮询机制精确寻找 DOM 元素进行平滑滚动（最多重试 15 次，每次 80ms）
    let attempts = 0;
    const scrollTarget = () => {
      attempts++;
      const specificCommentEl = notice.commentId ? document.getElementById(`comment-${notice.commentId}`) : null;
      const commentsSectionEl = document.getElementById('comments-section') || document.querySelector('.comments-section');

      if (specificCommentEl) {
        specificCommentEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
      } else if (commentsSectionEl) {
        commentsSectionEl.scrollIntoView({ behavior: 'smooth', block: 'start' });
      } else if (attempts < 15) {
        setTimeout(scrollTarget, 80);
      }
    };
    setTimeout(scrollTarget, isAlreadyAtPost ? 50 : 250);
  }
}

// Modal States
const showPublishModal = ref(false);
const editingPostId = ref(null);
const publishingPost = ref(false);
const newPostForm = ref({
  title: '',
  categoryName: '全栈开发 & CI/CD',
  author: '小东',
  tags: 'SpringBoot,MySQL',
  summary: '',
  content: '',
  status: 'PUBLISHED',
});

const showAdminDashboardModal = ref(false);
const adminTab = ref('posts');
const adminComments = ref([]);
const userList = ref([]);

const showCategoryModal = ref(false);
const editingCategoryId = ref(null);
const categoryFormData = ref({ name: '', icon: '📑', description: '' });

// Reset Filters
function resetFilters() {
  searchKeyword.value = '';
  selectedCategory.value = '全部';
  selectedTag.value = '';
  sortMode.value = 'latest';
  fetchPosts();
}

async function refreshPostData() {
  // An unfiltered fetch also refreshes allPosts, so avoid repeating GET /posts.
  if (selectedCategory.value === '全部' && !searchKeyword.value.trim()) {
    await fetchPosts();
    return;
  }
  await Promise.all([fetchAllPosts(), fetchPosts()]);
}

function openPublishModal() {
  editingPostId.value = null;
  newPostForm.value = {
    title: '',
    categoryName: availableCategories.value[0] || '云原生 & Linux',
    author: '小东',
    tags: '',
    summary: '',
    content: '',
    status: 'PUBLISHED',
  };
  showPublishModal.value = true;
}

function openEditPost(post) {
  editingPostId.value = post.id;
  newPostForm.value = {
    title: post.title,
    categoryName: post.categoryName,
    tags: post.tags,
    summary: post.summary,
    content: post.content,
    status: post.status || 'PUBLISHED',
  };
  showAdminDashboardModal.value = false;
  showPublishModal.value = true;
}

async function handleSavePost(formData) {
  if (!formData.title.trim() || !formData.content.trim()) {
    showToast('文章标题与正文不能为空！', 'error');
    return;
  }
  publishingPost.value = true;
  try {
    const isEdit = !!editingPostId.value;
    let result;
    if (isEdit) {
      result = await apiUpdatePost(editingPostId.value, formData);
    } else {
      result = await apiCreatePost(formData);
    }
    if (result.code === 200) {
      showToast(isEdit ? '文章修改已成功保存！' : '文章已成功发布上线！', 'success');
      showPublishModal.value = false;
      editingPostId.value = null;
      await refreshPostData();
      if (isEdit && activePost.value && activePost.value.id === result.data?.id) {
        activePost.value = result.data;
      }
    } else {
      showToast(result.msg || '保存失败，请稍后重试', 'error');
    }
  } catch (e) {
    showToast(e.message || '保存失败，无法连接服务器', 'error');
  } finally {
    publishingPost.value = false;
  }
}

async function togglePostStatusAction(post) {
  const nextStatus = post.status === 'DRAFT' ? 'PUBLISHED' : 'DRAFT';
  try {
    const result = await apiUpdatePost(post.id, { status: nextStatus });
    if (result.code === 200) {
      post.status = nextStatus;
      showToast(nextStatus === 'PUBLISHED' ? '文章已设为正式发布' : '文章已转为草稿', 'success');
      refreshPostData();
    } else {
      showToast(result.msg || '状态更新失败', 'error');
    }
  } catch (e) {
    showToast('更新文章状态失败', 'error');
  }
}

async function adminDeletePostAction(post) {
  const confirmed = await showConfirm({
    title: '彻底删除文章',
    message: `确定要彻底删除文章《${post.title}》吗？\n删除后文章正文及所有关联评论数据将无法恢复。`,
    confirmText: '彻底删除',
    type: 'danger',
    icon: '🗑️',
  });
  if (!confirmed) return;
  try {
    const result = await apiDeletePost(post.id);
    if (result.code === 200) {
      showToast('文章已删除', 'success');
      refreshPostData();
      if (currentView.value === 'post' && activePost.value?.id === post.id) {
        navigateView('home');
      }
    } else {
      showToast(result.msg || '删除失败', 'error');
    }
  } catch (e) {
    showToast('删除失败', 'error');
  }
}

// Category Management
function openCategoryModal(cat = null) {
  if (cat) {
    editingCategoryId.value = cat.id;
    categoryFormData.value = { name: cat.name, icon: cat.icon || '📑', description: cat.description || '' };
  } else {
    editingCategoryId.value = null;
    categoryFormData.value = { name: '', icon: '📑', description: '' };
  }
  showCategoryModal.value = true;
}

async function handleSaveCategory(form) {
  if (!form.name.trim()) {
    showToast('专栏名称不能为空', 'error');
    return;
  }
  try {
    const isEdit = !!editingCategoryId.value;
    const result = isEdit ? await apiUpdateCategory(editingCategoryId.value, form) : await apiCreateCategory(form);
    if (result.code === 200) {
      showToast(isEdit ? '专栏已成功更新！' : '新专栏创建成功！', 'success');
      showCategoryModal.value = false;
      await fetchCategories();
      await refreshPostData();
    } else {
      showToast(result.msg || '保存专栏失败', 'error');
    }
  } catch (e) {
    showToast(e.message || '网络异常，保存专栏失败', 'error');
  }
}

async function deleteCategoryAction(cat) {
  const count = getPostsCountByCategory(cat.name);
  if (count > 0) {
    showToast(`无法删除专栏【${cat.name}】：当前专栏下还有 ${count} 篇文章，请先迁移或删除文章后再操作！`, 'error');
    return;
  }
  const confirmed = await showConfirm({
    title: '删除专栏分类',
    message: `确定要删除专栏【${cat.name}】吗？删除后该分类标签将被彻底移除。`,
    confirmText: '确认删除',
    type: 'danger',
    icon: '🏷️',
  });
  if (!confirmed) return;
  try {
    const result = await apiDeleteCategory(cat.id);
    if (result.code === 200) {
      showToast('专栏已删除', 'success');
      await fetchCategories();
    } else {
      showToast(result.msg || '删除专栏失败', 'error');
    }
  } catch (e) {
    showToast('删除专栏失败', 'error');
  }
}

// Admin Dashboard
function openAdminDashboard(tab = 'posts', noticeSubView = 'compose') {
  if (!isAdmin.value) {
    showToast('无权访问：此功能仅限超级管理员使用', 'error');
    return;
  }
  adminTab.value = tab;
  if (tab === 'notifications') {
    adminNoticeSubView.value = noticeSubView;
  }
  showAdminDashboardModal.value = true;
  refreshAllAdminData();
}

async function refreshAllAdminData() {
  if (!isAdmin.value) return;
  await Promise.allSettled([
    fetchPosts(),
    fetchCategories(),
    fetchAdminComments(),
    fetchNotifications(token.value),
    fetchUserList(),
  ]);
}

async function fetchAdminComments(postId) {
  try {
    const result = await apiGetAdminComments(postId);
    adminComments.value = result.data || [];
  } catch (e) {
    console.warn('获取后台评论失败:', e);
  }
}

async function handleToggleCommentTop(comment) {
  if (!isAdmin.value) {
    showToast('无权操作：需管理员权限', 'error');
    return;
  }
  try {
    const result = await apiToggleCommentTop(comment.id);
    if (result.code === 200) {
      const isNowTop = result.data?.isTop;
      comment.isTop = isNowTop;
      showToast(isNowTop ? '📌 评论已成功置顶！' : '已取消置顶', 'success');
      if (activePost.value) fetchComments(activePost.value.id);
      if (showAdminDashboardModal.value) fetchAdminComments();
    } else {
      showToast(result.msg || '操作失败', 'error');
    }
  } catch (e) {
    showToast('置顶操作失败', 'error');
  }
}

async function handleDeleteComment(comment) {
  if (!isAdmin.value) {
    showToast('无权操作：需管理员权限', 'error');
    return;
  }
  const confirmed = await showConfirm({
    title: '删除读者评论',
    message: '确定要删除这条读者评论吗？删除后该评论及其互动内容将不可恢复。',
    confirmText: '确认删除',
    type: 'danger',
    icon: '💬',
  });
  if (!confirmed) return;
  try {
    const result = await apiDeleteComment(comment.id);
    if (result.code === 200) {
      showToast('评论已成功删除', 'success');
      if (activePost.value) fetchComments(activePost.value.id);
      if (showAdminDashboardModal.value) fetchAdminComments();
      fetchRecentComments();
    } else {
      showToast(result.msg || '删除失败', 'error');
    }
  } catch (e) {
    showToast('删除评论失败', 'error');
  }
}

async function handleAuditComment(comment, status) {
  if (!isAdmin.value) {
    showToast('无权操作：需管理员权限', 'error');
    return;
  }
  try {
    const result = await apiAuditComment(comment.id, status);
    if (result.code === 200) {
      comment.status = status;
      const text = status === 'APPROVED' ? '审核通过，已公开上线' : '已成功驳回';
      showToast(`评论${text}，已自动向该读者发送站内通知`, 'success');
      if (activePost.value) fetchComments(activePost.value.id);
      fetchAdminComments();
    } else {
      showToast(result.msg || '审核操作失败', 'error');
    }
  } catch (e) {
    showToast(e.message || '审核请求失败', 'error');
  }
}

// User List Management
async function fetchUserList() {
  try {
    const result = await apiGetUsers();
    userList.value = result.data || [];
  } catch (e) {
    showToast('获取用户列表失败', 'error');
  }
}

async function toggleUserStatusAction(u) {
  const newStatus = u.status === '0' ? '1' : '0';
  try {
    const result = await apiToggleUserStatus(u.id, newStatus);
    if (result.code === 200) {
      u.status = newStatus;
      showToast(`已${newStatus === '1' ? '停用' : '启用'}用户 [${u.username}]`, 'success');
    } else {
      showToast(result.msg || '操作失败', 'error');
    }
  } catch (e) {
    showToast('网络请求失败', 'error');
  }
}

async function deleteUserAction(u) {
  const confirmed = await showConfirm({
    title: '永久删除用户',
    message: `确定要永久删除用户 [${u.username}] 吗？\n该用户账号及所有登录凭证将被永久注销。`,
    confirmText: '永久删除',
    type: 'danger',
    icon: '👤',
  });
  if (!confirmed) return;
  try {
    const result = await apiDeleteUser(u.id);
    if (result.code === 200) {
      showToast(`用户 [${u.username}] 已被删除`, 'success');
      fetchUserList();
    } else {
      showToast(result.msg || '删除失败', 'error');
    }
  } catch (e) {
    showToast('网络请求失败', 'error');
  }
}

// Notifications
function openNotificationModal() {
  showNotificationModal.value = true;
  fetchNotifications(token.value);
}

function toggleNoticeUserDropdown() {
  if (newNotificationForm.value.type === 'ANNOUNCEMENT') return;
  showNoticeUserDropdown.value = !showNoticeUserDropdown.value;
  if (showNoticeUserDropdown.value && (!userList.value || userList.value.length === 0)) {
    fetchUserList();
  }
}

// Profile Management
function openProfileModal() {
  showProfileModal.value = true;
}

async function handleSaveProfile(form) {
  try {
    const result = await apiUpdateProfile(form);
    if (result.code === 200) {
      currentUser.value.nickname = form.nickname;
      currentUser.value.email = form.email;
      currentUser.value.avatar = form.avatar;
      showToast('个人资料已成功更新', 'success');
      showProfileModal.value = false;
    } else {
      showToast(result.msg || '修改失败', 'error');
    }
  } catch (e) {
    showToast(e.message || '请求失败', 'error');
  }
}

async function handleSavePassword(pwdForm) {
  if (!pwdForm.oldPassword || !pwdForm.newPassword) {
    showToast('旧密码和新密码不能为空', 'error');
    return;
  }
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    showToast('两次输入的新密码不一致', 'error');
    return;
  }
  try {
    const result = await apiUpdatePassword(pwdForm);
    if (result.code === 200) {
      showToast('密码修改成功，请使用新密码重新登录', 'success');
      handleLogout();
      showProfileModal.value = false;
      openAuthModal('login');
    } else {
      showToast(result.msg || '修改密码失败', 'error');
    }
  } catch (e) {
    showToast(e.message || '请求失败', 'error');
  }
}

function onAuthSuccess(user) {
  if (user?.role === 'ADMIN') {
    refreshAllAdminData();
  } else {
    fetchNotifications(token.value);
  }
}

// Lifecycle
let cleanMouseEffects = null;
let pingTimer = null;
let noticePollTimer = null;
let lastNoticeFetchTime = 0;

const onWindowActive = () => {
  const now = Date.now();
  // 增加 15 秒节流防抖，避免窗口切换焦点或在 DevTools 调试时高频重复发请求
  if (!document.hidden && now - lastNoticeFetchTime > 15000) {
    lastNoticeFetchTime = now;
    fetchNotifications(token.value);
  }
};

onMounted(async () => {
  initTheme();
  setupMarked();
  cleanMouseEffects = initMouseEffects();

  window.addEventListener('scroll', handleScroll);
  window.addEventListener('popstate', handleRoute);
  window.addEventListener('hashchange', handleRoute);

  // 1. 先验证并获取当前登录用户信息
  await fetchUserInfo();

  // 2. 统一协调并发拉取基础数据，彻底消除重叠调用
  const initTasks = [
    fetchCategories(),
    fetchPosts(),
    fetchRecentComments(),
    fetchNotifications(token.value),
  ];

  // 3. 若为管理员，顺带并行拉取管理面板需要的专有数据（评论与用户列表）
  if (isAdmin.value) {
    initTasks.push(fetchAdminComments(), fetchUserList());
  }

  await Promise.allSettled(initTasks);
  lastNoticeFetchTime = Date.now();

  // 4. 处理初次进入页面时的标准 History / 兼容 Hash 路由定位
  handleRoute();

  // 5. 挂载完成后再绑定窗口可见性与焦点事件（带 15 秒节流防抖，避免刷新瞬间多重派发）
  document.addEventListener('visibilitychange', onWindowActive);
  window.addEventListener('focus', onWindowActive);

  pingTimer = setInterval(() => {
    systemPing.value = 35 + Math.floor(Math.random() * 8);
  }, 4000);

  // 定时每 30 秒轮询同步站内私信/通知
  noticePollTimer = setInterval(() => {
    lastNoticeFetchTime = Date.now();
    fetchNotifications(token.value);
  }, 30000);
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
  window.removeEventListener('popstate', handleRoute);
  window.removeEventListener('hashchange', handleRoute);
  document.removeEventListener('visibilitychange', onWindowActive);
  window.removeEventListener('focus', onWindowActive);
  if (cleanMouseEffects) cleanMouseEffects();
  if (pingTimer) clearInterval(pingTimer);
  if (noticePollTimer) clearInterval(noticePollTimer);
});
</script>
