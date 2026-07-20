<template>
  <div class="min-h-screen overflow-x-hidden bg-gray-50 pt-6">
    <section class="photo-hero-shell" aria-label="精选照片轮播">
      <Swiper
        class="photo-hero-swiper"
        :modules="swiperModules"
        :slides-per-view="1"
        :space-between="24"
        :loop="heroSlides.length > 1"
        :speed="850"
        :autoplay="heroSlides.length > 1 ? { delay: 4800, disableOnInteraction: false, pauseOnMouseEnter: true } : false"
        :pagination="{ clickable: true }"
      >
        <SwiperSlide v-for="(slide, index) in heroSlides" :key="slide.id">
          <article class="photo-hero-card" :style="{ background: slide.background, boxShadow: slide.shadow }">
            <div class="photo-hero-glow photo-hero-glow--top" :style="{ background: slide.glowTop || 'rgba(255,255,255,0.15)' }"></div>
            <div class="photo-hero-glow photo-hero-glow--bottom" :style="{ background: slide.glowBottom || 'rgba(255,255,255,0.1)' }"></div>
            <div class="photo-hero-grid"></div>

            <div class="photo-hero-copy">
              <div class="photo-hero-kicker"><component :is="slide.icon" :size="15" /><span>{{ slide.kicker }}</span></div>
              <h2>{{ slide.heading }}<br class="hidden sm:block" />精彩瞬间</h2>
              <p>{{ slide.subtitle }}</p>
              <div class="photo-hero-rule"><span></span></div>
            </div>

            <figure v-if="slide.imageUrl" class="photo-hero-photo">
              <div class="photo-hero-photo-shine"></div>
              <img :src="slide.imageUrl" :alt="slide.heading" />
              <figcaption v-if="slide.title" class="photo-hero-caption">
                <span>{{ slide.title }}</span><span>{{ String(index + 1).padStart(2, '0') }}</span>
              </figcaption>
            </figure>
            <div v-else class="photo-hero-photo photo-hero-placeholder">
              <component :is="slide.icon" :size="42" />
              <span>等待一份新的美好</span>
            </div>
          </article>
        </SwiperSlide>
      </Swiper>
    </section>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pb-16">
      <div class="mb-6 max-w-2xl">
        <div class="rounded-full h-12 bg-white shadow border border-mangrove-100 flex items-center px-4">
          <Search class="w-4 h-4 text-mangrove-400 flex-shrink-0" />
          <input v-model="searchQuery" type="text" placeholder="搜索照片..." class="flex-1 outline-none text-sm px-3 bg-transparent" />
        </div>
      </div>
      <div class="flex justify-center mb-8">
        <div class="flex gap-2 flex-wrap justify-center" @touchstart.stop @touchend.stop>
          <button v-for="c in displayCategories" :key="c.value" class="shrink-0 rounded-full px-5 py-2 text-sm font-medium transition-all"
            :class="activeCategory===c.value ? 'bg-mangrove-600 text-white shadow-sm' : 'bg-white text-gray-500 border border-gray-200 hover:border-mangrove-300'"
            @click="activeCategory=c.value">{{ c.label }} ({{ c.count }})</button>
        </div>
      </div>
      <div v-if="loading" class="flex justify-center py-20"><div class="animate-spin rounded-full h-8 w-8 border-2 border-mangrove-500 border-t-transparent" /></div>
      <div v-else class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        <div v-for="p in displayedPhotos" :key="p.id" class="group rounded-2xl overflow-hidden bg-white border border-mangrove-100 shadow-sm hover:shadow-md transition-shadow cursor-pointer">
          <div class="aspect-[3/4] bg-mangrove-50 flex items-center justify-center overflow-hidden relative">
            <img v-if="p.fileUrl" :src="p.fileUrl" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" loading="lazy" />
            <Camera v-else class="w-10 h-10 text-mangrove-300" />
            <span class="absolute top-2 left-2 px-2 py-0.5 rounded-lg text-[10px] bg-black/50 text-white" v-if="p.categoryLabel">{{ p.categoryLabel }}</span>
          </div>
          <div class="p-3">
            <p class="text-sm font-medium text-gray-800 truncate">{{ p.title || '未命名' }}</p>
            <div class="flex items-center justify-between mt-1">
              <span :class="['px-2 py-0.5 rounded-full text-[11px] font-medium', catClass(p.categoryLabel)]">{{ p.categoryLabel || '未分类' }}</span>
              <span class="text-[11px] text-gray-400">{{ p.date || '' }}</span>
            </div>
          </div>
        </div>
      </div>
      <div v-if="!loading && displayedPhotos.length===0" class="text-center py-16 text-gray-400">
        <Camera class="w-10 h-10 mx-auto mb-2" /><p>暂无照片</p>
      </div>
      <h2 class="text-lg font-semibold text-gray-900 mb-4 mt-12">照片分类</h2>
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4">
        <div v-for="c in displayCategories" :key="c.value" class="card p-4 cursor-pointer hover:shadow-md transition-all"
          :class="activeCategory===c.value ? 'border-mangrove-400 bg-mangrove-50' : 'border-gray-100 bg-white'" @click="activeCategory=c.value">
          <div class="flex items-center justify-between mb-2">
            <div><p class="font-medium text-sm text-gray-900">{{ c.label }}</p><p class="text-xs text-gray-400 mt-0.5">共 {{ c.count }} 张</p></div>
            <Heart :size="18" :class="activeCategory===c.value ? 'text-mangrove-500' : 'text-gray-300'" />
          </div>
          <div class="flex gap-1">
            <div v-for="(p,pi) in getCatThumbs(c.value)" :key="pi" class="w-10 h-10 rounded-full bg-mangrove-100 overflow-hidden">
              <img v-if="p" :src="p" class="w-full h-full object-cover" />
            </div>
            <div v-if="c.count > 3" class="w-10 h-10 rounded-full bg-mangrove-100 flex items-center justify-center text-xs text-mangrove-600">+{{ c.count-3 }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Search, Camera, Heart, Sparkles, Star, Sun, MapPin, Eye } from 'lucide-vue-next'
