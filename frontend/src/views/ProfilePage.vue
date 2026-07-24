<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-20 pb-16">
    <!-- Back Button -->
    <div class="mb-4">
      <router-link to="/" class="inline-flex items-center gap-1 text-sm text-gray-400 hover:text-mangrove-600 transition-colors">
        ← 返回首页
      </router-link>
    </div>
    <!-- Not Logged In -->
    <div v-if="!isLoggedIn" class="max-w-md mx-auto">
      <div class="card p-8 text-center">
        <div class="bg-mangrove-100 rounded-2xl h-48 flex items-center justify-center mb-6">
          <User class="w-20 h-20 text-mangrove-400/60" />
        </div>
        <p class="text-gray-500 mb-6">登录后查看个人中心</p>
        <router-link to="/login" class="btn-primary inline-block">立即登录</router-link>
      </div>
    </div>

    <!-- Logged In -->
    <div v-else>
      <!-- Profile Header -->
      <div class="card p-8 mb-8">
        <div class="flex flex-col sm:flex-row items-center sm:items-start gap-6">
          <UserAvatar :public-id="profile.publicId" :username="profile.nickname" size="xl" />
          <div class="text-center sm:text-left">
            <h1 class="text-xl font-bold text-gray-900 mb-1">{{ profile.nickname }}</h1>
            <p class="text-sm text-gray-600 mb-3">{{ profile.bio || '这个人很懒，什么都没写' }}</p>
            <p class="text-sm text-gray-400">{{ profile.username }}</p>
            <p class="mt-1 font-mono text-xs text-gray-400">ID · {{ profile.publicId }}</p>
          </div>
        </div>
      </div>

      <!-- Content Tabs -->
      <div class="flex items-center gap-2 mb-6 overflow-x-auto">
        <button
          v-for="tab in contentTabs"
          :key="tab.key"
          class="px-5 py-2 rounded-full text-sm font-medium transition-colors whitespace-nowrap"
          :class="activeTab === tab.key
            ? 'bg-mangrove-700 text-white'
            : 'bg-gray-100 text-gray-500 hover:bg-gray-200'"
          @click="activeTab = tab.key"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- Tab: My Works -->
      <div v-if="activeTab === 'works'" class="mb-12">
        <div v-if="myWorks.length === 0" class="text-center py-8 text-gray-400">
          <p>还没有投稿作品</p>
        </div>
        <div v-else class="space-y-4">
          <div v-for="work in myWorks" :key="work.id" class="card p-4">
            <div class="flex items-center justify-between">
              <div>
                <h3 class="font-medium text-gray-900">{{ work.title }}</h3>
                <p class="text-xs text-gray-400 mt-1">{{ formatTime(work.createdAt) }}</p>
              </div>
              <span
                class="px-3 py-1 rounded-full text-xs font-medium"
                :class="statusClass(work.status)"
              >
                {{ statusText(work.status) }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Tab: My Notes -->
      <div v-if="activeTab === 'notes'" class="mb-12">
        <div class="flex justify-between items-center mb-4">
          <h2 class="font-semibold text-gray-900">我的记事簿</h2>
          <button class="btn-primary text-sm" @click="showNoteForm = true">新建记事</button>
        </div>

        <!-- Note Form -->
        <div v-if="showNoteForm" class="card p-4 mb-4">
          <input
            v-model="noteForm.title"
            placeholder="标题"
            class="w-full rounded-lg border border-gray-200 p-3 text-sm mb-3 focus:outline-none focus:ring-2 focus:ring-mangrove-500"
          />
          <textarea
            v-model="noteForm.content"
            placeholder="写下你的想法..."
            class="w-full rounded-lg border border-gray-200 p-3 text-sm min-h-[120px] resize-none focus:outline-none focus:ring-2 focus:ring-mangrove-500"
          ></textarea>
          <div class="flex justify-end gap-2 mt-3">
            <button class="btn-secondary text-sm" @click="showNoteForm = false">取消</button>
            <button class="btn-primary text-sm" @click="saveNote">保存</button>
          </div>
        </div>

        <div v-if="notes.length === 0 && !showNoteForm" class="text-center py-8 text-gray-400">
          <p>还没有记事，写下你的第一条笔记吧</p>
        </div>
        <div v-else class="space-y-3">
          <div v-for="note in notes" :key="note.id" class="card p-4">
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <h3 class="font-medium text-gray-900">{{ note.title }}</h3>
                <p class="text-sm text-gray-600 mt-1 line-clamp-2">{{ note.content }}</p>
                <p class="text-xs text-gray-400 mt-2">{{ formatTime(note.updatedAt) }}</p>
              </div>
              <button class="text-gray-400 hover:text-red-500 ml-3" @click="deleteNote(note.id)">
                <Trash2 class="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Tab: Merchandise submissions -->
      <div v-if="activeTab === 'merchandise'" class="mb-12">
        <div class="card p-5 sm:p-6">
          <div class="mb-5"><h2 class="font-semibold text-gray-900">{{ editingMerchId ? '编辑周边' : '周边投稿' }}</h2><p class="mt-1 text-xs text-gray-500">提交后会自动记录你的昵称作为产出者。</p></div>
          <div class="grid gap-4 sm:grid-cols-2">
            <label class="block sm:col-span-2"><span class="mb-1 block text-sm text-gray-600">{{ merchForm.category === 'BUNDLE' ? '套装名称' : '名称' }}</span><input v-model.trim="merchForm.name" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm" /></label>
            <label class="block"><span class="mb-1 block text-sm text-gray-600">类型</span><select v-model="merchForm.category" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm" @change="syncMerchCategory"><option value="PHOTOCARD">小卡</option><option value="HAND_BANNER">纸质周边</option><option value="PHYSICAL">实物应援</option><option value="DIGITAL">电子周边</option><option value="ARCHIVE">往期应援存档</option><option value="BUNDLE">产出周边套装</option></select></label>
            <label class="block"><span class="mb-1 block text-sm text-gray-600">分类</span><select v-model="merchForm.subCategory" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm"><option v-for="option in merchSubcategories" :key="option" :value="option">{{ option }}</option></select></label>
            <label class="block sm:col-span-2"><span class="mb-1 block text-sm text-gray-600">{{ merchForm.category === 'BUNDLE' ? '设计介绍' : '描述' }}</span><textarea v-model.trim="merchForm.description" rows="3" class="w-full resize-none rounded-lg border border-gray-200 px-3 py-2 text-sm"></textarea></label>

            <template v-if="isDoubleMerchandise">
              <ImageUploadField :label="merchForm.category === 'PHOTOCARD' ? '卡面' : '正面'" :url="merchForm.cardFrontImageUrl" :uploading="merchUploading === 'front'" @upload="uploadMerchImage($event, 'cardFrontImageUrl', 'front')" @clear="merchForm.cardFrontImageUrl = ''" />
              <ImageUploadField :label="merchForm.category === 'PHOTOCARD' ? '卡背' : '背面'" :url="merchForm.cardBackImageUrl" :uploading="merchUploading === 'back'" @upload="uploadMerchImage($event, 'cardBackImageUrl', 'back')" @clear="merchForm.cardBackImageUrl = ''" />
            </template>
            <template v-else-if="merchForm.category === 'BUNDLE'">
              <div class="sm:col-span-2"><div class="mb-3 flex items-center justify-between"><span class="text-sm text-gray-600">套装周边图片</span><button type="button" class="btn-secondary text-xs" @click="merchForm.bundleImageUrls.push('')">添加图片</button></div><div class="grid gap-4 sm:grid-cols-3"><ImageUploadField v-for="(url, index) in merchForm.bundleImageUrls" :key="index" :label="`周边 ${index + 1}`" :url="url" :uploading="merchUploading === `bundle-${index}`" wide @upload="uploadMerchBundleImage($event, index)" @clear="removeMerchBundleImage(index)" /></div></div>
            </template>
            <ImageUploadField v-else class="sm:col-span-2" label="周边图片" :url="merchForm.highResImageUrl" :uploading="merchUploading === 'single'" wide @upload="uploadMerchImage($event, 'highResImageUrl', 'single')" @clear="merchForm.highResImageUrl = ''" />
          </div>
          <p v-if="merchError" class="mt-4 text-sm text-red-600">{{ merchError }}</p>
          <div class="mt-5 flex justify-end gap-2">
            <button v-if="editingMerchId" class="btn-secondary text-sm" @click="cancelEdit">取消编辑</button>
            <button class="btn-primary text-sm" :disabled="merchSaving || !!merchUploading" @click="submitMerchandise">{{ merchSaving ? '保存中...' : (editingMerchId ? '保存修改' : '提交周边') }}</button>
          </div>
        </div>

        <div class="mt-6 space-y-3">
          <h2 class="font-semibold text-gray-900">我的周边产出</h2>
          <div v-if="myMerchandise.length === 0" class="py-8 text-center text-sm text-gray-400">还没有周边投稿</div>
          <div v-for="item in myMerchandise" :key="item.id" class="card flex items-center gap-4 p-4">
            <img :src="item.bundleImageUrls?.[0] || item.cardFrontImageUrl || item.highResImageUrl" alt="" class="h-16 w-16 rounded object-cover" />
            <div class="min-w-0 flex-1">
              <p class="truncate font-medium text-gray-900">{{ item.name }}</p>
              <p class="mt-1 text-xs text-gray-500">{{ merchandiseCategoryLabel(item.category) }} · 产出者：{{ item.producerName }}</p>
            </div>
            <div class="flex items-center gap-2 flex-shrink-0">
              <button class="p-2 rounded-lg hover:bg-blue-50 text-gray-400 hover:text-blue-600 transition-colors" title="编辑" @click="editMerchandise(item)">
                <Edit2 class="w-4 h-4" />
              </button>
              <button class="p-2 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-colors" title="删除" @click="deleteMerchandise(item)">
                <Trash2 class="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Tab: Lottery -->
      <div v-if="activeTab === 'lottery'" class="mb-12">
        <!-- 发起抽奖 -->
        <div class="card p-5 sm:p-6 mb-6">
          <div class="mb-5"><h2 class="font-semibold text-gray-900">{{ lotteryEditingId ? '编辑抽奖' : '发起抽奖' }}</h2><p class="mt-1 text-xs text-gray-500">发起人、抽奖声明、抽奖图片、抽奖条件、起止日期</p></div>
          <div class="grid gap-4 sm:grid-cols-2">
            <label class="block sm:col-span-2"><span class="mb-1 block text-sm text-gray-600">抽奖声明</span><input v-model.trim="lotteryForm.title" placeholder="给关注我的粉丝抽个奖" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm" /></label>
            <label class="block sm:col-span-2"><span class="mb-1 block text-sm text-gray-600">抽奖说明</span><textarea v-model.trim="lotteryForm.description" rows="2" placeholder="奖品详情..." class="w-full resize-none rounded-lg border border-gray-200 px-3 py-2 text-sm"></textarea></label>
            <label class="block sm:col-span-2"><span class="mb-1 block text-sm text-gray-600">抽奖图片</span>
              <ImageUploadField label="" :url="lotteryForm.imageUrl" :uploading="lotteryUploading === 'image'" @upload="uploadLotteryImage" @clear="lotteryForm.imageUrl = ''" wide />
            </label>
            <label class="block sm:col-span-2"><span class="mb-1 block text-sm text-gray-600">抽奖条件</span><textarea v-model.trim="lotteryForm.conditions" rows="2" placeholder="例如：关注我、转发动态、点赞即可参与" class="w-full resize-none rounded-lg border border-gray-200 px-3 py-2 text-sm"></textarea></label>
            <label class="block"><span class="mb-1 block text-sm text-gray-600">中奖人数</span><input v-model.number="lotteryForm.winnerCount" type="number" min="1" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm" /></label>
            <label class="block"><span class="mb-1 block text-sm text-gray-600">开始时间</span><input v-model="lotteryForm.startTime" type="datetime-local" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm" /></label>
            <label class="block"><span class="mb-1 block text-sm text-gray-600">结束时间</span><input v-model="lotteryForm.endTime" type="datetime-local" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm" /></label>
          </div>
          <p v-if="lotteryError" class="mt-4 text-sm text-red-600">{{ lotteryError }}</p>
          <div class="mt-5 flex justify-end gap-2">
            <button v-if="lotteryEditingId" class="btn-secondary text-sm" @click="cancelLotteryEdit">取消编辑</button>
            <button class="btn-primary text-sm" :disabled="lotterySaving || !!lotteryUploading" @click="submitLottery">{{ lotterySaving ? '保存中...' : (lotteryEditingId ? '保存修改' : '发起抽奖') }}</button>
          </div>
        </div>

        <!-- 我发起的抽奖 -->
        <h2 class="font-semibold text-gray-900 mb-4">我的抽奖</h2>
        <div v-if="myLotteries.length === 0" class="py-8 text-center text-sm text-gray-400">还没有发起过抽奖</div>
        <div v-else class="space-y-3">
          <div v-for="lt in myLotteries" :key="lt.id" class="card p-4">
            <div class="flex items-start gap-3">
              <img v-if="lt.imageUrl" :src="lt.imageUrl" class="w-16 h-16 rounded-lg object-cover shrink-0" />
              <div v-else class="w-16 h-16 rounded-lg bg-mangrove-100 flex items-center justify-center shrink-0"><Gift class="w-6 h-6 text-mangrove-400" /></div>
              <div class="min-w-0 flex-1">
                <div class="flex items-center gap-2 mb-1">
                  <span class="px-2 py-0.5 rounded-full text-xs font-medium" :class="lotteryCardStatusClass(lt.status)">{{ lotteryCardStatusLabel(lt.status) }}</span>
                  <span class="text-xs text-gray-400">{{ lt.entryCount }} 人参与</span>
                </div>
                <p class="font-medium text-gray-900 truncate">{{ lt.title }}</p>
                <p v-if="lt.conditions" class="text-xs text-gray-500 mt-1 line-clamp-1">条件：{{ lt.conditions }}</p>
                <p class="text-xs text-gray-400 mt-1">{{ lotteryCardTime(lt.startTime) }} ~ {{ lotteryCardTime(lt.endTime) }}</p>
                <div v-if="lt.status === 'DRAWN' && lt.winnerNicknames?.length" class="mt-2 flex flex-wrap gap-1">
                  <span v-for="(n,i) in lt.winnerNicknames" :key="i" class="px-2 py-0.5 rounded-full bg-amber-100 text-amber-700 text-[11px]">中奖：{{ n }}</span>
                </div>
              </div>
              <div class="flex items-center gap-1 shrink-0">
                <button v-if="lt.status === 'PENDING'" class="p-1.5 rounded-lg hover:bg-blue-50 text-gray-400 hover:text-blue-600" title="编辑" @click="editMyLottery(lt)"><Edit2 class="w-4 h-4" /></button>
                <button class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500" title="删除" @click="deleteMyLottery(lt)"><Trash2 class="w-4 h-4" /></button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 我的提问 -->
      <div v-if="activeTab === 'questions'" class="mb-12">
        <h2 class="font-semibold text-gray-900 mb-4">我的提问</h2>
        <p class="text-xs text-gray-500 mb-3">你向艺人提出的问题，等管理员回答后会在艺人页面展示</p>

        <div v-if="myQuestions.length === 0" class="py-8 text-center text-sm text-gray-400">还没有提过问</div>
        <div v-else class="space-y-3">
          <div v-for="q in myQuestions" :key="q.id" class="card p-4">
            <p class="text-sm font-medium text-gray-800 mb-1">❓ {{ q.question }}</p>
            <p v-if="q.answer" class="text-sm text-gray-600">💡 {{ q.answer }}</p>
            <p v-else class="text-xs text-amber-500">等待回答中...</p>
          </div>
        </div>
      </div>

      <!-- Settings -->
      <h2 class="font-semibold text-gray-900 mb-4">设置</h2>
      <div class="card overflow-hidden">
        <!-- 喜欢艺人开始日期 -->
        <div class="flex items-center justify-between px-5 py-4 border-b border-gray-100">
          <span class="text-sm text-gray-700">喜欢艺人的开始日期</span>
          <div class="flex items-center gap-2">
            <input
              v-model="favoriteDate"
              type="date"
              class="rounded-lg border border-gray-200 px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500"
              :max="todayStr"
            />
            <button
              class="btn-primary text-xs px-3 py-1.5"
              :disabled="savingFavoriteDate"
              @click="saveFavoriteDate"
            >
              {{ savingFavoriteDate ? '保存中...' : '保存' }}
            </button>
          </div>
        </div>
        <div
          v-for="(item, idx) in settings"
          :key="item"
          class="flex items-center justify-between px-5 py-4 cursor-pointer hover:bg-gray-50"
          :class="idx < settings.length - 1 ? 'border-b border-gray-100' : ''"
          @click="handleSetting(item)"
        >
          <span class="text-sm text-gray-700">{{ item }}</span>
          <ChevronRight class="w-4 h-4 text-gray-400" />
        </div>
      </div>
      <p v-if="favoriteDateSaved" class="mt-2 text-xs text-green-600 text-center">保存成功！首页日历将显示您的相伴天数</p>
    </div>
    <!-- Edit Profile Modal -->
    <div v-if="showEditProfile" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4" @click.self="showEditProfile = false">
      <div class="bg-white rounded-2xl shadow-xl w-full max-w-md p-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">修改资料</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm text-gray-600 mb-1">昵称</label>
            <input v-model="editForm.nickname" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" placeholder="输入昵称" />
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-1">个性签名</label>
            <textarea v-model="editForm.bio" rows="3" class="w-full resize-none rounded-lg border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" placeholder="写点什么介绍自己..."></textarea>
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-1">邮箱</label>
            <input v-model="editForm.email" type="email" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" placeholder="可选" />
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-1">手机号</label>
            <input v-model="editForm.phone" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" placeholder="可选" />
          </div>
        </div>
        <p v-if="editProfileMsg" class="text-xs mt-3" :class="editProfileMsg.startsWith('✓') ? 'text-green-600' : 'text-red-500'">{{ editProfileMsg }}</p>
        <div class="flex justify-end gap-2 mt-5">
          <button class="btn-secondary text-sm" @click="showEditProfile = false">取消</button>
          <button class="btn-primary text-sm" :disabled="editProfileSaving" @click="saveProfile">{{ editProfileSaving ? '保存中...' : '保存' }}</button>
        </div>
      </div>
    </div>

    <!-- Change Password Modal -->
    <div v-if="showChangePassword" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4" @click.self="showChangePassword = false">
      <div class="bg-white rounded-2xl shadow-xl w-full max-w-md p-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">账号安全</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm text-gray-600 mb-1">当前密码</label>
            <input v-model="pwdForm.oldPassword" type="password" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" placeholder="输入当前密码" />
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-1">新密码</label>
            <input v-model="pwdForm.newPassword" type="password" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" placeholder="至少6位" />
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-1">确认新密码</label>
            <input v-model="pwdForm.confirmPassword" type="password" class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" placeholder="再次输入新密码" />
          </div>
        </div>
        <p v-if="pwdMsg" class="text-xs mt-3" :class="pwdMsg.startsWith('✓') ? 'text-green-600' : 'text-red-500'">{{ pwdMsg }}</p>
        <div class="flex justify-end gap-2 mt-5">
          <button class="btn-secondary text-sm" @click="showChangePassword = false">取消</button>
          <button class="btn-primary text-sm" :disabled="pwdSaving" @click="savePassword">{{ pwdSaving ? '修改中...' : '修改密码' }}</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { computed, ref, reactive, onMounted } from 'vue'
import { User, ChevronRight, Trash2, Edit2, Gift } from 'lucide-vue-next'
import { useAuth } from '@/composables/useAuth'
import { useRoute, useRouter } from 'vue-router'
import ImageUploadField from '@/components/admin/ImageUploadField.vue'
import UserAvatar from '@/components/UserAvatar.vue'

const { isLoggedIn, currentUser, getToken, logout } = useAuth()
const router = useRouter()
const route = useRoute()

const profile = ref({
  username: '',
  nickname: '',
  bio: '',
  email: ''
})

const contentTabs = [
  { key: 'works', label: '我的作品' },
  { key: 'notes', label: '记事簿' },
  { key: 'merchandise', label: '周边投稿' },
  { key: 'lottery', label: '抽奖' },
  { key: 'questions', label: '我的提问' }
]
const activeTab = ref('works')

const settings = ['修改资料', '账号安全', '退出登录']

const favoriteDate = ref('')
const savingFavoriteDate = ref(false)
const favoriteDateSaved = ref(false)
const todayStr = new Date().toISOString().split('T')[0]

const myWorks = ref([])
const notes = ref([])
const showNoteForm = ref(false)
const noteForm = ref({ title: '', content: '' })
const myMerchandise = ref([])
const editingMerchId = ref(null)
const merchSaving = ref(false)
const merchUploading = ref('')
const merchError = ref('')
const merchCategoryOptions = {
  PHOTOCARD: ['官方卡', '饭制卡', '随机卡', '全套图鉴'], HAND_BANNER: ['手幅', '明信片', '书签', '贴纸', '台历', '海报'],
  PHYSICAL: ['徽章', '立牌', '钥匙扣', '透卡', '收纳周边'], DIGITAL: ['壁纸', '头像', '锁屏', '模板', '表情包'],
  ARCHIVE: ['生日应援', '回归应援', '线下活动', '周年纪念'], BUNDLE: ['周边套装']
}
const newMerchForm = () => ({ name: '', category: 'PHOTOCARD', subCategory: '官方卡', description: '', highResImageUrl: '', thumbnailUrl: '', cardFrontImageUrl: '', cardBackImageUrl: '', bundleImageUrls: [''], rarity: 'COMMON' })
const merchForm = ref(newMerchForm())
const merchSubcategories = computed(() => merchCategoryOptions[merchForm.value.category] || [])
const isDoubleMerchandise = computed(() => ['PHOTOCARD', 'HAND_BANNER'].includes(merchForm.value.category))

async function fetchProfile() {
  try {
    const res = await fetch('/api/auth/me', {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      profile.value = json.data
      if (json.data.favoriteStartDate) {
        favoriteDate.value = json.data.favoriteStartDate
      }
    }
  } catch (e) {
    console.error('获取用户信息失败:', e)
  }
}

async function saveFavoriteDate() {
  savingFavoriteDate.value = true
  favoriteDateSaved.value = false
  try {
    const res = await fetch('/api/auth/me/favorite-date', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({ favoriteStartDate: favoriteDate.value || null })
    })
    const json = await res.json()
    if (json.code === 200) {
      favoriteDateSaved.value = true
      setTimeout(() => { favoriteDateSaved.value = false }, 3000)
    }
  } catch (e) {
    console.error('保存失败:', e)
  } finally {
    savingFavoriteDate.value = false
  }
}

async function fetchMyWorks() {
  try {
    const res = await fetch('/api/works/my', {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      myWorks.value = json.data.content || []
    }
  } catch (e) {
    console.error('获取作品失败:', e)
  }
}

async function fetchNotes() {
  try {
    const res = await fetch('/api/notes', {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200 && json.data) {
      notes.value = json.data.content || []
    }
  } catch (e) {
    console.error('获取记事失败:', e)
  }
}

async function fetchMyMerchandise() {
  try { const res = await fetch('/api/user/merchandise', { headers: { Authorization: `Bearer ${getToken()}` } }); const json = await res.json(); if (json.code === 200) myMerchandise.value = json.data || [] } catch (e) { console.error('获取周边投稿失败:', e) }
}

function syncMerchCategory() { merchForm.value.subCategory = merchCategoryOptions[merchForm.value.category]?.[0] || '' }
async function uploadMerchFile(event, uploadKey) {
  const file = event.target.files?.[0]; event.target.value = ''; if (!file) return ''
  merchUploading.value = uploadKey; merchError.value = ''
  try { const body = new FormData(); body.append('file', file); const res = await fetch('/api/files/upload', { method: 'POST', headers: { Authorization: `Bearer ${getToken()}` }, body }); const json = await res.json(); if (!res.ok || json.code !== 200) throw new Error(json.msg || '上传失败'); return json.data.url } catch (e) { merchError.value = e.message; return '' } finally { merchUploading.value = '' }
}
async function uploadMerchImage(event, field, key) { const url = await uploadMerchFile(event, key); if (url) merchForm.value[field] = url }
async function uploadMerchBundleImage(event, index) { const url = await uploadMerchFile(event, `bundle-${index}`); if (url) merchForm.value.bundleImageUrls[index] = url }
function removeMerchBundleImage(index) { merchForm.value.bundleImageUrls.splice(index, 1); if (!merchForm.value.bundleImageUrls.length) merchForm.value.bundleImageUrls.push('') }
function merchandiseCategoryLabel(category) { return { PHOTOCARD: '小卡', HAND_BANNER: '纸质周边', PHYSICAL: '实物应援', DIGITAL: '电子周边', ARCHIVE: '往期存档', BUNDLE: '产出周边套装' }[category] || category }
async function submitMerchandise() {
  merchError.value = ''; const payload = { ...merchForm.value, bundleImageUrls: merchForm.value.bundleImageUrls.filter(Boolean) }
  if (!payload.name) return void (merchError.value = '请填写名称')
  if (isDoubleMerchandise.value) { if (!payload.cardFrontImageUrl) return void (merchError.value = '请上传正面图片'); payload.highResImageUrl = payload.cardFrontImageUrl; payload.thumbnailUrl = payload.cardFrontImageUrl }
  else if (payload.category === 'BUNDLE') { if (!payload.description) return void (merchError.value = '请填写设计介绍'); if (!payload.bundleImageUrls.length) return void (merchError.value = '请至少上传一张套装周边图片'); payload.highResImageUrl = payload.bundleImageUrls[0]; payload.thumbnailUrl = payload.bundleImageUrls[0] }
  else if (!payload.highResImageUrl) return void (merchError.value = '请上传周边图片')
  merchSaving.value = true
  try {
    const isEdit = !!editingMerchId.value
    const url = isEdit ? `/api/user/merchandise/${editingMerchId.value}` : '/api/user/merchandise'
    const method = isEdit ? 'PUT' : 'POST'
    const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` }, body: JSON.stringify(payload) })
    const json = await res.json()
    if (!res.ok || json.code !== 200) throw new Error(json.msg || '提交失败')
    merchForm.value = newMerchForm(); editingMerchId.value = null; await fetchMyMerchandise()
  } catch (e) { merchError.value = e.message } finally { merchSaving.value = false }
}

function editMerchandise(item) {
  editingMerchId.value = item.id
  merchForm.value = {
    name: item.name || '',
    category: item.category || 'PHOTOCARD',
    subCategory: item.subCategory || '',
    description: item.description || '',
    highResImageUrl: item.highResImageUrl || '',
    thumbnailUrl: item.thumbnailUrl || '',
    cardFrontImageUrl: item.cardFrontImageUrl || '',
    cardBackImageUrl: item.cardBackImageUrl || '',
    bundleImageUrls: item.bundleImageUrls?.length ? [...item.bundleImageUrls] : [''],
    rarity: item.rarity || 'COMMON'
  }
  merchError.value = ''
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function cancelEdit() {
  editingMerchId.value = null
  merchForm.value = newMerchForm()
  merchError.value = ''
}

async function deleteMerchandise(item) {
  if (!confirm(`确定要删除「${item.name}」吗？此操作不可撤销。`)) return
  try {
    const res = await fetch(`/api/user/merchandise/${item.id}`, { method: 'DELETE', headers: { Authorization: `Bearer ${getToken()}` } })
    const json = await res.json()
    if (json.code === 200) {
      await fetchMyMerchandise()
    } else {
      alert(json.msg || '删除失败')
    }
  } catch (e) {
    alert('删除失败：' + e.message)
  }
}

async function saveNote() {
  if (!noteForm.value.title.trim() || !noteForm.value.content.trim()) return
  try {
    const res = await fetch('/api/notes', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify(noteForm.value)
    })
    const json = await res.json()
    if (json.code === 200) {
      noteForm.value = { title: '', content: '' }
      showNoteForm.value = false
      await fetchNotes()
    }
  } catch (e) {
    console.error('保存记事失败:', e)
  }
}

async function deleteNote(id) {
  if (!confirm('确定要删除这条记事吗？')) return
  try {
    const res = await fetch(`/api/notes/${id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      await fetchNotes()
    }
  } catch (e) {
    console.error('删除记事失败:', e)
  }
}

function handleSetting(item) {
  if (item === '修改资料') {
    editForm.value = { nickname: profile.value.nickname || '', bio: profile.value.bio || '', email: profile.value.email || '', phone: profile.value.phone || '' }
    editProfileMsg.value = ''
    showEditProfile.value = true
  } else if (item === '账号安全') {
    pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
    pwdMsg.value = ''
    showChangePassword.value = true
  } else if (item === '退出登录') {
    logout()
    router.push('/')
  }
}

// ── 修改资料 ──
const showEditProfile = ref(false)
const editForm = ref({ nickname: '', bio: '', email: '', phone: '' })
const editProfileSaving = ref(false)
const editProfileMsg = ref('')

async function saveProfile() {
  if (!editForm.value.nickname.trim()) { editProfileMsg.value = '昵称不能为空'; return }
  editProfileSaving.value = true
  editProfileMsg.value = ''
  try {
    const res = await fetch('/api/auth/me', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` },
      body: JSON.stringify(editForm.value)
    })
    const json = await res.json()
    if (json.code === 200) {
      profile.value.nickname = editForm.value.nickname
      profile.value.bio = editForm.value.bio
      profile.value.email = editForm.value.email
      profile.value.phone = editForm.value.phone
      editProfileMsg.value = '✓ 资料已更新'
      setTimeout(() => { showEditProfile.value = false }, 1500)
    } else {
      editProfileMsg.value = '✗ ' + (json.msg || '保存失败')
    }
  } catch (e) { editProfileMsg.value = '✗ 网络错误' }
  finally { editProfileSaving.value = false }
}

// ── 账号安全 ──
const showChangePassword = ref(false)
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })
const pwdSaving = ref(false)
const pwdMsg = ref('')

async function savePassword() {
  if (!pwdForm.value.oldPassword) { pwdMsg.value = '请输入当前密码'; return }
  if (!pwdForm.value.newPassword || pwdForm.value.newPassword.length < 6) { pwdMsg.value = '新密码至少6位'; return }
  if (pwdForm.value.newPassword !== pwdForm.value.confirmPassword) { pwdMsg.value = '两次输入的新密码不一致'; return }
  pwdSaving.value = true
  pwdMsg.value = ''
  try {
    const res = await fetch('/api/auth/me/password', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` },
      body: JSON.stringify({ oldPassword: pwdForm.value.oldPassword, newPassword: pwdForm.value.newPassword })
    })
    const json = await res.json()
    if (json.code === 200) {
      pwdMsg.value = '✓ 密码已修改'
      setTimeout(() => { showChangePassword.value = false }, 1500)
    } else {
      pwdMsg.value = '✗ ' + (json.msg || '修改失败')
    }
  } catch (e) { pwdMsg.value = '✗ 网络错误' }
  finally { pwdSaving.value = false }
}

function statusClass(status) {
  const map = {
    PENDING: 'bg-yellow-100 text-yellow-700',
    PUBLISHED: 'bg-green-100 text-green-700',
    HIDDEN: 'bg-red-100 text-red-700',
    DRAFT: 'bg-gray-100 text-gray-700'
  }
  return map[status] || 'bg-gray-100 text-gray-700'
}

function statusText(status) {
  const map = {
    PENDING: '审核中',
    PUBLISHED: '已通过',
    HIDDEN: '未通过',
    DRAFT: '草稿'
  }
  return map[status] || status
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString()
}

// ========== Lottery ==========
const lotteryForm = reactive({ title: '', description: '', imageUrl: '', conditions: '', winnerCount: 1, startTime: '', endTime: '' })
const lotteryEditingId = ref(null)
const lotterySaving = ref(false)
const lotteryUploading = ref('')
const lotteryError = ref('')
const myLotteries = ref([])
const myQuestions = ref([])

async function fetchMyQuestions() {
  try {
    const res = await fetch('/api/artist-bio/my-questions', { headers: { Authorization: `Bearer ${getToken()}` } })
    const json = await res.json()
    if (json.code === 200) myQuestions.value = json.data || []
  } catch {}
}

function emptyLotteryForm() { return { title: '', description: '', imageUrl: '', conditions: '', winnerCount: 1, startTime: '', endTime: '' } }

async function fetchMyLotteries() {
  try {
    const res = await fetch('/api/public/lotteries', { headers: { Authorization: `Bearer ${getToken()}` } })
    const json = await res.json()
    if (json.code === 200) {
      const user = JSON.parse(localStorage.getItem('mangrove_user') || '{}')
      myLotteries.value = (json.data || []).filter(l => l.createdBy === user.id)
    }
  } catch { myLotteries.value = [] }
}

function editMyLottery(lt) {
  lotteryEditingId.value = lt.id
  Object.assign(lotteryForm, {
    title: lt.title, description: lt.description || '', imageUrl: lt.imageUrl || '',
    conditions: lt.conditions || '', winnerCount: lt.winnerCount,
    startTime: lt.startTime ? lt.startTime.slice(0, 16) : '',
    endTime: lt.endTime ? lt.endTime.slice(0, 16) : ''
  })
  lotteryError.value = ''
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function cancelLotteryEdit() {
  lotteryEditingId.value = null
  Object.assign(lotteryForm, emptyLotteryForm())
  lotteryError.value = ''
}

async function submitLottery() {
  lotteryError.value = ''
  if (!lotteryForm.title) return void (lotteryError.value = '请填写抽奖声明')
  if (!lotteryForm.startTime || !lotteryForm.endTime) return void (lotteryError.value = '请设置起止日期')
  lotterySaving.value = true
  try {
    const isEdit = !!lotteryEditingId.value
    const url = isEdit ? `/api/user/lotteries/${lotteryEditingId.value}` : '/api/user/lotteries'
    const res = await fetch(url, {
      method: isEdit ? 'PUT' : 'POST',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` },
      body: JSON.stringify(lotteryForm)
    })
    const json = await res.json()
    if (!res.ok || json.code !== 200) throw new Error(json.msg || '保存失败')
    Object.assign(lotteryForm, emptyLotteryForm())
    lotteryEditingId.value = null
    await fetchMyLotteries()
  } catch (e) { lotteryError.value = e.message } finally { lotterySaving.value = false }
}

async function deleteMyLottery(lt) {
  if (!confirm(`确定删除抽奖"${lt.title}"吗？`)) return
  try {
    const res = await fetch(`/api/user/lotteries/${lt.id}`, { method: 'DELETE', headers: { Authorization: `Bearer ${getToken()}` } })
    const json = await res.json()
    if (json.code === 200) await fetchMyLotteries()
    else alert(json.msg || '删除失败')
  } catch (e) { alert('删除失败：' + e.message) }
}

async function uploadLotteryImage(event) {
  const file = event.target.files?.[0]; event.target.value = ''; if (!file) return
  lotteryUploading.value = 'image'; lotteryError.value = ''
  try {
    const body = new FormData(); body.append('file', file)
    const res = await fetch('/api/files/upload', { method: 'POST', headers: { Authorization: `Bearer ${getToken()}` }, body })
    const json = await res.json()
    if (!res.ok || json.code !== 200) throw new Error(json.msg || '上传失败')
    lotteryForm.imageUrl = json.data.url
  } catch (e) { lotteryError.value = e.message } finally { lotteryUploading.value = '' }
}

function lotteryCardStatusLabel(s) { return { PENDING:'未开始', ACTIVE:'报名中', ENDED:'已结束', DRAWN:'已开奖' }[s] || s }
function lotteryCardStatusClass(s) { return { PENDING:'bg-gray-100 text-gray-600', ACTIVE:'bg-green-100 text-green-700', ENDED:'bg-yellow-100 text-yellow-700', DRAWN:'bg-amber-100 text-amber-700' }[s] || 'bg-gray-100 text-gray-600' }
function lotteryCardTime(dt) { if (!dt) return ''; return new Date(dt).toLocaleDateString() }

onMounted(() => {
  if (isLoggedIn.value) {
    if (route.query.tab === 'merchandise') activeTab.value = 'merchandise'
    if (route.query.type === 'BUNDLE') { merchForm.value.category = 'BUNDLE'; syncMerchCategory() }
    fetchProfile()
    fetchMyWorks()
    fetchNotes()
    fetchMyMerchandise()
    fetchMyLotteries()
    fetchMyQuestions()
  }
})
</script>
