<template>
  <!-- ======================= 写作发布工作室模态框 (双栏实时预览) ======================= -->
  <div v-if="show"
       class="fixed inset-0 z-50 overflow-y-auto bg-slate-900/70 backdrop-blur-sm flex items-center justify-center p-3 sm:p-6"
       @click.self="emit('close')">
    <div class="w-full max-w-4xl bg-white dark:bg-slate-900 rounded-3xl shadow-2xl border border-slate-200 dark:border-slate-800 overflow-hidden flex flex-col max-h-[92vh] animate-in fade-in zoom-in-95 duration-200">
      
      <!-- 模态框顶部控制条 -->
      <div class="px-6 py-4 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between shrink-0">
        <div class="flex items-center gap-3">
          <span class="text-xl">✍️</span>
          <div>
            <h3 class="font-bold text-base text-slate-900 dark:text-white">{{ editingPostId ? '编辑技术博客' : 'Markdown 创作工作室' }}</h3>
            <p class="text-xs text-slate-400">支持 Markdown 实时排版与语法高亮</p>
          </div>
        </div>

        <!-- 模式切换 -->
        <div class="flex items-center gap-2">
          <div class="hidden sm:flex bg-slate-100 dark:bg-slate-800 p-1 rounded-xl text-xs font-semibold">
            <button @click="editorMode = 'edit'"
                    :class="editorMode === 'edit' ? 'bg-white dark:bg-slate-700 shadow-sm text-sky-600' : 'text-slate-500'"
                    class="px-3 py-1 rounded-lg transition-all">编辑</button>
            <button @click="editorMode = 'split'"
                    :class="editorMode === 'split' ? 'bg-white dark:bg-slate-700 shadow-sm text-sky-600' : 'text-slate-500'"
                    class="px-3 py-1 rounded-lg transition-all">分屏实时预览</button>
            <button @click="editorMode = 'preview'"
                    :class="editorMode === 'preview' ? 'bg-white dark:bg-slate-700 shadow-sm text-sky-600' : 'text-slate-500'"
                    class="px-3 py-1 rounded-lg transition-all">仅预览</button>
          </div>
          <button @click="emit('close')" class="p-1.5 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-lg">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
          </button>
        </div>
      </div>

      <!-- 模态框主体内容区 -->
      <div class="p-6 overflow-y-auto flex-1 space-y-4 text-sm">
        
        <!-- 标题输入 -->
        <div>
          <label class="block font-semibold mb-1 text-slate-700 dark:text-slate-300 text-xs">文章标题 *</label>
          <input v-model="form.title" type="text" placeholder="输入吸睛的技术长文标题..."
                 class="w-full px-4 py-2.5 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:ring-2 focus:ring-sky-500 focus:outline-none text-base font-bold">
        </div>

        <!-- 分类与标签 -->
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <!-- 自定义技术专栏分类选择器 -->
          <div class="relative">
            <label class="block font-semibold mb-1 text-slate-700 dark:text-slate-300 text-xs">技术专栏分类</label>
            
            <button type="button"
                    @click="showCategorySelect = !showCategorySelect"
                    class="w-full px-3.5 py-2.5 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 hover:border-sky-500 dark:hover:border-sky-500 focus:outline-none flex items-center justify-between text-xs transition-colors shadow-sm">
              <div class="flex items-center gap-2 font-medium text-slate-800 dark:text-slate-100">
                <span class="text-base">{{ getCategoryIcon(form.categoryName) }}</span>
                <span>{{ form.categoryName }}</span>
              </div>
              <svg class="w-4 h-4 text-slate-400 transition-transform duration-200" :class="showCategorySelect ? 'rotate-180 text-sky-500' : ''" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
              </svg>
            </button>

            <!-- PC 桌面端浮层下拉菜单 -->
            <div v-show="showCategorySelect"
                 class="hidden sm:block absolute left-0 right-0 mt-1.5 z-40 bg-white/95 dark:bg-slate-900/95 backdrop-blur-xl border border-slate-200 dark:border-slate-700/80 rounded-2xl shadow-2xl p-1.5 animate-in fade-in zoom-in-95 duration-150">
              <div class="max-h-56 overflow-y-auto space-y-1">
                <button v-for="cat in availableCategories" :key="cat"
                        type="button"
                        @click="form.categoryName = cat; showCategorySelect = false"
                        :class="form.categoryName === cat ? 'bg-sky-50 text-sky-700 dark:bg-sky-950/60 dark:text-sky-300 font-bold' : 'text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800/60'"
                        class="w-full px-3 py-2 rounded-xl text-left text-xs flex items-center justify-between transition-colors">
                  <div class="flex items-center gap-2">
                    <span class="text-base">{{ getCategoryIcon(cat) }}</span>
                    <span>{{ cat }}</span>
                  </div>
                  <svg v-if="form.categoryName === cat" class="w-4 h-4 text-sky-600 dark:text-sky-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M5 13l4 4L19 7"></path>
                  </svg>
                </button>
              </div>
            </div>

            <!-- 移动端专属：App 级底部分类动作抽屉 -->
            <teleport to="body">
              <div v-if="showCategorySelect" class="sm:hidden fixed inset-0 z-[70]">
                <div class="fixed inset-0 bg-slate-900/70 backdrop-blur-sm transition-opacity"
                     @click="showCategorySelect = false"></div>

                <div class="fixed inset-x-0 bottom-0 z-10 bg-white dark:bg-slate-900 rounded-t-3xl border-t border-slate-200 dark:border-slate-800 shadow-2xl safe-bottom p-5 animate-in slide-in-from-bottom duration-250">
                  <div class="w-10 h-1.5 bg-slate-200 dark:bg-slate-700 rounded-full mx-auto mb-4"></div>

                  <div class="flex items-center justify-between pb-3 border-b border-slate-100 dark:border-slate-800 mb-3">
                    <div class="flex items-center gap-2">
                      <span class="text-base">🏷️</span>
                      <h4 class="font-bold text-sm text-slate-900 dark:text-white">选择技术专栏分类</h4>
                    </div>
                    <span class="text-xs text-slate-400 font-mono">共 {{ availableCategories.length }} 个专栏</span>
                  </div>

                  <div class="space-y-2 max-h-[50vh] overflow-y-auto">
                    <button v-for="cat in availableCategories" :key="cat"
                            type="button"
                            @click="form.categoryName = cat; showCategorySelect = false"
                            :class="form.categoryName === cat ? 'bg-sky-50 dark:bg-sky-950/60 border-sky-500 text-sky-700 dark:text-sky-300 font-bold shadow-sm' : 'bg-slate-50/60 dark:bg-slate-800/40 border-slate-200 dark:border-slate-800 text-slate-700 dark:text-slate-300'"
                            class="w-full p-3 rounded-2xl border flex items-center justify-between text-left transition-all active:scale-[0.98]">
                      <div class="flex items-center gap-3">
                        <span class="text-xl p-2 rounded-xl bg-white dark:bg-slate-800 shadow-xs border border-slate-100 dark:border-slate-700">
                          {{ getCategoryIcon(cat) }}
                        </span>
                        <div>
                          <div class="text-sm font-bold">{{ cat }}</div>
                          <div class="text-[11px] text-slate-400 mt-0.5">{{ getCategoryDesc(cat) }}</div>
                        </div>
                      </div>
                      <div class="w-6 h-6 rounded-full flex items-center justify-center shrink-0"
                           :class="form.categoryName === cat ? 'bg-sky-500 text-white' : 'border-2 border-slate-300 dark:border-slate-600'">
                        <svg v-if="form.categoryName === cat" class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7"></path>
                        </svg>
                      </div>
                    </button>
                  </div>

                  <button type="button"
                          @click="showCategorySelect = false"
                          class="w-full mt-4 py-3 rounded-2xl text-xs font-bold text-slate-600 dark:text-slate-300 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 transition-colors">
                    取消
                  </button>
                </div>
              </div>
            </teleport>
          </div>
          <div>
            <label class="block font-semibold mb-1 text-slate-700 dark:text-slate-300 text-xs">技术标签 (逗号分隔)</label>
            <input v-model="form.tags" type="text" placeholder="例如: SpringBoot3,MySQL,Docker"
                   class="w-full px-3.5 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:ring-2 focus:ring-sky-500 focus:outline-none text-xs">
          </div>
        </div>

        <!-- 一句话要点摘要 -->
        <div>
          <label class="block font-semibold mb-1 text-slate-700 dark:text-slate-300 text-xs">一句话摘要提要</label>
          <input v-model="form.summary" type="text" placeholder="简述本文的核心技术价值与解决的问题..."
                 class="w-full px-3.5 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:ring-2 focus:ring-sky-500 focus:outline-none text-xs">
        </div>

        <!-- 快捷 Markdown 工具栏与模板插入 -->
        <div class="flex flex-wrap items-center justify-between gap-2 pt-2 border-t border-slate-100 dark:border-slate-800">
          <div class="flex items-center gap-1 text-xs">
            <button @click="insertText('**粗体**')" class="px-2 py-1 rounded bg-slate-100 dark:bg-slate-800 hover:bg-slate-200">B</button>
            <button @click="insertText('*斜体*')" class="px-2 py-1 rounded bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 italic">I</button>
            <button @click="insertText('## 二级标题\n')" class="px-2 py-1 rounded bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 font-bold">H2</button>
            <button @click="insertText('### 三级标题\n')" class="px-2 py-1 rounded bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 font-bold">H3</button>
            <button @click="insertText('`代码片段`')" class="px-2 py-1 rounded bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 font-mono">Code</button>
            <button @click="insertText('\n```bash\n# 命令行指令\n```\n')" class="px-2 py-1 rounded bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 font-mono">CodeBlock</button>
            <button @click="insertText('> 引用要点说明\n')" class="px-2 py-1 rounded bg-slate-100 dark:bg-slate-800 hover:bg-slate-200">Quote</button>
            <button type="button" @click="triggerFileInput" :disabled="uploadingImage"
                    class="px-2 py-1 rounded bg-sky-50 dark:bg-sky-950/60 hover:bg-sky-100 dark:hover:bg-sky-900 text-sky-600 dark:text-sky-400 font-semibold flex items-center gap-1 transition-colors"
                    title="点击选取图片或在编辑区截图 Ctrl+V 粘贴/直接拖拽图片">
              <span v-if="uploadingImage" class="w-3 h-3 border-2 border-sky-600 dark:border-sky-400 border-t-transparent rounded-full animate-spin"></span>
              <span v-else>🖼️</span>
              <span>{{ uploadingImage ? '上传中...' : '图片' }}</span>
            </button>
            <input ref="fileInputRef" type="file" accept="image/*" class="hidden" @change="handleFileChange">
          </div>
          <div class="flex items-center gap-1.5">
            <button @click="insertTemplate('arch')" class="text-xs text-sky-600 dark:text-sky-400 font-semibold px-2 py-1 rounded hover:bg-sky-50 dark:hover:bg-sky-950/50">
              📑 插入实战架构模板
            </button>
          </div>
        </div>

        <!-- Markdown 编辑器输入框与预览区 -->
        <div :class="editorMode === 'split' ? 'grid grid-cols-1 sm:grid-cols-2 gap-4' : ''" class="min-h-[280px]">
          <div v-show="editorMode !== 'preview'"
               class="flex flex-col relative"
               @dragover.prevent="isDragging = true"
               @dragleave.prevent="isDragging = false"
               @drop.prevent="handleDropImage">
            <textarea id="editor-textarea" v-model="form.content" rows="12"
                      @paste="handlePasteImage"
                      placeholder="写下你的技术思考或教程步骤... (支持点击上方图片按钮、直接拖拽图片松开、或微信/截图直接 Ctrl+V 粘贴上传)"
                      class="w-full flex-1 p-3.5 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:ring-2 focus:ring-sky-500 focus:outline-none font-mono text-xs sm:text-sm leading-relaxed resize-none"></textarea>

            <!-- 拖拽图片悬浮提示 -->
            <div v-if="isDragging" class="absolute inset-0 z-20 bg-sky-500/10 dark:bg-sky-500/20 backdrop-blur-[2px] border-2 border-dashed border-sky-500 rounded-xl flex flex-col items-center justify-center pointer-events-none transition-all">
              <span class="text-3xl mb-1 animate-bounce">📥</span>
              <span class="text-xs font-bold text-sky-600 dark:text-sky-400">松开鼠标即可自动上传至 MinIO 对象存储</span>
            </div>

            <!-- 异步上传中悬浮角标 -->
            <div v-if="uploadingImage" class="absolute top-2 right-2 z-20 bg-sky-600 text-white text-[11px] font-medium px-2.5 py-1 rounded-lg shadow-lg flex items-center gap-1.5 animate-pulse">
              <span class="w-3 h-3 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
              <span>MinIO 上传中...</span>
            </div>
          </div>
          <div v-show="editorMode !== 'edit'" class="p-4 rounded-xl bg-slate-50/50 dark:bg-slate-800/40 border border-slate-200 dark:border-slate-700 overflow-y-auto max-h-[340px]">
            <div class="text-xs font-mono text-slate-400 mb-2 border-b border-slate-200 dark:border-slate-700 pb-1">实时预览效果</div>
            <div class="markdown-body text-xs sm:text-sm" v-html="previewHtml" @click="handlePreviewClick"></div>
          </div>
        </div>

      </div>

      <!-- 模态框底部按钮 -->
      <div class="px-6 py-4 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between shrink-0 bg-slate-50 dark:bg-slate-900/90">
        <div class="text-xs text-slate-400">
          预计字数: <span class="font-mono font-bold text-slate-600 dark:text-slate-300">{{ (form.content || '').length }}</span> 字
        </div>
        <div class="flex items-center gap-3">
          <button @click="emit('close')" class="px-4 py-2 rounded-xl border border-slate-300 dark:border-slate-700 hover:bg-slate-100 dark:hover:bg-slate-800 font-semibold text-xs">
            取消
          </button>
          <button @click="emit('save', form)" :disabled="submitting"
                  class="px-6 py-2 rounded-xl bg-gradient-to-r from-sky-600 to-indigo-600 hover:from-sky-700 hover:to-indigo-700 disabled:opacity-50 text-white font-bold text-xs shadow-md shadow-sky-600/25 flex items-center gap-2">
            <span v-if="submitting" class="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
            <span>{{ submitting ? '正在保存...' : (editingPostId ? '保存修改' : '发布文章') }}</span>
          </button>
        </div>
      </div>

    </div>

    <!-- 编辑器分屏预览极客大图灯箱 -->
    <ImageLightboxModal
      :show="showPreviewLightbox"
      :src="previewLightboxSrc"
      :alt-text="previewLightboxAlt"
      @close="showPreviewLightbox = false"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue';
