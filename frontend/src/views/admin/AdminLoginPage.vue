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

      <!-- Form -->
      <div class="space-y-4">
        <div class="relative">
          <User class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            type="text"
            placeholder="用户名"
            class="w-full rounded-xl border border-gray-200 pl-10 pr-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent"
          />
        </div>
        <div class="relative">
          <Lock class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            type="password"
            placeholder="密码"
            class="w-full rounded-xl border border-gray-200 pl-10 pr-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent"
          />
        </div>
        <button class="btn-primary w-full">登录</button>
      </div>

      <p class="text-xs text-gray-400 text-center mt-6">
        <router-link to="/" class="text-mangrove-600 hover:text-mangrove-700">← 返回前台</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { User, Lock, Settings } from 'lucide-vue-next'
</script>
