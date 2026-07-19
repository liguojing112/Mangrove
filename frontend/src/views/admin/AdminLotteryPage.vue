<template>
  <div class="min-w-0">
    <div class="flex flex-wrap items-center justify-between gap-2 mb-6">
      <h2 class="text-lg font-semibold text-gray-900">抽奖管理</h2>
      <button class="btn-primary text-sm" @click="openCreate">+ 新建抽奖</button>
    </div>

    <!-- Table -->
    <div class="card overflow-hidden max-w-full">
      <div class="overflow-x-auto">
        <table class="w-full text-sm min-w-[700px]">
          <thead>
            <tr class="bg-gray-50">
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">#</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">标题</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">参与资格</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">中奖人数</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">时间段</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">状态</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">参与人数</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">显示</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(lottery, idx) in lotteries" :key="lottery.id" class="border-b border-gray-100 hover:bg-gray-50">
              <td class="px-4 py-3 text-gray-400">{{ (currentPage - 1) * pageSize + idx + 1 }}</td>
              <td class="px-4 py-3 font-medium text-gray-900 max-w-[180px] truncate">{{ lottery.title }}</td>
              <td class="px-4 py-3">
                <span class="px-2 py-1 rounded-full text-xs" :class="eligibleClass(lottery.eligibleRole)">{{ eligibleLabel(lottery.eligibleRole) }}</span>
              </td>
              <td class="px-4 py-3 text-gray-700">{{ lottery.winnerCount }}</td>
              <td class="px-4 py-3 text-gray-500 text-xs whitespace-nowrap">
                {{ formatShortTime(lottery.startTime) }} ~ {{ formatShortTime(lottery.endTime) }}
              </td>
              <td class="px-4 py-3">
                <span class="px-2 py-1 rounded-full text-xs" :class="statusClass(lottery.status)">{{ statusLabel(lottery.status) }}</span>
              </td>
              <td class="px-4 py-3 text-gray-700">{{ lottery.entryCount }}</td>
              <td class="px-4 py-3">
                <button class="p-1 rounded hover:bg-gray-100" :title="lottery.statusFlag === 1 ? '隐藏' : '显示'" @click="toggleStatus(lottery)">
                  <Eye v-if="lottery.statusFlag === 1" class="w-4 h-4 text-green-500" />
                  <EyeOff v-else class="w-4 h-4 text-gray-400" />
                </button>
              </td>
              <td class="px-4 py-3">
                <div class="flex items-center gap-1">
                  <button class="p-1.5 rounded-lg hover:bg-emerald-50 text-gray-400 hover:text-emerald-600 transition-colors" title="查看参与者" @click="openEntries(lottery)">
                    <Users class="w-4 h-4" />
                  </button>
                  <button v-if="lottery.status !== 'DRAWN' && lottery.status !== 'PENDING'" class="p-1.5 rounded-lg hover:bg-amber-50 text-gray-400 hover:text-amber-600 transition-colors" title="开奖" @click="openDraw(lottery)">
                    <Sparkles class="w-4 h-4" />
                  </button>
                  <button v-if="lottery.status === 'PENDING'" class="p-1.5 rounded-lg hover:bg-blue-50 text-gray-400 hover:text-blue-600 transition-colors" title="编辑" @click="openEdit(lottery)">
                    <Edit2 class="w-4 h-4" />
                  </button>
                  <button class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-colors" title="删除" @click="deleteLottery(lottery)">
                    <Trash2 class="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="lotteries.length === 0">
              <td colspan="9" class="px-4 py-12 text-center text-sm text-gray-400">暂无抽奖活动</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div class="flex flex-wrap items-center justify-between gap-2 px-4 py-3 border-t border-gray-100">
        <span class="text-xs text-gray-500">共 {{ total }} 条，第 {{ currentPage }}/{{ totalPages }} 页</span>
        <div class="flex flex-wrap items-center gap-1">
          <button class="px-3 py-1.5 rounded-lg text-xs font-medium border border-gray-200 hover:bg-gray-50 disabled:opacity-40" :disabled="currentPage <= 1" @click="goPage(currentPage - 1)">上一页</button>
          <button v-for="p in visiblePages" :key="p" class="w-8 h-8 rounded-lg text-xs font-medium transition-colors" :class="p === currentPage ? 'bg-mangrove-700 text-white' : 'text-gray-600 hover:bg-gray-100'" @click="goPage(p)">{{ p }}</button>
          <button class="px-3 py-1.5 rounded-lg text-xs font-medium border border-gray-200 hover:bg-gray-50 disabled:opacity-40" :disabled="currentPage >= totalPages" @click="goPage(currentPage + 1)">下一页</button>
        </div>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showForm" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="closeForm">
      <div class="card p-6 w-full max-w-md mx-4 max-h-[90vh] overflow-y-auto">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">{{ editingId ? '编辑抽奖' : '新建抽奖' }}</h3>
        <form @submit.prevent="saveLottery">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">标题</label>
              <input v-model="formData.title" type="text" class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" required />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">描述</label>
              <textarea v-model="formData.description" rows="3" class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 resize-none"></textarea>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">奖品描述</label>
              <input v-model="formData.prizeDescription" type="text" placeholder="小卡 + 周边" class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" />
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">参与资格</label>
                <select v-model="formData.eligibleRole" class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500">
                  <option value="ALL">所有用户</option>
                  <option value="FAN">仅粉丝</option>
                  <option value="CREATOR">仅创作者</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">中奖人数</label>
                <input v-model.number="formData.winnerCount" type="number" min="1" class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" required />
              </div>
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">开始时间</label>
                <input v-model="formData.startTime" type="datetime-local" class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" required />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">结束时间</label>
                <input v-model="formData.endTime" type="datetime-local" class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" required />
              </div>
            </div>
          </div>
          <div class="flex items-center gap-3 mt-6">
            <button type="submit" class="btn-primary text-sm" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
            <button type="button" class="btn-secondary text-sm" @click="closeForm">取消</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Draw Confirmation Modal -->
    <div v-if="drawingLottery" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="drawingLottery = null">
      <div class="card p-6 w-full max-w-sm mx-4">
        <h3 class="text-lg font-semibold text-gray-900 mb-2">确认开奖</h3>
        <p class="text-sm text-gray-600 mb-1">活动：<strong>{{ drawingLottery.title }}</strong></p>
        <p class="text-sm text-gray-600 mb-1">参与人数：{{ drawingLottery.entryCount }} 人</p>
        <p class="text-sm text-gray-600 mb-4">中奖名额：{{ drawingLottery.winnerCount }} 人</p>
        <div class="bg-amber-50 border border-amber-200 rounded-lg p-3 mb-4">
          <p class="text-xs text-amber-700">开奖后将随机选出中奖者，此操作不可撤销。</p>
        </div>
        <div class="flex items-center gap-3">
          <button class="btn-primary text-sm" :disabled="drawing" @click="confirmDraw">{{ drawing ? '开奖中...' : '确认开奖' }}</button>
          <button class="btn-secondary text-sm" @click="drawingLottery = null">取消</button>
        </div>
      </div>
    </div>

    <!-- Entries Modal -->
    <div v-if="entriesLottery" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="entriesLottery = null">
      <div class="card w-full max-w-lg mx-4 max-h-[85vh] flex flex-col">
        <div class="flex items-center justify-between px-5 py-4 border-b border-gray-100">
          <div>
            <h3 class="text-lg font-semibold text-gray-900">参与者列表</h3>
            <p class="text-xs text-gray-500 mt-0.5">{{ entriesLottery.title }} · {{ entries.length }} 人参与</p>
          </div>
          <button class="p-2 text-gray-400 hover:text-gray-700" @click="entriesLottery = null"><X class="w-5 h-5" /></button>
        </div>
        <div class="overflow-y-auto p-5 flex-1">
          <div v-if="entries.length === 0" class="text-center py-8 text-sm text-gray-400">暂无参与者</div>
          <div v-else class="space-y-2">
            <div v-for="(entry, i) in entries" :key="entry.id" class="flex items-center gap-3 p-3 rounded-lg bg-gray-50">
              <span class="text-xs text-gray-400 w-6 text-right">{{ i + 1 }}</span>
              <div class="w-8 h-8 rounded-full bg-mangrove-200 flex items-center justify-center">
                <User class="w-4 h-4 text-mangrove-700" />
              </div>
              <div class="min-w-0 flex-1">
                <p class="text-sm font-medium text-gray-900 truncate">{{ entry.userNickname || '匿名' }}</p>
              </div>
              <span v-if="entriesLottery.winnerUserIds?.includes(entry.userId)" class="px-2 py-0.5 rounded-full text-xs bg-amber-100 text-amber-700 font-medium">中奖</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Edit2, Eye, EyeOff, Sparkles, Trash2, User, Users, X } from 'lucide-vue-next'

