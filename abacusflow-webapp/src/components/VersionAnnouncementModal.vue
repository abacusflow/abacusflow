<template>
  <a-modal v-model:open="visible" title="📢 版本更新公告" :closable="false" :maskClosable="false">
    <template #footer>
      <a-button type="primary" :disabled="countdown > 0" @click="handleClose">
        {{ countdown > 0 ? `请阅读 ${countdown}s` : "我已知晓" }}
      </a-button>
    </template>

    <div style="max-height: 300px; overflow-y: auto">
      <p>
        🎉 欢迎使用 <strong>v{{ CURRENT_VERSION }}</strong
        >，本次更新内容如下：
      </p>
      <ul>
        <li>✅ 客户支持查看最近订单时间</li>
        <li>✅ 客户页面聚合订单金额、热销商品</li>
        <li>⚠️ 修复部分状态错误问题</li>
      </ul>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { CURRENT_VERSION } from "@/constants/version";
import { markAnnouncementAsRead, shouldShowAnnouncement } from "@/util/version";
import { onMounted, ref } from "vue";

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
