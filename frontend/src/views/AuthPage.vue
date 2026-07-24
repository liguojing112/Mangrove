<template>
  <div class="min-h-screen flex items-center justify-center px-4 py-20 bg-gray-50">
    <div class="card max-w-md w-full p-8">
      <!-- Tab Toggle -->
      <div class="flex items-center justify-center gap-6 mb-8">
        <button
          class="text-lg font-medium pb-1 transition-colors"
          :class="activeTab === 'login'
            ? 'text-mangrove-700 underline underline-offset-4 decoration-2'
            : 'text-gray-400 hover:text-gray-600'"
          @click="switchTab('login')"
        >
          登录
        </button>
        <button
          class="text-lg font-medium pb-1 transition-colors"
          :class="activeTab === 'register'
            ? 'text-mangrove-700 underline underline-offset-4 decoration-2'
            : 'text-gray-400 hover:text-gray-600'"
          @click="switchTab('register')"
        >
          注册
        </button>
      </div>

      <!-- Error Message -->
      <div v-if="authError" class="mb-4 p-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm flex items-center gap-2">
        <AlertCircle :size="16" />
        {{ authError }}
      </div>

      <!-- Login Form -->
      <form v-if="activeTab === 'login'" @submit.prevent="handleLogin" class="space-y-4">
        <div class="relative">
          <User class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            v-model="loginForm.username"
            type="text"
            placeholder="用户名"
            required
            class="w-full rounded-xl border border-gray-200 pl-10 pr-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent"
          />
        </div>
        <div class="relative">
          <Lock class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            required
            class="w-full rounded-xl border border-gray-200 pl-10 pr-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent"
          />
        </div>
        <div class="flex items-center justify-between">
          <label class="flex items-center gap-2 text-sm text-gray-600 cursor-pointer">
            <input type="checkbox" class="rounded border-gray-300 text-mangrove-600 focus:ring-mangrove-500" />
            记住我
          </label>
          <a href="#" class="text-sm text-mangrove-600 hover:text-mangrove-700">忘记密码？</a>
        </div>
        <button
          type="submit"
          class="btn-primary w-full flex items-center justify-center gap-2"
          :disabled="authLoading"
        >
          <Loader2 v-if="authLoading" :size="16" class="animate-spin" />
          {{ authLoading ? '登录中...' : '登录' }}
        </button>
      </form>

      <!-- Register Form -->
      <form v-if="activeTab === 'register'" @submit.prevent="handleRegister" class="space-y-4">
        <div class="relative">
          <User class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            v-model="registerForm.username"
            type="text"
            placeholder="用户名"
            required
            minlength="1"
            class="w-full rounded-xl border border-gray-200 pl-10 pr-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent"
          />
        </div>
        <div class="relative">
          <UserPlus class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            v-model="registerForm.nickname"
            type="text"
            placeholder="昵称"
            required
            class="w-full rounded-xl border border-gray-200 pl-10 pr-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent"
          />
        </div>
        <div class="relative">
          <Mail class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            v-model="registerForm.email"
            type="email"
            placeholder="邮箱（选填）"
            class="w-full rounded-xl border border-gray-200 pl-10 pr-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent"
          />
        </div>
        <div class="relative">
          <Lock class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            v-model="registerForm.password"
            type="password"
            placeholder="密码"
            required
            minlength="6"
            class="w-full rounded-xl border border-gray-200 pl-10 pr-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent"
          />
        </div>
        <div class="relative">
          <Lock class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="确认密码"
            required
            class="w-full rounded-xl border border-gray-200 pl-10 pr-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent"
          />
        </div>
        <button
          type="submit"
          class="btn-primary w-full flex items-center justify-center gap-2"
          :disabled="authLoading"
        >
          <Loader2 v-if="authLoading" :size="16" class="animate-spin" />
          {{ authLoading ? '注册中...' : '注册' }}
        </button>
        <p class="text-center text-sm text-gray-500 mt-2">
          已有账号？
          <a href="#" class="text-mangrove-600 hover:text-mangrove-700" @click.prevent="switchTab('login')">去登录</a>
        </p>
      </form>

      <!-- Footer -->
      <p class="text-xs text-gray-400 text-center mt-8">
        登录即表示同意
        <a href="#" class="text-mangrove-600 hover:text-mangrove-700">用户协议</a>
        和
        <a href="#" class="text-mangrove-600 hover:text-mangrove-700">隐私政策</a>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { User, Lock, Mail, UserPlus, AlertCircle, Loader2 } from 'lucide-vue-next'
import { useAuth } from '@/composables/useAuth'

const router = useRouter()
const route = useRoute()
const { login, register, loading: authLoading } = useAuth()

const activeTab = ref('login')
const authError = ref(null)

const loginForm = reactive({ username: '', password: '' })
const registerForm = reactive({
  username: '',
  nickname: '',
  email: '',
  password: '',
  confirmPassword: ''
})

function switchTab(tab) {
  activeTab.value = tab
  authError.value = null
}

async function handleLogin() {
  authError.value = null
  const result = await login(loginForm.username, loginForm.password)
  if (result.success) {
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } else {
    authError.value = result.error
  }
}

async function handleRegister() {
  authError.value = null
  if (registerForm.password !== registerForm.confirmPassword) {
    authError.value = '两次输入的密码不一致'
    return
  }
  if (registerForm.password.length < 6) {
    authError.value = '密码至少需要6个字符'
    return
  }
  const result = await register(
    registerForm.username,
    registerForm.password,
    registerForm.nickname,
    registerForm.email || null
  )
  if (result.success) {
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } else {
    authError.value = result.error
  }
}
</script>
