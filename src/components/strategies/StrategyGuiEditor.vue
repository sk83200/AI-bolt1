<template>
  <div class="h-full flex flex-col bg-white dark:bg-gray-800">
    <!-- Header -->
    <div class="p-4 border-b border-gray-200 dark:border-gray-700 flex-shrink-0">
      <div class="flex items-center justify-between">
        <h3 class="font-medium text-gray-900 dark:text-white">Strategy Builder</h3>
        <div v-if="isGuest" class="text-xs text-warning-600 dark:text-warning-400 bg-warning-100 dark:bg-warning-900 px-2 py-1 rounded">
          Read-only (Guest)
        </div>
      </div>
    </div>
    
    <!-- Form Content -->
    <div class="flex-1 overflow-y-auto p-4">
      <form @submit.prevent="handleSubmit" class="space-y-6">
        <!-- Basic Information -->
        <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
          <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Basic Information</h4>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="label">Strategy Name</label>
              <input
                v-model="localData.name"
                type="text"
                class="input"
                :disabled="isGuest"
                :placeholder="isGuest ? 'Sample Momentum Strategy' : 'Enter strategy name'"
              />
            </div>
            <div>
              <label class="label">Strategy Type</label>
              <select
                v-model="localData.type"
                class="input"
                :disabled="isGuest"
              >
                <option value="momentum">Momentum</option>
                <option value="mean_reversion">Mean Reversion</option>
                <option value="trend_following">Trend Following</option>
                <option value="breakout">Breakout</option>
              </select>
            </div>
            <div>
              <label class="label">Asset Class</label>
              <select
                v-model="localData.assetClass"
                class="input"
                :disabled="isGuest"
              >
                <option value="stocks">Stocks</option>
                <option value="crypto">Cryptocurrency</option>
                <option value="forex">Forex</option>
                <option value="commodities">Commodities</option>
              </select>
            </div>
            <div>
              <label class="label">Timeframe</label>
              <select
                v-model="localData.timeframe"
                class="input"
                :disabled="isGuest"
              >
                <option value="1m">1 Minute</option>
                <option value="5m">5 Minutes</option>
                <option value="15m">15 Minutes</option>
                <option value="1h">1 Hour</option>
                <option value="4h">4 Hours</option>
                <option value="1d">1 Day</option>
              </select>
            </div>
          </div>
        </div>

        <!-- Entry Rules -->
        <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
          <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Entry Rules</h4>
          
          <!-- Indicators -->
          <div class="mb-4">
            <label class="label">Technical Indicators</label>
            <div class="grid grid-cols-2 md:grid-cols-3 gap-2">
              <label 
                v-for="indicator in availableIndicators" 
                :key="indicator.value"
                class="flex items-center space-x-2"
              >
                <input
                  type="checkbox"
                  :value="indicator.value"
                  v-model="localData.entryRules.indicators"
                  :disabled="isGuest"
                  class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                />
                <span class="text-sm text-gray-700 dark:text-gray-300">{{ indicator.label }}</span>
              </label>
            </div>
          </div>

          <!-- Entry Conditions -->
          <div>
            <label class="label">Entry Conditions</label>
            <div class="space-y-2">
              <div 
                v-for="(condition, index) in localData.entryRules.conditions" 
                :key="index"
                class="flex items-center space-x-2"
              >
                <input
                  v-model="condition.text"
                  type="text"
                  class="flex-1 input"
                  :disabled="isGuest"
                  placeholder="e.g., RSI > 70"
                />
                <button 
                  v-if="!isGuest"
                  @click="removeCondition(index)"
                  class="text-red-600 hover:text-red-700 text-sm"
                  type="button"
                >
                  Remove
                </button>
              </div>
              <button 
                v-if="!isGuest"
                @click="addCondition"
                class="text-primary-600 hover:text-primary-700 text-sm"
                type="button"
              >
                + Add Condition
              </button>
            </div>
          </div>
        </div>

        <!-- Exit Rules -->
        <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
          <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Exit Rules</h4>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="label">Profit Target (%)</label>
              <input
                v-model.number="localData.exitRules.profitTarget"
                type="number"
                min="0"
                step="0.1"
                class="input"
                :disabled="isGuest"
              />
            </div>
            <div>
              <label class="label">Stop Loss (%)</label>
              <input
                v-model.number="localData.exitRules.stopLoss"
                type="number"
                min="0"
                step="0.1"
                class="input"
                :disabled="isGuest"
              />
            </div>
          </div>
          
          <div class="mt-3 flex items-center space-x-2">
            <input
              v-model="localData.exitRules.useTrailingStop"
              type="checkbox"
              :disabled="isGuest"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label class="text-sm text-gray-700 dark:text-gray-300">Use trailing stop</label>
          </div>
        </div>

        <!-- Risk Management -->
        <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
          <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Risk Management</h4>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="label">Max Risk Per Trade (%)</label>
              <input
                v-model.number="localData.riskManagement.maxRiskPerTrade"
                type="number"
                min="0"
                max="10"
                step="0.1"
                class="input"
                :disabled="isGuest"
              />
            </div>
            <div>
              <label class="label">Max Positions</label>
              <input
                v-model.number="localData.riskManagement.maxPositions"
                type="number"
                min="1"
                max="20"
                class="input"
                :disabled="isGuest"
              />
            </div>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="flex space-x-3">
          <button 
            type="submit"
            :disabled="isGuest"
            :class="[
              'btn',
              isGuest 
                ? 'bg-gray-300 text-gray-500 cursor-not-allowed' 
                : 'btn-primary'
            ]"
          >
            {{ isGuest ? 'Save (Sign up required)' : 'Save Strategy' }}
          </button>
          <button 
            @click="runBacktest"
            :disabled="isGuest"
            :class="[
              'btn',
              isGuest 
                ? 'bg-gray-300 text-gray-500 cursor-not-allowed' 
                : 'btn-secondary'
            ]"
            type="button"
          >
            {{ isGuest ? 'Backtest (Sign up required)' : 'Run Backtest' }}
          </button>
          <button 
            @click="resetForm"
            :disabled="isGuest"
            :class="[
              'btn',
              isGuest 
                ? 'bg-gray-300 text-gray-500 cursor-not-allowed' 
                : 'btn-secondary'
            ]"
            type="button"
          >
            Reset
          </button>
        </div>

        <!-- Guest Mode Upgrade Prompt -->
        <div v-if="isGuest" class="bg-gradient-to-r from-primary-50 to-secondary-50 dark:from-primary-900 dark:to-secondary-900 rounded-lg p-4 text-center">
          <h4 class="text-sm font-semibold text-gray-900 dark:text-white mb-2">
            🔒 Unlock Full Strategy Builder
          </h4>
          <p class="text-xs text-gray-600 dark:text-gray-300 mb-3">
            Sign up to customize parameters, save strategies, and run backtests
          </p>
          <div class="space-x-2">
            <button 
              @click="$router.push('/register')"
              class="bg-primary-600 hover:bg-primary-700 text-white text-xs px-4 py-2 rounded-md font-medium"
              type="button"
            >
              Sign Up Free
            </button>
            <button 
              @click="$router.push('/login')"
              class="text-primary-600 hover:text-primary-700 dark:text-primary-400 text-xs px-4 py-2"
              type="button"
            >
              Log In
            </button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'