const lotteries = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(15)
const showForm = ref(false)
const editingId = ref(null)
const saving = ref(false)
const drawingLottery = ref(null)
const drawing = ref(false)
const entriesLottery = ref(null)
const entries = ref([])

const formData = ref(emptyForm())

function emptyForm() {
  return { title: '', description: '', prizeDescription: '', eligibleRole: 'ALL', winnerCount: 1, startTime: '', endTime: '' }
}

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))
const visiblePages = computed(() => {
  const pages = []; const start = Math.max(1, currentPage.value - 2); const end = Math.min(totalPages.value, start + 4)
  for (let i = start; i <= end; i++) pages.push(i); return pages
})

function getToken() { return localStorage.getItem('mangrove_token') }

async function fetchLotteries() {
  try {
    const res = await fetch(`/api/admin/lotteries?page=${currentPage.value - 1}&size=${pageSize.value}`, { headers: { Authorization: `Bearer ${getToken()}` } })
    const json = await res.json()
    if (json.code === 200 && json.data) { lotteries.value = json.data.content || []; total.value = json.data.totalElements || 0 }
  } catch (e) { console.error('获取抽奖列表失败:', e) }
}

function goPage(p) { if (p < 1 || p > totalPages.value) return; currentPage.value = p; fetchLotteries() }

