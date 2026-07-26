<template>
  <main class="relative min-h-screen bg-gray-50 pb-16 pt-14">
    <!-- 背景装饰：五线谱纹理 + 浮动音符 -->
    <div class="absolute inset-0 pointer-events-none select-none z-0" aria-hidden="true">
      <svg class="absolute top-0 left-0 w-full h-full opacity-[0.08]" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <pattern id="staff-lines-videos" x="0" y="0" width="100%" height="150" patternUnits="userSpaceOnUse">
            <line x1="0" y1="20" x2="100%" y2="20" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="45" x2="100%" y2="45" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="70" x2="100%" y2="70" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="95" x2="100%" y2="95" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="120" x2="100%" y2="120" stroke="#2E8B57" stroke-width="1.5"/>
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill="url(#staff-lines-videos)"/>
      </svg>
      <div class="floating-note note-1">♪</div>
      <div class="floating-note note-2">♫</div>
      <div class="floating-note note-3">♬</div>
      <div class="floating-note note-4">♩</div>
      <div class="floating-note note-5">♪</div>
      <div class="floating-note note-6">𝄞</div>
      <div class="floating-note note-7">♫</div>
      <div class="floating-note note-8">♬</div>
      <div class="floating-note note-9">♪</div>
      <div class="floating-note note-10">𝄢</div>
      <div class="floating-note note-11">♫</div>
      <div class="floating-note note-12">♩</div>
      <div class="floating-note note-13">♬</div>
      <div class="floating-note note-14">♪</div>
      <div class="floating-note note-15">𝄞</div>
    </div>

    <div class="relative z-10 mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
      <header class="mb-7 flex flex-col gap-5 border-b border-gray-200 pb-7 md:flex-row md:items-end md:justify-between">
        <div>
          <p class="text-sm font-medium text-mangrove-700">小芒即时影像</p>
          <h1 class="mt-1 text-3xl font-bold text-gray-900">短视频</h1>
          <p class="mt-2 text-sm text-gray-500">记录舞台与日常片段</p>
        </div>
        <label class="flex h-11 w-full items-center rounded-lg border border-gray-200 bg-white px-3 shadow-sm md:w-80">
          <Search class="h-4 w-4 shrink-0 text-gray-400" />
          <input v-model="searchQuery" type="search" placeholder="搜索短视频" class="min-w-0 flex-1 bg-transparent px-3 text-sm outline-none" />
        </label>
      </header>

      <div class="flex justify-center mb-7">
        <div class="flex w-max gap-2 flex-wrap justify-center" @touchstart.stop @touchend.stop>
          <button v-for="c in displayCategories" :key="c.value" type="button" class="shrink-0 rounded-full border px-4 py-2 text-sm font-medium transition-colors"
            :class="activeCategory===c.value ? 'border-mangrove-600 bg-mangrove-600 text-white' : 'border-gray-200 bg-white text-gray-500 hover:border-mangrove-300'"
            @click="activeCategory=c.value">{{ c.label }} <span class="ml-1 opacity-70">{{ c.count }}</span></button>
        </div>
      </div>

      <!-- 随机播放 -->
      <div class="flex justify-center mb-7">
        <button v-if="!randomVideo" type="button" class="flex items-center gap-2 rounded-full border border-mangrove-300 bg-white px-5 py-2.5 text-sm font-medium text-mangrove-700 shadow-sm transition-all hover:bg-mangrove-50 hover:shadow-md"
          @click="playRandom">
          <Shuffle class="w-4 h-4" /> 随机播放
        </button>
        <div v-else class="relative mx-auto rounded-2xl overflow-hidden bg-black shadow-2xl">
          <video :src="randomVideo.thumbnailUrl" controls autoplay class="block max-h-[80vh] max-w-full bg-black" />
          <div class="absolute top-3 right-3 flex gap-2 z-20">
            <button type="button" class="flex items-center gap-1.5 rounded-full bg-white/90 px-3 py-1.5 text-xs font-medium text-gray-700 shadow-sm hover:bg-white transition-colors" @click.stop="playRandom">
              <Shuffle class="w-3 h-3" /> 换一个
            </button>
            <button type="button" class="flex items-center justify-center w-8 h-8 rounded-full bg-white/90 text-gray-700 shadow-sm hover:bg-white transition-colors" @click.stop="randomVideo = null">
              <X class="w-4 h-4" />
            </button>
          </div>
          <div class="absolute bottom-0 inset-x-0 bg-gradient-to-t from-black/80 to-transparent px-4 pb-3 pt-8">
            <p class="text-sm font-medium text-white truncate">{{ randomVideo.title || '未命名' }}</p>
          </div>
        </div>
      </div>

      <div v-if="loading" class="py-24 text-center text-sm text-gray-400"><Loader2 class="mx-auto mb-3 h-7 w-7 animate-spin" />加载中...</div>
      <div v-else-if="displayedVideos.length === 0" class="py-24 text-center text-sm text-gray-400"><Video class="mx-auto mb-3 h-10 w-10 text-gray-300" />没有找到短视频</div>

      <!-- 保留原有两列瀑布流布局 -->
      <section v-else class="columns-2 gap-3" aria-label="短视频列表">
        <button v-for="v in displayedVideos" :key="v.id" type="button" class="video-card group relative mb-3 block w-full break-inside-avoid overflow-hidden rounded-lg bg-black text-left shadow-sm transition-all duration-300 hover:-translate-y-2 hover:shadow-xl hover:shadow-green-500/20" @click="openVideo(v)">
          <img v-if="v.coverUrl" :src="v.coverUrl" class="max-h-[400px] w-full bg-black object-cover transition-transform duration-300 group-hover:scale-[1.10]" loading="lazy" @error="e => e.target.style.display='none'" />
          <video v-else :src="v.thumbnailUrl" muted class="max-h-[400px] w-full bg-black object-cover transition-transform duration-300 group-hover:scale-[1.10]" preload="metadata" @loadeddata="$event.target.currentTime=0.5" />
          <span class="absolute inset-0 bg-black/5 transition-colors group-hover:bg-black/20"></span>
          <span class="absolute left-1/2 top-1/2 flex h-11 w-11 -translate-x-1/2 -translate-y-1/2 items-center justify-center rounded-full bg-white/90 text-gray-900 shadow-lg transition-transform group-hover:scale-105">
            <Play class="h-4 w-4 fill-current" />
          </span>
          <span v-if="v.categoryLabel" class="absolute left-2 top-2 rounded bg-black/60 px-2 py-1 text-[10px] font-medium text-white backdrop-blur-sm">{{ v.categoryLabel }}</span>
          <span class="absolute inset-x-0 bottom-0 bg-gradient-to-t from-black/90 via-black/55 to-transparent px-3 pb-3 pt-10">
            <span class="block truncate text-sm font-medium text-white">{{ v.title || '未命名' }}</span>
            <span class="mt-1 block text-[11px] text-white/65">{{ v.date || '小芒短视频' }}</span>
          </span>
        </button>
      </section>
    </div>

    <div v-if="playingVideo" class="fixed inset-0 z-[120] flex items-center justify-center bg-black/95 p-4" @click.self="playingVideo=null">
      <button type="button" class="absolute right-4 top-4 p-2 text-white/80 hover:text-white" aria-label="关闭视频" @click="playingVideo=null"><X class="h-6 w-6" /></button>
      <video :src="playingVideo" controls autoplay class="max-h-[90vh] max-w-[96vw]" />
    </div>
  </main>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Loader2, Play, Search, Shuffle, Video, X } from 'lucide-vue-next'

