<template>
  <div class="w-full">
    <div class="flex flex-col sm:flex-row sm:items-center justify-between mb-8 gap-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white">커뮤니티 (Community)</h1>
        <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">운전자들의 실시간 소통 공간</p>
      </div>
      <nuxt-link to="/community/write" class="bg-blue-600 hover:bg-blue-700 text-white px-5 py-2.5 rounded-lg text-sm font-medium shadow-sm flex items-center justify-center gap-2 transition-colors">
        ✏️ 글쓰기
      </nuxt-link>
    </div>

    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
      <div class="hidden md:grid grid-cols-12 gap-4 px-6 py-3 bg-gray-50 dark:bg-gray-700/50 border-b border-gray-200 dark:border-gray-700 text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider">
        <div class="col-span-1 text-center">No.</div>
        <div class="col-span-8">Title</div>
        <div class="col-span-2">Author</div>
        <div class="col-span-1 text-right">Date</div>
      </div>

      <div class="divide-y divide-gray-200 dark:divide-gray-700">
        <div v-for="post in posts" :key="post.id" @click="$router.push(`/community/${post.id}`)" class="group hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors duration-150 p-4 md:px-6 md:py-4 cursor-pointer">
          <div class="md:grid md:grid-cols-12 md:gap-4 md:items-center">

            <div class="flex items-center justify-between md:hidden mb-2 text-xs text-gray-500 dark:text-gray-400">
              <span class="px-2 py-0.5 rounded bg-blue-100 dark:bg-blue-900 text-blue-700 dark:text-blue-300 font-medium">제보</span>
              <span>{{ new Date(post.createdAt).toLocaleDateString() }}</span>
            </div>

            <div class="hidden md:block col-span-1 text-center text-sm text-gray-500">{{ post.id }}</div>

            <div class="col-span-8">
              <span class="text-base md:text-sm font-medium text-gray-900 dark:text-gray-100 group-hover:text-blue-600 transition-colors block md:inline">
                {{ post.content.length > 50 ? post.content.substring(0, 50) + '...' : post.content }}
              </span>
              <span class="md:hidden mt-1 block text-xs text-gray-500">By {{ post.member?.nickname }}</span>
            </div>

            <div class="hidden md:flex col-span-2 items-center text-sm text-gray-600 dark:text-gray-300">
              <div class="flex items-center gap-2">
                <span class="w-6 h-6 rounded-full bg-gray-200 dark:bg-gray-600 flex items-center justify-center text-xs">👤</span>
                {{ post.member?.nickname || '익명' }}
              </div>
            </div>

            <div class="hidden md:block col-span-1 text-right text-sm text-gray-500">{{ new Date(post.createdAt).toLocaleDateString() }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const posts = ref([]);

const fetchPosts = async () => {
  try {
    const res = await fetch('/api/community/posts');
    if (res.ok) posts.value = await res.json();
  } catch (e) { console.error(e); }
};

onMounted(fetchPosts);
</script>