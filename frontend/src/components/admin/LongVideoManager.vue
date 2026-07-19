<template>
  <section>
    <div class="mb-4 flex flex-wrap items-center justify-between gap-4">
      <div>
        <h3 class="text-base font-semibold text-gray-900">长视频</h3>
        <p class="mt-1 text-xs text-gray-500">每条内容可配置本地视频、B站链接或两者同时配置。</p>
      </div>
      <div class="flex flex-wrap gap-2">
        <button type="button" class="btn-outline flex items-center gap-2 text-sm" @click="openBilibiliCreate">
          <ExternalLink class="h-4 w-4" />添加 B站链接
        </button>
        <button type="button" class="btn-primary flex items-center gap-2 text-sm" @click="openCreate">
          <Plus class="h-4 w-4" />新增长视频
        </button>
      </div>
    </div>

    <div v-if="error" class="mb-4 border-l-4 border-red-400 bg-red-50 px-4 py-3 text-sm text-red-700">{{ error }}</div>

    <div class="mb-5 border border-gray-200 bg-gray-50 p-4">
      <div class="mb-3 flex flex-wrap items-center justify-between gap-2">
        <div>
          <h4 class="text-sm font-semibold text-gray-800">长视频分类管理</h4>
          <p class="mt-1 text-xs text-gray-400">这里添加的分类会同步到前台筛选按钮和长视频编辑表单。</p>
        </div>
        <div class="flex w-full gap-2 sm:w-auto">
          <input v-model.trim="newCategory" maxlength="50" class="min-w-0 flex-1 border border-gray-200 bg-white px-3 py-2 text-sm focus:border-mangrove-500 focus:outline-none sm:w-48" placeholder="输入新分类" @keyup.enter="addCategory" />
          <button type="button" class="btn-primary flex shrink-0 items-center gap-1.5 text-sm" :disabled="categorySaving || !newCategory" @click="addCategory">
            <Plus class="h-4 w-4" />添加
          </button>
        </div>
      </div>
      <div class="flex flex-wrap gap-2">
        <span v-for="category in categories" :key="category.id" class="group inline-flex items-center gap-1.5 border border-gray-200 bg-white px-2.5 py-1.5 text-xs text-gray-600">
          {{ category.name }}
          <button type="button" class="text-gray-300 hover:text-red-500" :aria-label="`删除分类 ${category.name}`" title="删除分类" @click="removeCategory(category)">
            <X class="h-3.5 w-3.5" />
          </button>
        </span>
        <span v-if="categories.length === 0" class="text-xs text-gray-400">暂无分类，请先添加</span>
      </div>
      <p v-if="categoryError" class="mt-2 text-xs text-red-600">{{ categoryError }}</p>
    </div>

    <div class="overflow-hidden border border-gray-200 bg-white">
      <div class="hidden grid-cols-[minmax(0,1fr)_130px_140px_90px] gap-4 border-b border-gray-200 bg-gray-50 px-4 py-3 text-xs font-medium text-gray-500 md:grid">
        <span>视频</span><span>来源</span><span>分类与状态</span><span class="text-right">操作</span>
      </div>
      <div v-if="loading" class="py-14 text-center text-sm text-gray-400"><Loader2 class="mx-auto mb-2 h-6 w-6 animate-spin" />加载中</div>
      <div v-else-if="videos.length === 0" class="py-14 text-center text-sm text-gray-400">暂无长视频</div>
      <div v-for="video in videos" v-else :key="video.id" class="grid gap-3 border-b border-gray-100 px-4 py-4 last:border-b-0 md:grid-cols-[minmax(0,1fr)_130px_140px_90px] md:items-center md:gap-4">
        <div class="flex min-w-0 items-center gap-3">
          <div class="flex h-12 w-20 shrink-0 items-center justify-center overflow-hidden bg-gray-900">
            <img v-if="video.coverUrl" :src="video.coverUrl" class="h-full w-full object-cover" alt="" />
            <video v-else-if="video.localVideoUrl" :src="video.localVideoUrl" muted preload="metadata" class="h-full w-full object-cover" @loadeddata="$event.target.currentTime=0.5"></video>
            <Clapperboard v-else class="h-5 w-5 text-white/60" />
          </div>
          <div class="min-w-0">
            <p class="truncate text-sm font-medium text-gray-900">{{ video.title }}</p>
            <p class="mt-0.5 truncate text-xs text-gray-400">{{ video.description || video.publishedDate || '小芒长视频' }}</p>
          </div>
        </div>
        <div class="flex flex-wrap gap-1.5">
          <span v-if="video.localVideoUrl" class="bg-emerald-50 px-2 py-1 text-[11px] text-emerald-700">本地视频</span>
          <span v-if="video.bilibiliUrl" class="bg-pink-50 px-2 py-1 text-[11px] text-pink-700">B站</span>
        </div>
        <div class="text-xs text-gray-500">
          <span>{{ video.category }}</span><span class="mx-2 text-gray-300">|</span><span :class="video.status === 1 ? 'text-emerald-600' : 'text-gray-400'">{{ video.status === 1 ? '已上架' : '已下架' }}</span>
        </div>
        <div class="flex justify-end gap-1">
          <button type="button" class="p-2 text-gray-400 hover:text-mangrove-700" title="编辑" @click="openEdit(video)"><Pencil class="h-4 w-4" /></button>
          <button type="button" class="p-2 text-gray-400 hover:text-red-600" title="删除" @click="removeVideo(video)"><Trash2 class="h-4 w-4" /></button>
        </div>
      </div>
    </div>

    <div v-if="editing" class="fixed inset-0 z-[120] flex items-center justify-center bg-black/50 p-4" @click.self="closeEditor">
      <div class="max-h-[92vh] w-full max-w-2xl overflow-y-auto bg-white p-6 shadow-xl">
        <div class="mb-5 flex items-center justify-between">
          <h3 class="text-lg font-semibold text-gray-900">{{ form.id ? '编辑长视频' : '新增长视频' }}</h3>
          <button type="button" class="p-2 text-gray-400 hover:text-gray-700" title="关闭" @click="closeEditor"><X class="h-5 w-5" /></button>
        </div>

        <div class="grid gap-4 sm:grid-cols-2">
          <label class="block sm:col-span-2"><span class="mb-1 block text-sm text-gray-600">标题</span><input v-model.trim="form.title" class="w-full border border-gray-200 px-3 py-2 text-sm focus:border-mangrove-500 focus:outline-none" placeholder="请输入视频标题" /></label>
          <label class="block"><span class="mb-1 block text-sm text-gray-600">分类</span><select v-model="form.category" class="w-full border border-gray-200 px-3 py-2 text-sm"><option v-for="category in categoryOptions" :key="category" :value="category">{{ category }}</option></select></label>
          <label class="block"><span class="mb-1 block text-sm text-gray-600">发布日期</span><input v-model="form.publishedDate" type="date" class="w-full border border-gray-200 px-3 py-2 text-sm" /></label>
          <label class="block sm:col-span-2"><span class="mb-1 block text-sm text-gray-600">简介</span><textarea v-model.trim="form.description" rows="3" class="w-full resize-none border border-gray-200 px-3 py-2 text-sm"></textarea></label>

          <label class="block sm:col-span-2">
            <span class="mb-1 block text-sm text-gray-600">B站链接</span>
            <div class="flex gap-2">
              <input ref="bilibiliInput" v-model.trim="form.bilibiliUrl" class="min-w-0 flex-1 border border-gray-200 px-3 py-2 text-sm focus:border-mangrove-500 focus:outline-none" placeholder="https://www.bilibili.com/video/... 或 https://b23.tv/..." />
              <a v-if="form.bilibiliUrl" :href="form.bilibiliUrl" target="_blank" rel="noopener noreferrer" class="flex h-10 w-10 shrink-0 items-center justify-center border border-gray-200 text-gray-500" title="打开链接"><ExternalLink class="h-4 w-4" /></a>
            </div>
          </label>
          <p class="-mt-2 text-xs text-gray-400 sm:col-span-2">可以只填写 B站链接，无需上传本地视频。</p>

          <div class="sm:col-span-2">
            <span class="mb-1 block text-sm text-gray-600">本地长视频</span>
            <div class="flex flex-wrap items-center gap-2">
              <label class="inline-flex cursor-pointer items-center gap-2 border border-gray-200 px-3 py-2 text-sm text-gray-600 hover:border-mangrove-400"><Upload class="h-4 w-4" />{{ uploadingVideo ? '上传中...' : '上传视频' }}<input type="file" accept="video/*,.mp4,.webm,.mov,.avi" class="hidden" :disabled="uploadingVideo" @change="uploadAsset($event, 'localVideoUrl')" /></label>
              <span v-if="form.localVideoUrl" class="max-w-md truncate text-xs text-gray-500">{{ form.localVideoUrl }}</span>
              <button v-if="form.localVideoUrl" type="button" class="p-2 text-gray-400 hover:text-red-500" title="移除本地视频" @click="form.localVideoUrl=''" ><X class="h-4 w-4" /></button>
            </div>
          </div>

          <div class="sm:col-span-2">
            <span class="mb-1 block text-sm text-gray-600">封面</span>
            <div class="flex items-center gap-3">
              <div class="flex h-20 w-32 shrink-0 items-center justify-center overflow-hidden bg-gray-100"><img v-if="form.coverUrl" :src="form.coverUrl" class="h-full w-full object-cover" alt="封面预览" /><ImageIcon v-else class="h-6 w-6 text-gray-300" /></div>
              <label class="inline-flex cursor-pointer items-center gap-2 border border-gray-200 px-3 py-2 text-sm text-gray-600 hover:border-mangrove-400"><Upload class="h-4 w-4" />{{ uploadingCover ? '上传中...' : '上传封面' }}<input type="file" accept="image/*" class="hidden" :disabled="uploadingCover" @change="uploadAsset($event, 'coverUrl')" /></label>
              <button v-if="form.coverUrl" type="button" class="p-2 text-gray-400 hover:text-red-500" title="移除封面" @click="form.coverUrl=''" ><X class="h-4 w-4" /></button>
            </div>
          </div>

          <label class="block"><span class="mb-1 block text-sm text-gray-600">排序</span><input v-model.number="form.sortOrder" type="number" class="w-full border border-gray-200 px-3 py-2 text-sm" /></label>
          <label class="block"><span class="mb-1 block text-sm text-gray-600">状态</span><select v-model.number="form.status" class="w-full border border-gray-200 px-3 py-2 text-sm"><option :value="1">上架</option><option :value="0">下架</option></select></label>
        </div>

        <p v-if="formError" class="mt-4 text-sm text-red-600">{{ formError }}</p>
        <div class="mt-6 flex justify-end gap-2">
          <button type="button" class="btn-outline text-sm" @click="closeEditor">取消</button>
          <button type="button" class="btn-primary flex items-center gap-2 text-sm" :disabled="saving || uploadingVideo || uploadingCover" @click="saveVideo"><Loader2 v-if="saving" class="h-4 w-4 animate-spin" />保存</button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { Clapperboard, ExternalLink, Image as ImageIcon, Loader2, Pencil, Plus, Trash2, Upload, X } from 'lucide-vue-next'