import { getCategoryIcon } from '@/utils/formatters';
import { renderMarkdownWithHeadings } from '@/utils/markdown';
import { uploadImage } from '@/api/file';
import ImageLightboxModal from '@/components/common/ImageLightboxModal.vue';
import { useToast } from '@/composables/useToast';

const props = defineProps({
  show: Boolean,
  editingPostId: [Number, String],
  initialPost: Object,
  availableCategories: {
    type: Array,
    default: () => [],
  },
  getCategoryDesc: {
    type: Function,
    default: () => '',
  },
  submitting: Boolean,
});

const emit = defineEmits(['close', 'save']);
const { showToast } = useToast();

const form = ref({
  title: '',
  categoryName: '全栈开发 & CI/CD',
  author: '小东',
  tags: 'SpringBoot,MySQL',
  summary: '',
  content: '',
  status: 'PUBLISHED',
  ...(props.initialPost || {}),
});

// 监听模态框打开与传入的待编辑数据，确保每次打开均能精准同步表单字段
watch(() => props.show, (newVal) => {
  if (newVal) {
    if (props.initialPost) {
      form.value = {
        title: props.initialPost.title || '',
        categoryName: props.initialPost.categoryName || '全栈开发 & CI/CD',
        author: props.initialPost.author || '小东',
        tags: props.initialPost.tags || 'SpringBoot,MySQL',
        summary: props.initialPost.summary || '',
        content: props.initialPost.content || '',
        status: props.initialPost.status || 'PUBLISHED',
      };
    } else {
      form.value = {
        title: '',
        categoryName: '全栈开发 & CI/CD',
        author: '小东',
        tags: 'SpringBoot,MySQL',
        summary: '',
        content: '',
        status: 'PUBLISHED',
      };
    }
  }
}, { immediate: true });

