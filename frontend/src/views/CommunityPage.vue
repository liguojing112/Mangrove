<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24 pb-16">
    <!-- Header -->
    <h1 class="section-title mb-1">音符互动</h1>
    <p class="text-gray-500 mb-8">分享你的想法，和小伙伴们一起讨论</p>

    <!-- New Post Card -->
    <div v-if="isLoggedIn" class="card p-6 mb-8">
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
    <div v-else class="card p-6 mb-8 text-center">
      <p class="text-gray-500">登录后参与讨论</p>
      <router-link to="/login" class="btn-primary inline-block mt-3">立即登录</router-link>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-8">
      <p class="text-gray-400">加载中...</p>
    </div>

    <!-- Feed -->
    <div v-else class="space-y-4">
      <div
        v-for="comment in comments"
        :key="comment.id"
        class="card-hover p-5"
      >
        <!-- Post Header -->
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-full bg-mangrove-200 flex items-center justify-center">
            <User class="w-5 h-5 text-mangrove-700" />
          </div>
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
        </div>

        <!-- Replies -->
        <div v-if="comment.children && comment.children.length > 0" class="ml-12 border-l-2 border-gray-100 pl-4 mt-3 space-y-2">
          <div v-for="reply in comment.children" :key="reply.id" class="text-sm text-gray-600">
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
    <div v-if="hasMore" class="text-center mt-6">
      <button class="btn-secondary text-sm" @click="loadMore">加载更多</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Heart, MessageCircle, Image as ImageIcon } from 'lucide-vue-next'
import { useAuth } from '@/composables/useAuth'

const { isLoggedIn, currentUser, getToken } = useAuth()

const comments = ref([])
const newContent = ref('')
const imageFile = ref(null)
const imagePreview = ref('')
const uploading = ref(false)
const loading = ref(false)
const page = ref(0)
const hasMore = ref(true)

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
