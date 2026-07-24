<template>
  <section>
    <div class="mb-4 flex items-center justify-between gap-4">
      <div>
        <h3 class="text-base font-semibold text-gray-900">音乐歌曲</h3>
        <p class="mt-1 text-xs text-gray-500">歌手固定为“小芒”，每首歌至少配置 MP3 或 QQ 音乐链接。</p>
      </div>
      <div class="flex flex-wrap justify-end gap-2">
        <button type="button" class="btn-outline flex items-center gap-2 text-sm" @click="openQqCreate">
          <ExternalLink class="h-4 w-4" />添加 QQ 链接
        </button>
        <button type="button" class="btn-primary flex items-center gap-2 text-sm" @click="openCreate">
          <Plus class="h-4 w-4" />新增歌曲
        </button>
      </div>
    </div>

    <div v-if="error" class="mb-4 border-l-4 border-red-400 bg-red-50 px-4 py-3 text-sm text-red-700">{{ error }}</div>

    <div class="overflow-hidden border border-gray-200 bg-white">
      <div class="hidden grid-cols-[minmax(0,1fr)_120px_150px_90px] gap-4 border-b border-gray-200 bg-gray-50 px-4 py-3 text-xs font-medium text-gray-500 md:grid">
        <span>歌曲</span><span>来源</span><span>排序与状态</span><span class="text-right">操作</span>
      </div>
      <div v-if="loading" class="py-14 text-center text-sm text-gray-400">
        <Loader2 class="mx-auto mb-2 h-6 w-6 animate-spin" />加载中
      </div>
      <div v-else-if="tracks.length === 0" class="py-14 text-center text-sm text-gray-400">暂无歌曲</div>
      <div v-for="track in tracks" v-else :key="track.id" class="grid gap-3 border-b border-gray-100 px-4 py-4 last:border-b-0 md:grid-cols-[minmax(0,1fr)_120px_150px_90px] md:items-center md:gap-4">
        <div class="flex min-w-0 items-center gap-3">
          <div class="flex h-10 w-10 shrink-0 items-center justify-center overflow-hidden bg-mangrove-50">
            <img v-if="track.coverUrl" :src="track.coverUrl" class="h-full w-full object-cover" alt="" />
            <Music2 v-else class="h-4 w-4 text-mangrove-600" />
          </div>
          <div class="min-w-0">
            <p class="truncate text-sm font-medium text-gray-900">{{ track.title }}</p>
            <p class="mt-0.5 text-xs text-gray-400">小芒 · {{ track.category }}</p>
          </div>
        </div>
        <div class="flex flex-wrap gap-1.5">
          <span v-if="track.localAudioUrl" class="bg-emerald-50 px-2 py-1 text-[11px] text-emerald-700">MP3</span>
          <span v-if="track.qqMusicUrl" class="bg-blue-50 px-2 py-1 text-[11px] text-blue-700">QQ 音乐</span>
        </div>
        <div class="text-xs text-gray-500">
          <span>#{{ track.sortOrder || 0 }}</span>
          <span class="mx-2 text-gray-300">|</span>
          <span :class="track.status === 1 ? 'text-emerald-600' : 'text-gray-400'">{{ track.status === 1 ? '已上架' : '已下架' }}</span>
        </div>
        <div class="flex justify-end gap-1">
          <button type="button" class="p-2 text-gray-400 hover:text-mangrove-700" title="编辑" @click="openEdit(track)"><Pencil class="h-4 w-4" /></button>
          <button type="button" class="p-2 text-gray-400 hover:text-red-600" title="删除" @click="removeTrack(track)"><Trash2 class="h-4 w-4" /></button>
        </div>
      </div>
    </div>

    <div v-if="editing" class="fixed inset-0 z-[120] flex items-center justify-center bg-black/50 p-4" @click.self="closeEditor">
      <div class="max-h-[92vh] w-full max-w-2xl overflow-y-auto bg-white p-6 shadow-xl">
        <div class="mb-5 flex items-center justify-between">
          <h3 class="text-lg font-semibold text-gray-900">{{ form.id ? '编辑歌曲' : '新增歌曲' }}</h3>
          <button type="button" class="p-2 text-gray-400 hover:text-gray-700" title="关闭" @click="closeEditor"><X class="h-5 w-5" /></button>
        </div>

        <div class="grid gap-4 sm:grid-cols-2">
          <label class="block sm:col-span-2">
            <span class="mb-1 block text-sm text-gray-600">歌曲名</span>
            <input v-model.trim="form.title" class="w-full border border-gray-200 px-3 py-2 text-sm focus:border-mangrove-500 focus:outline-none" placeholder="请输入歌曲名" />
          </label>
          <label class="block">
            <span class="mb-1 block text-sm text-gray-600">分类</span>
            <select v-model="form.category" class="w-full border border-gray-200 px-3 py-2 text-sm focus:border-mangrove-500 focus:outline-none">
              <option v-for="category in categories" :key="category" :value="category">{{ category }}</option>
            </select>
          </label>
          <label class="block">
            <span class="mb-1 block text-sm text-gray-600">排序</span>
            <input v-model.number="form.sortOrder" type="number" class="w-full border border-gray-200 px-3 py-2 text-sm focus:border-mangrove-500 focus:outline-none" />
          </label>
          <label class="block sm:col-span-2">
            <span class="mb-1 block text-sm text-gray-600">QQ 音乐链接</span>
            <div class="flex gap-2">
              <input ref="qqUrlInput" v-model.trim="form.qqMusicUrl" class="min-w-0 flex-1 border border-gray-200 px-3 py-2 text-sm focus:border-mangrove-500 focus:outline-none" placeholder="粘贴 QQ 音乐分享链接：https://y.qq.com/..." />
              <a v-if="form.qqMusicUrl" :href="form.qqMusicUrl" target="_blank" rel="noopener noreferrer" class="flex h-10 w-10 shrink-0 items-center justify-center border border-gray-200 text-gray-500" title="打开链接"><ExternalLink class="h-4 w-4" /></a>
            </div>
          </label>

          <p class="-mt-2 text-xs text-gray-400 sm:col-span-2">可以只填写 QQ 音乐链接，无需上传 MP3。</p>

          <div class="sm:col-span-2">
            <span class="mb-1 block text-sm text-gray-600">本地 MP3</span>
            <div class="flex flex-wrap items-center gap-2">
              <label class="inline-flex cursor-pointer items-center gap-2 border border-gray-200 px-3 py-2 text-sm text-gray-600 hover:border-mangrove-400">
                <Upload class="h-4 w-4" />{{ uploadingAudio ? '上传中...' : '上传音频' }}
                <input type="file" accept="audio/*,.mp3,.wav,.flac,.aac,.ogg,.m4a" class="hidden" :disabled="uploadingAudio" @change="uploadAsset($event, 'localAudioUrl')" />
              </label>
              <span v-if="form.localAudioUrl" class="max-w-md truncate text-xs text-gray-500">{{ form.localAudioUrl }}</span>
              <button v-if="form.localAudioUrl" type="button" class="p-2 text-gray-400 hover:text-red-500" title="移除音频" @click="form.localAudioUrl=''" ><X class="h-4 w-4" /></button>
            </div>
          </div>

          <div class="sm:col-span-2">
            <span class="mb-1 block text-sm text-gray-600">歌曲封面（自有或已授权）</span>
            <div class="flex items-center gap-3">
              <div class="flex h-20 w-20 shrink-0 items-center justify-center overflow-hidden bg-gray-100">
                <img v-if="form.coverUrl" :src="form.coverUrl" class="h-full w-full object-cover" alt="歌曲封面预览" />
                <ImageIcon v-else class="h-6 w-6 text-gray-300" />
              </div>
              <label class="inline-flex cursor-pointer items-center gap-2 border border-gray-200 px-3 py-2 text-sm text-gray-600 hover:border-mangrove-400">
                <Upload class="h-4 w-4" />{{ uploadingCover ? '上传中...' : '上传封面' }}
                <input type="file" accept="image/*" class="hidden" :disabled="uploadingCover" @change="uploadAsset($event, 'coverUrl')" />
              </label>
              <button v-if="form.coverUrl" type="button" class="p-2 text-gray-400 hover:text-red-500" title="移除封面" @click="form.coverUrl=''" ><X class="h-4 w-4" /></button>
            </div>
          </div>

          <label class="block">
            <span class="mb-1 block text-sm text-gray-600">状态</span>
            <template v-if="isSuperAdmin">
              <select v-model.number="form.status" class="w-full border border-gray-200 px-3 py-2 text-sm focus:border-mangrove-500 focus:outline-none">
                <option :value="1">上架</option><option :value="0">下架（待审核）</option>
              </select>
            </template>
            <template v-else>
              <div class="w-full border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-400 rounded">下架（待审核）— 需超级管理员审核后才会上架</div>
            </template>
          </label>
        </div>

        <p v-if="formError" class="mt-4 text-sm text-red-600">{{ formError }}</p>
        <div class="mt-6 flex justify-end gap-2">
          <button type="button" class="btn-outline text-sm" @click="closeEditor">取消</button>
          <button type="button" class="btn-primary flex items-center gap-2 text-sm" :disabled="saving || uploadingAudio || uploadingCover" @click="saveTrack">
            <Loader2 v-if="saving" class="h-4 w-4 animate-spin" />保存
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { ExternalLink, Image as ImageIcon, Loader2, Music2, Pencil, Plus, Trash2, Upload, X } from 'lucide-vue-next'

