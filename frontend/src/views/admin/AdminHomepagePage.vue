<template>
  <div class="min-w-0">
    <div class="flex flex-wrap items-center justify-between gap-2 mb-6">
      <h2 class="text-lg font-semibold text-gray-900">首页管理</h2>
    </div>

    <!-- Main Tabs -->
    <div class="flex gap-2 mb-6">
      <button class="px-4 py-2 rounded-lg text-sm font-medium transition-colors" :class="activeView === 'announcements' ? 'bg-mangrove-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'" @click="activeView = 'announcements'">公告管理</button>
      <button class="px-4 py-2 rounded-lg text-sm font-medium transition-colors" :class="activeView === 'content' ? 'bg-mangrove-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'" @click="activeView = 'content'">内容管理</button>
      <button class="px-4 py-2 rounded-lg text-sm font-medium transition-colors" :class="activeView === 'music' ? 'bg-mangrove-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'" @click="activeView = 'music'">音乐管理</button>
      <button class="px-4 py-2 rounded-lg text-sm font-medium transition-colors" :class="activeView === 'background' ? 'bg-mangrove-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'" @click="activeView = 'background'">背景图管理</button>
    </div>

    <!-- ========== 公告管理 ========== -->
    <template v-if="activeView === 'announcements'">
      <div class="mb-4">
        <button class="btn-primary flex items-center gap-2 text-sm" @click="showAnnouncementModal = true; editingAnnouncement = null; announcementForm = { title: '', content: '' }"><Plus class="w-4 h-4" />新建公告</button>
      </div>
      <div class="card overflow-hidden max-w-full">
        <div class="overflow-x-auto">
          <table class="w-full text-sm min-w-[500px]">
            <thead><tr class="bg-gray-50">
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">#</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">标题</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">内容</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">时间</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">操作</th>
            </tr></thead>
            <tbody>
              <tr v-for="(a, idx) in announcements" :key="a.id" class="border-b border-gray-100 hover:bg-gray-50">
                <td class="px-4 py-3 text-gray-400">{{ idx + 1 }}</td>
                <td class="px-4 py-3 font-medium text-gray-900 max-w-[160px] truncate">{{ a.title }}</td>
                <td class="px-4 py-3 text-gray-600 max-w-[200px] truncate">{{ a.content }}</td>
                <td class="px-4 py-3 text-gray-500 text-xs">{{ formatDate(a.createdAt) }}</td>
                <td class="px-4 py-3 flex gap-2">
                  <button class="p-1.5 rounded-lg hover:bg-blue-50 text-gray-400 hover:text-blue-600" title="编辑" @click="editAnnouncement(a)"><Edit3 class="w-4 h-4" /></button>
                  <button class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500" title="删除" @click="deleteAnnouncement(a)"><Trash2 class="w-4 h-4" /></button>
                </td>
              </tr>
              <tr v-if="announcements.length===0"><td colspan="5" class="px-4 py-10 text-center text-sm text-gray-400">暂无公告</td></tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>

    <!-- ========== 内容管理 ========== -->
    <template v-if="activeView === 'content'">
      <div class="flex gap-2 mb-6">
        <button v-for="tab in contentTabs" :key="tab.value" class="px-4 py-2 rounded-lg text-sm font-medium transition-colors" :class="activeContentTab === tab.value ? 'bg-mangrove-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'" @click="switchContentTab(tab.value)">{{ tab.label }}</button>
      </div>
      <div class="mb-4">
        <button class="btn-primary flex items-center gap-2 text-sm" @click="showContentModal = true"><Plus class="w-4 h-4" />添加{{ currentContentTabLabel }}</button>
      </div>
      <div class="card overflow-hidden max-w-full">
        <div class="overflow-x-auto">
          <table class="w-full text-sm min-w-[600px]">
            <thead><tr class="bg-gray-50">
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">#</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">类型</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">内容</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">排序</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">操作</th>
            </tr></thead>
            <tbody>
              <tr v-for="(item, idx) in contentItems" :key="item.id" class="border-b border-gray-100 hover:bg-gray-50">
                <td class="px-4 py-3 text-gray-400">{{ idx + 1 }}</td>
                <td class="px-4 py-3"><span class="px-2 py-1 rounded-full text-xs" :class="item.contentType === 'PHOTO' ? 'bg-teal-100 text-teal-700' : item.contentType === 'VIDEO' ? 'bg-purple-100 text-purple-700' : 'bg-blue-100 text-blue-700'">{{ contentLabel(item.contentType) }}</span></td>
                <td class="px-4 py-3 text-gray-800"><p class="font-medium">{{ getItemTitle(item) }}</p><p class="text-xs text-gray-500">{{ getItemDesc(item) }}</p></td>
                <td class="px-4 py-3"><input v-model.number="item.sortOrder" type="number" class="w-20 px-2 py-1 border border-gray-200 rounded text-sm" @change="updateSortOrder(item)" /></td>
                <td class="px-4 py-3"><button class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500" title="移除" @click="removeContentItem(item)"><Trash2 class="w-4 h-4" /></button></td>
              </tr>
              <tr v-if="contentItems.length===0"><td colspan="5" class="px-4 py-10 text-center text-sm text-gray-400">暂无内容</td></tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>

    <!-- ========== 音乐管理 ========== -->
    <template v-if="activeView === 'music'">
      <div class="max-w-2xl">
        <div class="card p-6">
          <h3 class="text-sm font-semibold text-gray-900 mb-2">首页背景音乐</h3>
          <p class="text-xs text-gray-500 mb-4">上传 MP3 文件作为首页背景音乐，用户点击右下角音符按钮后可播放。</p>

          <!-- 当前音乐列表 -->
          <div v-if="musicItems.length > 0" class="mb-4 space-y-2">
            <div v-for="item in musicItems" :key="item.id" class="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
              <Music class="w-5 h-5 text-mangrove-600 flex-shrink-0" />
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-900 truncate">{{ item.extraData?.title || '未命名' }}</p>
                <audio v-if="item.extraData?.url" :src="item.extraData.url" controls class="w-full mt-1 h-8" preload="none"></audio>
              </div>
              <button class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 flex-shrink-0" title="删除" @click="removeMusicItem(item)">
                <Trash2 class="w-4 h-4" />
              </button>
            </div>
          </div>
          <div v-else class="mb-4 p-4 text-center text-sm text-gray-400 bg-gray-50 rounded-lg">暂未设置背景音乐</div>

          <!-- 添加音乐表单 -->
          <div class="border-t border-gray-200 pt-4">
            <h4 class="text-sm font-medium text-gray-700 mb-3">上传新音乐</h4>
            <div class="space-y-3">
              <label class="block">
                <span class="mb-1 block text-xs text-gray-600">歌曲名称</span>
                <input v-model="musicForm.title" placeholder="例如：春日序曲" class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm" />
              </label>
              <label class="block">
                <span class="mb-1 block text-xs text-gray-600">MP3 文件</span>
                <label class="flex items-center gap-2 w-full border border-gray-200 rounded-lg px-3 py-2 text-sm cursor-pointer hover:bg-gray-50 transition-colors">
                  <Upload class="w-4 h-4 text-gray-400" />
                  <span :class="musicFileName ? 'text-gray-800' : 'text-gray-400'">{{ musicFileName || '选择 MP3 文件...' }}</span>
                  <input type="file" accept=".mp3,audio/mpeg" class="hidden" @change="onMusicFileChange" />
                </label>
              </label>
              <p v-if="musicSaveMsg" class="text-xs" :class="musicSaveMsg.startsWith('✓') ? 'text-green-600' : 'text-red-500'">{{ musicSaveMsg }}</p>
              <div class="flex justify-end">
                <button class="btn-primary text-sm" :disabled="musicSaving || !musicFile" @click="saveMusicItem">{{ musicSaving ? '上传中...' : '添加音乐' }}</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ========== 背景图管理 ========== -->
    <template v-if="activeView === 'background'">
      <div class="max-w-2xl">
        <div class="card p-6">
          <h3 class="text-sm font-semibold text-gray-900 mb-4">首页背景图</h3>
          <p class="text-xs text-gray-500 mb-4">上传一张图片作为首页的全屏背景。建议尺寸 1920×1080 以上，支持 JPG、PNG 格式。</p>

          <!-- 当前背景图预览 -->
          <div v-if="bgImageUrl" class="mb-4 rounded-lg overflow-hidden border border-gray-200">
            <img :src="bgImageUrl" class="w-full h-48 object-cover bg-gray-100" alt="当前背景图" />
          </div>
          <div v-else class="mb-4 rounded-lg border-2 border-dashed border-gray-200 h-48 flex items-center justify-center text-gray-400 text-sm">
            暂未设置背景图
          </div>

          <!-- 上传区域 -->
          <div class="flex items-center gap-3">
            <label class="btn-primary flex items-center gap-2 text-sm cursor-pointer">
              <Upload class="w-4 h-4" />
              {{ bgImageUrl ? '更换背景图' : '上传背景图' }}
              <input type="file" accept="image/*" class="hidden" @change="onBgFileChange" />
            </label>
            <button v-if="bgImageUrl" class="btn-secondary flex items-center gap-2 text-sm text-red-600 hover:text-red-700" @click="removeBgImage">
              <Trash2 class="w-4 h-4" />移除
            </button>
          </div>

          <!-- 上传状态 -->
          <p v-if="bgUploadStatus" class="mt-3 text-xs" :class="bgUploadStatus.startsWith('✓') ? 'text-green-600' : bgUploadStatus.startsWith('✗') ? 'text-red-500' : 'text-gray-500'">
            {{ bgUploadStatus }}
          </p>
        </div>
      </div>
    </template>

    <!-- Announcement Modal -->
    <div v-if="showAnnouncementModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4" @click.self="showAnnouncementModal = false">
      <div class="bg-white rounded-2xl p-6 w-full max-w-lg">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">{{ editingAnnouncement ? '编辑公告' : '新建公告' }}</h3>
        <div class="space-y-4">
          <label class="block"><span class="mb-1 block text-sm font-medium text-gray-700">标题</span><input v-model="announcementForm.title" class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm" required /></label>
          <label class="block"><span class="mb-1 block text-sm font-medium text-gray-700">内容</span><textarea v-model="announcementForm.content" rows="3" class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm resize-none"></textarea></label>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <button class="btn-secondary text-sm" @click="showAnnouncementModal = false">取消</button>
          <button class="btn-primary text-sm" @click="saveAnnouncement">保存</button>
        </div>
      </div>
    </div>

    <!-- Content Add Modal -->
    <div v-if="showContentModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4" @click.self="showContentModal = false">
      <div class="bg-white rounded-2xl p-6 w-full max-w-lg max-h-[80vh] overflow-y-auto">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">添加{{ currentContentTabLabel }}</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">选择内容</label>
            <div class="max-h-60 overflow-y-auto border border-gray-200 rounded-lg">
              <div v-for="item in availableContent" :key="item.id" class="flex items-center gap-3 p-3 cursor-pointer hover:bg-gray-50 border-b border-gray-100 last:border-0 transition-colors" :class="selectedContentId === item.id ? 'bg-mangrove-50 border-mangrove-200' : ''" @click="selectedContentId = item.id">
                <img v-if="getItemThumb(item)" :src="getItemThumb(item)" class="w-12 h-12 rounded object-cover bg-gray-100" />
                <div v-else class="w-12 h-12 rounded bg-gray-100 flex items-center justify-center text-gray-400">🖼️</div>
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium text-gray-900 truncate">{{ item.title || getItemTitle(item) }}</p>
                  <p class="text-xs text-gray-500 truncate">{{ item.filename }}</p>
                  <div class="flex flex-wrap items-center gap-1.5 mt-1">
                    <span class="px-1.5 py-0.5 rounded text-[11px] bg-gray-100 text-gray-600">{{ item.kindLabel }}</span>
                    <span v-if="item.category" class="px-1.5 py-0.5 rounded text-[11px] bg-amber-50 text-amber-700">分类：{{ item.category }}</span>
                    <span v-if="item.fileSizeText" class="text-[11px] text-gray-400">{{ item.fileSizeText }}</span>
                    <span v-if="item.dateText" class="text-[11px] text-gray-400">{{ item.dateText }}</span>
                  </div>
                </div>
                <span v-if="selectedContentId === item.id" class="text-mangrove-600">✓</span>
              </div>
              <div v-if="availableContent.length === 0" class="p-4 text-center text-sm text-gray-400">暂无可用内容</div>
            </div>
          </div>
          <label class="block"><span class="mb-1 block text-sm font-medium text-gray-700">排序</span><input v-model.number="newItemSortOrder" type="number" class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm" /></label>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <button class="btn-secondary text-sm" @click="showContentModal = false">取消</button>
          <button class="btn-primary text-sm" @click="addContentItem">添加</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { Plus, Edit3, Trash2, Upload, Music } from 'lucide-vue-next'

