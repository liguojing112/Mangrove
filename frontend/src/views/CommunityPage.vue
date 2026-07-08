<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24 pb-16">
    <!-- Header -->
    <h1 class="section-title mb-1">音符互动</h1>
    <p class="text-gray-500 mb-8">分享你的想法，和小伙伴们一起讨论</p>

    <!-- New Post Card -->
    <div class="card p-6 mb-8">
      <textarea
        placeholder="写下你想说的话..."
        class="w-full rounded-xl border border-gray-200 p-4 text-sm min-h-[100px] resize-none focus:outline-none focus:ring-2 focus:ring-mangrove-500 focus:border-transparent"
      ></textarea>
      <div class="flex items-center justify-between mt-4">
        <div class="flex items-center gap-2">
          <span class="tag">选择板块</span>
        </div>
        <button class="btn-primary text-sm">发布</button>
      </div>
    </div>

    <!-- Feed -->
    <div class="space-y-4">
      <div
        v-for="post in posts"
        :key="post.id"
        class="card-hover p-5"
      >
        <!-- Post Header -->
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-full bg-mangrove-200 flex items-center justify-center">
            <User class="w-5 h-5 text-mangrove-700" />
          </div>
          <div>
            <span class="font-medium text-gray-900 text-sm">{{ post.nickname }}</span>
            <span class="text-xs text-gray-400 ml-2">{{ post.time }}</span>
          </div>
        </div>

        <!-- Target Badge -->
        <div class="mt-3">
          <span class="tag">{{ post.target }}</span>
        </div>

        <!-- Content -->
        <p class="text-gray-700 mt-2 text-sm leading-relaxed">{{ post.content }}</p>

        <!-- Footer -->
        <div class="flex items-center gap-4 mt-3 text-xs text-gray-400">
          <button class="flex items-center gap-1 hover:text-mangrove-600 transition-colors">
            <Heart class="w-3.5 h-3.5" />
            <span>{{ post.likes }}</span>
          </button>
          <button class="flex items-center gap-1 hover:text-mangrove-600 transition-colors">
            <MessageCircle class="w-3.5 h-3.5" />
            <span>{{ post.replies }}</span>
          </button>
        </div>

        <!-- Reply -->
        <div
          v-if="post.reply"
          class="ml-12 border-l-2 border-gray-100 pl-4 mt-3 text-sm text-gray-600"
        >
          <p>{{ post.reply }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { User, Heart, MessageCircle } from 'lucide-vue-next'

const posts = [
  {
    id: 1,
    nickname: '星河漫步',
    time: '2 小时前',
    target: '@小芒新专辑',
    content: '新专辑太好听了！每首歌都是精品，循环播放一整天都不腻。最喜欢第三首的编曲，层次感拉满！',
    likes: 128,
    replies: 32,
    reply: '芒果小达人：同感！第三首的间奏太绝了',
  },
  {
    id: 2,
    nickname: '夏日清风',
    time: '5 小时前',
    target: '@小芒新专辑',
    content: '今天线下听了新歌，现场氛围太棒了！小芒的嗓音比录音室版本更有感染力。',
    likes: 89,
    replies: 15,
    reply: null,
  },
  {
    id: 3,
    nickname: '绿野仙踪',
    time: '8 小时前',
    target: '@小芒演唱会',
    content: '抢到票了！终于可以去现场了，期待了好久。有没有一起去的小伙伴？可以拼车～',
    likes: 256,
    replies: 48,
    reply: '青芒守护者：我也抢到了！我们可以一起',
  },
  {
    id: 4,
    nickname: '芒果小达人',
    time: '1 天前',
    target: '@小芒周边',
    content: '刚入手了新款应援手环，质量超好！戴在手上手感很棒，颜色也很正。推荐大家入手！',
    likes: 67,
    replies: 12,
    reply: null,
  },
  {
    id: 5,
    nickname: '青芒守护者',
    time: '2 天前',
    target: '@小芒新专辑',
    content: '把新专辑的歌词都打印出来了，准备学习里面的舞蹈动作。有没有一起练舞的？',
    likes: 45,
    replies: 8,
    reply: '星河漫步：加我一个！我也想学',
  },
]
</script>
