import { defineStore } from "pinia"
import { ref } from "vue"

export const useUserStore = defineStore("user", () => {
  // 从本地存储恢复用户信息
  const saved = uni.getStorageSync("user-profile")
  const profile = ref(saved || null)
  const role = ref(null)

  // 保存用户信息
  const setProfile = (val) => {
    profile.value = val
    uni.setStorageSync("user-profile", val)
  }

  // 清理用户信息
  const clearProfile = () => {
    profile.value = null
    role.value = null
    uni.removeStorageSync("user-profile")
  }

  // 设置角色
  const setRole = (r) => {
    role.value = r
  }

  return {
    profile,
    role,
    setProfile,
    clearProfile,
    setRole,
  }
})
