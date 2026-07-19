<template>
  <div class="min-w-0">
    <!-- Header -->
    <div class="flex flex-wrap items-center justify-between gap-2 mb-6">
      <h2 class="text-lg font-semibold text-gray-900">行程管理</h2>
      <div class="flex items-center gap-3">
        <span class="text-sm text-gray-500">共 {{ total }} 条</span>
        <button class="btn-primary text-sm" @click="showCreateModal = true">
          + 新建行程
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="card overflow-hidden max-w-full">
      <div class="overflow-x-auto">
        <table class="w-full text-sm min-w-[580px]">
          <thead>
            <tr class="bg-gray-50">
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">#</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">标题</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">时间</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">地点</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">类型</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">状态</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(schedule, idx) in schedules"
              :key="schedule.id"
              class="border-b border-gray-100 hover:bg-gray-50"
            >
              <td class="px-4 py-3 text-gray-400">{{ (currentPage - 1) * pageSize + idx + 1 }}</td>
              <td class="px-4 py-3 font-medium text-gray-900">{{ schedule.title }}</td>
              <td class="px-4 py-3 text-gray-600">{{ formatDateTime(schedule.startTime) }}</td>
              <td class="px-4 py-3 text-gray-600">{{ schedule.location }}</td>
              <td class="px-4 py-3">
                <span :class="typeClass(schedule.scheduleType)">{{ typeLabel(schedule.scheduleType) }}</span>
              </td>
              <td class="px-4 py-3">
                <span
                  class="px-2 py-1 rounded-full text-xs"
                  :class="schedule.status === 1 ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'"
                >
                  {{ schedule.status === 1 ? '显示' : '隐藏' }}
                </span>
              </td>
              <td class="px-4 py-3">
                <div class="flex items-center gap-2">
                  <button
                    class="p-1.5 rounded-lg hover:bg-emerald-50 text-gray-400 hover:text-emerald-600 transition-colors"
                    title="管理图片和视频"
                    @click="openMediaManager(schedule)"
                  >
                    <Images class="w-4 h-4" />
                  </button>
                  <button
                    class="p-1.5 rounded-lg hover:bg-yellow-50 text-gray-400 hover:text-yellow-600 transition-colors"
                    :title="schedule.status === 1 ? '隐藏' : '显示'"
                    @click="toggleStatus(schedule)"
                  >
                    <Eye v-if="schedule.status === 1" class="w-4 h-4" />
                    <EyeOff v-else class="w-4 h-4" />
                  </button>
                  <button
                    class="p-1.5 rounded-lg hover:bg-blue-50 text-gray-400 hover:text-blue-600 transition-colors"
                    title="编辑"
                    @click="editSchedule(schedule)"
                  >
                    <Edit2 class="w-4 h-4" />
                  </button>
                  <button
                    class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-colors"
                    title="删除"
                    @click="deleteSchedule(schedule)"
                  >
                    <Trash2 class="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="schedules.length === 0">
              <td colspan="7" class="px-4 py-12 text-center text-sm text-gray-400">
                暂无行程数据
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

    <!-- Create/Edit Modal -->
    <div v-if="showCreateModal || editingSchedule" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="closeModal">
      <div class="card p-6 w-full max-w-md mx-4">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">{{ editingSchedule ? '编辑行程' : '新建行程' }}</h3>
        <form @submit.prevent="saveSchedule">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">标题</label>
              <input
                v-model="formData.title"
                type="text"
                class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500"
                required
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">时间</label>
              <input
                v-model="formData.startTime"
                type="datetime-local"
                class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500"
                required
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">地点</label>
              <input
                v-model="formData.location"
                type="text"
                class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">类型</label>
              <select
                v-model="formData.scheduleType"
                class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500"
              >
                <option value="PERFORMANCE">演出</option>
                <option value="FANMEETING">见面会</option>
                <option value="VARIETY">综艺</option>
                <option value="AIRPORT">机场</option>
                <option value="OTHER">其他</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">描述</label>
              <textarea
                v-model="formData.description"
                rows="3"
                class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 resize-none"
              ></textarea>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">短视频链接（小红书 / 抖音等）</label>
              <input
                v-model="formData.videoLink"
                type="url"
                placeholder="https://www.xiaohongshu.com/..."
                class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500"
              />
              <p class="text-xs text-gray-400 mt-1">填写后将在前台行程卡片上显示"观看视频"按钮</p>
            </div>
          </div>
          <div class="flex items-center gap-3 mt-6">
            <button type="submit" class="btn-primary text-sm" :disabled="saving">
              {{ saving ? '保存中...' : '保存' }}
            </button>
            <button type="button" class="btn-secondary text-sm" @click="closeModal">取消</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Schedule Media Manager -->
    <div v-if="mediaSchedule" class="fixed inset-0 z-[70] flex items-center justify-center bg-black/55 p-4" @click.self="closeMediaManager">
      <div class="flex max-h-[92vh] w-full max-w-4xl flex-col overflow-hidden bg-white shadow-xl">
        <div class="flex items-start justify-between border-b border-gray-200 px-5 py-4">
          <div>
            <h3 class="text-lg font-semibold text-gray-900">行程素材</h3>
            <p class="mt-1 text-sm text-gray-500">{{ mediaSchedule.title }}</p>
          </div>
          <button type="button" class="p-2 text-gray-400 hover:text-gray-700" title="关闭" @click="closeMediaManager"><X class="h-5 w-5" /></button>
        </div>

        <div class="flex flex-wrap items-center justify-between gap-3 border-b border-gray-100 bg-gray-50 px-5 py-3">
          <p class="text-xs text-gray-500">支持图片和视频，可一次选择多个文件。</p>
          <label class="btn-primary inline-flex cursor-pointer items-center gap-2 text-sm">
            <Upload class="h-4 w-4" />{{ uploadingMedia ? '上传中...' : '上传素材' }}
            <input type="file" accept="image/*,video/*" multiple class="hidden" :disabled="uploadingMedia" @change="uploadScheduleMedia" />
          </label>
        </div>

        <div class="overflow-y-auto p-5">
          <p v-if="mediaError" class="mb-4 border-l-4 border-red-400 bg-red-50 px-3 py-2 text-sm text-red-700">{{ mediaError }}</p>
          <div v-if="mediaLoading" class="py-16 text-center text-sm text-gray-400"><Loader2 class="mx-auto mb-2 h-6 w-6 animate-spin" />加载中...</div>
          <div v-else-if="scheduleMedia.length === 0" class="py-16 text-center text-sm text-gray-400">
            <Images class="mx-auto mb-3 h-10 w-10 text-gray-300" />暂无素材
          </div>
          <div v-else class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            <div v-for="(media, index) in scheduleMedia" :key="media.id" class="overflow-hidden border border-gray-200 bg-white">
              <div class="aspect-video bg-gray-900">
                <img v-if="media.mediaType === 'IMAGE'" :src="media.mediaUrl" :alt="media.title || '行程图片'" class="h-full w-full object-cover" />
                <video v-else :src="media.mediaUrl" muted preload="metadata" class="h-full w-full object-cover"></video>
              </div>
              <div class="flex items-center gap-2 px-3 py-2">
                <span class="min-w-0 flex-1 truncate text-xs text-gray-600">{{ media.title || (media.mediaType === 'IMAGE' ? '图片' : '视频') }}</span>
                <button type="button" class="p-1 text-gray-400 hover:text-gray-700 disabled:opacity-25" title="前移" :disabled="index === 0" @click="moveMedia(index, -1)"><ArrowLeft class="h-4 w-4" /></button>
                <button type="button" class="p-1 text-gray-400 hover:text-gray-700 disabled:opacity-25" title="后移" :disabled="index === scheduleMedia.length - 1" @click="moveMedia(index, 1)"><ArrowRight class="h-4 w-4" /></button>
                <button type="button" class="p-1 text-gray-400 hover:text-red-500" title="移除素材" @click="removeScheduleMedia(media)"><Trash2 class="h-4 w-4" /></button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ArrowLeft, ArrowRight, Edit2, Eye, EyeOff, Images, Loader2, Trash2, Upload, X } from 'lucide-vue-next'

