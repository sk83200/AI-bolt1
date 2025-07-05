<template>
  <div class="card">
    <h2 class="text-lg font-medium text-gray-900 dark:text-white mb-4">Portfolio Allocator</h2>
    
    <!-- Input form -->
    <form @submit.prevent="generateAllocation" class="space-y-4">
      <!-- Asset selector -->
      <div>
        <label class="label">Select Assets</label>
        <div class="space-y-2">
          <div 
            v-for="asset in availableAssets" 
            :key="asset.symbol"
            class="flex items-center"
          >
            <input
              :id="asset.symbol"
              v-model="selectedAssets"
              :value="asset.symbol"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label :for="asset.symbol" class="ml-2 block text-sm text-gray-900 dark:text-gray-300">
              {{ asset.name }} ({{ asset.symbol }})
            </label>
          </div>
        </div>
      </div>
      
      <!-- Risk profile -->
      <div>
        <label for="riskProfile" class="label">Risk Profile</label>
        <select
          id="riskProfile"
          v-model="form.riskProfile"
          class="input"
          required
        >
          <option value="conservative">Conservative</option>
          <option value="moderate">Moderate</option>
          <option value="aggressive">Aggressive</option>
        </select>
      </div>
      
      <!-- Investment amount -->
      <div>
        <label for="amount" class="label">Investment Amount</label>
        <input
          id="amount"
          v-model="form.amount"
          type="number"
          min="1000"
          step="1000"
          class="input"
          required
        />
      </div>
      
      <!-- Constraints -->
      <div>
        <label class="label">Constraints</label>
        <div class="space-y-2">
          <div class="flex items-center">
            <input
              id="maxAllocation"
              v-model="form.constraints.maxAllocation"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label for="maxAllocation" class="ml-2 block text-sm text-gray-900 dark:text-gray-300">
              Maximum allocation per asset (30%)
            </label>
          </div>
          <div class="flex items-center">
            <input
              id="sectorDiversification"
              v-model="form.constraints.sectorDiversification"
              type="checkbox"
              class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <label for="sectorDiversification" class="ml-2 block text-sm text-gray-900 dark:text-gray-300">
              Sector diversification
            </label>
          </div>
        </div>
      </div>
      
      <!-- AI optimization -->
      <div class="flex items-center">
        <input
          id="useAI"
          v-model="form.useAI"
          type="checkbox"
          class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
        />
        <label for="useAI" class="ml-2 block text-sm text-gray-900 dark:text-gray-300">
          Use AI to optimize allocation
        </label>
      </div>
      
      <div class="flex justify-end">
        <button 
          type="submit" 
          class="btn btn-primary"
          :disabled="isLoading || selectedAssets.length < 2"
        >
          {{ isLoading ? 'Generating...' : 'Generate Allocation' }}
        </button>
      </div>
    </form>
    
    <!-- Results -->
    <div v-if="allocation" class="mt-6 space-y-6">
      <!-- Allocation chart -->
      <div class="bg-white dark:bg-gray-800 rounded-lg p-4">
        <canvas ref="chartRef" class="w-full h-64"></canvas>
      </div>
      
      <!-- Allocation table -->
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
          <thead class="bg-gray-50 dark:bg-gray-800">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                Asset
              </th>
              <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                Allocation
              </th>
              <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                Amount
              </th>
            </tr>
          </thead>
          <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            <tr 
              v-for="item in allocation.allocations" 
              :key="item.symbol"
              class="hover:bg-gray-50 dark:hover:bg-gray-700"
            >
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 dark:text-white">
                {{ item.symbol }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm text-gray-500 dark:text-gray-400">
                {{ (item.weight * 100).toFixed(1) }}%
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm text-gray-500 dark:text-gray-400">
                {{ formatCurrency(item.weight * form.amount) }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <!-- Portfolio metrics -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 p-4">
          <h4 class="text-sm font-medium mb-2">Expected Return</h4>
          <p class="text-2xl font-semibold text-primary-600">{{ allocation.metrics.expectedReturn }}%</p>
        </div>
        
        <div class="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 p-4">
          <h4 class="text-sm font-medium mb-2">Volatility</h4>
          <p class="text-2xl font-semibold text-primary-600">{{ allocation.metrics.volatility }}%</p>
        </div>
        
        <div class="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 p-4">
          <h4 class="text-sm font-medium mb-2">Sharpe Ratio</h4>
          <p class="text-2xl font-semibold text-primary-600">{{ allocation.metrics.sharpeRatio.toFixed(2) }}</p>
        </div>
      </div>
      
      <!-- Export button -->
      <div class="flex justify-end">
        <button 
          @click="exportAllocation" 
          class="btn btn-secondary"
        >
          Export Allocation
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

// Sample available assets
const availableAssets = ref([
  { symbol: 'AAPL', name: 'Apple Inc.' },
  { symbol: 'MSFT', name: 'Microsoft Corp' },
  { symbol: 'GOOGL', name: 'Alphabet Inc' },
  { symbol: 'AMZN', name: 'Amazon.com Inc' },
  { symbol: 'TSLA', name: 'Tesla Inc' },
  { symbol: 'NVDA', name: 'NVIDIA Corp' },
  { symbol: 'META', name: 'Meta Platforms Inc' }
])

const selectedAssets = ref<string[]>([])
const form = ref({
  riskProfile: 'moderate',
  amount: 100000,
  constraints: {
    maxAllocation: true,
    sectorDiversification: true
  },
  useAI: false
})

const allocation = ref<{
  allocations: Array<{
    symbol: string
    weight: number
  }>
  metrics: {
    expectedReturn: number
    volatility: number
    sharpeRatio: number
  }
} | null>(null)

const generateAllocation = async () => {
  if (!authStore.isProUser) {
    alert('Portfolio allocation is a Pro feature. Please upgrade to continue.')
    return
  }
  
  isLoading.value = true
  try {
    const response = await axios.post('/api/portfolio/allocate', {
      assets: selectedAssets.value,
      ...form.value,
      userId: authStore.user?.id
    })
    
    allocation.value = response.data
    updateChart()
  } catch (error) {
    console.error('Error generating allocation:', error)
  } finally {
    isLoading.value = false
  }
}

const updateChart = () => {
  if (!chartRef.value || !allocation.value) return
  
  if (chart.value) {
    chart.value.destroy()
  }
  
  const ctx = chartRef.value.getContext('2d')
  if (!ctx) return
  
  chart.value = new Chart(ctx, {
    type: 'pie',
    data: {
      labels: allocation.value.allocations.map(a => a.symbol),
      datasets: [{
        data: allocation.value.allocations.map(a => a.weight * 100),
        backgroundColor: [
          '#4F46E5',
          '#10B981',
          '#F59E0B',
          '#EF4444',
          '#8B5CF6',
          '#EC4899',
          '#6366F1'
        ]
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false
    }
  })
}

const formatCurrency = (value: number): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(value)
}

const exportAllocation = () => {
  // Implementation for exporting allocation
  console.log('Exporting allocation...')
}

watch(() => allocation.value, () => {
  updateChart()
})

onMounted(() => {
  if (allocation.value) {
    updateChart()
  }
})
</script>