<template>
  <!-- ======================= 视图 2：沉浸式文章详情阅读页 (POST VIEW - 1440px 宽屏极客排版) ======================= -->
  <main v-if="post" class="flex-1 max-w-[1440px] w-full mx-auto px-4 sm:px-6 lg:px-8 py-6">
    
    <!-- 面包屑与顶部返回导航 -->
    <div class="mb-6 flex items-center justify-between">
      <button @click="emit('back')"
              class="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 hover:border-sky-500 text-xs font-semibold transition-all shadow-sm">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"></path></svg>
        <span>返回文章列表</span>
      </button>

      <div class="flex items-center gap-2">
        <button @click="emit('share')"
                class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-xs font-medium hover:border-slate-300 dark:hover:border-slate-700 transition-colors">
          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z"></path></svg>
          <span>分享本文</span>
        </button>
        <button v-if="isAdmin" @click="emit('delete-post', post)"
                class="inline-flex items-center gap-1 px-3 py-1.5 rounded-xl text-xs text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-950/40 border border-rose-200/50 dark:border-rose-900/50 transition-colors">
          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
          <span>删除文章</span>
        </button>
      </div>
    </div>

    <!-- 主文章与大纲侧边栏 12 栏宽屏布局 -->
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
      
      <!-- PC 宽屏左侧悬浮快捷操作坞 (hidden xl:flex xl:col-span-1) -->
      <aside class="hidden xl:flex xl:col-span-1 flex-col items-center gap-3 sticky top-24">
        <button @click="emit('like', post)"
                class="group p-3 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm hover:shadow-md hover:scale-110 active:scale-95 transition-all text-center w-full"
                title="点赞本文">
          <svg class="w-5 h-5 mx-auto text-rose-500 fill-rose-500 group-hover:scale-110 transition-transform" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z" clip-rule="evenodd"></path></svg>
          <span class="block text-[11px] font-mono font-bold text-slate-600 dark:text-slate-300 mt-1">{{ post.likes || 0 }}</span>
        </button>
        <button @click="scrollToComments"
                class="p-3 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm hover:shadow-md hover:scale-110 active:scale-95 transition-all text-slate-600 dark:text-slate-300 text-center w-full"
                title="直达评论区">
          <svg class="w-5 h-5 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path></svg>
          <span class="block text-[11px] font-mono font-bold mt-1">{{ comments.length }}</span>
        </button>
        <button @click="emit('share')"
                class="p-3 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm hover:shadow-md hover:scale-110 active:scale-95 transition-all text-slate-600 dark:text-slate-300 text-center w-full"
                title="复制永久链接">
          <svg class="w-5 h-5 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z"></path></svg>
        </button>
        <button @click="emit('scroll-top')"
                class="p-3 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm hover:shadow-md hover:scale-110 active:scale-95 transition-all text-slate-600 dark:text-slate-300 text-center w-full"
                title="回到顶部">
          <svg class="w-5 h-5 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18"></path></svg>
        </button>
      </aside>

      <!-- 中间主文章内容区 (占 8 栏) -->
      <div class="lg:col-span-8 xl:col-span-8">
        <article class="p-6 sm:p-10 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm">
          
          <!-- 文章元信息头 -->
          <header class="mb-8 pb-6 border-b border-slate-100 dark:border-slate-800">
            <div class="flex items-center gap-2 mb-3">
              <span class="px-2.5 py-1 text-xs font-bold rounded-lg bg-sky-50 text-sky-700 dark:bg-sky-950/80 dark:text-sky-300 border border-sky-100 dark:border-sky-900/60">
                {{ post.categoryName }}
              </span>
              <span class="text-xs text-slate-400">·</span>
              <span class="text-xs text-slate-500 font-mono">发布于 {{ formatDate(post.createdAt) }}</span>
            </div>

            <h1 class="text-2xl sm:text-4xl font-black text-slate-900 dark:text-white tracking-tight leading-tight mb-4">
              {{ post.title }}
            </h1>

            <div class="flex flex-wrap items-center gap-4 text-xs text-slate-500 dark:text-slate-400 font-mono">
              <span class="flex items-center gap-1.5">
                <span class="w-5 h-5 rounded-full bg-slate-800 text-white flex items-center justify-center text-[10px]">东</span>
                <span>作者: {{ post.author || '小东' }}</span>
              </span>
              <span>·</span>
              <span>⏱️ 预计耗时 {{ post.readTime || '5 min' }}</span>
              <span>·</span>
              <span>👁️ {{ post.views || 0 }} 次阅读</span>
              <span>·</span>
              <span class="text-rose-500">❤️ {{ post.likes || 0 }} 点赞</span>
            </div>

            <!-- 标签群 -->
            <div class="mt-4 flex flex-wrap gap-1.5">
              <span v-for="tag in parseTags(post.tags)" :key="tag"
                    class="px-2 py-0.5 rounded-md bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 font-mono text-xs">
                #{{ tag }}
              </span>
            </div>
          </header>

          <!-- ✨ 2026 标配：AI 核心要点速读卡片 -->
          <div class="mb-8 p-5 rounded-2xl bg-gradient-to-br from-sky-500/5 via-indigo-500/5 to-purple-500/5 border border-sky-500/20 dark:border-sky-500/30">
            <div class="flex items-center gap-2 mb-2 text-sky-600 dark:text-sky-400 font-bold text-sm">
              <span>✨</span>
              <span>AI 智能速读 · 核心要点提炼</span>
            </div>
            <div class="text-xs sm:text-sm text-slate-700 dark:text-slate-300 leading-relaxed space-y-1.5">
              <p>📌 <strong>核心主题</strong>：{{ post.summary || post.title }}</p>
              <p>💡 <strong>实践价值</strong>：基于真实生产架构提炼，避免重复踩坑，代码与配置经过多轮全流程验证。</p>
              <p>⚡ <strong>架构设计</strong>：模块化高可用架构，支持容器化自动化持续集成与平滑部署交付。</p>
            </div>
          </div>

          <!-- 正文 Markdown 渲染 (支持点击图片展开极客全屏灯箱预览) -->
          <div class="markdown-body" v-html="renderedContent" @click="handleContentClick"></div>

          <!-- 文章底部互动与点赞 -->
          <div class="mt-12 pt-8 border-t border-slate-100 dark:border-slate-800 flex flex-col sm:flex-row items-center justify-between gap-4">
            <button @click="emit('like', post)"
                    class="group inline-flex items-center gap-2 px-6 py-3 rounded-2xl bg-rose-50 hover:bg-rose-100 dark:bg-rose-950/40 dark:hover:bg-rose-950/60 text-rose-600 dark:text-rose-400 border border-rose-200 dark:border-rose-900 transition-all active:scale-95 shadow-sm">
              <svg class="w-5 h-5 fill-rose-500 group-hover:scale-125 transition-transform" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z" clip-rule="evenodd"></path></svg>
              <span class="font-bold">点赞支持本文 ({{ post.likes || 0 }})</span>
            </button>

            <button @click="emit('share')"
                    class="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-xs font-semibold text-slate-700 dark:text-slate-200 transition-colors">
              <span>📋 复制文章链接</span>
            </button>
          </div>
        </article>

        <!-- 读者互动评论专区 (带 comments-section ID 直达) -->
        <section id="comments-section" class="mt-8 p-6 sm:p-8 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm scroll-mt-24">
          <div class="flex items-center justify-between mb-6">
            <h3 class="font-extrabold text-lg text-slate-900 dark:text-white flex items-center gap-2">
              <span>💬 读者交流与讨论</span>
              <span class="px-2 py-0.5 text-xs font-mono rounded-full bg-slate-100 dark:bg-slate-800 text-slate-500">
                {{ visibleComments.length }} 条评论
              </span>
            </h3>
          </div>

          <!-- 撰写评论区域：已登录用户 vs 未登录访客 -->
          <!-- 1. 已登录用户：撰写评论卡片 -->
          <div v-if="isLoggedIn" class="bg-slate-50/80 dark:bg-slate-800/40 p-4 sm:p-5 rounded-2xl border border-slate-200/60 dark:border-slate-700/60 mb-6 space-y-3">
            <div class="flex items-center justify-between text-xs">
              <div class="flex items-center gap-2">
                <span class="text-base">{{ currentUser?.avatar || '👨‍💻' }}</span>
                <span class="text-slate-600 dark:text-slate-300">正在以</span>
                <strong class="text-slate-900 dark:text-white">{{ currentUser?.nickname }}</strong>
                <span v-if="currentUser?.username" class="text-slate-400 font-mono text-[11px]">(@{{ currentUser?.username }})</span>
                <span :class="isAdmin ? 'bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300' : 'bg-sky-100 text-sky-800 dark:bg-sky-950 dark:text-sky-300'"
                      class="px-1.5 py-0.2 text-[10px] font-bold rounded-full">
                  {{ isAdmin ? '👑 博主' : '👤 读者' }}
                </span>
              </div>
            </div>
            <textarea ref="commentInputRef"
                      :value="newComment.content"
                      @input="newComment.content = $event.target.value"
                      rows="3"
                      placeholder="写下你的技术思考、疑问或独到见解..."
                      class="w-full px-3.5 py-2.5 rounded-xl bg-white dark:bg-slate-900 border border-slate-300 dark:border-slate-700 text-xs sm:text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 transition-shadow"></textarea>
            
            <div class="flex items-center justify-between pt-1 relative">
              <!-- 左侧：极客 Emoji 表情唤起按钮与悬浮气泡面板 -->
              <div ref="emojiPickerRef" class="relative">
                <button type="button"
                        @click="toggleEmojiPicker"
                        :class="showEmojiPicker 
                          ? 'bg-sky-100 text-sky-700 dark:bg-sky-950 dark:text-sky-300 border-sky-300 dark:border-sky-700' 
                          : 'bg-white hover:bg-slate-100 dark:bg-slate-900 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300 border-slate-200 dark:border-slate-700'"
                        class="px-3 py-1.5 rounded-xl border text-xs font-semibold flex items-center gap-1.5 transition-all shadow-xs hover:scale-105 active:scale-95">
                  <span class="text-sm">😊</span>
                  <span>表情</span>
                </button>

                <!-- 向上弹出的极客表情气泡选择浮层 -->
                <div v-if="showEmojiPicker"
                     class="absolute bottom-full left-0 mb-2.5 w-72 sm:w-80 p-3 rounded-2xl bg-white/95 dark:bg-slate-900/95 backdrop-blur-md border border-slate-200 dark:border-slate-800 shadow-2xl z-40 animate-in fade-in zoom-in-95 duration-150">
                  <!-- 分类 Tab 切换 -->
                  <div class="flex items-center justify-between pb-2 mb-2 border-b border-slate-100 dark:border-slate-800 text-[11px] font-bold">
                    <button v-for="(cat, idx) in emojiCategories" :key="cat.name"
                            type="button"
                            @click="activeEmojiTab = idx"
                            :class="activeEmojiTab === idx 
                              ? 'text-sky-600 dark:text-sky-400 bg-sky-50 dark:bg-sky-950/70 border-sky-200 dark:border-sky-800' 
                              : 'text-slate-500 hover:text-slate-800 dark:hover:text-slate-200 border-transparent'"
                            class="px-2 py-1 rounded-lg border flex items-center gap-1 transition-colors">
                      <span>{{ cat.icon }}</span>
                      <span>{{ cat.name }}</span>
                    </button>
                  </div>

                  <!-- Emoji 网格 -->
                  <div class="grid grid-cols-6 sm:grid-cols-7 gap-1.5 max-h-44 overflow-y-auto custom-scrollbar p-1">
                    <button v-for="emoji in emojiCategories[activeEmojiTab].emojis" :key="emoji"
                            type="button"
                            @click="insertEmoji(emoji)"
                            class="w-8 h-8 rounded-lg flex items-center justify-center text-lg hover:bg-slate-100 dark:hover:bg-slate-800 hover:scale-125 active:scale-95 transition-transform select-none">
                      {{ emoji }}
                    </button>
                  </div>

                  <!-- 气泡底端小三角指示标 -->
                  <div class="absolute -bottom-1.5 left-5 w-3 h-3 bg-white dark:bg-slate-900 border-b border-r border-slate-200 dark:border-slate-800 rotate-45"></div>
                </div>
              </div>

              <!-- 右侧：提交评论按钮 -->
              <button @click="emit('submit-comment')" :disabled="submittingComment"
                      class="px-5 py-2 rounded-xl bg-sky-600 hover:bg-sky-700 disabled:opacity-50 text-white text-xs font-bold shadow-sm transition-all flex items-center gap-1.5 active:scale-95">
                <span v-if="submittingComment" class="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
                <span>{{ submittingComment ? '提交中...' : '提交评论' }}</span>
              </button>
            </div>
          </div>

          <!-- 2. 未登录访客：极客质感登录引导卡片 (杜绝匿名灌水) -->
          <div v-else class="mb-6 p-6 sm:p-7 rounded-2xl bg-gradient-to-br from-slate-50 via-sky-50/30 to-indigo-50/20 dark:from-slate-800/40 dark:via-slate-800/20 dark:to-slate-900/40 border border-slate-200/80 dark:border-slate-700/60 text-center space-y-3.5 shadow-xs">
            <div class="w-12 h-12 mx-auto rounded-2xl bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 shadow-sm flex items-center justify-center text-2xl">
              🔒
            </div>
            <div class="space-y-1">
              <h4 class="font-bold text-sm text-slate-800 dark:text-slate-100">登录读者账号参与技术交流</h4>
              <p class="text-xs text-slate-500 dark:text-slate-400 max-w-md mx-auto leading-relaxed">
                为杜绝垃圾广告与恶意刷屏，保障高质量技术社区交流氛围，评论功能仅面向已认证读者开放。登录后即可畅所欲言。
              </p>
            </div>
            <div class="pt-1">
              <button @click="emit('open-auth', 'login')"
                      class="px-5 py-2.5 rounded-xl bg-gradient-to-r from-sky-600 to-indigo-600 hover:from-sky-700 hover:to-indigo-700 text-white font-bold text-xs shadow-md shadow-sky-600/20 transition-all inline-flex items-center gap-1.5 active:scale-95">
                <span>👉</span>
                <span>立即登录 / 注册读者账号</span>
              </button>
            </div>
          </div>

          <!-- 评论展示列表 (两级楼中楼树形渲染与行内即时回复) -->
          <div v-if="structuredComments.length > 0" class="space-y-4">
            <div v-for="root in structuredComments" :key="root.id" 
                 :id="'comment-' + root.id"
                 :class="[
                   highlightCommentId && String(highlightCommentId) === String(root.id) ? 'ring-2 ring-sky-500 shadow-xl shadow-sky-500/20 scroll-mt-28' : '',
                   root.status === 'PENDING' ? 'border-amber-400 bg-amber-50/50 dark:bg-amber-950/30 ring-1 ring-amber-400/50 shadow-sm' :
                   root.isTop ? 'border-amber-400/60 bg-amber-50/20 dark:bg-amber-950/20 shadow-sm shadow-amber-500/5 ring-1 ring-amber-400/30' : 
                   'border-slate-100 dark:border-slate-800/80 bg-white dark:bg-slate-900'
                 ]"
                 class="p-4 sm:p-5 rounded-2xl border transition-all relative">
              
              <!-- 1. 根评论头部 -->
              <div class="flex items-center justify-between mb-2 text-xs">
                <div class="flex items-center gap-2 flex-wrap">
                  <div class="w-7 h-7 rounded-xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center text-sm shadow-sm">
                    {{ root.avatar || (root.role === 'ADMIN' ? '👑' : '👨‍💻') }}
                  </div>
                  <span class="font-bold text-slate-900 dark:text-slate-100">{{ (root.author && root.author !== 'null') ? root.author : '匿名访客' }}</span>
                  
                  <!-- 审核状态指示胶囊 -->
                  <span v-if="root.status === 'PENDING'"
                        class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-500 text-white shadow-xs flex items-center gap-1">
                    <span>⏳</span> <span>审核中 (仅自己与博主可见)</span>
                  </span>

                  <span v-if="root.isTop && root.status === 'APPROVED'" class="px-2 py-0.5 rounded text-[10px] font-bold bg-amber-500 text-white shadow-sm flex items-center gap-0.5">
                    <span>📌</span> <span>置顶</span>
                  </span>
                  <span v-if="root.role === 'ADMIN'" class="px-1.5 py-0.5 rounded text-[10px] font-bold bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300 border border-amber-200 dark:border-amber-800">
                    👑 博主/管理员
                  </span>
                  <span v-else-if="root.role === 'USER'" class="px-1.5 py-0.5 rounded text-[10px] font-medium bg-sky-100 text-sky-800 dark:bg-sky-950 dark:text-sky-300 border border-sky-200 dark:border-sky-800">
                    读者
                  </span>
                </div>

                <div class="flex items-center gap-2">
                  <span class="text-xs text-slate-400 font-mono">{{ formatDate(root.createdAt) }}</span>
                  
                  <!-- 回复操作按钮 (仅审核通过的评论才允许回复) -->
                  <button v-if="root.status === 'APPROVED'"
                          @click="startReply(root, root)"
                          class="px-2 py-1 rounded-lg text-xs font-semibold text-sky-600 hover:text-sky-700 hover:bg-sky-50 dark:hover:bg-sky-950/60 transition-colors flex items-center gap-1 ml-1"
                          title="回复此条评论">
                    <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6"></path></svg>
                    <span>回复</span>
                  </button>

                  <!-- 博主管理员行内快捷置顶与删除 (仅审核通过的评论才允许置顶) -->
                  <div v-if="isAdmin" class="flex items-center gap-1.5 ml-1 border-l border-slate-200 dark:border-slate-700 pl-2">
                    <button v-if="root.status === 'APPROVED'"
                            @click="emit('toggle-comment-top', root)"
                            :class="root.isTop ? 'text-amber-600 dark:text-amber-400 bg-amber-50 dark:bg-amber-950/60' : 'text-slate-400 hover:text-amber-500'"
                            class="p-1 rounded-lg text-xs font-semibold flex items-center gap-0.5 transition-colors"
                            :title="root.isTop ? '取消置顶' : '置顶此评论'">
                      <span>📌</span>
                      <span class="hidden sm:inline">{{ root.isTop ? '取消置顶' : '置顶' }}</span>
                    </button>
                    <button @click="emit('delete-comment', root)"
                            class="p-1 rounded-lg text-slate-400 hover:text-rose-500 hover:bg-rose-50 dark:hover:bg-rose-950/40 text-xs transition-colors"
                            title="删除评论">
                      <span>🗑️</span>
                      <span class="hidden sm:inline">删除</span>
                    </button>
                  </div>
                </div>
              </div>

              <!-- 根评论正文 -->
              <p class="text-slate-700 dark:text-slate-300 leading-relaxed pl-9 text-xs sm:text-sm">{{ root.content }}</p>

              <!-- 待审核提示 -->
              <div v-if="root.status === 'PENDING'"
                   class="mt-2.5 ml-9 p-2.5 rounded-xl bg-amber-100/70 dark:bg-amber-950/50 border border-amber-300/50 dark:border-amber-800/60 text-[11px] text-amber-800 dark:text-amber-300 flex items-center gap-1.5 leading-snug">
                <span>💡</span>
                <span>此评论包含敏感词汇触发1级安全检测，目前处于待审核状态，博主人工审核通过后将对全网公开。</span>
              </div>

              <!-- ================= 2. 楼中楼子评论嵌套区域 (Nested Replies) ================= -->
              <div v-if="root.replies && root.replies.length > 0"
                   class="mt-3 ml-2 sm:ml-9 p-3 sm:p-3.5 rounded-xl bg-slate-50/90 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-800/70 space-y-3">
                
                <div v-for="reply in root.replies" :key="reply.id"
                     :id="'comment-' + reply.id"
                     :class="highlightCommentId && String(highlightCommentId) === String(reply.id) ? 'bg-sky-50/90 dark:bg-sky-950/70 ring-1 ring-sky-500 p-2.5 rounded-xl transition-all shadow-sm' : ''"
                     class="text-xs space-y-1.5 pb-2.5 border-b border-slate-200/50 dark:border-slate-700/50 last:border-none last:pb-0 scroll-mt-28">
                  <div class="flex items-center justify-between">
                    <div class="flex items-center gap-1.5 flex-wrap">
                      <div class="w-5 h-5 rounded-md bg-slate-200 dark:bg-slate-700 flex items-center justify-center text-[11px] shrink-0">
                        {{ reply.avatar || (reply.role === 'ADMIN' ? '👑' : '👨‍💻') }}
                      </div>
                      <span class="font-bold text-slate-800 dark:text-slate-200">{{ reply.author }}</span>
                      
                      <span v-if="reply.role === 'ADMIN'" class="px-1 py-0.2 rounded text-[9px] font-bold bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300">
                        👑 博主
                      </span>
                      
                      <!-- 回复指向标记 -->
                      <span v-if="reply.replyTo" class="text-slate-400 font-normal flex items-center gap-1">
                        <span>回复</span>
                        <span class="text-sky-600 dark:text-sky-400 font-semibold">@{{ reply.replyTo }}</span>
                      </span>

                      <!-- 待审核标识 -->
                      <span v-if="reply.status === 'PENDING'"
                            class="px-1.5 py-0.2 rounded-full text-[9px] font-bold bg-amber-500 text-white">
                        审核中
                      </span>
                    </div>

                    <div class="flex items-center gap-1.5">
                      <span class="text-[10px] text-slate-400 font-mono">{{ formatDate(reply.createdAt) }}</span>
                      
                      <!-- 仅审核通过的子评论才允许被回复 -->
                      <button v-if="reply.status === 'APPROVED'"
                              @click="startReply(root, reply)"
                              class="text-[11px] text-slate-500 hover:text-sky-600 dark:hover:text-sky-400 font-medium px-1.5 py-0.5 rounded transition-colors flex items-center gap-0.5"
                              title="回复此条">
                        <span>回复</span>
                      </button>

                      <button v-if="isAdmin" @click="emit('delete-comment', reply)"
                              class="text-[11px] text-slate-400 hover:text-rose-500 px-1 py-0.5 transition-colors"
                              title="删除子评论">
                        ✕
                      </button>
                    </div>
                  </div>

                  <p class="text-slate-700 dark:text-slate-300 pl-6 text-xs leading-relaxed">
                    {{ reply.content }}
                  </p>
                </div>
              </div>

              <!-- ================= 3. 行内专属回复输入框 (Inline Reply Box) ================= -->
              <div v-if="activeReplyContext && activeReplyContext.rootId === root.id"
                   class="mt-3 ml-2 sm:ml-9 p-3 sm:p-3.5 rounded-xl bg-sky-50/60 dark:bg-sky-950/30 border border-sky-200 dark:border-sky-900/60 space-y-2 animate-in fade-in zoom-in-95 duration-150">
                
                <div class="flex items-center justify-between text-xs">
                  <div class="flex items-center gap-1.5 text-sky-700 dark:text-sky-300 font-semibold">
                    <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6"></path></svg>
                    <span>正在回复</span>
                    <span class="px-1.5 py-0.5 rounded bg-sky-200/70 dark:bg-sky-900 text-sky-800 dark:text-sky-200 font-bold">@{{ activeReplyContext.replyTo }}</span>
                  </div>
                  <button @click="cancelReply" class="text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 text-xs flex items-center gap-0.5">
                    <span>✕ 取消</span>
                  </button>
                </div>

                <textarea ref="replyingInputRef"
                          v-model="activeReplyContext.content"
                          @keydown.ctrl.enter.prevent="handleSendReply"
                          @keydown.meta.enter.prevent="handleSendReply"
                          @keydown.esc.prevent="cancelReply"
                          rows="2"
                          :placeholder="'回复 @' + activeReplyContext.replyTo + ' (按 Ctrl+Enter 快捷发送)...'"
                          class="w-full px-3 py-2 rounded-lg bg-white dark:bg-slate-900 border border-slate-300 dark:border-slate-700 text-xs focus:outline-none focus:ring-2 focus:ring-sky-500"></textarea>

                <div class="flex items-center justify-between pt-0.5 relative">
                  <!-- 行内 Emoji 选择浮层 -->
                  <div ref="replyEmojiPickerRef" class="relative">
                    <button type="button"
                            @click.stop="showReplyEmojiPicker = !showReplyEmojiPicker"
                            class="px-2.5 py-1 rounded-lg border text-xs bg-white dark:bg-slate-900 hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300 border-slate-200 dark:border-slate-700 flex items-center gap-1">
                      <span>😊</span>
                      <span>表情</span>
                    </button>

                    <div v-if="showReplyEmojiPicker"
                         class="absolute bottom-full left-0 mb-2 w-64 sm:w-72 p-2.5 rounded-xl bg-white/95 dark:bg-slate-900/95 backdrop-blur-md border border-slate-200 dark:border-slate-800 shadow-xl z-50">
                      <div class="grid grid-cols-6 gap-1 max-h-36 overflow-y-auto custom-scrollbar p-1">
                        <button v-for="emoji in emojiCategories[0].emojis.concat(emojiCategories[2].emojis.slice(0, 6))" :key="emoji"
                                type="button"
                                @click="insertReplyEmoji(emoji)"
                                class="w-7 h-7 rounded flex items-center justify-center text-base hover:bg-slate-100 dark:hover:bg-slate-800 select-none">
                          {{ emoji }}
                        </button>
                      </div>
                    </div>
                  </div>

                  <div class="flex items-center gap-2">
                    <button type="button" @click="cancelReply"
                            class="px-3 py-1.5 rounded-lg text-xs font-semibold text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800">
                      取消
                    </button>
                    <button type="button"
                            @click="handleSendReply"
                            :disabled="submittingComment || !activeReplyContext.content.trim()"
                            class="px-4 py-1.5 rounded-lg bg-sky-600 hover:bg-sky-700 disabled:opacity-50 text-white text-xs font-bold shadow-xs transition-all flex items-center gap-1">
                      <span v-if="submittingComment" class="w-3 h-3 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
                      <span>{{ submittingComment ? '发送中...' : '发送回复' }}</span>
                    </button>
                  </div>
                </div>

              </div>

            </div>
          </div>
          <div v-else class="text-center py-8 text-xs text-slate-400">
            暂无评论，留下你的第一条精彩见解吧！
          </div>
        </section>
      </div>

      <!-- 右侧悬浮目录大纲 (TOC Sidebar, 占 4 栏 / xl: 占 3 栏) -->
      <aside class="hidden lg:block lg:col-span-4 xl:col-span-3 space-y-5 lg:sticky lg:top-20">
        <div class="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm">
          <div class="flex items-center gap-2 font-bold text-xs uppercase tracking-wider text-slate-400 dark:text-slate-500 mb-4 pb-2 border-b border-slate-100 dark:border-slate-800">
            <span>📑 文章大纲目录</span>
          </div>
          
          <nav v-if="toc.length > 0" class="space-y-1.5 text-xs max-h-[55vh] overflow-y-auto pr-1">
            <a v-for="item in toc" :key="item.id"
               :href="'#' + item.id"
               @click.prevent="emit('scroll-to-heading', item.id)"
               :class="activeTocId === item.id 
                  ? 'text-sky-600 dark:text-sky-400 font-bold bg-sky-50 dark:bg-sky-950/60 rounded-lg px-2' 
                  : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white px-2'"
               :style="{ paddingLeft: (item.level === 1 ? '0.5rem' : item.level === 2 ? '1.25rem' : '2rem') }"
               class="block py-1 rounded transition-colors truncate">
              {{ item.text }}
            </a>
          </nav>
          <div v-else class="text-xs text-slate-400">
            本文未设置分级标题
          </div>

          <!-- 快捷工具按钮 -->
          <div class="mt-6 pt-4 border-t border-slate-100 dark:border-slate-800 flex flex-col gap-2">
            <button @click="emit('scroll-top')" class="w-full py-2 rounded-xl text-xs font-semibold bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 transition-colors">
              ↑ 回到顶部
            </button>
          </div>
        </div>

        <!-- 侧栏热榜精选推荐 -->
        <div class="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm">
          <div class="flex items-center justify-between mb-3 pb-2 border-b border-slate-100 dark:border-slate-800">
            <span class="text-xs font-bold text-slate-800 dark:text-slate-200 flex items-center gap-1.5">
              <span>🔥</span> <span>热门推荐</span>
            </span>
          </div>
          <div class="space-y-3">
            <div v-for="p in trendingPosts.slice(0, 4)" :key="p.id"
                 @click="emit('view-post', p)"
                 class="cursor-pointer group text-xs">
              <h4 class="font-bold text-slate-800 dark:text-slate-200 group-hover:text-sky-600 dark:group-hover:text-sky-400 line-clamp-1">
                {{ p.title }}
              </h4>
              <div class="text-[10px] text-slate-400 font-mono mt-0.5">👁️ {{ p.views || 0 }} 次阅读</div>
            </div>
          </div>
        </div>
      </aside>

    </div>

    <!-- 移动端文章阅读悬浮操作胶囊 (Mobile Bottom Reading Pill) -->
    <div class="md:hidden fixed bottom-4 left-1/2 -translate-x-1/2 z-40 bg-white/95 dark:bg-slate-900/95 backdrop-blur-xl border border-slate-200 dark:border-slate-800 rounded-full px-5 py-2.5 shadow-2xl flex items-center gap-4 text-xs">
      <button @click="emit('like', post)"
              class="flex items-center gap-1.5 text-rose-500 font-mono font-bold active:scale-125 transition-transform" title="点赞支持">
        <svg class="w-4 h-4 fill-rose-500" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z" clip-rule="evenodd"></path></svg>
        <span>{{ post.likes || 0 }}</span>
      </button>

      <span class="w-px h-3.5 bg-slate-200 dark:bg-slate-700"></span>

      <button @click="scrollToComments"
              class="flex items-center gap-1.5 text-slate-600 dark:text-slate-300 font-mono font-bold" title="直达评论">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path></svg>
        <span>{{ visibleComments.length }}</span>
      </button>

      <span class="w-px h-3.5 bg-slate-200 dark:bg-slate-700"></span>

      <button @click="showMobileToc = true"
              class="flex items-center gap-1 text-sky-600 dark:text-sky-400 font-bold" title="展开文章目录">
        <span>📑</span>
        <span>目录</span>
      </button>

      <span class="w-px h-3.5 bg-slate-200 dark:bg-slate-700"></span>

      <button @click="emit('scroll-top')"
              class="text-slate-500 hover:text-slate-800 dark:text-slate-400" title="回到顶部">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18"></path></svg>
      </button>
    </div>

    <!-- 移动端长文章节大纲半屏抽屉 (Mobile TOC Drawer) -->
    <div v-if="showMobileToc"
         class="fixed inset-0 z-50 overflow-hidden md:hidden animate-in fade-in duration-200">
      <div class="absolute inset-0 bg-slate-900/60 backdrop-blur-sm transition-opacity"
           @click="showMobileToc = false"></div>
      
      <div class="fixed inset-x-0 bottom-0 max-h-[75vh] bg-white dark:bg-slate-900 rounded-t-3xl border-t border-slate-200 dark:border-slate-800 shadow-2xl flex flex-col p-5 overflow-hidden animate-in slide-in-from-bottom duration-300">
        <div class="w-10 h-1 bg-slate-300 dark:bg-slate-700 rounded-full mx-auto mb-3"></div>
        <div class="flex items-center justify-between pb-3 border-b border-slate-100 dark:border-slate-800">
          <div class="font-bold text-sm text-slate-900 dark:text-white flex items-center gap-2">
            <span>📑</span>
            <span>文章大纲目录 ({{ toc.length }} 节)</span>
          </div>
          <button @click="showMobileToc = false" class="p-1 rounded-lg text-slate-400 hover:text-slate-600">✕</button>
        </div>

        <div v-if="toc.length > 0" class="overflow-y-auto py-3 space-y-1 text-xs">
          <a v-for="item in toc" :key="item.id"
             :href="'#' + item.id"
             @click.prevent="emit('scroll-to-heading', item.id); showMobileToc = false;"
             :class="activeTocId === item.id 
                ? 'text-sky-600 dark:text-sky-400 font-bold bg-sky-50 dark:bg-sky-950/60 rounded-xl px-3 py-2' 
                : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white px-3 py-2'"
             :style="{ paddingLeft: (item.level === 1 ? '0.75rem' : item.level === 2 ? '1.5rem' : '2.25rem') }"
             class="block rounded-xl transition-colors truncate">
            {{ item.text }}
          </a>
        </div>
        <div v-else class="text-center py-8 text-xs text-slate-400">
          本文暂未设置分级标题
        </div>
      </div>
    </div>

    <!-- 极客全屏大图预览灯箱 (支持滚轮缩放、拖拽平移、旋转与快捷键) -->
    <ImageLightboxModal
      :show="showLightbox"
      :src="lightboxSrc"
      :alt-text="lightboxAlt"
      @close="showLightbox = false"
    />
  </main>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue';
