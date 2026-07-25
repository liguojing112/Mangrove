<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24 pb-16 relative">
    <!-- 背景装饰：五线谱纹理 + 浮动音符 -->
    <div class="absolute inset-0 pointer-events-none select-none z-0" aria-hidden="true">
      <svg class="absolute top-0 left-0 w-full h-full opacity-[0.08]" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <pattern id="staff-lines-games" x="0" y="0" width="100%" height="150" patternUnits="userSpaceOnUse">
            <line x1="0" y1="20" x2="100%" y2="20" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="45" x2="100%" y2="45" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="70" x2="100%" y2="70" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="95" x2="100%" y2="95" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="120" x2="100%" y2="120" stroke="#2E8B57" stroke-width="1.5"/>
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill="url(#staff-lines-games)"/>
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

    <!-- Header -->
    <h1 class="section-title mb-1 relative z-10">芒园小游戏</h1>
    <p class="text-gray-500 mb-8 relative z-10">测测你对小芒的了解程度吧！</p>

    <!-- External Game Links -->
    <div class="card games-card-hover p-4 mb-8 max-w-2xl mx-auto relative z-10">
      <a
        href="https://www.d8ok.com/g/km2560/fx.php"
        target="_blank"
        rel="noopener noreferrer"
        class="flex items-center justify-center gap-3 w-full py-4 px-6 rounded-2xl bg-gradient-to-r from-green-400 to-emerald-500 text-white font-bold text-lg shadow-lg hover:shadow-xl hover:from-green-500 hover:to-emerald-600 transition-all"
      >
        <span class="text-2xl">🍉</span>
        西瓜大合成
        <span class="text-xs opacity-70 ml-1">↗</span>
      </a>
    </div>

    <!-- Difficulty Tabs -->
    <div class="flex items-center gap-2 mb-8 relative z-10">
      <button
        v-for="tab in difficulties"
        :key="tab.value"
        class="px-5 py-2 rounded-full text-sm font-medium transition-colors"
        :class="activeDifficulty === tab.value
          ? 'bg-mangrove-700 text-white'
          : 'bg-gray-100 text-gray-500 hover:bg-gray-200'"
        @click="selectDifficulty(tab.value)"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-8 relative z-10">
      <p class="text-gray-400">加载中...</p>
    </div>

    <!-- No Levels -->
    <div v-else-if="levels.length === 0" class="card games-card-hover max-w-2xl mx-auto p-8 text-center relative z-10">
      <p class="text-gray-400">暂无游戏关卡</p>
    </div>

    <!-- Game Area -->
    <div v-else class="card games-card-hover max-w-2xl mx-auto p-8 relative z-10">
      <!-- Current Level Info -->
      <div class="mb-6">
        <p class="text-sm text-gray-400 mb-1">关卡 {{ currentLevel.levelNumber }}</p>
        <p class="text-lg font-medium text-gray-900">{{ currentLevel.title }}</p>
        <p class="text-sm text-gray-500 mt-2">{{ currentLevel.description }}</p>
      </div>

      <!-- Question -->
      <div v-if="currentQuestion" class="mb-6">
        <p class="text-sm text-mangrove-600 mb-3">第 {{ currentQuestionIndex + 1 }} / {{ questions.length }} 题</p>
        <p class="text-lg font-medium text-gray-900 mb-6">{{ currentQuestion.question }}</p>

        <!-- Options -->
        <div class="space-y-3 mb-8">
          <button
            v-for="(opt, idx) in currentOptions"
            :key="idx"
            class="w-full rounded-xl border-2 p-4 text-left transition-colors"
            :class="optionClass(idx)"
            @click="selectOption(idx)"
          >
            <span class="font-bold mr-3 text-mangrove-700">{{ opt.letter }}</span>
            {{ opt.text }}
          </button>
        </div>

        <!-- Next Button -->
        <div class="flex justify-end">
          <button
            v-if="answered"
            class="btn-primary"
            @click="nextQuestion"
          >
            {{ currentQuestionIndex < questions.length - 1 ? '下一题' : '查看结果' }}
          </button>
        </div>
      </div>

      <!-- Results -->
      <div v-if="showResults" class="text-center">
        <p class="text-2xl font-bold text-mangrove-700 mb-4">游戏完成！</p>
        <p class="text-lg text-gray-700 mb-2">得分：{{ score }} 分</p>
        <p class="text-gray-500">答对 {{ correctCount }} / {{ questions.length }} 题</p>
        <button class="btn-primary mt-6" @click="resetGame">重新开始</button>
      </div>
    </div>

    <!-- Leaderboard -->
    <div class="card games-card-hover max-w-2xl mx-auto mt-12 p-6 relative z-10">
      <h2 class="font-semibold text-gray-900 mb-4">排行榜</h2>
      <div v-if="leaderboard.length === 0" class="text-center py-4 text-gray-400">
        暂无排名数据
      </div>
      <div v-else class="space-y-3">
        <div
          v-for="(user, idx) in leaderboard"
          :key="idx"
          class="flex items-center gap-3"
        >
          <span
            class="w-6 text-center font-bold"
            :class="rankColor(idx)"
          >{{ idx + 1 }}</span>
          <div class="w-8 h-8 rounded-full bg-mangrove-200 flex items-center justify-center">
            <User class="w-4 h-4 text-mangrove-700" />
          </div>
          <span class="flex-1 text-sm font-medium text-gray-900">{{ user.userNickname || '匿名' }}</span>
          <span class="text-sm text-gray-500">{{ user.score }} 分</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { User } from 'lucide-vue-next'
