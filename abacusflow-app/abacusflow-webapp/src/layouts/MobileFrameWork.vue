<template>
  <div class="mobile-framework">
    <!-- 顶部导航栏 -->
    <div class="mobile-header" v-if="showHeader">
      <div class="header-left">
        <a-button type="text" size="large" @click="handleBack" v-if="showBackButton">
          <template #icon>
            <LeftOutlined />
          </template>
        </a-button>
      </div>

      <div class="header-center">
        <span class="header-title">{{ title }}</span>
      </div>

      <div class="header-right">
        <slot name="header-right"></slot>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div
      class="mobile-content"
      :class="{
        'with-header': showHeader,
        'with-footer': showFooter,
        'safe-area': enableSafeArea
      }"
    >
      <slot></slot>
    </div>

    <!-- 底部导航栏 -->
    <!-- <div class="mobile-footer" v-if="showFooter">
      <a-tabbar
        v-model:activeKey="activeTab"
        @change="handleTabChange"
      >
        <a-tabbar-item
          v-for="item in footerTabs"
          :key="item.key"
          :title="item.title"
        >
          <template #icon>
            <component :is="item.icon" />
          </template>
        </a-tabbar-item>
      </a-tabbar>
    </div> -->

    <!-- 全局loading -->
    <div class="mobile-loading" v-if="loading">
      <a-spin size="large" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { LeftOutlined } from "@ant-design/icons-vue";

interface TabItem {
  key: string;
  title: string;
  icon: unknown;
  path: string;
}

interface Props {
  title?: string;
  showHeader?: boolean;
  showFooter?: boolean;
  showBackButton?: boolean;
  enableSafeArea?: boolean;
  loading?: boolean;
  footerTabs?: TabItem[];
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const props = withDefaults(defineProps<Props>(), {
  title: "小算盘移动端",
  showHeader: true,
  showFooter: true,
  showBackButton: true,
  enableSafeArea: true,
  loading: false
  // footerTabs: () => [
  //   {
  //     key: 'home',
  //     title: '首页',
  //     icon: HomeOutlined,
  //     path: '/home'
  //   },
  //   {
  //     key: 'category',
  //     title: '分类',
  //     icon: ShoppingCartOutlined,
  //     path: '/category'
  //   },
  //   {
  //     key: 'favorite',
  //     title: '收藏',
  //     icon: HeartOutlined,
  //     path: '/favorite'
  //   },
  //   {
  //     key: 'profile',
  //     title: '我的',
  //     icon: UserOutlined,
  //     path: '/profile'
  //   }
  // ]
});

const router = useRouter();
// const route = useRoute();

// 当前激活的tab
// const activeTab = ref('home');

// // 根据当前路由设置激活tab
// const setActiveTabByRoute = () => {
//   const currentTab = props.footerTabs.find(tab =>
//     route.path.startsWith(tab.path)
//   );
//   if (currentTab) {
//     activeTab.value = currentTab.key;
//   }
// };

// 处理返回按钮
const handleBack = () => {
  if (window.history.length > 1) {
    router.go(-1);
  } else {
    router.push("/");
  }
};

// // 处理tab切换
// const handleTabChange = (key: string) => {
//   const tab = props.footerTabs.find(item => item.key === key);
//   if (tab) {
//     router.push(tab.path);
//   }
// };

// 监听路由变化
onMounted(() => {
  // setActiveTabByRoute();

  // 设置viewport meta标签
  const viewport = document.querySelector('meta[name="viewport"]');
  if (viewport) {
    viewport.setAttribute(
      "content",
      "width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"
    );
  }

  // 禁用双指缩放
  document.addEventListener("gesturestart", handleGestureStart, { passive: false });
  document.addEventListener("touchstart", handleTouchStart, { passive: false });
});

onUnmounted(() => {
  document.removeEventListener("gesturestart", handleGestureStart);
  document.removeEventListener("touchstart", handleTouchStart);
});

// 禁用手势缩放
const handleGestureStart = (e: Event) => {
  e.preventDefault();
};

// 禁用双击缩放
const handleTouchStart = (e: TouchEvent) => {
  if (e.touches.length > 1) {
    e.preventDefault();
  }
};

// // 监听路由变化更新activeTab
// router.afterEach(() => {
//   setActiveTabByRoute();
// });
</script>

<style scoped>
.mobile-framework {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
  position: relative;
  overflow: hidden;
}

/* 顶部导航栏 */
.mobile-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 44px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  position: relative;
  z-index: 1000;
  padding: 0 16px;
}

.header-left,
.header-right {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
}

.header-center {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 主要内容区域 */
.mobile-content {
  flex: 1;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  position: relative;
}

.mobile-content.with-header {
  padding-top: 0;
}

.mobile-content.with-footer {
  padding-bottom: 0;
}

.mobile-content.safe-area {
  padding-bottom: env(safe-area-inset-bottom);
}

/* 底部导航栏 */
.mobile-footer {
  background: #fff;
  border-top: 1px solid #e8e8e8;
  padding-bottom: env(safe-area-inset-bottom);
}

/* 全局loading */
.mobile-loading {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .mobile-framework {
    font-size: 14px;
  }

  .header-title {
    font-size: 16px;
  }
}

/* 安全区域适配 */
@supports (padding: max(0px)) {
  .mobile-header {
    padding-top: max(8px, env(safe-area-inset-top));
  }

  .mobile-footer {
    padding-bottom: max(8px, env(safe-area-inset-bottom));
  }
}

/* 滚动条样式 */
.mobile-content::-webkit-scrollbar {
  width: 0;
  background: transparent;
}

/* 去除点击高亮 */
* {
  -webkit-tap-highlight-color: transparent;
  -webkit-touch-callout: none;
  -webkit-user-select: none;
  user-select: none;
}

/* 输入框可选择 */
input,
textarea {
  -webkit-user-select: auto;
  user-select: auto;
}
</style>
