<template>
  <div class="min-h-screen overflow-x-hidden bg-gray-50 pt-6 relative">
    <!-- 背景装饰：五线谱纹理 + 浮动音符 -->
    <div class="absolute inset-0 pointer-events-none select-none z-0" aria-hidden="true">
      <!-- 五线谱底纹 -->
      <svg class="absolute top-0 left-0 w-full h-full opacity-[0.08]" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <pattern id="staff-lines-photo" x="0" y="0" width="100%" height="150" patternUnits="userSpaceOnUse">
            <line x1="0" y1="20" x2="100%" y2="20" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="45" x2="100%" y2="45" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="70" x2="100%" y2="70" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="95" x2="100%" y2="95" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="120" x2="100%" y2="120" stroke="#2E8B57" stroke-width="1.5"/>
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill="url(#staff-lines-photo)"/>
      </svg>
      <!-- 浮动音符装饰 -->
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

    <section class="photo-hero-shell relative z-10" aria-label="精选照片轮播">
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
            </figure>
            <div v-else class="photo-hero-photo photo-hero-placeholder">
              <component :is="slide.icon" :size="42" />
              <span>等待一份新的美好</span>
            </div>
          </article>
        </SwiperSlide>
      </Swiper>
    </section>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pb-16 relative z-10">
      <!-- 搜索框 -->
      <div class="mb-5 max-w-xl mx-auto">
        <div class="rounded-full h-11 bg-white border-2 border-pink-200 flex items-center px-2 shadow-sm">
          <input v-model="searchQuery" type="text" placeholder="搜索照片..." class="flex-1 outline-none text-sm px-3 bg-transparent text-gray-700 placeholder:text-pink-300" />
          <button class="w-9 h-9 rounded-full bg-pink-100 flex items-center justify-center flex-shrink-0 hover:bg-pink-200 transition-colors">
            <Search class="w-4 h-4 text-pink-400" />
          </button>
        </div>
      </div>

      <!-- 分类标签 -->
      <div class="flex justify-center mb-6">
        <div class="flex gap-2 flex-wrap justify-center" @touchstart.stop @touchend.stop>
          <button v-for="c in displayCategories" :key="c.value" class="shrink-0 rounded-full px-5 py-2 text-sm font-medium transition-all"
            :class="activeCategory===c.value ? 'bg-pink-500 text-white shadow-sm shadow-pink-200' : 'bg-white text-pink-400 border border-pink-200 hover:border-pink-300 hover:bg-pink-50'"
            @click="activeCategory=c.value">{{ c.label }}</button>
        </div>
      </div>

      <!-- 照片网格 -->
      <div v-if="loading" class="flex justify-center py-20"><div class="animate-spin rounded-full h-8 w-8 border-2 border-pink-400 border-t-transparent" /></div>
      <div v-else-if="displayedPhotos.length===0" class="text-center py-16 text-pink-300">
        <Camera class="w-10 h-10 mx-auto mb-2" /><p>暂无照片</p>
      </div>
      <div v-else class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
        <div v-for="(p, idx) in displayedPhotos" :key="p.id" @click="openPreview(p)"
          class="photo-card group rounded-3xl overflow-hidden bg-white border-[4.5px] border-teal-200 shadow-sm cursor-pointer"
          :style="{ animationDelay: (idx % 8) * 0.06 + 's' }">
          <div class="aspect-[3/4] bg-pink-50 overflow-hidden relative">
            <img v-if="p.fileUrl" :src="p.fileUrl" class="w-full h-full object-cover group-hover:scale-110 group-hover:brightness-110 transition-all duration-500" loading="lazy" />
            <Camera v-else class="w-10 h-10 text-pink-200" />
            <!-- hover 浮动音符 -->
            <div class="photo-note photo-note-1">♪</div>
            <div class="photo-note photo-note-2">♫</div>
            <div class="photo-note photo-note-3">♬</div>
            <!-- hover 底部渐变遮罩 -->
            <div class="photo-card-overlay"></div>
          </div>
          <!-- 底部绿色光线 -->
          <div class="photo-card-line"></div>
        </div>
      </div>

      <!-- 加载更多 -->
      <div v-if="hasMore" class="flex justify-center mt-8">
        <button @click="loadMore"
          class="px-8 py-3 rounded-full bg-white border-2 border-pink-300 text-pink-500 font-medium text-sm hover:bg-pink-50 hover:border-pink-400 transition-all shadow-sm">
          加载更多 ({{ visibleCount }} / {{ filteredPhotos.length }})
        </button>
      </div>

      <!-- 照片分类（拍立得风格） -->
      <div v-if="categoryCards.length > 1" class="mt-12">
        <h2 class="text-lg font-semibold text-gray-800 mb-5 text-center">照片分类</h2>
        <div class="grid grid-cols-2 md:grid-cols-3 gap-5">
          <div v-for="(card, ci) in categoryCards" :key="card.value"
            class="polaroid-card cursor-pointer group"
            :style="{ transform: `rotate(${card.rotate}deg)` }"
            @click="activeCategory = card.value; window.scrollTo({ top: 0, behavior: 'smooth' })">
            <div class="polaroid-inner">
              <div class="aspect-square overflow-hidden rounded-lg bg-pink-50">
                <img v-if="card.thumb" :src="card.thumb" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" loading="lazy" />
                <Camera v-else class="w-8 h-8 text-pink-200 absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2" />
              </div>
              <p class="text-center text-sm font-medium text-gray-700 mt-3 pb-1">{{ card.label }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 灯箱预览 -->
    <Teleport to="body">
      <div v-if="previewPhoto" class="fixed inset-0 z-[100] bg-black/95 flex items-center justify-center p-4"
        @click.self="closePreview" @keydown.escape="closePreview">
        <button @click="closePreview"
          class="absolute top-4 right-4 z-10 w-10 h-10 rounded-full bg-white/10 hover:bg-white/20 flex items-center justify-center text-white text-xl transition-colors">
          ✕
        </button>
        <img :src="previewPhoto.fileUrl" :alt="previewPhoto.title"
          class="max-h-[92vh] max-w-[95vw] object-contain rounded-lg shadow-2xl" />
        <p v-if="previewPhoto.title" class="absolute bottom-4 left-1/2 -translate-x-1/2 text-white/70 text-sm">{{ previewPhoto.title }}</p>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Search, Camera, Sparkles, Star, Sun, MapPin, Eye } from 'lucide-vue-next'
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
const bgExcludeUrls = ref([]) // 背景图 URL（芒果园、小树、首页背景）

