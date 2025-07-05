<template>
  <div class="card">
    <h2 class="text-lg font-medium text-gray-900 dark:text-white mb-4">Market Forecast</h2>
    
    <!-- Input form -->
    <form @submit.prevent="runForecast" class="space-y-4">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label for="symbol" class="label">Symbol</label>
          <input
            id="symbol"
            v-model="form.symbol"
            type="text"
            class="input"
            placeholder="e.g., AAPL"
            required
          />
        </div>
        
        <div>
          <label for="lookback" class="label">Lookback Period (days)</label>
          <input
            id="lookback"
            v-model="form.lookback"
            type="number"
            min="1"
            max="365"
            class="input"
            required
          />
        </div>
        
        <div>
          <label for="horizon" class="label">Forecast Horizon (days)</label>
          <input
            id="horizon"
            v-model="form.horizon"
            type="number"
            min="1"
            max="30"
            class="input"
            required
          />
        </div>
        
        <div>
          <label for="model" class="label">Model Type</label>
          <select
            id="model"
            v-model="form.model"
            class="input"
            required
          >
            <option value="arima">ARIMA</option>
            <option value="lstm">LSTM</option>
            <option value="prophet">Prophet</option>
            <option value="ensemble">Ensemble</option>
          </select>
        </div>
      </div>
      
      <!-- AI Assistance -->
      <div class="flex items-center">
        <input
          id="aiAssist"
          v-model="form.useAI"
          type="checkbox"
          class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
        />
        <label for="aiAssist" class="ml-2 block text-sm text-gray-900 dark:text-gray-300">
          Let AI recommend best model configuration
        </label>
      </div>
      
      <div class="flex justify-end">
        <button 
          type="submit" 
          class="btn btn-primary"
          :disabled="isLoading"
        >
          {{ isLoading ? 'Running Forecast...' : 'Run Forecast' }}
        </button>
      </div>
    </form>
    
    <!-- Results -->
    <div v-if="results" class="mt-6 space-y-6">
      <!-- Forecast chart -->
      <div class="bg-white dark:bg-gray-800 rounded-lg p-4">
        <canvas ref="chartRef" class="w-full h-64"></canvas>
      </div>
      
      <!-- Metrics -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 p-4">
          <h4 class="text-sm font-medium mb-2">Mean Absolute Error</h4>
          <p class="text-2xl font-semibold text-primary-600">{{ results.metrics.mae.toFixed(2) }}</p>
        </div>
        
        <div class="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 p-4">
          <h4 class="text-sm font-medium mb-2">RMSE</h4>
          <p class="text-2xl font-semibold text-primary-600">{{ results.metrics.rmse.toFixed(2) }}</p>
        </div>
        
        <div class="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 p-4">
          <h4 class="text-sm font-medium mb-2">Hit Rate</h4>
          <p class="text-2xl font-semibold text-primary-600">{{ results.metrics.hitRate.toFixed(1) }}%</p>
        </div>
      </div>
      
      <!-- Forecast summary -->
      <div class="bg-primary-50 dark:bg-primary-900 rounded-lg p-4">
        <h4 class="text-sm font-medium mb-2">Forecast Summary</h4>
        <p class="text-sm text-gray-600 dark:text-gray-300">{{ results.summary }}</p>
      </div>
      
      <!-- Export button -->
      <div class="flex justify-end">
        <button 
          @click="exportResults" 
          class="btn btn-secondary"
        >
          Export Results
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { Chart } from 'chart.js'
import { useAuthStore } from '../../stores/auth'
import axios from 'axios'

const authStore = useAuthStore()
const isLoading = ref(false)
const chartRef = ref<HTMLCanvasElement | null>(null)
const chart = ref<Chart | null>(null)

const form = ref({
  symbol: '',
  lookback: 30,
  horizon: 5,
  model: 'ensemble',
  useAI: false
})

const results = ref<{
  metrics: {
    mae: number
    rmse: number
    hitRate: number
  }
  summary: string
  predictions: number[]
  dates: string[]
} | null>(null)

const runForecast = async () => {
  if (!authStore.isProUser) {
    alert('Forecasting is a Pro feature. Please upgrade to continue.')
    return
  }
  
  isLoading.value = true
  try {
    const response = await axios.post('/api/forecast/run', {
      ...form.value,
      userId: authStore.user?.id
    })
    
    results.value = response.data
    updateChart()
  } catch (error) {
    console.error('Error running forecast:', error)
  } finally {
    isLoading.value = false
  }
}

const updateChart = () => {
  if (!chartRef.value || !results.value) return
  
  if (chart.value) {
    chart.value.destroy()
  }
  
  const ctx = chartRef.value.getContext('2d')
  if (!ctx) return
  
  chart.value = new Chart(ctx, {
    type: 'line',
    data: {
      labels: results.value.dates,
      datasets: [
        {
          label: 'Historical',
          data: results.value.predictions.slice(0, -form.value.horizon),
          borderColor: '#4F46E5',
          tension: 0.1
        },
        {
          label: 'Forecast',
          data: results.value.predictions.slice(-form.value.horizon),
          borderColor: '#10B981',
          borderDash: [5, 5],
          tension: 0.1
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false
    }
  })
}

const exportResults = () => {
  // Implementation for exporting results
  console.log('Exporting results...')
}

watch(() => results.value, () => {
  updateChart()
})

onMounted(() => {
  if (results.value) {
    updateChart()
  }
})
</script>