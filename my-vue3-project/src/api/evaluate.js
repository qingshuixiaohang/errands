import { http } from "@/utils/http"

/**
 * 提交评价
 */
export const submitEvaluateAPI = (params) => {
  return http({
    url: "/evaluate/submit",
    method: "POST",
    data: params,
  })
}

/**
 * 获取评价列表
 */
export const getEvaluateListAPI = (params) => {
  return http({
    url: "/evaluate/list",
    method: "GET",
    data: params,
  })
}

/**
 * 获取店铺评价统计
 */
export const getStoreStatsAPI = (storeId) => {
  return http({
    url: `/evaluate/stats/${storeId}`,
    method: "GET",
  })
}
