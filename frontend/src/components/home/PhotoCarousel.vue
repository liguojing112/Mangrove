<template>
  <div
    class="rounded-2xl overflow-hidden"
    @mouseenter="onMouseEnter"
    @mouseleave="onMouseLeave"
  >
    <Swiper
      :modules="modules"
      :slides-per-view="1"
      :loop="true"
      :autoplay="autoplayConfig"
      :speed="600"
      :pagination="{ clickable: true }"
      class="h-[420px] lg:h-full"
    >
      <SwiperSlide v-for="(slide, i) in slides" :key="i">
        <div
          class="w-full h-full flex items-center justify-center relative"
          :class="slide.bg"
        >
          <div class="text-center px-6">
            <h3 class="text-xl font-bold text-white drop-shadow-sm">{{ slide.title }}</h3>
            <p class="text-sm text-white/80 mt-2">{{ slide.date }}</p>
          </div>
        </div>
      </SwiperSlide>
    </Swiper>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Autoplay, Pagination } from 'swiper/modules'
import 'swiper/css'
import 'swiper/css/pagination'

const modules = [Autoplay, Pagination]

const autoplayConfig = {
  delay: 3000,
  disableOnInteraction: false
}

const swiperRef = ref(null)

const slides = [
  { bg: 'bg-gradient-to-br from-mangrove-300 to-mangrove-500', title: '舞台瞬间', date: '2025-06-20' },
  { bg: 'bg-gradient-to-br from-mangrove-200 to-mangrove-400', title: '机场路透', date: '2025-06-18' },
  { bg: 'bg-gradient-to-br from-mangrove-400 to-mangrove-700', title: '签售会', date: '2025-06-15' },
  { bg: 'bg-gradient-to-br from-mangrove-100 to-mangrove-300', title: '综艺花絮', date: '2025-06-12' },
  { bg: 'bg-gradient-to-br from-mangrove-500 to-mangrove-800', title: '后台直拍', date: '2025-06-10' },
  { bg: 'bg-gradient-to-br from-mangrove-300 to-mangrove-600', title: '粉丝见面', date: '2025-06-08' }
]

function onMouseEnter(e) {
  const swiper = e.target?.querySelector('.swiper')?.swiper
  if (swiper?.autoplay) swiper.autoplay.stop()
}

function onMouseLeave(e) {
  const swiper = e.target?.querySelector('.swiper')?.swiper
  if (swiper?.autoplay) swiper.autoplay.start()
}
</script>

<style scoped>
:deep(.swiper-pagination-bullet) {
  width: 8px;
  height: 8px;
  background: rgba(255, 255, 255, 0.5);
  opacity: 1;
  transition: background-color 0.2s;
}

:deep(.swiper-pagination-bullet-active) {
  background: #2E8B57;
}
</style>
