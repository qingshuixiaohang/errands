<template>
  <view class="login-page">
    <view class="login-logo">
      <svg viewBox="0 0 24 24" width="32" height="32">
        <path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/>
      </svg>
    </view>
    <h2>校园外跑</h2>
    <text class="sub">点餐 · 跑腿 · 赚零花</text>
    <view style="width:100%">
      <view class="role-btn" :class="{ selected: selectedRole === 'customer' }" @tap="selectRole('customer')">
        <view class="icon customer">
          <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="1.8">
            <circle cx="12" cy="8" r="4"/><path d="M20 21a8 8 0 10-16 0"/>
          </svg>
        </view>
        <view class="info">
          <h4>我是顾客</h4>
          <p>点餐下单，享受校园美食</p>
        </view>
        <view class="check" v-if="selectedRole === 'customer'">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="white" stroke-width="3">
            <polyline points="20 6 9 17 4 12"/>
          </svg>
        </view>
      </view>
      <view class="role-btn" :class="{ selected: selectedRole === 'rider' }" @tap="selectRole('rider')">
        <view class="icon rider">
          <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="1.8">
            <circle cx="5" cy="18" r="2"/><circle cx="19" cy="18" r="2"/>
            <path d="M12 18V12"/><path d="M11 12h2l3-4H9l4-4"/>
          </svg>
        </view>
        <view class="info">
          <h4>我是骑手</h4>
          <p>接单配送，赚取配送费</p>
        </view>
        <view class="check" v-if="selectedRole === 'rider'">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="white" stroke-width="3">
            <polyline points="20 6 9 17 4 12"/>
          </svg>
        </view>
      </view>
    </view>
    <view class="loading-tip" v-if="selectedRole">
      <text>正在进入{{ selectedRole === 'customer' ? '校园外跑' : '骑手中心' }}…</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from "vue"
import { useUserStore } from "@/stores/user"
import { loginAPI } from "@/api/login"

const u = useUserStore()
const selectedRole = ref(null)

async function selectRole(role) {
  selectedRole.value = role

  try {
    // 1. 调用 uni.login 获取微信临时 code
    let code
    try {
      const loginRes = await uni.login()
      code = loginRes.code
      console.log("获取到微信code:", code)
    } catch (e) {
      console.log("uni.login 失败（模拟器环境），使用模拟code", e)
      code = "mock_code_" + role
    }

    // 2. 用 code 调后端登录
    const loginRes = await loginAPI(code)
    if (loginRes && loginRes.data) {
      u.setProfile(loginRes.data)
    }
  } catch (e) {
    console.log("后端登录失败，使用本地登录", e)
    // 如果后端不可用，用本地模拟数据
    u.setProfile({
      id: Date.now(),
      name: role === "customer" ? "林小明" : "李明",
      token: "local_token_" + Date.now(),
      role: role,
    })
  }
  u.setRole(role)

  // 延迟800ms 让选中状态明显展示再跳转
  setTimeout(() => {
    if (role === "customer") {
      uni.reLaunch({ url: "/pages/index/index" })
    } else {
      uni.reLaunch({ url: "/pages/rider-pending/index" })
    }
  }, 800)
}
</script>

<style scoped>
.role-btn {
  position: relative;
  transition: all .25s cubic-bezier(.16,1,.3,1);
}
.role-btn:active {
  transform: scale(.97);
}
.role-btn.selected {
  border-color: var(--accent);
  background: var(--accent-light);
  transform: scale(1.02);
  box-shadow: 0 4px 16px rgba(47,111,235,0.2);
}
.role-btn.selected .icon.customer {
  background: var(--accent);
  color: white;
}
.role-btn.selected .icon.rider {
  background: #C2410C;
  color: white;
}
.role-btn.selected .info h4 {
  color: var(--accent);
}
.check {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--accent);
  display: flex;
  align-items: center;
  justify-content: center;
  animation: popIn .3s cubic-bezier(.16,1,.3,1);
}
@keyframes popIn {
  0% { transform: translateY(-50%) scale(0); }
  70% { transform: translateY(-50%) scale(1.15); }
  100% { transform: translateY(-50%) scale(1); }
}
.loading-tip {
  margin-top: 24px;
  font-size: 13px;
  color: var(--muted);
  animation: fadeIn .3s ease;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(6px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
