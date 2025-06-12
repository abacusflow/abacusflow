import type { App } from 'vue'
import { Configuration, UserApi } from '../core/openapi'

export default {
  install: (app: App, config: Configuration) => {
    // 使用提供的配置初始化 UserApi
    const userApi = new UserApi(config)

    // 提供 `UserApi` 实例
    app.provide('userApi', userApi)
  },
}
