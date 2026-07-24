<template>
  <!-- 开场欢迎页 -->
  <div v-if="visible" class="fixed inset-0 z-[200] bg-black overflow-hidden" style="font-family: system-ui, sans-serif;">
    <!-- 跳过按钮（最顶层） -->
    <button @click="enter"
      class="absolute top-4 right-4 z-[300] px-4 py-2 rounded-full bg-white/10 backdrop-blur-md text-white/60 text-xs font-medium border border-white/10 hover:bg-white/20 hover:text-white/80 active:scale-95 transition-all">
      跳过
    </button>

    <!-- Video -->
    <video
      v-if="animationUrl"
      ref="videoRef"
      :src="animationUrl"
      playsinline webkit-playsinline
      x5-video-player-type="h5"
      x5-video-fullscreen="true"
      x5-video-orientation="portrait"
      preload="auto"
      @ended="enter"
      @canplay="onCanPlay"
      @error="onVideoError"
      class="absolute inset-0 w-full h-full object-cover"
    />
    <canvas ref="frameCanvas" class="hidden" />

    <!-- 视频未开始时：点击播放 -->
    <div v-if="animationUrl && !videoStarted" @click="startVideo"
      class="absolute inset-0 z-20 flex flex-col items-center justify-center bg-black/50 cursor-pointer">
      <p class="text-white/80 text-lg font-medium">点击屏幕进入芒果园</p>
      <p class="text-white/40 text-xs mt-2">TAP TO START</p>
    </div>

    <!-- 视频已开始 |
    <button v-if="animationUrl && videoStarted" @click="enter"
      class="absolute bottom-16 left-1/2 -translate-x-1/2 px-12 py-4 rounded-full bg-white/15 backdrop-blur-xl text-white font-bold text-base tracking-widest border border-white/20 shadow-xl z-10"
      style="margin-bottom: env(safe-area-inset-bottom, 0px);">
      点击进入
    </button>

    <!-- 无动画，纯海报 -->
    <button v-if="!animationUrl && posterUrl" @click="enter"
      class="absolute bottom-16 left-1/2 -translate-x-1/2 px-12 py-4 rounded-full bg-white/15 backdrop-blur-xl text-white font-bold text-base tracking-widest border border-white/20 shadow-xl z-10"
      style="margin-bottom: env(safe-area-inset-bottom, 0px);">
      点击进入
    </button>

    <!-- 无动画也无海报：纯文字 -->
    <div v-if="!animationUrl && !posterUrl" class="flex flex-col items-center justify-center h-full">
      <div class="text-6xl mb-6 animate-bounce">🥭</div>
      <h1 class="text-white text-3xl font-bold mb-2">Candice's Mango Ode</h1>
      <p class="text-white/60 text-sm mb-8">欢迎来到小芒的芒果园</p>
      <button @click="enter"
        class="px-10 py-4 rounded-full bg-gradient-to-r from-green-600 to-emerald-600 text-white font-bold text-base shadow-xl border-none cursor-pointer"
        style="margin-bottom: env(safe-area-inset-bottom, 0px);">
        进入芒果园 🥭
      </button>
    </div>

    <!-- Poster image when no video -->
    <img v-if="!animationUrl && posterUrl" :src="posterUrl"
      class="absolute inset-0 w-full h-full object-cover" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const emit = defineEmits(['enter', 'ready'])

const visible = ref(false)
const animationUrl = ref('')
const posterUrl = ref('')
const videoStarted = ref(false)
const videoRef = ref(null)
const frameCanvas = ref(null)

function startVideo() {
  videoStarted.value = true
  const video = videoRef.value
  if (video) video.play().catch(() => {})
}

function onCanPlay() {
  const video = videoRef.value
  if (video) {
    video.play().then(() => { videoStarted.value = true }).catch(() => {})
    // Android 微信内置浏览器
    if (typeof window.WeixinJSBridge !== 'undefined') {
      document.addEventListener('WeixinJSBridgeReady', () => {
        if (videoRef.value) videoRef.value.play().catch(() => {})
      }, { once: true })
    }
  }
}

function onVideoError() {
  // 视频加载失败，隐藏视频，显示纯文字兜底
  animationUrl.value = ''
}

function enter() {
  visible.value = false
  localStorage.setItem('intro_dismissed', '1')
  // 通知音乐播放器：用户已交互
  window.dispatchEvent(new CustomEvent('intro-enter'))
}

onMounted(async () => {
  // 每次打开都显示欢迎页（用 sessionStorage 代替 localStorage，每次浏览器会话最多一次）
  if (sessionStorage.getItem('intro_shown')) {
    emit('ready')
    return
  }

  try {
    const r1 = await fetch('/api/public/config/intro_animation_url')
    const j1 = await r1.json()
    if (j1.code === 200 && j1.data) animationUrl.value = j1.data
  } catch {}
  try {
    const r2 = await fetch('/api/public/config/intro_poster_url')
    const j2 = await r2.json()
    if (j2.code === 200 && j2.data) posterUrl.value = j2.data
  } catch {}

  visible.value = true
  sessionStorage.setItem('intro_shown', '1')
  emit('ready')
})
</script>
