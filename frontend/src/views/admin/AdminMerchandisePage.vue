<template>
  <div class="min-w-0">
    <div class="mb-6 flex flex-wrap items-center justify-between gap-3">
      <div>
        <h2 class="text-lg font-semibold text-gray-900">周边管理</h2>
        <p class="mt-1 text-xs text-gray-500">小卡和纸质周边支持正反面，其余周边上传单张图片。</p>
      </div>
      <button type="button" class="btn-primary flex items-center gap-2 text-sm" @click="openCreate"><Plus class="h-4 w-4" />新增周边</button>
    </div>

    <div v-if="error" class="mb-4 border-l-4 border-red-400 bg-red-50 px-4 py-3 text-sm text-red-700">{{ error }}</div>

    <div class="card overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead><tr class="bg-gray-50 text-left text-xs font-medium text-gray-500"><th class="px-4 py-3">图片</th><th class="px-4 py-3">名称</th><th class="px-4 py-3">类型</th><th class="px-4 py-3">分类</th><th class="px-4 py-3">产出者</th><th class="px-4 py-3">稀有度</th><th class="px-4 py-3 text-right">操作</th></tr></thead>
          <tbody>
            <tr v-for="item in items" :key="item.id" class="border-b border-gray-100 last:border-b-0">
              <td class="px-4 py-3"><div class="flex gap-1.5"><img v-if="displayImage(item)" :src="displayImage(item)" alt="" class="h-14 w-10 rounded object-cover" /><img v-if="isDoubleCategory(item.category) && item.cardBackImageUrl" :src="item.cardBackImageUrl" alt="" class="h-14 w-10 rounded object-cover" /></div></td>
              <td class="max-w-xs px-4 py-3 font-medium text-gray-900"><p class="truncate">{{ item.name }}</p></td>
              <td class="px-4 py-3 text-gray-600">{{ categoryLabel(item.category) }}</td>
              <td class="px-4 py-3 text-gray-500">{{ item.subCategory || '-' }}</td>
              <td class="px-4 py-3 text-gray-500">{{ item.producerName || '站点' }}</td>
              <td class="px-4 py-3 text-gray-500">{{ item.rarity || 'COMMON' }}</td>
              <td class="px-4 py-3"><div class="flex justify-end gap-1"><button type="button" class="p-2 text-gray-400 hover:text-blue-600" title="编辑" @click="openEdit(item)"><Edit3 class="h-4 w-4" /></button><button type="button" class="p-2 text-gray-400 hover:text-red-500" title="删除" @click="removeItem(item)"><Trash2 class="h-4 w-4" /></button></div></td>
            </tr>
            <tr v-if="!loading && items.length === 0"><td colspan="7" class="px-4 py-16 text-center text-sm text-gray-400">暂无周边，请先新增</td></tr>
          </tbody>
        </table>
      </div>
      <div v-if="loading" class="py-16 text-center text-sm text-gray-400"><Loader2 class="mx-auto mb-2 h-6 w-6 animate-spin" />加载中...</div>
    </div>

    <div v-if="editing" class="fixed inset-0 z-[80] flex items-center justify-center bg-black/55 p-4" @click.self="closeEditor">
      <div class="max-h-[92vh] w-full max-w-3xl overflow-y-auto bg-white p-6 shadow-xl">
        <div class="mb-5 flex items-center justify-between"><h3 class="text-lg font-semibold text-gray-900">{{ form.id ? '编辑周边' : '新增周边' }}</h3><button type="button" class="p-2 text-gray-400 hover:text-gray-700" title="关闭" @click="closeEditor"><X class="h-5 w-5" /></button></div>
        <div class="grid gap-4 sm:grid-cols-2">
          <label class="block sm:col-span-2"><span class="mb-1 block text-sm text-gray-600">{{ form.category === 'BUNDLE' ? '套装名称' : '名称' }}</span><input v-model.trim="form.name" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm" /></label>
          <label class="block"><span class="mb-1 block text-sm text-gray-600">类型</span><select v-model="form.category" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm" @change="syncSubCategory"><option value="PHOTOCARD">小卡</option><option value="HAND_BANNER">纸质周边</option><option value="PHYSICAL">实物应援</option><option value="DIGITAL">电子周边</option><option value="ARCHIVE">往期应援存档</option><option value="BUNDLE">产出周边套装</option></select></label>
          <label class="block"><span class="mb-1 block text-sm text-gray-600">分类</span><select v-model="form.subCategory" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm"><option v-for="option in subCategoryOptions" :key="option" :value="option">{{ option }}</option></select></label>
          <label class="block"><span class="mb-1 block text-sm text-gray-600">稀有度</span><select v-model="form.rarity" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm"><option value="COMMON">COMMON</option><option value="RARE">RARE</option><option value="LIMITED">LIMITED</option><option value="LEGENDARY">LEGENDARY</option></select></label>
          <label class="block"><span class="mb-1 block text-sm text-gray-600">发布日期</span><input v-model="form.publishDate" type="date" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm" /></label>
          <label class="block sm:col-span-2"><span class="mb-1 block text-sm text-gray-600">{{ form.category === 'BUNDLE' ? '设计介绍' : '描述' }}</span><textarea v-model.trim="form.description" rows="3" class="w-full resize-none rounded-lg border border-gray-200 px-3 py-2 text-sm"></textarea></label>

          <template v-if="isDoubleCategory(form.category)">
            <ImageUploadField :label="form.category === 'PHOTOCARD' ? '卡面（左）' : '正面（左）'" :url="form.cardFrontImageUrl" :uploading="uploadingKey === 'cardFrontImageUrl'" @upload="uploadImage($event, 'cardFrontImageUrl')" @clear="clearImage('cardFrontImageUrl')" />
            <ImageUploadField :label="form.category === 'PHOTOCARD' ? '卡背（右）' : '背面（右）'" :url="form.cardBackImageUrl" :uploading="uploadingKey === 'cardBackImageUrl'" @upload="uploadImage($event, 'cardBackImageUrl')" @clear="clearImage('cardBackImageUrl')" />
          </template>
          <template v-else-if="form.category === 'BUNDLE'">
            <div class="sm:col-span-2">
              <div class="mb-3 flex items-center justify-between"><span class="text-sm text-gray-600">套装周边图片</span><button type="button" class="btn-outline flex items-center gap-1 text-xs" @click="addBundleImage"><Plus class="h-3.5 w-3.5" />添加图片</button></div>
              <div class="grid gap-4 sm:grid-cols-3">
                <ImageUploadField v-for="(url, index) in form.bundleImageUrls" :key="index" :label="`周边 ${index + 1}`" :url="url" :uploading="uploadingKey === `bundle-${index}`" wide @upload="uploadBundleImage($event, index)" @clear="removeBundleImage(index)" />
              </div>
            </div>
          </template>
          <ImageUploadField v-else class="sm:col-span-2" label="周边图片" :url="form.highResImageUrl" :uploading="uploadingKey === 'highResImageUrl'" wide @upload="uploadImage($event, 'highResImageUrl')" @clear="clearImage('highResImageUrl')" />
        </div>
        <p v-if="formError" class="mt-4 text-sm text-red-600">{{ formError }}</p>
        <div class="mt-6 flex justify-end gap-2"><button type="button" class="btn-outline text-sm" @click="closeEditor">取消</button><button type="button" class="btn-primary text-sm" :disabled="saving || uploadingKey !== ''" @click="saveItem">{{ saving ? '保存中...' : '保存' }}</button></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { Edit3, Loader2, Plus, Trash2, X } from 'lucide-vue-next'
