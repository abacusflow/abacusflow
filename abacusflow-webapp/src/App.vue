<template>
  <a-config-provider :locale="zhCN">
    <FrameWork></FrameWork>
  </a-config-provider>
</template>

<script setup lang="ts">
import zhCN from "ant-design-vue/es/locale/zh_CN";

import FrameWork from "./layouts/FrameWork.vue";
</script>

<style scoped></style>