import { formatDate, parseTags } from '@/utils/formatters';
import ImageLightboxModal from '@/components/common/ImageLightboxModal.vue';

const props = defineProps({
  post: Object,
  renderedContent: String,
  toc: Array,
  activeTocId: String,
  trendingPosts: Array,
  comments: Array,
  newComment: Object,
  submittingComment: Boolean,
  currentUser: Object,
  isLoggedIn: Boolean,
  isAdmin: Boolean,
  highlightCommentId: [Number, String],
});

// 评论区安全可见性过滤：驳回/未通过的评论对任何人均不再展示
const visibleComments = computed(() => {
  return (props.comments || []).filter(c => c && c.status !== 'REJECTED');
});

// 两级楼中楼评论树结构编排 (根评论按置顶与时间倒序，子评论按时间正序排列)
const structuredComments = computed(() => {
  const all = visibleComments.value;
  const rootMap = new Map();
  const roots = [];
  const orphanChildren = [];

  all.forEach(c => {
    if (!c.parentId || c.parentId <= 0) {
      const item = { ...c, replies: [] };
      rootMap.set(c.id, item);
      roots.push(item);
    }
  });

  all.forEach(c => {
    if (c.parentId && c.parentId > 0) {
      if (rootMap.has(c.parentId)) {
        rootMap.get(c.parentId).replies.push(c);
      } else {
        orphanChildren.push({ ...c, replies: [] });
      }
    }
  });

  roots.forEach(r => {
    r.replies.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
  });

  return [...roots, ...orphanChildren];
});

