import type { App } from "vue";
import {
  Configuration,
  DepotApi,
  InventoryApi,
  type Middleware,
  PartnerApi,
  ProductApi,
  TransactionApi,
  UserApi
} from "../core/openapi";
import { notification } from "ant-design-vue";

export default {
  install: (app: App) => {
    // 使用提供的配置初始化 UserApi
    const userApi = new UserApi(config);
    const productApi = new ProductApi(config);
    const depotApi = new DepotApi(config);
    const inventoryApi = new InventoryApi(config);
    const transactionApi = new TransactionApi(config);
    const partnerApi = new PartnerApi(config);

    // 提供 API 实例
    app.provide("userApi", userApi);
    app.provide("productApi", productApi);
    app.provide("depotApi", depotApi);
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
  post: async ({ response }) => {
    if (!response.ok) {
      if (response.status === 401) {
        const redirectUrl = document.location.pathname;
        window.location.href = `/login?redirect=${encodeURIComponent(redirectUrl)}`;
        return response;
      }

      const error = await response.json();
      // message.error(error.message || "接口异常");
      notification.error({
        message: "请求出错",
        description: error.message || "接口异常",
        duration: 30
      });
    }

    return response;
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
