<template>
  <div>
    <h2 class="text-lg font-semibold text-gray-900 mb-6">内容管理</h2>

    <!-- 上传表单 -->
    <div class="card p-6 mb-6">
      <h3 class="text-sm font-semibold text-gray-800 mb-4">上传新内容</h3>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">标题</label>
          <input v-model="form.title" class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" placeholder="输入标题（批量上传时作为前缀）" />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">分类</label>
          <select v-model="form.category" class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 bg-white">
            <option value="PHOTO">照片馆藏</option>
            <option value="VIDEO">影像存档</option>
          </select>
        </div>
        <div v-if="form.category === 'PHOTO'">
          <label class="block text-sm font-medium text-gray-700 mb-1">照片子分类</label>
          <select v-model="form.photoCategoryId" class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 bg-white">
            <option :value="null">未分类</option>
            <option v-for="cat in photoCategories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
          </select>
        </div>
        <div class="md:col-span-2">
          <label class="block text-sm font-medium text-gray-700 mb-1">文件（可多选）</label>
          <div class="flex items-center gap-3">
            <button class="btn-outline text-sm flex items-center gap-2" @click="$refs.fileInput.click()">
              <Upload class="w-4 h-4" />
              选择文件
            </button>
            <span v-if="form.files.length > 0" class="text-sm text-gray-600">已选 {{ form.files.length }} 个文件</span>
            <input ref="fileInput" type="file" accept="image/*,video/*" multiple class="hidden" @change="onFilesSelect" />
          </div>
          <div v-if="form.files.length > 0" class="mt-2 space-y-1">
            <p v-for="(f, i) in form.files" :key="i" class="text-xs text-gray-500">{{ f.name }}</p>
          </div>
          <p v-if="uploadProgress" class="text-xs text-mangrove-600 mt-1">{{ uploadProgress }}</p>
        </div>
        <div class="md:col-span-2">
          <label class="block text-sm font-medium text-gray-700 mb-1">描述（选填）</label>
          <textarea v-model="form.description" rows="2" class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 resize-none" placeholder="描述内容..."></textarea>
        </div>
      </div>
      <div class="mt-4 flex justify-end">
        <button class="btn-primary text-sm flex items-center gap-2" :disabled="!canSubmit || submitting" @click="submitUpload">
          <span v-if="submitting" class="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent"></span>
          <Upload v-else class="w-4 h-4" />
          {{ submitting ? `上传中 (${uploadedCount}/${form.files.length})...` : `提交上传 (${form.files.length} 个文件)` }}
        </button>
      </div>
    </div>

    <!-- 内容列表 -->
    <div class="card overflow-hidden">
      <div class="px-5 py-4 border-b border-gray-100 flex items-center justify-between">
        <h3 class="text-sm font-semibold text-gray-800">已有内容（{{ total }} 条）</h3>
        <select v-model="listCategory" class="rounded-lg border border-gray-200 px-3 py-1.5 text-xs focus:outline-none" @change="fetchList">
          <option value="">全部</option>
          <option value="PHOTO">照片</option>
          <option value="VIDEO">视频</option>
        </select>
      </div>

      <div v-if="loading" class="flex justify-center py-12">
        <div class="animate-spin rounded-full h-6 w-6 border-2 border-mangrove-700 border-t-transparent"></div>
      </div>

      <div v-else-if="items.length === 0" class="py-12 text-center text-sm text-gray-400">
        暂无内容，请先上传
      </div>

      <div v-else class="overflow-x-auto">
        <!-- 简单列表替代 table -->
        <div class="space-y-3 p-4">
          <div v-for="item in items" :key="item.id" class="flex items-center gap-4 p-3 bg-gray-50 rounded-lg border border-gray-100">
            <div class="w-14 h-14 rounded-lg bg-gray-200 overflow-hidden flex-shrink-0">
              <img :src="item.thumbnailUrl || item.fileUrl" class="w-full h-full object-cover" @error="$event.target.style.display='none'" />
            </div>
            <div class="flex-1 min-w-0">
              <p class="font-medium text-gray-900 text-sm truncate">{{ item.title }}</p>
              <div class="flex items-center gap-2 mt-1">
                <span class="tag text-xs">{{ getCategoryLabel(item.category) }}</span>
                <span :class="statusClass(item.status)" class="text-xs">{{ statusLabel(item.status) }}</span>
                <span class="text-xs text-gray-400">{{ formatDate(item.createdAt) }}</span>
              </div>
            </div>
            <div class="flex items-center gap-2 flex-shrink-0">
              <button class="text-sky-600 hover:text-sky-800 text-xs" @click="openEdit(item)">编辑</button>
              <button class="text-red-500 hover:text-red-700 text-xs" @click="confirmDelete(item)">删除</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <div v-if="showEditModal" class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4" @click.self="showEditModal = false">
      <div class="bg-white rounded-2xl p-6 w-full max-w-md">
        <h3 class="text-base font-semibold text-gray-900 mb-4">编辑内容</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">标题</label>
            <input v-model="editForm.title" class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">描述</label>
            <textarea v-model="editForm.description" rows="3" class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 resize-none"></textarea>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-5">
          <button class="btn-outline text-sm" @click="showEditModal = false">取消</button>
          <button class="btn-primary text-sm" @click="saveEdit">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Upload, ImageIcon } from 'lucide-vue-next'