// ================= 行内即时回复状态与操作 =================
const activeReplyContext = ref(null);
const replyingInputRef = ref(null);
const showReplyEmojiPicker = ref(false);
const replyEmojiPickerRef = ref(null);

function startReply(rootComment, targetComment) {
  if (rootComment?.status !== 'APPROVED' || targetComment?.status !== 'APPROVED') {
    return;
  }
  if (!props.isLoggedIn) {
    emit('open-auth', 'login');
    return;
  }

  // 若点击同一个目标的回复，直接聚焦
  if (activeReplyContext.value && activeReplyContext.value.targetCommentId === targetComment.id) {
    nextTick(() => {
      replyingInputRef.value?.focus();
    });
    return;
  }

  activeReplyContext.value = {
    rootId: rootComment.id,
    targetCommentId: targetComment.id,
    replyTo: targetComment.author || '读者',
    replyToUsername: targetComment.username || null,
    content: '',
  };

  showReplyEmojiPicker.value = false;

  nextTick(() => {
    replyingInputRef.value?.focus();
  });
}

function cancelReply() {
  activeReplyContext.value = null;
  showReplyEmojiPicker.value = false;
}

function handleSendReply() {
  if (!activeReplyContext.value) return;
  const ctx = activeReplyContext.value;
  if (!ctx.content.trim()) return;

  const payload = {
    parentId: ctx.rootId,
    replyTo: ctx.replyTo,
    replyToUsername: ctx.replyToUsername,
    content: ctx.content.trim(),
  };

  emit('submit-comment', payload);
  cancelReply();
}

