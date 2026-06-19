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
      <h1>空闲时间管理</h1>
      <view style="width:60px"></view>
    </view>

    <scroll-view scroll-y class="scroll" style="flex:1">
      <view class="tip-section" style="padding:12px 20px;font-size:13px;color:#666;background:#F0EDF5;margin:0 16px 12px;border-radius:8px">
        📋 设置空闲时间后，AI 派单系统会在你有空时自动分配订单。
      </view>

      <view style="padding:0 16px 12px">
        <view class="section-title">当前空闲时间</view>
        <view v-if="scheduleList.length === 0" style="text-align:center;padding:40px 0;color:#999">
          <text>暂无设置</text>
        </view>
        <view class="schedule-card" v-for="s in scheduleList" :key="s.id">
          <view class="s-info">
            <text class="s-day">{{ weekdays[s.weekday] }}</text>
            <text class="s-time">{{ s.startTime.slice(0,5) }} - {{ s.endTime.slice(0,5) }}</text>
            <text class="s-loc" v-if="s.location">{{ s.location }}</text>
          </view>
          <view class="s-del" @tap="deleteSchedule(s.id)">删除</view>
        </view>
      </view>

      <view style="padding:0 16px;margin-top:8px">
        <view class="section-title">添加空闲时间</view>
        <view class="form-card">
          <view class="form-row">
            <text class="label">星期</text>
            <picker :range="weekdayOpts" @change="onWeekdayChange">
              <text class="picker-val">{{ form.weekday ? weekdays[form.weekday] : '请选择' }}</text>
            </picker>
          </view>
          <view class="form-row">
            <text class="label">开始时间</text>
            <picker mode="time" :value="form.startTime" @change="(e) => form.startTime = e.detail.value">
              <text class="picker-val">{{ form.startTime || '请选择' }}</text>
            </picker>
          </view>
          <view class="form-row">
            <text class="label">结束时间</text>
            <picker mode="time" :value="form.endTime" @change="(e) => form.endTime = e.detail.value">
              <text class="picker-val">{{ form.endTime || '请选择' }}</text>
            </picker>
          </view>
          <view class="form-row">
            <text class="label">常驻区域</text>
            <input v-model="form.location" placeholder="如：北区宿舍" style="flex:1;text-align:right;font-size:14px" />
          </view>
          <view class="btn-solid" style="text-align:center;padding:12px;margin-top:12px" @tap="saveSchedule">保存</view>
        </view>
      </view>

      <view style="height:40px"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onShow } from "vue"
import { useUserStore } from "@/stores/user"
import { getRiderScheduleAPI, saveRiderScheduleAPI, deleteRiderScheduleAPI } from "@/api/order"

const time = ref("")
const userStore = useUserStore()
const scheduleList = ref([])
const weekdays = {1:"周一",2:"周二",3:"周三",4:"周四",5:"周五",6:"周六",7:"周日"}
const weekdayOpts = ["周一","周二","周三","周四","周五","周六","周日"]
const form = ref({ weekday: 0, startTime: "", endTime: "", location: "" })

const riderId = userStore.profile?.id || 0

const goBack = () => { try { uni.navigateBack() } catch(e) { uni.navigateTo({ url:"/pages/rider-profile/index" }) } }

function onWeekdayChange(e) { form.value.weekday = e.detail.value + 1 }

async function loadSchedules() {
  if (!riderId) return
  try {
    const res = await getRiderScheduleAPI(riderId)
    scheduleList.value = res.data || []
  } catch(e) { console.log("获取空闲时间失败", e) }
}

async function saveSchedule() {
  if (!form.value.weekday || !form.value.startTime || !form.value.endTime) {
    return uni.showToast({ title: "请填写完整", icon: "none" })
  }
  if (form.value.startTime >= form.value.endTime) {
    return uni.showToast({ title: "结束时间需大于开始时间", icon: "none" })
  }
  try {
    await saveRiderScheduleAPI({
      riderId, weekday: form.value.weekday,
      startTime: form.value.startTime + ":00",
      endTime: form.value.endTime + ":00",
      location: form.value.location
    })
    uni.showToast({ title: "保存成功", icon: "none" })
    form.value = { weekday: 0, startTime: "", endTime: "", location: "" }
    await loadSchedules()
  } catch(e) { uni.showToast({ title: "保存失败", icon: "none" }) }
}

async function deleteSchedule(id) {
  try {
    await deleteRiderScheduleAPI(id)
    uni.showToast({ title: "已删除", icon: "none" })
    await loadSchedules()
  } catch(e) { uni.showToast({ title: "删除失败", icon: "none" }) }
}

onShow(() => {
  const n = new Date()
  time.value = n.getHours().toString().padStart(2,"0") + ":" + n.getMinutes().toString().padStart(2,"0")
  loadSchedules()
})
</script>

<style scoped>
.section-title { font-size:15px; font-weight:600; margin-bottom:10px; color:#1C1C1E }
.schedule-card { display:flex; align-items:center; justify-content:space-between; background:var(--surface); border:1px solid var(--border); border-radius:8px; padding:12px; margin-bottom:8px }
.s-info { display:flex; align-items:center; gap:12px; flex-wrap:wrap }
.s-day { font-weight:600; font-size:14px; min-width:36px }
.s-time { font-size:14px; color:var(--muted) }
.s-loc { font-size:12px; color:#2F6FEB; background:#EBF1FF; padding:2px 8px; border-radius:4px }
.s-del { color:#FF6B6B; font-size:13px; padding:4px 8px }
.form-card { background:var(--surface); border:1px solid var(--border); border-radius:8px; padding:12px }
.form-row { display:flex; align-items:center; justify-content:space-between; padding:8px 0; border-bottom:1px solid var(--border) }
.form-row:last-child { border-bottom:none }
.label { font-size:14px; color:#3C3C43; min-width:70px }
.picker-val { font-size:14px; color:#2F6FEB }
</style>
