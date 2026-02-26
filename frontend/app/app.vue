<template>
  <div class="min-h-screen flex flex-col bg-gray-50 dark:bg-gray-900 transition-colors duration-300 font-sans">

    <header class="sticky top-0 z-50 bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 shadow-sm">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16 items-center">
          <div class="flex items-center gap-2 cursor-pointer" @click="$router.push('/')">
            <span class="text-blue-600 text-3xl font-bold">Traffic<span class="text-gray-800 dark:text-white">Eye</span></span>
          </div>

          <nav class="hidden md:flex space-x-8">
            <nuxt-link to="/" class="px-3 py-2 text-sm font-medium transition-colors" :class="$route.path === '/' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-white'">
              CCTV 모니터링
            </nuxt-link>
            <nuxt-link to="/incidents" class="px-3 py-2 text-sm font-medium transition-colors" :class="$route.path.includes('/incidents') ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-white'">
              돌발상황
            </nuxt-link>
            <nuxt-link to="/community" class="px-3 py-2 text-sm font-medium transition-colors" :class="$route.path.includes('/community') ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-white'">
              커뮤니티
            </nuxt-link>
          </nav>

          <div class="flex items-center gap-6">
            <button class="p-2 text-gray-400 hover:text-gray-500 dark:hover:text-gray-300 rounded-full transition-colors" @click="toggleDark">
              <span v-if="isDark" class="material-symbols-outlined text-xl">light_mode</span>
              <span v-else class="material-symbols-outlined text-xl">dark_mode</span>
            </button>

            <div class="flex items-center gap-4">
              <nuxt-link to="/login" class="text-sm font-medium text-gray-700 dark:text-gray-300 hover:text-blue-600 dark:hover:text-blue-400 transition-colors">
                로그인
              </nuxt-link>

              <span class="h-4 w-px bg-gray-300 dark:bg-gray-600"></span>

              <nuxt-link to="/signup" class="text-sm font-medium text-gray-700 dark:text-gray-300 hover:text-blue-600 dark:hover:text-blue-400 transition-colors">
                회원가입
              </nuxt-link>
            </div>
          </div>
        </div>
      </div>
    </header>

    <main class="flex-grow max-w-7xl mx-auto w-full px-4 sm:px-6 lg:px-8 py-8">
      <NuxtPage />
    </main>

    <footer class="bg-white dark:bg-gray-800 border-t border-gray-200 dark:border-gray-700 mt-auto">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <p class="text-center text-sm text-gray-500 dark:text-gray-400">© 2026 TrafficEye System. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const isDark = ref(false);

onMounted(() => {
  const userTheme = localStorage.getItem('theme');
  const systemDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
  if (userTheme === 'dark' || (!userTheme && systemDark)) {
    isDark.value = true;
    document.documentElement.classList.add('dark');
  } else {
    isDark.value = false;
    document.documentElement.classList.remove('dark');
  }
});

const toggleDark = () => {
  isDark.value = !isDark.value;
  if (isDark.value) {
    document.documentElement.classList.add('dark');
    localStorage.setItem('theme', 'dark');
  } else {
    document.documentElement.classList.remove('dark');
    localStorage.setItem('theme', 'light');
  }
};
</script>