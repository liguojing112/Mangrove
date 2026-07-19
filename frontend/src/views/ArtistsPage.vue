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
          <input
            v-model="searchQuery"
            type="text"
            placeholder="搜索艺人..."
            class="flex-1 outline-none text-sm px-3 bg-transparent placeholder-gray-400"
          />
          <button class="bg-mangrove-600 text-white rounded-full w-10 h-10 flex items-center justify-center flex-shrink-0 hover:bg-mangrove-700 transition-colors">
            <Search class="w-4 h-4" />
          </button>
        </div>
      </div>

      <!-- Artist Grid -->
      <div v-if="filteredArtists.length" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        <router-link
          v-for="artist in filteredArtists"
          :key="artist.id"
          :to="'/artists/' + artist.id"
          class="card-hover block overflow-hidden"
        >
          <div class="aspect-square bg-gradient-to-br from-mangrove-100 to-mangrove-200 rounded-t-2xl"></div>
          <div class="p-4">
            <h3 class="text-sm font-semibold text-gray-900">{{ artist.name }}</h3>
            <p class="text-xs text-gray-500 mt-0.5">{{ artist.stageName }}</p>
            <span class="tag mt-2">出道 {{ artist.debutDays }}天</span>
          </div>
        </router-link>
      </div>

      <!-- Empty State -->
      <div v-else class="text-center py-16 text-gray-400">
        <Camera class="w-10 h-10 mx-auto mb-2" />
        <p>暂无艺人</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Search, Camera } from 'lucide-vue-next'

const searchQuery = ref('')

const artists = ref([
  { id: 1, name: '张小芒', stageName: '小芒', avatar: '', debutDays: 1287 },
  { id: 2, name: '李明', stageName: '明仔', avatar: '', debutDays: 965 },
  { id: 3, name: '王子轩', stageName: '轩轩', avatar: '', debutDays: 823 },
  { id: 4, name: '陈小青', stageName: '小青', avatar: '', debutDays: 1102 },
  { id: 5, name: '林夏', stageName: '夏夏', avatar: '', debutDays: 1456 },
  { id: 6, name: '苏念', stageName: '念念', avatar: '', debutDays: 734 },
])

const filteredArtists = computed(() => {
  if (!searchQuery.value.trim()) return artists.value
  const q = searchQuery.value.trim().toLowerCase()
  return artists.value.filter(a =>
    a.name.toLowerCase().includes(q) ||
    a.stageName.toLowerCase().includes(q)
  )
})
</script>
