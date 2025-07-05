<script setup lang="ts">
import { ref } from 'vue'
import { CheckCircleIcon, XCircleIcon, ClockIcon } from '@heroicons/vue/24/outline'

// Recent activity data
const activities = ref([
  {
    id: 1,
    type: 'buy',
    status: 'completed',
    symbol: 'AAPL',
    quantity: 10,
    price: 198.23,
    total: 1982.30,
    date: '2023-11-05T10:23:15'
  },
  {
    id: 2,
    type: 'sell',
    status: 'completed',
    symbol: 'TSLA',
    quantity: 5,
    price: 234.45,
    total: 1172.25,
    date: '2023-11-04T15:12:48'
  },
  {
    id: 3,
    type: 'buy',
    status: 'completed',
    symbol: 'NVDA',
    quantity: 8,
    price: 486.20,
    total: 3889.60,
    date: '2023-11-03T09:41:30'
  },
  {
    id: 4,
    type: 'buy',
    status: 'pending',
    symbol: 'MSFT',
    quantity: 12,
    price: 297.75,
    total: 3573.00,
    date: '2023-11-02T11:05:22'
  },
  {
    id: 5,
    type: 'sell',
    status: 'failed',
    symbol: 'AMD',
    quantity: 15,
    price: 123.45,
    total: 1851.75,
    date: '2023-11-01T14:22:30'
  }
])

// Format currency
const formatCurrency = (value: number): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(value)
}

// Format date
const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('en-US', {
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: 'numeric'
  }).format(date)
}

// Get status icon
const getStatusIcon = (status: string) => {
  if (status === 'completed') return CheckCircleIcon
  if (status === 'failed') return XCircleIcon
  return ClockIcon
}

// Get status color
const getStatusColor = (status: string): string => {
  if (status === 'completed') return 'text-success-500'
  if (status === 'failed') return 'text-error-500'
  return 'text-warning-500'
}
</script>

<template>
  <div class="card h-full">
    <div class="flex items-center justify-between mb-4">
      <h2 class="text-lg font-medium text-gray-900 dark:text-white">Recent Activity</h2>
      <button class="btn btn-secondary text-sm py-1">View All</button>
    </div>
    
    <div class="space-y-4">
      <div 
        v-for="activity in activities" 
        :key="activity.id"
        class="flex items-start p-3 rounded-md hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
      >
        <div 
          :class="[
            'p-2 rounded-full mr-3',
            getStatusColor(activity.status)
          ]"
        >
          <component 
            :is="getStatusIcon(activity.status)" 
            class="h-5 w-5"
          />
        </div>
        
        <div class="flex-1">
          <div class="flex justify-between">
            <div>
              <p class="text-sm font-medium text-gray-900 dark:text-white">
                {{ activity.type === 'buy' ? 'Bought' : 'Sold' }} {{ activity.quantity }} {{ activity.symbol }}
              </p>
              <p class="text-xs text-gray-500 dark:text-gray-400">
                {{ formatDate(activity.date) }}
              </p>
            </div>
            <div class="text-right">
              <p class="text-sm font-medium text-gray-900 dark:text-white">
                {{ formatCurrency(activity.total) }}
              </p>
              <p class="text-xs text-gray-500 dark:text-gray-400">
                {{ formatCurrency(activity.price) }} per share
              </p>
            </div>
          </div>
          
          <div class="mt-1">
            <span 
              :class="[
                'text-xs px-2 py-1 rounded-full',
                activity.status === 'completed' ? 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200' :
                activity.status === 'pending' ? 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200' :
                'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200'
              ]"
            >
              {{ activity.status.charAt(0).toUpperCase() + activity.status.slice(1) }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>