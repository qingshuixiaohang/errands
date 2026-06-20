<script setup lang="ts" name="layout">
import { RouterView, useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserInfoStore } from '@/store'
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { fixPwdAPI } from '@/api/employee'
import { getStatusAPI, fixStatusAPI } from '@/api/shop'
import { ElNotification } from 'element-plus'

const dialogFormVisible = ref(false)
const dialogStatusVisible = ref(false)
const formLabelWidth = '80px'
const isCollapse = ref(false)

const menuList = [
  { title: '控制台', path: '/dashboard', icon: 'pieChart' },
  { title: '数据统计', path: '/statistics', icon: 'memo' },
  { title: '订单管理', path: '/order', icon: 'collection' },
  { title: '商家管理', path: '/category', icon: 'postcard' },
  { title: '商品管理', path: '/dish', icon: 'dish' },
  { title: '用户管理', path: '/user', icon: 'user' },
  { title: '骑手管理', path: '/rider', icon: 'userFilled' },
  { title: '管理员管理', path: '/employee', icon: 'setting' },
]

const form = reactive({ oldPwd: "", newPwd: "", rePwd: "" })
const pwdRef = ref()
const status = ref(1)
const status_active = ref(1)

const samePwd = (_: any, value: any, callback: any) => {
  if (value !== form.newPwd) { callback(new Error("两次输入的密码不一致!")) }
  else { callback() }
}
const rules = {
  oldPwd: [{ required: true, message: "请输入原密码", trigger: "blur" }, { pattern: /^[a-zA-Z0-9]{1,10}$/, message: "原密码必须是1-10的大小写字母数字", trigger: "blur" }],
  newPwd: [{ required: true, message: "请输入新密码", trigger: "blur" }, { pattern: /^\S{6,15}$/, message: "新密码必须是6-15的非空字符", trigger: "blur" }],
  rePwd: [{ required: true, message: "请再次输入新密码", trigger: "blur" }, { pattern: /^\S{6,15}$/, message: "新密码必须是6-15的非空字符", trigger: "blur" }, { validator: samePwd, trigger: "blur" }]
}

const router = useRouter()
const userInfoStore = useUserInfoStore()
const route = useRoute()
const getActiveAside = () => route.path

const init = async () => {
  const { data: res } = await getStatusAPI()
  status.value = res.data
  status_active.value = res.data
}
init()

const cancelStatus = () => { ElMessage({ type: "info", message: "已取消修改" }); dialogStatusVisible.value = false }
const cancelForm = () => { ElMessage({ type: "info", message: "已取消修改" }); dialogFormVisible.value = false }
const fixStatus = async () => {
  const { data: res } = await fixStatusAPI(status_active.value)
  if (res.code != 0) return
  status.value = status_active.value
  ElMessage({ type: "success", message: "修改成功" })
  dialogStatusVisible.value = false
}
const fixPwd = async () => {
  const valid = await pwdRef.value.validate()
  if (!valid) return false
  const { data: res } = await fixPwdAPI({ oldPwd: form.oldPwd, newPwd: form.newPwd })
  if (res.code != 0) return
  ElMessage({ type: "success", message: "修改成功" })
  dialogFormVisible.value = false
}

const quitFn = () => {
  ElMessageBox.confirm("走了，爱是会消失的吗?", "退出登录", { confirmButtonText: "OK", cancelButtonText: "Cancel", type: "warning" })
    .then(() => {
      ElMessage({ type: "success", message: "退出成功" })
      userInfoStore.userInfo = null
      router.push("/login")
    })
    .catch(() => ElMessage({ type: "info", message: "已取消退出" }))
}

const websocket = ref<WebSocket | null>(null)
const shopShow = ref(false)

