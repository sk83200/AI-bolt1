<template>
  <div class="card">
    <h2 class="text-lg font-medium text-gray-900 dark:text-white mb-4">Compare Strategies</h2>
    
    <!-- Strategy selector -->
    <div class="mb-6">
      <label class="label">Select Strategies to Compare</label>
      <div class="space-y-2">
        <div 
          v-for="strategy in strategies" 
          :key="strategy.id"
          class="flex items-center"
        >
          <input
            :id="strategy.id"
            v-model="selectedStrategies"
            :value="strategy.id"
            type="checkbox"
            class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
          />
          <label :for="strategy.id" class="ml-2 block text-sm text-gray-900 dark:text-gray-300">
            {{ strategy.name }}
          </label>
        </div>
      </div>
    </div>
    
    <!-- Comparison chart -->
    <div v-if="selectedStrategies.length > 0" class="mb-6">
      <canvas ref="chartRef" class="w-full h-64"></canvas>
    </div>
    
    <!-- Metrics table -->
    <div v-if="selectedStrategies.length > 0" class="overflow-x-auto">
      <table class="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
        <thead class="bg-gray-50 dark:bg-gray-800">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
              Strategy
            </th>
            <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
              CAGR
            </th>
            <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
              Sharpe
            </th>
            <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
              Max DD
            </th>
            <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
              Win Rate
            </th>
          </tr>
        </thead>
        <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
          <tr 
            v-for="strategy in selectedStrategyData" 
            :key="strategy.id"
            class="hover:bg-gray-50 dark:hover:bg-gray-700"
          >
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 dark:text-white">
              {{ strategy.name }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm text-gray-500 dark:text-gray-400">
              {{ formatPercent(strategy.metrics.cagr) }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm text-gray-500 dark:text-gray-400">
              {{ strategy.metrics.sharpe.toFixed(2) }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm text-gray-500 dark:text-gray-400">
              {{ formatPercent(strategy.metrics.maxDrawdown) }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm text-gray-500 dark:text-gray-400">
              {{ formatPercent(strategy.metrics.winRate) }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- Export buttons -->
    <div v-if="selectedStrategies.length > 0" class="mt-4 flex justify-end space-x-3">
      <button 
        @click="exportCSV"
        class="btn btn-secondary"
      >
        Export CSV
      </button>
      <button 
        @click="exportPDF"
        class="btn btn-primary"
      >
        Export PDF
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { Chart } from 'chart.js'
import axios from 'axios'

// Sample data - replace with API calls
const strategies = ref([
  { id: '1', name: 'Moving Average Crossover' },
  { id: '2', name: 'RSI Mean Reversion' },
  { id: '3', name: 'Trend Following' }
])

const selectedStrategies = ref<string[]>([])
const chartRef = ref<HTMLCanvasElement | null>(null)
const chart = ref<Chart | null>(null)

const selectedStrategyData = computed(() => {
  // This would fetch from API in real implementation
  return selectedStrategies.value.map(id => ({
    id,
    name: strategies.value.find(s => s.id === id)?.name || '',
    metrics: {
      cagr: Math.random() * 30,
      sharpe: 1 + Math.random() * 2,
      maxDrawdown: -(Math.random() * 20),
      winRate: 40 + Math.random() * 30
    }
  }))
})

const formatPercent = (value: number) => {
  return `${value.toFixed(2)}%`
}

const initChart = () => {
  if (!chartRef.value || selectedStrategies.value.length === 0) return
  
  if (chart.value) {
    chart.value.destroy()
  }
  
  const ctx = chartRef.value.getContext('2d')
  if (!ctx) return
  
  // Create chart with selected strategy data
  chart.value = new Chart(ctx, {
    type: 'line',
    data: {
      labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
      datasets: selectedStrategyData.value.map((strategy, index) => ({
        label: strategy.name,
        data: Array(6).fill(0).map(() => Math.random() * 100),
        borderColor: `hsl(${index * 137.5}, 70%, 50%)`,
        tension: 0.1
      }))
    },
    options: {
      responsive: true,
      maintainAspectRatio: false
    }
  })
}

const exportCSV = () => {
  // Implementation for CSV export
  console.log('Exporting CSV...')
}

const exportPDF = () => {
  // Implementation for PDF export
  console.log('Exporting PDF...')
}

watch(selectedStrategies, () => {
  initChart()
})

onMounted(() => {
  initChart()
})
</script>