const form = ref({ title: '', category: 'PHOTO', description: '', files: [], photoCategoryId: null })
const uploadProgress = ref('')
const uploadedCount = ref(0)
const submitting = ref(false)
const items = ref([])
const loading = ref(true)
const total = ref(0)
const listCategory = ref('')
const photoCategories = ref([])

// 编辑相关
const showEditModal = ref(false)
const editForm = ref({ title: '', description: '' })
const editingId = ref(null)

function openEdit(item) {
  editingId.value = item.id
  editForm.value.title = item.title || ''
  editForm.value.description = item.description || ''
  showEditModal.value = true
}

async function saveEdit() {
  if (!editForm.value.title.trim()) return
  try {
    const res = await fetch(`/api/content/${editingId.value}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({
        title: editForm.value.title,
        description: editForm.value.description,
        fileUrl: '',
        category: 'PHOTO'
      })
    })
    const json = await res.json()
    if (json.code === 200) {
      showEditModal.value = false
      fetchList()
    }
  } catch {
    alert('保存失败')
  }
}

function onImgError(e) {
  e.target.style.display = 'none'
}

const canSubmit = computed(() => form.value.title && form.value.files.length > 0)

function onFilesSelect(e) {
  form.value.files = Array.from(e.target.files || [])
}

function getToken() {
  return localStorage.getItem('mangrove_token')
}

async function submitUpload() {
  if (!canSubmit.value) return
  submitting.value = true
  uploadedCount.value = 0

  try {
    for (let i = 0; i < form.value.files.length; i++) {
      const file = form.value.files[i]
      const idx = i + 1

      // 上传文件
      uploadProgress.value = `${idx}/${form.value.files.length} 正在上传文件...`
      const fd = new FormData()
      fd.append('file', file)
      const fileRes = await fetch('/api/files/upload', {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${getToken()}` },
        body: fd
      })
      const fileJson = await fileRes.json()
      if (fileJson.code !== 200 || !fileJson.data?.url) {
        throw new Error(`文件 ${file.name} 上传失败`)
      }

      // 创建内容记录
      const title = form.value.files.length > 1
        ? `${form.value.title} ${idx}`
        : form.value.title

      uploadProgress.value = `${idx}/${form.value.files.length} 正在保存...`
      await fetch('/api/content/upload', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${getToken()}`
        },
        body: JSON.stringify({
          title,
          fileUrl: fileJson.data.url,
          category: form.value.category,
          description: form.value.description,
          photoCategoryId: form.value.photoCategoryId || null
        })
      })

      uploadedCount.value = idx
    }

    uploadProgress.value = ''
      form.value = { title: '', category: 'PHOTO', description: '', files: [], photoCategoryId: null }
    fetchList()
  } catch (e) {
    uploadProgress.value = ''
    alert(e.message || '上传失败')
  } finally {
    submitting.value = false
  }
}

async function fetchList() {
  loading.value = true
  try {
    const cat = listCategory.value ? `?category=${listCategory.value}` : ''
    const res = await fetch(`/api/content${cat}`)
    const json = await res.json()
    if (json.code === 200) {
      items.value = json.data || []
      total.value = items.value.length
    }
  } catch (e) {
    items.value = []
  } finally {
    loading.value = false
  }
}

function confirmDelete(item) {
  if (!confirm(`确定删除「${item.title}」吗？`)) return
  fetch(`/api/content/${item.id}`, {
    method: 'DELETE',
    headers: { 'Authorization': `Bearer ${getToken()}` }
  }).then(r => r.json()).then(json => {
    if (json.code === 200) fetchList()
  })
}

function getCategoryLabel(cat) {
  const map = { PHOTO: '照片', VIDEO: '视频', WORK: '作品' }
  return map[cat] || cat
}

function statusLabel(s) {
  const map = { 0: '待审核', 1: '已通过', 2: '已驳回' }
  return map[s] || '未知'
}

function statusClass(s) {
  const map = { 0: 'text-yellow-600 bg-yellow-50 px-2 py-0.5 rounded text-xs font-medium', 1: 'text-green-600 bg-green-50 px-2 py-0.5 rounded text-xs font-medium', 2: 'text-red-600 bg-red-50 px-2 py-0.5 rounded text-xs font-medium' }
  return map[s] || ''
}

function formatDate(d) {
  if (!d) return '-'
  return d.substring(0, 10)
}

onMounted(async () => {
  fetchList()
  try {
    const res = await fetch('/api/photo-categories')
    const json = await res.json()
    if (json.code === 200) photoCategories.value = json.data || []
  } catch { photoCategories.value = [] }
})
</script>
