<template>
  <div class="min-h-screen bg-gradient-to-b from-sky-50 to-blue-100 py-8 px-4">
    <div class="max-w-3xl mx-auto">

      <!-- 顶部标题 -->
      <div class="text-center mb-6">
        <h1 class="text-3xl font-black text-sky-600 tracking-wider" style="text-shadow: 2px 2px 0 rgba(186,230,253,0.6);">
          艺人介绍
        </h1>
      </div>

      <!-- 主体：左侧头像 + 右侧基础信息 -->
      <div class="bg-white rounded-2xl border-2 border-sky-200 p-6 mb-4">
        <div class="flex flex-col md:flex-row gap-6">

          <!-- 左侧：头像 + 签名 + 代表色 -->
          <div class="md:w-[200px] flex-shrink-0 flex flex-col gap-4">
            <!-- 头像 -->
            <div class="w-44 h-56 mx-auto rounded-2xl bg-sky-50 border-2 border-sky-200 overflow-hidden flex items-center justify-center"
                 :class="isSuperAdmin ? 'cursor-pointer hover:border-sky-400 transition-colors' : ''"
                 @click="isSuperAdmin && triggerAvatarUpload()">
              <img v-if="avatarUrl" :src="avatarUrl" class="w-full h-full object-cover" />
              <div v-else class="text-center">
                <User class="w-16 h-16 text-sky-300 mx-auto" />
                <span class="text-xs text-sky-400 mt-1 block">{{ isSuperAdmin ? '点击上传头像' : '照片待上传' }}</span>
              </div>
              <input ref="avatarInput" type="file" accept="image/*" class="hidden" @change="onAvatarUpload" />
            </div>

            <!-- 签名（点击上传图片，仅超管可操作） -->
            <div class="rounded-xl border-2 border-sky-200 p-3 bg-sky-50/50"
                 :class="isSuperAdmin ? 'cursor-pointer hover:border-sky-400 transition-colors relative group' : ''"
                 @click="triggerSignatureUpload">
              <div class="text-xs text-sky-500 font-medium mb-1">签名</div>
              <div v-if="signatureUrl" class="flex items-center justify-center h-16">
                <img :src="signatureUrl" class="max-h-16 max-w-full object-contain" />
              </div>
              <div v-else class="flex items-center justify-center h-16 text-gray-400 text-sm">
                <span>{{ isSuperAdmin ? '点击上传签名图片' : '签名待上传' }}</span>
              </div>
              <div v-if="isSuperAdmin" class="absolute inset-0 bg-black/10 rounded-xl opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                <span class="text-xs text-white bg-black/50 px-2 py-1 rounded">更换</span>
              </div>
              <input ref="signatureInput" type="file" accept="image/*" class="hidden" @change="onSignatureUpload" />
            </div>

            <!-- 代表色 -->
            <div class="rounded-xl border-2 border-sky-200 p-3 bg-sky-50/50">
              <div class="text-xs text-sky-500 font-medium mb-1">代表色</div>
              <div class="flex items-center gap-2">
                <span class="text-2xl">💚</span>
                <span class="text-sm text-gray-600">绿色 & 白色</span>
              </div>
            </div>
          </div>

          <!-- 右侧：基础信息 -->
          <div class="flex-1">
            <div class="bg-sky-100 rounded-xl px-4 py-2 mb-4">
              <h2 class="text-base font-bold text-sky-700 text-center">基础信息</h2>
            </div>

            <div class="grid grid-cols-2 gap-x-6 gap-y-2.5 text-sm">
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">艺名：</span>
                <span class="text-gray-800 font-bold text-base">{{ artist.stageName }}</span>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">韩文名：</span>
                <span class="text-gray-800">취창</span>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">本名：</span>
                <span class="text-gray-800 font-medium">{{ artist.name }}</span>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">生日：</span>
                <span class="text-gray-800">07.28 🦁</span>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">年龄：</span>
                <span class="text-gray-800">{{ artist.age }}岁</span>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">星座：</span>
                <span class="text-gray-800">狮子座♌️</span>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">身高：</span>
                <span class="text-gray-800">178.1cm</span>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">国籍：</span>
                <span class="text-gray-800">🇨🇳 中国</span>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">MBTI：</span>
                <span class="text-gray-800">ENTP-A</span>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">籍贯：</span>
                <span class="text-gray-800">江苏</span>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">公司：</span>
                <span class="text-gray-800">A2O_Channel</span>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">组合：</span>
                <span class="text-gray-800">A2O_MAY</span>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">担当：</span>
                <span class="text-gray-800">Vocal 主唱</span>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">昵称：</span>
                <span class="text-gray-800">小芒 / Candice / 曲小叨</span>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">微博：</span>
                <a href="https://m.weibo.cn/u/7957169158" target="_blank" class="text-sky-600 hover:text-sky-800 underline text-xs truncate max-w-[160px] inline-block align-middle">@A2O-曲唱QUCHANG 🔗</a>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">抖音：</span>
                <a href="https://v.douyin.com/maFW9iTCSQw/" target="_blank" class="text-sky-600 hover:text-sky-800 underline text-xs truncate max-w-[160px] inline-block align-middle">@A2O-曲唱QUCHANG 🔗</a>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">粉丝名：</span>
                <span class="text-gray-800">音符 🎶</span>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">动物塑：</span>
                <span class="text-gray-800">🦊狐狸 🐹仓鼠</span>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">学校：</span>
                <span class="text-gray-800">苏州外国语 → 伯克利</span>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">出道日：</span>
                <span class="text-gray-800">2024.12.20</span>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">语言：</span>
                <span class="text-gray-800">中文 · 英语 · 西语</span>
              </div>
              <div class="flex">
                <span class="text-sky-500 w-16 shrink-0 font-medium">爱好：</span>
                <span class="text-gray-800">睡觉😴 唱歌🎤 弹钢琴🎹 烘焙🧁 拍小狗丑照🐶</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 出道天数 -->
      <div class="bg-white rounded-2xl border-2 border-sky-200 p-4 mb-4 text-center">
        <span class="text-sky-500 text-sm">已出道</span>
        <span class="text-3xl font-black text-sky-600 mx-2">{{ artist.debutDays }}</span>
        <span class="text-sky-500 text-sm">天 ✨</span>
      </div>

      <!-- 详细介绍 -->
      <div class="bg-white rounded-2xl border-2 border-sky-200 p-6">
        <div class="bg-sky-100 rounded-xl px-4 py-2 mb-5">
          <h2 class="text-base font-bold text-sky-700 text-center">详细介绍</h2>
        </div>

        <div class="space-y-4">
          <div class="border border-sky-200 rounded-xl p-4 bg-sky-50/40">
            <h3 class="text-sky-600 font-bold text-sm mb-2">为什么叫"小芒"？</h3>
            <p class="text-gray-700 text-sm leading-relaxed">
              "小芒"这个名字来源于粉丝对曲唱的昵称，寓意像芒果一样温暖甜蜜。
              英文名 Candice 则代表着纯净与美好，与她的音乐风格相得益彰。
              而"曲小叨"是因她爱碎碎念的可爱性格得来的。
            </p>
          </div>

          <div class="border border-sky-200 rounded-xl p-4 bg-sky-50/40">
            <h3 class="text-sky-600 font-bold text-sm mb-2">音乐风格与舞台特色</h3>
            <p class="text-gray-700 text-sm leading-relaxed">
              作为A2O_MAY的主唱，曲唱拥有极具辨识度的嗓音和感染力十足的舞台表现。
              从小在苏州长大，后赴伯克利音乐学院深造，精通中英西三语，
              将东西方音乐元素完美融合在自己的演唱风格中。
            </p>
          </div>

          <div class="border border-sky-200 rounded-xl p-4 bg-sky-50/40">
            <h3 class="text-sky-600 font-bold text-sm mb-2">自我定位与未来方向</h3>
            <p class="text-gray-700 text-sm leading-relaxed">
              曾斩获三所美国顶尖音乐学院offer，选择在伯克利继续学业的同时追逐音乐梦想。
              热爱唱歌、弹钢琴、烘焙，是个多才多艺、充满能量的少女。
              希望用音乐传递温暖，成为不被定义的偶像。
            </p>
          </div>

          <div class="border border-sky-200 rounded-xl p-4 bg-sky-50/40">
            <h3 class="text-sky-600 font-bold text-sm mb-2">家人与生活</h3>
            <p class="text-gray-700 text-sm leading-relaxed">
              家里有妈妈、爸爸和两只可爱的小狗 —— 哥哥大宝是柴犬，
              弟弟白糖是从流浪狗基地领养的小土狗串串。
              个人技包括扑克牌魔术🪄和手指打结🤌🏻。
            </p>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { User } from 'lucide-vue-next'

