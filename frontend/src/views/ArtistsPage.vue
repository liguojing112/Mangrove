<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Header -->
    <div class="pt-16 pb-4 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <h1 class="text-2xl font-bold text-gray-900">艺人档案</h1>
      <p class="text-sm text-gray-400 mt-1">追踪你喜爱的艺人，了解他们的每一刻</p>
    </div>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pb-16">
      <!-- Search Bar -->
      <div class="mb-6 max-w-2xl mx-auto">
        <div class="rounded-full h-12 bg-white shadow border border-gray-200 flex items-center px-4">
          <Search class="w-4 h-4 text-gray-400 flex-shrink-0" />
          <input v-model="searchQuery" type="text" placeholder="搜索艺人..." class="flex-1 outline-none text-sm px-3 bg-transparent placeholder-gray-400" />
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="text-center py-16 text-gray-400">
        <Loader2 class="w-8 h-8 mx-auto mb-2 animate-spin" />
        <p>加载中...</p>
      </div>

      <!-- Artist Grid -->
      <div v-else-if="filteredArtists.length" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        <router-link
          v-for="artist in filteredArtists"
          :key="artist.id"
          :to="'/artists/' + artist.id"
          class="card-hover block overflow-hidden"
        >
          <div class="aspect-square overflow-hidden bg-gradient-to-br from-mangrove-100 to-mangrove-200 rounded-t-2xl">
            <img v-if="artist.avatarUrl" :src="artist.avatarUrl" :alt="artist.stageName" class="w-full h-full object-cover" />
            <div v-else class="w-full h-full flex items-center justify-center">
              <User class="w-16 h-16 text-mangrove-300" />
            </div>
          </div>
          <div class="p-4">
            <h3 class="text-sm font-semibold text-gray-900">{{ artist.stageName || artist.name }}</h3>
            <p class="text-xs text-gray-500 mt-0.5">{{ artist.name }}</p>
            <span v-if="debutDays(artist)" class="tag mt-2">出道 {{ debutDays(artist) }}天</span>
          </div>
        </router-link>
      </div>

      <!-- Empty State -->
      <div v-else class="text-center py-16 text-gray-400">
        <Camera class="w-10 h-10 mx-auto mb-2" />
        <p>{{ searchQuery ? '没有找到匹配的艺人' : '暂无艺人' }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search, Camera, Loader2, User } from 'lucide-vue-next'

const searchQuery = ref('')
const artists = ref([])
const loading = ref(true)

function debutDays(artist) {
  if (!artist.debutDate) return null
  const debut = new Date(artist.debutDate)
  const now = new Date()
  return Math.floor((now - debut) / (1000 * 60 * 60 * 24))
}

const filteredArtists = computed(() => {
  if (!searchQuery.value.trim()) return artists.value
  const q = searchQuery.value.trim().toLowerCase()
  return artists.value.filter(a =>
    (a.name || '').toLowerCase().includes(q) ||
    (a.stageName || '').toLowerCase().includes(q)
  )
})

onMounted(async () => {
  try {
    const res = await fetch('/api/public/artists')
    const json = await res.json()
    if (json.code === 200 && json.data) {
      artists.value = json.data
    }
  } catch (e) {
    console.error('获取艺人列表失败:', e)
  } finally {
    loading.value = false
  }
})
</script>
