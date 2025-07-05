<template>
  <div class="space-y-3">
    <!-- Order Type -->
    <div>
      <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Order Type</label>
      <select v-model="localData.orderType" class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white">
        <option value="market">Market</option>
        <option value="limit">Limit</option>
        <option value="stop_limit">Stop Limit</option>
      </select>
    </div>

    <!-- Commission -->
    <div>
      <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Commission ($)</label>
      <input
        v-model.number="localData.commission"
        type="number"
        min="0"
        step="0.01"
        class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
      />
    </div>

    <!-- Slippage -->
    <div>
      <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Slippage (%)</label>
      <input
        v-model.number="localData.slippageTolerance"
        type="number"
        min="0"
        step="0.01"
        class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
      />
    </div>

    <!-- Data Feed -->
    <div>
      <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Data Feed</label>
      <select v-model="localData.primaryDataFeed" class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white">
        <option value="real_time">Real-time</option>
        <option value="delayed">Delayed</option>
        <option value="simulation">Simulation</option>
      </select>
    </div>

    <!-- Logging Level -->
    <div>
      <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Log Level</label>
      <select v-model="localData.logLevel" class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white">
        <option value="error">Error</option>
        <option value="warning">Warning</option>
        <option value="info">Info</option>
        <option value="debug">Debug</option>
      </select>
    </div>

    <!-- Advanced Options -->
    <div class="space-y-2">
      <div class="flex items-center space-x-2">
        <input
          v-model="localData.enablePerformanceTracking"
          type="checkbox"
          class="h-3 w-3 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
        />
        <label class="text-xs text-gray-700 dark:text-gray-300">Performance tracking</label>
      </div>
      <div class="flex items-center space-x-2">
        <input
          v-model="localData.enableAlerts"
          type="checkbox"
          class="h-3 w-3 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
        />
        <label class="text-xs text-gray-700 dark:text-gray-300">Enable alerts</label>
      </div>
      <div class="flex items-center space-x-2">
        <input
          v-model="localData.enableTradeLogging"
          type="checkbox"
          class="h-3 w-3 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
        />
        <label class="text-xs text-gray-700 dark:text-gray-300">Trade logging</label>
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
  orderType: 'market',
  commission: 1.0,
  slippageTolerance: 0.1,
  primaryDataFeed: 'real_time',
  logLevel: 'info',
  enablePerformanceTracking: true,
  enableAlerts: true,
  enableTradeLogging: true,
  ...props.modelValue
})

watch(localData, (newValue) => {
  emit('update:modelValue', newValue)
}, { deep: true })

watch(() => props.modelValue, (newValue) => {
  Object.assign(localData.value, newValue)
}, { deep: true })
</script>