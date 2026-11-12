<template>
  <!-- 1. 每日极客金句 & 终端广播条 (Tech Broadcast Banner) -->
  <div class="p-3 sm:p-4 rounded-2xl bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 dark:from-slate-900 dark:via-sky-950/40 dark:to-slate-900 text-white border border-slate-800 shadow-sm flex items-center justify-between gap-3 overflow-hidden relative">
    <div class="flex items-center gap-2.5 text-xs z-10 flex-1 min-w-0">
      <span class="w-6 h-6 rounded-lg bg-emerald-500/20 text-emerald-400 flex items-center justify-center font-mono text-[11px] font-bold shrink-0">
        &gt;_
      </span>
      <div class="overflow-x-auto whitespace-nowrap no-scrollbar flex-1 py-0.5" style="-webkit-overflow-scrolling: touch;">
        <span class="text-emerald-400 font-mono font-bold">[2026 架构实录]</span>
        <span class="text-slate-300 ml-1.5">容器化数据隔离 + 微服务自愈 + 3D 沉浸式极客交互</span>
      </div>
    </div>
    <span class="hidden sm:inline-block px-2 py-0.5 rounded text-[10px] font-mono bg-white/10 text-slate-300 shrink-0">
      LIVE
    </span>
  </div>
</template>
