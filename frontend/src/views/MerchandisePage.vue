<template>
  <div class="bg-gray-950 min-h-screen">
    <!-- PAGE HEADER -->
    <section class="pt-24 pb-8 text-center">
      <p class="text-amber-400/60 tracking-[0.3em] text-xs mb-2">MANGO COLLECTION</p>
      <h1 class="text-white text-4xl font-light">芒园周边</h1>
      <p class="text-gray-500 text-sm mt-2">全站应援物料展示库</p>
      <div class="w-16 h-px bg-gradient-to-r from-transparent via-amber-400 to-transparent mx-auto mt-4"></div>
    </section>

    <!-- STICKY NAV -->
    <div class="sticky top-16 z-40 bg-gray-950/80 backdrop-blur-md border-b border-gray-800/50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-3 flex gap-2 justify-center flex-wrap">
        <button v-for="s in sectionNav" :key="s.id"
          class="px-4 py-1.5 rounded-full text-xs font-medium text-gray-400 hover:text-amber-400 hover:bg-amber-500/10 border border-gray-800 hover:border-amber-500/30 transition-all"
          @click="scrollTo(s.id)">{{ s.label }}</button>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 space-y-20">

      <!-- ========== 1. 小卡合集 ========== -->
      <section id="s1" class="space-y-6 scroll-mt-28">
        <div><h2 class="text-2xl font-light text-white">小卡合集</h2><p class="text-gray-500 text-sm mt-1">官方卡 · 饭制卡 · 随机卡 · 高清扫图</p></div>
        <!-- Hero -->
        <div class="relative rounded-2xl overflow-hidden border border-gray-800 bg-gradient-to-br from-gray-800 to-gray-900">
          <div class="aspect-[21/9] flex items-center justify-center relative">
            <div class="absolute inset-0 bg-gradient-to-br from-amber-500/10 via-transparent to-mangrove-500/10"></div>
            <span class="text-amber-400/30 text-6xl">🃏</span>
            <div class="absolute top-4 right-4 bg-amber-500 text-gray-900 px-3 py-1 rounded-full text-xs font-bold">本月推荐</div>
          </div>
          <div class="absolute bottom-0 left-0 right-0 p-6 bg-gradient-to-t from-black/80">
            <div class="flex items-center gap-3">
              <h3 class="text-white text-xl font-light">林夏五月限定小卡</h3>
              <span class="bg-gradient-to-r from-amber-400 to-yellow-300 text-gray-900 px-2 py-0.5 rounded text-xs font-bold">LEGENDARY</span>
            </div>
            <p class="text-gray-400 text-sm mt-1">五月花期系列</p>
          </div>
        </div>
        <!-- Sub filters -->
        <div class="flex gap-2 flex-wrap">
          <button v-for="f in cardFilters" :key="f"
            class="px-3 py-1 rounded-full text-xs font-medium transition-all"
            :class="cardFilter===f ? 'bg-amber-500/20 text-amber-400 border border-amber-500/30' : 'text-gray-500 hover:text-gray-300 border border-gray-800'"
            @click="cardFilter=f">{{ f }}</button>
        </div>
        <!-- Grid -->
        <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-3">
          <div v-for="c in filteredCards" :key="c.name" class="group cursor-pointer">
            <div class="aspect-[2/3] rounded-xl overflow-hidden border border-gray-800 hover:border-amber-500/30 transition-all bg-gradient-to-br from-gray-800 to-gray-900 relative">
              <div class="absolute inset-0 flex items-center justify-center"><span class="text-2xl opacity-20">🃏</span></div>
              <div class="absolute bottom-0 left-0 right-0 p-3 bg-gradient-to-t from-black/80">
                <p class="text-white text-xs font-medium truncate">{{ c.name }}</p>
                <div class="flex items-center gap-2 mt-1">
                  <span :class="[rarityCls(c.rarity), 'px-1.5 py-0.5 rounded text-[10px]']">{{ c.rarity }}</span>
                  <span class="text-gray-500 text-[10px]">{{ c.series }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- ========== 2. 纸质周边 ========== -->
      <section id="s2" class="space-y-6 scroll-mt-28">
        <div><h2 class="text-2xl font-light text-white">纸质周边</h2><p class="text-gray-500 text-sm mt-1">手幅 · 明信片 · 书签 · 贴纸 · 台历 · 海报</p></div>
        <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
          <div v-for="p in paperItems" :key="p.name" class="group cursor-pointer">
            <div :class="['rounded-xl overflow-hidden border border-gray-800 hover:border-amber-500/30 transition-all bg-gradient-to-br from-gray-800 to-gray-900 relative', p.ratio]">
              <span :class="['absolute top-3 left-3 px-2 py-0.5 rounded-full text-[10px] font-medium z-10', p.color]">{{ p.type }}</span>
              <div class="absolute inset-0 flex items-center justify-center"><span class="text-xl opacity-15">{{ p.icon }}</span></div>
              <div class="absolute bottom-0 left-0 right-0 p-3 bg-gradient-to-t from-black/60"><p class="text-white text-xs font-medium truncate">{{ p.name }}</p></div>
            </div>
          </div>
        </div>
      </section>

      <!-- ========== 3. 实物应援 ========== -->
      <section id="s3" class="space-y-6 scroll-mt-28">
        <div><h2 class="text-2xl font-light text-white">实物应援</h2><p class="text-gray-500 text-sm mt-1">徽章 · 立牌 · 钥匙扣 · 透卡 · 收纳周边</p></div>
        <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-5">
          <div v-for="p in physicalItems" :key="p.name" class="group cursor-pointer">
            <div class="rounded-2xl bg-gray-900 p-4 border border-gray-800 hover:border-amber-500/30 transition-all duration-300 hover:rotate-1">
              <div class="bg-gradient-to-br from-mangrove-900/30 to-gray-800 rounded-xl aspect-square mb-3 flex items-center justify-center"><span class="text-3xl opacity-30">{{ p.icon }}</span></div>
              <p class="font-medium text-sm text-white truncate">{{ p.name }}</p>
              <div class="flex items-center gap-2 mt-1.5">
                <span :class="['px-2 py-0.5 rounded-full text-[10px] font-medium', p.badge]">{{ p.type }}</span>
                <span class="text-xs text-gray-500">{{ p.material }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- ========== 4. 电子周边 ========== -->
      <section id="s4" class="space-y-6 scroll-mt-28">
        <div><h2 class="text-2xl font-light text-white">电子周边</h2><p class="text-gray-500 text-sm mt-1">壁纸 · 头像 · 锁屏 · 模板 · 可保存素材</p></div>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div v-for="d in digitalItems" :key="d.name" class="group cursor-pointer">
            <div class="rounded-2xl border-2 border-gray-700 bg-gray-800 p-1">
              <div class="aspect-[9/19] bg-gradient-to-b from-mangrove-400/20 to-mangrove-900/40 rounded-xl relative overflow-hidden flex items-center justify-center">
                <span class="text-2xl opacity-20">📱</span>
                <div class="absolute bottom-3 left-0 right-0 flex justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                  <span class="bg-amber-500/90 text-gray-900 px-3 py-1.5 rounded-full text-xs font-medium">💾 保存</span>
                </div>
              </div>
              <p class="text-xs text-center text-gray-400 mt-2">{{ d.name }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- ========== 5. 往期应援存档 ========== -->
      <section id="s5" class="space-y-6 scroll-mt-28">
        <div><h2 class="text-2xl font-light text-white">往期应援存档</h2><p class="text-gray-500 text-sm mt-1">生日应援 · 回归应援 · 线下活动全套物料</p></div>
        <div class="border-l-2 border-amber-500/30 pl-8 space-y-10">
          <div v-for="a in archiveItems" :key="a.date" class="relative">
            <div class="absolute left-0 top-1 w-3 h-3 rounded-full bg-amber-500 -translate-x-[calc(50%+1px)] ring-4 ring-gray-950"></div>
            <p class="text-amber-400 text-sm font-medium">{{ a.date }}</p>
            <h3 class="text-white text-lg font-semibold mt-1">{{ a.title }}</h3>
            <p class="text-gray-400 text-sm mt-1">{{ a.desc }}</p>
            <div class="grid grid-cols-3 gap-2 mt-3">
              <div v-for="n in 6" :key="n" class="aspect-square bg-gray-800 rounded-lg flex items-center justify-center"><span class="text-lg opacity-15">📸</span></div>
            </div>
          </div>
        </div>
      </section>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const sectionNav = [
  { id: 's1', label: '小卡合集' }, { id: 's2', label: '纸质周边' },
  { id: 's3', label: '实物应援' }, { id: 's4', label: '电子周边' },
  { id: 's5', label: '往期存档' }
]
function scrollTo(id) { document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' }) }

// --- Section 1 ---
const cardFilter = ref('全部')
const cardFilters = ['全部', '官方卡', '饭制卡', '随机卡', '全套图鉴']
const allCards = [
  { name: '林夏限定小卡', rarity: 'LEGENDARY', series: '五月花期', type: '官方卡' },
  { name: '苏念亲签照', rarity: 'LIMITED', series: '星河系列', type: '官方卡' },
  { name: '陈予随机小卡', rarity: 'RARE', series: '第三辑', type: '随机卡' },
  { name: '林夏生日限定卡', rarity: 'LEGENDARY', series: '生日系列', type: '官方卡' },
  { name: '全员合照小卡', rarity: 'LIMITED', series: '周年纪念', type: '官方卡' },
  { name: '隐藏款闪卡', rarity: 'LEGENDARY', series: '绿野篇', type: '随机卡' },
  { name: '官方出道小卡', rarity: 'COMMON', series: '出道纪念', type: '官方卡' },
  { name: '饭制手绘卡', rarity: 'RARE', series: '同人创作', type: '饭制卡' },
  { name: '随机闪卡包', rarity: 'RARE', series: '星光系列', type: '随机卡' },
  { name: '五周年纪念卡', rarity: 'LIMITED', series: '周年纪念', type: '全套图鉴' }
]
const filteredCards = ref(cardFilter.value === '全部' ? allCards : allCards.filter(c => c.type === cardFilter.value))
import { computed } from 'vue'
const filteredCards2 = computed(() => cardFilter.value === '全部' ? allCards : allCards.filter(c => c.type === cardFilter.value))

// --- Section 2 ---
const paperItems = [
  { name: '绿野演唱会手幅', type: '手幅', ratio: 'aspect-[5/2]', color: 'bg-rose-100 text-rose-800', icon: '🎀' },
  { name: '周年纪念明信片', type: '明信片', ratio: 'aspect-[4/3]', color: 'bg-sky-100 text-sky-800', icon: '💌' },
  { name: '角色书签套装', type: '书签', ratio: 'aspect-[1/2]', color: 'bg-emerald-100 text-emerald-800', icon: '🔖' },
  { name: '主题贴纸包', type: '贴纸', ratio: 'aspect-square', color: 'bg-violet-100 text-violet-800', icon: '✨' },
  { name: '新年台历', type: '台历', ratio: 'aspect-[3/4]', color: 'bg-amber-100 text-amber-800', icon: '📅' },
  { name: '绿野巡回海报', type: '海报', ratio: 'aspect-[2/3]', color: 'bg-cyan-100 text-cyan-800', icon: '🖼️' },
  { name: '应援手幅·星辰', type: '手幅', ratio: 'aspect-[5/2]', color: 'bg-rose-100 text-rose-800', icon: '🎀' },
  { name: '限定明信片', type: '明信片', ratio: 'aspect-[4/3]', color: 'bg-sky-100 text-sky-800', icon: '💌' }
]

// --- Section 3 ---
const physicalItems = [
  { name: '金属徽章套装', type: '徽章', material: '金属', icon: '🎖️', badge: 'bg-red-100 text-red-800' },
  { name: '亚克力立牌', type: '立牌', material: '亚克力', icon: '🧸', badge: 'bg-blue-100 text-blue-800' },
  { name: '珐琅钥匙扣', type: '钥匙扣', material: '金属', icon: '🔑', badge: 'bg-purple-100 text-purple-800' },
  { name: '透明卡套', type: '透卡', material: 'PVC', icon: '💳', badge: 'bg-pink-100 text-pink-800' },
  { name: '主题收纳包', type: '收纳', material: '帆布', icon: '👜', badge: 'bg-emerald-100 text-emerald-800' },
  { name: '限定徽章礼盒', type: '徽章', material: '金属', icon: '🎁', badge: 'bg-red-100 text-red-800' },
  { name: '钥匙扣套装', type: '钥匙扣', material: '金属', icon: '🗝️', badge: 'bg-purple-100 text-purple-800' },
  { name: '帆布包·青芒绿', type: '收纳', material: '帆布', icon: '🎒', badge: 'bg-emerald-100 text-emerald-800' }
]

// --- Section 4 ---
const digitalItems = [
  { name: '绿野主题壁纸' }, { name: '星辰动态壁纸' }, { name: '林夏头像框' }, { name: '苏念头像框' },
  { name: '花雨锁屏' }, { name: '绿野锁屏' }, { name: '应援模板' }, { name: '社交头图模板' }
]

// --- Section 5 ---
const archiveItems = [
  { date: '2026年3月', title: '🎂 林夏生日应援', desc: '全网联动的生日应援企划，包含限定小卡、生日海报、应援灯牌全套物料。' },
  { date: '2025年10月', title: '🔥 绿野回归应援', desc: '新专辑回归应援活动，机场应援+回归直播+线下快闪全套物料。' },
  { date: '2024年7月', title: '🎪 线下见面会', desc: '粉丝见面会应援，入场手幅+纪念票根+见面会专属贴纸。' },
  { date: '2023年12月', title: '⭐ 出道五周年', desc: '出道五周年庆典应援，限量纪念徽章+五周年特别画册+全员签名海报。' }
]

function rarityCls(r) {
  const m = { COMMON: 'bg-gray-700/60 text-gray-300', RARE: 'bg-amber-500/20 text-amber-400', LIMITED: 'bg-purple-500/20 text-purple-400', LEGENDARY: 'bg-gradient-to-r from-amber-400 to-yellow-300 text-gray-900 font-bold' }
  return m[r] || m.COMMON
}
</script>
