import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'
import './style.css'
import App from './App.vue'
import { routes } from './router'
import { useAuthStore } from './stores/auth'

// Create application
const app = createApp(App)

// Initialize Pinia
const pinia = createPinia()
app.use(pinia)

// Initialize router
const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guards for authentication
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // Check if route requires authentication
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'Login' })
  } 
  // Check if route requires specific role
  else if (to.meta.roles && !authStore.hasRole(to.meta.roles)) {
    next({ name: 'Dashboard' })
  }
  // If trying to access login/register while authenticated
  else if ((to.name === 'Login' || to.name === 'Register') && authStore.isAuthenticated) {
    next({ name: 'Dashboard' })
  } 
  else {
    next()
  }
})

// Mount the app
app.use(router)
app.mount('#app')