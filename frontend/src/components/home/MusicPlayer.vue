<template>
  <div class="fixed bottom-6 right-6 z-50">
    <!-- Collapsed: circle button -->
    <button
      v-if="!isExpanded"
      class="w-14 h-14 rounded-full bg-mangrove-700 shadow-lg flex items-center justify-center text-white hover:bg-mangrove-800 transition-colors duration-200 animate-pulse"
      @click="expand"
    >
      <Music class="w-6 h-6" />
    </button>

    <!-- Expanded: horizontal bar -->
    <div
      v-else
      class="bg-white rounded-2xl shadow-lg px-4 py-3 flex items-center gap-3"
    >
      <!-- Cover -->
      <div class="w-10 h-10 rounded-lg bg-mangrove-200 flex-shrink-0 flex items-center justify-center">
        <Music class="w-5 h-5 text-mangrove-600" />
      </div>

      <!-- Song info -->
      <div class="min-w-0">
        <p class="text-sm font-medium text-gray-800">春日序曲</p>
        <p class="text-xs text-gray-400">小芒</p>
      </div>

      <!-- Mute toggle -->
      <button
        class="w-8 h-8 rounded-full hover:bg-gray-100 flex items-center justify-center transition-colors duration-150"
        @click="toggleMute"
      >
        <VolumeX v-if="isMuted" class="w-4 h-4 text-gray-400" />
        <Volume2 v-else class="w-4 h-4 text-mangrove-600" />
      </button>

      <!-- Close -->
      <button
        class="w-6 h-6 text-gray-400 hover:text-gray-600 flex items-center justify-center transition-colors duration-150"
        @click="collapse"
      >
        <X class="w-4 h-4" />
      </button>
    </div>

    <!-- Hidden audio element -->
    <audio ref="audioRef" src="/audio/bgm.mp3" loop autoplay muted @play="isPlaying = true" @pause="isPlaying = false" />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { Music, Volume2, VolumeX, X } from 'lucide-vue-next'

const audioRef = ref(null)
const isMuted = ref(true)
const isExpanded = ref(false)
const isPlaying = ref(false)

function expand() {
  isExpanded.value = true
  isMuted.value = false
}

function collapse() {
  isExpanded.value = false
  isMuted.value = true
}

function toggleMute() {
  isMuted.value = !isMuted.value
}

watch(isMuted, (val) => {
  if (audioRef.value) {
    audioRef.value.muted = val
  }
})

onMounted(() => {
  if (audioRef.value) {
    audioRef.value.play().catch(() => {})
  }
})
</script>
