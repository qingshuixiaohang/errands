<template>
  <view class="page-viewport" style="padding-bottom:62px">
    <view class="status-bar"><text class="time">{{ time }}</text>
      <view class="status-icons">
        <svg width="16" height="12" viewBox="0 0 16 12" fill="none" stroke="currentColor" stroke-width="1.5">
          <rect x="1" y="1" width="14" height="10" rx="2"/><path d="M4 5V7M8 3V9M12 5V7"/>
        </svg>
      </view>
    </view>
    <view class="header"><h1>骑手中心</h1></view>

    <view class="profile-header">
      <view class="avatar" style="background:#FFF7ED;color:#C2410C">{{ riderName.charAt(0) }}</view>
      <view class="profile-info">
        <h2>{{ riderName }}</h2>
        <view class="sub">骑手 · 已接{{ completedCount }}单</view>
      </view>
    </view>

    <view class="rider-stats">
      <view class="item">
        <view class="num">¥{{ store.riderEarnings.toFixed(1) }}</view>
        <view class="label">已结算配送费</view>
      </view>
      <view class="item">
        <view class="num warn">¥{{ store.riderAdvancedTotal.toFixed(1) }}</view>
        <view class="label">当前垫付中</view>
      </view>
      <view class="item">
        <view class="num">{{ completedCount }}</view>
        <view class="label">完成订单</view>
      </view>
    </view>

    <view class="menu-list">
      <view class="menu-item" @tap="showDev">
        <svg viewBox="0 0 24 24"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
        <text>我的钱包（提现）</text>
      </view>
      <view class="menu-item" @tap="showDev">
        <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="3"/><path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/></svg>
        <text>设置</text>
      </view>
    </view>

    <view class="btn-switch" @tap="switchRole">切换身份</view>

    <AIChat />
  </view>

  <!-- 骑手底部导航 -->
  <view class="bottom-nav">
    <view class="nav-item" @tap="switchTab('rider-pending')">
      <svg viewBox="0 0 24 24"><circle cx="5" cy="18" r="2"/><circle cx="19" cy="18" r="2"/><path d="M12 18V12"/><path d="M11 12h2l3-4H9l4-4"/></svg>
      <span>待接单</span>
    </view>
    <view class="nav-item" @tap="switchTab('rider-active')">
      <svg viewBox="0 0 24 24"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
      <span>配送中</span>
    </view>
    <view class="nav-item active" @tap="switchTab('rider-profile')">
      <svg viewBox="0 0 24 24"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
      <span>我的</span>
          <view class="menu-item" @tap="goSchedule">
        <view class="menu-left">
          <svg width="20" height="20" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg>
          <text class="menu-text">空闲时间管理</text>
        </view>
        <text class="menu-arrow">›</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from "vue"
import { onShow } from "@dcloudio/uni-app"
import { useCampusStore } from "@/stores/campus"
import { useUserStore } from "@/stores/user"
import AIChat from "@/components/AIChat.vue"

const store = useCampusStore()
const user = useUserStore()
const time = ref("")

const riderName = computed(() => user.profile?.name || "李明")
const completedCount = computed(() => store.orders.filter(o => o.status === "completed").length)

const showDev = () => uni.showToast({ title: "功能开发中", icon: "none" })
const switchRole = () => {
  user.clearProfile() // 清空 profile + role
  uni.reLaunch({ url: "/pages/login/index" })
}

function switchTab(tab) {
  const paths = {
    "rider-pending": "/pages/rider-pending/index",
    "rider-active": "/pages/rider-active/index",
    "rider-profile": "/pages/rider-profile/index",
  }
  uni.redirectTo({ url: paths[tab] })
}

onShow(() => {
  const n = new Date()
  const h = n.getHours().toString().padStart(2, "0")
  const m = n.getMinutes().toString().padStart(2, "0")
  time.value = h + ":" + m
})
function goSchedule() {
  uni.navigateTo({ url: '/pages/rider-schedule/index' })
}
</script>

