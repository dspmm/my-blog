<template>
  <!-- ======================= 视图 1：主页文章流 (HOME VIEW - PC 宽屏三栏视口锁高与中轴独立滚动) ======================= -->
  <main class="flex-1 max-w-[1440px] w-full mx-auto px-4 sm:px-6 lg:px-8 py-3 sm:py-5 lg:h-[calc(100vh-4rem)] lg:overflow-hidden flex flex-col">
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start flex-1 lg:h-full lg:overflow-hidden">

      <!-- ================= 左侧定格侧栏 (260px~300px, lg:col-span-4 xl:col-span-3) ================= -->
      <aside class="hidden lg:block lg:col-span-4 xl:col-span-3 space-y-5 lg:h-full lg:overflow-y-auto no-scrollbar lg:pr-1">
        <AuthorCard :stats="stats" @open-feedback="emit('open-feedback')" />
        <CategoryNavCard
          :categories="categories"
          :selectedCategory="selectedCategory"
          :totalCount="allPosts.length || posts.length"
          :getCount="getPostsCountByCategory"
          @select-category="selectCategory"
        />
        <SystemHealthCard :ping="systemPing" />
      </aside>

      <!-- ================= 中间主轴文章流 (独立滚轮控制, lg:col-span-8 xl:col-span-6) ================= -->
      <section
        ref="middleScrollRef"
        @scroll.passive="onMiddleScroll"
        class="lg:col-span-8 xl:col-span-6 space-y-3.5 sm:space-y-5 lg:h-full lg:overflow-y-auto custom-scrollbar lg:pr-2.5 flex flex-col justify-between"
      >
        <div class="space-y-3.5 sm:space-y-5 flex-1">
          <!-- 移动端精简头部博主卡片与快捷横向分类胶囊 -->
          <div class="lg:hidden space-y-2.5">
            <div class="p-3 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm flex items-center justify-between">
              <div class="flex items-center gap-2.5">
                <div class="w-10 h-10 rounded-xl bg-gradient-to-tr from-slate-900 to-indigo-950 flex items-center justify-center text-lg shadow-md shrink-0">
                  👨‍💻
                </div>
                <div>
                  <div class="flex items-center gap-1.5">
                    <h3 class="font-bold text-sm text-slate-900 dark:text-white">小东</h3>
                    <span class="inline-flex items-center gap-1 px-1.5 py-0.5 rounded-full text-[10px] font-mono font-medium bg-emerald-50 dark:bg-emerald-950/60 text-emerald-600 dark:text-emerald-400 border border-emerald-200/50 dark:border-emerald-800/50">
                      <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
                      在职·架构研发
                    </span>
                  </div>
                  <p class="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5">高并发系统 · 独立云端基建实战</p>
                </div>
              </div>
              <div class="text-right font-mono shrink-0 pl-2">
                <div class="text-xs font-black text-sky-600 dark:text-sky-400">{{ allPosts.length || posts.length }} 篇</div>
                <div class="text-[10px] text-slate-400">文章总计</div>
              </div>
            </div>

            <!-- 移动端横向滑动分类胶囊 -->
            <div class="flex items-center gap-1.5 overflow-x-auto no-scrollbar py-0.5">
              <button v-for="cat in categories" :key="cat"
                      @click="selectCategory(cat)"
                      :class="selectedCategory === cat 
                        ? 'bg-sky-500 text-white shadow-sm shadow-sky-500/30 font-bold' 
                        : 'bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-300 border border-slate-200 dark:border-slate-800'"
                      class="text-xs px-3 py-1.5 rounded-xl shrink-0 transition-all font-medium flex items-center gap-1.5">
                <span>{{ getCategoryIcon(cat) }}</span>
                <span>{{ cat }}</span>
              </button>
            </div>
          </div>

          <!-- 1. 每日极客金句 & 终端广播条 -->
          <TechBroadcastBanner />

          <!-- 2. 多维排序与检索工具栏 -->
          <div class="p-2.5 sm:p-4 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-2.5 sm:gap-3">
            <div class="grid grid-cols-3 sm:flex items-center gap-1 bg-slate-100 dark:bg-slate-800/80 p-1 rounded-xl text-xs font-semibold w-full sm:w-auto shrink-0">
              <button @click="emit('update:sortMode', 'latest')"
                      :class="sortMode === 'latest' ? 'bg-white dark:bg-slate-700 text-sky-600 dark:text-sky-400 shadow-sm' : 'text-slate-500 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'"
                      class="w-full sm:w-auto px-2.5 sm:px-3 py-1.5 rounded-lg transition-all flex items-center justify-center gap-1 text-center cursor-pointer">
                <span>⚡</span> <span>最新发布</span>
              </button>
              <button @click="emit('update:sortMode', 'views')"
                      :class="sortMode === 'views' ? 'bg-white dark:bg-slate-700 text-sky-600 dark:text-sky-400 shadow-sm' : 'text-slate-500 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'"
                      class="w-full sm:w-auto px-2.5 sm:px-3 py-1.5 rounded-lg transition-all flex items-center justify-center gap-1 text-center cursor-pointer">
                <span>🔥</span> <span>最多阅读</span>
              </button>
              <button @click="emit('update:sortMode', 'likes')"
                      :class="sortMode === 'likes' ? 'bg-white dark:bg-slate-700 text-sky-600 dark:text-sky-400 shadow-sm' : 'text-slate-500 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'"
                      class="w-full sm:w-auto px-2.5 sm:px-3 py-1.5 rounded-lg transition-all flex items-center justify-center gap-1 text-center cursor-pointer">
                <span>❤️</span> <span>点赞推荐</span>
              </button>
            </div>

            <!-- 搜索框 -->
            <div class="relative flex-1 max-w-full sm:max-w-sm w-full sm:w-auto flex items-center">
              <svg class="w-3.5 h-3.5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
              <input :value="searchKeyword"
                     @input="emit('update:searchKeyword', $event.target.value)"
                     type="text"
                     placeholder="输入关键词实时检索..."
                     class="w-full pl-9 pr-8 py-2 text-xs rounded-xl bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 focus:outline-none focus:ring-2 focus:ring-sky-500/40 transition-all leading-normal">
              <button v-if="searchKeyword"
                      @click="emit('update:searchKeyword', ''); emit('search');"
                      type="button"
                      class="absolute right-2.5 top-1/2 -translate-y-1/2 w-4 h-4 rounded-full flex items-center justify-center text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors"
                      title="清空搜索内容">
                <svg class="w-2.5 h-2.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>

          <!-- 激活标签过滤胶囊提示 -->
          <div v-if="selectedTag" class="flex items-center justify-between px-4 py-2 bg-sky-50 dark:bg-sky-950/50 rounded-xl border border-sky-200 dark:border-sky-800 text-xs">
            <div class="flex items-center gap-2 text-sky-700 dark:text-sky-300">
              <span>🏷️</span>
              <span>已按标签筛选：<strong class="font-mono">#{{ selectedTag }}</strong> (共 {{ displayedPosts.length }} 篇)</span>
            </div>
            <button @click="emit('clear-tag')" class="text-xs text-sky-600 hover:text-sky-800 dark:hover:text-white font-semibold cursor-pointer">
              清除筛选 ×
            </button>
          </div>

          <!-- 3. 文章卡片流 (分页展示) -->
          <div v-if="paginatedPosts.length > 0" class="space-y-4">
            <PostCard
              v-for="post in paginatedPosts"
              :key="post.id"
              :post="post"
              :selectedTag="selectedTag"
              @view="handleViewPost(post)"
              @like="emit('like-post', post)"
              @filter-tag="emit('filter-tag', $event)"
            />
          </div>

          <!-- 搜索无结果空状态 -->
          <div v-else class="text-center py-16 bg-white dark:bg-slate-900 rounded-3xl border border-slate-200/80 dark:border-slate-800 shadow-sm">
            <div class="text-4xl mb-3">🔍</div>
            <h3 class="text-base font-bold text-slate-800 dark:text-slate-200">没有检索到匹配的文章</h3>
            <p class="text-xs text-slate-500 dark:text-slate-400 mt-1 max-w-sm mx-auto">
              尝试清空搜索关键词或重置分类筛选，也可以点击右上角发布新文章！
            </p>
            <button @click="emit('reset-filters')"
                    class="mt-4 px-4 py-1.5 rounded-xl text-xs font-semibold bg-sky-50 dark:bg-sky-950 text-sky-700 dark:text-sky-300 border border-sky-200 dark:border-sky-800 cursor-pointer">
              🔄 重置所有筛选
            </button>
          </div>

          <!-- 4. 极客数字分页栏 (PAGINATION BAR) -->
          <div v-if="totalPages > 1" class="mt-4 sm:mt-6 p-3 sm:p-4 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm flex flex-col sm:flex-row items-center justify-between gap-3 text-xs">
            <!-- 左侧篇数与页码状态 -->
            <div class="text-slate-500 dark:text-slate-400 font-mono text-[11px] sm:text-xs text-center sm:text-left flex items-center gap-2">
              <span>共 <strong class="text-slate-900 dark:text-white font-bold">{{ displayedPosts.length }}</strong> 篇</span>
              <span class="text-slate-300 dark:text-slate-700">|</span>
              <span>第 <strong class="text-sky-600 dark:text-sky-400 font-bold">{{ currentPage }}</strong> / {{ totalPages }} 页</span>
            </div>

            <!-- 右侧页码控制器 -->
            <div class="flex items-center gap-1.5">
              <!-- 上一页 -->
              <button
                @click="changePage(currentPage - 1)"
                :disabled="currentPage === 1"
                class="px-2.5 sm:px-3 py-1.5 rounded-xl border border-slate-200 dark:border-slate-700 text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-800 disabled:opacity-30 disabled:cursor-not-allowed transition-all font-semibold flex items-center gap-1 cursor-pointer"
                title="上一页"
              >
                <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" /></svg>
                <span class="hidden sm:inline">上一页</span>
              </button>

              <!-- 数字页码胶囊 -->
              <div class="flex items-center gap-1">
                <template v-for="(page, index) in visiblePageNumbers" :key="index">
                  <span v-if="page === '...'" class="px-1.5 text-slate-400 select-none">…</span>
                  <button
                    v-else
                    @click="changePage(page)"
                    :class="currentPage === page 
                      ? 'bg-sky-500 text-white font-bold shadow-sm shadow-sky-500/30' 
                      : 'bg-slate-50 dark:bg-slate-800 text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-700 border border-slate-200/60 dark:border-slate-700/60'"
                    class="w-7 h-7 sm:w-8 sm:h-8 rounded-xl flex items-center justify-center font-mono font-bold transition-all cursor-pointer"
                  >
                    {{ page }}
                  </button>
                </template>
              </div>

              <!-- 下一页 -->
              <button
                @click="changePage(currentPage + 1)"
                :disabled="currentPage === totalPages"
                class="px-2.5 sm:px-3 py-1.5 rounded-xl border border-slate-200 dark:border-slate-700 text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-800 disabled:opacity-30 disabled:cursor-not-allowed transition-all font-semibold flex items-center gap-1 cursor-pointer"
                title="下一页"
              >
                <span class="hidden sm:inline">下一页</span>
                <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" /></svg>
              </button>
            </div>
          </div>
        </div>

        <!-- 5. 桌面端内嵌页脚 (滑到底部可见) -->
        <div class="hidden lg:block pt-6 pb-2 border-t border-slate-100 dark:border-slate-800/80">
          <AppFooter />
        </div>
      </section>

      <!-- ================= 右侧定格侧栏 (300px, hidden xl:block xl:col-span-3) ================= -->
      <aside class="hidden xl:block xl:col-span-3 space-y-5 xl:h-full xl:overflow-y-auto no-scrollbar xl:pl-1">
        <TrendingPostsCard :posts="trendingPosts" @view="handleViewPost($event)" />
        <TagCloudCard :tags="allTags" :selectedTag="selectedTag" @filter-tag="emit('filter-tag', $event)" />
        <RecentCommentsCard :comments="recentComments" :getPostTitle="getPostTitle" @jump="handleJumpPost($event)" />
        <CyberTerminalCard
          :stats="stats"
          :posts="allPosts.length ? allPosts : posts"
          :systemPing="systemPing"
          :categoriesCount="categories.length"
          @view-post="handleViewPost"
        />
      </aside>

    </div>
  </main>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue';
