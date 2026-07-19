<template>
  <div class="min-h-screen bg-gray-50 pb-[96px]">
    <!-- Swiper -->
    <section class="mb-5 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-16" aria-label="音乐精选">
      <Swiper
        class="music-showcase"
        :modules="swiperModules"
        :autoplay="{ delay: 4500, disableOnInteraction: false, pauseOnMouseEnter: true }"
        :pagination="{ clickable: true }"
        :slides-per-view="1.08"
        :space-between="16"
        :speed="600"
        :loop="true"
        :grab-cursor="true"
        :breakpoints="swiperBreakpoints">
        <SwiperSlide v-for="slide in slides" :key="slide.id">
          <button
            type="button"
            class="showcase-card"
            :class="{ 'showcase-card--active': activeCollection === slide.id }"
            :style="{ background: slide.background }"
            @click="selectCollection(slide.id)">
            <div class="min-w-0 flex-1 text-left">
              <span class="inline-flex items-center gap-1.5 text-xs font-medium text-gray-500">
                <component :is="slide.icon" class="w-3.5 h-3.5" />
                {{ slide.label }}
              </span>
              <h3 class="mt-4 text-xl font-semibold text-gray-900">{{ slide.title }}</h3>
              <p class="mt-1 text-sm text-gray-500">{{ slide.subtitle }}</p>
              <p v-if="slide.leadSong" class="mt-5 max-w-[75%] truncate text-xs font-medium text-gray-600">
                {{ slide.leadSong.name }} · {{ slide.leadSong.artist }}
              </p>
            </div>
            <div class="showcase-card__cover" :style="{ background: slide.cover }">
              <component :is="slide.icon" class="w-8 h-8 text-white/85" />
            </div>
            <ChevronRight class="showcase-card__arrow" />
          </button>
        </SwiperSlide>
      </Swiper>
    </section>

    <!-- 筛选 -->
    <div class="overflow-x-auto mb-6 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 touch-pan-x" @touchstart.stop @touchend.stop>
      <div class="flex gap-2 w-max mx-auto">
        <button v-for="f in filters" :key="f" @click="selectCategory(f)" class="shrink-0 rounded-full px-4 py-2 text-sm font-medium transition-all"
          :class="!activeCollection && (activeFilter===f || (!activeFilter && f === filters[0])) ? 'bg-mangrove-600 text-white' : 'bg-white text-gray-500 border border-gray-200 hover:border-mangrove-300'">{{ f }}</button>
      </div>
    </div>

    <!-- 歌曲列表 -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <template v-if="activeFilter === '专辑'">
        <div v-if="albums.length === 0" class="py-16 text-center text-gray-400">
          <Images class="w-10 h-10 mx-auto mb-2" /><p>暂无照片专辑</p>
        </div>
        <div v-else class="grid grid-cols-2 gap-4 md:grid-cols-3 lg:grid-cols-4">
          <RouterLink v-for="album in albums" :key="album.id" :to="`/music/albums/${album.id}`" class="group block min-w-0">
            <div class="aspect-[4/3] overflow-hidden bg-gray-100">
              <img :src="album.coverUrl" :alt="album.title" class="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105" />
            </div>
            <h2 class="mt-2 truncate text-sm font-medium text-gray-900">{{ album.title }}</h2>
            <p class="mt-0.5 line-clamp-1 text-xs text-gray-400">{{ album.description || '小芒照片专辑' }}</p>
          </RouterLink>
        </div>
      </template>
      <template v-else>
        <div v-if="selectedCollection" class="mb-2 flex items-center justify-between border-b border-gray-100 pb-3">
          <div>
            <h2 class="text-base font-semibold text-gray-900">{{ selectedCollection.title }}</h2>
            <p class="mt-0.5 text-xs text-gray-400">{{ filteredSongs.length }} 首歌曲</p>
          </div>
          <button type="button" class="p-2 text-gray-400 transition-colors hover:text-gray-700" aria-label="关闭精选歌单" @click="clearCollection">
            <X class="w-4 h-4" />
          </button>
        </div>
        <div v-if="filteredSongs.length === 0" class="py-16 text-center text-gray-400">
          <Music class="w-10 h-10 mx-auto mb-2" /><p>暂无歌曲</p>
        </div>
        <div v-for="song in filteredSongs" :key="song.id" class="flex items-center gap-3 border-b border-gray-100 px-2 py-3 hover:bg-gray-50"
          :class="song.localAudioUrl ? 'cursor-pointer' : ''" @click="song.localAudioUrl && playSong(song)">
          <div class="w-10 h-10 rounded-lg flex-shrink-0 flex items-center justify-center" :style="{ background: song.cover }">
            <Music class="w-4 h-4 text-white/70" />
          </div>
          <div class="flex-1 min-w-0">
            <p class="text-sm font-medium text-gray-900 truncate">{{ song.name }}</p>
            <p class="text-xs text-gray-400">小芒 · {{ song.category }}</p>
          </div>
          <span class="hidden text-xs text-gray-400 w-14 text-right tabular-nums sm:block">{{ song.localAudioUrl ? getSongDuration(song) : 'QQ 音乐' }}</span>
          <a v-if="song.qqMusicUrl" :href="song.qqMusicUrl" target="_blank" rel="noopener noreferrer" :aria-label="`在 QQ 音乐打开 ${song.name}`" class="inline-flex h-8 items-center gap-1.5 border border-blue-200 px-2.5 text-xs text-blue-700 hover:bg-blue-50" @click.stop="recordPlay(song)">
            <ExternalLink class="h-3.5 w-3.5" /><span class="hidden sm:inline">QQ 音乐</span>
          </a>
          <button v-if="song.localAudioUrl" type="button" class="p-2 text-mangrove-600" :aria-label="isCurrentSong(song) && isPlaying ? '暂停' : '播放'" @click.stop="isCurrentSong(song) && isPlaying ? togglePlay() : playSong(song)">
            <Pause v-if="isCurrentSong(song) && isPlaying" class="w-4 h-4" />
            <Play v-else class="w-4 h-4" />
          </button>
          <Heart class="w-4 h-4 flex-shrink-0 cursor-pointer transition-colors"
            :class="likedSongs.has(song.id) ? 'text-pink-500 fill-pink-500' : 'text-gray-300 hover:text-pink-400'"
            :fill="likedSongs.has(song.id) ? 'currentColor' : 'none'"
            @click.stop="toggleLike(song)" />
        </div>
      </template>
    </div>

    <!-- 真实音频元素 -->
    <audio ref="audioRef"
      @timeupdate="onTimeUpdate"
      @loadedmetadata="onMetadataLoaded"
      @ended="playNext" />

    <!-- 底部播放器 -->
    <div v-if="currentSong" class="music-player" data-testid="music-player">
      <!-- 进度条 -->
      <div class="music-player__progress-wrap">
        <div ref="progressBarRef" class="music-player__progress"
          role="slider" aria-label="歌曲播放进度" aria-valuemin="0" aria-valuemax="100" :aria-valuenow="Math.round(progressPercent)"
          @mousedown="startSeek" @touchstart.prevent="startSeek">
          <div class="music-player__progress-fill" :style="{ width: progressPercent + '%' }"></div>
          <div class="music-player__progress-thumb" :style="{ left: progressPercent + '%' }"></div>
        </div>
      </div>
      <!-- 控制栏 -->
      <div class="h-[60px] flex items-center px-4 gap-3">
        <div class="w-10 h-10 rounded-lg flex-shrink-0 flex items-center justify-center" :style="{ background: currentSong.cover }">
          <Music class="w-4 h-4 text-white/60" />
        </div>
        <div class="min-w-0 flex-1">
          <p class="text-sm font-medium text-gray-900 truncate">{{ currentSong.name }}</p>
          <p class="text-[11px] text-gray-400 truncate">{{ currentSong.artist }}</p>
        </div>
        <span class="text-[11px] text-gray-400 w-[80px] text-center flex-shrink-0 tabular-nums">{{ formatTime(currentTime) }} / {{ currentDurationDisplay }}</span>
        <div class="flex items-center gap-1.5 group">
          <component :is="isMuted || volume === 0 ? VolumeX : Volume2" class="w-4 h-4 text-gray-400 cursor-pointer flex-shrink-0 hover:text-mangrove-600 transition-colors" @click="toggleMute" />
          <input type="range" min="0" max="1" step="0.01" :value="volume" @input="setVolume(Number($event.target.value))"
            class="w-20 h-1 accent-mangrove-600 cursor-pointer opacity-70 group-hover:opacity-100 transition-opacity" />
        </div>
        <div class="flex items-center gap-2">
          <SkipBack class="w-4 h-4 text-gray-400 cursor-pointer" @click="playPrev" />
          <div class="w-8 h-8 rounded-full bg-mangrove-600 flex items-center justify-center cursor-pointer" @click="togglePlay">
            <Play v-if="!isPlaying" class="w-4 h-4 text-white ml-0.5" />
            <Pause v-else class="w-4 h-4 text-white" />
          </div>
          <SkipForward class="w-4 h-4 text-gray-400 cursor-pointer" @click="playNext" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Autoplay, Pagination, A11y } from 'swiper/modules'
