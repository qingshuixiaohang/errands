import { http } from "@/utils/http"

/**
 * 根据套餐分类id获取套餐列表
 */
export const getSetmealListAPI = (id) => {
  return http({
    method: "GET",
    url: `/user/setmeal/list/${id}`,
  })
}

/**
 * 获取套餐详情
 */
export const getSetmealAPI = (id) => {
  return http({
    method: "GET",
    url: `/user/setmeal/${id}`,
  })
}
