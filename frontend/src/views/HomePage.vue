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

    <!-- 大横幅照片轮播 -->
    <div class="mb-4">
      <PhotoCarousel :slideTo="slideToCarousel" />
    </div>

    <!-- 三个视频播放窗口 -->
    <div class="grid grid-cols-3 gap-2 sm:gap-3 mb-6">
      <div v-for="(v, i) in videoCards" :key="i"
        class="overflow-hidden bg-gray-900 aspect-video relative">
        <!-- Video cover poster -->
        <div v-if="!v.url || videoCoverShowing[i]" class="absolute inset-0 z-10 flex flex-col items-center justify-center cursor-pointer transition-opacity duration-300"
          :style="{ background: v.background }"
          @click.stop="playCardVideo(i)">
          <component :is="v.icon" :size="32" class="text-white/50 mb-1" />
          <span class="text-white/80 text-xs font-medium">{{ v.kicker }}</span>
          <span class="text-white/60 text-[10px] mt-0.5">{{ v.heading }}</span>
        </div>
        <video v-if="v.url" :ref="el => videoRefs[i] = el" :src="v.url" muted playsinline webkit-playsinline preload="auto"
          class="w-full h-full object-cover relative z-0"
          @loadeddata="videoCoverShowing[i] = false"
          @error="videoCoverShowing[i] = true" />
      </div>
    </div>

    <!-- 绝美舞台（极简列表） -->
    <div class="mb-6">
      <h3 class="font-semibold text-xs text-gray-500 mb-2">绝美舞台</h3>
      <div class="space-y-1">
        <div v-for="(v, i) in homepageVideos" :key="'stage-'+i"
          class="flex items-center gap-2 rounded-lg bg-gray-900 cursor-pointer h-12 px-2"
          :class="activeVideoIdx === i ? 'ring-1 ring-white/40' : ''"
          @click.stop="playCardVideo(i)"
          @touchend.stop.prevent="playCardVideo(i)">
          <div class="w-16 h-full flex items-center justify-center flex-shrink-0 rounded"
            :style="{ background: videoCards[i]?.background || '#374151' }">
            <Play :size="14" class="text-white/60" fill="white" />
          </div>
          <span class="text-xs text-white truncate flex-1">{{ v.title }}</span>
          <span class="text-[10px] text-white/30">{{ String(i + 1).padStart(2, '0') }}</span>
        </div>
      </div>
    </div>

    <!-- Calendar bar -->
    <div class="mt-8">
      <CalendarBar />
    </div>

    <!-- Feature cards -->
    <div class="mt-12 grid grid-cols-2 md:grid-cols-4 gap-6">
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
      <router-link to="/community" class="backdrop-blur-sm bg-white/80 dark:bg-gray-900/80 border border-gray-200/50 rounded-2xl p-6 text-center block transition-shadow hover:shadow-lg hover:shadow-mangrove-500/10">
        <Mail class="w-10 h-10 text-mangrove-600 mx-auto mb-4" />
        <h3 class="font-semibold text-pink-300">信箱</h3>
        <p class="text-sm text-gray-500 mt-2">给艺人写一封信，留下你的心声</p>
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

    <!-- 右下角信箱按钮（可拖动） -->
    <div
      class="fixed z-40 w-[33vw] h-[33vw] max-w-[140px] max-h-[140px] rounded-full bg-transparent flex items-center justify-center shadow-none active:scale-95 cursor-grab select-none"
      :style="mailStyle"
      @mousedown="startDrag($event, 'mail')"
      @touchstart.stop="startDrag($event, 'mail')"
    >
      <img src="/mail-btn.png" class="w-full h-full rounded-full object-cover pointer-events-none" />
    </div>
  </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { Users, PenTool, TreePine, X, ChevronLeft, ChevronRight, Video, Film, Music, Sparkles, Star, Play, Mail } from 'lucide-vue-next'
import PhotoCarousel from '@/components/home/PhotoCarousel.vue'
import CalendarBar from '@/components/home/CalendarBar.vue'
import MusicPlayer from '@/components/home/MusicPlayer.vue'

const router = useRouter()

// ── 可拖动按钮逻辑 ──
const dragState = ref({ active: false, which: '', startX: 0, startY: 0, initX: 0, initY: 0, moved: false })

const mailPos = ref({ x: window.innerWidth - 64, y: window.innerHeight - 178 })
const mailStyle = computed(() => ({
  left: mailPos.value.x + 'px',
  top: mailPos.value.y + 'px',
  transition: dragState.value.active && dragState.value.which === 'mail' ? 'none' : 'all 0.2s ease',
}))

