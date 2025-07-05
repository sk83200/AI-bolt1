<template>
  <div class="space-y-4">
    <div v-if="isStrategySupported" class="space-y-4">
      <!-- Indicators Selection -->
      <div>
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
              v-model="localData.indicators"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <span class="text-sm text-gray-700 dark:text-gray-300">{{ indicator.label }}</span>
          </label>
        </div>
      </div>

      <!-- Strategy-specific parameters -->
      <div v-if="strategyType === 'momentum'" class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label class="label">RSI Threshold</label>
          <input
            v-model.number="localData.rsiThreshold"
            type="number"
            min="0"
            max="100"
            class="input"
          />
        </div>
        <div>
          <label class="label">Momentum Period</label>
          <input
            v-model.number="localData.momentumPeriod"
            type="number"
            min="1"
            max="100"
            class="input"
          />
        </div>
      </div>

      <div v-else-if="strategyType === 'mean_reversion'" class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label class="label">Oversold Level</label>
          <input
            v-model.number="localData.oversoldLevel"
            type="number"
            min="0"
            max="50"
            class="input"
          />
        </div>
        <div>
          <label class="label">Overbought Level</label>
          <input
            v-model.number="localData.overboughtLevel"
            type="number"
            min="50"
            max="100"
            class="input"
          />
        </div>
        <div>
          <label class="label">Bollinger Bands Period</label>
          <input
            v-model.number="localData.bbPeriod"
            type="number"
            min="5"
            max="50"
            class="input"
          />
        </div>
        <div>
          <label class="label">Bollinger Bands Std Dev</label>
          <input
            v-model.number="localData.bbStdDev"
            type="number"
            min="1"
            max="3"
            step="0.1"
            class="input"
          />
        </div>
      </div>

      <div v-else-if="strategyType === 'trend_following'" class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label class="label">Fast MA Period</label>
          <input
            v-model.number="localData.fastMAPeriod"
            type="number"
            min="5"
            max="50"
            class="input"
          />
        </div>
        <div>
          <label class="label">Slow MA Period</label>
          <input
            v-model.number="localData.slowMAPeriod"
            type="number"
            min="20"
            max="200"
            class="input"
          />
        </div>
        <div>
          <label class="label">Trend Strength Filter</label>
          <select v-model="localData.trendFilter" class="input">
            <option value="none">None</option>
            <option value="adx">ADX</option>
            <option value="slope">MA Slope</option>
          </select>
        </div>
      </div>

      <!-- Common parameters -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label class="label">Lookback Period</label>
          <input
            v-model.number="localData.lookbackPeriod"
            type="number"
            min="1"
            max="100"
            class="input"
          />
        </div>
        <div>
          <label class="label">Confirmation Bars</label>
          <input
            v-model.number="localData.confirmationBars"
            type="number"
            min="0"
            max="5"
            class="input"
          />
        </div>
      </div>
    </div>

    <div v-else class="p-4 bg-yellow-50 dark:bg-yellow-900 rounded-lg">
      <p class="text-sm text-yellow-800 dark:text-yellow-200">
        Core logic configuration for "{{ strategyType }}" strategy is not yet implemented.
        This section will be available in a future update.
      </p>
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
  confirmationBars: 1,
  // Momentum specific
  rsiThreshold: 70,
  momentumPeriod: 14,
  // Mean reversion specific
  oversoldLevel: 30,
  overboughtLevel: 70,
  bbPeriod: 20,
  bbStdDev: 2,
  // Trend following specific
  fastMAPeriod: 10,
  slowMAPeriod: 50,
  trendFilter: 'none',
  ...props.modelValue
})

const availableIndicators = [
  { value: 'RSI', label: 'RSI' },
  { value: 'MACD', label: 'MACD' },
  { value: 'SMA', label: 'Simple MA' },
  { value: 'EMA', label: 'Exponential MA' },
  { value: 'BB', label: 'Bollinger Bands' },
  { value: 'ADX', label: 'ADX' },
  { value: 'Stochastic', label: 'Stochastic' },
  { value: 'Williams_R', label: 'Williams %R' }
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