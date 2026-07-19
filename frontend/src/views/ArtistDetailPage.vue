<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-4xl mx-auto px-4 py-8">
      <h1 class="text-3xl font-bold text-gray-900 text-center mb-6">艺人介绍</h1>

      <!-- 头像 + 基础信息 -->
      <div class="flex flex-col md:flex-row gap-0 mb-6">
        <!-- 左侧：头像 + 签名 + 品牌 -->
        <div class="md:w-72 shrink-0 space-y-0">
          <div class="aspect-square rounded-tl-xl bg-mangrove-50 border border-mangrove-200 flex flex-col items-center justify-center overflow-hidden">
            <img v-if="artist.avatarUrl" :src="artist.avatarUrl" class="w-full h-full object-cover" />
            <div v-else class="flex flex-col items-center">
              <User class="w-16 h-16 text-mangrove-300 mb-3" />
              <span class="text-xs text-mangrove-400">点击上传头像</span>
            </div>
          </div>
          <div class="rounded-bl-xl border border-mangrove-200 bg-white border-t-0 p-4 text-center overflow-hidden">
            <span class="text-xs text-mangrove-500 block mb-2">签名</span>
            <img v-if="artist.signatureImageUrl" :src="artist.signatureImageUrl" class="w-full h-24 object-cover rounded-lg" />
            <div v-else class="h-24 bg-mangrove-50 rounded-lg flex items-center justify-center">
              <span class="text-gray-400 text-sm">点击上传签名图</span>
            </div>
          </div>
          <div class="rounded-br-xl border border-mangrove-200 bg-white border-t-0 p-4 text-center overflow-hidden">
            <span class="text-xs text-mangrove-500 block mb-2">品牌</span>
            <img v-if="artist.brandImageUrl" :src="artist.brandImageUrl" class="w-full h-24 object-cover rounded-lg" />
            <div v-else class="h-24 bg-mangrove-50 rounded-lg flex items-center justify-center">
              <span class="text-gray-400 text-sm">点击上传品牌标识</span>
            </div>
          </div>
        </div>
        <div class="flex-1 border border-mangrove-200 rounded-r-xl bg-white">
          <div class="bg-mangrove-50 rounded-tr-xl px-4 py-6 border-b border-mangrove-200">
            <h2 class="text-mangrove-700 font-bold text-center text-2xl">基础信息</h2>
          </div>
          <div class="p-4 pb-1">
            <div class="grid grid-cols-2 gap-x-6 gap-y-2 text-sm">
              <div class="flex items-start border-b border-gray-100 pb-2"><span class="text-mangrove-600 w-24 shrink-0">汉字名：</span><span class="text-gray-900 font-bold">{{ artist.name || '曲唱' }}</span></div>
              <div class="flex items-start border-b border-gray-100 pb-2"><span class="text-mangrove-600 w-24 shrink-0">日文名：</span><span class="text-gray-800">曲唱</span></div>
              <div class="flex items-start border-b border-gray-100 pb-2"><span class="text-mangrove-600 w-24 shrink-0">罗马音：</span><span class="text-gray-800">QUCHANG</span></div>
              <div class="flex items-start border-b border-gray-100 pb-2"><span class="text-mangrove-600 w-24 shrink-0">性别：</span><span class="text-gray-800">女</span></div>
              <div class="flex items-start border-b border-gray-100 pb-2"><span class="text-mangrove-600 w-24 shrink-0">年龄：</span><span class="text-gray-800">18岁</span></div>
              <div class="flex items-start border-b border-gray-100 pb-2"><span class="text-mangrove-600 w-24 shrink-0">生日：</span><span class="text-gray-800">07.28</span></div>
              <div class="flex items-start border-b border-gray-100 pb-2"><span class="text-mangrove-600 w-24 shrink-0">身高：</span><span class="text-gray-800">178.1cm</span></div>
              <div class="flex items-start border-b border-gray-100 pb-2"><span class="text-mangrove-600 w-24 shrink-0">星座：</span><span class="text-gray-800">狮子座 ♌</span></div>
              <div class="flex items-start border-b border-gray-100 pb-2"><span class="text-mangrove-600 w-24 shrink-0">国籍：</span><span class="text-gray-800">CN 中国</span></div>
              <div class="flex items-start border-b border-gray-100 pb-2"><span class="text-mangrove-600 w-24 shrink-0">MBTI：</span><span class="text-gray-800">ENTP-A</span></div>
              <div class="flex items-start border-b border-gray-100 pb-2"><span class="text-mangrove-600 w-24 shrink-0">公司：</span><span class="text-gray-800">A2O_Channel</span></div>
              <div class="flex items-start border-b border-gray-100 pb-2"><span class="text-mangrove-600 w-24 shrink-0">组合：</span><span class="text-gray-800">A2O_MAY</span></div>
              <div class="flex items-start border-b border-gray-100 pb-2"><span class="text-mangrove-600 w-24 shrink-0">担当：</span><span class="text-gray-800">Vocal 主唱</span></div>
              <div class="flex items-start border-b border-gray-100 pb-2"><span class="text-mangrove-600 w-24 shrink-0">昵称：</span><span class="text-gray-800">小芒 / Candice</span></div>
              <div class="flex items-start border-b border-gray-100 pb-2"><span class="text-mangrove-600 w-24 shrink-0">粉丝名：</span><span class="text-gray-800">音符 🎵</span></div>
              <div class="flex items-start border-b border-gray-100 pb-2"><span class="text-mangrove-600 w-24 shrink-0">出道日：</span><span class="text-gray-800">2024.12.20</span></div>
              <div class="flex items-start border-b border-gray-100 pb-2"><span class="text-mangrove-600 w-24 shrink-0">语言：</span><span class="text-gray-800">中文 · 英语 · 西语</span></div>
              <div class="flex items-start"><span class="text-mangrove-600 w-24 shrink-0">爱好：</span><span class="text-gray-800">睡觉 😴 唱歌 🎤 弹钢琴 🎹</span></div>
              <div class="flex items-start"><span class="text-mangrove-600 w-24 shrink-0">微博：</span><a href="https://m.weibo.cn/u/7957169158?wm=3333_2001&from=10G7093010&sourcetype=weixin&s_trans=Z%2BAptj5bg0L8TxBuBaeHSw%3D%3D__s&s_channel=4" target="_blank" class="text-mangrove-600 hover:underline whitespace-nowrap">@QUCHANG 🔗</a></div>
              <div class="flex items-start"><span class="text-mangrove-600 w-24 shrink-0">抖音：</span><a href="https://v.douyin.com/maFW9iTCSQw/" target="_blank" class="text-mangrove-600 hover:underline whitespace-nowrap">@QUCHANG 🔗</a></div>
            </div>
          </div>
        </div>
      </div>

      <!-- 详细介绍 -->
      <div class="bg-mangrove-50 rounded-xl px-4 py-3 mb-4">
        <h2 class="text-mangrove-700 font-bold text-center">详细介绍</h2>
      </div>
      <div class="space-y-3">
        <div
          v-for="section in bioSections"
          :key="section.id"
          class="rounded-xl border border-mangrove-200 bg-white p-4"
        >
          <p class="text-mangrove-600 font-medium text-sm mb-2">{{ section.question }}</p>
          <p class="text-sm text-gray-700 leading-relaxed">{{ section.answer }}</p>
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
const artist = ref({ stageName: '' })
const bioSections = ref([])
const loading = ref(true)

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
