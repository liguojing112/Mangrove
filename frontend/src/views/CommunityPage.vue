<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24 pb-16 relative">
    <!-- 背景装饰：五线谱纹理 + 浮动音符 -->
    <div class="absolute inset-0 pointer-events-none select-none z-0" aria-hidden="true">
      <svg class="absolute top-0 left-0 w-full h-full opacity-[0.08]" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <pattern id="staff-lines-community" x="0" y="0" width="100%" height="150" patternUnits="userSpaceOnUse">
            <line x1="0" y1="20" x2="100%" y2="20" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="45" x2="100%" y2="45" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="70" x2="100%" y2="70" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="95" x2="100%" y2="95" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="120" x2="100%" y2="120" stroke="#2E8B57" stroke-width="1.5"/>
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill="url(#staff-lines-community)"/>
      </svg>
      <div class="floating-note note-1">♪</div>
      <div class="floating-note note-2">♫</div>
      <div class="floating-note note-3">♬</div>
      <div class="floating-note note-4">♩</div>
      <div class="floating-note note-5">♪</div>
      <div class="floating-note note-6">𝄞</div>
      <div class="floating-note note-7">♫</div>
      <div class="floating-note note-8">♬</div>
      <div class="floating-note note-9">♪</div>
      <div class="floating-note note-10">𝄢</div>
      <div class="floating-note note-11">♫</div>
      <div class="floating-note note-12">♩</div>
      <div class="floating-note note-13">♬</div>
      <div class="floating-note note-14">♪</div>
      <div class="floating-note note-15">𝄞</div>
    </div>

    <!-- Header -->
    <h1 class="section-title mb-1 relative z-10">音符互动</h1>
    <p class="text-gray-500 mb-8 relative z-10">分享你的想法，和小伙伴们一起讨论</p>

    <!-- New Post Card -->
    <div v-if="isLoggedIn" class="card community-card-hover p-6 mb-8 relative z-10">
      <textarea
        v-model="newContent"
        placeholder="写下你想说的话..."
        class="w-full rounded-xl border border-gray-200 p-4 text-sm min-h-[100px] resize-none focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent"
      ></textarea>
      <!-- Image Preview -->
      <div v-if="imagePreview" class="mt-3 relative inline-block">
        <img :src="imagePreview" class="max-h-32 rounded-lg border border-gray-200" />
        <button class="absolute -top-2 -right-2 w-5 h-5 bg-red-500 text-white rounded-full text-xs flex items-center justify-center" @click="removeImage">✕</button>
      </div>
      <div class="flex items-center justify-between mt-4">
        <div class="flex items-center gap-2">
          <label class="cursor-pointer text-gray-400 hover:text-mangrove-600 transition-colors">
            <ImageIcon class="w-5 h-5" />
            <input type="file" accept="image/*" class="hidden" @change="handleImageSelect" />
          </label>
          <span class="tag">{{ currentUser?.nickname || '匿名用户' }}</span>
        </div>
        <button class="btn-primary text-sm" @click="submitComment" :disabled="!newContent.trim()">
          发布
        </button>
      </div>
    </div>
    <div v-else class="card community-card-hover p-6 mb-8 text-center relative z-10">
      <p class="text-gray-500">登录后参与讨论</p>
      <router-link to="/login" class="btn-primary inline-block mt-3">立即登录</router-link>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-8 relative z-10">
      <p class="text-gray-400">加载中...</p>
    </div>

    <!-- Feed -->
    <div v-else class="space-y-4 relative z-10">
      <div
        v-for="comment in comments"
        :key="comment.id"
        class="card-hover p-5"
      >
        <!-- Post Header -->
        <div class="flex items-center gap-3">
          <UserAvatar :public-id="comment.user?.publicId" :username="comment.user?.nickname" size="md" />
          <div>
            <span class="font-medium text-gray-900 text-sm">{{ comment.user?.nickname || '匿名用户' }}</span>
            <span class="text-xs text-gray-400 ml-2">{{ formatTime(comment.createdAt) }}</span>
          </div>
        </div>

        <!-- Content -->
        <p class="text-gray-700 mt-3 text-sm leading-relaxed">{{ comment.content }}</p>
        <img v-if="comment.imageUrl" :src="comment.imageUrl" class="mt-3 max-h-48 rounded-lg border border-gray-200" />

        <!-- Footer -->
        <div class="flex items-center gap-4 mt-3 text-xs text-gray-400">
          <button
            class="flex items-center gap-1 hover:text-mangrove-600 transition-colors"
            @click="toggleLike(comment)"
          >
            <Heart class="w-3.5 h-3.5" :class="comment.liked ? 'fill-pink-500 text-pink-500' : ''" />
            <span>{{ comment.likeCount || 0 }}</span>
          </button>
          <button
            v-if="isLoggedIn"
            class="flex items-center gap-1 hover:text-mangrove-600 transition-colors"
            @click="toggleReply(comment)"
          >
            <MessageCircle class="w-3.5 h-3.5" />
            <span>回复</span>
          </button>
        </div>

        <!-- Reply Input -->
        <div v-if="replyTarget === comment.id" class="mt-3 ml-3">
          <textarea
            v-model="replyContent"
            placeholder="写下你的回复..."
            class="w-full rounded-xl border border-gray-200 p-2 text-xs min-h-[50px] resize-none focus:outline-none focus:ring-1 focus:ring-mangrove-400"
          ></textarea>
          <div class="flex justify-end gap-2 mt-2">
            <button class="text-xs text-gray-400 hover:text-gray-600" @click="cancelReply">取消</button>
            <button class="text-xs bg-mangrove-500 text-white px-3 py-1 rounded-full hover:bg-mangrove-600 disabled:opacity-40" :disabled="!replyContent.trim() || replySending" @click="sendReply(comment)">{{ replySending ? '发送中...' : '发送' }}</button>
          </div>
        </div>

        <!-- Replies -->
        <div v-if="comment.children && comment.children.length > 0" class="ml-12 border-l-2 border-gray-100 pl-4 mt-3 space-y-2">
          <div v-for="reply in comment.children" :key="reply.id" class="text-sm text-gray-600 flex items-start gap-2">
            <UserAvatar :public-id="reply.user?.publicId" :username="reply.user?.nickname" size="xs" class="mt-0.5" />
            <span class="font-medium">{{ reply.user?.nickname || '匿名用户' }}：</span>
            <span>{{ reply.content }}</span>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="comments.length === 0" class="text-center py-12">
        <MessageCircle class="w-12 h-12 text-gray-300 mx-auto mb-3" />
        <p class="text-gray-400">还没有评论，快来抢沙发吧！</p>
      </div>
    </div>

    <!-- Load More -->
    <div v-if="hasMore" class="text-center mt-6 relative z-10">
      <button class="btn-secondary text-sm" @click="loadMore">加载更多</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Heart, MessageCircle, Image as ImageIcon } from 'lucide-vue-next'
