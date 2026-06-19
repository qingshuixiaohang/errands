import { createSSRApp } from "vue"
import App from "./App.vue"
import { createPinia } from "pinia"

// 注册 HTTP 拦截器（只需引入一次）
import "@/utils/http"

export function createApp() {
  const app = createSSRApp(App)
  app.use(createPinia())
  return { app }
}
