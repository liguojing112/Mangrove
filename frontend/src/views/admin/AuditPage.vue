<template>
  <div>
    <div class="flex flex-wrap items-center justify-between gap-2 mb-6">
      <div>
        <h2 class="text-xl font-bold text-gray-900">内容审核面板</h2>
        <p class="text-sm text-gray-500 mt-1">审核普通管理员上传的内容</p>
      </div>
    </div>

    <!-- 内容分类 Tab -->
    <div class="flex gap-2 mb-4 flex-wrap">
      <button v-for="tab in contentTabs" :key="tab.value"
        class="px-4 py-2 rounded-lg text-sm font-medium transition-colors"
        :class="activeContent === tab.value ? 'bg-mangrove-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
        @click="activeContent = tab.value; activeStatus = 'PENDING'">
        {{ tab.label }}
        <span v-if="tab.count > 0" class="ml-1.5 px-1.5 py-0.5 rounded-full text-xs" :class="activeContent === tab.value ? 'bg-white/20' : 'bg-red-100 text-red-600'">{{ tab.count }}</span>
      </button>
    </div>

    <!-- 状态 Tab -->
    <div class="flex gap-2 mb-6">
      <button v-for="st in statusTabs" :key="st.value"
        class="px-3 py-1.5 rounded-full text-xs font-medium transition-colors"
        :class="activeStatus === st.value ? st.activeClass : 'bg-gray-50 text-gray-500 hover:bg-gray-100'"
        @click="activeStatus = st.value">
        {{ st.label }}
      </button>
    </div>

    <div v-if="loading" class="py-16 text-center text-sm text-gray-400">
      <div class="animate-spin rounded-full h-6 w-6 border-2 border-mangrove-500 border-t-transparent mx-auto mb-3"></div>
      加载中...
    </div>

    <!-- 文件审核 -->
    <template v-if="activeContent === 'files'">
      <div v-if="filteredFiles.length === 0" class="py-16 text-center text-sm text-gray-400">{{ emptyText }}</div>
      <div v-else class="space-y-3">
        <div v-for="item in filteredFiles" :key="item.filename" class="flex items-center gap-3 p-3 bg-white rounded-xl border border-gray-200">
          <span class="w-10 h-10 rounded-lg bg-gray-100 flex items-center justify-center text-lg flex-shrink-0">
            {{ isImage(item) ? '📷' : isVideo(item) ? '🎬' : '🎵' }}
          </span>
          <div class="flex-1 min-w-0">
            <p class="text-sm font-medium text-gray-800 truncate">{{ item.displayName || item.filename }}</p>
            <p class="text-xs text-gray-400 mt-0.5">{{ item.category || '未分类' }}</p>
          </div>
          <span class="text-xs px-2 py-1 rounded-full" :class="statusBadgeClass(item.status)">{{ statusLabel(item.status) }}</span>
          <div class="flex gap-1">
            <button v-if="item.status !== 1" @click="approveFile(item)" class="px-3 py-1.5 text-xs font-medium text-white bg-emerald-500 rounded-lg hover:bg-emerald-600">通过</button>
            <button v-if="item.status !== 2" @click="rejectFile(item)" class="px-3 py-1.5 text-xs font-medium text-white bg-red-400 rounded-lg hover:bg-red-500">驳回</button>
          </div>
        </div>
      </div>
    </template>

    <!-- 音乐审核 -->
    <template v-if="activeContent === 'music'">
      <div v-if="filteredMusic.length === 0" class="py-16 text-center text-sm text-gray-400">{{ emptyText }}</div>
      <div v-else class="space-y-3">
        <div v-for="item in filteredMusic" :key="item.id" class="flex items-center gap-3 p-3 bg-white rounded-xl border border-gray-200">
          <img v-if="item.coverUrl" :src="item.coverUrl" class="w-12 h-12 rounded-lg object-cover flex-shrink-0" />
          <span v-else class="w-12 h-12 rounded-lg bg-purple-50 flex items-center justify-center text-lg flex-shrink-0">🎵</span>
          <div class="flex-1 min-w-0">
            <p class="text-sm font-medium text-gray-800 truncate">{{ item.title }}</p>
            <p class="text-xs text-gray-400 mt-0.5">{{ item.category || '单曲' }}</p>
          </div>
          <span class="text-xs px-2 py-1 rounded-full" :class="item.status === 1 ? 'bg-emerald-100 text-emerald-700' : item.status === -1 ? 'bg-red-100 text-red-600' : 'bg-yellow-100 text-yellow-700'">{{ item.status === 1 ? '已上架' : item.status === -1 ? '已驳回' : '待审核' }}</span>
          <div class="flex gap-1">
            <button v-if="item.status !== 1" @click="approveMusic(item)" class="px-3 py-1.5 text-xs font-medium text-white bg-emerald-500 rounded-lg hover:bg-emerald-600">通过</button>
            <button v-if="item.status === 0" @click="rejectMusic(item)" class="px-3 py-1.5 text-xs font-medium text-white bg-red-400 rounded-lg hover:bg-red-500">驳回</button>
          </div>
        </div>
      </div>
    </template>

    <!-- 长视频审核 -->
    <template v-if="activeContent === 'longVideos'">
      <div v-if="filteredLongVideos.length === 0" class="py-16 text-center text-sm text-gray-400">{{ emptyText }}</div>
      <div v-else class="space-y-3">
        <div v-for="item in filteredLongVideos" :key="item.id" class="flex items-center gap-3 p-3 bg-white rounded-xl border border-gray-200">
          <img v-if="item.coverUrl" :src="item.coverUrl" class="w-16 h-10 rounded-lg object-cover flex-shrink-0" />
          <span v-else class="w-16 h-10 rounded-lg bg-blue-50 flex items-center justify-center text-lg flex-shrink-0">🎬</span>
          <div class="flex-1 min-w-0">
            <p class="text-sm font-medium text-gray-800 truncate">{{ item.title }}</p>
            <p class="text-xs text-gray-400 mt-0.5">{{ item.category || '长视频' }}</p>
          </div>
          <span class="text-xs px-2 py-1 rounded-full" :class="item.status === 1 ? 'bg-emerald-100 text-emerald-700' : item.status === -1 ? 'bg-red-100 text-red-600' : 'bg-yellow-100 text-yellow-700'">{{ item.status === 1 ? '已上架' : item.status === -1 ? '已驳回' : '待审核' }}</span>
          <div class="flex gap-1">
            <button v-if="item.status !== 1" @click="approveLongVideo(item)" class="px-3 py-1.5 text-xs font-medium text-white bg-emerald-500 rounded-lg hover:bg-emerald-600">通过</button>
            <button v-if="item.status === 0" @click="rejectLongVideo(item)" class="px-3 py-1.5 text-xs font-medium text-white bg-red-400 rounded-lg hover:bg-red-500">驳回</button>
          </div>
        </div>
      </div>
    </template>

    <!-- 投稿审核 -->
    <template v-if="activeContent === 'works'">
      <div v-if="filteredWorks.length === 0" class="py-16 text-center text-sm text-gray-400">{{ emptyText }}</div>
      <div v-else class="space-y-3">
        <div v-for="item in filteredWorks" :key="item.id" class="flex items-center gap-3 p-3 bg-white rounded-xl border border-gray-200">
          <img v-if="item.fileUrl" :src="item.fileUrl" class="w-12 h-12 rounded-lg object-cover flex-shrink-0" />
          <span v-else class="w-12 h-12 rounded-lg bg-amber-50 flex items-center justify-center text-lg flex-shrink-0">🖼️</span>
          <div class="flex-1 min-w-0">
            <p class="text-sm font-medium text-gray-800 truncate">{{ item.title }}</p>
            <p class="text-xs text-gray-400 mt-0.5">{{ item.creator?.nickname || '匿名' }} · {{ categoryLabel(item.category) }}</p>
          </div>
          <span class="text-xs px-2 py-1 rounded-full" :class="workStatusClass(item.status)">{{ workStatusLabel(item.status) }}</span>
          <div class="flex gap-1">
            <button v-if="item.status === 'PENDING'" @click="approveWork(item)" class="px-3 py-1.5 text-xs font-medium text-white bg-emerald-500 rounded-lg hover:bg-emerald-600">通过</button>
            <button v-if="item.status === 'PENDING'" @click="rejectWork(item)" class="px-3 py-1.5 text-xs font-medium text-white bg-red-400 rounded-lg hover:bg-red-500">驳回</button>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const loading = ref(false)
