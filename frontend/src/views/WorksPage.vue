<template>
  <div class="bg-gray-950 min-h-screen">
    <!-- PAGE HEADER -->
    <section class="pt-24 pb-12 text-center">
      <p class="text-amber-400/60 tracking-[0.3em] text-xs mb-2">CREATOR HALL</p>
      <h1 class="text-white text-4xl font-light tracking-wide">创作者殿堂</h1>
      <p class="text-gray-500 text-sm mt-3">独家高端产出专区</p>
      <div class="w-24 h-px bg-gradient-to-r from-transparent via-amber-400 to-transparent mx-auto mt-6"></div>
    </section>

    <div class="sticky top-16 z-40 bg-gray-950/80 backdrop-blur-md border-b border-gray-800/50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-3 flex gap-2 justify-center flex-wrap">
        <button v-for="s in nav" :key="s.id" class="px-4 py-1.5 rounded-full text-xs font-medium text-gray-400 hover:text-amber-400 hover:bg-amber-500/10 border border-gray-800 hover:border-amber-500/30 transition-all" @click="scrollTo(s.id)">{{ s.label }}</button>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 space-y-24">

      <!-- ① 高清路透专区 -->
      <section id="s1" class="space-y-6 scroll-mt-28">
        <div class="flex items-center gap-3">
          <span class="w-1 h-8 bg-amber-400 rounded-full"></span>
          <div><h2 class="text-2xl font-light text-white">高清路透专区</h2><p class="text-gray-500 text-xs mt-0.5">线下活动 · 机场上班路 · 无水印精修存档</p></div>
          <span class="ml-auto bg-red-500/20 text-red-400 text-xs px-2 py-0.5 rounded-full">HOT</span>
        </div>
        <div class="flex gap-4 overflow-x-auto pb-2 -mx-4 px-4">
          <div v-for="r in rehearsals" :key="r.title" class="shrink-0 w-[300px] md:w-[360px] group cursor-pointer">
            <div class="aspect-[16/10] rounded-xl overflow-hidden border border-gray-800 hover:border-amber-500/30 transition-all bg-gradient-to-br from-gray-800 to-gray-900 relative">
              <div class="absolute inset-0 flex items-center justify-center"><span class="text-4xl opacity-15">📸</span></div>
              <div class="absolute top-3 left-3"><span :class="['px-2 py-0.5 rounded-full text-[10px]', r.tagColor]">{{ r.tag }}</span></div>
              <div class="absolute bottom-0 left-0 right-0 p-4 bg-gradient-to-t from-black/90">
                <h3 class="text-white text-sm font-medium truncate">{{ r.title }}</h3>
                <div class="flex items-center gap-3 mt-1 text-xs text-gray-400"><span>👁 {{ r.views }}</span><span>{{ r.date }}</span></div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- ② 优秀产出创作者榜 -->
      <section id="s2" class="space-y-6 scroll-mt-28">
        <div class="flex items-center gap-3">
          <span class="w-1 h-8 bg-amber-400 rounded-full"></span>
          <div><h2 class="text-2xl font-light text-white">优秀产出创作者榜</h2><p class="text-gray-500 text-xs mt-0.5">修图太太 · 剪辑太太 · 饭绘太太 · 文案博主</p></div>
        </div>
        <div class="flex gap-2 flex-wrap">
          <button v-for="c in creatorCats" :key="c" class="px-3 py-1 rounded-full text-xs font-medium transition-all"
            :class="creatorCat===c ? 'bg-amber-500/20 text-amber-400 border border-amber-500/30' : 'text-gray-500 hover:text-gray-300 border border-gray-800'" @click="creatorCat=c">{{ c }}</button>
        </div>
        <div class="relative rounded-2xl overflow-hidden bg-gradient-to-br from-amber-900/20 via-gray-900 to-gray-900 border border-amber-500/20 p-6">
          <div class="absolute top-3 right-3"><span class="text-2xl">🥇</span></div>
          <div class="flex items-center gap-5">
            <div class="w-20 h-20 rounded-full bg-gradient-to-br from-amber-400 to-amber-600 flex items-center justify-center shrink-0"><span class="text-3xl">👑</span></div>
            <div class="flex-1">
              <p class="text-amber-400 text-xs tracking-wider mb-1">TOP 1 · 修图</p>
              <h3 class="text-white text-xl font-semibold">星野太太</h3>
              <p class="text-gray-400 text-sm mt-1">128 作品 · 3.2w 获赞 · 连续6月高产</p>
            </div>
            <div class="hidden md:flex gap-2">
              <div v-for="i in 3" :key="i" class="w-16 h-16 rounded-lg bg-gray-800 flex items-center justify-center"><span class="text-xs opacity-20">🖼️</span></div>
            </div>
          </div>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div v-for="c in topCreators" :key="c.name" class="rounded-xl bg-gray-900 border border-gray-800 p-5 flex items-center gap-4 hover:border-amber-500/20 transition-all">
            <div :class="['w-10 h-10 rounded-full flex items-center justify-center shrink-0 text-sm font-bold', c.rankBg]">{{ c.rank }}</div>
            <div class="w-12 h-12 rounded-full bg-gray-800 flex items-center justify-center shrink-0"><span class="text-lg">{{ c.avatar }}</span></div>
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2"><span class="text-sm font-medium text-white truncate">{{ c.name }}</span><span class="text-[10px] px-1.5 py-0.5 rounded-full" :class="c.tagBg">{{ c.type }}</span></div>
              <p class="text-xs text-gray-500 mt-0.5">{{ c.stats }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- ③ 精选作品展厅 -->
      <section id="s3" class="space-y-6 scroll-mt-28">
        <div class="flex items-center gap-3">
          <span class="w-1 h-8 bg-amber-400 rounded-full"></span>
          <div><h2 class="text-2xl font-light text-white">精选作品展厅</h2><p class="text-gray-500 text-xs mt-0.5">月度最佳修图 · 剪辑 · 饭绘 · 原创图文</p></div>
          <span class="ml-auto text-amber-400 text-xs px-2 py-0.5 rounded-full border border-amber-500/30">月度精选</span>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div v-for="w in showcaseWorks" :key="w.title" class="group cursor-pointer">
            <div class="rounded-2xl overflow-hidden border border-gray-800 hover:border-amber-500/30 transition-all bg-gradient-to-br from-gray-800 to-gray-900">
              <div :class="['aspect-[4/3] flex items-center justify-center relative', w.bg]">
                <span class="text-4xl opacity-20">{{ w.icon }}</span>
                <div class="absolute top-3 left-3"><span :class="['px-2 py-0.5 rounded-full text-[10px] font-medium', w.tagColor]">{{ w.tag }}</span></div>
                <div v-if="w.award" class="absolute top-3 right-3 bg-amber-500 text-gray-900 px-2 py-0.5 rounded-full text-[10px] font-bold">🏆 {{ w.award }}</div>
              </div>
              <div class="p-5">
                <h3 class="text-white font-medium">{{ w.title }}</h3>
                <div class="flex items-center gap-2 mt-2">
                  <div class="w-6 h-6 rounded-full bg-gray-700 flex items-center justify-center"><span class="text-[10px]">{{ w.avatar }}</span></div>
                  <span class="text-xs text-gray-400">{{ w.author }}</span>
                </div>
                <p class="text-gray-500 text-xs mt-2 line-clamp-2">{{ w.desc }}</p>
                <div class="flex items-center gap-4 mt-3 text-xs text-gray-500"><span>👁 {{ w.views }}</span><span>❤ {{ w.likes }}</span></div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- ④ 创作者荣誉墙 -->
      <section id="s4" class="space-y-6 scroll-mt-28">
        <div class="flex items-center gap-3">
          <span class="w-1 h-8 bg-amber-400 rounded-full"></span>
          <div><h2 class="text-2xl font-light text-white">创作者荣誉墙</h2><p class="text-gray-500 text-xs mt-0.5">长期高产 · 高质量创作者公示</p></div>
        </div>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div v-for="h in honorWall" :key="h.name" class="rounded-xl bg-gray-900 border border-gray-800 p-5 text-center hover:border-amber-500/20 transition-all">
            <div :class="['text-4xl font-bold mb-2', h.rank <= 3 ? 'text-amber-400' : 'text-gray-600']">{{ h.rank }}</div>
            <div :class="['w-14 h-14 mx-auto rounded-full flex items-center justify-center border-2', h.rank <= 3 ? 'border-amber-400 bg-amber-400/10' : 'border-gray-700 bg-gray-800']"><span class="text-xl">{{ h.avatar }}</span></div>
            <p class="text-sm font-medium text-white mt-3">{{ h.name }}</p>
            <p :class="['text-[10px] px-2 py-0.5 rounded-full inline-block mt-1', h.badgeBg]">{{ h.badge }}</p>
            <p class="text-xs text-gray-500 mt-2">{{ h.stats }}</p>
          </div>
        </div>
        <div class="text-center pt-4"><p class="text-gray-600 text-xs">以上创作者由网站根据长期产出质量、数量、社区贡献综合评定，每月更新</p></div>
      </section>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const nav = [{ id: 's1', label: '高清路透' }, { id: 's2', label: '创作者榜' }, { id: 's3', label: '精选展厅' }, { id: 's4', label: '荣誉墙' }]
function scrollTo(id) { document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' }) }

const rehearsals = [
  { title: '机场上班路高清纪实·7月', tag: '机场', tagColor: 'bg-teal-500/20 text-teal-400', views: '2.3k', date: '07/03' },
  { title: '绿野音乐会现场直击', tag: '演出', tagColor: 'bg-red-500/20 text-red-400', views: '5.1k', date: '06/28' },
  { title: '综艺录制下班路透', tag: '综艺', tagColor: 'bg-purple-500/20 text-purple-400', views: '3.8k', date: '06/25' },
  { title: '签售会高清精修合集', tag: '活动', tagColor: 'bg-blue-500/20 text-blue-400', views: '4.2k', date: '06/20' },
  { title: '机场出发·夏日清新', tag: '机场', tagColor: 'bg-teal-500/20 text-teal-400', views: '1.9k', date: '06/15' },
  { title: '红毯高清无水印', tag: '演出', tagColor: 'bg-red-500/20 text-red-400', views: '6.7k', date: '06/10' },
  { title: '上班路侧拍纪实', tag: '机场', tagColor: 'bg-teal-500/20 text-teal-400', views: '1.5k', date: '06/05' },
  { title: '生日会高清精选', tag: '活动', tagColor: 'bg-blue-500/20 text-blue-400', views: '8.3k', date: '05/28' },
]

const creatorCat = ref('全部')
const creatorCats = ['全部', '修图', '剪辑', '饭绘', '文案']
const topCreators = [
  { rank: '2', rankBg: 'bg-amber-400/20 text-amber-400', avatar: '🎨', name: '月光修图坊', type: '修图', tagBg: 'bg-rose-100 text-rose-800', stats: '96作品 · 2.1w赞' },
  { rank: '3', rankBg: 'bg-amber-700/20 text-amber-700', avatar: '🎬', name: '剪辑手小森', type: '剪辑', tagBg: 'bg-blue-100 text-blue-800', stats: '72作品 · 1.8w赞' },
  { rank: '4', rankBg: 'bg-gray-700 text-gray-400', avatar: '✏️', name: '饭绘阿琳', type: '饭绘', tagBg: 'bg-purple-100 text-purple-800', stats: '58作品 · 1.5w赞' },
]

const showcaseWorks = [
  { title: '绿野舞台·光影修图', tag: '修图', tagColor: 'bg-rose-500/20 text-rose-400', award: '月度最佳修图', bg: 'bg-gradient-to-br from-rose-900/20 to-gray-800', icon: '🖼️', avatar: '🌙', author: '月光修图坊', desc: '绿野演唱会舞台高清修图，光影层次细腻，质感拉满。', views: '4.2k', likes: '892' },
  { title: '小芒·四季剪辑混剪', tag: '剪辑', tagColor: 'bg-blue-500/20 text-blue-400', award: '月度最佳剪辑', bg: 'bg-gradient-to-br from-blue-900/20 to-gray-800', icon: '🎬', avatar: '🌲', author: '剪辑手小森', desc: '四季主题混剪，画面与BGM完美卡点。', views: '6.8k', likes: '1.5k' },
  { title: 'Q版小芒日常系列', tag: '饭绘', tagColor: 'bg-purple-500/20 text-purple-400', award: '月度最佳饭绘', bg: 'bg-gradient-to-br from-purple-900/20 to-gray-800', icon: '✏️', avatar: '🖊️', author: '饭绘阿琳', desc: 'Q版小芒日常生活系列，可爱治愈画风。', views: '3.1k', likes: '756' },
  { title: '给小芒的一封信', tag: '文案', tagColor: 'bg-emerald-500/20 text-emerald-400', award: '优质原创图文', bg: 'bg-gradient-to-br from-emerald-900/20 to-gray-800', icon: '📝', avatar: '✨', author: '文字里的星光', desc: '细腻的文字记录与小芒的点滴，感动万千粉丝。', views: '2.4k', likes: '623' },
]

const honorWall = [
  { rank: 1, avatar: '👑', name: '星野太太', badge: '6月冠军', badgeBg: 'bg-amber-500/20 text-amber-400', stats: '128作品' },
  { rank: 2, avatar: '🌙', name: '月光修图坊', badge: '5月冠军', badgeBg: 'bg-amber-500/20 text-amber-400', stats: '96作品' },
  { rank: 3, avatar: '🌲', name: '剪辑手小森', badge: '4月冠军', badgeBg: 'bg-amber-500/20 text-amber-400', stats: '72作品' },
  { rank: 4, avatar: '🖊️', name: '饭绘阿琳', badge: '3月冠军', badgeBg: 'bg-gray-700 text-gray-400', stats: '58作品' },
  { rank: 5, avatar: '✨', name: '文字里的星光', badge: '2月冠军', badgeBg: 'bg-gray-700 text-gray-400', stats: '45作品' },
  { rank: 6, avatar: '🎨', name: '画画的北北', badge: '1月冠军', badgeBg: 'bg-gray-700 text-gray-400', stats: '38作品' },
  { rank: 7, avatar: '📸', name: '镜头下的光', badge: '12月冠军', badgeBg: 'bg-gray-700 text-gray-400', stats: '31作品' },
  { rank: 8, avatar: '🎬', name: '影像记录者', badge: '11月冠军', badgeBg: 'bg-gray-700 text-gray-400', stats: '27作品' },
]
</script>
