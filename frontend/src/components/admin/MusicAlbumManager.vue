<template>
  <section>
    <div class="mb-4 flex items-center justify-between gap-4">
      <div>
        <h3 class="text-base font-semibold text-gray-900">音乐专辑</h3>
        <p class="mt-1 text-xs text-gray-500">每个专辑使用独立封面，并管理一组内页照片。</p>
      </div>
      <button type="button" class="btn-primary flex items-center gap-2 text-sm" @click="openCreate">
        <Plus class="h-4 w-4" />新增专辑
      </button>
    </div>

    <div v-if="error" class="mb-4 border-l-4 border-red-400 bg-red-50 px-4 py-3 text-sm text-red-700">{{ error }}</div>

    <div v-if="loading" class="border border-gray-200 bg-white py-14 text-center text-sm text-gray-400">
      <Loader2 class="mx-auto mb-2 h-6 w-6 animate-spin" />加载中
    </div>
    <div v-else-if="albums.length === 0" class="border border-gray-200 bg-white py-14 text-center text-sm text-gray-400">
      <Images class="mx-auto mb-2 h-8 w-8 text-gray-300" />
      <p>暂无专辑</p>
      <p class="mt-1 text-xs">点击“新增专辑”，可以同时选择封面和内页照片。</p>
    </div>
    <div v-else class="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
      <article v-for="album in albums" :key="album.id" class="overflow-hidden border border-gray-200 bg-white">
        <button type="button" class="block aspect-[16/10] w-full overflow-hidden bg-gray-100" @click="selectAlbum(album)">
          <img :src="album.coverUrl" :alt="album.title" class="h-full w-full object-cover" />
        </button>
        <div class="flex items-center gap-3 p-4">
          <button type="button" class="min-w-0 flex-1 text-left" @click="selectAlbum(album)">
            <p class="truncate text-sm font-medium text-gray-900">{{ album.title }}</p>
            <p class="mt-1 text-xs text-gray-400">#{{ album.sortOrder || 0 }} · {{ album.status === 1 ? '已上架' : '已下架' }}</p>
          </button>
          <button type="button" class="p-2 text-gray-400 hover:text-mangrove-700" title="编辑专辑" @click="openEdit(album)"><Pencil class="h-4 w-4" /></button>
          <button type="button" class="p-2 text-gray-400 hover:text-red-600" title="删除专辑" @click="removeAlbum(album)"><Trash2 class="h-4 w-4" /></button>
        </div>
        <button type="button" class="mx-4 mb-4 inline-flex items-center gap-2 border border-gray-200 px-3 py-2 text-xs font-medium text-gray-600 hover:border-mangrove-400 hover:text-mangrove-700" @click="selectAlbum(album)">
          <Images class="h-4 w-4" />管理 / 上传照片
        </button>
      </article>
    </div>

    <section v-if="selectedAlbum" class="mt-8 border-t border-gray-200 pt-6">
      <div class="mb-4 flex flex-wrap items-center justify-between gap-3">
        <div>
          <h4 class="text-base font-semibold text-gray-900">{{ selectedAlbum.title }} · 内页照片</h4>
          <p class="mt-1 text-xs text-gray-400">{{ photos.length }} 张，使用箭头调整前台展示顺序。</p>
        </div>
        <div class="flex items-center gap-2">
          <label class="inline-flex cursor-pointer items-center gap-2 border border-gray-200 bg-white px-3 py-2 text-sm text-gray-600 hover:border-mangrove-400">
            <Upload class="h-4 w-4" />{{ uploadingPhotos ? '上传中...' : '批量上传照片' }}
            <input type="file" accept="image/*" multiple class="hidden" :disabled="uploadingPhotos" @change="uploadPhotos" />
          </label>
          <button type="button" class="p-2 text-gray-400 hover:text-gray-700" title="关闭照片管理" @click="selectedAlbum=null; photos=[]"><X class="h-5 w-5" /></button>
        </div>
      </div>

      <div v-if="detailLoading" class="py-10 text-center text-sm text-gray-400"><Loader2 class="mx-auto h-6 w-6 animate-spin" /></div>
      <div v-else-if="photos.length === 0" class="border border-dashed border-gray-300 py-12 text-center text-sm text-gray-400">还没有照片</div>
      <div v-else class="grid grid-cols-2 gap-3 md:grid-cols-3 xl:grid-cols-4">
        <div v-for="(photo, index) in photos" :key="photo.id" class="group relative aspect-square overflow-hidden bg-gray-100">
          <img :src="photo.imageUrl" :alt="photo.caption || selectedAlbum.title" class="h-full w-full object-cover" />
          <div class="absolute inset-x-0 bottom-0 flex items-center justify-end gap-1 bg-black/55 p-2 opacity-100 transition-opacity md:opacity-0 md:group-hover:opacity-100">
            <button type="button" class="bg-white/90 p-1.5 text-gray-700 disabled:opacity-30" title="前移" :disabled="index === 0" @click="movePhoto(index, -1)"><ChevronUp class="h-4 w-4" /></button>
            <button type="button" class="bg-white/90 p-1.5 text-gray-700 disabled:opacity-30" title="后移" :disabled="index === photos.length - 1" @click="movePhoto(index, 1)"><ChevronDown class="h-4 w-4" /></button>
            <button type="button" class="bg-red-500 p-1.5 text-white" title="删除照片" @click="removePhoto(photo)"><Trash2 class="h-4 w-4" /></button>
          </div>
        </div>
      </div>
    </section>

    <div v-if="editing" class="fixed inset-0 z-[120] flex items-center justify-center bg-black/50 p-4" @click.self="closeEditor">
      <div class="max-h-[92vh] w-full max-w-xl overflow-y-auto bg-white p-6 shadow-xl">
        <div class="mb-5 flex items-center justify-between">
          <h3 class="text-lg font-semibold text-gray-900">{{ form.id ? '编辑专辑' : '新增专辑' }}</h3>
          <button type="button" class="p-2 text-gray-400 hover:text-gray-700" title="关闭" @click="closeEditor"><X class="h-5 w-5" /></button>
        </div>
        <div class="space-y-4">
          <label class="block">
            <span class="mb-1 block text-sm text-gray-600">专辑名称</span>
            <input v-model.trim="form.title" class="w-full border border-gray-200 px-3 py-2 text-sm focus:border-mangrove-500 focus:outline-none" />
          </label>
          <label class="block">
            <span class="mb-1 block text-sm text-gray-600">简介</span>
            <textarea v-model.trim="form.description" rows="3" class="w-full resize-none border border-gray-200 px-3 py-2 text-sm focus:border-mangrove-500 focus:outline-none"></textarea>
          </label>
          <div>
            <span class="mb-1 block text-sm text-gray-600">独立封面</span>
            <div class="flex items-center gap-3">
              <div class="flex h-24 w-36 shrink-0 items-center justify-center overflow-hidden bg-gray-100">
                <img v-if="form.coverUrl" :src="form.coverUrl" class="h-full w-full object-cover" alt="专辑封面预览" />
                <ImageIcon v-else class="h-6 w-6 text-gray-300" />
              </div>
              <label class="inline-flex cursor-pointer items-center gap-2 border border-gray-200 px-3 py-2 text-sm text-gray-600 hover:border-mangrove-400">
                <Upload class="h-4 w-4" />{{ uploadingCover ? '上传中...' : '上传封面' }}
                <input type="file" accept="image/*" class="hidden" :disabled="uploadingCover" @change="uploadCover" />
              </label>
            </div>
          </div>
          <div>
            <span class="mb-1 block text-sm text-gray-600">内页照片</span>
            <label class="inline-flex cursor-pointer items-center gap-2 border border-gray-200 px-3 py-2 text-sm text-gray-600 hover:border-mangrove-400">
              <Images class="h-4 w-4" />选择照片（可多选）
              <input type="file" accept="image/*" multiple class="hidden" @change="queuePhotos" />
            </label>
            <p v-if="pendingPhotoFiles.length" class="mt-2 text-xs text-mangrove-700">已选择 {{ pendingPhotoFiles.length }} 张，保存专辑后自动上传。</p>
            <p v-else class="mt-2 text-xs text-gray-400">也可以保存专辑后，通过“管理 / 上传照片”继续添加。</p>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <label class="block"><span class="mb-1 block text-sm text-gray-600">排序</span><input v-model.number="form.sortOrder" type="number" class="w-full border border-gray-200 px-3 py-2 text-sm" /></label>
            <label class="block"><span class="mb-1 block text-sm text-gray-600">状态</span><select v-model.number="form.status" class="w-full border border-gray-200 px-3 py-2 text-sm"><option :value="1">上架</option><option :value="0">下架</option></select></label>
          </div>
        </div>
        <p v-if="formError" class="mt-4 text-sm text-red-600">{{ formError }}</p>
        <div class="mt-6 flex justify-end gap-2">
          <button type="button" class="btn-outline text-sm" @click="closeEditor">取消</button>
          <button type="button" class="btn-primary flex items-center gap-2 text-sm" :disabled="saving || uploadingCover" @click="saveAlbum"><Loader2 v-if="saving" class="h-4 w-4 animate-spin" />保存</button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ChevronDown, ChevronUp, Image as ImageIcon, Images, Loader2, Pencil, Plus, Trash2, Upload, X } from 'lucide-vue-next'

