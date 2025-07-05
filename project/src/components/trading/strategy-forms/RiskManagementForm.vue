<template>
  <div class="space-y-4">
    <div class="space-y-4">
      <!-- Portfolio Risk Limits -->
      <div>
        <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Portfolio Risk Limits</h4>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">Max Risk Per Trade (%)</label>
            <input
              v-model.number="localData.maxRiskPerTrade"
              type="number"
              min="0"
              max="10"
              step="0.1"
              class="input"
            />
          </div>
          <div>
            <label class="label">Max Portfolio Risk (%)</label>
            <input
              v-model.number="localData.maxPortfolioRisk"
              type="number"
              min="0"
              max="50"
              step="0.5"
              class="input"
            />
          </div>
          <div>
            <label class="label">Max Positions</label>
            <input
              v-model.number="localData.maxPositions"
              type="number"
              min="1"
              max="50"
              class="input"
            />
          </div>
          <div>
            <label class="label">Max Correlation</label>
            <input
              v-model.number="localData.maxCorrelation"
              type="number"
              min="0"
              max="1"
              step="0.1"
              class="input"
            />
          </div>
        </div>
      </div>

      <!-- Drawdown Controls -->
      <div>
        <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Drawdown Controls</h4>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">Max Daily Drawdown (%)</label>
            <input
              v-model.number="localData.maxDailyDrawdown"
              type="number"
              min="0"
              max="20"
              step="0.5"
              class="input"
            />
          </div>
          <div>
            <label class="label">Max Total Drawdown (%)</label>
            <input
              v-model.number="localData.maxTotalDrawdown"
              type="number"
              min="0"
              max="50"
              step="1"
              class="input"
            />
          </div>
        </div>
        
        <div class="space-y-2 mt-3">
          <div class="flex items-center space-x-2">
            <input
              v-model="localData.stopTradingOnDrawdown"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label class="text-sm text-gray-700 dark:text-gray-300">Stop trading when drawdown limit reached</label>
          </div>
          <div class="flex items-center space-x-2">
            <input
              v-model="localData.reducePositionOnDrawdown"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label class="text-sm text-gray-700 dark:text-gray-300">Reduce position size during drawdown</label>
          </div>
        </div>
      </div>

      <!-- Volatility Management -->
      <div>
        <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Volatility Management</h4>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">Volatility Adjustment</label>
            <select v-model="localData.volatilityAdjustment" class="input">
              <option value="none">None</option>
              <option value="atr">ATR-based</option>
              <option value="vix">VIX-based</option>
              <option value="realized">Realized Volatility</option>
            </select>
          </div>
          <div v-if="localData.volatilityAdjustment !== 'none'">
            <label class="label">Volatility Threshold</label>
            <input
              v-model.number="localData.volatilityThreshold"
              type="number"
              min="0"
              step="0.1"
              class="input"
            />
          </div>
        </div>
        
        <div class="flex items-center space-x-2 mt-2">
          <input
            v-model="localData.adjustPositionForVolatility"
            type="checkbox"
            class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
          />
          <label class="text-sm text-gray-700 dark:text-gray-300">Adjust position size based on volatility</label>
        </div>
      </div>

      <!-- Market Conditions -->
      <div>
        <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Market Condition Filters</h4>
        <div class="space-y-2">
          <div class="flex items-center space-x-2">
            <input
              v-model="localData.avoidHighVolatility"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label class="text-sm text-gray-700 dark:text-gray-300">Avoid trading during high volatility periods</label>
          </div>
          <div class="flex items-center space-x-2">
            <input
              v-model="localData.avoidEarningsWeek"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label class="text-sm text-gray-700 dark:text-gray-300">Avoid trading during earnings week</label>
          </div>
          <div class="flex items-center space-x-2">
            <input
              v-model="localData.avoidFOMC"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label class="text-sm text-gray-700 dark:text-gray-300">Avoid trading around FOMC meetings</label>
          </div>
          <div class="flex items-center space-x-2">
            <input
              v-model="localData.requireMarketTrend"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label class="text-sm text-gray-700 dark:text-gray-300">Require favorable market trend</label>
          </div>
        </div>
      </div>

      <!-- Emergency Controls -->
      <div>
        <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Emergency Controls</h4>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">Circuit Breaker Loss (%)</label>
            <input
              v-model.number="localData.circuitBreakerLoss"
              type="number"
              min="0"
              max="20"
              step="0.5"
              class="input"
            />
          </div>
          <div>
            <label class="label">Emergency Exit Method</label>
            <select v-model="localData.emergencyExitMethod" class="input">
              <option value="market">Market Orders</option>
              <option value="limit">Limit Orders</option>
              <option value="gradual">Gradual Exit</option>
            </select>
          </div>
        </div>
        
        <div class="flex items-center space-x-2 mt-2">
          <input
            v-model="localData.enableEmergencyStop"
            type="checkbox"
            class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
          />
          <label class="text-sm text-gray-700 dark:text-gray-300">Enable emergency stop functionality</label>
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
  maxRiskPerTrade: 2,
  maxPortfolioRisk: 10,
  maxPositions: 5,
  maxCorrelation: 0.7,
  maxDailyDrawdown: 5,
  maxTotalDrawdown: 15,
  stopTradingOnDrawdown: true,
  reducePositionOnDrawdown: true,
  volatilityAdjustment: 'atr',
  volatilityThreshold: 20,
  adjustPositionForVolatility: true,
  avoidHighVolatility: true,
  avoidEarningsWeek: true,
  avoidFOMC: true,
  requireMarketTrend: false,
  circuitBreakerLoss: 10,
  emergencyExitMethod: 'market',
  enableEmergencyStop: true,
  ...props.modelValue
})

watch(localData, (newValue) => {
  emit('update:modelValue', newValue)
}, { deep: true })

watch(() => props.modelValue, (newValue) => {
  Object.assign(localData.value, newValue)
}, { deep: true })
</script>