import type { ProductUnit, ProductType } from "@/core/openapi";

/**
 * 将单位英文值翻译为中文
 */
export function translateProductUnit(input?: ProductUnit): string {
  if (!input) {
    return "";
  }
  const unitMap: Record<ProductUnit, string> = {
    item: "个",
    piece: "件",
    box: "箱",
    pack: "包",
    dozen: "打",
    pair: "对",
    gram: "克",
    kilogram: "千克",
    liter: "升",
    milliliter: "毫升",
    meter: "米",
    centimeter: "厘米",
    bottle: "瓶",
    barrel: "桶",
    bag: "袋",
    sheet: "张",
    roll: "卷"
  };

  return unitMap[input] || input;
}

/**
 * 将单位英文值翻译为中文
 */
export function translateProductType(input?: ProductType): string {
  if (!input) {
    return "";
  }
  const typeMap: Record<ProductType, string> = {
    material: "普通商品",
    asset: "资产"
  };

  return typeMap[input] || input;
}

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    $translateProductUnit: (input: ProductUnit) => string;
    $translateProductType: (input: ProductType) => string;
  }
}
