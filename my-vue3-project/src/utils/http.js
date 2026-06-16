import { useUserStore } from "@/stores/user"

// 请求基地址（真机调试请改为电脑局域网IP）
const baseURL = "http://10.16.195.130:8081"

// 拦截器配置
const httpInterceptor = {
  invoke(options) {
    // 1. 非 http 开头需拼接地址
    if (!options.url.startsWith("http")) {
      options.url = baseURL + options.url
    }
    // 2. 请求超时
    options.timeout = 10000
    // 3. 添加小程序端请求头标识
    options.header = {
      "source-client": "miniapp",
      ...options.header,
    }
    // 4. 添加 token 请求头标识
    const userStore = useUserStore()
    const token = userStore.profile?.token
    if (token) {
      options.header.Authorization = token
    }
  },
}

// 拦截 request 请求
uni.addInterceptor("request", httpInterceptor)

// 封装 http 请求函数
export const http = (options) => {
  return new Promise((resolve, reject) => {
    uni.request({
      ...options,
      // 响应成功
      success(res) {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          // 获取数据成功，返回 res.data
          resolve(res.data)
        } else if (res.statusCode === 401) {
          // 401 错误，token 过期
          const userStore = useUserStore()
          userStore.clearProfile()
          uni.navigateTo({ url: "/pages/login/index" })
          reject(res)
        } else {
          // 通用错误
          uni.showToast({
            title: res.data?.msg || "请求失败",
            icon: "none",
          })
          reject(res)
        }
      },
      // 响应失败
      fail(err) {
        uni.showToast({
          title: "网络开小差了",
          icon: "none",
        })
        reject(err)
      },
    })
  })
}
