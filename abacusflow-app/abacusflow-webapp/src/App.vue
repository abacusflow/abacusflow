<template>
  <a-config-provider :locale="zhCN">
    <!-- 动态判断框架 -->
    <component :is="layoutComponent">
      <RouterView />
    </component>
  </a-config-provider>
</template>

<script setup lang="ts">
import zhCN from "ant-design-vue/es/locale/zh_CN";
import AdminFrameWork from "./layouts/AdminFrameWork.vue";
import BlankFrameWork from "./layouts/BlankFrameWork.vue";
import MobileFrameWork from "./layouts/MobileFrameWork.vue";
import { ref, onMounted, computed, type Component } from "vue";
import { useRoute } from "vue-router";
import { LAYOUTS, type LayoutType } from "./layouts/layouts";

const isMobile = ref(false);
const route = useRoute();

const layouts: Record<LayoutType, Component> = {
  [LAYOUTS.ADMIN]: AdminFrameWork,
  [LAYOUTS.BLANK]: BlankFrameWork
};

const layoutComponent = computed(() => {
  // 移动端优先
  if (isMobile.value) {
    return MobileFrameWork;
  }

  // 路由 meta 中指定的布局名，如果未指定，默认使用 AdminFrameWork
  const layoutName = (route.meta.layout || LAYOUTS.ADMIN) as LayoutType;
  return layouts[layoutName] || AdminFrameWork;
});

onMounted(() => {
  isMobile.value = window.innerWidth <= 768;
});
</script>

<style scoped></style>