const webSocket = () => {
  const clientId = Math.random().toString(36).slice(2)
  const socketUrl = "ws://localhost:8081/ws/" + clientId
  if (typeof WebSocket == "undefined") {
    ElNotification({ title: "提示", message: "当前浏览器无法接收实时报警信息，请使用谷歌浏览器！", type: "warning", duration: 0 })
  } else {
    websocket.value = new WebSocket(socketUrl)
    websocket.value.onopen = () => console.log("浏览器WebSocket已打开")
    websocket.value.onmessage = (msg) => {
      const jsonMsg = JSON.parse(msg.data)
      ElNotification({
        title: jsonMsg.type === 1 ? "待接单" : "催单",
        message: jsonMsg.type === 1 ? `<span>您有1个<span style="color:#6C4FA7">订单待处理</span>,${jsonMsg.content},请及时接单</span>` : `${jsonMsg.content}<span style="color:#6C4FA7;cursor:pointer">去处理</span>`,
        duration: 0, dangerouslyUseHTMLString: true,
        onClick: () => { router.push(`/order?orderId=${jsonMsg.orderId}`).catch(() => {}); setTimeout(() => location.reload(), 100) }
      })
    }
    websocket.value.onerror = () => ElNotification({ title: "错误", message: "服务器错误，无法接收实时报警信息", type: "error", duration: 0 })
    websocket.value.onclose = () => console.log("WebSocket已关闭")
  }
}

const handleClose = () => { shopShow.value = false }
onMounted(() => { document.addEventListener("click", handleClose); webSocket() })
onBeforeUnmount(() => { if (websocket.value) websocket.value.close() })
</script>

<template>
  <div class="common-layout">
    <!-- 店铺状态设置弹窗 -->
    <el-dialog v-model="dialogStatusVisible" title="店铺状态设置" width="520" class="mingchao-dialog" top="5vh">
      <div class="status-radio-group">
        <label class="status-radio-card" :class="{ checked: status_active === 1 }" @click="status_active = 1">
          <span class="s-radio-circle"><span class="s-radio-dot" v-if="status_active === 1"></span></span>
          <span class="s-radio-content">
            <span class="s-radio-title">营业中</span>
            <span class="s-radio-desc">当前餐厅处于营业状态，自动接收任何订单，可点击打烊进入店铺打烊状态。</span>
          </span>
        </label>
        <label class="status-radio-card" :class="{ checked: status_active === 0 }" @click="status_active = 0">
          <span class="s-radio-circle"><span class="s-radio-dot" v-if="status_active === 0"></span></span>
          <span class="s-radio-content">
            <span class="s-radio-title">打烊中</span>
            <span class="s-radio-desc">当前餐厅处于打烊状态，仅接受营业时间内的预定订单，可点击营业中手动恢复营业状态。</span>
          </span>
        </label>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="mingchao-btn" @click="cancelStatus">取消</el-button>
          <el-button class="mingchao-btn mingchao-btn-primary" @click="fixStatus">确定</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 修改密码弹窗 -->
    <el-dialog v-model="dialogFormVisible" title="修改密码" width="500" class="mingchao-dialog">
      <el-form :model="form" :rules="rules" ref="pwdRef">
        <el-form-item prop="oldPwd" label="原密码" :label-width="formLabelWidth">
          <el-input v-model="form.oldPwd" autocomplete="off" class="mingchao-input" />
        </el-form-item>
        <el-form-item prop="newPwd" label="新密码" :label-width="formLabelWidth">
          <el-input v-model="form.newPwd" autocomplete="off" class="mingchao-input" />
        </el-form-item>
        <el-form-item prop="rePwd" label="确认密码" :label-width="formLabelWidth">
          <el-input v-model="form.rePwd" autocomplete="off" class="mingchao-input" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="mingchao-btn" @click="cancelForm">取消</el-button>
          <el-button class="mingchao-btn mingchao-btn-primary" @click="fixPwd">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="mingchao-header">
        <div class="header-bg-decor">
          <span class="hd-s s1">✦</span>
          <span class="hd-s s2">✧</span>
          <span class="hd-s s3">✦</span>
        </div>
        <div class="header-left">
          <div class="logo-area">
            <span class="logo-icon">🏃‍♂️</span>
            <span class="logo-text">抱抱速达</span>
            <span class="logo-badge">后台管理</span>
          </div>
          <el-icon class="collapse-btn" v-if="isCollapse"><Expand @click.stop="isCollapse = !isCollapse" /></el-icon>
          <el-icon class="collapse-btn" v-else><Fold @click.stop="isCollapse = !isCollapse" /></el-icon>
        </div>
        <div class="header-right">
          <div class="status-badge" :class="status == 1 ? 'online' : 'offline'">
            <span class="status-dot"></span>{{ status == 1 ? '营业中' : "打烊中" }}
          </div>
          <button class="status-change-btn" @click="dialogStatusVisible = true">
            <span>⚙</span> 店铺状态
          </button>
          </div>
          <el-dropdown class="user-dropdown">
            <span class="user-trigger">
              <span class="user-avatar">{{ (userInfoStore.userInfo ? userInfoStore.userInfo.account : '未').charAt(0).toUpperCase() }}</span>
              <span class="user-name">{{ userInfoStore.userInfo ? userInfoStore.userInfo.account : '未登录' }}</span>
              <el-icon class="arrow-down-icon"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu class="mingchao-dropdown">
                <el-dropdown-item @click="dialogFormVisible = true"><el-icon><lock /></el-icon> 修改密码</el-dropdown-item>
                <el-dropdown-item @click="quitFn" divided><el-icon><switch-button /></el-icon> 退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
      </el-header>
      <el-container class="main-container">
        <!-- 左侧导航菜单 -->
        <el-aside :width="isCollapse ? '64px' : '220px'" class="mingchao-aside">
          <el-menu :default-active="getActiveAside()" :collapse="isCollapse" :collapse-transition="false" unique-opened router class="mingchao-menu">
            <template v-for="item in menuList" :key="item.path">
              <el-menu-item :index="item.path">
                <el-icon><component :is="item.icon" /></el-icon>
                <template #title><span>{{ item.title }}</span></template>
              </el-menu-item>
            </template>
          </el-menu>
        </el-aside>
        <!-- 右侧主内容 -->
        <el-container class="content-container">
          <el-main class="mingchao-main"><router-view></router-view></el-main>
          <el-footer class="mingchao-footer">
            <span class="footer-icon">✦</span> © 2024 抱抱速达 · 后台管理系统 <span class="footer-icon">✦</span>
          </el-footer>
        </el-container>
      </el-container>
    </el-container>
  </div>
