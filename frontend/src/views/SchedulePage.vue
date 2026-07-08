<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24 pb-16">
    <!-- Header -->
    <h1 class="section-title mb-8">行程日历</h1>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Left: Timeline -->
      <div class="lg:col-span-2">
        <div
          v-for="item in schedules"
          :key="item.id"
          class="card border-l-4 border-mangrove-600 pl-4 py-4 mb-4"
        >
          <div class="flex items-start justify-between">
            <div>
              <div class="flex items-baseline gap-3 mb-1">
                <span class="text-mangrove-700 font-bold text-lg">{{ item.date }}</span>
                <span class="text-sm text-gray-500">{{ item.time }}</span>
              </div>
              <p class="font-semibold text-gray-900">{{ item.title }}</p>
              <div class="flex items-center gap-1 mt-1.5 text-sm text-gray-500">
                <MapPin class="w-3.5 h-3.5" />
                <span>{{ item.location }}</span>
              </div>
            </div>
            <span :class="typeClass(item.type)">{{ typeLabel(item.type) }}</span>
          </div>
        </div>
      </div>

      <!-- Right: Mini Calendar -->
      <div class="lg:col-span-1">
        <div class="card p-5">
          <p class="font-semibold text-gray-900 mb-4">{{ calendarTitle }}</p>
          <!-- Weekday Headers -->
          <div class="grid grid-cols-7 gap-1 text-center text-xs text-gray-400 mb-2">
            <span v-for="d in weekDays" :key="d">{{ d }}</span>
          </div>
          <!-- Day Grid -->
          <div class="grid grid-cols-7 gap-1 text-center text-sm">
            <!-- Empty cells before first day -->
            <span v-for="n in firstDayOffset" :key="'e' + n"></span>
            <!-- Day cells -->
            <span
              v-for="day in daysInMonth"
              :key="day"
              :class="[
                'w-8 h-8 flex items-center justify-center rounded-full mx-auto',
                eventDays.includes(day)
                  ? 'bg-mangrove-700 text-white font-medium'
                  : day === today
                    ? 'bg-mangrove-100 text-mangrove-700 font-medium'
                    : 'text-gray-600'
              ]"
            >
              {{ day }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { MapPin } from 'lucide-vue-next'

const now = new Date()
const currentMonth = now.getMonth()
const currentYear = now.getFullYear()
const today = now.getDate()

const weekDays = ['日', '一', '二', '三', '四', '五', '六']

const calendarTitle = computed(() => {
  return `${currentYear}年${currentMonth + 1}月`
})

const daysInMonth = computed(() => {
  return new Date(currentYear, currentMonth + 1, 0).getDate()
})

const firstDayOffset = computed(() => {
  return new Date(currentYear, currentMonth, 1).getDay()
})

// Event dates for this month (placeholder)
const eventDays = ref([5, 12, 15, 20, 25, 28])

const schedules = ref([
  { id: 1, date: '07月15日', time: '19:30', title: '「绿野之约」巡回演唱会 · 北京站', location: '国家体育馆', type: 'PERFORMANCE' },
  { id: 2, date: '07月20日', time: '14:00', title: '林夏粉丝见面会', location: '朝阳大悦城', type: 'FANMEETING' },
  { id: 3, date: '07月25日', time: '20:00', title: '《青春快充》综艺录制', location: '浙江卫视演播厅', type: 'VARIETY' },
  { id: 4, date: '07月28日', time: '10:00', title: '机场出发 · 上海', location: '首都国际机场 T3', type: 'AIRPORT' },
  { id: 5, date: '08月02日', time: '19:00', title: '「绿野之约」巡回演唱会 · 上海站', location: '梅赛德斯-奔驰文化中心', type: 'PERFORMANCE' },
  { id: 6, date: '08月05日', time: '15:00', title: '品牌代言拍摄', location: '棚拍·具体地址待定', type: 'OTHER' },
  { id: 7, date: '08月10日', time: '19:30', title: '苏念生日会', location: '粉丝俱乐部', type: 'FANMEETING' },
  { id: 8, date: '08月15日', time: '11:00', title: '机场返回 · 北京', location: '虹桥国际机场', type: 'AIRPORT' },
])

function typeClass(type) {
  const base = 'rounded-full px-2.5 py-0.5 text-xs font-medium shrink-0'
  switch (type) {
    case 'PERFORMANCE': return `${base} bg-red-100 text-red-700`
    case 'FANMEETING': return `${base} bg-blue-100 text-blue-700`
    case 'VARIETY': return `${base} bg-purple-100 text-purple-700`
    case 'AIRPORT': return `${base} bg-teal-100 text-teal-700`
    default: return `${base} bg-gray-100 text-gray-600`
  }
}

function typeLabel(type) {
  const map = {
    PERFORMANCE: '演出',
    FANMEETING: '见面会',
    VARIETY: '综艺',
    AIRPORT: '机场',
    OTHER: '其他',
  }
  return map[type] || '其他'
}
</script>
