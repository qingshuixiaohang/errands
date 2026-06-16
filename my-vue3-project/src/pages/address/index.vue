<template>
  <view class="page-viewport">
    <!-- 状态栏 -->
    <view class="status-bar">
      <text class="time">{{ time }}</text>
      <view class="status-icons">
        <svg width="16" height="12" viewBox="0 0 16 12" fill="none" stroke="currentColor" stroke-width="1.5">
          <rect x="1" y="1" width="14" height="10" rx="2"/><path d="M4 5V7M8 3V9M12 5V7"/>
        </svg>
      </view>
    </view>

    <!-- 顶部导航 -->
    <view class="header">
      <view class="back-btn" @tap="goBack">
        <svg viewBox="0 0 24 24"><polyline points="15 18 9 12 15 6"/></svg>
        返回
      </view>
      <h1>地址管理</h1>
      <view style="width:60px"></view>
    </view>

    <!-- 地址列表 -->
    <scroll-view scroll-y class="scroll" style="flex:1">
      <!-- 加载中 -->
      <view v-if="loading" style="text-align:center;padding:60px 20px;color:var(--muted)">
        <text>加载中…</text>
      </view>

      <!-- 空状态 -->
      <view v-else-if="addrs.length === 0" style="text-align:center;padding:60px 20px;color:var(--muted)">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--border)" stroke-width="1.5">
          <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"/>
          <circle cx="12" cy="10" r="3"/>
        </svg>
        <text style="margin-top:12px;display:block">暂无收货地址</text>
        <text style="font-size:12px;margin-top:4px;display:block">点击下方按钮添加地址</text>
      </view>

      <!-- 地址卡片列表 -->
      <view v-for="a in addrs" :key="a.id" class="addr-card" :class="{ default: a.isDefault }" @tap="setDefault(a)">
        <view class="addr-main">
          <view class="addr-top">
            <text class="consignee">{{ a.consignee }}</text>
            <text class="phone">{{ a.phone }}</text>
            <text class="default-tag" v-if="a.isDefault">默认</text>
          </view>
          <text class="addr-detail">{{ a.provinceName }}{{ a.cityName }}{{ a.districtName }}{{ a.detail }}</text>
        </view>
        <view class="addr-actions">
          <view class="action-btn" @tap.stop="confirmDelete(a)">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="3 6 5 6 21 6"/>
              <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/>
            </svg>
          </view>
        </view>
      </view>

      <view class="page-spacer"></view>
    </scroll-view>

    <!-- 新增地址按钮 -->
    <view class="add-addr-bar">
      <view class="btn-solid add-btn" @tap="showAddForm = true">
        <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
          <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        <text>新增地址</text>
      </view>
    </view>

    <!-- ===== 新增地址弹窗 ===== -->
    <view class="overlay" v-if="showAddForm" @tap="showAddForm = false">
      <view class="form-panel" @tap.stop>
        <view class="form-header">
          <text class="form-title">新增地址</text>
          <view class="close-btn" @tap="showAddForm = false">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
              <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </view>
        </view>

        <scroll-view scroll-y class="form-body">
          <view class="form-group">
            <text class="form-label">收件人</text>
            <input class="form-input" type="text" v-model="form.consignee" placeholder="请输入收件人姓名" maxlength="20" />
          </view>
          <view class="form-group">
            <text class="form-label">手机号</text>
            <input class="form-input" type="text" v-model="form.phone" placeholder="请输入手机号码" maxlength="11" />
          </view>
          <view class="form-group">
            <text class="form-label">详细地址</text>
            <input class="form-input" type="text" v-model="form.detail" placeholder="街道、门牌号等" maxlength="50" />
          </view>
          <view class="form-group switch-group">
            <text class="form-label">设为默认地址</text>
            <view class="toggle" :class="{ on: form.isDefault }" @tap="form.isDefault = !form.isDefault">
              <view class="toggle-knob"></view>
            </view>
          </view>
        </scroll-view>

        <view class="form-footer">
          <view class="btn-outline" style="flex:1;padding:12px;text-align:center;border-radius:var(--radius-sm)" @tap="showAddForm = false">取消</view>
          <view class="btn-solid" style="flex:2;padding:12px;text-align:center;border-radius:var(--radius-sm);margin-left:10px" @tap="submitAddress">保存地址</view>
        </view>
      </view>
    </view>

    <!-- ===== 删除确认弹窗 ===== -->
    <view class="overlay" v-if="deleteTarget" @tap="deleteTarget = null">
      <view class="confirm-dialog" @tap.stop>
        <text class="confirm-title">确认删除</text>
        <text class="confirm-desc">确定要删除该收货地址吗？</text>
        <view class="confirm-actions">
          <view class="btn-outline" style="flex:1;padding:12px;text-align:center;border-radius:var(--radius-sm)" @tap="deleteTarget = null">取消</view>
          <view class="btn-solid" style="flex:1;padding:12px;text-align:center;border-radius:var(--radius-sm);margin-left:10px;background:var(--danger)" @tap="doDelete">删除</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from "vue"
