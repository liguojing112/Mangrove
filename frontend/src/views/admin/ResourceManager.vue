<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <div>
        <h2 class="text-lg font-semibold text-gray-900">资源管理</h2>
        <p class="text-sm text-gray-500 mt-1">管理全站照片 & 视频</p>
      </div>
    </div>

    <!-- 上传区 -->
    <div class="card p-6 mb-6">
      <h3 class="text-sm font-semibold text-gray-800 mb-4">📤 上传文件</h3>
      <div class="flex flex-col gap-4">
        <!-- 分类 + 拍摄时间 -->
        <div class="flex items-center gap-4 flex-wrap">
          <div class="flex items-center gap-2">
            <span class="text-sm text-gray-500">分类：</span>
            <div class="relative">
              <input v-model="newCategory" @keydown.enter="addCategory" placeholder="选择或新建分类" list="categoryList"
                class="rounded-lg border border-gray-200 px-3 py-2 text-sm w-40 focus:ring-2 focus:ring-mangrove-500 focus:border-transparent" />
              <datalist id="categoryList">
                <option v-for="c in customCategories" :key="c" :value="c" />
              </datalist>
            </div>
            <button @click="addCategory" class="text-xs text-mangrove-600 hover:text-mangrove-700">+ 新建</button>
          </div>
          <div class="flex items-center gap-2">
            <span class="text-sm text-gray-500">拍摄日期：</span>
            <input type="date" v-model="uploadDate"
              class="rounded-lg border border-gray-200 px-3 py-2 text-sm focus:ring-2 focus:ring-mangrove-500 focus:border-transparent" />
          </div>
        </div>
        <!-- 管理已有分类 -->
        <div class="flex items-center gap-2 flex-wrap">
          <span class="text-sm text-gray-500">已有分类：</span>
          <span v-for="c in customCategories" :key="c"
            class="tag text-xs cursor-pointer group relative">
            {{ c }}
            <button @click="removeCategory(c)" class="ml-1 opacity-0 group-hover:opacity-100 text-gray-400 hover:text-red-500">✕</button>
          </span>
          <span v-if="customCategories.length===0" class="text-xs text-gray-400">暂无自定义分类</span>
        </div>
        <!-- 上传区域 -->
        <div class="border-2 border-dashed border-gray-300 rounded-2xl p-8 text-center hover:border-mangrove-400 transition-colors cursor-pointer mt-2"
          @click="$refs.fileInput.click()"
          @dragover.prevent
          @drop.prevent="handleDrop">
          <Upload class="w-10 h-10 text-gray-300 mx-auto mb-3" />
          <p class="text-sm text-gray-500">拖拽文件到此处，或点击选择</p>
          <p class="text-xs text-gray-400 mt-1">支持 JPG / PNG / WebP / MP4，单文件最大 10MB</p>
          <input ref="fileInput" type="file" accept="image/*,video/*" multiple class="hidden" @change="handleUpload" />
        </div>
        <!-- 待上传列表 -->
        <div v-if="pendingFiles.length > 0" class="space-y-2">
          <div v-for="(f, i) in pendingFiles" :key="i" class="flex items-center gap-2 bg-gray-50 rounded-xl p-3 flex-wrap">
            <span class="text-xs text-gray-400 w-6">#{{ i + 1 }}</span>
            <input v-model="f.displayName" placeholder="名称" class="text-sm border rounded px-2 py-1 w-28 focus:ring-1 focus:ring-mangrove-400 focus:border-transparent" />
            <input v-model="f.seq" type="number" placeholder="序号" class="text-sm border rounded px-2 py-1 w-16 focus:ring-1 focus:ring-mangrove-400 focus:border-transparent" />
            <select v-model="f.category" class="text-xs border rounded px-2 py-1 focus:ring-1 focus:ring-mangrove-400 focus:border-transparent">
              <option value="">分类</option>
              <option v-for="c in customCategories" :key="c" :value="c">{{ c }}</option>
            </select>
            <span class="flex-1 text-xs text-gray-500 truncate">{{ f.name }}</span>
            <span class="text-xs text-gray-400">{{ (f.size/1024).toFixed(0) }}KB</span>
            <span v-if="f.status==='done'" class="text-green-500 text-xs">✓</span>
            <span v-else-if="f.status==='error'" class="text-red-500 text-xs">✕</span>
            <span v-else class="text-gray-400 text-xs">等待</span>
          </div>
        </div>
        <button v-if="pendingFiles.length > 0" class="btn-primary text-sm self-start" @click="startUpload" :disabled="uploading">
          <Loader2 v-if="uploading" class="w-4 h-4 inline animate-spin mr-1" />
          {{ uploading ? '上传中...' : '开始上传' }}
        </button>
      </div>
    </div>

    <!-- 筛选 -->
    <div class="flex items-center gap-3 mb-4">
      <button v-for="t in types" :key="t.value" class="px-3 py-1.5 rounded-full text-xs font-medium border transition-all"
        :class="activeType===t.value ? 'bg-mangrove-600 text-white border-mangrove-600' : 'text-gray-500 border-gray-200 hover:border-mangrove-300'"
        @click="activeType=t.value; page=0; loadFiles()">{{ t.label }}</button>
      <div class="flex-1"></div>
      <span class="text-xs text-gray-400">{{ totalCount }} 个文件</span>
    </div>

    <!-- 文件网格 -->
    <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
      <div v-for="file in files" :key="file.id || file.filename" class="card overflow-hidden group">
        <div class="aspect-square bg-gray-100 relative overflow-hidden">
          <template v-if="isImage(file)">
            <img :src="file.url || file.fileUrl" class="w-full h-full object-cover" @error="e => e.target.style.display='none'" />
          </template>
          <template v-else>
            <div class="w-full h-full bg-gray-800 flex items-center justify-center">
              <Video class="w-10 h-10 text-gray-400" />
            </div>
          </template>
          <div class="absolute inset-0 bg-black/0 group-hover:bg-black/40 transition-all flex items-center justify-center gap-2">
            <button class="opacity-0 group-hover:opacity-100 transition-all bg-white rounded-full p-2 shadow-lg" @click="previewFile(file)">
              <Eye class="w-4 h-4 text-gray-700" />
            </button>
            <button class="opacity-0 group-hover:opacity-100 transition-all bg-red-500 rounded-full p-2 shadow-lg" @click="deleteFile(file)">
              <Trash2 class="w-4 h-4 text-white" />
            </button>
          </div>
          <span class="absolute top-2 left-2 px-2 py-0.5 rounded-full text-[10px] font-medium bg-black/60 text-white">
            {{ isImage(file) ? '照片' : '视频' }}
          </span>
        </div>
        <div class="p-3">
          <input class="text-xs text-gray-800 w-full bg-transparent border-b border-transparent hover:border-gray-200 focus:border-mangrove-400 focus:outline-none truncate" :value="file.filename || file.title || '未命名'" @blur="renameFile(file, $event.target.value)" />
          <p class="text-[10px] text-gray-400 mt-1">{{ file.fileType || '' }} · {{ formatSize(file) }}</p>
        </div>
      </div>
    </div>

    <div v-if="!loading && files.length === 0" class="card p-16 text-center">
      <FolderOpen class="w-12 h-12 text-gray-300 mx-auto mb-3" />
      <p class="text-gray-400 text-sm">暂无资源，拖拽文件上传开始</p>
    </div>

    <div v-if="loading" class="card p-16 text-center">
      <Loader2 class="w-8 h-8 text-mangrove-500 animate-spin mx-auto" />
    </div>

    <div v-if="totalCount > pageSize" class="flex items-center justify-center gap-2 mt-6">
      <button class="px-3 py-1.5 rounded-lg text-sm border border-gray-200 text-gray-500 hover:bg-gray-50 disabled:opacity-30" :disabled="page===0" @click="page--; loadFiles()">上一页</button>
      <span class="text-sm text-gray-500">第 {{ page+1 }} 页 / 共 {{ Math.ceil(totalCount/pageSize) }} 页</span>
      <button class="px-3 py-1.5 rounded-lg text-sm border border-gray-200 text-gray-500 hover:bg-gray-50 disabled:opacity-30" :disabled="(page+1)*pageSize >= totalCount" @click="page++; loadFiles()">下一页</button>
    </div>

    <div v-if="previewVisible" class="fixed inset-0 bg-black/90 z-50 flex items-center justify-center p-4" @click.self="previewVisible=false">
      <button class="absolute top-4 right-4 text-white text-sm hover:text-gray-300 z-10" @click="previewVisible=false">✕ 关闭</button>
      <img v-if="previewFileObj && isImage(previewFileObj)" :src="previewFileObj.url || previewFileObj.fileUrl" class="max-h-[85vh] max-w-[90vw] rounded-lg object-contain" />
      <video v-else-if="previewFileObj" :src="previewFileObj.url || previewFileObj.fileUrl" controls class="max-h-[85vh] max-w-[90vw] rounded-lg" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Upload, Loader2, Eye, Trash2, Video, FolderOpen } from 'lucide-vue-next'

