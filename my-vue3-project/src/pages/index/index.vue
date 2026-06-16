<template>
  <view class="page-viewport">
    <view class="status-bar"><text class="time" id="statusTime">{{ time }}</text>
      <view class="status-icons">
        <svg width="16" height="12" viewBox="0 0 16 12" fill="none" stroke="currentColor" stroke-width="1.5">
          <rect x="1" y="1" width="14" height="10" rx="2"/><path d="M4 5V7M8 3V9M12 5V7"/>
        </svg>
      </view>
    </view>
    <view class="header"><h1>校园外跑</h1></view>
    <scroll-view scroll-y class="scroll" style="flex:1">
      <view class="search-bar">
        <svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
        <input type="text" v-model="searchText" placeholder="搜索商家或商品…" class="search-input" />
      </view>
      <view v-if="filteredShops.length === 0" style="text-align:center;padding:40px 20px;color:var(--muted)">
        <p>没有找到相关商家</p>
      </view>
      <view class="shop-card" v-for="s in filteredShops" :key="s.id" @tap="goShop(s)">
        <view class="thumb">{{ s.logo }}</view>
        <view class="info">
          <h3>{{ s.name }}</h3>
          <view class="tags"><span v-for="t in s.tags" :key="t">{{ t }}</span></view>
          <view class="meta">
            <text class="rating">★ {{ s.rating }}</text>
            <text>月售{{ s.sales }}</text>
            <text>配送{{ s.delivery }}</text>
            <text>起送¥{{ s.minOrder }}</text>
          </view>
        </view>
      </view>
      <view class="page-spacer"></view>
    </scroll-view>
    <view class="cart-bar" v-if="store.cartCount > 0" @tap="goCart">
      <view class="left">
        <view class="icon-badge">
          <svg viewBox="0 0 24 24">
            <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
            <path d="M1 1h4l2.68 13.39a2 2 0 002 1.61h9.72a2 2 0 002-1.61L23 6H6"/>
          </svg>
          <view class="badge">{{ store.cartCount }}</view>
        </view>
        <text>查看购物车</text>
      </view>
      <view class="total">¥{{ store.cartTotal.toFixed(1) }}</view>
    </view>
    <AIChat />
    <!-- 顾客底部导航 -->
    <view class="bottom-nav">
      <view class="nav-item active" @tap="switchTab('home')">
        <svg viewBox="0 0 24 24"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2V9z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
        <span>首页</span>
      </view>
      <view class="nav-item" @tap="switchTab('orders')">
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
import { ref, computed } from "vue"
import { onShow } from "@dcloudio/uni-app"
import { useCampusStore } from "@/stores/campus"
import { useUserStore } from "@/stores/user"
import AIChat from "@/components/AIChat.vue"

const store = useCampusStore()
const user = useUserStore()
const time = ref("")
const searchText = ref("")

// 根据搜索文本过滤商家
const filteredShops = computed(() => {
  const q = searchText.value.trim().toLowerCase()
  if (!q) return store.SHOPS
  return store.SHOPS.filter((s) => {
    // 搜索商家名称
    if (s.name.toLowerCase().includes(q)) return true
    // 搜索商家标签
    if (s.tags && s.tags.some((t) => t.toLowerCase().includes(q))) return true
    return false
  })
})

onShow(() => {
  if (!user.role) {
    uni.reLaunch({ url: "/pages/login/index" })
    return
  }
  const n = new Date()
  const h = n.getHours().toString().padStart(2, "0")
  const m = n.getMinutes().toString().padStart(2, "0")
  time.value = h + ":" + m
  // 从后端获取商家列表
  store.fetchShops()
})

const goShop = (s) => uni.navigateTo({ url: `/pages/shop/index?id=${s.id}` })
const goCart = () => uni.navigateTo({ url: "/pages/cart/index" })

function switchTab(tab) {
  if (tab === "home") return
  const paths = { orders: "/pages/orders/index", profile: "/pages/profile/index" }
  uni.reLaunch({ url: paths[tab] })
}
</script>

<style scoped>
.scroll { padding-bottom: 80px; }
.search-bar { display: flex; align-items: center; gap: 8px; margin: 12px 20px; padding: 8px 14px; background: #f5f5f5; border-radius: 20px; }
.search-bar svg { width: 18px; height: 18px; color: #999; flex-shrink: 0; }
.search-input { flex: 1; font-size: 14px; background: transparent; border: none; outline: none; height: 24px; }
</style>
