<template>
  <!-- ======================= 模态框 2：极客综合管理控制台 (Admin Dashboard Pro) ======================= -->
  <div v-if="show"
       class="fixed inset-0 z-50 overflow-hidden bg-slate-900/75 backdrop-blur-md flex items-center justify-center p-2 sm:p-4 md:p-6"
       @click.self="emit('close')">
    <div class="w-full max-w-6xl h-[90vh] sm:h-[88vh] bg-white dark:bg-slate-900 rounded-3xl shadow-2xl border border-slate-200 dark:border-slate-800 overflow-hidden flex flex-col animate-in fade-in zoom-in-95 duration-200">
      
      <!-- 控制台头部 -->
      <div class="px-4 py-3 sm:px-6 sm:py-4 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between shrink-0 bg-slate-50/50 dark:bg-slate-950/40">
        <div class="flex items-center gap-2.5 sm:gap-3">
          <div class="w-8 h-8 sm:w-10 sm:h-10 rounded-xl sm:rounded-2xl overflow-hidden shadow-lg shadow-sky-500/25 border border-slate-200/80 dark:border-slate-700/60 bg-slate-900 flex items-center justify-center shrink-0">
            <img src="/logo.svg" alt="小东全栈 Logo" class="w-full h-full object-cover select-none" />
          </div>
          <div>
            <div class="flex items-center gap-2">
              <h3 class="font-black text-sm sm:text-base text-slate-900 dark:text-white">小东的极客管理控制台</h3>
              <span class="hidden sm:inline-block px-2 py-0.5 rounded-full text-[10px] font-mono font-bold bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300 border border-amber-200 dark:border-amber-800">
                👑 ADMIN PRO
              </span>
            </div>
            <p class="hidden sm:block text-xs text-slate-400 mt-0.5">文章专栏、读者互动、全站通知与用户权限综合中枢</p>
          </div>
        </div>

        <button @click="emit('close')" class="p-1.5 sm:p-2 rounded-xl text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800 text-base sm:text-lg transition-colors">
          ✕
        </button>
      </div>

      <!-- 导航 Tab 栏 -->
      <div class="border-b border-slate-200 dark:border-slate-800 px-3 sm:px-6 bg-white dark:bg-slate-900 shrink-0 overflow-x-auto no-scrollbar flex items-center gap-1 sm:gap-2 pt-1.5 sm:pt-2">
        <button @click="activeTab = 'posts'"
                :class="activeTab === 'posts' ? 'text-sky-600 dark:text-sky-400 border-sky-500 font-bold bg-sky-50/50 dark:bg-sky-950/40' : 'text-slate-500 dark:text-slate-400 border-transparent hover:text-slate-800 dark:hover:text-slate-200'"
                class="px-2.5 sm:px-4 py-2 sm:py-2.5 rounded-t-xl text-xs sm:text-sm font-medium border-b-2 flex items-center gap-1.5 sm:gap-2 whitespace-nowrap shrink-0 transition-colors">
          <span>📝</span> <span><span class="sm:hidden">文章</span><span class="hidden sm:inline">文章管理</span> ({{ allPosts.length }})</span>
        </button>

        <button @click="activeTab = 'categories'"
                :class="activeTab === 'categories' ? 'text-sky-600 dark:text-sky-400 border-sky-500 font-bold bg-sky-50/50 dark:bg-sky-950/40' : 'text-slate-500 dark:text-slate-400 border-transparent hover:text-slate-800 dark:hover:text-slate-200'"
                class="px-2.5 sm:px-4 py-2 sm:py-2.5 rounded-t-xl text-xs sm:text-sm font-medium border-b-2 flex items-center gap-1.5 sm:gap-2 whitespace-nowrap shrink-0 transition-colors">
          <span>🏷️</span> <span><span class="sm:hidden">专栏</span><span class="hidden sm:inline">专栏分类</span> ({{ categoryList.length }})</span>
        </button>

        <button @click="activeTab = 'comments'"
                :class="activeTab === 'comments' ? 'text-sky-600 dark:text-sky-400 border-sky-500 font-bold bg-sky-50/50 dark:bg-sky-950/40' : 'text-slate-500 dark:text-slate-400 border-transparent hover:text-slate-800 dark:hover:text-slate-200'"
                class="px-2.5 sm:px-4 py-2 sm:py-2.5 rounded-t-xl text-xs sm:text-sm font-medium border-b-2 flex items-center gap-1.5 sm:gap-2 whitespace-nowrap shrink-0 transition-colors">
          <span>💬</span> <span><span class="sm:hidden">评论</span><span class="hidden sm:inline">评论控制</span> ({{ adminComments.length }})</span>
        </button>

        <button @click="activeTab = 'notifications'"
                :class="activeTab === 'notifications' ? 'text-sky-600 dark:text-sky-400 border-sky-500 font-bold bg-sky-50/50 dark:bg-sky-950/40' : 'text-slate-500 dark:text-slate-400 border-transparent hover:text-slate-800 dark:hover:text-slate-200'"
                class="px-2.5 sm:px-4 py-2 sm:py-2.5 rounded-t-xl text-xs sm:text-sm font-medium border-b-2 flex items-center gap-1.5 sm:gap-2 whitespace-nowrap shrink-0 transition-colors">
          <span>📢</span> <span><span class="sm:hidden">通知</span><span class="hidden sm:inline">消息通知</span> ({{ allNotifications.length }})</span>
        </button>

        <button @click="activeTab = 'users'"
                :class="activeTab === 'users' ? 'text-sky-600 dark:text-sky-400 border-sky-500 font-bold bg-sky-50/50 dark:bg-sky-950/40' : 'text-slate-500 dark:text-slate-400 border-transparent hover:text-slate-800 dark:hover:text-slate-200'"
                class="px-2.5 sm:px-4 py-2 sm:py-2.5 rounded-t-xl text-xs sm:text-sm font-medium border-b-2 flex items-center gap-1.5 sm:gap-2 whitespace-nowrap shrink-0 transition-colors">
          <span>👥</span> <span><span class="sm:hidden">用户</span><span class="hidden sm:inline">用户权限</span> ({{ userList.length }})</span>
        </button>
      </div>

      <!-- Tab 内容容器 -->
      <div class="p-4 sm:p-6 overflow-y-auto flex-1 min-h-0 text-sm bg-slate-50/30 dark:bg-slate-950/20 space-y-4">
        
        <!-- ============ 选项卡 1：文章管理 ============ -->
        <div v-if="activeTab === 'posts'" class="space-y-4">
          <div class="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-3">
            <div class="flex items-center gap-2 flex-1">
              <input v-model="postsSearch" type="text" placeholder="快速检索文章标题或作者..."
                     class="px-3.5 py-1.5 text-xs rounded-xl bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 focus:outline-none focus:ring-2 focus:ring-sky-500 flex-1 max-w-xs">
              <div class="relative">
                <button type="button"
                        @click="showCatDropdown = !showCatDropdown"
                        class="px-3 py-1.5 text-xs rounded-xl bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 hover:border-sky-500 focus:outline-none flex items-center gap-1.5 font-medium whitespace-nowrap shadow-xs">
                  <span>{{ getCategoryIcon(postsCategory) }}</span>
                  <span>{{ postsCategory }}</span>
                  <svg class="w-3.5 h-3.5 text-slate-400 transition-transform" :class="showCatDropdown ? 'rotate-180 text-sky-500' : ''" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path></svg>
                </button>
                <div v-show="showCatDropdown"
                     class="absolute left-0 mt-1 z-30 min-w-[170px] bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl shadow-xl p-1 animate-in fade-in zoom-in-95 duration-100">
                  <button v-for="cat in categories" :key="cat"
                          type="button"
                          @click="postsCategory = cat; showCatDropdown = false"
                          :class="postsCategory === cat ? 'bg-sky-50 dark:bg-sky-950 text-sky-600 dark:text-sky-400 font-bold' : 'text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800'"
                          class="w-full text-left px-3 py-1.5 rounded-lg text-xs flex items-center justify-between transition-colors">
                    <span class="flex items-center gap-2">
                      <span>{{ getCategoryIcon(cat) }}</span>
                      <span>{{ cat }}</span>
                    </span>
                    <span v-if="postsCategory === cat" class="text-sky-500 text-xs font-bold">✓</span>
                  </button>
                </div>
              </div>
            </div>
            <button @click="emit('open-publish')"
                    class="px-4 py-2 rounded-xl text-xs font-bold bg-sky-600 hover:bg-sky-700 text-white flex items-center justify-center gap-1.5 shadow-md shadow-sky-500/20 shrink-0">
              <span>✍️</span> <span>写新博客</span>
            </button>
          </div>

          <!-- PC 表格 -->
          <div class="hidden md:block overflow-x-auto bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800 shadow-sm">
            <table class="w-full text-left text-xs border-collapse min-w-[780px]">
              <thead>
                <tr class="border-b border-slate-200 dark:border-slate-800 text-slate-400 bg-slate-50/60 dark:bg-slate-800/40 whitespace-nowrap">
                  <th class="py-3 px-4 min-w-[220px]">文章标题</th>
                  <th class="py-3 px-3 whitespace-nowrap">专栏分类</th>
                  <th class="py-3 px-3 whitespace-nowrap">阅读 / 点赞</th>
                  <th class="py-3 px-3 whitespace-nowrap">发布状态</th>
                  <th class="py-3 px-3 whitespace-nowrap">发布时间</th>
                  <th class="py-3 px-4 text-right whitespace-nowrap">管理操作</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-100 dark:divide-slate-800/60">
                <tr v-for="p in paginatedPosts" :key="p.id" class="hover:bg-slate-50 dark:hover:bg-slate-800/40 transition-colors">
                  <td class="py-3 px-4 max-w-xs">
                    <div class="font-bold text-slate-900 dark:text-slate-100 hover:text-sky-600 cursor-pointer truncate"
                         @click="emit('view-post', p); emit('close');">
                      {{ p.title }}
                    </div>
                    <div class="text-[10px] text-slate-400 font-mono mt-0.5 whitespace-nowrap">作者：{{ p.author }} · {{ p.readTime || '3 min' }}</div>
                  </td>
                  <td class="py-3 px-3 whitespace-nowrap">
                    <span class="px-2 py-0.5 rounded-lg text-[10px] font-semibold bg-sky-50 text-sky-700 dark:bg-sky-950 dark:text-sky-300 border border-sky-100 dark:border-sky-900/60 whitespace-nowrap inline-flex items-center gap-1">
                      {{ getCategoryIcon(p.categoryName) }} {{ p.categoryName }}
                    </span>
                  </td>
                  <td class="py-3 px-3 font-mono text-slate-600 dark:text-slate-400 whitespace-nowrap">
                    👁️ {{ p.views || 0 }} · ❤️ {{ p.likes || 0 }}
                  </td>
                  <td class="py-3 px-3 whitespace-nowrap">
                    <span :class="p.status === 'PUBLISHED' ? 'bg-emerald-50 text-emerald-600 dark:bg-emerald-950/60 dark:text-emerald-400 border-emerald-200' : 'bg-slate-100 text-slate-500 border-slate-200'"
                          class="px-2 py-0.5 rounded-full text-[10px] font-bold border whitespace-nowrap inline-block">
                      {{ p.status === 'PUBLISHED' ? '已发布' : '草稿' }}
                    </span>
                  </td>
                  <td class="py-3 px-3 text-slate-400 font-mono whitespace-nowrap">{{ formatDate(p.createdAt) }}</td>
                  <td class="py-3 px-4 text-right whitespace-nowrap">
                    <div class="flex items-center justify-end gap-1.5 whitespace-nowrap">
                      <button @click="switchTabToComments(p.id)"
                              class="px-2.5 py-1 rounded-lg text-[11px] font-semibold bg-sky-50/90 text-sky-600 hover:bg-sky-100 dark:bg-sky-950 dark:text-sky-300 whitespace-nowrap flex items-center gap-1 transition-colors"
                              title="查看此文章专属评论">
                        <span>💬</span>
                        <span>评论</span>
                        <span class="px-1.5 py-0.2 rounded-full text-[10px] bg-sky-200/60 dark:bg-sky-900 text-sky-800 dark:text-sky-200 font-mono font-bold">
                          {{ getCommentsCountByPost(p.id) }}
                        </span>
                      </button>
                      <button @click="emit('edit-post', p)" class="px-2.5 py-1 rounded-lg text-[11px] font-semibold bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-200 whitespace-nowrap transition-colors">
                        ✏️ 编辑
                      </button>
                      <button @click="emit('toggle-post-status', p)" class="px-2.5 py-1 rounded-lg text-[11px] font-semibold text-slate-600 hover:bg-slate-100 dark:text-slate-400 dark:hover:bg-slate-800 whitespace-nowrap">
                        {{ p.status === 'PUBLISHED' ? '转草稿' : '发布' }}
                      </button>
                      <button @click="emit('delete-post', p)" class="px-2.5 py-1 rounded-lg text-[11px] font-semibold text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-950/40 whitespace-nowrap">
                        删除
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- 移动端卡片 -->
          <div class="md:hidden space-y-3">
            <div v-for="p in paginatedPosts" :key="p.id" class="p-4 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm space-y-2.5">
              <div class="flex items-center justify-between">
                <span class="px-2 py-0.5 rounded-lg text-[10px] font-semibold bg-sky-50 text-sky-700 dark:bg-sky-950 dark:text-sky-300 border border-sky-100 dark:border-sky-900/60">
                  {{ getCategoryIcon(p.categoryName) }} {{ p.categoryName }}
                </span>
                <span :class="p.status === 'PUBLISHED' ? 'text-emerald-500' : 'text-slate-400'" class="text-[10px] font-bold font-mono">
                  {{ p.status === 'PUBLISHED' ? '● 已发布' : '○ 草稿' }}
                </span>
              </div>
              <h4 class="font-bold text-sm text-slate-900 dark:text-white" @click="emit('view-post', p); emit('close');">
                {{ p.title }}
              </h4>
              <div class="flex items-center justify-between text-[11px] text-slate-400 font-mono">
                <span>👁️ {{ p.views || 0 }} · ❤️ {{ p.likes || 0 }}</span>
                <span>{{ formatDate(p.createdAt) }}</span>
              </div>
              <div class="pt-2 border-t border-slate-100 dark:border-slate-800 flex items-center justify-end gap-2">
                <button @click="switchTabToComments(p.id)" class="px-2.5 py-1 rounded-xl text-xs font-semibold bg-sky-50 text-sky-600 dark:bg-sky-950 dark:text-sky-300 flex items-center gap-1">
                  <span>💬 评论 ({{ getCommentsCountByPost(p.id) }})</span>
                </button>
                <button @click="emit('edit-post', p)" class="px-2.5 py-1 rounded-xl text-xs font-semibold text-slate-700 dark:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800">
                  ✏️ 编辑
                </button>
                <button @click="emit('toggle-post-status', p)" class="px-2.5 py-1 rounded-xl text-xs text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800">
                  {{ p.status === 'PUBLISHED' ? '转草稿' : '发布' }}
                </button>
                <button @click="emit('delete-post', p)" class="px-2.5 py-1 rounded-xl text-xs text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-950/40">
                  删除
                </button>
              </div>
            </div>
          </div>

          <!-- 文章极客分页工具栏 -->
          <div v-if="filteredPosts.length > 0" class="flex flex-col sm:flex-row items-center justify-between gap-3 pt-3 border-t border-slate-200 dark:border-slate-800 text-xs">
            <div class="text-slate-400 font-mono text-[11px]">
              第 <span class="font-bold text-slate-700 dark:text-slate-200">{{ postPage }}</span> / {{ totalPostPages }} 页 · 共 <span class="font-bold text-slate-700 dark:text-slate-200">{{ filteredPosts.length }}</span> 篇文章
            </div>
            <div class="flex items-center gap-1.5" v-if="totalPostPages > 1">
              <button type="button"
                      @click="postPage = Math.max(1, postPage - 1)"
                      :disabled="postPage <= 1"
                      class="px-2.5 py-1.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-300 hover:border-sky-500 hover:text-sky-600 disabled:opacity-40 disabled:pointer-events-none transition-colors">
                ‹ 上一页
              </button>
              <div class="flex items-center gap-1">
                <button v-for="p in getPageNumbers(postPage, totalPostPages)" :key="p"
                        type="button"
                        @click="typeof p === 'number' && (postPage = p)"
                        :disabled="typeof p !== 'number'"
                        :class="p === postPage ? 'bg-sky-600 text-white font-bold shadow-xs' : (typeof p === 'number' ? 'bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 text-slate-600 dark:text-slate-300 hover:border-sky-500' : 'text-slate-400 cursor-default')"
                        class="w-7 h-7 rounded-lg text-xs flex items-center justify-center transition-colors">
                  {{ p }}
                </button>
              </div>
              <button type="button"
                      @click="postPage = Math.min(totalPostPages, postPage + 1)"
                      :disabled="postPage >= totalPostPages"
                      class="px-2.5 py-1.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-300 hover:border-sky-500 hover:text-sky-600 disabled:opacity-40 disabled:pointer-events-none transition-colors">
                下一页 ›
              </button>
            </div>
          </div>
        </div>

        <!-- ============ 选项卡 2：专栏分类管理 ============ -->
        <div v-if="activeTab === 'categories'" class="space-y-4">
          <div class="flex items-center justify-between">
            <div class="text-xs text-slate-500 font-mono">共 {{ categoryList.length }} 个技术专栏</div>
            <button @click="emit('open-category-modal')"
                    class="px-4 py-2 rounded-xl text-xs font-bold bg-sky-600 hover:bg-sky-700 text-white flex items-center gap-1.5 shadow-md shadow-sky-500/20">
              <span>➕</span> <span>新增专栏</span>
            </button>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            <div v-for="cat in categoryList" :key="cat.id"
                 class="p-4 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm flex flex-col justify-between space-y-3">
              <div>
                <div class="flex items-center justify-between mb-2">
                  <span class="text-2xl">{{ cat.icon || getCategoryIcon(cat.name) }}</span>
                  <span class="px-2 py-0.5 rounded-full text-[10px] font-mono font-bold bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300">
                    {{ getPostsCountByCategory(cat.name) }} 篇博客
                  </span>
                </div>
                <h4 class="font-bold text-sm text-slate-900 dark:text-white">{{ cat.name }}</h4>
                <p class="text-xs text-slate-500 dark:text-slate-400 mt-1 line-clamp-2 leading-relaxed">
                  {{ cat.description || cat.desc || '暂无专栏详细介绍' }}
                </p>
              </div>
              <div class="pt-2 border-t border-slate-100 dark:border-slate-800 flex items-center justify-end gap-2">
                <button @click="emit('open-category-modal', cat)" class="px-2.5 py-1 rounded-lg text-xs font-semibold text-sky-600 hover:bg-sky-50 dark:hover:bg-sky-950">
                  ✏️ 编辑
                </button>
                <button @click="emit('delete-category', cat)" class="px-2.5 py-1 rounded-lg text-xs font-semibold text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-950/40">
                  🗑️ 删除
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- ============ 选项卡 3：评论审核与置顶 (支持按文章精准筛选与2级审核流) ============ -->
        <div v-if="activeTab === 'comments'" class="space-y-4">
          <!-- 智能筛选与控制工具栏 -->
          <div class="p-3.5 sm:p-4 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm space-y-3">
            
            <!-- 审核状态快捷筛选标签页 -->
            <div class="flex items-center gap-1.5 p-1 rounded-xl bg-slate-100 dark:bg-slate-800/80 text-xs font-bold overflow-x-auto no-scrollbar">
              <button @click="commentFilterStatus = 'ALL'"
                      :class="commentFilterStatus === 'ALL' ? 'bg-white dark:bg-slate-700 text-sky-600 dark:text-sky-400 shadow-xs' : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'"
                      class="px-3 py-1.5 rounded-lg transition-all flex items-center gap-1.5 whitespace-nowrap">
                <span>全部</span>
                <span class="px-1.5 py-0.2 rounded-full text-[10px] bg-slate-200 dark:bg-slate-600 text-slate-700 dark:text-slate-200 font-mono">{{ adminComments.length }}</span>
              </button>
              <button @click="commentFilterStatus = 'PENDING'"
                      :class="commentFilterStatus === 'PENDING' ? 'bg-amber-500 text-white shadow-xs' : 'text-amber-700 dark:text-amber-400 hover:bg-amber-50 dark:hover:bg-amber-950/40'"
                      class="px-3 py-1.5 rounded-lg transition-all flex items-center gap-1.5 whitespace-nowrap relative">
                <span>⏳ 待审核</span>
                <span :class="commentFilterStatus === 'PENDING' ? 'bg-white text-amber-700' : 'bg-amber-500 text-white'"
                      class="px-1.5 py-0.2 rounded-full text-[10px] font-mono font-bold">
                  {{ pendingCommentsCount }}
                </span>
                <span v-if="pendingCommentsCount > 0" class="w-2 h-2 rounded-full bg-rose-500 animate-ping absolute -top-0.5 -right-0.5"></span>
              </button>
              <button @click="commentFilterStatus = 'APPROVED'"
                      :class="commentFilterStatus === 'APPROVED' ? 'bg-white dark:bg-slate-700 text-emerald-600 dark:text-emerald-400 shadow-xs' : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'"
                      class="px-3 py-1.5 rounded-lg transition-all flex items-center gap-1.5 whitespace-nowrap">
                <span>✅ 已通过</span>
                <span class="px-1.5 py-0.2 rounded-full text-[10px] bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300 font-mono">{{ approvedCommentsCount }}</span>
              </button>
              <button @click="commentFilterStatus = 'REJECTED'"
                      :class="commentFilterStatus === 'REJECTED' ? 'bg-white dark:bg-slate-700 text-rose-600 dark:text-rose-400 shadow-xs' : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'"
                      class="px-3 py-1.5 rounded-lg transition-all flex items-center gap-1.5 whitespace-nowrap">
                <span>🚫 已驳回</span>
                <span class="px-1.5 py-0.2 rounded-full text-[10px] bg-rose-100 text-rose-800 dark:bg-rose-950 dark:text-rose-300 font-mono">{{ rejectedCommentsCount }}</span>
              </button>
            </div>

            <div class="flex flex-col md:flex-row items-stretch md:items-center justify-between gap-3">
              <!-- 按文章筛选下拉菜单 -->
              <div class="flex-1 flex flex-col sm:flex-row items-stretch sm:items-center gap-2">
                <div class="flex items-center gap-1 text-xs font-bold text-slate-700 dark:text-slate-300 shrink-0">
                  <span>📑 按文章筛选：</span>
                </div>
                <div class="relative flex-1">
                  <select v-model="commentFilterPostId"
                          class="w-full pl-3 pr-8 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 text-xs font-semibold text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-2 focus:ring-sky-500 cursor-pointer">
                    <option value="all">🌟 全部文章评论 (共 {{ adminComments.length }} 条)</option>
                    <option v-for="p in allPosts" :key="p.id" :value="String(p.id)">
                      《{{ p.title }}》 · ({{ getCommentsCountByPost(p.id) }} 条评论)
                    </option>
                  </select>
                </div>
              </div>

              <!-- 关键词检索框（按评论内容/读者/文章标题/敏感词） -->
              <div class="relative sm:w-64 flex items-center">
                <span class="absolute left-2.5 top-1/2 -translate-y-1/2 text-slate-400 text-xs pointer-events-none">🔍</span>
                <input v-model="commentSearchKeyword"
                       type="text"
                       placeholder="搜索评论、读者或敏感词..."
                       class="w-full pl-8 pr-7 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 text-xs text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-2 focus:ring-sky-500">
                <button v-if="commentSearchKeyword" @click="commentSearchKeyword = ''"
                        class="absolute right-2.5 top-1/2 -translate-y-1/2 w-4 h-4 rounded-full flex items-center justify-center text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 hover:bg-slate-200 dark:hover:bg-slate-700 text-xs transition-colors">✕</button>
              </div>

              <!-- 刷新按钮 -->
              <button @click="handleRefreshComments"
                      class="px-3.5 py-2 rounded-xl bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-xs font-semibold text-slate-700 dark:text-slate-300 flex items-center justify-center gap-1.5 transition-colors shrink-0">
                <span>🔄</span> <span>刷新</span>
              </button>
            </div>

            <!-- 激活文章筛选时的专属提示横幅 -->
            <div v-if="selectedPostInfo"
                 class="flex flex-wrap items-center justify-between gap-2 pt-2.5 border-t border-slate-100 dark:border-slate-800 text-xs">
              <div class="flex items-center gap-2 truncate">
                <span class="px-2 py-0.5 rounded-lg text-[10px] font-bold bg-sky-100 text-sky-800 dark:bg-sky-950 dark:text-sky-300 border border-sky-200 dark:border-sky-800 shrink-0">
                  当前专区
                </span>
                <span class="font-bold text-slate-900 dark:text-white truncate">
                  《{{ selectedPostInfo.title }}》
                </span>
                <span class="text-slate-400 font-mono text-[11px] shrink-0">
                  (当前展示 {{ filteredAdminComments.length }} 条)
                </span>
              </div>
              <div class="flex items-center gap-3 shrink-0">
                <button @click="emit('view-post', selectedPostInfo); emit('close');"
                        class="text-sky-600 dark:text-sky-400 hover:underline font-semibold flex items-center gap-1">
                  <span>📖 前往阅读原文</span>
                </button>
                <span class="text-slate-300 dark:text-slate-700">|</span>
                <button @click="commentFilterPostId = 'all'"
                        class="text-rose-500 hover:text-rose-600 font-semibold flex items-center gap-0.5">
                  <span>✕ 查看全部文章评论</span>
                </button>
              </div>
            </div>
          </div>

          <!-- 评论卡片列表 (集成2级人工审核操作) -->
          <div v-if="filteredAdminComments.length > 0" class="space-y-3">
            <div v-for="c in paginatedComments" :key="c.id"
                 :class="[
                   c.status === 'PENDING' ? 'border-amber-400 bg-amber-50/40 dark:bg-amber-950/20 ring-1 ring-amber-400/40' :
                   c.status === 'REJECTED' ? 'border-rose-300 bg-rose-50/30 dark:bg-rose-950/20' :
                   c.isTop ? 'border-amber-400/60 bg-amber-50/20 dark:bg-amber-950/20 ring-1 ring-amber-400/20' : 
                   'bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-800'
                 ]"
                 class="p-4 rounded-2xl border shadow-sm transition-all space-y-2.5">
              <div class="flex flex-wrap items-center justify-between gap-2 text-xs">
                <div class="flex items-center gap-2 flex-wrap">
                  <div class="w-7 h-7 rounded-xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center text-sm shadow-xs shrink-0">
                    {{ c.avatar || (c.role === 'ADMIN' ? '👑' : '👨‍💻') }}
                  </div>
                  <span class="font-bold text-slate-900 dark:text-slate-100">
                    {{ (c.author && c.author !== 'null') ? c.author : '匿名读者' }}
                  </span>

                  <!-- 楼中楼回复指向标记 -->
                  <span v-if="c.replyTo" class="px-1.5 py-0.5 rounded text-[10px] font-mono bg-sky-50 dark:bg-sky-950 text-sky-600 dark:text-sky-300 border border-sky-200 dark:border-sky-800">
                    ↳ 回复 @{{ c.replyTo }}
                  </span>

                  <!-- 审核状态标识 -->
                  <span v-if="c.status === 'PENDING'"
                        class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-500 text-white flex items-center gap-1 shadow-xs">
                    <span>⏳</span> <span>待2级审核</span>
                  </span>
                  <span v-else-if="c.status === 'APPROVED'"
                        class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300 border border-emerald-200 dark:border-emerald-800 flex items-center gap-0.5">
                    <span>✓</span> <span>已通过上线</span>
                  </span>
                  <span v-else-if="c.status === 'REJECTED'"
                        class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-rose-100 text-rose-800 dark:bg-rose-950 dark:text-rose-300 border border-rose-200 dark:border-rose-800 flex items-center gap-0.5">
                    <span>✕</span> <span>已驳回</span>
                  </span>

                  <span v-if="c.isTop && c.status === 'APPROVED'" class="px-1.5 py-0.5 rounded text-[10px] font-bold bg-amber-500 text-white flex items-center gap-0.5">
                    <span>📌</span> <span>置顶</span>
                  </span>
                  <span v-if="c.role === 'ADMIN'" class="px-1.5 py-0.5 rounded text-[10px] font-bold bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300 border border-amber-200 dark:border-amber-800">
                    👑 博主/管理员
                  </span>
                  <span class="text-slate-400">· 评论于</span>
                  <button @click="commentFilterPostId = String(c.postId)"
                          class="font-mono text-sky-600 dark:text-sky-400 hover:underline font-semibold truncate max-w-[240px] text-left"
                          title="点击只看该文章评论">
                    《{{ getPostTitle(c.postId) }}》
                  </button>
                </div>
                <span class="text-[10px] text-slate-400 font-mono">{{ formatDate(c.createdAt) }}</span>
              </div>

              <!-- 敏感词高亮预警提示卡 -->
              <div v-if="c.sensitiveWords"
                   class="p-2 sm:p-2.5 rounded-xl bg-amber-100/70 dark:bg-amber-950/60 border border-amber-300/80 dark:border-amber-800/80 text-xs text-amber-900 dark:text-amber-200 flex items-center gap-2">
                <span class="text-sm shrink-0">⚠️</span>
                <span class="leading-normal">
                  <strong>1级机审命中敏感词：</strong>
                  <span v-for="w in c.sensitiveWords.split(',')" :key="w"
                        class="inline-block mx-0.5 px-1.5 py-0.2 rounded bg-rose-500 text-white font-mono font-bold text-[10px]">
                    {{ w.trim() }}
                  </span>
                </span>
              </div>

              <p class="text-xs sm:text-sm text-slate-700 dark:text-slate-300 leading-relaxed pl-9">
                {{ c.content }}
              </p>

              <div class="pt-2 border-t border-slate-100 dark:border-slate-800/80 flex flex-wrap items-center justify-between gap-2 text-xs">
                <span class="text-[11px] text-slate-400 pl-9 font-mono">
                  文章 ID: #{{ c.postId }}
                </span>
                <div class="flex items-center gap-2">
                  <!-- 2级人工审核通过操作（仅待审核状态展示，已驳回或已通过均不展示） -->
                  <button v-if="c.status === 'PENDING'"
                          @click="emit('audit-comment', c, 'APPROVED')"
                          class="px-2.5 py-1 rounded-xl text-xs font-semibold bg-emerald-600 hover:bg-emerald-700 text-white shadow-xs transition-colors flex items-center gap-1">
                    <span>✓</span> <span>审核通过</span>
                  </button>

                  <!-- 2级人工审核驳回操作 -->
                  <button v-if="c.status !== 'REJECTED'"
                          @click="emit('audit-comment', c, 'REJECTED')"
                          class="px-2.5 py-1 rounded-xl text-xs font-semibold bg-amber-100 hover:bg-amber-200 dark:bg-amber-950 dark:hover:bg-amber-900 text-amber-800 dark:text-amber-300 transition-colors flex items-center gap-1">
                    <span>🚫</span> <span>驳回</span>
                  </button>

                  <!-- 置顶操作（仅针对已发布评论） -->
                  <button v-if="c.status === 'APPROVED'"
                          @click="emit('toggle-comment-top', c)"
                          :class="c.isTop ? 'bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300 border-amber-200 dark:border-amber-800' : 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300 border-slate-200 dark:border-slate-700'"
                          class="px-2.5 py-1 rounded-xl text-xs font-semibold flex items-center gap-1 transition-colors border">
                    <span>📌</span> <span>{{ c.isTop ? '取消置顶' : '设为置顶' }}</span>
                  </button>

                  <!-- 彻底删除 -->
                  <button @click="emit('delete-comment', c)"
                          class="px-2.5 py-1 rounded-xl text-xs font-semibold text-rose-600 bg-rose-50 hover:bg-rose-100 dark:bg-rose-950/40 dark:hover:bg-rose-950/70 border border-rose-200/50 dark:border-rose-900/50 transition-colors flex items-center gap-1">
                    <span>🗑️</span> <span>删除</span>
                  </button>
                </div>
              </div>
            </div>

            <!-- 评论极客分页工具栏 -->
            <div class="flex flex-col sm:flex-row items-center justify-between gap-3 pt-3 border-t border-slate-200 dark:border-slate-800 text-xs">
              <div class="text-slate-400 font-mono text-[11px]">
                第 <span class="font-bold text-slate-700 dark:text-slate-200">{{ commentPage }}</span> / {{ totalCommentPages }} 页 · 共 <span class="font-bold text-slate-700 dark:text-slate-200">{{ filteredAdminComments.length }}</span> 条评论
              </div>
              <div class="flex items-center gap-1.5" v-if="totalCommentPages > 1">
                <button type="button"
                        @click="commentPage = Math.max(1, commentPage - 1)"
                        :disabled="commentPage <= 1"
                        class="px-2.5 py-1.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-300 hover:border-sky-500 hover:text-sky-600 disabled:opacity-40 disabled:pointer-events-none transition-colors">
                  ‹ 上一页
                </button>
                <div class="flex items-center gap-1">
                  <button v-for="p in getPageNumbers(commentPage, totalCommentPages)" :key="p"
                          type="button"
                          @click="typeof p === 'number' && (commentPage = p)"
                          :disabled="typeof p !== 'number'"
                          :class="p === commentPage ? 'bg-sky-600 text-white font-bold shadow-xs' : (typeof p === 'number' ? 'bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 text-slate-600 dark:text-slate-300 hover:border-sky-500' : 'text-slate-400 cursor-default')"
                          class="w-7 h-7 rounded-lg text-xs flex items-center justify-center transition-colors">
                    {{ p }}
                  </button>
                </div>
                <button type="button"
                        @click="commentPage = Math.min(totalCommentPages, commentPage + 1)"
                        :disabled="commentPage >= totalCommentPages"
                        class="px-2.5 py-1.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-300 hover:border-sky-500 hover:text-sky-600 disabled:opacity-40 disabled:pointer-events-none transition-colors">
                  下一页 ›
                </button>
              </div>
            </div>
          </div>
          <div v-else class="text-center py-12 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-xs text-slate-400 space-y-2">
            <div class="text-2xl">📭</div>
            <div>{{ commentFilterPostId !== 'all' ? '该文章下暂无匹配评论' : (commentFilterStatus !== 'ALL' ? `暂无「${commentFilterStatus}」状态的评论` : '未检索到符合条件的读者评论') }}</div>
            <button v-if="commentFilterPostId !== 'all' || commentFilterStatus !== 'ALL' || commentSearchKeyword"
                    @click="commentFilterPostId = 'all'; commentFilterStatus = 'ALL'; commentSearchKeyword = '';"
                    class="text-sky-600 dark:text-sky-400 font-bold hover:underline">
              重置全部筛选条件
            </button>
          </div>
        </div>

        <!-- ============ 选项卡 4：站内消息通知发布 & 历史管理 ============ -->
        <div v-if="activeTab === 'notifications'" class="space-y-4">
          <!-- 顶部子模式分段切换栏 (发布新通知 VS 已发通知历史) -->
          <div class="flex flex-wrap items-center justify-between gap-3 p-2.5 rounded-2xl bg-slate-100/80 dark:bg-slate-800/60 border border-slate-200/80 dark:border-slate-700/80">
            <div class="flex items-center gap-1.5 p-1 rounded-xl bg-white dark:bg-slate-900 border border-slate-200/60 dark:border-slate-800 shadow-xs">
              <button type="button"
                      @click="noticeSubView = 'compose'"
                      :class="noticeSubView === 'compose' ? 'bg-sky-500 text-white font-bold shadow-xs' : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'"
                      class="px-4 py-1.5 rounded-lg text-xs transition-all flex items-center gap-1.5">
                <span>➕</span>
                <span>发布新通知</span>
              </button>
              <button type="button"
                      @click="noticeSubView = 'history'"
                      :class="noticeSubView === 'history' ? 'bg-sky-500 text-white font-bold shadow-xs' : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'"
                      class="px-4 py-1.5 rounded-lg text-xs transition-all flex items-center gap-1.5">
                <span>📜</span>
                <span>已发历史通知</span>
                <span :class="noticeSubView === 'history' ? 'bg-white/25 text-white' : 'bg-slate-100 dark:bg-slate-800 text-slate-500 dark:text-slate-400'"
                      class="px-1.5 py-0.2 rounded-full text-[10px] font-mono">
                  {{ allNotifications.length }}
                </span>
              </button>
            </div>

            <div class="flex items-center gap-2 pr-1">
              <button v-if="noticeSubView === 'compose'"
                      @click="noticeSubView = 'history'"
                      class="text-xs text-sky-600 dark:text-sky-400 hover:underline flex items-center gap-1 font-medium">
                <span>查看所有已发记录 ({{ allNotifications.length }}) &rarr;</span>
              </button>
              <button v-else
                      @click="noticeSubView = 'compose'"
                      class="text-xs text-sky-600 dark:text-sky-400 hover:underline flex items-center gap-1 font-medium">
                <span>&larr; 返回发布模式</span>
              </button>
            </div>
          </div>

          <!-- 子视图 A：发布新通知表单 -->
          <div v-if="noticeSubView === 'compose'"
               class="p-5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm space-y-4">
            <div class="flex items-center justify-between pb-2 border-b border-slate-100 dark:border-slate-800">
              <div class="flex items-center gap-2">
                <span class="text-base">📢</span>
                <h4 class="font-bold text-sm text-slate-900 dark:text-white">广播全站公告 / 读者定向私信</h4>
              </div>
              <span class="text-[11px] text-slate-400 font-mono hidden sm:inline">支持 Markdown 语法排版</span>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-12 gap-3.5 text-xs">
              <div class="sm:col-span-5">
                <label class="block font-semibold mb-1 text-slate-700 dark:text-slate-300">通知标题 *</label>
                <input v-model="noticeForm.title" type="text" placeholder="例如: 🎉 2026 架构实录上线通知"
                       class="w-full px-3.5 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:outline-none focus:ring-2 focus:ring-sky-500 text-xs">
              </div>

              <div class="sm:col-span-3">
                <label class="block font-semibold mb-1 text-slate-700 dark:text-slate-300">通知类型</label>
                <div class="grid grid-cols-2 gap-1 p-0.5 bg-slate-100 dark:bg-slate-800 rounded-xl border border-slate-300 dark:border-slate-700">
                  <button type="button"
                          @click="emit('set-notice-type', 'ANNOUNCEMENT')"
                          :class="noticeForm.type === 'ANNOUNCEMENT' ? 'bg-white dark:bg-slate-700 text-sky-600 dark:text-sky-400 font-bold shadow-xs' : 'text-slate-500 hover:text-slate-700 dark:hover:text-slate-300'"
                          class="py-1.5 rounded-lg text-xs transition-all flex items-center justify-center gap-1">
                    <span>📢</span> <span>公告</span>
                  </button>
                  <button type="button"
                          @click="emit('set-notice-type', 'NOTICE')"
                          :class="(noticeForm.type === 'NOTICE' || noticeForm.type === 'SYSTEM') ? 'bg-white dark:bg-slate-700 text-sky-600 dark:text-sky-400 font-bold shadow-xs' : 'text-slate-500 hover:text-slate-700 dark:hover:text-slate-300'"
                          class="py-1.5 rounded-lg text-xs transition-all flex items-center justify-center gap-1">
                    <span>💬</span> <span>通知</span>
                  </button>
                </div>
              </div>

              <!-- 接收用户 -->
              <div class="sm:col-span-4 relative">
                <div class="flex items-center justify-between mb-1">
                  <label class="font-semibold text-slate-700 dark:text-slate-300">接收用户</label>
                  <span v-if="noticeForm.type === 'ANNOUNCEMENT'" class="text-[10px] text-sky-600 dark:text-sky-400 font-mono">全站广播</span>
                  <span v-else class="text-[10px] text-slate-400 font-mono">
                    {{ isAllSelected ? '已全选' : (selectedUsers.length ? `已选 ${selectedUsers.length} 人` : '未选') }}
                  </span>
                </div>

                <div v-if="noticeForm.type === 'ANNOUNCEMENT'"
                     class="w-full min-h-[38px] px-3 py-2 rounded-xl bg-slate-100/80 dark:bg-slate-800/60 border border-dashed border-slate-300 dark:border-slate-700 text-slate-500 dark:text-slate-400 flex items-center justify-between cursor-not-allowed select-none"
                     title="公告类型全员可见，不可限制接收用户">
                  <div class="flex items-center gap-1.5 truncate">
                    <span class="text-sky-500">🌐</span>
                    <span class="font-medium text-slate-600 dark:text-slate-300 text-xs truncate">全站公开 · 所有人可见</span>
                  </div>
                  <span class="px-1.5 py-0.5 rounded text-[10px] font-mono font-bold bg-sky-100 dark:bg-sky-950 text-sky-600 dark:text-sky-400 shrink-0">ALL</span>
                </div>

                <div v-else class="relative">
                  <button type="button"
                          @click="emit('toggle-user-dropdown')"
                          class="w-full min-h-[38px] px-3 py-1.5 rounded-xl bg-slate-50 dark:bg-slate-800 border transition-all text-left flex items-center justify-between gap-1.5 focus:outline-none"
                          :class="showUserDropdown ? 'border-sky-500 ring-2 ring-sky-500/20' : 'border-slate-300 dark:border-slate-700 hover:border-slate-400 dark:hover:border-slate-600'">
                    <div v-if="selectedUsers.length === 0" class="text-slate-400 text-xs flex items-center gap-1 truncate">
                      <span>👥</span>
                      <span class="truncate">选择接收用户 (支持多选/全选)...</span>
                    </div>
                    <div v-else-if="isAllSelected" class="flex items-center gap-1.5 truncate">
                      <span class="px-1.5 py-0.5 rounded text-[10px] font-bold bg-sky-100 dark:bg-sky-950 text-sky-600 dark:text-sky-300 shrink-0 flex items-center gap-1">
                        <span>⚡</span> <span>全选用户</span>
                      </span>
                      <span class="text-[11px] text-slate-500 dark:text-slate-400 font-mono truncate">({{ userList.length }}人全覆盖)</span>
                    </div>
                    <div v-else class="flex flex-wrap items-center gap-1 max-h-12 overflow-hidden truncate">
                      <span v-for="username in selectedUsers.slice(0, 2)" :key="username"
                            class="inline-flex items-center gap-1 px-1.5 py-0.5 rounded text-[10px] font-mono bg-sky-50 dark:bg-sky-950/80 text-sky-600 dark:text-sky-300 border border-sky-200 dark:border-sky-800 shrink-0">
                        <span>@{{ username }}</span>
                        <span @click.stop="emit('remove-user', username)" class="hover:text-rose-500 font-bold leading-none cursor-pointer">×</span>
                      </span>
                      <span v-if="selectedUsers.length > 2" class="px-1.5 py-0.5 rounded text-[9px] font-bold bg-slate-200 dark:bg-slate-700 text-slate-600 dark:text-slate-300 shrink-0">
                        +{{ selectedUsers.length - 2 }}
                      </span>
                    </div>

                    <div class="flex items-center gap-1 shrink-0 text-slate-400">
                      <span v-if="selectedUsers.length > 0" class="text-[10px] font-mono font-bold px-1.5 py-0.5 bg-sky-100 dark:bg-sky-950 text-sky-600 dark:text-sky-400 rounded-md">
                        {{ selectedUsers.length }}
                      </span>
                      <svg class="w-3.5 h-3.5 transition-transform duration-200" :class="showUserDropdown ? 'rotate-180 text-sky-500' : ''" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                      </svg>
                    </div>
                  </button>

                  <!-- PC 下拉菜单 -->
                  <div v-if="showUserDropdown"
                       class="hidden sm:flex flex-col absolute right-0 top-full mt-1.5 z-50 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-2xl p-2.5 w-72 max-w-[90vw] animate-in fade-in zoom-in-95 duration-150">
                    <div class="space-y-2 pb-2 border-b border-slate-100 dark:border-slate-800">
                      <div class="relative">
                        <input :value="userSearch"
                               @input="emit('update:userSearch', $event.target.value)"
                               type="text" placeholder="快速检索用户或昵称..."
                               class="w-full pl-7 pr-7 py-1.5 text-xs rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 focus:outline-none focus:ring-1 focus:ring-sky-500">
                        <span class="absolute left-2.5 top-1.5 text-slate-400 text-xs">🔍</span>
                        <button v-if="userSearch" @click="emit('update:userSearch', '')" class="absolute right-2.5 top-1.5 text-slate-400 hover:text-slate-600 text-xs">✕</button>
                      </div>
                      <div class="flex items-center justify-between text-[11px] px-1">
                        <button type="button" @click="emit('toggle-select-all-users')"
                                class="text-sky-600 dark:text-sky-400 hover:underline font-bold flex items-center gap-1">
                          <span>{{ isAllSelected ? '✕ 清空选择' : '✓ 一键全选' }}</span>
                          <span class="text-slate-400 font-normal">({{ userList.length }}人)</span>
                        </button>
                        <span class="text-slate-400 text-[10px]">已勾选 <strong class="text-sky-600 dark:text-sky-400 font-mono">{{ selectedUsers.length }}</strong> 人</span>
                      </div>
                    </div>

                    <div class="overflow-y-auto max-h-48 py-1 space-y-0.5 divide-y divide-slate-100 dark:divide-slate-800/40">
                      <div v-for="u in filteredUsers" :key="u.username"
                           @click="emit('toggle-user', u.username)"
                           class="px-2 py-1.5 rounded-xl hover:bg-slate-100 dark:hover:bg-slate-800 flex items-center justify-between gap-2 cursor-pointer transition-colors"
                           :class="selectedUsers.includes(u.username) ? 'bg-sky-50/70 dark:bg-sky-950/40' : ''">
                        <div class="flex items-center gap-2 truncate">
                          <div class="w-4 h-4 rounded border flex items-center justify-center shrink-0 transition-colors"
                               :class="selectedUsers.includes(u.username) ? 'bg-sky-500 border-sky-500 text-white' : 'border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-800'">
                            <svg v-if="selectedUsers.includes(u.username)" class="w-3 h-3" viewBox="0 0 20 20" fill="currentColor">
                              <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
                            </svg>
                          </div>
                          <span class="text-xs shrink-0">{{ u.avatar || '👤' }}</span>
                          <div class="truncate text-xs">
                            <span class="font-semibold text-slate-800 dark:text-slate-200">{{ u.nickname || u.username }}</span>
                            <span class="text-[10px] text-slate-400 font-mono ml-1">@{{ u.username }}</span>
                          </div>
                        </div>
                        <span class="text-[9px] px-1.5 py-0.5 rounded font-mono shrink-0"
                              :class="String(u.role).toLowerCase() === 'admin' ? 'bg-amber-100 text-amber-700 dark:bg-amber-950 dark:text-amber-300 font-bold' : 'bg-slate-100 text-slate-500 dark:bg-slate-800 dark:text-slate-400'">
                          {{ String(u.role).toLowerCase() === 'admin' ? '管理员' : '读者' }}
                        </span>
                      </div>
                      <div v-if="filteredUsers.length === 0" class="py-4 text-center text-xs text-slate-400">
                        未检索到匹配用户
                      </div>
                    </div>

                    <div class="pt-2 border-t border-slate-100 dark:border-slate-800 flex justify-end">
                      <button type="button" @click="emit('toggle-user-dropdown')"
                              class="px-3 py-1 rounded-lg text-xs font-bold bg-sky-600 hover:bg-sky-700 text-white">
                        完成 ({{ selectedUsers.length }})
                      </button>
                    </div>
                  </div>

                  <!-- 移动端抽屉 -->
                  <teleport to="body">
                    <div v-if="showUserDropdown" class="sm:hidden fixed inset-0 z-[70]">
                      <div class="fixed inset-0 bg-slate-900/70 backdrop-blur-xs transition-opacity"
                           @click="emit('toggle-user-dropdown')"></div>

                      <div class="fixed inset-x-0 bottom-0 z-10 bg-white dark:bg-slate-900 rounded-t-3xl border-t border-slate-200 dark:border-slate-800 shadow-2xl safe-bottom p-4 flex flex-col max-h-[82vh] space-y-3 animate-in slide-in-from-bottom duration-250">
                        <div class="w-10 h-1.5 bg-slate-200 dark:bg-slate-700 rounded-full mx-auto"></div>

                        <div class="flex items-center justify-between pb-2 border-b border-slate-100 dark:border-slate-800">
                          <div class="flex items-center gap-2">
                            <span class="text-base">👥</span>
                            <h4 class="font-bold text-sm text-slate-900 dark:text-white">选择通知接收用户</h4>
                            <span class="text-xs text-slate-400 font-mono">(共 {{ userList.length }} 人)</span>
                          </div>
                          <button @click="emit('toggle-user-dropdown')" class="text-slate-400 hover:text-slate-600 p-1 text-base">✕</button>
                        </div>

                        <div class="relative">
                          <input :value="userSearch"
                                 @input="emit('update:userSearch', $event.target.value)"
                                 type="text" placeholder="输入昵称或用户名搜索..."
                                 class="w-full pl-8 pr-7 py-2 text-xs rounded-xl bg-slate-100 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 focus:outline-none focus:ring-2 focus:ring-sky-500">
                          <span class="absolute left-2.5 top-2 text-slate-400 text-xs">🔍</span>
                          <button v-if="userSearch" @click="emit('update:userSearch', '')" class="absolute right-2.5 top-2 text-slate-400 text-xs">✕</button>
                        </div>

                        <div class="flex items-center justify-between text-xs px-1">
                          <button type="button" @click="emit('toggle-select-all-users')"
                                  class="px-3 py-1 rounded-lg bg-sky-50 dark:bg-sky-950/60 text-sky-600 dark:text-sky-300 font-bold flex items-center gap-1.5">
                            <span>{{ isAllSelected ? '✕ 取消全选' : '✓ 一键全选' }}</span>
                          </button>
                          <div class="text-xs text-slate-500 dark:text-slate-400">
                            已勾选 <span class="font-bold font-mono text-sky-600 dark:text-sky-400">{{ selectedUsers.length }}</span> 位用户
                          </div>
                        </div>

                        <div class="overflow-y-auto flex-1 min-h-[160px] max-h-[45vh] space-y-1.5 divide-y divide-slate-100 dark:divide-slate-800/60 pr-0.5">
                          <div v-for="u in filteredUsers" :key="u.username"
                               @click="emit('toggle-user', u.username)"
                               class="p-2.5 rounded-xl active:bg-slate-100 dark:active:bg-slate-800 flex items-center justify-between gap-3 transition-colors cursor-pointer"
                               :class="selectedUsers.includes(u.username) ? 'bg-sky-50/80 dark:bg-sky-950/40 border border-sky-200 dark:border-sky-800' : 'border border-transparent'">
                            <div class="flex items-center gap-2.5 truncate">
                              <div class="w-5 h-5 rounded-md border flex items-center justify-center shrink-0 transition-colors"
                                   :class="selectedUsers.includes(u.username) ? 'bg-sky-500 border-sky-500 text-white' : 'border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-800'">
                                <svg v-if="selectedUsers.includes(u.username)" class="w-3.5 h-3.5" viewBox="0 0 20 20" fill="currentColor">
                                  <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
                                </svg>
                              </div>
                              <span class="text-base shrink-0">{{ u.avatar || '👤' }}</span>
                              <div class="truncate text-xs">
                                <div class="font-semibold text-slate-800 dark:text-slate-100">{{ u.nickname || u.username }}</div>
                                <div class="text-[10px] text-slate-400 font-mono">@{{ u.username }}</div>
                              </div>
                            </div>
                            <span class="text-[10px] px-2 py-0.5 rounded font-mono shrink-0"
                                  :class="String(u.role).toLowerCase() === 'admin' ? 'bg-amber-100 text-amber-700 dark:bg-amber-950 dark:text-amber-300 font-bold' : 'bg-slate-100 text-slate-500 dark:bg-slate-800 dark:text-slate-400'">
                              {{ String(u.role).toLowerCase() === 'admin' ? '管理员' : '读者' }}
                            </span>
                          </div>
                          <div v-if="filteredUsers.length === 0" class="py-8 text-center text-xs text-slate-400">
                            未检索到匹配用户
                          </div>
                        </div>

                        <div class="pt-2 border-t border-slate-100 dark:border-slate-800">
                          <button type="button"
                                  @click="emit('toggle-user-dropdown')"
                                  class="w-full py-2.5 rounded-xl bg-sky-600 hover:bg-sky-700 text-white font-bold text-xs shadow-lg shadow-sky-500/20 flex items-center justify-center gap-1.5 active:scale-[0.99] transition-all">
                            <span>✓ 确定选择 (已选 {{ selectedUsers.length }} 人)</span>
                          </button>
                        </div>
                      </div>
                    </div>
                  </teleport>
                </div>
              </div>
            </div>

            <div>
              <label class="block font-semibold mb-1 text-slate-700 dark:text-slate-300 text-xs">正文内容 (支持 Markdown) *</label>
              <textarea v-model="noticeForm.content" rows="6" placeholder="输入公告详情或通知正文，支持 Markdown 语法..."
                        class="w-full min-h-[160px] px-3.5 py-2.5 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:outline-none focus:ring-2 focus:ring-sky-500 text-xs font-mono leading-relaxed"></textarea>
            </div>

            <div class="flex items-center justify-between pt-2">
              <button type="button"
                      @click="noticeSubView = 'history'"
                      class="text-xs text-slate-500 hover:text-slate-800 dark:hover:text-slate-200 flex items-center gap-1 transition-colors">
                <span>📜 查看已发通知历史记录 ({{ allNotifications.length }})</span>
              </button>

              <button @click="emit('publish-notification', noticeForm)" :disabled="publishingNotice"
                      class="px-6 py-2.5 rounded-xl text-xs font-bold bg-sky-600 hover:bg-sky-700 text-white shadow-lg shadow-sky-500/20 flex items-center gap-1.5 disabled:opacity-50 transition-all active:scale-95">
                <span>🚀</span> <span>{{ publishingNotice ? '正在广播中...' : '立即发布通知' }}</span>
              </button>
            </div>
          </div>

          <!-- 子视图 B：已发通知历史管理与五态分类查看 -->
          <div v-else class="space-y-4">
            <!-- 历史分类与检索栏 -->
            <div class="p-3.5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm space-y-3">
              <div class="flex flex-wrap items-center justify-between gap-3">
                <!-- 五态分类标签页 -->
                <div class="flex items-center gap-1.5 overflow-x-auto scrollbar-none text-xs">
                  <button v-for="tab in noticeHistoryTabs" :key="tab.key"
                          type="button"
                          @click="noticeHistoryCategory = tab.key"
                          :class="noticeHistoryCategory === tab.key
                            ? 'bg-sky-500 text-white font-bold shadow-xs'
                            : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'"
                          class="px-3 py-1.5 rounded-xl flex items-center gap-1.5 transition-all whitespace-nowrap">
                    <span>{{ tab.icon }}</span>
                    <span>{{ tab.label }}</span>
                    <span :class="noticeHistoryCategory === tab.key ? 'bg-white/25 text-white' : 'bg-white dark:bg-slate-700 text-slate-500 dark:text-slate-300 border border-slate-200/60 dark:border-slate-600'"
                          class="px-1.5 py-0.2 rounded-full text-[10px] font-mono">
                      {{ tab.count }}
                    </span>
                  </button>
                </div>

                <!-- 关键词搜索框 -->
                <div class="relative w-full sm:w-64 flex items-center">
                  <span class="absolute left-2.5 top-1/2 -translate-y-1/2 text-slate-400 text-xs pointer-events-none">🔍</span>
                  <input v-model="noticeHistorySearch"
                         type="text"
                         placeholder="搜索标题、内容或用户..."
                         class="w-full pl-8 pr-7 py-2 text-xs rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 focus:outline-none focus:ring-2 focus:ring-sky-500">
                  <button v-if="noticeHistorySearch" @click="noticeHistorySearch = ''"
                          class="absolute right-2.5 top-1/2 -translate-y-1/2 w-4 h-4 rounded-full flex items-center justify-center text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 hover:bg-slate-200 dark:hover:bg-slate-700 text-xs transition-colors">✕</button>
                </div>
              </div>

              <!-- 状态小结与发布快捷入口 -->
              <div class="flex items-center justify-between pt-2 border-t border-slate-100 dark:border-slate-800 text-[11px] text-slate-400 font-mono">
                <div>
                  展示 <span class="font-bold text-slate-700 dark:text-slate-200">{{ filteredHistoryNotifications.length }}</span> / {{ allNotifications.length }} 条记录
                </div>
                <button type="button"
                        @click="noticeSubView = 'compose'"
                        class="text-sky-600 dark:text-sky-400 hover:underline font-semibold flex items-center gap-1 font-sans">
                  <span>➕ 发布新通知</span>
                </button>
              </div>
            </div>

            <!-- 历史记录列表 -->
            <div v-if="filteredHistoryNotifications.length > 0" class="space-y-3">
              <div v-for="n in paginatedHistoryNotifications" :key="n.id"
                   class="p-4 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm transition-all hover:border-slate-300 dark:hover:border-slate-700 space-y-2.5">
                <div class="flex flex-wrap items-center justify-between gap-2">
                  <div class="flex items-center gap-2 flex-wrap">
                    <!-- 类型徽标 -->
                    <span :class="getNoticeTypeBadge(n.type).class"
                          class="px-2 py-0.5 rounded-md text-[10px] font-bold font-mono border flex items-center gap-1">
                      <span>{{ getNoticeTypeBadge(n.type).icon }}</span>
                      <span>{{ getNoticeTypeBadge(n.type).label }}</span>
                    </span>

                    <h5 class="font-bold text-xs sm:text-sm text-slate-900 dark:text-white line-clamp-1">
                      {{ n.title }}
                    </h5>

                    <!-- 接收人 -->
                    <span v-if="n.type === 'ANNOUNCEMENT' || n.targetUsername === 'ALL'"
                          class="px-1.5 py-0.5 rounded text-[10px] font-mono bg-sky-50 dark:bg-sky-950/60 text-sky-600 dark:text-sky-300 border border-sky-200 dark:border-sky-800">
                      🌐 全员广播
                    </span>
                    <span v-else-if="n.targetUsername"
                          class="px-1.5 py-0.5 rounded text-[10px] font-mono bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 border border-slate-200 dark:border-slate-700">
                      🎯 发送至: @{{ n.targetUsername }}
                    </span>
                  </div>

                  <span class="text-[11px] text-slate-400 font-mono">{{ formatDate(n.createdAt) }}</span>
                </div>

                <!-- 正文内容展示 -->
                <p class="text-xs text-slate-600 dark:text-slate-300 leading-relaxed whitespace-pre-line pl-1 bg-slate-50/50 dark:bg-slate-800/30 p-2.5 rounded-xl border border-slate-100 dark:border-slate-800/60">
                  {{ n.content }}
                </p>

                <!-- 底部元信息与操作 -->
                <div class="pt-2 border-t border-slate-100 dark:border-slate-800/80 flex items-center justify-between text-xs">
                  <div class="text-[11px] text-slate-400 font-mono flex items-center gap-2">
                    <span v-if="n.sender">发送者: {{ n.sender }}</span>
                    <span>ID: #{{ n.id }}</span>
                  </div>

                  <div class="flex items-center gap-2">
                    <!-- 快捷回复建议 (当属于 FEEDBACK 类型时) -->
                    <button v-if="n.type === 'FEEDBACK'"
                            type="button"
                            @click="handleReplyFeedbackFromHistory(n)"
                            class="px-2.5 py-1 text-xs rounded-xl bg-sky-50 hover:bg-sky-100 dark:bg-sky-950/60 dark:hover:bg-sky-900/60 text-sky-600 dark:text-sky-300 border border-sky-200 dark:border-sky-800 flex items-center gap-1 transition-colors font-medium">
                      <span>✉️</span> <span>回复此建议</span>
                    </button>

                    <!-- 撤回/删除 -->
                    <button @click="emit('delete-notification', n)"
                            class="px-2.5 py-1 text-xs font-medium text-rose-600 bg-rose-50 hover:bg-rose-100 dark:bg-rose-950/40 dark:hover:bg-rose-950/70 border border-rose-200/60 dark:border-rose-900/60 rounded-xl transition-colors flex items-center gap-1">
                      <span>🗑️</span> <span>撤回</span>
                    </button>
                  </div>
                </div>
              </div>

              <!-- 历史通知极客分页工具栏 -->
              <div class="flex flex-col sm:flex-row items-center justify-between gap-3 pt-3 border-t border-slate-200 dark:border-slate-800 text-xs">
                <div class="text-slate-400 font-mono text-[11px]">
                  第 <span class="font-bold text-slate-700 dark:text-slate-200">{{ noticePage }}</span> / {{ totalNoticePages }} 页 · 共 <span class="font-bold text-slate-700 dark:text-slate-200">{{ filteredHistoryNotifications.length }}</span> 条通知
                </div>
                <div class="flex items-center gap-1.5" v-if="totalNoticePages > 1">
                  <button type="button"
                          @click="noticePage = Math.max(1, noticePage - 1)"
                          :disabled="noticePage <= 1"
                          class="px-2.5 py-1.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-300 hover:border-sky-500 hover:text-sky-600 disabled:opacity-40 disabled:pointer-events-none transition-colors">
                    ‹ 上一页
                  </button>
                  <div class="flex items-center gap-1">
                    <button v-for="p in getPageNumbers(noticePage, totalNoticePages)" :key="p"
                            type="button"
                            @click="typeof p === 'number' && (noticePage = p)"
                            :disabled="typeof p !== 'number'"
                            :class="p === noticePage ? 'bg-sky-600 text-white font-bold shadow-xs' : (typeof p === 'number' ? 'bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 text-slate-600 dark:text-slate-300 hover:border-sky-500' : 'text-slate-400 cursor-default')"
                            class="w-7 h-7 rounded-lg text-xs flex items-center justify-center transition-colors">
                      {{ p }}
                    </button>
                  </div>
                  <button type="button"
                          @click="noticePage = Math.min(totalNoticePages, noticePage + 1)"
                          :disabled="noticePage >= totalNoticePages"
                          class="px-2.5 py-1.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-300 hover:border-sky-500 hover:text-sky-600 disabled:opacity-40 disabled:pointer-events-none transition-colors">
                    下一页 ›
                  </button>
                </div>
              </div>
            </div>

            <!-- 空状态提示 -->
            <div v-else class="text-center py-12 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-xs text-slate-400 space-y-2">
              <div class="text-2xl">📭</div>
              <div>{{ noticeHistorySearch ? '未搜索到匹配的通知记录' : (noticeHistoryCategory !== 'ALL' ? '该分类下暂无已发通知' : '暂无已发历史通知') }}</div>
              <div class="flex items-center justify-center gap-3 pt-1">
                <button v-if="noticeHistoryCategory !== 'ALL' || noticeHistorySearch"
                        @click="noticeHistoryCategory = 'ALL'; noticeHistorySearch = '';"
                        class="text-sky-600 dark:text-sky-400 font-bold hover:underline">
                  重置筛选条件
                </button>
                <button @click="noticeSubView = 'compose'"
                        class="px-3 py-1 rounded-xl bg-sky-600 text-white font-bold hover:bg-sky-700 shadow-xs">
                  去发布一条
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- ============ 选项卡 5：用户权限管理 ============ -->
        <div v-if="activeTab === 'users'" class="space-y-4">
          <div class="flex items-center justify-between">
            <div class="text-xs text-slate-500 font-mono">共 {{ userList.length }} 位注册用户</div>
            <button @click="emit('refresh-users')" class="px-3 py-1.5 rounded-xl bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 text-xs font-semibold flex items-center gap-1.5">
              <span>🔄</span> <span>刷新列表</span>
            </button>
          </div>

          <div class="overflow-x-auto bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800 shadow-sm">
            <table class="w-full text-left text-xs border-collapse min-w-[720px]">
              <thead>
                <tr class="border-b border-slate-200 dark:border-slate-800 text-slate-400 bg-slate-50/60 dark:bg-slate-800/40 whitespace-nowrap">
                  <th class="py-3 px-4 min-w-[180px]">用户</th>
                  <th class="py-3 px-3 whitespace-nowrap">角色权限</th>
                  <th class="py-3 px-3 whitespace-nowrap">邮箱</th>
                  <th class="py-3 px-3 whitespace-nowrap">注册时间</th>
                  <th class="py-3 px-3 whitespace-nowrap">状态</th>
                  <th class="py-3 px-4 text-right whitespace-nowrap">操作</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-100 dark:divide-slate-800/60">
                <tr v-for="u in paginatedUsers" :key="u.id" class="hover:bg-slate-50 dark:hover:bg-slate-800/40 transition-colors">
                  <td class="py-3 px-4 flex items-center gap-2.5 whitespace-nowrap">
                    <div class="w-8 h-8 rounded-xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center text-sm font-bold shadow-sm shrink-0">
                      {{ u.avatar || '👨‍💻' }}
                    </div>
                    <div>
                      <div class="font-bold text-slate-900 dark:text-slate-100">{{ u.nickname || u.username }}</div>
                      <div class="text-[10px] text-slate-400 font-mono">@{{ u.username }}</div>
                    </div>
                  </td>
                  <td class="py-3 px-3 whitespace-nowrap">
                    <span :class="u.role === 'ADMIN' ? 'bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300 border-amber-200' : 'bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300 border-slate-200'"
                          class="px-2 py-0.5 rounded-full text-[10px] font-bold border whitespace-nowrap">
                      {{ u.role === 'ADMIN' ? '👑 超级管理员' : '🌟 注册极客' }}
                    </span>
                  </td>
                  <td class="py-3 px-3 text-slate-500 font-mono whitespace-nowrap">{{ u.email || '—' }}</td>
                  <td class="py-3 px-3 text-slate-400 font-mono whitespace-nowrap">{{ formatDate(u.createdAt) }}</td>
                  <td class="py-3 px-3 whitespace-nowrap">
                    <span :class="u.status === '0' ? 'text-emerald-500 bg-emerald-50 dark:bg-emerald-950/60 border-emerald-200' : 'text-rose-500 bg-rose-50 dark:bg-rose-950/60 border-rose-200'"
                          class="px-2 py-0.5 rounded-full text-[10px] font-semibold border whitespace-nowrap">
                      {{ u.status === '0' ? '正常启用' : '已停用' }}
                    </span>
                  </td>
                  <td class="py-3 px-4 text-right whitespace-nowrap">
                    <div class="flex items-center justify-end gap-1.5 whitespace-nowrap" v-if="u.username !== 'admin'">
                      <button @click="emit('toggle-user-status', u)"
                              :class="u.status === '0' ? 'text-amber-600 hover:bg-amber-50' : 'text-emerald-600 hover:bg-emerald-50'"
                              class="px-2 py-1 rounded-lg text-[11px] font-medium transition-colors whitespace-nowrap">
                        {{ u.status === '0' ? '停用' : '启用' }}
                      </button>
                      <button @click="emit('delete-user', u)"
                              class="px-2 py-1 rounded-lg text-[11px] font-medium text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-950/40 transition-colors whitespace-nowrap">
                        删除
                      </button>
                    </div>
                    <span v-else class="text-[10px] text-slate-400 font-mono whitespace-nowrap">系统超级保留</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- 用户权限极客分页工具栏 -->
          <div v-if="(userList || []).length > 0" class="flex flex-col sm:flex-row items-center justify-between gap-3 pt-3 border-t border-slate-200 dark:border-slate-800 text-xs">
            <div class="text-slate-400 font-mono text-[11px]">
              第 <span class="font-bold text-slate-700 dark:text-slate-200">{{ userPage }}</span> / {{ totalUserPages }} 页 · 共 <span class="font-bold text-slate-700 dark:text-slate-200">{{ userList.length }}</span> 位用户
            </div>
            <div class="flex items-center gap-1.5" v-if="totalUserPages > 1">
              <button type="button"
                      @click="userPage = Math.max(1, userPage - 1)"
                      :disabled="userPage <= 1"
                      class="px-2.5 py-1.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-300 hover:border-sky-500 hover:text-sky-600 disabled:opacity-40 disabled:pointer-events-none transition-colors">
                ‹ 上一页
              </button>
              <div class="flex items-center gap-1">
                <button v-for="p in getPageNumbers(userPage, totalUserPages)" :key="p"
                        type="button"
                        @click="typeof p === 'number' && (userPage = p)"
                        :disabled="typeof p !== 'number'"
                        :class="p === userPage ? 'bg-sky-600 text-white font-bold shadow-xs' : (typeof p === 'number' ? 'bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 text-slate-600 dark:text-slate-300 hover:border-sky-500' : 'text-slate-400 cursor-default')"
                        class="w-7 h-7 rounded-lg text-xs flex items-center justify-center transition-colors">
                  {{ p }}
                </button>
              </div>
              <button type="button"
                      @click="userPage = Math.min(totalUserPages, userPage + 1)"
                      :disabled="userPage >= totalUserPages"
                      class="px-2.5 py-1.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-300 hover:border-sky-500 hover:text-sky-600 disabled:opacity-40 disabled:pointer-events-none transition-colors">
                下一页 ›
              </button>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { formatDate, getCategoryIcon } from '@/utils/formatters';

