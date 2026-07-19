<template>
  <div>
    <!-- Stats -->
    <div class="grid grid-cols-4 gap-4 mb-8">
      <div v-for="stat in stats" :key="stat.label" class="card p-5 flex items-start justify-between">
        <div>
          <p class="text-3xl font-bold text-gray-900">{{ stat.value }}</p>
          <p class="text-sm text-gray-500 mt-1">{{ stat.label }}</p>
        </div>
        <div class="w-10 h-10 rounded-xl bg-mangrove-50 flex items-center justify-center flex-shrink-0">
          <component :is="stat.icon" class="w-5 h-5 text-mangrove-600" />
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="grid grid-cols-2 gap-4 mb-8">
      <router-link to="/admin/media" class="card p-4 flex items-center gap-3 hover:shadow-lg transition-shadow cursor-pointer">
        <div class="w-10 h-10 rounded-xl bg-mangrove-100 flex items-center justify-center">
          <FolderOpen class="w-5 h-5 text-mangrove-600" />
        </div>
        <div>
          <p class="font-semibold text-gray-800 text-sm">资源管理</p>
          <p class="text-xs text-gray-500">管理全站照片和视频</p>
        </div>
      </router-link>
      <router-link to="/admin/audit" class="card p-4 flex items-center gap-3 hover:shadow-lg transition-shadow cursor-pointer">
        <div class="w-10 h-10 rounded-xl bg-mangrove-100 flex items-center justify-center">
          <ShieldCheck class="w-5 h-5 text-mangrove-600" />
        </div>
        <div>
          <p class="font-semibold text-gray-800 text-sm">内容审核</p>
          <p class="text-xs text-gray-500">审核用户上传和删除申请</p>
        </div>
      </router-link>
    </div>

    <!-- 封面设置 -->
    <div class="card p-5 mb-8">
      <h2 class="text-sm font-semibold text-gray-800 mb-4">🖼️ 照片馆藏 - Hero 封面设置</h2>
      <div class="flex items-start gap-6">
        <!-- 预览 -->
        <div class="w-48 h-32 rounded-xl overflow-hidden bg-gray-100 border border-gray-200 flex items-center justify-center shrink-0">
          <img v-if="coverUrl" :src="coverUrl" class="w-full h-full object-cover" />
          <Image v-else class="w-8 h-8 text-gray-300" />
        </div>
        <div class="flex-1">
          <p class="text-sm text-gray-600 mb-3">上传艺人照片作为照片馆藏页面的 Hero 封面图</p>
          <div class="flex items-center gap-3">
            <label class="btn-outline text-sm cursor-pointer flex items-center gap-2">
              <Upload class="w-4 h-4" />
              选择图片
              <input type="file" accept="image/*" class="hidden" @change="handleCoverUpload" />
            </label>
            <span v-if="uploading" class="text-sm text-gray-400">上传中...</span>
            <span v-else-if="coverUrl" class="text-sm text-green-600 flex items-center gap-1">
              <CheckCircle class="w-4 h-4" /> 已设置
            </span>
          </div>
          <p v-if="uploadMsg" class="text-xs mt-2" :class="uploadMsg.includes('成功') ? 'text-green-600' : 'text-red-500'">{{ uploadMsg }}</p>
        </div>
      </div>
    </div>

    <!-- 芒果园背景图设置 -->
    <div class="card p-5 mb-8">
      <h2 class="text-sm font-semibold text-gray-800 mb-4">🌳 芒果园 - 背景图设置</h2>
      <div class="flex items-start gap-6">
        <div class="w-48 h-32 rounded-xl overflow-hidden bg-gray-100 border border-gray-200 flex items-center justify-center shrink-0">
          <img v-if="treeBgUrl" :src="treeBgUrl" class="w-full h-full object-cover" />
          <TreePine v-else class="w-8 h-8 text-gray-300" />
        </div>
        <div class="flex-1">
          <p class="text-sm text-gray-600 mb-3">上传芒果园大树页面的全屏背景图，建议 1920×1080 以上</p>
          <div class="flex items-center gap-3">
            <label class="btn-outline text-sm cursor-pointer flex items-center gap-2">
              <Upload class="w-4 h-4" /> 选择图片
              <input type="file" accept="image/*" class="hidden" @change="handleTreeBgUpload" />
            </label>
            <button v-if="treeBgUrl" class="btn-outline text-sm text-red-500 flex items-center gap-2" @click="removeTreeBg">
              <Trash2 class="w-4 h-4" /> 移除
            </button>
            <span v-if="treeBgUploading" class="text-sm text-gray-400">上传中...</span>
            <span v-else-if="treeBgUrl" class="text-sm text-green-600 flex items-center gap-1"><CheckCircle class="w-4 h-4" /> 已设置</span>
          </div>
          <p v-if="treeBgMsg" class="text-xs mt-2" :class="treeBgMsg.startsWith('✓') ? 'text-green-600' : 'text-red-500'">{{ treeBgMsg }}</p>
        </div>
      </div>
    </div>

    <!-- 小树背景图设置 -->
    <div class="card p-5 mb-8">
      <h2 class="text-sm font-semibold text-gray-800 mb-4">🌱 我的小树 - 背景图设置</h2>
      <div class="flex items-start gap-6">
        <div class="w-48 h-32 rounded-xl overflow-hidden bg-gray-100 border border-gray-200 flex items-center justify-center shrink-0">
          <img v-if="littleTreeBgUrl" :src="littleTreeBgUrl" class="w-full h-full object-cover" />
          <TreePine v-else class="w-8 h-8 text-gray-300" />
        </div>
        <div class="flex-1">
          <p class="text-sm text-gray-600 mb-3">上传"我的小树"页面的背景图，建议与芒果园风格一致</p>
          <div class="flex items-center gap-3">
            <label class="btn-outline text-sm cursor-pointer flex items-center gap-2">
              <Upload class="w-4 h-4" /> 选择图片
              <input type="file" accept="image/*" class="hidden" @change="handleLittleTreeBgUpload" />
            </label>
            <button v-if="littleTreeBgUrl" class="btn-outline text-sm text-red-500 flex items-center gap-2" @click="removeLittleTreeBg">
              <Trash2 class="w-4 h-4" /> 移除
            </button>
            <span v-if="littleTreeBgUploading" class="text-sm text-gray-400">上传中...</span>
            <span v-else-if="littleTreeBgUrl" class="text-sm text-green-600 flex items-center gap-1"><CheckCircle class="w-4 h-4" /> 已设置</span>
          </div>
          <p v-if="littleTreeBgMsg" class="text-xs mt-2" :class="littleTreeBgMsg.startsWith('✓') ? 'text-green-600' : 'text-red-500'">{{ littleTreeBgMsg }}</p>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Users, PenTool, Gift, FileText, ShieldCheck, Upload, Image, CheckCircle, FolderOpen, TreePine, Trash2 } from 'lucide-vue-next'

