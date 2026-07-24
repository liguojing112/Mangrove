<template>
  <div class="min-w-0">
    <div class="flex flex-wrap items-center justify-between gap-2 mb-6">
      <h2 class="text-lg font-semibold text-gray-900">创作管理</h2>
      <div class="flex items-center gap-2">
        <span v-if="activeView === 'works'" class="text-sm text-gray-500">共 {{ total }} 条</span>
      </div>
    </div>

    <!-- Main Tabs -->
    <div class="flex gap-2 mb-6">
      <button
        class="px-4 py-2 rounded-lg text-sm font-medium transition-colors"
        :class="activeView === 'works' ? 'bg-mangrove-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
        @click="activeView = 'works'"
      >
        作品审核
      </button>
      <button
        class="px-4 py-2 rounded-lg text-sm font-medium transition-colors"
        :class="activeView === 'sections' ? 'bg-mangrove-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
        @click="activeView = 'sections'"
      >
        创作页面管理
      </button>
    </div>

    <!-- Works Management View -->
    <template v-if="activeView === 'works'">
      <!-- Status Tabs -->
      <div class="flex gap-2 mb-6">
        <button
          v-for="tab in statusTabs"
          :key="tab.value"
          class="px-4 py-2 rounded-lg text-sm font-medium transition-colors"
          :class="activeTab === tab.value ? 'bg-mangrove-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
          @click="switchTab(tab.value)"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- Works Table -->
      <div class="card overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="bg-gray-50">
                <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">#</th>
                <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">标题</th>
                <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">分类</th>
                <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">提交者</th>
                <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">状态</th>
                <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">提交时间</th>
                <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(work, idx) in works" :key="work.id" class="border-b border-gray-100 hover:bg-gray-50">
                <td class="px-4 py-3 text-gray-400">{{ (currentPage - 1) * pageSize + idx + 1 }}</td>
                <td class="px-4 py-3 font-medium text-gray-900 max-w-[200px] truncate">{{ work.title }}</td>
                <td class="px-4 py-3">
                  <span class="px-2 py-1 rounded-full text-xs" :class="categoryClass(work.category)">
                    {{ categoryLabel(work.category) }}
                  </span>
                </td>
                <td class="px-4 py-3 text-gray-600">{{ work.creator?.nickname || '未知' }}</td>
                <td class="px-4 py-3">
                  <span class="px-2 py-1 rounded-full text-xs" :class="statusClass(work.status)">
                    {{ statusLabel(work.status) }}
                  </span>
                </td>
                <td class="px-4 py-3 text-gray-500 text-xs">{{ formatDate(work.createdAt) }}</td>
                <td class="px-4 py-3">
                  <div class="flex items-center gap-2">
                    <button
                      v-if="work.status === 'PENDING'"
                      class="p-1.5 rounded-lg hover:bg-green-50 text-gray-400 hover:text-green-600 transition-colors"
                      title="通过"
                      @click="approveWork(work)"
                    >
                      <CheckCircle class="w-4 h-4" />
                    </button>
                    <button
                      v-if="work.status === 'PENDING'"
                      class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-colors"
                      title="拒绝"
                      @click="rejectWork(work)"
                    >
                      <XCircle class="w-4 h-4" />
                    </button>
                    <button
                      v-if="work.status === 'PUBLISHED'"
                      class="p-1.5 rounded-lg hover:bg-yellow-50 text-gray-400 hover:text-yellow-600 transition-colors"
                      title="精选"
                      @click="featureWork(work)"
                    >
                      <Star class="w-4 h-4" />
                    </button>
                    <button
                      v-if="work.status === 'FEATURED'"
                      class="p-1.5 rounded-lg hover:bg-gray-100 text-yellow-500 hover:text-gray-500 transition-colors"
                      title="取消精选"
                      @click="unfeatureWork(work)"
                    >
                      <StarOff class="w-4 h-4" />
                    </button>
                    <button
                      class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-colors"
                      title="删除"
                      @click="deleteWork(work)"
                    >
                      <Trash2 class="w-4 h-4" />
                    </button>
                  </div>
                </td>
              </tr>
              <tr v-if="works.length === 0">
                <td colspan="7" class="px-4 py-12 text-center text-sm text-gray-400">
                  暂无数据
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
    </template>

    <!-- Work Sections Management View -->
    <template v-if="activeView === 'sections'">
      <!-- Section Tabs -->
      <div class="flex gap-2 mb-6">
        <button
          v-for="tab in sectionTabs"
          :key="tab.value"
          class="px-4 py-2 rounded-lg text-sm font-medium transition-colors"
          :class="activeSection === tab.value ? 'bg-mangrove-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
          @click="switchSection(tab.value)"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- Add Item Button -->
      <div class="mb-4">
        <button class="btn-primary flex items-center gap-2 text-sm" @click="showAddModal = true">
          <Plus class="w-4 h-4" />
          添加{{ currentSectionLabel }}
        </button>
      </div>

      <!-- Section Items List -->
      <div class="card overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="bg-gray-50">
                <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">#</th>
                <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">类型</th>
                <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">内容</th>
                <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">排序</th>
                <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, idx) in sectionItems" :key="item.id" class="border-b border-gray-100 hover:bg-gray-50">
                <td class="px-4 py-3 text-gray-400">{{ idx + 1 }}</td>
                <td class="px-4 py-3">
                  <span class="px-2 py-1 rounded-full text-xs" :class="item.itemType === 'WORK' ? 'bg-blue-100 text-blue-700' : 'bg-purple-100 text-purple-700'">
                    {{ item.itemType === 'WORK' ? '作品' : '创作者' }}
                  </span>
                </td>
                <td class="px-4 py-3 text-gray-800">
                  <div v-if="item.itemType === 'WORK'">
                    <p class="font-medium">{{ getWorkTitle(item.targetId) }}</p>
                    <p class="text-xs text-gray-500">{{ getWorkCreator(item.targetId) }}</p>
                  </div>
                  <div v-else>
                    <p class="font-medium">{{ getCreatorName(item.targetId) }}</p>
                    <p class="text-xs text-gray-500">{{ getCreatorUsername(item.targetId) }}</p>
                  </div>
                </td>
                <td class="px-4 py-3">
                  <input
                    v-model.number="item.sortOrder"
                    type="number"
                    class="w-20 px-2 py-1 border border-gray-200 rounded text-sm"
                    @change="updateSortOrder(item)"
                  />
                </td>
                <td class="px-4 py-3">
                  <button
                    class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-colors"
                    title="移除"
                    @click="removeSectionItem(item)"
                  >
                    <Trash2 class="w-4 h-4" />
                  </button>
                </td>
              </tr>
              <tr v-if="sectionItems.length === 0">
                <td colspan="5" class="px-4 py-12 text-center text-sm text-gray-400">
                  暂无数据，请点击"添加"按钮
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>

    <!-- Add Section Item Modal -->
    <div v-if="showAddModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4" @click.self="showAddModal = false">
      <div class="bg-white rounded-2xl p-6 w-full max-w-lg max-h-[80vh] overflow-y-auto">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">添加{{ currentSectionLabel }}</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">选择类型</label>
            <div class="flex gap-2">
              <button
                class="px-4 py-2 rounded-lg text-sm font-medium transition-colors"
                :class="addForm.itemType === 'WORK' ? 'bg-mangrove-600 text-white' : 'bg-gray-100 text-gray-600'"
                @click="addForm.itemType = 'WORK'"
              >
                作品
              </button>
              <button
                v-if="activeSection !== 'PREVIEW'"
                class="px-4 py-2 rounded-lg text-sm font-medium transition-colors"
                :class="addForm.itemType === 'CREATOR' ? 'bg-mangrove-600 text-white' : 'bg-gray-100 text-gray-600'"
                @click="addForm.itemType = 'CREATOR'"
              >
                创作者
              </button>
            </div>
          </div>

          <div v-if="addForm.itemType === 'WORK'">
            <label class="block text-sm font-medium text-gray-700 mb-2">选择作品</label>
            <div class="max-h-60 overflow-y-auto border border-gray-200 rounded-lg">
              <div v-for="work in availableWorks" :key="work.id"
                class="flex items-center gap-3 p-3 cursor-pointer hover:bg-gray-50 border-b border-gray-100 last:border-0 transition-colors"
                :class="addForm.targetId === work.id ? 'bg-mangrove-50 border-mangrove-200' : ''"
                @click="addForm.targetId = work.id">
                <img v-if="work.fileUrl" :src="work.fileUrl" class="w-12 h-12 rounded object-cover bg-gray-100" />
                <div v-else class="w-12 h-12 rounded bg-gray-100 flex items-center justify-center text-gray-400">🖼️</div>
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium text-gray-900 truncate">{{ work.title }}</p>
                  <p class="text-xs text-gray-500">{{ work.creator?.nickname || '匿名' }}</p>
                </div>
                <span v-if="addForm.targetId === work.id" class="text-mangrove-600">✓</span>
              </div>
              <div v-if="availableWorks.length === 0" class="p-4 text-center text-sm text-gray-400">
                暂无已审核的作品
              </div>
            </div>
          </div>

          <div v-if="addForm.itemType === 'CREATOR'">
            <label class="block text-sm font-medium text-gray-700 mb-2">选择创作者</label>
            <select v-model.number="addForm.targetId" class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm">
              <option value="">请选择创作者</option>
              <option v-for="user in availableCreators" :key="user.id" :value="user.id">
                {{ user.nickname || user.username }} ({{ user.username }})
              </option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">排序（数字越小越靠前）</label>
            <input v-model.number="addForm.sortOrder" type="number" class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm" />
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <button class="btn-outline text-sm" @click="showAddModal = false">取消</button>
          <button class="btn-primary text-sm" @click="addSectionItem">添加</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { CheckCircle, XCircle, Trash2, Star, StarOff, Plus } from 'lucide-vue-next'

