<template>
  <div class="fixed bottom-6 right-6 z-50">
    <!-- Collapsed: floating music note button -->
    <button
      v-if="!isExpanded && musicData"
      class="w-14 h-14 rounded-full bg-mangrove-700 shadow-lg flex items-center justify-center text-white hover:bg-mangrove-800 transition-all duration-200 hover:scale-110"
      :class="{ 'animate-pulse': !hasInteracted }"
      @click="expand"
    >
      <Music class="w-6 h-6" />
    </button>

    <!-- Expanded: music player card -->
    <div
      v-else-if="isExpanded && musicData"
      class="bg-white rounded-2xl shadow-2xl w-72 overflow-hidden transition-all duration-300"
    >
      <!-- Header -->
      <div class="bg-gradient-to-r from-mangrove-700 to-mangrove-600 px-4 py-3 flex items-center justify-between">
        <div class="flex items-center gap-2 min-w-0">
          <Music class="w-4 h-4 text-white/80 flex-shrink-0" />
          <span class="text-sm font-medium text-white truncate">{{ musicData.title || '背景音乐' }}</span>
        </div>
        <button
          class="w-6 h-6 rounded-full hover:bg-white/20 flex items-center justify-center transition-colors"
          @click="collapse"
        >
          <X class="w-4 h-4 text-white" />
        </button>
      </div>

      <!-- Player body -->
      <div class="px-4 py-4">
        <div class="flex items-center gap-3 mb-3">
          <div class="w-12 h-12 rounded-xl bg-gradient-to-br from-mangrove-400 to-mangrove-600 flex items-center justify-center flex-shrink-0">
            <Music class="w-6 h-6 text-white" />
          </div>
          <div class="min-w-0">
            <p class="text-sm font-semibold text-gray-900 truncate">{{ musicData.title || '背景音乐' }}</p>
            <p class="text-xs text-gray-400 mt-0.5">Candice's Mango Ode</p>
          </div>
        </div>

        <div class="flex items-center gap-3">
          <button
            class="w-10 h-10 rounded-full bg-mangrove-600 hover:bg-mangrove-700 text-white flex items-center justify-center flex-shrink-0 transition-colors"
            @click="togglePlay"
          >
            <Pause v-if="globalIsPlaying" class="w-5 h-5" />
            <Play v-else class="w-5 h-5 ml-0.5" />
          </button>
          <div class="flex-1 min-w-0">
            <div class="w-full bg-gray-200 rounded-full h-1.5 cursor-pointer relative" @click="seekTo">
              <div class="bg-mangrove-600 h-1.5 rounded-full transition-all" :style="{ width: progressPercent + '%' }" />
            </div>
            <div class="flex justify-between mt-1">
              <span class="text-[10px] text-gray-400">{{ formatTime(globalCurrentTime) }}</span>
              <span class="text-[10px] text-gray-400">{{ formatTime(globalTotalDuration) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { Music, X, Play, Pause } from 'lucide-vue-next'
import { useAudioPlayer } from '@/composables/useAudioPlayer'

const isExpanded = ref(false)
const hasInteracted = ref(false)
const musicData = ref(null)

const {
  isPlaying: globalIsPlaying,
  currentTime: globalCurrentTime,
  totalDuration: globalTotalDuration,
  currentSong: globalCurrentSong,
  progressPercent,
  playSong,
  togglePlay: globalTogglePlay,
  seek,
  preload,
} = useAudioPlayer()

function expand() {
  isExpanded.value = true
  hasInteracted.value = true
}

function collapse() {
  isExpanded.value = false
}

function togglePlay() {
  if (!musicData.value?.url) return
  if (isCurrentSong()) {
    globalTogglePlay()
  } else {
    playSong({
      url: musicData.value.url,
      name: musicData.value.title || '背景音乐',
      artist: "Candice's Mango Ode",
    })
  }
}

function isCurrentSong() {
  return globalCurrentSong.value?.url === musicData.value?.url
}

function seekTo(e) {
  const rect = e.currentTarget.getBoundingClientRect()
  const ratio = (e.clientX - rect.left) / rect.width
  seek(ratio * 100)
}

function formatTime(s) {
  if (!s || !isFinite(s)) return '0:00'
  const m = Math.floor(s / 60)
  const sec = Math.floor(s % 60)
  return m + ':' + String(sec).padStart(2, '0')
}

// 统一的音乐播放函数
function autoPlayMusic() {
  if (!musicData.value?.url) return
  if (globalIsPlaying.value) return
  playSong({
    url: musicData.value.url,
    name: musicData.value.title || '背景音乐',
    artist: "Candice's Mango Ode",
  })
  localStorage.setItem('mg_music_played', '1')
}

onMounted(async () => {
  // 获取音乐配置
  try {
    const res = await fetch('/api/public/homepage-items/MUSIC')
    const json = await res.json()
    if (json.code === 200 && json.data?.length > 0) {
      musicData.value = json.data[0].extraData || null
      // 只在未播放时预加载，避免打断正在播放的音乐
      if (musicData.value?.url && !globalIsPlaying.value) preload(musicData.value.url)
    }
  } catch {}

  // 首次访问自动播放（桌面端可行）
  if (musicData.value?.url && !localStorage.getItem('mg_music_played')) {
    autoPlayMusic()
  }

  // 监听欢迎页的"进入"事件（用户点击后触发，无障碍播放）
  window.addEventListener('intro-enter', autoPlayMusic)
})

onBeforeUnmount(() => {
  window.removeEventListener('intro-enter', autoPlayMusic)
})
</script>
