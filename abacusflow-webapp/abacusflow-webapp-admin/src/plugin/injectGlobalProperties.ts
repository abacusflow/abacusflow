// injectGlobalProperties.ts
import type { App } from "vue";

// 定义工具函数的键值对类型
// eslint-disable-next-line @typescript-eslint/no-explicit-any
type UtilsMap = Record<string, (...args: any[]) => unknown>;

export default {
  install: (app: App, utils: UtilsMap) => {
    // 遍历传入的 utils 并挂载到 globalProperties 上
    for (const [key, fn] of Object.entries(utils)) {
      if (typeof fn === "function") {
        app.config.globalProperties[`$${key}`] = fn;
      } else {
        console.warn(`[Plugin Warning] ${key} is not a function and was not injected.`);
      }
    }
  }
};
