<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-left">
        <div class="brand-area">
          <div class="brand-icon">🏃‍♂️</div>
          <h1 class="brand-title">抱抱速达</h1>
          <p class="brand-subtitle">校园互助 · 即时配送</p>
        </div>
        <div class="mascot-area">
          <!-- CSS 绘制的鸣潮风格团子 -->
          <div class="dango dango-1"><div class="dango-face">◠‿◠</div></div>
          <div class="dango dango-2"><div class="dango-face">◡‿◡</div></div>
          <div class="dango dango-3"><div class="dango-face">◕‿◕</div></div>
          <div class="sparkle s1">✦</div>
          <div class="sparkle s2">✧</div>
          <div class="sparkle s3">✦</div>
          <div class="sparkle s4">✧</div>
          <div class="glow-ring r1"></div>
          <div class="glow-ring r2"></div>
          <div class="glow-ring r3"></div>
        </div>
      </div>
      <div class="login-right">
        <el-form label-width="0px" class="login-box" :model="form" :rules="rules" ref="loginRef">
          <div class="title-box">
            <span class="title-accent">✦</span> 欢迎回来 <span class="title-accent">✦</span>
            <div class="title-sub">后台管理</div>
          </div>
          <el-form-item prop="account">
            <el-input v-model="form.account" placeholder="请输入账号" prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input type="password" v-model="form.password" placeholder="请输入密码" prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item class="my-el-form-item">
            <el-button type="primary" class="btn-login" @click="loginFn">登 录</el-button>
          </el-form-item>
          <div class="login-footer">
            <el-link type="info" @click="$router.push('/reg')">还没有账号？去注册</el-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { loginAPI } from '@/api/employee'
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserInfoStore } from '@/store'

const userInfoStore = useUserInfoStore()

const form = ref({ account: '', password: '' })
const loginRef = ref()

const rules = {
  account: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9]{1,10}$/, message: '用户名必须是1-10的字母数字', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^\S{6,15}$/, message: '密码必须是6-15的非空字符', trigger: 'blur' }
  ]
}

const router = useRouter()

const loginFn = async () => {
  const valid = await loginRef.value.validate()
  if (valid) {
    const { data: res } = await loginAPI(form.value)
    if (res.code !== 0) return false
    ElMessage.success('登录成功')
    userInfoStore.userInfo = res.data
    router.push('/')
  }
}
</script>

<style lang="less" scoped>
.login-page {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a0a2e 0%, #2d1b4e 30%, #4a2d7a 60%, #2d1b4e 100%);
  overflow: hidden;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(ellipse at 30% 50%, rgba(108,79,167,0.3) 0%, transparent 50%),
                radial-gradient(ellipse at 70% 50%, rgba(245,166,35,0.15) 0%, transparent 50%);
    animation: bgShift 12s ease-in-out infinite alternate;
  }
}

@keyframes bgShift {
  0% { transform: translate(0, 0) rotate(0deg); }
  100% { transform: translate(-30px, -20px) rotate(3deg); }
}

.login-container {
  display: flex;
  width: 880px;
  height: 520px;
  background: rgba(255,255,255,0.06);
  border-radius: 24px;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255,255,255,0.1);
  box-shadow: 0 20px 80px rgba(0,0,0,0.4);
  overflow: hidden;
  position: relative;
  z-index: 1;
}

.login-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  position: relative;
  background: linear-gradient(160deg, rgba(108,79,167,0.2) 0%, transparent 100%);
}

