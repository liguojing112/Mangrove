<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Hero Card Showcase -->
    <section class="pt-20 pb-6">
      <div class="max-w-5xl mx-auto px-4">
        <div v-if="heroCards.length > 0" class="grid grid-cols-4 md:grid-cols-5 gap-3">
          <div v-for="(item, i) in heroCards" :key="i" class="group cursor-pointer"
            @click="openDouble(item, '卡面', '卡背')">
            <div class="relative aspect-[2/3] overflow-hidden rounded-xl border-[4.5px] border-pink-200 bg-white shadow-sm hover:shadow-md transition-all hover:border-pink-400">
              <img v-if="item.frontImageUrl" :src="item.frontImageUrl" :alt="item.name" class="absolute inset-0 h-full w-full object-cover transition-transform duration-300 group-hover:scale-105" />
              <div v-else class="absolute inset-0 flex items-center justify-center text-3xl text-gray-300">🃏</div>
              <div class="absolute inset-x-0 bottom-0 bg-gradient-to-t from-black/70 p-2">
                <p class="truncate text-[11px] font-medium text-white">{{ item.name }}</p>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="rounded-3xl bg-white border-[4.5px] border-pink-200 shadow-sm flex items-center justify-center py-20 text-gray-400">
          <ImageIcon class="w-10 h-10 mr-2" /><span class="text-sm">暂无小卡展示</span>
        </div>
      </div>
    </section>

    <div class="sticky top-16 z-40 border-b border-gray-200 bg-white/80 backdrop-blur-md">
      <div class="mx-auto flex max-w-7xl flex-wrap justify-center gap-2 px-4 py-3 sm:px-6 lg:px-8">
        <button v-for="section in sectionNav" :key="section.id" class="rounded-full border border-gray-200 px-4 py-1.5 text-xs font-medium text-gray-500 transition-all hover:border-teal-300 hover:bg-teal-50 hover:text-teal-600" @click="scrollTo(section.id)">{{ section.label }}</button>
      </div>
    </div>

    <div class="mx-auto max-w-7xl space-y-20 px-4 py-12 sm:px-6 lg:px-8">
      <section id="s1" class="scroll-mt-28 space-y-6">
        <header><h2 class="text-2xl font-light text-gray-800">小卡合集</h2><p class="mt-1 text-sm text-gray-500">官方卡 · 饭制卡 · 随机卡 · 高清扫图</p></header>
        <div class="flex flex-wrap gap-2"><button v-for="filter in cardFilters" :key="filter" class="rounded-full border px-3 py-1 text-xs font-medium transition-all" :class="cardFilter === filter ? 'border-teal-400 bg-teal-50 text-teal-600' : 'border-gray-200 text-gray-500 hover:text-gray-700'" @click="cardFilter = filter">{{ filter }}</button></div>
        <div class="grid grid-cols-2 gap-3 md:grid-cols-3 lg:grid-cols-5">
          <button v-for="item in filteredCards" :key="item.id || item.name" type="button" class="group text-left" :aria-label="`查看小卡：${item.name}`" @click="openDouble(item, '卡面', '卡背')">
            <div class="relative aspect-[2/3] overflow-hidden rounded-xl border-[4.5px] border-teal-200 bg-white transition-all hover:border-teal-400 shadow-sm hover:shadow-md">
              <img v-if="item.frontImageUrl" :src="item.frontImageUrl" :alt="item.name" class="absolute inset-0 h-full w-full object-cover transition-transform duration-300 group-hover:scale-105" />
              <div v-else class="absolute inset-0 flex items-center justify-center text-2xl text-gray-700"><ImageIcon /></div>
              <div class="absolute inset-x-0 bottom-0 bg-gradient-to-t from-black/90 p-3"><p class="truncate text-xs font-medium text-white">{{ item.name }}</p><div class="mt-1 flex items-center gap-2"><span :class="[rarityClass(item.rarity), 'rounded px-1.5 py-0.5 text-[10px]']">{{ item.rarity }}</span><span class="truncate text-[10px] text-gray-500">产出：{{ item.producerName }}</span></div></div>
            </div>
          </button>
        </div>
      </section>

      <section id="s2" class="scroll-mt-28 space-y-6">
        <header><h2 class="text-2xl font-light text-gray-800">纸质周边</h2><p class="mt-1 text-sm text-gray-500">手幅 · 明信片 · 书签 · 贴纸 · 台历 · 海报</p></header>
        <div class="columns-2 gap-3 lg:columns-4" aria-label="纸质周边列表">
          <button v-for="item in paperItems" :key="item.id || item.name" type="button" class="group mb-3 block w-full break-inside-avoid text-left" @click="openDouble(item, '正面', '背面')">
            <div class="relative overflow-hidden rounded-xl border-[4.5px] border-teal-200 bg-white transition-all hover:border-teal-400 shadow-sm hover:shadow-md">
              <img v-if="item.frontImageUrl" :src="item.frontImageUrl" :alt="`${item.name}正面`" class="block h-auto w-full transition-transform duration-300 group-hover:scale-105" />
              <div v-else class="flex aspect-[4/3] items-center justify-center text-gray-700"><ImageIcon /></div>
              <span class="absolute left-3 top-3 rounded-full bg-white/90 px-2 py-0.5 text-[10px] font-medium text-gray-800">{{ item.type }}</span>
              <div class="absolute inset-x-0 bottom-0 bg-gradient-to-t from-black/80 p-3"><p class="truncate text-xs font-medium text-white">{{ item.name }}</p><p class="mt-1 truncate text-[10px] text-white/60">产出：{{ item.producerName }}</p></div>
            </div>
          </button>
        </div>
      </section>

      <section id="s3" class="scroll-mt-28 space-y-6">
        <header><h2 class="text-2xl font-light text-gray-800">实物应援</h2><p class="mt-1 text-sm text-gray-500">徽章 · 立牌 · 钥匙扣 · 透卡 · 收纳周边</p></header>
        <div class="grid grid-cols-2 gap-5 md:grid-cols-3 lg:grid-cols-4">
          <button v-for="item in physicalItems" :key="item.id || item.name" type="button" class="group text-left" @click="selectedSingle = item">
            <div class="rounded-xl border-[4.5px] border-teal-200 bg-white p-4 shadow-sm hover:shadow-md transition-all hover:border-teal-400">
              <div class="relative mb-3 aspect-square overflow-hidden rounded-lg bg-gray-100"><img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.name" class="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105" /><div v-else class="flex h-full items-center justify-center text-gray-700"><ImageIcon /></div></div>
              <p class="truncate text-sm font-medium text-white">{{ item.name }}</p><div class="mt-1.5 flex items-center justify-between gap-2"><span class="inline-block rounded-full bg-amber-500/10 px-2 py-0.5 text-[10px] text-amber-400">{{ item.type }}</span><span class="truncate text-[10px] text-gray-500">产出：{{ item.producerName }}</span></div>
            </div>
          </button>
        </div>
      </section>

      <section id="s4" class="scroll-mt-28 space-y-6">
        <header><h2 class="text-2xl font-light text-gray-800">电子周边</h2><p class="mt-1 text-sm text-gray-500">壁纸 · 头像 · 锁屏 · 模板 · 表情包 · 可保存素材</p></header>
        <div class="flex flex-wrap gap-2"><button v-for="filter in digitalFilters" :key="filter" class="rounded-full border px-3 py-1 text-xs transition-all" :class="digitalFilter === filter ? 'border-teal-400 bg-teal-50 text-teal-600' : 'border-gray-200 text-gray-500 hover:text-gray-700'" @click="digitalFilter = filter">{{ filter }}</button></div>
        <div class="grid grid-cols-2 gap-4 md:grid-cols-4">
          <button v-for="item in filteredDigitalItems" :key="item.id || item.name" type="button" class="group text-left" @click="selectedSingle = item">
            <div class="overflow-hidden rounded-xl border-[4.5px] border-teal-200 bg-white p-1 shadow-sm hover:shadow-md transition-all hover:border-teal-400">
              <div class="relative aspect-[4/5] overflow-hidden rounded-lg bg-gray-100"><img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.name" class="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105" /><div v-else class="flex h-full items-center justify-center text-gray-400"><ImageIcon /></div><span class="absolute left-2 top-2 rounded-full bg-black/65 px-2 py-1 text-[10px] text-white">{{ item.type }}</span></div>
              <div class="px-2 py-2 text-center"><p class="truncate text-xs text-gray-400">{{ item.name }}</p><p class="mt-1 truncate text-[10px] text-gray-600">产出：{{ item.producerName }}</p></div>
            </div>
          </button>
        </div>
      </section>

      <section id="s5" class="scroll-mt-28 space-y-6">
        <header><h2 class="text-2xl font-light text-gray-800">往期应援存档</h2><p class="mt-1 text-sm text-gray-500">生日应援 · 回归应援 · 线下活动 · 周年纪念</p></header>
        <div class="grid gap-5 md:grid-cols-2 lg:grid-cols-3">
          <button v-for="item in archiveItems" :key="item.id || item.name" type="button" class="group overflow-hidden rounded-3xl border-[4.5px] border-teal-200 bg-white shadow-sm hover:shadow-md transition-all text-left hover:border-teal-400" @click="selectedSingle = item">
            <div class="relative aspect-video bg-gray-100"><img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.name" class="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105" /><div v-else class="flex h-full items-center justify-center text-gray-400"><ImageIcon /></div></div>
            <div class="p-4"><p class="text-xs text-amber-400">{{ item.date || item.type }}</p><h3 class="mt-1 truncate text-base font-medium text-white">{{ item.name }}</h3><p class="mt-1 text-[10px] text-gray-600">产出：{{ item.producerName }}</p><p v-if="item.description" class="mt-1 line-clamp-2 text-xs leading-5 text-gray-500">{{ item.description }}</p></div>
          </button>
        </div>
      </section>

      <section id="s6" class="scroll-mt-28 space-y-8">
        <header class="flex flex-wrap items-end justify-between gap-4"><div><h2 class="text-2xl font-light text-gray-800">产出周边套装</h2><p class="mt-1 text-sm text-gray-500">周边套装</p></div><router-link v-if="isLoggedIn" to="/profile?tab=merchandise&type=BUNDLE" class="rounded-lg border border-amber-500/30 px-4 py-2 text-xs font-medium text-amber-400 transition-colors hover:bg-amber-500/10">上传套装</router-link></header>
        <div v-if="bundleCreators.length" class="grid grid-cols-3 gap-x-4 gap-y-8 sm:grid-cols-4 md:grid-cols-6 lg:grid-cols-8">
          <router-link v-for="creator in bundleCreators" :key="creator.publicId" :to="`/merchandise/producer/${creator.publicId}`" class="group min-w-0 text-center">
            <div class="mx-auto flex aspect-square w-full max-w-24 items-center justify-center rounded-full border text-lg font-semibold transition-transform duration-200 group-hover:-translate-y-1" :style="avatarStyle(creator.publicId)">{{ avatarText(creator.username) }}</div>
            <p class="mt-3 truncate text-sm font-medium text-gray-800">{{ creator.username }}</p>
            <p class="mt-1 truncate font-mono text-[10px] text-gray-600">{{ creator.publicId }}</p>
            <p class="mt-1 text-[10px] text-gray-500">{{ creator.bundleCount }} 套作品</p>
          </router-link>
        </div>
        <div v-else class="border-y border-gray-200 py-16 text-center text-sm text-gray-500"><p>暂无套装产出</p><router-link v-if="isLoggedIn" to="/profile?tab=merchandise&type=BUNDLE" class="mt-4 inline-block text-amber-400 hover:text-amber-300">成为第一位产出者</router-link><router-link v-else to="/login" class="mt-4 inline-block text-amber-400 hover:text-amber-300">登录后上传套装</router-link></div>
      </section>

      <section id="s7" class="scroll-mt-28 space-y-6">
        <header><h2 class="text-2xl font-light text-gray-800">抽奖活动</h2><p class="mt-1 text-sm text-gray-500">参与抽奖，赢取小卡和周边</p></header>
        <div v-if="lotteries.length" class="grid gap-5 md:grid-cols-2 lg:grid-cols-3">
          <div v-for="lottery in lotteries" :key="lottery.id" class="rounded-3xl border-[4.5px] border-teal-200 bg-white shadow-sm hover:shadow-md transition-all p-5">
            <div class="flex items-start justify-between mb-3">
              <span class="px-2 py-0.5 rounded-full text-[10px] font-medium" :class="lotteryStatusClass(lottery.status)">{{ lotteryStatusLabel(lottery.status) }}</span>
              <span class="text-[10px] text-gray-500">{{ lottery.entryCount }} 人参与</span>
            </div>
            <h3 class="text-base font-medium text-gray-800 mb-1">{{ lottery.title }}</h3>
            <p v-if="lottery.description" class="text-xs text-gray-500 line-clamp-2 mb-3">{{ lottery.description }}</p>
            <div class="space-y-1.5 mb-4">
              <div class="flex items-center gap-2 text-xs text-gray-400">
                <Gift class="w-3.5 h-3.5 text-amber-400/70" />
                <span>奖品：{{ lottery.prizeDescription }}</span>
                <button v-if="lottery.imageUrl" type="button" class="ml-auto rounded-full border border-amber-500/30 px-2 py-0.5 text-[10px] text-amber-400 transition-colors hover:bg-amber-500/10" @click.stop="showPrize = lottery">展示奖品</button>
              </div>
              <div class="flex items-center gap-2 text-xs text-gray-500">
                <Clock class="w-3.5 h-3.5" />
                <span>{{ formatLotteryTime(lottery.startTime) }} ~ {{ formatLotteryTime(lottery.endTime) }}</span>
              </div>
              <div class="flex items-center gap-2 text-xs text-gray-500">
                <Users class="w-3.5 h-3.5" />
                <span>{{ eligibleLabel(lottery.eligibleRole) }} · {{ lottery.winnerCount }} 个中奖名额</span>
              </div>
              <div v-if="lottery.conditions" class="flex items-start gap-2 text-xs text-gray-500">
                <CheckCircle class="w-3.5 h-3.5 shrink-0 mt-0.5 text-amber-400/70" />
                <span>{{ lottery.conditions }}</span>
              </div>
            </div>
            <!-- Winners display -->
            <div v-if="lottery.status === 'DRAWN' && lottery.winnerNicknames?.length" class="bg-amber-500/10 border border-amber-500/20 rounded-lg p-3 mb-3">
              <p class="text-xs text-amber-400 font-medium mb-1.5">恭喜中奖者</p>
              <div class="flex flex-wrap gap-1.5">
                <span v-for="(name, i) in lottery.winnerNicknames" :key="i" class="px-2 py-0.5 rounded-full bg-amber-500/20 text-[11px] text-amber-300">{{ name }}</span>
              </div>
            </div>
            <!-- Action button -->
            <div v-if="isLoggedIn">
              <div v-if="lottery.status === 'DRAWN'" class="text-xs text-gray-500 text-center py-2">已开奖</div>
              <div v-else-if="lottery.userEntered" class="text-xs text-green-400 text-center py-2">✓ 已参与</div>
              <button v-else-if="lottery.status === 'ACTIVE'" class="w-full rounded-lg bg-amber-500/20 border border-amber-500/30 py-2 text-xs font-medium text-amber-400 transition-colors hover:bg-amber-500/30" @click="enterLottery(lottery)">参与抽奖</button>
              <div v-else-if="lottery.status === 'PENDING'" class="text-xs text-gray-500 text-center py-2">尚未开始</div>
              <div v-else-if="lottery.status === 'ENDED'" class="text-xs text-gray-500 text-center py-2">等待开奖</div>
            </div>
            <router-link v-else to="/login" class="block w-full rounded-lg border border-gray-700 py-2 text-center text-xs text-gray-500 transition-colors hover:border-gray-600 hover:text-gray-400">登录后参与</router-link>
          </div>
        </div>
        <div v-else class="border-y border-gray-200 py-16 text-center text-sm text-gray-500">暂无抽奖活动</div>
      </section>
    </div>

    <div v-if="selectedDouble" class="fixed inset-0 z-[120] flex items-center justify-center bg-black/90 p-4 backdrop-blur-sm" @click.self="selectedDouble = null">
      <div class="max-h-[94vh] w-full max-w-4xl overflow-y-auto rounded-xl border border-gray-800 bg-gray-950 p-5 shadow-2xl sm:p-7">
        <div class="mb-5 flex items-start justify-between"><div><p class="text-xs text-amber-400">{{ selectedDouble.type }}</p><h2 class="mt-1 text-xl font-light text-white">{{ selectedDouble.name }}</h2></div><button type="button" class="p-2 text-gray-500 hover:text-white" aria-label="关闭详情" @click="selectedDouble = null"><X class="h-5 w-5" /></button></div>
        <div class="grid grid-cols-2 gap-3 sm:gap-6">
          <section><p class="mb-2 text-center text-xs text-gray-500">{{ selectedDouble.frontLabel }}</p><div class="aspect-[2/3] overflow-hidden rounded-xl border border-gray-800 bg-gray-900"><img v-if="selectedDouble.frontImageUrl" :src="selectedDouble.frontImageUrl" :alt="`${selectedDouble.name}${selectedDouble.frontLabel}`" class="h-full w-full object-contain" /><div v-else class="flex h-full items-center justify-center text-xs text-gray-600">暂未上传</div></div></section>
          <section><p class="mb-2 text-center text-xs text-gray-500">{{ selectedDouble.backLabel }}</p><div class="aspect-[2/3] overflow-hidden rounded-xl border border-gray-800 bg-gray-900"><img v-if="selectedDouble.backImageUrl" :src="selectedDouble.backImageUrl" :alt="`${selectedDouble.name}${selectedDouble.backLabel}`" class="h-full w-full object-contain" /><div v-else class="flex h-full items-center justify-center text-xs text-gray-600">暂未上传</div></div></section>
        </div>
        <p v-if="selectedDouble.description" class="mt-5 text-sm leading-6 text-gray-500">{{ selectedDouble.description }}</p>
      </div>
    </div>

    <div v-if="selectedSingle" class="fixed inset-0 z-[120] flex items-center justify-center bg-black/90 p-4 backdrop-blur-sm" @click.self="selectedSingle = null">
      <div class="max-h-[94vh] w-full max-w-3xl overflow-y-auto rounded-xl border border-gray-800 bg-gray-950 p-5 shadow-2xl">
        <div class="mb-4 flex items-start justify-between"><div><p class="text-xs text-amber-400">{{ selectedSingle.type }}</p><h2 class="mt-1 text-xl font-light text-white">{{ selectedSingle.name }}</h2></div><button type="button" class="p-2 text-gray-500 hover:text-white" aria-label="关闭详情" @click="selectedSingle = null"><X class="h-5 w-5" /></button></div>
        <img v-if="selectedSingle.imageUrl" :src="selectedSingle.imageUrl" :alt="selectedSingle.name" class="max-h-[68vh] w-full rounded-lg bg-gray-900 object-contain" />
        <p v-if="selectedSingle.description" class="mt-4 text-sm leading-6 text-gray-500">{{ selectedSingle.description }}</p>
      </div>
    </div>
    <!-- 奖品展示弹窗 -->
    <div v-if="showPrize" class="fixed inset-0 z-[120] flex items-center justify-center bg-black/90 p-4 backdrop-blur-sm" @click.self="showPrize = null">
      <div class="max-h-[94vh] w-full max-w-3xl overflow-y-auto rounded-xl border border-gray-800 bg-gray-950 p-5 shadow-2xl">
        <div class="mb-4 flex items-start justify-between">
          <div>
            <p class="text-xs text-amber-400">奖品图片</p>
            <h2 class="mt-1 text-xl font-light text-white">{{ showPrize.title }}</h2>
          </div>
          <button type="button" class="p-2 text-gray-500 hover:text-white" @click="showPrize = null"><X class="h-5 w-5" /></button>
        </div>
        <img v-if="showPrize.imageUrl" :src="showPrize.imageUrl" :alt="showPrize.title" class="max-h-[70vh] w-full rounded-lg bg-gray-900 object-contain" />
        <p v-if="showPrize.description" class="mt-4 text-sm leading-6 text-gray-500">{{ showPrize.description }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { Clock, Gift, ImageIcon, Users, X, CheckCircle } from 'lucide-vue-next'

const sectionNav = [{ id: 's1', label: '小卡合集' }, { id: 's2', label: '纸质周边' }, { id: 's3', label: '实物应援' }, { id: 's4', label: '电子周边' }, { id: 's5', label: '往期存档' }, { id: 's6', label: '周边套装' }, { id: 's7', label: '抽奖活动' }]
const merchandise = ref([])
const isLoggedIn = !!localStorage.getItem('mangrove_token')
const cardFilter = ref('全部')
const digitalFilter = ref('全部')
const selectedDouble = ref(null)
const selectedSingle = ref(null)
const showPrize = ref(null)

function scrollTo(id) { document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' }) }
function byCategory(category) { return merchandise.value.filter(item => item.category === category) }
function normalize(item) { return { id: item.id, name: item.name, type: item.subCategory || '', rarity: item.rarity || 'COMMON', series: item.tags || item.subCategory || '', description: item.description || '', date: item.publishDate || '', frontImageUrl: item.cardFrontImageUrl || item.thumbnailUrl || item.highResImageUrl || '', backImageUrl: item.cardBackImageUrl || '', imageUrl: item.highResImageUrl || item.thumbnailUrl || item.cardFrontImageUrl || '', bundleImageUrls: item.bundleImageUrls || [], producerName: item.producerName || '站点', producerUsername: item.producerUsername || item.producerName || '站点', producerPublicId: item.producerPublicId || `SITE-${item.producerId || 0}`, producerRole: item.producerRole || '', category: item.category } }
const cards = computed(() => byCategory('PHOTOCARD').map(normalize))
const heroCards = computed(() => cards.value.slice(0, 5))
const paperItems = computed(() => byCategory('HAND_BANNER').map(normalize))
const physicalItems = computed(() => byCategory('PHYSICAL').map(normalize))
const digitalItems = computed(() => byCategory('DIGITAL').map(normalize))
const archiveItems = computed(() => byCategory('ARCHIVE').map(normalize))
const bundleItems = computed(() => merchandise.value.filter(item => item.category === 'BUNDLE').map(normalize))
const bundleCreators = computed(() => {
  const creators = new Map()
  bundleItems.value.forEach(bundle => {
    const key = bundle.producerPublicId
    if (!creators.has(key)) creators.set(key, { publicId: key, username: bundle.producerUsername, name: bundle.producerName, bundleCount: 0 })
    creators.get(key).bundleCount += 1
  })
  return [...creators.values()]
})
const cardFilters = computed(() => ['全部', ...new Set(cards.value.map(item => item.type).filter(Boolean))])
const filteredCards = computed(() => cardFilter.value === '全部' ? cards.value : cards.value.filter(item => item.type === cardFilter.value))
const digitalFilters = computed(() => ['全部', '壁纸', '头像', '锁屏', '模板', '表情包', ...new Set(digitalItems.value.map(item => item.type).filter(Boolean))].filter((item, index, array) => array.indexOf(item) === index))
const filteredDigitalItems = computed(() => digitalFilter.value === '全部' ? digitalItems.value : digitalItems.value.filter(item => item.type === digitalFilter.value))

function openDouble(item, frontLabel, backLabel) { selectedDouble.value = { ...item, frontLabel, backLabel } }
function avatarText(username) { return (username || 'U').slice(0, 2).toUpperCase() }
function avatarStyle(publicId) { let hash = 0; for (const char of publicId || '0') hash = ((hash << 5) - hash + char.charCodeAt(0)) | 0; const hue = Math.abs(hash) % 360; return { color: `hsl(${hue} 78% 72%)`, backgroundColor: `hsl(${hue} 55% 18%)`, borderColor: `hsl(${hue} 48% 38%)` } }
function rarityClass(rarity) { return { COMMON: 'bg-gray-700/60 text-gray-300', RARE: 'bg-amber-500/20 text-amber-400', LIMITED: 'bg-purple-500/20 text-purple-400', LEGENDARY: 'bg-amber-400 text-gray-900 font-bold' }[rarity] || 'bg-gray-700/60 text-gray-300' }

// Lottery
const lotteries = ref([])
const token = localStorage.getItem('mangrove_token')

async function fetchLotteries() {
  try {
    const res = await fetch('/api/public/lotteries')
    const json = await res.json()
    if (json.code === 200 && json.data) {
      const list = json.data
      if (token) {
        for (const l of list) {
          try {
            const sr = await fetch(`/api/user/lotteries/${l.id}/status`, { headers: { Authorization: `Bearer ${token}` } })
            const sj = await sr.json()
            l.userEntered = sj.data?.entered || false
          } catch { l.userEntered = false }
        }
      }
      lotteries.value = list
    }
  } catch { lotteries.value = [] }
}

async function enterLottery(lottery) {
  if (!token) return
  try {
    const res = await fetch(`/api/user/lotteries/${lottery.id}/enter`, { method: 'POST', headers: { Authorization: `Bearer ${token}` } })
    const json = await res.json()
    if (json.code === 200) { lottery.userEntered = true; lottery.entryCount = (lottery.entryCount || 0) + 1 }
    else { alert(json.msg || '参与失败') }
  } catch (e) { alert('参与失败：' + e.message) }
}

function formatLotteryTime(dt) { if (!dt) return ''; return dt.replace('T', ' ').substring(0, 16) }
function eligibleLabel(role) { return { ALL: '所有用户可参与', PRODUCER: '仅产出者可参与' }[role] || role }
function lotteryStatusLabel(s) { return { PENDING: '未开始', ACTIVE: '报名中', ENDED: '已结束', DRAWN: '已开奖' }[s] || s }
function lotteryStatusClass(s) { return { PENDING: 'bg-gray-700 text-gray-400', ACTIVE: 'bg-green-500/20 text-green-400', ENDED: 'bg-yellow-500/20 text-yellow-400', DRAWN: 'bg-amber-500/20 text-amber-400' }[s] || 'bg-gray-700 text-gray-400' }

async function fetchMerchandise() { try { const response = await fetch('/api/public/merchandise?page=0&size=100'); const json = await response.json(); if (response.ok && json.code === 200) merchandise.value = json.data?.content || [] } catch { merchandise.value = [] } }
onMounted(() => { fetchMerchandise(); fetchLotteries() })
</script>