import { Play, Pause, SkipBack, SkipForward, Heart, Music, Volume2, VolumeX, Sparkles, Flame, Clock3, ChevronRight, ExternalLink, Images, X } from 'lucide-vue-next'
import 'swiper/css'
import 'swiper/css/pagination'

const swiperModules = [Autoplay, Pagination, A11y]
const swiperBreakpoints = {
  640: { slidesPerView: 1.25, spaceBetween: 16 },
  1024: { slidesPerView: 1.55, spaceBetween: 20 },
}

const songs = ref([])
const albums = ref([])
const activeFilter = ref(null)
const activeCollection = ref(null)
const filters = ['全部', '单曲', '舞台', '翻唱', '专辑']
const likedSongs = ref(new Set())

const dailySongs = computed(() => {
  if (!songs.value.length) return []
  const dayNumber = Math.floor(Date.now() / 86400000)
  const offset = dayNumber % songs.value.length
  return [...songs.value.slice(offset), ...songs.value.slice(0, offset)].slice(0, 10)
})

const hotSongs = computed(() => [...songs.value]
  .sort((a, b) => (b.playCount || 0) - (a.playCount || 0)
    || new Date(b.createdAt || 0) - new Date(a.createdAt || 0))
  .slice(0, 10))

const newSongs = computed(() => [...songs.value]
  .sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0))
  .slice(0, 10))