const activeContent = ref('files')
const activeStatus = ref('PENDING')

const allFiles = ref([])
const allMusic = ref([])
const allLongVideos = ref([])
const allWorks = ref([])

function getToken() { return localStorage.getItem('mangrove_token') }

const statusTabs = [
  { label: '待审核', value: 'PENDING', activeClass: 'bg-yellow-500 text-white' },
  { label: '已通过', value: 'APPROVED', activeClass: 'bg-emerald-500 text-white' },
  { label: '已驳回', value: 'REJECTED', activeClass: 'bg-red-400 text-white' },
]

const filteredFiles = computed(() => {
  if (activeStatus.value === 'PENDING') return allFiles.value.filter(f => f.status === 0)
  if (activeStatus.value === 'APPROVED') return allFiles.value.filter(f => f.status === 1)
  return allFiles.value.filter(f => f.status === 2)
})
const filteredMusic = computed(() => {
  if (activeStatus.value === 'PENDING') return allMusic.value.filter(t => t.status === 0)
  if (activeStatus.value === 'APPROVED') return allMusic.value.filter(t => t.status === 1)
  return allMusic.value.filter(t => t.status === -1)
})
const filteredLongVideos = computed(() => {
  if (activeStatus.value === 'PENDING') return allLongVideos.value.filter(v => v.status === 0)
  if (activeStatus.value === 'APPROVED') return allLongVideos.value.filter(v => v.status === 1)
  return allLongVideos.value.filter(v => v.status === -1)
})
const filteredWorks = computed(() => {
  if (activeStatus.value === 'PENDING') return allWorks.value.filter(w => w.status === 'PENDING')
  if (activeStatus.value === 'APPROVED') return allWorks.value.filter(w => w.status === 'PUBLISHED' || w.status === 'FEATURED')
  return allWorks.value.filter(w => w.status === 'HIDDEN')
})