import ImageUploadField from '@/components/admin/ImageUploadField.vue'

const categoryOptions = {
  PHOTOCARD: ['官方卡', '饭制卡', '随机卡', '全套图鉴'],
  HAND_BANNER: ['手幅', '明信片', '书签', '贴纸', '台历', '海报'],
  PHYSICAL: ['徽章', '立牌', '钥匙扣', '透卡', '收纳周边'],
  DIGITAL: ['壁纸', '头像', '锁屏', '模板', '表情包'],
  ARCHIVE: ['生日应援', '回归应援', '线下活动', '周年纪念'],
  BUNDLE: ['周边套装']
}
const items = ref([])
const loading = ref(true)
const error = ref('')
const editing = ref(false)
const saving = ref(false)
const uploadingKey = ref('')
const formError = ref('')
const form = reactive(emptyForm())
const subCategoryOptions = computed(() => {
  const options = categoryOptions[form.category] || []
  return form.subCategory && !options.includes(form.subCategory) ? [form.subCategory, ...options] : options
})

function emptyForm() { return { id: null, name: '', category: 'PHOTOCARD', subCategory: '官方卡', description: '', highResImageUrl: '', thumbnailUrl: '', cardFrontImageUrl: '', cardBackImageUrl: '', bundleImageUrls: [''], tags: '', publishDate: '', rarity: 'COMMON' } }
function token() { return localStorage.getItem('mangrove_token') || '' }
function isDoubleCategory(category) { return category === 'PHOTOCARD' || category === 'HAND_BANNER' }
function displayImage(item) { return item.bundleImageUrls?.[0] || item.cardFrontImageUrl || item.thumbnailUrl || item.highResImageUrl }
function categoryLabel(value) { return { PHOTOCARD: '小卡', HAND_BANNER: '纸质周边', PHYSICAL: '实物应援', DIGITAL: '电子周边', ARCHIVE: '往期应援存档', BUNDLE: '产出周边套装' }[value] || value }
function syncSubCategory() { form.subCategory = categoryOptions[form.category]?.[0] || '' }
async function request(url, options = {}) {
  const isForm = options.body instanceof FormData
  const response = await fetch(url, { ...options, headers: { ...(isForm ? {} : { 'Content-Type': 'application/json' }), Authorization: `Bearer ${token()}`, ...(options.headers || {}) } })
  const json = await response.json()
  if (!response.ok || json.code !== 200) throw new Error(json.msg || '请求失败')
  return json.data
}
async function loadItems() { loading.value = true; error.value = ''; try { items.value = (await request('/api/admin/merchandise?page=0&size=100'))?.content || [] } catch (e) { error.value = e.message } finally { loading.value = false } }
function openCreate() { Object.assign(form, emptyForm()); formError.value = ''; editing.value = true }
function openEdit(item) { Object.assign(form, emptyForm(), item, { publishDate: item.publishDate || '', bundleImageUrls: item.bundleImageUrls?.length ? [...item.bundleImageUrls] : [''] }); formError.value = ''; editing.value = true }
function closeEditor() { if (!saving.value && !uploadingKey.value) editing.value = false }
function clearImage(key) { form[key] = ''; if (key === 'cardFrontImageUrl') { form.highResImageUrl = ''; form.thumbnailUrl = '' } }
async function uploadImage(event, key) {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file) return
  uploadingKey.value = key
  formError.value = ''
  try { const body = new FormData(); body.append('file', file); const data = await request('/api/files/upload', { method: 'POST', body }); form[key] = data.url; if (key === 'cardFrontImageUrl') { form.highResImageUrl = data.url; form.thumbnailUrl = data.url } } catch (e) { formError.value = e.message } finally { uploadingKey.value = '' }
}
function addBundleImage() { form.bundleImageUrls.push('') }
function removeBundleImage(index) { form.bundleImageUrls.splice(index, 1); if (!form.bundleImageUrls.length) form.bundleImageUrls.push('') }
async function uploadBundleImage(event, index) {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file) return
  uploadingKey.value = `bundle-${index}`
  formError.value = ''
  try { const body = new FormData(); body.append('file', file); const data = await request('/api/files/upload', { method: 'POST', body }); form.bundleImageUrls[index] = data.url } catch (e) { formError.value = e.message } finally { uploadingKey.value = '' }
}
async function saveItem() {
  formError.value = ''
  if (!form.name) return void (formError.value = '请填写周边名称')
  if (isDoubleCategory(form.category) && !form.cardFrontImageUrl) return void (formError.value = form.category === 'PHOTOCARD' ? '请上传小卡卡面' : '请上传纸质周边正面')
  if (form.category === 'BUNDLE') {
    form.bundleImageUrls = form.bundleImageUrls.filter(Boolean)
    if (!form.description) return void (formError.value = '请填写设计介绍')
    if (!form.bundleImageUrls.length) return void (formError.value = '请至少上传一张套装周边图片')
    form.highResImageUrl = form.bundleImageUrls[0]
    form.thumbnailUrl = form.bundleImageUrls[0]
  }
  if (!isDoubleCategory(form.category) && form.category !== 'BUNDLE' && !form.highResImageUrl) return void (formError.value = '请上传周边图片')
  if (isDoubleCategory(form.category)) { form.highResImageUrl = form.cardFrontImageUrl; form.thumbnailUrl = form.cardFrontImageUrl }
  saving.value = true
  try { await request(form.id ? `/api/admin/merchandise/${form.id}` : '/api/admin/merchandise', { method: form.id ? 'PUT' : 'POST', body: JSON.stringify(form) }); editing.value = false; await loadItems() } catch (e) { formError.value = e.message } finally { saving.value = false }
}
async function removeItem(item) { if (!confirm(`确定删除周边”${item.name}”吗？上传文件不会被删除。`)) return; try { await request(`/api/admin/merchandise/${item.id}`, { method: 'DELETE' }); await loadItems() } catch (e) { error.value = e.message } }
onMounted(loadItems)
</script>
