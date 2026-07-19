<template>
  <div class="min-w-0">
    <div class="flex flex-wrap items-center justify-between gap-2 mb-6">
      <h2 class="text-lg font-semibold text-gray-900">来信管理</h2>
      <div class="flex items-center gap-3">
        <span class="text-sm text-gray-500">共 {{ letters.length }} 条</span>
        <button class="btn-secondary text-sm" @click="showCategoryModal = true">
          管理分类
        </button>
        <button class="btn-primary text-sm" @click="showCreateModal = true">
          + 添加来信
        </button>
      </div>
    </div>

    <!-- Category Management Modal -->
    <div v-if="showCategoryModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="showCategoryModal = false">
      <div class="card p-6 w-full max-w-lg mx-4">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-gray-900">分类管理</h3>
          <button class="btn-primary text-sm" @click="showAddCategory = true">+ 添加分类</button>
        </div>

        <div v-if="categories.length === 0" class="text-center py-8 text-gray-400">
          暂无分类
        </div>

        <div v-else class="space-y-3">
          <div v-for="cat in categories" :key="cat.id" class="flex items-center justify-between p-3 border border-gray-200 rounded-xl">
            <div class="flex items-center gap-3">
              <span class="px-2 py-1 rounded-full text-xs bg-gray-100 text-gray-700">{{ cat.code }}</span>
              <span class="text-sm text-gray-900">{{ cat.name }}</span>
            </div>
            <div class="flex items-center gap-2">
              <button
                class="p-1.5 rounded-lg hover:bg-yellow-50 text-gray-400 hover:text-yellow-600 transition-colors"
                @click="editCategory(cat)"
              >
                <Edit2 class="w-4 h-4" />
              </button>
              <button
                class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-colors"
                @click="deleteCategory(cat)"
              >
                <Trash2 class="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>

        <!-- Add/Edit Category Form -->
        <div v-if="showAddCategory || editingCategory" class="mt-4 p-4 bg-gray-50 rounded-xl">
          <h4 class="text-sm font-medium text-gray-700 mb-3">{{ editingCategory ? '编辑分类' : '添加分类' }}</h4>
          <div class="space-y-3">
            <input
              v-model="categoryForm.name"
              type="text"
              placeholder="分类名称（如：语录）"
              class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500"
            />
            <input
              v-model="categoryForm.code"
              type="text"
              placeholder="分类代码（如：QUOTE）"
              class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500"
            />
            <input
              v-model.number="categoryForm.sortOrder"
              type="number"
              placeholder="排序权重"
              class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500"
            />
            <div class="flex items-center gap-2">
              <button class="btn-primary text-sm" @click="saveCategory">保存</button>
              <button class="btn-secondary text-sm" @click="closeCategoryForm">取消</button>
            </div>
          </div>
        </div>

        <div class="flex justify-end mt-4">
          <button class="btn-secondary text-sm" @click="showCategoryModal = false">关闭</button>
        </div>
      </div>
    </div>

    <!-- Table -->
    <div class="card overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead>
            <tr class="bg-gray-50">
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">#</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">标题</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">分类</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">内容</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">来源</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">点赞</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">状态</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(letter, idx) in letters"
              :key="letter.id"
              class="border-b border-gray-100 hover:bg-gray-50"
            >
              <td class="px-4 py-3 text-gray-400">{{ idx + 1 }}</td>
              <td class="px-4 py-3 font-medium text-gray-900 max-w-xs">
                <div class="flex min-w-0 items-center gap-2">
                  <img v-if="letter.coverUrl" :src="letter.coverUrl" alt="" class="h-9 w-12 shrink-0 rounded object-cover" />
                  <span class="truncate">{{ letter.title }}</span>
                </div>
              </td>
              <td class="px-4 py-3">
                <span :class="categoryBadgeClass(letter.category)">
                  {{ getCategoryName(letter.category) }}
                </span>
              </td>
              <td class="px-4 py-3 text-gray-600 max-w-sm truncate">{{ letter.content }}</td>
              <td class="px-4 py-3 text-gray-500 text-xs">{{ letter.source || '-' }}</td>
              <td class="px-4 py-3 text-gray-600">{{ letter.likes }}</td>
              <td class="px-4 py-3">
                <span
                  class="px-2 py-1 rounded-full text-xs"
                  :class="letter.status === 1 ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'"
                >
                  {{ letter.status === 1 ? '显示' : '隐藏' }}
                </span>
              </td>
              <td class="px-4 py-3">
                <div class="flex items-center gap-2">
                  <button
                    class="p-1.5 rounded-lg hover:bg-yellow-50 text-gray-400 hover:text-yellow-600 transition-colors"
                    :title="letter.status === 1 ? '隐藏' : '显示'"
                    @click="toggleStatus(letter)"
                  >
                    <Eye v-if="letter.status === 1" class="w-4 h-4" />
                    <EyeOff v-else class="w-4 h-4" />
                  </button>
                  <button
                    class="p-1.5 rounded-lg hover:bg-blue-50 text-gray-400 hover:text-blue-600 transition-colors"
                    title="编辑"
                    @click="editLetter(letter)"
                  >
                    <Edit2 class="w-4 h-4" />
                  </button>
                  <button
                    class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-colors"
                    title="删除"
                    @click="deleteLetter(letter)"
                  >
                    <Trash2 class="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="letters.length === 0">
              <td colspan="8" class="px-4 py-12 text-center text-sm text-gray-400">
                暂无来信数据
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create/Edit Letter Modal -->
    <div v-if="showCreateModal || editingLetter" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="closeModal">
      <div class="card max-h-[92vh] w-full max-w-lg overflow-y-auto p-6 mx-4">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">{{ editingLetter ? '编辑来信' : '添加来信' }}</h3>
        <form @submit.prevent="saveLetter">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">标题</label>
              <input v-model="formData.title" type="text" class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" required />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">分类</label>
              <select v-model="formData.category" class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" required>
                <option v-for="cat in categories" :key="cat.id" :value="cat.code">{{ cat.name }}</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">内容</label>
              <textarea v-model="formData.content" rows="6" class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 resize-none" required></textarea>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">来源（可选）</label>
              <input v-model="formData.source" type="text" class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">日期</label>
              <input v-model="formData.noteDate" type="date" class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">图片（可选）</label>
              <div v-if="formData.coverUrl" class="mb-2 overflow-hidden rounded-xl border border-gray-200 bg-gray-50">
                <img :src="formData.coverUrl" alt="来信图片预览" class="max-h-56 w-full object-contain" />
              </div>
              <div class="flex flex-wrap items-center gap-2">
                <label class="btn-secondary inline-flex cursor-pointer items-center gap-2 text-sm">
                  <Upload class="h-4 w-4" />{{ uploadingImage ? '上传中...' : '上传图片' }}
                  <input type="file" accept="image/*" class="hidden" :disabled="uploadingImage" @change="uploadLetterImage" />
                </label>
                <button v-if="formData.coverUrl" type="button" class="px-3 py-2 text-sm text-gray-500 hover:text-red-500" @click="formData.coverUrl = ''">移除图片</button>
              </div>
              <p v-if="uploadError" class="mt-2 text-xs text-red-600">{{ uploadError }}</p>
            </div>
          </div>
          <div class="flex items-center gap-3 mt-6">
            <button type="submit" class="btn-primary text-sm" :disabled="saving || uploadingImage">{{ saving ? '保存中...' : '保存' }}</button>
            <button type="button" class="btn-secondary text-sm" @click="closeModal">取消</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Edit2, Eye, EyeOff, Trash2, Upload } from 'lucide-vue-next'