const props = defineProps({
  show: Boolean,
  initialTab: {
    type: String,
    default: 'posts',
  },
  initialCommentStatus: {
    type: String,
    default: 'ALL',
  },
  initialNoticeSubView: {
    type: String,
    default: 'compose',
  },
  allPosts: {
    type: Array,
    default: () => [],
  },
  categoryList: {
    type: Array,
    default: () => [],
  },
  categories: {
    type: Array,
    default: () => [],
  },
  adminComments: {
    type: Array,
    default: () => [],
  },
  allNotifications: {
    type: Array,
    default: () => [],
  },
  userList: {
    type: Array,
    default: () => [],
  },
  selectedUsers: {
    type: Array,
    default: () => [],
  },
  userSearch: {
    type: String,
    default: '',
  },
  showUserDropdown: Boolean,
  noticeForm: {
    type: Object,
    default: () => ({ title: '', content: '', type: 'ANNOUNCEMENT', targetUsername: 'ALL' }),
  },
  publishingNotice: Boolean,
  getPostsCountByCategory: Function,
  getPostTitle: Function,
});

const emit = defineEmits([
  'close',
  'view-post',
  'open-publish',
  'edit-post',
  'toggle-post-status',
  'delete-post',
  'open-category-modal',
  'delete-category',
  'refresh-comments',
  'audit-comment',
  'toggle-comment-top',
  'delete-comment',
  'set-notice-type',
  'toggle-user-dropdown',
  'toggle-user',
  'toggle-select-all-users',
  'remove-user',
  'update:userSearch',
  'publish-notification',
  'delete-notification',
  'reply-feedback',
  'refresh-users',
  'toggle-user-status',
  'delete-user',
]);

