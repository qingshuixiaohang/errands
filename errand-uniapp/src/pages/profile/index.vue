<template>
  <view class="page-viewport">
    <view class="status-bar"><text class="time">{{ time }}</text>
      <view class="status-icons">
        <svg width="16" height="12" viewBox="0 0 16 12" fill="none" stroke="currentColor" stroke-width="1.5">
          <rect x="1" y="1" width="14" height="10" rx="2"/><path d="M4 5V7M8 3V9M12 5V7"/>
        </svg>
      </view>
    </view>
    <view class="header"><h1>个人中心</h1></view>
    <view class="profile-header">
      <view class="avatar">{{ userName.charAt(0) }}</view>
      <view class="profile-info">
        <h2>{{ userName }}</h2>
        <view class="sub">2024****021</view>
      </view>
    </view>
    <view class="stats-row">
      <view class="item">
        <view class="num">{{ store.orders.length }}</view>
        <view class="label">订单数</view>
      </view>
      <view class="item">
        <view class="num">4.9</view>
        <view class="label">好评率</view>
      </view>
      <view class="item">
        <view class="num">{{ unreadCount }}</view>
        <view class="label">未读消息</view>
      </view>
    </view>
    <view class="menu-list">
      <view class="menu-item" @tap="goMessages">
        <svg viewBox="0 0 24 24">
          <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
          <polyline points="22,6 12,13 2,6"/>
        </svg>
        <text>消息通知</text>
        <view v-if="unreadCount > 0" style="margin-left:auto;background:var(--danger);color:white;font-size:11px;padding:2px 8px;border-radius:10px">{{ unreadCount }}</view>
      </view>
      <view class="menu-item" @tap="showDev">
        <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="3"/><path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/></svg>
        <text>设置</text>
      </view>
    </view>
    <view class="btn-switch" @tap="switchRole">切换身份</view>
    <AIChat />
    <!-- 顾客底部导航 -->
    <view class="bottom-nav">
      <view class="nav-item" @tap="switchTab('home')">
        <svg viewBox="0 0 24 24"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2V9z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
        <span>首页</span>
      </view>
      <view class="nav-item" @tap="switchTab('orders')">
        <svg viewBox="0 0 24 24"><path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="1"/><path d="M9 14l2 2 4-4"/></svg>
        <span>订单</span>
      </view>
      <view class="nav-item active" @tap="switchTab('profile')">
        <svg viewBox="0 0 24 24"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
        <span>我的</span>
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

const userName = computed(() => user.profile?.name || "林小明")
const unreadCount = computed(() => store.messages.filter(m => !m.read).length)

const goMessages = () => uni.navigateTo({ url: "/pages/messages/index" })
const showDev = () => uni.showToast({ title: "功能开发中", icon: "none" })
const switchRole = () => { user.clearProfile(); uni.reLaunch({ url: "/pages/login/index" }) }

function switchTab(tab) {
  const paths = { home: "/pages/index/index", orders: "/pages/orders/index", profile: "/pages/profile/index" }
  if (tab === "profile") return
  uni.reLaunch({ url: paths[tab] })
}

onShow(() => {
  const n = new Date()
  const h = n.getHours().toString().padStart(2, "0")
  const m = n.getMinutes().toString().padStart(2, "0")
  time.value = h + ":" + m
})
</script>
