<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { 
  HomeIcon, 
  ChartBarIcon, 
  CurrencyDollarIcon, 
  LightBulbIcon,
  XMarkIcon,
  Cog6ToothIcon
} from '@heroicons/vue/24/outline'
import { useAuthStore } from '../../stores/auth'

// Props
defineProps<{
  isOpen: boolean
}>()

// Emits
const emit = defineEmits(['close'])

// Store and router
const router = useRouter()
const authStore = useAuthStore()

// Computed
const currentRoute = computed(() => router.currentRoute.value.name)
const userRole = computed(() => authStore.user?.role || 'Trader')

// Navigation items with role-based access control
const navItems = computed(() => [
  { 
    name: 'Dashboard', 
    icon: HomeIcon, 
    route: '/dashboard',
    allowedRoles: ['Trader', 'Analyst', 'Admin']
  },
  { 
    name: 'Trading', 
    icon: CurrencyDollarIcon, 
    route: '/trading',
    allowedRoles: ['Trader', 'Admin']
  },
  { 
    name: 'Analytics', 
    icon: ChartBarIcon, 
    route: '/analytics',
    allowedRoles: ['Trader', 'Analyst', 'Admin']
  },
  { 
    name: 'Strategies', 
    icon: LightBulbIcon, 
    route: '/strategies',
    allowedRoles: ['Trader', 'Analyst', 'Admin']
  },
  { 
    name: 'Profile', 
    icon: Cog6ToothIcon, 
    route: '/profile',
    allowedRoles: ['Trader', 'Analyst', 'Admin']
  }
])

// Filtered navigation items based on user role
const filteredNavItems = computed(() => {
  return navItems.value.filter(item => 
    item.allowedRoles.includes(userRole.value)
  )
})

// Methods
const closeSidebar = () => {
  emit('close')
}
</script>

<template>
  <div>
    <!-- Mobile sidebar backdrop -->
    <div 
      v-if="isOpen" 
      class="fixed inset-0 z-40 bg-gray-600 bg-opacity-75 lg:hidden"
      @click="closeSidebar"
    ></div>

    <!-- Sidebar -->
    <aside
      :class="[
        'fixed top-16 bottom-0 left-0 z-40 w-64 transform transition-transform duration-300 ease-in-out',
        'bg-white dark:bg-gray-900 shadow-lg border-r border-gray-200 dark:border-gray-800',
        isOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
      ]"
    >
      <div class="h-full flex flex-col">
        <!-- Mobile close button -->
        <div class="p-4 flex justify-end lg:hidden">
          <button
            @click="closeSidebar"
            class="text-gray-500 hover:text-gray-600 dark:text-gray-400 dark:hover:text-gray-300"
          >
            <XMarkIcon class="h-6 w-6" />
          </button>
        </div>

        <!-- User info -->
        <div class="p-4 border-b border-gray-200 dark:border-gray-800">
          <div class="text-sm font-medium text-gray-500 dark:text-gray-400">Logged in as</div>
          <div class="flex items-center justify-between">
            <div class="text-lg font-semibold text-gray-900 dark:text-white truncate">
              {{ authStore.user?.name || 'User' }}
            </div>
            <span 
              class="px-2 py-1 text-xs font-medium rounded-full bg-primary-100 text-primary-800 
              dark:bg-primary-900 dark:text-primary-200"
            >
              {{ userRole }}
            </span>
          </div>
        </div>

        <!-- Navigation menu -->
        <nav class="mt-4 px-3 flex-1 overflow-y-auto">
          <div class="space-y-1">
            <router-link
              v-for="item in filteredNavItems"
              :key="item.name"
              :to="item.route"
              :class="[
                'group flex items-center px-3 py-2 text-sm font-medium rounded-md transition-colors',
                currentRoute === item.name
                  ? 'bg-primary-100 text-primary-800 dark:bg-primary-900 dark:text-primary-200'
                  : 'text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 hover:text-gray-900 dark:hover:text-white'
              ]"
            >
              <component 
                :is="item.icon" 
                :class="[
                  'mr-3 h-5 w-5 transition-colors',
                  currentRoute === item.name
                    ? 'text-primary-600 dark:text-primary-400'
                    : 'text-gray-400 dark:text-gray-500 group-hover:text-gray-500 dark:group-hover:text-gray-400'
                ]" 
              />
              {{ item.name }}
            </router-link>
          </div>
        </nav>

        <!-- Version info -->
        <div class="p-4 text-xs text-gray-500 dark:text-gray-400 border-t border-gray-200 dark:border-gray-800">
          <span>AI Trader v0.1.0</span>
        </div>
      </div>
    </aside>
  </div>
</template>