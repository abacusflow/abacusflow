import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import { VueQueryPlugin } from '@tanstack/vue-query'
import { ApiPlugin } from './core/plugin/api-plugin'
import { Configuration } from './core/openapi'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Antd)
app.use(VueQueryPlugin)
const config = new Configuration({ basePath: '/api/v1' })
app.use(ApiPlugin, config)

app.mount('#app')