const categories = ref([])
const categoryOptions = computed(() => {
  const names = [...categories.value.map(category => category.name), ...videos.value.map(video => video.category)]
  return [...new Set(names.filter(Boolean))]
})
const videos = ref([])
const loading = ref(true)
const error = ref('')
const editing = ref(false)
const saving = ref(false)
const uploadingVideo = ref(false)
const uploadingCover = ref(false)
const categorySaving = ref(false)
const categoryError = ref('')
const newCategory = ref('')
const formError = ref('')
const bilibiliInput = ref(null)
const form = reactive(emptyForm())

function emptyForm() { return { id: null, title: '', category: categoryOptions.value[0] || '官方', description: '', localVideoUrl: '', bilibiliUrl: '', coverUrl: '', publishedDate: '', sortOrder: 0, status: 1 } }
function token() { return localStorage.getItem('mangrove_token') || '' }
async function request(url, options = {}) {
  const response = await fetch(url, { ...options, headers: { ...(options.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }), Authorization: `Bearer ${token()}`, ...(options.headers || {}) } })
  const json = await response.json()
  if (!response.ok || json.code !== 200) throw new Error(json.msg || '请求失败')
  return json.data
}
async function loadVideos() { loading.value = true; error.value = ''; try { videos.value = await request('/api/admin/long-videos') || [] } catch (e) { error.value = e.message } finally { loading.value = false } }
async function loadCategories() { try { categories.value = await request('/api/long-video-categories') || [] } catch (e) { categoryError.value = e.message } }
async function addCategory() {
  const name = newCategory.value.trim()
  if (!name || categorySaving.value) return
  categorySaving.value = true
  categoryError.value = ''
  try {
    await request('/api/long-video-categories', { method: 'POST', body: JSON.stringify({ name }) })
    newCategory.value = ''
    await loadCategories()
  } catch (e) {
    categoryError.value = e.message
  } finally {
    categorySaving.value = false
  }
}
async function removeCategory(category) {
  if (!confirm(`确定删除长视频分类“${category.name}”吗？已有视频不会被删除。`)) return
  categoryError.value = ''
  try {
    await request(`/api/long-video-categories/${category.id}`, { method: 'DELETE' })
    await loadCategories()
  } catch (e) {
    categoryError.value = e.message
  }
}
function resetForm(value = emptyForm()) { Object.assign(form, value) }
function openCreate() { resetForm(); formError.value = ''; editing.value = true }
function openBilibiliCreate() { openCreate(); nextTick(() => bilibiliInput.value?.focus()) }
function openEdit(video) { resetForm({ id: video.id, title: video.title, category: video.category, description: video.description || '', localVideoUrl: video.localVideoUrl || '', bilibiliUrl: video.bilibiliUrl || '', coverUrl: video.coverUrl || '', publishedDate: video.publishedDate || '', sortOrder: video.sortOrder || 0, status: video.status ?? 1 }); formError.value = ''; editing.value = true }
function closeEditor() { if (!saving.value) editing.value = false }
function validBilibiliUrl(value) {
  if (!value) return true
  try { const url = new URL(value); return url.protocol === 'https:' && (url.hostname === 'bilibili.com' || url.hostname.endsWith('.bilibili.com') || url.hostname === 'b23.tv' || url.hostname.endsWith('.b23.tv')) } catch { return false }
}
async function uploadAsset(event, key) {
  const file = event.target.files?.[0]; event.target.value = ''; if (!file) return
  const flag = key === 'coverUrl' ? uploadingCover : uploadingVideo; flag.value = true; formError.value = ''
  try { const body = new FormData(); body.append('file', file); form[key] = (await request('/api/files/upload', { method: 'POST', body })).url } catch (e) { formError.value = e.message } finally { flag.value = false }
}
async function saveVideo() {
  formError.value = ''
  if (!form.title) return void (formError.value = '请填写视频标题')
  if (!form.localVideoUrl && !form.bilibiliUrl) return void (formError.value = '本地视频和 B站链接至少填写一个')
  if (!validBilibiliUrl(form.bilibiliUrl)) return void (formError.value = '请输入有效的 B站 HTTPS 链接')
  saving.value = true
  try { await request(form.id ? `/api/admin/long-videos/${form.id}` : '/api/admin/long-videos', { method: form.id ? 'PUT' : 'POST', body: JSON.stringify(form) }); editing.value = false; await loadVideos() } catch (e) { formError.value = e.message } finally { saving.value = false }
}
async function removeVideo(video) { if (!confirm(`确定删除长视频“${video.title}”吗？上传文件不会被删除。`)) return; try { await request(`/api/admin/long-videos/${video.id}`, { method: 'DELETE' }); await loadVideos() } catch (e) { error.value = e.message } }
onMounted(() => Promise.all([loadVideos(), loadCategories()]))
</script>
