<template>
  <div>
    <h2 class="text-lg font-semibold text-gray-900 mb-6">艺人信息</h2>

    <div v-if="loading" class="text-center py-8">
      <p class="text-gray-400">加载中...</p>
    </div>

    <div v-else class="space-y-6">
      <!-- Images Section -->
      <div class="card p-6">
        <h3 class="text-md font-semibold text-gray-900 mb-4">图片管理</h3>
        <div class="grid grid-cols-2 gap-6">
          <!-- Signature Image -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">签名图</label>
            <div class="relative w-full h-32 rounded-xl border-2 border-dashed border-gray-300 overflow-hidden hover:border-mangrove-500 transition-colors cursor-pointer">
              <input type="file" accept="image/*" class="absolute inset-0 w-full h-full opacity-0 cursor-pointer" @change="e => handleImageUpload(e, 'signature')" />
              <div v-if="artist.signatureImageUrl" class="w-full h-full">
                <img :src="artist.signatureImageUrl" class="w-full h-full object-cover" />
              </div>
              <div v-else class="flex flex-col items-center justify-center h-full text-gray-400">
                <Upload class="w-6 h-6" />
                <span class="text-xs mt-1">点击上传签名图</span>
              </div>
            </div>
          </div>

          <!-- Brand Image -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">品牌标识</label>
            <div class="relative w-full h-32 rounded-xl border-2 border-dashed border-gray-300 overflow-hidden hover:border-mangrove-500 transition-colors cursor-pointer">
              <input type="file" accept="image/*" class="absolute inset-0 w-full h-full opacity-0 cursor-pointer" @change="e => handleImageUpload(e, 'brand')" />
              <div v-if="artist.brandImageUrl" class="w-full h-full">
                <img :src="artist.brandImageUrl" class="w-full h-full object-cover" />
              </div>
              <div v-else class="flex flex-col items-center justify-center h-full text-gray-400">
                <Upload class="w-6 h-6" />
                <span class="text-xs mt-1">点击上传品牌标识</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Carousel Photos -->
      <div class="card p-6 mt-4">
        <h3 class="text-md font-semibold text-gray-900 mb-4">轮播头像</h3>
        <p class="text-xs text-gray-500 mb-3">上传多张照片作为艺人页面轮播展示</p>
        <div class="flex flex-wrap gap-3">
          <div v-for="(url, i) in avatarImages" :key="i"
            class="relative w-20 h-20 rounded-lg border-2 border-gray-200 overflow-hidden">
            <img :src="url" class="w-full h-full object-cover" />
            <button @click="removeAvatarImage(i)"
              class="absolute top-0 right-0 w-5 h-5 bg-red-500 rounded-full flex items-center justify-center text-white text-xs hover:bg-red-600">×</button>
          </div>
          <label class="w-20 h-20 rounded-lg border-2 border-dashed border-gray-300 flex flex-col items-center justify-center cursor-pointer hover:border-mangrove-500 transition-colors">
            <input type="file" accept="image/*" class="hidden" @change="addAvatarImage" />
            <Upload class="w-6 h-6 text-gray-400" />
            <span class="text-[10px] text-gray-400 mt-1">添加</span>
          </label>
        </div>
      </div>

      <!-- Bio Q&A Section -->
      <div class="card p-6">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-md font-semibold text-gray-900">详细介绍</h3>
          <button class="btn-primary text-sm" @click="showAddModal = true">
            + 添加组件
          </button>
        </div>

        <div v-if="answeredSections.length === 0" class="text-center py-8 text-gray-400">
          暂无内容，点击"添加组件"开始创建
        </div>

        <div v-else class="space-y-4">
          <div v-for="section in answeredSections" :key="section.id" class="border border-gray-200 rounded-xl p-4 hover:border-mangrove-300 transition-colors">
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <h4 class="font-medium text-mangrove-700 mb-2">{{ section.question }}</h4>
                <p class="text-gray-700 text-sm leading-relaxed">{{ section.answer }}</p>
              </div>
              <div class="flex items-center gap-2 ml-4">
                <button
                  class="p-1.5 rounded-lg hover:bg-blue-50 text-gray-400 hover:text-blue-600 transition-colors"
                  title="编辑"
                  @click="editBio(section)"
                >
                  <Edit2 class="w-4 h-4" />
                </button>
                <button
                  class="p-1.5 rounded-lg hover:bg-yellow-50 text-gray-400 hover:text-yellow-600 transition-colors"
                  :title="section.status === 1 ? '隐藏' : '显示'"
                  @click="toggleStatus(section)"
                >
                  <Eye v-if="section.status === 1" class="w-4 h-4" />
                  <EyeOff v-else class="w-4 h-4" />
                </button>
                <button
                  class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-colors"
                  title="删除"
                  @click="deleteBio(section)"
                >
                  <Trash2 class="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 用户提问 -->
      <div class="card p-6 mt-4">
        <h3 class="text-md font-semibold text-gray-900 mb-4">💬 用户提问</h3>
        <p class="text-xs text-gray-500 mb-3">普通用户在艺人页面提交的问题，回答后将显示在详细介绍区域</p>

        <div v-if="userQuestions.length === 0" class="text-center py-6 text-gray-400 text-sm">
          暂无待回答的提问
        </div>

        <div v-else class="space-y-3">
          <div v-for="q in userQuestions" :key="q.id" class="border border-amber-200 bg-amber-50 rounded-xl p-4">
            <p class="text-sm font-medium text-gray-800 mb-2">
              ❓ {{ q.question }}
              <span v-if="q.askerNickname" class="text-xs text-gray-400 ml-2">—— 来自 {{ q.askerNickname }}</span>
              <span v-else class="text-xs text-gray-400 ml-2">—— 来自匿名用户</span>
            </p>
            <div class="flex items-center gap-2">
              <input v-model="q._answer" placeholder="填写回答..."
                class="flex-1 rounded-lg border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" />
              <button @click="answerQuestion(q)" class="btn-primary text-sm px-4 py-2">回答</button>
              <button @click="deleteBio(q)" class="p-2 text-red-400 hover:text-red-600" title="删除"><Trash2 class="w-4 h-4" /></button>
            </div>
          </div>
        </div>
      </div>

      <!-- Add/Edit Modal -->
      <div v-if="showAddModal || editingBio" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="closeModal">
        <div class="card p-6 w-full max-w-lg mx-4">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">{{ editingBio ? '编辑组件' : '添加组件' }}</h3>
          <form @submit.prevent="saveBio">
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">问题（标题）</label>
                <input
                  v-model="formData.question"
                  type="text"
                  class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500"
                  placeholder="例如：为什么想要成为偶像？"
                  required
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">回答（内容）</label>
                <textarea
                  v-model="formData.answer"
                  rows="4"
                  class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 resize-none"
                  placeholder="输入回答内容..."
                  required
                ></textarea>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">排序权重</label>
                <input
                  v-model.number="formData.sortOrder"
                  type="number"
                  class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500"
                />
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
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Upload, Edit2, Eye, EyeOff, Trash2 } from 'lucide-vue-next'