const stats = ref([
  { label: '艺人', value: 0, icon: Users },
  { label: '照片', value: 0, icon: FileText },
  { label: '视频', value: 0, icon: PenTool },
  { label: '用户', value: 0, icon: Gift },
])

const loading = ref(true)

// 封面上传
const coverUrl = ref('')
const uploading = ref(false)
const uploadMsg = ref('')

async function handleCoverUpload(e) {
  const file = e.target.files?.[0]
  if (!file) return
  const token = getToken()
  if (!token) {
    uploadMsg.value = '请先登录（localStorage 中无 token）'
    return
  }
  uploading.value = true
  uploadMsg.value = `调试: token=${token.substring(0, 30)}...`
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await fetch('/api/files/upload', {
      method: 'POST', headers: { Authorization: `Bearer ${token}` }, body: formData
    })
    if (!res.ok) {
      uploadMsg.value = `上传失败(${res.status})`
      return
    }
    const json = await res.json()
    if (json.code !== 200) {
      uploadMsg.value = json.msg || '上传失败'
      return
    }
    coverUrl.value = json.data.url
    const artistRes = await fetch('/api/public/artists')
    const artistJson = await artistRes.json()
    if (artistJson.code !== 200 || !artistJson.data?.length) {
      uploadMsg.value = '图片已上传，但未找到艺人信息'
      return
    }
    const artist = artistJson.data[0]
    const updateRes = await fetch(`/api/files/cover/${artist.id}?coverUrl=${encodeURIComponent(coverUrl.value)}`, {
      method: 'PUT'
    })
    if (updateRes.ok) {
      uploadMsg.value = '上传成功！封面已更新'
    } else {
      const err = await updateRes.json().catch(() => ({}))
      uploadMsg.value = `保存失败(${updateRes.status}): ${err.msg || ''}`
    }
  } catch {
    uploadMsg.value = '上传失败，请检查网络'
  } finally { uploading.value = false }
}

