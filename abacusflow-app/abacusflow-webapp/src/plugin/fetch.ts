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
import authService from "../services/auth";

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

    try {
      const isAuthenticated = await authService.isAuthenticated();
      if (isAuthenticated) {
        const accessToken = await authService.getAccessToken();
        headers.set("Authorization", `Bearer ${accessToken}`);
      }
    } catch (error) {
      console.warn("Failed to add auth token to request:", error);
    }

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
        try {
          await authService.login();
        } catch (error) {
          console.error("Failed to redirect to login:", error);
          window.location.href = "/";
        }
        return response;
      }

      const error = await response.json();
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