const artist = ref({})
const bioSections = ref([])
const saving = ref(false)
const loading = ref(true)
const showAddModal = ref(false)
const editingBio = ref(null)
const avatarImages = ref([])

const userQuestions = computed(() => bioSections.value.filter(s => !s.answer || s.answer.trim() === '').map(q => ({ ...q, _answer: q._answer || '' })))
const answeredSections = computed(() => bioSections.value.filter(s => s.answer && s.answer.trim() !== ''))

const formData = ref({
  question: '',
  answer: '',
  sortOrder: 0
})

function getToken() {
  return localStorage.getItem('mangrove_token')
}

async function fetchArtist() {
  loading.value = true
  try {
    const res = await fetch('/api/admin/artist', {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      artist.value = json.data
      if (json.data.avatarImages) {
        avatarImages.value = Array.isArray(json.data.avatarImages) ? json.data.avatarImages : []
      }
    }
  } catch (e) {
    console.error('获取艺人信息失败:', e)
  } finally {
    loading.value = false
  }
}

async function fetchBioSections() {
  try {
    const res = await fetch('/api/admin/artist-bio', {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      bioSections.value = json.data
    }
  } catch (e) {
    console.error('获取详细介绍失败:', e)
  }
}

async function addAvatarImage(e) {
  const file = e.target.files[0]
  if (!file) return
  const uploadData = new FormData()
  uploadData.append('file', file)
  try {
    const res = await fetch('/api/files/upload', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getToken()}` },
      body: uploadData
    })
    const json = await res.json()
    if (json.code === 200 && json.data?.url) {
      avatarImages.value.push(json.data.url)
      artist.value.avatarImages = avatarImages.value
      await saveArtist()
    }
  } catch (e) {
    console.error('上传图片失败:', e)
  }
}

function removeAvatarImage(idx) {
  avatarImages.value.splice(idx, 1)
  artist.value.avatarImages = avatarImages.value
  saveArtist()
}

async function handleImageUpload(e, type) {
  const file = e.target.files[0]
  if (!file) return

  const uploadData = new FormData()
  uploadData.append('file', file)

  try {
    const res = await fetch('/api/files/upload', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getToken()}` },
      body: uploadData
    })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      const url = json.data.url || json.data
      if (type === 'avatar') artist.value.avatarUrl = url
      else if (type === 'signature') artist.value.signatureImageUrl = url
      else if (type === 'brand') artist.value.brandImageUrl = url

      await saveArtist()
    }
  } catch (e) {
    console.error('上传图片失败:', e)
  }
}

