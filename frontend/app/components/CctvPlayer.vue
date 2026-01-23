<template>
  <div class="video-wrapper">
    <video ref="videoElement" controls muted autoplay playsinline class="cctv-video"></video>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue';
import Hls from 'hls.js';

const props = defineProps({
  src: { type: String, required: true }
});

const videoElement = ref(null);
let hls = null;

const initPlayer = () => {
  if (!props.src || !videoElement.value) return;

  if (Hls.isSupported()) {
    if (hls) hls.destroy();
    hls = new Hls();
    hls.loadSource(props.src);
    hls.attachMedia(videoElement.value);
  } else if (videoElement.value.canPlayType('application/vnd.apple.mpegurl')) {
    videoElement.value.src = props.src;
  }
};

onMounted(initPlayer);
watch(() => props.src, initPlayer); // URL이 바뀌면 영상 교체

onBeforeUnmount(() => {
  if (hls) hls.destroy();
});
</script>

<style scoped>
.video-wrapper { width: 100%; aspect-ratio: 16/9; background: black; border-radius: 8px; overflow: hidden; }
.cctv-video { width: 100%; height: 100%; object-fit: cover; }
</style>