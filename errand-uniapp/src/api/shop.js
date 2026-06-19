import { http } from "@/utils/http"

/**
 * 查询店铺营业状态
 */
export const getStatusAPI = () => {
  return http({
    method: "GET",
    url: "/user/shop/status",
  })
}