const slides = computed(() => [
  {
    id: 'daily',
    label: '今日精选',
    title: '每日推荐',
    subtitle: `为你挑选 ${dailySongs.value.length} 首歌曲`,
    icon: Sparkles,
    background: '#e7f7ee',
    cover: dailySongs.value[0]?.cover || 'linear-gradient(135deg,#2E8B57,#3CB371)',
    leadSong: dailySongs.value[0],
  },
  {
    id: 'hot',
    label: '你的热度榜',
    title: '热门歌单',
    subtitle: `根据播放记录整理 ${hotSongs.value.length} 首`,
    icon: Flame,
    background: '#fff1ec',
    cover: hotSongs.value[0]?.cover || 'linear-gradient(135deg,#f97316,#ec4899)',
    leadSong: hotSongs.value[0],
  },
  {
    id: 'new',
    label: '最近更新',
    title: '新歌速递',
    subtitle: `最新上传的 ${newSongs.value.length} 首歌曲`,
    icon: Clock3,
    background: '#edf4ff',
    cover: newSongs.value[0]?.cover || 'linear-gradient(135deg,#0ea5e9,#4f46e5)',
    leadSong: newSongs.value[0],
  },
])

const selectedCollection = computed(() => slides.value.find(slide => slide.id === activeCollection.value) || null)
const filteredSongs = computed(() => {
  if (activeCollection.value === 'daily') return dailySongs.value
  if (activeCollection.value === 'hot') return hotSongs.value
  if (activeCollection.value === 'new') return newSongs.value
  if (activeFilter.value === '专辑') return []
  return activeFilter.value ? songs.value.filter(s => s.category === activeFilter.value) : songs.value
})

