<template>
  <div class="bg-gray-950 min-h-screen">
    <!-- PAGE HEADER -->
    <section class="pt-24 pb-12 text-center">
      <p class="text-amber-400/60 tracking-[0.3em] text-xs mb-2">CREATOR HALL</p>
      <h1 class="text-white text-4xl font-light tracking-wide">创作者殿堂</h1>
      <p class="text-gray-500 text-sm mt-3">独家高端产出专区</p>
      <div class="w-24 h-px bg-gradient-to-r from-transparent via-amber-400 to-transparent mx-auto mt-6"></div>
      <div class="mt-6">
        <button class="px-6 py-2 rounded-full bg-amber-500 text-gray-900 text-sm font-medium hover:bg-amber-400 transition-colors" @click="showSubmitForm = true">
          投稿作品
        </button>
        <p class="text-xs text-gray-600 mt-2">高清无水印图片更容易通过审核</p>
      </div>
    </section>

    <div class="sticky top-16 z-40 bg-gray-950/80 backdrop-blur-md border-b border-gray-800/50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-3 flex gap-2 justify-center flex-wrap">
        <button v-for="s in nav" :key="s.id" class="px-4 py-1.5 rounded-full text-xs font-medium text-gray-400 hover:text-amber-400 hover:bg-amber-500/10 border border-gray-800 hover:border-amber-500/30 transition-all" @click="scrollTo(s.id)">{{ s.label }}</button>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 space-y-24">

      <!-- 高清路透专区 -->
      <section id="s1" class="space-y-6 scroll-mt-28">
        <div class="flex items-center gap-3">
          <span class="w-1 h-8 bg-amber-400 rounded-full"></span>
          <div><h2 class="text-2xl font-light text-white">高清路透专区</h2><p class="text-gray-500 text-xs mt-0.5">线下活动 · 机场上班路 · 无水印精修存档</p></div>
          <span class="ml-auto bg-red-500/20 text-red-400 text-xs px-2 py-0.5 rounded-full">HOT</span>
        </div>
        <div v-if="loading" class="text-center py-8">
          <p class="text-gray-400">加载中...</p>
        </div>
        <div v-else-if="previewItems.length === 0" class="text-center py-8">
          <p class="text-gray-400">暂无作品</p>
        </div>
        <div v-else class="flex gap-4 overflow-x-auto pb-2 -mx-4 px-4">
          <div v-for="item in previewItems" :key="item.id" class="shrink-0 w-[300px] md:w-[360px] group cursor-pointer">
            <div class="aspect-[16/10] rounded-xl overflow-hidden border border-gray-800 hover:border-amber-500/30 transition-all bg-gradient-to-br from-gray-800 to-gray-900 relative">
              <img v-if="getItemImage(item)" :src="getItemImage(item)" class="absolute inset-0 w-full h-full object-cover" />
              <div v-else class="absolute inset-0 flex items-center justify-center"><span class="text-4xl opacity-15">📸</span></div>
              <div class="absolute top-3 left-3"><span class="px-2 py-0.5 rounded-full text-[10px] bg-amber-500/20 text-amber-400">{{ item.extraData?.category || getWorkById(item.targetId)?.category || '作品' }}</span></div>
              <div class="absolute bottom-0 left-0 right-0 p-4 bg-gradient-to-t from-black/90">
                <h3 class="text-white text-sm font-medium truncate">{{ item.extraData?.title || getWorkById(item.targetId)?.title || '未命名' }}</h3>
                <div class="flex items-center gap-3 mt-1 text-xs text-gray-400"><span>👁 {{ getWorkById(item.targetId)?.viewCount || 0 }}</span></div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 优秀产出创作者榜 -->
      <section id="s2" class="space-y-6 scroll-mt-28">
        <div class="flex items-center gap-3">
          <span class="w-1 h-8 bg-amber-400 rounded-full"></span>
          <div><h2 class="text-2xl font-light text-white">优秀产出创作者榜</h2><p class="text-gray-500 text-xs mt-0.5">修图太太 · 剪辑太太 · 饭绘太太 · 文案博主</p></div>
        </div>
        <div v-if="loading" class="text-center py-8">
          <p class="text-gray-400">加载中...</p>
        </div>
        <div v-else-if="rankItems.length === 0" class="text-center py-8">
          <p class="text-gray-400">暂无创作者</p>
        </div>
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div v-for="(item, index) in rankItems" :key="item.id" class="rounded-xl bg-gray-900 border border-gray-800 p-5 hover:border-amber-500/20 transition-all">
            <div class="flex items-center gap-4">
              <div :class="['w-10 h-10 rounded-full flex items-center justify-center shrink-0 text-sm font-bold', index < 3 ? 'bg-amber-400/20 text-amber-400' : 'bg-gray-700 text-gray-400']">{{ index + 1 }}</div>
              <div class="mx-auto flex w-12 h-12 items-center justify-center rounded-full border text-lg font-semibold shrink-0" :style="avatarStyle(item.extraData?.publicId)">
                {{ avatarText(item.extraData?.username) }}
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-white truncate">{{ item.extraData?.nickname || getCreatorById(item.targetId)?.nickname || getCreatorById(item.targetId)?.username || '用户' }}</p>
                <p class="text-xs text-gray-500">{{ item.extraData?.username || '' }}</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 精选作品展厅 -->
      <section id="s3" class="space-y-6 scroll-mt-28">
        <div class="flex items-center gap-3">
          <span class="w-1 h-8 bg-amber-400 rounded-full"></span>
          <div><h2 class="text-2xl font-light text-white">精选作品展厅</h2><p class="text-gray-500 text-xs mt-0.5">月度最佳修图 · 剪辑 · 饭绘 · 原创图文</p></div>
          <span class="ml-auto text-amber-400 text-xs px-2 py-0.5 rounded-full border border-amber-500/30">月度精选</span>
        </div>
        <div v-if="loading" class="text-center py-8">
          <p class="text-gray-400">加载中...</p>
        </div>
        <div v-else-if="showcaseItems.length === 0" class="text-center py-8">
          <p class="text-gray-400">暂无作品</p>
        </div>
        <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div v-for="item in showcaseItems" :key="item.id" class="group cursor-pointer">
            <div class="rounded-2xl overflow-hidden border border-gray-800 hover:border-amber-500/30 transition-all bg-gradient-to-br from-gray-800 to-gray-900">
              <div class="aspect-[4/3] flex items-center justify-center relative bg-gradient-to-br from-gray-900 to-gray-800">
                <img v-if="getShowcaseImage(item)" :src="getShowcaseImage(item)" class="w-full h-full object-cover" />
                <span v-else class="text-4xl opacity-20">🖼️</span>
                <div class="absolute top-3 left-3"><span class="px-2 py-0.5 rounded-full text-[10px] font-medium bg-amber-500/20 text-amber-400">{{ item.itemType === 'WORK' ? (item.extraData?.category || '作品') : '创作者' }}</span></div>
              </div>
              <div class="p-5">
                <h3 class="text-white font-medium">{{ getShowcaseTitle(item) }}</h3>
                <div class="flex items-center gap-2 mt-2">
                  <div class="mx-auto flex w-6 h-6 items-center justify-center rounded-full border text-[10px]" :style="avatarStyle(item.extraData?.publicId)">
                    {{ avatarText(item.extraData?.username) }}
                  </div>
                  <span class="text-xs text-gray-400">{{ item.extraData?.creatorNickname || item.extraData?.nickname || '匿名' }}</span>
                </div>
                <p v-if="item.itemType === 'WORK' && item.extraData?.description" class="text-gray-500 text-xs mt-2 line-clamp-2">{{ item.extraData.description }}</p>
                <p v-if="item.itemType === 'CREATOR' && item.extraData?.username" class="text-gray-500 text-xs mt-1">@{{ item.extraData.username }}</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 创作者荣誉墙 -->
      <section id="s4" class="space-y-6 scroll-mt-28">
        <div class="flex items-center gap-3">
          <span class="w-1 h-8 bg-amber-400 rounded-full"></span>
          <div><h2 class="text-2xl font-light text-white">创作者荣誉墙</h2><p class="text-gray-500 text-xs mt-0.5">长期高产 · 高质量创作者公示</p></div>
        </div>
        <div v-if="loading" class="text-center py-8">
          <p class="text-gray-400">加载中...</p>
        </div>
        <div v-else-if="honorItems.length === 0" class="text-center py-8">
          <p class="text-gray-400">暂无创作者</p>
        </div>
        <div v-else class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div v-for="(item, index) in honorItems" :key="item.id" class="rounded-xl bg-gray-900 border border-gray-800 p-5 text-center hover:border-amber-500/20 transition-all">
            <div :class="['text-4xl font-bold mb-2', index < 3 ? 'text-amber-400' : 'text-gray-600']">{{ index + 1 }}</div>
            <div class="mx-auto flex w-14 h-14 items-center justify-center rounded-full border text-xl font-semibold" :style="avatarStyle(item.extraData?.publicId)">
              {{ avatarText(item.extraData?.username) }}
            </div>
            <p class="text-sm font-medium text-white mt-3">{{ item.extraData?.nickname || getCreatorById(item.targetId)?.nickname || getCreatorById(item.targetId)?.username || '用户' }}</p>
            <p class="text-xs text-gray-500 mt-1">{{ item.extraData?.username || '' }}</p>
          </div>
        </div>
      </section>

      <!-- 高清路透专区 -->
      <section id="s1" class="space-y-6 scroll-mt-28">
        <div class="flex items-center gap-3">
          <span class="w-1 h-8 bg-amber-400 rounded-full"></span>
          <div><h2 class="text-2xl font-light text-white">高清路透专区</h2><p class="text-gray-500 text-xs mt-0.5">线下活动 · 机场上班路 · 无水印精修存档</p></div>
          <span class="ml-auto bg-red-500/20 text-red-400 text-xs px-2 py-0.5 rounded-full">HOT</span>
        </div>
        <div v-if="loading" class="text-center py-8">
          <p class="text-gray-400">加载中...</p>
        </div>
        <div v-else-if="previewItems.length === 0" class="text-center py-8">
          <p class="text-gray-400">暂无作品</p>
        </div>
        <div v-else class="flex gap-4 overflow-x-auto pb-2 -mx-4 px-4">
          <div v-for="item in previewItems" :key="item.id" class="shrink-0 w-[300px] md:w-[360px] group cursor-pointer">
            <div class="aspect-[16/10] rounded-xl overflow-hidden border border-gray-800 hover:border-amber-500/30 transition-all bg-gradient-to-br from-gray-800 to-gray-900 relative">
              <img v-if="getItemImage(item)" :src="getItemImage(item)" class="absolute inset-0 w-full h-full object-cover" />
              <div v-else class="absolute inset-0 flex items-center justify-center"><span class="text-4xl opacity-15">📸</span></div>
              <div class="absolute top-3 left-3"><span class="px-2 py-0.5 rounded-full text-[10px] bg-amber-500/20 text-amber-400">{{ item.extraData?.category || getWorkById(item.targetId)?.category || '作品' }}</span></div>
              <div class="absolute bottom-0 left-0 right-0 p-4 bg-gradient-to-t from-black/90">
                <h3 class="text-white text-sm font-medium truncate">{{ item.extraData?.title || getWorkById(item.targetId)?.title || '未命名' }}</h3>
                <div class="flex items-center gap-3 mt-1 text-xs text-gray-400"><span>👁 {{ getWorkById(item.targetId)?.viewCount || 0 }}</span></div>
              </div>
            </div>
          </div>
        </div>
      </section>

    </div>

    <!-- Submit Modal -->
    <div v-if="showSubmitForm" class="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm p-4" @click.self="showSubmitForm = false">
      <div class="bg-gray-900 rounded-2xl p-6 w-full max-w-lg max-h-[90vh] overflow-y-auto border border-gray-800">
        <h2 class="text-xl font-semibold text-white mb-4">投稿作品</h2>
        <div class="space-y-4">
          <div>
            <label class="text-sm text-gray-400 mb-1 block">作品标题</label>
            <input v-model="submitForm.title" placeholder="输入作品标题" class="w-full bg-gray-800 border border-gray-700 rounded-lg p-3 text-white text-sm focus:outline-none focus:border-amber-500" />
          </div>
          <div>
            <label class="text-sm text-gray-400 mb-1 block">作品描述</label>
            <textarea v-model="submitForm.description" placeholder="描述你的作品..." class="w-full bg-gray-800 border border-gray-700 rounded-lg p-3 text-white text-sm min-h-[100px] resize-none focus:outline-none focus:border-amber-500"></textarea>
          </div>
          <div>
            <label class="text-sm text-gray-400 mb-1 block">作品分类</label>
            <select v-model="submitForm.category" class="w-full bg-gray-800 border border-gray-700 rounded-lg p-3 text-white text-sm focus:outline-none focus:border-amber-500">
              <option value="PREVIEW">路透图</option>
              <option value="EDITED_PHOTO">剪辑修图</option>
              <option value="VIDEO_EDIT">视频剪辑</option>
            </select>
          </div>
          <div>
            <label class="text-sm text-gray-400 mb-1 block">上传图片</label>
            <div class="bg-pink-50 border-2 border-dashed border-pink-200 rounded-lg p-4 text-center min-h-[120px] flex flex-col items-center justify-center gap-3">
              <div v-if="submitForm.fileUrl" class="text-center">
                <img :src="submitForm.fileUrl" class="max-h-32 mx-auto mb-2 rounded" />
                <button type="button" class="text-xs text-red-400 hover:text-red-300" @click="submitForm.fileUrl = ''">移除图片</button>
              </div>
              <div v-else class="text-center">
                <p class="text-gray-400 text-xs mb-2">暂无图片</p>
                <button type="button" @click="$refs.workFileInput.click()" class="inline-block px-4 py-2 bg-pink-100 text-pink-600 rounded-lg text-sm cursor-pointer hover:bg-pink-200 transition-colors">
                  上传图片
                </button>
                <input type="file" accept="image/*" @change="handleUpload" class="hidden" ref="workFileInput" />
              </div>
            </div>
            <p class="text-xs text-gray-500 mt-1">支持上传图片，自动获取链接</p>
          </div>
          <div>
            <label class="text-sm text-gray-400 mb-1 block">文件链接（可选）</label>
            <input v-model="submitForm.fileUrl" placeholder="如果已上传图片，可不填" class="w-full bg-gray-800 border border-gray-700 rounded-lg p-3 text-white text-sm focus:outline-none focus:border-amber-500" />
          </div>
          <div>
            <label class="text-sm text-gray-400 mb-1 block">封面链接（可选）</label>
            <input v-model="submitForm.coverUrl" placeholder="输入封面图URL" class="w-full bg-gray-800 border border-gray-700 rounded-lg p-3 text-white text-sm focus:outline-none focus:border-amber-500" />
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <button class="px-4 py-2 rounded-lg text-gray-400 hover:text-white transition-colors" @click="showSubmitForm = false">取消</button>
          <button class="px-6 py-2 rounded-lg bg-amber-500 text-gray-900 font-medium hover:bg-amber-400 transition-colors" @click="submitWork">
            提交审核
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const showSubmitForm = ref(false)
const previewItems = ref([])
const rankItems = ref([])
const showcaseItems = ref([])
const honorItems = ref([])
const allWorks = ref([])
const allCreators = ref([])
const loading = ref(true)
const submitForm = ref({
  title: '',
  description: '',
  category: 'PREVIEW',
  fileUrl: '',
  coverUrl: ''
})

