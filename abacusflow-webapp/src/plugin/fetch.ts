import type { App } from "vue";
import {
  Configuration,
  InventoryApi,
  PartnerApi,
  ProductApi,
  TransactionApi,
  UserApi,
  WarehouseApi,
  type Middleware
} from "../core/openapi";

export default {
  install: (app: App) => {
    // 使用提供的配置初始化 UserApi
    const userApi = new UserApi(config);
    const productApi = new ProductApi(config);
    const warehouseApi = new WarehouseApi(config);
    const inventoryApi = new InventoryApi(config);
    const transactionApi = new TransactionApi(config);
    const partnerApi = new PartnerApi(config);

    // 提供 API 实例
    app.provide("userApi", userApi);
    app.provide("productApi", productApi);
    app.provide("warehouseApi", warehouseApi);
    app.provide("inventoryApi", inventoryApi);
    app.provide("transactionApi", transactionApi);
    app.provide("partnerApi", partnerApi);
  }
};

const authMiddleware: Middleware = {
  pre: async (context) => {
    const { url, init } = context;

    const headers = new Headers(init?.headers || {});
    headers.set("X-Requested-With", "XMLHttpRequest");

    return {
      url,
      init: {
        ...init,
        headers
      }
    };
  },
  post: async (context) => {
    if (context.response.status === 401) {
      alert("身份认证已过期，请重新登录");
      const redirectUrl = document.location.pathname;
      window.location.href = `/login?redirect=${encodeURIComponent(redirectUrl)}`;
      return context.response;
    }
    return;
  },

  onError: async (context) => {
    console.error("请求发生错误:", context.error);
    return;
  }
};
const config = new Configuration({
  basePath: `${window.location.origin}/api`,
  middleware: [authMiddleware]
});
