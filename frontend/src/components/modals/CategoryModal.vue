<template>
  <!-- ======================= 模态框 4：新建/编辑专栏分类弹窗 ======================= -->
  <div v-if="show"
       class="fixed inset-0 z-50 overflow-y-auto bg-slate-900/70 backdrop-blur-sm flex items-center justify-center p-4"
       @click.self="emit('close')">
    <div class="w-full max-w-md bg-white dark:bg-slate-900 rounded-3xl shadow-2xl border border-slate-200 dark:border-slate-800 p-6 space-y-4 animate-in zoom-in-95 duration-200">
      <div class="flex items-center justify-between pb-3 border-b border-slate-100 dark:border-slate-800">
        <h3 class="font-bold text-base text-slate-900 dark:text-white flex items-center gap-2">
          <span>🏷️</span> <span>{{ editingId ? '编辑专栏分类' : '新建专栏分类' }}</span>
        </h3>
        <button @click="emit('close')" class="text-slate-400 hover:text-slate-600">✕</button>
      </div>

      <div class="space-y-3 text-xs">
        <div>
          <label class="block font-semibold mb-1 text-slate-700 dark:text-slate-300">专栏名称 *</label>
          <input v-model="form.name" type="text" placeholder="例如: Rust & 高性能编程"
                 class="w-full px-3.5 py-2.5 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:outline-none focus:ring-2 focus:ring-sky-500 text-sm font-bold">
        </div>

        <div>
          <label class="block font-semibold mb-1 text-slate-700 dark:text-slate-300">专栏图标 (Emoji)</label>
          <input v-model="form.icon" type="text" placeholder="例如: 🦀、⚡、☁️"
                 class="w-full px-3.5 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:outline-none">
        </div>

        <div>
          <label class="block font-semibold mb-1 text-slate-700 dark:text-slate-300">专栏详细介绍</label>
          <textarea v-model="form.description" rows="3" placeholder="描述该专栏的核心技术栈与探索方向..."
                    class="w-full px-3.5 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:outline-none leading-relaxed"></textarea>
        </div>
      </div>

      <div class="pt-3 flex justify-end gap-2">
        <button @click="emit('close')" class="px-4 py-2 rounded-xl text-xs text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800">
          取消
        </button>
        <button @click="emit('save', form)" class="px-5 py-2 rounded-xl text-xs font-bold bg-sky-600 hover:bg-sky-700 text-white shadow-md shadow-sky-500/20">
          确认保存
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  show: Boolean,
  editingId: [Number, String],
  initialData: Object,
});

const emit = defineEmits(['close', 'save']);

const form = ref({
  name: '',
  icon: '📑',
  description: '',
  ...(props.initialData || {}),
});

watch(() => props.initialData, (newVal) => {
  if (newVal) {
    form.value = { ...newVal };
  } else {
    form.value = { name: '', icon: '📑', description: '' };
  }
});
</script>
