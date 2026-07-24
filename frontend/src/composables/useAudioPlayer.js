import { ref, computed, watch } from 'vue'

const currentSong = ref(null)
const isPlaying = ref(false)
const currentTime = ref(0)
const totalDuration = ref(0)
const volume = ref(0.8)
const isMuted = ref(false)
const queue = ref([])
const queueIndex = ref(-1)

let audioEl = null
let timeUpdateTimer = null

function getAudio() {
  if (!audioEl) {
    audioEl = new Audio()
    audioEl.loop = true
    audioEl.volume = volume.value
    audioEl.addEventListener('ended', onEnded)
    audioEl.addEventListener('loadedmetadata', onMetadata)
  }
  return audioEl
}

function onEnded() {
  playNext()
}

function onMetadata() {
  const a = getAudio()
  if (a && isFinite(a.duration)) totalDuration.value = a.duration
}

function startTimer() {
  stopTimer()
  timeUpdateTimer = setInterval(() => {
    if (!isSeeking && audioEl) currentTime.value = audioEl.currentTime
  }, 100)
}
function stopTimer() {
  if (timeUpdateTimer) { clearInterval(timeUpdateTimer); timeUpdateTimer = null }
}

const isSeeking = ref(false)
const seekPercent = ref(0)

const progressPercent = computed(() => {
  if (isSeeking.value) return seekPercent.value
  if (!totalDuration.value) return 0
  return (currentTime.value / totalDuration.value) * 100
})

export function useAudioPlayer() {
  const currentSongDisplay = computed(() => currentSong.value)
  const currentDurationDisplay = computed(() => {
    if (!currentSong.value) return '00:00'
    if (totalDuration.value) return formatTime(totalDuration.value)
    return currentSong.value.duration || '00:00'
  })

  function playSong(song) {
    if (!song?.url) return
    const a = getAudio()
    a.src = song.url
    a.play().then(() => {
      isPlaying.value = true
      startTimer()
      currentSong.value = song
    }).catch(() => {})
  }

  function togglePlay() {
    const a = getAudio()
    if (!a.src) return
    if (isPlaying.value) { a.pause(); isPlaying.value = false; stopTimer() }
    else { a.play().then(() => { isPlaying.value = true; startTimer() }).catch(() => {}) }
  }

  function playNext() {
    if (queue.value.length === 0) return
    queueIndex.value = (queueIndex.value + 1) % queue.value.length
    playSong(queue.value[queueIndex.value])
  }

  function playPrev() {
    if (queue.value.length === 0) return
    queueIndex.value = (queueIndex.value - 1 + queue.value.length) % queue.value.length
    playSong(queue.value[queueIndex.value])
  }

  function setQueue(songs) {
    queue.value = songs
    queueIndex.value = songs.findIndex(s => s.id === currentSong.value?.id)
  }

  function seek(percent) {
    const a = getAudio()
    if (!a || !totalDuration.value) return
    a.currentTime = (percent / 100) * totalDuration.value
    currentTime.value = a.currentTime
  }

  function setVolume(v) {
    volume.value = v
    const a = getAudio()
    if (a) a.volume = v
    isMuted.value = v === 0
  }

  function toggleMute() {
    if (isMuted.value) { setVolume(0.8); isMuted.value = false }
    else { setVolume(0); isMuted.value = true }
  }

  function preload(url) {
    if (!url) return
    const a = getAudio()
    a.src = url
    a.load()
  }

  return {
    currentSong: currentSongDisplay,
    currentDurationDisplay,
    isPlaying,
    currentTime,
    totalDuration,
    volume,
    isMuted,
    queue,
    queueIndex,
    isSeeking,
    seekPercent,
    progressPercent,
    playSong,
    togglePlay,
    playNext,
    playPrev,
    setQueue,
    seek,
    setVolume,
    toggleMute,
    preload,
    formatTime,
  }
}

function formatTime(sec) {
  if (!sec || !isFinite(sec)) return '00:00'
  const m = Math.floor(sec / 60)
  const s = Math.floor(sec % 60)
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}
