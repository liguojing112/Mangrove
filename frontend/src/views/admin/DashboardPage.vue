<template>
  <div>
    <!-- Stats -->
    <div class="grid grid-cols-2 sm:grid-cols-4 gap-2 sm:gap-4 mb-8">
      <div v-for="stat in stats" :key="stat.label" class="card p-5 flex items-start justify-between">
        <div>
          <p class="text-3xl font-bold text-gray-900">{{ stat.value }}</p>
          <p class="text-sm text-gray-500 mt-1">{{ stat.label }}</p>
        </div>
        <div class="w-10 h-10 rounded-xl bg-mangrove-50 flex items-center justify-center flex-shrink-0">
          <component :is="stat.icon" class="w-5 h-5 text-mangrove-600" />
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="grid grid-cols-2 gap-4 mb-8">
      <router-link to="/admin/media" class="card p-4 flex items-center gap-3 hover:shadow-lg transition-shadow cursor-pointer">
        <div class="w-10 h-10 rounded-xl bg-mangrove-100 flex items-center justify-center">
          <FolderOpen class="w-5 h-5 text-mangrove-600" />
        </div>
        <div>
          <p class="font-semibold text-gray-800 text-sm">资源管理</p>
          <p class="text-xs text-gray-500">管理全站照片和视频</p>
        </div>
      </router-link>
      <router-link to="/admin/audit" class="card p-4 flex items-center gap-3 hover:shadow-lg transition-shadow cursor-pointer">
        <div class="w-10 h-10 rounded-xl bg-mangrove-100 flex items-center justify-center">
          <ShieldCheck class="w-5 h-5 text-mangrove-600" />
        </div>
        <div>
          <p class="font-semibold text-gray-800 text-sm">内容审核</p>
          <p class="text-xs text-gray-500">审核用户上传和删除申请</p>
        </div>
      </router-link>
    </div>

    <!-- 照片馆藏 Hero 封面设置（5 张独立卡片） -->
    <div class="card p-5 mb-8">
      <h2 class="text-sm font-semibold text-gray-800 mb-1">🖼️ 照片馆藏 - Hero 卡片设置</h2>
      <p class="text-xs text-gray-400 mb-4">为照片页顶部的五张轮播大卡片分别上传照片（每张卡片有独立配色和标题）</p>
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-3">
        <div v-for="(card, idx) in heroCards" :key="card.id"
          class="rounded-xl border p-3 flex flex-col items-center gap-2"
          :style="{ borderColor: card.borderColor }">
          <!-- 色条 -->
          <div class="w-full h-1.5 rounded-full" :style="{ background: card.gradient }"></div>
          <!-- 图标 + 标题 -->
          <div class="flex items-center gap-1.5">
            <component :is="card.iconComp" :size="14" :style="{ color: card.accentColor }" />
            <span class="text-xs font-medium text-gray-700">{{ card.label }}</span>
          </div>
          <!-- 预览图 -->
          <div class="w-full aspect-[4/3] rounded-lg overflow-hidden bg-gray-100 border border-gray-200 flex items-center justify-center"
            :class="{ 'border-dashed': !card.url }">
            <img v-if="card.url" :src="card.url" class="w-full h-full object-cover" />
            <Image v-else class="w-8 h-8 text-gray-300" />
          </div>
          <!-- 操作 -->
          <div class="flex items-center gap-1.5 w-full">
            <label class="flex-1 text-center text-[11px] px-2 py-1.5 rounded-lg bg-gray-50 hover:bg-gray-100 border border-gray-200 cursor-pointer transition-colors text-gray-500">
              <Upload class="w-3 h-3 inline mr-1" />{{ card.url ? '更换' : '上传' }}
              <input type="file" accept="image/*" class="hidden" @change="e => handleHeroCardUpload(idx, e)" />
            </label>
            <button v-if="card.url" @click="removeHeroCard(idx)"
              class="text-[11px] px-2 py-1.5 rounded-lg text-red-400 hover:bg-red-50 border border-transparent hover:border-red-200 transition-colors"
              title="移除">✕</button>
          </div>
          <!-- 状态 -->
          <span v-if="card.uploading" class="text-[10px] text-gray-400">上传中...</span>
          <span v-else-if="card.url" class="text-[10px] text-green-500 flex items-center gap-0.5">
            <CheckCircle class="w-3 h-3" />已设置
          </span>
          <span v-else class="text-[10px] text-gray-300">未设置</span>
        </div>
      </div>
      <!-- 全局消息 -->
      <p v-if="heroCardsMsg" class="text-xs mt-3 text-center"
        :class="heroCardsMsg.startsWith('✓') ? 'text-green-600' : 'text-red-500'">{{ heroCardsMsg }}</p>
    </div>

    <!-- 芒果园背景图设置 -->
    <div class="card p-5 mb-8">
      <h2 class="text-sm font-semibold text-gray-800 mb-4">🌳 芒果园 - 背景图设置</h2>
      <div class="flex items-start gap-6">
        <div class="w-48 h-32 rounded-xl overflow-hidden bg-gray-100 border border-gray-200 flex items-center justify-center shrink-0">
          <img v-if="treeBgUrl" :src="treeBgUrl" class="w-full h-full object-cover" />
          <TreePine v-else class="w-8 h-8 text-gray-300" />
        </div>
        <div class="flex-1">
          <p class="text-sm text-gray-600 mb-3">上传芒果园大树页面的全屏背景图，建议 1920×1080 以上</p>
          <div class="flex items-center gap-3">
            <label class="btn-outline text-sm cursor-pointer flex items-center gap-2">
              <Upload class="w-4 h-4" /> 选择图片
              <input type="file" accept="image/*" class="hidden" @change="handleTreeBgUpload" />
            </label>
            <button v-if="treeBgUrl" class="btn-outline text-sm text-red-500 flex items-center gap-2" @click="removeTreeBg">
              <Trash2 class="w-4 h-4" /> 移除
            </button>
            <span v-if="treeBgUploading" class="text-sm text-gray-400">上传中...</span>
            <span v-else-if="treeBgUrl" class="text-sm text-green-600 flex items-center gap-1"><CheckCircle class="w-4 h-4" /> 已设置</span>
          </div>
          <p v-if="treeBgMsg" class="text-xs mt-2" :class="treeBgMsg.startsWith('✓') ? 'text-green-600' : 'text-red-500'">{{ treeBgMsg }}</p>
        </div>
      </div>
    </div>

    <!-- 小树背景图设置 -->
    <div class="card p-5 mb-8">
      <h2 class="text-sm font-semibold text-gray-800 mb-4">🌱 我的小树 - 背景图设置</h2>
      <div class="flex items-start gap-6">
        <div class="w-48 h-32 rounded-xl overflow-hidden bg-gray-100 border border-gray-200 flex items-center justify-center shrink-0">
          <img v-if="littleTreeBgUrl" :src="littleTreeBgUrl" class="w-full h-full object-cover" />
          <TreePine v-else class="w-8 h-8 text-gray-300" />
        </div>
        <div class="flex-1">
          <p class="text-sm text-gray-600 mb-3">上传"我的小树"页面的背景图，建议与芒果园风格一致</p>
          <div class="flex items-center gap-3">
            <label class="btn-outline text-sm cursor-pointer flex items-center gap-2">
              <Upload class="w-4 h-4" /> 选择图片
              <input type="file" accept="image/*" class="hidden" @change="handleLittleTreeBgUpload" />
            </label>
            <button v-if="littleTreeBgUrl" class="btn-outline text-sm text-red-500 flex items-center gap-2" @click="removeLittleTreeBg">
              <Trash2 class="w-4 h-4" /> 移除
            </button>
            <span v-if="littleTreeBgUploading" class="text-sm text-gray-400">上传中...</span>
            <span v-else-if="littleTreeBgUrl" class="text-sm text-green-600 flex items-center gap-1"><CheckCircle class="w-4 h-4" /> 已设置</span>
          </div>
          <p v-if="littleTreeBgMsg" class="text-xs mt-2" :class="littleTreeBgMsg.startsWith('✓') ? 'text-green-600' : 'text-red-500'">{{ littleTreeBgMsg }}</p>
        </div>
      </div>
    </div>

    <!-- 开场动画设置 -->
    <div class="card p-5 mb-8">
      <h2 class="text-sm font-semibold text-gray-800 mb-1">🎬 开场欢迎动画</h2>
      <p class="text-xs text-gray-400 mb-4">上传视频文件（手机端可能受限于浏览器策略无法自动播放，建议压缩到 500KB 以内）+ 上传一张封面图作为兜底展示</p>
      <div class="flex items-start gap-6">
        <div class="w-48 h-32 rounded-xl overflow-hidden bg-gray-900 border border-gray-200 flex items-center justify-center shrink-0">
          <video v-if="introAnimUrl" :src="introAnimUrl" muted preload="metadata" class="w-full h-full object-cover" @loadeddata="$event.target.currentTime=0.5" />
          <img v-else-if="introPosterUrl" :src="introPosterUrl" class="w-full h-full object-cover" />
          <Clapperboard v-else class="w-8 h-8 text-gray-600" />
        </div>
        <div class="flex-1">
          <p class="text-sm text-gray-600 mb-3">视频：支持 MP4/WEBM，建议高度压缩到 500KB 以内以确保手机端顺利加载</p>
          <div class="flex items-center gap-3 mb-3">
            <label class="btn-outline text-sm cursor-pointer flex items-center gap-2">
              <Upload class="w-4 h-4" /> {{ introAnimUrl ? '更换动画' : '上传动画' }}
              <input type="file" accept="video/mp4,video/webm" class="hidden" @change="handleIntroAnimUpload" />
            </label>
            <button v-if="introAnimUrl" class="btn-outline text-sm text-red-500 flex items-center gap-2" @click="removeIntroAnim">
              <Trash2 class="w-4 h-4" /> 移除
            </button>
            <span v-if="introAnimUploading" class="text-sm text-gray-400">上传中...</span>
            <span v-else-if="introAnimUrl" class="text-sm text-green-600 flex items-center gap-1"><CheckCircle class="w-4 h-4" /> 已设置</span>
          </div>
          <p class="text-sm text-gray-600 mb-1">封面图（兜底展示，视频无法播放时显示）：</p>
          <div class="flex items-center gap-3">
            <label class="btn-outline text-sm cursor-pointer flex items-center gap-2">
              <Image class="w-4 h-4" /> {{ introPosterUrl ? '更换封面' : '上传封面' }}
              <input type="file" accept="image/*" class="hidden" @change="handleIntroPosterUpload" />
            </label>
            <button v-if="introPosterUrl" class="btn-outline text-sm text-red-500 flex items-center gap-2" @click="removeIntroPoster">✕ 移除</button>
            <span v-if="introPosterUploading" class="text-sm text-gray-400">上传中...</span>
            <span v-else-if="introPosterUrl" class="text-sm text-green-600">✓</span>
          </div>
          <p v-if="introAnimMsg" class="text-xs mt-2" :class="introAnimMsg.startsWith('✓') ? 'text-green-600' : 'text-red-500'">{{ introAnimMsg }}</p>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Users, PenTool, Gift, FileText, ShieldCheck, Upload, Image, CheckCircle, FolderOpen, TreePine, Trash2, Sparkles, Star, Sun, MapPin, Eye, Clapperboard } from 'lucide-vue-next'

