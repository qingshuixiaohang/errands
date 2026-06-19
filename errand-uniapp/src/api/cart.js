import { http } from "@/utils/http"

/**
 * 添加购物车
 */
export const addToCartAPI = (cartDTO) => {
  return http({
    method: "POST",
    url: "/user/cart/add",
    data: cartDTO,
  })
}

/**
 * 购物车商品数量-1
 */
export const subCartAPI = (cartDTO) => {
  return http({
    method: "PUT",
    url: "/user/cart/sub",
    data: cartDTO,
  })
}

/**
 * 查看当前购物车列表
 */
export const getCartAPI = () => {
  return http({
    method: "GET",
    url: "/user/cart/list",
  })
}

/**
 * 清空购物车
 */
export const cleanCartAPI = () => {
  return http({
    method: "DELETE",
    url: "/user/cart/clean",
  })
}
