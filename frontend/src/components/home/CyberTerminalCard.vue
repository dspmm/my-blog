<template>
  <!-- 💻 Cyber Terminal 真实可交互极客微终端 (Interactive Mini CLI) -->
  <div class="rounded-2xl bg-[#0d1117] border border-[#30363d] shadow-xl overflow-hidden text-[11px] font-mono leading-relaxed flex flex-col transition-all hover:border-slate-600 group">
    
    <!-- 终端顶部标题栏 (macOS 三色窗体控制点) -->
    <div class="px-3 py-2 bg-[#161b22] border-b border-[#30363d] flex items-center justify-between select-none">
      <div class="flex items-center gap-1.5">
        <button type="button" @click="handleClear" title="清空终端 (clear)" class="w-2.5 h-2.5 rounded-full bg-rose-500 hover:opacity-80 transition-opacity"></button>
        <button type="button" @click="handleRunCommand('stats')" title="查看服务状态 (stats)" class="w-2.5 h-2.5 rounded-full bg-amber-500 hover:opacity-80 transition-opacity"></button>
        <button type="button" @click="handleRunCommand('help')" title="获取帮助 (help)" class="w-2.5 h-2.5 rounded-full bg-emerald-500 hover:opacity-80 transition-opacity"></button>
      </div>

      <div class="flex items-center gap-1.5 text-[10px] text-slate-400">
        <span class="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse"></span>
        <span class="font-semibold text-slate-300">geek@xiaodong-blog</span>
        <span class="text-slate-600">·</span>
        <span class="text-slate-500">v2.5</span>
      </div>
    </div>

    <!-- 终端内容显示主区域 (可滚动) -->
    <div ref="terminalBodyRef"
         @click="focusInput"
         class="p-3.5 max-h-[260px] min-h-[170px] overflow-y-auto custom-scrollbar space-y-2 text-slate-300 cursor-text">
      
      <!-- 历史消息队列 -->
      <div v-for="(item, idx) in historyOutputs" :key="idx" class="space-y-1">
        <!-- 用户键入的命令行 -->
        <div v-if="item.type === 'command'" class="flex items-center gap-1.5 text-slate-400">
          <span class="text-emerald-400 font-bold">geek@blog</span>
          <span class="text-slate-600">:</span>
          <span class="text-sky-400 font-bold">~</span>
          <span class="text-slate-500">$</span>
          <span class="text-white font-semibold">{{ item.text }}</span>
        </div>

        <!-- 终端响应内容：文本型 -->
        <div v-else-if="item.type === 'text'" class="text-slate-400 whitespace-pre-wrap pl-1">
          {{ item.text }}
        </div>

        <!-- 终端响应内容：帮助菜单 (help) -->
        <div v-else-if="item.type === 'help'" class="pl-1 space-y-1 text-slate-400">
          <div class="text-sky-400 font-bold mb-1">=== 极客终端指令手册 (Geek CLI Manual) ===</div>
          <div class="grid grid-cols-1 gap-1 text-[10px]">
            <div class="flex items-center"><span class="text-emerald-400 w-16 font-bold">help</span><span>显示指令手册帮助</span></div>
            <div class="flex items-center"><span class="text-emerald-400 w-16 font-bold">stats</span><span>实时监控全站文章、评论与延迟</span></div>
            <div class="flex items-center"><span class="text-emerald-400 w-16 font-bold">posts</span><span>探索精选极客博文并一键跳转</span></div>
            <div class="flex items-center"><span class="text-emerald-400 w-16 font-bold">about</span><span>查阅博主小东全栈档案与技能</span></div>
            <div class="flex items-center"><span class="text-emerald-400 w-16 font-bold">theme</span><span>极速切换暗黑 / 浅色模式</span></div>
            <div class="flex items-center"><span class="text-emerald-400 w-16 font-bold">matrix</span><span>[彩蛋] 触发黑客帝国矩阵字符雨</span></div>
            <div class="flex items-center"><span class="text-emerald-400 w-16 font-bold">sudo</span><span>[彩蛋] 尝试以超级管理员身份提权</span></div>
            <div class="flex items-center"><span class="text-emerald-400 w-16 font-bold">clear</span><span>清空当前终端屏幕历史</span></div>
          </div>
        </div>

        <!-- 终端响应内容：服务监控 (stats) -->
        <div v-else-if="item.type === 'stats'" class="pl-1 space-y-1.5 py-0.5">
          <div class="text-emerald-400 font-bold flex items-center gap-1.5">
            <span class="animate-pulse">●</span>
            <span>SYSTEM HEALTH REPORT · ALL RUNNING</span>
          </div>
          <div class="grid grid-cols-2 gap-2 text-[10px] bg-[#161b22] p-2 rounded-xl border border-[#30363d]">
            <div>
              <span class="text-slate-500">博文收录: </span>
              <span class="font-bold text-sky-400">{{ posts.length }} 篇</span>
            </div>
            <div>
              <span class="text-slate-500">技术专栏: </span>
              <span class="font-bold text-purple-400">{{ categoriesCount }} 个</span>
            </div>
            <div>
              <span class="text-slate-500">网络延迟: </span>
              <span class="font-bold text-emerald-400 font-mono">{{ systemPing }} ms</span>
            </div>
            <div>
              <span class="text-slate-500">核心架构: </span>
              <span class="font-bold text-amber-400">Spring Boot 3</span>
            </div>
          </div>
        </div>

        <!-- 终端响应内容：博客列表 (posts) -->
        <div v-else-if="item.type === 'posts'" class="pl-1 space-y-1.5">
          <div class="text-sky-400 font-bold">=== 精选技术博文推荐 (点击直达) ===</div>
          <div v-if="recommendedPosts.length > 0" class="space-y-1">
            <div v-for="(p, pIdx) in recommendedPosts" :key="p.id"
                 @click="emit('view-post', p)"
                 class="p-1.5 rounded-lg bg-[#161b22] hover:bg-sky-950/50 border border-transparent hover:border-sky-800 transition-colors cursor-pointer flex items-center justify-between text-[10px] group/item">
              <span class="text-slate-300 group-hover/item:text-sky-300 truncate max-w-[190px]">
                <span class="text-sky-500 font-mono">[{{ pIdx + 1 }}]</span> {{ p.title }}
              </span>
              <span class="text-slate-500 text-[9px] font-mono shrink-0">点击阅读 ›</span>
            </div>
          </div>
          <div v-else class="text-slate-500 text-[10px]">暂无可推荐文章</div>
        </div>

        <!-- 终端响应内容：博主资料 (about) -->
        <div v-else-if="item.type === 'about'" class="pl-1 space-y-1 text-slate-400 text-[10px] bg-[#161b22] p-2.5 rounded-xl border border-[#30363d]">
          <div class="text-amber-400 font-bold text-[11px] flex items-center gap-1.5">
            <span>👨‍💻</span> <span>小东 (XiaoDong) · 架构研发工程师</span>
          </div>
          <div><span class="text-slate-500">专注领域:</span> 高并发架构、微服务工程治理、云端自动化交付</div>
          <div><span class="text-slate-500">核心技术:</span> Java 21 / Spring Boot 3 / Vue 3 / Docker / GCP / MySQL</div>
          <div class="pt-1 text-slate-400 italic">“用极客代码雕琢工程之美，让每一行逻辑都有回响。”</div>
        </div>

        <!-- 终端响应内容：矩阵代码雨彩蛋 (matrix) -->
        <div v-else-if="item.type === 'matrix'" class="text-emerald-400 font-mono text-[9px] leading-tight overflow-hidden break-all pl-1 py-1">
          01001000 01100001 01100011 01101011 00100000 01010100 01101000 01100101 00100000 01010000 01101100 01100001 01101110 01100101 01110100 00100001<br/>
          >>> GEEK SYSTEM MATRIX ENGAGED <<<<br/>
          Wake up, Neo... The Matrix has you. Follow the white rabbit. 🐇
        </div>

        <!-- 终端响应内容：错误提示 -->
        <div v-else-if="item.type === 'error'" class="text-rose-400 pl-1">
          {{ item.text }}
        </div>
      </div>

      <!-- 当前命令行输入行 -->
      <div class="flex items-center gap-1.5 pt-1">
        <span class="text-emerald-400 font-bold shrink-0">geek@blog</span>
        <span class="text-slate-600 shrink-0">:</span>
        <span class="text-sky-400 font-bold shrink-0">~</span>
        <span class="text-slate-500 shrink-0">$</span>
        <input ref="inputRef"
               v-model="commandInput"
               @keydown.enter.prevent="handleEnter"
               @keydown.up.prevent="navigateHistory(-1)"
               @keydown.down.prevent="navigateHistory(1)"
               type="text"
               spellcheck="false"
               autocomplete="off"
               placeholder="输入命令 (如 help)..."
               class="flex-1 bg-transparent border-none outline-none text-white font-mono text-[11px] p-0 focus:ring-0 placeholder:text-slate-600" />
      </div>
    </div>

    <!-- 快捷指令一键胶囊按钮栏 (支持鼠标滚轮横滑、左右箭头导航与触屏滑行) -->
    <div class="px-2 py-1.5 bg-[#161b22] border-t border-[#30363d] flex items-center gap-1 select-none">
      <span class="text-[9px] text-slate-500 font-mono pl-0.5 shrink-0">快捷:</span>
      
      <!-- 左滑动微型箭头 -->
      <button type="button"
              @click="scrollQuickBar(-90)"
              title="向左滑动指令栏"
              class="w-4 h-5 rounded flex items-center justify-center text-slate-400 hover:text-white hover:bg-[#30363d] text-xs shrink-0 transition-colors">
        ‹
      </button>

      <!-- 快捷指令横滑容器 (绑定滚轮横移) -->
      <div ref="quickBarRef"
           @wheel.passive="handleWheelScroll"
           class="flex-1 flex items-center gap-1.5 overflow-x-auto custom-scrollbar scroll-smooth py-0.5">
        <button v-for="cmd in quickCommands" :key="cmd"
                type="button"
                @click="handleRunCommand(cmd)"
                class="px-2 py-0.5 rounded-md text-[10px] font-mono bg-[#21262d] hover:bg-[#30363d] text-sky-400 hover:text-white border border-[#30363d] transition-all shrink-0 active:scale-95 whitespace-nowrap">
          {{ cmd }}
        </button>
      </div>

      <!-- 右滑动微型箭头 -->
      <button type="button"
              @click="scrollQuickBar(90)"
              title="向右滑动指令栏"
              class="w-4 h-5 rounded flex items-center justify-center text-slate-400 hover:text-white hover:bg-[#30363d] text-xs shrink-0 transition-colors">
        ›
      </button>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue';