const albums = ref([])
const photos = ref([])
const selectedAlbum = ref(null)
const loading = ref(true)
const detailLoading = ref(false)
const error = ref('')
const editing = ref(false)
const saving = ref(false)
const uploadingCover = ref(false)
const uploadingPhotos = ref(false)
const pendingPhotoFiles = ref([])
const formError = ref('')
const form = reactive(emptyForm())

function emptyForm() { return { id: null, title: '', description: '', coverUrl: '', sortOrder: 0, status: 1 } }
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

async function upload(file) {
  const body = new FormData()
  body.append('file', file)
  return request('/api/files/upload', { method: 'POST', body })
}

async function loadAlbums() {
  loading.value = true
  error.value = ''
  try { albums.value = await request('/api/admin/music/albums') || [] }
  catch (e) { error.value = e.message }
  finally { loading.value = false }
}

function resetForm(value = emptyForm()) { Object.assign(form, value) }
function openCreate() { resetForm(); pendingPhotoFiles.value = []; formError.value = ''; editing.value = true }
function openEdit(album) { resetForm({ id: album.id, title: album.title, description: album.description || '', coverUrl: album.coverUrl, sortOrder: album.sortOrder || 0, status: album.status ?? 1 }); pendingPhotoFiles.value = []; formError.value = ''; editing.value = true }
function closeEditor() { if (!saving.value) editing.value = false }