import { useBlog } from '@/composables/useBlog';
import { getCategoryIcon } from '@/utils/formatters';
import AuthorCard from '@/components/home/AuthorCard.vue';
import CategoryNavCard from '@/components/home/CategoryNavCard.vue';
import SystemHealthCard from '@/components/home/SystemHealthCard.vue';
import TechBroadcastBanner from '@/components/home/TechBroadcastBanner.vue';
import PostCard from '@/components/home/PostCard.vue';
import TrendingPostsCard from '@/components/home/TrendingPostsCard.vue';
import TagCloudCard from '@/components/home/TagCloudCard.vue';
import RecentCommentsCard from '@/components/home/RecentCommentsCard.vue';
import CyberTerminalCard from '@/components/home/CyberTerminalCard.vue';
import AppFooter from '@/components/layout/AppFooter.vue';

const props = defineProps({
  stats: Object,
  categories: Array,
  selectedCategory: String,
  allPosts: Array,
  posts: Array,
  displayedPosts: Array,
  trendingPosts: Array,
  allTags: Array,
  selectedTag: String,
  recentComments: Array,
  systemPing: Number,
  searchKeyword: String,
  sortMode: String,
  getPostsCountByCategory: Function,
  getPostTitle: Function,
});

const emit = defineEmits([
  'select-category',
  'update:sortMode',
  'update:searchKeyword',
  'search',
  'filter-tag',
  'clear-tag',
  'view-post',
  'like-post',
  'reset-filters',
  'jump-post',
  'open-feedback',
]);