import { useTheme } from '@/composables/useTheme';

const props = defineProps({
  stats: {
    type: Object,
    default: () => ({}),
  },
  posts: {
    type: Array,
    default: () => [],
  },
  systemPing: {
    type: Number,
    default: 38,
  },
  categoriesCount: {
    type: Number,
    default: 7,
  },
});

const emit = defineEmits(['view-post']);

const { isDark, toggleDarkMode } = useTheme();

const commandInput = ref('');
const inputRef = ref(null);
const terminalBodyRef = ref(null);
const quickBarRef = ref(null);

function handleWheelScroll(e) {
  if (quickBarRef.value) {
    quickBarRef.value.scrollLeft += (e.deltaY !== 0 ? e.deltaY : e.deltaX);
  }
}

function scrollQuickBar(offset) {
  if (quickBarRef.value) {
    quickBarRef.value.scrollBy({ left: offset, behavior: 'smooth' });
  }
}

const historyOutputs = ref([
  {
    type: 'text',
    text: 'Welcome to XiaoDong Geek Terminal v2.5.0-LTS\nType "help" or click chips below to explore commands.'
  }
]);

const quickCommands = ['help', 'stats', 'posts', 'about', 'theme', 'clear'];

// 历史命令记录（用于上下键翻阅）
const cmdHistory = ref([]);
const historyIndex = ref(-1);