const contentTabs = computed(() => [
  { label: '📁 文件', value: 'files', count: allFiles.value.filter(f => f.status === 0).length },
  { label: '🎵 音乐', value: 'music', count: allMusic.value.filter(t => t.status === 0).length },
  { label: '🎬 长视频', value: 'longVideos', count: allLongVideos.value.filter(v => v.status === 0).length },
  { label: '🖼️ 投稿', value: 'works', count: allWorks.value.filter(w => w.status === 'PENDING').length },
])

const emptyText = computed(() => {
  const map = { PENDING: '暂无待审核内容', APPROVED: '暂无已通过内容', REJECTED: '暂无已驳回内容' }
  return map[activeStatus.value] || '暂无数据'
})

function isImage(item) { return /\.(jpg|jpeg|png|gif|webp)$/i.test(item.filename || '') }
function isVideo(item) { return /\.(mp4|webm|mov|avi)$/i.test(item.filename || '') }
function categoryLabel(cat) { return { PREVIEW: '路透图', EDITED_PHOTO: '剪辑修图', VIDEO_EDIT: '视频剪辑' }[cat] || cat }
function statusLabel(s) { return { 0: '待审核', 1: '已通过', 2: '已驳回' }[s] || '未知' }
function statusBadgeClass(s) { return { 0: 'bg-yellow-100 text-yellow-700', 1: 'bg-emerald-100 text-emerald-700', 2: 'bg-red-100 text-red-600' }[s] || 'bg-gray-100' }
function workStatusLabel(s) { return { PENDING: '待审核', PUBLISHED: '已通过', HIDDEN: '已驳回', FEATURED: '精选' }[s] || s }
function workStatusClass(s) { return { PENDING: 'bg-yellow-100 text-yellow-700', PUBLISHED: 'bg-emerald-100 text-emerald-700', HIDDEN: 'bg-red-100 text-red-600', FEATURED: 'bg-amber-100 text-amber-700' }[s] || 'bg-gray-100' }

