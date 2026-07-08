<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24 pb-16">
    <!-- Not Logged In -->
    <div class="max-w-md mx-auto">
      <div class="card p-8 text-center">
        <div class="bg-mangrove-100 rounded-2xl h-48 flex items-center justify-center mb-6">
          <User class="w-20 h-20 text-mangrove-400/60" />
        </div>
        <p class="text-gray-500 mb-6">登录后查看个人中心</p>
        <router-link to="/login" class="btn-primary inline-block">立即登录</router-link>
      </div>
    </div>

    <!-- Logged In (placeholder) -->
    <div>
      <!-- Profile Header -->
      <div class="card p-8 mb-8">
        <div class="flex flex-col sm:flex-row items-center sm:items-start gap-6">
          <div class="w-24 h-24 rounded-full bg-mangrove-200 flex items-center justify-center flex-shrink-0">
            <User class="w-12 h-12 text-mangrove-700" />
          </div>
          <div class="text-center sm:text-left">
            <h1 class="text-xl font-bold text-gray-900 mb-1">芒果小达人</h1>
            <p class="text-sm text-gray-600 mb-3">热爱音乐，热爱生活，青芒纪忠实粉丝 🌿</p>
            <p class="text-sm text-gray-400">关注 128 · 粉丝 256 · 获赞 1.2k</p>
          </div>
        </div>
      </div>

      <!-- Content Tabs -->
      <div class="flex items-center gap-2 mb-6">
        <button
          v-for="tab in contentTabs"
          :key="tab"
          class="px-5 py-2 rounded-full text-sm font-medium transition-colors"
          :class="activeTab === tab
            ? 'bg-mangrove-700 text-white'
            : 'bg-gray-100 text-gray-500 hover:bg-gray-200'"
          @click="activeTab = tab"
        >
          {{ tab }}
        </button>
      </div>

      <!-- Content Grid -->
      <div class="grid grid-cols-3 gap-4 mb-12">
        <div
          v-for="n in 9"
          :key="n"
          class="bg-mangrove-100 rounded-xl aspect-square flex items-center justify-center"
        >
          <Image class="w-8 h-8 text-mangrove-400/50" />
        </div>
      </div>

      <!-- Settings -->
      <h2 class="font-semibold text-gray-900 mb-4">设置</h2>
      <div class="card overflow-hidden">
        <div
          v-for="(item, idx) in settings"
          :key="item"
          class="flex items-center justify-between px-5 py-4"
          :class="idx < settings.length - 1 ? 'border-b border-gray-100' : ''"
        >
          <span class="text-sm text-gray-700">{{ item }}</span>
          <ChevronRight class="w-4 h-4 text-gray-400" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { User, Image, ChevronRight } from 'lucide-vue-next'

const contentTabs = ['我的作品', '我的收藏', '我的评论']
const activeTab = ref('我的作品')

const settings = ['修改资料', '账号安全', '通知设置', '关于青芒纪']
</script>
