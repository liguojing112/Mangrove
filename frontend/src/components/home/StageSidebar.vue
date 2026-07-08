<template>
  <div class="flex flex-col gap-4 h-full">
    <!-- Upper: Featured video -->
    <div class="card aspect-video rounded-2xl overflow-hidden group cursor-pointer relative">
      <div class="absolute inset-0 bg-gradient-to-br from-mangrove-400 to-mangrove-700 transition-transform duration-200 group-hover:scale-105" />
      <div class="absolute inset-0 flex items-center justify-center">
        <div class="w-14 h-14 rounded-full bg-white/20 backdrop-blur-sm flex items-center justify-center">
          <Play class="w-6 h-6 text-white ml-0.5" fill="white" />
        </div>
      </div>
      <div class="absolute top-3 right-3 bg-black/40 backdrop-blur-sm text-white text-xs px-2 py-1 rounded-lg">
        03:28
      </div>
      <div class="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/60 to-transparent p-4">
        <h3 class="text-white font-semibold text-sm">最新舞台直拍</h3>
      </div>
    </div>

    <!-- Lower: Video list -->
    <div class="flex-1 min-h-0">
      <h3 class="font-semibold text-sm text-gray-600 mb-3">绝美舞台</h3>
      <div class="max-h-[300px] overflow-y-auto scrollbar-thin space-y-1">
        <div
          v-for="(video, i) in videos"
          :key="i"
          class="flex items-center gap-3 p-2 rounded-xl cursor-pointer transition-colors duration-150"
          :class="activeIndex === i ? 'bg-mangrove-50 border-l-2 border-mangrove-600' : 'hover:bg-mangrove-50'"
          @click="activeIndex = i"
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
import { ref } from 'vue'
import { Play } from 'lucide-vue-next'

const activeIndex = ref(0)

const videos = [
  { title: '直拍 - 樱花树下', date: '03:28' },
  { title: '舞台 - 月光奏鸣', date: '04:12' },
  { title: '直拍 - 星河漫步', date: '03:45' },
  { title: '舞台 - 热力全开', date: '02:58' },
  { title: '直拍 - 清风徐来', date: '03:15' },
  { title: '舞台 - 夜之光', date: '04:02' },
  { title: '直拍 - 夏日限定', date: '03:33' },
  { title: '舞台 - 梦想起航', date: '03:50' }
]
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
