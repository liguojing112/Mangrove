<template>
  <div class="birthday-page min-h-screen bg-gray-950 text-white overflow-x-hidden">
    <!-- ═══════════════════════════════════════════════════════════════
         HERO SECTION - Full viewport immersive countdown
    ═══════════════════════════════════════════════════════════════ -->
    <section class="hero relative min-h-screen flex flex-col items-center justify-center overflow-hidden">
      <!-- Particle background -->
      <div class="particle-field absolute inset-0 pointer-events-none">
        <div
          v-for="i in 28"
          :key="'p-' + i"
          class="particle absolute rounded-full"
          :style="particleStyle(i)"
        />
      </div>

      <!-- Radial glow behind countdown -->
      <div class="absolute inset-0 flex items-center justify-center pointer-events-none">
        <div class="w-[600px] h-[600px] rounded-full bg-amber-500/5 blur-[120px]" />
      </div>

      <!-- Main content -->
      <div class="relative z-10 text-center px-4">
        <!-- Crown sparkle -->
        <div class="mb-6 flex items-center justify-center gap-2">
          <Sparkles class="w-5 h-5 text-amber-400/60 animate-pulse" />
          <span class="text-amber-400/80 text-sm tracking-[0.3em] uppercase font-light">Special Day</span>
          <Sparkles class="w-5 h-5 text-amber-400/60 animate-pulse" />
        </div>

        <!-- Label -->
        <p class="text-amber-400/80 text-lg tracking-wider mb-8 font-light">
          距离小芒生日还有
        </p>

        <!-- Birthday Today? -->
        <div v-if="isBirthdayToday" class="mb-8">
          <p class="text-4xl md:text-5xl font-bold bg-gradient-to-r from-amber-400 via-yellow-300 to-amber-400 bg-clip-text text-transparent animate-pulse">
            🎂 今天是小芒的生日！
          </p>
          <div class="mt-4 flex items-center justify-center gap-3">
            <span class="text-6xl">🎉</span>
            <span class="text-6xl">🎈</span>
            <span class="text-6xl">🎁</span>
          </div>
        </div>

        <!-- Countdown -->
        <div v-else class="flex items-end justify-center gap-4 md:gap-8 mb-12">
          <CountdownUnit :value="countdown.days" label="天" large />
          <span class="text-amber-500/40 text-5xl font-thin mb-4">:</span>
          <CountdownUnit :value="countdown.hours" label="时" />
          <span class="text-amber-500/40 text-5xl font-thin mb-4">:</span>
          <CountdownUnit :value="countdown.minutes" label="分" />
          <span class="text-amber-500/40 text-5xl font-thin mb-4">:</span>
          <CountdownUnit :value="countdown.seconds" label="秒" />
        </div>

        <!-- Artist name -->
        <div v-if="artist" class="mb-6">
          <div class="inline-flex items-center gap-3 px-6 py-3 rounded-full bg-white/5 border border-white/10 backdrop-blur-sm">
            <Cake class="w-5 h-5 text-amber-400" />
            <span class="text-gray-300 font-medium">{{ artist.name }} · {{ artist.birthdayDisplay }}</span>
            <Star class="w-5 h-5 text-amber-400" />
          </div>
        </div>
      </div>

      <!-- Scroll indicator -->
      <div class="absolute bottom-8 left-1/2 -translate-x-1/2 flex flex-col items-center gap-2 text-gray-500 animate-bounce z-10">
        <span class="text-xs tracking-widest uppercase">向下探索</span>
        <ChevronDown class="w-5 h-5" />
      </div>
    </section>

    <!-- ═══════════════════════════════════════════════════════════════
         PHOTO GALLERY - Masonry grid
    ═══════════════════════════════════════════════════════════════ -->
    <section class="py-24 px-4 md:px-8 max-w-7xl mx-auto">
      <!-- Section header -->
      <div class="text-center mb-16">
        <div class="inline-flex items-center gap-2 mb-4">
          <Camera class="w-5 h-5 text-amber-400/60" />
          <span class="text-amber-400/60 text-sm tracking-[0.2em] uppercase font-light">Moments</span>
        </div>
        <h2 class="text-3xl md:text-4xl font-light text-white mb-3">精彩瞬间</h2>
        <p class="text-amber-400/60 font-light">站姐镜头下的小芒</p>
      </div>

      <!-- Masonry grid -->
      <div class="columns-2 md:columns-3 lg:columns-4 gap-3">
        <div
          v-for="(photo, i) in galleryPhotos"
          :key="'photo-' + i"
          class="gallery-card group relative mb-3 rounded-xl overflow-hidden cursor-pointer break-inside-avoid"
          :class="photo.ratio"
        >
          <!-- Gradient placeholder with emoji -->
          <div
            class="absolute inset-0 transition-all duration-500 group-hover:scale-110"
            :style="{ background: photo.bg }"
          />
          <div class="absolute inset-0 flex items-center justify-center text-5xl opacity-30 group-hover:opacity-50 transition-opacity">
            {{ photo.emoji }}
          </div>

          <!-- Glow border on hover -->
          <div class="absolute inset-0 rounded-xl border border-transparent group-hover:border-amber-500/30 transition-all duration-300" />

          <!-- Heart overlay -->
          <div class="absolute top-3 right-3 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
            <div class="w-8 h-8 rounded-full bg-black/40 backdrop-blur-sm flex items-center justify-center">
              <Heart class="w-4 h-4 text-rose-400" />
            </div>
          </div>

          <!-- Caption -->
          <div class="absolute bottom-0 left-0 right-0 p-3 bg-gradient-to-t from-black/60 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300">
            <p class="text-xs text-gray-300 font-light">{{ photo.caption }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- ═══════════════════════════════════════════════════════════════
         BIRTHDAY WISH WALL
    ═══════════════════════════════════════════════════════════════ -->
    <section class="py-24 px-4 md:px-8 bg-gray-900/30">
      <div class="max-w-4xl mx-auto">
        <!-- Section header -->
        <div class="text-center mb-12">
          <div class="inline-flex items-center gap-2 mb-4">
            <Gift class="w-5 h-5 text-amber-400/60" />
            <span class="text-amber-400/60 text-sm tracking-[0.2em] uppercase font-light">Wishes</span>
          </div>
          <h2 class="text-3xl md:text-4xl font-light text-white mb-3">生日祝福</h2>
          <p class="text-amber-400/60 font-light mb-8">为你写下最真挚的祝福</p>

          <!-- Submit button -->
          <button
            @click="showWishModal = true"
            class="inline-flex items-center gap-2 bg-amber-500 hover:bg-amber-400 text-gray-900 rounded-full px-8 py-3 font-medium transition-all duration-200 hover:shadow-[0_0_30px_rgba(245,158,11,0.3)] active:scale-95"
          >
            <Send class="w-4 h-4" />
            写下祝福
          </button>
        </div>

        <!-- Wish cards grid -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div
            v-for="(wish, i) in wishes"
            :key="'wish-' + i"
            class="wish-card bg-gray-900/80 backdrop-blur-sm rounded-2xl border border-gray-800 p-5 hover:border-amber-500/20 transition-all duration-300 group"
          >
            <div class="flex items-start gap-3 mb-3">
              <!-- Avatar -->
              <div
                class="w-10 h-10 rounded-full flex-shrink-0 flex items-center justify-center text-lg font-medium"
                :style="{ background: wish.avatarBg, color: wish.avatarColor }"
              >
                {{ wish.author.charAt(0) }}
              </div>
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2">
                  <span class="text-sm font-medium text-white">{{ wish.author }}</span>
                  <span v-if="wish.badge" class="text-xs px-2 py-0.5 rounded-full bg-amber-500/10 text-amber-400 border border-amber-500/20">
                    {{ wish.badge }}
                  </span>
                </div>
                <span class="text-xs text-gray-500">{{ wish.time }}</span>
              </div>
            </div>

            <!-- Content -->
            <p class="text-gray-300 text-sm leading-relaxed mb-4 pl-[52px]">{{ wish.content }}</p>

            <!-- Like button -->
            <div class="flex justify-end">
              <button
                @click="toggleLike(i)"
                class="flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs transition-all duration-200"
                :class="wish.liked
                  ? 'bg-rose-500/10 text-rose-400 border border-rose-500/20'
                  : 'bg-white/5 text-gray-500 border border-transparent hover:bg-white/10 hover:text-gray-300'"
              >
                <Heart class="w-3.5 h-3.5" :class="wish.liked && 'fill-current'" />
                {{ wish.likes }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ═══════════════════════════════════════════════════════════════
         BACKGROUND MUSIC PLAYER - Fixed bottom-right
    ═══════════════════════════════════════════════════════════════ -->
    <div class="fixed bottom-6 right-6 z-40">
      <!-- Collapsed state -->
      <button
        v-if="!musicExpanded"
        @click="musicExpanded = true"
        class="w-12 h-12 rounded-full bg-gray-900/90 border border-gray-700/50 backdrop-blur-md flex items-center justify-center text-amber-400 hover:bg-gray-800/90 hover:border-amber-500/30 transition-all duration-300 shadow-lg hover:shadow-amber-500/10"
      >
        <Music class="w-5 h-5" />
      </button>

      <!-- Expanded state -->
      <div
        v-else
        class="bg-gray-900/95 border border-gray-700/50 backdrop-blur-md rounded-2xl p-4 shadow-xl w-64"
      >
        <div class="flex items-center justify-between mb-3">
          <div class="flex items-center gap-2 min-w-0">
            <div class="w-8 h-8 rounded-lg bg-amber-500/10 flex items-center justify-center flex-shrink-0">
              <Music class="w-4 h-4 text-amber-400" />
            </div>
            <div class="min-w-0">
              <p class="text-xs text-amber-400 font-medium truncate">正在播放</p>
              <p class="text-xs text-gray-400 truncate">Happy Birthday</p>
            </div>
          </div>
          <button
            @click="musicExpanded = false"
            class="w-6 h-6 rounded-full bg-white/5 flex items-center justify-center text-gray-500 hover:text-gray-300 transition-colors"
          >
            <span class="text-xs">✕</span>
          </button>
        </div>

        <!-- Progress bar -->
        <div class="w-full h-1 bg-gray-800 rounded-full mb-3 overflow-hidden">
          <div class="h-full bg-amber-500 rounded-full transition-all duration-1000" :style="{ width: musicProgress + '%' }" />
        </div>

        <div class="flex items-center justify-between text-xs text-gray-500 mb-3">
          <span>{{ formatTime(musicCurrentTime) }}</span>
          <span>3:42</span>
        </div>

        <!-- Controls -->
        <div class="flex items-center justify-center gap-4">
          <button
            @click="musicMuted = !musicMuted"
            class="w-10 h-10 rounded-full bg-white/5 flex items-center justify-center text-gray-400 hover:text-amber-400 hover:bg-amber-500/10 transition-all duration-200"
          >
            <VolumeX v-if="musicMuted" class="w-4 h-4" />
            <Volume2 v-else class="w-4 h-4" />
          </button>
          <button
            @click="musicPlaying = !musicPlaying"
            class="w-12 h-12 rounded-full bg-amber-500 flex items-center justify-center text-gray-900 hover:bg-amber-400 transition-all duration-200 shadow-lg shadow-amber-500/20"
          >
            <span v-if="musicPlaying" class="text-lg">⏸</span>
            <span v-else class="text-lg">▶</span>
          </button>
        </div>
      </div>
    </div>

    <!-- ═══════════════════════════════════════════════════════════════
         WISH SUBMIT MODAL
    ═══════════════════════════════════════════════════════════════ -->
    <Teleport to="body">
      <Transition name="modal">
        <div
          v-if="showWishModal"
          class="fixed inset-0 z-50 flex items-center justify-center p-4"
          @click.self="showWishModal = false"
        >
          <!-- Overlay -->
          <div class="absolute inset-0 bg-black/80 backdrop-blur-sm" />

          <!-- Modal card -->
          <div class="relative bg-gray-900 rounded-2xl p-6 max-w-md w-full border border-gray-800 shadow-2xl">
            <!-- Close button -->
            <button
              @click="showWishModal = false"
              class="absolute top-4 right-4 w-8 h-8 rounded-full bg-white/5 flex items-center justify-center text-gray-500 hover:text-gray-300 hover:bg-white/10 transition-all"
            >
              <span class="text-sm">✕</span>
            </button>

            <!-- Header -->
            <div class="mb-6">
              <div class="w-12 h-12 rounded-2xl bg-amber-500/10 border border-amber-500/20 flex items-center justify-center mb-3">
                <Gift class="w-6 h-6 text-amber-400" />
              </div>
              <h3 class="text-xl font-medium text-white mb-1">写下你的祝福</h3>
              <p class="text-sm text-gray-500">让小芒感受到你的爱</p>
            </div>

            <!-- Form -->
            <div class="space-y-4">
              <div>
                <textarea
                  v-model="newWish.content"
                  rows="4"
                  maxlength="200"
                  placeholder="写下你对小芒的生日祝福..."
                  class="w-full bg-gray-800/50 border border-gray-700/50 rounded-xl px-4 py-3 text-sm text-gray-200 placeholder-gray-600 focus:outline-none focus:border-amber-500/50 focus:ring-1 focus:ring-amber-500/20 resize-none transition-all"
                />
                <div class="text-right text-xs text-gray-600 mt-1">{{ newWish.content.length }}/200</div>
              </div>

              <div>
                <input
                  v-model="newWish.author"
                  type="text"
                  placeholder="署名（选填）"
                  maxlength="20"
                  class="w-full bg-gray-800/50 border border-gray-700/50 rounded-xl px-4 py-3 text-sm text-gray-200 placeholder-gray-600 focus:outline-none focus:border-amber-500/50 focus:ring-1 focus:ring-amber-500/20 transition-all"
                />
              </div>

              <button
                @click="submitWish"
                :disabled="!newWish.content.trim()"
                class="w-full bg-amber-500 hover:bg-amber-400 disabled:bg-gray-700 disabled:text-gray-500 text-gray-900 font-medium rounded-xl px-6 py-3 transition-all duration-200 hover:shadow-[0_0_30px_rgba(245,158,11,0.2)] active:scale-[0.98]"
              >
                送出祝福 🎂
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- ═══════════════════════════════════════════════════════════════
         FOOTER
    ═══════════════════════════════════════════════════════════════ -->
    <footer class="py-12 text-center border-t border-gray-800/50">
      <p class="text-gray-600 text-sm">© 青芒纪 · 生日特别企划</p>
      <p class="text-gray-700 text-xs mt-2">Made with 💚 by 站姐</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, defineComponent, h } from 'vue'
import { Heart, Send, Camera, Star, Sparkles, Music, Volume2, VolumeX, ChevronDown, Gift, Cake, Clock } from 'lucide-vue-next'

// ─── Countdown Unit Sub-Component ──────────────────────────────
const CountdownUnit = defineComponent({
  props: {
    value: { type: Number, required: true },
    label: { type: String, required: true },
    large: { type: Boolean, default: false }
  },
  setup(props) {
    return () => h('div', { class: 'text-center' }, [
      h('div', {
        class: props.large
          ? 'text-7xl md:text-8xl font-bold bg-gradient-to-b from-white to-gray-400 bg-clip-text text-transparent tabular-nums leading-none'
          : 'text-4xl md:text-5xl font-bold bg-gradient-to-b from-white to-gray-400 bg-clip-text text-transparent tabular-nums leading-none',
        style: { fontFamily: '"DIN Alternate", "Roboto Mono", monospace' }
      }, String(props.value).padStart(2, '0')),
      h('div', {
        class: props.large
          ? 'text-amber-400/60 text-sm mt-3 tracking-widest font-light'
          : 'text-amber-400/40 text-xs mt-2 tracking-widest font-light'
      }, props.label)
    ])
  }
})

// ─── State ──────────────────────────────────────────────────────
const artist = ref(null)
const countdown = reactive({ days: 0, hours: 0, minutes: 0, seconds: 0 })
const isBirthdayToday = ref(false)
let countdownTimer = null

// Music player
const musicExpanded = ref(false)
const musicPlaying = ref(false)
const musicMuted = ref(false)
const musicProgress = ref(0)
const musicCurrentTime = ref(0)
let musicTimer = null

// Wish wall
const showWishModal = ref(false)
const newWish = reactive({ content: '', author: '' })

const wishes = ref([
  {
    author: '芒果味的星星',
    avatarBg: 'linear-gradient(135deg, #f59e0b, #d97706)',
    avatarColor: '#fff',
    time: '3 小时前',
    badge: '铁粉',
    content: '小芒生日快乐！🎉 从你出道那天起，你就是我生命中最闪亮的星。愿你的每一年都如今日般灿烂，我们会一直在这里，为你点亮每一片星光。',
    likes: 1247,
    liked: false
  },
  {
    author: '青芒纪守护者',
    avatarBg: 'linear-gradient(135deg, #10b981, #059669)',
    avatarColor: '#fff',
    time: '5 小时前',
    badge: '站长',
    content: '又陪你走过了一年！记得第一次在舞台上看到你的时候，就觉得这个少年眼里有光。生日快乐，未来的路我们一起走。💚',
    likes: 892,
    liked: false
  },
  {
    author: '小芒的甜甜圈',
    avatarBg: 'linear-gradient(135deg, #ec4899, #be185d)',
    avatarColor: '#fff',
    time: '8 小时前',
    content: '祝你生日快乐！永远做自己就好，你的笑容就是我们最大的动力。新的一岁，要天天开心哦～🎂',
    likes: 634,
    liked: false
  },
  {
    author: '星河拾荒者',
    avatarBg: 'linear-gradient(135deg, #6366f1, #4f46e5)',
    avatarColor: '#fff',
    time: '12 小时前',
    content: '你是我追星路上最温柔的意外。感谢你让平凡的日子都有了期待。生日快乐，愿所有美好都如期而至。✨',
    likes: 456,
    liked: false
  },
  {
    author: '芒果核研究所',
    avatarBg: 'linear-gradient(135deg, #f97316, #ea580c)',
    avatarColor: '#fff',
    time: '1 天前',
    badge: '元老',
    content: '从小透明到现在的大明星，你的努力我们都看在眼里。生日快乐！希望你在新的一岁里，能够做更多自己想做的事情，我们会永远支持你！',
    likes: 1023,
    liked: false
  },
  {
    author: '追光的小芒虫',
    avatarBg: 'linear-gradient(135deg, #06b6d4, #0891b2)',
    avatarColor: '#fff',
    time: '1 天前',
    content: '这是我陪你过的第 3 个生日了！每一年都比上一年更爱你。生日快乐小芒，你值得世界上所有的美好！🎈',
    likes: 789,
    liked: false
  }
])

// Gallery photos
const galleryPhotos = [
  { ratio: 'aspect-[3/4]', bg: 'linear-gradient(135deg, #1a1a2e, #16213e, #0f3460)', emoji: '🎤', caption: '演唱会精彩瞬间' },
  { ratio: 'aspect-square', bg: 'linear-gradient(135deg, #2d1b69, #11998e)', emoji: '🌟', caption: '舞台上的光芒' },
  { ratio: 'aspect-[4/3]', bg: 'linear-gradient(135deg, #0c0c0c, #1a1a2e)', emoji: '🎵', caption: '录音室日常' },
  { ratio: 'aspect-[3/4]', bg: 'linear-gradient(135deg, #200122, #6f0000)', emoji: '🎭', caption: '幕后花絮' },
  { ratio: 'aspect-square', bg: 'linear-gradient(135deg, #0f2027, #203a43, #2c5364)', emoji: '📸', caption: '站姐生图直出' },
  { ratio: 'aspect-[4/3]', bg: 'linear-gradient(135deg, #141e30, #243b55)', emoji: '✨', caption: '粉丝见面会' },
  { ratio: 'aspect-[3/4]', bg: 'linear-gradient(135deg, #1f1c2c, #928dab)', emoji: '🎬', caption: 'MV拍摄现场' },
  { ratio: 'aspect-square', bg: 'linear-gradient(135deg, #000428, #004e92)', emoji: '🏆', caption: '获奖时刻' },
  { ratio: 'aspect-[4/3]', bg: 'linear-gradient(135deg, #1a0533, #4a0e4e)', emoji: '🎶', caption: '新歌发布日' },
  { ratio: 'aspect-[3/4]', bg: 'linear-gradient(135deg, #0d1117, #161b22)', emoji: '💫', caption: '舞台侧拍' },
  { ratio: 'aspect-square', bg: 'linear-gradient(135deg, #1c1c1c, #383838)', emoji: '🎄', caption: '节日特别企划' },
  { ratio: 'aspect-[4/3]', bg: 'linear-gradient(135deg, #0a0e27, #1a1a4e)', emoji: '🌈', caption: '综艺录制花絮' },
]

// ─── Methods ──────────────────────────────────────────────────
function calculateCountdown() {
  const now = new Date()
  const thisYear = now.getFullYear()

  // Try to use artist's birthday, fallback to August 15
  let birthdayMonth = 7 // August (0-indexed)
  let birthdayDay = 15

  if (artist.value?.birthday) {
    const bd = new Date(artist.value.birthday)
    birthdayMonth = bd.getMonth()
    birthdayDay = bd.getDate()
  }

  let nextBirthday = new Date(thisYear, birthdayMonth, birthdayDay, 0, 0, 0)

  // If birthday already passed this year, use next year
  if (now > nextBirthday) {
    nextBirthday = new Date(thisYear + 1, birthdayMonth, birthdayDay, 0, 0, 0)
  }

  // Check if today is the birthday
  const todayMonth = now.getMonth()
  const todayDate = now.getDate()
  isBirthdayToday.value = (todayMonth === birthdayMonth && todayDate === birthdayDay)

  const diff = nextBirthday.getTime() - now.getTime()
  const totalSeconds = Math.floor(diff / 1000)

  countdown.days = Math.floor(totalSeconds / 86400)
  countdown.hours = Math.floor((totalSeconds % 86400) / 3600)
  countdown.minutes = Math.floor((totalSeconds % 3600) / 60)
  countdown.seconds = totalSeconds % 60
}

function particleStyle(index) {
  const size = 2 + (index % 4) * 1.5
  const left = (index * 3.7) % 100
  const delay = (index * 0.8) % 12
  const duration = 8 + (index % 5) * 2
  const opacity = 0.15 + (index % 3) * 0.1
  return {
    width: size + 'px',
    height: size + 'px',
    left: left + '%',
    bottom: '-10px',
    opacity: opacity,
    background: index % 3 === 0
      ? 'radial-gradient(circle, rgba(245,158,11,0.6), transparent)'
      : 'radial-gradient(circle, rgba(255,255,255,0.4), transparent)',
    animation: `float-up ${duration}s ease-in-out ${delay}s infinite`
  }
}

function toggleLike(index) {
  const wish = wishes.value[index]
  wish.liked = !wish.liked
  wish.likes += wish.liked ? 1 : -1
}

function submitWish() {
  if (!newWish.content.trim()) return

  wishes.value.unshift({
    author: newWish.author.trim() || '匿名粉丝',
    avatarBg: 'linear-gradient(135deg, #f59e0b, #d97706)',
    avatarColor: '#fff',
    time: '刚刚',
    content: newWish.content.trim(),
    likes: 0,
    liked: false
  })

  newWish.content = ''
  newWish.author = ''
  showWishModal.value = false
}

function formatTime(seconds) {
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}:${String(s).padStart(2, '0')}`
}

// ─── Lifecycle ──────────────────────────────────────────────────
onMounted(async () => {
  // Start countdown
  calculateCountdown()
  countdownTimer = setInterval(calculateCountdown, 1000)

  // Music timer simulation
  musicTimer = setInterval(() => {
    if (musicPlaying.value && !musicMuted.value) {
      musicCurrentTime.value = (musicCurrentTime.value + 1) % 222
      musicProgress.value = (musicCurrentTime.value / 222) * 100
    }
  }, 1000)

  // Fetch artist data
  try {
    const res = await fetch('/api/birthday/artist/1')
    if (res.ok) {
      artist.value = await res.json()
    } else {
      // Fallback artist data
      artist.value = {
        name: '小芒',
        birthday: '2025-08-15',
        birthdayDisplay: '8月15日'
      }
    }
  } catch {
    artist.value = {
      name: '小芒',
      birthday: '2025-08-15',
      birthdayDisplay: '8月15日'
    }
  }
})

onUnmounted(() => {
  if (countdownTimer) clearInterval(countdownTimer)
  if (musicTimer) clearInterval(musicTimer)
})
</script>

<style scoped>
/* ─── Particle Float Animation ─────────────────────────────────── */
@keyframes float-up {
  0% {
    transform: translateY(0) translateX(0) scale(1);
    opacity: 0;
  }
  10% {
    opacity: var(--particle-opacity, 0.3);
  }
  90% {
    opacity: var(--particle-opacity, 0.3);
  }
  100% {
    transform: translateY(-100vh) translateX(30px) scale(0.5);
    opacity: 0;
  }
}

.particle {
  --particle-opacity: 0.3;
  will-change: transform, opacity;
}

/* ─── Modal Transitions ────────────────────────────────────────── */
.modal-enter-active {
  transition: all 0.3s ease-out;
}
.modal-leave-active {
  transition: all 0.2s ease-in;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
.modal-enter-from > div:last-child,
.modal-leave-to > div:last-child {
  transform: scale(0.95) translateY(10px);
}

/* ─── Gallery card subtle shimmer ──────────────────────────────── */
.gallery-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    transparent 40%,
    rgba(255, 255, 255, 0.03) 50%,
    transparent 60%
  );
  opacity: 0;
  transition: opacity 0.5s;
}
.gallery-card:hover::after {
  opacity: 1;
}

/* ─── Custom scrollbar for dark theme ──────────────────────────── */
.birthday-page::-webkit-scrollbar {
  width: 6px;
}
.birthday-page::-webkit-scrollbar-track {
  background: #030712;
}
.birthday-page::-webkit-scrollbar-thumb {
  background: #374151;
  border-radius: 3px;
}
.birthday-page::-webkit-scrollbar-thumb:hover {
  background: #4b5563;
}

/* ─── Tabular nums for countdown ───────────────────────────────── */
.tabular-nums {
  font-variant-numeric: tabular-nums;
}
</style>
