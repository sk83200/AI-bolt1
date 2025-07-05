<script setup lang="ts">
import { ref, watch } from 'vue'

// Props
const props = defineProps<{
  symbol: string
}>()

// Order form data
const orderType = ref('market')
const action = ref('buy')
const quantity = ref(1)
const price = ref(0)
const total = ref(0)

// Get current market price based on symbol
const getMarketPrice = (symbol: string) => {
  // In real app, this would call an API
  return symbol === 'AAPL' ? 198.11 :
         symbol === 'MSFT' ? 297.75 :
         symbol === 'GOOGL' ? 2137.80 : 100.00
}

// Update price when symbol changes
watch(() => props.symbol, (newSymbol) => {
  price.value = getMarketPrice(newSymbol)
  updateTotal()
})

// Calculate total
const updateTotal = () => {
  total.value = quantity.value * price.value
}

// Watch for changes to update total
watch([quantity, price], () => {
  updateTotal()
})

// Initialize on component creation
price.value = getMarketPrice(props.symbol)
updateTotal()

// Format currency
const formatCurrency = (value: number): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(value)
}

// Submit order
const submitOrder = () => {
  // In real app, this would submit to backend
  const order = {
    symbol: props.symbol,
    type: orderType.value,
    action: action.value,
    quantity: quantity.value,
    price: price.value,
    total: total.value
  }
  
  console.log('Order submitted:', order)
  
  // Reset form
  quantity.value = 1
  updateTotal()
  
  // Show success message
  alert(`Order successfully placed for ${quantity.value} shares of ${props.symbol}`)
}
</script>

<template>
  <div class="card h-full">
    <h2 class="text-lg font-medium text-gray-900 dark:text-white mb-4">Place Order</h2>
    
    <form @submit.prevent="submitOrder" class="space-y-4">
      <!-- Symbol info -->
      <div class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700 rounded-md">
        <div>
          <div class="text-lg font-medium text-gray-900 dark:text-white">{{ symbol }}</div>
          <div class="text-sm text-gray-500 dark:text-gray-400">Current Price: {{ formatCurrency(price) }}</div>
        </div>
        <div 
          class="px-2 py-1 text-xs font-medium rounded-full bg-green-100 text-green-800 
                 dark:bg-green-900 dark:text-green-200"
        >
          Active
        </div>
      </div>
      
      <!-- Order type -->
      <div>
        <label class="label">Order Type</label>
        <div class="flex rounded-md shadow-sm">
          <button
            type="button"
            class="flex-1 py-2 px-4 text-sm font-medium rounded-l-md border border-r-0 focus:z-10"
            :class="orderType === 'market' ? 
                   'bg-primary-100 text-primary-800 border-primary-300 dark:bg-primary-900 dark:text-primary-200 dark:border-primary-800' : 
                   'bg-white text-gray-700 border-gray-300 dark:bg-gray-800 dark:text-gray-300 dark:border-gray-600'"
            @click="orderType = 'market'"
          >
            Market
          </button>
          <button
            type="button"
            class="flex-1 py-2 px-4 text-sm font-medium rounded-r-md border focus:z-10"
            :class="orderType === 'limit' ? 
                   'bg-primary-100 text-primary-800 border-primary-300 dark:bg-primary-900 dark:text-primary-200 dark:border-primary-800' : 
                   'bg-white text-gray-700 border-gray-300 dark:bg-gray-800 dark:text-gray-300 dark:border-gray-600'"
            @click="orderType = 'limit'"
          >
            Limit
          </button>
        </div>
      </div>
      
      <!-- Buy/Sell action -->
      <div>
        <label class="label">Action</label>
        <div class="flex rounded-md shadow-sm">
          <button
            type="button"
            class="flex-1 py-2 px-4 text-sm font-medium rounded-l-md border border-r-0 focus:z-10"
            :class="action === 'buy' ? 
                   'bg-success-500 text-white border-success-600 dark:border-success-800' : 
                   'bg-white text-gray-700 border-gray-300 dark:bg-gray-800 dark:text-gray-300 dark:border-gray-600'"
            @click="action = 'buy'"
          >
            Buy
          </button>
          <button
            type="button"
            class="flex-1 py-2 px-4 text-sm font-medium rounded-r-md border focus:z-10"
            :class="action === 'sell' ? 
                   'bg-error-500 text-white border-error-600 dark:border-error-800' : 
                   'bg-white text-gray-700 border-gray-300 dark:bg-gray-800 dark:text-gray-300 dark:border-gray-600'"
            @click="action = 'sell'"
          >
            Sell
          </button>
        </div>
      </div>
      
      <!-- Quantity -->
      <div>
        <label for="quantity" class="label">Quantity</label>
        <input
          id="quantity"
          v-model="quantity"
          type="number"
          min="1"
          class="input"
          required
        />
      </div>
      
      <!-- Price (for limit orders) -->
      <div v-if="orderType === 'limit'">
        <label for="limit-price" class="label">Limit Price</label>
        <input
          id="limit-price"
          v-model="price"
          type="number"
          min="0.01"
          step="0.01"
          class="input"
          required
        />
      </div>
      
      <!-- Order summary -->
      <div class="p-3 bg-gray-50 dark:bg-gray-700 rounded-md">
        <div class="flex justify-between text-sm">
          <span class="text-gray-500 dark:text-gray-400">Total</span>
          <span class="font-semibold text-gray-900 dark:text-white">{{ formatCurrency(total) }}</span>
        </div>
      </div>
      
      <!-- Submit button -->
      <button 
        type="submit" 
        class="w-full btn"
        :class="action === 'buy' ? 'bg-success-500 hover:bg-success-600 text-white' : 'bg-error-500 hover:bg-error-600 text-white'"
      >
        {{ action === 'buy' ? 'Buy' : 'Sell' }} {{ symbol }}
      </button>
    </form>
  </div>
</template>