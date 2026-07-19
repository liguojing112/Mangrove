<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24 pb-16">
    <!-- Header -->
    <h1 class="section-title mb-8">行程日历</h1>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Left: Timeline -->
      <div class="lg:col-span-2">
        <!-- Loading -->
        <div v-if="loading" class="text-center py-8">
          <p class="text-gray-400">加载中...</p>
        </div>

        <!-- Empty -->
        <div v-else-if="schedules.length === 0" class="text-center py-12">
          <Calendar class="w-12 h-12 text-gray-300 mx-auto mb-3" />
          <p class="text-gray-400">暂无行程安排</p>
        </div>

        <!-- Schedule List -->
        <div v-else>
          <button
            v-for="item in schedules"
            :key="item.id"
            type="button"
            class="card mb-4 w-full border-l-4 border-mangrove-600 py-4 pl-4 text-left transition-all hover:-translate-y-0.5 hover:shadow-md focus:outline-none focus:ring-2 focus:ring-mangrove-400"
            :aria-label="`查看行程详情：${item.title}`"
            @click="openSchedule(item)"
          >
            <div class="flex items-start justify-between">
              <div>
                <div class="flex items-baseline gap-3 mb-1">
                  <span class="text-mangrove-700 font-bold text-lg">{{ formatDateTime(item.startTime).date }}</span>
                  <span class="text-sm text-gray-500">{{ formatDateTime(item.startTime).time }}</span>
                </div>
                <p class="font-semibold text-gray-900">{{ item.title }}</p>
                <div class="flex items-center gap-1 mt-1.5 text-sm text-gray-500">
                  <MapPin class="w-3.5 h-3.5" />
                  <span>{{ item.location }}</span>
                </div>
                <a
                  v-if="item.videoLink"
                  :href="item.videoLink"
                  target="_blank"
                  rel="noopener noreferrer"
                  class="inline-flex items-center gap-1.5 mt-2 text-xs font-medium text-mangrove-600 hover:text-mangrove-800 transition-colors"
                  @click.stop
                >
                  <PlayCircle class="w-3.5 h-3.5" />
                  观看视频
                </a>
              </div>
              <span :class="typeClass(item.scheduleType)">{{ typeLabel(item.scheduleType) }}</span>
            </div>
          </button>
        </div>
      </div>

      <!-- Right: Mini Calendar -->
      <div class="lg:col-span-1">
        <div class="card p-5">
          <div class="flex items-center justify-between mb-4">
            <button @click="prevMonth" class="p-1 hover:bg-gray-100 rounded-lg transition-colors">
              <svg class="w-4 h-4 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
              </svg>
            </button>
            <p class="font-semibold text-gray-900">{{ calendarTitle }}</p>
            <button @click="nextMonth" class="p-1 hover:bg-gray-100 rounded-lg transition-colors">
              <svg class="w-4 h-4 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
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
                  : day === now.getDate() && currentMonth === now.getMonth() && currentYear === now.getFullYear()
                    ? 'bg-mangrove-100 text-mangrove-700 font-medium'
                    : 'text-gray-600 hover:bg-gray-100'
              ]"
            >
              {{ day }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- Schedule Detail -->
    <div v-if="selectedSchedule" class="fixed inset-0 z-[110] flex items-center justify-center bg-black/60 p-4" @click.self="closeSchedule">
      <div class="max-h-[92vh] w-full max-w-5xl overflow-y-auto bg-white shadow-2xl">
        <header class="sticky top-0 z-10 flex items-start justify-between border-b border-gray-200 bg-white px-5 py-4 sm:px-6">
          <div class="min-w-0">
            <div class="flex flex-wrap items-center gap-2 text-sm">
              <span class="font-semibold text-mangrove-700">{{ formatDateTime(selectedSchedule.startTime).date }}</span>
              <span class="text-gray-400">{{ formatDateTime(selectedSchedule.startTime).time }}</span>
              <span :class="typeClass(selectedSchedule.scheduleType)">{{ typeLabel(selectedSchedule.scheduleType) }}</span>
            </div>
            <h2 class="mt-2 text-xl font-semibold text-gray-900 sm:text-2xl">{{ selectedSchedule.title }}</h2>
            <p v-if="selectedSchedule.location" class="mt-2 flex items-center gap-1.5 text-sm text-gray-500"><MapPin class="h-4 w-4" />{{ selectedSchedule.location }}</p>
            <a
              v-if="selectedSchedule.videoLink"
              :href="selectedSchedule.videoLink"
              target="_blank"
              rel="noopener noreferrer"
              class="mt-2 inline-flex items-center gap-1.5 text-sm font-medium text-mangrove-600 hover:text-mangrove-800 transition-colors"
            >
              <PlayCircle class="h-4 w-4" />
              观看短视频
            </a>
          </div>
          <button type="button" class="ml-4 shrink-0 p-2 text-gray-400 hover:text-gray-700" aria-label="关闭行程详情" @click="closeSchedule"><X class="h-5 w-5" /></button>
        </header>

        <div class="space-y-8 p-5 sm:p-6">
          <p v-if="selectedSchedule.description" class="whitespace-pre-line text-sm leading-7 text-gray-600">{{ selectedSchedule.description }}</p>

          <div v-if="detailLoading" class="py-16 text-center text-sm text-gray-400"><Loader2 class="mx-auto mb-2 h-6 w-6 animate-spin" />加载当天素材...</div>
          <p v-else-if="detailError" class="border-l-4 border-red-400 bg-red-50 px-4 py-3 text-sm text-red-700">{{ detailError }}</p>
          <div v-else-if="scheduleMedia.length === 0" class="border-y border-gray-100 py-14 text-center text-sm text-gray-400">
            <Images class="mx-auto mb-3 h-10 w-10 text-gray-300" />当天暂未上传图片或视频
          </div>
          <template v-else>
            <section v-if="imageMedia.length" aria-label="行程图片">
              <h3 class="mb-3 text-base font-semibold text-gray-900">当天图片 <span class="ml-1 text-sm font-normal text-gray-400">{{ imageMedia.length }}</span></h3>
              <div class="grid grid-cols-2 gap-3 md:grid-cols-3">
                <button v-for="media in imageMedia" :key="media.id" type="button" class="group relative aspect-[4/3] overflow-hidden bg-gray-100" :aria-label="`查看大图：${media.title || selectedSchedule.title}`" @click="previewImage = media">
                  <img :src="media.mediaUrl" :alt="media.title || selectedSchedule.title" class="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105" />
                </button>
              </div>
            </section>

            <section v-if="videoMedia.length" aria-label="行程视频">
              <h3 class="mb-3 text-base font-semibold text-gray-900">当天视频 <span class="ml-1 text-sm font-normal text-gray-400">{{ videoMedia.length }}</span></h3>
              <div class="grid gap-4 md:grid-cols-2">
                <div v-for="media in videoMedia" :key="media.id" class="overflow-hidden border border-gray-200 bg-white">
                  <video :src="media.mediaUrl" controls playsinline preload="metadata" class="aspect-video w-full bg-black object-contain"></video>
                  <p v-if="media.title" class="truncate px-3 py-2 text-sm text-gray-600">{{ media.title }}</p>
                </div>
              </div>
            </section>
          </template>
        </div>
      </div>
    </div>

    <div v-if="previewImage" class="fixed inset-0 z-[130] flex items-center justify-center bg-black/95 p-4" @click="previewImage = null">
      <button type="button" class="absolute right-4 top-4 p-2 text-white/70 hover:text-white" aria-label="关闭大图" @click="previewImage = null"><X class="h-6 w-6" /></button>
      <img :src="previewImage.mediaUrl" :alt="previewImage.title || '行程图片'" class="max-h-[92vh] max-w-[96vw] object-contain" @click.stop />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Calendar, Images, Loader2, MapPin, PlayCircle, X } from 'lucide-vue-next'