function insertReplyEmoji(emoji) {
  if (!activeReplyContext.value) return;
  activeReplyContext.value.content += emoji;
  nextTick(() => {
    replyingInputRef.value?.focus();
  });
}

const emit = defineEmits([
  'back',
  'share',
  'like',
  'delete-post',
  'scroll-top',
  'scroll-to-heading',
  'view-post',
  'open-auth',
  'submit-comment',
  'toggle-comment-top',
  'delete-comment',
]);

const showMobileToc = ref(false);

const showLightbox = ref(false);
const lightboxSrc = ref('');
const lightboxAlt = ref('');

function handleContentClick(e) {
  const target = e.target;
  if (target && target.tagName === 'IMG') {
    e.preventDefault();
    lightboxSrc.value = target.src || target.getAttribute('src');
    lightboxAlt.value = target.alt || target.getAttribute('alt') || '';
    showLightbox.value = true;
  }
}

function scrollToComments() {
  const el = document.getElementById('comments-section');
  if (el) {
    el.scrollIntoView({ behavior: 'smooth' });
  }
}

// ================= 极客 Emoji 表情选择器 =================
const showEmojiPicker = ref(false);
const activeEmojiTab = ref(0);
const commentInputRef = ref(null);
const emojiPickerRef = ref(null);

