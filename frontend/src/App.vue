<template>
  <!-- 管理后台：无导航栏/页脚，AdminLayout 自己管理布局 -->
  <div v-if="isAdminRoute" class="h-screen overflow-hidden">
    <router-view :key="$route.fullPath" />
  </div>

  <!-- 前台页面：正常布局 -->
  <div v-else class="min-h-screen flex flex-col bg-gray-50" @touchstart.passive="onTouchStart" @touchend="onTouchEnd">
    <AppNavbar />
    <main class="flex-1">
      <router-view :key="$route.fullPath" />
    </main>
    <AppFooter />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import AppNavbar from '@/components/AppNavbar.vue'
import AppFooter from '@/components/AppFooter.vue'

const router = useRouter()
const route = useRoute()

const isAdminRoute = computed(() => route.path.startsWith('/admin'))

const tabRoutes = [
  '/', '/artists', '/photos', '/videos', '/music',
  '/merchandise', '/works', '/schedule', '/games', '/community', '/tree'
]

let startX = 0
let startY = 0

function onTouchStart(e) {
  startX = e.touches[0].clientX
  startY = e.touches[0].clientY
}

function onTouchEnd(e) {
  const dx = e.changedTouches[0].clientX - startX
  const dy = e.changedTouches[0].clientY - startY

  if (Math.abs(dx) > 80 && Math.abs(dx) > Math.abs(dy)) {
    const currentIdx = tabRoutes.indexOf(router.currentRoute.value.path)
    if (currentIdx === -1) return

    if (dx < 0) {
      const next = (currentIdx + 1) % tabRoutes.length
      router.push(tabRoutes[next])
    } else if (dx > 0) {
      const prev = (currentIdx - 1 + tabRoutes.length) % tabRoutes.length
      router.push(tabRoutes[prev])
    }
  }
}
</script>
