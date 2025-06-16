import { createRouter, createWebHistory } from "vue-router";
import ProductEditView from "@/views/product/ProductEditView.vue";
import ProductAddView from "@/views/product/ProductAddView.vue";
import UserAddView from "@/views/user/UserAddView.vue";
import UserEditView from "@/views/user/UserEditView.vue";
import WarehouseAddView from "@/views/warehouse/WarehouseAddView.vue";
import WarehouseEditView from "@/views/warehouse/WarehouseEditView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      redirect: "/dashboard"
    },
    {
      path: "/dashboard",
      name: "dashboard",
      component: () => import("@/views/dashboard/AnalysisView.vue"),
      meta: { title: "仪表盘", icon: "dashboard", permission: ["dashboard"] }
    },
    {
      path: "/user",
      name: "user",
      component: () => import("@/views/user/UserListView.vue"),
      meta: {
        title: "用户管理",
        icon: "user",
        permission: ["user"]
      },
      children: [
        {
          path: "add",
          name: "user-add",
          component: UserAddView,
          meta: {
            title: "新增用户",
            permission: ["user:add"],
            activeMenu: "/user"
          }
        },
        {
          path: "edit/:id",
          name: "user-edit",
          component: UserEditView
        }
      ]
    },
    {
      path: "/product",
      name: "product",
      component: () => import("@/views/product/ProductListView.vue"),
      children: [
        {
          path: "add",
          name: "product-create",
          component: ProductAddView
        },
        {
          path: "edit/:id",
          name: "product-edit",
          component: ProductEditView
        }
      ]
    },
    {
      path: "/product/category",
      name: "product-category",
      component: () => import("@/views/product/category/ProductCategoryListView.vue")
    },
    {
      path: "/warehouse",
      name: "warehouse",
      component: () => import("@/views/warehouse/WarehouseListView.vue"),
      children: [
        {
          path: "add",
          name: "warehouse-create",
          component: WarehouseAddView
        },
        {
          path: "edit/:id",
          name: "warehouse-edit",
          component: WarehouseEditView
        }
      ]
    },
    {
      path: "/inventory",
      name: "inventory",
      component: () => import("@/views/inventory/InventoryListView.vue"),
      children: []
    },
    {
      path: "/partner/customer",
      name: "customer",
      component: () => import("@/views/partner/customer/CustomerListView.vue"),
      children: []
    },
    {
      path: "/partner/supplier",
      name: "supplier",
      component: () => import("@/views/partner/supplier/SupplierListView.vue"),
      children: []
    },
    {
      path: "/:pathMatch(.*)*",
      name: "not-found",
      component: () => import("@/views/NotFoundView.vue")
    }
  ]
});

export default router;