const currentSongIndex = ref(-1)
const isPlaying = ref(false)
const currentTime = ref(0)
const totalDuration = ref(0)
const durations = ref({})
const durationSeconds = ref({})
const audioRef = ref(null)
const progressBarRef = ref(null)
const durationLoaders = new Set()
const isSeeking = ref(false)
const seekPercent = ref(0)
const currentSong = computed(() => songs.value[currentSongIndex.value] || null)
const currentDurationDisplay = computed(() => {
  if (!currentSong.value) return '00:00'
  if (totalDuration.value) return formatTime(totalDuration.value)
  return getSongDuration(currentSong.value)
})

const volume = ref(0.8)
const isMuted = ref(false)
const volumeBeforeMute = ref(0.8)

function setVolume(v) {
  volume.value = v
  isMuted.value = v === 0
  if (audioRef.value) audioRef.value.volume = v
}

function toggleMute() {
  if (isMuted.value) {
    volume.value = volumeBeforeMute.value || 0.8
    isMuted.value = false
  } else {
    volumeBeforeMute.value = volume.value
    volume.value = 0
    isMuted.value = true
  }
  if (audioRef.value) audioRef.value.volume = volume.value
}

function selectCollection(id) {
  activeCollection.value = id
  activeFilter.value = null
}

function clearCollection() {
  activeCollection.value = null
}

function selectCategory(category) {
  activeCollection.value = null
  activeFilter.value = category === filters[0] ? null : category
}

function recordPlay(song) {
  if (!song?.id) return
  song.playCount = (song.playCount || 0) + 1
  fetch(`/api/music/tracks/${song.id}/play`, { method: 'POST' }).catch(() => {})
}

function playSong(song) {
  const idx = songs.value.findIndex(s => s.id === song.id)
  if (idx === -1 || !audioRef.value || !song.localAudioUrl) return
  currentSongIndex.value = idx
  currentTime.value = 0
  totalDuration.value = durationSeconds.value[song.url] || 0
  audioRef.value.src = song.url
  audioRef.value.play().then(() => {
    isPlaying.value = true
    recordPlay(song)
    updateDuration(song.url)
  }).catch(err => { console.warn('播放失败:', err) })
}

function updateDuration(url) {
  if (audioRef.value && audioRef.value.duration && isFinite(audioRef.value.duration)) {
    cacheDuration(url, audioRef.value.duration)
    totalDuration.value = audioRef.value.duration
  }
}

const progressPercent = computed(() => {
  if (isSeeking.value) return seekPercent.value
  if (!totalDuration.value) return 0
  return (currentTime.value / totalDuration.value) * 100
})

function formatTime(sec) {
  if (!sec || !isFinite(sec)) return '00:00'
  const m = Math.floor(sec / 60)
  const s = Math.floor(sec % 60)
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}

function getSeekPercent(e) {
  const rect = progressBarRef.value?.getBoundingClientRect()
  if (!rect?.width) return 0
  const pointer = e.touches?.[0] || e.changedTouches?.[0] || e
  const x = pointer.clientX - rect.left
  return Math.min(100, Math.max(0, (x / rect.width) * 100))
}

