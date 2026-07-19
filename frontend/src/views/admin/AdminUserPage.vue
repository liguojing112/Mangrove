<template>
  <div class="min-w-0">
    <!-- Header -->
    <div class="flex flex-wrap items-center justify-between gap-2 mb-6">
      <h2 class="text-lg font-semibold text-gray-900">用户管理</h2>
      <div class="flex items-center gap-2">
        <span class="text-sm text-gray-500">共 {{ total }} 条</span>
        <button
          v-if="isSuperAdmin"
          class="btn-primary text-sm inline-flex items-center gap-2"
          @click="openCreateAdmin"
        >
          <UserPlus class="w-4 h-4" />
          添加普通管理员
        </button>
      </div>
    </div>

    <p
      v-if="notice"
      class="mb-4 rounded-lg border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700"
    >
      {{ notice }}
    </p>

    <!-- Search -->
    <div class="relative mb-6">
      <Search class="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
      <input
        v-model="searchQuery"
        type="text"
        placeholder="搜索用户名/昵称..."
        class="w-full pl-11 pr-4 py-2.5 rounded-full border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent"
        @input="onSearch"
      />
    </div>

    <!-- Table -->
    <div class="card overflow-hidden max-w-full">
      <div class="overflow-x-auto">
        <table class="w-full text-sm min-w-[580px]">
          <thead>
            <tr class="bg-gray-50">
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">#</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">用户</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">公开 ID</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">邮箱</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">角色</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">状态</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">注册时间</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(user, idx) in users"
              :key="user.id"
              class="border-b border-gray-100 hover:bg-gray-50"
            >
              <td class="px-4 py-3 text-gray-400">{{ (currentPage - 1) * pageSize + idx + 1 }}</td>
              <td class="px-4 py-3">
                <div class="flex items-center gap-2">
                  <div class="w-8 h-8 rounded-full bg-mangrove-200 flex items-center justify-center">
                    <User class="w-4 h-4 text-mangrove-700" />
                  </div>
                  <div>
                    <div class="font-medium text-gray-800">{{ user.nickname || user.username }}</div>
                    <div class="text-xs text-gray-400">{{ user.username }}</div>
                  </div>
                </div>
              </td>
              <td class="px-4 py-3 font-mono text-xs text-gray-500">{{ user.publicId }}</td>
              <td class="px-4 py-3 text-gray-600">{{ user.email || '-' }}</td>
              <td class="px-4 py-3">
                <span
                  class="px-2 py-1 rounded-full text-xs"
                  :class="roleClass(user.role)"
                >
                  {{ roleLabel(user.role) }}
                </span>
              </td>
              <td class="px-4 py-3">
                <span
                  class="px-2 py-1 rounded-full text-xs"
                  :class="user.status === 1 ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'"
                >
                  {{ user.status === 1 ? '正常' : '禁用' }}
                </span>
              </td>
              <td class="px-4 py-3 text-gray-500 text-xs">{{ formatTime(user.createdAt) }}</td>
              <td class="px-4 py-3">
                <div class="flex items-center gap-2">
                  <button
                    class="p-1.5 rounded-lg hover:bg-yellow-50 text-gray-400 hover:text-yellow-600 transition-colors"
                    :title="user.status === 1 ? '禁用' : '启用'"
                    @click="toggleStatus(user)"
                  >
                    <Ban v-if="user.status === 1" class="w-4 h-4" />
                    <CheckCircle v-else class="w-4 h-4" />
                  </button>
                  <button
                    class="p-1.5 rounded-lg hover:bg-blue-50 text-gray-400 hover:text-blue-600 transition-colors"
                    title="修改角色"
                    @click="showEditRole(user)"
                  >
                    <Shield class="w-4 h-4" />
                  </button>
                  <button
                    class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-colors"
                    title="删除"
                    @click="deleteUser(user)"
                  >
                    <Trash2 class="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="users.length === 0">
              <td colspan="8" class="px-4 py-12 text-center text-sm text-gray-400">
                暂无用户数据
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div class="flex flex-wrap items-center justify-between gap-2 px-4 py-3 border-t border-gray-100">
        <span class="text-xs text-gray-500">
          共 {{ total }} 条，第 {{ currentPage }}/{{ totalPages }} 页
        </span>
        <div class="flex flex-wrap items-center gap-1">
          <button
            class="px-3 py-1.5 rounded-lg text-xs font-medium border border-gray-200 hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
            :disabled="currentPage <= 1"
            @click="goPage(currentPage - 1)"
          >
            上一页
          </button>
          <button
            v-for="p in visiblePages"
            :key="p"
            class="w-8 h-8 rounded-lg text-xs font-medium transition-colors"
            :class="p === currentPage ? 'bg-mangrove-700 text-white' : 'text-gray-600 hover:bg-gray-100'"
            @click="goPage(p)"
          >
            {{ p }}
          </button>
          <button
            class="px-3 py-1.5 rounded-lg text-xs font-medium border border-gray-200 hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
            :disabled="currentPage >= totalPages"
            @click="goPage(currentPage + 1)"
          >
            下一页
          </button>
        </div>
      </div>
    </div>

    <!-- Edit Role Modal -->
    <div v-if="editingUser" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="closeModal">
      <div class="card p-6 w-full max-w-sm mx-4">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">修改用户角色</h3>
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-2">用户</label>
          <p class="text-gray-900">{{ editingUser.nickname || editingUser.username }}</p>
        </div>
        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-2">角色</label>
          <select
            v-model="newRole"
            class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500"
          >
            <option value="FAN">粉丝</option>
            <option value="CREATOR">创作者</option>
            <option v-if="isSuperAdmin" value="ADMIN">普通管理员</option>
          </select>
        </div>
        <div class="flex items-center gap-3">
          <button class="btn-primary text-sm" @click="saveRole">保存</button>
          <button class="btn-secondary text-sm" @click="closeModal">取消</button>
        </div>
      </div>
    </div>

    <!-- Create Admin Modal -->
    <div v-if="showCreateAdmin" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="closeCreateAdmin">
      <div class="card p-6 w-full max-w-md mx-4">
        <div class="flex items-start justify-between gap-4 mb-5">
          <div>
            <h3 class="text-lg font-semibold text-gray-900">添加普通管理员</h3>
            <p class="mt-1 text-sm text-gray-500">该账号可以登录管理后台并管理普通用户。</p>
          </div>
          <button class="p-1 text-gray-400 hover:text-gray-700" title="关闭" @click="closeCreateAdmin">
            <X class="w-5 h-5" />
          </button>
        </div>

        <form class="space-y-4" @submit.prevent="createAdmin">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">用户名</label>
            <input v-model.trim="adminForm.username" required minlength="3" maxlength="50" class="w-full rounded-lg border border-gray-200 px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" placeholder="用于登录后台" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">昵称</label>
            <input v-model.trim="adminForm.nickname" required maxlength="50" class="w-full rounded-lg border border-gray-200 px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" placeholder="管理员显示名称" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">初始密码</label>
            <input v-model="adminForm.password" required minlength="6" type="password" autocomplete="new-password" class="w-full rounded-lg border border-gray-200 px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" placeholder="至少 6 个字符" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">邮箱 <span class="font-normal text-gray-400">（选填）</span></label>
            <input v-model.trim="adminForm.email" type="email" class="w-full rounded-lg border border-gray-200 px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" placeholder="name@example.com" />
          </div>

          <p v-if="formError" class="text-sm text-red-600">{{ formError }}</p>

          <div class="flex items-center justify-end gap-3 pt-2">
            <button type="button" class="btn-secondary text-sm" @click="closeCreateAdmin">取消</button>
            <button type="submit" class="btn-primary text-sm" :disabled="creatingAdmin">
              {{ creatingAdmin ? '创建中...' : '创建管理员' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { User, UserPlus, Search, Ban, CheckCircle, Shield, Trash2, X } from 'lucide-vue-next'

const users = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(15)
const searchQuery = ref('')
const editingUser = ref(null)
const newRole = ref('FAN')
const showCreateAdmin = ref(false)
const creatingAdmin = ref(false)
const formError = ref('')
const notice = ref('')
const adminForm = ref({ username: '', nickname: '', password: '', email: '' })
const currentRole = JSON.parse(localStorage.getItem('mangrove_user') || 'null')?.role || ''
const isSuperAdmin = computed(() => currentRole === 'SUPER_ADMIN')

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(1, currentPage.value - 2)
  const end = Math.min(totalPages.value, start + 4)
  for (let i = start; i <= end; i++) pages.push(i)
  return pages
})