async function fetchSectionItems(section) {
  try {
    const res = await fetch(`/api/public/work-sections/${section}`)
    const json = await res.json()
    if (json.code === 200) {
      return json.data || []
    }
  } catch (e) {
    console.error(`获取${section}数据失败:`, e)
  }
  return []
}

async function fetchAllWorks() {
  try {
    const res = await fetch('/api/works?page=0&size=100')
    const json = await res.json()
    if (json.code === 200 && json.data) {
      allWorks.value = json.data.content || []
    }
  } catch (e) {
    console.error('获取作品失败:', e)
  }
}

async function fetchAllCreators() {
  try {
    const res = await fetch('/api/admin/users?page=0&size=100')
    const json = await res.json()
    if (json.code === 200 && json.data) {
      allCreators.value = json.data.content || []
    }
  } catch (e) {
    console.error('获取用户失败:', e)
  }
}

async function fetchAllData() {
  loading.value = true
  try {
    const [preview, rank, showcase, honor] = await Promise.all([
      fetchSectionItems('PREVIEW'),
      fetchSectionItems('RANK'),
      fetchSectionItems('SHOWCASE'),
      fetchSectionItems('HONOR')
    ])
    previewItems.value = preview
    rankItems.value = rank
    showcaseItems.value = showcase
    honorItems.value = honor
    await Promise.all([fetchAllWorks(), fetchAllCreators()])
  } catch (e) {
    console.error('获取数据失败:', e)
  } finally {
    loading.value = false
  }
}

