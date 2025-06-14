import { User } from './../core/openapi/models/user'
import { createRouter, createWebHistory } from 'vue-router'
import ProductEditView from '@/views/product/ProductEditView.vue'
import ProductAddView from '@/views/product/ProductCreateView.vue'

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
      component: () => import('@/views/user/UserList.vue'),
      children: [
        {
          path: 'add',
          name: 'user-add',
          component: UserAddView,
        },
        {
          path: 'edit/:id',
          name: 'user-edit',
          component: UserEditView,
        },
      ],
    },
    {
      path: '/product',
      name: 'product',
      component: () => import('@/views/product/ProductListView.vue'),
      children: [
        {
          path: 'add',
          name: 'product-create',
          component: ProductAddView,
        },
        {
          path: 'edit/:id',
          name: 'product-edit',
          component: ProductEditView,
        },
      ],
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