const files = ref([])
const loading = ref(true)
const activeType = ref('all')
const page = ref(0)
const pageSize = 20
const totalCount = ref(0)
const uploading = ref(false)
const newCategory = ref('')
const uploadDate = ref('')
const customCategories = ref(['舞台', '路透', '活动', '生活', '修图'])
const pendingFiles = ref([])
const previewVisible = ref(false)
const previewFileObj = ref(null)
const fileInput = ref(null)

const types = [{ label: '全部', value: 'all' }, { label: '照片', value: 'image' }, { label: '视频', value: 'video' }]

function getToken() { return localStorage.getItem('mangrove_token') }
function isImage(file) { const t = file.fileType || ''; const n = file.filename || ''; return t.includes('image') || /\.(jpg|jpeg|png|gif|webp)$/i.test(n) }
function formatSize(file) { if (file.fileSize) return (file.fileSize / 1024).toFixed(0) + ' KB'; return '' }

async function loadFiles() {
  loading.value = true
  try {
    const res = await fetch('/api/files/list', { headers: { Authorization: `Bearer ${getToken()}` } })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      const all = json.data
      files.value = activeType.value === 'all' ? all : all.filter(f => activeType.value === 'image' ? isImage(f) : !isImage(f))
      totalCount.value = files.value.length
    }
  } catch {} finally { loading.value = false }
}