const editorMode = ref('split'); // 'split' | 'edit' | 'preview'
const showCategorySelect = ref(false);
const fileInputRef = ref(null);
const isDragging = ref(false);
const uploadingImage = ref(false);

const showPreviewLightbox = ref(false);
const previewLightboxSrc = ref('');
const previewLightboxAlt = ref('');

function handlePreviewClick(e) {
  const target = e.target;
  if (target && target.tagName === 'IMG') {
    e.preventDefault();
    previewLightboxSrc.value = target.src || target.getAttribute('src');
    previewLightboxAlt.value = target.alt || target.getAttribute('alt') || '';
    showPreviewLightbox.value = true;
  }
}

function triggerFileInput() {
  if (fileInputRef.value) {
    fileInputRef.value.value = '';
    fileInputRef.value.click();
  }
}

async function handleFileChange(e) {
  const file = e.target.files?.[0];
  if (file) {
    await uploadAndInsert(file);
  }
}

async function handleDropImage(e) {
  isDragging.value = false;
  const files = e.dataTransfer?.files;
  if (files && files.length > 0) {
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      if (file.type.startsWith('image/')) {
        await uploadAndInsert(file);
      }
    }
  }
}

async function handlePasteImage(e) {
  const items = e.clipboardData?.items;
  if (!items) return;
  for (let i = 0; i < items.length; i++) {
    const item = items[i];
    if (item.type.indexOf('image') !== -1) {
      e.preventDefault();
      const file = item.getAsFile();
      if (file) {
        await uploadAndInsert(file);
      }
      break;
    }
  }
}