const activeView = ref('announcements')
const activeContentTab = ref('PHOTO')
const announcements = ref([])
const contentItems = ref([])
const allPhotos = ref([])
const allVideos = ref([])
const showAnnouncementModal = ref(false)
const showContentModal = ref(false)
const editingAnnouncement = ref(null)
const announcementForm = ref({ title: '', content: '' })
const selectedContentId = ref('')
const newItemSortOrder = ref(0)

// 背景图管理
const bgImageUrl = ref('')
const bgUploadStatus = ref('')

// 音乐管理
const musicItems = ref([])
const musicForm = ref({ title: '' })
const musicFile = ref(null)
const musicFileName = ref('')
const musicSaving = ref(false)
const musicSaveMsg = ref('')

const contentTabs = [
  { label: '照片', value: 'PHOTO' },
  { label: '视频', value: 'VIDEO' }
]

const currentContentTabLabel = computed(() => contentTabs.find(t => t.value === activeContentTab.value)?.label || '')

function getToken() { return localStorage.getItem('mangrove_token') || '' }
function formatDate(d) { return d ? new Date(d).toLocaleDateString('zh-CN') : '-' }

const contentLabelMap = { PHOTO: '照片', VIDEO: '视频', LONG_VIDEO: '长视频' }
function contentLabel(type) { return contentLabelMap[type] || type }

