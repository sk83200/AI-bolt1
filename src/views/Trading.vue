<template>
  <div class="h-full">
    <!-- Authenticated users: Full Strategy Builder -->
    <div v-if="authStore.isAuthenticated" class="h-full">
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Trading Strategy Builder</h1>
        <button 
          @click="openAlertDialog(currentSymbol)" 
          class="btn btn-accent"
        >
          Create Alert
        </button>
      </div>

      <!-- Strategy Builder with Property Sheet Layout -->
      <StrategyBuilder />
    </div>

    <!-- Guest users: Split view with AI Assistant and Preview -->
    <div v-else class="h-full flex">
      <!-- Left: AI Assistant -->
      <div class="w-1/3 border-r border-gray-200 dark:border-gray-700">
        <GuestModeAssistant />
      </div>
      
      <!-- Right: Strategy Builder Preview -->
      <div class="flex-1">
        <GuestStrategyBuilder />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '../stores/auth'
import StrategyBuilder from '../components/trading/StrategyBuilder.vue'
import GuestModeAssistant from '../components/ai/GuestModeAssistant.vue'
import GuestStrategyBuilder from '../components/trading/GuestStrategyBuilder.vue'

const authStore = useAuthStore()
const currentSymbol = ref('AAPL')

const openAlertDialog = (symbol: string) => {
  // Implementation for opening alert dialog
  console.log('Opening alert dialog for:', symbol)
}
</script>