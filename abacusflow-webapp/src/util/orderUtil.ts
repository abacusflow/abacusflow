import type {OrderStatus} from "@/core/openapi";

/**
 * 将单位英文值翻译为中文
 */
export function translateOrderStatus(input?: OrderStatus): string {
  if (!input) {
    return "";
  }
  const typeMap: Record<OrderStatus, string> = {
    pending: "进行中",
    completed: "已完成",
    canceled: "已取消",
    reversed: "已撤回"
  };

  return typeMap[input] || input;
}

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    $translateOrderStatus: (unit: OrderStatus) => string;
  }
}