function openCreate() { editingId.value = null; formData.value = emptyForm(); showForm.value = true }

function openEdit(lottery) {
  editingId.value = lottery.id
  formData.value = {
    title: lottery.title, description: lottery.description || '', prizeDescription: lottery.prizeDescription || '',
    eligibleRole: lottery.eligibleRole, winnerCount: lottery.winnerCount,
    startTime: lottery.startTime ? lottery.startTime.slice(0, 16) : '',
    endTime: lottery.endTime ? lottery.endTime.slice(0, 16) : ''
  }
  showForm.value = true
}

function closeForm() { showForm.value = false; editingId.value = null; formData.value = emptyForm() }

async function saveLottery() {
  saving.value = true
  try {
    const url = editingId.value ? `/api/admin/lotteries/${editingId.value}` : '/api/admin/lotteries'
    const method = editingId.value ? 'PUT' : 'POST'
    const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` }, body: JSON.stringify(formData.value) })
    const json = await res.json()
    if (json.code === 200) { closeForm(); await fetchLotteries() } else { alert(json.msg || '保存失败') }
  } catch (e) { alert('保存失败：' + e.message) } finally { saving.value = false }
}

async function toggleStatus(lottery) {
  const newStatus = lottery.statusFlag === 1 ? 0 : 1
  try {
    const res = await fetch(`/api/admin/lotteries/${lottery.id}/status?status=${newStatus}`, { method: 'PUT', headers: { Authorization: `Bearer ${getToken()}` } })
    const json = await res.json()
    if (json.code === 200) { lottery.statusFlag = newStatus }
  } catch (e) { console.error('切换状态失败:', e) }
}

async function deleteLottery(lottery) {
  if (!confirm(`确定要删除「${lottery.title}」吗？`)) return
  try {
    const res = await fetch(`/api/admin/lotteries/${lottery.id}`, { method: 'DELETE', headers: { Authorization: `Bearer ${getToken()}` } })
    const json = await res.json()
    if (json.code === 200) await fetchLotteries()
  } catch (e) { console.error('删除失败:', e) }
}

function openDraw(lottery) { drawingLottery.value = lottery }

async function confirmDraw() {
  drawing.value = true
  try {
    const res = await fetch(`/api/admin/lotteries/${drawingLottery.value.id}/draw`, { method: 'POST', headers: { Authorization: `Bearer ${getToken()}` } })
    const json = await res.json()
    if (json.code === 200) { drawingLottery.value = null; await fetchLotteries(); alert('开奖成功！') } else { alert(json.msg || '开奖失败') }
  } catch (e) { alert('开奖失败：' + e.message) } finally { drawing.value = false }
}

async function openEntries(lottery) {
  entriesLottery.value = lottery; entries.value = []
  try {
    const res = await fetch(`/api/admin/lotteries/${lottery.id}/entries?page=0&size=200`, { headers: { Authorization: `Bearer ${getToken()}` } })
    const json = await res.json()
    if (json.code === 200 && json.data) entries.value = json.data.content || []
  } catch (e) { console.error('获取参与者失败:', e) }
}

function formatShortTime(dt) { if (!dt) return '-'; return dt.replace('T', ' ').substring(5, 16) }

function eligibleLabel(role) { return { ALL: '所有用户', FAN: '仅粉丝', CREATOR: '仅创作者' }[role] || role }
function eligibleClass(role) { return { ALL: 'bg-blue-100 text-blue-700', FAN: 'bg-green-100 text-green-700', CREATOR: 'bg-purple-100 text-purple-700' }[role] || 'bg-gray-100 text-gray-600' }

function statusLabel(status) { return { PENDING: '未开始', ACTIVE: '进行中', ENDED: '已结束', DRAWN: '已开奖' }[status] || status }
function statusClass(status) { return { PENDING: 'bg-gray-100 text-gray-600', ACTIVE: 'bg-green-100 text-green-700', ENDED: 'bg-yellow-100 text-yellow-700', DRAWN: 'bg-amber-100 text-amber-700' }[status] || 'bg-gray-100 text-gray-600' }

onMounted(fetchLotteries)
</script>
