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

/**
 * 判断某个时间是否在指定天数之内（未超时）
 * @param date ISO 字符串或 Date 对象（如 createdAt）
 * @param days 最大允许的天数（例如 7）
 * @returns boolean 是否在 days 天内
 */
export function isWithinDays(date: string | Date, days: number): boolean {
  return dayjs().diff(dayjs(date), "day") <= days;
}

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    $dateToFormattedString: (date: Date | string | null, format?: string) => string;
    $timestampToLocaleString: (timestamp: number) => string;
    $isWithinDays: (date: string | Date, days: number) => boolean;
  }
}