const currentUser = JSON.parse(localStorage.getItem('mangrove_user') || 'null')
const isSuperAdmin = computed(() => currentUser?.role === 'SUPER_ADMIN')

const activeView = ref('works')
const works = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(15)
const activeTab = ref('PENDING')

const statusTabs = [
  { label: '待审核', value: 'PENDING' },
  { label: '已通过', value: 'PUBLISHED' },
  { label: '已拒绝', value: 'HIDDEN' },
]

// Work Section Management
const activeSection = ref('PREVIEW')
const sectionItems = ref([])
const availableWorks = ref([])
const availableCreators = ref([])
const allWorks = ref([])
const allCreators = ref([])
const showAddModal = ref(false)
const addForm = ref({ itemType: 'WORK', targetId: '', sortOrder: 0 })

const sectionTabs = [
  { label: '高清路透专区', value: 'PREVIEW' },
  { label: '优秀产出创作者榜', value: 'RANK' },
  { label: '精选作品展厅', value: 'SHOWCASE' },
  { label: '创作者荣誉墙', value: 'HONOR' }
]

const currentSectionLabel = computed(() => {
  const tab = sectionTabs.find(t => t.value === activeSection.value)
  return tab ? tab.label : ''
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
  return localStorage.getItem('mangrove_token') || ''
}

async function fetchWorks() {
  try {
    let url = `/api/admin/works?page=${currentPage.value - 1}&size=${pageSize.value}&status=${activeTab.value}`
    const res = await fetch(url, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      works.value = json.data.content || []
      total.value = json.data.totalElements || 0
    }
  } catch (e) {
    console.error('获取作品失败:', e)
  }
}

function switchTab(tab) {
  activeTab.value = tab
  currentPage.value = 1
  fetchWorks()
}

function goPage(p) {
  if (p < 1 || p > totalPages.value) return
  currentPage.value = p
  fetchWorks()
}

async function approveWork(work) {
  try {
    const res = await fetch(`/api/admin/works/${work.id}/approve`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      alert('已通过')
      fetchWorks()
    }
  } catch (e) {
    alert('操作失败')
  }
}

async function rejectWork(work) {
  try {
    const res = await fetch(`/api/admin/works/${work.id}/reject`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      alert('已拒绝')
      fetchWorks()
    }
  } catch (e) {
    alert('操作失败')
  }
}

async function featureWork(work) {
  try {
    const res = await fetch(`/api/admin/works/${work.id}/feature`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      alert('已设为精选')
      fetchWorks()
    }
  } catch (e) {
    alert('操作失败')
  }
}

async function unfeatureWork(work) {
  try {
    const res = await fetch(`/api/admin/works/${work.id}/unfeature`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      alert('已取消精选')
      fetchWorks()
    }
  } catch (e) {
    alert('操作失败')
  }
}

async function deleteWork(work) {
  if (!confirm('确认删除该作品？')) return
  try {
    const res = await fetch(`/api/admin/works/${work.id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      alert('已删除')
      fetchWorks()
    }
  } catch (e) {
    alert('删除失败')
  }
}

function categoryLabel(cat) {
  const map = { PREVIEW: '路透图', EDITED_PHOTO: '剪辑修图', VIDEO_EDIT: '视频剪辑' }
  return map[cat] || cat
}

function categoryClass(cat) {
  const map = { PREVIEW: 'bg-teal-100 text-teal-700', EDITED_PHOTO: 'bg-purple-100 text-purple-700', VIDEO_EDIT: 'bg-blue-100 text-blue-700' }
  return map[cat] || 'bg-gray-100 text-gray-600'
}

function statusLabel(status) {
  const map = { PENDING: '待审核', PUBLISHED: '已通过', HIDDEN: '已拒绝', FEATURED: '精选' }
  return map[status] || status
}

function statusClass(status) {
  const map = { PENDING: 'bg-yellow-100 text-yellow-700', PUBLISHED: 'bg-green-100 text-green-700', HIDDEN: 'bg-red-100 text-red-700', FEATURED: 'bg-amber-100 text-amber-700' }
  return map[status] || 'bg-gray-100 text-gray-600'
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

// Work Section Management Functions
async function fetchSectionItems() {
  try {
    const res = await fetch(`/api/admin/work-sections/${activeSection.value}`, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      sectionItems.value = json.data || []
    }
  } catch (e) {
    console.error('获取板块数据失败:', e)
  }
}

async function fetchAvailableWorks() {
  try {
    const res = await fetch('/api/works?page=0&size=100', {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      allWorks.value = json.data.content || []
      availableWorks.value = allWorks.value.filter(w => w.status === 'PUBLISHED' || w.status === 'FEATURED')
    }
  } catch (e) {
    console.error('获取作品失败:', e)
  }
}

async function fetchAvailableCreators() {
  try {
    const res = await fetch('/api/admin/users?page=0&size=100', {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      allCreators.value = json.data.content || []
      availableCreators.value = allCreators.value
    }
  } catch (e) {
    console.error('获取用户失败:', e)
  }
}

function switchSection(section) {
  activeSection.value = section
  addForm.value.itemType = (section === 'RANK' || section === 'HONOR') ? 'CREATOR' : 'WORK'
  addForm.value.targetId = ''
  fetchSectionItems()
}

function buildExtraData(itemType, targetId) {
  if (itemType === 'CREATOR') {
    const user = allCreators.value.find(u => u.id === targetId)
    if (user) return { nickname: user.nickname || user.username, username: user.username, publicId: user.publicId }
  } else {
    const work = allWorks.value.find(w => w.id === targetId)
    if (work) return { title: work.title, category: work.category, fileUrl: work.fileUrl, creatorNickname: work.creator?.nickname || '匿名' }
  }
  return null
}

async function addSectionItem() {
  if (!addForm.value.targetId) {
    alert('请选择项目')
    return
  }
  try {
    const res = await fetch(`/api/admin/work-sections/${activeSection.value}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({
        itemType: addForm.value.itemType,
        targetId: addForm.value.targetId,
        sortOrder: addForm.value.sortOrder,
        extraData: buildExtraData(addForm.value.itemType, addForm.value.targetId)
      })
    })
    const json = await res.json()
    if (json.code === 200) {
      alert('添加成功')
      showAddModal.value = false
      addForm.value = { itemType: activeSection.value === 'PREVIEW' ? 'WORK' : 'CREATOR', targetId: '', sortOrder: 0 }
      fetchSectionItems()
    }
  } catch (e) {
    alert('添加失败')
  }
}

