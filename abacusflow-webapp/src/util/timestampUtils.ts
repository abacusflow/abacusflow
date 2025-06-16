export const formatDate = (timestamp: number): string => {
  const date = new Date(timestamp);
  return date.toLocaleString("zh-CN");
};

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    $formatDate: (timestamp: number) => string;
  }
}

export const formatNumber = (num: number): string => {
  return num.toLocaleString("zh-CN");
};

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    $formatNumber: (timestamp: number) => string;
  }
}
