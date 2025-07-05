<template>
  <div class="space-y-3">
    <!-- Position Sizing Method -->
    <div>
      <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Sizing Method</label>
      <select v-model="localData.method" class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white">
        <option value="fixed_amount">Fixed Amount</option>
        <option value="fixed_percentage">Fixed Percentage</option>
        <option value="risk_based">Risk-Based</option>
        <option value="volatility_adjusted">Volatility Adjusted</option>
        <option value="kelly_criterion">Kelly Criterion</option>
      </select>
    </div>

    <!-- Method-specific parameters -->
    <div v-if="localData.method === 'fixed_amount'">
      <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Fixed Amount ($)</label>
      <input
        v-model.number="localData.fixedAmount"
        type="number"
        min="0"
        step="100"
        class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
      />
    </div>

    <div v-else-if="localData.method === 'fixed_percentage'">
      <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Portfolio Percentage (%)</label>
      <input
        v-model.number="localData.portfolioPercentage"
        type="number"
        min="0"
        max="100"
        step="0.5"
        class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
      />
    </div>

    <div v-else-if="localData.method === 'risk_based'">
      <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Risk Per Trade (%)</label>
      <input
        v-model.number="localData.riskPerTrade"
        type="number"
        min="0"
        max="10"
        step="0.1"
        class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
      />
    </div>

    <div v-else-if="localData.method === 'volatility_adjusted'">
      <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">ATR Multiplier</label>
      <input
        v-model.number="localData.atrMultiplier"
        type="number"
        min="0"
        step="0.1"
        class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
      />
    </div>

    <!-- Position Limits -->
    <div>
      <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Max Position Size ($)</label>
      <input
        v-model.number="localData.maxPositionSize"
        type="number"
        min="0"
        step="1000"
        class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
      />
    </div>

    <!-- Dynamic Adjustments -->
    <div class="space-y-2">
      <div class="flex items-center space-x-2">
        <input
          v-model="localData.adjustForWinStreak"
          type="checkbox"
          class="h-3 w-3 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
        />
        <label class="text-xs text-gray-700 dark:text-gray-300">Adjust for win streaks</label>
      </div>
      <div class="flex items-center space-x-2">
        <input
          v-model="localData.adjustForCorrelation"
          type="checkbox"
          class="h-3 w-3 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
        />
        <label class="text-xs text-gray-700 dark:text-gray-300">Reduce for correlation</label>
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
  method: 'risk_based',
  fixedAmount: 10000,
  portfolioPercentage: 10,
  riskPerTrade: 2,
  atrMultiplier: 2,
  maxPositionSize: 50000,
  adjustForWinStreak: false,
  adjustForCorrelation: true,
  ...props.modelValue
})

watch(localData, (newValue) => {
  emit('update:modelValue', newValue)
}, { deep: true })

watch(() => props.modelValue, (newValue) => {
  Object.assign(localData.value, newValue)
}, { deep: true })
</script>