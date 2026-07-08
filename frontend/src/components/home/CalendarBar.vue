<template>
  <div class="bg-gradient-to-r from-gray-800 to-gray-900 text-white rounded-2xl px-8 py-5">
    <!-- Loading -->
    <div v-if="loading" class="flex justify-around">
      <div v-for="i in 4" :key="i" class="flex flex-col items-center gap-2">
        <div class="w-16 h-8 bg-gray-700 rounded-lg animate-pulse" />
        <div class="w-14 h-3 bg-gray-700 rounded animate-pulse" />
      </div>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="text-center text-gray-400 text-sm py-2">
      日历数据加载中...
    </div>

    <!-- Data -->
    <div v-else class="flex justify-around">
      <div v-for="item in stats" :key="item.label" class="flex flex-col items-center">
        <span class="text-3xl font-bold text-mangrove-400">{{ item.value }}</span>
        <span class="text-xs text-gray-400 uppercase tracking-wider mt-1">{{ item.label }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'

const props = defineProps({
  artistId: { type: Number, default: 1 }
})

const loading = ref(true)
const error = ref(false)
const data = ref(null)

const stats = computed(() => {
  if (!data.value) return []
  return [
    { label: '生存天数', value: data.value.aliveDays ?? '--' },
    { label: '相伴之日', value: data.value.loveDays ?? '--' },
    { label: '出道天数', value: data.value.debutDays ?? '--' },
    { label: '距下次生日', value: data.value.nextBirthdayDays != null ? data.value.nextBirthdayDays + '天' : '--' }
  ]
})

onMounted(async () => {
  try {
    const res = await fetch(`/api/calendar/days?artistId=${props.artistId}`)
    const json = await res.json()
    if (json.code === 200) {
      data.value = json.data
    } else {
      error.value = true
    }
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
})
</script>
