<template>
  <div class="min-w-0">
    <div class="flex flex-wrap items-center justify-between gap-2 mb-6">
      <div>
        <h2 class="text-lg font-semibold text-gray-900">资源管理</h2>
        <p class="text-sm text-gray-500 mt-1">管理全站照片 & 视频 & 音乐</p>
      </div>
    </div>

    <div class="mb-6 flex w-fit flex-wrap border border-gray-200 bg-white p-1">
      <button v-for="section in sections" :key="section.value" type="button"
        class="px-4 py-2 text-sm font-medium transition-colors"
        :class="activeSection === section.value ? 'bg-mangrove-600 text-white' : 'text-gray-500 hover:text-gray-800'"
        @click="activeSection = section.value">
        {{ section.label }}
      </button>
    </div>

    <MusicTrackManager v-if="activeSection === 'tracks'" :categories="musicCats" />
    <MusicAlbumManager v-else-if="activeSection === 'albums'" />
    <LongVideoManager v-else-if="activeSection === 'longVideos'" />
    <template v-else>

    <!-- 音乐分类管理 -->
    <div class="card p-5 mb-6">
      <h3 class="text-sm font-semibold text-gray-800 mb-3">🎵 音乐分类管理</h3>
      <div class="flex items-center gap-2 flex-wrap mb-2">
        <span v-for="(cat, i) in musicCats" :key="cat"
          class="tag text-xs cursor-pointer group relative hover:bg-mangrove-100 transition-colors">
          {{ cat }}
          <button @click="removeMusicCat(i)" class="ml-1 opacity-0 group-hover:opacity-100 text-gray-400 hover:text-red-500">✕</button>
        </span>
      </div>
      <div class="flex gap-2 items-center">
        <input v-model="newMusicCat" @keydown.enter="addMusicCat" placeholder="输入新分类" class="rounded-lg border border-gray-200 px-3 py-1.5 text-xs w-40 focus:ring-1 focus:ring-mangrove-400" />
        <button @click="addMusicCat" class="text-xs text-mangrove-600 hover:text-mangrove-700 font-medium">+ 添加</button>
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
            <!-- 自定义滚动下拉 -->
            <div class="relative w-48">
              <div @click="catDropOpen=!catDropOpen" class="rounded-lg border border-gray-200 px-3 py-2 text-sm cursor-pointer bg-white hover:border-mangrove-300">
                {{ newCategory || '选择分类' }}
              </div>
              <div v-if="catDropOpen" class="absolute top-full mt-1 w-full bg-white rounded-lg shadow-lg border border-gray-200 z-20 max-h-48 overflow-y-auto">
                <div v-for="c in allCategories" :key="c" @click="newCategory=c; catDropOpen=false" class="px-3 py-1.5 text-sm hover:bg-mangrove-50 cursor-pointer">{{ c }}</div>
              </div>
              <div v-if="catDropOpen" class="fixed inset-0 z-10" @click="catDropOpen=false"></div>
            </div>
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
          @click="openFileDialog"
          @dragover.prevent
          @drop.prevent="handleDrop">
          <Upload class="w-10 h-10 text-gray-300 mx-auto mb-3" />
          <p class="text-sm text-gray-500">拖拽文件到此处，或点击选择</p>
          <p class="text-xs text-gray-400 mt-1">支持 JPG / PNG / WebP / MP4 / MP3，单文件最大 500MB</p>
          <input ref="fileInput" type="file" accept="image/*,video/*,audio/*" multiple class="hidden" @change="handleUpload" />
        </div>
        <!-- 待上传列表 -->
        <div v-if="pendingFiles.length > 0" class="space-y-2">
          <div v-for="(f, i) in pendingFiles" :key="i" class="flex items-center gap-2 bg-gray-50 rounded-xl p-3 flex-wrap">
            <span class="text-xs text-gray-400 w-6">#{{ f.seq || (i + 1) }}</span>
            <input v-model="f.displayName" placeholder="名称" class="text-sm border rounded px-2 py-1 w-28 focus:ring-1 focus:ring-mangrove-400 focus:border-transparent" />
            <input v-model="f.seq" type="number" placeholder="序号" class="text-sm border rounded px-2 py-1 w-16 focus:ring-1 focus:ring-mangrove-400 focus:border-transparent" />
            <select v-model="f.category" size="5" class="text-xs border rounded px-2 py-1 focus:ring-1 focus:ring-mangrove-400 focus:border-transparent max-h-24 overflow-y-auto">
              <option value="">分类</option>
              <option v-for="c in allCategories" :key="c" :value="c">{{ c }}</option>
            </select>
            <span class="flex-1 text-xs text-gray-500 truncate">{{ f.name }}</span>
            <span class="text-xs text-gray-400">{{ (f.size/1024).toFixed(0) }}KB</span>
            <span v-if="f.status==='done'" class="text-green-500 text-xs">✓</span>
            <span v-else-if="f.status==='error'" class="text-red-500 text-xs" :title="f.error">✕</span>
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
          <template v-else-if="isVideo(file)">
            <div class="w-full h-full bg-gray-900 flex items-center justify-center relative">
              <video :src="file.url || file.fileUrl" class="w-full h-full object-cover opacity-40" muted preload="metadata" @loadeddata="$event.target.currentTime=0.5" />
              <div class="absolute inset-0 flex items-center justify-center">
                <div class="w-12 h-12 rounded-full bg-white/30 flex items-center justify-center">
                  <Play class="w-5 h-5 text-white ml-0.5" />
                </div>
              </div>
            </div>
          </template>
          <template v-else>
            <div class="w-full h-full bg-gray-800 flex items-center justify-center">
              <Music class="w-10 h-10 text-gray-400" />
            </div>
          </template>
          <div class="absolute inset-0 bg-black/0 group-hover:bg-black/40 transition-all flex items-center justify-center gap-2">
            <button class="opacity-0 group-hover:opacity-100 transition-all bg-white rounded-full p-2 shadow-lg" @click="previewFile(file)" title="预览">
              <Eye class="w-4 h-4 text-gray-700" />
            </button>
            <button class="opacity-0 group-hover:opacity-100 transition-all bg-blue-500 rounded-full p-2 shadow-lg" @click="editFile(file)" title="编辑属性">
              <Pencil class="w-4 h-4 text-white" />
            </button>
            <button class="opacity-0 group-hover:opacity-100 transition-all bg-red-500 rounded-full p-2 shadow-lg" @click="deleteFile(file)" title="删除">
              <Trash2 class="w-4 h-4 text-white" />
            </button>
          </div>
          <span class="absolute top-2 left-2 px-2 py-0.5 rounded-full text-[10px] font-medium bg-black/60 text-white">
            {{ isImage(file) ? '照片' : isVideo(file) ? '视频' : '音乐' }}
          </span>
        </div>
        <div class="p-3">
          <p class="text-xs text-gray-800 font-medium truncate">{{ file.displayName || file.filename || '未命名' }}</p>
          <div class="flex items-center gap-2 mt-1 flex-wrap">
            <span v-if="file.category" class="text-[10px] px-1.5 py-0.5 rounded-full bg-mangrove-50 text-mangrove-700">{{ file.category }}</span>
            <span v-if="file.seqNo" class="text-[10px] text-gray-400">#{{ file.seqNo }}</span>
            <span class="text-[10px] text-gray-400">{{ file.fileType || '' }}</span>
            <span class="text-[10px] text-gray-400">{{ formatSize(file) }}</span>
          </div>
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
      <video v-else-if="previewFileObj && isVideo(previewFileObj)" :src="previewFileObj.url || previewFileObj.fileUrl" controls class="max-h-[85vh] max-w-[90vw] rounded-lg" />
      <audio v-else-if="previewFileObj" :src="previewFileObj.url || previewFileObj.fileUrl" controls class="w-full max-w-md" />
    </div>

    <!-- 编辑弹窗 -->
    <div v-if="editingFile" class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4" @click.self="editingFile=null">
      <div class="bg-white rounded-2xl shadow-xl max-w-lg w-full p-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">编辑照片属性</h3>
        <div class="space-y-4">
          <div>
            <label class="text-sm text-gray-500 mb-1 block">序号</label>
            <input v-model="editForm.seqNo" type="number" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm focus:ring-2 focus:ring-mangrove-500" />
          </div>
          <div>
            <label class="text-sm text-gray-500 mb-1 block">名称</label>
            <input v-model="editForm.displayName" type="text" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm focus:ring-2 focus:ring-mangrove-500" />
          </div>
          <div>
            <label class="text-sm text-gray-500 mb-1 block">分类</label>
            <select v-model="editForm.category" size="5" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm focus:ring-2 focus:ring-mangrove-500 max-h-32 overflow-y-auto">
              <option value="">未分类</option>
              <option v-for="c in allCategories" :key="c" :value="c">{{ c }}</option>
            </select>
          </div>
          <div>
            <label class="text-sm text-gray-500 mb-1 block">拍摄日期</label>
            <input v-model="editForm.photoDate" type="date" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm focus:ring-2 focus:ring-mangrove-500" />
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-6">
          <button class="btn-outline text-sm" @click="editingFile=null">取消</button>
          <button class="btn-primary text-sm flex items-center gap-1" @click="saveEdit">
            <span v-if="saveSuccess">✓ 已保存</span>
            <span v-else>保存</span>
          </button>
        </div>
      </div>
    </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Upload, Loader2, Eye, Trash2, Video, FolderOpen, Pencil, Play, Music } from 'lucide-vue-next'