const stats = ref([
  { label: '艺人', value: 0, icon: Users },
  { label: '照片', value: 0, icon: FileText },
  { label: '视频', value: 0, icon: PenTool },
  { label: '用户', value: 0, icon: Gift },
])

const loading = ref(true)

// ── 照片馆藏 Hero 卡片（5 张） ──
const HERO_CARD_DEFS = [
  { id: 'spotlight', label: '聚光时刻', gradient: 'linear-gradient(90deg,#1b6842,#3ec97a)', borderColor: '#a3e6c0', accentColor: '#1b6842', iconComp: Sparkles },
  { id: 'stage',     label: '舞台光影', gradient: 'linear-gradient(90deg,#3b2d6e,#7c5ce0)', borderColor: '#c4b5fd', accentColor: '#7c5ce0', iconComp: Star },
  { id: 'daily',     label: '日常碎片', gradient: 'linear-gradient(90deg,#b85c1e,#f59e4b)', borderColor: '#fcd9a7', accentColor: '#f59e4b', iconComp: Sun },
  { id: 'event',     label: '活动掠影', gradient: 'linear-gradient(90deg,#1a4a8a,#4da6ff)', borderColor: '#a3d0ff', accentColor: '#4da6ff', iconComp: MapPin },
  { id: 'candid',    label: '路透珍藏', gradient: 'linear-gradient(90deg,#8b6914,#e8b830)', borderColor: '#fde68a', accentColor: '#e8b830', iconComp: Eye },
]