function queuePhotos(event) {
  pendingPhotoFiles.value = [...(event.target.files || [])]
  event.target.value = ''
}

async function uploadCover(event) {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file) return
  uploadingCover.value = true
  formError.value = ''
  try { form.coverUrl = (await upload(file)).url }
  catch (e) { formError.value = e.message }
  finally { uploadingCover.value = false }
}

async function saveAlbum() {
  formError.value = ''
  if (!form.title) return void (formError.value = '请填写专辑名称')
  if (!form.coverUrl) return void (formError.value = '请上传专辑封面')
  saving.value = true
  try {
    const saved = await request(form.id ? `/api/admin/music/albums/${form.id}` : '/api/admin/music/albums', { method: form.id ? 'PUT' : 'POST', body: JSON.stringify(form) })
    form.id = saved.id
    let nextOrder = 0
    if (pendingPhotoFiles.value.length) {
      const detail = await request(`/api/admin/music/albums/${saved.id}`)
      nextOrder = detail.photos?.length || 0
    }
    while (pendingPhotoFiles.value.length) {
      const data = await upload(pendingPhotoFiles.value[0])
      await request(`/api/admin/music/albums/${saved.id}/photos`, { method: 'POST', body: JSON.stringify({ imageUrl: data.url, sortOrder: nextOrder++ }) })
      pendingPhotoFiles.value = pendingPhotoFiles.value.slice(1)
    }
    editing.value = false
    await loadAlbums()
    await selectAlbum(saved)
  } catch (e) { formError.value = e.message }
  finally { saving.value = false }
}

async function selectAlbum(album) {
  selectedAlbum.value = album
  detailLoading.value = true
  try { photos.value = (await request(`/api/admin/music/albums/${album.id}`)).photos || [] }
  catch (e) { error.value = e.message }
  finally { detailLoading.value = false }
}

async function removeAlbum(album) {
  if (!confirm(`确定删除专辑“${album.title}”及其照片记录吗？物理图片文件不会被删除。`)) return
  try {
    await request(`/api/admin/music/albums/${album.id}`, { method: 'DELETE' })
    if (selectedAlbum.value?.id === album.id) { selectedAlbum.value = null; photos.value = [] }
    await loadAlbums()
  } catch (e) { error.value = e.message }
}

async function uploadPhotos(event) {
  const files = [...(event.target.files || [])]
  event.target.value = ''
  if (!files.length || !selectedAlbum.value) return
  uploadingPhotos.value = true
  error.value = ''
  try {
    for (let index = 0; index < files.length; index++) {
      const data = await upload(files[index])
      await request(`/api/admin/music/albums/${selectedAlbum.value.id}/photos`, { method: 'POST', body: JSON.stringify({ imageUrl: data.url, sortOrder: photos.value.length + index }) })
    }
    await selectAlbum(selectedAlbum.value)
  } catch (e) { error.value = e.message }
  finally { uploadingPhotos.value = false }
}

async function movePhoto(index, direction) {
  const target = index + direction
  if (target < 0 || target >= photos.value.length) return
  const next = [...photos.value]
  ;[next[index], next[target]] = [next[target], next[index]]
  photos.value = next
  try {
    await request(`/api/admin/music/albums/${selectedAlbum.value.id}/photos/order`, { method: 'PUT', body: JSON.stringify({ items: next.map((photo, order) => ({ id: photo.id, sortOrder: order })) }) })
  } catch (e) { error.value = e.message; await selectAlbum(selectedAlbum.value) }
}

async function removePhoto(photo) {
  if (!confirm('确定从专辑中移除这张照片吗？物理图片文件不会被删除。')) return
  try { await request(`/api/admin/music/albums/${selectedAlbum.value.id}/photos/${photo.id}`, { method: 'DELETE' }); await selectAlbum(selectedAlbum.value) }
  catch (e) { error.value = e.message }
}

onMounted(loadAlbums)
</script>