import { Swiper, SwiperSlide } from 'swiper/vue'
import { A11y, Autoplay, Pagination } from 'swiper/modules'

import 'swiper/css'
import 'swiper/css/pagination'

const route = useRoute()
const swiperModules = [A11y, Autoplay, Pagination]
const artistCover = ref('')
const searchQuery = ref('')
const activeCategory = ref('all')
const loading = ref(true)
const photos = ref([])
const backendCats = ref([])
const heroCardUrls = ref([]) // 从管理后台配置的 5 张 Hero 卡片照片

const fetchHeroCardUrls = async () => {
  try {
    const res = await fetch('/api/public/config/photos-hero-cards')
    const json = await res.json()
    if (json.code === 200 && Array.isArray(json.data)) {
      heroCardUrls.value = json.data
    }
  } catch {}
}

const heroSlides = computed(() => {
  // 五张预定义风格卡片：各有独立的颜色、标题、图标
  const definitions = [
    {
      id: 'spotlight',
      kicker: 'SPOTLIGHT',
      heading: '聚光时刻',
      subtitle: '舞台上的每一个高光瞬间',
      icon: Sparkles,
      background: 'linear-gradient(112deg, #0f2b1a 0%, #1b6842 47%, #3ec97a 100%)',
      shadow: '0 24px 56px -28px rgba(15, 71, 43, 0.75)',
      glowTop: 'rgba(138, 255, 164, 0.30)',
      glowBottom: 'rgba(70, 202, 125, 0.25)',
    },
    {
      id: 'stage',
      kicker: 'ON STAGE',
      heading: '舞台光影',
      subtitle: '灯光之下，魅力无限',
      icon: Star,
      background: 'linear-gradient(112deg, #1a1035 0%, #3b2d6e 47%, #7c5ce0 100%)',
      shadow: '0 24px 56px -28px rgba(60, 35, 110, 0.75)',
      glowTop: 'rgba(160, 140, 255, 0.28)',
      glowBottom: 'rgba(120, 90, 220, 0.22)',
    },
    {
      id: 'daily',
      kicker: 'DAILY LIFE',
      heading: '日常碎片',
      subtitle: '镜头之外的温暖瞬间',
      icon: Sun,
      background: 'linear-gradient(112deg, #3d1e0f 0%, #b85c1e 47%, #f59e4b 100%)',
      shadow: '0 24px 56px -28px rgba(150, 70, 20, 0.75)',
      glowTop: 'rgba(255, 180, 100, 0.30)',
      glowBottom: 'rgba(240, 140, 60, 0.24)',
    },
    {
      id: 'event',
      kicker: 'EVENT',
      heading: '活动掠影',
      subtitle: '活动现场的第一手记录',
      icon: MapPin,
      background: 'linear-gradient(112deg, #0c1f3f 0%, #1a4a8a 47%, #4da6ff 100%)',
      shadow: '0 24px 56px -28px rgba(20, 55, 120, 0.75)',
      glowTop: 'rgba(120, 180, 255, 0.28)',
      glowBottom: 'rgba(70, 150, 240, 0.22)',
    },
    {
      id: 'candid',
      kicker: 'CANDID',
      heading: '路透珍藏',
      subtitle: '不经意的抓拍，最真实的模样',
      icon: Eye,
      background: 'linear-gradient(112deg, #2d1a0c 0%, #8b6914 47%, #e8b830 100%)',
      shadow: '0 24px 56px -28px rgba(100, 70, 15, 0.75)',
      glowTop: 'rgba(255, 210, 80, 0.30)',
      glowBottom: 'rgba(220, 170, 40, 0.24)',
    },
  ]

  // 按顺序给每张卡片分配照片：管理后台配置的 Hero 照片优先 → 艺人封面/照片列表兜底
  const configuredUrls = heroCardUrls.value
  const assignedPhotos = [
    configuredUrls[0] || artistCover.value || photos.value[0]?.fileUrl || '',
    configuredUrls[1] || photos.value[1]?.fileUrl || photos.value[0]?.fileUrl || '',
    configuredUrls[2] || photos.value[2]?.fileUrl || photos.value[0]?.fileUrl || '',
    configuredUrls[3] || photos.value[3]?.fileUrl || photos.value[0]?.fileUrl || '',
    configuredUrls[4] || photos.value[4]?.fileUrl || photos.value[0]?.fileUrl || '',
  ]

  const technicalNamePattern = /^[0-9a-f]{8}(?:-[0-9a-f]{4}){3}-[0-9a-f]{12}$/i
  return definitions.map((def, i) => {
    const imageUrl = assignedPhotos[i]
    const photo = photos.value[i] || null
    const rawTitle = photo?.title || ''
    return {
      ...def,
      imageUrl,
      title: technicalNamePattern.test(rawTitle) ? '' : rawTitle,
    }
  })
})

