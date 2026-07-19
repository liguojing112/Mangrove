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
        @click="selectTab(tab.value)"
      >
        {{ tab.label }}
      </button>
    </div>

    <div v-if="loading" class="text-center py-8">
      <p class="text-gray-400">加载中...</p>
    </div>

    <!-- Timeline -->
    <div v-else class="relative">
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
            <span>{{ formatDate(letter.noteDate || letter.createdAt) }}</span>
          </div>

          <!-- Image -->
          <button v-if="letter.coverUrl" type="button" class="mt-4 block w-full overflow-hidden rounded-lg border border-gray-100 bg-gray-50" :aria-label="`查看图片：${letter.title}`" @click="previewImage = letter">
            <img :src="letter.coverUrl" :alt="letter.title" class="max-h-[28rem] w-full object-contain transition-transform duration-300 hover:scale-[1.01]" />
          </button>

          <!-- Content -->
          <div class="text-gray-700 mt-3 leading-relaxed whitespace-pre-line text-sm">
            {{ letter.content }}
          </div>

          <!-- Source -->
          <p v-if="letter.source" class="text-sm text-gray-400 italic mt-2">{{ letter.source }}</p>

          <!-- Footer: Like + Upload -->
          <div class="flex items-center justify-between mt-4">
            <div class="flex items-center gap-1.5 text-gray-400 text-sm">
              <Heart class="w-4 h-4" />
              <span>{{ letter.likes }}</span>
            </div>
            <label v-if="isLoggedIn" class="flex items-center gap-1.5 text-xs text-gray-400 hover:text-mangrove-600 cursor-pointer transition-colors">
              <Upload class="w-3.5 h-3.5" />
              上传图片
              <input type="file" accept="image/*" class="hidden" @change="handleLetterImageUpload($event, letter)" />
            </label>
          </div>

          <!-- Uploaded Images Gallery -->
          <div v-if="letterImages[letter.id]?.length" class="mt-3 grid grid-cols-3 gap-2">
            <div v-for="img in letterImages[letter.id]" :key="img.id" class="relative group rounded-lg overflow-hidden border border-gray-100">
              <img :src="img.imageUrl" class="w-full h-24 object-cover cursor-pointer" @click="previewUploadedImage = img.imageUrl" />
              <button
                v-if="img.user?.id === currentUser?.id || isAdmin"
                class="absolute top-1 right-1 w-5 h-5 rounded-full bg-black/60 text-white flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
                @click="deleteLetterImage(letter.id, img.id)"
              >
                <X class="w-3 h-3" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty state -->
      <div v-if="filteredLetters.length === 0" class="text-center py-16 text-gray-400">
        <Mail class="w-12 h-12 mx-auto mb-3 opacity-40" />
        <p>暂无此类来信</p>
      </div>
    </div>

    <div v-if="previewImage" class="fixed inset-0 z-[130] flex items-center justify-center bg-black/95 p-4" @click="previewImage = null">
      <button type="button" class="absolute right-4 top-4 p-2 text-white/70 hover:text-white" aria-label="关闭大图" @click="previewImage = null"><X class="h-6 w-6" /></button>
      <img :src="previewImage.coverUrl" :alt="previewImage.title" class="max-h-[92vh] max-w-[96vw] object-contain" @click.stop />
    </div>

    <div v-if="previewUploadedImage" class="fixed inset-0 z-[130] flex items-center justify-center bg-black/95 p-4" @click="previewUploadedImage = null">
      <button type="button" class="absolute right-4 top-4 p-2 text-white/70 hover:text-white" aria-label="关闭大图" @click="previewUploadedImage = null"><X class="h-6 w-6" /></button>
      <img :src="previewUploadedImage" class="max-h-[92vh] max-w-[96vw] object-contain" @click.stop />
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { Clock, Heart, Mail, Upload, X } from 'lucide-vue-next'
import { useAuth } from '@/composables/useAuth'

const { isLoggedIn, getToken, currentUser, isAdmin } = useAuth()

const tabs = ref([{ label: '全部', value: 'ALL' }])
const activeTab = ref('ALL')
const letters = ref([])
const loading = ref(true)
const previewImage = ref(null)
const previewUploadedImage = ref(null)
const letterImages = ref({})

const filteredLetters = computed(() => {
  if (activeTab.value === 'ALL') return letters.value
  return letters.value.filter(letter => letter.category === activeTab.value)
})

async function fetchCategories() {
  try {
    const res = await fetch('/api/letter-categories')
    const json = await res.json()
    if (json.code === 200 && json.data) {
      tabs.value = [
        { label: '全部', value: 'ALL' },
        ...json.data.map(cat => ({ label: cat.name, value: cat.code }))
      ]
    }
  } catch (e) {
    console.error('加载分类失败:', e)
  }
}

function selectTab(tab) {
  activeTab.value = tab
}

async function fetchLetters() {
  loading.value = true
  try {
    const res = await fetch('/api/letters')
    const json = await res.json()
    if (json.code === 200) {
      // Handle both array and PageResult formats
      if (Array.isArray(json.data)) {
        letters.value = json.data
      } else if (json.data && json.data.content) {
        letters.value = json.data.content
      } else {
        letters.value = []
      }
    }
  } catch (e) {
    console.error('加载来信失败:', e)
  } finally {
    loading.value = false
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

function categoryBadgeClass(category) {
  const colors = {
    QUOTE: 'bg-purple-100 text-purple-700',
    LETTER: 'bg-blue-100 text-blue-700',
    DIARY: 'bg-green-100 text-green-700',
    MONOLOGUE: 'bg-orange-100 text-orange-700',
  }
  return `inline-block px-2.5 py-0.5 rounded-full text-xs font-medium ${colors[category] || 'bg-gray-100 text-gray-600'}`
}

function categoryLabel(category) {
  const tab = tabs.value.find(t => t.value === category)
  return tab ? tab.label : category
}

async function fetchLetterImages(letterId) {
  try {
    const res = await fetch(`/api/letters/${letterId}/images`)
    const json = await res.json()
    if (json.code === 200) {
      letterImages.value[letterId] = json.data || []
    }
  } catch {}
}

async function handleLetterImageUpload(event, letter) {
  const file = event.target.files?.[0]
  if (!file) return
  event.target.value = ''
  try {
    const formData = new FormData()
    formData.append('file', file)
    const uploadRes = await fetch('/api/files/upload', {
      method: 'POST',
      headers: { Authorization: `Bearer ${getToken()}` },
      body: formData
    })
    const uploadJson = await uploadRes.json()
    if (uploadJson.code !== 200) throw new Error(uploadJson.msg || '上传失败')

    await fetch(`/api/letters/${letter.id}/images`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` },
      body: JSON.stringify({ imageUrl: uploadJson.data.url })
    })
    await fetchLetterImages(letter.id)
  } catch (e) {
    console.error('上传图片失败:', e)
  }
}

async function deleteLetterImage(letterId, imageId) {
  if (!confirm('确定要删除这张图片吗？')) return
  try {
    await fetch(`/api/letters/${letterId}/images/${imageId}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${getToken()}` }
    })
    await fetchLetterImages(letterId)
  } catch (e) {
    console.error('删除图片失败:', e)
  }
}

onMounted(async () => {
  await fetchCategories()
  await fetchLetters()
  // Fetch images for all letters
  for (const letter of letters.value) {
    await fetchLetterImages(letter.id)
  }
})
</script>
