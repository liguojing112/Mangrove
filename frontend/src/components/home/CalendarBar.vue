<template>
  <div class="relative overflow-hidden rounded-2xl" :style="bgStyle">
    <div class="absolute inset-0 bg-gradient-to-b from-black/60 via-black/40 to-black/70" />

    <div class="relative z-10 px-6 py-8 sm:px-10 sm:py-10">
      <!-- Loading -->
      <div v-if="loading" class="space-y-4">
        <div v-for="i in 4" :key="i" class="flex justify-center gap-3">
          <div v-for="j in 4" :key="j" class="w-20 h-24 sm:w-28 sm:h-24 bg-white/10 rounded-lg animate-pulse" />
        </div>
      </div>

      <template v-else>
        <div class="space-y-4">
          <!-- 第1行：距离生日还有（倒计时） -->
          <countdown-row
            v-if="targetCountdown.target"
            label="距离生日还有"
            :d="targetCountdown.days"
            :h="targetCountdown.hours"
            :m="targetCountdown.minutes"
            :s="targetCountdown.seconds"
          />

          <!-- 第2行：相伴之日（已登录） -->
          <countdown-row
            v-if="isLoggedIn && timeSinceCountdowns[0]"
            label="相伴之日"
            :d="timeSinceCountdowns[0].days"
            :h="timeSinceCountdowns[0].hours"
            :m="timeSinceCountdowns[0].minutes"
            :s="timeSinceCountdowns[0].seconds"
            link="/profile"
            title="点击去个人中心设置喜欢日期"
          />

          <!-- 第3行：出道纪念日 -->
          <countdown-row
            v-if="timeSinceCountdowns[1]"
            label="出道纪念日"
            :d="timeSinceCountdowns[1].days"
            :h="timeSinceCountdowns[1].hours"
            :m="timeSinceCountdowns[1].minutes"
            :s="timeSinceCountdowns[1].seconds"
          />

          <!-- 第4行：诞生天数 -->
          <countdown-row
            v-if="timeSinceCountdowns[2]"
            label="诞生天数"
            :d="timeSinceCountdowns[2].days"
            :h="timeSinceCountdowns[2].hours"
            :m="timeSinceCountdowns[2].minutes"
            :s="timeSinceCountdowns[2].seconds"
          />
        </div>

        <!-- 未登录提示 -->
        <div v-if="!isLoggedIn && timeSinceCountdowns.length === 0" class="text-center mt-4 pt-4 border-t border-white/10">
          <router-link to="/profile" class="text-sm text-white/50 hover:text-white/80 transition-colors">
            登录查看你的相伴之日 →
          </router-link>
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import { h } from 'vue'

const CountdownRow = {
  props: {
    label: String,
    d: Number,
    h: Number,
    m: Number,
    s: Number,
    link: String,
    title: String
  },
  setup(props) {
    const units = [
      { val: () => props.d, label: '天' },
      { val: () => props.h, label: '时' },
      { val: () => props.m, label: '分' },
      { val: () => props.s, label: '秒' }
    ]

    return () => {
      const boxes = units.map(u =>
        h('div', { class: 'w-20 h-24 sm:w-28 sm:h-24 flex flex-col items-center justify-center rounded-lg border border-white/10 bg-white/5 backdrop-blur-sm' }, [
          h('span', { class: 'text-4xl sm:text-5xl font-serif text-white font-bold leading-none tabular-nums' },
            String(u.val()).padStart(2, '0')),
          h('span', { class: 'text-[11px] sm:text-xs text-white/50 mt-2 tracking-wider' }, u.label)
        ])
      )

      const row = h('div', { class: 'text-center' }, [
        h('p', { class: 'text-xs sm:text-sm tracking-[0.25em] text-white/60 mb-3 uppercase' }, props.label),
        h('div', { class: 'flex justify-center gap-3 sm:gap-4' }, boxes)
      ])

      if (props.link) {
        return h('router-link', { to: props.link, title: props.title || '', class: 'block' }, [row])
      }
      return row
    }
  }
}
</script>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useAuth } from '@/composables/useAuth'

const props = defineProps({
  artistId: { type: Number, default: 1 }
})

const { isLoggedIn, getToken } = useAuth()

const loading = ref(true)
const bgStyle = {}

// 距离目标日期倒计时（生日）
const targetCountdown = ref({ target: null, days: 0, hours: 0, minutes: 0, seconds: 0 })

// 从过去某个日期开始计时（相伴/出道/存活）
const timeSinceCountdowns = ref([])

let timer = null

function calcTimeUntil(targetStr) {
  const now = new Date()
  const target = new Date(targetStr + 'T00:00:00')
  let diff = target.getTime() - now.getTime()
  if (diff <= 0) return { days: 0, hours: 0, minutes: 0, seconds: 0 }
  const days = Math.floor(diff / 86400000)
  diff %= 86400000
  const hours = Math.floor(diff / 3600000)
  diff %= 3600000
  const minutes = Math.floor(diff / 60000)
  diff %= 60000
  return { days, hours, minutes, seconds: Math.floor(diff / 1000) }
}

function calcTimeSince(startStr) {
  const now = new Date()
  const start = new Date(startStr + 'T00:00:00')
  let diff = now.getTime() - start.getTime()
  if (diff < 0) return { days: 0, hours: 0, minutes: 0, seconds: 0 }
  const days = Math.floor(diff / 86400000)
  diff %= 86400000
  const hours = Math.floor(diff / 3600000)
  diff %= 3600000
  const minutes = Math.floor(diff / 60000)
  diff %= 60000
  return { days, hours, minutes, seconds: Math.floor(diff / 1000) }
}

function tick() {
  if (targetCountdown.value.target) {
    Object.assign(targetCountdown.value, calcTimeUntil(targetCountdown.value.target))
  }
  timeSinceCountdowns.value.forEach(item => {
    Object.assign(item, calcTimeSince(item.start))
  })
}

onMounted(async () => {
  try {
    const headers = {}
    const token = getToken()
    if (token) headers['Authorization'] = `Bearer ${token}`
    const res = await fetch(`/api/calendar/days?artistId=${props.artistId}`, { headers })
    const json = await res.json()
    if (json.code === 200) {
      const d = json.data
      if (d.nextBirthdayDate) {
        targetCountdown.value.target = d.nextBirthdayDate
      }
      const since = []
      if (d.loveStartDate) since.push({ start: d.loveStartDate })
      if (d.debutDate) since.push({ start: d.debutDate })
      if (d.birthDate) since.push({ start: d.birthDate })
      timeSinceCountdowns.value = since
    }
  } catch {} finally {
    loading.value = false
  }
  tick()
  timer = setInterval(tick, 1000)
})

onBeforeUnmount(() => { if (timer) clearInterval(timer) })
</script>