const displayCategories = computed(() => {
  const counts = {}
  photos.value.forEach(p => { const c = p.categoryLabel || '未分类'; if (c) counts[c] = (counts[c] || 0) + 1 })
  backendCats.value.forEach(c => { if (!counts[c]) counts[c] = 0 })
  const list = [{ value: 'all', label: '全部', count: photos.value.length }]
  Object.entries(counts).forEach(([k, v]) => list.push({ value: k, label: k, count: v }))
  return list
})
const filteredPhotos = computed(() => {
  let list = photos.value
  if (activeCategory.value !== 'all') list = list.filter(p => (p.categoryLabel || '未分类') === activeCategory.value)
  if (searchQuery.value.trim()) { const q = searchQuery.value.trim().toLowerCase(); list = list.filter(p => (p.title||'').toLowerCase().includes(q)) }
  // 过滤掉已在 Hero 大卡片上展示的照片
  const heroUrls = new Set(heroCardUrls.value.filter(Boolean))
  if (heroUrls.size > 0) list = list.filter(p => !heroUrls.has(p.fileUrl))
  return list
})
const displayedPhotos = computed(() => filteredPhotos.value.slice(0, 24))
function catClass(c) { const m = { 'roadshow':'bg-blue-50 text-blue-700','event':'bg-purple-50 text-purple-700','daily':'bg-green-50 text-green-700' }; return m[c] || 'bg-mangrove-50 text-mangrove-700' }
function getCatThumbs(cat) { const list = cat === 'all' ? photos.value : photos.value.filter(p => (p.categoryLabel||'未分类') === cat); return list.slice(0, 3).map(p => p.fileUrl) }

