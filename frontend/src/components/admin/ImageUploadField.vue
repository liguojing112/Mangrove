<template>
  <div>
    <span class="mb-1 block text-sm text-gray-600">{{ label }}</span>
    <div class="overflow-hidden rounded-lg border border-gray-200 bg-gray-50" :class="wide ? 'aspect-video' : 'aspect-[2/3]'">
      <img v-if="url" :src="url" :alt="label" class="h-full w-full object-contain" />
      <div v-else class="flex h-full items-center justify-center text-sm text-gray-300">暂无图片</div>
    </div>
    <div class="mt-2 flex items-center gap-2">
      <label class="inline-flex items-center gap-2 px-4 py-2 bg-gray-200 hover:bg-gray-300 rounded-lg text-sm cursor-pointer transition-colors">
        <Upload class="h-4 w-4" />{{ uploading ? '上传中...' : '点击上传图片' }}
        <input type="file" accept="image/*" class="hidden" :disabled="uploading" @change="$emit('upload', $event)" />
      </label>
      <button v-if="url" type="button" class="px-2 py-2 text-xs text-gray-400 hover:text-red-500" @click="$emit('clear')">移除</button>
    </div>
  </div>
</template>

<script setup>
import { Upload } from 'lucide-vue-next'

defineProps({
  label: { type: String, required: true },
  url: { type: String, default: '' },
  uploading: { type: Boolean, default: false },
  wide: { type: Boolean, default: false }
})
defineEmits(['upload', 'clear'])
</script>
