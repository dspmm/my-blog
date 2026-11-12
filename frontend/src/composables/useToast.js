import { ref } from 'vue';

const toasts = ref([]);

export function useToast() {
  function showToast(text, type = 'info', icon = '🔔') {
    const id = Date.now() + Math.random();
    if (type === 'success') icon = '✅';
    if (type === 'error') icon = '⚠️';
    toasts.value.push({ id, text, type, icon });
    setTimeout(() => {
      toasts.value = toasts.value.filter(t => t.id !== id);
    }, 3000);
  }

  function removeToast(id) {
    toasts.value = toasts.value.filter(t => t.id !== id);
  }

  return {
    toasts,
    showToast,
    removeToast,
  };
}