import { useAuth } from '@/composables/useAuth'

const { isLoggedIn, currentUser, getToken } = useAuth()

const difficulties = [
  { label: '简单', value: 'EASY' },
  { label: '中等', value: 'MEDIUM' },
  { label: '困难', value: 'HARD' },
]
const activeDifficulty = ref('EASY')
const levels = ref([])
const questions = ref([])
const currentLevel = ref(null)
const currentQuestionIndex = ref(0)
const selectedIndex = ref(null)
const answered = ref(false)
const showResults = ref(false)
const score = ref(0)
const correctCount = ref(0)
const leaderboard = ref([])
const loading = ref(true)

const currentQuestion = computed(() => {
  return questions.value[currentQuestionIndex.value] || null
})

const currentOptions = computed(() => {
  if (!currentQuestion.value) return []
  const q = currentQuestion.value
  return [
    { letter: 'A', text: q.optionA },
    { letter: 'B', text: q.optionB },
    { letter: 'C', text: q.optionC },
    { letter: 'D', text: q.optionD },
  ].filter(opt => opt.text)
})

function selectDifficulty(diff) {
  activeDifficulty.value = diff
  loadLevels()
}

async function loadLevels() {
  loading.value = true
  try {
    const res = await fetch('/api/game/levels')
    const json = await res.json()
    if (json.code === 200) {
      levels.value = json.data.filter(l => l.difficulty === activeDifficulty.value && l.status === 1)
      if (levels.value.length > 0) {
        currentLevel.value = levels.value[0]
        await loadQuestions(currentLevel.value.id)
      } else {
        questions.value = []
        currentLevel.value = null
      }
    }
  } catch (e) {
    console.error('加载关卡失败:', e)
  } finally {
    loading.value = false
  }
}

async function loadQuestions(levelId) {
  try {
    const res = await fetch(`/api/game/levels/${levelId}/questions`)
    const json = await res.json()
    if (json.code === 200) {
      questions.value = json.data
    }
  } catch (e) {
    console.error('加载题目失败:', e)
  }
}

function selectOption(idx) {
  if (answered.value) return
  selectedIndex.value = idx
  answered.value = true

  if (currentOptions.value[idx].letter === currentQuestion.value.correctAnswer) {
    correctCount.value++
  }
}

function optionClass(idx) {
  if (!answered.value || selectedIndex.value === null) {
    return 'border-gray-200 hover:border-mangrove-400 hover:bg-mangrove-50'
  }

  const isCorrect = currentOptions.value[idx].letter === currentQuestion.value.correctAnswer
  const isSelected = idx === selectedIndex.value

  // 用户选对了，显示绿色
  if (isSelected && isCorrect) {
    return 'border-mangrove-600 bg-mangrove-50'
  }

  // 用户选错了，选中的显示红色
  if (isSelected && !isCorrect) {
    return 'border-red-400 bg-red-50'
  }

  // 其他选项显示灰色
  return 'border-gray-200 opacity-50'
}

async function nextQuestion() {
  if (currentQuestionIndex.value < questions.value.length - 1) {
    currentQuestionIndex.value++
    selectedIndex.value = null
    answered.value = false
  } else {
    showResults.value = true
    score.value = correctCount.value * { EASY: 50, MEDIUM: 100, HARD: 200 }[activeDifficulty.value]
    await saveScore()
  }
}

async function saveScore() {
  if (!isLoggedIn.value) {
    alert('请先登录后再保存分数')
    return
  }

  try {
    await fetch('/api/game/scores', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({
        userId: currentUser.value.id,
        userNickname: currentUser.value.nickname,
        levelId: currentLevel.value.id,
        score: score.value,
        correctCount: correctCount.value,
        totalQuestions: questions.value.length,
        difficulty: activeDifficulty.value,
      })
    })
    await loadLeaderboard()
  } catch (e) {
    console.error('保存分数失败:', e)
  }
}

async function loadLeaderboard() {
  try {
    const res = await fetch('/api/game/scores/leaderboard')
    const json = await res.json()
    if (json.code === 200) {
      leaderboard.value = json.data
    }
  } catch (e) {
    console.error('加载排行榜失败:', e)
  }
}

function resetGame() {
  currentQuestionIndex.value = 0
  selectedIndex.value = null
  answered.value = false
  showResults.value = false
  score.value = 0
  correctCount.value = 0
}

function rankColor(idx) {
  if (idx === 0) return 'text-yellow-500'
  if (idx === 1) return 'text-gray-400'
  if (idx === 2) return 'text-orange-500'
  return 'text-gray-500'
}

onMounted(() => {
  loadLevels()
  loadLeaderboard()
})
</script>

<style scoped>
.games-card-hover {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.games-card-hover:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(46, 139, 87, 0.15), 0 4px 8px rgba(46, 139, 87, 0.1);
}

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
</style>