import { useAuth } from '@/composables/useAuth'
import UserAvatar from '@/components/UserAvatar.vue'

const { isLoggedIn, currentUser, getToken } = useAuth()

const comments = ref([])
const newContent = ref('')
const imageFile = ref(null)
const imagePreview = ref('')
const uploading = ref(false)
const loading = ref(false)
const page = ref(0)
const hasMore = ref(true)
const replyTarget = ref(null)
const replyContent = ref('')
const replySending = ref(false)

function handleImageSelect(e) {
  const file = e.target.files[0]
  if (!file) return
  imageFile.value = file
  const reader = new FileReader()
  reader.onload = (ev) => { imagePreview.value = ev.target.result }
  reader.readAsDataURL(file)
}

function removeImage() {
  imageFile.value = null
  imagePreview.value = ''
}

async function uploadImage() {
  if (!imageFile.value) return null
  const formData = new FormData()
  formData.append('file', imageFile.value)
  try {
    const res = await fetch('/api/files/upload', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getToken()}` },
      body: formData
    })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      return json.data.url || json.data
    }
  } catch (e) {
    console.error('上传图片失败:', e)
  }
  return null
}

async function fetchComments() {
  loading.value = true
  try {
    const res = await fetch(`/api/comments?targetType=COMMUNITY&targetId=1&page=${page.value}&size=20`)
    const json = await res.json()
    if (json.code === 200 && json.data) {
      if (page.value === 0) {
        comments.value = json.data.content || []
      } else {
        comments.value.push(...(json.data.content || []))
      }
      hasMore.value = comments.value.length < json.data.totalElements
    }
  } catch (e) {
    console.error('加载评论失败:', e)
  } finally {
    loading.value = false
  }
}

async function submitComment() {
  if (!newContent.value.trim()) return
  uploading.value = true
  try {
    let imageUrl = null
    if (imageFile.value) {
      imageUrl = await uploadImage()
    }
    const res = await fetch('/api/comments', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({
        targetType: 'COMMUNITY',
        targetId: 1,
        content: newContent.value.trim(),
        imageUrl
      })
    })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      newContent.value = ''
      removeImage()
      page.value = 0
      await fetchComments()
    }
  } catch (e) {
    console.error('发布失败:', e)
  } finally {
    uploading.value = false
  }
}

async function toggleLike(comment) {
  if (!isLoggedIn.value) return
  try {
    const res = await fetch(`/api/comments/${comment.id}/like`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      comment.liked = !comment.liked
      comment.likeCount = comment.liked ? (comment.likeCount || 0) + 1 : Math.max(0, (comment.likeCount || 0) - 1)
    }
  } catch (e) {
    console.error('点赞失败:', e)
  }
}

function loadMore() {
  page.value++
  fetchComments()
}

function toggleReply(comment) {
  if (replyTarget.value === comment.id) {
    cancelReply()
  } else {
    replyTarget.value = comment.id
    replyContent.value = ''
  }
}

function cancelReply() {
  replyTarget.value = null
  replyContent.value = ''
}

async function sendReply(parent) {
  if (!replyContent.value.trim() || replySending.value) return
  replySending.value = true
  try {
    const res = await fetch('/api/comments', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({
        targetType: 'COMMUNITY',
        targetId: 1,
        content: replyContent.value.trim(),
        parentId: parent.id
      })
    })
    const json = await res.json()
    if (json.code === 200) {
      replyTarget.value = null
      replyContent.value = ''
      page.value = 0
      await fetchComments()
    }
  } catch (e) {
    console.error('回复失败:', e)
  } finally {
    replySending.value = false
  }
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = (now - date) / 1000
  if (diff < 60) return '刚刚'
  if (diff < 3600) return `${Math.floor(diff / 60)} 分钟前`
  if (diff < 86400) return `${Math.floor(diff / 3600)} 小时前`
  if (diff < 604800) return `${Math.floor(diff / 86400)} 天前`
  return date.toLocaleDateString()
}

onMounted(() => {
  fetchComments()
})
</script>

<style scoped>
.community-card-hover {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.community-card-hover:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(46, 139, 87, 0.15), 0 4px 8px rgba(46, 139, 87, 0.1);
}
.card-hover {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.card-hover:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(46, 139, 87, 0.15), 0 4px 8px rgba(46, 139, 87, 0.1);
}

.floating-note {
  position: fixed;
  color: rgba(46, 139, 87, 0.3);
  pointer-events: none;
  user-select: none;
  z-index: 9999;
  text-shadow: 0 0 20px rgba(46, 139, 87, 0.4);
}
.note-1 { top: 8%; left: 5%; animation: floatNote 4s ease-in-out infinite; font-size: 2.5rem; }
.note-2 { top: 5%; right: 12%; animation: floatNote 5s ease-in-out infinite 0.5s; font-size: 2rem; }
.note-3 { top: 15%; left: 18%; animation: floatNote 6s ease-in-out infinite 1s; font-size: 3.5rem; }
.note-4 { top: 12%; right: 5%; animation: floatNote 4.5s ease-in-out infinite 0.3s; font-size: 3rem; }
.note-5 { top: 25%; left: 8%; animation: floatNote 5.5s ease-in-out infinite 1.2s; font-size: 2.5rem; }
.note-6 { top: 20%; right: 18%; animation: floatNote 7s ease-in-out infinite 2s; font-size: 4rem; }
.note-7 { top: 35%; left: 22%; animation: floatNote 5s ease-in-out infinite 0.8s; font-size: 2.8rem; }
.note-8 { top: 30%; right: 8%; animation: floatNote 6.5s ease-in-out infinite 1.5s; font-size: 3.5rem; }
.note-9 { top: 45%; left: 10%; animation: floatNote 4.5s ease-in-out infinite 0.6s; font-size: 2.2rem; }
.note-10 { top: 40%; right: 20%; animation: floatNote 7.5s ease-in-out infinite 2.5s; font-size: 3.5rem; }
.note-11 { top: 55%; left: 25%; animation: floatNote 5.5s ease-in-out infinite 1s; font-size: 3rem; }
.note-12 { top: 60%; right: 10%; animation: floatNote 6s ease-in-out infinite 1.8s; font-size: 2.8rem; }
.note-13 { top: 70%; left: 8%; animation: floatNote 5s ease-in-out infinite 0.4s; font-size: 3.2rem; }
.note-14 { top: 75%; right: 22%; animation: floatNote 7s ease-in-out infinite 2.2s; font-size: 2.5rem; }
.note-15 { top: 85%; left: 30%; animation: floatNote 6.5s ease-in-out infinite 1.5s; font-size: 3.8rem; }

@keyframes floatNote {
  0%, 100% { transform: translate(0, 0) rotate(0deg); opacity: 0.3; }
  25% { transform: translate(50px, -100px) rotate(20deg); opacity: 0.5; }
  50% { transform: translate(-40px, -50px) rotate(-15deg); opacity: 0.35; }
  75% { transform: translate(60px, -120px) rotate(15deg); opacity: 0.45; }
}
</style>
