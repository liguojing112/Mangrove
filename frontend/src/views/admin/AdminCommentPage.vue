<template>
  <div class="min-w-0">
    <!-- Header -->
    <div class="flex flex-wrap items-center justify-between gap-2 mb-6">
      <h2 class="text-lg font-semibold text-gray-900">评论管理</h2>
      <div class="flex items-center gap-2">
        <span class="text-sm text-gray-500">共 {{ total }} 条</span>
      </div>
    </div>

    <!-- Search -->
    <div class="relative mb-6">
      <Search class="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
      <input
        v-model="searchQuery"
        type="text"
        placeholder="搜索评论内容..."
        class="w-full pl-11 pr-4 py-2.5 rounded-full border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent"
        @input="onSearch"
      />
    </div>

    <!-- Table -->
    <div class="card overflow-hidden max-w-full">
      <div class="overflow-x-auto">
        <table class="w-full text-sm min-w-[640px]">
          <thead>
            <tr class="bg-gray-50">
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">#</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">用户</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">内容</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">类型</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">图片</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">点赞</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">状态</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">时间</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(comment, idx) in comments"
              :key="comment.id"
              class="border-b border-gray-100 hover:bg-gray-50"
            >
              <td class="px-4 py-3 text-gray-400">{{ (currentPage - 1) * pageSize + idx + 1 }}</td>
              <td class="px-4 py-3">
                <div class="flex items-center gap-2">
                  <div class="w-8 h-8 rounded-full bg-mangrove-200 flex items-center justify-center">
                    <User class="w-4 h-4 text-mangrove-700" />
                  </div>
                  <span class="text-gray-800">{{ comment.user?.nickname || '匿名' }}</span>
                </div>
              </td>
              <td class="px-4 py-3 text-gray-800 max-w-xs truncate">{{ comment.content }}</td>
              <td class="px-4 py-3">
                <span class="tag">{{ typeLabel(comment.targetType) }}</span>
              </td>
              <td class="px-4 py-3">
                <img v-if="comment.imageUrl" :src="comment.imageUrl" class="w-10 h-10 object-cover rounded" />
                <span v-else class="text-gray-400">-</span>
              </td>
              <td class="px-4 py-3 text-gray-600">{{ comment.likeCount || 0 }}</td>
              <td class="px-4 py-3">
                <span
                  class="px-2 py-1 rounded-full text-xs"
                  :class="comment.status === 1 ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'"
                >
                  {{ comment.status === 1 ? '正常' : '隐藏' }}
                </span>
              </td>
              <td class="px-4 py-3 text-gray-500 text-xs">{{ formatTime(comment.createdAt) }}</td>
              <td class="px-4 py-3">
                <div class="flex items-center gap-2">
                  <button
                    class="p-1.5 rounded-lg hover:bg-yellow-50 text-gray-400 hover:text-yellow-600 transition-colors"
                    :title="comment.status === 1 ? '隐藏' : '显示'"
                    @click="toggleStatus(comment)"
                  >
                    <Eye v-if="comment.status === 1" class="w-4 h-4" />
                    <EyeOff v-else class="w-4 h-4" />
                  </button>
                  <button
                    class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-colors"
                    title="删除"
                    @click="deleteComment(comment)"
                  >
                    <Trash2 class="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="comments.length === 0">
              <td colspan="9" class="px-4 py-12 text-center text-sm text-gray-400">
                暂无评论数据
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search, User, Eye, EyeOff, Trash2 } from 'lucide-vue-next'

const comments = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(15)
const searchQuery = ref('')

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

async function fetchComments() {
  try {
    const token = getToken()
    console.log('Token:', token ? token.substring(0, 50) + '...' : 'NO TOKEN')
    const params = new URLSearchParams({
      page: currentPage.value - 1,
      size: pageSize.value
    })
    const url = `/api/admin/comments?${params}`
    console.log('Fetching:', url)
    const res = await fetch(url, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    console.log('Response status:', res.status)
    const json = await res.json()
    console.log('Response data:', json)
    if (json.code === 200 && json.data) {
      comments.value = json.data.content || []
      total.value = json.data.totalElements || 0
      console.log('Comments loaded:', comments.value.length)
    }
  } catch (e) {
    console.error('获取评论失败:', e)
  }
}

function onSearch() {
  currentPage.value = 1
  fetchComments()
}

function goPage(p) {
  if (p < 1 || p > totalPages.value) return
  currentPage.value = p
  fetchComments()
}

async function toggleStatus(comment) {
  const newStatus = comment.status === 1 ? 0 : 1
  try {
    const res = await fetch(`/api/admin/comments/${comment.id}/status?status=${newStatus}`, {
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      comment.status = newStatus
    }
  } catch (e) {
    console.error('更新状态失败:', e)
  }
}

async function deleteComment(comment) {
  if (!confirm('确认删除此评论？')) return
  try {
    const res = await fetch(`/api/admin/comments/${comment.id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      await fetchComments()
    }
  } catch (e) {
    console.error('删除失败:', e)
  }
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

function typeLabel(type) {
  const map = {
    MUSIC: '音乐',
    ALBUM: '专辑',
    FAN_WORK: '作品',
    MERCHANDISE: '周边',
    COMMUNITY: '社区',
  }
  return map[type] || type
}

onMounted(() => {
  fetchComments()
})
</script>
