<template>
  <div class="min-h-screen bg-gray-50 relative overflow-hidden">
    <!-- 背景装饰：五线谱纹理 + 浮动音符 -->
    <div class="absolute inset-0 pointer-events-none select-none" aria-hidden="true">
      <!-- 五线谱底纹 -->
      <svg class="absolute top-0 left-0 w-full h-full opacity-[0.08]" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <pattern id="staff-lines" x="0" y="0" width="100%" height="150" patternUnits="userSpaceOnUse">
            <line x1="0" y1="20" x2="100%" y2="20" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="45" x2="100%" y2="45" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="70" x2="100%" y2="70" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="95" x2="100%" y2="95" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="120" x2="100%" y2="120" stroke="#2E8B57" stroke-width="1.5"/>
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill="url(#staff-lines)"/>
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

    <!-- Hero 横幅 -->
    <div class="relative pt-16 pb-2">
      <div class="relative bg-gradient-to-br from-mangrove-700 via-mangrove-600 to-mangrove-500 mx-4 sm:mx-6 lg:mx-auto lg:max-w-7xl rounded-3xl overflow-hidden shadow-xl">
        <!-- 钢琴键装饰底纹 -->
        <div class="absolute inset-0 opacity-[0.06]" aria-hidden="true">
          <svg class="w-full h-full" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="none">
            <defs>
              <pattern id="piano-keys" x="0" y="0" width="60" height="200" patternUnits="userSpaceOnUse">
                <rect x="0" y="0" width="24" height="200" fill="white"/>
                <rect x="24" y="0" width="24" height="200" fill="white"/>
                <rect x="48" y="0" width="12" height="200" fill="white"/>
                <rect x="16" y="0" width="16" height="120" fill="#111"/>
                <rect x="40" y="0" width="16" height="120" fill="#111"/>
              </pattern>
            </defs>
            <rect width="100%" height="100%" fill="url(#piano-keys)"/>
          </svg>
        </div>
        <!-- 光晕 -->
        <div class="absolute -top-20 -right-20 w-60 h-60 bg-mangrove-300/20 rounded-full blur-3xl"></div>
        <div class="absolute -bottom-10 -left-10 w-40 h-40 bg-mangrove-400/20 rounded-full blur-2xl"></div>
        <!-- 内容 -->
        <div class="relative px-6 sm:px-10 py-10 sm:py-14 text-center">
          <div class="inline-flex items-center gap-2 bg-white/15 backdrop-blur-sm rounded-full px-4 py-1.5 mb-4">
            <span class="text-white/90 text-sm">🎵</span>
            <span class="text-white/90 text-sm font-medium">Artist Profiles</span>
          </div>
          <h1 class="text-3xl sm:text-4xl font-bold text-white mb-3 tracking-wide">艺人档案</h1>
          <p class="text-mangrove-100 text-sm sm:text-base max-w-lg mx-auto leading-relaxed">追踪你喜爱的艺人，了解他们的每一刻</p>
          <!-- 高音谱号装饰 -->
          <div class="absolute right-6 bottom-4 text-white/10 text-7xl sm:text-8xl font-serif select-none" aria-hidden="true">𝄞</div>
        </div>
      </div>
    </div>

    <div class="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pb-16">
      <!-- Search Bar -->
      <div class="mb-8 max-w-2xl mx-auto -mt-5 relative z-10">
        <div class="rounded-full h-13 bg-white shadow-lg border border-mangrove-100 flex items-center px-5 transition-all duration-300 hover:shadow-xl focus-within:ring-2 focus-within:ring-mangrove-300 focus-within:border-mangrove-400">
          <Search class="w-5 h-5 text-mangrove-400 flex-shrink-0" />
          <input v-model="searchQuery" type="text" placeholder="搜索艺人..." class="flex-1 outline-none text-sm px-3 py-3 bg-transparent placeholder-mangrove-300 text-gray-700" />
          <div v-if="searchQuery" @click="searchQuery = ''" class="cursor-pointer p-1 rounded-full hover:bg-mangrove-50 transition-colors">
            <X class="w-4 h-4 text-mangrove-400" />
          </div>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="text-center py-20">
        <div class="inline-flex items-center gap-3 bg-white rounded-2xl px-8 py-6 shadow-card">
          <div class="w-8 h-8 border-3 border-mangrove-200 border-t-mangrove-600 rounded-full animate-spin"></div>
          <span class="text-mangrove-600 text-sm font-medium">加载中...</span>
        </div>
      </div>

      <!-- Artist Grid -->
      <div v-else-if="filteredArtists.length" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-5 sm:gap-6">
        <router-link
          v-for="(artist, index) in filteredArtists"
          :key="artist.id"
          :to="'/artists/' + artist.id"
          class="artist-card group block overflow-hidden rounded-2xl bg-white shadow-card border border-transparent hover:border-mangrove-300/80"
          :style="{ animationDelay: index * 80 + 'ms' }"
        >
          <!-- 头像区域 -->
          <div class="aspect-square overflow-hidden relative bg-gradient-to-br from-mangrove-100 via-mangrove-50 to-mangrove-200">
            <img v-if="artist.avatarUrl" :src="artist.avatarUrl" :alt="artist.stageName" class="w-full h-full object-cover transition-all duration-500 group-hover:scale-110 group-hover:brightness-110" />
            <div v-else class="w-full h-full flex items-center justify-center">
              <User class="w-16 h-16 text-mangrove-300/60" />
            </div>
            <!-- hover 音符装饰 - 多个散开 -->
            <div class="absolute top-3 right-3 w-10 h-10 bg-white/90 backdrop-blur-sm rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all duration-400 group-hover:scale-110 scale-50 shadow-lg border border-mangrove-200/50">
              <span class="text-mangrove-600 text-base">♪</span>
            </div>
            <div class="absolute top-1/2 right-8 w-7 h-7 bg-white/85 backdrop-blur-sm rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all duration-500 delay-75 group-hover:scale-100 scale-25 shadow-md border border-mangrove-200/40">
              <span class="text-mangrove-500 text-xs">♫</span>
            </div>
            <div class="absolute bottom-12 right-4 w-6 h-6 bg-white/85 backdrop-blur-sm rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all duration-500 delay-150 group-hover:scale-100 scale-25 shadow-md border border-mangrove-200/40">
              <span class="text-mangrove-400 text-[10px]">♬</span>
            </div>
            <!-- 底部渐变遮罩加深 -->
            <div class="absolute bottom-0 left-0 right-0 h-20 bg-gradient-to-t from-black/30 to-transparent group-hover:h-28 transition-all duration-500"></div>
          </div>
          <!-- 信息区域 -->
          <div class="p-4 relative">
            <h3 class="text-sm font-bold text-gray-900 group-hover:text-mangrove-700 transition-colors duration-300">{{ artist.stageName || artist.name }}</h3>
            <p class="text-xs text-gray-400 mt-0.5 group-hover:text-gray-600 transition-colors duration-300">{{ artist.name }}</p>
            <div class="flex items-center gap-2 mt-2.5">
              <span v-if="debutDays(artist)" class="inline-flex items-center gap-1 bg-mangrove-50 text-mangrove-600 text-[11px] font-medium px-2.5 py-1 rounded-full group-hover:bg-mangrove-100 transition-colors duration-300">
                <span class="text-[10px]">🎵</span>
                出道 {{ debutDays(artist) }}天
              </span>
            </div>
            <!-- 底部绿色装饰线 - 加粗加长 -->
            <div class="absolute bottom-0 left-1/2 -translate-x-1/2 w-0 group-hover:w-[90%] h-1 bg-gradient-to-r from-transparent via-mangrove-400 to-transparent transition-all duration-500 rounded-full"></div>
          </div>
        </router-link>
      </div>

      <!-- Empty State -->
      <div v-else class="text-center py-20">
        <div class="inline-block relative">
          <!-- 钢琴造型空状态 -->
          <div class="bg-white rounded-3xl p-8 shadow-card">
            <div class="w-20 h-20 mx-auto mb-4 relative">
              <!-- 五线谱圆圈 -->
              <div class="w-20 h-20 rounded-full bg-mangrove-50 flex items-center justify-center">
                <svg class="w-10 h-10 text-mangrove-300" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M9 18V5l12-2v13"/>
                  <circle cx="6" cy="18" r="3"/>
                  <circle cx="18" cy="16" r="3"/>
                </svg>
              </div>
              <!-- 浮动音符 -->
              <div class="absolute -top-2 -right-2 text-mangrove-400/60 text-lg animate-bounce">♪</div>
            </div>
            <p class="text-gray-500 text-sm">{{ searchQuery ? '没有找到匹配的艺人' : '暂无艺人' }}</p>
            <p v-if="searchQuery" class="text-gray-400 text-xs mt-1">试试其他关键词吧</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search, X, User } from 'lucide-vue-next'

