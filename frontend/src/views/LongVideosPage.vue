<template>
  <main class="min-h-screen bg-gray-50 pb-20">
    <!-- Hero Video Player -->
    <section class="pt-20 pb-6">
      <div class="max-w-5xl mx-auto px-4">
        <div v-if="heroVideo" class="relative overflow-hidden bg-black shadow-2xl border-2 border-gray-900">
          <video :src="heroVideo.localVideoUrl" controls autoplay muted class="block w-full max-h-[70vh] bg-black" />
          <div class="absolute bottom-0 inset-x-0 bg-gradient-to-t from-black/80 to-transparent px-6 pb-5 pt-12">
            <p class="text-lg font-semibold text-white">{{ heroVideo.title || '精选长视频' }}</p>
            <p v-if="heroVideo.description" class="text-sm text-gray-300 mt-1 line-clamp-1">{{ heroVideo.description }}</p>
          </div>
        </div>
        <div v-else class="bg-gray-900 border-2 border-gray-900 flex items-center justify-center py-24 text-gray-400">
          <Clapperboard class="w-12 h-12 mr-3" /><span class="text-sm">暂无精选视频</span>
        </div>
        <label class="mx-auto mt-5 flex h-10 w-full max-w-sm items-center rounded-full border border-gray-300 bg-white px-4 transition-colors focus-within:border-teal-400 shadow-sm">
          <Search class="h-4 w-4 shrink-0 text-gray-400" />
          <input v-model="searchQuery" type="search" placeholder="搜索长视频" class="min-w-0 flex-1 bg-transparent px-3 text-sm text-gray-700 outline-none placeholder:text-gray-400" />
        </label>
      </div>
    </section>

    <div class="sticky top-16 z-40 border-b border-gray-200 bg-white/80 backdrop-blur-md" @touchstart.stop @touchend.stop>
      <div class="mx-auto flex max-w-7xl gap-2 overflow-x-auto px-4 py-3 sm:justify-center sm:px-6 lg:px-8 touch-pan-x">
        <button v-for="category in categories" :key="category.value" type="button" class="shrink-0 rounded-full border px-4 py-1.5 text-xs font-medium transition-all"
          :class="activeCategory === category.value ? 'border-teal-400 bg-teal-50 text-teal-600' : 'border-gray-200 text-gray-500 hover:border-teal-300 hover:bg-teal-50 hover:text-teal-600'"
          @click="activeCategory = category.value">
          {{ category.label }} <span class="ml-1 opacity-60">{{ category.count }}</span>
        </button>
      </div>
    </div>

    <!-- 随机播放 -->
    <div class="flex justify-center py-6">
      <button v-if="!randomVideo" type="button" class="flex items-center gap-2 rounded-full border border-teal-300 bg-white px-5 py-2.5 text-sm font-medium text-teal-600 shadow-sm transition-all hover:bg-teal-50 hover:shadow-md"
        @click="playRandom">
        <Shuffle class="w-4 h-4" /> 随机播放
      </button>
      <div v-else class="relative mx-auto rounded-2xl overflow-hidden bg-black shadow-2xl">
        <video :src="randomVideo.localVideoUrl" controls autoplay class="block max-h-[80vh] max-w-full bg-black" />
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

    <div class="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
      <div class="mb-7 flex items-center gap-3">
        <span class="h-8 w-1 rounded-full bg-amber-400"></span>
        <div>
          <h2 class="text-2xl font-light text-gray-800">完整影像展厅</h2>
          <p class="mt-0.5 text-xs text-gray-500">本地高清播放 · B站官方链接</p>
        </div>
        <span class="ml-auto rounded-full border border-amber-500/30 px-2.5 py-1 text-xs text-amber-400">{{ filteredVideos.length }} 部</span>
      </div>

      <div v-if="loading" class="py-24 text-center text-sm text-gray-500">加载中...</div>
      <div v-else-if="filteredVideos.length === 0" class="rounded-2xl border border-gray-200 bg-white py-24 text-center text-sm text-gray-500">
        <Clapperboard class="mx-auto mb-3 h-10 w-10 text-gray-300" />暂无长视频
      </div>
      <section v-else class="grid gap-6 md:grid-cols-2" aria-label="长视频列表">
        <article v-for="video in filteredVideos" :key="video.id" class="group overflow-hidden rounded-3xl bg-white border-[4.5px] border-teal-200 shadow-sm hover:shadow-md transition-all">
          <button v-if="video.localVideoUrl" type="button" class="relative block aspect-video w-full overflow-hidden bg-gray-900" :aria-label="`播放 ${video.title}`" @click="playingVideo = video.localVideoUrl">
            <img v-if="video.coverUrl" :src="video.coverUrl" :alt="video.title" class="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105" />
            <video v-else :src="video.localVideoUrl" muted preload="metadata" class="h-full w-full object-cover" @loadeddata="$event.target.currentTime=0.5"></video>
            <span class="absolute inset-0 bg-black/20 transition-colors group-hover:bg-black/35"></span>
            <span class="absolute left-3 top-3 rounded-full bg-amber-500/20 px-2.5 py-1 text-[10px] font-medium text-amber-300 backdrop-blur-sm">{{ video.category }}</span>
            <span class="absolute left-1/2 top-1/2 flex h-14 w-14 -translate-x-1/2 -translate-y-1/2 items-center justify-center rounded-full border border-amber-300/40 bg-amber-400 text-gray-950 shadow-lg transition-transform group-hover:scale-105"><Play class="h-5 w-5 fill-current" /></span>
          </button>
          <div v-else class="relative aspect-video overflow-hidden bg-gray-900">
            <img v-if="video.coverUrl" :src="video.coverUrl" :alt="video.title" class="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105" />
            <div v-else class="flex h-full items-center justify-center"><Clapperboard class="h-10 w-10 text-gray-700" /></div>
            <span class="absolute left-3 top-3 rounded-full bg-amber-500/20 px-2.5 py-1 text-[10px] font-medium text-amber-300 backdrop-blur-sm">{{ video.category }}</span>
          </div>

          <div class="p-5">
            <div class="flex items-start justify-between gap-4">
              <div class="min-w-0">
                <h2 class="line-clamp-2 text-base font-medium leading-6 text-gray-800">{{ video.title }}</h2>
                <p class="mt-1 text-xs text-gray-500">{{ video.publishedDate || '小芒长视频' }}</p>
              </div>
              <a v-if="video.bilibiliUrl" :href="video.bilibiliUrl" target="_blank" rel="noopener noreferrer" :aria-label="`在 B站打开 ${video.title}`" class="inline-flex h-8 shrink-0 items-center gap-1.5 rounded-full border border-teal-300 px-2.5 text-xs font-medium text-teal-600 transition-colors hover:bg-teal-50">
                <ExternalLink class="h-3.5 w-3.5" />B站
              </a>
            </div>
            <p v-if="video.description" class="mt-3 line-clamp-2 text-sm leading-6 text-gray-500">{{ video.description }}</p>
          </div>
        </article>
      </section>
    </div>

    <div v-if="playingVideo" class="fixed inset-0 z-[120] flex items-center justify-center bg-black/95 p-4 backdrop-blur-sm" @click.self="playingVideo = null">
      <button type="button" class="absolute right-4 top-4 p-2 text-white/70 hover:text-white" aria-label="关闭视频" @click="playingVideo = null"><X class="h-6 w-6" /></button>
      <video :src="playingVideo" controls autoplay class="max-h-[90vh] max-w-[96vw]" />
    </div>
  </main>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { Clapperboard, ExternalLink, Play, Search, Shuffle, X } from 'lucide-vue-next'

