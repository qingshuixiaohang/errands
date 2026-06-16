import { http } from "@/utils/http"

// 用户下单
export const submitOrderAPI = (params) => {
  return http({
    url: "/user/order/submit",
    method: "POST",
    data: params,
  })
}

// 支付订单
export const payOrderAPI = (params) => {
  return http({
    url: "/user/order/payment",
    method: "PUT",
    data: params,
  })
}

// 未支付订单数量
export const getUnPayOrderAPI = () => {
  return http({
    url: "/user/order/unPayOrderCount",
    method: "GET",
  })
}

// 根据订单id获取订单详情
export const getOrderAPI = (id) => {
  return http({
    url: `/user/order/orderDetail/${id}`,
    method: "GET",
  })
}

// 查询历史订单
export const getOrderPageAPI = (params) => {
  return http({
    url: "/user/order/historyOrders",
    method: "GET",
    data: params,
  })
}

// 取消订单
export const cancelOrderAPI = (id) => {
  return http({
    url: `/user/order/cancel/${id}`,
    method: "PUT",
  })
}

// 再来一单
export const reOrderAPI = (id) => {
  return http({
    url: `/user/order/reOrder/${id}`,
    method: "POST",
  })
}

// 催单
export const urgeOrderAPI = (id) => {
  return http({
    url: `/user/order/reminder/${id}`,
    method: "GET",
  })
}

// ===== 骑手端 API =====

// 骑手接单
export const riderAcceptAPI = (id) => {
  return http({
    url: `/user/rider/accept/${id}`,
    method: "PUT",
  })
}

// 骑手开始配送
export const riderDeliveryAPI = (id) => {
  return http({
    url: `/user/rider/delivery/${id}`,
    method: "PUT",
  })
}

// 骑手送达完成
export const riderCompleteAPI = (id) => {
  return http({
    url: `/user/rider/complete/${id}`,
    method: "PUT",
  })
}

// 骑手查看所有订单（不按用户过滤）
export const getRiderOrdersAPI = (params) => {
  // 手动拼接查询参数，避免 uni.request 的 data 在 GET 请求中不可用
  const query = Object.keys(params)
    .filter((k) => params[k] !== undefined && params[k] !== null)
    .map((k) => encodeURIComponent(k) + "=" + encodeURIComponent(params[k]))
    .join("&")
  const url = "/user/rider/orders" + (query ? "?" + query : "")
  return http({
    url,
    method: "GET",
  })
}