const activeTab = ref(props.initialTab);
const postsSearch = ref('');
const postsCategory = ref('全部');
const showCatDropdown = ref(false);

// 评论多维筛选与精准定位状态
const commentFilterPostId = ref('all');
const commentFilterStatus = ref(props.initialCommentStatus || 'ALL');
const commentSearchKeyword = ref('');

watch(() => props.initialTab, (newTab) => {
  activeTab.value = newTab;
  if (newTab === 'comments' && props.initialCommentStatus) {
    commentFilterStatus.value = props.initialCommentStatus;
  }
});

watch(() => props.initialCommentStatus, (newStatus) => {
  if (newStatus) {
    commentFilterStatus.value = newStatus;
  }
});

const pendingCommentsCount = computed(() => (props.adminComments || []).filter(c => c.status === 'PENDING').length);
const approvedCommentsCount = computed(() => (props.adminComments || []).filter(c => c.status === 'APPROVED').length);
const rejectedCommentsCount = computed(() => (props.adminComments || []).filter(c => c.status === 'REJECTED').length);

function getCommentsCountByPost(postId) {
  if (!props.adminComments) return 0;
  return props.adminComments.filter(c => String(c.postId) === String(postId)).length;
}

function switchTabToComments(postId) {
  activeTab.value = 'comments';
  commentFilterPostId.value = String(postId);
  commentFilterStatus.value = 'ALL';
  commentSearchKeyword.value = '';
}

