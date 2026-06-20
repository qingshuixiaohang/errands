<template>
  <view>
    <!-- AI 浮动按钮 -->
    <view
      ref="btnRef"
      class="ai-float-btn"
      :class="{ dragging: isDragging }"
      :style="{ transform: initTransform }"
      @touchstart="onTouchStart"
      @touchmove.prevent="onTouchMove"
      @touchend="onTouchEnd"
      v-if="showAI"
    >
      <svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
        <path d="M8 10h.01M12 10h.01M16 10h.01M9 16h6"/>
      </svg>
    </view>

    <!-- AI 面板 -->
    <view class="ai-panel" :class="{ open }">
      <view class="ai-header">
        <view class="ai-avatar">
          <svg viewBox="0 0 24 24" width="18" height="18">
            <circle cx="12" cy="12" r="10"/><path d="M8 14s1.5 2 4 2 4-2 4-2"/>
            <line x1="9" y1="9" x2="9.01" y2="9"/><line x1="15" y1="9" x2="15.01" y2="9"/>
          </svg>
        </view>
        <text class="ai-title">抱抱速达 · AI助手</text>
        <view class="close-btn" @tap="open = false">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
            <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
          </svg>
        </view>
      </view>
      <scroll-view class="msg-area" scroll-y :scroll-top="scrollTop">
        <view class="msg" :class="m.role" v-for="(m, i) in messages" :key="i">
          <view class="bubble">{{ m.text }}</view>
        </view>
        <view v-if="loading" class="msg ai">
          <view class="bubble thinking">AI 思考中...</view>
        </view>
      </scroll-view>
      <view class="input-area">
        <input type="text" v-model="inputText" placeholder="输入问题…" maxlength="120" :disabled="loading" @confirm="send" />
        <view class="send-btn" :class="{ disabled: loading }" @tap="send">{{ loading ? '...' : '发送' }}</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, nextTick } from "vue"
import { useUserStore } from "@/stores/user"

defineProps({ showAI: { type: Boolean, default: true } })

const open = ref(false)
const inputText = ref("")
const scrollTop = ref(0)
const messages = ref([])
const loading = ref(false)

// ===== 拖拽逻辑 =====
const btnRef = ref(null)
const isDragging = ref(false)
const hasMoved = ref(false)
const initTransform = ref("translate(310px, 550px)")
let posX = 310, posY = 550, startX = 0, startY = 0

function initPos() {
  try {
    const sys = uni.getSystemInfoSync()
    posX = sys.windowWidth - 74
    posY = sys.windowHeight - 150
  } catch (e) {}
  initTransform.value = "translate(" + posX + "px, " + posY + "px)"
}
function applyTransform(x, y) {
  if (btnRef.value) btnRef.value.style.transform = "translate(" + x + "px, " + y + "px)"
}
function syncTransform(x, y) {
  initTransform.value = "translate(" + x + "px, " + y + "px)"
}
initPos()

function onTouchStart(e) {
  const touch = e.touches[0]
  startX = touch.clientX; startY = touch.clientY
  isDragging.value = true; hasMoved.value = false
}
function onTouchMove(e) {
  if (!isDragging.value) return
  const touch = e.touches[0]
  const dx = touch.clientX - startX, dy = touch.clientY - startY
  if (Math.abs(dx) > 5 || Math.abs(dy) > 5) hasMoved.value = true
  applyTransform(posX + dx, posY + dy)
}
function onTouchEnd(e) {
  if (!isDragging.value) return
  isDragging.value = false
  const touch = e.changedTouches[0]
  posX = posX + (touch.clientX - startX)
  posY = posY + (touch.clientY - startY)
  try {
    const sys = uni.getSystemInfoSync()
    posX = Math.max(0, Math.min(posX, sys.windowWidth - 54))
    posY = Math.max(0, Math.min(posY, sys.windowHeight - 54))
  } catch (e) {}
  applyTransform(posX, posY)
  syncTransform(posX, posY)
  if (!hasMoved.value) toggle()
}

