import Home from '../views/Home.vue'
import Dashboard from '../views/Dashboard.vue'
import Trading from '../views/Trading.vue'
import Analytics from '../views/Analytics.vue'
import Strategies from '../views/Strategies.vue'
import Profile from '../views/Profile.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import NotFound from '../views/NotFound.vue'
import Pricing from '../views/Pricing.vue'

export const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/trading',
    name: 'Trading',
    component: Trading
    // Removed requiresAuth - now accessible to guests
  },
  {
    path: '/analytics',
    name: 'Analytics',
    component: Analytics
    // Removed requiresAuth - now accessible to guests with limited functionality
  },
  {
    path: '/strategies',
    name: 'Strategies',
    component: Strategies
    // Removed requiresAuth - now accessible to guests with limited functionality
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true }
  },
  {
    path: '/pricing',
    name: 'Pricing',
    component: Pricing
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound
  }
]