const fetchHeroCardUrls = async () => {
  try {
    const res = await fetch('/api/public/config/photos-hero-cards')
    const json = await res.json()
    if (json.code === 200 && Array.isArray(json.data)) {
      heroCardUrls.value = json.data
    }
  } catch {}
}

const fetchBgExcludeUrls = async () => {
  try {
    const token = localStorage.getItem('mangrove_token')
    const headers = token ? { Authorization: `Bearer ${token}` } : {}
    const [treeRes, littleRes, homeRes] = await Promise.all([
      fetch('/api/admin/config/tree_background_url', { headers }).catch(() => null),
      fetch('/api/admin/config/littletree_background_url', { headers }).catch(() => null),
      fetch('/api/admin/config/homepage_background_url', { headers }).catch(() => null),
    ])
    const urls = []
    const extractUrl = async (res) => {
      if (!res?.ok) return
      const j = await res.json()
      if (j.code === 200 && j.data) urls.push(j.data)
    }
    await Promise.all([extractUrl(treeRes), extractUrl(littleRes), extractUrl(homeRes)])
    bgExcludeUrls.value = urls
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
  // 过滤掉已在 Hero 大卡片上展示的照片 + 背景图
  const heroUrls = new Set(heroCardUrls.value.filter(Boolean))
  const excludeUrls = new Set([...heroUrls, ...bgExcludeUrls.value.filter(Boolean)])
  if (excludeUrls.size > 0) list = list.filter(p => !excludeUrls.has(p.fileUrl))
  return list
})
const visibleCount = ref(24)
const previewPhoto = ref(null)
const displayedPhotos = computed(() => filteredPhotos.value.slice(0, visibleCount.value))
const hasMore = computed(() => visibleCount.value < filteredPhotos.value.length)
function loadMore() { visibleCount.value += 24 }
function openPreview(photo) { previewPhoto.value = photo; document.body.style.overflow = 'hidden' }
function closePreview() { previewPhoto.value = null; document.body.style.overflow = '' }

// 切换分类或搜索时重置可见数量
watch([activeCategory, searchQuery], () => { visibleCount.value = 24 })

function getCatThumbs(cat) {
  const list = cat === 'all' ? photos.value : photos.value.filter(p => (p.categoryLabel || '未分类') === cat)
  return list.slice(0, 1).map(p => p.fileUrl)
}

