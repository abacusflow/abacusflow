import dayjs from "dayjs";

export const timestampToLocaleString = (timestamp?: number): string => {
  if (timestamp === undefined || timestamp === null) {
    return "";
  }
  const date = new Date(timestamp);
  return date.toLocaleString("zh-CN");
};

export const dateToFormattedString = (
  date?: Date | string | null,
  format = "YYYY-MM-DD"
): string => {
  if (!date) return "";
  return dayjs(date).format(format);
};

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    $dateToFormattedString: (date: Date | string | null, format?: string) => string;
    $timestampToLocaleString: (timestamp: number) => string;
  }
}