async function fetchAnnouncements() {
  try {
    const res = await fetch('/api/admin/announcements?page=0&size=100', { headers: { Authorization: `Bearer ${getToken()}` } })
    const json = await res.json()
    if (json.code === 200 && json.data) announcements.value = json.data.content || []
  } catch (e) { console.error(e) }
}

async function saveAnnouncement() {
  if (!announcementForm.value.title) return alert('请填写标题')
  try {
    const url = editingAnnouncement.value ? `/api/admin/announcements/${editingAnnouncement.value.id}` : '/api/admin/announcements'
    const method = editingAnnouncement.value ? 'PUT' : 'POST'
    await request(url, { method, body: JSON.stringify(announcementForm.value) })
    showAnnouncementModal.value = false
    await fetchAnnouncements()
  } catch (e) { alert(e.message) }
}

function editAnnouncement(a) { editingAnnouncement.value = a; announcementForm.value = { title: a.title, content: a.content || '' }; showAnnouncementModal.value = true }

async function deleteAnnouncement(a) {
  if (!confirm('确认删除？')) return
  try { await request(`/api/admin/announcements/${a.id}`, { method: 'DELETE' }); await fetchAnnouncements() } catch (e) { alert(e.message) }
}

async function request(url, opts = {}) {
  const isForm = opts.body instanceof FormData
  const res = await fetch(url, { ...opts, headers: { ...(isForm ? {} : { 'Content-Type': 'application/json' }), Authorization: `Bearer ${getToken()}`, ...(opts.headers || {}) } })
  const json = await res.json()
  if (!res.ok || json.code !== 200) throw new Error(json.msg || '请求失败')
  return json.data
}

