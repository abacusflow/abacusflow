<template>
  <a-modal v-model:open="visible" title="📢 版本更新公告" :closable="false" :maskClosable="false">
    <template #footer>
      <a-button type="primary" :disabled="countdown > 0" @click="handleClose">
        {{ countdown > 0 ? `请阅读 ${countdown}s` : "我已知晓" }}
      </a-button>
    </template>

    <template #default>
      <div v-for="item in ANNOUNCEMENTS" :key="item.version">
        <h3>📌 v{{ item.version }}（{{ item.date }}）</h3>
        <ul>
          <li v-for="line in item.content" :key="line" v-html="line"></li>
        </ul>
      </div>
    </template>
  </a-modal>
</template>

<script setup lang="ts">
import { VersionAnnouncement } from "@/constants/version";
import { markAnnouncementAsRead, shouldShowAnnouncement } from "@/util/version";
import { onMounted, ref } from "vue";

const ANNOUNCEMENTS: VersionAnnouncement[] = [
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

const visible = ref(false);
const countdown = ref(10);

function handleClose() {
  markAnnouncementAsRead();
  visible.value = false;
}

onMounted(() => {
  if (shouldShowAnnouncement()) {
    visible.value = true;
    const timer = setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0) clearInterval(timer);
    }, 1000);
  }
});
</script>
