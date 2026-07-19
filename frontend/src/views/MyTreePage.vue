<template>
  <div class="relative min-h-screen overflow-hidden bg-gradient-to-b from-mangrove-50 to-mangrove-100">
    <!-- Not Logged In -->
    <div v-if="!isLoggedIn" class="max-w-md mx-auto pt-24 px-4">
      <div class="card p-8 text-center">
        <div class="bg-mangrove-100 rounded-2xl h-48 flex items-center justify-center mb-6">
          <TreePine class="w-20 h-20 text-mangrove-400/60" />
        </div>
        <p class="text-gray-500 mb-6">登录后查看你的专属芒果树</p>
        <router-link to="/login" class="btn-primary inline-block">立即登录</router-link>
      </div>
    </div>

    <!-- Logged In: Full-screen Big Tree -->
    <div v-else class="relative w-full full-vh flex items-end justify-center overflow-hidden">
      <!-- 自定义背景图 -->
      <img v-if="treeBgUrl" :src="treeBgUrl" alt="" class="absolute inset-0 w-full h-full object-cover z-0" />
      <!-- 底部白色渐变 -->
      <div class="absolute bottom-0 left-0 right-0 h-16 bg-gradient-to-t from-white/60 to-transparent z-10" />

      <!-- 摇动的大树 -->
      <div class="mango-tree-wrap relative z-20 w-full h-full flex items-end justify-center">
        <img src="/assets/tree/golden-tree.png" alt="芒果导航树" class="h-full w-auto object-contain max-w-none" />

        <!-- 可点击的果实 -->
        <div class="absolute inset-0">
          <router-link
            v-for="fruit in navFruits"
            :key="fruit.label"
            :to="fruit.to"
            class="fruit absolute flex flex-col items-center group cursor-pointer transition-transform duration-200 hover:scale-125"
            :style="{ left: fruit.x + '%', top: fruit.y + '%' }"
          >
            <div class="w-0.5 h-1.5 bg-green-700/60 mb-0.5" />
            <div class="w-5 h-6 rounded-[50%_50%_50%_50%/60%_60%_40%_40%] bg-amber-500 shadow-sm flex items-center justify-center border border-amber-400/50 group-hover:shadow group-hover:shadow-amber-300/40">
              <span class="text-[7px] font-bold text-amber-900 leading-none text-center">{{ fruit.label }}</span>
            </div>
          </router-link>
        </div>

        <!-- 装饰粒子 -->
        <Sparkles class="sparkle s1 w-5 h-5 text-mangrove-400/60" />
        <Star class="sparkle s2 w-4 h-4 text-yellow-400/50" />
        <Sparkles class="sparkle s3 w-6 h-6 text-amber-300/40" />
      </div>

      <!-- 右侧"我的小树"按钮 -->
      <router-link
        to="/tree/my"
        class="absolute right-6 top-1/2 -translate-y-1/2 z-30 flex flex-col items-center gap-2 group"
      >
        <div class="w-14 h-14 rounded-full bg-white/90 backdrop-blur shadow-lg flex items-center justify-center border border-mangrove-200 group-hover:bg-mangrove-50 group-hover:shadow-xl transition-all duration-200">
          <TreePine class="w-6 h-6 text-mangrove-600" />
        </div>
        <span class="text-xs font-medium text-mangrove-700 bg-white/80 backdrop-blur rounded-full px-3 py-1 shadow-sm">我的小树</span>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { TreePine, Sparkles, Star } from 'lucide-vue-next'
import { useAuth } from '@/composables/useAuth'

const { isLoggedIn } = useAuth()

const treeBgUrl = ref('')

const navFruits = [
  { label: '照片', to: '/photos', x: 42, y: 18 },
  { label: '音乐', to: '/music', x: 58, y: 20 },
  { label: '创作', to: '/works', x: 44, y: 24 },
  { label: '周边', to: '/merchandise', x: 42, y: 32 },
  { label: '行程', to: '/schedule', x: 54, y: 24 },
  { label: '游戏', to: '/games', x: 50, y: 16 },
]

onMounted(async () => {
  try {
    const res = await fetch('/api/public/config/tree_background_url')
    const json = await res.json()
    if (json.code === 200 && json.data) treeBgUrl.value = json.data
  } catch {}
})
</script>

<style scoped>
.full-vh {
  height: 100vh;
  height: 100dvh;
}

.mango-tree-wrap {
  animation: sway 5s ease-in-out infinite;
  transform-origin: bottom center;
}
@keyframes sway {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(1.5deg); }
  75% { transform: rotate(-1.5deg); }
}

.sparkle {
  position: absolute;
  animation: sparkle-float 3s ease-in-out infinite;
}
.sparkle.s1 { top: 15%; left: 25%; animation-delay: 0s; }
.sparkle.s2 { top: 20%; right: 22%; animation-delay: 1s; }
.sparkle.s3 { top: 12%; left: 50%; animation-delay: 2s; }

@keyframes sparkle-float {
  0%, 100% { opacity: 0.2; transform: translateY(0) scale(1); }
  50% { opacity: 0.8; transform: translateY(-10px) scale(1.3); }
}
</style>
