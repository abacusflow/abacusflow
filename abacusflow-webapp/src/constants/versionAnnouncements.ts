export interface VersionAnnouncement {
  version: string;
  date: string;
  content: string[];
}

export const ANNOUNCEMENTS: VersionAnnouncement[] = [
  {
    version: "0.0.2",
    date: "2025-07-02",
    content: [
      "🧾 <strong>库存打印</strong>支持列表式打印",
      "📊 <strong>销售 / 采购订单-详情</strong>订单明细列表展示"
    ]
  },
  {
    version: "0.0.1",
    date: "2025-06-30",
    content: [
      "🛡️ 产品删除前必须确保<strong>无关联订单</strong>，避免误删已交易商品",
      "🧾 新增销售单时支持<strong>当场添加客户</strong>，操作更便捷",
      "📊 客户 / 供应商列表页添加<strong>历史订单总结信息</strong>，助力销售判断",
      "📅 销售 / 采购订单支持按<strong>订单日期筛选</strong>，查找更灵活",
      "🔍 客户 / 供应商 / 产品 / 库存<strong>选择器</strong>现支持模糊搜索，查找更高效"
    ]
  }
];