async function uploadAndInsert(file) {
  if (!file.type.startsWith('image/')) {
    showToast('仅支持上传图片文件 (JPG/PNG/GIF/WEBP/SVG)', 'error');
    return;
  }
  if (file.size > 10 * 1024 * 1024) {
    showToast('上传图片大小不能超过 10MB', 'error');
    return;
  }

  const textarea = document.getElementById('editor-textarea');
  const start = textarea ? textarea.selectionStart : (form.value.content || '').length;
  const end = textarea ? textarea.selectionEnd : start;
  const current = form.value.content || '';

  const cleanName = (file.name || 'image').replace(/[\[\]]/g, '');
  const placeholder = `![上传中: ${cleanName}...]()\n`;

  form.value.content = current.substring(0, start) + placeholder + current.substring(end);
  uploadingImage.value = true;

  try {
    const res = await uploadImage(file);
    const markdownImg = `![${cleanName}](${res.url})\n`;
    form.value.content = form.value.content.replace(placeholder, markdownImg);
  } catch (err) {
    form.value.content = form.value.content.replace(placeholder, '');
    showToast('图片上传失败: ' + (err.message || '网络异常'), 'error');
  } finally {
    uploadingImage.value = false;
  }
}

const previewHtml = computed(() => {
  return renderMarkdownWithHeadings(form.value.content || '*开始在左侧输入 Markdown 内容...*');
});