const heroCards = reactive(HERO_CARD_DEFS.map(def => ({
  ...def,
  url: '',
  uploading: false,
})))
const heroCardsMsg = ref('')

async function fetchHeroCards() {
  try {
    const res = await fetch('/api/admin/config/photos_hero_cards', {
      headers: { Authorization: `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      const urls = JSON.parse(json.data)
      if (Array.isArray(urls)) {
        urls.forEach((url, i) => {
          if (i < heroCards.length && url) heroCards[i].url = url
        })
      }
    }
  } catch {}
}

async function saveHeroCards() {
  const urls = heroCards.map(c => c.url || '')
  const res = await fetch('/api/admin/config/photos_hero_cards', {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` },
    body: JSON.stringify({ value: JSON.stringify(urls) })
  })
  if (!res.ok) throw new Error(`保存失败(${res.status})`)
}

async function handleHeroCardUpload(idx, e) {
  const file = e.target.files?.[0]
  if (!file) return
  heroCards[idx].uploading = true
  heroCardsMsg.value = ''
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await fetch('/api/files/upload', {
      method: 'POST',
      headers: { Authorization: `Bearer ${getToken()}` },
      body: formData
    })
    const json = await res.json()
    if (json.code !== 200) throw new Error(json.msg || '上传失败')
    heroCards[idx].url = json.data.url
    await saveHeroCards()
    heroCardsMsg.value = `✓ 「${HERO_CARD_DEFS[idx].label}」照片已更新`
    setTimeout(() => { heroCardsMsg.value = '' }, 3000)
  } catch (err) {
    heroCardsMsg.value = '✗ ' + err.message
  } finally {
    heroCards[idx].uploading = false
    e.target.value = ''
  }
}

