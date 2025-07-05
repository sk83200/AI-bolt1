<template>
  <div class="space-y-4">
    <div v-if="isStrategySupported" class="space-y-4">
      <!-- Entry Conditions -->
      <div>
        <label class="label">Entry Conditions</label>
        <div class="space-y-2">
          <div 
            v-for="(condition, index) in localData.conditions" 
            :key="index"
            class="flex items-center space-x-2"
          >
            <input
              v-model="condition.text"
              type="text"
              class="flex-1 input"
              placeholder="e.g., RSI > 70"
            />
            <select v-model="condition.operator" class="input w-20">
              <option value="AND">AND</option>
              <option value="OR">OR</option>
            </select>
            <button 
              @click="removeCondition(index)"
              class="btn btn-secondary text-sm px-2 py-1"
            >
              Remove
            </button>
          </div>
          <button 
            @click="addCondition"
            class="btn btn-primary text-sm"
          >
            Add Condition
          </button>
        </div>
      </div>

      <!-- Entry Timing -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label class="label">Market Session</label>
          <select v-model="localData.marketSession" class="input">
            <option value="any">Any Time</option>
            <option value="market_open">Market Open (First Hour)</option>
            <option value="market_close">Market Close (Last Hour)</option>
            <option value="custom">Custom Hours</option>
          </select>
        </div>
        <div v-if="localData.marketSession === 'custom'">
          <label class="label">Trading Hours (24h format)</label>
          <div class="flex space-x-2">
            <input
              v-model="localData.startTime"
              type="time"
              class="input"
            />
            <span class="self-center">to</span>
            <input
              v-model="localData.endTime"
              type="time"
              class="input"
            />
          </div>
        </div>
      </div>

      <!-- Volume and Liquidity Filters -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label class="label">Minimum Volume</label>
          <input
            v-model.number="localData.minVolume"
            type="number"
            min="0"
            class="input"
            placeholder="e.g., 1000000"
          />
        </div>
        <div>
          <label class="label">Volume Filter Type</label>
          <select v-model="localData.volumeFilterType" class="input">
            <option value="absolute">Absolute Volume</option>
            <option value="average">Above Average Volume</option>
            <option value="relative">Relative Volume Ratio</option>
          </select>
        </div>
      </div>

      <!-- Confirmation Settings -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div>
          <label class="label">Confirmation Bars</label>
          <input
            v-model.number="localData.confirmationBars"
            type="number"
            min="0"
            max="10"
            class="input"
          />
        </div>
        <div>
          <label class="label">Max Entries Per Day</label>
          <input
            v-model.number="localData.maxEntriesPerDay"
            type="number"
            min="1"
            max="50"
            class="input"
          />
        </div>
        <div>
          <label class="label">Entry Order Type</label>
          <select v-model="localData.orderType" class="input">
            <option value="market">Market Order</option>
            <option value="limit">Limit Order</option>
            <option value="stop">Stop Order</option>
          </select>
        </div>
      </div>

      <!-- Advanced Entry Settings -->
      <div class="space-y-4">
        <div class="flex items-center space-x-2">
          <input
            v-model="localData.requireGapUp"
            type="checkbox"
            class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
          />
          <label class="text-sm text-gray-700 dark:text-gray-300">Require gap up for long entries</label>
        </div>
        <div class="flex items-center space-x-2">
          <input
            v-model="localData.avoidEarnings"
            type="checkbox"
            class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
          />
          <label class="text-sm text-gray-700 dark:text-gray-300">Avoid entries before earnings</label>
        </div>
        <div class="flex items-center space-x-2">
          <input
            v-model="localData.requireTrendAlignment"
            type="checkbox"
            class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
          />
          <label class="text-sm text-gray-700 dark:text-gray-300">Require higher timeframe trend alignment</label>
        </div>
      </div>
    </div>

    <div v-else class="p-4 bg-yellow-50 dark:bg-yellow-900 rounded-lg">
      <p class="text-sm text-yellow-800 dark:text-yellow-200">
        Entry rules configuration for "{{ strategyType }}" strategy is not yet implemented.
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
  conditions: [
    { text: '', operator: 'AND' }
  ],
  marketSession: 'any',
  startTime: '09:30',
  endTime: '16:00',
  minVolume: 0,
  volumeFilterType: 'absolute',
  confirmationBars: 1,
  maxEntriesPerDay: 5,
  orderType: 'market',
  requireGapUp: false,
  avoidEarnings: true,
  requireTrendAlignment: false,
  ...props.modelValue
})

const isStrategySupported = computed(() => {
  return ['momentum', 'mean_reversion', 'trend_following', 'breakout'].includes(props.strategyType)
})

const addCondition = () => {
  localData.value.conditions.push({ text: '', operator: 'AND' })
}

const removeCondition = (index: number) => {
  if (localData.value.conditions.length > 1) {
    localData.value.conditions.splice(index, 1)
  }
}

watch(localData, (newValue) => {
  emit('update:modelValue', newValue)
}, { deep: true })

watch(() => props.modelValue, (newValue) => {
  Object.assign(localData.value, newValue)
}, { deep: true })
</script>