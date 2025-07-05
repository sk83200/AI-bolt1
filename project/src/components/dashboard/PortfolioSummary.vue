<script setup lang="ts">
import { ref } from 'vue'

// Portfolio data
const portfolioItems = ref([
  {
    id: 1,
    symbol: 'AAPL',
    name: 'Apple Inc.',
    value: 12450.75,
    quantity: 25,
    price: 498.03,
    change: 2.75,
    allocation: 22.1
  },
  {
    id: 2,
    symbol: 'MSFT',
    name: 'Microsoft Corp',
    value: 8932.40,
    quantity: 30,
    price: 297.75,
    change: 1.2,
    allocation: 15.9
  },
  {
    id: 3,
    symbol: 'AMZN',
    name: 'Amazon.com Inc',
    value: 6820.30,
    quantity: 2,
    price: 3410.15,
    change: -0.5,
    allocation: 12.1
  },
  {
    id: 4,
    symbol: 'GOOGL',
    name: 'Alphabet Inc',
    value: 4275.60,
    quantity: 2,
    price: 2137.80,
    change: 1.5,
    allocation: 7.6
  },
  {
    id: 5,
    symbol: 'TSLA',
    name: 'Tesla Inc',
    value: 8790.00,
    quantity: 10,
    price: 879.00,
    change: 3.2,
    allocation: 15.6
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
</script>

<template>
  <div class="card h-full">
    <div class="flex items-center justify-between mb-4">
      <h2 class="text-lg font-medium text-gray-900 dark:text-white">Portfolio Summary</h2>
      <button class="btn btn-secondary text-sm py-1">Manage</button>
    </div>
    
    <!-- Portfolio items -->
    <div class="overflow-x-auto -mx-6">
      <table class="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
        <thead class="bg-gray-50 dark:bg-gray-800">
          <tr>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
              Asset
            </th>
            <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
              Price
            </th>
            <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
              Change
            </th>
            <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
              Value
            </th>
          </tr>
        </thead>
        <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
          <tr v-for="item in portfolioItems" :key="item.id" class="hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="flex items-center">
                <div>
                  <div class="text-sm font-medium text-gray-900 dark:text-white">
                    {{ item.symbol }}
                  </div>
                  <div class="text-sm text-gray-500 dark:text-gray-400">
                    {{ item.name }}
                  </div>
                </div>
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm text-gray-500 dark:text-gray-400">
              {{ formatCurrency(item.price) }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm"
                :class="item.change >= 0 ? 'text-success-500' : 'text-error-500'">
              {{ formatPercent(item.change) }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium text-gray-900 dark:text-white">
              {{ formatCurrency(item.value) }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <div class="mt-4 pt-4 border-t border-gray-200 dark:border-gray-700">
      <div class="flex items-center justify-between text-sm">
        <span class="text-gray-500 dark:text-gray-400">Total Portfolio Value</span>
        <span class="font-semibold text-gray-900 dark:text-white">{{ formatCurrency(portfolioItems.reduce((total, item) => total + item.value, 0)) }}</span>
      </div>
    </div>
  </div>
</template>