async function loadData() {
  loading.value = true
  try {
    const metaMap = {}
    const metaRes = await fetch('/api/files/meta').catch(() => null)
    if (metaRes && metaRes.ok) { const j = await metaRes.json(); if (j.data) j.data.forEach(m => { metaMap[m.filename] = m }) }
    const fileRes = await fetch('/api/files/list').catch(() => null)
    if (fileRes && fileRes.ok) {
      const j = await fileRes.json()
      if (j.code === 200 && j.data) {
        photos.value = j.data.filter(f => /\.(jpg|jpeg|png|gif|webp)$/i.test(f.filename||'')).map((f,i) => {
          const m = metaMap[f.filename] || {}
          return { id: 'f'+i, title: m.displayName || (f.filename||'').replace(/\.[^.]+$/,''), fileUrl: f.url, categoryLabel: m.category||'', date: m.photoDate || (f.createdAt||'').substring(0,10) }
        })
      }
    }
    const catRes = await fetch('/api/files/categories').catch(() => null)
    if (catRes && catRes.ok) { const cj = await catRes.json(); if (cj.data) backendCats.value = cj.data }
    // 获取 Hero 卡片照片
    await fetchHeroCardUrls()
    // 获取艺人封面
    const ar = await fetch('/api/public/artists').catch(() => null)
    if (ar && ar.ok) {
      const aj = await ar.json()
      if (aj.code===200 && aj.data?.length>0 && aj.data[0].coverUrl) {
        artistCover.value = aj.data[0].coverUrl
      }
    }
  } catch {} finally { loading.value = false }
  // 兜底：用第一张上传图片作为封面
  if (!artistCover.value && photos.value.length > 0) {
    const first = photos.value.find(p => p.fileUrl)
    if (first) artistCover.value = first.fileUrl
  }
}
watch(() => route.path, () => { if (route.path === '/photos') loadData() })
onMounted(loadData)
</script>

<style scoped>
@keyframes marquee {
  0% { transform: translateX(0); }
  100% { transform: translateX(-50%); }
}
.animate-marquee { animation: marquee 30s linear infinite; }

.photo-hero-shell {
  width: min(calc(100% - 2rem), 80rem);
  margin: 0 auto 1.75rem;
}

.photo-hero-swiper {
  overflow: visible;
  padding-bottom: 2rem;
}

.photo-hero-swiper :deep(.swiper-slide) {
  opacity: 0.48;
  transform: scale(0.985);
  transition: opacity 500ms ease, transform 700ms cubic-bezier(0.22, 1, 0.36, 1);
}

.photo-hero-swiper :deep(.swiper-slide-active) {
  opacity: 1;
  transform: scale(1);
}

.photo-hero-card {
  position: relative;
  isolation: isolate;
  height: 20rem;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.22);
  border-radius: 1.75rem;
}

.photo-hero-card::after {
  content: '';
  position: absolute;
  z-index: 1;
  inset: 0;
  background: linear-gradient(90deg, rgba(0, 0, 0, 0.28), transparent 62%);
  pointer-events: none;
}

.photo-hero-grid {
  position: absolute;
  z-index: 0;
  inset: 0;
  opacity: 0.12;
  background-image: linear-gradient(rgba(255, 255, 255, 0.24) 1px, transparent 1px), linear-gradient(90deg, rgba(255, 255, 255, 0.24) 1px, transparent 1px);
  background-size: 42px 42px;
  mask-image: linear-gradient(90deg, black, transparent 72%);
}

.photo-hero-glow {
  position: absolute;
  z-index: 0;
  border-radius: 999px;
  filter: blur(4px);
  pointer-events: none;
}

.photo-hero-glow--top {
  top: -8rem;
  right: 21%;
  width: 25rem;
  height: 25rem;
}

.photo-hero-glow--bottom {
  bottom: -7rem;
  left: 30%;
  width: 18rem;
  height: 18rem;
}