const route = useRoute()
const searchQuery = ref('')
const activeCategory = ref('all')
const loading = ref(true)
const videos = ref([])
const backendCats = ref([])
const playingVideo = ref(null)
const randomVideo = ref(null)

function playRandom() {
  const list = filteredVideos.value
  if (list.length === 0) return
  const idx = Math.floor(Math.random() * list.length)
  randomVideo.value = list[idx]
}

function openVideo(v) {
  playingVideo.value = v.thumbnailUrl || v.url || v.fileUrl
}

const displayCategories = computed(() => {
  const counts = {}
  videos.value.forEach(v => { const c = v.categoryLabel || '未分类'; if (c) counts[c] = (counts[c] || 0) + 1 })
  backendCats.value.forEach(c => { if (!counts[c]) counts[c] = 0 })
  const list = [{ value: 'all', label: '全部', count: videos.value.length }]
  Object.entries(counts).forEach(([k, v]) => list.push({ value: k, label: k, count: v }))
  return list
})

const filteredVideos = computed(() => {
  let list = videos.value
  if (activeCategory.value !== 'all') list = list.filter(v => (v.categoryLabel || '未分类') === activeCategory.value)
  if (searchQuery.value.trim()) { const q = searchQuery.value.trim().toLowerCase(); list = list.filter(v => (v.title||'').toLowerCase().includes(q)) }
  return list
})
const displayedVideos = computed(() => filteredVideos.value.slice(0, 24))

