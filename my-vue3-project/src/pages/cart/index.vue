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
      <h1>购物车</h1>
      <view style="width:60px"></view>
    </view>

    <scroll-view scroll-y class="scroll" style="flex:1">
      <!-- 空购物车 -->
      <view v-if="store.cart.length === 0" style="text-align:center;padding:60px 20px;color:var(--muted)">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--border)" stroke-width="1.5">
          <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
          <path d="M1 1h4l2.68 13.39a2 2 0 002 1.61h9.72a2 2 0 002-1.61L23 6H6"/>
        </svg>
        <p style="margin-top:12px">购物车空空如也</p>
      </view>

      <!-- 按商家分组 -->
      <template v-for="c in store.cart" :key="c.shopId">
        <view style="padding:4px 20px;font-weight:600;font-size:14px;margin-top:8px">{{ c.shopName }}</view>
        <view class="cart-item" v-for="i in c.items" :key="i.foodId">
          <view class="info"><h4>{{ i.name }}</h4></view>
          <view class="price">¥{{ (i.price * i.qty).toFixed(1) }}</view>
          <view class="qty-control">
            <button @tap="remove(c.shopId, i.foodId)">−</button>
            <span>{{ i.qty }}</span>
            <button @tap="add(c.shopId, i.foodId)">+</button>
          </view>
        </view>
      </template>

      <view style="padding:16px 20px" v-if="store.cart.length > 0">
        <view style="display:flex;justify-content:space-between;font-size:16px;font-weight:700;margin-bottom:12px">
          <span>总计</span>
          <span style="color:var(--accent)">¥{{ store.cartTotal.toFixed(1) }}</span>
        </view>
        <view class="btn-solid" style="width:100%;padding:14px;font-size:16px;text-align:center" @tap="goCheckout">去结算</view>
      </view>
      <view class="page-spacer"></view>
    </scroll-view>
    <AIChat />
  </view>
</template>

<script setup>
import { ref } from "vue"
import { onShow } from "@dcloudio/uni-app"
import { useCampusStore } from "@/stores/campus"
import AIChat from "@/components/AIChat.vue"

const store = useCampusStore()
const time = ref("")

const add = (sid, fid) => { store.addToCart(sid, fid) }
const remove = (sid, fid) => { store.removeFromCart(sid, fid) }
const goBack = () => { try { uni.navigateBack() } catch(e) { uni.reLaunch({ url: "/pages/index/index" }) } }
const goCheckout = () => uni.navigateTo({ url: "/pages/checkout/index" })

onShow(() => {
  const n = new Date()
  const h = n.getHours().toString().padStart(2, "0")
  const m = n.getMinutes().toString().padStart(2, "0")
  time.value = h + ":" + m
})
</script>

<style scoped>
.scroll { padding-bottom: 100px; }
</style>

