<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <!-- Title -->
    <h1 class="section-title mb-8">艺人档案</h1>

    <!-- Search Bar -->
    <div class="relative mb-8">
      <Search class="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
      <input
        type="text"
        placeholder="搜索艺人..."
        class="w-full pl-12 pr-4 py-3 rounded-full border border-gray-200 focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent text-sm"
      />
    </div>

    <!-- Artist Grid -->
    <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
      <router-link
        v-for="artist in artists"
        :key="artist.id"
        :to="'/artists/' + artist.id"
        class="card-hover p-4 block"
      >
        <div class="bg-mangrove-100 rounded-xl aspect-square mb-3"></div>
        <h3 class="font-semibold text-gray-900 text-sm">{{ artist.name }}</h3>
        <p class="text-xs text-gray-500 mb-2">{{ artist.stageName }}</p>
        <span class="tag">出道 {{ artist.debutDays }}天</span>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { Search } from 'lucide-vue-next'

const artists = [
  { id: 1, name: '林小芒', stageName: 'Mango Lin', debutDays: 1287 },
  { id: 2, name: '陈青青', stageName: 'Qing Chen', debutDays: 856 },
  { id: 3, name: '王绿意', stageName: 'Ivy Wang', debutDays: 2103 },
  { id: 4, name: '张夏果', stageName: 'Summer Zhang', debutDays: 432 },
  { id: 5, name: '李芒果', stageName: 'Mango Lee', debutDays: 1567 },
  { id: 6, name: '周星芒', stageName: 'Star Zhou', debutDays: 923 },
  { id: 7, name: '吴青柠', stageName: 'Lemon Wu', debutDays: 678 },
  { id: 8, name: '赵小树', stageName: 'Tree Zhao', debutDays: 1845 },
]
</script>