const {
  currentPage,
  pageSize,
  homeScrollPosition,
  registerHomeScrollEl,
  restoreHomeScroll,
} = useBlog();

const middleScrollRef = ref(null);

onMounted(() => {
  if (middleScrollRef.value) {
    registerHomeScrollEl(middleScrollRef.value);
  }
  restoreHomeScroll();
});

onUnmounted(() => {
  registerHomeScrollEl(null);
});

function onMiddleScroll(e) {
  homeScrollPosition.value.middleTop = e.target.scrollTop;
}

function handleViewPost(post) {
  if (middleScrollRef.value) {
    homeScrollPosition.value.middleTop = middleScrollRef.value.scrollTop;
  }
  homeScrollPosition.value.windowY = window.scrollY || document.documentElement.scrollTop || 0;
  homeScrollPosition.value.page = currentPage.value;
  emit('view-post', post);
}

function handleJumpPost(postId) {
  if (middleScrollRef.value) {
    homeScrollPosition.value.middleTop = middleScrollRef.value.scrollTop;
  }
  homeScrollPosition.value.windowY = window.scrollY || document.documentElement.scrollTop || 0;
  homeScrollPosition.value.page = currentPage.value;
  emit('jump-post', postId);
}

function selectCategory(cat) {
  emit('select-category', cat);
}

