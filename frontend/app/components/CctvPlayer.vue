<template>
  <div class="video-wrapper">
    <video
        ref="vElement"
        controls
        muted
        autoplay
        playsinline
        class="cctv-video"
        disablePictureInPicture
        controlsList="nodownload"
    ></video>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue';

const props = defineProps({
  src: { type: String, required: true }
});

const vElement = ref(null);
let blobUrl = null;

const loadSecureVideo = async (url) => {
  if (!url) return;

  try {
    // 1. 비디오 데이터를 Blob으로 가져옴
    const response = await fetch(url);
    const buffer = await response.blob();

    // 2. 이전 Blob URL 메모리 해제
    if (blobUrl) URL.revokeObjectURL(blobUrl);

    // 3. 임시 Blob URL 생성 (blob:http://localhost:3000/...)
    blobUrl = URL.createObjectURL(buffer);

    // 4. 비디오 엘리먼트에 할당
    if (vElement.value) {
      vElement.value.src = blobUrl;
    }
  } catch (e) {
    console.error("Secure load failed");
  }
};

onMounted(() => loadSecureVideo(props.src));

// URL 변경 감지 시 재로드
watch(() => props.src, (newUrl) => loadSecureVideo(newUrl));

onBeforeUnmount(() => {
  if (blobUrl) URL.revokeObjectURL(blobUrl);
});
</script>

<style scoped>
.video-wrapper { width: 100%; aspect-ratio: 16/9; background: black; border-radius: 8px; overflow: hidden; }
.cctv-video { width: 100%; height: 100%; object-fit: cover; }
</style>