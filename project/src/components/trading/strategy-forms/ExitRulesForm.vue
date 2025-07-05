<template>
  <div class="space-y-4">
    <div v-if="isStrategySupported" class="space-y-4">
      <!-- Profit Taking -->
      <div>
        <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Profit Taking</h4>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">Profit Target (%)</label>
            <input
              v-model.number="localData.profitTarget"
              type="number"
              min="0"
              step="0.1"
              class="input"
            />
          </div>
          <div>
            <label class="label">Profit Target Type</label>
            <select v-model="localData.profitTargetType" class="input">
              <option value="percentage">Percentage</option>
              <option value="atr_multiple">ATR Multiple</option>
              <option value="resistance">Resistance Level</option>
              <option value="fibonacci">Fibonacci Level</option>
            </select>
          </div>
        </div>
        
        <div class="flex items-center space-x-2 mt-2">
          <input
            v-model="localData.usePartialProfits"
            type="checkbox"
            class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
          />
          <label class="text-sm text-gray-700 dark:text-gray-300">Use partial profit taking</label>
        </div>
        
        <div v-if="localData.usePartialProfits" class="mt-2 grid grid-cols-2 gap-4">
          <div>
            <label class="label">First Target (%)</label>
            <input
              v-model.number="localData.firstProfitTarget"
              type="number"
              min="0"
              step="0.1"
              class="input"
            />
          </div>
          <div>
            <label class="label">Position Size to Close (%)</label>
            <input
              v-model.number="localData.firstProfitSize"
              type="number"
              min="0"
              max="100"
              class="input"
            />
          </div>
        </div>
      </div>

      <!-- Stop Loss -->
      <div>
        <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Stop Loss</h4>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">Stop Loss (%)</label>
            <input
              v-model.number="localData.stopLoss"
              type="number"
              min="0"
              step="0.1"
              class="input"
            />
          </div>
          <div>
            <label class="label">Stop Loss Type</label>
            <select v-model="localData.stopLossType" class="input">
              <option value="percentage">Percentage</option>
              <option value="atr_multiple">ATR Multiple</option>
              <option value="support">Support Level</option>
              <option value="previous_low">Previous Low</option>
            </select>
          </div>
        </div>
        
        <div class="space-y-2 mt-2">
          <div class="flex items-center space-x-2">
            <input
              v-model="localData.useTrailingStop"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label class="text-sm text-gray-700 dark:text-gray-300">Use trailing stop</label>
          </div>
          
          <div v-if="localData.useTrailingStop" class="grid grid-cols-2 gap-4">
            <div>
              <label class="label">Trailing Distance (%)</label>
              <input
                v-model.number="localData.trailingDistance"
                type="number"
                min="0"
                step="0.1"
                class="input"
              />
            </div>
            <div>
              <label class="label">Activation Threshold (%)</label>
              <input
                v-model.number="localData.trailingActivation"
                type="number"
                min="0"
                step="0.1"
                class="input"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- Time-based Exits -->
      <div>
        <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Time-based Exits</h4>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="label">Max Hold Time</label>
            <select v-model="localData.maxHoldTime" class="input">
              <option value="none">No Limit</option>
              <option value="intraday">Close at End of Day</option>
              <option value="1_day">1 Day</option>
              <option value="3_days">3 Days</option>
              <option value="1_week">1 Week</option>
              <option value="1_month">1 Month</option>
            </select>
          </div>
          <div>
            <label class="label">Exit Time (if intraday)</label>
            <input
              v-model="localData.exitTime"
              type="time"
              class="input"
              :disabled="localData.maxHoldTime !== 'intraday'"
            />
          </div>
        </div>
      </div>

      <!-- Technical Exit Conditions -->
      <div>
        <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Technical Exit Conditions</h4>
        <div class="space-y-2">
          <div 
            v-for="(condition, index) in localData.exitConditions" 
            :key="index"
            class="flex items-center space-x-2"
          >
            <input
              v-model="condition.text"
              type="text"
              class="flex-1 input"
              placeholder="e.g., RSI < 30"
            />
            <select v-model="condition.operator" class="input w-20">
              <option value="AND">AND</option>
              <option value="OR">OR</option>
            </select>
            <button 
              @click="removeExitCondition(index)"
              class="btn btn-secondary text-sm px-2 py-1"
            >
              Remove
            </button>
          </div>
          <button 
            @click="addExitCondition"
            class="btn btn-primary text-sm"
          >
            Add Exit Condition
          </button>
        </div>
      </div>

      <!-- Advanced Exit Settings -->
      <div class="space-y-2">
        <div class="flex items-center space-x-2">
          <input
            v-model="localData.breakEvenStop"
            type="checkbox"
            class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
          />
          <label class="text-sm text-gray-700 dark:text-gray-300">Move stop to break-even after first profit target</label>
        </div>
        <div class="flex items-center space-x-2">
          <input
            v-model="localData.scaleOut"
            type="checkbox"
            class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
          />
          <label class="text-sm text-gray-700 dark:text-gray-300">Scale out of position gradually</label>
        </div>
      </div>
    </div>

    <div v-else class="p-4 bg-yellow-50 dark:bg-yellow-900 rounded-lg">
      <p class="text-sm text-yellow-800 dark:text-yellow-200">
        Exit rules configuration for "{{ strategyType }}" strategy is not yet implemented.
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
  profitTarget: 5,
  profitTargetType: 'percentage',
  usePartialProfits: false,
  firstProfitTarget: 2.5,
  firstProfitSize: 50,
  stopLoss: 2,
  stopLossType: 'percentage',
  useTrailingStop: false,
  trailingDistance: 1,
  trailingActivation: 2,
  maxHoldTime: 'none',
  exitTime: '15:30',
  exitConditions: [],
  breakEvenStop: false,
  scaleOut: false,
  ...props.modelValue
})

const isStrategySupported = computed(() => {
  return ['momentum', 'mean_reversion', 'trend_following', 'breakout'].includes(props.strategyType)
})

const addExitCondition = () => {
  localData.value.exitConditions.push({ text: '', operator: 'OR' })
}

const removeExitCondition = (index: number) => {
  localData.value.exitConditions.splice(index, 1)
}

watch(localData, (newValue) => {
  emit('update:modelValue', newValue)
}, { deep: true })

watch(() => props.modelValue, (newValue) => {
  Object.assign(localData.value, newValue)
}, { deep: true })
</script>