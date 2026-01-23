<template>
  <div class="flex flex-1 items-center justify-center py-12 px-4 sm:px-6 lg:px-8 w-full">
    <div class="w-full max-w-md">

      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-lg border border-gray-200 dark:border-gray-700 p-6 sm:p-8 w-full">

        <div class="flex flex-col gap-2 mb-8 text-center">
          <div class="mx-auto bg-blue-100 dark:bg-blue-900/30 rounded-full p-3 w-16 h-16 flex items-center justify-center mb-2">
            <span class="material-symbols-outlined text-blue-600 dark:text-blue-400 text-3xl">person_add</span>
          </div>
          <h1 class="text-gray-900 dark:text-white tracking-tight text-2xl font-bold">계정 만들기</h1>
          <p class="text-gray-500 dark:text-gray-400 text-sm">
            커뮤니티에 가입하여 교통 정보를 제보하고 확인하세요.
          </p>
        </div>

        <form @submit.prevent="handleSignup" class="flex flex-col gap-5">
          <div class="flex flex-col gap-1.5">
            <label class="text-gray-900 dark:text-gray-100 text-sm font-medium">이메일 주소</label>
            <div class="flex gap-2">
              <div class="relative flex-grow">
                <input v-model="form.username" type="email" placeholder="name@example.com" class="form-input block w-full rounded-lg border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:border-blue-500 focus:ring-blue-500 sm:text-sm h-11 px-4"/>
                <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none text-gray-400">
                  <span class="material-symbols-outlined text-lg">mail</span>
                </div>
              </div>
              <button type="button" @click="sendCode" :disabled="isCodeSent" class="px-4 bg-gray-100 dark:bg-gray-600 text-gray-700 dark:text-gray-200 rounded-lg text-sm font-bold whitespace-nowrap hover:bg-gray-200 dark:hover:bg-gray-500 transition-colors border border-gray-300 dark:border-gray-500">
                {{ isCodeSent ? '전송됨' : '인증' }}
              </button>
            </div>
          </div>

          <div v-if="isCodeSent" class="flex flex-col gap-1.5">
            <label class="text-gray-900 dark:text-gray-100 text-sm font-medium">인증번호</label>
            <input v-model="form.verificationCode" type="text" placeholder="6자리 인증번호 입력" class="form-input block w-full rounded-lg border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:border-blue-500 focus:ring-blue-500 sm:text-sm h-11 px-4"/>
          </div>

          <div class="flex flex-col gap-1.5">
            <label class="text-gray-900 dark:text-gray-100 text-sm font-medium">닉네임</label>
            <div class="relative">
              <input v-model="form.nickname" type="text" placeholder="커뮤니티에서 사용할 이름" class="form-input block w-full rounded-lg border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:border-blue-500 focus:ring-blue-500 sm:text-sm h-11 px-4"/>
              <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none text-gray-400">
                <span class="material-symbols-outlined text-lg">badge</span>
              </div>
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="flex flex-col gap-1.5">
              <label class="text-gray-900 dark:text-gray-100 text-sm font-medium">비밀번호</label>
              <div class="relative group">
                <input v-model="form.password" type="password" placeholder="••••••••" class="form-input block w-full rounded-lg border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:border-blue-500 focus:ring-blue-500 sm:text-sm h-11 px-4"/>
              </div>
            </div>
            <div class="flex flex-col gap-1.5">
              <label class="text-gray-900 dark:text-gray-100 text-sm font-medium">비밀번호 확인</label>
              <div class="relative group">
                <input v-model="confirmPassword" type="password" placeholder="••••••••" class="form-input block w-full rounded-lg border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:border-blue-500 focus:ring-blue-500 sm:text-sm h-11 px-4"/>
              </div>
            </div>
          </div>

          <div class="flex items-start gap-3 mt-2">
            <div class="flex h-5 items-center">
              <input id="terms" type="checkbox" v-model="isAgreed" required class="w-4 h-4 rounded border-gray-300 dark:border-gray-600 text-blue-600 focus:ring-blue-500 bg-white dark:bg-gray-700"/>
            </div>
            <div class="text-sm leading-tight">
              <label for="terms" class="text-gray-500 dark:text-gray-400 select-none cursor-pointer">
                <button type="button" @click.stop="openModal('terms')" class="font-medium text-blue-600 hover:underline">서비스 이용약관</button> 및
                <button type="button" @click.stop="openModal('privacy')" class="font-medium text-blue-600 hover:underline">개인정보 처리방침</button>에 동의합니다.
              </label>
            </div>
          </div>

          <button type="submit" class="mt-2 w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-4 rounded-lg shadow-md transition-all active:scale-[0.98]">
            회원가입 완료
          </button>
        </form>

        <div class="mt-6 text-center">
          <p class="text-sm text-gray-500 dark:text-gray-400">
            이미 계정이 있으신가요?
            <nuxt-link to="/login" class="font-bold text-blue-600 hover:text-blue-700 ml-1">로그인하기</nuxt-link>
          </p>
        </div>
      </div>
    </div>

    <div v-if="showModal" class="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm" @click.self="closeModal">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-2xl w-full max-w-lg overflow-hidden border border-gray-200 dark:border-gray-700 animate-[fadeIn_0.2s_ease-out]">

        <div class="px-6 py-4 border-b border-gray-100 dark:border-gray-700 flex justify-between items-center bg-gray-50 dark:bg-gray-700/50">
          <h3 class="text-lg font-bold text-gray-900 dark:text-white">
            서비스 이용약관 및 개인정보 처리방침
          </h3>
          <button @click="closeModal" class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200">
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>

        <div class="p-6 max-h-[60vh] overflow-y-auto">

          <div class="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-4 mb-6">
            <div class="flex items-start gap-3">
              <span class="material-symbols-outlined text-red-600 dark:text-red-400 text-3xl">warning</span>
              <div>
                <h4 class="text-red-700 dark:text-red-400 font-bold text-lg mb-1">경고: 실제 정보 입력 금지</h4>
                <p class="text-red-600 dark:text-red-300 text-sm font-medium leading-relaxed">
                  본 사이트는 포트폴리오용 <strong>테스트 페이지</strong>입니다.<br/>
                  보안이 완벽하지 않으므로, <span class="underline decoration-2 underline-offset-2">실제 사용하시는 비밀번호나 민감한 개인정보를 절대 입력하지 마십시오.</span>
                </p>
              </div>
            </div>
          </div>

          <div class="mb-6">
            <h4 class="font-bold text-gray-900 dark:text-white mb-2">법적 책임 면책 고지</h4>
            <p class="text-gray-600 dark:text-gray-300 text-sm leading-relaxed">
              사용자가 본 테스트 사이트에 실제 개인정보(실명, 실제 전화번호, 금융 정보, 타 사이트와 동일한 비밀번호 등)를 입력하여 발생하는
              <strong>개인정보 유출, 해킹 피해, 금전적 손실 등 모든 사고에 대해 개발자는 어떠한 법적 책임도 지지 않습니다.</strong>
              테스트 용도의 가상 정보만 사용해주시기 바랍니다.
            </p>
          </div>

          <div class="text-gray-500 dark:text-gray-400 text-xs space-y-2">
            <p>1. 목적: 본 약관은 TrafficEye 테스트 서비스 이용 조건 및 절차를 규정합니다.</p>
            <p>2. 계정 관리: 테스트 계정은 예고 없이 삭제될 수 있습니다.</p>
            <p>3. 데이터 파기: 수집된 모든 정보는 프로젝트 종료 시 지체 없이 파기됩니다.</p>
            <p>4. 동의: 가입 버튼을 누름으로써 위 면책 조항에 동의한 것으로 간주합니다.</p>
          </div>
        </div>

        <div class="px-6 py-4 bg-gray-50 dark:bg-gray-700/50 flex justify-end">
          <button @click="agreeAndClose" class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-6 rounded-lg transition-colors">
            확인 및 동의
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const isCodeSent = ref(false);
const confirmPassword = ref('');
const form = ref({ username: '', password: '', nickname: '', verificationCode: '' });