const selectedPostInfo = computed(() => {
  if (commentFilterPostId.value === 'all') return null;
  return props.allPosts.find(p => String(p.id) === String(commentFilterPostId.value));
});

const filteredAdminComments = computed(() => {
  let list = props.adminComments || [];
  if (commentFilterPostId.value !== 'all') {
    list = list.filter(c => String(c.postId) === String(commentFilterPostId.value));
  }
  if (commentFilterStatus.value !== 'ALL') {
    list = list.filter(c => c.status === commentFilterStatus.value);
  }
  if (commentSearchKeyword.value.trim()) {
    const kw = commentSearchKeyword.value.trim().toLowerCase();
    list = list.filter(c => 
      (c.content && c.content.toLowerCase().includes(kw)) ||
      (c.author && c.author.toLowerCase().includes(kw)) ||
      (c.sensitiveWords && c.sensitiveWords.toLowerCase().includes(kw)) ||
      (props.getPostTitle && props.getPostTitle(c.postId).toLowerCase().includes(kw))
    );
  }
  return list;
});

function handleRefreshComments() {
  emit('refresh-comments', commentFilterPostId.value === 'all' ? null : commentFilterPostId.value);
}

watch(() => props.initialTab, (t) => {
  activeTab.value = t;
});

const filteredPosts = computed(() => {
  let list = props.allPosts.slice();
  if (postsCategory.value && postsCategory.value !== '全部') {
    list = list.filter(p => p.categoryName === postsCategory.value);
  }
  if (postsSearch.value.trim()) {
    const kw = postsSearch.value.trim().toLowerCase();
    list = list.filter(p => (p.title && p.title.toLowerCase().includes(kw)) ||
                            (p.author && p.author.toLowerCase().includes(kw)));
  }
  return list;
});

