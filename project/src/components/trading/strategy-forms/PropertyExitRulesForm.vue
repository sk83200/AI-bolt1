<template>
  <div class="space-y-3">
    <div v-if="isStrategySupported">
      <!-- Profit Target -->
      <div>
        <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Profit Target (%)</label>
        <input
          v-model.number="localData.profitTarget"
          type="number"
          min="0"
          step="0.1"
          class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
        />
      </div>

      <!-- Stop Loss -->
      <div>
        <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Stop Loss (%)</label>
        <input
          v-model.number="localData.stopLoss"
          type="number"
          min="0"
          step="0.1"
          class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
        />
      </div>

      <!-- Trailing Stop -->
      <div class="flex items-center space-x-2">
        <input
          v-model="localData.useTrailingStop"
          type="checkbox"
          class="h-3 w-3 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
        />
        <label class="text-xs text-gray-700 dark:text-gray-300">Use trailing stop</label>
      </div>

      <div v-if="localData.useTrailingStop">
        <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Trailing Distance (%)</label>
        <input
          v-model.number="localData.trailingDistance"
          type="number"
          min="0"
          step="0.1"
          class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
        />
      </div>

      <!-- Max Hold Time -->
      <div>
        <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Max Hold Time</label>
        <select v-model="localData.maxHoldTime" class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white">
          <option value="none">No Limit</option>
          <option value="intraday">End of Day</option>
          <option value="1_day">1 Day</option>
          <option value="1_week">1 Week</option>
        </select>
      </div>

      <!-- Partial Profits -->
      <div class="flex items-center space-x-2">
        <input
          v-model="localData.usePartialProfits"
          type="checkbox"
          class="h-3 w-3 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
        />
        <label class="text-xs text-gray-700 dark:text-gray-300">Use partial profits</label>
      </div>
    </div>

    <div v-else class="p-2 bg-yellow-50 dark:bg-yellow-900 rounded text-xs text-yellow-800 dark:text-yellow-200">
      Configuration for "{{ strategyType }}" not yet implemented.
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

const props = defineProps<{
  modelValue: any
  strategyType: string
}>()

const emit = defineEmits(['update:modelValue'])

const localData = ref({
  profitTarget: 5,
  stopLoss: 2,
  useTrailingStop: false,
  trailingDistance: 1,
  maxHoldTime: 'none',
  usePartialProfits: false,
  ...props.modelValue
})

const isStrategySupported = computed(() => {
  return ['momentum', 'mean_reversion', 'trend_following', 'breakout'].includes(props.strategyType)
})

watch(localData, (newValue) => {
  emit('update:modelValue', newValue)
}, { deep: true })

watch(() => props.modelValue, (newValue) => {
  Object.assign(localData.value, newValue)
}, { deep: true })
</script>