// 1. 체크박스 상태 변수 추가
const isAgreed = ref(false);

const showModal = ref(false);
const modalType = ref('');

const openModal = (type) => {
  modalType.value = type;
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
};

// 2. 확인 및 동의 버튼 클릭 시 실행될 함수
const agreeAndClose = () => {
  isAgreed.value = true; // 체크박스 체크
  closeModal();          // 모달 닫기
};

const sendCode = async () => {
  if (!form.value.username.includes('@')) return alert('유효한 이메일을 입력하세요.');

  try {
    const res = await fetch('/api/auth/send-code', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: form.value.username })
    });
    if (res.ok) {
      alert('인증번호가 이메일로 발송되었습니다.');
      isCodeSent.value = true;
    } else {
      alert('전송 실패');
    }
  } catch(e) { console.error(e); }
};

const handleSignup = async () => {
  // 3. 약관 동의 체크 여부 확인
  if (!isAgreed.value) {
    return alert('서비스 이용약관 및 개인정보 처리방침에 동의해주세요.');
  }

  if (form.value.password !== confirmPassword.value) {
    return alert('비밀번호가 일치하지 않습니다.');
  }

  try {
    const res = await fetch('/api/auth/signup', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form.value)
    });

    if (res.ok) {
      alert('가입 성공! 로그인해주세요.');
      router.push('/login');
    } else {
      const msg = await res.text();
      alert('가입 실패: ' + msg);
    }
  } catch(e) { alert('오류 발생'); }
};
</script>