import { onShow } from "@dcloudio/uni-app"
import { getAddressListAPI, addAddressAPI, deleteAddressAPI, updateDefaultAddressAPI } from "@/api/address"

const time = ref("")
const addrs = ref([])
const loading = ref(true)
const showAddForm = ref(false)
const deleteTarget = ref(null)

// 新增地址表单
const form = ref({
  consignee: "",
  phone: "",
  detail: "",
  isDefault: false,
})

// 获取地址列表
async function loadAddresses() {
  loading.value = true
  try {
    const res = await getAddressListAPI()
    addrs.value = res.data || []
  } catch (e) {
    console.log("获取地址列表失败", e)
    addrs.value = []
  } finally {
    loading.value = false
  }
}

// 设置默认地址
async function setDefault(addr) {
  if (addr.isDefault) return
  try {
    await updateDefaultAddressAPI({ id: addr.id })
    uni.showToast({ title: "已设为默认地址", icon: "none" })
    // 重新加载列表
    loadAddresses()
  } catch (e) {
    uni.showToast({ title: "设置失败", icon: "none" })
  }
}

// 提交新增地址
async function submitAddress() {
  // 表单校验
  if (!form.value.consignee.trim()) {
    uni.showToast({ title: "请输入收件人姓名", icon: "none" })
    return
  }
  if (!form.value.phone.trim() || !/^1\d{10}$/.test(form.value.phone.trim())) {
    uni.showToast({ title: "请输入正确的手机号码", icon: "none" })
    return
  }
  if (!form.value.detail.trim()) {
    uni.showToast({ title: "请输入详细地址", icon: "none" })
    return
  }

  try {
    // 后端 AddressBook 需要地区字段，发送空字符串兜底
    // isDefault 必须转为 Integer(0/1)，因为后端实体是 Integer 类型，Jackson 无法反序列化 boolean
    const payload = {
      ...form.value,
      provinceName: form.value.provinceName || "",
      cityName: form.value.cityName || "",
      districtName: form.value.districtName || "",
      isDefault: form.value.isDefault ? 1 : 0,
    }
    await addAddressAPI(payload)
    uni.showToast({ title: "地址添加成功", icon: "none" })
    showAddForm.value = false
    // 重置表单
    form.value = { consignee: "", phone: "", detail: "", isDefault: false }
    // 重新加载列表
    loadAddresses()
  } catch (e) {
    const msg = e?.data?.msg || e?.errMsg || "请重试"
    uni.showToast({ title: "添加失败：" + msg, icon: "none" })
  }
}

// 删除确认
function confirmDelete(addr) {
  deleteTarget.value = addr
}

// 执行删除
async function doDelete() {
  if (!deleteTarget.value) return
  try {
    await deleteAddressAPI(deleteTarget.value.id)
    uni.showToast({ title: "地址已删除", icon: "none" })
    deleteTarget.value = null
    loadAddresses()
  } catch (e) {
    uni.showToast({ title: "删除失败", icon: "none" })
    deleteTarget.value = null
  }
}

// 返回
const goBack = () => {
  try { uni.navigateBack() } catch(e) { uni.reLaunch({ url: "/pages/profile/index" }) }
}

onShow(() => {
  const n = new Date()
  const h = n.getHours().toString().padStart(2, "0")
  const m = n.getMinutes().toString().padStart(2, "0")
  time.value = h + ":" + m
  loadAddresses()
})
</script>

<style scoped>
.scroll {
  padding: 4px 0 80px;
}