const isAllSelected = computed(() => {
  return props.userList && props.userList.length > 0 && props.selectedUsers.length === props.userList.length;
});

const filteredUsers = computed(() => {
  const list = props.userList || [];
  const kw = (props.userSearch || '').trim().toLowerCase();
  if (!kw) return list;
  return list.filter(u => {
    return (u.username && u.username.toLowerCase().includes(kw)) ||
           (u.nickname && u.nickname.toLowerCase().includes(kw));
  });
});

// 站内消息通知子视图模式与历史记录五态分类状态
const noticeSubView = ref(props.initialNoticeSubView || 'compose'); // 'compose' | 'history'
const noticeHistoryCategory = ref('ALL'); // 'ALL' | 'ANNOUNCEMENT' | 'PRIVATE' | 'AUDIT' | 'FEEDBACK'
const noticeHistorySearch = ref('');

watch(() => props.initialNoticeSubView, (val) => {
  if (val) {
    noticeSubView.value = val;
  }
});

// 历史通知分类与实时计数
const noticeHistoryTabs = computed(() => {
  const list = props.allNotifications || [];
  return [
    { key: 'ALL', label: '全部', icon: '📋', count: list.length },
    { key: 'ANNOUNCEMENT', label: '全站公告', icon: '📢', count: list.filter(n => n.type === 'ANNOUNCEMENT').length },
    { key: 'REPLY', label: '互动回复', icon: '💬', count: list.filter(n => n.type === 'REPLY').length },
    { key: 'PRIVATE', label: '定向私信', icon: '✉️', count: list.filter(n => n.type === 'NOTICE' || n.type === 'SYSTEM').length },
    { key: 'AUDIT', label: '审核通知', icon: '⚖️', count: list.filter(n => n.type === 'AUDIT').length },
    { key: 'FEEDBACK', label: '意见建议', icon: '💡', count: list.filter(n => n.type === 'FEEDBACK').length },
  ];
});

