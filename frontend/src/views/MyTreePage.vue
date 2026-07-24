<template>
  <div class="relative h-screen overflow-hidden bg-gradient-to-b from-mangrove-50 to-mangrove-100" style="height:100dvh">
    <!-- 返回首页按钮 -->
    <router-link to="/"
      class="absolute left-4 top-4 z-40 flex items-center gap-1.5 bg-white/80 backdrop-blur rounded-full px-3 py-2 shadow-md hover:bg-white hover:shadow-lg transition-all duration-200">
      <Home class="w-4 h-4 text-mangrove-600" />
      <span class="text-xs font-medium text-mangrove-700">首页</span>
    </router-link>

    <!-- 顶部弹幕 -->
    <div v-if="barrageMessages.length > 0" class="absolute top-0 left-0 right-0 h-[500px] z-30 overflow-hidden">
      <span v-for="(msg, i) in barrageMessages" :key="msg.id"
        class="absolute barrag-item inline-flex items-center gap-1.5 rounded-full pointer-events-auto cursor-pointer"
        :class="{ 'barrag-paused': pausedId === msg.id }"
        :style="{
          color: '#ffffff',
          background: pausedId === msg.id ? 'rgba(128, 128, 128, 0.45)' : 'rgba(128, 128, 128, 0.25)',
          top: (10 + (i * 56) % 440) + 'px',
          animationDelay: (-(i * 4)) + 's',
          animationDuration: (20 + (msg.content.length * 0.3)) + 's',
        }"
        @click="pauseBarrage(msg.id)">
        <span class="text-sm font-bold px-3 py-1 whitespace-nowrap select-none">{{ msg.nickname }}：{{ msg.content }}</span>
        <!-- 点赞 + 删除 -->
        <span class="inline-flex items-center gap-0.5 pr-2 pointer-events-auto cursor-pointer" @click.stop="likeBarrage(msg)">
          <span class="text-xs">❤️</span>
          <span class="text-[10px] text-white/70">{{ msg.likeCount || 0 }}</span>
        </span>
        <button v-if="isAdmin || msg.userId === currentUserId"
          class="text-[10px] text-white/50 hover:text-white/90 pointer-events-auto pr-1" @click.stop="deleteBarrage(msg.id)">✕</button>
      </span>
    </div>

    <!-- 弹幕输入框 -->
    <div v-if="isLoggedIn" class="absolute bottom-6 left-4 right-4 z-40 flex items-center gap-2 max-w-md mx-auto">
      <input
        v-model="barrageInput"
        @keydown.enter="sendBarrage"
        maxlength="100"
        placeholder="说点什么..."
        class="flex-1 h-10 rounded-full bg-white/80 backdrop-blur border border-gray-200 px-4 text-sm outline-none focus:border-mangrove-400 focus:ring-1 focus:ring-mangrove-400 transition-all"
      />
      <button
        @click="sendBarrage"
        :disabled="!barrageInput.trim() || barrageSending"
        class="h-10 px-5 rounded-full bg-mangrove-500 text-white text-sm font-medium hover:bg-mangrove-600 disabled:opacity-40 disabled:cursor-not-allowed transition-colors shadow-lg"
      >
        发射 🚀
      </button>
    </div>

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
      <img v-if="treeBgUrl" :src="treeBgUrl" alt="" class="absolute inset-0 w-full h-full object-cover z-0" />
      <div class="absolute bottom-0 left-0 right-0 h-16 bg-gradient-to-t from-white/60 to-transparent z-10" />

      <div class="relative z-20 w-full h-full flex items-end justify-center overflow-hidden">
        <img src="/assets/tree/golden-tree.png?v=2" alt="芒果树"
          class="max-w-full max-h-full object-contain sm:w-auto sm:h-full sm:max-w-none mb-0 translate-y-[-220px]" />

        <Sparkles class="sparkle s1 w-5 h-5 text-mangrove-400/60" />
        <Star class="sparkle s2 w-4 h-4 text-yellow-400/50" />
        <Sparkles class="sparkle s3 w-6 h-6 text-amber-300/40" />
      </div>

      <router-link to="/tree/my"
        class="absolute right-6 top-1/2 -translate-y-1/2 z-30 flex flex-col items-center gap-2 group">
        <div class="w-14 h-14 rounded-full bg-white/90 backdrop-blur shadow-lg flex items-center justify-center border border-mangrove-200 group-hover:bg-mangrove-50 group-hover:shadow-xl transition-all duration-200">
          <TreePine class="w-6 h-6 text-mangrove-600" />
        </div>
        <span class="text-xs font-medium text-mangrove-700 bg-white/80 backdrop-blur rounded-full px-3 py-1 shadow-sm">我的小树</span>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { TreePine, Sparkles, Star, Home } from 'lucide-vue-next'
import { useAuth } from '@/composables/useAuth'

const { isLoggedIn, isAdmin, currentUser, getToken } = useAuth()
const treeBgUrl = ref('')
const barrageMessages = ref([])
const barrageInput = ref('')
const barrageSending = ref(false)
const pausedId = ref(null)
const currentUserId = computed(() => currentUser.value?.id || null)

let pauseTimer = null

function pauseBarrage(id) {
  if (pausedId.value === id) {
    pausedId.value = null
    clearTimeout(pauseTimer)
    return
  }
  pausedId.value = id
  clearTimeout(pauseTimer)
  pauseTimer = setTimeout(() => { pausedId.value = null }, 10000)
}

async function fetchBarrage() {
  try {
    const res = await fetch('/api/tree/barrage')
    const json = await res.json()
    if (json.code === 200 && json.data) {
      barrageMessages.value = json.data
    }
  } catch {}
}

async function sendBarrage() {
  const content = barrageInput.value.trim()
  if (!content || barrageSending.value) return
  barrageSending.value = true
  try {
    const res = await fetch('/api/tree/barrage', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({ content })
    })
    const json = await res.json()
    if (json.code === 200) {
      barrageInput.value = ''
      await fetchBarrage()
    }
  } catch (e) {
    console.error('发送弹幕失败:', e)
  } finally {
    barrageSending.value = false
  }
}

async function likeBarrage(msg) {
  try {
    const res = await fetch(`/api/tree/barrage/${msg.id}/like`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    if (res.ok) {
      msg.likeCount = (msg.likeCount || 0) + 1
    }
  } catch (e) {
    console.error('点赞失败:', e)
  }
}

async function deleteBarrage(id) {
  try {
    const res = await fetch(`/api/tree/barrage/${id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    if (res.ok) {
      barrageMessages.value = barrageMessages.value.filter(m => m.id !== id)
    }
  } catch (e) {
    console.error('删除失败:', e)
  }
}

onMounted(async () => {
  // 隐藏导航栏和页脚，树页面全屏
  document.body.classList.add('tree-fullscreen')
  try {
    const res = await fetch('/api/public/config/tree_background_url')
    const json = await res.json()
    if (json.code === 200 && json.data) treeBgUrl.value = json.data
  } catch {}

  await fetchBarrage()
})

onBeforeUnmount(() => {
  document.body.classList.remove('tree-fullscreen')
})
</script>

<style scoped>
.full-vh {
  height: 100vh;
  height: 100dvh;
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

@keyframes barrage-scroll {
  0% { transform: translateX(100vw); opacity: 1; }
  92% { opacity: 1; }
  100% { transform: translateX(-110%); opacity: 0; }
}

.barrag-item {
  animation: barrage-scroll 20s linear infinite;
}
.barrag-paused {
  animation-play-state: paused !important;
  box-shadow: 0 0 12px rgba(255, 255, 255, 0.3);
}
</style>
