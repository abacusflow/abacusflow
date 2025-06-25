import { createRouter, createWebHistory, RouterView } from "vue-router";

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
      component: () => import("@/views/user/UsersView.vue"),
      meta: {
        title: "用户管理",
        icon: "user",
        permission: ["user"]
      }
    },
    {
      path: "/inventory",
      name: "inventory",
      component: () => import("@/views/inventory/InventoriesView.vue"),
      meta: {
        title: "库存管理",
        icon: "database",
        permission: ["inventory"]
      }
    },
    {
      path: "/transaction",
      name: "transaction",
      component: RouterView,
      meta: {
        title: "交易管理",
        icon: "transaction",
        permission: ["transaction"]
      },
      children: [
        {
          path: "purchase-order",
          name: "transaction-purchase-order",
          component: () => import("@/views/transaction/purchase-order/PurchaseOrdersView.vue"),
          meta: {
            title: "采购单管理",
            icon: "shopping-cart",
            permission: ["transaction:purchase-order"]
          }
        },
        {
          path: "sale-order",
          name: "transaction-sale-order",
          component: () => import("@/views/transaction/sale-order/SaleOrdersView.vue"),
          meta: {
            title: "销售单管理",
            icon: "shopping-cart",
            permission: ["transaction:sale-order"]
          }
        }
      ]
    },
    {
      path: "/product",
      name: "product",
      component: RouterView,
      meta: {
        title: "产品中心",
        icon: "appstore",
        permission: ["product:*"]
      },
      children: [
        {
          path: "",
          name: "product-home",
          component: () => import("@/views/product/ProductsView.vue"),
          meta: {
            title: "产品管理",
            icon: "appstore",
            permission: ["product:list"]
          }
        },
        {
          path: "category",
          name: "product-category",
          component: () => import("@/views/product/category/ProductCategoriesView.vue"),
          meta: {
            title: "产品类别管理",
            icon: "tags",
            permission: ["product:category"]
          }
        }
      ]
    },
    {
      path: "/partner",
      name: "合作伙伴管理",
      component: RouterView,
      meta: {
        title: "合作伙伴管理",
        icon: "team",
        permission: ["partner:*"]
      },
      children: [
        {
          path: "customer",
          name: "partner-customer",
          component: () => import("@/views/partner/customer/CustomersView.vue"),
          meta: {
            title: "客户管理",
            icon: "user",
            permission: ["partner:customer"],
            parent: "partner"
          }
        },
        {
          path: "supplier",
          name: "partner-supplier",
          component: () => import("@/views/partner/supplier/SuppliersView.vue"),
          meta: {
            title: "供应商管理",
            icon: "shop",
            permission: ["partner:supplier"],
            parent: "partner"
          }
        }
      ]
    },
    {
      path: "/depot",
      name: "depot",
      component: () => import("@/views/depot/DepotsView.vue"),
      meta: {
        title: "储存点管理",
        icon: "home",
        permission: ["depot"]
      }
    },
    {
      path: "/:pathMatch(.*)*",
      name: "not-found",
      component: () => import("@/views/NotFoundView.vue"),
      meta: {
        title: "未找到页面",
        hidden: true
      }
    }
  ]
});

export default router;