onMounted(fetchAllData)

function getWorkById(id) {
  return allWorks.value.find(w => w.id === id) || null
}

function getShowcaseImage(item) {
  if (item.extraData?.fileUrl) return item.extraData.fileUrl
  if (item.itemType === 'WORK') {
    const work = getWorkById(item.targetId)
    if (work?.fileUrl) return work.fileUrl
  }
  return null
}

function getShowcaseTitle(item) {
  if (item.itemType === 'WORK') {
    const work = getWorkById(item.targetId)
    return work?.title || item.extraData?.title || '未命名'
  }
  return item.extraData?.nickname || item.extraData?.username || '创作者'
}

function getItemImage(item) {
  if (item.extraData?.fileUrl) return item.extraData.fileUrl
  if (item.itemType === 'WORK') {
    const work = getWorkById(item.targetId)
    if (work?.fileUrl) return work.fileUrl
  }
  return null
}

function getCreatorById(id) {
  return allCreators.value.find(u => u.id === id) || null
}

function avatarText(username) { return (username || 'U').slice(0, 2).toUpperCase() }
function avatarStyle(publicId) { let hash = 0; for (const char of publicId || '0') hash = ((hash << 5) - hash + char.charCodeAt(0)) | 0; const hue = Math.abs(hash) % 360; return { color: `hsl(${hue} 78% 72%)`, backgroundColor: `hsl(${hue} 55% 18%)`, borderColor: `hsl(${hue} 48% 38%)` } }

