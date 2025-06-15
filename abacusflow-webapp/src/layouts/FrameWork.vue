<template>
  <a-layout has-sider>
    <a-layout-sider
      :style="{ overflow: 'auto', height: '100vh', position: 'fixed', left: 0, top: 0, bottom: 0 }"
    >
      <div class="logo" />
      <a-menu
        v-model:selectedKeys="selectedKeys"
        theme="dark"
        mode="inline"
        @select="handleMenuSelect"
      >
        <a-menu-item key="/dashboard">
          <template #icon>
            <DashboardOutlined />
          </template>
          <span>仪表盘</span>
        </a-menu-item>
        <a-menu-item key="/user">
          <template #icon>
            <UserOutlined />
          </template>
          <span>用户管理</span>
        </a-menu-item>
        <a-sub-menu key="/product">
          <template #icon>
            <ShoppingOutlined />
          </template>
          <template #title>产品管理</template>
          <a-menu-item key="/product">产品列表</a-menu-item>
          <a-menu-item key="/product/category">产品分类</a-menu-item>
        </a-sub-menu>
        <a-menu-item key="/warehouse">
          <template #icon>
            <ShopOutlined />
          </template>
          <span>仓库管理</span>
        </a-menu-item>
        <a-menu-item key="/inventory">
          <template #icon>
            <InboxOutlined />
          </template>
          <span>库存管理</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout :style="{ marginLeft: '200px' }">
      <a-layout-header :style="{ background: '#fff', padding: 0 }" />
      <a-layout-content :style="{ margin: '24px 16px 0', overflow: 'initial' }">
        <RouterView />
      </a-layout-content>
      <a-layout-footer :style="{ textAlign: 'center' }"> BruWave ©2025 </a-layout-footer>
    </a-layout>
  </a-layout>
</template>
<script lang="ts" setup>
import { useRoute, useRouter } from "vue-router";
import { ref, watch } from "vue";
import {
  DashboardOutlined,
  UserOutlined,
  ShoppingOutlined,
  ShopOutlined,
  InboxOutlined
} from "@ant-design/icons-vue";

const route = useRoute();
const router = useRouter();
const selectedKeys = ref(["/"]);

watch(
  () => route.path,
  (newPath) => {
    selectedKeys.value = [newPath];
  }
);

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