function handleUpload(e) {
  const fl = e.target.files
  if (!fl) return
  for (let i = 0; i < fl.length; i++) {
    const name = fl[i].name.replace(/\.[^.]+$/, '')
    pendingFiles.value.push({
      file: fl[i], name: fl[i].name, size: fl[i].size, status: 'pending',
      displayName: name, seq: pendingFiles.value.length + 1,
      category: newCategory.value || '', date: uploadDate.value
    })
  }
  e.target.value = ''
}

function handleDrop(e) {
  const fl = e.dataTransfer.files
  if (!fl) return
  for (let i = 0; i < fl.length; i++) {
    const name = fl[i].name.replace(/\.[^.]+$/, '')
    pendingFiles.value.push({
      file: fl[i], name: fl[i].name, size: fl[i].size, status: 'pending',
      displayName: name, seq: pendingFiles.value.length + 1,
      category: newCategory.value || '', date: uploadDate.value
    })
  }
}

async function startUpload() {
  uploading.value = true
  const token = getToken()
  for (const pf of pendingFiles.value) {
    if (pf.status === 'done') continue
    const formData = new FormData()
    formData.append('file', pf.file)
    try {
      const res = await fetch('/api/files/upload', { method: 'POST', headers: { Authorization: `Bearer ${token}` }, body: formData })
      const json = await res.json()
      pf.status = json.code === 200 ? 'done' : 'error'
      // 保存元数据
      if (json.code === 200 && json.data) {
        const storedName = json.data.url ? json.data.url.split('/').pop() : '';
        await fetch('/api/files/meta', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
          body: JSON.stringify({
            filename: storedName,
            displayName: pf.displayName || '',
            seqNo: pf.seq || 0,
            category: pf.category || '',
            photoDate: pf.date || '',
          })
        }).catch(() => {})
      }
    } catch { pf.status = 'error' }
  }
  uploading.value = false
  loadFiles()
}

async function deleteFile(file) {
  if (!confirm('确定删除？')) return
  try {
    const name = file.filename || file.fileUrl?.split('/').pop() || ''
    if (!name) return
    await fetch(`/api/files/${name}`, { method: 'DELETE', headers: { Authorization: `Bearer ${getToken()}` } })
    loadFiles()
  } catch {}
}

function previewFile(file) { previewFileObj.value = file; previewVisible.value = true }

function renameFile(file, newName) {
  if (newName && newName !== file.filename) { file.filename = newName }
}

function addCategory() {
  const name = newCategory.value.trim()
  if (name && !customCategories.value.includes(name)) {
    customCategories.value.push(name)
  }
}

function removeCategory(name) {
  customCategories.value = customCategories.value.filter(c => c !== name)
}

onMounted(async () => {
  loadFiles()
  loadCategories()
})

async function loadCategories() {
  try {
    const res = await fetch('/api/files/categories')
    const json = await res.json()
    if (json.code === 200 && json.data?.length > 0) {
      customCategories.value = json.data
    }
  } catch {}
}
</script>