function catClass(c) {
  const m = { '路透':'bg-blue-50 text-blue-700','活动':'bg-purple-50 text-purple-700','日常':'bg-green-50 text-green-700','舞台':'bg-red-50 text-red-700','未分类':'bg-gray-50 text-gray-500' }
  return m[c] || 'bg-mangrove-50 text-mangrove-700'
}

async function loadData() {
  loading.value = true
  try {
    const metaRes = await fetch('/api/files/meta?status=1').catch(() => null)
    if (metaRes && metaRes.ok) {
      const j = await metaRes.json()
      if (j.code === 200 && j.data) {
        videos.value = j.data.filter(m => /\.(mp4|webm|mov|avi)$/i.test(m.filename||'')).map((m,i) => {
          return { id: 'v'+i, title: m.displayName || (m.filename||'').replace(/\.[^.]+$/,''), thumbnailUrl: m.url, coverUrl: m.coverUrl || '', categoryLabel: m.category||'', date: m.photoDate || '', duration: '' }
        })
      }
    }
    const catRes = await fetch('/api/files/categories?type=video').catch(() => null)
    if (catRes && catRes.ok) { const cj = await catRes.json(); if (cj.data) backendCats.value = cj.data }
  } catch {} finally { loading.value = false }
}

watch(() => route.path, () => { if (route.path === '/videos') loadData() })
onMounted(loadData)
</script>

<style scoped>
/* === 浮动音符 === */
.floating-note {
  position: fixed;
  color: rgba(46, 139, 87, 0.3);
  pointer-events: none;
  user-select: none;
  z-index: 9999;
  text-shadow: 0 0 20px rgba(46, 139, 87, 0.4);
}
.note-1 { top: 8%; left: 5%; animation: floatNote 4s ease-in-out infinite; font-size: 2.5rem; }
.note-2 { top: 5%; right: 12%; animation: floatNote 5s ease-in-out infinite 0.5s; font-size: 2rem; }
.note-3 { top: 15%; left: 18%; animation: floatNote 6s ease-in-out infinite 1s; font-size: 3.5rem; }
.note-4 { top: 12%; right: 5%; animation: floatNote 4.5s ease-in-out infinite 0.3s; font-size: 3rem; }
.note-5 { top: 25%; left: 8%; animation: floatNote 5.5s ease-in-out infinite 1.2s; font-size: 2.5rem; }
.note-6 { top: 20%; right: 18%; animation: floatNote 7s ease-in-out infinite 2s; font-size: 4rem; }
.note-7 { top: 35%; left: 22%; animation: floatNote 5s ease-in-out infinite 0.8s; font-size: 2.8rem; }
.note-8 { top: 30%; right: 8%; animation: floatNote 6.5s ease-in-out infinite 1.5s; font-size: 3.5rem; }
.note-9 { top: 45%; left: 10%; animation: floatNote 4.5s ease-in-out infinite 0.6s; font-size: 2.2rem; }
.note-10 { top: 40%; right: 20%; animation: floatNote 7.5s ease-in-out infinite 2.5s; font-size: 3.5rem; }
.note-11 { top: 55%; left: 25%; animation: floatNote 5.5s ease-in-out infinite 1s; font-size: 3rem; }
.note-12 { top: 60%; right: 10%; animation: floatNote 6s ease-in-out infinite 1.8s; font-size: 2.8rem; }
.note-13 { top: 70%; left: 8%; animation: floatNote 5s ease-in-out infinite 0.4s; font-size: 3.2rem; }
.note-14 { top: 75%; right: 22%; animation: floatNote 7s ease-in-out infinite 2.2s; font-size: 2.5rem; }
.note-15 { top: 85%; left: 30%; animation: floatNote 6.5s ease-in-out infinite 1.5s; font-size: 3.8rem; }

@keyframes floatNote {
  0%, 100% { transform: translate(0, 0) rotate(0deg); opacity: 0.3; }
  25% { transform: translate(50px, -100px) rotate(20deg); opacity: 0.5; }
  50% { transform: translate(-40px, -50px) rotate(-15deg); opacity: 0.35; }
  75% { transform: translate(60px, -120px) rotate(15deg); opacity: 0.45; }
}
</style>
