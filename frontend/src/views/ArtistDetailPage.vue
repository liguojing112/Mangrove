<template>
  <div class="min-h-screen bg-gray-50 relative overflow-hidden">
    <!-- 背景装饰：五线谱纹理 + 浮动音符 -->
    <div class="absolute inset-0 pointer-events-none select-none" aria-hidden="true">
      <!-- 五线谱底纹 -->
      <svg class="absolute top-0 left-0 w-full h-full opacity-[0.08]" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <pattern id="staff-lines-detail" x="0" y="0" width="100%" height="150" patternUnits="userSpaceOnUse">
            <line x1="0" y1="20" x2="100%" y2="20" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="45" x2="100%" y2="45" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="70" x2="100%" y2="70" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="95" x2="100%" y2="95" stroke="#2E8B57" stroke-width="1.5"/>
            <line x1="0" y1="120" x2="100%" y2="120" stroke="#2E8B57" stroke-width="1.5"/>
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill="url(#staff-lines-detail)"/>
      </svg>
      <!-- 浮动音符装饰 -->
      <div class="floating-note-detail note-d1">♪</div>
      <div class="floating-note-detail note-d2">♫</div>
      <div class="floating-note-detail note-d3">♬</div>
      <div class="floating-note-detail note-d4">♩</div>
      <div class="floating-note-detail note-d5">♪</div>
      <div class="floating-note-detail note-d6">𝄞</div>
      <div class="floating-note-detail note-d7">♫</div>
      <div class="floating-note-detail note-d8">♬</div>
      <div class="floating-note-detail note-d9">♪</div>
      <div class="floating-note-detail note-d10">𝄢</div>
      <div class="floating-note-detail note-d11">♫</div>
      <div class="floating-note-detail note-d12">♩</div>
      <div class="floating-note-detail note-d13">♬</div>
      <div class="floating-note-detail note-d14">♪</div>
      <div class="floating-note-detail note-d15">𝄞</div>
    </div>

    <!-- 主内容区 -->
    <div class="relative max-w-4xl mx-auto px-4 sm:px-6 pb-16">
      <!-- 页面标题 -->
      <h1 class="text-3xl font-bold text-gray-900 text-center pt-10 mb-8 detail-fade-in">艺人介绍</h1>

      <!-- 头像 + 基础信息 -->
      <div class="flex flex-col md:flex-row gap-0 mb-8 detail-fade-in">
        <!-- 左侧：头像 + 签名 + 品牌 -->
        <div class="md:w-72 shrink-0 space-y-0">
          <!-- 头像区域 -->
          <div class="relative aspect-square rounded-tl-2xl bg-gradient-to-br from-mangrove-50 to-mangrove-100 border border-mangrove-200 overflow-hidden shadow-card">
            <!-- 绿色光晕 -->
            <div class="absolute -inset-2 bg-mangrove-300/20 rounded-tl-2xl blur-xl -z-10"></div>
            <div v-if="avatarPhotos.length > 0" class="w-full h-full relative">
              <Swiper :modules="swiperModules" :slides-per-view="1" :loop="avatarPhotos.length > 1"
                :autoplay="avatarPhotos.length > 1 ? { delay: 3000, disableOnInteraction: false } : false"
                class="w-full h-full">
                <SwiperSlide v-for="(url, i) in avatarPhotos" :key="i">
                  <img :src="url" class="w-full h-full object-cover" />
                </SwiperSlide>
              </Swiper>
              <!-- 图片指示器 -->
              <div v-if="avatarPhotos.length > 1" class="absolute bottom-3 left-1/2 -translate-x-1/2 flex gap-1.5">
                <span v-for="(_, i) in avatarPhotos" :key="i" class="w-1.5 h-1.5 rounded-full bg-white/70"></span>
              </div>
            </div>
            <div v-else class="flex flex-col items-center justify-center h-full">
              <User class="w-16 h-16 text-mangrove-300/60 mb-3" />
              <span class="text-xs text-mangrove-400">点击上传头像</span>
            </div>
          </div>
          <!-- 签名区 -->
          <div class="rounded-bl-2xl border border-mangrove-200 bg-white border-t-0 p-4 text-center overflow-hidden">
            <span class="inline-flex items-center gap-1 text-xs text-mangrove-500 font-medium mb-2">
              <PenLine class="w-3 h-3"/>
              签名
            </span>
            <img v-if="artist.signatureImageUrl" :src="artist.signatureImageUrl" class="w-full h-24 object-cover rounded-xl" />
            <div v-else class="h-24 bg-mangrove-50/50 rounded-xl flex items-center justify-center">
              <span class="text-gray-400 text-xs">点击上传签名图</span>
            </div>
          </div>
          <!-- 品牌区 -->
          <div class="rounded-b-2xl border border-mangrove-200 bg-white border-t-0 p-4 text-center overflow-hidden">
            <span class="inline-flex items-center gap-1 text-xs text-mangrove-500 font-medium mb-2">
              <Award class="w-3 h-3"/>
              品牌
            </span>
            <img v-if="artist.brandImageUrl" :src="artist.brandImageUrl" class="w-full h-24 object-cover rounded-xl" />
            <div v-else class="h-24 bg-mangrove-50/50 rounded-xl flex items-center justify-center">
              <span class="text-gray-400 text-xs">点击上传品牌标识</span>
            </div>
          </div>
        </div>

        <!-- 右侧：基础信息 -->
        <div class="flex-1 border border-mangrove-200 rounded-r-2xl bg-white shadow-card overflow-hidden">
          <div class="bg-gradient-to-r from-mangrove-50 via-mangrove-50/80 to-transparent rounded-tr-2xl px-5 py-5 border-b border-mangrove-100">
            <div class="flex items-center justify-center gap-2">
              <span class="text-mangrove-400 text-lg">𝄞</span>
              <h2 class="text-mangrove-700 font-bold text-xl">基础信息</h2>
              <span class="text-mangrove-400 text-lg">𝄞</span>
            </div>
          </div>
          <div class="p-5 pb-3">
            <div class="grid grid-cols-2 gap-x-6 gap-y-0 text-sm">
              <div v-for="item in infoItems" :key="item.label" class="flex items-start border-b border-dashed border-mangrove-100/80 py-2.5 last:border-0">
                <span class="text-mangrove-600 w-20 shrink-0 font-medium text-xs">{{ item.label }}</span>
                <span v-if="item.link" class="text-gray-800 text-xs">
                  <a :href="item.link" target="_blank" class="text-mangrove-600 hover:underline whitespace-nowrap">{{ item.value }} 🔗</a>
                </span>
                <span v-else class="text-gray-800 text-xs">{{ item.value }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 详细介绍 -->
      <div class="bg-gradient-to-r from-mangrove-50 to-mangrove-50/60 rounded-2xl px-5 py-4 mb-5 flex items-center justify-between border border-mangrove-100 detail-fade-in" style="animation-delay: 150ms">
        <div class="flex items-center gap-2">
          <span class="text-mangrove-500">📝</span>
          <h2 class="text-mangrove-700 font-bold">详细介绍</h2>
        </div>
        <button @click="showAskModal = true" class="inline-flex items-center gap-1.5 text-sm text-mangrove-600 hover:text-mangrove-800 font-medium bg-white/60 hover:bg-white px-3 py-1.5 rounded-full transition-all duration-200 shadow-sm hover:shadow">
          <MessageCircle class="w-4 h-4"/>
          提问
        </button>
      </div>

      <!-- 提问弹窗 -->
      <Teleport to="body">
        <Transition name="modal">
          <div v-if="showAskModal" class="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50" @click.self="showAskModal = false">
            <div class="bg-white rounded-2xl p-6 w-full max-w-md mx-4 shadow-xl detail-modal-enter">
              <div class="flex items-center gap-2 mb-4">
                <div class="w-8 h-8 bg-mangrove-100 rounded-full flex items-center justify-center">
                  <MessageCircle class="w-4 h-4 text-mangrove-600"/>
                </div>
                <h3 class="text-lg font-bold text-gray-900">向艺人提问</h3>
              </div>
              <textarea v-model="askText" class="w-full rounded-xl border border-mangrove-200 px-4 py-3 text-sm resize-none h-32 focus:outline-none focus:ring-2 focus:ring-mangrove-300 focus:border-mangrove-400 transition-all placeholder-gray-400" placeholder="请输入你的问题..."></textarea>
              <div class="flex justify-end gap-3 mt-4">
                <button @click="showAskModal = false" class="px-4 py-2 text-sm text-gray-500 hover:text-gray-700 hover:bg-gray-50 rounded-lg transition-colors">取消</button>
                <button @click.stop="submitAsk" @touchstart.stop.prevent="submitAsk" :disabled="!askText.trim()" class="px-5 py-2 text-sm font-medium text-white bg-gradient-to-r from-mangrove-600 to-mangrove-500 rounded-lg hover:from-mangrove-700 hover:to-mangrove-600 disabled:opacity-40 transition-all shadow-sm hover:shadow">提交</button>
              </div>
            </div>
          </div>
        </Transition>
      </Teleport>

      <!-- Q&A 卡片列表 -->
      <div class="space-y-3">
        <TransitionGroup name="qa-list">
          <div
            v-for="(section, index) in bioSections"
            :key="section.id"
            class="qa-card rounded-2xl border-2 border-mangrove-200/80 bg-white p-5 shadow-card transition-all duration-300 detail-fade-in hover:bg-gradient-to-r hover:from-mangrove-50/60 hover:to-white hover:border-mangrove-400/70"
            :style="{ animationDelay: (200 + index * 60) + 'ms' }"
          >
            <div class="flex items-start gap-3">
              <div class="qa-badge w-8 h-8 bg-mangrove-100 rounded-full flex items-center justify-center shrink-0 mt-0.5 transition-all duration-300">
                <span class="text-mangrove-600 text-xs font-bold transition-colors duration-300">Q</span>
              </div>
              <div class="flex-1 min-w-0">
                <p class="qa-question text-mangrove-600 font-semibold text-sm mb-2 transition-colors duration-300">{{ section.question }}</p>
                <p class="text-sm text-gray-700 leading-relaxed">{{ section.answer }}</p>
              </div>
            </div>
          </div>
        </TransitionGroup>
      </div>

      <!-- 详细介绍空状态 -->
      <div v-if="bioSections.length === 0" class="text-center py-12">
        <div class="inline-block bg-white rounded-2xl px-8 py-6 shadow-card">
          <div class="w-16 h-16 mx-auto mb-3 rounded-full bg-mangrove-50 flex items-center justify-center">
            <span class="text-2xl">🎵</span>
          </div>
          <p class="text-gray-400 text-sm">暂无详细介绍，快来提问吧</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { User, PenLine, Award, MessageCircle } from 'lucide-vue-next'
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Autoplay } from 'swiper/modules'
import 'swiper/css'