const props = defineProps({
  categories: {
    type: Array,
    default: () => ['单曲', '舞台', '翻唱'],
  },
})

const tracks = ref([])
const loading = ref(true)
const error = ref('')
const editing = ref(false)
const saving = ref(false)
const uploadingAudio = ref(false)
const uploadingCover = ref(false)
const formError = ref('')
const qqUrlInput = ref(null)
const categories = computed(() => props.categories.length ? props.categories : ['单曲', '舞台', '翻唱'])
const isSuperAdmin = JSON.parse(localStorage.getItem('mangrove_user') || '{}').role === 'SUPER_ADMIN'
const form = reactive(emptyForm())

function emptyForm() {
  const userRole = JSON.parse(localStorage.getItem('mangrove_user') || '{}').role
  const defaultStatus = userRole === 'SUPER_ADMIN' ? 1 : 0
  return { id: null, title: '', category: categories.value[0] || '单曲', localAudioUrl: '', qqMusicUrl: '', coverUrl: '', sortOrder: 0, status: defaultStatus }
}

function token() { return localStorage.getItem('mangrove_token') || '' }

async function request(url, options = {}) {
  const response = await fetch(url, {
    ...options,
    headers: { ...(options.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }), Authorization: `Bearer ${token()}`, ...(options.headers || {}) },
  })
  const json = await response.json()
  if (!response.ok || json.code !== 200) throw new Error(json.msg || '请求失败')
  return json.data
}