// 历史通知分类与关键词实时过滤
const filteredHistoryNotifications = computed(() => {
  let list = props.allNotifications || [];
  if (noticeHistoryCategory.value === 'ANNOUNCEMENT') {
    list = list.filter(n => n.type === 'ANNOUNCEMENT');
  } else if (noticeHistoryCategory.value === 'REPLY') {
    list = list.filter(n => n.type === 'REPLY');
  } else if (noticeHistoryCategory.value === 'PRIVATE') {
    list = list.filter(n => n.type === 'NOTICE' || n.type === 'SYSTEM');
  } else if (noticeHistoryCategory.value === 'AUDIT') {
    list = list.filter(n => n.type === 'AUDIT');
  } else if (noticeHistoryCategory.value === 'FEEDBACK') {
    list = list.filter(n => n.type === 'FEEDBACK');
  }

  if (noticeHistorySearch.value.trim()) {
    const kw = noticeHistorySearch.value.trim().toLowerCase();
    list = list.filter(n =>
      (n.title && n.title.toLowerCase().includes(kw)) ||
      (n.content && n.content.toLowerCase().includes(kw)) ||
      (n.targetUsername && n.targetUsername.toLowerCase().includes(kw)) ||
      (n.sender && n.sender.toLowerCase().includes(kw))
    );
  }
  return list;
});