async function removeHeroCard(idx) {
  heroCards[idx].url = ''
  try {
    await saveHeroCards()
    heroCardsMsg.value = `✓ 「${HERO_CARD_DEFS[idx].label}」照片已移除`
    setTimeout(() => { heroCardsMsg.value = '' }, 3000)
  } catch (err) {
    heroCardsMsg.value = '✗ ' + err.message
  }
}

function getToken() {
  return localStorage.getItem('mangrove_token')
}

// 芒果园背景图
const treeBgUrl = ref('')
const treeBgUploading = ref(false)
const treeBgMsg = ref('')

async function fetchTreeBg() {
  try {
    const res = await fetch('/api/admin/config/tree_background_url', {
      headers: { Authorization: `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) treeBgUrl.value = json.data
  } catch {}
}

async function handleTreeBgUpload(e) {
  const file = e.target.files?.[0]
  if (!file) return
  treeBgUploading.value = true
  treeBgMsg.value = ''
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await fetch('/api/files/upload', {
      method: 'POST',
      headers: { Authorization: `Bearer ${getToken()}` },
      body: formData
    })
    const json = await res.json()
    if (json.code !== 200) throw new Error(json.msg || '上传失败')
    const url = json.data.url
    await fetch('/api/admin/config/tree_background_url', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` },
      body: JSON.stringify({ value: url })
    })
    treeBgUrl.value = url
    treeBgMsg.value = '✓ 背景图已更新'
    setTimeout(() => { treeBgMsg.value = '' }, 3000)
  } catch (err) {
    treeBgMsg.value = '✗ ' + err.message
  } finally {
    treeBgUploading.value = false
    e.target.value = ''
  }
}

async function removeTreeBg() {
  if (!confirm('确认移除芒果园背景图？')) return
  try {
    await fetch('/api/admin/config/tree_background_url', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` },
      body: JSON.stringify({ value: '' })
    })
    treeBgUrl.value = ''
    treeBgMsg.value = '✓ 背景图已移除'
    setTimeout(() => { treeBgMsg.value = '' }, 3000)
  } catch (err) {
    treeBgMsg.value = '✗ ' + err.message
  }
}

// 小树背景图
const littleTreeBgUrl = ref('')
const littleTreeBgUploading = ref(false)
const littleTreeBgMsg = ref('')

async function fetchLittleTreeBg() {
  try {
    const res = await fetch('/api/admin/config/littletree_background_url', {
      headers: { Authorization: `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) littleTreeBgUrl.value = json.data
  } catch {}
}

async function handleLittleTreeBgUpload(e) {
  const file = e.target.files?.[0]
  if (!file) return
  littleTreeBgUploading.value = true
  littleTreeBgMsg.value = ''
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await fetch('/api/files/upload', {
      method: 'POST',
      headers: { Authorization: `Bearer ${getToken()}` },
      body: formData
    })
    const json = await res.json()
    if (json.code !== 200) throw new Error(json.msg || '上传失败')
    const url = json.data.url
    await fetch('/api/admin/config/littletree_background_url', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` },
      body: JSON.stringify({ value: url })
    })
    littleTreeBgUrl.value = url
    littleTreeBgMsg.value = '✓ 背景图已更新'
    setTimeout(() => { littleTreeBgMsg.value = '' }, 3000)
  } catch (err) {
    littleTreeBgMsg.value = '✗ ' + err.message
  } finally {
    littleTreeBgUploading.value = false
    e.target.value = ''
  }
}

async function removeLittleTreeBg() {
  if (!confirm('确认移除小树背景图？')) return
  try {
    await fetch('/api/admin/config/littletree_background_url', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` },
      body: JSON.stringify({ value: '' })
    })
    littleTreeBgUrl.value = ''
    littleTreeBgMsg.value = '✓ 背景图已移除'
    setTimeout(() => { littleTreeBgMsg.value = '' }, 3000)
  } catch (err) {
    littleTreeBgMsg.value = '✗ ' + err.message
  }
}

