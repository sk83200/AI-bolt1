<template>
  <div class="h-full bg-white dark:bg-gray-800 flex flex-col">
    <!-- Header -->
    <div class="p-4 border-b border-gray-200 dark:border-gray-700 flex-shrink-0">
      <div class="flex items-center justify-between">
        <h3 class="font-medium text-gray-900 dark:text-white">Strategy Output</h3>
        <div class="flex items-center space-x-2">
          <div class="flex rounded-md shadow-sm">
            <button
              @click="activeTab = 'code'"
              :class="[
                'px-3 py-1 text-xs font-medium rounded-l-md border',
                activeTab === 'code'
                  ? 'bg-primary-100 text-primary-800 border-primary-300 dark:bg-primary-900 dark:text-primary-200 dark:border-primary-800'
                  : 'bg-white text-gray-700 border-gray-300 dark:bg-gray-800 dark:text-gray-300 dark:border-gray-600'
              ]"
            >
              Code
            </button>
            <button
              @click="activeTab = 'preview'"
              :class="[
                'px-3 py-1 text-xs font-medium rounded-r-md border border-l-0',
                activeTab === 'preview'
                  ? 'bg-primary-100 text-primary-800 border-primary-300 dark:bg-primary-900 dark:text-primary-200 dark:border-primary-800'
                  : 'bg-white text-gray-700 border-gray-300 dark:bg-gray-800 dark:text-gray-300 dark:border-gray-600'
              ]"
            >
              Preview
            </button>
          </div>
          <button 
            v-if="!isGuest && generatedCode"
            @click="copyCode"
            class="text-xs text-primary-600 hover:text-primary-700 dark:text-primary-400"
          >
            Copy
          </button>
        </div>
      </div>
    </div>
    
    <!-- Content -->
    <div class="flex-1 overflow-hidden">
      <!-- Code Tab -->
      <div v-if="activeTab === 'code'" class="h-full">
        <div v-if="isGuest" class="h-full flex flex-col">
          <!-- Sample code for guest users -->
          <div class="flex-1 overflow-auto p-4">
            <div class="bg-gray-900 rounded-lg p-4 text-green-400 font-mono text-xs overflow-auto">
              <pre>{{ sampleCode }}</pre>
            </div>
          </div>
          
          <!-- Guest upgrade prompt -->
          <div class="p-4 border-t border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-900">
            <div class="text-center">
              <p class="text-xs text-gray-600 dark:text-gray-400 mb-2">
                🔒 This is sample code. Sign up to generate custom strategies.
              </p>
              <button 
                @click="$router.push('/register')"
                class="bg-primary-600 hover:bg-primary-700 text-white text-xs px-4 py-2 rounded-md font-medium"
              >
                Sign Up for Custom Code Generation
              </button>
            </div>
          </div>
        </div>
        
        <!-- Full code for authenticated users -->
        <div v-else class="h-full overflow-auto p-4">
          <div v-if="generatedCode" class="bg-gray-900 rounded-lg p-4 text-green-400 font-mono text-xs overflow-auto">
            <pre>{{ generatedCode }}</pre>
          </div>
          <div v-else class="flex items-center justify-center h-full text-gray-500 dark:text-gray-400">
            <div class="text-center">
              <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 20l4-16m4 4l4 4-4 4M6 16l-4-4 4-4" />
              </svg>
              <p class="mt-2 text-sm">Configure your strategy to generate code</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Preview Tab -->
      <div v-else-if="activeTab === 'preview'" class="h-full overflow-auto p-4">
        <div class="space-y-4">
          <!-- Strategy Summary -->
          <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
            <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-2">Strategy Summary</h4>
            <div class="grid grid-cols-2 gap-4 text-xs">
              <div>
                <span class="text-gray-500 dark:text-gray-400">Type:</span>
                <span class="ml-2 text-gray-900 dark:text-white capitalize">{{ strategyData.type?.replace('_', ' ') || 'Not set' }}</span>
              </div>
              <div>
                <span class="text-gray-500 dark:text-gray-400">Asset Class:</span>
                <span class="ml-2 text-gray-900 dark:text-white capitalize">{{ strategyData.assetClass || 'Not set' }}</span>
              </div>
              <div>
                <span class="text-gray-500 dark:text-gray-400">Timeframe:</span>
                <span class="ml-2 text-gray-900 dark:text-white">{{ strategyData.timeframe || 'Not set' }}</span>
              </div>
              <div>
                <span class="text-gray-500 dark:text-gray-400">Risk per Trade:</span>
                <span class="ml-2 text-gray-900 dark:text-white">{{ strategyData.riskManagement?.maxRiskPerTrade || 2 }}%</span>
              </div>
            </div>
          </div>
          
          <!-- Entry Rules Preview -->
          <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
            <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-2">Entry Rules</h4>
            <div class="text-xs space-y-1">
              <div v-if="strategyData.entryRules?.indicators?.length">
                <span class="text-gray-500 dark:text-gray-400">Indicators:</span>
                <span class="ml-2 text-gray-900 dark:text-white">{{ strategyData.entryRules.indicators.join(', ') }}</span>
              </div>
              <div v-if="strategyData.entryRules?.conditions?.length">
                <span class="text-gray-500 dark:text-gray-400">Conditions:</span>
                <ul class="ml-4 mt-1 space-y-1">
                  <li v-for="condition in strategyData.entryRules.conditions" :key="condition.text" class="text-gray-900 dark:text-white">
                    • {{ condition.text || 'Empty condition' }}
                  </li>
                </ul>
              </div>
            </div>
          </div>
          
          <!-- Exit Rules Preview -->
          <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
            <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-2">Exit Rules</h4>
            <div class="text-xs space-y-1">
              <div>
                <span class="text-gray-500 dark:text-gray-400">Profit Target:</span>
                <span class="ml-2 text-gray-900 dark:text-white">{{ strategyData.exitRules?.profitTarget || 5 }}%</span>
              </div>
              <div>
                <span class="text-gray-500 dark:text-gray-400">Stop Loss:</span>
                <span class="ml-2 text-gray-900 dark:text-white">{{ strategyData.exitRules?.stopLoss || 2 }}%</span>
              </div>
              <div v-if="strategyData.exitRules?.useTrailingStop">
                <span class="text-gray-500 dark:text-gray-400">Trailing Stop:</span>
                <span class="ml-2 text-gray-900 dark:text-white">Enabled</span>
              </div>
            </div>
          </div>
          
          <!-- Guest mode limitations -->
          <div v-if="isGuest" class="bg-warning-50 dark:bg-warning-900 rounded-lg p-4">
            <h4 class="text-sm font-medium text-warning-800 dark:text-warning-200 mb-2">🔒 Limited Preview</h4>
            <p class="text-xs text-warning-700 dark:text-warning-300">
              Sign up to access backtesting, performance metrics, and detailed analysis.
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