function startSeek(e) {
  isSeeking.value = true
  seekPercent.value = getSeekPercent(e)
  const onMove = (ev) => { ev.preventDefault(); seekPercent.value = getSeekPercent(ev) }
  const onUp = (ev) => {
    document.removeEventListener('mousemove', onMove)
    document.removeEventListener('mouseup', onUp)
    document.removeEventListener('touchmove', onMove)
    document.removeEventListener('touchend', onUp)
    isSeeking.value = false
    if (audioRef.value && totalDuration.value) {
      audioRef.value.currentTime = (seekPercent.value / 100) * totalDuration.value
      currentTime.value = audioRef.value.currentTime
    }
  }
  document.addEventListener('mousemove', onMove)
  document.addEventListener('mouseup', onUp)
  document.addEventListener('touchmove', onMove, { passive: false })
  document.addEventListener('touchend', onUp)
}

function togglePlay() {
  if (!audioRef.value || !currentSong.value?.url) return
  if (isPlaying.value) { audioRef.value.pause(); isPlaying.value = false }
  else {
    const targetSrc = new URL(currentSong.value.url, window.location.href).href
    if (audioRef.value.src !== targetSrc) {
      audioRef.value.src = currentSong.value.url
    }
    totalDuration.value = totalDuration.value || durationSeconds.value[currentSong.value.url] || 0
    audioRef.value.play().then(() => { isPlaying.value = true }).catch(err => { console.warn('播放失败:', err) })
  }
}

function playPrev() {
  playAdjacent(-1)
}

function playNext() {
  playAdjacent(1)
}

function playAdjacent(direction) {
  if (!songs.value.some(song => song.localAudioUrl)) return
  let index = currentSongIndex.value
  for (let count = 0; count < songs.value.length; count++) {
    index = (index + direction + songs.value.length) % songs.value.length
    if (songs.value[index].localAudioUrl) {
      playSong(songs.value[index])
      return
    }
  }
}

function onMetadataLoaded() {
  if (audioRef.value && audioRef.value.duration && isFinite(audioRef.value.duration)) {
    const song = songs.value[currentSongIndex.value]
    if (song) {
      cacheDuration(song.url, audioRef.value.duration)
      totalDuration.value = audioRef.value.duration
    }
  }
}

function onTimeUpdate() {
  if (!audioRef.value) return
  if (!isSeeking.value) currentTime.value = audioRef.value.currentTime
  if (!totalDuration.value && audioRef.value.duration && isFinite(audioRef.value.duration)) {
    totalDuration.value = audioRef.value.duration
  }
}

function toggleLike(song) {
  const k = song.id
  const next = new Set(likedSongs.value)
  if (next.has(k)) next.delete(k); else next.add(k)
  likedSongs.value = next
}

function cacheDuration(url, seconds) {
  if (!url || !seconds || !isFinite(seconds)) return
  durationSeconds.value = { ...durationSeconds.value, [url]: seconds }
  durations.value = { ...durations.value, [url]: formatTime(seconds) }
}

function getSongDuration(song) {
  if (!song) return '00:00'
  return durations.value[song.url] || song.duration || '00:00'
}

function isCurrentSong(song) {
  return currentSong.value?.id === song.id
}

function preloadDuration(song) {
  if (!song?.url || durationSeconds.value[song.url]) return

  const audio = new Audio()
  audio.preload = 'metadata'
  durationLoaders.add(audio)

  const cleanup = () => {
    durationLoaders.delete(audio)
    audio.removeAttribute('src')
    audio.load()
  }
  const handleLoaded = () => {
    cacheDuration(song.url, audio.duration)
    cleanup()
  }
  const handleError = () => cleanup()

  audio.addEventListener('loadedmetadata', handleLoaded, { once: true })
  audio.addEventListener('error', handleError, { once: true })
  audio.src = song.url
}

function preloadDurations(list) {
  list.forEach((song, index) => {
    window.setTimeout(() => preloadDuration(song), index * 60)
  })
}