// 开场动画
const introAnimUrl = ref('')
const introAnimUploading = ref(false)
const introAnimMsg = ref('')
const introPosterUrl = ref('')
const introPosterUploading = ref(false)

async function fetchIntroAnim() {
  try {
    const res = await fetch('/api/admin/config/intro_animation_url', {
      headers: { Authorization: `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) introAnimUrl.value = json.data
  } catch {}
  try {
    const res = await fetch('/api/admin/config/intro_poster_url', {
      headers: { Authorization: `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) introPosterUrl.value = json.data
  } catch {}
}

async function handleIntroAnimUpload(e) {
  const file = e.target.files?.[0]
  if (!file) return
  introAnimUploading.value = true
  introAnimMsg.value = ''
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await fetch('/api/files/upload', {
      method: 'POST',
      headers: { Authorization: `Bearer ${getToken()}` },
      body: formData
    })
    const json = await res.json()
    if (json.code !== 200) throw new Error(json.msg || '上传失败')
    const url = json.data.url
    await fetch('/api/admin/config/intro_animation_url', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` },
      body: JSON.stringify({ value: url })
    })
    introAnimUrl.value = url
    introAnimMsg.value = '✓ 开场动画已更新'
    setTimeout(() => { introAnimMsg.value = '' }, 3000)
  } catch (err) {
    introAnimMsg.value = '✗ ' + err.message
  } finally {
    introAnimUploading.value = false
    e.target.value = ''
  }
}

async function removeIntroAnim() {
  if (!confirm('确认移除开场动画？')) return
  try {
    await fetch('/api/admin/config/intro_animation_url', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` },
      body: JSON.stringify({ value: '' })
    })
    introAnimUrl.value = ''
    introAnimMsg.value = '✓ 开场动画已移除'
    setTimeout(() => { introAnimMsg.value = '' }, 3000)
  } catch (err) {
    introAnimMsg.value = '✗ ' + err.message
  }
}

async function handleIntroPosterUpload(e) {
  const file = e.target.files?.[0]
  if (!file) return
  introPosterUploading.value = true
  introAnimMsg.value = ''
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await fetch('/api/files/upload', {
      method: 'POST',
      headers: { Authorization: `Bearer ${getToken()}` },
      body: formData
    })
    const json = await res.json()
    if (json.code !== 200) throw new Error(json.msg || '上传失败')
    const url = json.data.url
    await fetch('/api/admin/config/intro_poster_url', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` },
      body: JSON.stringify({ value: url })
    })
    introPosterUrl.value = url
    introAnimMsg.value = '✓ 封面图已更新'
    setTimeout(() => { introAnimMsg.value = '' }, 3000)
  } catch (err) {
    introAnimMsg.value = '✗ ' + err.message
  } finally {
    introPosterUploading.value = false
    e.target.value = ''
  }
}

async function removeIntroPoster() {
  try {
    await fetch('/api/admin/config/intro_poster_url', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` },
      body: JSON.stringify({ value: '' })
    })
    introPosterUrl.value = ''
    introAnimMsg.value = '✓ 封面图已移除'
    setTimeout(() => { introAnimMsg.value = '' }, 3000)
  } catch (err) {
    introAnimMsg.value = '✗ ' + err.message
  }
}

onMounted(async () => {
  fetchTreeBg()
  fetchLittleTreeBg()
  fetchHeroCards()
  fetchIntroAnim()
  try {
    const res = await fetch('/api/admin/dashboard', {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      const d = json.data
      if (d.stats) {
        stats.value = [
          { label: '艺人', value: d.stats.artistCount || 0, icon: Users },
          { label: '照片', value: d.stats.photoCount || 0, icon: FileText },
          { label: '视频', value: d.stats.videoCount || 0, icon: PenTool },
          { label: '用户', value: d.stats.userCount || 0, icon: Gift },
        ]
      }
    }
  } catch {
    // 保持默认 0
  } finally {
    loading.value = false
  }
})

</script>
