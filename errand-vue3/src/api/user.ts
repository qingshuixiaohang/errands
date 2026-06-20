import request from '@/utils/request'

/** 用户列表 */
export const getUserListAPI = (params?: { page?: number; pageSize?: number; name?: string; phone?: string }) => {
  return request({ url: '/user/list', method: 'GET', params: params })
}

/** 某个用户的订单 */
export const getUserOrdersAPI = (userId: number) => {
  return request({ url: `/user/${userId}/orders`, method: 'GET' })
}

/** 设为骑手 */
export const setRiderAPI = (userId: number) => {
  return request({ url: `/user/${userId}/setRider`, method: 'PUT' })
}

/** 取消骑手 */
export const removeRiderAPI = (userId: number) => {
  return request({ url: `/user/${userId}/removeRider`, method: 'PUT' })
}