const emojiCategories = [
  {
    name: '常用',
    icon: '😀',
    emojis: ['😊', '😂', '🤣', '🥹', '😍', '🤔', '🫡', '🥳', '😎', '😴', '😭', '🤯', '🤡', '🤫', '🫠', '😋']
  },
  {
    name: '极客',
    icon: '💻',
    emojis: ['💻', '🖥️', '📱', '⌨️', '🖱️', '🚀', '🛸', '🛰️', '⚙️', '🛠️', '🔌', '💡', '🔍', '🧠', '🤖', '👾', '⚡', '📡', '🌐', '📦', '🐳', '☕']
  },
  {
    name: '赞同',
    icon: '👍',
    emojis: ['👍', '👏', '🤝', '🙌', '🫶', '❤️', '💖', '🔥', '💯', '🎯', '⭐', '🌟', '🏆', '🎖️', '🍻']
  },
  {
    name: '氛围',
    icon: '🎉',
    emojis: ['🎉', '🎊', '🎈', '🎁', '🍾', '🍰', '🍕', '🍔', '🍿', '🌈', '✨', '🪄', '💣', '💥', '🎸']
  }
];

function toggleEmojiPicker(e) {
  e.stopPropagation();
  showEmojiPicker.value = !showEmojiPicker.value;
}

function insertEmoji(emoji) {
  const textarea = commentInputRef.value;
  const currentContent = props.newComment?.content || '';
  if (!textarea) {
    if (props.newComment) {
      props.newComment.content = currentContent + emoji;
    }
    return;
  }
  const start = textarea.selectionStart ?? currentContent.length;
  const end = textarea.selectionEnd ?? currentContent.length;
  const before = currentContent.substring(0, start);
  const after = currentContent.substring(end);
  props.newComment.content = before + emoji + after;
  nextTick(() => {
    textarea.focus();
    const nextCursor = start + emoji.length;
    textarea.setSelectionRange(nextCursor, nextCursor);
  });
}

function handleDocumentClick(e) {
  if (emojiPickerRef.value && !emojiPickerRef.value.contains(e.target)) {
    showEmojiPicker.value = false;
  }
  if (replyEmojiPickerRef.value && !replyEmojiPickerRef.value.contains(e.target)) {
    showReplyEmojiPicker.value = false;
  }
}

onMounted(() => {
  document.addEventListener('click', handleDocumentClick);
});

onUnmounted(() => {
  document.removeEventListener('click', handleDocumentClick);
});
</script>
