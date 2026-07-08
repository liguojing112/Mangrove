<template>
  <div class="max-w-3xl mx-auto px-4 sm:px-6 pt-24 pb-16">
    <!-- Header -->
    <h1 class="section-title mb-4">小芒来信</h1>
    <p class="text-sm text-gray-500 mb-6">来自艺人的语录、来信、日记与独白</p>

    <!-- Category Filter Tabs -->
    <div class="flex flex-wrap gap-2 mb-8">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        :class="[
          'px-4 py-1.5 rounded-full text-sm font-medium transition-colors duration-150',
          activeTab === tab.value
            ? 'bg-mangrove-600 text-white'
            : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
        ]"
        @click="activeTab = tab.value"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- Timeline -->
    <div class="relative">
      <!-- Vertical line -->
      <div class="absolute left-4 top-0 bottom-0 w-px bg-gray-200 hidden sm:block"></div>

      <!-- Letter Cards -->
      <div
        v-for="letter in filteredLetters"
        :key="letter.id"
        class="relative sm:pl-12 mb-6"
      >
        <!-- Timeline dot -->
        <div class="absolute left-2.5 top-7 w-3 h-3 rounded-full bg-mangrove-400 border-2 border-white hidden sm:block"></div>

        <div class="card-hover p-6">
          <!-- Category Badge -->
          <span :class="categoryBadgeClass(letter.category)">
            {{ categoryLabel(letter.category) }}
          </span>

          <!-- Title -->
          <h2 class="text-lg font-semibold text-gray-900 mt-1">{{ letter.title }}</h2>

          <!-- Date -->
          <div class="flex items-center gap-1 mt-1 text-xs text-gray-400">
            <Clock class="w-3.5 h-3.5" />
            <span>{{ letter.date }}</span>
          </div>

          <!-- Content -->
          <div class="text-gray-700 mt-3 leading-relaxed whitespace-pre-line text-sm">
            {{ letter.content }}
          </div>

          <!-- Source -->
          <p v-if="letter.source" class="text-sm text-gray-400 italic mt-2">{{ letter.source }}</p>

          <!-- Footer: Like -->
          <div class="flex items-center gap-1.5 mt-4 text-gray-400 text-sm">
            <Heart class="w-4 h-4" />
            <span>{{ letter.likes }}</span>
          </div>
        </div>
      </div>

      <!-- Empty state -->
      <div v-if="filteredLetters.length === 0" class="text-center py-16 text-gray-400">
        <Mail class="w-12 h-12 mx-auto mb-3 opacity-40" />
        <p>暂无此类来信</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Clock, Heart, Mail } from 'lucide-vue-next'

const tabs = [
  { label: '全部', value: 'ALL' },
  { label: '语录', value: 'QUOTE' },
  { label: '来信', value: 'LETTER' },
  { label: '日记', value: 'DIARY' },
  { label: '独白', value: 'MONOLOGUE' },
]

const activeTab = ref('ALL')

const placeholderLetters = [
  {
    id: 1,
    title: '关于坚持',
    category: 'QUOTE',
    date: '2024年3月15日',
    content: '每一次站在舞台上的瞬间，我都告诉自己：这不是终点，而是另一段旅程的开始。光芒不是别人给的，是你自己一步步走出来的。',
    source: '—— 摘自《小芒语录》',
    likes: 328,
  },
  {
    id: 2,
    title: '写给芒园的你们',
    category: 'LETTER',
    date: '2024年2月28日',
    content: '亲爱的芒园家人们：\n\n春天来了，窗外的芒果树又抽出了新芽。每次看到它们，我都会想起我们一起走过的日子。你们的每一条留言、每一个点赞，都是我继续前行的力量。\n\n谢谢你们一直在。',
    source: '—— 艺人来信',
    likes: 512,
  },
  {
    id: 3,
    title: '深夜随笔',
    category: 'DIARY',
    date: '2024年1月10日',
    content: '今天录了一整天的歌，嗓子有点哑了。但听到混音出来的效果，觉得一切都值得。音乐就是这样——用尽全力之后，才能听见真正的声音。',
    source: '—— 摘自《小芒日记》',
    likes: 267,
  },
  {
    id: 4,
    title: '舞台独白',
    category: 'MONOLOGUE',
    date: '2024年4月2日',
    content: '你知道吗？站在聚光灯下的那一刻，世界会变得很安静。所有的嘈杂、所有的质疑，都退到了幕布后面。剩下的只有我和音乐，和台下那些闪闪发光的眼睛。\n\n我不害怕黑暗，因为我知道，总有人在为我点亮一盏灯。',
    source: '—— 演出独白',
    likes: 445,
  },
  {
    id: 5,
    title: '论创作',
    category: 'QUOTE',
    date: '2024年5月20日',
    content: '好的作品不是写出来的，是活出来的。每一首歌背后都有一段真实的故事，每一个音符都藏着一个未说出口的秘密。',
    source: '—— 采访语录',
    likes: 189,
  },
  {
    id: 6,
    title: '生日那天',
    category: 'DIARY',
    date: '2024年6月18日',
    content: '今天是我的生日。打开手机看到了你们准备的惊喜视频，从世界各地发来的祝福，眼眶一下子就湿了。\n\n二十几岁的自己，还在学着长大，学着变得更好。谢谢你们陪我。',
    source: '—— 摘自《小芒日记》',
    likes: 623,
  },
  {
    id: 7,
    title: '给未来的自己',
    category: 'LETTER',
    date: '2024年7月8日',
    content: '嘿，未来的我：\n\n不管你现在在哪里，经历了什么，请记得当初为什么出发。舞台上那个笑着唱歌的少年，从未走远。',
    source: '—— 艺人来信',
    likes: 398,
  },
  {
    id: 8,
    title: '关于成长',
    category: 'MONOLOGUE',
    date: '2024年8月15日',
    content: '成长是什么？是学会在掌声中保持清醒，也是在寂静中听见自己。是跌倒了不再急着站起来，而是先看看地上有什么值得记住的东西。\n\n我不是完美的，但我一直在努力成为更好的自己。',
    source: '—— 直播独白',
    likes: 476,
  },
]

const letters = ref(placeholderLetters)

const filteredLetters = computed(() => {
  if (activeTab.value === 'ALL') return letters.value
  return letters.value.filter(l => l.category === activeTab.value)
})

function categoryBadgeClass(category) {
  const map = {
    QUOTE: 'inline-block text-xs rounded-full px-2 py-0.5 bg-amber-100 text-amber-800 float-right',
    LETTER: 'inline-block text-xs rounded-full px-2 py-0.5 bg-blue-100 text-blue-800 float-right',
    DIARY: 'inline-block text-xs rounded-full px-2 py-0.5 bg-purple-100 text-purple-800 float-right',
    MONOLOGUE: 'inline-block text-xs rounded-full px-2 py-0.5 bg-mangrove-100 text-mangrove-800 float-right',
  }
  return map[category] || ''
}

function categoryLabel(category) {
  const map = { QUOTE: '语录', LETTER: '来信', DIARY: '日记', MONOLOGUE: '独白' }
  return map[category] || category
}

onMounted(async () => {
  try {
    const res = await fetch('/api/letters?artistId=1')
    if (res.ok) {
      const data = await res.json()
      if (Array.isArray(data) && data.length > 0) {
        letters.value = data
      }
    }
  } catch {
    // Keep placeholder data
  }
})
</script>
