<template>
  <div class="space-y-4">
    <div class="space-y-4">
      <!-- Execution Settings -->
      <div>
        <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Execution Settings</h4>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">Order Type</label>
            <select v-model="localData.orderType" class="input">
              <option value="market">Market</option>
              <option value="limit">Limit</option>
              <option value="stop_limit">Stop Limit</option>
              <option value="iceberg">Iceberg</option>
              <option value="twap">TWAP</option>
              <option value="vwap">VWAP</option>
            </select>
          </div>
          <div v-if="localData.orderType === 'limit'">
            <label class="label">Limit Price Offset (%)</label>
            <input
              v-model.number="localData.limitPriceOffset"
              type="number"
              step="0.01"
              class="input"
            />
          </div>
          <div>
            <label class="label">Order Timeout (minutes)</label>
            <input
              v-model.number="localData.orderTimeout"
              type="number"
              min="1"
              max="1440"
              class="input"
            />
          </div>
          <div>
            <label class="label">Slippage Tolerance (%)</label>
            <input
              v-model.number="localData.slippageTolerance"
              type="number"
              min="0"
              step="0.01"
              class="input"
            />
          </div>
        </div>
      </div>

      <!-- Backtesting Settings -->
      <div>
        <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Backtesting Settings</h4>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">Commission per Trade ($)</label>
            <input
              v-model.number="localData.commission"
              type="number"
              min="0"
              step="0.01"
              class="input"
            />
          </div>
          <div>
            <label class="label">Commission Type</label>
            <select v-model="localData.commissionType" class="input">
              <option value="fixed">Fixed per Trade</option>
              <option value="percentage">Percentage</option>
              <option value="per_share">Per Share</option>
            </select>
          </div>
          <div>
            <label class="label">Spread (bps)</label>
            <input
              v-model.number="localData.spread"
              type="number"
              min="0"
              step="0.1"
              class="input"
            />
          </div>
          <div>
            <label class="label">Market Impact (%)</label>
            <input
              v-model.number="localData.marketImpact"
              type="number"
              min="0"
              step="0.001"
              class="input"
            />
          </div>
        </div>
      </div>

      <!-- Performance Monitoring -->
      <div>
        <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Performance Monitoring</h4>
        <div class="space-y-2">
          <div class="flex items-center space-x-2">
            <input
              v-model="localData.enablePerformanceTracking"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label class="text-sm text-gray-700 dark:text-gray-300">Enable real-time performance tracking</label>
          </div>
          <div class="flex items-center space-x-2">
            <input
              v-model="localData.enableAlerts"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label class="text-sm text-gray-700 dark:text-gray-300">Enable performance alerts</label>
          </div>
          <div class="flex items-center space-x-2">
            <input
              v-model="localData.enableAutoOptimization"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label class="text-sm text-gray-700 dark:text-gray-300">Enable automatic parameter optimization</label>
          </div>
        </div>
      </div>

      <!-- Data Sources -->
      <div>
        <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Data Sources</h4>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">Primary Data Feed</label>
            <select v-model="localData.primaryDataFeed" class="input">
              <option value="real_time">Real-time Market Data</option>
              <option value="delayed">Delayed Market Data</option>
              <option value="simulation">Simulation Data</option>
            </select>
          </div>
          <div>
            <label class="label">Backup Data Feed</label>
            <select v-model="localData.backupDataFeed" class="input">
              <option value="none">None</option>
              <option value="alternative">Alternative Provider</option>
              <option value="cached">Cached Data</option>
            </select>
          </div>
        </div>
      </div>

      <!-- Logging and Debugging -->
      <div>
        <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Logging and Debugging</h4>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">Log Level</label>
            <select v-model="localData.logLevel" class="input">
              <option value="error">Error Only</option>
              <option value="warning">Warning</option>
              <option value="info">Info</option>
              <option value="debug">Debug</option>
              <option value="trace">Trace</option>
            </select>
          </div>
          <div>
            <label class="label">Log Retention (days)</label>
            <input
              v-model.number="localData.logRetention"
              type="number"
              min="1"
              max="365"
              class="input"
            />
          </div>
        </div>
        
        <div class="space-y-2 mt-3">
          <div class="flex items-center space-x-2">
            <input
              v-model="localData.enableTradeLogging"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label class="text-sm text-gray-700 dark:text-gray-300">Log all trade decisions</label>
          </div>
          <div class="flex items-center space-x-2">
            <input
              v-model="localData.enableSignalLogging"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label class="text-sm text-gray-700 dark:text-gray-300">Log all trading signals</label>
          </div>
          <div class="flex items-center space-x-2">
            <input
              v-model="localData.enableErrorLogging"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label class="text-sm text-gray-700 dark:text-gray-300">Log errors and exceptions</label>
          </div>
        </div>
      </div>

      <!-- Custom Parameters -->
      <div>
        <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Custom Parameters</h4>
        <div class="space-y-2">
          <div 
            v-for="(param, index) in localData.customParameters" 
            :key="index"
            class="flex items-center space-x-2"
          >
            <input
              v-model="param.name"
              type="text"
              class="flex-1 input"
              placeholder="Parameter name"
            />
            <input
              v-model="param.value"
              type="text"
              class="flex-1 input"
              placeholder="Parameter value"
            />
            <select v-model="param.type" class="input w-24">
              <option value="string">String</option>
              <option value="number">Number</option>
              <option value="boolean">Boolean</option>
            </select>
            <button 
              @click="removeCustomParameter(index)"
              class="btn btn-secondary text-sm px-2 py-1"
            >
              Remove
            </button>
          </div>
          <button 
            @click="addCustomParameter"
            class="btn btn-primary text-sm"
          >
            Add Custom Parameter
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{
  modelValue: any
  strategyType: string
}>()

const emit = defineEmits(['update:modelValue'])

const localData = ref({
  // Execution
  orderType: 'market',
  limitPriceOffset: 0.1,
  orderTimeout: 60,
  slippageTolerance: 0.1,
  // Backtesting
  commission: 1.0,
  commissionType: 'fixed',
  spread: 1.0,
  marketImpact: 0.01,
  // Performance monitoring
  enablePerformanceTracking: true,
  enableAlerts: true,
  enableAutoOptimization: false,
  // Data sources
  primaryDataFeed: 'real_time',
  backupDataFeed: 'alternative',
  // Logging
  logLevel: 'info',
  logRetention: 30,
  enableTradeLogging: true,
  enableSignalLogging: true,
  enableErrorLogging: true,
  // Custom parameters
  customParameters: [],
  ...props.modelValue
})

const addCustomParameter = () => {
  localData.value.customParameters.push({
    name: '',
    value: '',
    type: 'string'
  })
}

const removeCustomParameter = (index: number) => {
  localData.value.customParameters.splice(index, 1)
}

watch(localData, (newValue) => {
  emit('update:modelValue', newValue)
}, { deep: true })

watch(() => props.modelValue, (newValue) => {
  Object.assign(localData.value, newValue)
}, { deep: true })
</script>