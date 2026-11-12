import { ref } from 'vue';

const confirmState = ref({
  isOpen: false,
  title: '操作确认',
  message: '确定要执行此操作吗？',
  confirmText: '确定',
  cancelText: '取消',
  type: 'danger', // 'danger' | 'warning' | 'info'
  icon: '',
  resolve: null,
});

export function useConfirm() {
  function showConfirm(options = {}) {
    return new Promise((resolve) => {
      confirmState.value = {
        isOpen: true,
        title: options.title || '操作确认',
        message: options.message || '确定要执行此操作吗？',
        confirmText: options.confirmText || (options.type === 'danger' ? '确认删除' : '确定'),
        cancelText: options.cancelText || '取消',
        type: options.type || 'danger',
        icon: options.icon || (options.type === 'warning' ? '⚠️' : options.type === 'info' ? 'ℹ️' : '🗑️'),
        resolve,
      };
    });
  }

  function handleConfirm() {
    if (confirmState.value.resolve) {
      confirmState.value.resolve(true);
      confirmState.value.resolve = null;
    }
    confirmState.value.isOpen = false;
  }

  function handleCancel() {
    if (confirmState.value.resolve) {
      confirmState.value.resolve(false);
      confirmState.value.resolve = null;
    }
    confirmState.value.isOpen = false;
  }

  return {
    confirmState,
    showConfirm,
    handleConfirm,
    handleCancel,
  };
}
