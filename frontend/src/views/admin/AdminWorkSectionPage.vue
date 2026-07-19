<template>
  <div class="min-w-0">
    <div class="flex flex-wrap items-center justify-between gap-2 mb-6">
      <h2 class="text-lg font-semibold text-gray-900">创作页面管理</h2>
    </div>

    <!-- Tabs -->
    <div class="flex gap-2 mb-6">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        class="px-4 py-2 rounded-lg text-sm font-medium transition-colors"
        :class="activeTab === tab.value ? 'bg-mangrove-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
        @click="switchTab(tab.value)"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- Add Item Button -->
    <div class="mb-4">
      <button class="btn-primary flex items-center gap-2 text-sm" @click="showAddModal = true">
        <Plus class="w-4 h-4" />
        添加{{ currentTabLabel }}
      </button>
    </div>

    <!-- Items List -->
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
            <tr v-for="(item, idx) in items" :key="item.id" class="border-b border-gray-100 hover:bg-gray-50">
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
                  @click="removeItem(item)"
                >
                  <Trash2 class="w-4 h-4" />
                </button>
              </td>
            </tr>
            <tr v-if="items.length === 0">
              <td colspan="5" class="px-4 py-12 text-center text-sm text-gray-400">
                暂无数据，请点击"添加"按钮
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Add Modal -->
    <div v-if="showAddModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4" @click.self="showAddModal = false">
      <div class="bg-white rounded-2xl p-6 w-full max-w-lg max-h-[80vh] overflow-y-auto">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">添加{{ currentTabLabel }}</h3>
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
                v-if="activeTab !== 'PREVIEW'"
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
            <select v-model.number="addForm.targetId" class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm">
              <option value="">请选择作品</option>
              <option v-for="work in availableWorks" :key="work.id" :value="work.id">
                {{ work.title }} - {{ work.creator?.nickname || '匿名' }}
              </option>
            </select>
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
          <button class="btn-primary text-sm" @click="addItem">添加</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { Plus, Trash2 } from 'lucide-vue-next'

const activeTab = ref('PREVIEW')
const items = ref([])
const availableWorks = ref([])
const availableCreators = ref([])
const showAddModal = ref(false)
const allWorks = ref([])
const allCreators = ref([])

const addForm = ref({
  itemType: 'WORK',
  targetId: '',
  sortOrder: 0
})

const tabs = [
  { label: '高清路透专区', value: 'PREVIEW' },
  { label: '优秀产出创作者榜', value: 'RANK' },
  { label: '精选作品展厅', value: 'SHOWCASE' },
  { title: '创作者荣誉墙', value: 'HONOR' }
]

const currentTabLabel = computed(() => {
  const tab = tabs.find(t => t.value === activeTab.value)
  return tab ? tab.label : ''
})

function getToken() {
  return localStorage.getItem('mangrove_token') || ''
}

async function fetchItems() {
  try {
    const res = await fetch(`/api/admin/work-sections/${activeTab.value}`, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      items.value = json.data || []
    }
  } catch (e) {
    console.error('获取数据失败:', e)
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

function switchTab(tab) {
  activeTab.value = tab
  addForm.value.itemType = tab === 'PREVIEW' ? 'WORK' : 'CREATOR'
  addForm.value.targetId = ''
  fetchItems()
}

async function addItem() {
  if (!addForm.value.targetId) {
    alert('请选择项目')
    return
  }
  try {
    const res = await fetch(`/api/admin/work-sections/${activeTab.value}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({
        itemType: addForm.value.itemType,
        targetId: addForm.value.targetId,
        sortOrder: addForm.value.sortOrder
      })
    })
    const json = await res.json()
    if (json.code === 200) {
      alert('添加成功')
      showAddModal.value = false
      addForm.value = { itemType: activeTab.value === 'PREVIEW' ? 'WORK' : 'CREATOR', targetId: '', sortOrder: 0 }
      fetchItems()
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

async function removeItem(item) {
  if (!confirm('确认移除？')) return
  try {
    const res = await fetch(`/api/admin/work-sections/${item.id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      alert('已移除')
      fetchItems()
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
  fetchItems()
  fetchAvailableWorks()
  fetchAvailableCreators()
})
</script>
