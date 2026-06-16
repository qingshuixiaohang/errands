import { http } from "@/utils/http"

/**
 * 根据菜品分类id获取菜品列表
 */
export const getDishListAPI = (id) => {
  return http({
    method: "GET",
    url: `/user/dish/list/${id}`,
  })
}

/**
 * 根据id获取菜品详情
 */
export const getDishByIdAPI = (id) => {
  return http({
    method: "GET",
    url: `/user/dish/dish/${id}`,
  })
}