const schedules = ref([])
const loading = ref(false)
const selectedSchedule = ref(null)
const scheduleMedia = ref([])
const detailLoading = ref(false)
const detailError = ref('')
const previewImage = ref(null)

const imageMedia = computed(() => scheduleMedia.value.filter(media => media.mediaType === 'IMAGE'))
const videoMedia = computed(() => scheduleMedia.value.filter(media => media.mediaType === 'VIDEO'))

const now = new Date()
const currentMonth = ref(now.getMonth())
const currentYear = ref(now.getFullYear())
const today = computed(() => now.getDate())  // 改成 computed，确保不会被修改

const weekDays = ['日', '一', '二', '三', '四', '五', '六']

const calendarTitle = computed(() => {
  return `${currentYear.value}年${currentMonth.value + 1}月`
})

const daysInMonth = computed(() => {
  return new Date(currentYear.value, currentMonth.value + 1, 0).getDate()
})

const firstDayOffset = computed(() => {
  return new Date(currentYear.value, currentMonth.value, 1).getDay()
})

const eventDays = computed(() => {
  return schedules.value
    .filter(s => {
      const d = new Date(s.startTime)
      const month = d.getMonth()
      const year = d.getFullYear()
      return month === currentMonth.value && year === currentYear.value
    })
    .map(s => {
      const d = new Date(s.startTime)
      return d.getDate()
    })
})

async function fetchSchedules() {
  loading.value = true
  try {
    const res = await fetch('/api/schedules')
    const json = await res.json()
    if (json.code === 200 && json.data) {
      schedules.value = json.data
    }
  } catch (e) {
    console.error('加载行程失败:', e)
  } finally {
    loading.value = false
  }
}

async function openSchedule(schedule) {
  selectedSchedule.value = schedule
  scheduleMedia.value = []
  detailError.value = ''
  detailLoading.value = true
  try {
    const res = await fetch(`/api/schedules/${schedule.id}/media`)
    const json = await res.json()
    if (!res.ok || json.code !== 200) throw new Error(json.msg || '加载素材失败')
    scheduleMedia.value = json.data || []
  } catch (e) {
    detailError.value = e.message
  } finally {
    detailLoading.value = false
  }
}

function closeSchedule() {
  selectedSchedule.value = null
  scheduleMedia.value = []
  detailError.value = ''
  previewImage.value = null
}

function prevMonth() {
  if (currentMonth.value === 0) {
    currentMonth.value = 11
    currentYear.value--
  } else {
    currentMonth.value--
  }
}

function nextMonth() {
  if (currentMonth.value === 11) {
    currentMonth.value = 0
    currentYear.value++
  } else {
    currentMonth.value++
  }
}

function formatDateTime(dateStr) {
  if (!dateStr) return { date: '', time: '' }
  const d = new Date(dateStr + 'Z')  // 强制 UTC
  return {
    date: `${String(d.getUTCMonth() + 1).padStart(2, '0')}月${String(d.getUTCDate()).padStart(2, '0')}日`,
    time: `${String(d.getUTCHours()).padStart(2, '0')}:${String(d.getUTCMinutes()).padStart(2, '0')}`
  }
}

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

onMounted(() => {
  fetchSchedules()
})
</script>
