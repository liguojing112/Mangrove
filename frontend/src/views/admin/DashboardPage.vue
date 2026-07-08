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

    <!-- 最近上传 -->
    <div class="card">
      <div class="px-5 py-4 border-b border-gray-100 flex items-center justify-between">
        <h2 class="text-sm font-semibold text-gray-800">最近上传</h2>
        <span class="text-xs text-gray-400">{{ recentFiles.length }} 条</span>
      </div>
      <div v-if="recentFiles.length > 0" class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead>
            <tr class="bg-gray-50">
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-5 py-3">标题</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-5 py-3">分类</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-5 py-3">日期</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="file in recentFiles"
              :key="file.id"
              class="border-b border-gray-100 hover:bg-gray-50"
            >
              <td class="px-5 py-3 text-gray-800">{{ file.title }}</td>
              <td class="px-5 py-3">
                <span class="tag">{{ getCategoryLabel(file.category) }}</span>
              </td>
              <td class="px-5 py-3 text-gray-500">{{ formatDate(file.createdAt) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-else class="px-5 py-12 text-center text-sm text-gray-400">
        暂无上传内容，请先在「媒体管理」中添加照片或视频
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Users, PenTool, Gift, FileText, ShieldCheck, Upload, Image, CheckCircle, FolderOpen } from 'lucide-vue-next'

const stats = ref([
  { label: '艺人', value: 0, icon: Users },
  { label: '照片', value: 0, icon: FileText },
  { label: '视频', value: 0, icon: PenTool },
  { label: '用户', value: 0, icon: Gift },
])

const recentFiles = ref([])
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

onMounted(async () => {
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
      if (d.recentUploads) {
        recentFiles.value = d.recentUploads
      }
    }
  } catch {
    // 保持默认 0
  } finally {
    loading.value = false
  }
})

function getCategoryLabel(cat) {
  const map = { PHOTO: '照片', VIDEO: '视频', WORK: '作品' }
  return map[cat] || cat
}

function formatDate(d) {
  if (!d) return '-'
  return d.substring(0, 10)
}
</script>
