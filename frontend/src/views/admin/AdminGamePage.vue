<template>
  <div class="min-w-0">
    <h2 class="text-lg font-semibold text-gray-900 mb-6">游戏管理</h2>

    <!-- Levels Section -->
    <div class="space-y-6">
      <!-- Create Level -->
      <div class="card p-6">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-md font-semibold text-gray-900">关卡列表</h3>
          <button class="btn-primary text-sm" @click="showCreateLevel = true">
            + 新建关卡
          </button>
        </div>

        <div v-if="levels.length === 0" class="text-center py-8 text-gray-400">
          暂无关卡，点击"新建关卡"开始创建
        </div>

        <div v-else class="space-y-4">
          <div v-for="level in levels" :key="level.id" class="border border-gray-200 rounded-xl p-4 hover:border-mangrove-300 transition-colors">
            <div class="flex items-start justify-between mb-4">
              <div class="flex-1">
                <div class="flex items-center gap-3 mb-2">
                  <span class="px-2 py-1 rounded-full text-xs bg-mangrove-100 text-mangrove-700">
                    Level {{ level.levelNumber }}
                  </span>
                  <h4 class="font-medium text-gray-900">{{ level.title }}</h4>
                </div>
                <p class="text-sm text-gray-500">{{ level.description }}</p>
              </div>
              <div class="flex items-center gap-2">
                <button
                  class="p-1.5 rounded-lg hover:bg-blue-50 text-gray-400 hover:text-blue-600 transition-colors"
                  title="编辑"
                  @click="editLevel(level)"
                >
                  <Edit2 class="w-4 h-4" />
                </button>
                <button
                  class="p-1.5 rounded-lg hover:bg-yellow-50 text-gray-400 hover:text-yellow-600 transition-colors"
                  :title="level.status === 1 ? '隐藏' : '显示'"
                  @click="toggleLevelStatus(level)"
                >
                  <Eye v-if="level.status === 1" class="w-4 h-4" />
                  <EyeOff v-else class="w-4 h-4" />
                </button>
                <button
                  class="p-1.5 rounded-lg hover:bg-green-50 text-gray-400 hover:text-green-600 transition-colors"
                  title="管理题目"
                  @click="showQuestions(level)"
                >
                  <List class="w-4 h-4" />
                </button>
                <button
                  class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-colors"
                  title="删除"
                  @click="deleteLevel(level)"
                >
                  <Trash2 class="w-4 h-4" />
                </button>
              </div>
            </div>

            <!-- Questions Count -->
            <div class="text-xs text-gray-400">
              已添加 {{ level.questionCount || 0 }} 道题目
            </div>
          </div>
        </div>
      </div>

      <!-- Questions Section (shown when level selected) -->
      <div v-if="selectedLevel" class="card p-6">
        <div class="flex items-center justify-between mb-4">
          <div class="flex items-center gap-3">
            <button class="p-1.5 rounded-lg hover:bg-gray-100" @click="selectedLevel = null">
              <ArrowLeft class="w-4 h-4" />
            </button>
            <h3 class="text-md font-semibold text-gray-900">
              Level {{ selectedLevel.levelNumber }} - {{ selectedLevel.title }} 的题目
            </h3>
          </div>
          <button class="btn-primary text-sm" @click="showAddQuestion = true">
            + 添加题目
          </button>
        </div>

        <div v-if="questions.length === 0" class="text-center py-8 text-gray-400">
          暂无题目，点击"添加题目"开始创建
        </div>

        <div v-else class="space-y-4">
          <div v-for="(question, idx) in questions" :key="question.id" class="border border-gray-200 rounded-xl p-4">
            <div class="flex items-start justify-between mb-3">
              <div class="flex-1">
                <div class="flex items-center gap-2 mb-2">
                  <span class="px-2 py-1 rounded-full text-xs bg-blue-100 text-blue-700">
                    题目 {{ idx + 1 }}
                  </span>
                  <p class="text-gray-900 font-medium">{{ question.question }}</p>
                </div>
                <div class="text-sm text-gray-500 space-y-1">
                  <p><span class="font-medium">A：</span>{{ question.optionA }}</p>
                  <p><span class="font-medium">B：</span>{{ question.optionB }}</p>
                  <p v-if="question.optionC"><span class="font-medium">C：</span>{{ question.optionC }}</p>
                  <p v-if="question.optionD"><span class="font-medium">D：</span>{{ question.optionD }}</p>
                  <p class="text-green-600"><span class="font-medium">正确答案：</span>{{ question.correctAnswer }}</p>
                  <p v-if="question.explanation"><span class="font-medium">解析：</span>{{ question.explanation }}</p>
                </div>
              </div>
              <div class="flex items-center gap-2">
                <button
                  class="p-1.5 rounded-lg hover:bg-blue-50 text-gray-400 hover:text-blue-600 transition-colors"
                  title="编辑"
                  @click="editQuestion(question)"
                >
                  <Edit2 class="w-4 h-4" />
                </button>
                <button
                  class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-colors"
                  title="删除"
                  @click="deleteQuestion(question)"
                >
                  <Trash2 class="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Create/Edit Level Modal -->
    <div v-if="showCreateLevel || editingLevel" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="closeLevelModal">
      <div class="card p-6 w-full max-w-md mx-4">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">{{ editingLevel ? '编辑关卡' : '新建关卡' }}</h3>
        <form @submit.prevent="saveLevel">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">关卡编号</label>
              <input v-model.number="levelForm.levelNumber" type="number" class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" required />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">标题</label>
              <input v-model="levelForm.title" type="text" class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" required />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">难度</label>
              <select v-model="levelForm.difficulty" class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" required>
                <option value="EASY">简单</option>
                <option value="MEDIUM">中等</option>
                <option value="HARD">困难</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">描述</label>
              <textarea v-model="levelForm.description" rows="3" class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 resize-none"></textarea>
            </div>
          </div>
          <div class="flex items-center gap-3 mt-6">
            <button type="submit" class="btn-primary text-sm" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
            <button type="button" class="btn-secondary text-sm" @click="closeLevelModal">取消</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Add/Edit Question Modal -->
    <div v-if="showAddQuestion || editingQuestion" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="closeQuestionModal">
      <div class="card p-6 w-full max-w-lg mx-4">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">{{ editingQuestion ? '编辑题目' : '添加题目' }}</h3>
        <form @submit.prevent="saveQuestion">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">题目内容</label>
              <textarea v-model="questionForm.question" rows="2" class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 resize-none" required></textarea>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">选项 A（必填）</label>
              <input v-model="questionForm.optionA" type="text" class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" required />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">选项 B（必填）</label>
              <input v-model="questionForm.optionB" type="text" class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" required />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">选项 C</label>
              <input v-model="questionForm.optionC" type="text" class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">选项 D</label>
              <input v-model="questionForm.optionD" type="text" class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">正确答案（填写选项字母，如：A）</label>
              <select v-model="questionForm.correctAnswer" class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" required>
                <option value="A">A</option>
                <option value="B">B</option>
                <option value="C">C</option>
                <option value="D">D</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">解析（可选）</label>
              <textarea v-model="questionForm.explanation" rows="2" class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500 resize-none"></textarea>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">排序权重</label>
              <input v-model.number="questionForm.sortOrder" type="number" class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" />
            </div>
          </div>
          <div class="flex items-center gap-3 mt-6">
            <button type="submit" class="btn-primary text-sm" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
            <button type="button" class="btn-secondary text-sm" @click="closeQuestionModal">取消</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Leaderboard Management Section -->
    <div class="card p-6 mt-6 max-w-full overflow-hidden">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-md font-semibold text-gray-900">排行榜管理</h3>
        <div class="flex items-center gap-2">
          <select
            v-model="scoreFilterDifficulty"
            class="rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500"
            @change="fetchScores"
          >
            <option value="">全部</option>
            <option value="EASY">简单</option>
            <option value="MEDIUM">中等</option>
            <option value="HARD">困难</option>
          </select>
        </div>
      </div>

      <div v-if="scores.length === 0" class="text-center py-8 text-gray-400">
        暂无分数记录
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full text-sm min-w-[580px]">
          <thead>
            <tr class="bg-gray-50">
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">#</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">用户</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">关卡</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">得分</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">答对/总数</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">难度</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">时间</th>
              <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-4 py-3">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(score, idx) in scores" :key="score.id" class="border-b border-gray-100 hover:bg-gray-50">
              <td class="px-4 py-3 text-gray-400">{{ idx + 1 }}</td>
              <td class="px-4 py-3">
                <span class="text-gray-900">{{ score.userNickname || '匿名' }}</span>
              </td>
              <td class="px-4 py-3 text-gray-600">关卡 {{ score.levelId }}</td>
              <td class="px-4 py-3">
                <span class="text-mangrove-600 font-bold">{{ score.score }}</span>
              </td>
              <td class="px-4 py-3 text-gray-600">{{ score.correctCount }}/{{ score.totalQuestions }}</td>
              <td class="px-4 py-3">
                <span
                  class="px-2 py-1 rounded-full text-xs"
                  :class="difficultyClass(score.difficulty)"
                >
                  {{ difficultyLabel(score.difficulty) }}
                </span>
              </td>
              <td class="px-4 py-3 text-gray-500 text-xs">{{ formatTime(score.createdAt) }}</td>
              <td class="px-4 py-3">
                <button
                  class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-colors"
                  title="删除"
                  @click="deleteScore(score)"
                >
                  <Trash2 class="w-4 h-4" />
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Edit2, Eye, EyeOff, Trash2, List, ArrowLeft } from 'lucide-vue-next'

