<template>
  <nav class="sticky top-0 z-50 bg-gradient-to-b from-gray-900 to-gray-950 border-b border-gray-800">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-16">
        <router-link to="/" class="flex items-center gap-1.5 shrink-0">
          <span class="text-2xl">🥭</span>
          <span class="text-mangrove-400 font-bold text-xl">青芒纪</span>
        </router-link>

        <!-- 桌面端钢琴键标签（居中） -->
        <div class="hidden md:flex items-center justify-center flex-1">
          <template v-for="(tab, idx) in tabs" :key="tab.to">
            <!-- 芒果树：顶部弧线钢琴键 -->
            <router-link v-if="tab.special" :to="tab.to"
              class="shrink-0 relative px-4 py-2 text-sm font-medium border border-gray-700 -mx-px z-10 nav-arc-key"
              :class="isActive(tab.to) ? 'bg-mangrove-600 text-white shadow-inner border-mangrove-700' : 'bg-gray-800 text-gray-400 hover:text-white hover:bg-gray-700 shadow-sm'">
              {{ tab.label }}
            </router-link>
            <!-- 普通标签 -->
            <router-link v-else :to="tab.to"
              class="shrink-0 relative px-4 py-2 text-sm font-medium rounded-b-lg border border-gray-700 -mx-px first:rounded-l-lg first:ml-0 last:rounded-r-lg last:mr-0"
              :class="isActive(tab.to) ? 'bg-mangrove-600 text-white shadow-inner translate-y-0.5 z-10 border-mangrove-700 scale-y-95' : 'bg-gray-800 text-gray-400 hover:text-white hover:bg-gray-700 shadow-sm'">
              {{ tab.label }}
            </router-link>
          </template>
        </div>

        <!-- 右侧用户菜单 -->
        <div class="relative">
          <button @click="showRightMenu = !showRightMenu"
            class="p-2 rounded-lg text-gray-400 hover:text-white hover:bg-gray-800 flex items-center gap-1">
            <template v-if="isLoggedIn"><User :size="18" /></template>
            <template v-else><Menu :size="20" /></template>
          </button>
          <div v-if="showRightMenu" class="absolute right-0 top-full mt-2 w-48 bg-gray-800 rounded-xl shadow-lg border border-gray-700 py-1 z-50">
            <template v-if="isLoggedIn">
              <div class="px-4 py-3 border-b border-gray-700">
                <div class="text-sm font-medium text-white">{{ currentUser?.nickname }}</div>
                <div class="text-xs text-gray-400">{{ isAdmin ? '管理员' : '粉丝' }}</div>
              </div>
              <router-link to="/profile" class="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-300 hover:bg-gray-700 hover:text-white" @click="showRightMenu = false"><User :size="16" /> 个人中心</router-link>
              <router-link to="/tree" class="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-300 hover:bg-gray-700 hover:text-white" @click="showRightMenu = false"><TreePine :size="16" /> 我的芒果树</router-link>
              <router-link v-if="isAdmin" to="/admin" class="flex items-center gap-2 px-4 py-2.5 text-sm text-mangrove-400 hover:bg-gray-700" @click="showRightMenu = false"><Settings :size="16" /> 管理后台</router-link>
              <div class="border-t border-gray-700 my-1"></div>
              <button @click="handleLogout" class="flex items-center gap-2 px-4 py-2.5 text-sm text-red-400 hover:bg-gray-700 w-full text-left"><LogOut :size="16" /> 退出登录</button>
            </template>
            <template v-else>
              <router-link to="/login" class="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-300 hover:bg-gray-700 hover:text-white" @click="showRightMenu = false"><LogIn :size="16" /> 登录</router-link>
              <router-link to="/login" class="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-300 hover:bg-gray-700 hover:text-white" @click="showRightMenu = false"><UserPlus :size="16" /> 注册</router-link>
            </template>
          </div>
          <div v-if="showRightMenu" class="fixed inset-0 z-40" @click="showRightMenu = false"></div>
        </div>
      </div>
    </div>

    <!-- 手机端横滑标签栏 -->
    <div class="md:hidden overflow-x-auto border-t border-gray-800 bg-gray-900/95 touch-pan-x" data-tabs="mobile"
      @touchstart.stop @touchend.stop>
      <div class="flex items-center gap-0 px-2 py-1">
        <template v-for="tab in tabs" :key="tab.to">
          <!-- 芒果树：顶部弧线 -->
          <router-link v-if="tab.special" :to="tab.to"
            class="shrink-0 px-3 pt-3 pb-2 text-xs font-medium mx-0.5 nav-arc-key-mobile"
            :class="isActive(tab.to) ? 'bg-mangrove-600 text-white' : 'text-gray-400'">
            {{ tab.label }}
          </router-link>
          <!-- 普通标签 -->
          <router-link v-else :to="tab.to"
            class="shrink-0 px-3 py-2 text-xs font-medium rounded-full mx-0.5"
            :class="isActive(tab.to) ? 'bg-mangrove-600 text-white' : 'text-gray-400'">
            {{ tab.label }}
          </router-link>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Menu, User, TreePine, Settings, LogOut, LogIn, UserPlus } from 'lucide-vue-next'
import { useAuth } from '@/composables/useAuth'

const route = useRoute()
const router = useRouter()
const { isLoggedIn, isAdmin, currentUser, logout } = useAuth()
const showRightMenu = ref(false)

const tabs = [
  { label: '首页', to: '/' },
  { label: '艺人', to: '/artists' },
  { label: '照片', to: '/photos' },
  { label: '短视频', to: '/videos' },
  { label: '长视频', to: '/long-videos' },
  { label: '音乐', to: '/music' },
  { label: '芒果树', to: '/tree', special: true },
  { label: '创作', to: '/works' },
  { label: '周边', to: '/merchandise' },
  { label: '行程', to: '/schedule' },
  { label: '游戏', to: '/games' },
  { label: '社区', to: '/community' },
  { label: '来信', to: '/letters' },
]

function handleLogout() {
  logout()
  showRightMenu.value = false
  router.push('/')
}

function isActive(to) {
  if (to === '/') return route.path === '/'
  return route.path.startsWith(to)
}

// 切换页面时手机端标签栏自动滚动
watch(() => route.path, () => {
  nextTick(() => {
    const el = document.querySelector('[data-tabs="mobile"]')
    if (!el) return
    const active = el.querySelector('.bg-mangrove-600')
    if (active) active.scrollIntoView({ behavior: 'smooth', inline: 'center' })
  })
})
</script>

<style scoped>
.nav-arc-key {
  border-radius: 0 0 0.5rem 0.5rem;
  position: relative;
}
.nav-arc-key::before {
  content: '';
  position: absolute;
  top: -6px;
  left: -1px;
  right: -1px;
  height: 12px;
  border-radius: 50% 50% 0 0;
  border: 1px solid #374151;
  border-bottom: none;
  background: inherit;
}
.nav-arc-key:hover::before {
  border-color: #4b5563;
}
.nav-arc-key-mobile {
  border-radius: 50% 50% 0.5rem 0.5rem;
}
</style>