const swiperModules = [Autoplay]

const avatarPhotos = computed(() => {
  const photos = []
  if (artist.value.avatarImages && artist.value.avatarImages.length > 0) {
    photos.push(...artist.value.avatarImages)
  } else if (artist.value.avatarUrl) {
    photos.push(artist.value.avatarUrl)
  }
  return photos
})

const route = useRoute()
const artist = ref({ stageName: '' })

function calcAge(birthDate) {
  if (!birthDate) return '-'
  const birth = new Date(birthDate)
  const today = new Date()
  let age = today.getFullYear() - birth.getFullYear()
  if (today.getMonth() < birth.getMonth() || (today.getMonth() === birth.getMonth() && today.getDate() < birth.getDate())) age--
  return age + '岁'
}

function formatBirthday(birthDate) {
  if (!birthDate) return '-'
  const d = new Date(birthDate)
  return String(d.getMonth() + 1).padStart(2, '0') + '.' + String(d.getDate()).padStart(2, '0')
}

const infoItems = computed(() => [
  { label: '中文名', value: '曲唱' },
  { label: '英文名', value: 'candice' },
  { label: '罗马音', value: 'QUCHANG' },
  { label: '性别', value: '女' },
  { label: '年龄', value: calcAge(artist.value.birthDate) },
  { label: '生日', value: formatBirthday(artist.value.birthDate) },
  { label: '身高', value: '178.1cm' },
  { label: '星座', value: '狮子座 ♌' },
  { label: '国籍', value: 'CN 中国' },
  { label: 'MBTI', value: 'ENTP-A' },
  { label: '公司', value: 'A2O_Channel' },
  { label: '组合', value: 'A2O_MAY' },
  { label: '担当', value: 'Vocal 主唱' },
  { label: '昵称', value: '小芒 / Candice' },
  { label: '粉丝名', value: '音符 🎵' },
  { label: '出道日', value: '2024.12.20' },
  { label: '语言', value: '中文 · 英语 · 西语' },
  { label: '爱好', value: '睡觉 😴 唱歌 🎤 弹钢琴 🎹' },
  { label: '微博', value: '@QUCHANG', link: 'https://m.weibo.cn/u/7957169158?wm=3333_2001&from=10G7093010&sourcetype=weixin&s_trans=Z%2BAptj5bg0L8TxBuBaeHSw%3D%3D__s&s_channel=4' },
  { label: '抖音', value: '@QUCHANG', link: 'https://v.douyin.com/maFW9iTCSQw/' }
])

