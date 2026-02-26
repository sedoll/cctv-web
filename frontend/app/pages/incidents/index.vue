<template>
  <div class="max-w-4xl mx-auto">
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-white mb-2">돌발상황</h1>
      <p class="text-gray-500 dark:text-gray-400">실시간 사고, 공사, 통제 정보를 확인하세요.</p>
    </div>

    <div class="mb-6 flex flex-col sm:flex-row gap-2">
      <select
          v-model="searchType"
          class="w-full sm:w-44 px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md bg-white dark:bg-gray-800 text-sm text-gray-900 dark:text-white"
      >
        <option value="all">전체</option>
        <option value="event_type">상황</option>
        <option value="message">내용</option>
      </select>
      <input
          v-model="searchInput"
          @keyup.enter="handleSearch"
          type="text"
          placeholder="돌발상황 검색어 입력"
          class="flex-1 px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md bg-white dark:bg-gray-800 text-sm text-gray-900 dark:text-white placeholder-gray-500"
      />
      <button
          @click="handleSearch"
          class="px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700"
      >
        검색
      </button>
    </div>

    <div v-if="loading" class="text-center py-10 text-gray-500">데이터를 불러오는 중입니다...</div>

    <div v-else class="space-y-4">
      <details
          v-for="event in events"
          :key="event.eventId"
          :open="openedEventId === event.eventId"
          class="group bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden"
      >
        <summary
            class="flex items-center justify-between p-5 cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors select-none"
            @click.prevent="toggleEvent(event.eventId)"
        >
          <div class="flex items-center gap-4 flex-1">
            <div
                class="flex-shrink-0 w-10 h-10 rounded-full bg-red-100 dark:bg-red-900/30 flex items-center justify-center text-red-600 dark:text-red-400"
            >
              <span v-if="event.eventType?.includes('교통사고')">💥</span>
              <span v-else-if="event.eventType?.includes('공사')">🚧</span>
              <span v-else>⚠️</span>
            </div>
            <div>
              <div class="flex items-center gap-2 mb-1">
                <span
                    class="px-2 py-0.5 rounded text-xs font-bold bg-red-100 text-red-700 dark:bg-red-900/50 dark:text-red-300 uppercase tracking-wide"
                >
                  {{ event.eventType || event.type }}
                </span>
                <span class="text-xs text-gray-500 dark:text-gray-400">{{ event.roadName }} ({{ event.roadNo }})</span>
                <span v-if="event.roadDrcType" class="text-xs text-gray-500 dark:text-gray-400">방향: {{ event.roadDrcType }}</span>
              </div>
              <h3 class="text-lg font-semibold text-gray-900 dark:text-white leading-tight">
                {{ shortMessage(event.eventDetailType + ' ' + event.message) }}
              </h3>
            </div>
          </div>
          <div class="flex items-center gap-4">
            <span class="text-sm font-medium text-gray-500 dark:text-gray-400 hidden sm:block">{{ formatDate(event.startDate) }}</span>
            <span class="text-gray-400 transition-transform duration-300 group-open:rotate-180">▼</span>
          </div>
        </summary>
        <div class="px-5 pb-5 pt-0 border-t border-gray-100 dark:border-gray-700">
          <div class="mt-4 space-y-2 text-sm">
            <p class="text-gray-600 dark:text-gray-300 leading-relaxed">{{ event.lanesBlocked }}</p>
            <p class="text-gray-600 dark:text-gray-300 leading-relaxed">{{ event.message }}</p>
            <div class="bg-gray-50 dark:bg-gray-700/50 p-3 rounded-lg flex justify-between">
              <span class="text-gray-500">상세 타입</span>
              <span class="font-medium text-gray-900 dark:text-white">{{ event.eventDetailType }}</span>
            </div>
            <div class="bg-gray-50 dark:bg-gray-700/50 p-3 rounded-lg flex justify-between">
              <span class="text-gray-500">발생 시각</span>
              <span class="font-medium text-gray-900 dark:text-white">{{ formatDate(event.startDate) }}</span>
            </div>
            <div class="bg-gray-50 dark:bg-gray-700/50 p-3 rounded-lg flex justify-between">
              <span class="text-gray-500">종료 시각</span>
              <span class="font-medium text-gray-900 dark:text-white">{{ formatDate(event.endDate) || '-' }}</span>
            </div>
            <div class="bg-gray-50 dark:bg-gray-700/50 p-3 rounded-lg flex justify-between">
              <span class="text-gray-500">위치</span>
              <a
                  :href="getKakaoMapUrl(event)"
                  target="_blank"
                  rel="noopener noreferrer"
                  class="font-medium text-blue-600 dark:text-blue-300 hover:underline"
              >
                지도({{ event.coordY }}, {{ event.coordX }})
              </a>
            </div>
          </div>
        </div>
      </details>

      <div v-if="events.length === 0" class="text-center py-10 text-gray-500">현재 돌발 정보가 없습니다.</div>

      <template v-else>
        <div v-if="totalPages > 0" class="mt-8 flex justify-center">
          <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
            <button
                @click="movePage(currentPage - 1)"
                :disabled="currentPage === 1"
                class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-sm font-medium text-gray-500 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700"
                :class="{ 'opacity-50 cursor-not-allowed': currentPage === 1 }"
            >
              <span class="sr-only">Previous</span>
              <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                <path fill-rule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clip-rule="evenodd" />
              </svg>
            </button>

            <button
                v-for="page in displayedPages"
                :key="page"
                @click="movePage(page)"
                class="relative inline-flex items-center px-4 py-2 border text-sm font-medium"
                :class="page === currentPage
              ? 'z-10 bg-blue-50 dark:bg-blue-900 border-blue-500 text-blue-600 dark:text-blue-200'
              : 'bg-white dark:bg-gray-800 border-gray-300 dark:border-gray-600 text-gray-500 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700'"
            >
              {{ page }}
            </button>

            <button
                @click="movePage(currentPage + 1)"
                :disabled="currentPage === totalPages"
                class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-sm font-medium text-gray-500 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700"
                :class="{ 'opacity-50 cursor-not-allowed': currentPage === totalPages }"
            >
              <span class="sr-only">Next</span>
              <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
              </svg>
            </button>
          </nav>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';

