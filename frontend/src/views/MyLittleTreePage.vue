<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24 pb-16 relative">
    <!-- 背景装饰：五线谱纹理 + 浮动音符 -->
    <div class="absolute inset-0 pointer-events-none select-none z-0" aria-hidden="true">
      <svg class="absolute top-0 left-0 w-full h-full opacity-[0.08]" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <pattern id="staff-lines-little-tree" x="0" y="0" width="100%" height="150" patternUnits="userSpaceOnUse">
            <line x1="0" y1="20" x2="100%" y2="20" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="45" x2="100%" y2="45" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="70" x2="100%" y2="70" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="95" x2="100%" y2="95" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="120" x2="100%" y2="120" stroke="#2E8B57" stroke-width="1.5"/>
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill="url(#staff-lines-little-tree)"/>
      </svg>
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

    <!-- Not Logged In -->
    <div v-if="!isLoggedIn" class="max-w-md mx-auto relative z-10">
      <div class="card p-8 text-center">
        <div class="bg-mangrove-100 rounded-2xl h-48 flex items-center justify-center mb-6">
          <TreePine class="w-20 h-20 text-mangrove-400/60" />
        </div>
        <p class="text-gray-500 mb-6">登录后查看你的专属芒果树</p>
        <router-link to="/login" class="btn-primary inline-block">立即登录</router-link>
      </div>
    </div>

    <!-- Logged In -->
    <div v-else class="relative z-10">
      <!-- Header -->
      <div class="flex items-center gap-3 mb-4">
        <router-link to="/tree" class="text-gray-400 hover:text-mangrove-600 transition-colors">
          ← 返回芒果树
        </router-link>
      </div>
      <div class="flex items-center gap-3 mb-6">
        <h1 class="section-title">🌱 我的小树</h1>
        <span class="bg-mangrove-100 text-mangrove-700 rounded-full px-3 py-0.5 text-xs font-medium">
          {{ treeStage.label }}
        </span>
      </div>

      <!-- Tree Name -->
      <div class="mb-4 flex items-center gap-2">
        <template v-if="editingName">
          <input
            v-model="treeNameInput"
            maxlength="20"
            class="border border-gray-300 rounded-lg px-3 py-1.5 text-sm w-48 focus:outline-none focus:ring-2 focus:ring-mangrove-500"
            placeholder="给你的小树取个名字"
            @keyup.enter="saveTreeName"
          />
          <button class="text-xs text-mangrove-600 hover:text-mangrove-800" @click="saveTreeName">保存</button>
          <button class="text-xs text-gray-400 hover:text-gray-600" @click="editingName = false">取消</button>
        </template>
        <template v-else>
          <span class="text-sm text-gray-600">
            🌳 {{ tree.treeName || '点击取名' }}
          </span>
          <button class="text-xs text-gray-400 hover:text-mangrove-600" @click="editingName = true; treeNameInput = tree.treeName || ''">
            <Edit2 class="w-3 h-3" />
          </button>
        </template>
      </div>

      <!-- EXP Bar -->
      <div class="mb-6">
        <div class="bg-gray-200 rounded-full h-3 w-full">
          <div class="bg-mangrove-600 rounded-full h-3 transition-all" :style="{ width: expPercent + '%' }"></div>
        </div>
        <p class="text-xs text-gray-400 mt-1">{{ tree.experience }} / {{ requiredExp }} EXP</p>
      </div>

      <!-- Tree Display -->
      <div class="relative rounded-2xl h-[380px] sm:h-[420px] flex items-end justify-center mb-8 overflow-hidden">
        <img v-if="littleTreeBgUrl" :src="littleTreeBgUrl" alt="" class="absolute inset-0 w-full h-full object-cover" />
        <div v-else class="absolute inset-0 bg-gradient-to-b from-mangrove-50 to-mangrove-100" />
        <div class="absolute bottom-0 left-0 right-0 h-16 bg-gradient-to-t from-mangrove-200/50 to-transparent rounded-b-2xl" />

        <!-- 阳光光斑 -->
        <div class="sunbeam sunbeam-1"></div>
        <div class="sunbeam sunbeam-2"></div>
        <div class="sunbeam sunbeam-3"></div>

        <!-- 飘落的音符粒子 -->
        <div class="tree-note-particle tnp-1">♪</div>
        <div class="tree-note-particle tnp-2">♫</div>
        <div class="tree-note-particle tnp-3">♬</div>
        <div class="tree-note-particle tnp-4">♩</div>
        <div class="tree-note-particle tnp-5">♪</div>
        <div class="tree-note-particle tnp-6">𝄞</div>
        <div class="tree-note-particle tnp-7">♫</div>
        <div class="tree-note-particle tnp-8">♬</div>

        <div class="relative z-10 mb-4 w-full h-full flex items-end justify-center tree-wrapper">
          <img
            :src="treeImage"
            :alt="treeName || '芒果树'"
            class="h-full w-auto object-contain max-w-none tree-img-sway"
            @error="treeImageError = true"
          />
          <svg v-if="treeImageError" viewBox="0 0 280 340" width="220" height="270" class="tree-img-sway">
            <path d="M133 340 Q136 270 132 220 Q130 185 138 160 Q146 185 144 220 Q140 270 142 340Z" fill="#8B6914" stroke="#6B5210" stroke-width="0.8"/>
            <circle cx="140" cy="110" r="60" fill="#2E8B57" opacity="0.9">
              <animate attributeName="r" values="60;62;60" dur="4s" repeatCount="indefinite"/>
            </circle>
            <circle cx="105" cy="95" r="48" fill="#3CA55C" opacity="0.85">
              <animate attributeName="r" values="48;50;48" dur="3.5s" repeatCount="indefinite"/>
            </circle>
            <circle cx="175" cy="95" r="48" fill="#3CA55C" opacity="0.85">
              <animate attributeName="r" values="48;50;48" dur="4.2s" repeatCount="indefinite"/>
            </circle>
            <circle cx="140" cy="75" r="52" fill="#4DB870" opacity="0.9">
              <animate attributeName="r" values="52;54;52" dur="3.8s" repeatCount="indefinite"/>
            </circle>
            <circle cx="110" cy="68" r="38" fill="#5CC87A" opacity="0.8">
              <animate attributeName="r" values="38;40;38" dur="3.2s" repeatCount="indefinite"/>
            </circle>
            <circle cx="170" cy="68" r="38" fill="#5CC87A" opacity="0.8">
              <animate attributeName="r" values="38;40;38" dur="4.5s" repeatCount="indefinite"/>
            </circle>
            <circle cx="140" cy="55" r="42" fill="#6DD88A" opacity="0.7">
              <animate attributeName="r" values="42;44;42" dur="3.6s" repeatCount="indefinite"/>
            </circle>
          </svg>

          <Sparkles v-if="tree.level >= 3" class="sparkle s1 w-4 h-4 text-mangrove-400" />
          <Star v-if="tree.level >= 5" class="sparkle s2 w-3 h-3 text-yellow-400" />
          <Sparkles v-if="tree.level >= 10" class="sparkle s3 w-5 h-5 text-amber-300" />
        </div>
      </div>

      <!-- Sign In Button -->
      <div class="flex justify-center mb-4">
        <button
          class="btn-primary text-base px-8 py-3"
          :class="{ 'opacity-50 cursor-not-allowed': checkedIn }"
          :disabled="checkedIn"
          @click="doCheckin"
        >
          {{ checkedIn ? '今日已浇水 ✓' : '💧 今日浇水' }}
        </button>
      </div>
      <p v-if="checkinMsg" class="text-center text-sm text-mangrove-600 mb-6">{{ checkinMsg }}</p>
      <p v-else class="text-center text-xs text-gray-400 mb-6">每日浇水可累积积分，解锁更多福利</p>

      <!-- 帮别人打卡 -->
      <div class="flex justify-center mb-6">
        <div class="flex items-center gap-2 bg-gray-50 rounded-full px-4 py-2 border border-gray-200">
          <input v-model="helpUsername" placeholder="输入用户名帮TA打卡" maxlength="20"
            class="bg-transparent text-sm w-40 outline-none placeholder-gray-400" />
          <button @click="doCheckinFor" :disabled="!helpUsername.trim()"
            class="text-xs font-medium px-3 py-1.5 rounded-full transition-colors"
            :class="helpUsername.trim() ? 'bg-mangrove-500 text-white hover:bg-mangrove-600' : 'bg-gray-200 text-gray-400 cursor-not-allowed'">
            帮TA打卡
          </button>
        </div>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-4 gap-3 mb-10">
        <div class="card p-4 text-center hover:-translate-y-1 hover:shadow-xl hover:shadow-green-500/15 transition-all duration-300 cursor-default">
          <p class="text-xl font-bold text-mangrove-700">{{ tree.totalCheckins ?? 0 }} 天</p>
          <p class="text-xs text-gray-500 mt-1">累计签到</p>
        </div>
        <div class="card p-4 text-center hover:-translate-y-1 hover:shadow-xl hover:shadow-green-500/15 transition-all duration-300 cursor-default">
          <p class="text-xl font-bold text-mangrove-700">{{ tree.consecutiveDays }} 天</p>
          <p class="text-xs text-gray-500 mt-1">连续签到</p>
        </div>
        <div class="card p-4 text-center hover:-translate-y-1 hover:shadow-xl hover:shadow-green-500/15 transition-all duration-300 cursor-default">
          <p class="text-xl font-bold text-amber-600">{{ tree.points ?? 0 }}</p>
          <p class="text-xs text-gray-500 mt-1">可用积分</p>
        </div>
        <div class="card p-4 text-center hover:-translate-y-1 hover:shadow-xl hover:shadow-green-500/15 transition-all duration-300 cursor-default">
          <p class="text-xl font-bold text-mangrove-700">{{ tree.totalLikes }}</p>
          <p class="text-xs text-gray-500 mt-1">累计获赞</p>
        </div>
      </div>

      <!-- Private Journal -->
      <div class="mb-10">
        <div class="flex items-center justify-between mb-4">
          <h2 class="font-semibold text-gray-900">📖 我的成长记事</h2>
          <button class="btn-primary text-sm" @click="showJournalForm = true">写记事</button>
        </div>
        <p class="text-xs text-gray-400 mb-4">记录和小芒的每一次线下相遇瞬间，内容仅本人可见</p>

        <div v-if="showJournalForm" class="card p-4 mb-4">
          <input
            v-model="journalForm.title"
            placeholder="标题（选填）"
            class="w-full rounded-lg border border-gray-200 p-3 text-sm mb-3 focus:outline-none focus:ring-2 focus:ring-mangrove-500"
          />
          <textarea
            v-model="journalForm.content"
            placeholder="写下你的回忆..."
            class="w-full rounded-lg border border-gray-200 p-3 text-sm min-h-[100px] resize-none focus:outline-none focus:ring-2 focus:ring-mangrove-500"
          ></textarea>
          <div class="flex justify-end gap-2 mt-3">
            <button class="btn-secondary text-sm" @click="showJournalForm = false">取消</button>
            <button class="btn-primary text-sm" @click="saveJournal">保存</button>
          </div>
        </div>

        <div v-if="journals.length === 0 && !showJournalForm" class="text-center py-6 text-gray-400 text-sm">
          还没有记事，写下你的第一条回忆吧
        </div>
        <div v-else class="space-y-3">
          <div v-for="journal in journals" :key="journal.id" class="card p-4 hover:-translate-y-1 hover:shadow-xl hover:shadow-green-500/15 transition-all duration-300">
            <div class="flex items-start justify-between">
              <div class="flex-1 min-w-0">
                <h3 v-if="journal.title" class="font-medium text-gray-900">{{ journal.title }}</h3>
                <p class="text-sm text-gray-600 mt-1 whitespace-pre-wrap">{{ journal.content }}</p>
                <p class="text-xs text-gray-400 mt-2">{{ formatTime(journal.createdAt) }}</p>
              </div>
              <button class="text-gray-400 hover:text-red-500 ml-3 shrink-0" @click="deleteJournal(journal.id)">
                <Trash2 class="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 打卡积分排行榜 -->
      <div class="mb-10">
        <h2 class="font-semibold text-gray-900 mb-4">🏆 打卡积分排行榜</h2>
        <div v-if="ranking.length === 0" class="text-center py-6 text-gray-400 text-sm">暂无排名数据</div>
        <div v-else class="space-y-2">
          <div v-for="(item, idx) in ranking" :key="idx"
            class="card p-3 flex items-center gap-3 hover:-translate-y-1 hover:shadow-xl hover:shadow-green-500/15 transition-all duration-300"
            :class="idx === 0 ? 'bg-gradient-to-r from-amber-50 to-yellow-50 border-amber-200' : ''">
            <div class="w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold shrink-0"
              :class="idx === 0 ? 'bg-amber-400 text-white' : idx === 1 ? 'bg-gray-300 text-white' : idx === 2 ? 'bg-amber-600/60 text-white' : 'bg-gray-100 text-gray-500'">
              {{ idx + 1 }}
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium text-gray-800 truncate">{{ item.nickname || '匿名用户' }}</p>
              <p class="text-[10px] text-gray-400">连续 {{ item.consecutiveDays }} 天 · 共签到 {{ item.totalCheckins }} 天</p>
            </div>
            <div class="text-right shrink-0">
              <p class="text-sm font-bold text-amber-600">{{ item.totalPoints }} 积分</p>
              <p class="text-[10px] text-gray-400">Lv.{{ item.level }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Watering Popup Modal -->
    <Teleport to="body">
      <Transition name="fade">
        <div v-if="showWaterPopup" class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4" @click.self="showWaterPopup = false">
          <div class="bg-white rounded-2xl p-6 max-w-xs w-full text-center shadow-2xl animate-bounce-in">
            <div class="text-4xl mb-3">💧</div>
            <h3 class="text-lg font-bold text-gray-900 mb-2">{{ waterPopupData.title }}</h3>
            <p class="text-sm text-gray-600 mb-4 leading-relaxed">{{ waterPopupData.message }}</p>
            <p class="text-xs text-mangrove-600 mb-4">🌳 {{ treeStage.label }} · 已陪伴 {{ tree.consecutiveDays }} 天</p>
            <button class="btn-primary w-full" @click="showWaterPopup = false">开心收下～</button>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { TreePine, Sparkles, Star, Edit2, Trash2 } from 'lucide-vue-next'
import { useAuth } from '@/composables/useAuth'

const { isLoggedIn, getToken } = useAuth()

const tree = ref({
  level: 1, experience: 0, consecutiveDays: 0,
  totalLikes: 0, totalUploads: 0, lastCheckinAt: null,
  points: 0, totalPoints: 0, treeName: null
})
const checkedIn = ref(false)
const checkinMsg = ref('')
const showWaterPopup = ref(false)
const waterPopupData = ref({ title: '', message: '' })
const treeImageError = ref(false)
const editingName = ref(false)
const treeNameInput = ref('')
const littleTreeBgUrl = ref('')

const journals = ref([])
const showJournalForm = ref(false)
const journalForm = ref({ title: '', content: '' })
const ranking = ref([])
const helpUsername = ref('')

const STAGE_MAP = [
  { min: 1, max: 7, img: 'sapling', label: '光秃秃小苗' },
  { min: 8, max: 30, img: 'young-tree', label: '开花花苞期' },
  { min: 31, max: 90, img: 'shade-tree', label: '青涩小芒挂枝' },
  { min: 91, max: 180, img: 'fruit-tree', label: '满树金芒丰收' },
  { min: 181, max: Infinity, img: 'golden-tree', label: '星光满级仙芒树' },
]

const STAGE_MESSAGES = [
  { title: '💧 浇水啦！', message: '救命！小树快渴蔫啦，谢谢音符宝宝救我一命～' },
  { title: '🌸 咕嘟咕嘟', message: '咕嘟咕嘟喝饱水，马上要结小芒果咯！' },
  { title: '🥭 蓄力长大', message: '青芒正在蓄力长大，音符的爱意全吸收！' },
  { title: '🎉 大丰收！', message: '大丰收！树上挂满属于我们的小芒！' },
  { title: '✨ 超级强壮！', message: '谢谢音符的陪伴～小芒现在超级强壮！！' },
]

const requiredExp = computed(() => tree.value.level * 100)
const expPercent = computed(() => {
  if (!requiredExp.value) return 0
  return Math.min(100, (tree.value.experience / requiredExp.value) * 100)
})

const treeStageIndex = computed(() => {
  const days = tree.value.totalCheckins || 0
  if (days >= 181) return 4
  if (days >= 91) return 3
  if (days >= 31) return 2
  if (days >= 8) return 1
  return 0
})

const treeStage = computed(() => STAGE_MAP[treeStageIndex.value])
const treeImage = computed(() => `/assets/tree/${treeStage.value.img}.png?v=2`)
const treeName = computed(() => tree.value.treeName || '未命名小树')

async function saveTreeName() {
  try {
    const res = await fetch('/api/tree/my', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${getToken()}` },
      body: JSON.stringify({ treeName: treeNameInput.value })
    })
    const json = await res.json()
    if (json.code === 200) {
      tree.value.treeName = treeNameInput.value
      editingName.value = false
    }
  } catch (e) {
    console.error('保存树名失败:', e)
  }
}

async function fetchTree() {
  try {
    const res = await fetch('/api/tree/my', { headers: { 'Authorization': `Bearer ${getToken()}` } })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      tree.value = json.data
      const today = new Date().toISOString().split('T')[0]
      const lastCheckin = json.data.lastCheckinAt
      checkedIn.value = lastCheckin && lastCheckin.startsWith(today)
    }
  } catch (e) {
    console.error('获取芒果树失败:', e)
  }
}

async function doCheckin() {
  if (checkedIn.value) return
  try {
    const res = await fetch('/api/tree/checkin', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      tree.value = json.data
      checkedIn.value = true
      const stageIdx = (() => {
        const d = json.data.totalCheckins || 0
        if (d >= 181) return 4; if (d >= 91) return 3; if (d >= 31) return 2; if (d >= 8) return 1; return 0
      })()
      const msg = STAGE_MESSAGES[stageIdx]
      waterPopupData.value = { title: msg.title, message: msg.message }
      showWaterPopup.value = true
      const consecutive = json.data.consecutiveDays
      if (consecutive % 30 === 0) {
        checkinMsg.value = `🎉 连续签到${consecutive}天！获得 200 积分奖励！`
      } else if (consecutive % 7 === 0) {
        checkinMsg.value = `✨ 连续签到${consecutive}天！获得 50 积分奖励！`
      } else {
        checkinMsg.value = `💧 +10 EXP, +10 积分`
      }
    } else {
      checkinMsg.value = json.msg || '签到失败，请稍后重试'
    }
  } catch (e) {
    console.error('签到失败:', e)
  }
}

async function doCheckinFor() {
  if (!helpUsername.value.trim()) return
  try {
    const res = await fetch(`/api/tree/checkin-for/${encodeURIComponent(helpUsername.value.trim())}`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      checkinMsg.value = `✅ ${json.data}`
      helpUsername.value = ''
    } else {
      checkinMsg.value = `❌ ${json.msg || '帮打卡失败'}`
    }
  } catch (e) {
    console.error('帮打卡失败:', e)
  }
}

async function fetchJournals() {
  try {
    const res = await fetch('/api/tree/journal?page=0&size=50', { headers: { 'Authorization': `Bearer ${getToken()}` } })
    const json = await res.json()
    if (json.code === 200) journals.value = json.data?.content || []
  } catch (e) {
    console.error('获取记事失败:', e)
  }
}

async function saveJournal() {
  if (!journalForm.value.content.trim()) return
  try {
    const res = await fetch('/api/tree/journal', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${getToken()}` },
      body: JSON.stringify(journalForm.value)
    })
    const json = await res.json()
    if (json.code === 200) {
      journalForm.value = { title: '', content: '' }
      showJournalForm.value = false
      await fetchJournals()
    }
  } catch (e) {
    console.error('保存记事失败:', e)
  }
}