// ========== 背景图管理 ==========
async function fetchBgImage() {
  try {
    const url = await request('/api/admin/config/homepage_background_url')
    if (url) bgImageUrl.value = url
  } catch {}
}

async function onBgFileChange(e) {
  const file = e.target.files?.[0]
  if (!file) return
  bgUploadStatus.value = '上传中...'
  try {
    const formData = new FormData()
    formData.append('file', file)
    const uploaded = await fetch('/api/files/upload', {
      method: 'POST',
      headers: { Authorization: `Bearer ${getToken()}` },
      body: formData
    })
    const json = await uploaded.json()
    if (json.code !== 200) throw new Error(json.msg || '上传失败')
    const imageUrl = json.data.url
    await request('/api/admin/config/homepage_background_url', {
      method: 'PUT',
      body: JSON.stringify({ value: imageUrl })
    })
    bgImageUrl.value = imageUrl
    bgUploadStatus.value = '✓ 背景图已更新'
    setTimeout(() => { bgUploadStatus.value = '' }, 3000)
  } catch (err) {
    bgUploadStatus.value = `✗ ${err.message}`
  } finally {
    e.target.value = ''
  }
}

async function removeBgImage() {
  if (!confirm('确认移除首页背景图？')) return
  try {
    await request('/api/admin/config/homepage_background_url', {
      method: 'PUT',
      body: JSON.stringify({ value: '' })
    })
    bgImageUrl.value = ''
    bgUploadStatus.value = '✓ 背景图已移除'
    setTimeout(() => { bgUploadStatus.value = '' }, 3000)
  } catch (err) {
    alert(err.message)
  }
}