function insertText(text) {
  const textarea = document.getElementById('editor-textarea');
  if (!textarea) return;
  const start = textarea.selectionStart;
  const end = textarea.selectionEnd;
  const current = form.value.content || '';
  form.value.content = current.substring(0, start) + text + current.substring(end);
  nextTick(() => {
    textarea.focus();
    textarea.setSelectionRange(start + text.length, start + text.length);
  });
}

function insertTemplate(type) {
  if (type === 'arch') {
    form.value.title = '架构实战：基于 Docker Compose 编排的高可用微服务治理实践';
    form.value.summary = '深度复盘在 Google Cloud 生产环境如何通过命名卷隔离 MySQL、Spring Boot 3 自动重构及 Nginx 反向代理配置。';
    form.value.content = `# 架构实战：基于 Docker Compose 的微服务治理实践\n\n## 1. 架构目标与背景\n在现代全栈开发中，单体架构往往面临环境漂移、手动部署繁杂等问题。\n\n## 2. 核心架构设计\n- **前端接入层**：Nginx Alpine 极简镜像，反向代理 \`/api/\`；\n- **业务核心层**：Spring Boot 3.2.3 + Spring Data JPA；\n- **数据持久层**：MySQL 8.0 挂载卷持久化保护。\n\n\`\`\`bash\n# 快速检查容器服务运行状态\ndocker compose ps -a\n\`\`\`\n\n> 💡 **核心建议**：将数据库与代码构建分离，确保自动化流水线只构建业务镜像，保障数据绝对安全！`;
  }
}
</script>
