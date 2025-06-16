export const formatDate = (timestamp?: number): string => {
  if (timestamp === undefined || timestamp === null) {
    return "";
  }
  const date = new Date(timestamp);
  return date.toLocaleString("zh-CN");
};

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    $formatDate: (timestamp: number) => string;
  }
}

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
