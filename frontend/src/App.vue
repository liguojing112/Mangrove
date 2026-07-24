<template>
  <IntroOverlay @ready="onIntroReady" />

  <!-- 欢迎页加载期间隐藏主内容，避免闪现 -->
  <template v-if="appReady">
    <div v-if="isAdminRoute" class="h-screen overflow-hidden">
      <router-view :key="$route.fullPath" />
    </div>

    <div v-else class="min-h-screen flex flex-col bg-gray-50">
      <AppNavbar />
      <main class="flex-1">
        <router-view :key="$route.fullPath" />
      </main>
      <AppFooter />
    </div>
  </template>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAudioPlayer } from '@/composables/useAudioPlayer'
import IntroOverlay from '@/components/IntroOverlay.vue'
import AppNavbar from '@/components/AppNavbar.vue'
import AppFooter from '@/components/AppFooter.vue'

const route = useRoute()
const isAdminRoute = computed(() => route.path.startsWith('/admin'))
const appReady = ref(false)

function onIntroReady() {
  appReady.value = true
}

useAudioPlayer()
</script>