import MusicTrackManager from '@/components/admin/MusicTrackManager.vue'
import MusicAlbumManager from '@/components/admin/MusicAlbumManager.vue'
import LongVideoManager from '@/components/admin/LongVideoManager.vue'

const activeSection = ref('files')
const sections = [
  { label: '资源文件', value: 'files' },
  { label: '音乐歌曲 / QQ链接', value: 'tracks' },
  { label: '音乐专辑', value: 'albums' },
  { label: '长视频 / B站链接', value: 'longVideos' },
]

const files = ref([])
const loading = ref(true)
const activeType = ref('all')
const page = ref(0)
const pageSize = 20
const totalCount = ref(0)
const uploading = ref(false)
const newCategory = ref('')
const catDropOpen = ref(false)
const uploadDate = ref('')
const customCategories = ref(['官方', '饭拍', '活动', '日常', '微博日常'])
const defaultMusicCats = ['单曲', '舞台', '翻唱']
const storedMusicCats = JSON.parse(localStorage.getItem('mangrove_music_cats') || '[]')
const musicCats = ref([...new Set([...defaultMusicCats, ...storedMusicCats])])
const allCategories = computed(() => {
  const stored = JSON.parse(localStorage.getItem('mangrove_music_cats') || '[]')
  const base = defaultMusicCats
  const merged = [...new Set([...base, ...stored])]
  return [...new Set([...customCategories.value, ...merged])]
})
const newMusicCat = ref('')
const pendingFiles = ref([])
const previewVisible = ref(false)
const previewFileObj = ref(null)
const editingFile = ref(null)
const editForm = ref({ filename: '', displayName: '', seqNo: 0, category: '', photoDate: '' })
const saveSuccess = ref(false)
const fileInput = ref(null)

