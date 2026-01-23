<template>
  <div class="page-container">
    <h2>글쓰기</h2>
    <textarea v-model="content" placeholder="교통 정보를 공유하세요." rows="8"></textarea>
    <button @click="submit" class="submit-btn">등록하기</button>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const content = ref('');
const router = useRouter();

const submit = async () => {
  const token = localStorage.getItem('accessToken');
  if (!token) return alert('로그인이 필요합니다.');

  const res = await fetch('/api/community/posts', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ content: content.value })
  });

  if (res.ok) {
    router.push('/community');
  } else {
    const msg = await res.text();
    alert('실패: ' + msg);
  }
};
</script>

<style scoped>
.page-container { max-width: 600px; margin: 20px auto; padding: 20px; background: white; border-radius: 8px; }
textarea { width: 100%; padding: 10px; margin-bottom: 10px; resize: vertical; }
.submit-btn { background: #28a745; color: white; border: none; padding: 10px 20px; cursor: pointer; float: right; }
</style>