const polaroidRotations = [-2.5, 1.8, -1.2, 2.3, -0.8, 1.5, -2.0, 0.9]
const categoryCards = computed(() => {
  return displayCategories.value
    .filter(c => c.value !== 'all')
    .map((c, i) => ({
      ...c,
      thumb: getCatThumbs(c.value)[0] || '',
      rotate: polaroidRotations[i % polaroidRotations.length],
    }))
})

async function loadData() {
  loading.value = true
  try {
    // 只从元数据加载照片（仅 ResourceManager 上传的才有元数据）
    const metaRes = await fetch('/api/files/meta?status=1').catch(() => null)
    if (metaRes && metaRes.ok) {
      const j = await metaRes.json()
      if (j.code === 200 && j.data) {
        photos.value = j.data.filter(m => /\.(jpg|jpeg|png|gif|webp)$/i.test(m.filename||'')).map((m,i) => {
          return { id: 'f'+i, title: m.displayName || (m.filename||'').replace(/\.[^.]+$/,''), fileUrl: m.url, categoryLabel: m.category||'', date: m.photoDate || '' }
        })
      }
    }
    const catRes = await fetch('/api/files/categories?type=photo').catch(() => null)
    if (catRes && catRes.ok) { const cj = await catRes.json(); if (cj.data) backendCats.value = cj.data }
    // 获取 Hero 卡片照片
    await fetchHeroCardUrls()
    await fetchBgExcludeUrls()
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
  border: none;
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

/* 拍立得风格分类卡片 */
.polaroid-card {
  background: #fff;
  border-radius: 0.75rem;
  padding: 0.6rem 0.6rem 0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06), 0 1px 4px rgba(0, 0, 0, 0.04);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.polaroid-card:hover {
  transform: rotate(0deg) scale(1.03) !important;
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.10), 0 2px 8px rgba(0, 0, 0, 0.06);
}
.polaroid-inner {
  background: #fafafa;
  border-radius: 0.5rem;
  overflow: hidden;
}

/* === 浮动音符 - 视口固定，明显可见 === */
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

/* === 照片卡片 hover 动效增强 === */
.photo-card {
  position: relative;
  transition: transform 0.4s cubic-bezier(0.34, 1.56, 0.64, 1), box-shadow 0.4s ease;
  animation: photoFadeInUp 0.6s ease-out both;
}

.photo-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 20px 40px -12px rgba(46, 139, 87, 0.25), 0 8px 16px -6px rgba(46, 139, 87, 0.15);
}

.photo-card:hover .photo-card-line {
  transform: scaleX(0.95);
  opacity: 1;
}

/* 底部绿色光线 */
.photo-card-line {
  position: absolute;
  bottom: 0;
  left: 5%;
  right: 5%;
  height: 1px;
  background: linear-gradient(90deg, transparent, #2E8B57, transparent);
  transform: scaleX(0);
  opacity: 0;
  transition: transform 0.5s ease, opacity 0.5s ease;
  z-index: 5;
}

/* hover 底部渐变遮罩 */
.photo-card-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 0;
  background: linear-gradient(to top, rgba(46, 139, 87, 0.3), transparent);
  transition: height 0.4s ease;
  z-index: 3;
  pointer-events: none;
}

.photo-card:hover .photo-card-overlay {
  height: 40%;
}

/* hover 浮动音符 */
.photo-note {
  position: absolute;
  z-index: 4;
  color: rgba(46, 139, 87, 0.85);
  font-size: 0.9rem;
  opacity: 0;
  pointer-events: none;
  text-shadow: 0 0 8px rgba(46, 139, 87, 0.5);
  transition: opacity 0.3s ease, transform 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.photo-card:hover .photo-note-1 {
  opacity: 1;
  transform: translate(15px, -25px) rotate(-15deg);
  transition-delay: 0s;
}

.photo-card:hover .photo-note-2 {
  opacity: 1;
  transform: translate(-10px, -35px) rotate(12deg);
  transition-delay: 0.075s;
}

.photo-card:hover .photo-note-3 {
  opacity: 1;
  transform: translate(8px, -20px) rotate(-8deg);
  transition-delay: 0.15s;
}

.photo-note-1 { top: 40%; right: 15%; }
.photo-note-2 { top: 55%; left: 20%; font-size: 0.75rem; }
.photo-note-3 { top: 30%; left: 50%; font-size: 0.7rem; }

/* 卡片入场动画 */
@keyframes photoFadeInUp {
  from { opacity: 0; transform: translateY(25px) scale(0.97); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}
</style>