const events = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const totalPages = ref(1);
const pageSize = 5;
const openedEventId = ref(null);
const searchType = ref('all');
const searchInput = ref('');
const searchKeyword = ref('');

const displayedPages = computed(() => {
  const total = totalPages.value;
  const current = currentPage.value;
  const pages = [];

  let start = Math.max(1, current - 2);
  let end = Math.min(total, start + 4);

  if (end - start < 4) {
    start = Math.max(1, end - 4);
  }

  for (let i = start; i <= end; i++) {
    pages.push(i);
  }

  return pages;
});

const fetchEvents = async (page = 1) => {
  loading.value = true;
  try {
    const params = new URLSearchParams({
      pageIndex: String(page),
      pageSize: String(pageSize),
      searchType: searchType.value,
      query: searchKeyword.value
    });
    const res = await fetch(`/api/events?${params.toString()}`);
    if (!res.ok) throw new Error('failed to fetch events');

    const data = await res.json();
    events.value = data.events || [];
    openedEventId.value = null;
    currentPage.value = data.currentPage || 1;
    totalPages.value = data.totalPages || 1;
  } catch (e) {
    console.error(e);
    events.value = [];
    openedEventId.value = null;
    currentPage.value = 1;
    totalPages.value = 1;
  } finally {
    loading.value = false;
  }
};

const movePage = (page) => {
  if (page < 1 || page > totalPages.value) return;
  fetchEvents(page);
};

const handleSearch = () => {
  searchKeyword.value = searchInput.value.trim();
  fetchEvents(1);
};


const shortMessage = (message = '') => {
  if (message.length <= 30) return message;
  return `${message.substring(0, 30)}...`;
};

const formatDate = (value = '') => {
  if (!value || value.length !== 14) return '';
  const y = value.slice(0, 4);
  const m = value.slice(4, 6);
  const d = value.slice(6, 8);
  const hh = value.slice(8, 10);
  const mm = value.slice(10, 12);
  return `${y}-${m}-${d} ${hh}:${mm}`;
};


const toggleEvent = (eventId) => {
  openedEventId.value = openedEventId.value === eventId ? null : eventId;
};

const getKakaoMapUrl = (event) => {
  return `https://map.kakao.com/link/map/${event.eventType},${event.coordY},${event.coordX}`;
};

onMounted(() => fetchEvents(1));
</script>