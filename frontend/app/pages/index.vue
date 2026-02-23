<template>
  <div>
    <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
      <h1 class="text-2xl font-bold text-gray-900 dark:text-white">실시간 CCTV 모니터링</h1>
      <div class="flex w-full md:w-auto gap-2">
        <div class="relative w-full md:w-64 group">
          <input type="text" class="block w-full pl-4 pr-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md leading-5 bg-white dark:bg-gray-800 text-gray-900 dark:text-white placeholder-gray-500 focus:outline-none focus:ring-1 focus:ring-blue-600 sm:text-sm shadow-sm" placeholder="위치 검색..." />
        </div>
        <button class="px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700">검색</button>
      </div>
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
      <div v-for="(cctv, index) in cctvList" :key="cctv.id" class="bg-white dark:bg-gray-800 rounded-lg overflow-hidden shadow-sm hover:shadow-lg transition-all duration-200 border border-gray-200 dark:border-gray-700 flex flex-col group">
        <div class="relative aspect-video bg-gray-200 dark:bg-gray-700 cursor-pointer" @click="openModal(cctv)">
          <img :src="cctv.thumbnailUrl" @error="$event.target.src = thumImg" :alt="cctv.cctvName" loading="lazy" class="w-full h-[189px] object-cover" alt="CCTV Thumbnail" />

<!--          <div class="absolute top-2 left-2 flex items-center gap-1 bg-black/60 px-2 py-0.5 rounded text-white text-[10px] font-bold tracking-wider uppercase">-->
<!--            <span class="w-1.5 h-1.5 rounded-full bg-red-500 animate-pulse"></span> Live-->
<!--          </div>-->
        </div>

        <div class="p-4 flex flex-col justify-between flex-grow">
          <div>
            <div class="flex justify-between items-start mb-1 cursor-pointer" @click="openModal(cctv)">
              <h3 class="text-sm font-bold text-gray-900 dark:text-white truncate w-[200px]" :title="cctv.cctvName">{{ cctv.cctvName }}</h3>
              <span v-if="!cctv.roadType || cctv.roadType == 'ex'" class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium w-[66px] justify-center bg-orange-100 text-orange-800 dark:bg-orange-900 dark:text-orange-300">고속도로</span>
              <span v-if="cctv.roadType == 'its'" class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium w-[66px] justify-center bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-300">국도</span>
            </div>
            <a target="_blank" :href="getKakaoMapUrl(cctv)" class="text-xs text-gray-500 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400 hover:underline">CCTV 위치</a>
          </div>
          <div class="mt-3 flex items-center justify-between text-xs text-gray-400 dark:text-gray-500">
            <span>최근 업데이트: {{ formatDate(cctv.updateDate) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div class="mt-8 flex justify-center" v-if="totalPages > 0">
      <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
        <button
          @click="page > 1 ? page-- : null"
          :disabled="page === 1"
          class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-sm font-medium text-gray-500 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700"
          :class="{ 'opacity-50 cursor-not-allowed': page === 1 }"
        >
          <span class="sr-only">Previous</span>
          <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
            <path fill-rule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clip-rule="evenodd" />
          </svg>
        </button>

        <button
            v-for="p in displayedPages"
            :key="p"
            @click="page = p"
            class="relative inline-flex items-center px-4 py-2 border text-sm font-medium"
            :class="p === page ? 'z-10 bg-blue-50 dark:bg-blue-900 border-blue-500 text-blue-600 dark:text-blue-200' : 'bg-white dark:bg-gray-800 border-gray-300 dark:border-gray-600 text-gray-500 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700'"
        >
            {{ p }}
        </button>

        <button
          @click="page < totalPages ? page++ : null"
          :disabled="page === totalPages"
          class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-sm font-medium text-gray-500 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700"
          :class="{ 'opacity-50 cursor-not-allowed': page === totalPages }"
        >
          <span class="sr-only">Next</span>
          <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
            <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
          </svg>
        </button>
      </nav>
    </div>

    <!-- CCTV Modal -->
    <div v-if="isModalOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 p-4" @click.self="closeModal">
      <div class="relative w-full max-w-4xl bg-black rounded-lg overflow-hidden shadow-2xl">
        <button @click="closeModal" class="absolute top-4 right-4 z-10 text-white hover:text-gray-300 bg-black/50 rounded-full p-1">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
        <div class="aspect-video w-full">
           <CctvPlayer v-if="selectedCctv?.cctvUrl" :src="selectedCctv.cctvUrl" />
           <div v-else class="w-full h-full flex items-center justify-center text-gray-500">영상 준비중</div>
        </div>
        <div class="p-4 bg-white dark:bg-gray-800">
            <h3 class="text-lg font-bold text-gray-900 dark:text-white">{{ selectedCctv?.cctvName }}</h3>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue';
import CctvPlayer from '~/components/CctvPlayer.vue';
import thumImg from '~/assets/img/thum-img.png';

const page = ref(1);
const pageSize = ref(8);

const { data: cctvPage, error } = await useFetch('/api/cctvs', {
  params: {
    pageIndex: page,
    pageSize: pageSize
  },
  watch: [page]
});

// 에러 처리 추가
if (error.value) {
  console.error('Failed to fetch CCTV data:', error.value);
}

const cctvList = computed(() => cctvPage.value?.cctvs || []);
const totalPages = computed(() => cctvPage.value?.totalPages || 0);

const displayedPages = computed(() => {
  const total = totalPages.value;
  const current = page.value;
  const pages = [];

  let start = Math.max(1, current - 2);
  let end = Math.min(total, start + 4);

  if (end - start < 4) {
      start = Math.max(1, end - 4);
  }

  // Ensure start is not less than 1
  if (start < 1) start = 1;

  for (let i = start; i <= end; i++) {
    pages.push(i);
  }
  return pages;
});

const isModalOpen = ref(false);
const selectedCctv = ref(null);

const openModal = (cctv) => {
  selectedCctv.value = cctv;
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
  selectedCctv.value = null;
};

// 최근 업데이트 날짜 포멧팅
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return `${month}-${day} ${hours}:${minutes}`;
};

const getKakaoMapUrl = (event) => {
  return `https://map.kakao.com/link/map/CCTV,${event.coordY},${event.coordX}`;
};
</script>