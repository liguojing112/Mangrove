<template>
  <div>
    <h2 class="text-lg font-semibold text-gray-900 mb-6">照片分类管理</h2>

    <!-- 添加分类 -->
    <div class="card p-6 mb-6">
      <h3 class="text-sm font-semibold text-gray-800 mb-4">添加新分类</h3>
      <div class="flex items-end gap-4">
        <div class="flex-1">
          <label class="block text-sm font-medium text-gray-700 mb-1">分类名称</label>
          <input v-model="newCatName" class="w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" placeholder="例如：微博日常" @keyup.enter="addCategory" />
        </div>
        <div class="w-24">
          <label class="block text-sm font-medium text-gray-700 mb-1">排序</label>
          <input v-model="newCatOrder" type="number" class="w-full rounded-xl border border-gray-200 px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" />
        </div>
        <button class="btn-primary text-sm h-[42px] px-5" @click="addCategory">添加</button>
      </div>
    </div>

    <!-- 分类列表 -->
    <div class="card overflow-hidden">
      <div class="px-5 py-4 border-b border-gray-100">
        <h3 class="text-sm font-semibold text-gray-800">已有分类（{{ categories.length }} 个，每行最多 5 个）</h3>
      </div>
      <div v-if="categories.length === 0" class="p-12 text-center text-sm text-gray-400">
        暂无分类，请添加
      </div>
      <table v-else class="w-full text-sm">
        <thead>
          <tr class="bg-gray-50">
            <th class="text-left text-xs font-medium text-gray-500 px-5 py-3">名称</th>
            <th class="text-left text-xs font-medium text-gray-500 px-5 py-3">排序</th>
            <th class="text-left text-xs font-medium text-gray-500 px-5 py-3">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="cat in categories" :key="cat.id" class="border-b border-gray-100">
            <td class="px-5 py-3">
              <input v-if="cat._editing" v-model="cat._name" class="rounded-lg border border-gray-200 px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-mangrove-500" />
              <span v-else class="text-gray-800">{{ cat.name }}</span>
            </td>
            <td class="px-5 py-3">
              <input v-if="cat._editing" v-model="cat._order" type="number" class="w-16 rounded-lg border border-gray-200 px-2 py-1.5 text-sm" />
              <span v-else class="text-gray-500">{{ cat.sortOrder }}</span>
            </td>
            <td class="px-5 py-3">
              <template v-if="cat._editing">
                <button class="text-green-600 hover:text-green-800 text-xs mr-2" @click="saveEdit(cat)">保存</button>
                <button class="text-gray-500 text-xs" @click="cancelEdit(cat)">取消</button>
              </template>
              <template v-else>
                <button class="text-sky-600 hover:text-sky-800 text-xs mr-2" @click="startEdit(cat)">编辑</button>
                <button class="text-red-500 hover:text-red-700 text-xs" @click="deleteCat(cat)">删除</button>
              </template>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const categories = ref([])
const newCatName = ref('')
const newCatOrder = ref(0)

function getToken() {
  return localStorage.getItem('mangrove_token')
}

async function fetchCats() {
  const res = await fetch('/api/photo-categories')
  const json = await res.json()
  if (json.code === 200) categories.value = json.data || []
}

async function addCategory() {
  if (!newCatName.value.trim()) return
  await fetch('/api/photo-categories', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${getToken()}` },
    body: JSON.stringify({ name: newCatName.value.trim(), sortOrder: Number(newCatOrder.value) || 0 })
  })
  newCatName.value = ''
  newCatOrder.value = 0
  fetchCats()
}

function startEdit(cat) {
  cat._editing = true
  cat._name = cat.name
  cat._order = cat.sortOrder
}

function cancelEdit(cat) {
  cat._editing = false
}

async function saveEdit(cat) {
  await fetch(`/api/photo-categories/${cat.id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${getToken()}` },
    body: JSON.stringify({ name: cat._name.trim(), sortOrder: Number(cat._order) || 0 })
  })
  cat._editing = false
  fetchCats()
}

async function deleteCat(cat) {
  if (!confirm(`确定删除分类「${cat.name}」吗？`)) return
  await fetch(`/api/photo-categories/${cat.id}`, {
    method: 'DELETE',
    headers: { 'Authorization': `Bearer ${getToken()}` }
  })
  fetchCats()
}

onMounted(fetchCats)
</script>
