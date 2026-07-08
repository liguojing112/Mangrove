<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <h1 class="section-title mb-8">影像存档</h1>

    <div v-if="loading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-8 w-8 border-2 border-mangrove-700 border-t-transparent"></div>
    </div>

    <div v-else-if="videos.length === 0" class="flex flex-col items-center justify-center py-20 text-gray-400">
      <Video class="w-12 h-12 mb-3" />
      <p class="text-sm">暂无视频</p>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="video in videos"
        :key="video.id"
        class="card-hover overflow-hidden group cursor-pointer"
        @click="playVideo(video)"
      >
        <div class="relative aspect-video bg-mangrove-100 rounded-xl overflow-hidden">
          <!-- 直接用 video 标签展示缩略图帧 -->
          <video
            v-if="video.fileUrl"
            :src="video.fileUrl"
            class="w-full h-full object-cover"
            preload="metadata"
            muted
            @error="$event.target.style.display='none'"
          />
          <div class="absolute inset-0 flex items-center justify-center">
            <div class="w-12 h-12 rounded-full bg-mangrove-700/80 flex items-center justify-center group-hover:scale-110 transition-transform">
              <Play class="w-5 h-5 text-white ml-0.5" />
            </div>
          </div>
        </div>
        <div class="p-4">
          <h3 class="font-semibold text-gray-900 text-sm mb-1">{{ video.title }}</h3>
          <p class="text-xs text-gray-500">{{ formatDate(video.createdAt) }}</p>
        </div>
      </div>
    </div>

    <!-- 视频播放弹窗 -->
    <div v-if="showPlayer" class="fixed inset-0 bg-black/80 z-50 flex items-center justify-center p-4" @click.self="closePlayer">
      <div class="relative w-full max-w-4xl">
        <button class="absolute -top-10 right-0 text-white text-sm hover:text-gray-300" @click="closePlayer">关闭 ✕</button>
        <div class="bg-black rounded-xl overflow-hidden">
          <video
            ref="playerRef"
            :src="currentVideo?.fileUrl"
            class="w-full max-h-[80vh]"
            controls
            autoplay
          ></video>
        </div>
        <p class="text-white text-sm mt-2 font-medium">{{ currentVideo?.title }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Play, Video } from 'lucide-vue-next'

const videos = ref([])
const loading = ref(true)
const showPlayer = ref(false)
const currentVideo = ref(null)
const playerRef = ref(null)

onMounted(async () => {
  try {
    const res = await fetch('/api/content?category=VIDEO')
    const json = await res.json()
    if (json.code === 200) {
      videos.value = json.data || []
    }
  } catch {
    videos.value = []
  } finally {
    loading.value = false
  }
})

function playVideo(video) {
  currentVideo.value = video
  showPlayer.value = true
}

function closePlayer() {
  showPlayer.value = false
  currentVideo.value = null
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return dateStr.substring(0, 10)
}
</script>