const recommendedPosts = computed(() => {
  return (props.posts || []).slice(0, 3);
});

function scrollToBottom() {
  nextTick(() => {
    if (terminalBodyRef.value) {
      terminalBodyRef.value.scrollTop = terminalBodyRef.value.scrollHeight;
    }
  });
}

function focusInput() {
  inputRef.value?.focus();
}

function handleClear() {
  historyOutputs.value = [
    {
      type: 'text',
      text: 'XiaoDong Geek Terminal v2.5.0-LTS (Screen cleared).'
    }
  ];
  scrollToBottom();
}

function handleRunCommand(cmd) {
  commandInput.value = cmd;
  handleEnter();
}

function navigateHistory(direction) {
  if (cmdHistory.value.length === 0) return;
  if (direction === -1) {
    if (historyIndex.value === -1) {
      historyIndex.value = cmdHistory.value.length - 1;
    } else if (historyIndex.value > 0) {
      historyIndex.value--;
    }
  } else if (direction === 1) {
    if (historyIndex.value !== -1) {
      if (historyIndex.value < cmdHistory.value.length - 1) {
        historyIndex.value++;
      } else {
        historyIndex.value = -1;
        commandInput.value = '';
        return;
      }
    }
  }
  if (historyIndex.value >= 0 && historyIndex.value < cmdHistory.value.length) {
    commandInput.value = cmdHistory.value[historyIndex.value];
  }
}

