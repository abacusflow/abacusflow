<template>
  <a-layout has-sider>
    <a-layout-sider
      :style="{ overflow: 'auto', height: '100vh', position: 'fixed', left: 0, top: 0, bottom: 0 }"
    >
      <div class="logo" />
      <a-menu
        v-model:selectedKeys="selectedKeys"
        v-model:openKeys="openKeys"
        theme="dark"
        mode="inline"
        :items="menuItems"
        @select="handleMenuSelect"
      >
      </a-menu>
    </a-layout-sider>
    <a-layout :style="{ marginLeft: '200px' }">
      <a-layout-header :style="{ background: '#fff', padding: 0 }" />
      <a-layout-content :style="{ margin: '24px 16px 0', overflow: 'initial' }">
        <slot></slot>
      </a-layout-content>
      <a-layout-footer :style="{ textAlign: 'center' }">
        <div>BruWave ©2025</div>
      </a-layout-footer>
    </a-layout>
  </a-layout>
  <VersionAnnouncementModal />
</template>
<script lang="ts" setup>
import { type RouteRecordRaw, useRoute, useRouter } from "vue-router";
import { capitalize, h, ref, type VNode, watch } from "vue";
import * as Icons from "@ant-design/icons-vue"; // 用于动态渲染图标
import { type ItemType } from "ant-design-vue";
import VersionAnnouncementModal from "@/components/VersionAnnouncementModal.vue";

const route = useRoute();
const router = useRouter();
const selectedKeys = ref(["/"]);
const openKeys = ref<string[]>([]);

watch(
  () => route.path,
  (newPath) => {
    selectedKeys.value = [newPath];
  }
);

/**
 * 获取图标组件
 */
function renderIcon(iconName?: string): VNode | undefined {
  if (!iconName) return undefined;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const iconStr = (Icons as any)[capitalize(iconName) + "Outlined"];
  return iconStr ? h(iconStr) : undefined;
}

/**
 * 递归生成菜单项
 */

function generateMenuItems(routes: readonly RouteRecordRaw[], parentPath = ""): ItemType[] {
  return routes
    .filter((r) => r.meta?.title && !r.meta?.hidden)
    .map((route) => {
      const fullPath = route.path
        ? route.path.startsWith("/")
          ? route.path
          : `${parentPath}/${route.path}`.replace(/\/+/g, "/")
        : parentPath;

      const visibleChildren = route.children?.filter((child) => !child.meta?.hidden);

      if (Array.isArray(visibleChildren) && visibleChildren.length > 0) {
        // 设置submenu展开
        openKeys.value.push(route.path);

        return {
          key: fullPath,
          label: route.meta?.title ?? route.name,
          icon: renderIcon(route.meta?.icon as string),
          children: generateMenuItems(route.children, fullPath)
        };
      }

      return {
        key: fullPath,
        icon: renderIcon(route.meta?.icon as string),
        label: route.meta?.title
      };
    });
}

/**
 * 菜单节点（计算属性）
 */
const menuItems = generateMenuItems(router.options.routes);

const handleMenuSelect = ({ key }: { key: string }) => {
  // 只有当前路径不一致时才跳转
  if (route.path !== key) {
    router.push(key).catch((err: Error) => {
      if (err.name !== "NavigationDuplicated") {
        console.error("路由跳转失败:", err);
      }
    });
  }
};
</script>
<style scoped>
#components-layout-demo-fixed-sider .logo {
  height: 32px;
  background: rgba(255, 255, 255, 0.2);
  margin: 16px;
}
.site-layout .site-layout-background {
  background: #fff;
}

[data-theme="dark"] .site-layout .site-layout-background {
  background: #141414;
}
</style>
