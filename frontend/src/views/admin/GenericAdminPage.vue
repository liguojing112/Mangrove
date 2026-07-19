<template>
  <div class="min-w-0">
    <!-- Header -->
    <div class="flex flex-wrap items-center justify-between gap-2 mb-6">
      <h2 class="text-lg font-semibold text-gray-900">{{ title }}</h2>
      <button class="btn-primary flex items-center gap-2 text-sm" @click="openCreate">
        <Plus class="w-4 h-4" />
        新增
      </button>
    </div>

    <!-- Search -->
    <div class="relative mb-6">
      <Search class="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
      <input
        v-model="searchQuery"
        type="text"
        placeholder="搜索..."
        class="w-full pl-11 pr-4 py-2.5 rounded-full border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent"
        @input="onSearch"
      />
    </div>

    <!-- Table -->
    <div class="card overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead>
            <tr class="bg-gray-50">
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">
                #
              </th>
              <th
                v-for="col in columns"
                :key="col.key"
                class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3"
              >
                {{ col.label }}
              </th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">
                操作
              </th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(row, idx) in rows"
              :key="row.id ?? idx"
              class="border-b border-gray-100 hover:bg-gray-50"
            >
              <td class="px-4 py-3 text-gray-400">{{ (currentPage - 1) * pageSize + idx + 1 }}</td>
              <td
                v-for="col in columns"
                :key="col.key"
                class="px-4 py-3 text-gray-800"
              >
                {{ row[col.key] }}
              </td>
              <td class="px-4 py-3">
                <div class="flex items-center gap-2">
                  <button
                    class="p-1.5 rounded-lg hover:bg-mangrove-50 text-gray-400 hover:text-mangrove-600 transition-colors"
                    title="编辑"
                    @click="openEdit(row)"
                  >
                    <Edit3 class="w-4 h-4" />
                  </button>
                  <button
                    class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-colors"
                    title="删除"
                    @click="onDelete(row)"
                  >
                    <Trash2 class="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="rows.length === 0">
              <td :colspan="columns.length + 2" class="px-4 py-12 text-center text-sm text-gray-400">
                暂无数据
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div class="flex items-center justify-between px-4 py-3 border-t border-gray-100">
        <span class="text-xs text-gray-500">
          共 {{ total }} 条，第 {{ currentPage }}/{{ totalPages }} 页
        </span>
        <div class="flex items-center gap-1">
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
            :class="
              p === currentPage
                ? 'bg-mangrove-700 text-white'
                : 'text-gray-600 hover:bg-gray-100'
            "
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

    <!-- Modal -->
    <div
      v-if="showModal"
      class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4"
      @click.self="showModal = false"
    >
      <div class="bg-white rounded-2xl p-6 w-full max-w-lg">
        <h3 class="text-base font-semibold text-gray-900 mb-5">
          {{ editingId ? '编辑' : '新增' }}
        </h3>
        <div class="space-y-4">
          <div v-for="field in formFields" :key="field.key">
            <label class="block text-sm font-medium text-gray-700 mb-1">{{ field.label }}</label>
            <textarea
              v-if="field.type === 'textarea'"
              v-model="formData[field.key]"
              rows="3"
              class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent resize-none"
            ></textarea>
            <select
              v-else-if="field.type === 'select'"
              v-model="formData[field.key]"
              class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent bg-white"
            >
              <option value="" disabled>请选择</option>
              <option
                v-for="opt in field.options"
                :key="opt.value"
                :value="opt.value"
              >
                {{ opt.label }}
              </option>
            </select>
            <input
              v-else
              v-model="formData[field.key]"
              type="text"
              class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent"
              :placeholder="field.label"
            />
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <button class="btn-outline text-sm" @click="showModal = false">取消</button>
          <button class="btn-primary text-sm" @click="onSave">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Plus, Search, Edit3, Trash2 } from 'lucide-vue-next'

const props = defineProps({
  title: { type: String, default: '' },
  apiBase: { type: String, default: '' },
  columns: { type: Array, default: () => [] },
  formFields: { type: Array, default: () => [] },
})

const rows = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(15)
const searchQuery = ref('')
const showModal = ref(false)
const editingId = ref(null)
const formData = reactive({})

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(1, currentPage.value - 2)
  const end = Math.min(totalPages.value, start + 4)
  for (let i = start; i <= end; i++) pages.push(i)
  return pages
})

async function fetchData() {
  if (!props.apiBase) return
  try {
    const params = new URLSearchParams({
      page: currentPage.value,
      size: pageSize.value,
    })
    if (searchQuery.value) params.set('keyword', searchQuery.value)

    const res = await fetch(`${props.apiBase}?${params}`)
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    const data = await res.json()

    // Support both { content, totalElements } (Spring Page) and { data, total }
    if (data.content) {
      rows.value = data.content
      total.value = data.totalElements ?? data.content.length
    } else if (data.data) {
      rows.value = data.data
      total.value = data.total ?? data.data.length
    } else if (Array.isArray(data)) {
      rows.value = data
      total.value = data.length
    }
  } catch {
    rows.value = []
    total.value = 0
  }
}

function onSearch() {
  currentPage.value = 1
  fetchData()
}

function goPage(p) {
  if (p < 1 || p > totalPages.value) return
  currentPage.value = p
  fetchData()
}

function openCreate() {
  editingId.value = null
  props.formFields.forEach((f) => { formData[f.key] = '' })
  showModal.value = true
}

function openEdit(row) {
  editingId.value = row.id
  props.formFields.forEach((f) => { formData[f.key] = row[f.key] ?? '' })
  showModal.value = true
}

async function onSave() {
  if (!props.apiBase) { showModal.value = false; return }
  try {
    const method = editingId.value ? 'PUT' : 'POST'
    const url = editingId.value ? `${props.apiBase}/${editingId.value}` : props.apiBase
    await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ...formData }),
    })
    showModal.value = false
    fetchData()
  } catch {
    // silent — backend error handled separately
  }
}

async function onDelete(row) {
  if (!confirm('确认删除？')) return
  if (!props.apiBase) return
  try {
    await fetch(`${props.apiBase}/${row.id}`, { method: 'DELETE' })
    fetchData()
  } catch {
    // silent
  }
}

onMounted(() => {
  fetchData()
})
</script>