function getToken() {
  return localStorage.getItem('mangrove_token')
}

async function fetchUsers() {
  try {
    const params = new URLSearchParams({
      page: currentPage.value - 1,
      size: pageSize.value
    })
    if (searchQuery.value) {
      params.append('keyword', searchQuery.value)
    }
    const res = await fetch(`/api/admin/users?${params}`, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      users.value = json.data.content || []
      total.value = json.data.totalElements || 0
    }
  } catch (e) {
    console.error('获取用户失败:', e)
  }
}

function onSearch() {
  currentPage.value = 1
  fetchUsers()
}

function goPage(p) {
  if (p < 1 || p > totalPages.value) return
  currentPage.value = p
  fetchUsers()
}

function showEditRole(user) {
  editingUser.value = user
  newRole.value = user.role || 'FAN'
}

function closeModal() {
  editingUser.value = null
  newRole.value = 'FAN'
}

function openCreateAdmin() {
  formError.value = ''
  notice.value = ''
  showCreateAdmin.value = true
}

function closeCreateAdmin() {
  showCreateAdmin.value = false
  formError.value = ''
  adminForm.value = { username: '', nickname: '', password: '', email: '' }
}

async function createAdmin() {
  creatingAdmin.value = true
  formError.value = ''
  try {
    const res = await fetch('/api/admin/users/admins', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify(adminForm.value)
    })
    const json = await res.json()
    if (json.code !== 200) {
      formError.value = json.msg || '创建普通管理员失败'
      return
    }
    const createdName = json.data?.nickname || adminForm.value.nickname
    closeCreateAdmin()
    notice.value = `普通管理员“${createdName}”已创建，可以使用新账号登录管理后台。`
    currentPage.value = 1
    await fetchUsers()
  } catch (e) {
    formError.value = '无法连接后端，请确认服务已启动'
    console.error('创建普通管理员失败:', e)
  } finally {
    creatingAdmin.value = false
  }
}

