<template>
  <div class="max-w-4xl mx-auto">
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-white mb-2">Traffic Incidents (돌발상황)</h1>
      <p class="text-gray-500 dark:text-gray-400">실시간 사고, 공사, 통제 정보를 확인하세요.</p>
    </div>

    <div class="flex gap-2 mb-6">
      <button class="px-4 py-1.5 rounded-full text-sm font-medium bg-blue-600 text-white shadow-sm">전체</button>
      <button class="px-4 py-1.5 rounded-full text-sm font-medium bg-white dark:bg-gray-800 text-gray-600 dark:text-gray-300 border border-gray-200 dark:border-gray-700 hover:bg-gray-50">사고</button>
      <button class="px-4 py-1.5 rounded-full text-sm font-medium bg-white dark:bg-gray-800 text-gray-600 dark:text-gray-300 border border-gray-200 dark:border-gray-700 hover:bg-gray-50">공사</button>
    </div>

    <div class="space-y-4">
      <details v-for="event in events" :key="event.eventId" class="group bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <summary class="flex items-center justify-between p-5 cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors select-none">
          <div class="flex items-center gap-4 flex-1">
            <div class="flex-shrink-0 w-10 h-10 rounded-full bg-red-100 dark:bg-red-900/30 flex items-center justify-center text-red-600 dark:text-red-400">
              <span v-if="event.type.includes('사고')">💥</span>
              <span v-else-if="event.type.includes('공사')">🚧</span>
              <span v-else>⚠️</span>
            </div>
            <div>
              <div class="flex items-center gap-2 mb-1">
                <span class="px-2 py-0.5 rounded text-xs font-bold bg-red-100 text-red-700 dark:bg-red-900/50 dark:text-red-300 uppercase tracking-wide">{{ event.type }}</span>
              </div>
              <h3 class="text-lg font-semibold text-gray-900 dark:text-white leading-tight">{{ event.message.substring(0, 30) }}...</h3>
            </div>
          </div>
          <div class="flex items-center gap-4">
            <span class="text-sm font-medium text-gray-500 dark:text-gray-400 hidden sm:block">{{ new Date(event.updateTime).toLocaleTimeString() }}</span>
            <span class="text-gray-400 transition-transform duration-300 group-open:rotate-180">▼</span>
          </div>
        </summary>
        <div class="px-5 pb-5 pt-0 border-t border-gray-100 dark:border-gray-700">
          <div class="mt-4">
            <h4 class="text-sm font-semibold text-gray-900 dark:text-gray-100 uppercase tracking-wider mb-2">상세 내용</h4>
            <p class="text-gray-600 dark:text-gray-300 text-sm leading-relaxed">{{ event.message }}</p>
            <div class="mt-3 bg-gray-50 dark:bg-gray-700/50 p-3 rounded-lg flex justify-between text-sm">
              <span class="text-gray-500">좌표:</span>
              <span class="font-medium text-gray-900 dark:text-white">{{ event.coordX }}, {{ event.coordY }}</span>
            </div>
          </div>
        </div>
      </details>

      <div v-if="events.length === 0" class="text-center py-10 text-gray-500">현재 돌발 정보가 없습니다.</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
const events = ref([]);

const fetchEvents = async () => {
  try {
    const res = await fetch('/api/events');
    if (res.ok) events.value = await res.json();
  } catch (e) { console.error(e); }
};

onMounted(fetchEvents);
</script>