function startDrag(e, which) {
  const pos = which === 'mail' ? mailPos.value : null
  if (!pos) return
  const clientX = e.touches ? e.touches[0].clientX : e.clientX
  const clientY = e.touches ? e.touches[0].clientY : e.clientY
  dragState.value = { active: true, which, startX: clientX, startY: clientY, initX: pos.x, initY: pos.y, moved: false }
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', endDrag)
  document.addEventListener('touchmove', onDrag, { passive: false })
  document.addEventListener('touchend', endDrag)
}

function onDrag(e) {
  if (!dragState.value.active) return
  e.preventDefault()
  const clientX = e.touches ? e.touches[0].clientX : e.clientX
  const clientY = e.touches ? e.touches[0].clientY : e.clientY
  const dx = clientX - dragState.value.startX
  const dy = clientY - dragState.value.startY
  if (Math.abs(dx) > 5 || Math.abs(dy) > 5) dragState.value.moved = true
  const pos = dragState.value.which === 'mail' ? mailPos.value : null
  if (pos) {
    pos.x = Math.max(0, Math.min(window.innerWidth - 48, dragState.value.initX + dx))
    pos.y = Math.max(0, Math.min(window.innerHeight - 48, dragState.value.initY + dy))
  }
}

function endDrag() {
  const wasMoved = dragState.value.moved
  dragState.value.active = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', endDrag)
  document.removeEventListener('touchmove', onDrag)
  document.removeEventListener('touchend', endDrag)
  if (!wasMoved) {
    // 没有拖动，视为点击
    setTimeout(() => {
      if (dragState.value.which === 'mail') router.push('/community')
    }, 10)
  } else {
    localStorage.setItem('mangrove_mail_pos', JSON.stringify(mailPos.value))
  }
}

// 读取保存的位置
function loadPositions() {
  try {
    const saved = JSON.parse(localStorage.getItem('mangrove_mail_pos'))
    if (saved) mailPos.value = saved
  } catch {}
}
loadPositions()

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

// 首页视频
const homepageVideos = ref([
  { title: '舞台瞬间', url: '' },
  { title: '路透精选', url: '' },
  { title: '日常碎片', url: '' },
])

const slideToCarousel = ref(-1)
const activeVideoIdx = ref(-1)
const videoRefs = ref([])
const videoCoverShowing = ref([true, true, true])

function playCardVideo(i) {
  activeVideoIdx.value = i
  slideToCarousel.value = i
  videoCoverShowing.value[i] = false
  const v = videoRefs.value[i]
  if (v) {
    v.currentTime = 0
    v.play().catch(() => {})
  }
}

const videoCards = computed(() => {
  const defs = [
    { kicker: 'STAGE', heading: '舞台瞬间', subtitle: '聚光灯下的每一个精彩', icon: Sparkles, background: 'linear-gradient(135deg, #0f2b1a 0%, #1b6842 50%, #3ec97a 100%)', shadow: '0 8px 24px rgba(15,71,43,0.4)', glow: 'rgba(138,255,164,0.4)' },
    { kicker: 'BEHIND', heading: '路透精选', subtitle: '镜头背后的温暖瞬间', icon: Star, background: 'linear-gradient(135deg, #1a1035 0%, #3b2d6e 50%, #7c5ce0 100%)', shadow: '0 8px 24px rgba(60,35,110,0.4)', glow: 'rgba(160,140,255,0.4)' },
    { kicker: 'DAILY', heading: '日常碎片', subtitle: '记录生活中的点点滴滴', icon: Music, background: 'linear-gradient(135deg, #3d1e0f 0%, #b85c1e 50%, #f59e4b 100%)', shadow: '0 8px 24px rgba(150,70,20,0.4)', glow: 'rgba(255,180,100,0.4)' },
  ]
  return defs.map((def, i) => ({
    ...def,
    url: homepageVideos.value[i]?.url || '',
    title: homepageVideos.value[i]?.title || def.heading,
  }))
})

async function fetchHomepageVideos() {
  try {
    // 直接从文件列表获取视频
    const res = await fetch('/api/files/list').catch(() => null)
    if (res?.ok) {
      const j = await res.json()
      if (j.code === 200 && j.data?.length) {
        const videoFiles = j.data.filter(f => /\.(mp4|webm|mov)$/i.test(f.filename || ''))
	        if (videoFiles.length > 0) {
	          homepageVideos.value = [
	            { title: '舞台瞬间', url: videoFiles[0]?.url || '' },
	            { title: '路透精选', url: videoFiles[1]?.url || videoFiles[0]?.url || '' },
	            { title: '日常碎片', url: videoFiles[2]?.url || videoFiles[0]?.url || '' },
	          ]
	          videoCoverShowing.value = [true, true, true]
	        }
      }
    }
  } catch {}
}

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
  fetchHomepageVideos()
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
