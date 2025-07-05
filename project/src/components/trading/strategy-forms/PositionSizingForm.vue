<template>
  <div class="space-y-4">
    <div class="space-y-4">
      <!-- Position Sizing Method -->
      <div>
        <label class="label">Position Sizing Method</label>
        <select v-model="localData.method" class="input" @change="onMethodChange">
          <option value="fixed_amount">Fixed Amount</option>
          <option value="fixed_percentage">Fixed Percentage</option>
          <option value="risk_based">Risk-Based</option>
          <option value="volatility_adjusted">Volatility Adjusted</option>
          <option value="kelly_criterion">Kelly Criterion</option>
          <option value="optimal_f">Optimal F</option>
          <option value="equal_weight">Equal Weight</option>
        </select>
      </div>

      <!-- Method-specific parameters -->
      <div v-if="localData.method === 'fixed_amount'" class="space-y-4">
        <div>
          <label class="label">Fixed Amount ($)</label>
          <input
            v-model.number="localData.fixedAmount"
            type="number"
            min="0"
            step="100"
            class="input"
          />
        </div>
      </div>

      <div v-else-if="localData.method === 'fixed_percentage'" class="space-y-4">
        <div>
          <label class="label">Portfolio Percentage (%)</label>
          <input
            v-model.number="localData.portfolioPercentage"
            type="number"
            min="0"
            max="100"
            step="0.5"
            class="input"
          />
        </div>
      </div>

      <div v-else-if="localData.method === 'risk_based'" class="space-y-4">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">Risk Per Trade (%)</label>
            <input
              v-model.number="localData.riskPerTrade"
              type="number"
              min="0"
              max="10"
              step="0.1"
              class="input"
            />
          </div>
          <div>
            <label class="label">Stop Loss Distance (%)</label>
            <input
              v-model.number="localData.stopLossDistance"
              type="number"
              min="0"
              step="0.1"
              class="input"
            />
          </div>
        </div>
      </div>

      <div v-else-if="localData.method === 'volatility_adjusted'" class="space-y-4">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">Base Position Size (%)</label>
            <input
              v-model.number="localData.basePositionSize"
              type="number"
              min="0"
              max="50"
              step="0.5"
              class="input"
            />
          </div>
          <div>
            <label class="label">ATR Multiplier</label>
            <input
              v-model.number="localData.atrMultiplier"
              type="number"
              min="0"
              step="0.1"
              class="input"
            />
          </div>
          <div>
            <label class="label">Volatility Lookback Period</label>
            <input
              v-model.number="localData.volatilityLookback"
              type="number"
              min="5"
              max="100"
              class="input"
            />
          </div>
          <div>
            <label class="label">Max Volatility Adjustment</label>
            <input
              v-model.number="localData.maxVolatilityAdjustment"
              type="number"
              min="0.1"
              max="5"
              step="0.1"
              class="input"
            />
          </div>
        </div>
      </div>

      <div v-else-if="localData.method === 'kelly_criterion'" class="space-y-4">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">Win Rate (%)</label>
            <input
              v-model.number="localData.winRate"
              type="number"
              min="0"
              max="100"
              step="0.1"
              class="input"
            />
          </div>
          <div>
            <label class="label">Average Win/Loss Ratio</label>
            <input
              v-model.number="localData.winLossRatio"
              type="number"
              min="0"
              step="0.1"
              class="input"
            />
          </div>
          <div>
            <label class="label">Kelly Fraction Limit (%)</label>
            <input
              v-model.number="localData.kellyFractionLimit"
              type="number"
              min="0"
              max="50"
              step="1"
              class="input"
            />
          </div>
        </div>
      </div>

      <!-- Position Limits -->
      <div>
        <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Position Limits</h4>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">Minimum Position Size ($)</label>
            <input
              v-model.number="localData.minPositionSize"
              type="number"
              min="0"
              step="100"
              class="input"
            />
          </div>
          <div>
            <label class="label">Maximum Position Size ($)</label>
            <input
              v-model.number="localData.maxPositionSize"
              type="number"
              min="0"
              step="1000"
              class="input"
            />
          </div>
          <div>
            <label class="label">Max Position as % of Portfolio</label>
            <input
              v-model.number="localData.maxPositionPercentage"
              type="number"
              min="0"
              max="100"
              step="1"
              class="input"
            />
          </div>
          <div>
            <label class="label">Max Position as % of Daily Volume</label>
            <input
              v-model.number="localData.maxVolumePercentage"
              type="number"
              min="0"
              max="50"
              step="0.5"
              class="input"
            />
          </div>
        </div>
      </div>

      <!-- Dynamic Adjustments -->
      <div>
        <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Dynamic Adjustments</h4>
        <div class="space-y-2">
          <div class="flex items-center space-x-2">
            <input
              v-model="localData.adjustForWinStreak"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label class="text-sm text-gray-700 dark:text-gray-300">Increase size during winning streaks</label>
          </div>
          <div class="flex items-center space-x-2">
            <input
              v-model="localData.adjustForLossStreak"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label class="text-sm text-gray-700 dark:text-gray-300">Decrease size during losing streaks</label>
          </div>
          <div class="flex items-center space-x-2">
            <input
              v-model="localData.adjustForMarketConditions"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label class="text-sm text-gray-700 dark:text-gray-300">Adjust for market conditions</label>
          </div>
          <div class="flex items-center space-x-2">
            <input
              v-model="localData.adjustForCorrelation"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label class="text-sm text-gray-700 dark:text-gray-300">Reduce size for correlated positions</label>
          </div>
        </div>
      </div>

      <!-- Scaling Options -->
      <div>
        <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Scaling Options</h4>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">Scale-In Method</label>
            <select v-model="localData.scaleInMethod" class="input">
              <option value="none">No Scaling</option>
              <option value="equal_parts">Equal Parts</option>
              <option value="pyramid">Pyramid (Decreasing)</option>
              <option value="reverse_pyramid">Reverse Pyramid (Increasing)</option>
            </select>
          </div>
          <div v-if="localData.scaleInMethod !== 'none'">
            <label class="label">Number of Scale-In Orders</label>
            <input
              v-model.number="localData.scaleInOrders"
              type="number"
              min="2"
              max="10"
              class="input"
            />
          </div>
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
  method: 'risk_based',
  // Fixed amount
  fixedAmount: 10000,
  // Fixed percentage
  portfolioPercentage: 10,
  // Risk-based
  riskPerTrade: 2,
  stopLossDistance: 2,
  // Volatility adjusted
  basePositionSize: 10,
  atrMultiplier: 2,
  volatilityLookback: 20,
  maxVolatilityAdjustment: 3,
  // Kelly criterion
  winRate: 55,
  winLossRatio: 1.5,
  kellyFractionLimit: 25,
  // Position limits
  minPositionSize: 1000,
  maxPositionSize: 50000,
  maxPositionPercentage: 20,
  maxVolumePercentage: 5,
  // Dynamic adjustments
  adjustForWinStreak: false,
  adjustForLossStreak: true,
  adjustForMarketConditions: true,
  adjustForCorrelation: true,
  // Scaling
  scaleInMethod: 'none',
  scaleInOrders: 3,
  ...props.modelValue
})

const onMethodChange = () => {
  // Reset method-specific values when method changes
  // This helps maintain clean data structure
}

watch(localData, (newValue) => {
  emit('update:modelValue', newValue)
}, { deep: true })

watch(() => props.modelValue, (newValue) => {
  Object.assign(localData.value, newValue)
}, { deep: true })
</script>