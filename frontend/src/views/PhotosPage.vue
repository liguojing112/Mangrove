<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Hero 横幅 -->
    <div class="relative rounded-2xl overflow-hidden mb-8 mx-4 sm:mx-6 lg:mx-auto max-w-7xl h-[320px]">
      <div class="absolute inset-0 bg-gradient-to-r from-mangrove-900 via-mangrove-700 to-mangrove-400"></div>
      <div class="absolute right-0 top-0 bottom-0 w-[55%] overflow-hidden">
        <div class="absolute inset-0 bg-gradient-to-r from-mangrove-900 via-mangrove-800/60 to-transparent z-10"></div>
        <img :src="artistCover" class="absolute right-0 bottom-0 h-full w-[120%] object-cover object-center" v-if="artistCover" @error="$event.target.style.display='none'" />
      </div>
      <div class="relative z-20 h-full flex flex-col justify-center px-10 md:px-14">
        <h2 class="text-3xl md:text-4xl font-black text-white mb-3">收藏每一份精彩瞬间</h2>
        <p class="text-mangrove-200 text-base">记录每一次心动 · 定格每一份美好</p>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pb-16">
      <!-- 搜索框 -->
      <div class="mb-6">
        <div class="relative rounded-full h-12 bg-white shadow-lg border border-mangrove-100 flex items-center px-4 max-w-2xl mx-auto">
          <Search class="w-4 h-4 text-mangrove-400 flex-shrink-0" />
          <input v-model="searchQuery" type="text" placeholder="搜索照片..." class="flex-1 outline-none text-sm px-3 bg-transparent" />
          <button class="w-10 h-10 rounded-full bg-mangrove-500 text-white flex items-center justify-center hover:bg-mangrove-600">
            <Search class="w-4 h-4" />
          </button>
        </div>
      </div>

      <!-- 分类标签（从 API 加载，和底部卡片同步） -->
      <div class="flex gap-3 justify-center mb-8 flex-wrap">
        <button v-for="cat in categoryList" :key="cat.value"
          class="rounded-full px-5 py-2 text-sm font-medium transition-all duration-200"
          :class="activeCategory === cat.value
            ? 'bg-mangrove-600 text-white shadow-sm'
            : 'bg-white text-gray-500 border border-gray-200 hover:border-mangrove-300 hover:text-mangrove-600'"
          @click="activeCategory = cat.value">
          {{ cat.label }}
          <span v-if="getCatCount(cat.value) > 0" class="ml-1 text-xs opacity-70">{{ getCatCount(cat.value) }}</span>
        </button>
      </div>

      <!-- 照片网格 -->
      <div v-if="loading" class="flex justify-center py-20">
        <div class="animate-spin rounded-full h-8 w-8 border-2 border-mangrove-500 border-t-transparent" />
      </div>
      <div v-else-if="filteredPhotos.length === 0" class="text-center py-16 text-gray-400">
        <Camera class="w-10 h-10 mx-auto mb-2" />
        <p>暂无照片，请先在后台管理上传</p>
      </div>
      <div v-else class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        <div v-for="photo in filteredPhotos" :key="photo.id"
          class="group rounded-2xl overflow-hidden bg-white border border-gray-100 shadow-sm hover:shadow-lg transition-all duration-200 cursor-pointer"
          @click="previewPhoto(photo)">
          <div class="aspect-[4/5] overflow-hidden bg-gray-50">
            <img v-if="photo.fileUrl" :src="photo.fileUrl" :alt="photo.title"
              class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
              loading="lazy" />
          </div>
          <div class="px-3 py-2.5">
            <p class="text-sm font-medium text-gray-800 truncate">{{ photo.title }}</p>
            <div class="flex items-center justify-between mt-1">
              <span class="text-xs text-gray-400">{{ getPhotoCategory(photo) }}</span>
              <span class="text-xs text-gray-400">{{ formatDate(photo.createdAt) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 照片分类卡片（和标签栏同一数据源） -->
      <div class="mt-12">
        <h2 class="text-lg font-bold text-gray-900 mb-4">照片分类</h2>
        <div class="grid grid-cols-2 md:grid-cols-5 gap-4">
          <div v-for="cat in categoryList" :key="cat.value"
            class="rounded-2xl p-4 cursor-pointer transition-all duration-200 border"
            :class="activeCategory === cat.value
              ? 'bg-mangrove-100 border-2 border-mangrove-300 shadow-md'
              : 'bg-white border-gray-100 hover:shadow-md hover:border-mangrove-200'"
            @click="activeCategory = cat.value">
            <div class="flex items-start justify-between mb-3">
              <div>
                <h3 class="font-bold text-gray-900 text-sm">{{ cat.label }}</h3>
                <p class="text-xs text-gray-400 mt-0.5">共 {{ getCatCount(cat.value) }} 张</p>
              </div>
              <div class="w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0"
                :class="activeCategory === cat.value ? 'bg-mangrove-600 text-white' : 'bg-gray-100 text-gray-400'">
                <Heart class="w-4 h-4" fill="currentColor" />
              </div>
            </div>
            <div class="flex -space-x-2 mt-1">
              <div v-for="(thumb, i) in getCatThumbs(cat.value)" :key="i"
                class="w-10 h-10 rounded-full bg-gray-200 overflow-hidden border-2 border-white">
                <img :src="thumb" class="w-full h-full object-cover" @error="$event.target.style.display='none'" />
              </div>
              <div v-if="getCatCount(cat.value) > 3"
                class="w-10 h-10 rounded-full bg-mangrove-100 flex items-center justify-center border-2 border-white">
                <span class="text-xs font-medium text-mangrove-700">+{{ getCatCount(cat.value) - 3 }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search, Heart, Camera } from 'lucide-vue-next'

const photos = ref([])
const loading = ref(true)
const activeCategory = ref('all')
const searchQuery = ref('')
const artistCover = ref('')
const categoryList = ref([])

const filteredPhotos = computed(() => {
  let list = photos.value
  if (activeCategory.value !== 'all') {
    list = list.filter(p => getPhotoCategory(p) === activeCategory.value)
  }
  if (searchQuery.value.trim()) {
    const q = searchQuery.value.trim().toLowerCase()
    list = list.filter(p => (p.title || '').toLowerCase().includes(q))
  }
  return list
})

function getPhotoCategory(photo) {
  if (photo.photoCategoryId) {
    const cat = categoryList.value.find(c => c.id === photo.photoCategoryId)
    if (cat) return cat.label
  }
  return '未分类'
}

function getCatThumbs(catValue) {
  const filtered = catValue === 'all' ? photos.value : photos.value.filter(p => getPhotoCategory(p) === catValue)
  return filtered.slice(0, 3).map(p => p.thumbnailUrl || p.fileUrl)
}

function getCatCount(catValue) {
  return catValue === 'all' ? photos.value.length : photos.value.filter(p => getPhotoCategory(p) === catValue).length
}

function previewPhoto(photo) {
  if (!photo.fileUrl) return
  previewUrl.value = photo.fileUrl
  previewTitle.value = photo.title
  showPreview.value = true
}

function formatDate(d) {
  if (!d) return ''
  return d.substring(0, 10)
}

const showPreview = ref(false)
const previewUrl = ref('')
const previewTitle = ref('')

onMounted(async () => {
  try {
    const [photoRes, artistRes, catRes] = await Promise.all([
      fetch('/api/content?category=PHOTO'),
      fetch('/api/public/artists'),
      fetch('/api/photo-categories')
    ])

    if (photoRes.ok) {
      const json = await photoRes.json()
      photos.value = (json.code === 200 ? json.data : null) || []
    }

    if (artistRes.ok) {
      const json = await artistRes.json()
      if (json.data?.length > 0) artistCover.value = json.data[0].avatarUrl || ''
    }

    if (catRes.ok) {
      const json = await catRes.json()
      if (json.code === 200 && json.data?.length) {
        categoryList.value = [
          { value: 'all', label: '全部', id: null },
          ...json.data.map(c => ({ value: c.name, label: c.name, id: c.id }))
        ]
      }
    }
  } catch (e) {
    console.error('加载照片数据失败:', e)
  } finally {
    loading.value = false
  }
})
</script>
