import request from '@/utils/request'

/** 骑手列表 */
export const getRiderListAPI = () => {
  return request({ url: '/rider/list', method: 'GET' })
}

/** 骑手统计 */
export const getRiderStatsAPI = () => {
  return request({ url: '/rider/stats', method: 'GET' })
}

/** 某个骑手的订单 */
export const getRiderOrdersAPI = (riderId: number) => {
  return request({ url: `/rider/${riderId}/orders`, method: 'GET' })
}

/** 派单记录 */
export const getRiderLogsAPI = () => {
  return request({ url: '/rider/logs', method: 'GET' })
}

/** 骑手排班 */
export const getRiderScheduleAPI = (riderId: number) => {
  return request({ url: `/user/rider/schedule/list/${riderId}`, method: 'GET' })
}

/** 取消骑手 */
export const removeRiderAPI = (riderId: number) => {
  return request({ url: `/rider/${riderId}/removeRider`, method: 'PUT' })
}
