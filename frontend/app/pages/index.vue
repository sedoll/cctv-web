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
      <div v-for="(cctv, index) in cctvList" :key="index" class="bg-white dark:bg-gray-800 rounded-lg overflow-hidden shadow-sm hover:shadow-lg transition-all duration-200 border border-gray-200 dark:border-gray-700 flex flex-col group">
        <div class="relative aspect-video bg-gray-200 dark:bg-gray-700">
          <CctvPlayer v-if="cctv.url" :src="cctv.url" />
          <div v-else class="w-full h-full flex items-center justify-center text-gray-500">영상 준비중</div>

          <div class="absolute top-2 left-2 flex items-center gap-1 bg-black/60 px-2 py-0.5 rounded text-white text-[10px] font-bold tracking-wider uppercase">
            <span class="w-1.5 h-1.5 rounded-full bg-red-500 animate-pulse"></span> Live
          </div>
        </div>

        <div class="p-4 flex flex-col justify-between flex-grow">
          <div>
            <div class="flex justify-between items-start mb-1">
              <h3 class="text-sm font-bold text-gray-900 dark:text-white truncate">{{ cctv.name }}</h3>
              <span class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-300">원활</span>
            </div>
            <p class="text-xs text-gray-500 dark:text-gray-400">ID: CAM-{{ index + 100 }}</p>
          </div>
          <div class="mt-3 flex items-center justify-between text-xs text-gray-400 dark:text-gray-500">
            <span>업데이트: 실시간</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import CctvPlayer from '~/components/CctvPlayer.vue'; // 컴포넌트 임포트

// 실제 데이터로 교체하세요
const cctvList = ref([
  { name: '강남대로 (신논현)', url: 'https://cctvsec.ktict.co.kr/...' },
  { name: '올림픽대로 (여의도)', url: '' },
  { name: '경부고속도로 (양재)', url: '' },
  { name: '강변북로 (반포)', url: '' }
]);
</script>