function getToken() {
  return localStorage.getItem('mangrove_token')
}

// 芒果园背景图
const treeBgUrl = ref('')
const treeBgUploading = ref(false)
const treeBgMsg = ref('')

async function fetchTreeBg() {
  try {
    const res = await fetch('/api/admin/config/tree_background_url', {
      headers: { Authorization: `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) treeBgUrl.value = json.data
  } catch {}
}

async function handleTreeBgUpload(e) {
  const file = e.target.files?.[0]
  if (!file) return
  treeBgUploading.value = true
  treeBgMsg.value = ''
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await fetch('/api/files/upload', {
      method: 'POST',
      headers: { Authorization: `Bearer ${getToken()}` },
      body: formData
    })
    const json = await res.json()
    if (json.code !== 200) throw new Error(json.msg || '上传失败')
    const url = json.data.url
    await fetch('/api/admin/config/tree_background_url', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` },
      body: JSON.stringify({ value: url })
    })
    treeBgUrl.value = url
    treeBgMsg.value = '✓ 背景图已更新'
    setTimeout(() => { treeBgMsg.value = '' }, 3000)
  } catch (err) {
    treeBgMsg.value = '✗ ' + err.message
  } finally {
    treeBgUploading.value = false
    e.target.value = ''
  }
}

async function removeTreeBg() {
  if (!confirm('确认移除芒果园背景图？')) return
  try {
    await fetch('/api/admin/config/tree_background_url', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` },
      body: JSON.stringify({ value: '' })
    })
    treeBgUrl.value = ''
    treeBgMsg.value = '✓ 背景图已移除'
    setTimeout(() => { treeBgMsg.value = '' }, 3000)
  } catch (err) {
    treeBgMsg.value = '✗ ' + err.message
  }
}

// 小树背景图
const littleTreeBgUrl = ref('')
const littleTreeBgUploading = ref(false)
const littleTreeBgMsg = ref('')

async function fetchLittleTreeBg() {
  try {
    const res = await fetch('/api/admin/config/littletree_background_url', {
      headers: { Authorization: `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) littleTreeBgUrl.value = json.data
  } catch {}
}

async function handleLittleTreeBgUpload(e) {
  const file = e.target.files?.[0]
  if (!file) return
  littleTreeBgUploading.value = true
  littleTreeBgMsg.value = ''
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await fetch('/api/files/upload', {
      method: 'POST',
      headers: { Authorization: `Bearer ${getToken()}` },
      body: formData
    })
    const json = await res.json()
    if (json.code !== 200) throw new Error(json.msg || '上传失败')
    const url = json.data.url
    await fetch('/api/admin/config/littletree_background_url', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` },
      body: JSON.stringify({ value: url })
    })
    littleTreeBgUrl.value = url
    littleTreeBgMsg.value = '✓ 背景图已更新'
    setTimeout(() => { littleTreeBgMsg.value = '' }, 3000)
  } catch (err) {
    littleTreeBgMsg.value = '✗ ' + err.message
  } finally {
    littleTreeBgUploading.value = false
    e.target.value = ''
  }
}

async function removeLittleTreeBg() {
  if (!confirm('确认移除小树背景图？')) return
  try {
    await fetch('/api/admin/config/littletree_background_url', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` },
      body: JSON.stringify({ value: '' })
    })
    littleTreeBgUrl.value = ''
    littleTreeBgMsg.value = '✓ 背景图已移除'
    setTimeout(() => { littleTreeBgMsg.value = '' }, 3000)
  } catch (err) {
    littleTreeBgMsg.value = '✗ ' + err.message
  }
}

onMounted(async () => {
  fetchTreeBg()
  fetchLittleTreeBg()
  try {
    const res = await fetch('/api/admin/dashboard', {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      const d = json.data
      if (d.stats) {
        stats.value = [
          { label: '艺人', value: d.stats.artistCount || 0, icon: Users },
          { label: '照片', value: d.stats.photoCount || 0, icon: FileText },
          { label: '视频', value: d.stats.videoCount || 0, icon: PenTool },
          { label: '用户', value: d.stats.userCount || 0, icon: Gift },
        ]
      }
    }
  } catch {
    // 保持默认 0
  } finally {
    loading.value = false
  }
})

</script>
