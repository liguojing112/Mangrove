<template>
  <div class="relative overflow-hidden min-h-screen" :style="pageBgStyle">
    <!-- 背景遮罩：确保内容可读 -->
    <div v-if="bgUrl" class="absolute inset-0 bg-black/40 pointer-events-none" />
    <!-- 背景装饰：漂浮音符和星星 -->
    <div class="absolute inset-0 pointer-events-none overflow-hidden" aria-hidden="true">
      <!-- 音符 🎵 -->
      <span class="absolute text-mangrove-300/20 text-6xl animate-float-slow select-none" style="top:8%;left:5%">🎵</span>
      <span class="absolute text-mangrove-400/15 text-5xl animate-float-medium select-none" style="top:15%;right:8%">🎶</span>
      <span class="absolute text-mangrove-300/15 text-7xl animate-float-fast select-none" style="top:40%;left:2%">♪</span>
      <span class="absolute text-mangrove-200/15 text-4xl animate-float-slow select-none" style="top:55%;right:5%">♫</span>
      <span class="absolute text-mangrove-400/10 text-6xl animate-float-medium select-none" style="top:70%;left:8%">🎼</span>
      <!-- 星星 ✨ -->
      <span class="absolute text-amber-400/20 text-4xl animate-twinkle select-none" style="top:10%;left:15%">✨</span>
      <span class="absolute text-amber-300/15 text-3xl animate-twinkle-delayed select-none" style="top:25%;right:15%">⭐</span>
      <span class="absolute text-amber-400/15 text-5xl animate-twinkle select-none" style="top:50%;right:3%">✨</span>
      <span class="absolute text-mangrove-300/10 text-3xl animate-twinkle-delayed select-none" style="top:80%;left:12%">⭐</span>
      <span class="absolute text-mangrove-200/15 text-4xl animate-float-fast select-none" style="top:35%;left:20%">🎵</span>
      <span class="absolute text-amber-400/10 text-2xl animate-twinkle select-none" style="top:65%;right:10%">✨</span>
    </div>

  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-[10px] pb-16 relative z-10">
    <!-- 公告栏 -->
    <div v-if="showNotice && notices.length > 0" class="mb-8 backdrop-blur-md bg-black/50 border border-white/10 rounded-2xl px-5 py-3 flex items-center gap-3">
      <span class="text-lg shrink-0">📢</span>
      <div class="flex-1 min-w-0 overflow-hidden">
        <p class="text-sm text-white/80 truncate">
          <span class="text-amber-400 font-medium">公告：</span>{{ notices[currentNotice] }}
        </p>
      </div>
      <div class="flex items-center gap-2 shrink-0">
        <button @click="manualPrev" class="p-1 rounded hover:bg-gray-700 text-gray-500 hover:text-gray-300 transition-colors">
          <ChevronLeft :size="14" />
        </button>
        <button @click="manualNext" class="p-1 rounded hover:bg-gray-700 text-gray-500 hover:text-gray-300 transition-colors">
          <ChevronRight :size="14" />
        </button>
        <button @click="showNotice = false" class="p-1 rounded hover:bg-gray-700 text-gray-500 hover:text-gray-300 transition-colors ml-1">
          <X :size="14" />
        </button>
      </div>
    </div>

    <!-- Top section: Carousel + Sidebar -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 min-h-[520px]">
      <div class="lg:col-span-2">
        <PhotoCarousel />
      </div>
      <div class="lg:col-span-1">
        <StageSidebar />
      </div>
    </div>

    <!-- Calendar bar -->
    <div class="mt-8">
      <CalendarBar />
    </div>

    <!-- Feature cards -->
    <div class="mt-12 grid grid-cols-1 md:grid-cols-3 gap-6">
      <router-link to="/artists" class="backdrop-blur-sm bg-white/80 dark:bg-gray-900/80 border border-gray-200/50 rounded-2xl p-6 text-center block transition-shadow hover:shadow-lg hover:shadow-mangrove-500/10">
        <Users class="w-10 h-10 text-mangrove-600 mx-auto mb-4" />
        <h3 class="font-semibold text-pink-300">艺人档案</h3>
        <p class="text-sm text-gray-500 mt-2">追踪你喜爱的艺人，了解他们的每一刻</p>
      </router-link>
      <router-link to="/works" class="backdrop-blur-sm bg-white/80 dark:bg-gray-900/80 border border-gray-200/50 rounded-2xl p-6 text-center block transition-shadow hover:shadow-lg hover:shadow-mangrove-500/10">
        <PenTool class="w-10 h-10 text-mangrove-600 mx-auto mb-4" />
        <h3 class="font-semibold text-pink-300">创作者殿堂</h3>
        <p class="text-sm text-gray-500 mt-2">路透、修图、剪辑 — 展现你的创作才华</p>
      </router-link>
      <router-link to="/tree" class="backdrop-blur-sm bg-white/80 dark:bg-gray-900/80 border border-gray-200/50 rounded-2xl p-6 text-center block transition-shadow hover:shadow-lg hover:shadow-mangrove-500/10">
        <TreePine class="w-10 h-10 text-mangrove-600 mx-auto mb-4" />
        <h3 class="font-semibold text-pink-300">芒果树养成</h3>
        <p class="text-sm text-gray-500 mt-2">每日签到，培育专属芒果树</p>
      </router-link>
    </div>

    <!-- Stats section -->
    <div class="mt-12 mb-8 grid grid-cols-2 md:grid-cols-4 gap-6 backdrop-blur-sm bg-white/60 dark:bg-gray-900/60 rounded-2xl py-6 px-4 border border-gray-200/30">
      <div class="text-center">
        <div class="text-3xl font-bold text-mangrove-700">{{ formatCount(siteStats.fanCount) }}</div>
        <div class="text-sm text-gray-500 mt-1">注册粉丝</div>
      </div>
      <div class="text-center">
        <div class="text-3xl font-bold text-mangrove-700">{{ siteStats.artistCount ?? '--' }}</div>
        <div class="text-sm text-gray-500 mt-1">收录艺人</div>
      </div>
      <div class="text-center">
        <div class="text-3xl font-bold text-mangrove-700">{{ formatCount(siteStats.workCount) }}</div>
        <div class="text-sm text-gray-500 mt-1">创作者作品</div>
      </div>
      <div class="text-center">
        <div class="text-3xl font-bold text-mangrove-700">{{ formatCount(siteStats.merchandiseCount) }}</div>
        <div class="text-sm text-gray-500 mt-1">周边图鉴</div>
      </div>
    </div>

    <!-- Music player (fixed position, renders here) -->
    <MusicPlayer />
  </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { Users, PenTool, TreePine, X, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import PhotoCarousel from '@/components/home/PhotoCarousel.vue'
import StageSidebar from '@/components/home/StageSidebar.vue'
import CalendarBar from '@/components/home/CalendarBar.vue'
import MusicPlayer from '@/components/home/MusicPlayer.vue'

// 站点统计
const siteStats = ref({ fanCount: 0, artistCount: 0, workCount: 0, merchandiseCount: 0 })

// 首页背景图
const bgUrl = ref('')
const pageBgStyle = computed(() => {
  if (!bgUrl.value) return {}
  return {
    backgroundImage: `url(${bgUrl.value})`,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    backgroundRepeat: 'no-repeat'
  }
})

async function fetchHomepageConfig() {
  try {
    const res = await fetch('/api/public/config/homepage')
    const json = await res.json()
    if (json.code === 200 && json.data?.backgroundUrl) {
      bgUrl.value = json.data.backgroundUrl
    }
  } catch {}
}

async function fetchStats() {
  try {
    const res = await fetch('/api/public/stats')
    const json = await res.json()
    if (json.code === 200) siteStats.value = json.data
  } catch {}
}

function formatCount(n) {
  if (n == null) return '--'
  if (n >= 10000) return (n / 10000).toFixed(1).replace(/\.0$/, '') + 'w+'
  if (n >= 1000) return (n / 1000).toFixed(1).replace(/\.0$/, '') + 'k'
  return String(n)
}

// 公告栏
const showNotice = ref(true)
const currentNotice = ref(0)
const notices = ref([])

async function fetchNotices() {
  try {
    const res = await fetch('/api/public/announcements')
    const json = await res.json()
    if (json.code === 200 && json.data) {
      notices.value = json.data.map(a => a.title)
    }
  } catch (e) {
    console.error('获取公告失败:', e)
  }
}

function nextNotice() { currentNotice.value = (currentNotice.value + 1) % notices.value.length }
function prevNotice() { currentNotice.value = (currentNotice.value - 1 + notices.value.length) % notices.value.length }

// 自动轮播：每 5 秒切换下一条
let noticeTimer = null
const NOTICE_INTERVAL = 5000
function startNoticeRotation() {
  stopNoticeRotation()
  if (notices.value.length > 1) {
    noticeTimer = setInterval(nextNotice, NOTICE_INTERVAL)
  }
}
function stopNoticeRotation() {
  if (noticeTimer) { clearInterval(noticeTimer); noticeTimer = null }
}
// 手动切换后重置计时，避免刚切完又被自动切走
function manualNext() { nextNotice(); startNoticeRotation() }
function manualPrev() { prevNotice(); startNoticeRotation() }

onMounted(async () => {
  fetchStats()
  fetchHomepageConfig()
  await fetchNotices()
  startNoticeRotation()
})
onBeforeUnmount(stopNoticeRotation)
</script>

<style scoped>
@keyframes float-slow {
  0%, 100% { transform: translateY(0) rotate(0deg); opacity: 0.15; }
  50% { transform: translateY(-20px) rotate(5deg); opacity: 0.28; }
}
@keyframes float-medium {
  0%, 100% { transform: translateY(0) rotate(0deg); opacity: 0.12; }
  50% { transform: translateY(-15px) rotate(-3deg); opacity: 0.22; }
}
@keyframes float-fast {
  0%, 100% { transform: translateY(0) rotate(0deg); opacity: 0.1; }
  50% { transform: translateY(-10px) rotate(3deg); opacity: 0.2; }
}
@keyframes twinkle {
  0%, 100% { opacity: 0.1; transform: scale(1); }
  50% { opacity: 0.35; transform: scale(1.2); }
}
.animate-float-slow { animation: float-slow 8s ease-in-out infinite; }
.animate-float-medium { animation: float-medium 6s ease-in-out infinite; }
.animate-float-fast { animation: float-fast 4s ease-in-out infinite; }
.animate-twinkle { animation: twinkle 3s ease-in-out infinite; }
.animate-twinkle-delayed { animation: twinkle 4s ease-in-out 1.5s infinite; }
</style>
