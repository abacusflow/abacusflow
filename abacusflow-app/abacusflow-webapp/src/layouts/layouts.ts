export const LAYOUTS = {
  ADMIN: "AdminLayout",
  BLANK: "BlankFrameWork"
} as const; // as const 将其变为只读元组
export type LayoutType = (typeof LAYOUTS)[keyof typeof LAYOUTS];
