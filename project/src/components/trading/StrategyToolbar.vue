<template>
  <div class="bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 p-4">
    <div class="flex items-center justify-between">
      <!-- Left side buttons -->
      <div class="flex space-x-2">
        <button 
          @click="$emit('create-strategy')"
          class="btn btn-primary flex items-center"
        >
          <PlusIcon class="h-5 w-5 mr-1" />
          Create Strategy
        </button>
        
        <button 
          @click="$emit('run-backtest')"
          class="btn btn-secondary flex items-center"
          :disabled="!hasActiveStrategy"
        >
          <PlayIcon class="h-5 w-5 mr-1" />
          Run Backtest
        </button>
      </div>
      
      <!-- Right side buttons -->
      <div class="flex space-x-2">
        <button 
          @click="$emit('compare-strategies')"
          class="btn btn-secondary flex items-center"
          :disabled="!hasStrategies"
        >
          <ChartBarIcon class="h-5 w-5 mr-1" />
          Compare
        </button>
        
        <button 
          @click="$emit('forecast')"
          class="btn btn-secondary flex items-center"
        >
          <ArrowTrendingUpIcon class="h-5 w-5 mr-1" />
          Forecast
        </button>
        
        <button 
          @click="$emit('portfolio')"
          class="btn btn-secondary flex items-center"
        >
          <WalletIcon class="h-5 w-5 mr-1" />
          Portfolio
        </button>
        
        <button 
          @click="exportData"
          class="btn btn-secondary flex items-center"
          :disabled="!hasData"
        >
          <ArrowDownTrayIcon class="h-5 w-5 mr-1" />
          Export
        </button>
      </div>
    </div>
    
    <!-- Pro feature alert -->
    <div 
      v-if="showProAlert"
      class="mt-4 p-4 bg-primary-50 dark:bg-primary-900 rounded-lg flex items-center justify-between"
    >
      <div class="flex items-center">
        <SparklesIcon class="h-5 w-5 text-primary-600 mr-2" />
        <span class="text-sm text-primary-800 dark:text-primary-200">
          Upgrade to Pro to unlock all features
        </span>
      </div>
      <button 
        @click="upgradeToPro"
        class="btn bg-primary-600 hover:bg-primary-700 text-white text-sm"
      >
        Upgrade Now
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { 
  PlusIcon,
  PlayIcon,
  ChartBarIcon,
  ArrowTrendingUpIcon,
  WalletIcon,
  ArrowDownTrayIcon,
  SparklesIcon
} from '@heroicons/vue/24/outline'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()

// Props
const props = withDefaults(defineProps<{
  hasActiveStrategy?: boolean
  hasStrategies?: boolean
  hasData?: boolean
}>(), {
  hasActiveStrategy: false,
  hasStrategies: false,
  hasData: false
})

// Emits
defineEmits([
  'create-strategy',
  'run-backtest',
  'compare-strategies',
  'forecast',
  'portfolio'
])

// Computed
const showProAlert = computed(() => !authStore.isProUser)

// Methods
const upgradeToPro = () => {
  authStore.upgradeToPro()
}

const exportData = () => {
  // Implementation for exporting data
  console.log('Exporting data...')
}
</script>