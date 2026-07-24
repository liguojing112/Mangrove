<template>
  <div class="flex h-full">
    <!-- Mobile overlay -->
    <div v-if="sidebarOpen" class="fixed inset-0 bg-black/50 z-30 md:hidden" @click="sidebarOpen=false" />

    <!-- Sidebar -->
    <aside
      class="w-64 bg-gray-900 flex-shrink-0 flex flex-col fixed h-full z-40 transition-transform duration-200"
      :class="sidebarOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0'"
    >
      <!-- Logo -->
      <div class="px-6 py-5 border-b border-gray-800 flex items-center justify-between">
        <router-link to="/admin" class="text-lg font-bold text-mangrove-400 tracking-wide">
          Candice's Mango Ode Admin
        </router-link>
        <button class="md:hidden p-1 text-gray-400 hover:text-white" @click="sidebarOpen=false">
          <X class="w-5 h-5" />
        </button>
      </div>

      <!-- Nav -->
      <nav class="flex-1 overflow-y-auto py-2">
        <router-link
          v-for="item in visibleNavItems"
          :key="item.to"
          :to="item.to"
          class="flex items-center gap-3 px-6 py-3 text-sm transition-colors"
          :class="
            route.path === item.to
              ? 'text-white bg-mangrove-700/20 border-r-2 border-mangrove-500'
              : 'text-gray-400 hover:text-white hover:bg-gray-800/50'
          "
          @click="sidebarOpen=false"
        >
          <component :is="item.icon" class="w-[18px] h-[18px]" />
          <span>{{ item.label }}</span>
        </router-link>
      </nav>

      <!-- Bottom -->
      <div class="border-t border-gray-800 pt-4 px-6 pb-5">
        <router-link
          to="/"
          class="flex items-center gap-3 text-sm text-gray-400 hover:text-white transition-colors"
        >
          <LayoutDashboard class="w-[18px] h-[18px]" />
          <span>返回前台</span>
        </router-link>
      </div>
    </aside>

    <!-- Main -->
    <div class="flex-1 md:ml-64 flex flex-col bg-gray-100 min-h-0 min-w-0 overflow-x-hidden">
      <!-- Top bar -->
      <header class="h-14 bg-white border-b border-gray-200 flex items-center justify-between px-4 md:px-6 flex-shrink-0">
        <div class="flex items-center gap-3">
          <button class="md:hidden p-1 text-gray-500 hover:text-gray-800" @click="sidebarOpen=true">
            <Menu class="w-5 h-5" />
          </button>
          <h1 class="text-sm font-semibold text-gray-800">{{ route.meta.title || '管理后台' }}</h1>
        </div>
        <div class="w-8 h-8 rounded-full bg-mangrove-200 flex items-center justify-center">
          <span class="text-xs font-medium text-mangrove-800">A</span>
        </div>
      </header>

      <!-- Content -->
      <main class="p-4 md:p-6 flex-1 overflow-y-auto min-h-0">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import {
  LayoutDashboard,
  Users,
  Gift,
  PenTool,
  CalendarDays,
  MessageSquare,
  ShieldCheck,
  FolderOpen,
  Gamepad2,
  Menu,
  X,
  Star,
} from 'lucide-vue-next'

const route = useRoute()
const sidebarOpen = ref(false)

const { currentUser } = useAuth()

const userRole = computed(() => currentUser.value?.role || '')

const navItems = [
  { to: '/admin', label: '仪表盘', icon: LayoutDashboard },
  { to: '/admin/homepage', label: '首页管理', icon: Star },
  { to: '/admin/audit', label: '内容审核', icon: ShieldCheck, role: 'SUPER_ADMIN' },
  { to: '/admin/media', label: '资源管理', icon: FolderOpen },
  { to: '/admin/artists', label: '艺人管理', icon: Users },
  { to: '/admin/merchandise', label: '周边管理', icon: Gift },
  { to: '/admin/works', label: '创作管理', icon: PenTool },
  { to: '/admin/schedules', label: '行程管理', icon: CalendarDays },
  { to: '/admin/comments', label: '评论管理', icon: MessageSquare },
  { to: '/admin/users', label: '用户管理', icon: Users },
  { to: '/admin/game', label: '游戏管理', icon: Gamepad2 },
  { to: '/admin/letters', label: '来信管理', icon: MessageSquare },
]

const visibleNavItems = computed(() => {
  return navItems.filter(item => !item.role || item.role === userRole.value)
})
</script>
