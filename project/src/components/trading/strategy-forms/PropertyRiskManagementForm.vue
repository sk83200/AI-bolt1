<template>
  <div class="space-y-3">
    <!-- Max Risk Per Trade -->
    <div>
      <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Max Risk Per Trade (%)</label>
      <input
        v-model.number="localData.maxRiskPerTrade"
        type="number"
        min="0"
        max="10"
        step="0.1"
        class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
      />
    </div>

    <!-- Max Portfolio Risk -->
    <div>
      <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Max Portfolio Risk (%)</label>
      <input
        v-model.number="localData.maxPortfolioRisk"
        type="number"
        min="0"
        max="50"
        step="0.5"
        class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
      />
    </div>

    <!-- Max Positions -->
    <div>
      <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Max Positions</label>
      <input
        v-model.number="localData.maxPositions"
        type="number"
        min="1"
        max="50"
        class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
      />
    </div>

    <!-- Max Daily Drawdown -->
    <div>
      <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Max Daily Drawdown (%)</label>
      <input
        v-model.number="localData.maxDailyDrawdown"
        type="number"
        min="0"
        max="20"
        step="0.5"
        class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
      />
    </div>

    <!-- Risk Management Options -->
    <div class="space-y-2">
      <div class="flex items-center space-x-2">
        <input
          v-model="localData.stopTradingOnDrawdown"
          type="checkbox"
          class="h-3 w-3 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
        />
        <label class="text-xs text-gray-700 dark:text-gray-300">Stop trading on drawdown</label>
      </div>
      <div class="flex items-center space-x-2">
        <input
          v-model="localData.adjustPositionForVolatility"
          type="checkbox"
          class="h-3 w-3 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
        />
        <label class="text-xs text-gray-700 dark:text-gray-300">Adjust for volatility</label>
      </div>
      <div class="flex items-center space-x-2">
        <input
          v-model="localData.avoidEarningsWeek"
          type="checkbox"
          class="h-3 w-3 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
        />
        <label class="text-xs text-gray-700 dark:text-gray-300">Avoid earnings week</label>
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
  maxRiskPerTrade: 2,
  maxPortfolioRisk: 10,
  maxPositions: 5,
  maxDailyDrawdown: 5,
  stopTradingOnDrawdown: true,
  adjustPositionForVolatility: true,
  avoidEarningsWeek: true,
  ...props.modelValue
})

watch(localData, (newValue) => {
  emit('update:modelValue', newValue)
}, { deep: true })

watch(() => props.modelValue, (newValue) => {
  Object.assign(localData.value, newValue)
}, { deep: true })
</script>