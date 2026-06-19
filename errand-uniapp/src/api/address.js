import { http } from "@/utils/http"

/**
 * 新增地址
 */
export const addAddressAPI = (address) => {
  return http({
    method: "POST",
    url: "/user/address",
    data: address,
  })
}

/**
 * 查询默认地址
 */
export const getDefaultAddressAPI = () => {
  return http({
    method: "GET",
    url: "/user/address/default",
  })
}

/**
 * 查看当前用户的地址列表
 */
export const getAddressListAPI = () => {
  return http({
    method: "GET",
    url: "/user/address/list",
  })
}

/**
 * 根据id查询地址
 */
export const getAddressByIdAPI = (id) => {
  return http({
    method: "GET",
    url: `/user/address/${id}`,
  })
}

/**
 * 修改地址
 */
export const updateAddressAPI = (address) => {
  return http({
    method: "PUT",
    url: "/user/address",
    data: address,
  })
}

/**
 * 修改默认地址
 */
export const updateDefaultAddressAPI = (address) => {
  return http({
    method: "PUT",
    url: "/user/address/default",
    data: address,
  })
}

/**
 * 删除地址
 */
export const deleteAddressAPI = (id) => {
  return http({
    method: "DELETE",
    url: `/user/address/${id}`,
  })
}