.brand-area { text-align: center; z-index: 2; position: relative; }
.brand-icon { font-size: 64px; margin-bottom: 12px; filter: drop-shadow(0 0 20px rgba(245,166,35,0.4)); }
.brand-title { font-size: 36px; font-weight: 800; color: #fff; margin: 0; letter-spacing: 2px; text-shadow: 0 0 30px rgba(108,79,167,0.5); }
.brand-subtitle { font-size: 14px; color: rgba(255,255,255,0.5); margin-top: 8px; letter-spacing: 4px; }

.mascot-area { position: absolute; width: 100%; height: 100%; top: 0; left: 0; overflow: hidden; }

.mascot-ring {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(255,255,255,0.08);
  animation: ringPulse 4s ease-in-out infinite;
}
.ring-1 { width: 300px; height: 300px; top: 50%; left: 50%; transform: translate(-50%,-50%); animation-delay: 0s; }
.ring-2 { width: 400px; height: 400px; top: 50%; left: 50%; transform: translate(-50%,-50%); animation-delay: 1s; }
.ring-3 { width: 200px; height: 200px; top: 50%; left: 50%; transform: translate(-50%,-50%); animation-delay: 2s; }

@keyframes ringPulse {
  0%, 100% { transform: translate(-50%,-50%) scale(1); opacity: 0.3; }
  50% { transform: translate(-50%,-50%) scale(1.1); opacity: 0.6; }
}

.floating-orb {
  position: absolute;
  color: rgba(255,255,255,0.15);
  font-size: 24px;
  animation: orbFloat 6s ease-in-out infinite;
}
.orb-1 { top: 15%; left: 20%; animation-delay: 0s; }
.orb-2 { top: 60%; right: 25%; animation-delay: 2s; font-size: 18px; }
.orb-3 { bottom: 25%; left: 30%; animation-delay: 4s; font-size: 14px; }

@keyframes orbFloat {
  0%, 100% { transform: translateY(0) rotate(0deg); opacity: 0.15; }
  50% { transform: translateY(-20px) rotate(180deg); opacity: 0.3; }
}

.login-right {
  width: 380px;
  background: rgba(255,255,255,0.95);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.login-box { width: 100%; max-width: 320px; }

.title-box {
  text-align: center;
  margin-bottom: 32px;
  font-size: 22px;
  font-weight: 700;
  color: #2d1b4e;
}
.title-accent { color: #F5A623; }
.title-sub { font-size: 13px; color: #999; margin-top: 6px; font-weight: 400; letter-spacing: 2px; }

:deep(.el-input__wrapper) {
  border-radius: 12px;
  padding: 12px 16px;
  box-shadow: 0 0 0 1px #e8e8e8 !important;
  &:hover { box-shadow: 0 0 0 1px #6C4FA7 !important; }
}
:deep(.el-input__wrapper.is-focus) { box-shadow: 0 0 0 2px rgba(108,79,167,0.3) !important; }

.btn-login {
  width: 100%;
  padding: 22px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #6C4FA7, #4A2D7A);
  border: none;
  letter-spacing: 4px;
  transition: all 0.3s;
  &:hover { background: linear-gradient(135deg, #7D5FB8, #5B3D8F); transform: translateY(-1px); box-shadow: 0 8px 24px rgba(108,79,167,0.35); }
}

.my-el-form-item { margin-bottom: 8px; }

.login-footer { text-align: center; margin-top: 16px; }
.login-footer :deep(.el-link) { font-size: 13px; color: #999; }
/* ===== 鸣潮团子 CSS 插画 ===== */
.mascot-area { position: absolute; width: 100%; height: 100%; top: 0; left: 0; overflow: hidden; pointer-events: none; }

.dango {
  position: absolute;
  width: 80px; height: 80px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  animation: dangoFloat 4s ease-in-out infinite;
  box-shadow: 0 0 40px rgba(108,79,167,0.3);
}
.dango-face { font-size: 20px; color: rgba(255,255,255,0.9); text-shadow: 0 0 10px rgba(245,166,35,0.5); }
.dango-1 { background: linear-gradient(135deg, #8B5CF6, #6C4FA7); top: 25%; left: 25%; animation-delay: 0s; width: 90px; height: 90px; }
.dango-2 { background: linear-gradient(135deg, #F5A623, #D4891E); top: 55%; left: 55%; animation-delay: 1.5s; width: 70px; height: 70px; }
.dango-3 { background: linear-gradient(135deg, #7C3AED, #5B3D8F); top: 40%; left: 40%; animation-delay: 3s; width: 60px; height: 60px; }

@keyframes dangoFloat {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-25px) scale(1.05); }
}

.sparkle {
  position: absolute; color: #F5A623; font-size: 18px; animation: sparkle 2s ease-in-out infinite;
  text-shadow: 0 0 20px rgba(245,166,35,0.6);
}
.s1 { top: 20%; left: 20%; animation-delay: 0s; }
.s2 { top: 60%; left: 30%; animation-delay: 0.7s; font-size: 14px; }
.s3 { top: 30%; left: 60%; animation-delay: 1.4s; font-size: 22px; }
.s4 { top: 70%; left: 65%; animation-delay: 2.1s; font-size: 12px; }

@keyframes sparkle {
  0%, 100% { opacity: 0.3; transform: scale(0.8) rotate(0deg); }
  50% { opacity: 1; transform: scale(1.2) rotate(180deg); }
}

.glow-ring {
  position: absolute; border-radius: 50%; border: 1px solid rgba(139,92,246,0.2);
  animation: ringGlow 5s ease-in-out infinite;
}
.r1 { width: 280px; height: 280px; top: 50%; left: 50%; transform: translate(-50%,-50%); animation-delay: 0s; }
.r2 { width: 220px; height: 220px; top: 50%; left: 50%; transform: translate(-50%,-50%); animation-delay: 1.7s; border-color: rgba(245,166,35,0.15); }
.r3 { width: 160px; height: 160px; top: 50%; left: 50%; transform: translate(-50%,-50%); animation-delay: 3.4s; }

@keyframes ringGlow {
  0%, 100% { transform: translate(-50%,-50%) scale(1); opacity: 0.3; }
  50% { transform: translate(-50%,-50%) scale(1.15); opacity: 0.7; }
}
</style>

