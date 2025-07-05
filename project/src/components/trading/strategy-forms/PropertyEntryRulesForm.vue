<template>
  <div class="space-y-3">
    <div v-if="isStrategySupported">
      <!-- Entry Conditions -->
      <div>
        <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Entry Conditions</label>
        <div class="space-y-1">
          <div 
            v-for="(condition, index) in localData.conditions" 
            :key="index"
            class="flex items-center space-x-1"
          >
            <input
              v-model="condition.text"
              type="text"
              class="flex-1 px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
              placeholder="e.g., RSI > 70"
            />
            <button 
              @click="removeCondition(index)"
              class="px-1 py-1 text-xs text-red-600 hover:text-red-700"
              v-if="localData.conditions.length > 1"
            >
              ×
            </button>
          </div>
          <button 
            @click="addCondition"
            class="text-xs text-primary-600 hover:text-primary-700"
          >
            + Add Condition
          </button>
        </div>
      </div>

      <!-- Confirmation Bars -->
      <div>
        <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Confirmation Bars</label>
        <input
          v-model.number="localData.confirmationBars"
          type="number"
          min="0"
          max="10"
          class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
        />
      </div>

      <!-- Market Session -->
      <div>
        <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Market Session</label>
        <select v-model="localData.marketSession" class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white">
          <option value="any">Any Time</option>
          <option value="market_open">Market Open</option>
          <option value="market_close">Market Close</option>
        </select>
      </div>

      <!-- Volume Filter -->
      <div class="flex items-center space-x-2">
        <input
          v-model="localData.requireVolumeFilter"
          type="checkbox"
          class="h-3 w-3 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
        />
        <label class="text-xs text-gray-700 dark:text-gray-300">Require volume filter</label>
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
  conditions: [{ text: '', operator: 'AND' }],
  confirmationBars: 1,
  marketSession: 'any',
  requireVolumeFilter: false,
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