const levels = ref([])
const selectedLevel = ref(null)
const questions = ref([])
const scores = ref([])
const scoreFilterDifficulty = ref('')
const showCreateLevel = ref(false)
const editingLevel = ref(null)
const showAddQuestion = ref(false)
const editingQuestion = ref(null)
const saving = ref(false)

const levelForm = ref({ levelNumber: 1, title: '', description: '', difficulty: 'EASY' })
const questionForm = ref({ question: '', optionA: '', optionB: '', optionC: '', optionD: '', correctAnswer: 'A', explanation: '', sortOrder: 0 })

function getToken() {
  return localStorage.getItem('mangrove_token')
}

async function fetchLevels() {
  try {
    const res = await fetch('/api/admin/game/levels', {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      levels.value = json.data
    }
  } catch (e) {
    console.error('获取关卡失败:', e)
  }
}

function editLevel(level) {
  editingLevel.value = level
  levelForm.value = { levelNumber: level.levelNumber, title: level.title, description: level.description || '', difficulty: level.difficulty || 'EASY' }
}

function closeLevelModal() {
  showCreateLevel.value = false
  editingLevel.value = null
  levelForm.value = { levelNumber: 1, title: '', description: '', difficulty: 'EASY' }
}

async function saveLevel() {
  saving.value = true
  try {
    const url = editingLevel.value ? `/api/admin/game/levels/${editingLevel.value.id}` : '/api/admin/game/levels'
    const method = editingLevel.value ? 'PUT' : 'POST'
    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${getToken()}` },
      body: JSON.stringify(levelForm.value)
    })
    const json = await res.json()
    if (json.code === 200) {
      closeLevelModal()
      await fetchLevels()
    }
  } catch (e) {
    console.error('保存失败:', e)
  } finally {
    saving.value = false
  }
}

async function toggleLevelStatus(level) {
  const newStatus = level.status === 1 ? 0 : 1
  try {
    const res = await fetch(`/api/admin/game/levels/${level.id}/status?status=${newStatus}`, {
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      level.status = newStatus
    }
  } catch (e) {
    console.error('更新状态失败:', e)
  }
}

async function deleteLevel(level) {
  if (!confirm('确认删除此关卡？')) return
  try {
    const res = await fetch(`/api/admin/game/levels/${level.id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      await fetchLevels()
    }
  } catch (e) {
    console.error('删除失败:', e)
  }
}

async function showQuestions(level) {
  selectedLevel.value = level
  await fetchQuestions(level.id)
}

async function fetchQuestions(levelId) {
  try {
    const res = await fetch(`/api/admin/game/levels/${levelId}/questions`, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      questions.value = json.data
    }
  } catch (e) {
    console.error('获取题目失败:', e)
  }
}

function editQuestion(question) {
  editingQuestion.value = question
  questionForm.value = {
    question: question.question,
    optionA: question.optionA,
    optionB: question.optionB,
    optionC: question.optionC || '',
    optionD: question.optionD || '',
    correctAnswer: question.correctAnswer,
    explanation: question.explanation || '',
    sortOrder: question.sortOrder || 0
  }
}

function closeQuestionModal() {
  showAddQuestion.value = false
  editingQuestion.value = null
  questionForm.value = { question: '', optionA: '', optionB: '', optionC: '', optionD: '', correctAnswer: 'A', explanation: '', sortOrder: 0 }
}

async function saveQuestion() {
  saving.value = true
  try {
    const url = editingQuestion.value ? `/api/admin/game/questions/${editingQuestion.value.id}` : '/api/admin/game/questions'
    const method = editingQuestion.value ? 'PUT' : 'POST'
    const body = editingQuestion.value
      ? questionForm.value
      : { ...questionForm.value, levelId: selectedLevel.value.id }
    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${getToken()}` },
      body: JSON.stringify(body)
    })
    const json = await res.json()
    if (json.code === 200) {
      closeQuestionModal()
      await fetchQuestions(selectedLevel.value.id)
    }
  } catch (e) {
    console.error('保存失败:', e)
  } finally {
    saving.value = false
  }
}

async function deleteQuestion(question) {
  if (!confirm('确认删除此题目？')) return
  try {
    const res = await fetch(`/api/admin/game/questions/${question.id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      await fetchQuestions(selectedLevel.value.id)
    }
  } catch (e) {
    console.error('删除失败:', e)
  }
}

onMounted(() => {
  fetchLevels()
  fetchScores()
})

async function fetchScores() {
  try {
    let url = '/api/admin/game/scores'
    if (scoreFilterDifficulty.value) {
      url += `/difficulty/${scoreFilterDifficulty.value}`
    }
    const res = await fetch(url, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      scores.value = json.data
    }
  } catch (e) {
    console.error('获取分数失败:', e)
  }
}

async function deleteScore(score) {
  if (!confirm('确认删除此记录？')) return
  try {
    const res = await fetch(`/api/admin/game/scores/${score.id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    })
    const json = await res.json()
    if (json.code === 200) {
      await fetchScores()
    }
  } catch (e) {
    console.error('删除失败:', e)
  }
}

function difficultyClass(diff) {
  switch (diff) {
    case 'EASY': return 'bg-green-100 text-green-700'
    case 'MEDIUM': return 'bg-yellow-100 text-yellow-700'
    case 'HARD': return 'bg-red-100 text-red-700'
    default: return 'bg-gray-100 text-gray-600'
  }
}

function difficultyLabel(diff) {
  switch (diff) {
    case 'EASY': return '简单'
    case 'MEDIUM': return '中等'
    case 'HARD': return '困难'
    default: return '未知'
  }
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}
</script>