// 历史通知类型徽标样式辅助
function getNoticeTypeBadge(type) {
  if (type === 'ANNOUNCEMENT') {
    return {
      icon: '📢',
      label: '全站公告',
      class: 'bg-sky-50 dark:bg-sky-950/60 text-sky-600 dark:text-sky-300 border-sky-200 dark:border-sky-800'
    };
  }
  if (type === 'REPLY') {
    return {
      icon: '💬',
      label: '互动回复',
      class: 'bg-emerald-50 dark:bg-emerald-950/60 text-emerald-600 dark:text-emerald-300 border-emerald-200 dark:border-emerald-800'
    };
  }
  if (type === 'NOTICE' || type === 'SYSTEM') {
    return {
      icon: '✉️',
      label: '定向私信',
      class: 'bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-300 border-indigo-200 dark:border-indigo-800'
    };
  }
  if (type === 'AUDIT') {
    return {
      icon: '⚖️',
      label: '审核通知',
      class: 'bg-amber-50 dark:bg-amber-950/60 text-amber-700 dark:text-amber-300 border-amber-200 dark:border-amber-800'
    };
  }
  if (type === 'FEEDBACK') {
    return {
      icon: '💡',
      label: '意见建议',
      class: 'bg-purple-50 dark:bg-purple-950/60 text-purple-700 dark:text-purple-300 border-purple-200 dark:border-purple-800'
    };
  }
  return {
    icon: '📫',
    label: type || '通知',
    class: 'bg-slate-50 dark:bg-slate-800 text-slate-600 dark:text-slate-300 border-slate-200 dark:border-slate-700'
  };
}

