import { ref, computed, nextTick } from 'vue';
import {
  getPosts as apiGetPosts,
  getAllPosts as apiGetAllPosts,
  getPostById as apiGetPostById,
  likePost as apiLikePost,
  getStats as apiGetStats,
} from '@/api/posts';
import {
  getCategories as apiGetCategories,
} from '@/api/categories';
import {
  getComments as apiGetComments,
  submitComment as apiSubmitComment,
  getRecentComments as apiGetRecentComments,
} from '@/api/comments';
import { extractToc, renderMarkdownWithHeadings, bindCodeCopyButtons } from '@/utils/markdown';
import { parseTags, getCategoryIcon } from '@/utils/formatters';
import { useToast } from './useToast';

const currentView = ref('home'); // 'home' | 'post' | 'archive' | 'categories' | 'about'
const posts = ref([]);
const allPosts = ref([]);
const stats = ref({ totalPosts: 0, totalViews: 0, totalLikes: 0, totalCategories: 4 });
const selectedCategory = ref('全部');
const searchKeyword = ref('');
const sortMode = ref('latest'); // 'latest' | 'views' | 'likes'
const selectedTag = ref('');
const recentComments = ref([]);
const systemPing = ref(38);
const backendConnected = ref(false);

const activePost = ref(null);
const comments = ref([]);
const newComment = ref({ author: '访客', content: '' });
const submittingComment = ref(false);

const categories = ref(['全部']);
const categoryList = ref([]);

const readingProgress = ref(0);
const postToc = ref([]);
const activeTocId = ref('');
const showScrollTop = ref(false);
const showMobileDrawer = ref(false);
const showMobileToc = ref(false);

// 首页分页与滚动记忆持久化状态 (全局单例，跨路由销毁不丢失)
const currentPage = ref(1);
const pageSize = ref(8);
const homeScrollPosition = ref({
  middleTop: 0,
  windowY: 0,
  page: 1,
});
const homeMiddleScrollEl = ref(null);

// 默认全站基础 SEO TDK 属性
const DEFAULT_SEO = {
  title: '小东的技术全栈博客 | Full-Stack · Cloud-Native · DevOps',
  description: '小东的技术沉淀与极客空间，涵盖云原生、Linux 内核调优、Spring Boot 3、Docker 容器化、AI 本地大模型与现代前端架构。',
  keywords: '小东,技术博客,极客,云原生,Linux,Spring Boot 3,Docker,Vue3,全栈开发',
};

function updateSEO({ title, description, keywords } = {}) {
  if (typeof document === 'undefined') return;
  document.title = title || DEFAULT_SEO.title;

  let metaDesc = document.querySelector('meta[name="description"]');
  if (!metaDesc) {
    metaDesc = document.createElement('meta');
    metaDesc.setAttribute('name', 'description');
    document.head.appendChild(metaDesc);
  }
  metaDesc.setAttribute('content', description || DEFAULT_SEO.description);

  let metaKeywords = document.querySelector('meta[name="keywords"]');
  if (!metaKeywords) {
    metaKeywords = document.createElement('meta');
    metaKeywords.setAttribute('name', 'keywords');
    document.head.appendChild(metaKeywords);
  }
  metaKeywords.setAttribute('content', keywords || DEFAULT_SEO.keywords);
}

