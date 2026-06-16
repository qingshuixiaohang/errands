import { http } from "@/utils/http"

/**
 * 用户登录
 */
export const loginAPI = (code) => {
  return http({
    method: "POST",
    url: "/user/user/login",
    data: { code },
  })
}