const letters = ref([])
const categories = ref([])
const showCreateModal = ref(false)
const editingLetter = ref(null)
const showCategoryModal = ref(false)
const showAddCategory = ref(false)
const editingCategory = ref(null)
const saving = ref(false)
const uploadingImage = ref(false)
const uploadError = ref('')

const formData = ref({
  title: '',
  category: '',
  content: '',
  source: '',
  noteDate: '',
  coverUrl: ''
})

const categoryForm = ref({
  name: '',
  code: '',
  sortOrder: 0
})

function getToken() {
  return localStorage.getItem('mangrove_token')
}

async function fetchLetters() {
  try {
    const res = await fetch('/api/admin/letters', {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      letters.value = json.data
    }
  } catch (e) {
    console.error('获取来信失败:', e)
  }
}

async function fetchCategories() {
  try {
    const res = await fetch('/api/admin/letter-categories', {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      categories.value = json.data
    }
  } catch (e) {
    console.error('获取分类失败:', e)
  }
}

function getCategoryName(code) {
  const cat = categories.value.find(c => c.code === code)
  return cat ? cat.name : code
}

function editLetter(letter) {
  editingLetter.value = letter
  formData.value = {
    title: letter.title,
    category: letter.category,
    content: letter.content,
    source: letter.source || '',
    noteDate: letter.noteDate || '',
    coverUrl: letter.coverUrl || ''
  }
}

function closeModal() {
  showCreateModal.value = false
  editingLetter.value = null
  uploadError.value = ''
  formData.value = { title: '', category: '', content: '', source: '', noteDate: '', coverUrl: '' }
}

async function uploadLetterImage(event) {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file) return
  uploadingImage.value = true
  uploadError.value = ''
  try {
    const body = new FormData()
    body.append('file', file)
    const res = await fetch('/api/files/upload', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getToken()}` },
      body
    })
    const json = await res.json()
    if (!res.ok || json.code !== 200) throw new Error(json.msg || '上传失败')
    formData.value.coverUrl = json.data.url
  } catch (e) {
    uploadError.value = e.message
  } finally {
    uploadingImage.value = false
  }
}

async function saveLetter() {
  saving.value = true
  try {
    const url = editingLetter.value ? `/api/admin/letters/${editingLetter.value.id}` : '/api/admin/letters'
    const method = editingLetter.value ? 'PUT' : 'POST'
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
      await fetchLetters()
    }
  } catch (e) {
    console.error('保存失败:', e)
  } finally {
    saving.value = false
  }
}

async function toggleStatus(letter) {
  const newStatus = letter.status === 1 ? 0 : 1
  try {
    const res = await fetch(`/api/admin/letters/${letter.id}/status?status=${newStatus}`, {
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      letter.status = newStatus
    }
  } catch (e) {
    console.error('更新状态失败:', e)
  }
}

async function deleteLetter(letter) {
  if (!confirm('确认删除此来信？')) return
  try {
    const res = await fetch(`/api/admin/letters/${letter.id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      await fetchLetters()
    }
  } catch (e) {
    console.error('删除失败:', e)
  }
}

function editCategory(cat) {
  editingCategory.value = cat
  categoryForm.value = { name: cat.name, code: cat.code, sortOrder: cat.sortOrder }
  showAddCategory.value = true
}

function closeCategoryForm() {
  showAddCategory.value = false
  editingCategory.value = null
  categoryForm.value = { name: '', code: '', sortOrder: 0 }
}

async function saveCategory() {
  try {
    const url = editingCategory.value ? `/api/admin/letter-categories/${editingCategory.value.id}` : '/api/admin/letter-categories'
    const method = editingCategory.value ? 'PUT' : 'POST'
    const res = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify(categoryForm.value)
    })
    const json = await res.json()
    if (json.code === 200) {
      closeCategoryForm()
      await fetchCategories()
    }
  } catch (e) {
    console.error('保存分类失败:', e)
  }
}

async function deleteCategory(cat) {
  if (!confirm('确认删除此分类？')) return
  try {
    const res = await fetch(`/api/admin/letter-categories/${cat.id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      await fetchCategories()
    }
  } catch (e) {
    console.error('删除分类失败:', e)
  }
}

function categoryBadgeClass(category) {
  const cat = categories.value.find(c => c.code === category)
  const colors = {
    QUOTE: 'bg-purple-100 text-purple-700',
    LETTER: 'bg-blue-100 text-blue-700',
    DIARY: 'bg-green-100 text-green-700',
    MONOLOGUE: 'bg-orange-100 text-orange-700',
  }
  return `px-2 py-1 rounded-full text-xs ${colors[category] || 'bg-gray-100 text-gray-600'}`
}

onMounted(() => {
  fetchLetters()
  fetchCategories()
})
</script>
