import { createRouter, createWebHistory } from 'vue-router'
import FlatsPage from '../pages/FlatsPage.vue'
import AgencyPage from '../pages/AgencyPage.vue'

const routes = [
  {
    path: '/',
    redirect: '/flats'
  },
  {
    path: '/flats',
    name: 'Flats',
    component: FlatsPage
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
