import type { ProductType, ProductUnit } from "@/core/openapi";

/**
 * 将单位英文值翻译为中文
 */
export function translateUnit(unit?: ProductUnit): string {
  if (!unit) {
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

  return unitMap[unit] || unit;
}

/**
 * 将单位英文值翻译为中文
 */
export function translateType(type?: ProductType): string {
  if (!type) {
    return "";
  }
  const typeMap: Record<ProductType, string> = {
    material: "普通商品",
    asset: "资产"
  };

  return typeMap[type] || type;
}

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    $translateUnit: (unit: ProductUnit) => string;
  }
  interface ComponentCustomProperties {
    $translateType: (type: ProductType) => string;
  }
}
