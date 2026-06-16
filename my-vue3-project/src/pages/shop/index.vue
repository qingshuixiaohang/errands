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
      <h1>{{ shop?.name }}</h1>
      <view style="width:60px"></view>
    </view>
    <view style="display:flex;gap:20px;padding:10px 20px;font-size:12px;color:var(--muted);border-bottom:1px solid var(--border);background:var(--surface)">
      <span>★ {{ shop?.rating }}</span>
      <span>月售{{ shop?.sales }}</span>
      <span>配送{{ shop?.delivery }}</span>
    </view>
    <scroll-view scroll-y class="scroll" style="flex:1">
      <view style="padding:8px 0">
        <view class="food-item" v-for="m in menus" :key="m.id" :data-fid="m.id">
          <view class="info">
            <h4>{{ m.name }}</h4>
            <view class="desc" v-if="m.desc">{{ m.desc }}</view>
          </view>
          <view class="price">¥{{ m.price }}</view>
          <view class="qty-control">
            <button @tap.stop="dec(m.id)">−</button>
            <span>{{ qtys[m.id] || 0 }}</span>
            <button @tap.stop="inc(m.id)">+</button>
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
  </view>
</template>

<script setup>
import { ref, computed } from "vue"
import { onLoad, onShow } from "@dcloudio/uni-app"
import { useCampusStore } from "@/stores/campus"
import AIChat from "@/components/AIChat.vue"

const store = useCampusStore()
const id = ref(0)
const time = ref("")

const shop = computed(() => store.SHOPS.find(s => s.id === id.value))
const menus = computed(() => id.value ? (store.MENUS[id.value] || []) : [])

// 使用计算属性生成 qty 映射表，避免模板中每次渲染调用函数
const qtys = computed(() => {
  const map = {}
  if (!id.value) return map
  const sc = store.cart.find(c => c.shopId === id.value)
  if (sc) {
    sc.items.forEach(i => { map[i.foodId] = i.qty })
  }
  return map
})

const inc = (foodId) => { store.addToCart(id.value, foodId) }
const dec = (foodId) => { store.removeFromCart(id.value, foodId) }

const goBack = () => { try { uni.navigateBack() } catch(e) { uni.reLaunch({ url: "/pages/index/index" }) } }
const goCart = () => uni.navigateTo({ url: "/pages/cart/index" })

onLoad((o) => { if (o?.id) id.value = Number(o.id) })
onShow(() => {
  const n = new Date()
  const h = n.getHours().toString().padStart(2, "0")
  const m = n.getMinutes().toString().padStart(2, "0")
  time.value = h + ":" + m
  // 从后端获取菜单
  if (id.value) {
    store.fetchMenu(id.value)
  }
})
</script>

<style scoped>
.scroll { padding-bottom: 80px; }
</style>

