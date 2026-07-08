<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24 pb-16">
    <!-- Not Logged In -->
    <div v-if="!isLoggedIn" class="max-w-md mx-auto">
      <div class="card p-8 text-center">
        <div class="bg-mangrove-100 rounded-2xl h-48 flex items-center justify-center mb-6">
          <TreePine class="w-20 h-20 text-mangrove-400/60" />
        </div>
        <p class="text-gray-500 mb-6">登录后查看你的专属芒果树</p>
        <router-link to="/login" class="btn-primary inline-block">立即登录</router-link>
      </div>
    </div>

    <!-- Logged In -->
    <div v-else>
      <!-- Header -->
      <div class="flex items-center gap-3 mb-6">
        <h1 class="section-title">我的芒果树</h1>
        <span class="bg-mangrove-100 text-mangrove-700 rounded-full px-4 py-1 text-sm font-medium">
          Lv.12
        </span>
      </div>

      <!-- EXP Bar -->
      <div class="mb-6">
        <div class="bg-gray-200 rounded-full h-3 w-full">
          <div class="bg-mangrove-600 rounded-full h-3" style="width: 54%"></div>
        </div>
        <p class="text-xs text-gray-400 mt-1">650 / 1200 EXP</p>
      </div>

      <!-- Tree Display -->
      <div class="bg-gradient-to-b from-mangrove-50 to-mangrove-100 rounded-2xl h-64 flex items-center justify-center relative mb-8">
        <TreePine class="w-32 h-32 text-mangrove-700" />
        <Sparkles class="w-5 h-5 text-mangrove-400 absolute top-8 left-[30%]" />
        <Star class="w-4 h-4 text-yellow-400 absolute top-12 right-[28%]" />
        <Sparkles class="w-3 h-3 text-mangrove-300 absolute bottom-16 left-[25%]" />
      </div>

      <!-- Sign In Button -->
      <div class="flex justify-center mb-8">
        <button class="btn-primary text-base px-8 py-3">今日签到</button>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-3 gap-4 mb-10">
        <div class="card p-4 text-center">
          <p class="text-xl font-bold text-mangrove-700">45 天</p>
          <p class="text-xs text-gray-500 mt-1">连续签到</p>
        </div>
        <div class="card p-4 text-center">
          <p class="text-xl font-bold text-mangrove-700">328</p>
          <p class="text-xs text-gray-500 mt-1">累计获赞</p>
        </div>
        <div class="card p-4 text-center">
          <p class="text-xl font-bold text-mangrove-700">12</p>
          <p class="text-xs text-gray-500 mt-1">上传作品</p>
        </div>
      </div>

      <!-- Decorations -->
      <h2 class="font-semibold text-gray-900 mb-4">装饰道具</h2>
      <div class="grid grid-cols-3 md:grid-cols-5 gap-3">
        <div
          v-for="item in decorations"
          :key="item.name"
          class="bg-mangrove-50 rounded-xl p-3 text-center"
        >
          <component :is="item.icon" class="w-6 h-6 text-mangrove-600 mx-auto mb-1" />
          <p class="text-xs text-gray-600">{{ item.name }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { TreePine, Sparkles, Star, Heart, Music, Camera, Award, Sun, CloudRain } from 'lucide-vue-next'
import { useAuth } from '@/composables/useAuth'

const { isLoggedIn } = useAuth()

const decorations = [
  { name: '小太阳', icon: Sun },
  { name: '音乐盒', icon: Music },
  { name: '爱心', icon: Heart },
  { name: '相机', icon: Camera },
  { name: '奖杯', icon: Award },
  { name: '彩虹', icon: CloudRain },
  { name: '星星', icon: Star },
]
</script>
