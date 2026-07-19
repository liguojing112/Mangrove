<template>
  <div class="flex flex-col gap-4 h-full opacity-75 hover:opacity-100 transition-opacity duration-300">
    <!-- Upper: Featured video -->
    <div class="card aspect-video rounded-2xl overflow-hidden group cursor-pointer relative bg-gray-200">
      <template v-if="activeVideo">
        <video
          :src="activeVideo"
          controls
          autoplay
          class="absolute inset-0 w-full h-full object-cover"
          :key="activeVideo"
        />
      </template>
      <template v-else>
        <div class="absolute inset-0 bg-gradient-to-br from-mangrove-400 to-mangrove-700 transition-transform duration-200 group-hover:scale-105" />
        <div class="absolute inset-0 flex items-center justify-center">
          <div class="w-14 h-14 rounded-full bg-white/20 backdrop-blur-sm flex items-center justify-center">
            <Play class="w-6 h-6 text-white ml-0.5" fill="white" />
          </div>
        </div>
      </template>
      <div v-if="videos[activeIndex]?.date" class="absolute top-3 right-3 bg-black/40 backdrop-blur-sm text-white text-xs px-2 py-1 rounded-lg">
        {{ videos[activeIndex].date }}
      </div>
      <div class="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/60 to-transparent p-4">
        <h3 class="text-white font-semibold text-sm">{{ videos[activeIndex]?.title || '绝美舞台' }}</h3>
      </div>
    </div>

    <!-- Lower: Video list -->
    <div class="flex-1 min-h-0">
      <h3 class="font-semibold text-sm text-gray-600 mb-3">绝美舞台</h3>
      <div class="max-h-[240px] overflow-y-auto scrollbar-thin space-y-1">
        <div
          v-for="(video, i) in videos"
          :key="i"
          class="flex items-center gap-3 p-2 rounded-xl cursor-pointer transition-colors duration-150"
          :class="activeIndex === i ? 'bg-mangrove-50 border-l-2 border-mangrove-600' : 'hover:bg-mangrove-50'"
          @click="playVideo(i)"
        >
          <div class="w-16 h-10 rounded-lg bg-mangrove-200 flex-shrink-0 flex items-center justify-center">
            <Play class="w-4 h-4 text-mangrove-600" />
          </div>
          <div class="min-w-0 flex-1">
            <p class="text-sm font-medium text-gray-800 truncate">{{ video.title }}</p>
            <p class="text-xs text-gray-400">{{ video.date }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Play } from 'lucide-vue-next'

const activeIndex = ref(0)

const fallbackVideos = [
  { title: '直拍 - 樱花树下', date: '03:28' },
  { title: '舞台 - 月光奏鸣', date: '04:12' },
  { title: '直拍 - 星河漫步', date: '03:45' },
  { title: '舞台 - 热力全开', date: '02:58' },
  { title: '直拍 - 清风徐来', date: '03:15' },
  { title: '舞台 - 夜之光', date: '04:02' },
  { title: '直拍 - 夏日限定', date: '03:33' },
  { title: '舞台 - 梦想起航', date: '03:50' }
]

const videos = ref(fallbackVideos)
const activeVideo = ref(null)

onMounted(async () => {
  try {
    const res = await fetch('/api/public/homepage-items/VIDEO')
    const json = await res.json()
    if (json.code === 200 && json.data?.length > 0) {
      videos.value = json.data.map(item => ({
        title: item.extraData?.title || '视频',
        date: item.extraData?.description || '',
        url: item.extraData?.url || ''
      }))
    }
  } catch (e) {
    console.error('获取首页视频失败:', e)
  }
})

// 点击视频时播放该视频，同时设置侧栏顶部预览区
function playVideo(idx) {
  activeIndex.value = idx
  activeVideo.value = videos.value[idx]?.url || null
}
</script>

<style scoped>
.scrollbar-thin::-webkit-scrollbar {
  width: 4px;
}
.scrollbar-thin::-webkit-scrollbar-track {
  background: transparent;
}
.scrollbar-thin::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 9999px;
}
.scrollbar-thin::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}
</style>
