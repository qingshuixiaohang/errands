<template>
  <view class="page-viewport">
    <view class="status-bar"><text class="time">{{ time }}</text>
      <view class="status-icons">
        <svg width="16" height="12" viewBox="0 0 16 12" fill="none" stroke="currentColor" stroke-width="1.5">
          <rect x="1" y="1" width="14" height="10" rx="2"/><path d="M4 5V7M8 3V9M12 5V7"/>
        </svg>
      </view>
    </view>
    <view class="header"><h1>我的订单</h1></view>
    <scroll-view scroll-y class="scroll" style="flex:1">
      <view v-if="store.orders.length === 0" style="text-align:center;padding:60px 20px;color:var(--muted)">
        <p>暂无订单</p>
      </view>
      <view class="order-card" v-for="o in store.orders" :key="o.id" style="margin-top:12px">
        <view class="top" :class="statusClass(o.status)">
          <span>{{ o.shopName }}</span>
          <span>{{ statusText(o.status) }}</span>
        </view>
        <view class="content">
          <view v-if="o.rider" style="font-size:12px;color:var(--muted);margin-bottom:4px">骑手：{{ o.rider }}</view>
          <view class="items">{{ o.items.map(i => i.name + ' x' + i.qty).join('、') }}</view>
          <view class="amount">¥{{ o.total.toFixed(1) }}</view>
        </view>
        <view class="actions" style="display:flex;gap:8px;justify-content:flex-end;">
          <view v-if="o.status === 'delivered'" class="btn-success" style="text-align:center;padding:10px 16px" @tap="confirmOrder(o.id)">确认收货</view>
          <view v-if="o.status === 'completed' || o.status === 'delivered'" class="btn-del" style="text-align:center;padding:10px 16px" @tap="store.hideOrder(o.id)">删除</view>
        </view>
      </view>
      <view class="page-spacer"></view>
    </scroll-view>
    <AIChat />
    <!-- 顾客底部导航 -->
    <view class="bottom-nav">
      <view class="nav-item" @tap="switchTab('home')">
        <svg viewBox="0 0 24 24"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2V9z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
        <span>首页</span>
      </view>
      <view class="nav-item active" @tap="switchTab('orders')">
        <svg viewBox="0 0 24 24"><path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="1"/><path d="M9 14l2 2 4-4"/></svg>
        <span>订单</span>
      </view>
      <view class="nav-item" @tap="switchTab('profile')">
        <svg viewBox="0 0 24 24"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
        <span>我的</span>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from "vue"
import { onShow } from "@dcloudio/uni-app"
import { useCampusStore } from "@/stores/campus"
import AIChat from "@/components/AIChat.vue"

const store = useCampusStore()
const time = ref("")

function deleteOrder(orderId) {
  store.hideOrder(orderId)
}

function confirmOrder(orderId) {
  store.confirmOrder(orderId)
}

function statusText(status) {
  const map = {
    paid: "待接单", pending_pay: "骑手已接单（待垫付）",
    delivering: "配送中", delivered: "已送达，待确认", completed: "已完成",
  }
  return map[status] || status
}
function statusClass(status) {
  const map = {
    paid: "status-paid", pending_pay: "status-paid",
    delivering: "status-delivering", delivered: "status-delivered", completed: "status-completed",
  }
  return map[status] || ""
}
function switchTab(tab) {
  const paths = { home: "/pages/index/index", orders: "/pages/orders/index", profile: "/pages/profile/index" }
  if (tab === "orders") return
  uni.reLaunch({ url: paths[tab] })
}

onShow(() => {
  const n = new Date()
  const h = n.getHours().toString().padStart(2, "0")
  const m = n.getMinutes().toString().padStart(2, "0")
  time.value = h + ":" + m
  // 从后端获取订单列表
  store.fetchOrders()
})
</script>

<style scoped>
.scroll { padding-top: 4px; }
.btn-del {
  font-size: 12px; color: #999; padding: 10px 16px;
  border-radius: 6px; border: 1px solid #ddd;
  background: transparent; text-align: center;
}
.btn-del:active { opacity: 0.5; }
</style>