const types = [{ label: '全部', value: 'all' }, { label: '照片', value: 'image' }, { label: '视频', value: 'video' }, { label: '音乐', value: 'audio' }]

function getToken() { return localStorage.getItem('mangrove_token') }
function isImage(file) { const t = file.fileType || ''; const n = file.filename || ''; return t.includes('image') || /\.(jpg|jpeg|png|gif|webp)$/i.test(n) }
function isVideo(file) { const t = file.fileType || ''; const n = file.filename || ''; return t.includes('video') || /\.(mp4|webm|mov|avi)$/i.test(n) }
function isAudio(file) { const n = file.filename || ''; return /\.(mp3|wav|flac|aac|ogg|m4a)$/i.test(n) }
function formatSize(file) { if (file.fileSize) return (file.fileSize / 1024).toFixed(0) + ' KB'; return '' }

async function loadFiles() {
  loading.value = true
  try {
    const [fileRes, metaRes] = await Promise.all([
      fetch('/api/files/list', { headers: { Authorization: `Bearer ${getToken()}` } }),
      fetch('/api/files/meta', { headers: { Authorization: `Bearer ${getToken()}` } }),
    ])
    const fileJson = await fileRes.json()
    const metaJson = await metaRes.json()
    const metaMap = {}
    if (metaJson.code === 200 && metaJson.data) {
      metaJson.data.forEach(m => { metaMap[m.filename] = m })
    }
    if (fileJson.code === 200 && fileJson.data) {
      const all = fileJson.data.map(f => {
        const m = metaMap[f.filename] || {}
        return { ...f, displayName: m.displayName, seqNo: m.seqNo, category: m.category, photoDate: m.photoDate }
      })
      files.value = activeType.value === 'all' ? all : all.filter(f => activeType.value === 'image' ? isImage(f) : activeType.value === 'video' ? isVideo(f) : isAudio(f))
      totalCount.value = files.value.length
    }
  } catch {} finally { loading.value = false }
}

