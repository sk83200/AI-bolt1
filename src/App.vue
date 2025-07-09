<template>
  <div class="h-screen flex flex-col dark:bg-gray-900">
    <!-- Header -->
    <AppHeader 
      :isDarkMode="isDarkMode" 
      @toggle-dark-mode="toggleDarkMode" 
    />
    
    <!-- Guest Mode Banner -->
    <div 
      v-if="!authStore.isAuthenticated && showGuestBanner"
      class="bg-primary-50 dark:bg-primary-900 border-b border-primary-200 dark:border-primary-800 px-4 py-2"
    >
      <div class="flex items-center justify-between max-w-7xl mx-auto">
        <div class="flex items-center">
          <svg class="h-5 w-5 text-primary-600 mr-2" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
          </svg>
          <span class="text-sm text-primary-800 dark:text-primary-200">
            You're in Guest Mode. Sign up to run backtests, analyze performance, and save your work.
          </span>
        </div>
        <div class="flex items-center space-x-3">
          <button 
            @click="$router.push('/register')"
            class="bg-primary-600 hover:bg-primary-700 text-white text-sm px-4 py-1 rounded-md font-medium"
          >
            Sign Up Free
          </button>
          <button 
            @click="showGuestBanner = false"
            class="text-primary-600 hover:text-primary-700 dark:text-primary-400"
          >
            <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>
    </div>
    
    <!-- Main content -->
    <div class="flex-1 overflow-hidden">
      <!-- Landing page with centered AI Assistant -->
      <div v-if="showCenteredAssistant" class="h-full">
        <CenteredAiAssistant 
          @engage="handleUserEngagement"
          @login="handleLogin"
        />
      </div>
      
      <!-- Full workspace -->
      <div v-else-if="authStore.isAuthenticated" class="h-full">
        <Splitpanes class="h-full">
          <!-- Left panel - AI Input & Messages -->
          <Pane :size="leftPaneSize" min-size="20">
            <div class="h-full flex flex-col">
              <!-- AI Input -->
              <div class="flex-1 overflow-auto">
                <AiAssistant />
              </div>
              
              <!-- Messages Panel Toggle -->
              <div 
                class="h-8 bg-gray-100 dark:bg-gray-700 flex items-center justify-center cursor-pointer hover:bg-gray-200 dark:hover:bg-gray-600"
                @click="toggleMessages"
              >
                <ChevronUpIcon 
                  v-if="showMessages"
                  class="h-4 w-4 text-gray-500 dark:text-gray-400" 
                />
                <ChevronDownIcon 
                  v-else
                  class="h-4 w-4 text-gray-500 dark:text-gray-400" 
                />
                <span class="text-sm text-gray-500 dark:text-gray-400 ml-2">
                  Messages {{ unreadCount > 0 ? `(${unreadCount})` : '' }}
                </span>
              </div>
              
              <!-- Messages Panel -->
              <div 
                v-show="showMessages"
                class="h-80 border-t border-gray-200 dark:border-gray-700"
              >
                <MessagesPanel />
              </div>
            </div>
          </Pane>
          
          <!-- Center panel - Main content -->
          <Pane :size="centerPaneSize" min-size="50">
            <div class="h-full flex flex-col">
              <!-- Main content -->
              <div class="flex-1 overflow-auto">
                <router-view></router-view>
              </div>
            </div>
          </Pane>
        </Splitpanes>
      </div>
      
      <!-- Guest mode workspace -->
      <div v-else class="h-full">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted } from 'vue'
import { Splitpanes, Pane } from 'splitpanes'
import 'splitpanes/dist/splitpanes.css'
import { 
  ChevronUpIcon,
  ChevronDownIcon
} from '@heroicons/vue/24/outline'
import { useAuthStore } from './stores/auth'
import { useWorkspaceStore } from './stores/workspace'
import AppHeader from './components/layout/AppHeader.vue'
import AiAssistant from './components/ai/AiAssistant.vue'
import MessagesPanel from './components/messages/MessagesPanel.vue'
import CenteredAiAssistant from './components/ai/CenteredAiAssistant.vue'

const authStore = useAuthStore()
const workspaceStore = useWorkspaceStore()

// Dark mode state
const isDarkMode = ref(false)

// Panel states
const showMessages = ref(false)
const unreadCount = ref(0)
const showGuestBanner = ref(true)

// Compute pane sizes
const leftPaneSize = computed(() => 30)
const centerPaneSize = computed(() => 70)

// Centered assistant logic
const showCenteredAssistant = computed(() => {
  if (!authStore.isAuthenticated) return false
  if (authStore.isReturningUser && workspaceStore.hasWorkspaceState) return false
  return !workspaceStore.hasEngaged
})

// Toggle messages panel
const toggleMessages = () => {
  showMessages.value = !showMessages.value
}

// Toggle dark mode
const toggleDarkMode = () => {
  isDarkMode.value = !isDarkMode.value
  updateTheme()
}

// Update theme
const updateTheme = () => {
  if (isDarkMode.value) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
}

// Handle user engagement
const handleUserEngagement = () => {
  workspaceStore.setEngaged(true)
}

// Handle login from centered assistant
const handleLogin = () => {
  // This will be handled by the CenteredAiAssistant component
}

// Watch for system theme changes
watch(
  () => window.matchMedia('(prefers-color-scheme: dark)').matches,
  (isDark) => {
    isDarkMode.value = isDark
    updateTheme()
  },
  { immediate: true }
)

// Initialize workspace state on mount
onMounted(() => {
  workspaceStore.initializeWorkspace()
})
</script>

<style>
.splitpanes__splitter {
  @apply bg-gray-200 dark:bg-gray-700 hover:bg-gray-300 dark:hover:bg-gray-600 transition-colors;
  width: 6px !important;
  margin: 0 -3px;
}

.splitpanes--vertical > .splitpanes__splitter {
  min-width: 6px !important;
}
</style>