<template>
  <div class="h-full bg-gray-50 dark:bg-gray-900 relative">
    <!-- Guest Mode Overlay -->
    <div class="absolute inset-0 bg-white/50 dark:bg-gray-900/50 backdrop-blur-sm z-10 flex items-center justify-center">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-2xl p-8 max-w-md mx-4 text-center">
        <div class="w-16 h-16 bg-primary-100 dark:bg-primary-900 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-primary-600 dark:text-primary-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
          </svg>
        </div>
        <h3 class="text-xl font-bold text-gray-900 dark:text-white mb-2">
          Strategy Builder Preview
        </h3>
        <p class="text-gray-600 dark:text-gray-300 mb-6">
          You're viewing a sample strategy builder. Sign up to create, customize, and backtest your own strategies with real data.
        </p>
        <div class="space-y-3">
          <button 
            @click="$router.push('/register')"
            class="w-full bg-primary-600 hover:bg-primary-700 text-white px-6 py-3 rounded-lg font-medium"
          >
            Sign Up Free
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

    <!-- Blurred Strategy Builder (Read-only preview) -->
    <div class="opacity-60 pointer-events-none">
      <div class="grid grid-cols-1 lg:grid-cols-4 gap-6 h-full p-6">
        <!-- Property Sheet Panel (Left Side) -->
        <div class="lg:col-span-1 bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden">
          <div class="h-full flex flex-col">
            <!-- Property Sheet Header -->
            <div class="p-4 border-b border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-900">
              <h3 class="text-sm font-medium text-gray-900 dark:text-white">Strategy Properties</h3>
            </div>
            
            <!-- Sample form content -->
            <div class="flex-1 overflow-y-auto p-4 space-y-4">
              <div>
                <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Strategy Name</label>
                <input
                  type="text"
                  value="Sample Momentum Strategy"
                  class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded"
                  readonly
                />
              </div>
              <div>
                <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Strategy Type</label>
                <select class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded" disabled>
                  <option>Momentum</option>
                </select>
              </div>
              <div>
                <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Asset Class</label>
                <select class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded" disabled>
                  <option>Stocks</option>
                </select>
              </div>
            </div>
          </div>
        </div>

        <!-- Main Content Area (Right Side) -->
        <div class="lg:col-span-3 space-y-6">
          <!-- AI Prompt Section -->
          <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
            <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-4">AI Strategy Generator</h3>
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Describe your trading strategy:
                </label>
                <textarea
                  rows="4"
                  class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md"
                  value="Create a momentum strategy for tech stocks with 20% annual returns"
                  readonly
                ></textarea>
              </div>
            </div>
          </div>

          <!-- Strategy Overview -->
          <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
            <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-4">Strategy Overview</h3>
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
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// This component shows a read-only preview of the strategy builder for guests
</script>