export function useBlog() {
  const { showToast } = useToast();

  const availableCategories = computed(() => categories.value.filter(c => c !== '全部'));

  const trendingPosts = computed(() => {
    const source = (allPosts.value && allPosts.value.length > 0) ? allPosts.value : posts.value;
    return source
      .slice()
      .sort((a, b) => (b.views || 0) - (a.views || 0))
      .slice(0, 5);
  });

  const allTags = computed(() => {
    const tagMap = {};
    const source = (allPosts.value && allPosts.value.length > 0) ? allPosts.value : posts.value;
    source.forEach(p => {
      const tags = parseTags(p.tags);
      tags.forEach(t => {
        if (t) tagMap[t] = (tagMap[t] || 0) + 1;
      });
    });
    return Object.entries(tagMap)
      .map(([name, count]) => ({ name, count }))
      .sort((a, b) => b.count - a.count);
  });

  const displayedPosts = computed(() => {
    let list = posts.value.slice();
    if (selectedTag.value) {
      list = list.filter(p => parseTags(p.tags).includes(selectedTag.value));
    }
    if (sortMode.value === 'views') {
      list.sort((a, b) => ((Number(b.views) || 0) - (Number(a.views) || 0)) || (b.id - a.id));
    } else if (sortMode.value === 'likes') {
      list.sort((a, b) => ((Number(b.likes) || 0) - (Number(a.likes) || 0)) || (b.id - a.id));
    } else {
      list.sort((a, b) => {
        const timeB = b.createdAt ? new Date(b.createdAt).getTime() : 0;
        const timeA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
        return (timeB - timeA) || (b.id - a.id);
      });
    }
    return list;
  });

  const renderedPostContent = computed(() => {
    if (!activePost.value || !activePost.value.content) return '';
    return renderMarkdownWithHeadings(activePost.value.content);
  });

  function registerHomeScrollEl(el) {
    homeMiddleScrollEl.value = el;
  }

  function restoreHomeScroll() {
    const applyRestore = () => {
      if (homeMiddleScrollEl.value && homeScrollPosition.value.middleTop > 0) {
        homeMiddleScrollEl.value.scrollTop = homeScrollPosition.value.middleTop;
      }
      if (homeScrollPosition.value.windowY > 0) {
        window.scrollTo({
          top: homeScrollPosition.value.windowY,
          behavior: 'instant',
        });
      }
    };

    nextTick(() => {
      applyRestore();
      requestAnimationFrame(applyRestore);
      setTimeout(applyRestore, 50);
      setTimeout(applyRestore, 150);
    });
  }

  function backToHome() {
    currentView.value = 'home';
    if (window.location.pathname !== '/' && window.location.pathname !== '/home') {
      window.history.pushState(null, '', '/');
    }
    if (window.location.hash) {
      window.history.replaceState(null, '', window.location.pathname);
    }
    updateSEO();
    restoreHomeScroll();
  }

  function navigateView(viewName) {
    if (viewName === 'home') {
      if (currentView.value === 'home') {
        // 用户已在首页再次点击“文章列表”，意图为回到顶部与重置为第 1 页
        currentPage.value = 1;
        homeScrollPosition.value.middleTop = 0;
        homeScrollPosition.value.windowY = 0;
        if (homeMiddleScrollEl.value) {
          homeMiddleScrollEl.value.scrollTo({ top: 0, behavior: 'smooth' });
        }
        window.scrollTo({ top: 0, behavior: 'smooth' });
      } else {
        // 从详情页或其他页面回到首页，精准恢复之前浏览位置与页码
        backToHome();
      }
      return;
    }

    currentView.value = viewName;
    const targetPath = '/' + viewName;
    if (window.location.pathname !== targetPath) {
      window.history.pushState(null, '', targetPath);
    }
    if (window.location.hash) {
      window.history.replaceState(null, '', targetPath);
    }
    window.scrollTo({ top: 0, behavior: 'smooth' });

    if (viewName === 'categories') {
      updateSEO({ title: '专栏分类 - 小东的技术博客' });
    } else if (viewName === 'archive') {
      updateSEO({ title: '文章归档 - 小东的技术博客' });
    } else if (viewName === 'about') {
      updateSEO({ title: '关于小东 - 小东的技术博客' });
    }
  }

  function getPostsCountByCategory(catName) {
    const source = (allPosts.value && allPosts.value.length > 0) ? allPosts.value : posts.value;
    return source.filter(p => p.categoryName === catName).length;
  }

  function getCategoryDesc(catName) {
    const found = categoryList.value.find(c => c.name === catName);
    return (found && (found.desc || found.description)) || '极客专属技术深度沉淀专栏';
  }

  let isFetchingCategories = false;
  async function fetchCategories() {
    if (isFetchingCategories) return;
    isFetchingCategories = true;
    try {
      const result = await apiGetCategories();
      const list = result.data || result || [];
      if (list.length > 0) {
        categoryList.value = list.map(c => ({
          id: c.id,
          name: c.name,
          icon: getCategoryIcon(c.name),
          desc: c.description || '沉淀架构与工程规范'
        }));
        categories.value = ['全部', ...list.map(c => c.name)];
        return;
      }
    } catch (e) {
      console.warn('获取分类数据失败，使用基础兜底:', e);
    } finally {
      isFetchingCategories = false;
    }
    categories.value = ['全部', '云原生 & Linux', 'Java & Spring Boot', 'AI & 本地模型', '全栈开发 & CI/CD'];
    categoryList.value = [
      { id: 1, name: '云原生 & Linux', icon: '☁️', desc: '云服务器运维架构、Linux 系统内核参数调优、容器网络与安全防护实战。' },
      { id: 2, name: 'Java & Spring Boot', icon: '🍃', desc: 'Spring Boot 3.2 现代化分层设计、Spring Data JPA 对象关系映射、事务控制与统一 RESTful 规范。' },
      { id: 3, name: 'AI & 本地模型', icon: '🧠', desc: 'Ollama 容器化引擎、DeepSeek R1 本地推理性能压测与加速方案、Prompt 预填充降延迟实操。' },
      { id: 4, name: '全栈开发 & CI/CD', icon: '⚙️', desc: '前后端分离微服务架构、Docker Compose 多容器生命周期管理、私有 Gitea Actions 持续交付流水线。' }
    ];
  }

  let isFetchingAllPosts = false;
  async function fetchAllPosts() {
    if (isFetchingAllPosts) return;
    isFetchingAllPosts = true;
    try {
      const result = await apiGetAllPosts();
      allPosts.value = result.data || result || [];
    } catch (e) {
      console.warn('获取全局全量文章失败:', e);
    } finally {
      isFetchingAllPosts = false;
    }
  }

  let isFetchingPosts = false;
  let postsFetchQueued = false;
  async function fetchPosts() {
    if (isFetchingPosts) {
      postsFetchQueued = true;
      return;
    }
    isFetchingPosts = true;
    let hasSuccessfulFetch = false;
    try {
      do {
        postsFetchQueued = false;
        const category = selectedCategory.value;
        const search = searchKeyword.value;
        try {
          const result = await apiGetPosts({ category, search });
          posts.value = result.data || result || [];
          backendConnected.value = true;
          hasSuccessfulFetch = true;
          if ((!category || category === '全部') && !search.trim()) {
            allPosts.value = posts.value.slice();
          }
        } catch (e) {
          backendConnected.value = false;
          console.warn('后端 API 未就绪:', e);
        }
      } while (postsFetchQueued);

      if (hasSuccessfulFetch) fetchStats();
    } finally {
      isFetchingPosts = false;
    }
  }

  async function fetchStats() {
    try {
      const result = await apiGetStats();
      stats.value = result.data || result;
    } catch (e) {}
  }

  function filterByCategory(catName) {
    selectedCategory.value = catName;
    navigateView('home');
    fetchPosts();
  }

  function filterByTag(tag) {
    if (selectedTag.value === tag) {
      selectedTag.value = '';
    } else {
      selectedTag.value = tag;
      navigateView('home');
    }
  }

  let debounceTimer = null;
  function onSearchInput() {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
      fetchPosts();
    }, 250);
  }

  async function fetchRecentComments() {
    try {
      const result = await apiGetRecentComments();
      recentComments.value = result.data || [];
    } catch (e) {
      console.warn('获取最新评论动态失败:', e);
    }
  }

  function getPostTitle(postId) {
    const source = (allPosts.value && allPosts.value.length > 0) ? allPosts.value : posts.value;
    const p = source.find(item => item.id === postId);
    return p ? p.title : '技术长文';
  }

  function jumpToPost(postId) {
    const source = (allPosts.value && allPosts.value.length > 0) ? allPosts.value : posts.value;
    const p = source.find(item => item.id === postId);
    if (p) {
      viewPost(p);
    }
  }

  async function viewPost(post) {
    if (!post) return;
    // 避免重复触发：若已在浏览当前文章则直接返回
    if (currentView.value === 'post' && activePost.value && String(activePost.value.id) === String(post.id)) {
      return;
    }

    // 记录离开首页时的浏览位置与页码
    if (currentView.value === 'home') {
      if (homeMiddleScrollEl.value) {
        homeScrollPosition.value.middleTop = homeMiddleScrollEl.value.scrollTop;
      }
      homeScrollPosition.value.windowY = window.scrollY || document.documentElement.scrollTop || 0;
      homeScrollPosition.value.page = currentPage.value;
    }

    activePost.value = post;
    currentView.value = 'post';
    const targetPath = `/post/${post.id}`;
    if (window.location.pathname !== targetPath) {
      window.history.pushState(null, '', targetPath);
    }
    if (window.location.hash) {
      window.history.replaceState(null, '', targetPath);
    }
    window.scrollTo({ top: 0, behavior: 'smooth' });

    // 动态同步文章专属 SEO TDK
    const plainDesc = post.summary || (post.content ? post.content.slice(0, 150).replace(/[#*`\n\r]/g, ' ') : '');
    updateSEO({
      title: `${post.title} - 小东的技术博客`,
      description: plainDesc,
      keywords: `${post.categoryName || '技术杂谈'},${post.tags || ''},小东,技术博客`,
    });

    post.views = (post.views || 0) + 1;
    stats.value.totalViews = (stats.value.totalViews || 0) + 1;
    fetchComments(post.id);

    postToc.value = extractToc(post.content);
    if (postToc.value.length > 0) activeTocId.value = postToc.value[0].id;

    if (backendConnected.value) {
      try {
        const res = await apiGetPostById(post.id);
        if (res && res.data && res.data.views != null) {
          post.views = res.data.views;
          if (activePost.value && activePost.value.id === post.id) {
            activePost.value.views = res.data.views;
          }
        }
      } catch (e) {
        console.warn('同步文章阅读量失败:', e);
      }
    }

    nextTick(() => {
      bindCodeCopyButtons(showToast);
    });
  }

  function scrollToHeading(id) {
    activeTocId.value = id;
    const el = document.getElementById(id);
    if (el) {
      el.scrollIntoView({ behavior: 'smooth' });
    }
  }

  async function fetchComments(postId) {
    try {
      const result = await apiGetComments(postId);
      comments.value = result.data || [];
    } catch (e) {
      comments.value = [];
    }
  }

  async function submitCommentAction(currentUser, payload = null) {
    if (!currentUser) {
      showToast('访客暂无评论权限，请先登录读者账号', 'warning');
      return false;
    }

    const isReply = !!payload;
    const content = (isReply ? payload.content : newComment.value.content) || '';
    if (!content.trim()) {
      showToast(isReply ? '回复内容不能为空' : '评论内容不能为空', 'error');
      return false;
    }

    const commentData = {
      content: content.trim(),
      author: currentUser.nickname || currentUser.username || '',
      parentId: isReply ? (payload.parentId || null) : null,
      replyTo: isReply ? (payload.replyTo || null) : null,
      replyToUsername: isReply ? (payload.replyToUsername || null) : null,
    };

    submittingComment.value = true;
    try {
      const result = await apiSubmitComment(activePost.value.id, commentData);
      if (result.code === 200) {
        if (!isReply) {
          newComment.value.content = '';
        }
        if (result.data && result.data.status === 'PENDING') {
          showToast(isReply ? '回复已提交！因命中敏感词，正在等待博主2级人工审核 (仅自己可见)' : '评论已提交！因命中敏感词规则，正在等待博主2级人工审核 (仅自己可见)', 'info');
        } else {
          showToast(isReply ? '回复发表成功！' : '评论发表成功！', 'success');
        }
        await fetchComments(activePost.value.id);
        fetchRecentComments();
        return true;
      } else {
        showToast(result.msg || (isReply ? '回复发表失败' : '评论发表失败'), 'error');
        return false;
      }
    } catch (e) {
      showToast(e.message || '网络连接异常，提交失败', 'error');
      return false;
    } finally {
      submittingComment.value = false;
    }
  }

  async function likePostAction(post) {
    post.likes = (post.likes || 0) + 1;
    stats.value.totalLikes = (stats.value.totalLikes || 0) + 1;
    showToast('感谢点赞认可！❤️', 'success');
    if (backendConnected.value) {
      apiLikePost(post.id).catch(() => {});
    }
  }

  function copyShareLink() {
    const url = activePost.value ? `${window.location.origin}/post/${activePost.value.id}` : window.location.href;
    navigator.clipboard.writeText(url).then(() => {
      showToast('文章永久链接已复制到剪贴板！', 'success');
    });
  }

  function scrollToTop() {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  function handleScroll() {
    const scrollTop = window.scrollY || document.documentElement.scrollTop;
    showScrollTop.value = scrollTop > 300;

    const scrollHeight = document.documentElement.scrollHeight - document.documentElement.clientHeight;
    if (scrollHeight > 0) {
      readingProgress.value = Math.min(100, Math.round((scrollTop / scrollHeight) * 100));
    }

    if (postToc.value.length > 0) {
      for (let i = postToc.value.length - 1; i >= 0; i--) {
        const el = document.getElementById(postToc.value[i].id);
        if (el && el.getBoundingClientRect().top <= 120) {
          activeTocId.value = postToc.value[i].id;
          break;
        }
      }
    }
  }

  function handleRoute() {
    const hash = window.location.hash;
    let path = window.location.pathname;

    // 兼容历史哈希旧链接如 #/post/12 或 #/about，自动无感清洗为真实路径
    if (hash && hash.startsWith('#/')) {
      const legacyPath = hash.replace(/^#/, '');
      window.history.replaceState(null, '', legacyPath);
      path = legacyPath;
    }

    if (path.startsWith('/post/')) {
      const id = path.replace('/post/', '').replace(/\/$/, '');
      if (currentView.value === 'post' && activePost.value && String(activePost.value.id) === String(id)) {
        return;
      }
      const source = (allPosts.value && allPosts.value.length > 0) ? allPosts.value : posts.value;
      if (source.length > 0) {
        const target = source.find(p => String(p.id) === String(id));
        if (target) {
          viewPost(target);
          return;
        }
      }
      // 容错兜底：若初始加载时列表尚未返回，直接通过 API 单查该文章呈现
      if (id && !isNaN(Number(id))) {
        apiGetPostById(id).then(res => {
          if (res && res.data) {
            viewPost(res.data);
          }
        }).catch(() => {
          backToHome();
        });
      }
    } else if (path === '/archive') {
      currentView.value = 'archive';
      updateSEO({ title: '文章归档 - 小东的技术博客' });
    } else if (path === '/categories') {
      currentView.value = 'categories';
      updateSEO({ title: '专栏分类 - 小东的技术博客' });
    } else if (path === '/about') {
      currentView.value = 'about';
      updateSEO({ title: '关于小东 - 小东的技术博客' });
    } else {
      if (currentView.value !== 'home') {
        currentView.value = 'home';
        restoreHomeScroll();
      }
      updateSEO();
    }
  }

  return {
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
    showMobileToc,
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
    handleHashRoute: handleRoute,
    updateSEO,
    currentPage,
    pageSize,
    homeScrollPosition,
    registerHomeScrollEl,
    restoreHomeScroll,
    backToHome,
  };
}