async function deleteJournal(id) {
  if (!confirm('确定要删除这条记事吗？')) return
  try {
    const res = await fetch(`/api/tree/journal/${id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) await fetchJournals()
  } catch (e) {
    console.error('删除记事失败:', e)
  }
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

async function fetchRanking() {
  try {
    const token = getToken()
    if (!token) return
    const res = await fetch('/api/tree/ranking', { headers: { 'Authorization': `Bearer ${token}` } })
    const json = await res.json()
    if (json.code === 200 && json.data) ranking.value = json.data
  } catch (e) {
    console.error('获取排行榜失败:', e)
  }
}

onMounted(() => {
  fetch('/api/public/config/littletree_background_url')
    .then(r => r.json())
    .then(json => { if (json.code === 200 && json.data) littleTreeBgUrl.value = json.data })
    .catch(() => {})

  if (isLoggedIn.value) {
    fetchTree()
    fetchJournals()
    fetchRanking()
  }
})
</script>

<style scoped>
.sparkle {
  position: absolute;
  animation: sparkle-float 3s ease-in-out infinite;
}
.sparkle.s1 { top: 15%; left: 20%; animation-delay: 0s; }
.sparkle.s2 { top: 25%; right: 18%; animation-delay: 1s; }
.sparkle.s3 { top: 10%; left: 50%; animation-delay: 2s; }

@keyframes sparkle-float {
  0%, 100% { opacity: 0.3; transform: translateY(0) scale(1); }
  50% { opacity: 1; transform: translateY(-8px) scale(1.2); }
}

@keyframes bounce-in {
  0% { transform: scale(0.5); opacity: 0; }
  60% { transform: scale(1.05); }
  100% { transform: scale(1); opacity: 1; }
}
.animate-bounce-in {
  animation: bounce-in 0.4s ease-out;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

/* === 浮动音符 === */
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

/* === 小树动态效果 === */

/* 树干/图片微风摆动 */
.tree-wrapper {
  transform-origin: bottom center;
}
.tree-img-sway {
  animation: treeSway 5s ease-in-out infinite;
  transform-origin: bottom center;
}
@keyframes treeSway {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(1.5deg); }
  75% { transform: rotate(-1.5deg); }
}

/* 阳光光斑 */
.sunbeam {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
  background: radial-gradient(circle, rgba(255, 223, 100, 0.25), transparent 70%);
}
.sunbeam-1 {
  width: 120px; height: 120px;
  top: 5%; left: 15%;
  animation: sunPulse 6s ease-in-out infinite;
}
.sunbeam-2 {
  width: 80px; height: 80px;
  top: 15%; right: 20%;
  animation: sunPulse 8s ease-in-out infinite 2s;
}
.sunbeam-3 {
  width: 100px; height: 100px;
  top: 8%; left: 55%;
  animation: sunPulse 7s ease-in-out infinite 4s;
}
@keyframes sunPulse {
  0%, 100% { opacity: 0; transform: scale(0.8); }
  50% { opacity: 1; transform: scale(1.2); }
}

/* 从树上飘落的音符粒子 */
.tree-note-particle {
  position: absolute;
  z-index: 5;
  color: rgba(46, 139, 87, 0.6);
  pointer-events: none;
  text-shadow: 0 0 6px rgba(46, 139, 87, 0.4);
  animation: noteFall linear infinite;
}
.tnp-1 { left: 35%; font-size: 1.2rem; animation-duration: 8s; animation-delay: 0s; }
.tnp-2 { left: 50%; font-size: 1rem; animation-duration: 10s; animation-delay: 1.5s; }
.tnp-3 { left: 60%; font-size: 1.4rem; animation-duration: 7s; animation-delay: 3s; }
.tnp-4 { left: 40%; font-size: 0.9rem; animation-duration: 9s; animation-delay: 4.5s; }
.tnp-5 { left: 55%; font-size: 1.1rem; animation-duration: 11s; animation-delay: 2s; }
.tnp-6 { left: 45%; font-size: 1.3rem; animation-duration: 8.5s; animation-delay: 5.5s; }
.tnp-7 { left: 65%; font-size: 1rem; animation-duration: 9.5s; animation-delay: 1s; }
.tnp-8 { left: 30%; font-size: 0.8rem; animation-duration: 12s; animation-delay: 6s; }

@keyframes noteFall {
  0% {
    top: 20%;
    opacity: 0;
    transform: translateX(0) rotate(0deg) scale(0.5);
  }
  10% {
    opacity: 0.7;
    transform: translateX(10px) rotate(15deg) scale(1);
  }
  30% {
    opacity: 0.5;
    transform: translateX(-15px) rotate(-10deg);
  }
  50% {
    opacity: 0.6;
    transform: translateX(20px) rotate(20deg);
  }
  70% {
    opacity: 0.4;
    transform: translateX(-10px) rotate(-15deg);
  }
  90% {
    opacity: 0.2;
    transform: translateX(5px) rotate(10deg) scale(0.8);
  }
  100% {
    top: 95%;
    opacity: 0;
    transform: translateX(-5px) rotate(-5deg) scale(0.3);
  }
}
</style>
