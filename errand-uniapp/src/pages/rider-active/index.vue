<template>
  <view class="page-viewport" style="padding-bottom:62px">
    <view class="status-bar"><text class="time">{{ time }}</text>
      <view class="status-icons">
        <svg width="16" height="12" viewBox="0 0 16 12" fill="none" stroke="currentColor" stroke-width="1.5">
          <rect x="1" y="1" width="14" height="10" rx="2"/><path d="M4 5V7M8 3V9M12 5V7"/>
        </svg>
      </view>
    </view>
    <view class="header"><h1>配送中</h1></view>

    <scroll-view scroll-y class="scroll" style="flex:1">
      <view v-if="deliveringOrders.length === 0" style="text-align:center;padding:60px 20px;color:var(--muted)">
        <p>没有进行中的配送</p>
      </view>

      <view class="rider-card" v-for="o in deliveringOrders" :key="o.id" style="margin-top:12px">
        <view class="order-id">#{{ o.id }} · {{ o.shopName }}</view>
        <view style="font-size:13px;margin-bottom:4px">📍 送至 {{ o.deliveryAddr }}</view>
        <view style="font-size:12px;color:var(--muted);margin-bottom:6px">{{ o.items.map(i => i.name + '×' + i.qty).join('、') }}</view>
        <view style="display:flex;justify-content:space-between;font-size:13px;margin-bottom:8px">
          <span>垫付：<strong>¥{{ o.total.toFixed(1) }}</strong></span>
          <span>配送费：<strong style="color:var(--success)">¥{{ (o.total * 0.2).toFixed(1) }}</strong></span>
        </view>
        <view class="rider-action">
          <view class="btn-solid" style="text-align:center;padding:12px" @tap="markDelivered(o.id)">已送达，通知用户</view>
        </view>
      </view>
    </scroll-view>

    <!-- 骑手底部导航 -->
    <view class="bottom-nav">
      <view class="nav-item" @tap="switchTab('rider-pending')">
        <svg viewBox="0 0 24 24"><circle cx="5" cy="18" r="2"/><circle cx="19" cy="18" r="2"/><path d="M12 18V12"/><path d="M11 12h2l3-4H9l4-4"/></svg>
        <span>待接单</span>
      </view>
      <view class="nav-item active" @tap="switchTab('rider-active')">
        <svg viewBox="0 0 24 24"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
        <span>配送中</span>
      </view>
      <view class="nav-item" @tap="switchTab('rider-profile')">
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

const store = useCampusStore()
const time = ref("")
const deliveringOrders = computed(() => store.orders.filter(o => o.status === "delivering"))

async function markDelivered(id) {
  const ok = await store.markDelivered(id)
  if (ok) uni.showToast({ title: "已通知用户确认收货", icon: "none" })
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
  store.fetchRiderOrders() // 骑手获取所有订单（不按用户过滤）
})
</script>
