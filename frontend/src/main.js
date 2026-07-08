import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import './style.css'
import routes from './router'

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 路由守卫：需要登录 + 角色检查
router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const token = localStorage.getItem('mangrove_token')
  const user = JSON.parse(localStorage.getItem('mangrove_user') || 'null')

  if (requiresAuth && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  // 角色检查：支持单个 role 或 roles 数组
  const requiredRole = to.matched.find(r => r.meta.role || r.meta.roles)
  if (requiredRole) {
    const roles = requiredRole.meta.roles || [requiredRole.meta.role]
    if (!roles.includes(user?.role)) {
      next({ path: '/', replace: true })
      return
    }
  }

  next()
})

const app = createApp(App)
app.use(router)
app.use(ElementPlus)

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