.photo-hero-copy {
  position: relative;
  z-index: 2;
  display: flex;
  width: 58%;
  height: 100%;
  flex-direction: column;
  justify-content: center;
  padding: 2.5rem 4rem;
  color: white;
}

.photo-hero-kicker {
  display: inline-flex;
  width: fit-content;
  align-items: center;
  gap: 0.55rem;
  margin-bottom: 1.1rem;
  color: rgba(224, 255, 232, 0.82);
  font-size: 0.68rem;
  font-weight: 700;
  letter-spacing: 0.2em;
}

.photo-hero-copy h2 {
  max-width: 31rem;
  font-size: clamp(2rem, 3.4vw, 3.15rem);
  font-weight: 800;
  line-height: 1.12;
  letter-spacing: -0.035em;
  text-wrap: balance;
}

.photo-hero-copy p {
  margin-top: 1rem;
  color: rgba(226, 255, 234, 0.72);
  font-size: 0.95rem;
  letter-spacing: 0.04em;
}

.photo-hero-rule {
  width: 7rem;
  height: 1px;
  margin-top: 1.35rem;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.16);
}

.photo-hero-rule span {
  display: block;
  width: 2.5rem;
  height: 100%;
  background: rgba(255, 255, 255, 0.76);
}

.photo-hero-photo {
  position: absolute;
  z-index: 4;
  top: 1.25rem;
  right: 2rem;
  bottom: 1.25rem;
  width: min(37%, 27rem);
  overflow: hidden;
  margin: 0;
  border: 5px solid rgba(255, 255, 255, 0.62);
  border-radius: 1.25rem;
  background: rgba(7, 34, 21, 0.2);
  box-shadow: 0 24px 50px -16px rgba(5, 31, 19, 0.7);
  transform: rotate(0.6deg);
}

.photo-hero-photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center 24%;
  transition: transform 6s ease;
}

.swiper-slide-active .photo-hero-photo img { transform: scale(1.035); }

.photo-hero-photo-shine {
  position: absolute;
  z-index: 2;
  inset: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.18), transparent 34%);
  pointer-events: none;
}

.photo-hero-caption {
  position: absolute;
  z-index: 3;
  right: 0.75rem;
  bottom: 0.75rem;
  left: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  padding: 0.55rem 0.7rem;
  border: 1px solid rgba(255, 255, 255, 0.22);
  border-radius: 0.7rem;
  background: rgba(10, 34, 23, 0.55);
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.7rem;
  backdrop-filter: blur(10px);
}

.photo-hero-caption span:first-child {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.photo-hero-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  border-style: dashed;
  color: rgba(255, 255, 255, 0.72);
  font-size: 0.8rem;
}

.photo-hero-swiper :deep(.swiper-pagination) { bottom: 0; }

.photo-hero-swiper :deep(.swiper-pagination-bullet) {
  width: 0.45rem;
  height: 0.45rem;
  margin: 0 0.3rem;
  background: #92bba0;
  opacity: 0.42;
  transition: width 240ms ease, opacity 240ms ease, background 240ms ease;
}

.photo-hero-swiper :deep(.swiper-pagination-bullet-active) {
  width: 1.5rem;
  border-radius: 999px;
  background: #2e8b57;
  opacity: 1;
}

@media (max-width: 767px) {
  .photo-hero-card { height: 28rem; border-radius: 1.4rem; }
  .photo-hero-copy { width: 100%; height: auto; justify-content: flex-start; padding: 2rem 1.75rem; }
  .photo-hero-kicker { margin-bottom: 0.75rem; }
  .photo-hero-copy h2 { font-size: 2rem; }
  .photo-hero-copy p { margin-top: 0.65rem; font-size: 0.8rem; }
  .photo-hero-rule { display: none; }
  .photo-hero-photo { top: 12.25rem; right: 1.25rem; bottom: 1.25rem; left: 1.25rem; width: auto; transform: none; }
  .photo-hero-glow--top { right: -10rem; }
}

@media (prefers-reduced-motion: reduce) {
  .photo-hero-swiper :deep(.swiper-slide),
  .photo-hero-photo img { transition: none; }
}
</style>
