<template>
  <a-config-provider :locale="zhCN">
    <!-- 后台管理框架 -->
    <AdminFrameWork v-if="!isMobile">
      <RouterView />
    </AdminFrameWork>

    <!-- H5 移动端框架 -->
    <MobileFrameWork v-else>
      <RouterView />
    </MobileFrameWork>
  </a-config-provider>
</template>

<script setup lang="ts">
import zhCN from "ant-design-vue/es/locale/zh_CN";
import AdminFrameWork from "./layouts/AdminFrameWork.vue";
import MobileFrameWork from "./layouts/MobileFrameWork.vue";
import { ref, onMounted } from "vue";

const isMobile = ref(false);

onMounted(() => {
  isMobile.value = window.innerWidth <= 768;
});
</script>

<style scoped></style>