// 在已发历史列表中快捷回复建议
function handleReplyFeedbackFromHistory(notice) {
  emit('reply-feedback', notice);
  noticeSubView.value = 'compose';
}

// ======================== 极客控制台全模块标准分页 (10 条/页) ========================
const pageSize = 10;

// 通用极客数字页码生成辅助函数 (支持折叠省略符)
function getPageNumbers(currentPage, totalPages) {
  if (totalPages <= 5) {
    return Array.from({ length: totalPages }, (_, i) => i + 1);
  }
  const pages = [];
  pages.push(1);
  let left = Math.max(2, currentPage - 1);
  let right = Math.min(totalPages - 1, currentPage + 1);

  if (currentPage <= 3) {
    left = 2;
    right = 4;
  } else if (currentPage >= totalPages - 2) {
    left = totalPages - 3;
    right = totalPages - 1;
  }

  if (left > 2) {
    pages.push('...');
  }
  for (let i = left; i <= right; i++) {
    pages.push(i);
  }
  if (right < totalPages - 1) {
    pages.push('...');
  }
  pages.push(totalPages);
  return pages;
}

// 1. 文章管理分页
const postPage = ref(1);
const totalPostPages = computed(() => Math.ceil(filteredPosts.value.length / pageSize) || 1);
const paginatedPosts = computed(() => {
  const start = (postPage.value - 1) * pageSize;
  return filteredPosts.value.slice(start, start + pageSize);
});
watch([postsSearch, postsCategory], () => {
  postPage.value = 1;
});
watch(totalPostPages, (max) => {
  if (postPage.value > max) postPage.value = max;
  if (postPage.value < 1) postPage.value = 1;
});

// 2. 评论控制分页
const commentPage = ref(1);
const totalCommentPages = computed(() => Math.ceil(filteredAdminComments.value.length / pageSize) || 1);
const paginatedComments = computed(() => {
  const start = (commentPage.value - 1) * pageSize;
  return filteredAdminComments.value.slice(start, start + pageSize);
});
watch([commentFilterPostId, commentFilterStatus, commentSearchKeyword], () => {
  commentPage.value = 1;
});
watch(totalCommentPages, (max) => {
  if (commentPage.value > max) commentPage.value = max;
  if (commentPage.value < 1) commentPage.value = 1;
});

// 3. 消息通知历史分页
const noticePage = ref(1);
const totalNoticePages = computed(() => Math.ceil(filteredHistoryNotifications.value.length / pageSize) || 1);
const paginatedHistoryNotifications = computed(() => {
  const start = (noticePage.value - 1) * pageSize;
  return filteredHistoryNotifications.value.slice(start, start + pageSize);
});
watch([noticeHistoryCategory, noticeHistorySearch], () => {
  noticePage.value = 1;
});
watch(totalNoticePages, (max) => {
  if (noticePage.value > max) noticePage.value = max;
  if (noticePage.value < 1) noticePage.value = 1;
});

// 4. 用户权限管理分页
const userPage = ref(1);
const totalUserPages = computed(() => Math.ceil((props.userList || []).length / pageSize) || 1);
const paginatedUsers = computed(() => {
  const list = props.userList || [];
  const start = (userPage.value - 1) * pageSize;
  return list.slice(start, start + pageSize);
});
watch(totalUserPages, (max) => {
  if (userPage.value > max) userPage.value = max;
  if (userPage.value < 1) userPage.value = 1;
});
</script>
