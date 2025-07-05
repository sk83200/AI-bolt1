<script setup lang="ts">
import { ref } from 'vue'

// Market data
const marketData = ref([
  {
    symbol: 'AAPL',
    name: 'Apple Inc.',
    price: 198.11,
    change: 2.75,
    volume: '32.5M',
    marketCap: '3.12T'
  },
  {
    symbol: 'MSFT',
    name: 'Microsoft Corp',
    price: 297.75,
    change: 1.2,
    volume: '28.7M',
    marketCap: '2.21T'
  },
  {
    symbol: 'GOOGL',
    name: 'Alphabet Inc',
    price: 137.80,
    change: -0.5,
    volume: '22.1M',
    marketCap: '1.74T'
  },
  {
    symbol: 'AMZN',
    name: 'Amazon.com Inc',
    price: 152.45,
    change: 1.5,
    volume: '34.6M',
    marketCap: '1.58T'
  },
  {
    symbol: 'TSLA',
    name: 'Tesla Inc',
    price: 237.49,
    change: 3.2,
    volume: '75.9M',
    marketCap: '753.9B'
  },
  {
    symbol: 'META',
    name: 'Meta Platforms Inc',
    price: 325.12,
    change: -0.8,
    volume: '18.3M',
    marketCap: '832.6B'
  },
  {
    symbol: 'NVDA',
    name: 'NVIDIA Corp',
    price: 437.53,
    change: 5.2,
    volume: '42.7M',
    marketCap: '1.08T'
  }
])

// Format currency
const formatCurrency = (value: number): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(value)
}

// Format percentage
const formatPercent = (value: number): string => {
  return `${value > 0 ? '+' : ''}${value.toFixed(2)}%`
}

// Emit to parent for trading
const emit = defineEmits(['select'])

const selectStock = (symbol: string) => {
  emit('select', symbol)
}
</script>

<template>
  <div class="card h-full">
    <div class="flex items-center justify-between mb-4">
      <h2 class="text-lg font-medium text-gray-900 dark:text-white">Market Overview</h2>
      <div class="flex items-center space-x-2">
        <div class="text-xs text-gray-500 dark:text-gray-400">Last updated: Just now</div>
        <button class="btn btn-secondary text-sm py-1">Refresh</button>
      </div>
    </div>
    
    <!-- Market data table -->
    <div class="overflow-x-auto -mx-6">
      <table class="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
        <thead class="bg-gray-50 dark:bg-gray-800">
          <tr>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
              Symbol
            </th>
            <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
              Price
            </th>
            <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
              Change
            </th>
            <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
              Volume
            </th>
            <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
              Market Cap
            </th>
          </tr>
        </thead>
        <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
          <tr 
            v-for="stock in marketData" 
            :key="stock.symbol" 
            class="hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors cursor-pointer"
            @click="selectStock(stock.symbol)"
          >
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="flex items-center">
                <div>
                  <div class="text-sm font-medium text-gray-900 dark:text-white">
                    {{ stock.symbol }}
                  </div>
                  <div class="text-xs text-gray-500 dark:text-gray-400">
                    {{ stock.name }}
                  </div>
                </div>
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium text-gray-900 dark:text-white">
              {{ formatCurrency(stock.price) }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm"
                :class="stock.change >= 0 ? 'text-success-500' : 'text-error-500'">
              {{ formatPercent(stock.change) }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm text-gray-500 dark:text-gray-400">
              {{ stock.volume }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm text-gray-500 dark:text-gray-400">
              {{ stock.marketCap }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>