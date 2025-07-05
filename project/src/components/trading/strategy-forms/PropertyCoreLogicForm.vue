<template>
  <div class="space-y-3">
    <div v-if="isStrategySupported">
      <!-- Indicators -->
      <div>
        <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Indicators</label>
        <div class="space-y-1">
          <label 
            v-for="indicator in availableIndicators.slice(0, 4)" 
            :key="indicator.value"
            class="flex items-center space-x-2"
          >
            <input
              type="checkbox"
              :value="indicator.value"
              v-model="localData.indicators"
              class="h-3 w-3 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <span class="text-xs text-gray-700 dark:text-gray-300">{{ indicator.label }}</span>
          </label>
        </div>
      </div>

      <!-- Strategy-specific parameters -->
      <div v-if="strategyType === 'momentum'" class="space-y-3">
        <div>
          <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">RSI Threshold</label>
          <input
            v-model.number="localData.rsiThreshold"
            type="number"
            min="0"
            max="100"
            class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
          />
        </div>
        <div>
          <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Momentum Period</label>
          <input
            v-model.number="localData.momentumPeriod"
            type="number"
            min="1"
            max="100"
            class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
          />
        </div>
      </div>

      <div v-else-if="strategyType === 'mean_reversion'" class="space-y-3">
        <div>
          <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Oversold Level</label>
          <input
            v-model.number="localData.oversoldLevel"
            type="number"
            min="0"
            max="50"
            class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
          />
        </div>
        <div>
          <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Overbought Level</label>
          <input
            v-model.number="localData.overboughtLevel"
            type="number"
            min="50"
            max="100"
            class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
          />
        </div>
      </div>

      <!-- Common parameters -->
      <div>
        <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Lookback Period</label>
        <input
          v-model.number="localData.lookbackPeriod"
          type="number"
          min="1"
          max="100"
          class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
        />
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
  indicators: [],
  lookbackPeriod: 14,
  rsiThreshold: 70,
  momentumPeriod: 14,
  oversoldLevel: 30,
  overboughtLevel: 70,
  ...props.modelValue
})

const availableIndicators = [
  { value: 'RSI', label: 'RSI' },
  { value: 'MACD', label: 'MACD' },
  { value: 'SMA', label: 'SMA' },
  { value: 'EMA', label: 'EMA' },
  { value: 'BB', label: 'Bollinger' },
  { value: 'ADX', label: 'ADX' }
]

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