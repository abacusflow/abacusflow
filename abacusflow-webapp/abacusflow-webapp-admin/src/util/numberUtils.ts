export const formatNumber = (num?: number): string => {
  if (num === undefined || num === null) {
    return "";
  }
  return num.toLocaleString("zh-CN");
};

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    $formatNumber: (num: number) => string;
  }
}