async function handleUpload(event) {
  const file = event.target.files[0]
  if (!file) return
  const formData = new FormData()
  formData.append('file', file)
  try {
    const token = localStorage.getItem('mangrove_token')
    const res = await fetch('/api/files/upload', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` },
      body: formData
    })
    const json = await res.json()
    if (json.code === 200) {
      submitForm.value.fileUrl = json.data.url
      alert('上传成功')
    }
  } catch (e) {
    alert('上传失败')
  }
}

async function submitWork() {
  if (!submitForm.value.title.trim()) {
    alert('请填写标题')
    return
  }
  try {
    const token = localStorage.getItem('mangrove_token')
    const res = await fetch('/api/works', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(submitForm.value)
    })
    const json = await res.json()
    if (json.code === 200) {
      alert('投稿成功，等待审核')
      showSubmitForm.value = false
      submitForm.value = { title: '', description: '', category: 'PREVIEW', fileUrl: '', coverUrl: '' }
    } else {
      alert('投稿失败：' + (json.msg || '未知错误'))
    }
  } catch (e) {
    alert('网络错误')
  }
}

const nav = [{ id: 's1', label: '高清路透' }, { id: 's2', label: '创作者榜' }, { id: 's3', label: '精选展厅' }, { id: 's4', label: '荣誉墙' }]
function scrollTo(id) { document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' }) }
</script>
