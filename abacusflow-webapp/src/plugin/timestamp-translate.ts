import type { App } from "vue";

export default {
  install: (app: App) => {
    app.config.globalProperties.$formatDate = (timestamp: number) => {
      const date = new Date(timestamp);
      return date.toLocaleString("zh-CN");
    };
  }
};

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    $formatDate: (timestamp: number) => string;
  }
}
