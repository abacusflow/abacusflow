<template>
  <a-layout has-sider class="layout-root">
    <a-layout-sider
      id="layout-sider"
      class="layout-sider"
      v-model:collapsed="collapsed"
      collapsible
      :trigger="null"
      breakpoint="lg"
    >
      <div class="logo">
        <img src="@/assets/logo-simple.svg" alt="Logo" />
        <transition name="fade">
          <span v-if="!collapsed" class="logo-text">小算盘</span>
        </transition>
      </div>
      <a-menu
        v-model:selectedKeys="selectedKeys"
        v-model:openKeys="openKeys"
        theme="light"
        mode="inline"
        :items="menuItems"
        @select="handleMenuSelect"
      />
    </a-layout-sider>

    <a-layout class="content-wrapper" :style="{ marginLeft: layoutMarginLeft }">
      <a-layout-header
        id="layout-header-trigger"
        class="layout-header"
        :style="{ left: layoutMarginLeft }"
      >
        <!-- 左侧：折叠按钮 + 系统标题 -->
        <a-space size="middle" align="center" class="header-left">
          <menu-unfold-outlined
            v-if="collapsed"
            class="trigger"
            @click="() => (collapsed = !collapsed)"
          />
          <menu-fold-outlined v-else class="trigger" @click="() => (collapsed = !collapsed)" />
        </a-space>

        <!-- 右侧：通知、用户菜单 -->
        <a-space size="middle" align="center" class="header-right">
          <!-- 消息通知组件 -->
          <MessageOutlined />

          <!-- 用户下拉菜单 -->
          <a-dropdown placement="bottomRight">
            <span>
              <user-outlined style="margin-right: 4px" />
              <span style="font-size: 14px; margin-right: 4px">超级管理员</span>
              <down-outlined style="font-size: 12px" />
            </span>
            <template #overlay>
              <a-menu>
                <a-menu-item key="account">
                  <router-link to="/user/my-account"> <user-outlined /> 我的账户 </router-link>
                </a-menu-item>
                <a-menu-item key="change-password"> <key-outlined /> 修改密码 </a-menu-item>
                <a-menu-item key="logout"> <logout-outlined /> 退出登录 </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </a-space>
      </a-layout-header>

      <a-layout-content class="layout-content">
        <slot />
      </a-layout-content>

      <a-layout-footer class="layout-footer"> abacusflow ©2025 </a-layout-footer>
    </a-layout>
  </a-layout>

  <VersionAnnouncementModal />
</template>

<script lang="ts" setup>
import { type RouteRecordRaw, useRoute, useRouter } from "vue-router";
import { capitalize, computed, h, ref, type VNode, watch } from "vue";
import * as Icons from "@ant-design/icons-vue"; // 用于动态渲染图标
import {
  KeyOutlined,
  LogoutOutlined,
  MessageOutlined,
  UserOutlined,
  DownOutlined,
  MenuUnfoldOutlined,
  MenuFoldOutlined
} from "@ant-design/icons-vue";
import { type ItemType } from "ant-design-vue";
import VersionAnnouncementModal from "@/components/VersionAnnouncementModal.vue";

const route = useRoute();
const router = useRouter();
const selectedKeys = ref(["/"]);
const openKeys = ref<string[]>([]);
const collapsed = ref<boolean>(false);
const layoutMarginLeft = computed(() => (collapsed.value ? "80px" : "200px"));

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
          title: route.meta?.title ?? route.name,
          icon: renderIcon(route.meta?.icon as string),
          children: generateMenuItems(visibleChildren, fullPath)
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
/* 🌐 根布局结构（可用于调试） */
.layout-root {
  min-height: 100vh;
}

/* 🧱 侧边栏固定样式 */
.layout-sider {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  height: 100vh;
  overflow: auto;
  background-color: #ebedef; /* 比纯白更有层次感 */
  z-index: 101; /* 保证在 header 下方 */
}

/* 🔖 logo 容器样式 */
.logo {
  height: 48px;
  margin: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 8px;
  border-radius: 8px;
  padding: 0 12px;
}

/* 🖼️ logo 图标 */
.logo img {
  height: 32px;
}

/* ✏️ logo 文本 */
.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  white-space: nowrap;
}

/* 🧭 顶部 header 样式 */
.layout-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  height: 64px;
  background-color: #fdfdfc; /* 比纯白更有层次感 */
  padding: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: left 0.2s ease;
}

/* 🎛️ 所有 Header 图标样式（包括 trigger、user、down 等） */
.layout-header :deep(.anticon) {
  font-size: 18px;
  line-height: 64px;
  cursor: pointer;
  transition: color 0.3s;
}

.layout-header :deep(.anticon:hover) {
  color: #1890ff;
}

.header-left {
  padding: 0 24px;
}

.header-right {
  margin-right: 16px;
}
/* 📦 主要内容区域布局 */
.content-wrapper {
  transition: margin-left 0.2s ease;
  margin-left: 200px;
}

/* 📄 内容区域样式（padding-top 避让 header） */
.layout-content {
  padding: 64px 24px 24px; /* 64px header 高度 */
  background: #fff;
  min-height: 280px;
  overflow: initial;
}

/* 🧾 底部样式 */
.layout-footer {
  text-align: center;
  background: transparent;
  padding: 16px 0;
}

/* 🎨 菜单背景统一风格 */
.ant-menu-light {
  background-color: #ebedef;
}

/* 🌈 logo 文字过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