async function loadTracks() {
  loading.value = true
  error.value = ''
  try { tracks.value = await request('/api/admin/music/tracks') || [] }
  catch (e) { error.value = e.message }
  finally { loading.value = false }
}

function resetForm(value = emptyForm()) { Object.assign(form, value) }
function openCreate() { resetForm(); formError.value = ''; editing.value = true }
function openQqCreate() {
  openCreate()
  nextTick(() => qqUrlInput.value?.focus())
}
function openEdit(track) {
  resetForm({ id: track.id, title: track.title, category: track.category, localAudioUrl: track.localAudioUrl || '', qqMusicUrl: track.qqMusicUrl || '', coverUrl: track.coverUrl || '', sortOrder: track.sortOrder || 0, status: track.status ?? 1 })
  formError.value = ''
  editing.value = true
}
function closeEditor() { if (!saving.value) editing.value = false }

function validQqUrl(value) {
  if (!value) return true
  try {
    const url = new URL(value)
    return url.protocol === 'https:' && (url.hostname === 'y.qq.com' || url.hostname.endsWith('.y.qq.com'))
  } catch { return false }
}

async function uploadAsset(event, key) {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file) return
  const flag = key === 'coverUrl' ? uploadingCover : uploadingAudio
  flag.value = true
  formError.value = ''
  try {
    const body = new FormData()
    body.append('file', file)
    const data = await request('/api/files/upload', { method: 'POST', body })
    form[key] = data.url
  } catch (e) { formError.value = e.message }
  finally { flag.value = false }
}

async function saveTrack() {
  formError.value = ''
  if (!form.title) return void (formError.value = '请填写歌曲名')
  if (!form.localAudioUrl && !form.qqMusicUrl) return void (formError.value = 'MP3 和 QQ 音乐链接至少填写一个')
  if (!validQqUrl(form.qqMusicUrl)) return void (formError.value = '请输入有效的 QQ 音乐 HTTPS 链接')
  saving.value = true
  try {
    await request(form.id ? `/api/admin/music/tracks/${form.id}` : '/api/admin/music/tracks', { method: form.id ? 'PUT' : 'POST', body: JSON.stringify(form) })
    editing.value = false
    await loadTracks()
  } catch (e) { formError.value = e.message }
  finally { saving.value = false }
}

async function removeTrack(track) {
  if (!confirm(`确定删除歌曲“${track.title}”吗？上传文件不会被删除。`)) return
  try { await request(`/api/admin/music/tracks/${track.id}`, { method: 'DELETE' }); await loadTracks() }
  catch (e) { error.value = e.message }
}

onMounted(loadTracks)
</script>