// ===== AI 对话（流式，使用 wx.request enableChunked） =====
function send() {
  const text = inputText.value.trim()
  if (!text || loading.value) return
  inputText.value = ""
  messages.value.push({ role: "user", text })

  // 添加一个空的 AI 消息占位，后续逐步填充
  const aiMsg = { role: "ai", text: "" }
  messages.value.push(aiMsg)
  loading.value = true
  scrollToBottom()

  // 获取 userId
  let userId = "guest"
  try {
    const userStore = useUserStore()
    if (userStore.profile?.id) userId = String(userStore.profile.id)
  } catch (e) {}

  const baseUrl = "http://10.16.195.130:8082"
  const url = baseUrl + "/chatAi/stream?userId=" + encodeURIComponent(userId) + "&message=" + encodeURIComponent(text)

  // 使用 wx.request 的 enableChunked 实现流式接收
  const task = wx.request({
    url,
    method: "GET",
    enableChunked: true,  // 开启分块接收
    timeout: 60000,
    success(res) {
      // 最终完成，处理可能剩余的缓冲区数据
      loading.value = false
      if (!aiMsg.text.trim()) {
        aiMsg.text = res.data || "抱歉，AI 暂无回复。"
      }
      scrollToBottom()
    },
    fail(err) {
      loading.value = false
      console.log("AI 请求失败", err)
      if (!aiMsg.text.trim()) {
        aiMsg.text = "抱歉，AI 服务暂时不可用（" + (err.errMsg || "网络超时") + "）"
      }
      scrollToBottom()
    }
  })

  // 监听流式数据块
  task.onChunkReceived((chunkRes) => {
    // chunkRes.data 是 ArrayBuffer，需要转为字符串
    const buffer = chunkRes.data
    const uint8 = new Uint8Array(buffer)
    const chunk = new TextDecoder("utf-8").decode(uint8)
    // 解析 SSE data: 前缀的行
    const lines = chunk.split("\n")
    for (const line of lines) {
      if (line.startsWith("data:")) {
        aiMsg.text += line.substring(5)
      }
    }
    // 强制触发响应式更新
    messages.value = [...messages.value]
    scrollToBottom()
  })
}

function scrollToBottom() {
  nextTick(() => { scrollTop.value = 99999 })
}

function toggle() {
  open.value = !open.value
  if (open.value && messages.value.length === 0) {
    messages.value.push({ role: "ai", text: "你好👋 我是抱抱速达的AI助手，有什么可以帮你的？\n\n例如：垫付流程、配送说明、支付方式、骑手接单……" })
  }
}
</script>

<style scoped>
.ai-float-btn {
  position: fixed; left: 0; top: 0;
  width: 54px; height: 54px; border-radius: 50%;
  background: #2F6FEB; color: white;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; z-index: 100;
  box-shadow: 0 4px 12px rgba(47,111,235,0.4); border: none;
}
.ai-float-btn:active { opacity: 0.7; }
.ai-float-btn.dragging { opacity: 1 !important; will-change: transform; }
.ai-panel {
  position: fixed; right: 16px; bottom: 100px;
  width: 340px; max-width: calc(100vw - 32px);
  height: 460px; max-height: calc(100vh - 160px);
  background: white; border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.15);
  display: flex; flex-direction: column; overflow: hidden;
  opacity: 0; pointer-events: none; transform: translateY(12px) scale(.97);
  transition: all .25s cubic-bezier(.16,1,.3,1); z-index: 99;
}
.ai-panel.open { opacity: 1; pointer-events: auto; transform: translateY(0) scale(1); }
.ai-header {
  display: flex; align-items: center; gap: 10px;
  padding: 16px 20px; border-bottom: 1px solid #f0f0f0;
}
.ai-avatar {
  width: 32px; height: 32px; border-radius: 50%;
  background: #2F6FEB; color: white;
  display: flex; align-items: center; justify-content: center;
}
.ai-title { font-size: 15px; font-weight: 600; flex: 1; }
.close-btn {
  width: 28px; height: 28px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  color: #999; cursor: pointer;
}
.close-btn:active { background: #f0f0f0; }
.msg-area { flex: 1; overflow-y: auto; padding: 12px 16px; }
.msg { margin-bottom: 12px; display: flex; }
.msg.user { justify-content: flex-end; }
.msg.ai { justify-content: flex-start; }
.bubble {
  max-width: 80%; padding: 10px 14px;
  border-radius: 12px; font-size: 14px; line-height: 1.5;
  white-space: pre-wrap; word-break: break-word;
}
.msg.user .bubble { background: #2F6FEB; color: white; border-bottom-right-radius: 4px; }
.msg.ai .bubble { background: #f5f5f5; color: #333; border-bottom-left-radius: 4px; }
.bubble.thinking { color: #999; font-style: italic; }
.input-area {
  display: flex; gap: 8px; padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
}
.input-area input {
  flex: 1; height: 40px; border-radius: 20px; border: 1px solid #ddd;
  padding: 0 16px; font-size: 14px; outline: none;
}
.send-btn {
  height: 40px; padding: 0 20px; border-radius: 20px;
  background: #2F6FEB; color: white; font-size: 14px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
}
.send-btn:active { opacity: 0.7; }
.send-btn.disabled { background: #ccc; }
</style>
