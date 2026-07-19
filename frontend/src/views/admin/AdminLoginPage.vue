<template>
  <div class="min-h-screen flex items-center justify-center px-4 bg-gray-100">
    <div class="card max-w-sm w-full p-8">
      <!-- Header -->
      <div class="text-center mb-8">
        <div class="w-12 h-12 rounded-2xl bg-mangrove-700 flex items-center justify-center mx-auto mb-4">
          <Settings class="w-6 h-6 text-white" />
        </div>
        <h1 class="text-xl font-bold text-gray-900">青芒纪 管理后台</h1>
        <p class="text-sm text-gray-500 mt-1">请登录管理员账号</p>
      </div>

      <!-- Error Message -->
      <div v-if="errorMsg" class="bg-red-50 text-red-600 text-sm p-3 rounded-lg mb-4">
        {{ errorMsg }}
      </div>

      <!-- Form -->
      <form @submit.prevent="handleLogin">
        <div class="space-y-4">
          <div class="relative">
            <User class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              v-model="username"
              type="text"
              placeholder="用户名"
              class="w-full rounded-xl border border-gray-200 pl-10 pr-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent"
            />
          </div>
          <div class="relative">
            <Lock class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              v-model="password"
              type="password"
              placeholder="密码"
              class="w-full rounded-xl border border-gray-200 pl-10 pr-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent"
            />
          </div>
          <button
            type="submit"
            class="btn-primary w-full"
            :disabled="loading"
          >
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </div>
      </form>

      <p class="text-xs text-gray-400 text-center mt-6">
        <router-link to="/" class="text-mangrove-600 hover:text-mangrove-700">← 返回前台</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Settings } from 'lucide-vue-next'

const router = useRouter()
const username = ref('')
const password = ref('')
const errorMsg = ref('')
const loading = ref(false)

async function handleLogin() {
  if (!username.value || !password.value) {
    errorMsg.value = '请输入用户名和密码'
    return
  }

  loading.value = true
  errorMsg.value = ''

  try {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        username: username.value,
        password: password.value
      })
    })

    const json = await res.json()

    if (json.code === 200 && json.data) {
      const user = {
        id: json.data.userId,
        username: json.data.username,
        nickname: json.data.nickname,
        role: json.data.role
      }

      localStorage.setItem('mangrove_token', json.data.token)
      localStorage.setItem('mangrove_user', JSON.stringify(user))

      if (user.role === 'ADMIN' || user.role === 'SUPER_ADMIN') {
        router.push('/admin')
      } else {
        errorMsg.value = '此账号不是管理员'
      }
    } else {
      errorMsg.value = json.msg || '登录失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>