// ========== 音乐管理 ==========
async function fetchMusicItems() {
  try {
    const res = await fetch('/api/admin/homepage-items/MUSIC', { headers: { Authorization: `Bearer ${getToken()}` } })
    const json = await res.json()
    if (json.code === 200) musicItems.value = json.data || []
  } catch (e) { console.error(e) }
}

function onMusicFileChange(e) {
  const file = e.target.files?.[0]
  if (!file) return
  musicFile.value = file
  musicFileName.value = file.name.replace(/\.mp3$/i, '')
  if (!musicForm.value.title) musicForm.value.title = musicFileName.value
  e.target.value = ''
}

async function saveMusicItem() {
  if (!musicFile.value) return
  musicSaving.value = true
  musicSaveMsg.value = ''
  try {
    const formData = new FormData()
    formData.append('file', musicFile.value)
    const uploadRes = await fetch('/api/files/upload', {
      method: 'POST',
      headers: { Authorization: `Bearer ${getToken()}` },
      body: formData
    })
    const uploadJson = await uploadRes.json()
    if (uploadJson.code !== 200) throw new Error(uploadJson.msg || '上传失败')
    const mp3Url = uploadJson.data.url
    await request('/api/admin/homepage-items/MUSIC', {
      method: 'POST',
      body: JSON.stringify({
        contentType: 'PHOTO',
        targetId: 0,
        sortOrder: musicItems.value.length,
        extraData: { title: musicForm.value.title || musicFileName.value, url: mp3Url }
      })
    })
    musicForm.value = { title: '' }
    musicFile.value = null
    musicFileName.value = ''
    musicSaveMsg.value = '✓ 上传成功'
    setTimeout(() => { musicSaveMsg.value = '' }, 3000)
    await fetchMusicItems()
  } catch (e) {
    musicSaveMsg.value = '✗ ' + e.message
  } finally {
    musicSaving.value = false
  }
}

async function removeMusicItem(item) {
  if (!confirm(`确认删除「${item.extraData?.title || 'QQ音乐'}」？`)) return
  try {
    await request(`/api/admin/homepage-items/${item.id}`, { method: 'DELETE' })
    await fetchMusicItems()
  } catch (e) { alert(e.message) }
}

async function fetchContentItems() {
  try {
    const res = await fetch(`/api/admin/homepage-items/${activeContentTab.value}`, { headers: { Authorization: `Bearer ${getToken()}` } })
    const json = await res.json()
    if (json.code === 200) contentItems.value = json.data || []
  } catch (e) { console.error(e) }
}

