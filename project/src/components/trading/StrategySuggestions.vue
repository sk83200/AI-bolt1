<template>
  <div class="card">
    <h2 class="text-lg font-medium text-gray-900 dark:text-white mb-4">Strategy Suggestions</h2>
    
    <!-- Current Strategy -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div>
        <h3 class="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Current Strategy</h3>
        <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
          <pre class="text-sm text-gray-600 dark:text-gray-300 whitespace-pre-wrap">{{ currentStrategy }}</pre>
        </div>
      </div>
      
      <!-- AI Suggestions -->
      <div>
        <h3 class="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">AI Suggestions</h3>
        <div class="bg-primary-50 dark:bg-primary-900 rounded-lg p-4">
          <div v-if="isLoading" class="flex items-center justify-center py-4">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
          </div>
          <pre v-else class="text-sm text-gray-600 dark:text-gray-300 whitespace-pre-wrap">{{ aiSuggestions }}</pre>
        </div>
      </div>
    </div>
    
    <!-- Performance Comparison -->
    <div class="mt-6">
      <h3 class="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Performance Comparison</h3>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div class="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 p-4">
          <h4 class="text-sm font-medium mb-2">Current Strategy</h4>
          <div class="space-y-2">
            <div class="flex justify-between">
              <span class="text-sm text-gray-500 dark:text-gray-400">Return</span>
              <span class="text-sm font-medium">{{ currentMetrics.return }}%</span>
            </div>
            <div class="flex justify-between">
              <span class="text-sm text-gray-500 dark:text-gray-400">Sharpe Ratio</span>
              <span class="text-sm font-medium">{{ currentMetrics.sharpe }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-sm text-gray-500 dark:text-gray-400">Max Drawdown</span>
              <span class="text-sm font-medium">{{ currentMetrics.maxDrawdown }}%</span>
            </div>
          </div>
        </div>
        
        <div class="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 p-4">
          <h4 class="text-sm font-medium mb-2">Suggested Strategy</h4>
          <div class="space-y-2">
            <div class="flex justify-between">
              <span class="text-sm text-gray-500 dark:text-gray-400">Return</span>
              <span class="text-sm font-medium text-success-500">{{ suggestedMetrics.return }}%</span>
            </div>
            <div class="flex justify-between">
              <span class="text-sm text-gray-500 dark:text-gray-400">Sharpe Ratio</span>
              <span class="text-sm font-medium text-success-500">{{ suggestedMetrics.sharpe }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-sm text-gray-500 dark:text-gray-400">Max Drawdown</span>
              <span class="text-sm font-medium text-success-500">{{ suggestedMetrics.maxDrawdown }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Action buttons -->
    <div class="mt-6 flex justify-end space-x-3">
      <button 
        @click="requestNewSuggestion" 
        class="btn btn-secondary"
        :disabled="isLoading"
      >
        New Suggestion
      </button>
      <button 
        @click="applySuggestion" 
        class="btn btn-primary"
        :disabled="isLoading || !aiSuggestions"
      >
        Apply Suggestion
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '../../stores/auth'
import axios from 'axios'

const authStore = useAuthStore()
const isLoading = ref(false)

// Sample data - replace with actual strategy
const currentStrategy = ref(`Strategy: Moving Average Crossover
Timeframe: 1h
Entry:
- When 20 EMA crosses above 50 EMA
- RSI > 50
Exit:
- When 20 EMA crosses below 50 EMA
- Stop loss at 2%`)

const aiSuggestions = ref(`Suggested Improvements:
1. Add volume confirmation for entry
2. Implement trailing stop loss
3. Add RSI divergence check
4. Consider multiple timeframe analysis

Modified Strategy:
Entry:
- When 20 EMA crosses above 50 EMA
- RSI > 50
- Volume > 20-period average
- Confirm trend on higher timeframe
Exit:
- When 20 EMA crosses below 50 EMA
- Trailing stop loss at 2%
- RSI bearish divergence`)

// Sample metrics
const currentMetrics = ref({
  return: 15.2,
  sharpe: 1.2,
  maxDrawdown: -8.5
})

const suggestedMetrics = ref({
  return: 18.7,
  sharpe: 1.5,
  maxDrawdown: -7.2
})

const requestNewSuggestion = async () => {
  if (!authStore.isProUser) {
    alert('This feature requires a Pro subscription')
    return
  }
  
  isLoading.value = true
  try {
    const response = await axios.post('/api/strategies/suggest', {
      strategy: currentStrategy.value,
      userId: authStore.user?.id
    })
    aiSuggestions.value = response.data.suggestion
    suggestedMetrics.value = response.data.metrics
  } catch (error) {
    console.error('Error getting suggestion:', error)
  } finally {
    isLoading.value = false
  }
}

const applySuggestion = async () => {
  if (!authStore.isProUser) {
    alert('This feature requires a Pro subscription')
    return
  }
  
  try {
    await axios.post('/api/strategies/apply', {
      strategy: aiSuggestions.value,
      userId: authStore.user?.id
    })
    currentStrategy.value = aiSuggestions.value
    currentMetrics.value = { ...suggestedMetrics.value }
  } catch (error) {
    console.error('Error applying suggestion:', error)
  }
}
</script>