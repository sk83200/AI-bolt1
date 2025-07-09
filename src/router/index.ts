import Home from '../views/Home.vue'
import Dashboard from '../views/Dashboard.vue'
import Trading from '../views/Trading.vue'
import Analytics from '../views/Analytics.vue'
import Strategies from '../views/Strategies.vue'
import Portfolios from '../views/Portfolios.vue'
import Indicators from '../views/Indicators.vue'
import Charts from '../views/Charts.vue'
import Alerts from '../views/Alerts.vue'
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
    // Accessible to guests with limited functionality
  },
  {
    path: '/analytics',
    name: 'Analytics',
    component: Analytics
    // Accessible to guests with limited functionality
  },
  {
    path: '/strategies',
    name: 'Strategies',
    component: Strategies
    // Accessible to guests with limited functionality
  },
  {
    path: '/portfolios',
    name: 'Portfolios',
    component: Portfolios
    // Accessible to guests with limited functionality
  },
  {
    path: '/indicators',
    name: 'Indicators',
    component: Indicators
    // Accessible to guests with limited functionality
  },
  {
    path: '/charts',
    name: 'Charts',
    component: Charts
    // Accessible to guests with limited functionality
  },
  {
    path: '/alerts',
    name: 'Alerts',
    component: Alerts
    // Accessible to guests with limited functionality
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