function handleEnter() {
  const raw = commandInput.value.trim();
  commandInput.value = '';
  historyIndex.value = -1;

  if (!raw) {
    historyOutputs.value.push({ type: 'command', text: '' });
    scrollToBottom();
    return;
  }

  cmdHistory.value.push(raw);
  historyOutputs.value.push({ type: 'command', text: raw });

  const cmd = raw.toLowerCase();

  if (cmd === 'help') {
    historyOutputs.value.push({ type: 'help' });
  } else if (cmd === 'stats') {
    historyOutputs.value.push({ type: 'stats' });
  } else if (cmd === 'posts') {
    historyOutputs.value.push({ type: 'posts' });
  } else if (cmd === 'about') {
    historyOutputs.value.push({ type: 'about' });
  } else if (cmd === 'theme') {
    toggleDarkMode();
    historyOutputs.value.push({
      type: 'text',
      text: `✓ 主题模式已切换为：[${isDark.value ? '极客暗黑 (Dark)' : '纯净明亮 (Light)'}]`
    });
  } else if (cmd === 'clear') {
    handleClear();
    return;
  } else if (cmd === 'matrix') {
    historyOutputs.value.push({ type: 'matrix' });
  } else if (cmd === 'sudo' || cmd.startsWith('sudo ')) {
    historyOutputs.value.push({
      type: 'error',
      text: '[sudo] password for geek: Permission denied. 小东全栈安全系统已拦截，请先使用超级管理员账号登录后台 :)'
    });
  } else if (cmd === 'uname' || cmd === 'uname -a' || cmd === 'uname -srm') {
    historyOutputs.value.push({
      type: 'text',
      text: 'Linux 6.8.0-generic-geek x86_64 Cloud-Native Kernel (Containerized)'
    });
  } else {
    historyOutputs.value.push({
      type: 'error',
      text: `bash: command not found: "${raw}". 输入 "help" 查阅可用指令。`
    });
  }

  scrollToBottom();
  focusInput();
}

onMounted(() => {
  scrollToBottom();
});
</script>
