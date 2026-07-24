<template>
  <div
    class="flex items-center justify-center rounded-full border text-white font-bold shrink-0"
    :class="sizeClass"
    :style="avatarStyle(publicId)"
  >
    {{ avatarText(username) }}
  </div>
</template>

<script setup>
const props = defineProps({
  publicId: { type: String, default: '' },
  username: { type: String, default: '' },
  size: { type: String, default: 'md' }
})

const sizeClass = {
  xs: 'w-6 h-6 text-[9px]',
  sm: 'w-8 h-8 text-[10px]',
  md: 'w-10 h-10 text-xs',
  lg: 'w-14 h-14 text-xl',
  xl: 'w-20 h-20 text-2xl'
}[props.size] || 'w-10 h-10 text-xs'

function avatarText(name) {
  return (name || 'U').slice(0, 2).toUpperCase()
}

function avatarStyle(publicId) {
  let hash = 0
  for (const char of (publicId || '0')) hash = ((hash << 5) - hash + char.charCodeAt(0)) | 0
  const hue = Math.abs(hash) % 360
  return {
    color: `hsl(${hue} 78% 72%)`,
    backgroundColor: `hsl(${hue} 55% 18%)`,
    borderColor: `hsl(${hue} 48% 38%)`
  }
}
</script>
