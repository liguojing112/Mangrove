<template>
  <nav class="sticky top-0 z-50 bg-gradient-to-b from-gray-900 to-gray-950 border-b border-gray-800">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-16">
        <router-link to="/" class="flex items-center gap-1.5 shrink-0">
          <span class="text-2xl">🥭</span>
          <span class="text-mangrove-400 font-bold text-xl">Candice's Mango Ode</span>
        </router-link>

        <!-- 桌面端胶囊导航 -->
        <div v-show="!isCollapsed" class="hidden md:flex items-center justify-center flex-1">
          <div class="nav-pill">
            <template v-for="(tab, idx) in tabs" :key="tab.to">
              <span v-if="idx > 0" class="nav-divider" aria-hidden="true"></span>
              <router-link :to="tab.to"
                class="nav-pill-item"
                :class="[
                  isActive(tab.to) ? 'nav-pill-active' : '',
                  tab.special ? 'nav-pill-special' : ''
                ]">
                {{ tab.label }}
              </router-link>
            </template>
          </div>
        </div>

        <!-- 折叠按钮 -->
        <button @click="toggleCollapse"
          class="p-2 rounded-lg text-gray-400 hover:text-white hover:bg-white/10 transition-colors"
          :title="isCollapsed ? '展开导航栏' : '折叠导航栏'">
          <ChevronUp v-if="!isCollapsed" :size="18" />
          <ChevronDown v-else :size="18" />
        </button>

        <!-- 右侧用户菜单 -->
        <div class="relative">
          <button @click="showRightMenu = !showRightMenu"
            class="p-2 rounded-lg text-gray-400 hover:text-white hover:bg-white/10 flex items-center gap-1">
            <template v-if="isLoggedIn"><User :size="18" /></template>
            <template v-else><Menu :size="20" /></template>
          </button>
          <div v-if="showRightMenu" class="absolute right-0 top-full mt-2 w-48 rounded-xl shadow-lg py-1 z-50 nav-dropdown">
            <template v-if="isLoggedIn">
              <div class="px-4 py-3 border-b border-white/10">
                <div class="text-sm font-medium text-white">{{ currentUser?.nickname }}</div>
                <div class="text-xs text-gray-400">{{ isAdmin ? '管理员' : '粉丝' }}</div>
              </div>
              <router-link to="/profile" class="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-300 hover:text-white nav-dropdown-item" @click="showRightMenu = false"><User :size="16" /> 个人中心</router-link>
              <router-link to="/tree" class="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-300 hover:text-white nav-dropdown-item" @click="showRightMenu = false"><TreePine :size="16" /> 我的芒果树</router-link>
              <router-link v-if="isAdmin" to="/admin" class="flex items-center gap-2 px-4 py-2.5 text-sm text-mangrove-400 hover:text-white nav-dropdown-item" @click="showRightMenu = false"><Settings :size="16" /> 管理后台</router-link>
              <div class="border-t border-white/10 my-1"></div>
              <button @click="handleLogout" class="flex items-center gap-2 px-4 py-2.5 text-sm text-red-400 hover:text-white nav-dropdown-item w-full text-left"><LogOut :size="16" /> 退出登录</button>
            </template>
            <template v-else>
              <router-link to="/login" class="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-300 hover:text-white nav-dropdown-item" @click="showRightMenu = false"><LogIn :size="16" /> 登录</router-link>
              <router-link to="/login" class="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-300 hover:text-white nav-dropdown-item" @click="showRightMenu = false"><UserPlus :size="16" /> 注册</router-link>
            </template>
          </div>
          <div v-if="showRightMenu" class="fixed inset-0 z-40" @click="showRightMenu = false"></div>
        </div>
      </div>
    </div>

    <!-- 手机端横滑胶囊导航 -->
    <div v-show="!isCollapsed" class="md:hidden overflow-x-auto touch-pan-x nav-mobile-bar" data-tabs="mobile"
      @touchstart.stop @touchend.stop>
      <div class="flex items-center gap-0 px-3 py-2 nav-pill-mobile">
        <template v-for="(tab, idx) in tabs" :key="tab.to">
          <span v-if="idx > 0" class="nav-divider" aria-hidden="true"></span>
          <router-link :to="tab.to"
            class="shrink-0 px-3 py-1.5 text-xs font-medium whitespace-nowrap nav-pill-item"
            :class="[
              isActive(tab.to) ? 'nav-pill-active' : '',
              tab.special ? 'nav-pill-special' : ''
            ]">
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
import { Menu, User, TreePine, Settings, LogOut, LogIn, UserPlus, ChevronUp, ChevronDown } from 'lucide-vue-next'
import { useAuth } from '@/composables/useAuth'

