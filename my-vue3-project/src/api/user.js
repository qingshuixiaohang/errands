import { http } from "@/utils/http"

// 根据id查询用户信息
export const getUserInfoAPI = (id) => {
  return http({
    url: `/user/user/${id}`,
    method: "GET",
  })
}

// 更新用户信息
export const updateUserAPI = (params) => {
  return http({
    url: "/user/user",
    method: "PUT",
    data: params,
  })
}