// Props
const props = defineProps<{
  modelValue: any
  isGuest: boolean
}>()

// Emits
const emit = defineEmits(['update:modelValue', 'update'])

const router = useRouter()

// Available indicators
const availableIndicators = [
  { value: 'RSI', label: 'RSI' },
  { value: 'MACD', label: 'MACD' },
  { value: 'SMA', label: 'Simple MA' },
  { value: 'EMA', label: 'Exponential MA' },
  { value: 'BB', label: 'Bollinger Bands' },
  { value: 'ADX', label: 'ADX' }
]

// Local data with sample values for guest mode
const localData = reactive({
  name: props.isGuest ? 'Sample Momentum Strategy' : '',
  type: 'momentum',
  assetClass: 'stocks',
  timeframe: '1d',
  entryRules: {
    conditions: props.isGuest ? [
      { text: 'RSI > 70' },
      { text: 'Price > SMA20' },
      { text: 'Volume > 1.5x average' }
    ] : [{ text: '' }],
    indicators: props.isGuest ? ['RSI', 'SMA', 'Volume'] : []
  },
  exitRules: {
    profitTarget: 5,
    stopLoss: 2,
    useTrailingStop: false
  },
  riskManagement: {
    maxRiskPerTrade: 2,
    maxPositions: 5
  },
  ...props.modelValue
})

// Add condition
const addCondition = () => {
  localData.entryRules.conditions.push({ text: '' })
}

// Remove condition
const removeCondition = (index: number) => {
  if (localData.entryRules.conditions.length > 1) {
    localData.entryRules.conditions.splice(index, 1)
  }
}

// Handle form submission
const handleSubmit = () => {
  if (props.isGuest) {
    router.push('/register')
    return
  }
  
  console.log('Saving strategy:', localData)
  // Implementation for saving strategy
}

// Run backtest
const runBacktest = () => {
  if (props.isGuest) {
    router.push('/register')
    return
  }
  
  console.log('Running backtest for:', localData)
  // Implementation for backtesting
}

// Reset form
const resetForm = () => {
  if (props.isGuest) {
    router.push('/register')
    return
  }
  
  Object.assign(localData, {
    name: '',
    type: 'momentum',
    assetClass: 'stocks',
    timeframe: '1d',
    entryRules: {
      conditions: [{ text: '' }],
      indicators: []
    },
    exitRules: {
      profitTarget: 5,
      stopLoss: 2,
      useTrailingStop: false
    },
    riskManagement: {
      maxRiskPerTrade: 2,
      maxPositions: 5
    }
  })
}

// Watch for changes and emit updates (only for authenticated users)
watch(localData, (newValue) => {
  if (!props.isGuest) {
    emit('update:modelValue', newValue)
    emit('update', newValue)
  }
}, { deep: true })

// Watch for prop changes
watch(() => props.modelValue, (newValue) => {
  if (!props.isGuest) {
    Object.assign(localData, newValue)
  }
}, { deep: true })
</script>