const route = useRoute()
const router = useRouter()
const { isLoggedIn, isAdmin, currentUser, logout } = useAuth()
const showRightMenu = ref(false)
const isCollapsed = ref(localStorage.getItem('mangrove_nav_collapsed') === 'true')

function toggleCollapse() {
  isCollapsed.value = !isCollapsed.value
  localStorage.setItem('mangrove_nav_collapsed', isCollapsed.value)
}

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
    const active = el.querySelector('.nav-pill-active')
    if (active) active.scrollIntoView({ behavior: 'smooth', inline: 'center' })
  })
})
</script>

<style scoped>
/* ===== 透明毛玻璃导航栏 ===== */

/* 顶部导航容器：深色半透明 + 毛玻璃 */
.nav-glass {
  background: rgba(10, 10, 12, 0.55);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

/* 桌面端胶囊容器 */
.nav-pill {
  display: flex;
  align-items: center;
  background: rgba(0, 0, 0, 0.45);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 9999px;
  padding: 4px 6px;
  box-shadow:
    0 0 12px rgba(46, 139, 87, 0.5),
    0 0 30px rgba(46, 139, 87, 0.25),
    0 0 50px rgba(46, 139, 87, 0.1),
    0 2px 12px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
}

/* 导航项 */
.nav-pill-item {
  color: rgba(200, 210, 200, 0.7);
  font-size: 0.8125rem;
  font-weight: 500;
  padding: 6px 14px;
  border-radius: 9999px;
  transition: all 0.25s ease;
  white-space: nowrap;
  position: relative;
}
.nav-pill-item:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.06);
}

/* 激活状态：绿色高亮 */
.nav-pill-active {
  background: linear-gradient(135deg, rgba(46, 139, 87, 0.85) 0%, rgba(34, 112, 74, 0.85) 100%);
  color: #fff;
  box-shadow:
    0 2px 8px rgba(46, 139, 87, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
}
.nav-pill-active:hover {
  background: linear-gradient(135deg, rgba(46, 139, 87, 0.95) 0%, rgba(34, 112, 74, 0.95) 100%);
  color: #fff;
}

/* 芒果树特殊标签：微金色点缀 */
.nav-pill-special {
  color: rgba(245, 200, 120, 0.8);
}
.nav-pill-special:hover {
  color: rgba(245, 210, 140, 1);
}
.nav-pill-special.nav-pill-active {
  background: linear-gradient(135deg, rgba(46, 139, 87, 0.85) 0%, rgba(180, 130, 30, 0.75) 100%);
  color: #fff;
}

/* 分割线 */
.nav-divider {
  width: 1px;
  height: 14px;
  background: rgba(255, 255, 255, 0.1);
  flex-shrink: 0;
}

/* 手机端胶囊容器 */
.nav-pill-mobile {
  background: rgba(0, 0, 0, 0.45);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 9999px;
  margin: 0 12px 8px;
  box-shadow:
    0 0 12px rgba(46, 139, 87, 0.5),
    0 0 30px rgba(46, 139, 87, 0.25),
    0 0 50px rgba(46, 139, 87, 0.1),
    0 2px 12px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
  width: max-content;
}

/* 手机端外层容器 */
.nav-mobile-bar {
  background: transparent;
  border-top: none;
  padding-bottom: 4px;
}

/* 下拉菜单：毛玻璃 */
.nav-dropdown {
  background: rgba(20, 20, 24, 0.85);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.08);
}
.nav-dropdown-item {
  transition: all 0.2s ease;
}
.nav-dropdown-item:hover {
  background: rgba(255, 255, 255, 0.06);
}
</style>
