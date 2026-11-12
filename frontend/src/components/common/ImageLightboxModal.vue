<template>
  <teleport to="body">
    <transition name="lightbox-fade">
      <div v-if="show"
           class="fixed inset-0 z-[100] flex flex-col bg-slate-950/92 backdrop-blur-md select-none touch-none overflow-hidden outline-none"
           @click.self="handleClose"
           @wheel.prevent="handleWheel"
           tabindex="0"
           ref="containerRef">

        <!-- 顶部极客工具栏 -->
        <div class="h-14 px-4 sm:px-6 flex items-center justify-between border-b border-white/10 bg-slate-900/60 backdrop-blur-xl shrink-0 z-10 text-white">
          <div class="flex items-center gap-3 overflow-hidden pr-4">
            <span class="text-base shrink-0">🖼️</span>
            <span class="text-xs sm:text-sm font-semibold truncate text-slate-200" :title="altText">
              {{ altText || '大图高清预览' }}
            </span>
          </div>

          <div class="flex items-center gap-1 sm:gap-2 shrink-0">
            <!-- 缩放比例指示器 -->
            <span class="hidden sm:inline-block px-2.5 py-1 rounded-lg bg-white/10 font-mono text-xs text-slate-300">
              {{ Math.round(scale * 100) }}%
            </span>

            <!-- 缩小 -->
            <button @click="zoomOut" class="p-2 rounded-xl hover:bg-white/10 active:scale-95 transition-all text-slate-300 hover:text-white" title="缩小 (滚轮下滑)">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4"></path></svg>
            </button>

            <!-- 放大 -->
            <button @click="zoomIn" class="p-2 rounded-xl hover:bg-white/10 active:scale-95 transition-all text-slate-300 hover:text-white" title="放大 (滚轮上滑)">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path></svg>
            </button>

            <!-- 1:1 还原 -->
            <button @click="resetTransform" class="p-2 rounded-xl hover:bg-white/10 active:scale-95 transition-all text-slate-300 hover:text-white text-xs font-mono font-bold" title="还原 1:1">
              1:1
            </button>

            <!-- 顺时针旋转 90度 -->
            <button @click="rotateClockwise" class="p-2 rounded-xl hover:bg-white/10 active:scale-95 transition-all text-slate-300 hover:text-white" title="旋转 90°">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path></svg>
            </button>

            <!-- 浏览器新标签页打开原始图片直链 -->
            <a :href="src" target="_blank" rel="noopener noreferrer" class="p-2 rounded-xl hover:bg-white/10 active:scale-95 transition-all text-slate-300 hover:text-white" title="在新标签页打开原图">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14"></path></svg>
            </a>

            <!-- 关闭 -->
            <button @click="handleClose" class="p-2 ml-1 rounded-xl bg-white/10 hover:bg-rose-500/80 active:scale-95 transition-all text-white" title="关闭 (ESC)">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
            </button>
          </div>
        </div>

        <!-- 图像展示舞台画布 -->
        <div class="flex-1 w-full h-full flex items-center justify-center relative overflow-hidden cursor-grab active:cursor-grabbing"
             @mousedown="startDrag"
             @mousemove="onDrag"
             @mouseup="endDrag"
             @mouseleave="endDrag"
             @touchstart="onTouchStart"
             @touchmove="onTouchMove"
             @touchend="onTouchEnd"
             @dblclick="handleDoubleClick">
          <img :src="src"
               :alt="altText"
               :style="imageStyle"
               class="max-w-[90vw] max-h-[85vh] object-contain transition-transform duration-75 select-none pointer-events-none drop-shadow-2xl rounded-lg" />
        </div>

        <!-- 底部快捷提示栏 -->
        <div class="h-9 px-4 flex items-center justify-center text-[11px] text-slate-400 font-mono bg-slate-900/40 border-t border-white/5 shrink-0">
          <span>💡 支持鼠标滚轮缩放 · 按住拖拽平移 · 双击快速居中复原 · ESC 关闭</span>
        </div>

      </div>
    </transition>
  </teleport>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue';

const props = defineProps({
  show: Boolean,
  src: String,
  altText: {
    type: String,
    default: '',
  },
});

const emit = defineEmits(['close']);

const containerRef = ref(null);
const scale = ref(1);
const rotate = ref(0);
const translateX = ref(0);
const translateY = ref(0);

const isDragging = ref(false);
const startPos = ref({ x: 0, y: 0 });

const imageStyle = computed(() => ({
  transform: `translate(${translateX.value}px, ${translateY.value}px) scale(${scale.value}) rotate(${rotate.value}deg)`,
}));

function resetTransform() {
  scale.value = 1;
  rotate.value = 0;
  translateX.value = 0;
  translateY.value = 0;
}

function zoomIn() {
  scale.value = Math.min(scale.value * 1.25, 5);
}

function zoomOut() {
  scale.value = Math.max(scale.value / 1.25, 0.2);
}

function rotateClockwise() {
  rotate.value = (rotate.value + 90) % 360;
}

function handleDoubleClick() {
  if (scale.value === 1 && translateX.value === 0 && translateY.value === 0) {
    scale.value = 2;
  } else {
    resetTransform();
  }
}

function handleWheel(e) {
  if (e.deltaY < 0) {
    zoomIn();
  } else {
    zoomOut();
  }
}

function startDrag(e) {
  if (e.button !== 0) return; // 仅左键响应拖拽
  isDragging.value = true;
  startPos.value = {
    x: e.clientX - translateX.value,
    y: e.clientY - translateY.value,
  };
}

function onDrag(e) {
  if (!isDragging.value) return;
  translateX.value = e.clientX - startPos.value.x;
  translateY.value = e.clientY - startPos.value.y;
}

function endDrag() {
  isDragging.value = false;
}

// 触屏移动端单指平移支持
let touchStartPos = { x: 0, y: 0 };
function onTouchStart(e) {
  if (e.touches.length === 1) {
    touchStartPos = {
      x: e.touches[0].clientX - translateX.value,
      y: e.touches[0].clientY - translateY.value,
    };
  }
}

function onTouchMove(e) {
  if (e.touches.length === 1) {
    translateX.value = e.touches[0].clientX - touchStartPos.x;
    translateY.value = e.touches[0].clientY - touchStartPos.y;
  }
}

function onTouchEnd() {
  //
}

function handleClose() {
  emit('close');
}

function handleKeydown(e) {
  if (!props.show) return;
  if (e.key === 'Escape') {
    handleClose();
  } else if (e.key === '+' || e.key === '=') {
    zoomIn();
  } else if (e.key === '-') {
    zoomOut();
  } else if (e.key === '0') {
    resetTransform();
  }
}

watch(() => props.show, (val) => {
  if (val) {
    resetTransform();
    nextTick(() => {
      containerRef.value?.focus();
    });
    document.body.style.overflow = 'hidden';
  } else {
    document.body.style.overflow = '';
  }
});

onMounted(() => {
  window.addEventListener('keydown', handleKeydown);
});

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown);
  document.body.style.overflow = '';
});
</script>

<style scoped>
.lightbox-fade-enter-active,
.lightbox-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.lightbox-fade-enter-from,
.lightbox-fade-leave-to {
  opacity: 0;
  transform: scale(0.98);
}
</style>
