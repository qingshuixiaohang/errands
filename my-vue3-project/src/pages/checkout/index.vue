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
        返回购物车
      </view>
      <h1>确认订单</h1>
      <view style="width:60px"></view>
    </view>

    <scroll-view scroll-y class="scroll" style="flex:1">
      <view class="checkout-section">
        <!-- ===== 地址区域 ===== -->
        <view class="address-section" @tap="goToAddress">
          <view class="addr-icon">📍</view>
          <view class="addr-info" v-if="selectedAddr">
            <view class="addr-name">
              <text class="consignee">{{ selectedAddr.consignee }}</text>
              <text class="phone">{{ selectedAddr.phone }}</text>
              <text class="default-tag" v-if="selectedAddr.isDefault">默认</text>
            </view>
            <text class="addr-detail">{{ selectedAddr.provinceName }}{{ selectedAddr.cityName }}{{ selectedAddr.districtName }}{{ selectedAddr.detail }}</text>
          </view>
          <view class="addr-info addr-empty" v-else>
            <text class="empty-tip">请添加收货地址</text>
          </view>
          <view class="addr-arrow">›</view>
        </view>

        <h3>订单商品</h3>
        <view v-for="c in store.cart" :key="c.shopId" style="background:var(--surface);border-radius:8px;padding:12px;margin-bottom:8px;border:1px solid var(--border)">
          <view style="font-weight:600;font-size:14px;margin-bottom:6px">{{ c.shopName }}</view>
          <view v-for="i in c.items" :key="i.foodId" style="display:flex;justify-content:space-between;font-size:13px;color:var(--muted);padding:2px 0">
            <span>{{ i.name }} × {{ i.qty }}</span>
            <span>¥{{ (i.price * i.qty).toFixed(1) }}</span>
          </view>
        </view>

        <view class="order-summary" style="margin-top:12px">
          <view class="row"><span>商品小计</span><span>¥{{ store.cartTotal.toFixed(1) }}</span></view>
          <view class="row"><span>配送费</span><span>¥0</span></view>
          <view class="row total"><span>合计</span><span>¥{{ store.cartTotal.toFixed(1) }}</span></view>
        </view>

        <view class="pay-methods">
          <h3>支付方式</h3>
          <label>
            <input type="radio" name="pay" value="wechat" checked />
            <span>微信支付</span>
          </label>
          <label>
            <input type="radio" name="pay" value="alipay" />
            <span>支付宝</span>
          </label>
          <label>
            <input type="radio" name="pay" value="campus" />
            <span>校园卡余额</span>
          </label>
        </view>

        <view class="btn-solid" :class="{ 'btn-disabled': !selectedAddr }" style="width:100%;padding:14px;font-size:16px;margin-top:20px;text-align:center" @tap="pay">
          {{ selectedAddr ? '确认支付 ¥' + store.cartTotal.toFixed(1) : '请先添加收货地址' }}
        </view>

        <view style="margin-top:12px;padding:12px;background:#FFF7ED;border-radius:8px;font-size:12px;color:#C2410C">
          💡 支付后，由骑手先垫付给商家，送达并确认后资金到达骑手账户
        </view>
      </view>
    </scroll-view>
    <AIChat />
  </view>
</template>

<script setup>
import { ref } from "vue"
import { onShow } from "@dcloudio/uni-app"
import { useCampusStore } from "@/stores/campus"
import { getDefaultAddressAPI, getAddressListAPI } from "@/api/address"
import AIChat from "@/components/AIChat.vue"

const store = useCampusStore()
const time = ref("")
const submitting = ref(false)
const selectedAddr = ref(null)

const goBack = () => { try { uni.navigateBack() } catch(e) { uni.reLaunch({ url: "/pages/index/index" }) } }
const goToAddress = () => { uni.navigateTo({ url: "/pages/address/index" }) }

async function pay() {
  if (store.cart.length === 0 || submitting.value) return
  if (!selectedAddr.value) {
    uni.showToast({ title: "请先添加收货地址", icon: "none" })
    return
  }
  submitting.value = true
  try {
    const result = await store.submitOrder()
    if (result) {
      uni.showToast({ title: "支付成功！等待骑手接单", icon: "none" })
      setTimeout(() => {
        uni.reLaunch({ url: "/pages/orders/index" })
      }, 500)
    } else {
      uni.showToast({ title: "提交订单失败", icon: "none" })
    }
  } catch (e) {
    uni.showToast({ title: "提交订单失败", icon: "none" })
  } finally {
    submitting.value = false
  }
}

// 获取用户地址
async function loadAddress() {
  try {
    const defaultRes = await getDefaultAddressAPI()
    if (defaultRes.data) {
      selectedAddr.value = defaultRes.data
      return
    }
  } catch (e) { /* 无默认地址 */ }
  try {
    const listRes = await getAddressListAPI()
    if (listRes.data && listRes.data.length > 0) {
      selectedAddr.value = listRes.data[0]
    }
  } catch (e) { /* 无地址列表 */ }
}

onShow(() => {
  const n = new Date()
  const h = n.getHours().toString().padStart(2, "0")
  const m = n.getMinutes().toString().padStart(2, "0")
  time.value = h + ":" + m
  // 每次进入结算页重新加载地址（用户可能从地址管理页返回）
  loadAddress()
})
</script>

<style scoped>
.address-section {
  display: flex;
  align-items: center;
  background: var(--surface);
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
  border: 1px solid var(--border);
  cursor: pointer;
}
.addr-icon {
  font-size: 20px;
  margin-right: 10px;
}
.addr-info {
  flex: 1;
}
.addr-name {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
}
.consignee {
  font-weight: 600;
  font-size: 14px;
}
.phone {
  font-size: 12px;
  color: var(--muted);
  margin-left: 8px;
}
.default-tag {
  font-size: 10px;
  color: #2F6FEB;
  background: #EBF1FF;
  padding: 1px 6px;
  border-radius: 4px;
  margin-left: 8px;
}
.addr-detail {
  font-size: 12px;
  color: var(--muted);
  line-height: 1.4;
}
.addr-empty .empty-tip {
  font-size: 13px;
  color: #999;
}
.addr-arrow {
  font-size: 18px;
  color: #ccc;
  margin-left: 8px;
}
.btn-disabled {
  opacity: 0.5;
}
</style>

