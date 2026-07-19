<template>
  <main class="min-h-screen bg-gray-950 pb-20 pt-24 text-white">
    <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
      <router-link to="/merchandise#s6" class="inline-flex items-center gap-2 text-sm text-gray-500 transition-colors hover:text-white"><ArrowLeft class="h-4 w-4" />返回创作者目录</router-link>

      <div v-if="loading" class="py-28 text-center text-sm text-gray-600"><Loader2 class="mx-auto mb-3 h-6 w-6 animate-spin" />加载作品中...</div>
      <div v-else-if="!creator" class="py-28 text-center"><h1 class="text-2xl font-medium">没有找到这位产出者</h1><p class="mt-2 text-sm text-gray-600">该用户可能还没有公开的周边套装。</p></div>

      <template v-else>
        <header class="mt-10 grid gap-6 border-b border-gray-800 pb-10 sm:grid-cols-[112px_minmax(0,1fr)] sm:items-center">
          <div class="flex h-24 w-24 items-center justify-center rounded-full border text-xl font-semibold" :style="avatarStyle(creator.publicId)">{{ avatarText(creator.username) }}</div>
          <div class="min-w-0">
            <p class="text-xs tracking-[0.2em] text-amber-400/70">CREATOR</p>
            <h1 class="mt-2 break-words text-3xl font-medium">{{ creator.username }}</h1>
            <p class="mt-2 font-mono text-xs text-gray-500">ID · {{ creator.publicId }}</p>
            <p v-if="creator.name && creator.name !== creator.username" class="mt-2 text-sm text-gray-500">{{ creator.name }}</p>
          </div>
        </header>

        <div class="mt-12 space-y-16">
          <article v-for="bundle in bundles" :key="bundle.id">
            <div class="max-w-3xl">
              <p class="text-xs tracking-[0.16em] text-amber-400/70">DESIGN INTRODUCTION</p>
              <h2 class="mt-2 text-2xl font-medium">{{ bundle.name }}</h2>
              <p class="mt-4 whitespace-pre-line text-sm leading-7 text-gray-400">{{ bundle.description || '暂无设计介绍' }}</p>
            </div>
            <div class="mt-7 grid grid-cols-2 gap-3 md:grid-cols-3">
              <button v-for="(imageUrl, index) in bundle.bundleImageUrls" :key="`${bundle.id}-${index}`" type="button" class="group relative overflow-hidden rounded-lg border border-gray-800 bg-gray-900 text-left transition-colors hover:border-amber-500/30" @click="selectedImage = { url: imageUrl, name: `${bundle.name} · 周边 ${index + 1}` }">
                <img :src="imageUrl" :alt="`${bundle.name}周边${index + 1}`" class="block h-auto w-full transition-transform duration-300 group-hover:scale-[1.02]" />
                <span class="absolute inset-x-0 bottom-0 bg-gradient-to-t from-black/80 px-3 pb-3 pt-10 text-xs text-white/80">周边 {{ index + 1 }}</span>
              </button>
            </div>
          </article>
        </div>
      </template>
    </div>

    <div v-if="selectedImage" class="fixed inset-0 z-[120] flex items-center justify-center bg-black/95 p-4" @click.self="selectedImage = null">
      <button type="button" class="absolute right-4 top-4 p-2 text-white/70 hover:text-white" aria-label="关闭图片" @click="selectedImage = null"><X class="h-6 w-6" /></button>
      <img :src="selectedImage.url" :alt="selectedImage.name" class="max-h-[90vh] max-w-[96vw] object-contain" />
    </div>
  </main>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowLeft, Loader2, X } from 'lucide-vue-next'

const route = useRoute()
const loading = ref(true)
const allBundles = ref([])
const selectedImage = ref(null)
const bundles = computed(() => allBundles.value.filter(item => item.producerPublicId === route.params.publicId))
const creator = computed(() => {
  const item = bundles.value[0]
  return item ? { publicId: item.producerPublicId, username: item.producerUsername || item.producerName, name: item.producerName } : null
})

function avatarText(username) { return (username || 'U').slice(0, 2).toUpperCase() }
function avatarStyle(publicId) { let hash = 0; for (const char of publicId || '0') hash = ((hash << 5) - hash + char.charCodeAt(0)) | 0; const hue = Math.abs(hash) % 360; return { color: `hsl(${hue} 78% 72%)`, backgroundColor: `hsl(${hue} 55% 18%)`, borderColor: `hsl(${hue} 48% 38%)` } }
async function loadBundles() { try { const response = await fetch('/api/public/merchandise?page=0&size=100&category=BUNDLE'); const json = await response.json(); if (response.ok && json.code === 200) allBundles.value = json.data?.content || [] } finally { loading.value = false } }
onMounted(loadBundles)
</script>