async function fetchAllContent() {
  try {
    const [listRes, metaRes] = await Promise.all([
      fetch('/api/files/list?page=0&size=200'),
      fetch('/api/files/meta').catch(() => null)
    ])
    const json = await listRes.json()
    if (json.code !== 200) return
    // 合并 file_meta 元数据（displayName / category / photoDate），让候选可区分
    const metaMap = {}
    if (metaRes && metaRes.ok) {
      const mj = await metaRes.json()
      if (mj.data) mj.data.forEach(m => { metaMap[m.filename] = m })
    }
    const all = json.data || []
    const isImage = (f) => /\.(jpg|jpeg|png|gif|webp)$/i.test(f.filename || '')
    const isVideo = (f) => /\.(mp4|webm|mov)$/i.test(f.filename || '')
    const enrich = (files, kindLabel) => (files || []).map(f => {
      const m = metaMap[f.filename] || {}
      const title = m.displayName || (f.filename || '').replace(/\.[^.]+$/, '') || `#${f.filename}`
      return {
        ...f,
        id: stableFileId(f.filename),
        kindLabel,
        title,
        category: m.category || '',
        fileSizeText: formatSize(f.fileSize),
        dateText: m.photoDate || (f.createdAt || '').substring(0, 10) || ''
      }
    })
    allPhotos.value = enrich(all.filter(isImage), '图片')
    allVideos.value = enrich(all.filter(isVideo), '视频')
  } catch (e) { console.error(e) }
}

function formatSize(bytes) {
  const n = Number(bytes) || 0
  if (n === 0) return ''
  if (n < 1024) return n + ' B'
  if (n < 1024 * 1024) return (n / 1024).toFixed(1) + ' KB'
  return (n / 1024 / 1024).toFixed(1) + ' MB'
}

function stableFileId(filename) {
  let hash = 0
  const s = String(filename || '')
  for (let i = 0; i < s.length; i++) {
    hash = (hash * 31 + s.charCodeAt(i)) | 0
  }
  return Math.abs(hash) || 1
}

const availableContent = computed(() => {
  if (activeContentTab.value === 'PHOTO') return allPhotos.value
  return allVideos.value
})

function getItemTitle(item) { return item.extraData?.title || item.title || item.filename || `#${item.id}` }
function getItemDesc(item) { return item.extraData?.description || item.subCategory || item.filename || '' }
function getItemThumb(item) { return item.url || item.thumbnailUrl || item.extraData?.url || null }

function switchContentTab(tab) { activeContentTab.value = tab; selectedContentId.value = ''; fetchContentItems() }

async function addContentItem() {
  if (!selectedContentId.value) return alert('请选择内容')
  const source = availableContent.value.find(i => i.id === selectedContentId.value)
  try {
    const contentType = activeContentTab.value // PHOTO 或 VIDEO
    await request(`/api/admin/homepage-items/${activeContentTab.value}`, {
      method: 'POST',
      body: JSON.stringify({
        contentType,
        targetId: selectedContentId.value,
        sortOrder: newItemSortOrder.value,
        extraData: { title: source?.title || source?.filename, url: source?.url, description: source?.subCategory || '' }
      })
    })
    showContentModal.value = false; selectedContentId.value = ''; newItemSortOrder.value = 0
    await fetchContentItems()
  } catch (e) { alert(e.message) }
}

async function updateSortOrder(item) {
  try { await request(`/api/admin/homepage-items/${item.id}/sort?sortOrder=${item.sortOrder}`, { method: 'PUT' }) } catch (e) { console.error(e) }
}

async function removeContentItem(item) {
  if (!confirm('确认移除？')) return
  try { await request(`/api/admin/homepage-items/${item.id}`, { method: 'DELETE' }); await fetchContentItems() } catch (e) { alert(e.message) }
}

watch(activeView, (v) => {
  if (v === 'content') { fetchContentItems(); fetchAllContent() }
  if (v === 'background') { fetchBgImage() }
  if (v === 'music') { fetchMusicItems() }
})

onMounted(() => { fetchAnnouncements(); fetchAllContent() })
</script>