const route = useRoute()

const fallbackArtist = {
  id: 1,
  name: '曲唱',
  stageName: 'QUCHANG',
  bio: '',
  debutDays: Math.floor((Date.now() - new Date('2024-12-20').getTime()) / 86400000),
  age: 18,
  avatarUrl: '',
  totalPhotos: 0,
  totalLetters: 0,
}

const artist = ref({ ...fallbackArtist })

// 签名图片
const signatureUrl = ref(localStorage.getItem('artist_signature_url') || '')
const signatureInput = ref(null)

// 头像图片
const avatarUrl = ref(localStorage.getItem('artist_avatar_url') || '')
const avatarInput = ref(null)

const isSuperAdmin = (() => {
  try {
    const user = JSON.parse(localStorage.getItem('mangrove_user') || 'null')
    return user?.role === 'SUPER_ADMIN'
  } catch { return false }
})()

function triggerSignatureUpload() {
  if (!isSuperAdmin) return
  signatureInput.value?.click()
}

async function onSignatureUpload(e) {
  const file = e.target.files?.[0]
  if (!file) return
  const formData = new FormData()
  formData.append('file', file)
  try {
    const res = await fetch('/api/files/upload', { method: 'POST', body: formData })
    const json = await res.json()
    if (json.code === 200 && json.data?.url) {
      signatureUrl.value = json.data.url
      localStorage.setItem('artist_signature_url', json.data.url)
    }
  } catch {
    alert('上传失败，请重试')
  }
}

function triggerAvatarUpload() {
  if (!isSuperAdmin) return
  avatarInput.value?.click()
}

async function onAvatarUpload(e) {
  const file = e.target.files?.[0]
  if (!file) return
  const formData = new FormData()
  formData.append('file', file)
  try {
    const res = await fetch('/api/files/upload', { method: 'POST', body: formData })
    const json = await res.json()
    if (json.code === 200 && json.data?.url) {
      avatarUrl.value = json.data.url
      localStorage.setItem('artist_avatar_url', json.data.url)
    }
  } catch {
    alert('上传失败，请重试')
  }
}

onMounted(async () => {
  const id = route.params.id || 1
  try {
    const res = await fetch(`/api/artists/${id}/detail`)
    if (res.ok) {
      const json = await res.json()
      const data = json.data || json
      if (data && data.id) {
        artist.value = { ...fallbackArtist, ...data }
      }
    }
  } catch {
    // 使用 fallback 数据
  }
})
</script>