async function loadAll() {
  loading.value = true
  try {
    const headers = { Authorization: `Bearer ${getToken()}` }
    const [filesRes, musicRes, videosRes, worksRes] = await Promise.all([
      fetch('/api/files/meta', { headers }),
      fetch('/api/admin/music/tracks', { headers }),
      fetch('/api/admin/long-videos', { headers }),
      fetch('/api/admin/works?page=0&size=100', { headers }),
    ])
    const [fj, mj, vj, wj] = await Promise.all([filesRes.json(), musicRes.json(), videosRes.json(), worksRes.json()])
    if (fj.code === 200) allFiles.value = fj.data || []
    if (mj.code === 200) allMusic.value = mj.data || []
    if (vj.code === 200) allLongVideos.value = vj.data || []
    if (wj.code === 200) allWorks.value = wj.data?.content || []
  } catch (e) {
    console.error('加载审核数据失败:', e)
  } finally {
    loading.value = false
  }
}

// 文件
async function approveFile(item) {
  if (!confirm(`通过「${item.displayName || item.filename}」？`)) return
  const res = await fetch(`/api/files/meta/${item.filename}/status?status=1`, { method: 'PUT', headers: { Authorization: `Bearer ${getToken()}` } })
  if (res.ok) { item.status = 1 } else alert('操作失败')
}
async function rejectFile(item) {
  if (!confirm(`驳回「${item.displayName || item.filename}」？`)) return
  const res = await fetch(`/api/files/meta/${item.filename}/status?status=2`, { method: 'PUT', headers: { Authorization: `Bearer ${getToken()}` } })
  if (res.ok) { item.status = 2 } else alert('操作失败')
}

// 音乐
async function approveMusic(item) {
  if (!confirm(`通过音乐「${item.title}」？`)) return
  const res = await fetch(`/api/admin/music/tracks/${item.id}`, { method: 'PUT', headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` }, body: JSON.stringify({ ...item, status: 1 }) })
  if (res.ok) { item.status = 1 } else alert('操作失败')
}
async function rejectMusic(item) {
  if (!confirm(`驳回音乐「${item.title}」？`)) return
  const res = await fetch(`/api/admin/music/tracks/${item.id}`, { method: 'PUT', headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` }, body: JSON.stringify({ ...item, status: -1 }) })
  if (res.ok) { item.status = -1 } else alert('操作失败')
}

// 长视频
async function approveLongVideo(item) {
  if (!confirm(`通过长视频「${item.title}」？`)) return
  const res = await fetch(`/api/admin/long-videos/${item.id}`, { method: 'PUT', headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` }, body: JSON.stringify({ ...item, status: 1 }) })
  if (res.ok) { item.status = 1 } else alert('操作失败')
}
async function rejectLongVideo(item) {
  if (!confirm(`驳回长视频「${item.title}」？`)) return
  const res = await fetch(`/api/admin/long-videos/${item.id}`, { method: 'PUT', headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` }, body: JSON.stringify({ ...item, status: -1 }) })
  if (res.ok) { item.status = -1 } else alert('操作失败')
}

// 投稿
async function approveWork(item) {
  if (!confirm(`通过投稿「${item.title}」？`)) return
  const res = await fetch(`/api/admin/works/${item.id}/approve`, { method: 'POST', headers: { Authorization: `Bearer ${getToken()}` } })
  if (res.ok) { item.status = 'PUBLISHED' } else alert('操作失败')
}
async function rejectWork(item) {
  if (!confirm(`驳回投稿「${item.title}」？`)) return
  const res = await fetch(`/api/admin/works/${item.id}/reject`, { method: 'POST', headers: { Authorization: `Bearer ${getToken()}` } })
  if (res.ok) { item.status = 'HIDDEN' } else alert('操作失败')
}

onMounted(loadAll)
</script>