</template>

<style lang="less" scoped>
.common-layout {
  height: 100vh;
  overflow: hidden;
  background: #F8F7FC;
}
// ===== 头部 =====
.mingchao-header {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px !important;
  padding: 0 24px;
  background: linear-gradient(135deg, #1a0a2e 0%, #2d1b4e 50%, #4a2d7a 100%);
  border-bottom: 1px solid rgba(255,255,255,0.08);
  overflow: hidden;
  z-index: 100;
}
.header-bg-decor {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  pointer-events: none;
  overflow: hidden;
}
.hd-s {
  position: absolute;
  color: rgba(245,166,35,0.25);
  animation: hdSparkle 3s ease-in-out infinite;
}
.hd-s.s1 { top: 8px; left: 40%; font-size: 14px; animation-delay: 0s; }
.hd-s.s2 { top: 40px; left: 60%; font-size: 10px; animation-delay: 1.5s; }
.hd-s.s3 { top: 12px; left: 75%; font-size: 16px; animation-delay: 2.5s; }
@keyframes hdSparkle {
  0%, 100% { opacity: 0.2; transform: translateY(0) rotate(0deg); }
  50% { opacity: 0.6; transform: translateY(-6px) rotate(180deg); }
}
.header-left { display: flex; align-items: center; gap: 16px; z-index: 1; }
.logo-area { display: flex; align-items: center; gap: 10px; }
.logo-icon { font-size: 28px; filter: drop-shadow(0 0 12px rgba(245,166,35,0.3)); }
.logo-text { font-size: 20px; font-weight: 700; color: #fff; letter-spacing: 1px; }
.logo-badge {
  font-size: 11px; color: rgba(255,255,255,0.5); background: rgba(255,255,255,0.1);
  padding: 2px 10px; border-radius: 10px; letter-spacing: 1px; margin-left: 4px;
}
.collapse-btn { font-size: 20px; color: rgba(255,255,255,0.6); cursor: pointer; transition: all 0.3s; &:hover { color: #F5A623; } }
.header-right { display: flex; align-items: center; gap: 12px; z-index: 1; }
.status-badge {
  display: flex; align-items: center; gap: 6px;
  padding: 5px 14px; border-radius: 20px; font-size: 13px; font-weight: 500;
}
.status-badge.online { background: rgba(76,175,80,0.15); color: #4CAF50; border: 1px solid rgba(76,175,80,0.2); }
.status-badge.offline { background: rgba(255,107,107,0.15); color: #FF6B6B; border: 1px solid rgba(255,107,107,0.2); }
.status-dot { width: 7px; height: 7px; border-radius: 50%; background: currentColor; animation: pulse 2s ease-in-out infinite; }
@keyframes pulse { 0%,100% { opacity: 1; } 50% { opacity: 0.4; } }
.status-change-btn {
  display: flex; align-items: center; gap: 6px;
  background: rgba(255,255,255,0.1); border: 1px solid rgba(255,255,255,0.15); color: rgba(255,255,255,0.9);
  border-radius: 8px; padding: 7px 16px; font-size: 13px; cursor: pointer; transition: all 0.3s;
  &:hover { background: rgba(245,166,35,0.2); border-color: rgba(245,166,35,0.3); color: #F5A623; }
}
.user-dropdown { cursor: pointer; }
.user-trigger {
  display: flex; align-items: center; gap: 8px; padding: 4px 8px; border-radius: 8px;
  transition: background 0.2s; &:hover { background: rgba(255,255,255,0.1); }
}
.user-avatar {
  width: 32px; height: 32px; border-radius: 50%;
  background: linear-gradient(135deg, #6C4FA7, #F5A623);
  display: flex; align-items: center; justify-content: center;
  font-size: 14px; font-weight: 700; color: #fff;
  box-shadow: 0 0 16px rgba(108,79,167,0.3);
}
.user-name { font-size: 14px; color: rgba(255,255,255,0.9); font-weight: 500; }
.arrow-down-icon { font-size: 14px; color: rgba(255,255,255,0.5); }
// ===== 主内容 =====
.main-container { height: calc(100vh - 64px); }
.content-container { display: flex; flex-direction: column; flex: 1; background: #F8F7FC; }
.mingchao-main {
  flex: 1; padding: 24px; background: #F8F7FC; overflow-y: auto;
  &::-webkit-scrollbar { width: 6px; }
  &::-webkit-scrollbar-track { background: transparent; }
  &::-webkit-scrollbar-thumb { background: rgba(108,79,167,0.2); border-radius: 3px; }
}
.mingchao-footer {
  height: 44px !important; display: flex; align-items: center; justify-content: center;
  background: #fff; border-top: 1px solid #F0F0F5; color: #8E8E93; font-size: 12px; gap: 8px;
}
.footer-icon { color: rgba(108,79,167,0.3); font-size: 10px; }
</style>

<style lang="less">
// ===== 侧边栏 =====
.mingchao-aside {
  background: linear-gradient(180deg, #1a0a2e 0%, #2d1b4e 50%, #1a0a2e 100%);
  border-right: 1px solid rgba(255,255,255,0.06);
  transition: width 0.3s; overflow: hidden;
}
.mingchao-menu { background: transparent !important; border-right: none !important; padding: 12px 0; }
.mingchao-menu .el-menu-item {
  display: flex; align-items: center; height: 44px;
  margin: 2px 10px !important; padding: 0 14px !important;
  border-radius: 10px !important;
  color: rgba(255,255,255,0.82) !important;
  background: transparent !important; transition: all 0.25s ease;
  .el-icon { color: rgba(255,255,255,0.7); font-size: 18px; margin-right: 8px; }
}
.mingchao-menu .el-menu-item:hover {
  background: rgba(255,255,255,0.08) !important; color: #fff !important;
  .el-icon { color: rgba(255,255,255,0.9) !important; }
}
.mingchao-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, rgba(108,79,167,0.45), rgba(74,45,122,0.3)) !important;
  color: #F5A623 !important; font-weight: 600 !important;
  box-shadow: 0 2px 12px rgba(108,79,167,0.15);
  .el-icon { color: #F5A623 !important; }
}
.mingchao-menu.el-menu--collapse .el-menu-item {
  width: 44px !important; margin: 2px auto !important; padding: 0 !important;
  justify-content: center; border-radius: 10px !important;
  .el-icon { margin-right: 0 !important; }
}
// ===== 自定义radio卡片（修复Element Plus radio在弹窗中的布局bug）=====
.status-radio-group { display: flex; flex-direction: column; gap: 12px; }
.status-radio-card {
  display: flex; align-items: flex-start; gap: 14px;
  padding: 18px 20px; border-radius: 12px;
  background: #F8F7FC; border: 1.5px solid #E5E5EA;
  cursor: pointer; transition: all 0.25s ease; user-select: none;
  &:hover { border-color: #A78BFA; background: rgba(108,79,167,0.03); }
  &.checked {
    border-color: #6C4FA7; background: rgba(108,79,167,0.06);
    box-shadow: 0 0 0 3px rgba(108,79,167,0.1);
  }
}
.s-radio-circle {
  flex-shrink: 0; width: 20px; height: 20px; border-radius: 50%;
  border: 2px solid #C7C7CC; margin-top: 2px;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.25s ease;
  .checked & { border-color: #6C4FA7; }
}
.s-radio-dot {
  width: 10px; height: 10px; border-radius: 50%;
  background: #6C4FA7; animation: dotPop 0.2s ease;
}
@keyframes dotPop { 0% { transform: scale(0); } 100% { transform: scale(1); } }
.s-radio-content { display: flex; flex-direction: column; gap: 6px; }
.s-radio-title { font-size: 15px; font-weight: 600; color: #2d1b4e; }
.s-radio-desc { font-size: 13px; color: #666; line-height: 1.5; }
// ===== 弹窗 =====
.mingchao-dialog { border-radius: 14px !important; overflow: hidden; }
.mingchao-dialog .el-dialog__header {
  background: linear-gradient(135deg, #2d1b4e, #4a2d7a);
  color: #fff; padding: 16px 24px; margin: 0; font-weight: 600; font-size: 16px;
  border-radius: 14px 14px 0 0;
}
.mingchao-dialog .el-dialog__headerbtn .el-dialog__close { color: rgba(255,255,255,0.6); font-size: 18px; }
.mingchao-dialog .el-dialog__headerbtn:hover .el-dialog__close { color: #F5A623; }
.mingchao-dialog .el-dialog__body { padding: 24px; }
.mingchao-dialog .el-dialog__footer { padding: 0 24px 20px; border-top: none; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 12px; }
// ===== 按钮统一风格 =====
.mingchao-btn {
  display: inline-flex; align-items: center; justify-content: center;
  height: 38px; padding: 0 22px !important;
  border-radius: 8px !important; font-size: 14px; font-weight: 500;
  cursor: pointer; transition: all 0.25s ease;
  border: 1px solid #D1D1D6; background: #fff; color: #3C3C43;
  &:hover { border-color: #6C4FA7; color: #6C4FA7; }
  &:active { transform: scale(0.98); }
}
.mingchao-btn-primary {
  background: linear-gradient(135deg, #6C4FA7, #4A2D7A) !important;
  border: none !important; color: #fff !important;
  &:hover { background: linear-gradient(135deg, #7D5FB8, #5B3D8F) !important; box-shadow: 0 4px 16px rgba(108,79,167,0.3); color: #fff !important; }
}
// ===== 输入框 =====
.mingchao-input .el-input__wrapper {
  border-radius: 8px !important; padding: 6px 12px !important;
  &:hover { box-shadow: 0 0 0 1px #6C4FA7 !important; }
  &.is-focus { box-shadow: 0 0 0 2px rgba(108,79,167,0.25) !important; }
}
// ===== 下拉菜单 =====
.mingchao-dropdown {
  border-radius: 12px !important; border: 1px solid rgba(108,79,167,0.1) !important;
  box-shadow: 0 8px 24px rgba(0,0,0,0.12) !important; padding: 6px !important;
}
.mingchao-dropdown .el-dropdown-menu__item {
  border-radius: 8px !important; padding: 8px 16px !important;
  display: flex; align-items: center; gap: 8px;
  &:hover { background: rgba(108,79,167,0.08) !important; color: #6C4FA7 !important; }
  .el-icon { font-size: 16px; }
}
// ===== Element Plus 全局覆盖 =====
.el-card { border-radius: 12px !important; border: 1px solid var(--el-border-color) !important; }
.el-table { border-radius: 8px !important; overflow: hidden; }
.el-table th.el-table__cell { background-color: #F8F7FC !important; color: #3C3C43 !important; }
.el-pagination.is-background .el-pager li:not(.disabled).active { background: #6C4FA7 !important; }
</style>
