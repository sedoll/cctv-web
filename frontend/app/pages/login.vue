<template>
  <div class="flex flex-1 items-center justify-center py-12 px-4 sm:px-6 lg:px-8 w-full">
    <div class="w-full max-w-md">

      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-lg border border-gray-200 dark:border-gray-700 p-6 sm:p-8 w-full">

        <div class="flex flex-col gap-2 mb-8 text-center">
          <div class="mx-auto bg-blue-100 dark:bg-blue-900/30 rounded-full p-3 w-16 h-16 flex items-center justify-center mb-2">
            <span class="material-symbols-outlined text-blue-600 dark:text-blue-400 text-3xl">lock_open</span>
          </div>
          <h1 class="text-gray-900 dark:text-white tracking-tight text-2xl font-bold">로그인</h1>
          <p class="text-gray-500 dark:text-gray-400 text-sm">
            서비스 이용을 위해 계정에 로그인해주세요.
          </p>
        </div>

        <form @submit.prevent="handleLogin" class="flex flex-col gap-5">

          <div class="flex flex-col gap-1.5">
            <label class="text-gray-900 dark:text-gray-100 text-sm font-medium">이메일 아이디</label>
            <div class="relative">
              <input v-model="username" type="email" required placeholder="name@example.com"
                     class="form-input block w-full rounded-lg border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:border-blue-500 focus:ring-blue-500 sm:text-sm h-11 px-4 placeholder-gray-400"/>
              <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none text-gray-400">
                <span class="material-symbols-outlined text-lg">mail</span>
              </div>
            </div>
          </div>

          <div class="flex flex-col gap-1.5">
            <div class="flex justify-between items-center">
              <label class="text-gray-900 dark:text-gray-100 text-sm font-medium">비밀번호</label>
              <a href="#" class="text-xs font-medium text-blue-600 hover:text-blue-500">비밀번호를 잊으셨나요?</a>
            </div>
            <div class="relative group">
              <input v-model="password" type="password" required placeholder="비밀번호 입력"
                     class="form-input block w-full rounded-lg border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:border-blue-500 focus:ring-blue-500 sm:text-sm h-11 px-4 placeholder-gray-400"/>
              <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none text-gray-400">
                <span class="material-symbols-outlined text-lg">lock</span>
              </div>
            </div>
          </div>

          <div class="flex items-center gap-2">
            <input id="remember-me" type="checkbox" class="w-4 h-4 rounded border-gray-300 dark:border-gray-600 text-blue-600 focus:ring-blue-500 bg-white dark:bg-gray-700"/>
            <label for="remember-me" class="text-sm text-gray-600 dark:text-gray-400 cursor-pointer select-none">
              로그인 상태 유지
            </label>
          </div>

          <button type="submit" class="mt-2 w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-4 rounded-lg shadow-md transition-all active:scale-[0.98] flex justify-center items-center gap-2">
            로그인
            <span class="material-symbols-outlined text-sm">arrow_forward</span>
          </button>
        </form>

        <div class="mt-6 text-center">
          <p class="text-sm text-gray-500 dark:text-gray-400">
            아직 계정이 없으신가요?
            <nuxt-link to="/signup" class="font-bold text-blue-600 hover:text-blue-700 ml-1">회원가입 하기</nuxt-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const username = ref('');
const password = ref('');

const handleLogin = async () => {
  try {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: username.value, password: password.value })
    });

    if (res.ok) {
      const data = await res.json();
      localStorage.setItem('accessToken', data.token);

      // 로그인 성공 알림 대신 부드럽게 페이지 이동 (필요하면 alert 추가 가능)
      router.push('/');
    } else {
      alert('로그인 실패: 아이디와 비밀번호를 확인해주세요.');
    }
  } catch (e) {
    console.error(e);
    alert('서버 연결 오류가 발생했습니다.');
  }
};
</script>