const schedules = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(15)
const showCreateModal = ref(false)
const editingSchedule = ref(null)
const saving = ref(false)
const mediaSchedule = ref(null)
const scheduleMedia = ref([])
const mediaLoading = ref(false)
const uploadingMedia = ref(false)
const mediaError = ref('')

const formData = ref({
  title: '',
  startTime: '',
  location: '',
  scheduleType: 'OTHER',
  description: '',
  videoLink: ''
})

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

async function adminRequest(url, options = {}) {
  const isForm = options.body instanceof FormData
  const res = await fetch(url, {
    ...options,
    headers: {
      ...(isForm ? {} : { 'Content-Type': 'application/json' }),
      'Authorization': `Bearer ${getToken()}`,
      ...(options.headers || {})
    }
  })
  const json = await res.json()
  if (!res.ok || json.code !== 200) throw new Error(json.msg || '请求失败')
  return json.data
}

async function fetchSchedules() {
  try {
    const params = new URLSearchParams({
      page: currentPage.value - 1,
      size: pageSize.value
    })
    const res = await fetch(`/api/admin/schedules2?${params}`, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      schedules.value = json.data.content || []
      total.value = json.data.totalElements || 0
    }
  } catch (e) {
    console.error('获取行程失败:', e)
  }
}

function goPage(p) {
  if (p < 1 || p > totalPages.value) return
  currentPage.value = p
  fetchSchedules()
}