// ---------------- 分页计算属性 ----------------
const totalPages = computed(() => {
  const total = props.displayedPosts?.length || 0;
  return Math.ceil(total / pageSize.value) || 1;
});

const paginatedPosts = computed(() => {
  const list = props.displayedPosts || [];
  const start = (currentPage.value - 1) * pageSize.value;
  return list.slice(start, start + pageSize.value);
});

const visiblePageNumbers = computed(() => {
  const total = totalPages.value;
  const current = currentPage.value;
  if (total <= 7) {
    return Array.from({ length: total }, (_, i) => i + 1);
  }
  if (current <= 4) {
    return [1, 2, 3, 4, 5, '...', total];
  }
  if (current >= total - 3) {
    return [1, '...', total - 4, total - 3, total - 2, total - 1, total];
  }
  return [1, '...', current - 1, current, current + 1, '...', total];
});

function changePage(page) {
  if (page < 1 || page > totalPages.value || page === currentPage.value) return;
  currentPage.value = page;
  homeScrollPosition.value.page = page;
  homeScrollPosition.value.middleTop = 0;
  middleScrollRef.value?.scrollTo({ top: 0, behavior: 'smooth' });
}

// 筛选条件变动时重置回第 1 页并滚动至顶部
watch(
  () => [props.selectedCategory, props.selectedTag, props.searchKeyword, props.sortMode],
  () => {
    currentPage.value = 1;
    homeScrollPosition.value.page = 1;
    homeScrollPosition.value.middleTop = 0;
    homeScrollPosition.value.windowY = 0;
    middleScrollRef.value?.scrollTo({ top: 0, behavior: 'smooth' });
  }
);

watch(totalPages, (newTotal) => {
  if (currentPage.value > newTotal && newTotal > 0) {
    currentPage.value = newTotal;
  }
});
</script>