const bioSections = ref([])
const loading = ref(true)
const showAskModal = ref(false)
const askText = ref('')

async function submitAsk() {
  if (!askText.value.trim()) return
  try {
    const token = localStorage.getItem('mangrove_token')
    const res = await fetch('/api/artist-bio/ask', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {})
      },
      body: JSON.stringify({ question: askText.value })
    })
    const json = await res.json()
    if (json.code === 200) {
      bioSections.value.push(json.data)
      askText.value = ''
      showAskModal.value = false
    } else {
      alert('提交失败: ' + (json.msg || ''))
    }
  } catch (e) {
    alert('提交失败: ' + e.message)
  }
}

onMounted(async () => {
  try {
    const res = await fetch(`/api/public/artists/${route.params.id}`)
    const json = await res.json()
    if (json.code === 200) artist.value = json.data
  } catch {} finally { loading.value = false }

  try {
    const res = await fetch(`/api/public/artists/${route.params.id}/bio`)
    const json = await res.json()
    if (json.code === 200) bioSections.value = json.data
  } catch {}
})
</script>

<style scoped>
/* 浮动音符 - 视口固定，明显可见 */
.floating-note-detail {
  position: fixed;
  color: rgba(46, 139, 87, 0.3);
  pointer-events: none;
  user-select: none;
  z-index: 9999;
  text-shadow: 0 0 20px rgba(46, 139, 87, 0.4);
}