async function saveArtist() {
  try {
    const res = await fetch(`/api/admin/artist/${artist.value.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify(artist.value)
    })
    const json = await res.json()
    if (json.code !== 200) {
      console.error('保存失败:', json.msg)
    }
  } catch (e) {
    console.error('保存失败:', e)
  }
}

function editBio(section) {
  editingBio.value = section
  formData.value = {
    question: section.question,
    answer: section.answer,
    sortOrder: section.sortOrder || 0
  }
}

function closeModal() {
  showAddModal.value = false
  editingBio.value = null
  formData.value = { question: '', answer: '', sortOrder: 0 }
}

async function saveBio() {
  saving.value = true
  try {
    const url = editingBio.value
      ? `/api/admin/artist-bio/${editingBio.value.id}`
      : '/api/admin/artist-bio'
    const method = editingBio.value ? 'PUT' : 'POST'

    const res = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify(formData.value)
    })
    const json = await res.json()
    if (json.code === 200) {
      closeModal()
      await fetchBioSections()
    }
  } catch (e) {
    console.error('保存失败:', e)
  } finally {
    saving.value = false
  }
}

async function toggleStatus(section) {
  const newStatus = section.status === 1 ? 0 : 1
  try {
    const res = await fetch(`/api/admin/artist-bio/${section.id}/status?status=${newStatus}`, {
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      section.status = newStatus
    }
  } catch (e) {
    console.error('更新状态失败:', e)
  }
}

async function answerQuestion(q) {
  if (!q._answer || !q._answer.trim()) return alert('请填写回答')
  try {
    const res = await fetch(`/api/admin/artist-bio/${q.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${getToken()}` },
      body: JSON.stringify({ question: q.question, answer: q._answer.trim() })
    })
    const json = await res.json()
    if (json.code === 200) {
      const idx = bioSections.value.findIndex(s => s.id === q.id)
      if (idx !== -1) bioSections.value[idx] = json.data
      alert('回答成功！')
    }
  } catch (e) {
    console.error('回答失败:', e)
  }
}

async function deleteBio(section) {
  if (!confirm('确认删除此组件？')) return
  try {
    const res = await fetch(`/api/admin/artist-bio/${section.id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      await fetchBioSections()
    }
  } catch (e) {
    console.error('删除失败:', e)
  }
}

onMounted(() => {
  fetchArtist()
  fetchBioSections()
})
</script>