function openFileDialog() {
  fileInput.value?.click()
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
      if (json.code !== 200) pf.error = json.msg || '上传失败'
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
    } catch (e) {
      pf.status = 'error'
      pf.error = e.message || '上传失败'
    }
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

function editFile(file) {
  editForm.value = {
    filename: file.filename || (file.url || '').split('/').pop() || '',
    displayName: file.displayName || (file.filename || '').replace(/\.[^.]+$/, ''),
    seqNo: file.seqNo || 0,
    category: file.category || '',
    photoDate: file.photoDate || '',
  }
  editingFile.value = file
}

async function saveEdit() {
  const token = getToken()
  if (!token) return
  const meta = {
    filename: editForm.value.filename,
    displayName: editForm.value.displayName || '',
    seqNo: editForm.value.seqNo || 0,
    category: editForm.value.category || '',
    photoDate: editForm.value.photoDate || ''
  }
  if (!meta.filename) return
  try {
    const res = await fetch('/api/files/meta', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
      body: JSON.stringify(meta)
    })
    const json = await res.json()
    if (json.code === 200) {
      saveSuccess.value = true
      setTimeout(() => { saveSuccess.value = false }, 2000)
      editingFile.value = null
      loadFiles()
    }
  } catch (e) {
    console.error('保存失败:', e)
  }
}

function renameFile(file, newName) {
  if (newName && newName !== file.filename) { file.filename = newName }
}

function addCategory() {
  const name = newCategory.value.trim()
  if (!name || customCategories.value.includes(name)) return
  customCategories.value.push(name)
  newCategory.value = ''
  fetch('/api/files/categories', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` },
    body: JSON.stringify({ name })
  }).catch(() => {})
}

function removeCategory(name) {
  customCategories.value = customCategories.value.filter(c => c !== name)
  fetch(`/api/files/categories?name=${encodeURIComponent(name)}`, {
    method: 'DELETE',
    headers: { Authorization: `Bearer ${getToken()}` }
  }).catch(() => {})
}

function addMusicCat() {
  const name = newMusicCat.value.trim()
  if (!name || musicCats.value.includes(name)) return
  musicCats.value.push(name)
  newMusicCat.value = ''
  localStorage.setItem('mangrove_music_cats', JSON.stringify(musicCats.value))
}

function removeMusicCat(i) {
  musicCats.value.splice(i, 1)
  localStorage.setItem('mangrove_music_cats', JSON.stringify(musicCats.value))
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