.note-d1 { top: 8%; left: 5%; font-size: 2.2rem; animation: floatDetail 4s ease-in-out infinite; }
.note-d2 { top: 5%; right: 10%; font-size: 2rem; animation: floatDetail 5s ease-in-out infinite 0.5s; }
.note-d3 { top: 15%; left: 15%; font-size: 3rem; animation: floatDetail 6s ease-in-out infinite 1s; }
.note-d4 { top: 12%; right: 5%; font-size: 2.5rem; animation: floatDetail 4.5s ease-in-out infinite 0.3s; }
.note-d5 { top: 25%; left: 8%; font-size: 2.2rem; animation: floatDetail 5.5s ease-in-out infinite 1.2s; }
.note-d6 { top: 20%; right: 15%; font-size: 3.5rem; animation: floatDetail 7s ease-in-out infinite 2s; }
.note-d7 { top: 35%; left: 20%; font-size: 2.5rem; animation: floatDetail 5s ease-in-out infinite 0.8s; }
.note-d8 { top: 30%; right: 7%; font-size: 3rem; animation: floatDetail 6.5s ease-in-out infinite 1.5s; }
.note-d9 { top: 45%; left: 8%; font-size: 2rem; animation: floatDetail 4.5s ease-in-out infinite 0.6s; }
.note-d10 { top: 40%; right: 18%; font-size: 3.2rem; animation: floatDetail 7.5s ease-in-out infinite 2.5s; }
.note-d11 { top: 55%; left: 22%; font-size: 2.8rem; animation: floatDetail 5.5s ease-in-out infinite 1s; }
.note-d12 { top: 60%; right: 8%; font-size: 2.5rem; animation: floatDetail 6s ease-in-out infinite 1.8s; }
.note-d13 { top: 72%; left: 6%; font-size: 3rem; animation: floatDetail 5s ease-in-out infinite 0.4s; }
.note-d14 { top: 78%; right: 18%; font-size: 2.3rem; animation: floatDetail 7s ease-in-out infinite 2.2s; }
.note-d15 { top: 88%; left: 28%; font-size: 3.5rem; animation: floatDetail 6.5s ease-in-out infinite 1.5s; }

@keyframes floatDetail {
  0%, 100% { transform: translate(0, 0) rotate(0deg); opacity: 0.3; }
  25% { transform: translate(50px, -100px) rotate(20deg); opacity: 0.5; }
  50% { transform: translate(-40px, -50px) rotate(-15deg); opacity: 0.35; }
  75% { transform: translate(60px, -120px) rotate(15deg); opacity: 0.45; }
}

/* 入场动画 - 更明显 */
.detail-fade-in {
  animation: detailFadeIn 0.7s ease-out both;
}

@keyframes detailFadeIn {
  from { opacity: 0; transform: translateY(24px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Q&A 卡片 hover - 大幅上浮 + 阴影 */
.qa-card {
  transition: all 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.qa-card:hover {
  transform: translateY(-6px) scale(1.01);
  box-shadow:
    0 16px 32px -8px rgba(46, 139, 87, 0.15),
    0 4px 12px -4px rgba(46, 139, 87, 0.10);
}
.qa-card:hover .qa-badge {
  transform: scale(1.15);
  background: linear-gradient(135deg, #3CB371, #2E8B57);
}
.qa-card:hover .qa-badge span {
  color: white;
}
.qa-card:hover .qa-question {
  color: #1a6b40;
}

/* 弹窗动画 */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.25s ease;
}
.modal-enter-active > div,
.modal-leave-active > div {
  transition: transform 0.25s ease, opacity 0.25s ease;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
.modal-enter-from > div,
.modal-leave-to > div {
  opacity: 0;
  transform: scale(0.95) translateY(10px);
}

/* Q&A 列表动画 */
.qa-list-enter-active {
  transition: all 0.4s ease;
}
.qa-list-enter-from {
  opacity: 0;
  transform: translateY(12px);
}
</style>