async function saveRole() {
  try {
    const res = await fetch(`/api/admin/users/${editingUser.value.id}/role`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({ role: newRole.value })
    })
    const json = await res.json()
    if (json.code === 200) {
      editingUser.value.role = newRole.value
      closeModal()
    }
  } catch (e) {
    console.error('修改角色失败:', e)
  }
}

async function toggleStatus(user) {
  const newStatus = user.status === 1 ? 0 : 1
  try {
    const res = await fetch(`/api/admin/users/${user.id}/status?status=${newStatus}`, {
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      user.status = newStatus
    }
  } catch (e) {
    console.error('更新状态失败:', e)
  }
}

async function deleteUser(user) {
  if (!confirm(`确认删除用户 ${user.nickname || user.username}？`)) return
  try {
    const res = await fetch(`/api/admin/users/${user.id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      await fetchUsers()
    }
  } catch (e) {
    console.error('删除失败:', e)
  }
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

function roleClass(role) {
  switch (role) {
    case 'ADMIN':
      return 'bg-purple-100 text-purple-700'
    case 'SUPER_ADMIN':
      return 'bg-red-100 text-red-700'
    case 'CREATOR':
      return 'bg-blue-100 text-blue-700'
    default:
      return 'bg-gray-100 text-gray-600'
  }
}

function roleLabel(role) {
  const map = {
    FAN: '粉丝',
    CREATOR: '创作者',
    ADMIN: '普通管理员',
    SUPER_ADMIN: '超级管理员',
  }
  return map[role] || '粉丝'
}

onMounted(() => {
  fetchUsers()
})
</script>
