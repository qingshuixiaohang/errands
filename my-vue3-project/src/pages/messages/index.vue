<template>
  <view class="page-viewport">
    <view class="status-bar"><text class="time">{{ time }}</text>
      <view class="status-icons">
        <svg width="16" height="12" viewBox="0 0 16 12" fill="none" stroke="currentColor" stroke-width="1.5">
          <rect x="1" y="1" width="14" height="10" rx="2"/><path d="M4 5V7M8 3V9M12 5V7"/>
        </svg>
      </view>
    </view>
    <view class="header">
      <view class="back-btn" @tap="goBack">
        <svg viewBox="0 0 24 24"><polyline points="15 18 9 12 15 6"/></svg>
        返回
      </view>
      <h1>消息通知</h1>
      <view style="width:60px"></view>
    </view>

    <scroll-view scroll-y class="scroll" style="flex:1">
      <view v-if="store.messages.length === 0" style="text-align:center;padding:60px 20px;color:var(--muted)">
        <p>暂无消息</p>
      </view>

      <view class="msg-list">
        <view class="msg-item" v-for="m in store.messages" :key="m.id" @tap="markRead(m)">
          <view class="dot" :class="{ read: m.read }"></view>
          <view class="content">
            <view class="title">{{ m.title }}</view>
            <view class="body">{{ m.body }}</view>
            <view class="time">{{ m.time }}</view>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref } from "vue"
import { onShow } from "@dcloudio/uni-app"
import { useCampusStore } from "@/stores/campus"

const store = useCampusStore()
const time = ref("")

const goBack = () => { try { uni.navigateBack() } catch(e) { uni.reLaunch({ url: "/pages/profile/index" }) } }

function markRead(msg) {
  if (!msg.read) {
    msg.read = true
  }
}

onShow(() => {
  const n = new Date()
  const h = n.getHours().toString().padStart(2, "0")
  const m = n.getMinutes().toString().padStart(2, "0")
  time.value = h + ":" + m
})
</script>