const videos = ref([])
const configuredCategories = ref([])
const loading = ref(true)
const searchQuery = ref('')
const activeCategory = ref('all')
const playingVideo = ref(null)
const randomVideo = ref(null)

function playRandom() {
  const list = filteredVideos.value
  if (list.length === 0) return
  const idx = Math.floor(Math.random() * list.length)
  randomVideo.value = list[idx]
}

const categories = computed(() => {
  const counts = {}
  videos.value.forEach(video => { counts[video.category] = (counts[video.category] || 0) + 1 })
  const names = [...configuredCategories.value.map(category => category.name), ...Object.keys(counts)]
  return [
    { value: 'all', label: '全部', count: videos.value.length },
    ...[...new Set(names.filter(Boolean))].map(value => ({ value, label: value, count: counts[value] || 0 }))
  ]
})

const filteredVideos = computed(() => videos.value.filter(video => {
  const categoryMatch = activeCategory.value === 'all' || video.category === activeCategory.value
  const query = searchQuery.value.trim().toLowerCase()
  const searchMatch = !query || video.title.toLowerCase().includes(query) || (video.description || '').toLowerCase().includes(query)
  return categoryMatch && searchMatch
}))

const heroVideo = computed(() => {
  const list = videos.value.filter(v => v.localVideoUrl)
  return list[0] || null
})

onMounted(async () => {
  try {
    const [videoResponse, categoryResponse] = await Promise.all([
      fetch('/api/long-videos'),
      fetch('/api/long-video-categories')
    ])
    const [videoJson, categoryJson] = await Promise.all([videoResponse.json(), categoryResponse.json()])
    if (videoResponse.ok && videoJson.code === 200) videos.value = videoJson.data || []
    if (categoryResponse.ok && categoryJson.code === 200) configuredCategories.value = categoryJson.data || []
  } catch {
    videos.value = []
    configuredCategories.value = []
  } finally {
    loading.value = false
  }
})
</script>
