import { reactive, computed, readonly } from 'vue'

const TOKEN_KEY = 'mangrove_token'
const USER_KEY = 'mangrove_user'

// 全局单例状态
const state = reactive({
  token: localStorage.getItem(TOKEN_KEY) || null,
  user: JSON.parse(localStorage.getItem(USER_KEY) || 'null'),
  loading: false,
  error: null
})

export function useAuth() {
  const isLoggedIn = computed(() => !!state.token && !!state.user)
  const isAdmin = computed(() => state.user?.role === 'ADMIN' || state.user?.role === 'SUPER_ADMIN')
  const currentUser = computed(() => state.user)

  function saveAuth(token, user) {
    state.token = token
    state.user = user
    localStorage.setItem(TOKEN_KEY, token)
    localStorage.setItem(USER_KEY, JSON.stringify(user))
  }

  function clearAuth() {
    state.token = null
    state.user = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  async function login(username, password) {
    state.loading = true
    state.error = null
    try {
      const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      })
      const json = await res.json()
      if (json.code === 200 && json.data) {
        saveAuth(json.data.token, {
          id: json.data.userId,
          username: json.data.username,
          nickname: json.data.nickname,
          role: json.data.role
        })
        return { success: true }
      } else {
        state.error = json.msg || '登录失败'
        return { success: false, error: json.msg }
      }
    } catch (e) {
      state.error = '网络错误，请确保后端服务已启动'
      return { success: false, error: state.error }
    } finally {
      state.loading = false
    }
  }

  async function register(username, password, nickname, email) {
    state.loading = true
    state.error = null
    try {
      const res = await fetch('/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password, nickname, email })
      })
      const json = await res.json()
      if (json.code === 200 && json.data) {
        saveAuth(json.data.token, {
          id: json.data.userId,
          username: json.data.username,
          nickname: json.data.nickname,
          role: json.data.role
        })
        return { success: true }
      } else {
        state.error = json.msg || '注册失败'
        return { success: false, error: json.msg }
      }
    } catch (e) {
      state.error = '网络错误，请确保后端服务已启动'
      return { success: false, error: state.error }
    } finally {
      state.loading = false
    }
  }

  async function validateToken() {
    if (!state.token) return false
    try {
      const res = await fetch('/api/auth/me', {
        headers: { 'Authorization': `Bearer ${state.token}` }
      })
      const json = await res.json()
      if (json.code === 200 && json.data) {
        state.user = {
          id: json.data.id,
          username: json.data.username,
          nickname: json.data.nickname,
          role: json.data.role
        }
        localStorage.setItem(USER_KEY, JSON.stringify(state.user))
        return true
      } else {
        clearAuth()
        return false
      }
    } catch {
      // 后端未启动时不清除 token，允许离线使用
      return !!state.token
    }
  }

  function logout() {
    clearAuth()
  }

  // 初始化时静默验证 token
  if (state.token && state.user) {
    validateToken()
  }

  return {
    isLoggedIn,
    isAdmin,
    currentUser,
    loading: computed(() => state.loading),
    error: computed(() => state.error),
    login,
    register,
    logout,
    validateToken,
    getToken: () => state.token
  }
}
