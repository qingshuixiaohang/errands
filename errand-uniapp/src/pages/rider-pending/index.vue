<template>
  <view class="page-viewport" style="padding-bottom:62px">
    <view class="status-bar"><text class="time">{{ time }}</text>
      <view class="status-icons">
        <svg width="16" height="12" viewBox="0 0 16 12" fill="none" stroke="currentColor" stroke-width="1.5">
          <rect x="1" y="1" width="14" height="10" rx="2"/><path d="M4 5V7M8 3V9M12 5V7"/>
        </svg>
      </view>
    </view>
    <view class="header"><h1>待接单</h1></view>

    <scroll-view scroll-y class="scroll" style="flex:1">
      <!-- 无订单 -->
      <view v-if="paidOrders.length === 0 && pendingPayOrders.length === 0 && aiAssigningOrders.length === 0" style="text-align:center;padding:60px 20px;color:var(--muted)">
        <p>暂无订单</p>
      </view>

      <!-- AI派单（在可接单上面） -->
      <template v-if="aiAssigningOrders.length > 0">
        <view style="padding:4px 20px;font-size:13px;font-weight:600;color:#6C4FA7;margin-top:4px">
          🤖 AI派单 · 等待您接受
        </view>
        <view class="rider-card" v-for="o in aiAssigningOrders" :key="o.id">
          <view class="order-id">#{{ o.id }} · {{ o.shopName }}</view>
          <view style="font-size:13px;margin-bottom:4px">📍 送至 {{ o.deliveryAddr }}</view>
          <view style="font-size:12px;color:var(--muted);margin-bottom:6px">
            {{ o.items.map(i => i.name + '×' + i.qty).join('、') }}
          </view>
          <view style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px">
            <span style="font-size:16px;font-weight:700;color:var(--accent)">¥{{ o.total.toFixed(1) }}</span>
          </view>
          <view class="rider-action" style="display:flex;gap:8px">
            <view class="btn-solid" style="flex:1;text-align:center;padding:12px;background:#4CAF50;color:#fff"
                  @tap="acceptAssignment(o.id)">接受派单</view>
            <view class="btn-warn" style="flex:1;text-align:center;padding:12px"
                  @tap="rejectAssignment(o.id)">拒绝</view>
          </view>
        </view>
      </template>

      <!-- 可接单 -->
      <template v-if="paidOrders.length > 0">
        <view style="padding:4px 20px;font-size:13px;font-weight:600;color:var(--muted);margin-top:4px">可接单</view>
        <view class="rider-card" v-for="o in paidOrders" :key="o.id" :data-oid="o.id">
          <view class="order-id">#{{ o.id }} · {{ o.shopName }}</view>
          <view style="font-size:13px;margin-bottom:4px">📍 送至 {{ o.deliveryAddr }}</view>
          <view style="font-size:12px;color:var(--muted);margin-bottom:6px">{{ o.items.map(i => i.name + '×' + i.qty).join('、') }}</view>
          <view style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px">
            <span style="font-size:16px;font-weight:700;color:var(--accent)">¥{{ o.total.toFixed(1) }}</span>
            <span style="font-size:12px;color:var(--warn)">需垫付¥{{ o.total.toFixed(1) }}</span>
          </view>
          <view class="rider-action">
            <view class="btn-warn" style="text-align:center;padding:12px" @tap="acceptOrder(o.id)">接单（需垫付）</view>
          </view>
        </view>
      </template>

      <!-- 已接单 · 待垫付 -->
      <template v-if="pendingPayOrders.length > 0">
        <view style="padding:4px 20px;font-size:13px;font-weight:600;color:var(--muted);margin-top:12px">已接单 · 待垫付</view>
        <view class="rider-card" v-for="o in pendingPayOrders" :key="o.id" :data-oid="o.id">
          <view class="order-id">#{{ o.id }} · {{ o.shopName }}</view>
          <view style="font-size:13px;margin-bottom:4px">📍 送至 {{ o.deliveryAddr }}</view>
          <view style="font-size:12px;color:var(--muted);margin-bottom:6px">{{ o.items.map(i => i.name + '×' + i.qty).join('、') }}</view>
          <view style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px">
            <span style="font-size:16px;font-weight:700;color:var(--accent)">需垫付¥{{ o.total.toFixed(1) }}</span>
          </view>
          <view class="rider-action">
            <view class="btn-solid" style="text-align:center;padding:12px" @tap="payAndDeliver(o.id)">我已垫付，开始配送</view>
          </view>
        </view>
      </template>
    </scroll-view>

    <!-- 骑手底部导航 -->
    <view class="bottom-nav">
      <view class="nav-item active" @tap="switchTab('rider-pending')">
        <svg viewBox="0 0 24 24"><circle cx="5" cy="18" r="2"/><circle cx="19" cy="18" r="2"/><path d="M12 18V12"/><path d="M11 12h2l3-4H9l4-4"/></svg>
        <span>待接单</span>
      </view>
      <view class="nav-item" @tap="switchTab('rider-active')">
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
import { useUserStore } from "@/stores/user"
import { acceptAssignmentAPI, rejectAssignmentAPI } from "@/api/order"

const store = useCampusStore()
const userStore = useUserStore()
const time = ref("")

const paidOrders = computed(() => store.orders.filter(o => o.status === "paid"))
const pendingPayOrders = computed(() => store.orders.filter(o => o.status === "pending_pay"))
const aiAssigningOrders = computed(() => store.orders.filter(o => o.status === "ai_assigning"))

async function acceptOrder(id) {
  const ok = await store.acceptOrder(id)
  if (ok) uni.showToast({ title: "已接单，请先垫付餐费", icon: "none" })
}

async function payAndDeliver(id) {
  const ok = await store.payAndDeliver(id)
  if (ok) uni.showToast({ title: "已垫付，开始配送", icon: "none" })
}

async function acceptAssignment(id) {
  const riderId = userStore.profile?.id
  if (!riderId) return uni.showToast({ title: "无法获取骑手信息", icon: "none" })
  try {
    await acceptAssignmentAPI(id, riderId)
    uni.showToast({ title: "已接单，请尽快配送", icon: "none" })
    store.fetchRiderOrders()
  } catch(e) {
    uni.showToast({ title: "接单失败", icon: "none" })
  }
}

async function rejectAssignment(id) {
  try {
    await rejectAssignmentAPI(id)
    uni.showToast({ title: "已拒绝派单", icon: "none" })
    store.fetchRiderOrders()
  } catch(e) {
    uni.showToast({ title: "操作失败", icon: "none" })
  }
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
