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
import { ANNOUNCEMENTS } from "@/constants/versionAnnouncements";
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
