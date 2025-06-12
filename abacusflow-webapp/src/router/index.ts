import AnalysisView from '@/views/dashboard/AnalysisView.vue'
import NotFoundView from '@/views/NotFoundView.vue'
import UserView from '@/views/user/UserView.vue'
import { createRouter, createWebHistory } from 'vue-router'

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
      component: AnalysisView,
    },
    {
      path: '/user',
      name: 'user',
      component: UserView,
    },
    {
      path: '/404',
      name: '404',
      component: NotFoundView,
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/404',
    },
  ],
})

export default router