onMounted(async () => {
  if (audioRef.value) audioRef.value.volume = volume.value
  try {
    const [trackRes, albumRes] = await Promise.all([fetch('/api/music/tracks'), fetch('/api/music/albums')])
    const trackJson = await trackRes.json()
    const albumJson = await albumRes.json()
    const grads = ['linear-gradient(135deg,#2E8B57,#3CB371)','linear-gradient(135deg,#3CB371,#20B2AA)','linear-gradient(135deg,#20B2AA,#4682B4)']
    if (trackJson.code === 200 && Array.isArray(trackJson.data)) {
      songs.value = trackJson.data.map((track, index) => {
        const url = track.localAudioUrl || ''
        if (url && track.durationSeconds) cacheDuration(url, track.durationSeconds)
        return {
          ...track,
          name: track.title,
          artist: '小芒',
          duration: track.durationSeconds ? formatTime(track.durationSeconds) : '00:00',
          cover: track.coverUrl ? `url("${track.coverUrl}") center/cover` : grads[index % grads.length],
          url,
        }
      })
      currentSongIndex.value = songs.value.findIndex(song => song.localAudioUrl)
      preloadDurations(songs.value.filter(song => song.localAudioUrl))
    }
    if (albumJson.code === 200 && Array.isArray(albumJson.data)) albums.value = albumJson.data
  } catch (error) {
    console.warn('音乐数据加载失败:', error)
  }
})
</script>

<style scoped>
.music-showcase {
  padding-bottom: 28px;
}

.showcase-card {
  position: relative;
  display: flex;
  width: 100%;
  height: 200px;
  align-items: center;
  gap: 24px;
  padding: 28px;
  overflow: hidden;
  color: inherit;
  border: 2px solid transparent;
  border-radius: 16px;
  transition: border-color 180ms ease, box-shadow 180ms ease, transform 180ms ease;
}

.showcase-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgb(15 23 42 / 9%);
}

.showcase-card:focus-visible,
.showcase-card--active {
  border-color: #22a867;
  outline: none;
}

.showcase-card__cover {
  display: flex;
  width: 88px;
  height: 88px;
  flex: 0 0 88px;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  box-shadow: 0 10px 24px rgb(15 23 42 / 13%);
}

.showcase-card__arrow {
  position: absolute;
  right: 20px;
  bottom: 18px;
  width: 18px;
  height: 18px;
  color: #64748b;
}

.music-showcase :deep(.swiper-pagination) {
  bottom: 2px;
}

.music-showcase :deep(.swiper-pagination-bullet) {
  width: 7px;
  height: 7px;
  background: #94a3b8;
  opacity: 0.45;
}

.music-showcase :deep(.swiper-pagination-bullet-active) {
  width: 20px;
  background: #22a867;
  border-radius: 9999px;
  opacity: 1;
}

@media (max-width: 639px) {
  .showcase-card {
    height: 176px;
    gap: 16px;
    padding: 22px;
  }

  .showcase-card__cover {
    width: 68px;
    height: 68px;
    flex-basis: 68px;
  }
}

.music-player {
  position: fixed;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 100;
  background: #fff;
  border-top: 1px solid #d1d5db;
  box-shadow: 0 -8px 24px rgb(15 23 42 / 10%);
}

.music-player__progress-wrap {
  padding: 10px 16px 4px;
}

.music-player__progress {
  position: relative;
  height: 10px;
  cursor: pointer;
  touch-action: none;
  background: #cbd5e1;
  border: 1px solid #b6c0cc;
  border-radius: 9999px;
  box-shadow: inset 0 1px 2px rgb(15 23 42 / 12%);
}

.music-player__progress-fill {
  position: absolute;
  inset: -1px auto -1px -1px;
  min-width: 2px;
  background: #16a34a;
  border-radius: inherit;
}

.music-player__progress-thumb {
  position: absolute;
  top: 50%;
  width: 16px;
  height: 16px;
  background: #16a34a;
  border: 2px solid #fff;
  border-radius: 50%;
  box-shadow: 0 1px 4px rgb(15 23 42 / 35%);
  transform: translate(-50%, -50%);
  transition: transform 120ms ease;
}

.music-player__progress:hover .music-player__progress-thumb {
  transform: translate(-50%, -50%) scale(1.12);
}
</style>
