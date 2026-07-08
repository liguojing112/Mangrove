<template>
  <div class="audit-page">
    <!-- 标题 -->
    <div class="flex items-center justify-between mb-6">
      <div>
        <h2 class="text-xl font-bold text-gray-900">内容审核面板</h2>
        <p class="text-sm text-gray-500 mt-1">审核用户上传内容和删除申请</p>
      </div>
    </div>

    <!-- 分类标签 -->
    <div class="flex gap-3 mb-6">
      <button
        class="px-5 py-2.5 rounded-xl text-sm font-medium transition-all duration-200"
        :class="activeTab === 'upload'
          ? 'bg-emerald-600 text-white shadow-lg shadow-emerald-200'
          : 'bg-white text-gray-600 border border-gray-200 hover:border-emerald-300 hover:text-emerald-600'"
        @click="switchTab('upload')"
      >
        <el-icon class="mr-1.5" style="vertical-align: -2px"><UploadFilled /></el-icon>
        待审核内容
        <span
          v-if="uploadTotal > 0"
          class="ml-2 px-2 py-0.5 rounded-full text-xs"
          :class="activeTab === 'upload' ? 'bg-emerald-500 text-white' : 'bg-emerald-100 text-emerald-700'"
        >{{ uploadTotal }}</span>
      </button>
      <button
        class="px-5 py-2.5 rounded-xl text-sm font-medium transition-all duration-200"
        :class="activeTab === 'delete'
          ? 'bg-red-500 text-white shadow-lg shadow-red-200'
          : 'bg-white text-gray-600 border border-gray-200 hover:border-red-300 hover:text-red-500'"
        @click="switchTab('delete')"
      >
        <el-icon class="mr-1.5" style="vertical-align: -2px"><DeleteFilled /></el-icon>
        待审核删除申请
        <span
          v-if="deleteTotal > 0"
          class="ml-2 px-2 py-0.5 rounded-full text-xs"
          :class="activeTab === 'delete' ? 'bg-red-400 text-white' : 'bg-red-100 text-red-600'"
        >{{ deleteTotal }}</span>
      </button>
    </div>

    <!-- 列表 -->
    <div class="card overflow-hidden">
      <!-- 空状态 -->
      <div v-if="items.length === 0 && !loading" class="flex flex-col items-center justify-center py-20 text-gray-400">
        <el-icon class="text-5xl mb-4"><FolderOpened /></el-icon>
        <p class="text-sm">{{ activeTab === 'upload' ? '暂无待审核内容' : '暂无待审核删除申请' }}</p>
      </div>

      <!-- 加载中 -->
      <div v-if="loading" class="flex items-center justify-center py-20">
        <el-icon class="is-loading text-3xl text-emerald-500"><Loading /></el-icon>
      </div>

      <!-- 审核列表 -->
      <div v-for="item in items" :key="item.auditLogId" class="border-b border-gray-100 last:border-b-0">
        <div class="flex items-center gap-5 p-5 hover:bg-gray-50/50 transition-colors">
          <!-- 缩略图 -->
          <div class="flex-shrink-0 w-[120px] h-[80px] rounded-lg overflow-hidden bg-gray-100">
            <img
              v-if="item.thumbnailUrl || item.fileUrl"
              :src="item.thumbnailUrl || item.fileUrl"
              :alt="item.title"
              class="w-full h-full object-cover"
              @error="onImgError"
            />
            <div v-else class="flex items-center justify-center w-full h-full text-gray-400">
              <el-icon class="text-2xl"><PictureFilled /></el-icon>
            </div>
          </div>

          <!-- 信息区 -->
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-3 mb-1.5">
              <h3 class="text-sm font-semibold text-gray-900 truncate">{{ item.title }}</h3>
              <span
                class="flex-shrink-0 px-2 py-0.5 rounded-md text-xs font-medium"
                :class="getCategoryClass(item.category)"
              >{{ getCategoryLabel(item.category) }}</span>
              <span
                v-if="item.action === 'DELETE'"
                class="flex-shrink-0 px-2 py-0.5 rounded-md text-xs font-medium bg-red-100 text-red-700"
              >删除申请</span>
            </div>
            <p v-if="item.description" class="text-xs text-gray-500 truncate mb-1.5">{{ item.description }}</p>
            <div class="flex items-center gap-4 text-xs text-gray-400">
              <span>
                <el-icon style="vertical-align: -2px"><User /></el-icon>
                上传者: {{ item.uploaderName || item.uploaderId }}
              </span>
              <span v-if="item.initiatorName">
                <el-icon style="vertical-align: -2px"><EditPen /></el-icon>
                申请人: {{ item.initiatorName }}
              </span>
              <span>
                <el-icon style="vertical-align: -2px"><Clock /></el-icon>
                {{ formatTime(item.createdAt) }}
              </span>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="flex-shrink-0 flex items-center gap-3">
            <button
              class="audit-btn-approve"
              @click="openAuditDialog(item, 1)"
            >
              <el-icon><Check /></el-icon>
              同意
            </button>
            <button
              class="audit-btn-reject"
              @click="openAuditDialog(item, 2)"
            >
              <el-icon><Close /></el-icon>
              驳回
            </button>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="totalPages > 1" class="flex items-center justify-between px-5 py-4 border-t border-gray-100">
        <span class="text-xs text-gray-500">共 {{ total }} 条，第 {{ currentPage }}/{{ totalPages }} 页</span>
        <el-pagination
          small
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          @update:current-page="onPageChange"
        />
      </div>
    </div>

    <!-- 审核弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="auditAction === 1 ? '确认审核通过' : '确认驳回'"
      width="460px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div class="py-2">
        <div v-if="auditAction === 2" class="mb-4 p-3 rounded-lg bg-amber-50 border border-amber-200">
          <p class="text-sm text-amber-800">
            <el-icon style="vertical-align: -2px"><WarningFilled /></el-icon>
            <span class="ml-1">驳回后，该内容将不会在前台展示。</span>
          </p>
        </div>
        <div v-if="auditAction === 1 && activeTab === 'delete'" class="mb-4 p-3 rounded-lg bg-blue-50 border border-blue-200">
          <p class="text-sm text-blue-800">
            <el-icon style="vertical-align: -2px"><InfoFilled /></el-icon>
            <span class="ml-1">同意后将软删除该内容（前台不再显示）。</span>
          </p>
        </div>
        <div class="mb-1">
          <label class="block text-sm font-medium text-gray-700 mb-2">
            {{ auditAction === 1 ? '审核备注（选填）' : '驳回理由' }}
          </label>
          <el-input
            v-model="auditComment"
            type="textarea"
            :rows="3"
            :placeholder="auditAction === 1 ? '可填写审核备注...' : '请填写驳回理由...'"
            maxlength="500"
            show-word-limit
          />
        </div>
      </div>
      <template #footer>
        <div class="flex justify-end gap-3">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
            :type="auditAction === 1 ? 'success' : 'danger'"
            :loading="submitting"
            @click="submitAudit"
          >
            {{ auditAction === 1 ? '确认通过' : '确认驳回' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('upload')
const items = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = ref(0)
const uploadTotal = ref(0)
const deleteTotal = ref(0)

// 审核弹窗
const dialogVisible = ref(false)
const auditAction = ref(1)  // 1-同意, 2-驳回
const auditComment = ref('')
const currentAuditItem = ref(null)
const submitting = ref(false)

const apiBase = '/api/super-admin'

function getToken() {
  return localStorage.getItem('mangrove_token')
}

async function fetchList() {
  loading.value = true
  try {
    const params = new URLSearchParams({
      page: currentPage.value - 1,
      size: pageSize.value,
      type: activeTab.value,
    })
    const res = await fetch(`${apiBase}/pending-list?${params}`, {
      headers: { 'Authorization': `Bearer ${getToken()}` },
    })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      items.value = json.data.content || []
      total.value = json.data.totalElements || 0
      totalPages.value = json.data.totalPages || 1
      if (activeTab.value === 'upload') {
        uploadTotal.value = total.value
      } else {
        deleteTotal.value = total.value
      }
    } else {
      ElMessage.error(json.msg || '加载失败')
    }
  } catch {
    ElMessage.error('网络错误，请确保后端服务已启动')
  } finally {
    loading.value = false
  }
}

// 同时获取另一类别的数量
async function fetchCounts() {
  try {
    const [uploadRes, deleteRes] = await Promise.all([
      fetch(`${apiBase}/pending-list?page=0&size=1&type=upload`, {
        headers: { 'Authorization': `Bearer ${getToken()}` },
      }).then(r => r.json()),
      fetch(`${apiBase}/pending-list?page=0&size=1&type=delete`, {
        headers: { 'Authorization': `Bearer ${getToken()}` },
      }).then(r => r.json()),
    ])
    uploadTotal.value = uploadRes.data?.totalElements || 0
    deleteTotal.value = deleteRes.data?.totalElements || 0
  } catch { /* ignore */ }
}

function switchTab(tab) {
  activeTab.value = tab
  currentPage.value = 1
  fetchList()
}

function onPageChange(p) {
  currentPage.value = p
  fetchList()
}

function openAuditDialog(item, action) {
  currentAuditItem.value = item
  auditAction.value = action
  auditComment.value = ''
  dialogVisible.value = true
}

async function submitAudit() {
  if (auditAction.value === 2 && !auditComment.value.trim()) {
    ElMessage.warning('驳回时必须填写理由')
    return
  }
  submitting.value = true
  try {
    const res = await fetch(`${apiBase}/audit`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`,
      },
      body: JSON.stringify({
        auditId: currentAuditItem.value.auditLogId,
        action: auditAction.value,
        comment: auditComment.value,
      }),
    })
    const json = await res.json()
    if (json.code === 200) {
      ElMessage.success(auditAction.value === 1 ? '审核通过' : '已驳回')
      dialogVisible.value = false
      fetchList()
      fetchCounts()
    } else {
      ElMessage.error(json.msg || '操作失败')
    }
  } catch {
    ElMessage.error('网络错误')
  } finally {
    submitting.value = false
  }
}

function getCategoryLabel(cat) {
  const map = { PHOTO: '照片馆藏', VIDEO: '影像存档', WORK: '创作者产出' }
  return map[cat] || cat
}

function getCategoryClass(cat) {
  const map = { PHOTO: 'bg-blue-100 text-blue-700', VIDEO: 'bg-purple-100 text-purple-700', WORK: 'bg-amber-100 text-amber-700' }
  return map[cat] || 'bg-gray-100 text-gray-600'
}

function formatTime(t) {
  if (!t) return '-'
  return t.replace('T', ' ').substring(0, 19)
}

function onImgError(e) {
  e.target.style.display = 'none'
}

onMounted(() => {
  fetchList()
  fetchCounts()
})
</script>

<style scoped>
.audit-page {
  max-width: 1000px;
}

.audit-btn-approve {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 8px 18px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
  background: #059669;
  color: #fff;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}
.audit-btn-approve:hover {
  background: #047857;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(5, 150, 105, 0.3);
}

.audit-btn-reject {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 8px 18px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
  background: #fff;
  color: #dc2626;
  border: 1.5px solid #fca5a5;
  cursor: pointer;
  transition: all 0.2s;
}
.audit-btn-reject:hover {
  background: #fef2f2;
  border-color: #ef4444;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.12);
}
</style>