async function updateSortOrder(item) {
  try {
    await fetch(`/api/admin/work-sections/${item.id}/sort?sortOrder=${item.sortOrder}`, {
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
  } catch (e) {
    console.error('更新排序失败:', e)
  }
}

async function removeSectionItem(item) {
  if (!confirm('确认移除？')) return
  try {
    const res = await fetch(`/api/admin/work-sections/${item.id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      alert('已移除')
      fetchSectionItems()
    }
  } catch (e) {
    alert('移除失败')
  }
}

function getWorkTitle(workId) {
  const work = allWorks.value.find(w => w.id === workId)
  return work ? work.title : `作品#${workId}`
}

function getWorkCreator(workId) {
  const work = allWorks.value.find(w => w.id === workId)
  return work ? work.creator?.nickname || '匿名' : ''
}

function getCreatorName(userId) {
  const user = allCreators.value.find(u => u.id === userId)
  return user ? user.nickname || user.username : `用户#${userId}`
}

function getCreatorUsername(userId) {
  const user = allCreators.value.find(u => u.id === userId)
  return user ? user.username : ''
}

onMounted(() => {
  fetchWorks()
  fetchAvailableWorks()
  fetchAvailableCreators()
})

watch(activeView, (newView) => {
  if (newView === 'sections') {
    fetchSectionItems()
    fetchAvailableWorks()
  }
})
</script>
