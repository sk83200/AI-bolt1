<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { 
  MoonIcon, 
  SunIcon, 
  UserCircleIcon,
  ArrowRightOnRectangleIcon
} from '@heroicons/vue/24/outline'
import { useAuthStore } from '../../stores/auth'

// Props
defineProps<{
  isDarkMode: boolean
}>()

// Emits
const emit = defineEmits(['toggle-dark-mode'])

// Store and router
const authStore = useAuthStore()
const router = useRouter()
const isDropdownOpen = ref(false)

// Computed
const isAuthenticated = computed(() => authStore.isAuthenticated)
const isProUser = computed(() => authStore.isProUser)
const isGuestMode = computed(() => authStore.guestMode)

// Methods
const toggleDarkMode = () => {
  emit('toggle-dark-mode')
}

const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value
}

const logout = async () => {
  await authStore.logout()
  router.push('/')
}

const upgradeToPro = async () => {
  await authStore.upgradeToPro()
}

const navigateTo = (path: string) => {
  router.push(path)
  isDropdownOpen.value = false
}
</script>

<template>
  <header class="sticky top-0 z-50 bg-white dark:bg-gray-900 shadow-sm">
    <div class="max-w-full mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-16">
        <!-- Logo -->
        <router-link to="/" class="flex items-center space-x-2">
          <span class="text-primary-800 dark:text-primary-400 font-bold text-2xl">AI</span>
          <span class="font-semibold text-lg text-gray-900 dark:text-white">Trader</span>
        </router-link>

        <!-- Navigation -->
        <nav v-if="isAuthenticated && !isGuestMode" class="hidden md:flex space-x-8">
          <router-link 
            v-for="item in ['Dashboard', 'Trading', 'Analytics']"
            :key="item"
            :to="'/' + item.toLowerCase()"
            class="text-gray-700 dark:text-gray-300 hover:text-primary-600 dark:hover:text-primary-400"
          >
            {{ item }}
          </router-link>
        </nav>

        <!-- Right side -->
        <div class="flex items-center space-x-4">
          <!-- Theme toggle -->
          <button
            @click="toggleDarkMode"
            class="p-2 rounded-md text-gray-500 dark:text-gray-400 
              hover:text-gray-900 dark:hover:text-white hover:bg-gray-100 dark:hover:bg-gray-800"
          >
            <SunIcon v-if="isDarkMode" class="h-5 w-5" />
            <MoonIcon v-else class="h-5 w-5" />
          </button>

          <!-- Guest mode indicator -->
          <div v-if="isGuestMode" class="flex items-center space-x-2">
            <span class="text-xs bg-warning-100 text-warning-800 dark:bg-warning-900 dark:text-warning-200 px-2 py-1 rounded-full">
              Guest Mode
            </span>
          </div>

          <!-- User menu for authenticated users -->
          <div v-if="isAuthenticated && !isGuestMode" class="relative">
            <button
              @click="toggleDropdown"
              class="flex items-center space-x-2 text-gray-700 dark:text-gray-300 hover:text-primary-600 dark:hover:text-primary-400"
            >
              <UserCircleIcon class="h-6 w-6" />
              <span v-if="!isProUser" class="text-xs bg-accent-500 text-white px-2 py-1 rounded-full">Basic</span>
              <span v-else class="text-xs bg-primary-500 text-white px-2 py-1 rounded-full">Pro</span>
            </button>

            <div
              v-if="isDropdownOpen"
              class="absolute right-0 mt-2 w-48 bg-white dark:bg-gray-800 rounded-md shadow-lg py-1"
            >
              <a
                v-if="!isProUser"
                @click="upgradeToPro"
                class="block px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 cursor-pointer"
              >
                Upgrade to Pro
              </a>
              <a
                @click="navigateTo('/profile')"
                class="block px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 cursor-pointer"
              >
                Profile Settings
              </a>
              <a
                @click="logout"
                class="block px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 cursor-pointer"
              >
                Sign out
              </a>
            </div>
          </div>

          <!-- Login/Register for non-authenticated users or guest mode -->
          <div v-if="!isAuthenticated || isGuestMode" class="flex space-x-2">
            <router-link 
              to="/login" 
              class="text-sm text-gray-700 dark:text-white hover:text-primary-600 dark:hover:text-primary-400"
            >
              Log in
            </router-link>
            <router-link 
              to="/register" 
              class="btn btn-primary text-sm px-3 py-1"
            >
              Sign Up
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>