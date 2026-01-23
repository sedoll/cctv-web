<template>
  <div class="page-container" v-if="post">
    <div class="post-body">
      <h3>{{ post.member?.nickname }}님의 제보</h3>
      <p class="date">{{ new Date(post.createdAt).toLocaleString() }}</p>
      <div class="content">{{ post.content }}</div>
    </div>

    <hr>

    <div class="comments">
      <h4>댓글 {{ comments.length }}개</h4>
      <div v-for="c in comments" :key="c.id" class="comment-item">
        <strong>{{ c.author }}:</strong> {{ c.content }}
        <button v-if="c.isMyComment" @click="deleteComment(c.id)" class="del-btn">삭제</button>
      </div>

      <div class="comment-input">
        <input v-model="newComment" placeholder="댓글 작성..." @keyup.enter="addComment"/>
        <button @click="addComment">등록</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const postId = route.params.id;
const post = ref(null);
const comments = ref([]);
const newComment = ref('');

const getToken = () => localStorage.getItem('accessToken');
const getAuthHeaders = () => ({
  'Content-Type': 'application/json',
  'Authorization': `Bearer ${getToken()}`
});

const loadData = async () => {
  const [pRes, cRes] = await Promise.all([
    fetch(`/api/community/posts/${postId}`),
    fetch(`/api/community/posts/${postId}/comments`, {
      headers: getToken() ? { 'Authorization': `Bearer ${getToken()}` } : {}
    })
  ]);
  if (pRes.ok) post.value = await pRes.json();
  if (cRes.ok) comments.value = await cRes.json();
};

const addComment = async () => {
  if (!newComment.value || !getToken()) return;
  await fetch(`/api/community/posts/${postId}/comments`, {
    method: 'POST',
    headers: getAuthHeaders(),
    body: JSON.stringify({ content: newComment.value })
  });
  newComment.value = '';
  loadData();
};

const deleteComment = async (id) => {
  if (!confirm('삭제하시겠습니까?')) return;
  await fetch(`/api/community/comments/${id}`, {
    method: 'DELETE',
    headers: getAuthHeaders()
  });
  loadData();
};

onMounted(loadData);
</script>

<style scoped>
.page-container { max-width: 800px; margin: 20px auto; padding: 20px; background: white; border-radius: 8px; }
.content { font-size: 1.1em; padding: 20px 0; min-height: 100px; }
.comment-item { padding: 8px 0; border-bottom: 1px solid #eee; }
.del-btn { font-size: 0.8em; color: red; border: none; background: none; cursor: pointer; margin-left: 10px; }
.comment-input { margin-top: 15px; display: flex; gap: 5px; }
.comment-input input { flex: 1; padding: 8px; }
</style>