const searchQuery = ref('')
const artists = ref([])
const loading = ref(true)

function debutDays(artist) {
  if (!artist.debutDate) return null
  const debut = new Date(artist.debutDate)
  const now = new Date()
  return Math.floor((now - debut) / (1000 * 60 * 60 * 24))
}

const filteredArtists = computed(() => {
  if (!searchQuery.value.trim()) return artists.value
  const q = searchQuery.value.trim().toLowerCase()
  return artists.value.filter(a =>
    (a.name || '').toLowerCase().includes(q) ||
    (a.stageName || '').toLowerCase().includes(q)
  )
})

onMounted(async () => {
  try {
    const res = await fetch('/api/public/artists')
    const json = await res.json()
    if (json.code === 200 && json.data) {
      artists.value = json.data
    }
  } catch (e) {
    console.error('获取艺人列表失败:', e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
/* 浮动音符 - 视口固定，明显可见 */
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

/* 卡片入场动画 */
.grid > a {
  animation: fadeInUp 0.6s ease-out both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 卡片hover - 大幅上浮 + 阴影 + 绿色光晕 */
.artist-card {
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.artist-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow:
    0 20px 40px -8px rgba(46, 139, 87, 0.15),
    0 8px 16px -4px rgba(46, 139, 87, 0.10),
    0 0 0 1px rgba(46, 139, 87, 0.08);
}
.artist-card::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  opacity: 0;
  transition: opacity 0.4s ease;
  background: radial-gradient(circle at 50% 0%, rgba(46, 139, 87, 0.08) 0%, transparent 70%);
  pointer-events: none;
}
.artist-card:hover::after {
  opacity: 1;
}
</style>
