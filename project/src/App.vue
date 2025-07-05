<template>
  <div class="h-screen flex flex-col dark:bg-gray-900">
    <!-- Header -->
    <AppHeader 
      :isDarkMode="isDarkMode" 
      @toggle-dark-mode="toggleDarkMode" 
    />
    
    <!-- Main content -->
    <div v-if="authStore.isAuthenticated" class="flex-1 overflow-hidden">
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
    <div v-else class="flex-1 overflow-auto">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { Splitpanes, Pane } from 'splitpanes'
import 'splitpanes/dist/splitpanes.css'
import { 
  ChevronUpIcon,
  ChevronDownIcon
} from '@heroicons/vue/24/outline'
import { useAuthStore } from './stores/auth'
import AppHeader from './components/layout/AppHeader.vue'
import AiAssistant from './components/ai/AiAssistant.vue'
import MessagesPanel from './components/messages/MessagesPanel.vue'

const authStore = useAuthStore()

// Dark mode state
const isDarkMode = ref(false)

// Panel states
const showMessages = ref(false)
const unreadCount = ref(0) // This would be managed by your messages store

// Compute pane sizes
const leftPaneSize = computed(() => 30)
const centerPaneSize = computed(() => 70)

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

// Watch for system theme changes
watch(
  () => window.matchMedia('(prefers-color-scheme: dark)').matches,
  (isDark) => {
    isDarkMode.value = isDark
    updateTheme()
  },
  { immediate: true }
)
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