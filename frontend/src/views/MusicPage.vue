<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24 pb-16">
    <!-- Header -->
    <h1 class="section-title mb-2">音芒分享</h1>
    <div class="relative max-w-md mb-8">
      <input
        v-model="searchQuery"
        type="text"
        placeholder="搜索歌曲、歌手..."
        class="w-full rounded-full bg-gray-100 border border-gray-200 pl-11 pr-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent"
      />
      <Search class="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
    </div>

    <!-- Music Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-10">
      <div
        v-for="song in filteredSongs"
        :key="song.id"
        class="card-hover overflow-hidden"
      >
        <!-- Album Cover -->
        <div class="bg-mangrove-100 rounded-t-2xl aspect-square flex items-center justify-center">
          <Music class="w-12 h-12 text-mangrove-400/50" />
        </div>
        <!-- Info -->
        <div class="p-4">
          <p class="font-semibold text-gray-900">{{ song.title }}</p>
          <p class="text-sm text-gray-500 mt-0.5">{{ song.singer }}</p>
          <div class="flex items-center gap-1 mt-2 text-xs text-gray-400">
            <Play class="w-3.5 h-3.5" />
            <span>{{ song.playCount }} 次播放</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Footer Hint -->
    <div class="flex justify-center">
      <span class="tag">共 {{ songs.length }} 首歌曲</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Search, Music, Play } from 'lucide-vue-next'

const searchQuery = ref('')

const songs = ref([
  { id: 1, title: '青芒之歌', singer: '青芒纪 · 群星', playCount: '12.8万' },
  { id: 2, title: '绿野之约', singer: '林夏 · solo', playCount: '8.6万' },
  { id: 3, title: '潮汐回响', singer: '海洋乐团', playCount: '6.2万' },
  { id: 4, title: '月光渡口', singer: '苏念 · solo', playCount: '5.1万' },
  { id: 5, title: '森林漫步', singer: '青芒纪 · 合辑', playCount: '4.7万' },
  { id: 6, title: '晚风信笺', singer: '陈予', playCount: '3.9万' },
  { id: 7, title: '逆光飞行', singer: '青芒纪 · 团曲', playCount: '9.3万' },
  { id: 8, title: '星河列车', singer: '林夏 · 合作曲', playCount: '7.1万' },
  { id: 9, title: '夏日终章', singer: '苏念 × 陈予', playCount: '2.4万' },
])

const filteredSongs = computed(() => {
  if (!searchQuery.value) return songs.value
  const q = searchQuery.value.toLowerCase()
  return songs.value.filter(
    s => s.title.toLowerCase().includes(q) || s.singer.toLowerCase().includes(q)
  )
})
</script>
