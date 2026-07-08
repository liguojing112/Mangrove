<template>
  <div class="relative overflow-hidden">
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
    <div v-if="showNotice" class="mb-8 bg-gradient-to-r from-mangrove-900/80 via-gray-900/90 to-mangrove-900/80 border border-mangrove-700/30 rounded-2xl px-5 py-3 flex items-center gap-3 backdrop-blur-sm">
      <span class="text-lg shrink-0">📢</span>
      <div class="flex-1 min-w-0 overflow-hidden">
        <p class="text-sm text-gray-300 truncate">
          <span class="text-amber-400 font-medium">公告：</span>{{ notices[currentNotice] }}
        </p>
      </div>
      <div class="flex items-center gap-2 shrink-0">
        <button @click="prevNotice" class="p-1 rounded hover:bg-gray-700 text-gray-500 hover:text-gray-300 transition-colors">
          <ChevronLeft :size="14" />
        </button>
        <button @click="nextNotice" class="p-1 rounded hover:bg-gray-700 text-gray-500 hover:text-gray-300 transition-colors">
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
      <router-link to="/artists" class="card-hover p-6 text-center block">
        <Users class="w-10 h-10 text-mangrove-600 mx-auto mb-4" />
        <h3 class="font-semibold text-gray-900">艺人档案</h3>
        <p class="text-sm text-gray-500 mt-2">追踪你喜爱的艺人，了解他们的每一刻</p>
      </router-link>
      <router-link to="/works" class="card-hover p-6 text-center block">
        <PenTool class="w-10 h-10 text-mangrove-600 mx-auto mb-4" />
        <h3 class="font-semibold text-gray-900">创作者殿堂</h3>
        <p class="text-sm text-gray-500 mt-2">路透、修图、剪辑 — 展现你的创作才华</p>
      </router-link>
      <router-link to="/tree" class="card-hover p-6 text-center block">
        <TreePine class="w-10 h-10 text-mangrove-600 mx-auto mb-4" />
        <h3 class="font-semibold text-gray-900">芒果树养成</h3>
        <p class="text-sm text-gray-500 mt-2">每日签到，培育专属芒果树</p>
      </router-link>
    </div>

    <!-- Stats section -->
    <div class="mt-12 mb-8 grid grid-cols-2 md:grid-cols-4 gap-6">
      <div class="text-center">
        <div class="text-3xl font-bold text-mangrove-700">320+</div>
        <div class="text-sm text-gray-500 mt-1">注册粉丝</div>
      </div>
      <div class="text-center">
        <div class="text-3xl font-bold text-mangrove-700">58</div>
        <div class="text-sm text-gray-500 mt-1">收录艺人</div>
      </div>
      <div class="text-center">
        <div class="text-3xl font-bold text-mangrove-700">1.2w+</div>
        <div class="text-sm text-gray-500 mt-1">创作者作品</div>
      </div>
      <div class="text-center">
        <div class="text-3xl font-bold text-mangrove-700">8.6w+</div>
        <div class="text-sm text-gray-500 mt-1">周边图鉴</div>
      </div>
    </div>

    <!-- Music player (fixed position, renders here) -->
    <MusicPlayer />
  </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Users, PenTool, TreePine, X, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import PhotoCarousel from '@/components/home/PhotoCarousel.vue'
import StageSidebar from '@/components/home/StageSidebar.vue'
import CalendarBar from '@/components/home/CalendarBar.vue'
import MusicPlayer from '@/components/home/MusicPlayer.vue'

// 公告栏
const showNotice = ref(true)
const currentNotice = ref(0)
const notices = [
  '新专辑《绿野》正式上线！欢迎前往音芒分享收听 🎵',
  '小芒生日应援企划启动，快来送上你的祝福吧 🎂',
  '创作者殿堂月度评选正在进行中，为你喜欢的太太投票 ✨',
]
function nextNotice() { currentNotice.value = (currentNotice.value + 1) % notices.length }
function prevNotice() { currentNotice.value = (currentNotice.value - 1 + notices.length) % notices.length }
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