// Props
const props = defineProps<{
  strategyData: any
  isGuest: boolean
  generatedCode: string
}>()

const router = useRouter()
const activeTab = ref('code')

// Sample code for guest users
const sampleCode = computed(() => `# Sample Trading Strategy Code
# This is a demonstration of what your custom strategy would look like

import pandas as pd
import numpy as np
from trading_framework import Strategy, Indicator

class SampleMomentumStrategy(Strategy):
    def __init__(self):
        super().__init__()
        self.asset_class = "stocks"
        self.timeframe = "1d"
        
    def setup_indicators(self):
        # Technical indicators
        self.rsi = Indicator.RSI(period=14)
        self.sma_20 = Indicator.SMA(period=20)
        self.volume_avg = Indicator.SMA(period=20, field='volume')
        
    def entry_conditions(self, data):
        # Sample entry logic
        return (
            data['rsi'] > 70 and
            data['close'] > data['sma_20'] and
            data['volume'] > data['volume_avg'] * 1.5
        )
        
    def exit_conditions(self, data, position):
        # Sample exit logic
        profit_target = 5.0  # 5%
        stop_loss = 2.0      # 2%
        
        return (
            position.unrealized_pnl_pct >= profit_target or
            position.unrealized_pnl_pct <= -stop_loss or
            data['rsi'] < 30
        )
        
    def position_size(self, data):
        # Risk management
        max_risk = 2.0  # 2% risk per trade
        return self.calculate_position_size(max_risk)

# Sign up to generate custom strategies with your parameters!`)

// Copy code to clipboard
const copyCode = async () => {
  if (props.isGuest) return
  
  try {
    await navigator.clipboard.writeText(props.generatedCode)
    // You could add a toast notification here
    console.log('Code copied to clipboard')
  } catch (error) {
    console.error('Failed to copy code:', error)
  }
}
</script>