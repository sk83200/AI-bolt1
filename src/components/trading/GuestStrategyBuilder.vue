<template>
  <div class="h-full bg-gray-50 dark:bg-gray-900 p-6">
    <!-- Sample Strategy Display -->
    <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 mb-6">
      <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-4">Sample Strategy Preview</h3>
      
      <!-- Strategy Overview Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
          <div class="text-sm text-gray-500 dark:text-gray-400">Strategy Type</div>
          <div class="text-lg font-semibold text-gray-900 dark:text-white">Momentum</div>
        </div>
        <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
          <div class="text-sm text-gray-500 dark:text-gray-400">Asset Class</div>
          <div class="text-lg font-semibold text-gray-900 dark:text-white">Stocks</div>
        </div>
        <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
          <div class="text-sm text-gray-500 dark:text-gray-400">Timeframe</div>
          <div class="text-lg font-semibold text-gray-900 dark:text-white">1d</div>
        </div>
        <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
          <div class="text-sm text-gray-500 dark:text-gray-400">Risk Level</div>
          <div class="text-lg font-semibold text-gray-900 dark:text-white">Medium</div>
        </div>
      </div>

      <!-- Strategy Description -->
      <div class="bg-primary-50 dark:bg-primary-900 rounded-lg p-4 mb-4">
        <h4 class="text-sm font-medium text-primary-900 dark:text-primary-100 mb-2">Sample Strategy Description</h4>
        <p class="text-sm text-primary-800 dark:text-primary-200">
          This momentum strategy allocates equally to AAPL, TSLA, and NVDA when their 3-month returns exceed 12% and volatility is under 15%. 
          It rebalances monthly and exits positions if drawdown exceeds 10%.
        </p>
      </div>

      <!-- Sample Parameters -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Entry Rules</h4>
          <ul class="text-sm text-gray-600 dark:text-gray-300 space-y-1">
            <li>• 3-month return > 12%</li>
            <li>• Volatility < 15%</li>
            <li>• RSI > 50</li>
            <li>• Volume > average</li>
          </ul>
        </div>
        <div>
          <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-3">Exit Rules</h4>
          <ul class="text-sm text-gray-600 dark:text-gray-300 space-y-1">
            <li>• Stop loss: 10%</li>
            <li>• Profit target: 20%</li>
            <li>• Monthly rebalancing</li>
            <li>• RSI < 30 exit signal</li>
          </ul>
        </div>
      </div>
    </div>

    <!-- Upgrade Prompt -->
    <div class="bg-gradient-to-r from-primary-50 to-secondary-50 dark:from-primary-900 dark:to-secondary-900 rounded-lg p-6 text-center">
      <h4 class="text-lg font-semibold text-gray-900 dark:text-white mb-2">
        Ready to Create Your Own Strategy?
      </h4>
      <p class="text-gray-600 dark:text-gray-300 mb-4">
        Sign up to access the full strategy builder, backtesting, and performance analytics.
      </p>
      <div class="space-y-3">
        <button 
          @click="$router.push('/register')"
          class="w-full bg-primary-600 hover:bg-primary-700 text-white px-6 py-3 rounded-lg font-medium"
        >
          Sign Up Free - Build Custom Strategies
        </button>
        <button 
          @click="$router.push('/login')"
          class="w-full text-primary-600 hover:text-primary-700 dark:text-primary-400 px-6 py-2"
        >
          Already have an account? Log in
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// This component shows sample strategies without modal overlays
</script>