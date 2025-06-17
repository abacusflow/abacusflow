import type {App} from "vue";
import {
  Configuration,
  InventoryApi,
  PartnerApi,
  ProductApi,
  TransactionApi,
  UserApi,
  WarehouseApi
} from "../core/openapi";

export default {
  install: (app: App, config: Configuration) => {
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
