import { createRouter, createWebHistory } from 'vue-router'
import FlatsPage from '../pages/FlatsPage.vue'
import FlatsListPage from '../pages/FlatsListPage.vue'
import AgencyPage from '../pages/AgencyPage.vue'

const routes = [
  {
    path: '/',
    redirect: '/flat'
  },
  {
    path: '/flat',
    name: 'Flat',
    component: FlatsPage
  },
  {
    path: '/flats',
    name: 'Flats',
    component: FlatsListPage
  },
  {
    path: '/agency',
    name: 'Agency',
    component: AgencyPage
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
