import { ref } from 'vue';

const isDark = ref(false);

export function useTheme() {
  function applyTheme(dark) {
    if (dark) {
      document.documentElement.classList.add('dark');
      localStorage.theme = 'dark';
    } else {
      document.documentElement.classList.remove('dark');
      localStorage.theme = 'light';
    }
  }

  function initTheme() {
    if (localStorage.theme === 'dark' || (!('theme' in localStorage) && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
      isDark.value = true;
      document.documentElement.classList.add('dark');
    } else {
      isDark.value = false;
      document.documentElement.classList.remove('dark');
    }
  }

  function toggleDarkMode(event) {
    const isAppearanceTransition = typeof document.startViewTransition === 'function' &&
      !window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    if (!isAppearanceTransition || !event) {
      isDark.value = !isDark.value;
      applyTheme(isDark.value);
      return;
    }

    const x = event.clientX;
    const y = event.clientY;
    const endRadius = Math.hypot(
      Math.max(x, window.innerWidth - x),
      Math.max(y, window.innerHeight - y)
    );

    document.documentElement.style.setProperty('--theme-x', `${x}px`);
    document.documentElement.style.setProperty('--theme-y', `${y}px`);
    document.documentElement.style.setProperty('--theme-r', `${endRadius}px`);

    const ambientEl = document.getElementById('ambient-cursor');
    if (ambientEl) ambientEl.style.opacity = '0';

    const transition = document.startViewTransition(() => {
      isDark.value = !isDark.value;
      applyTheme(isDark.value);
    });

    transition.finished.finally(() => {
      if (ambientEl) ambientEl.style.opacity = '';
    });
  }

  return {
    isDark,
    initTheme,
    applyTheme,
    toggleDarkMode,
  };
}
