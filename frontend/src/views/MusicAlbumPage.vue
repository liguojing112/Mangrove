<template>
  <main class="min-h-screen bg-gray-50 pb-16 pt-20">
    <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
      <RouterLink to="/music" class="mb-5 inline-flex items-center gap-2 text-sm text-gray-500 hover:text-mangrove-700">
        <ArrowLeft class="h-4 w-4" />返回音乐页
      </RouterLink>

      <div v-if="loading" class="py-24 text-center text-sm text-gray-400">加载中...</div>
      <div v-else-if="error" class="py-24 text-center">
        <Images class="mx-auto mb-3 h-10 w-10 text-gray-300" />
        <p class="text-sm text-gray-500">{{ error }}</p>
      </div>
      <template v-else-if="album">
        <header class="grid items-end gap-6 border-b border-gray-200 pb-8 md:grid-cols-[minmax(0,520px)_1fr]">
          <div class="aspect-[16/10] overflow-hidden bg-gray-100">
            <img :src="album.coverUrl" :alt="album.title" class="h-full w-full object-cover" />
          </div>
          <div class="pb-1">
            <p class="text-sm font-medium text-mangrove-700">小芒照片专辑</p>
            <h1 class="mt-2 text-3xl font-bold text-gray-900">{{ album.title }}</h1>
            <p v-if="album.description" class="mt-3 max-w-xl text-sm leading-7 text-gray-500">{{ album.description }}</p>
            <p class="mt-4 text-xs text-gray-400">{{ photos.length }} 张照片</p>
          </div>
        </header>

        <section class="pt-8" aria-label="专辑照片">
          <div v-if="photos.length === 0" class="py-20 text-center text-sm text-gray-400">这个专辑还没有照片</div>
          <div v-else class="grid grid-cols-2 gap-3 md:grid-cols-3 lg:grid-cols-4">
            <button v-for="photo in photos" :key="photo.id" type="button" class="group aspect-square overflow-hidden bg-gray-100" @click="selectedPhoto = photo">
              <img :src="photo.imageUrl" :alt="photo.caption || album.title" class="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105" />
            </button>
          </div>
        </section>
      </template>
    </div>

    <div v-if="selectedPhoto" class="fixed inset-0 z-[130] flex items-center justify-center bg-black/90 p-4" @click.self="selectedPhoto = null">
      <button type="button" class="absolute right-4 top-4 p-2 text-white/80 hover:text-white" aria-label="关闭大图" @click="selectedPhoto = null"><X class="h-6 w-6" /></button>
      <img :src="selectedPhoto.imageUrl" :alt="selectedPhoto.caption || album?.title" class="max-h-[90vh] max-w-[94vw] object-contain" />
    </div>
  </main>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowLeft, Images, X } from 'lucide-vue-next'

const route = useRoute()
const album = ref(null)
const photos = ref([])
const loading = ref(true)
const error = ref('')
const selectedPhoto = ref(null)

onMounted(async () => {
  try {
    const response = await fetch(`/api/music/albums/${route.params.id}`)
    const json = await response.json()
    if (!response.ok || json.code !== 200) throw new Error(json.msg || '专辑不存在或已下架')
    album.value = json.data.album
    photos.value = json.data.photos || []
  } catch (e) {
    error.value = e.message || '专辑加载失败'
  } finally {
    loading.value = false
  }
})
</script>
