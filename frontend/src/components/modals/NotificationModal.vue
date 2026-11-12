<template>
  <!-- ======================= 模态框 3：读者站内消息通知中心 (Notification Center) ======================= -->
  <div v-if="show"
       class="fixed inset-0 z-50 overflow-hidden bg-slate-900/60 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4"
       @click.self="emit('close')">
    <div class="w-full sm:w-[760px] h-[85vh] sm:h-[660px] bg-white dark:bg-slate-900 rounded-t-3xl sm:rounded-3xl shadow-2xl border border-slate-200 dark:border-slate-800 overflow-hidden flex flex-col animate-in slide-in-from-bottom sm:zoom-in-95 duration-200 safe-bottom">
      
      <!-- 顶部标题栏 Header -->
      <div class="px-5 py-4 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between shrink-0 bg-slate-50/50 dark:bg-slate-950/40">
        <div class="flex items-center gap-2.5">
          <span class="text-xl">🔔</span>
          <div>
            <h3 class="font-bold text-sm text-slate-900 dark:text-white">站内消息与通知公告</h3>
            <div class="text-[11px] text-slate-400 font-mono">共 {{ notifications.length }} 条 · {{ unreadCount }} 条未读</div>
          </div>
        </div>

        <div class="flex items-center gap-2">
          <!-- 提改进建议按钮 -->
          <button @click="emit('open-feedback')"
                  class="px-2.5 py-1 text-xs rounded-lg font-semibold text-amber-700 bg-amber-50 hover:bg-amber-100 dark:bg-amber-950/60 dark:text-amber-300 border border-amber-200/80 dark:border-amber-800/60 transition-colors flex items-center gap-1">
            <span>💡</span>
            <span>提建议</span>
          </button>

          <!-- 按当前 Tab 一键清理已读 (未读不可清理) -->
          <button v-if="currentTabReadCount > 0"
                  @click="handleClearRead"
                  class="px-2.5 py-1 text-xs rounded-lg font-semibold text-rose-600 bg-rose-50 hover:bg-rose-100 dark:bg-rose-950 dark:text-rose-300 border border-rose-200/80 dark:border-rose-900/60 transition-colors flex items-center gap-1"
                  title="仅清理当前分类下的已读通知，未读通知受保护继续保留">
            <span>🗑️</span>
            <span>清理已读 ({{ currentTabReadCount }})</span>
          </button>

          <!-- 全部已读 -->
          <button v-if="unreadCount > 0"
                  @click="emit('mark-all-read')"
                  class="px-2.5 py-1 text-xs rounded-lg font-semibold text-sky-600 bg-sky-50 hover:bg-sky-100 dark:bg-sky-950 dark:text-sky-300 transition-colors">
            ✓ 全部已读
          </button>
          <button @click="emit('close')" class="p-1 rounded-lg text-slate-400 hover:text-slate-600 dark:hover:text-slate-200">
            ✕
          </button>
        </div>
      </div>

      <!-- 分类标签栏 Tab Bar (全部 / 公告 / 回复 / 私信 / 审核 / 建议) -->
      <div class="flex items-center gap-1.5 px-5 py-2.5 border-b border-slate-200/80 dark:border-slate-800 bg-slate-50/40 dark:bg-slate-950/20 overflow-x-auto scrollbar-none text-xs shrink-0">
        <button v-for="tab in tabs" :key="tab.key"
                @click="activeTab = tab.key"
                :class="activeTab === tab.key 
                  ? 'bg-white dark:bg-slate-800 text-slate-900 dark:text-white font-bold shadow-xs border-slate-300 dark:border-slate-700' 
                  : 'text-slate-500 dark:text-slate-400 hover:text-slate-800 dark:hover:text-slate-200 border-transparent'"
                class="px-3.5 py-1.5 rounded-xl border flex items-center gap-1.5 transition-all whitespace-nowrap">
          <span>{{ tab.icon }}</span>
          <span>{{ tab.label }}</span>
          <span v-if="tab.unread > 0"
                class="px-1.5 py-0.2 rounded-full text-[10px] font-mono font-bold bg-rose-500 text-white animate-pulse">
            {{ tab.unread > 99 ? '99+' : tab.unread }}
          </span>
        </button>
      </div>

      <!-- 通知卡片列表 Body (固定高度内自适应与独立极简细滚轮) -->
      <div class="p-4 sm:p-5 overflow-y-auto flex-1 custom-scrollbar flex flex-col">
        <div v-if="filteredNotifications.length > 0" class="space-y-3">
          <div v-for="notice in filteredNotifications" :key="notice.id"
               @click="handleNoticeClick(notice)"
               :class="!notice.isRead ? 'border-sky-300 dark:border-sky-800/80 bg-sky-50/30 dark:bg-sky-950/20 shadow-xs hover:border-sky-400' : 'border-slate-200/80 dark:border-slate-800 bg-white dark:bg-slate-900 hover:border-slate-300 dark:hover:border-slate-700'"
               class="p-4 rounded-2xl border transition-all cursor-pointer relative group hover:shadow-sm">
            
            <!-- 卡片头部：状态与标题 -->
            <div class="flex items-center justify-between gap-2 mb-2">
              <div class="flex items-center gap-2 flex-wrap">
                <span v-if="!notice.isRead" class="w-2 h-2 rounded-full bg-rose-500 animate-pulse"></span>
                
                <!-- 类型胶囊指示 -->
                <span :class="getTypeBadgeClass(notice.type)"
                      class="px-2 py-0.5 rounded text-[10px] font-bold font-mono flex items-center gap-1">
                  <span>{{ getTypeBadgeIcon(notice.type) }}</span>
                  <span>{{ getTypeBadgeLabel(notice.type) }}</span>
                </span>

                <h4 class="font-bold text-xs sm:text-sm text-slate-900 dark:text-white line-clamp-1 group-hover:text-sky-600 dark:group-hover:text-sky-400 transition-colors">
                  {{ notice.title }}
                </h4>
              </div>
              <span class="text-[11px] text-slate-400 font-mono shrink-0">{{ formatDate(notice.createdAt) }}</span>
            </div>

            <!-- 通知主体内容 -->
            <p class="text-xs text-slate-600 dark:text-slate-300 leading-relaxed whitespace-pre-line pl-1">
              {{ notice.content }}
            </p>

            <!-- 卡片底部信息与快捷动作 -->
            <div class="mt-3 pt-2.5 border-t border-slate-100 dark:border-slate-800/60 flex items-center justify-between text-[11px] text-slate-400 font-mono">
              <div class="flex items-center gap-2">
                <span>来自：{{ notice.sender }}</span>
                <span v-if="notice.targetUsername && notice.targetUsername !== 'ALL'" class="text-slate-400">
                  (发给: @{{ notice.targetUsername }})
                </span>
              </div>

              <div class="flex items-center gap-2">
                <!-- 仅当确实属于管理员待办人工审核任务时才展示前往审核按钮，已通过/驳回通知彻底绝不展示 -->
                <button v-if="canShowAuditButton(notice)"
                        @click.stop="handleGoAudit(notice)"
                        class="px-2 py-0.5 rounded-lg bg-amber-500 hover:bg-amber-600 text-white font-bold text-[10px] shadow-xs flex items-center gap-1 transition-colors">
                  <span>👉</span>
                  <span>前往审核</span>
                </button>

                <!-- 管理员快捷回复读者建议 -->
                <button v-if="notice.type === 'FEEDBACK' && isAdmin"
                        @click.stop="handleReplyFeedback(notice)"
                        class="px-2 py-0.5 rounded-lg bg-sky-600 hover:bg-sky-700 text-white font-bold text-[10px] shadow-xs flex items-center gap-1 transition-colors">
                  <span>✉️</span>
                  <span>回复此建议</span>
                </button>

                <!-- 彻底移除“点击查阅”，未读时由卡片左上角红点及底色优雅呈现，已读时显示“已读” -->
                <span v-if="notice.isRead" class="text-slate-400">已读</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态展示 (在固定高度视口正中央优雅居中) -->
        <div v-else class="flex-1 flex flex-col items-center justify-center py-10 text-center text-xs text-slate-400 select-none">
          <div class="text-3xl mb-2">{{ getEmptyIcon(activeTab) }}</div>
          <span>{{ getEmptyText(activeTab) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { formatDate } from '@/utils/formatters';
import { useConfirm } from '@/composables/useConfirm';

const props = defineProps({
  show: Boolean,
  notifications: {
    type: Array,
    default: () => [],
  },
  unreadCount: {
    type: Number,
    default: 0,
  },
  isAdmin: Boolean,
});

const emit = defineEmits([
  'close',
  'mark-read',
  'mark-all-read',
  'clear-read',
  'jump-notice',
  'open-feedback',
  'go-audit',
  'reply-feedback',
]);

const activeTab = ref('ALL'); // 'ALL' | 'ANNOUNCEMENT' | 'REPLY' | 'PRIVATE' | 'AUDIT' | 'FEEDBACK'

// 当前 Tab 下已读数量统计（用于一键清理，未读绝对不可被清理）
const currentTabReadCount = computed(() => {
  return filteredNotifications.value.filter(n => n.isRead).length;
});

const { showConfirm } = useConfirm();

async function handleClearRead() {
  if (currentTabReadCount.value === 0) return;
  const currentTabName = tabs.value.find(t => t.key === activeTab.value)?.label || '当前分类';
  const confirmed = await showConfirm({
    title: '一键清理已读',
    message: `确定清理「${currentTabName}」下的 ${currentTabReadCount.value} 条已读通知吗？\n（未读通知将继续妥善保留）`,
    confirmText: '确认清理',
    cancelText: '暂不清理',
    type: 'warning',
    icon: '🧹',
  });
  if (confirmed) {
    emit('clear-read', activeTab.value);
  }
}

// 点击卡片：标记已读并跳转对应业务页面
function handleNoticeClick(notice) {
  if (!notice) return;
  emit('mark-read', notice);
  // 审核类通知 (AUDIT) 全面取消跳转，仅在卡片内完成标读；管理员若需审核直接点击专属【👉 前往审核】按钮
  if (notice.type === 'AUDIT') return;
  emit('jump-notice', notice);
}

// 点击前往审核：主动标记已读并唤起管理后台审核面板
function handleGoAudit(notice) {
  if (!notice) return;
  emit('mark-read', notice);
  emit('go-audit', notice);
}

// 点击回复建议：主动标记已读并唤起快捷回复面板
function handleReplyFeedback(notice) {
  if (!notice) return;
  emit('mark-read', notice);
  emit('reply-feedback', notice);
}

// 仅当确实属于管理员待办人工审核任务时才展示前往审核按钮（已通过或已驳回的彻底排除）
function canShowAuditButton(notice) {
  if (!notice || notice.type !== 'AUDIT' || !props.isAdmin) return false;
  if (notice.targetUsername !== 'admin') return false;
  const title = notice.title || '';
  if (title.includes('已通过') || title.includes('已驳回') || title.includes('已处理') || title.includes('未能通过') || title.includes('审核记录')) {
    return false;
  }
  return title.includes('待审核') || title.includes('待人工审核');
}

// 分类标签页定义与未读数计算
const tabs = computed(() => {
  const list = props.notifications || [];
  const countUnread = (predicate) => list.filter(n => !n.isRead && predicate(n)).length;

  return [
    {
      key: 'ALL',
      label: '全部',
      icon: '📋',
      unread: props.unreadCount,
    },
    {
      key: 'ANNOUNCEMENT',
      label: '全站公告',
      icon: '📢',
      unread: countUnread(n => n.type === 'ANNOUNCEMENT'),
    },
    {
      key: 'REPLY',
      label: '互动回复',
      icon: '💬',
      unread: countUnread(n => n.type === 'REPLY'),
    },
    {
      key: 'PRIVATE',
      label: '私人私信',
      icon: '✉️',
      unread: countUnread(n => n.type === 'NOTICE' || n.type === 'SYSTEM'),
    },
    {
      key: 'AUDIT',
      label: '审核通知',
      icon: '⚖️',
      unread: countUnread(n => n.type === 'AUDIT'),
    },
    {
      key: 'FEEDBACK',
      label: '意见建议',
      icon: '💡',
      unread: countUnread(n => n.type === 'FEEDBACK'),
    },
  ];
});

// 当前标签页过滤列表
const filteredNotifications = computed(() => {
  const list = props.notifications || [];
  if (activeTab.value === 'ALL') return list;
  if (activeTab.value === 'ANNOUNCEMENT') return list.filter(n => n.type === 'ANNOUNCEMENT');
  if (activeTab.value === 'REPLY') return list.filter(n => n.type === 'REPLY');
  if (activeTab.value === 'PRIVATE') return list.filter(n => n.type === 'NOTICE' || n.type === 'SYSTEM');
  if (activeTab.value === 'AUDIT') return list.filter(n => n.type === 'AUDIT');
  if (activeTab.value === 'FEEDBACK') return list.filter(n => n.type === 'FEEDBACK');
  return list;
});

function getTypeBadgeClass(type) {
  switch (type) {
    case 'ANNOUNCEMENT':
      return 'bg-indigo-100 text-indigo-700 dark:bg-indigo-950 dark:text-indigo-300';
    case 'REPLY':
      return 'bg-emerald-100 text-emerald-700 dark:bg-emerald-950 dark:text-emerald-300';
    case 'AUDIT':
      return 'bg-amber-100 text-amber-700 dark:bg-amber-950 dark:text-amber-300';
    case 'FEEDBACK':
      return 'bg-purple-100 text-purple-700 dark:bg-purple-950 dark:text-purple-300';
    case 'NOTICE':
    case 'SYSTEM':
    default:
      return 'bg-sky-100 text-sky-700 dark:bg-sky-950 dark:text-sky-300';
  }
}

function getTypeBadgeIcon(type) {
  switch (type) {
    case 'ANNOUNCEMENT': return '📢';
    case 'REPLY': return '💬';
    case 'AUDIT': return '⚖️';
    case 'FEEDBACK': return '💡';
    case 'NOTICE':
    case 'SYSTEM':
    default: return '✉️';
  }
}

function getTypeBadgeLabel(type) {
  switch (type) {
    case 'ANNOUNCEMENT': return '全站公告';
    case 'REPLY': return '互动回复';
    case 'AUDIT': return '审核通知';
    case 'FEEDBACK': return '意见建议';
    case 'NOTICE':
    case 'SYSTEM':
    default: return '私人私信';
  }
}

function getEmptyIcon(tab) {
  switch (tab) {
    case 'ANNOUNCEMENT': return '📢';
    case 'REPLY': return '💬';
    case 'PRIVATE': return '✉️';
    case 'AUDIT': return '⚖️';
    case 'FEEDBACK': return '💡';
    default: return '📭';
  }
}

function getEmptyText(tab) {
  switch (tab) {
    case 'ANNOUNCEMENT': return '暂无全站公告';
    case 'REPLY': return '暂无互动回复，去文章下方与博主读者畅聊吧！';
    case 'PRIVATE': return '暂无私人私信';
    case 'AUDIT': return '暂无审核相关通知';
    case 'FEEDBACK': return '暂无意见建议，点击右上角提交第一条建议吧！';
    default: return '暂无新消息通知';
  }
}
</script>