/* ===== Address Card ===== */
.addr-card {
  display: flex;
  align-items: center;
  background: var(--surface);
  border-radius: var(--radius);
  margin: 0 20px 10px;
  padding: 14px 16px;
  border: 1px solid var(--border);
  cursor: pointer;
  transition: transform .12s, border-color .2s;
}
.addr-card:active {
  transform: scale(.98);
}
.addr-card.default {
  border-color: var(--accent);
  background: var(--accent-light);
}

.addr-main {
  flex: 1;
  min-width: 0;
}

.addr-top {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
}

.consignee {
  font-size: 15px;
  font-weight: 600;
}

.phone {
  font-size: 12px;
  color: var(--muted);
  margin-left: 10px;
}

.default-tag {
  font-size: 10px;
  color: var(--accent);
  background: rgba(47, 111, 235, .12);
  padding: 2px 8px;
  border-radius: 4px;
  margin-left: 8px;
  font-weight: 600;
}

.addr-detail {
  font-size: 13px;
  color: var(--muted);
  line-height: 1.45;
  display: block;
}

.addr-actions {
  flex-shrink: 0;
  margin-left: 12px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--bg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--danger);
  cursor: pointer;
  transition: background .15s;
}
.action-btn:active {
  background: var(--border);
}

/* ===== Add Button Bar ===== */
.add-addr-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 20px;
  padding-bottom: calc(12px + var(--safe-bottom));
  background: var(--surface);
  border-top: 1px solid var(--border);
  z-index: 50;
}

.add-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 14px;
  border-radius: var(--radius-sm);
  font-size: 15px;
  font-weight: 600;
}

/* ===== Overlay ===== */
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, .35);
  z-index: 200;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  animation: fadeIn .2s ease;
}

/* ===== Form Panel ===== */
.form-panel {
  width: 100%;
  max-width: 100%;
  max-height: 80vh;
  background: var(--surface);
  border-radius: 16px 16px 0 0;
  display: flex;
  flex-direction: column;
  animation: slideUp .3s cubic-bezier(.16,1,.3,1);
}

.form-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 20px 14px;
  border-bottom: 1px solid var(--border);
  flex-shrink: 0;
}

.form-title {
  font-size: 17px;
  font-weight: 700;
}

.close-btn {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: var(--bg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--muted);
  cursor: pointer;
}

.form-body {
  padding: 16px 20px;
  flex: 1;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 16px;
}

.form-label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: var(--fg);
  margin-bottom: 6px;
}

.form-input {
  width: 100%;
  padding: 12px 14px;
  font-size: 14px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  background: var(--bg);
  color: var(--fg);
  box-sizing: border-box;
  font-family: var(--font);
  outline: none;
}
.form-input:focus {
  border-color: var(--accent);
}

/* Toggle Switch */
.switch-group {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.switch-group .form-label {
  margin-bottom: 0;
}

.toggle {
  width: 44px;
  height: 24px;
  border-radius: 12px;
  background: var(--border);
  cursor: pointer;
  position: relative;
  transition: background .2s;
  flex-shrink: 0;
}
.toggle.on {
  background: var(--accent);
}

.toggle-knob {
  position: absolute;
  top: 2px;
  left: 2px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: white;
  box-shadow: 0 1px 3px rgba(0,0,0,.15);
  transition: transform .2s cubic-bezier(.16,1,.3,1);
}
.toggle.on .toggle-knob {
  transform: translateX(20px);
}

.form-footer {
  display: flex;
  padding: 12px 20px;
  padding-bottom: calc(12px + var(--safe-bottom));
  border-top: 1px solid var(--border);
  flex-shrink: 0;
}

/* ===== Confirm Dialog ===== */
.confirm-dialog {
  width: 280px;
  background: var(--surface);
  border-radius: var(--radius);
  padding: 24px 20px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: popIn .25s cubic-bezier(.16,1,.3,1);
}

.confirm-title {
  font-size: 17px;
  font-weight: 700;
  margin-bottom: 8px;
}

.confirm-desc {
  font-size: 14px;
  color: var(--muted);
  margin-bottom: 20px;
}

.confirm-actions {
  display: flex;
  width: 100%;
}

/* ===== Animations ===== */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from { transform: translateY(30px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

@keyframes popIn {
  from { transform: scale(.9); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}
</style>
