<template>
  <div class="flex h-full">
    <!-- Sidebar -->
    <aside class="w-64 bg-gray-900 flex-shrink-0 flex flex-col fixed h-full z-30">
      <!-- Logo -->
      <div class="px-6 py-5 border-b border-gray-800">
        <router-link to="/admin" class="text-lg font-bold text-mangrove-400 tracking-wide">
          青芒纪 Admin
        </router-link>
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
    <div class="flex-1 ml-64 flex flex-col bg-gray-100 min-h-0">
      <!-- Top bar -->
      <header class="h-14 bg-white border-b border-gray-200 flex items-center justify-between px-6 flex-shrink-0">
        <h1 class="text-sm font-semibold text-gray-800">{{ route.meta.title || '管理后台' }}</h1>
        <div class="w-8 h-8 rounded-full bg-mangrove-200 flex items-center justify-center">
          <span class="text-xs font-medium text-mangrove-800">A</span>
        </div>
      </header>

      <!-- Content -->
      <main class="p-6 flex-1 overflow-y-auto min-h-0">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  LayoutDashboard,
  Users,
  Music,
  Gift,
  PenTool,
  CalendarDays,
  MessageSquare,
  ShieldCheck,
  FolderOpen,
} from 'lucide-vue-next'

const route = useRoute()

const userRole = computed(() => {
  const user = JSON.parse(localStorage.getItem('mangrove_user') || 'null')
  return user?.role || ''
})

const navItems = [
  { to: '/admin', label: '仪表盘', icon: LayoutDashboard },
  { to: '/admin/audit', label: '内容审核', icon: ShieldCheck, role: 'SUPER_ADMIN' },
  { to: '/admin/media', label: '资源管理', icon: FolderOpen },
  { to: '/admin/artists', label: '艺人管理', icon: Users },
  { to: '/admin/music', label: '音乐管理', icon: Music },
  { to: '/admin/merchandise', label: '周边管理', icon: Gift },
  { to: '/admin/works', label: '作品管理', icon: PenTool },
  { to: '/admin/schedules', label: '行程管理', icon: CalendarDays },
  { to: '/admin/comments', label: '评论管理', icon: MessageSquare },
  { to: '/admin/users', label: '用户管理', icon: Users },
]

const visibleNavItems = computed(() => {
  return navItems.filter(item => !item.role || item.role === userRole.value)
})
</script>