function editSchedule(schedule) {
  editingSchedule.value = schedule
  formData.value = {
    title: schedule.title,
    startTime: schedule.startTime ? schedule.startTime.slice(0, 16) : '',
    location: schedule.location || '',
    scheduleType: schedule.scheduleType || 'OTHER',
    description: schedule.description || '',
    videoLink: schedule.videoLink || ''
  }
}

function closeModal() {
  showCreateModal.value = false
  editingSchedule.value = null
  formData.value = { title: '', startTime: '', location: '', scheduleType: 'OTHER', description: '', videoLink: '' }
}

async function saveSchedule() {
  saving.value = true
  try {
    const url = editingSchedule.value
      ? `/api/admin/schedules2/${editingSchedule.value.id}`
      : '/api/admin/schedules2'
    const method = editingSchedule.value ? 'PUT' : 'POST'

    const res = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({
        ...formData.value,
        startTime: formData.value.startTime  // 直接使用本地时间，不做转换
      })
    })
    const json = await res.json()
    if (json.code === 200) {
      closeModal()
      await fetchSchedules()
    }
  } catch (e) {
    console.error('保存失败:', e)
  } finally {
    saving.value = false
  }
}

async function toggleStatus(schedule) {
  const newStatus = schedule.status === 1 ? 0 : 1
  try {
    const res = await fetch(`/api/admin/schedules2/${schedule.id}/status?status=${newStatus}`, {
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      schedule.status = newStatus
    }
  } catch (e) {
    console.error('更新状态失败:', e)
  }
}

async function deleteSchedule(schedule) {
  if (!confirm('确认删除此行程？')) return
  try {
    const res = await fetch(`/api/admin/schedules2/${schedule.id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      await fetchSchedules()
    }
  } catch (e) {
    console.error('删除失败:', e)
  }
}

async function openMediaManager(schedule) {
  mediaSchedule.value = schedule
  mediaError.value = ''
  await loadScheduleMedia()
}

function closeMediaManager() {
  if (uploadingMedia.value) return
  mediaSchedule.value = null
  scheduleMedia.value = []
  mediaError.value = ''
}

async function loadScheduleMedia() {
  if (!mediaSchedule.value) return
  mediaLoading.value = true
  mediaError.value = ''
  try {
    scheduleMedia.value = await adminRequest(`/api/admin/schedules2/${mediaSchedule.value.id}/media`) || []
  } catch (e) {
    mediaError.value = e.message
  } finally {
    mediaLoading.value = false
  }
}

async function uploadScheduleMedia(event) {
  const files = [...(event.target.files || [])]
  event.target.value = ''
  if (!files.length || !mediaSchedule.value) return
  uploadingMedia.value = true
  mediaError.value = ''
  try {
    let sortOrder = scheduleMedia.value.length
    for (const file of files) {
      const body = new FormData()
      body.append('file', file)
      const uploaded = await adminRequest('/api/files/upload', { method: 'POST', body })
      await adminRequest(`/api/admin/schedules2/${mediaSchedule.value.id}/media`, {
        method: 'POST',
        body: JSON.stringify({
          mediaType: file.type.startsWith('video/') ? 'VIDEO' : 'IMAGE',
          mediaUrl: uploaded.url,
          title: file.name.replace(/\.[^.]+$/, ''),
          sortOrder: sortOrder++
        })
      })
    }
    await loadScheduleMedia()
  } catch (e) {
    mediaError.value = e.message
  } finally {
    uploadingMedia.value = false
  }
}

async function moveMedia(index, direction) {
  const otherIndex = index + direction
  if (otherIndex < 0 || otherIndex >= scheduleMedia.value.length || !mediaSchedule.value) return
  const current = scheduleMedia.value[index]
  const other = scheduleMedia.value[otherIndex]
  const currentOrder = current.sortOrder
  current.sortOrder = other.sortOrder
  other.sortOrder = currentOrder
  scheduleMedia.value.splice(index, 1)
  scheduleMedia.value.splice(otherIndex, 0, current)
  try {
    await Promise.all([
      adminRequest(`/api/admin/schedules2/${mediaSchedule.value.id}/media/${current.id}`, { method: 'PUT', body: JSON.stringify({ sortOrder: current.sortOrder }) }),
      adminRequest(`/api/admin/schedules2/${mediaSchedule.value.id}/media/${other.id}`, { method: 'PUT', body: JSON.stringify({ sortOrder: other.sortOrder }) })
    ])
  } catch (e) {
    mediaError.value = e.message
    await loadScheduleMedia()
  }
}

async function removeScheduleMedia(media) {
  if (!confirm('确定从该行程移除这项素材吗？资源文件本身会保留。') || !mediaSchedule.value) return
  try {
    await adminRequest(`/api/admin/schedules2/${mediaSchedule.value.id}/media/${media.id}`, { method: 'DELETE' })
    scheduleMedia.value = scheduleMedia.value.filter(item => item.id !== media.id)
  } catch (e) {
    mediaError.value = e.message
  }
}

function formatDateTime(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

function typeClass(type) {
  const base = 'rounded-full px-2.5 py-0.5 text-xs font-medium shrink-0'
  switch (type) {
    case 'PERFORMANCE': return `${base} bg-red-100 text-red-700`
    case 'FANMEETING': return `${base} bg-blue-100 text-blue-700`
    case 'VARIETY': return `${base} bg-purple-100 text-purple-700`
    case 'AIRPORT': return `${base} bg-teal-100 text-teal-700`
    default: return `${base} bg-gray-100 text-gray-600`
  }
}

function typeLabel(type) {
  const map = {
    PERFORMANCE: '演出',
    FANMEETING: '见面会',
    VARIETY: '综艺',
    AIRPORT: '机场',
    OTHER: '其他',
  }
  return map[type] || '其他'
}

onMounted(() => {
  fetchSchedules()
})
</script>
