import {createRouter, createWebHistory} from 'vue-router'
import FrameWork from '@/layouts/FrameWork.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/dashboard',
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/dashboard/AnalysisView.vue'),
    },
    {
      path: '/user',
      name: 'user',
      component: () => import('@/views/user/UserView.vue'),
    },
    {
      path: '/product',
      name: 'product',
      component: () => import('@/views/product/ProductListView.vue'),
    },
    {
      path: '/product/category',
      name: 'product-category',
      component: () => import('@/views/product/ProductCategoryView.vue'),
    },
    {
      path: '/warehouse',
      name: 'warehouse',
      component: () => import('@/views/warehouse/WarehouseView.vue'),
    },
    {
      path: '/inventory',
      name: 'inventory',
      component: () => import('@/views/inventory/InventoryView.vue'),
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundView.vue'),
    },
  ],
})

export default router
