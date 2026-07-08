<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24 pb-16">
    <!-- Header -->
    <h1 class="section-title mb-1">芒园小游戏</h1>
    <p class="text-gray-500 mb-8">测测你对小芒的了解程度吧！</p>

    <!-- Difficulty Tabs -->
    <div class="flex items-center gap-2 mb-8">
      <button
        v-for="tab in difficulties"
        :key="tab"
        class="px-5 py-2 rounded-full text-sm font-medium transition-colors"
        :class="activeDifficulty === tab
          ? 'bg-mangrove-700 text-white'
          : 'bg-gray-100 text-gray-500 hover:bg-gray-200'"
        @click="activeDifficulty = tab"
      >
        {{ tab }}
      </button>
    </div>

    <!-- Quiz Card -->
    <div class="card max-w-2xl mx-auto p-8">
      <p class="text-sm text-gray-400 mb-3">第 1 / 10 题</p>
      <p class="text-lg font-medium text-gray-900 mb-6">小芒的出道日期是？</p>

      <!-- Options -->
      <div class="space-y-3 mb-8">
        <button
          v-for="(opt, idx) in options"
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
        <button class="btn-primary">下一题</button>
      </div>
    </div>

    <!-- Leaderboard -->
    <div class="card max-w-2xl mx-auto mt-12 p-6">
      <h2 class="font-semibold text-gray-900 mb-4">排行榜</h2>
      <div class="space-y-3">
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
          <span class="flex-1 text-sm font-medium text-gray-900">{{ user.name }}</span>
          <span class="text-sm text-gray-500">{{ user.score }} 分</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { User } from 'lucide-vue-next'

const difficulties = ['简单', '中等', '困难']
const activeDifficulty = ref('简单')

const options = [
  { letter: 'A', text: '2024年6月1日', correct: true },
  { letter: 'B', text: '2024年8月15日', correct: false },
  { letter: 'C', text: '2023年12月25日', correct: false },
  { letter: 'D', text: '2024年3月20日', correct: false },
]

const selectedIndex = ref(null)
const answered = ref(false)

function selectOption(idx) {
  if (answered.value) return
  selectedIndex.value = idx
  answered.value = true
}

function optionClass(idx) {
  if (!answered.value || selectedIndex.value === null) {
    return 'border-gray-200 hover:border-mangrove-400 hover:bg-mangrove-50'
  }
  if (options[idx].correct) return 'border-mangrove-600 bg-mangrove-50'
  if (idx === selectedIndex.value) return 'border-red-400 bg-red-50'
  return 'border-gray-200 opacity-50'
}

const leaderboard = [
  { name: '芒果小达人', score: 980 },
  { name: '青芒守护者', score: 920 },
  { name: '星河漫步', score: 870 },
  { name: '夏日清风', score: 810 },
  { name: '绿野仙踪', score: 760 },
]

function rankColor(idx) {
  if (idx === 0) return 'text-yellow-500'
  if (idx === 1) return 'text-gray-400'
  if (idx === 2) return 'text-amber-600'
  return 'text-gray-400'
}
</script>
